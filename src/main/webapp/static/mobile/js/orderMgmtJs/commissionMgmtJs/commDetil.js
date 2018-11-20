(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.buyPriceFlag = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData();//获取数据		
			this.getPermissionList('biz:order:buyPrice:view','buyPriceFlag')//佣金权限
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
		},
		getPermissionList: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.buyPriceFlag = res.data;
                }
            });
        },
		getData: function() {
			var _this = this;
			if(_this.userInfo.option == 'detail') {
        		$('#comPayMentBtn').hide();
        		$('#commLastDate').attr('disabled','disabled');
//      		$('#commApplyNum').attr('disabled','disabled');
        	}
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizCommission/applyCommissionForm4Mobile",
                data:{
                	orderIds: _this.userInfo.orderIds,
					totalDetail:_this.userInfo.totalDetail,
					totalCommission:_this.userInfo.totalCommission,
					sellerId : _this.userInfo.sellerId,
					option : _this.userInfo.option
                },
                dataType: "json",
                success: function(res){
//              	console.log(res)
                	
					var entity = res.data.entity;
					var deadline = '';
					if(entity.deadline) {
						deadline = _this.newData(entity.deadline);
					}
//					var shouldPay = item.totalDetail + item.totalExp + item.freight + item.serviceFee-item.scoreMoney;
					$('#commOrdTotal').val(entity.totalDetail);//订单总价
					$('#commCardNum').val(entity.customerInfo.cardNumber);//零售商卡号
					$('#commName').val(entity.customerInfo.payee);//零售商收款人
					$('#commOpenBank').val(entity.customerInfo.bankName);//零售商开户行
					$('#commApplyNum').val(entity.totalCommission);//申请金额
					$('#commLastDate').val(deadline);//最后付款时间
					$('#commRemark').val(entity.remark);//备注
					
					$.each(res.data.orderHeaderList, function(jj,ss) {
//						console.log(ss)
						$('#commOrdNum').val(ss.orderNum);//订单编号
						$('#commMerchantName').val(ss.seller.name);//代销商名称
						$('#commOutletName').val(ss.customer.name);//经销店名称
						$('#commPurchase').val(ss.centersName);//采购中心
						$('#commOutletPhone').val(ss.customer.phone);//经销店电话
						$('#commReceived').val(ss.receiveTotal==null?0.00:ss.receiveTotal);//已收货款
						$('#commTotal').val(ss.totalDetail);//商品总价
						$('#commAdjustment').val(ss.totalExp);//调整金额
						$('#commFreight').val(ss.freight);//运费
						$('#commDeduction').val(ss.scoreMoney);//万户币抵扣
						$('#commAmount').val(ss.totalDetail+ss.totalExp+ss.freight+ss.serviceFee-ss.scoreMoney);//应付金额
						$('#commService').val(ss.totalExp+ss.serviceFee+ss.freight);//服务费
						$('#commissionNum').val(ss.commission);//佣金
						$('#commCreator').val(ss.createBy.name);//创建人
						//订单类型
		          	    $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/getDictLabel4Mobile",
			                dataType: "json",
			                data: {
			                	value:ss.orderType,
			                	type: "biz_order_type",
			                	defaultValue:'未知状态'
			                },
			                async:false,
			                success: function(hz){ 
			                	$('#commOrdType').val(hz.data.dictLabel);//订单类型
			                }
			            });
			            //发票状态
		          	    $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/getDictLabel4Mobile",
			                dataType: "json",
			                data: {
			                	value:ss.invStatus,
			                	type: "biz_order_invStatus",
			                	defaultValue:'未知状态'
			                },
			                async:false,
			                success: function(dj){ 
			                	$('#commInvoiceStatus').val(dj.data.dictLabel);//发票状态
			                }
			            });
//			            REFUND(0, "退款申请"),
//					    REFUNDING(1, "退款中"),
//					    REFUNDREJECT(2, "驳回"),
//					    REFUNDED(3, "退款完成");
			            if(ss.drawBack != '') {
			            	if(ss.drawBack.drawbackStatus == 0) {
								$('#commBusinessStatus').val('申请退款');//业务状态
							}
			            	if(ss.drawBack.drawbackStatus == 1) {
			            		$('#commBusinessStatus').val('退款中');//业务状态
							}
			            	if(ss.drawBack.drawbackStatus == 2) {
			            		$('#commBusinessStatus').val('退款驳回');//业务状态
							}
			            	if(ss.drawBack.drawbackStatus == 3) {
			            		$('#commBusinessStatus').val('退款完成');//业务状态
							}
						}else {
							$.ajax({
				                type: "GET",
				                url: "/a/sys/dict/getDictLabel4Mobile",
				                dataType: "json",
				                data: {
				                	value:ss.bizStatus,
				                	type: "biz_order_status",
				                	defaultValue:'未知状态'
				                },
				                async:false,
				                success: function(zj){ 
				                	$('#commBusinessStatus').val(zj.data.dictLabel);//业务状态
				                }
				            });
							var total = ss.totalDetail+ss.totalExp+ss.freight+ss.serviceFee;
							var receive = ss.receiveTotal + ss.scoreMoney;
							if(total > receive && ss.bizStatus!=10 && ss.bizStatus!=35 && ss.bizStatus!=40 && ss.bizStatus!=45 && ss.bizStatus!=60) {
								$('#commFinal').val("(有尾款)");
							}
						}
						if(ss.applyCommStatus == 'no') {
							$('#commCheckStatus').val('待申请');//审核状态
		                }
						if(ss.applyCommStatus == 'yes' && ss.bizCommission.bizStatus == '0') {
							if(ss.bizCommission.totalCommission == '0.00' && ss.bizCommission.paymentOrderProcess.name != '审批完成') {
								$('#commCheckStatus').val('待确认支付金额');//审核状态
		                    }
							if(ss.bizCommission.totalCommission != '0.00') {
								$('#commCheckStatus').val(ss.bizCommission.commonProcess.paymentOrderProcess.name);//审核状态
		                    }
		                }
						if(ss.applyCommStatus == 'yes' && ss.bizCommission.bizStatus == '1') {
							$('#commCheckStatus').val('已结佣');//审核状态
		                }
					});
					/*审核流程*/
					var lens = '';
					if(res.data.auditList) {
						lens = res.data.auditList.length;
					}
					if(lens > 0) {
						var CheckHtmlList = '';
						$.each(res.data.auditList, function(dd,mm) {
//							console.log(mm)
							var step = dd + 1;
							if(mm.current != 1) {
	                            CheckHtmlList +='<li class="step_item">'+
									'<div class="step_num">'+ step +' </div>'+
									'<div class="step_num_txt">'+
										'<div class="mui-input-row">'+
											'<label>处理人:</label>'+
											'<input type="text" value="'+ mm.user.name +'" class="mui-input-clear" disabled>'+
									    '</div>'+
										'<div class="mui-input-row">'+
									        '<label>批注:</label>'+
									        '<input type="text" value="'+ mm.description +'" class="mui-input-clear" disabled>'+
									    	'<label>状态:</label>'+
									        '<input type="text" value=" '+ mm.paymentOrderProcess.name +' " class="mui-input-clear" disabled>'+
									    	'<label>时间:</label>'+
									        '<input type="text" value="'+ _this.formatDateTime(mm.updateTime) +'" class="mui-input-clear" disabled>'+
									    '</div>'+
									'</div>'+
								'</li>'
	                        }
							if(mm.current == 1) {
	                            CheckHtmlList +='<li class="step_item">'+
									'<div class="step_num">'+ step +' </div>'+
									'<div class="step_num_txt">'+
										'<div class="mui-input-row">'+
											'<label>当前状态:</label>'+
											'<input type="text" value="'+ mm.paymentOrderProcess.name +'" class="mui-input-clear" disabled>'+
									   		'<label>时间:</label>'+
									        '<input type="text" value=" '+ _this.formatDateTime(mm.updateTime) +' " class="mui-input-clear" disabled>'+
									    '</div>'+
									'</div>'+
								'</li>'
	                        }
						});
						$('#commCheckMenu').html(CheckHtmlList);
					}
					/*商品详情*/
					if(res.data.orderHeaderList.length > 0) {
						var htmlCommodity = '';
						$.each(res.data.orderHeaderList, function(zz,ll) {
							if(ll.orderDetailList.length > 0) {
								$.each(ll.orderDetailList, function(hh,zz) {
									var totalMoney = zz.unitPrice * zz.ordQty;
									var consigner = '';
									if(zz.suplyis.name) {
										consigner = zz.suplyis.name;
									}
		
									htmlCommodity += '<div class="mui-row app_bline commodity" id="commoditybox">' +
			                    
				                    '<div class="mui-row lineStyle">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label>订单号:</label>' +
				                    '<input type="text" class="mui-input-clear" id="" value="' + ll.orderNum + '" disabled></div></li></div>'+
				                   
			                    	'<div class="mui-row">' +
				                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label>商品零售价:</label>' +
				                    '<input type="text" class="mui-input-clear" id="" value="' + zz.unitPrice + '" disabled></div></li></div>' +
				                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label>佣金:</label>' +
				                    '<input type="text" class="mui-input-clear" id="" value="' + zz.detailCommission + '" disabled></div></li></div></div>'+
								
									'<div class="mui-row">' +
				                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label>采购数量:</label>' +
				                    '<input type="text" class="mui-input-clear" id="" value="' + zz.ordQty + '" disabled></div></li></div>' +
				                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label>总 额:</label>' +
				                    '<input type="text" class="mui-input-clear" id="" value="' + totalMoney + '" disabled></div></li></div></div>'+
								
									'<div class="mui-row">' +
				                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label>已发货数量:</label>' +
				                    '<input type="text" class="mui-input-clear" id="" value="' + zz.sentQty + '" disabled></div></li></div>' +
				                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label>发货方:</label>' +
				                    '<input type="text" class="mui-input-clear" id="" value="' + consigner + '" disabled></div></li></div></div>'+
									
									'<div class="mui-row lineStyle">' +
				                    '<li class="mui-table-view-cell">' +   
				                    '<div class="mui-input-row ">' +
				                    '<label class="commodityName">商品名称:</label>' +
				                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + zz.skuName + '" disabled></div></li></div>'+
									
			                    	'<div class="mui-row lineStyle">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label class="commodityName">商品货号:</label>' +
				                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + zz.skuInfo.itemNo + '" disabled></div></li></div>' +
				                    
				                    '<div class="mui-row lineStyle">' +
				                    '<li class="mui-table-view-cell">' +
				                    '<div class="mui-input-row ">' +
				                    '<label class="commodityName">商品编号:</label>' +
				                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + zz.partNo + '" disabled></div></li></div>' +
				                   
			                    '</div>'
								});
							}
						});
					}
					$('#ommodity').html(htmlCommodity);
					
					_this.comPayMent();//申请付款					
                }
            });
		},
		newData:function(da){
        	var _this = this;
//      	 var date = new Date(da);//时间戳为10位需*1000，时间戳为13位的话不需乘1000      
            var now = new Date(da);
                y = now.getFullYear(),
                m = now.getMonth() + 1,
                d = now.getDate();
                var hours = now.getHours();
                var minutes = now.getMinutes();
                var seconds = now.getSeconds();
           // return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + "T" + now.toTimeString().substr(0, 8);
             return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d);
        },
        /*暂时没有接口*/
		comPayMent: function() {
			var _this = this;
			$('#comPayMentBtn').on('tap', function() {
				var orderIds = _this.userInfo.orderIds;
	            var payTotal = _this.userInfo.totalCommission;
	            var lastPayDate = $('#commLastDate').val();
	            var remark = $("#commRemark").val();
	            var sellerId = _this.userInfo.sellerId;
	            var commApplyNum = $('#commApplyNum').val();
	            if (isNaN(commApplyNum) || commApplyNum <= 0) {
	                mui.toast("申请金额不正确!");
	                return false;
	            }
	            if (lastPayDate == '') {
	                mui.toast("请选择最后付款时间!");
	                return false;
	            }
				$.ajax({
	                type: "GET",
	                url: "/a/biz/order/bizCommissionOrder/saveCommission4Mobile",
	                data: {
	                	'totalCommission': payTotal,/*申请金额*/
			            'deadline': lastPayDate,/*最后付款时间*/
			            'orderIds': orderIds,/*订单id*/
			            'remark': remark, /*备注*/
			            'sellerId': sellerId /*代销人id*/
	                },
	                dataType: "json",
	                success: function(rest){
	                	if(rest.ret==true){		                		
	                		mui.toast('保存成功！');
	                		GHUTILS.OPENPAGE({
							url:"../../../html/orderMgmtHtml/commissionMgmtHtml/commissionList.html",
								extras: {
								}
							})
	                	}
	                }	
				})
			})
		},
		formatDateTime: function(unix) {
        	var _this = this;

    		var now = new Date(parseInt(unix) * 1);
	        now =  now.toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
	        if(now.indexOf("下午") > 0) {
	            if (now.length == 18) {
	                var temp1 = now.substring(0, now.indexOf("下午"));   //2014/7/6
	                var temp2 = now.substring(now.indexOf("下午") + 2, now.length);  // 5:17:43
	                var temp3 = temp2.substring(0, 1);    //  5
	                var temp4 = parseInt(temp3); // 5
	                temp4 = 12 + temp4;  // 17
	                var temp5 = temp4 + temp2.substring(1, temp2.length); // 17:17:43
//	                now = temp1 + temp5; // 2014/7/6 17:17:43
//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
	                now = now.replace("-"); //  2014-7-6 17:17:43
	            }else {
	                var temp1 = now.substring(0, now.indexOf("下午"));   //2014/7/6
	                var temp2 = now.substring(now.indexOf("下午") + 2, now.length);  // 5:17:43
	                var temp3 = temp2.substring(0, 2);    //  5
	                if (temp3 == 12){
	                    temp3 -= 12;
	                }
	                var temp4 = parseInt(temp3); // 5
	                temp4 = 12 + temp4;  // 17
	                var temp5 = temp4 + temp2.substring(2, temp2.length); // 17:17:43
//	                now = temp1 + temp5; // 2014/7/6 17:17:43
//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
	                now = now.replace("-"); //  2014-7-6 17:17:43
	            }
	        }else {
	            var temp1 = now.substring(0,now.indexOf("上午"));   //2014/7/6
	            var temp2 = now.substring(now.indexOf("上午")+2,now.length);  // 5:17:43
	            var temp3 = temp2.substring(0,1);    //  5
	            var index = 1;
	            var temp4 = parseInt(temp3); // 5
	            if(temp4 == 0 ) {   //  00
	                temp4 = "0"+temp4;
	            }else if(temp4 == 1) {  // 10  11  12
	                index = 2;
	                var tempIndex = temp2.substring(1,2);
	                if(tempIndex != ":") {
	                    temp4 = temp4 + "" + tempIndex;
	                }else { // 01
	                    temp4 = "0"+temp4;
	                }
	            }else {  // 02 03 ... 09
	                temp4 = "0"+temp4;
	            }
	            var temp5 = temp4 + temp2.substring(index,temp2.length); // 07:17:43
//	            now = temp1 + temp5; // 2014/7/6 07:17:43
//	            now = now.replace("/","-"); //  2014-7/6 07:17:43
	            now = now.replace("-"); //  2014-7-6 07:17:43
	        }
	        return now;
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

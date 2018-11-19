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
//			_this.addRemark();
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
			console.log(_this.userInfo.orderIds)
			console.log(_this.userInfo.totalDetail)
			console.log(_this.userInfo.totalCommission)
			console.log(_this.userInfo.sellerId)
			console.log(_this.userInfo.option)
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
                	console.log(res)
					var entity = res.data.entity;
					var deadline = '';
					if(entity.deadline) {
						deadline = _this.formatDateTime(entity.deadline);
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
						console.log(ss)
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
			            if(ss.drawBack != null) {
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
				                	$('#commBusinessStatus').val(zj.data.dictLabel);//发票状态
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
					if(res.data.auditList.length > 0) {
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
							console.log(ll)
							if(ll.orderDetailList.length > 0) {
								$.each(ll.orderDetailList, function(hh,zz) {
									console.log(zz)
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
					
					return;
					
					//注意事项
					if(item.orderType==5){
						$('#notes').html('<ul><li>注：</li><li>一、甲方是万户通平台的运营商，乙方是箱包厂商，丙方是采购商。丙方委托甲方进行商品采购，并通过甲方向乙方支付货款。三方在友好协商、平等互利的基础上，就甲方提供商品采购服务事宜形成本订单。</li><li>二、自丙方下单完成起，至丙方支付完毕本订单所有费用时止。</li><li>三、乙、丙双方确定商品质量标准。丙方负责收货、验货，乙方负责提供质量达标的商品，如果商品达不到丙方要求，丙方有权要求乙方退换货。甲方不承担任何商品质量责任。</li><li>四、商品交付丙方前，丙方须支付全部货款。如果丙方不能及时付款，甲方有权利拒绝交付商品。</li><li>五、本订单商品价格为未含税价，如果丙方需要发票，乙方有义务提供正规发票，税点由丙方承担。</li><li>六、乙方保证其提供的商品具有完整的所有权，并达到国家相关质量标准要求。因乙方商品问题（包括但不限于质量问题、版权问题、款式不符、数量不符等）给甲方及（或）丙方或其他方造成损失的，须由乙方赔偿全部损失。</li><li>七、本订单在万户通平台经甲、乙、丙三方线上确认后生效，与纸质盖章订单具有同等的法律效力。在系统出现故障时，甲、乙、丙三方也可采用纸质订单并签字或盖章后生效。</li></ul>')
					}else {
						$('#notes').html('<ul><li>注：</li><li>（1）本订单作为丙方采购、验货和收货的依据，丙方可持本订单及付款凭证到甲方的仓库提货。</li><li>（2）本订单商品价格为未含税价，如果丙方需要发票，则乙方有义务提供正规发票，税点由丙方承担。</li><li>（3）乙方负责处理在甲方平台上销售的全部商品的质量问题和售后服务问题，由乙丙双方自行解决；甲方可配合协调处理；</li><li>（4）本订单在万户通平台经甲、乙、丙三方线上确认后生效，与纸质盖章订单具有同等的法律效力。在系统出现故障时，甲、乙、丙三方也可采用纸质订单签字或盖章后生效。</li></ul>')
					}
					_this.statusListHtml(res.data)
					_this.checkProcessHtml(res.data);
					_this.commodityHtml(res.data);
					_this.paylistHtml(res.data);//支付信息					
                }
            });
		},
		//添加备注
		addRemark:function(){
			var _this = this;
			document.getElementById("addRemarkBtn").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); 
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入你要添加的备注', '系统提示！', '系统提示！',btnArray, function(e) {
					if(e.index == 1) {
						var inText = e.value;
                        if (inText == null) {
			                return false;
			            }
                        $.ajax({
			                type:"post",
			                url:"/a/biz/order/bizOrderComment/addComment",
			                data:{orderId:$('#ordId').val(),remark:inText},
			                success:function (data) {
			                    if (data == "error") {
			                        mui.toast("添加订单备注失败，备注可能为空!");
			                    }
			                    if (data == "ok") {
			                        mui.toast("添加订单备注成功!");
			                        window.setTimeout(function(){
					                    _this.getData();
					                },500);
			                    }
			                }
			            });
					} else {						
					}
				})
			});
		},
		//供应商信息
		supplier:function(supplierId){						
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizOrderHeader/selectVendInfo",
                data: {vendorId:supplierId},		                
                dataType: "json",
                success: function(rest){
                	if(rest){
                		if(rest.vendName){
                			$('#insupplier').val(rest.vendName);//供应商
                		}else{
                			$('#insupplier').parent().hide();//供应商
                		}
                		if(rest.cardNumber){
                			$('#insupplierNum').val(rest.cardNumber);//供应商卡号
                		}else{
                			$('#insupplierNum').parent().hide();//供应商卡号
                		}
						if(rest.payee){
                			$('#insupplierMoney').val(rest.payee);//供应商收款人
                		}else{
                			$('#insupplierMoney').parent().hide();//供应商收款人
                		}
						if(rest.bankName){
                			$('#insupplierBank').val(rest.bankName);//供应商开户行
                		}else{
                			$('#insupplierBank').parent().hide();//供应商收款人
                		}						
						//供应商合同
						if(rest.compactImgList != undefined){
							$.each(rest.compactImgList,function (m, n) {
                                $("#insuppliercontract").append("<a href=\"" + n.imgServer + n.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + n.imgServer + n.imgPath + "\"></a>");
                            });
						}else{
							$('#insuppliercontract').parent().hide();
						}
						//供应商身份证
						if (rest.identityCardImgList != undefined) {
                        $.each(rest.identityCardImgList,function (i, card) {
                            $("#insuppliercardID").append("<a href=\"" + card.imgServer + card.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + card.imgServer + card.imgPath + "\"></a>");
                           });
                        }else{
                        	$('#insuppliercardID').parent().hide();
                        }
                	}else{
                		$('#insupplier').parent().hide();//供应商
						$('#insupplierNum').parent().hide();//供应商卡号
						$('#insupplierMoney').parent().hide();//供应商收款人
						$('#insupplierBank').parent().hide();//供应商开户行
						$('#insuppliercontract').parent().hide();//供应商合同
						$('#insuppliercardID').parent().hide();//供应商身份证
                	}
				}
			});
		}, 
		//排产信息接口
		scheduling:function(idval){
			var _this = this;			
			$.ajax({
                type: "GET",
                url: "/a/biz/po/bizPoHeader/scheduling4Mobile",
                data: {id:idval},
                dataType: "json",
                success: function(res){
                	if (res.data.detailHeaderFlg != true && res.data.detailSchedulingFlg != true) {
                        $("#inSchedultype").val("未排产")
                        $("#stockGoods").hide();
                        $("#schedulingPlan_forHeader").hide();
                        $("#schedulingPlan_forSku").hide();
                    }
                	//按订单排产
                	if (res.data.detailHeaderFlg == true) {
                        $("#inSchedultype").val("按订单排产")
                        $("#stockGoods").show();
                        $("#schedulingPlan_forHeader").show();
                        $("#schedulingPlan_forSku").hide();

                        var poDetailList = res.data.bizPoHeader.poDetailList;
                        var poDetailHtml = "";
                        $.each(poDetailList,function(n,v){
                        	poDetailHtml +='<li class="mui-table-view-cell mui-media">'+
								'<div class="photoParent mui-pull-left app_pa">'+
									'<img class="app_pa" src="'+v.skuInfo.productInfo.imgUrl+'">'+
								'</div>'+
								'<div class="mui-media-body app_w72p app_fr">'+
									'<div class="mui-input-row">'+
										'<label>品牌名称：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.skuInfo.productInfo.brandName +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>商品名称：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.skuInfo.name +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>商品货号：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.skuInfo.itemNo +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>采购数量：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.ordQty +'" reqQty disabled>'+
									'</div>'+
									'<div class="mui-input-row" id="unitprice">'+
										'<label>结算价：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.unitPrice +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>总金额：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.ordQty * v.unitPrice +'" disabled>'+
									'</div>'+
								'</div>'+
							'</li>'
                        })
                        $("#purchaseMenu").append(poDetailHtml);
                        var unitPriceList=$('#purchaseMenu #unitprice');
						$.each(unitPriceList,function(z,x){
							if(_this.unitPriceFlag==true){
								$(x).show();
							}else{
								$(x).hide();
							}
						})
                        //按订单排产中的排产记录
                        var bizCompletePalns = res.data.bizCompletePalns;
                        var schedulingHeaderHtml = "";
                        $.each(bizCompletePalns,function(n,v){
                        	schedulingHeaderHtml +='<li class="mui-table-view-cell mui-media app_pl0">'+
								'<div class="mui-media-body">'+
									'<div class="mui-input-row">'+
										'<label>完成日期：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(v.planDate) +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>排产数量：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.completeNum +'" disabled>'+
									'</div>'+
								'</div>'+
							'</li>'
                        });
                        $("#schedulingHeader").append(schedulingHeaderHtml);                        
                        //按订单排产中的排产备注
                        var remarkHtml = "<textarea id='schRemarkOrder' readonly>" + res.data.bizPoHeader.bizSchedulingPlan.remark + "</textarea>";
                        $(".schedulingHeaderRemark").append(remarkHtml);
                    }
                	//按商品排产
                	if (res.data.detailSchedulingFlg == true) {
                        $("#inSchedultype").val("按商品排产")
                        $("#stockGoods").hide();
                        $("#schedulingPlan_forHeader").hide();
                        $("#schedulingPlan_forSku").show();
                        var poDetailLists = res.data.bizPoHeader.poDetailList;
                        var poDetailHtmls = ""
                        $.each(poDetailLists,function(n,v){
//                      	console.log(v)
                        	poDetailHtmls +='<li class="mui-table-view-cell mui-media">'+
								'<div class="photoParent mui-pull-left app_pa">'+
									'<img class="app_pa" src="'+v.skuInfo.productInfo.imgUrl+'">'+
								'</div>'+
								'<div class="mui-media-body app_w72p app_fr">'+
									'<div class="mui-input-row">'+
										'<label>品牌名称：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.skuInfo.productInfo.brandName +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>商品名称：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.skuInfo.name +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>商品货号：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.skuInfo.itemNo +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>采购数量：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.ordQty +'" reqQty disabled>'+
									'</div>'+
									'<div class="mui-input-row" id="unitprice">'+
										'<label>结算价：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.unitPrice +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>总金额：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.ordQty * v.unitPrice +'" disabled>'+
									'</div>'+
								'</div>'+
							'</li>'	+
//							'<div class="mui-row app_bline2 app_bline4">'+
//								'<div class="mui-row mui-col-xs-6 ">'+
//										'<label class="app_pr0 app_f13">总申报数量：</label>'+
//										'<input type="text" class="mui-input-clear" value="'+v.ordQty+'" disabled>'+
//								'</div>'+
//								'<div class="mui-row mui-col-xs-6 ">'+
//										'<label class="app_pr0 app_f13">待排产量：</label>'+
//										'<input type="text" class="mui-input-clear" value="" disabled>'+
//								'</div>'+
//								'<div class="mui-row mui-col-xs-6 ">'+
//										'<label class="app_pr0 app_f13">已排产数量：</label>'+
//										'<input type="text" class="mui-input-clear" value="" disabled>'+
//								'</div>'+
//								'<div class="mui-row mui-col-xs-6 ">'+
//										'<label class="app_pr0 app_f13" id="scolor"> 已排产完成</label>'+
//								'</div>'+
//							'</div>'+
							'<div class="mui-row app_bline2">'+
								'<label class="app_pr0 app_f13">排产记录:</label>'+
								'<ul id="schedulingHeaders" class="schedulingHeaders mui-table-view app_fr app_w70p">'+_this.eachCompletePaln(v)+'</ul>'+
							'</div>'
                        });
                        $("#purchaseMenus").append(poDetailHtmls);
                        var unitPriceLists=$('#purchaseMenus #unitprice');
						$.each(unitPriceLists,function(z,x){
							if(_this.unitPriceFlag==true){
								$(x).show();
							}else{
								$(x).hide();
							}
						})
                        //按商品排产中的排产备注
                        var remarkHtmls = "<textarea id='schRemarkOrder' readonly>" + res.data.bizPoHeader.bizSchedulingPlan.remark + "</textarea>";
                        $(".schedulingHeaderRemarks").append(remarkHtmls);    
                	}
				}
			})
		},
		eachCompletePaln: function(ak) {
			var _this = this;
			var completePalnHtml = "";
			$.each(ak.bizSchedulingPlan.completePalnList,function(a,k){
            	completePalnHtml +='<li class="mui-table-view-cell mui-media app_pr app_pl0">'+
				'<div class="mui-media-body">'+
					'<div class="mui-input-row">'+
						'<label>完成日期：</label>'+
						'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(k.planDate) +'" disabled>'+
					'</div>'+
					'<div class="mui-input-row">'+
						'<label>排产数量：</label>'+
						'<input type="text" class="mui-input-clear" value="'+ k.completeNum +'" disabled>'+
					'</div>'+
				'</div>'+
			    '</li>'
            });
            return completePalnHtml;
		},
		//状态流程
		statusListHtml:function(data){
			var _this = this;
			var statusLen = data.statusList.length;
			if(statusLen > 0) {
				var pHtmlList = '';
				$.each(data.statusList, function(i, item) {
//					console.log(item)
					var step = i + 1;
					pHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>处理人:</label>'+
								'<input type="text" value="'+ item.createBy.name +'" class="mui-input-clear" disabled>'+
						    '</div>'+
							'<div class="mui-input-row">'+
						        '<label>状态:</label>'+
						        '<input type="text" class="mui-input-clear" disabled value="'+ data.stateDescMap[item.bizStatus] +'">'+
						    	'<label>时间:</label>'+
						        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
				});
				$("#staEvoMenu").html(pHtmlList)
			}else{
				$("#staEvoMenu").parent().hide();
			}
		},
		checkProcessHtml:function(data){
			var _this = this;
			var auditLen = data.auditList.length;
			if(auditLen > 0) {
				var CheckHtmlList ='';
				$.each(data.auditList, function(i, item) {
					//状态
					var ProcessName = '';
					var step = i + 1;
					var current = item.current;
					if(current !== 1) {
						if(item.objectName == 'ORDER_HEADER_SO_LOCAL') {
							ProcessName = item.jointOperationLocalProcess.name
						}
						if(item.objectName == 'ORDER_HEADER_SO_ORIGIN') {
							ProcessName = item.jointOperationOriginProcess.name
						}
						if(item.objectName == 'biz_po_header') {
							ProcessName = item.purchaseOrderProcess.name
						}
						if(item.objectName == 'biz_order_header') {
							if(data.entity2.payProportion == 1) {
								ProcessName = item.doOrderHeaderProcessFifth.name
							}
							if(data.entity2.payProportion == 2) {
								ProcessName = item.doOrderHeaderProcessAll.name
							}
						}
						//处理人
						var userName ="";
						if(item.user){
							userName = item.user.name;
						}else{
							userName = "";
						}
						//批注
						var Description ="";
						if(item.user){
							Description = item.description;
						}else{
							Description = "";
						}						
						CheckHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>处理人:</label>'+
								'<input type="text" value="'+ userName +'" class="mui-input-clear" disabled>'+
						    '</div>'+
							'<div class="mui-input-row">'+
						        '<label>批注:</label>'+
						        '<input type="text" value="'+ Description +'" class="mui-input-clear" disabled>'+
						    	'<label>状态:</label>'+
						        '<input type="text" value=" '+ ProcessName +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
					}
					if(current == 1) {
						if(item.objectName == 'ORDER_HEADER_SO_LOCAL') {
							ProcessName = item.jointOperationLocalProcess.name
						}
						if(item.objectName == 'ORDER_HEADER_SO_ORIGIN') {
							ProcessName = item.jointOperationOriginProcess.name
						}
						if(item.objectName == 'biz_po_header') {
							ProcessName = item.purchaseOrderProcess.name
						}
						if(item.objectName == 'biz_order_header') {
							ProcessName = item.doOrderHeaderProcessFifth.name
						}
						CheckHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>当前状态:</label>'+
								'<input type="text" value="'+ ProcessName +'" class="mui-input-clear" disabled>'+
						   		'<label>时间:</label>'+
						        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
					}
				});
				$("#staCheckMenu").html(CheckHtmlList);
			}else{
				$("#staCheckMenu").parent().hide();
			}
		},
		//支付列表
        paylistHtml:function(data){
        	var _this = this;
        	var htmlPaylist = '';
        	if(data.statu != '' && data.statu =='unline'){
        		var orWaterStatus = '';
                $.ajax({
            		type: "GET",
	                url: "/a/sys/dict/listData",
	                data:{type:'biz_order_unline_bizStatus'},
	                dataType: "json",
	                async:false,
	                success: function(zl){
	                	$.each(zl,function(z, l) {
	                		$.each(data.unlineList,function(u, n) {
	                		     if(n.bizStatus==l.value){
	                		     	orWaterStatus=l.label
	                		     }
	                	    });
	                	});
            		}
            	});
        		$.each(data.unlineList, function(i, item) {
					htmlPaylist +='<li class="mui-table-view-cell mui-media payList">'+
						'<div class="mui-media-body">'+
							'<div class="mui-input-row">'+
								'<label>流水号：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.serialNum +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>支付金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.unlinePayMoney.toFixed(2) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>实收金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.realMoney.toFixed(2) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>状态：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ orWaterStatus +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>创建时间：</label>'+
								'<input type="text" class="mui-input-clear realitypayTime" value="'+ _this.formatDateTime(item.createDate) +'" disabled>'+
							'</div>'+
						'</div>'+
					'</li>'
			    });
			    $("#inPaylist").html(htmlPaylist);
        	}else{
        		$('#inPaylistbox').hide();
        	}       	
        },
		commodityHtml: function(data) {
			var _this = this;
			var orderDetailLen = data.bizOrderHeader.orderDetailList.length;
			if(orderDetailLen > 0) {
				var htmlCommodity = '';
				$.each(data.bizOrderHeader.orderDetailList, function(i, item) {
					var opShelfInfo = '';
					if(data.orderType != data.PURSEHANGER){
						if(item.shelfInfo.opShelfInfo) {
							opShelfInfo = item.shelfInfo.opShelfInfo.name
						}else {
							opShelfInfo = ''
						}
					}					
					var primaryMobile = '';
					if(item.primary.mobile) {
						primaryMobile = item.primary.mobile
					}else {
						primaryMobile = ''
					}
					var suplyisName = '';
					if(data.bizOrderHeader.bizStatus>=15 && data.bizOrderHeader.bizStatus!=45) {
						suplyisName = item.suplyis.name
					}else {
						suplyisName = ''
					}
					//总额
					var totalMoney="";
					if(item.unitPrice !=null && item.ordQty !=null){
						totalMoney=(item.unitPrice * item.ordQty).toFixed(2);						
					}
					htmlCommodity += '<div class="mui-row app_bline commodity" id="commoditybox">' +
	                    
                    	'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>详情行号:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.lineNo + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6" id="opShelfInfo">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>货架名称:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + opShelfInfo  + '" disabled></div></li></div></div>' +
	                   
                    	'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>供应商:</label>' + 
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.vendor.name + '" disabled></div></li></div>' +
//	                    隐藏结算价
	                    '<div class="mui-col-sm-6 mui-col-xs-6" id="buyPrice">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>商品结算价:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.buyPrice + '" disabled></div></li></div>'+
	                    '</div>' +
	                   
                    	 '<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>供应商电话:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + primaryMobile + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>商品单价:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div></div>'+
					
						'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>采购数量:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.ordQty + '" disabled></div></li></div>' +
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
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.sentQty + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>发货方:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + suplyisName + '" disabled></div></li></div></div>'+
						
						'<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +   
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">商品名称:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuName + '" disabled></div></li></div>'+
						
                    	'<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">商品货号:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div>' +
	                    
	                    '<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">商品编号:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.partNo + '" disabled></div></li></div>' +
	                   
	                    '<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">创建时间:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + _this.formatDateTime(item.createDate) + '" disabled></div></li></div>' +
	                    
                    '</div>'
				});
				$("#staCommodity").html(htmlCommodity);
				if(data.orderType == data.PURSEHANGER){
					var buyPriceArr=$('.commodity #opShelfInfo')			
					$.each(buyPriceArr, function(o,p) {
						$(p).hide();
					});
				}
//				结算价隐藏
				if(_this.unitPriceFlag==true){
					if(data.bizOrderHeader.orderDetails == 'details' || data.bizOrderHeader.orderNoEditable == 'editable' || data.bizOrderHeader.flag == 'check_pending'){
						var buyPriceArr=$('#commoditybox #buyPrice');			
						$.each(buyPriceArr, function(o,p) {
							$(p).show();
						});
						
					}else{
						var buyPriceArr=$('#commoditybox #buyPrice');			
						$.each(buyPriceArr, function(o,p) {
							$(p).hide();
						});
					}
				}else{
					var buyPriceArr=$('#commoditybox #buyPrice');			
					$.each(buyPriceArr, function(o,p) {
						$(p).hide();
					});
				}
				
			}
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

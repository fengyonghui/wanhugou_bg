(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.prew = false;
		this.buyPriceFlag = false;
		this.unitPriceFlag = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getPermissionList('biz:order:buyPrice:view','buyPriceFlag')//佣金权限
			this.getPermissionList1('biz:order:unitPrice:view','unitPriceFlag')//结算价权限
			this.buyPrice(); //佣金显示
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
					//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.btnshow();
			_this.getData();
			_this.changePrice();
		},
		btnshow: function() {
			var _this = this;
//			$('#showMoney').hide()
			$('input[type=radio]').on('change', function() {
				if(this.id && this.checked) {
//					$('#showMoney').show()
					_this.prew = true
				} else {
//					$('#showMoney').hide()
					_this.prew = false
				}
			})
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
        getPermissionList1: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.unitPriceFlag = res.data;
                }
            });
        },
        buyPrice:function(){
        	var _this = this;
			if(_this.buyPriceFlag==true){
				$('#staCommission').parent().show();
			}else{
				$('#staCommission').parent().hide();
			}
		},
		getData: function() {
			var _this = this;
			console.log(_this.userInfo.stcheckIdTxt)
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizOrderHeader/form4Mobile",
                data: {
                	id: _this.userInfo.staOrdId,
                	orderDetails: 'details',
	                flag: _this.userInfo.flagTxt
                },
                dataType: "json",
                success: function(res){
                	$('#orderTypebox').hide();
                	if(res.data.bizOrderHeader.flag=='check_pending') {
                		if(res.data.orderType == 5) {
                			$('#nochecked').attr("checked","false" );
                			$('#yes').attr("checked","checked" );
                		}
                	}
                	//调取供应商信息
                	if(res.data.entity2){
                		var officeId = res.data.entity2.sellersId;
                	    $('#supplierId').val(officeId);
                	    _this.supplier($('#supplierId').val());
                	}else{
                		$('#insupplier').parent().hide();//供应商
						$('#insupplierNum').parent().hide();//供应商卡号
						$('#insupplierMoney').parent().hide();//供应商收款人
						$('#insupplierBank').parent().hide();//供应商开户行
						$('#insuppliercontract').parent().hide();//供应商合同
					    $('#insuppliercardID').parent().hide();//供应商身份证
                	} 
					$('#firstPart').val(res.data.entity2.customer.name);
					$('#firstPrincipal').val(res.data.custUser.name);
					$('#firstMobile').val(res.data.custUser.mobile);					
					$('#partB').val(res.data.vendUser.vendor.name);
					$('#partBPrincipal').val(res.data.vendUser.name);
					$('#partBMobile').val(res.data.vendUser.mobile);					
					$('#partCPrincipal').val(res.data.orderCenter.consultants.name);
					$('#partCMobile').val(res.data.orderCenter.consultants.mobile);
					if(res.data.appointedTimeList) {
						$.each(res.data.appointedTimeList, function(n, v) {
							$('#staPayTime').val(_this.formatDateTime(v.appointedDate));
							$('#staPayMoney').val(v.appointedMoney);
						})
					}else {
						$('#staPayTime').val();
						$('#staPayMoney').val();
					}
					//订单id
					$('#ordId').val(_this.userInfo.staOrdId);					
					var item = res.data.bizOrderHeader;
					//交货时间
					$('#appointedTime').val(item.bizLocation.appointedTime);
					//标志位
					$('#flag').val(item.flag);
					//选择供货方式 和 注意事项
					if(item.orderType==5){
						$('#notes').html('<ul><li>注：</li><li>一、甲方是万户通平台的运营商，乙方是箱包厂商，丙方是采购商。丙方委托甲方进行商品采购，并通过甲方向乙方支付货款。三方在友好协商、平等互利的基础上，就甲方提供商品采购服务事宜形成本订单。</li><li>二、自丙方下单完成起，至丙方支付完毕本订单所有费用时止。</li><li>三、乙、丙双方确定商品质量标准。丙方负责收货、验货，乙方负责提供质量达标的商品，如果商品达不到丙方要求，丙方有权要求乙方退换货。甲方不承担任何商品质量责任。</li><li>四、商品交付丙方前，丙方须支付全部货款。如果丙方不能及时付款，甲方有权利拒绝交付商品。</li><li>五、本订单商品价格为未含税价，如果丙方需要发票，乙方有义务提供正规发票，税点由丙方承担。</li><li>六、乙方保证其提供的商品具有完整的所有权，并达到国家相关质量标准要求。因乙方商品问题（包括但不限于质量问题、版权问题、款式不符、数量不符等）给甲方及（或）丙方或其他方造成损失的，须由乙方赔偿全部损失。</li><li>七、本订单在万户通平台经甲、乙、丙三方线上确认后生效，与纸质盖章订单具有同等的法律效力。在系统出现故障时，甲、乙、丙三方也可采用纸质订单并签字或盖章后生效。</li></ul>')
					}else {
						$('#notes').html('<ul><li>注：</li><li>（1）本订单作为丙方采购、验货和收货的依据，丙方可持本订单及付款凭证到甲方的仓库提货。</li><li>（2）本订单商品价格为未含税价，如果丙方需要发票，则乙方有义务提供正规发票，税点由丙方承担。</li><li>（3）乙方负责处理在甲方平台上销售的全部商品的质量问题和售后服务问题，由乙丙双方自行解决；甲方可配合协调处理；</li><li>（4）本订单在万户通平台经甲、乙、丙三方线上确认后生效，与纸质盖章订单具有同等的法律效力。在系统出现故障时，甲、乙、丙三方也可采用纸质订单签字或盖章后生效。)</li></ul>')
					}
					//本地发货 id
					if(item.localSendIds) {
						$('#localSendIds').val(item.localSendIds);
					}else {
						$('#localSendIds').val();
					}
					//详细地址
					if(item.bizLocation) {
						$('#provinceId').val(item.bizLocation.province.id); 
						$('#cityId').val(item.bizLocation.city.id); 
						$('#regionId').val(item.bizLocation.region.id); 
					}
					//发票状态
					var invStatusTxt = '';
					$.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {
		                	type:"biz_order_invStatus"
		                },
		                dataType: "json",
		                success: function(res){
		                	$.each(res,function(i,itemss){
		                		 if(itemss.value==item.invStatus){
		                		 	  invStatusTxt = itemss.label 
		                		 }
		                	})
		                	$('#staInvoice').val(invStatusTxt);
						}
					})
					//业务状态
					var statusTxt = '';
					$.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {
		                	type:"biz_order_status"
		                },
		                dataType: "json",
		                success: function(res){
		                	$.each(res,function(i,itemaa){
		                		 if(itemaa.value==item.bizStatus){
		                		 	  statusTxt = itemaa.label 
		                		 }
		                	})
		                	$('#staStatus').val(statusTxt);
						}
					})
					var total = item.totalDetail+item.totalExp+item.freight
					if(total > (item.receiveTotal+item.scoreMoney) && item.bizStatus!=10 && item.bizStatus!=35 && item.bizStatus!=40 && item.bizStatus!=45 && item.bizStatus!=60) {
						$('#staFinal').val("(有尾款)");
					}					
					$('#staPoordNum').val(item.orderNum);
					if(res.data.orderType==8){
						$('#customerName').html('零售用户'+'：');
					}
					if(res.data.orderType!=8){
						$('#customerName').html('经销店名称'+'：');
					}
					$('#staRelNum').val(item.customer.name);//经销店名称
					if(res.data.orderType!=8){
						$('#staCoin').val(item.scoreMoney.toFixed(2));//万户币抵扣
					}else{
						$('#staCoin').parent().hide();
					}
					//结佣状态
					if(res.data.orderType==8){
						$('#commission').parent().show();
					}else{
						$('#commission').parent().hide();
					}
					var comStatusTxt = '';
					$.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {
		                	type:"biz_commission_status"
		                },
		                dataType: "json",
		                success: function(res){
		                	$.each(res,function(i,itemss){
		                		if(itemss.value==item.commissionStatus){
		                		 	comStatusTxt = itemss.label 
		                		}
		                	})
		                	$('#commission').val(comStatusTxt);
						}
					})	
					$('#staPototal').val(item.totalDetail.toFixed(2));
					$('#staAdjustmentMoney').val(item.totalExp);
					$('#staFreight').val(item.freight.toFixed(2));
					var shouldPay = item.totalDetail + item.totalExp + item.freight + item.serviceFee-item.scoreMoney;
					$('#staShouldPay').val(shouldPay.toFixed(2));
					$('#staPoLastDa').val('('+ item.receiveTotal.toFixed(2) + ')');
					var poLastDa = 0;
					if(item.totalDetail+item.totalExp+item.freight+item.serviceFee-item.scoreMoney != 0) {
						poLastDa = ((item.receiveTotal/(item.totalDetail+item.totalExp+item.freight+item.serviceFee-item.scoreMoney))*100).toFixed(2)+'%';
					}
					$('#staPoLastDaPerent').val(poLastDa);
					$('#staServerPrice').val((item.totalExp + item.serviceFee+item.freight).toFixed(2));
					$('#staCommission').val((item.totalDetail - item.totalBuyPrice).toFixed(2));
					$('#staAddprice').val(item.serviceFee.toFixed(2));
					$('#staInvoice').val(invStatusTxt);
					$('#staStatus').val(statusTxt);
					$('#staConsignee').val(item.bizLocation.receiver);
					$('#staMobile').val(item.bizLocation.phone);
					$('#staShippAddress').val(item.bizLocation.pcrName);
					$('#staDateilAddress').val(item.bizLocation.address);
					$('#staEvolve').val();					
					_this.statusListHtml(res.data);
					_this.commodityHtml(res.data);
					_this.comfirDialig(res.data);
					
                }
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
						        '<input type="text" class="mui-input-clear" value="'+ data.stateDescMap[item.bizStatus] +'" disabled>'+
						    	'<label>时间:</label>'+
						        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
				});
				$("#staStatusMenu").html(pHtmlList)
			}
		},
		commodityHtml: function(data) {
			var _this = this;
			var orderDetailLen = data.bizOrderHeader.orderDetailList.length;
			if(orderDetailLen > 0) {
				var htmlCommodity = '';
				$.each(data.bizOrderHeader.orderDetailList, function(i, item) {
					var opShelfInfo = '';
					if(item.shelfInfo.opShelfInfo) {
						opShelfInfo = item.shelfInfo.opShelfInfo.name
					}else {
						opShelfInfo = ''
					}
					var primaryMobile = '';
					if(item.primary.mobile) {
						primaryMobile = item.primary.mobile
					}else {
						primaryMobile = ''
					}
					var repertory = '';
					if(data.bizOrderHeader.flag=='check_pending') {
						repertory = '<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>详情行号:</label>' + 
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.lineNo + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>库存数量:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + data.invSkuNumMap[item.id] + '" disabled></div></li></div></div>' 
					}else {
						repertory = '<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">详情行号:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.lineNo + '" disabled></div></li></div>' 
					}
					htmlCommodity += '<div class="mui-row app_bline commodity" id="' + item.id + '">' +
						repertory +
                    	'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>供应商:</label>' + 
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.vendor.name + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6" id="unitprice">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>商品出厂价:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.buyPrice + '" disabled></div></li></div></div>' +
	                    
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
	                    '<input type="text" class="mui-input-clear" id="" value="' + (item.unitPrice * item.ordQty).toFixed(2) + '" disabled></div></li></div></div>'+
					
						'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>已发货数量:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.sentQty + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>货架名称:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + opShelfInfo + '" disabled></div></li></div></div>'+
						
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
				$("#staCheckCommodity").html(htmlCommodity);
				//结算价判断
				var unitPriceList=$('.commodity #unitprice');
				$.each(unitPriceList,function(z,x){
					if(_this.unitPriceFlag==true){
						$(x).show();
					}else{
						$(x).hide();
					}
				})				
			}
		},
		changePrice: function() {
			var _this = this;
			document.getElementById("changePriceBtn").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
				var btnArray = ['取消', '确定'];
				mui.confirm('确定修改价格吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						var ss = $('#staAdjustmentMoney').val();
						IsNum(ss)
						function IsNum(num) {
							if (num) {
//								var reNum = /^\d+(\.\d+)?$/;
								var orderId = _this.userInfo.staOrdId;
				                var totalExp = $('#staAdjustmentMoney').val();
				                var totalDetail =$('#staPototal').val();
				                var totalDetails = totalDetail * 0.015;
				                var staFreights = $('#staFreight').val();
				                var abss = Math.abs(num);
				                if(num < 0) {
									if(abss > totalDetails) {
										mui.toast("调整金额不能大于总价的1.5%倍！");
										return false;
									}
									if(abss > staFreights) {
										mui.toast("调整金额不能大于运费！");
										return false;
									}
								}
				                $.ajax({
	                                type:"post",
	                                url:"/a/biz/order/bizOrderHeader/saveBizOrderHeader4Mobile",
	                                data:{orderId:orderId,money:totalExp},					                              
	                                success:function(flag){
	                    	            var flagVal=JSON.parse(flag)
	                                    if(flagVal.data.flag=="ok"){
	                                        mui.toast("修改成功！");
	                                       _this.getData();
	                                    }else{
	                                        mui.toast(" 修改失败 ");
	                                    }
	                                }
	                            });
	                            return true;
//								if(reNum.test(num)) {
//				                $.ajax({
//				                    type:"post",
//				                    url:"/a/biz/order/bizOrderHeader/checkTotalExp4Mobile",
//				                    data:{id:orderId,totalExp:totalExp,totalDetail:totalDetail},
//				                    success:function (data) {
//				                    	var dataVal=JSON.parse(data)
//				                        if (dataVal.data.resultValue == "serviceCharge") {
//				                            mui.toast("最多只能优惠服务费的50%，您优惠的价格已经超标！请修改调整金额");
//				                        } else if (dataVal.data.resultValue == "orderLoss") {
//				                            mui.toast("优惠后订单金额不能低于结算价，请修改调整金额");
//				                        } else if (dataVal.data.resultValue == "orderLowest") {
//				                            mui.toast("优惠后订单金额不能低于结算价的95%，请修改调整金额");
//				                        } else if (dataVal.data.resultValue == "orderLowest8") {
//				                            mui.toast("优惠后订单金额不能低于结算价的80%，请修改调整金额");
//				                        } else if (dataVal.data.resultValue == "ok") {
//				                            $.ajax({
//				                                type:"post",
//				                                url:"/a/biz/order/bizOrderHeader/saveBizOrderHeader4Mobile",
//				                                data:{orderId:orderId,money:totalExp},					                              
//				                                success:function(flag){
//				                    	            var flagVal=JSON.parse(flag)
//				                                    if(flagVal.data.flag=="ok"){
//				                                        mui.toast("修改成功！");
//				                                       _this.getData();
//				                                    }else{
//				                                        mui.toast(" 修改失败 ");
//				                                    }
//				                                }
//				                            });
//				                        }
//				                    }
//				                });									
//									return true;
//								} else {
//									mui.toast("价格必须为数字！");
//									return false;
//								}
							}else {
								mui.toast("价格不能为空！");
								return false;
							}
						}
					} else {						
					}
				})
			});
		},
		comfirDialig: function(data) {
			var _this = this;
			document.getElementById("rejectBtns").addEventListener('tap', function() {
				var btnArray = ['取消', '确定'];
				mui.confirm('确定不同意发货吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						_this.rejectData(45)
					} else {						
					}
				})
			});
			document.getElementById("checkBtns").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
				if(data.orderType == 5 && data.statusEnumState == 0) {
	                mui.toast("代采订单需至少付款20%，请付款后刷新页面再审核");
	                return;
	            }
				var btnArray = ['取消', '确定'];
				var choiceTxt = '';
				var hint = '';
				if(data.orderType == data.PURSEHANGER) {
					choiceTxt = '确定同意发货吗?'
					hint = '系统提示!'
				}else {
					choiceTxt = '<div id="changeTxt">'+
				    '本地备货<input style="margin: 10px 15px 10px 5px;" checked="checked" type="radio" name="localOriginType" value="1" class="inputRadio" id="nochecked"/>'+
		        	'产地直发<input style="margin: 10px 15px 10px 5px;" id="yes" type="radio" name="localOriginType" value="0"  class="inputRadio"/></div>'
					hint = '请选择供货方式：'
				}
				mui.confirm(choiceTxt, hint, btnArray, function(choice) {
					if(choice.index == 1) {
						_this.ajaxData(15)
					} else {						
					}
				})
			});
		},
		ajaxData:function(num) {
			var _this = this;
			var r2 = document.getElementsByName("localOriginType");
            var localOriginType = "";
            for (var i = 0; i < r2.length; i++) {
                if (r2[i].checked == true) {
                    localOriginType = r2[i].value;
                }
            }
//          console.log(_this.prew)
//          console.log(localOriginType)
			$.ajax({
				type: "POST",
				url: "/a/biz/order/bizOrderHeader/Commissioner4mobile",
				data:{
					id:$('#ordId').val(),
					flag:$('#flag').val(),
					objJsp:num,
					'bizLocation.address':$('#staDateilAddress').val(),
					'bizLocation.appointedTime': $('#appointedTime').val(),
					localSendIds: $('#localSendIds').val(),
					'bizLocation.province.id': $('#provinceId').val() ,
					'bizLocation.city.id': $('#cityId').val(), 
					'bizLocation.region.id': $('#regionId').val(),
					boo: _this.prew,
					localOriginType:localOriginType
				},
				dataType: "json",
				success: function(res) {
					var stcheckIdTxt = _this.userInfo.stcheckIdTxt;
//					console.log(res)
					if(res.data=='ok'){
						mui.toast('发货成功!')
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
							url: "../../../html/staffMgmtHtml/orderHtml/staOrderList.html",
							extras: {
								staListId:stcheckIdTxt,
								}
							})
			            },800);						
					}
				},
				error: function (e) {
				    //服务器响应失败处理函数
//				    console.info(e);
				}
			});
		},
		rejectData:function(num) {
			var _this = this;
			var r2 = document.getElementsByName("localOriginType");
            var localOriginType = "";
            for (var i = 0; i < r2.length; i++) {
                if (r2[i].checked == true) {
                    localOriginType = r2[i].value;
                }
            }
			$.ajax({
				type: "POST",
				url: "/a/biz/order/bizOrderHeader/Commissioner4mobile",
				data: {
					id:$('#ordId').val(),
					flag:$('#flag').val(),
					objJsp:num,
					'bizLocation.address':$('#staDateilAddress').val(),
					'bizLocation.appointedTime': $('#appointedTime').val(),
					localSendIds: $('#localSendIds').val(),
					'bizLocation.province.id': $('#provinceId').val() ,
					'bizLocation.city.id': $('#cityId').val(), 
					'bizLocation.region.id': $('#regionId').val(),
					localOriginType:localOriginType
				},
				dataType: "json",
				success: function(res) {
					var stcheckIdTxt = _this.userInfo.stcheckIdTxt;
					if(res.data=='comError'){
						mui.toast('发货失败!')
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
								url: "../../../html/staffMgmtHtml/orderHtml/staOrderList.html",
								extras: {
									staListId:stcheckIdTxt,
									}
							})
			            },800);
//						
					}
				}
			});
		},
		formatDateTime: function(unix) {
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







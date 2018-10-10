(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.prew = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
					//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
//			_this.btnshow()
			_this.getData();
//			_this.changePrice();
			_this.addRemark();
		},
//		btnshow: function() {
//			var _this = this;
//			$('input[type=radio]').on('change', function() {
//				if(this.id && this.checked) {
//					_this.prew = true
//				} else {
//					_this.prew = false
//				}
//			})
//		},
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizOrderHeader/form4Mobile",
                data: {
                	id: _this.userInfo.staOrdId,
                	str: 'audit',
	                type: 1
                },
                dataType: "json",
                success: function(res){
                	console.log(res)
//              	if(res.data.bizOrderHeader.flag=='check_pending') {
//              		if(res.data.orderType == 5) {
//              			$('#orderTypebox').hide();
//              			$('#nochecked').attr("checked","false" );
//              			$('#yes').attr("checked","checked" );
//              		}
//              	}
					/*判断是否是品类主管*/
					var ordLastDate = '';
					if(res.data.createPoHeader == 'yes') {	//品类主管审核才生成PO
						ordLastDate = '<label>最后时间：</label>'+
							'<input type="date" class="mui-input-clear" id="lastDate" placehohder="必填！">'+
							'<font>*</font>'
						$('#ordLastDate').append(ordLastDate);
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
                	//红色字体内容
					$('#firstPart').val(res.data.entity2.customer.name);
					$('#firstPrincipal').val(res.data.custUser.name);
					$('#firstMobile').val(res.data.custUser.mobile);					
					$('#partB').val(res.data.vendUser.vendor.name);
					$('#partBPrincipal').val(res.data.vendUser.name);
					$('#partBMobile').val(res.data.vendUser.mobile);					
					$('#partCPrincipal').val(res.data.orderCenter.consultants.name);
					$('#partCMobile').val(res.data.orderCenter.consultants.mobile);
					//付款约定
					if(res.data.appointedTimeList) {
						$.each(res.data.appointedTimeList, function(n, v) {
							$('#staPayTime').val(_this.formatDateTime(v.appointedDate));
							$('#staPayMoney').val(v.appointedMoney);
						})
					}else {
						$('#staPayTime').val();
						$('#staPayMoney').val();
					}
					//备注
					var RemarkHtml="";
					$.each(res.data.commentList, function(q, w) {
						console.log(w)						
						RemarkHtml +='<li class="step_items">'+
							'<div class="step_num_txt">'+
								'<div class="">'+
									w.comments +
							    '</div>'+
								'<div class="">'+
                                    w.createBy.name +
							    '</div>'+
							    '<div class="">'+
                                    _this.formatDateTime(w.createDate) +
							    '</div>'+
							'</div>'+
						'</li>'
						$('#Remarks').html(RemarkHtml);
					})
					//订单id
					$('#ordId').val(_this.userInfo.staOrdId);					
					var item = res.data.bizOrderHeader;
//					console.log(item)
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
					if(total > (item.receiveTotal+item.scoreMoney)  && item.bizStatus!=10 && item.bizStatus!=35 && item.bizStatus!=40 && item.bizStatus!=45 && item.bizStatus!=60) {
						$('#staFinal').val("(有尾款)");
					}					
					$('#staPoordNum').val(item.orderNum);
					$('#staCoin').val(item.scoreMoney.toFixed(2));//万户币抵扣
					$('#staRelNum').val(item.customer.name);
					$('#staPototal').val(item.totalDetail.toFixed(2));
					$('#staAdjustmentMoney').val(item.totalExp);
					$('#staFreight').val(item.freight.toFixed(2));
					var shouldPay = item.totalDetail + item.totalExp + item.freight + item.serviceFee-item.scoreMoney;
					$('#staShouldPay').val(shouldPay.toFixed(2));
					$('#staPoLastDa').val('('+ item.receiveTotal.toFixed(2) + ')');
					var poLastDa = ((item.receiveTotal/(item.totalDetail+item.totalExp+item.freight+item.serviceFee-item.scoreMoney))*100).toFixed(2)+'%';
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
					_this.checkProcessHtml(res.data);//审核流程
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
                	console.log(rest)
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
			}else{
				$("#staStatusMenu").parent().hide();
			}
		},
		//审核流程
		checkProcessHtml:function(data){
			var _this = this;
			var auditLen = data.auditList.length;
			if(auditLen > 0) {
				var CheckHtmlList ='';
				$.each(data.auditList, function(i, item) {
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
						CheckHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>处理人:</label>'+
								'<input type="text" value="'+ item.user.name +'" class="mui-input-clear" disabled>'+
						    '</div>'+
							'<div class="mui-input-row">'+
						        '<label>批注:</label>'+
						        '<input type="text" value="'+ item.description +'" class="mui-input-clear" disabled>'+
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
		commodityHtml: function(data) {
			var _this = this;
			var orderDetailLen = data.bizOrderHeader.orderDetailList.length;
			console.log(orderDetailLen)
			if(orderDetailLen > 0) {
				var htmlCommodity = '';
				$.each(data.bizOrderHeader.orderDetailList, function(i, item) {
//					console.log(data)
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
					var suplyisName = '';
					if(data.bizOrderHeader.bizStatus>=15 && data.bizOrderHeader.bizStatus!=45) {
						suplyisName = item.suplyis.name
					}else {
						suplyisName = ''
					}
					htmlCommodity += '<div class="mui-row app_bline commodity" id="' + item.id + '">' +
	                    
                    	'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>详情行号:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.lineNo + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
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
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
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
				$("#staCheckCommodity").html(htmlCommodity)
			}
		},
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
			                    }
			                }
			            });
					} else {						
					}
				})
			});
		},
//		changePrice: function() {
//			var _this = this;
//			document.getElementById("changePriceBtn").addEventListener('tap', function(e) {
//				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
//				var btnArray = ['取消', '确定'];
//				mui.confirm('确定修改价格吗？', '系统提示！', btnArray, function(choice) {
//					if(choice.index == 1) {
//						var ss = $('#staAdjustmentMoney').val();
//						IsNum(ss)
//						function IsNum(num) {
//							if (num) {
//								var reNum = /^\d+(\.\d+)?$/;
//								if(reNum.test(num)) {
//									var orderId = _this.userInfo.staOrdId;
//					                var totalExp = $('#staAdjustmentMoney').val();
//					                var totalDetail =$('#staPototal').val();
//					                $.ajax({
//					                    type:"post",
//					                    url:"/a/biz/order/bizOrderHeader/checkTotalExp4Mobile",
//					                    data:{id:orderId,totalExp:totalExp,totalDetail:totalDetail},
//					                    success:function (data) {
//					                    	var dataVal=JSON.parse(data)
//					                        if (dataVal.data.resultValue == "serviceCharge") {
//					                            mui.toast("最多只能优惠服务费的50%，您优惠的价格已经超标！请修改调整金额");
//					                        } else if (dataVal.data.resultValue == "orderLoss") {
//					                            mui.toast("优惠后订单金额不能低于结算价，请修改调整金额");
//					                        } else if (dataVal.data.resultValue == "orderLowest") {
//					                            mui.toast("优惠后订单金额不能低于结算价的95%，请修改调整金额");
//					                        } else if (dataVal.data.resultValue == "orderLowest8") {
//					                            mui.toast("优惠后订单金额不能低于结算价的80%，请修改调整金额");
//					                        } else if (dataVal.data.resultValue == "ok") {
//					                            $.ajax({
//					                                type:"post",
//					                                url:"/a/biz/order/bizOrderHeader/saveBizOrderHeader4Mobile",
//					                                data:{orderId:orderId,money:totalExp},					                              
//					                                success:function(flag){
//					                    	            var flagVal=JSON.parse(flag)
//					                                    if(flagVal.data.flag=="ok"){
//					                                        mui.toast("修改成功！");
//					                                       _this.getData();
//					                                    }else{
//					                                        mui.toast(" 修改失败 ");
//					                                    }
//					                                }
//					                            });
//					                        }
//					                    }
//					                });									
//									return true;
//								} else {
//									if(num < 0) {
//										mui.toast("价格不能为负数！");
//									}else {
//										mui.toast("价格必须为数字！");
//									}
//									return false;
//								}
//							}else {
//								mui.toast("价格不能为空！");
//								return false;
//							}
//						}
//					} else {						
//					}
//				})
//			});
//		},
		comfirDialig: function(data) {
			var _this = this;
			console.log(data)
			console.log(_this.userInfo.staOrdId)
			var orderType = data.orderType;
			var createPo = data.createPo;
			document.getElementById("rejectBtns").addEventListener('tap', function() {
				var btnArray = ['否', '是'];
				mui.confirm('确定审核驳回吗？', '系统提示！', btnArray, function(choice) {
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
				if(res.data.createPoHeader == 'yes') {//品类主管审核才生成PO
					var lastDates = $('#lastDate').val();
					if(lastDates == null || lastDates == '') {
						mui.toast('最后付款时间不能为空！')
						return;
					}
				}
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入通过理由：', '通过理由', '', btnArray, function(e) {
					if(e.index == 1) {
						var inText = e.value;
						if(e.value == '') {
							mui.toast('通过理由不能为空！')
							return;
						} else {
							var btnArray = ['否', '是'];
							mui.confirm('确定审核通过吗？', '系统提示！', btnArray, function(choice) {
								if(choice.index == 1) {
									if(orderType == 1) {
										_this.ajaxDataSo(inText,1,orderType,createPo)
									}
									if(orderType == 2) {
										_this.ajaxData(inText,1,orderType,createPo)
									}
								} else {						
								}
							})
						}	
					} else {}
				})
			});
		},
		ajaxDataSo:function(inText, num, orderType, createPo) {
			var _this = this;
//			var r2 = document.getElementsByName("localOriginType");
//          var localOriginType = "";
//          for (var i = 0; i < r2.length; i++) {
//              if (r2[i].checked == true) {
//                  localOriginType = r2[i].value;
//              }
//          }
//          console.log(localOriginType)
			var lastDateTxt = '';
			if(createPo == 'yes') {
				lastDateTxt = $('#lastDate').val() + ' 00:00:00'
			}
			$.ajax({
				type: "GET",
				url: "/a/biz/order/bizOrderHeader/auditSo",
				data:{
					id: _this.userInfo.staOrdId,//采购单id
					currentType: ,//当前审核状态
					auditType: num,//审核标识1：审核通过， 2：驳回
					description: inText,//通过/驳回理由
					orderType: orderType,//订单类型
					createPo: createPo,//是否生成po标识
					lastPayDateVal: lastDateTxt,//最后付款时间
				},
				dataType: "json",
				success: function(res) {
//					var stcheckIdTxt = _this.userInfo.stcheckIdTxt;
					console.log(res)
//					if(res.data=='ok'){
//						mui.toast('发货成功!')
//						window.setTimeout(function(){
//			                GHUTILS.OPENPAGE({
//							url: "../../../html/staffMgmtHtml/orderHtml/staOrderList.html",
//							extras: {
//								staListId:stcheckIdTxt,
//								}
//							})
//			            },800);						
//					}
				},
				error: function (e) {
				    //服务器响应失败处理函数
//				    console.info(e);
				}
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
						mui.toast('审核成功!')
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
						mui.toast('审核失败!')
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







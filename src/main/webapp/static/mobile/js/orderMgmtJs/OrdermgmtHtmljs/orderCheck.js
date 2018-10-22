(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.prew = false;
		this.checkResult = false;
		this.DOPOFlag = false;
		this.POFlag = false;
		this.poId = '';
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
//			权限:
//			biz:order:bizOrderHeader:audit    DO、JO
//			biz:po:bizPoHeader:audit     PO
			this.getPermissionList1('biz:order:bizOrderHeader:audit', 'DOPOFlag');
			this.getPermissionList2('biz:po:bizPoHeader:audit', 'POFlag');
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
					//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.getData();
			_this.addRemark();
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
                    _this.DOPOFlag = res.data;
                }
            });
        },
        getPermissionList2: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.POFlag = res.data;
                }
            });
        },
		getData: function() {
			var _this = this;
			$('#schedulingTxt').hide();
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
					/*判断是否是品类主管*/
					$('#checkBtns').attr('poid',res.data.bizOrderHeader.bizPoHeader.id);
					$('#rejectBtns').attr('poids',res.data.bizOrderHeader.bizPoHeader.id);
					$('#createPo').val(res.data.createPo);
					$("#soId").val(res.data.bizOrderHeader.id);
					var ordLastDate = '';
					if(res.data.createPo == 'yes') {	//品类主管审核才生成PO
						ordLastDate = '<label>最后时间：</label>'+
							'<input type="date" class="mui-input-clear" id="lastDate" placehohder="必填！">'+
							'<font>*</font>'
						$('#ordLastDate').append(ordLastDate);
						$('#schedulingTxt').show();
						_this.schedulGetData(res.data)
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
//					var RemarkHtml="";
//					$.each(res.data.commentList, function(q, w) {
//						RemarkHtml +='<div class="">'+
//						    w.comments
//                          +
//					    '</div>'
//						$('#staPoRemark').html(RemarkHtml);//备注
//					})
					//备注
					var RemarkHtml="";
					$.each(res.data.commentList, function(q, w) {						
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
					_this.statusListHtml(res.data);//状态流程
					_this.checkProcessHtml(res.data);//审核流程
					_this.commodityHtml(res.data);//商品信息
					var str = _this.userInfo.str;//审核
					console.log(str)
					if(res.data.bizOrderHeader.str == 'audit' && res.data.bizOrderHeader.bizPoHeader.commonProcessList != null && res.data.bizOrderHeader.bizPoHeader.commonProcessList.length > 0){
						$('#currentTypes').val(res.data.bizOrderHeader.bizPoHeader.commonProcess.purchaseOrderProcess.code)
					}
					if(str == "audit") {
						//支出信息的审核
						console.log('支出审核')
						_this.comfirDialigs();
					}else {
						//订单审核
						console.log('订单审核')
						_this.comfirDialig(res.data);//审核
					}
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
		//审核流程
		checkProcessHtml:function(data){
			var _this = this;
			var auditLen = data.auditList.length;
			if(auditLen > 0) {
				var CheckHtmlList ='';
				$.each(data.auditList, function(i, item) {
					$('#audiType').val(item.type);
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
				$("#orCheckCommodity").html(htmlCommodity)
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
		comfirDialigs: function() {
			var _this = this;
			document.getElementById("rejectBtns").addEventListener('tap', function() {
//				var id= $(this).attr('soId');
//				var currentType= $('#currentType').val();
				var id= $(this).attr('poids');
				var currentType= $('#currentTypes').val();
				var btnArray = ['否', '是'];
				mui.confirm('确认驳回审核吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						var btnArray = ['取消', '确定'];
						mui.prompt('请输入驳回理由：', '驳回理由', '', btnArray, function(a) {
							if(a.index == 1) {
								var rejectTxt = a.value;
								if(a.value == '') {
									mui.toast('驳货理由不能为空！')
								} else {
									_this.payOutAudit(id,rejectTxt, 2,currentType)
								}
							} else {}
						})
					} else {}
				})
			});
			document.getElementById("checkBtns").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault();
//				var id= $(this).attr('soId');
//				var currentType= $('#currentType').val();
				var id= $(this).attr('poid');
				var currentType= $('#currentTypes').val();
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入通过理由：', '通过理由', '', btnArray, function(e) {
					if(e.index == 1) {
						var inText = e.value;
						if(e.value == '') {
							mui.toast('通过理由不能为空！')
							return;
						} else {
							var btnArray = ['否', '是'];
							mui.confirm('确认通过审核吗？', '系统提示！', btnArray, function(choice) {
								if(choice.index == 1) {
									_this.payOutAudit(id,inText, 1,currentType)
								} else {}
							})
						}
					} else {}
				})
			});
		},
		payOutAudit: function(id,inText, num,currentType) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/audit",
				data: {
					id: id,
					currentType: currentType,
					fromPage: 'orderHeader',
					auditType: num,
					description: inText
				},
				dataType: "json",
				success: function(res) {
					if(res.ret == true) {
						mui.toast('操作成功!')
						GHUTILS.OPENPAGE({
							url: "../../../html/orderMgmtHtml/orderpaymentinfo.html",
							extras: {}
						})
					}
					if(res.ret == false) {
						mui.toast(res.errmsg)
					}
				},
				error: function(e) {
					//服务器响应失败处理函数
				}
			});

		},
		comfirDialig: function(data) {
			var _this = this;
			var id = data.entity2.bizPoHeader.id;
			var ordType = '';
			var commonProcessList = '';
			if(data.entity2.bizPoHeader) {
				commonProcessList = data.entity2.bizPoHeader.commonProcessList;
				var comProListLen = commonProcessList.length;
			}
			var bizPoHeader = data.entity2.bizPoHeader;
			if(_this.DOPOFlag == true) {
				/*代采订单*/
				if(data.entity2.str == 'audit' && data.entity2.orderType == data.PURCHASE_ORDER) {
					if(commonProcessList == null) {
						ordType = 'DO'
					}
				}
				/*普通订单*/
				if(data.entity2.str == 'audit' && (data.type != data.REFUND || data.type != data.ORDINARY_ORDER)) {
					if(data.entity2.orderType == 1 && data.currentAuditStatus.type != 777 && data.currentAuditStatus.type != 666) {
						ordType = 'JO'
					}
				}
			}	
			//采购单---支出信息审核
			if(_this.POFlag == true) {
				if(data.entity2.str == 'audit') {
					if(data.orderType != data.PURSEHANGER) {
						if(commonProcessList != null 
						&& comProListLen > 0
						&& data.currentAuditStatus.type == 777 || data.currentAuditStatus.type == 666) {
							$("#currentType").val(data.purchaseOrderProcess.code);
							ordType = 'PO'
						}
					}
					if(data.orderType == data.PURSEHANGER) {
						if(commonProcessList != null
						&& comProListLen > 0) {
							$("#currentType").val(data.purchaseOrderProcess.code);
							ordType = 'PO'
						}
					}
				}
			}
			var createPo = data.createPo;
			document.getElementById("rejectBtns").addEventListener('tap', function() {
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入驳回理由：', '驳回理由', '', btnArray, function(a) {
					if(a.index == 1) {
						var rejectTxt = a.value;
						if(a.value == '') {
							mui.toast('驳回理由不能为空！')
							return;
						} else {
							var btnArray = ['取消', '确定'];
							mui.confirm('确定驳回该流程吗？', '系统提示！', btnArray, function(choice) {
								if(choice.index == 1) {
									if(ordType == 'JO') {
										_this.auditJo(rejectTxt,2,createPo,data)
									}
									if(ordType == 'DO') {
										_this.auditDo(rejectTxt,2,createPo,data)
									}
									if(ordType == 'PO') {
										_this.auditPo(rejectTxt,2,id)
									}
								} else {}
							})
						}
					} else {}
				})
			});
			document.getElementById("checkBtns").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
				if(data.entity2.orderType == 5 && data.statusEnumState == 0) {
	                mui.toast("代采订单需至少付款20%，请付款后刷新页面再审核");
	                return;
	            }
				if(createPo == 'yes') {//品类主管审核才生成PO
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
									if(_this.checkResult == true) {
										_this.afterAjaxData(data)
									}
									if(_this.checkResult == false) {
										if(ordType == 'JO') {
											_this.auditJo(inText,1,createPo,data)
										}
										if(ordType == 'DO') {
											_this.auditDo(inText,1,createPo,data)
										}
										if(ordType == 'PO') {
											_this.auditPo(inText,1,id)
										}
									}
								} else {}
							})
						}	
					} else {}
				})
			});
		},
		afterAjaxData: function(dm) {
			var _this = this;
			var schedulingType = $("input[name='schedulType']:checked").val();
            if (schedulingType == 0) {
//          	var purchNums = $('#purchNum').val();
//          	if(purchNums != undefined) {
            		_this.saveComplete("0", _this.poId);
//          	}
            }
            if (schedulingType == 1) {
//          	var commdNums = $('.commdNum').val();
//          	if(commdNums != undefined) {
            		_this.batchSave("1", _this.poId, dm);
//          	}
            }
		},
		auditJo:function(inText, num, createPo, vn) {
			var _this = this;
			var lastDateTxt = '';
			if(createPo == 'yes' && num == 1) {
				lastDateTxt = $('#lastDate').val() + ' 00:00:00'
			}
			var suplys = vn.entity2.suplys;
			var orderType = 1;
			if (suplys == 0 || suplys == 721) {
                orderType = 0;
            }
			var audiType = $('#audiType').val();
			$.ajax({
				type: "GET",
				url: "/a/biz/order/bizOrderHeader/auditSo",
				data:{
					id: _this.userInfo.staOrdId,//采购单id
					currentType: audiType,//当前审核状态
					auditType: num,//审核标识1：审核通过， 2：驳回
					description: inText,//通过/驳回理由
					orderType: orderType,//订单类型
					createPo: createPo,//是否生成po标识
					lastPayDateVal: lastDateTxt,//最后付款时间
				},
				dataType: "json",
				success: function(res) {
					_this.checkResult = res.ret
                    if(_this.checkResult == true || _this.checkResult == 'true') {
						if(num == 2) {
							mui.toast('操作成功!')
							GHUTILS.OPENPAGE({
								url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
								extras: {}
							})
						}
						if(num == 1) {
							if($('#createPo').val() == 'yes') {
								//订单排产
		                        var resultData = res.data;
		                        var resultDataArr = resultData.split(",");
		                        if(resultDataArr[0] == "采购单生成") {
		                            _this.poId = resultDataArr[1];
		                            var schedulingType = $("input[name='schedulType']:checked").val();
		                            if (schedulingType == 0) {
//		                            	var purchNums = $('#purchNum').val();
//		                            	if(purchNums != undefined) {
		                            		_this.saveComplete("0", _this.poId);
//		                            	}
		                            }
		                            if (schedulingType == 1) {
//		                            	var commdNums = $('.commdNum').val();
//		                            	if(commdNums != undefined) {
		                            		_this.batchSave("1", _this.poId, vn);
//		                            	}
		                            }
		                        }
							}else {
								mui.toast('操作成功!')
								GHUTILS.OPENPAGE({
									url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
									extras: {}
								})
							}
						}
                    }else {
                        alert("操作失败！");
					}
				},
				error: function (e) {
				    //服务器响应失败处理函数
//				    console.info(e);
				}
			});
		},
		auditDo:function(inText, num, createPo,vn) {
			var _this = this;
			var lastDateTxt = '';
			if(createPo == 'yes' && num == 1) {
				lastDateTxt = $('#lastDate').val() + ' 00:00:00'
			}
			var audiType = $('#audiType').val();
			$.ajax({
				type: "GET",
				url: "/a/biz/order/bizOrderHeader/audit",
				data:{
					id: _this.userInfo.staOrdId,//采购单id
					currentType: audiType,//当前审核状态
					auditType: num,//审核标识1：审核通过， 2：驳回
					description: inText,//通过/驳回理由
					createPo: createPo,//是否生成po标识
					lastPayDateVal: lastDateTxt,//最后付款时间
				},
				dataType: "json",
				success: function(res) {
					_this.checkResult = res.ret
                    if(_this.checkResult == true || _this.checkResult == 'true') {
						if(num == 2) {
							mui.toast('操作成功!')
							GHUTILS.OPENPAGE({
								url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
								extras: {}
							})
						}
						if(num == 1) {
							if($('#createPo').val() == 'yes') {
								//订单排产
		                        var resultData = res.data;
		                        var resultDataArr = resultData.split(",");
		                        if(resultDataArr[0] == "采购单生成") {
		                            _this.poId = resultDataArr[1];
		                            var schedulingType = $("input[name='schedulType']:checked").val();
		                            if (schedulingType == 0) {
//		                            	var purchNums = $('#purchNum').val();
//		                            	if(purchNums != undefined) {
		                            		_this.saveComplete("0", _this.poId);
//		                            	}
		                            }
		                            if (schedulingType == 1) {
//		                            	var commdNums = $('.commdNum').val();
//		                            	if(commdNums != undefined) {
		                            		_this.batchSave("1", _this.poId, vn);
//		                            	}
		                            }
		                        }
							}else {
								mui.toast('操作成功!')
								GHUTILS.OPENPAGE({
									url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
									extras: {}
								})
							}
						}
                    }else {
                        alert("操作失败！");
					}
				},
				error: function (e) {
				    //服务器响应失败处理函数
//				    console.info(e);
				}
			});
		},
		auditPo: function(Text,num,id) {
			var _this = this;
            var currentType = $('#currentType').val();
            $.ajax({
                url: '/a/biz/po/bizPoHeader/audit',
                dataType: "json",
                data: {
                	"id": id, 
                	"currentType": currentType, 
                	"auditType": num, 
                	"description": Text, 
                	"fromPage": "orderHeader"
            	},
                type: 'get',
                success: function (result) {
                    if(result.ret == true || result.ret == 'true') {
                        mui.toast('操作成功!')
						GHUTILS.OPENPAGE({
							url: "../../../html/orderMgmtHtml/orderpaymentinfo.html",
							extras: {}
						})
                    }else {
                        alert(result.errmsg);
                    }
                },
                error: function (error) {
                    console.info(error);
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
		},
		//排产
		schedulGetData: function(chData) {
			var _this = this;
        	var htmlPurch = '';
        	var totalOrdQtyNumNums = 0;
        	$.each(chData.bizOrderHeader.orderDetailList, function(i,item) {
				var primaryMobile = '';
				if(item.primary.mobile) {
					primaryMobile = item.primary.mobile
				}else {
					primaryMobile = ''
				}
        		var ordQtyNum = item.ordQty;
        		totalOrdQtyNumNums = parseInt(totalOrdQtyNumNums) + parseInt(ordQtyNum);
			htmlPurch +='<li class="mui-table-view-cell mui-media app_bline app_pr">'+
//		产品图片
//			'<div class="photoParent mui-pull-left app_pa">'+
//				'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'"></div>'+
//		产品信息
			'<div class="mui-media-body app_w80p app_fr">'+
				'<div class="mui-input-row">'+
					'<label>商品名称：</label>'+
					'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuName +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品编号：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.partNo +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品货号：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>供应商：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.vendor.name +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>供应商电话：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ primaryMobile +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品单价：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>采购数量：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.ordQty +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>总额：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ (item.unitPrice * item.ordQty).toFixed(2) +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>已发货数量：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.sentQty +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>创建时间：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.createDate) +'" disabled></div>'+

				'</div></div></li>'
			});
			$("#purchOrdQty").val(totalOrdQtyNumNums);
			$("#orSchedPurch").html(htmlPurch)
			_this.btnshow(chData);
			_this.schedulPlan();
		},
		btnshow: function(data) {
			var _this = this;
			$('.schedCommd').hide();
			$('input[name=schedulType]').on('change', function() {
				if(this.checked && this.value == 0) {
					$('.schedPurch').show();
					$('.schedCommd').hide();
					_this.purchContent(data);
				}
				if(this.checked && this.value == 1) {
					$('.schedPurch').hide();
					$('.schedCommd').show();
					_this.commdContent(data);
				}
			})
		},
		purchContent: function(a) {
			var _this = this;
			var htmlPurch = '';
			var htmlSave = '';
			$.each(a.bizOrderHeader.orderDetailList, function(i,item) {
				var primaryMobile = '';
				if(item.primary.mobile) {
					primaryMobile = item.primary.mobile
				}else {
					primaryMobile = ''
				}
			htmlPurch +='<li class="mui-table-view-cell mui-media app_bline app_pr">'+
//		产品图片
//			'<div class="photoParent mui-pull-left app_pa">'+
//				'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'"></div>'+
//		产品信息
			'<div class="mui-media-body app_w80p app_fr">'+
				'<div class="mui-input-row">'+
					'<label>商品名称：</label>'+
					'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuName +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品编号：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.partNo +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品货号：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>供应商：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.vendor.name +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>供应商电话：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ primaryMobile +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品单价：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>采购数量：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.ordQty +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>总额：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ (item.unitPrice * item.ordQty).toFixed(2) +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>已发货数量：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.sentQty +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>创建时间：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.createDate) +'" disabled></div>'+
				'</div></div></li>'
			});
    		$("#orSchedPurch").html(htmlPurch)
		},
		commdContent: function(b) {
			var _this = this;
			var htmlCommodity = '';
			var htmlAllSave = '';
			$.each(b.bizOrderHeader.orderDetailList, function(i,item) {
				var waiteNum = item.ordQty - item.sumCompleteNum;
				var primaryMobile = '';
				if(item.primary.mobile) {
					primaryMobile = item.primary.mobile
				}else {
					primaryMobile = ''
				}
				htmlCommodity += '<li class="mui-table-view-cell app_bline2">'+
				'<div class="mui-input-row inComdty inDetailComdty app_pall11_15">'+
	//							<!--产品图片-->
//					'<div class="photoParent mui-pull-left app_pa">'+
//						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'"></div>'+
	//							<!--产品信息-->
					'<div class="mui-media-body app_w80p app_fr">'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuName +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>商品编号：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.partNo +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>供应商：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.vendor.name +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>供应商电话：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ primaryMobile +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>商品单价：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>采购数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.ordQty +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>总额：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ (item.unitPrice * item.ordQty).toFixed(2) +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>已发货数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.sentQty +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>创建时间：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.createDate) +'" disabled></div>'+
				'</div></div>'+
				'<div class="mui-row app_f13 app_bline">'+
					'<div class="mui-input-row">'+
						'<label>总申报数量：</label>'+
						'<input type="text" value="'+ item.ordQty +'" id="totalOrdQtyForSku_'+ item.skuInfo.id+'" class="commdOrdQty" disabled></div>'+
//					'<div class="mui-input-row">'+
//						'<label>总待排产量：</label>'+
//						'<input type="text" value="'+ waiteNum +'" class="commdWaiteNum" disabled></div>'+
//					'<div class="mui-input-row">'+
//						'<label>已排产数量：</label>'+
//						'<input type="text" name="toalSchedulingNumForSku" value="'+ item.sumCompleteNum +'" class="commdCompleteNum" disabled></div>'+
					'<button type="submit" class="commdAddBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">添加排产计划</button>'+
					'<div classs="app_bline"></div>'+
				'<div class="mui-row comdPlan">'+
					'<div class="labelLf">排产计划</div>'+
					'<div class="mui-row app_f13 commdAddPlan" id="'+ item.skuInfo.id+'">'+
						'<div class="mui-row app_bline commdPlan" name="'+ item.skuInfo.id +'">'+
							'<div class="mui-input-row">'+
								'<label>完成日期：</label>'+
								'<input type="date" name="'+ item.skuInfo.id +'_date" class="commdDate"></div>'+
							'<div class="mui-input-row">'+
								'<label>排产数量：</label>'+
								'<input type="text" name="'+ item.skuInfo.id +'_value" class="commdNum mui-input-clear"></div>'+
				'</div></div></div></div></li>'
			});
    		$("#orSchedCommd").html(htmlCommodity)
		},
		schedulPlan: function() {
			var _this = this;
			var htmlPurchPlan = '<div class="mui-row app_bline purchAddCont">'+
				'<div class="mui-input-row">'+
					'<label>完成日期：</label>'+
					'<input type="date" class="addpurchDate"></div>'+
				'<div class="mui-input-row">'+
					'<label>排产数量：</label>'+
					'<input type="text" class="addpurchNum mui-input-clear"></div>'+
				'<button type="submit" class="removeBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">删除</button>'+
			'</div>';
			var htmlcommdPlan = '<div class="mui-row app_bline commdAddCont">'+
				'<div class="mui-input-row">'+
					'<label>完成日期：</label>'+
					'<input type="date" name="" class="addCommdDate"></div>'+
				'<div class="mui-input-row">'+
					'<label>排产数量：</label>'+
					'<input type="text" class="addCommdNum mui-input-clear"></div>'+
				'<button type="submit" class="removeBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">删除</button>'+
			'</div>';
			var addPurchNum = _this.userInfo.staOrdId;
			$('#purchPlan').attr('name', addPurchNum);
			$('#purchDate').attr('name', addPurchNum + '_date');
			$('#purchNum').attr('name', addPurchNum + '_value');

			$(".schedPurch").on("tap", "#purchAddBtn", function() {
				$('#purchAddCont').append(htmlPurchPlan);
				var addPurchNum = _this.userInfo.staOrdId;
				$('.purchAddCont').attr('name', addPurchNum);
				$('.addpurchDate').attr('name', addPurchNum + '_date');
				$('.addpurchNum').attr('name', addPurchNum + '_value');
			})
			var commdDateName = '';
			var commdNumName = '';
			var commdPlanName = '';
			$(".schedCommd").on("tap", ".commdAddBtn", function() {
				$(this).parent('.app_f13').find('.commdAddPlan').append(htmlcommdPlan);
				commdPlanName = $(this).parent('.app_f13').find('.commdPlan').attr('name');
				commdDateName = $(this).parent('.app_f13').find('.commdDate').attr('name');
				commdNumName = $(this).parent('.app_f13').find('.commdNum').attr('name');
				$(this).parent('.app_f13').find('.commdAddCont').attr('name', commdPlanName);
				$(this).parent('.app_f13').find('.addCommdDate').attr('name', commdDateName);
				$(this).parent('.app_f13').find('.addCommdNum').attr('name', commdNumName);
			})
			_this.removeSchedul();
		},
		removeSchedul: function() {
			var _this = this;
			$('.mui-content').on('tap', '.removeBtn', function() {
				$(this.parentNode).remove();
			})
		},
		saveComplete: function(schedulingType,poId) {
			var _this = this;
            var reqId = _this.userInfo.staOrdId;
            var trArray = $("[name='" + reqId + "']");
            var params = new Array();
            var schRemark = "";
            var originalNum = $("#purchOrdQty").val();
            schRemark = $("#orRemark").val();

            var totalSchedulingHeaderNum = 0;
            var totalSchedulingDetailNum = 0;
            var poSchType = 0;
            for(i=0;i<trArray.length;i++){
                var div = trArray[i];
                var jqDiv = $(div);
                var value = jqDiv.find("[name='" + reqId + "_value']").val();

                totalSchedulingHeaderNum = parseInt(totalSchedulingHeaderNum) + parseInt(value);
            }

            var totalTotalSchedulingNum = 0;
            poSchType = originalNum >  parseInt(totalSchedulingHeaderNum)  ? 1 : 2;

            if(parseInt(totalSchedulingHeaderNum) > parseInt(originalNum)) {
                alert("排产量总和太大，请重新输入!")
                return;
            }

            for(i=0;i<trArray.length;i++){
                var div = trArray[i];
                var jqDiv = $(div);
                var date = jqDiv.find("[name='" + reqId + "_date']").val();
                var value = jqDiv.find("[name='" + reqId + "_value']").val();

                if (date == "") {
                    if (value != "") {
                        alert("第" + count + "个商品完成日期不能为空!")
                        return;
                    }
                }
                if (value == "") {
                    if (date != "") {
                        alert("第" + count + "个商品排产数量不能为空!")
                        return;
                    }
                }
                if (date == "" && value == "") {
                    continue;
                }
                var reg= /^[0-9]+[0-9]*]*$/;
                if (value != "" && (parseInt(value) <= 0 || parseInt(value) > originalNum || !reg.test(value))) {
                    alert("确认值输入不正确!")
                    return;
                }
                var entity = {};
                entity.id = poId;
                entity.objectId = poId;
                entity.originalNum = originalNum;
                entity.schedulingNum = value;
                entity.planDate=date + " 00:00:00";
                entity.schedulingType=schedulingType;
                entity.remark=schRemark;
                entity.poSchType = poSchType;
                //totalSchedulingHeaderNum = parseInt(totalSchedulingHeaderNum) + parseInt(value);
                params[i]=entity;
                //totalSchedulingNum = parseInt(totalSchedulingNum) + parseInt(value);
            }
            $.ajax({
                url: '/a/biz/po/bizPoHeader/saveSchedulingPlan',
                contentType: 'application/json',
                data:JSON.stringify(params),
                datatype:"json",
                type: 'post',
                success: function (result) {
                	if(result == true || result == 'true') {
                		mui.toast('操作成功!')
						GHUTILS.OPENPAGE({
							url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
							extras: {}
						})
                	}else{
                		mui.toast('操作失败!')
                	}
                },
                error: function (error) {
                    console.info(error);
                }
            });
		},
		batchSave: function(schedulingType,poId,vndm) {
			var _this = this;
			var skuInfoIdListList = vndm.skuInfoIdListListJson;
            var params = new Array();
            var totalSchedulingNum = 0;
            var totalOriginalNum = 0;
            var count = 1
            var ind = 0;
            var schRemark = "";
            schRemark = $("#orRemark").val();

            var totalSchedulingHeaderNum = 0;
            var totalSchedulingDetailNum = 0;
            var poSchType = 0;

            for(var index in skuInfoIdListList) {
                var skuInfoId = skuInfoIdListList[index];

                var originalNum = $(eval("totalOrdQtyForSku_" + skuInfoId)).val();
                totalOriginalNum = parseInt(totalOriginalNum) + parseInt(originalNum);
            }

            for(var index in skuInfoIdListList) {
                var skuInfoId = skuInfoIdListList[index];
                var trArray = $("[name='" + skuInfoId + "']");
                for(i=0;i<trArray.length;i++) {
                    var div = trArray[i];
                    var jqDiv = $(div);
                    var value = jqDiv.find("[name='" + skuInfoId + "_value']").val();
                    totalSchedulingDetailNum = parseInt(totalSchedulingDetailNum) + parseInt(value);
                }
            }
            poSchType = totalOriginalNum > parseInt(totalSchedulingDetailNum) ? 1 : 2;

            for(var index in skuInfoIdListList) {
                var skuInfoId = skuInfoIdListList[index];
                var originalNum = $(eval("totalOrdQtyForSku_" + skuInfoId)).val();
                var trArray = $("[name='" + skuInfoId + "']");
                for(i=0;i<trArray.length;i++) {
                    var div = trArray[i];
                    var jqDiv = $(div);
                    var date = jqDiv.find("[name='" + skuInfoId + "_date']").val();
                    var value = jqDiv.find("[name='" + skuInfoId + "_value']").val();
                    if (date == "") {
                        if (value != "") {
                            alert("第" + count + "个商品完成日期不能为空!")
                            return;
                        }
                    }
                    if (value == "") {
                        if (date != "") {
                            alert("第" + count + "个商品排产数量不能为空!")
                            return;
                        }
                    }
                    if (date == "" && value == "") {
                        continue;
                    }
                    var reg = /^[0-9]+[0-9]*]*$/;
                    if (value != "" && (parseInt(value) <= 0 || parseInt(value) > originalNum || !reg.test(value))) {
                        alert("第" + count + "个商品确认值输入不正确!")
                        return;
                    }
                    var entity = {};
                    entity.id = poId;
                    entity.objectId = skuInfoId;
                    entity.originalNum = originalNum;
                    entity.schedulingNum = value;
                    entity.planDate=date + " 00:00:00";
                    entity.schedulingType=schedulingType;
                    entity.remark=schRemark;
                    entity.poSchType = poSchType;

                    params[ind]=entity;
                    totalSchedulingNum = parseInt(totalSchedulingNum) + parseInt(value);
                    ind++;
                }
                count++;
            }
            if(parseInt(totalSchedulingNum) > parseInt(totalOriginalNum)) {
                alert("排产量总和太大，请重新输入!")
                return false
            }
            $.ajax({
                url: '/a/biz/po/bizPoHeader/batchSaveSchedulingPlan',
                contentType: 'application/json',
                data:JSON.stringify(params),
                datatype:"json",
                type: 'post',
                success: function (result) {
                	if(result == true || result == 'true') {
                		mui.toast('操作成功!')
						GHUTILS.OPENPAGE({
							url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
							extras: {}
						})
                	}else{
                		mui.toast('操作失败!')
                	}
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);







(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.prew = false;
		this.inLastPayDateFlag = "false"
		this.inpoFlag = "false"
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
//			biz:request:bizRequestHeader:audit    最后付款时间、审核
			this.getPermissionList('biz:request:bizRequestHeader:audit','inLastPayDateFlag')
			this.getPermissionList1('biz:po:bizPoHeader:audit','inpoFlag')
			this.pageInit(); //页面初始化
			//this.radioShow()
			//			this.btnshow()
			//			this.searchShow()
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.getData();

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
                    _this.inLastPayDateFlag = res.data;
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
                	console.log(res.data)
                    _this.inpoFlag = res.data;
                }
            });
        },
		getData: function() {
			var _this = this;
			var idd=_this.userInfo.staOrdIds;
			var audit=_this.userInfo.audits;
			var processPo=_this.userInfo.processPos;
//			console.log(idd)
//			console.log(audit)
//			console.log(processPo)
			datas={
				id: _this.userInfo.inListId,
				str: "audit"
			}
//			var datas={};
//			if(idd==null&&audit==null&&processPo==null){
//				datas={
//					id: _this.userInfo.inListId,
//					str: "detail"
//				}
//			}else{
//				datas={
//					id: idd,
//					str: audit,
//					processPo:processPo,
//				}
//			}
			$.ajax({
				type: "GET",
				url: "/a/biz/request/bizRequestHeaderForVendor/form4MobileNew",
				data: datas,
				dataType: "json",
				success: function(res) {
					console.log(res);
					console.log(res.data.bizRequestHeader.str);
					$('#inCheckBtn').attr('poid',res.data.bizRequestHeader.bizPoHeader.id);
					$('#inRejectBtn').attr('poids',res.data.bizRequestHeader.bizPoHeader.id);
					//备货单审核
					if(res.data.bizRequestHeader.str=='detail'){
						console.log(_this.inLastPayDateFlag);
						if(_this.inLastPayDateFlag == true) {
							_this.comfirDialig();
						}
					}
					//订单支出信息进来的备货单审核
					console.log(_this.inpoFlag);
					if(_this.inpoFlag == true) {
						if(res.data.bizRequestHeader.str=='audit'){
							if(res.data.bizRequestHeader.bizPoHeader.commonProcessList != null && res.data.bizRequestHeader.bizPoHeader.commonProcessList.length > 0 && res.data.bizRequestHeader.processPo == 'processPo')                {
								_this.comfirDialigs();
							}
						}
					}

					//调取供应商信息
					$('#createPo').val(res.data.createPo);
					/*判断是品类主管*/
					if(_this.inLastPayDateFlag == true) {
						if(res.data.bizRequestHeader.bizStatus == 4 && res.data.createPo == 'yes') {	//品类主管审核才生成PO
							inLastDate = '<label>最后时间：</label>'+
								'<input type="date" class="mui-input-clear" id="lastDate" placehohder="必填！">'+
								'<font>*</font>'
							$('#inlastDate').append(inLastDate);
						}
					}
					if(res.data.bizRequestHeader.bizVendInfo) {
						var officeId = res.data.bizRequestHeader.bizVendInfo.office.id;
						$('#supplierId').val(officeId);
						_this.supplier($('#supplierId').val());
					} else {
						$('#insupplier').parent().hide(); //供应商
						$('#insupplierNum').parent().hide(); //供应商卡号
						$('#insupplierMoney').parent().hide(); //供应商收款人
						$('#insupplierBank').parent().hide(); //供应商开户行
						$('#insuppliercontract').parent().hide();//供应商合同
					    $('#insuppliercardID').parent().hide();//供应商身份证
					}
					/*业务状态*/
				    $.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {type:"biz_req_status"},
		                dataType: "json",
		                success: function(resl){
		                	$.each(resl,function(i,item){
		                		if(item.value==res.data.bizRequestHeader.bizStatus){
		                		 	$('#inPoDizstatus').val(item.label);
		                		}
		                	})
						}
					});
					//排产状态
					if(res.data.bizRequestHeader.bizPoHeader) {
						var itempoSchType = res.data.bizRequestHeader.bizPoHeader.poSchType;
						var SchedulstatusTxt = '';
						$.ajax({
							type: "GET",
							url: "/a/sys/dict/listData",
							data: {
								type: "poSchType"
							},
							dataType: "json",
							success: function(reslt) {
								$.each(reslt, function(i, item) {
									if(item.value == itempoSchType) {
										SchedulstatusTxt = item.label
									}
									if(itempoSchType == null || itempoSchType == "") {
										SchedulstatusTxt = "未排产";
									}
								})
								$('#inSchedulstatus').val(SchedulstatusTxt);
							}
						});
					} else {
						$('#inSchedulstatus').val("未排产");
					};
					$('#inPoordNum').val(res.data.bizRequestHeader.reqNo); //备货单编号	
					//备货方
					if(res.data.bizRequestHeader.fromType == 1) {
						$('#fromType1').attr('checked', 'checked');
						$('#fromType2').removeAttr('checked');
					}
					if(res.data.bizRequestHeader.fromType == 2) {
						$('#fromType1').removeAttr('checked');
						$('#fromType2').attr('checked', 'checked');
					}
					$('#inOrordNum').val(res.data.bizRequestHeader.fromOffice.name); //采购中心
					if(res.data.bizRequestHeader.totalMoney){
						$('#inPototal').val(res.data.bizRequestHeader.totalMoney.toFixed(2)); //应付金额
					}
					if(res.data.bizRequestHeader.recvTotal){
						$('#inMoneyReceive').val(res.data.bizRequestHeader.recvTotal.toFixed(2)); //已收保证金
					}
					
					$('#inMarginLevel').val((res.data.bizRequestHeader.recvTotal * 100 / res.data.bizRequestHeader.totalMoney).toFixed(2) + '%'); //保证金比例
					if(res.data.bizRequestHeader.bizPoHeader == "") {
						$('#inMoneyPay').val();
					} else {
						$('#inMoneyPay').val(res.data.bizRequestHeader.bizPoHeader.payTotal.toFixed(2)); //已支付厂商保证金
					}
					$('#inPoLastDa').val(_this.newData(res.data.bizRequestHeader.recvEta)); //期望收货时间
					$('#inPoRemark').val(res.data.bizRequestHeader.remark); //备注
					_this.commodityHtml(res.data); //备货商品
					_this.statusListHtml(res.data); //状态流程					
					_this.paylistHtml(res.data); //支付列表
					_this.checkProcessHtml(res.data); //审批流程
					if(res.data.bizRequestHeader.str == 'audit' && res.data.bizRequestHeader.commonProcess.type == res.data.defaultProcessId){
						_this.stockGoodsHtml(res.data); //商品库存
					}else{
						$('#labelLf').hide();
					}
					//判断审核状态
					_this.checkStatus(res.data);
					//排产信息
					if(res.data.bizRequestHeader.str == 'detail'||res.data.bizRequestHeader.str == 'audit') {
						var poheaderId = res.data.bizRequestHeader.bizPoHeader.id;
//						console.log(poheaderId)
						if(poheaderId == null || poheaderId == "") {
							$("#inSchedultype").val("未排产")
							$("#stockGoods").hide();
							$("#schedulingPlan_forHeader").hide();
							$("#schedulingPlan_forSku").hide();
						}
						if(poheaderId != null && poheaderId != "") {
							_this.scheduling(poheaderId);
						}
					}
				}
			});
		},
		checkStatus: function(data) {
			console.log(data)
			var _this = this;
			var requProcess = data.bizRequestHeader.commonProcess.requestOrderProcess;
//			var purchProcess = data.bizRequestHeader.commonProcess.purchaseOrderProcess;
//			var purchPro = data.bizRequestHeader.bizPoHeader.commonProcessList;
//			var purchProcess = data.bizRequestHeader.commonProcess.purchaseOrderProcess;
			if(data.bizRequestHeader.str == 'audit' && data.bizRequestHeader.processPo != 'processPo') {
				if(requProcess.name != '审核完成') {
					$('#incheck').val(requProcess.name);
				}
				if(requProcess.name == '审核完成') {
					$('#incheck').val('订单支出信息审核');
				}
				$('#currentType').val(requProcess.code)
			}
//			var commonProcessList = '';
//			if(data.bizRequestHeader.bizPoHeader) {
//				commonProcessList = data.bizRequestHeader.bizPoHeader.commonProcessList
//			}
//			if(data.bizRequestHeader.str == 'audit' && commonProcessList != null && commonProcessList.length > 0 && data.bizRequestHeader.processPo == 'processPo') {
////				$('#incheck').val(purchProcess.name);
////				$('#currentType').val(purchProcess.code)
//              console.log(purchPro)
//              $.each(purchPro, function(i, item) {
//					$('#incheck').val(item.purchaseOrderProcess.name);
//				    $('#currentType').val(item.purchaseOrderProcess.code)
//				})
//			}
		},
		//供应商信息
		supplier: function(supplierId) {
			$.ajax({
				type: "GET",
				url: "/a/biz/request/bizRequestHeaderForVendor/selectVendInfo",
				data: {
					vendorId: supplierId
				},
				dataType: "json",
				success: function(rest) {
					console.log(rest)
					if(rest) {
						$('#insupplier').val(rest.vendName); //供应商
						$('#insupplierNum').val(rest.cardNumber); //供应商卡号
						$('#insupplierMoney').val(rest.payee); //供应商收款人
						$('#insupplierBank').val(rest.bankName); //供应商开户行
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
						
					} else {
						$('#insupplier').parent().hide(); //供应商
						$('#insupplierNum').parent().hide(); //供应商卡号
						$('#insupplierMoney').parent().hide(); //供应商收款人
						$('#insupplierBank').parent().hide(); //供应商开户行
						$('#insuppliercontract').parent().hide();//供应商合同
					    $('#insuppliercardID').parent().hide();//供应商身份证
					}

				}
			});
		},
		//排产信息接口
		scheduling: function(idval) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/scheduling4Mobile",
				data: {
					id: idval
				},
				dataType: "json",
				success: function(res) {
					if(res.data.detailHeaderFlg != true && res.data.detailSchedulingFlg != true) {
						$("#inSchedultype").val("未排产")
						$("#stockGoods").hide();
						$("#schedulingPlan_forHeader").hide();
						$("#schedulingPlan_forSku").hide();
					}
					//按订单排产
					if(res.data.detailHeaderFlg == true) {
						$("#inSchedultype").val("按订单排产")
						$("#stockGoods").show();
						$("#schedulingPlan_forHeader").show();
						$("#schedulingPlan_forSku").hide();

						var poDetailList = res.data.bizPoHeader.poDetailList;
						var poDetailHtml = "";
						$.each(poDetailList, function(n, v) {
							poDetailHtml += '<li class="mui-table-view-cell mui-media">' +
								'<div class="photoParent mui-pull-left app_pa">' +
								'<img class="app_pa" src="' + v.skuInfo.productInfo.imgUrl + '">' +
								'</div>' +
								'<div class="mui-media-body app_w72p app_fr">' +
								'<div class="mui-input-row">' +
								'<label>品牌名称：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.skuInfo.productInfo.brandName + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>商品名称：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.skuInfo.name + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>商品货号：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.skuInfo.itemNo + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>采购数量：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.ordQty + '" reqQty disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>结算价：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.unitPrice + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>总金额：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.ordQty * v.unitPrice + '" disabled>' +
								'</div>' +
								'</div>' +
								'</li>'
						})
						$("#purchaseMenu").append(poDetailHtml);
						//按订单排产中的排产记录
						var bizCompletePalns = res.data.bizCompletePalns;
						var schedulingHeaderHtml = "";
						$.each(bizCompletePalns, function(n, v) {
							schedulingHeaderHtml += '<li class="mui-table-view-cell mui-media app_pl0">' +
								'<div class="mui-media-body">' +
								'<div class="mui-input-row">' +
								'<label>完成日期：</label>' +
								'<input type="text" class="mui-input-clear" value="' + _this.formatDateTime(v.planDate) + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>排产数量：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.completeNum + '" disabled>' +
								'</div>' +
								'</div>' +
								'</li>'
						});
						$("#schedulingHeader").append(schedulingHeaderHtml);
						//按订单排产中的排产备注
						var remarkHtml = "<textarea id='schRemarkOrder' readonly>" + res.data.bizPoHeader.bizSchedulingPlan.remark + "</textarea>";
						$(".schedulingHeaderRemark").append(remarkHtml);
					}
					//按商品排产
					if(res.data.detailSchedulingFlg == true) {
						$("#inSchedultype").val("按商品排产")
						$("#stockGoods").hide();
						$("#schedulingPlan_forHeader").hide();
						$("#schedulingPlan_forSku").show();
						var poDetailLists = res.data.bizPoHeader.poDetailList;
						var poDetailHtmls = ""
						$.each(poDetailLists, function(n, v) {
							poDetailHtmls += '<li class="mui-table-view-cell mui-media">' +
								'<div class="photoParent mui-pull-left app_pa">' +
								'<img class="app_pa" src="' + v.skuInfo.productInfo.imgUrl + '">' +
								'</div>' +
								'<div class="mui-media-body app_w72p app_fr">' +
								'<div class="mui-input-row">' +
								'<label>品牌名称：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.skuInfo.productInfo.brandName + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>商品名称：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.skuInfo.name + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>商品货号：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.skuInfo.itemNo + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>采购数量：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.ordQty + '" reqQty disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>结算价：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.unitPrice + '" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>总金额：</label>' +
								'<input type="text" class="mui-input-clear" value="' + v.ordQty * v.unitPrice + '" disabled>' +
								'</div>' +
								'</div>' +
								'</li>'
						});
						$("#purchaseMenus").append(poDetailHtmls);
						//按商品排产中的排产记录
						var completePalnHtml = "";
						$.each(res.data.bizPoHeader.poDetailList, function(n, v) {
							$.each(v.bizSchedulingPlan.completePalnList, function(n, v) {
								completePalnHtml += '<li class="mui-table-view-cell mui-media app_pr app_pl0">' +
									'<div class="mui-media-body">' +
									'<div class="mui-input-row">' +
									'<label>完成日期：</label>' +
									'<input type="text" class="mui-input-clear" value="' + _this.formatDateTime(v.planDate) + '" disabled>' +
									'</div>' +
									'<div class="mui-input-row">' +
									'<label>排产数量：</label>' +
									'<input type="text" class="mui-input-clear" value="' + v.completeNum + '" disabled>' +
									'</div>' +
									'</div>' +
									'</li>'
								$("#schedulingHeaders").append(completePalnHtml);
							});
						});
						//按商品排产中的排产备注
						var remarkHtmls = "<textarea id='schRemarkOrder' readonly>" + res.data.bizPoHeader.bizSchedulingPlan.remark + "</textarea>";
						$(".schedulingHeaderRemarks").append(remarkHtmls);
					}
				}
			})
		},
		//支付列表
		paylistHtml: function(data) {
			var _this = this;
			var htmlPaylist = '';
			if(data.paymentOrderList != null && data.paymentOrderList.length > 0) {
				$.each(data.paymentOrderList, function(i, item) {
					if(item.payTime) {
						var realitypayTime = "";
						var realitypayTime = _this.formatDateTime(item.payTime);
					} else {
						var realitypayTime = "";
					}
					htmlPaylist += '<li class="mui-table-view-cell mui-media payList">' +
						'<div class="mui-media-body">' +
						'<div class="mui-input-row">' +
						'<label>付款金额：</label>' +
						'<input type="text" class="mui-input-clear" value="' + item.total.toFixed(2) + '" disabled>' +
						'</div>' +
						'<div class="mui-input-row">' +
						'<label>实际付款金额：</label>' +
						'<input type="text" class="mui-input-clear" value="' + item.payTotal.toFixed(2) + '" disabled>' +
						'</div>' +
						'<div class="mui-input-row">' +
						'<label>最后付款时间：</label>' +
						'<input type="text" class="mui-input-clear" value="' + _this.formatDateTime(item.deadline) + '" disabled>' +
						'</div>' +
						'<div class="mui-input-row">' +
						'<label>实际付款时间：</label>' +
						'<input type="text" class="mui-input-clear realitypayTime" value="' + realitypayTime + '" disabled>' +
						'</div>' +
						'</div>' +
						'</li>'
				});
				$("#inPaylist").html(htmlPaylist);
			} else {
				$('#inPaylistbox').hide();
			}
		},
		//状态流程
		statusListHtml: function(data) {
			var _this = this;
			var statusLen = data.auditStatusList.length;
			if(statusLen > 0) {
				var pHtmlList = '';
				$.each(data.auditStatusList, function(i, item) {
					if(i != statusLen - 1) {
						var step = i + 1;
						pHtmlList += '<li class="step_item">' +
							'<div class="step_num">' + step + ' </div>' +
							'<div class="step_num_txt">' +
							'<div class="mui-input-row">' +
							'<label>处理人:</label>' +
							'<input type="text" value="' + item.createBy.name + '" class="mui-input-clear" disabled>' +
							'</div>' +
							'<div class="mui-input-row">' +
							'<label>状态:</label>' +
							'<input type="text" value="' + data.stateDescMap[item.bizStatus] + '" class="mui-input-clear" disabled>' +
							'<label>时间:</label>' +
							'<input type="text" value=" ' + _this.formatDateTime(item.createDate) + ' " class="mui-input-clear" disabled>' +
							'</div>' +
							'</div>' +
							'</li>'
					}
					//					$("#inCheckAddMenu").html(pHtmlList);
					if(i === statusLen - 1) {
						var step = i + 1;
						pHtmlList += '<li class="step_item">' +
							'<div class="step_num">' + step + ' </div>' +
							'<div class="step_num_txt">' +
							'<div class="mui-input-row">' +
							'<label>处理人:</label>' +
							'<input type="text" value="' + item.createBy.name + '" class="mui-input-clear" disabled>' +
							'</div>' +
							'<div class="mui-input-row">' +
							'<label>状态:</label>' +
							'<input type="text" value="' + data.stateDescMap[item.bizStatus] + '" class="mui-input-clear" disabled>' +
							'<label>时间:</label>' +
							'<input type="text" value=" ' + _this.formatDateTime(item.createDate) + ' " class="mui-input-clear" disabled>' +
							'</div>' +
							'</div>' +
							'</li>'
					}
					$("#inCheckAddMenu").html(pHtmlList);
				});

			}
		},
		//审批流程
		checkProcessHtml: function(data) {
			var _this = this;
			var auditLen = data.bizRequestHeader.commonProcessList.length;
			if(data.bizRequestHeader.commonProcessList) {
				var CheckHtmlList = '';
				$.each(data.bizRequestHeader.commonProcessList, function(i, item) {
					var auditLen = data.bizRequestHeader.commonProcessList.length;
					var step = i + 1;
					if(i != auditLen - 1) {
						CheckHtmlList += '<li class="step_item">' +
							'<div class="step_num">' + step + ' </div>' +
							'<div class="step_num_txt">' +
							'<div class="mui-input-row">' +
							'<label>批注:</label>' +
							'<input type="text" value="' + item.description + '" class="mui-input-clear" disabled>' +
							'<label>审批人:</label>' +
							'<input type="text" value=" ' + item.user.name + ' " class="mui-input-clear" disabled>' +
							'<label>时间:</label>' +
							'<input type="text" value=" ' + _this.formatDateTime(item.updateTime) + ' " class="mui-input-clear" disabled>' +
							'</div>' +
							'</div>' +
							'</li>'
					}
					//auditLen = 1&& data.bizRequestHeader.bizPoHeader.commonProcessList == null
					//
					if(i == auditLen - 1 && data.bizRequestHeader.processPo != 'processPo' && item.requestOrderProcess.name != '审核完成') {
						if(item.requestOrderProcess.name != '审核完成') {
							CheckHtmlList += '<li class="step_item">' +
								'<div class="step_num">' + step + ' </div>' +
								'<div class="step_num_txt">' +
								'<div class="mui-input-row">' +
								'<label>当前状态:</label>' +
								'<input type="text" value="' + item.requestOrderProcess.name + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'</div>' +
								'</li>'
						}
//						if(item.requestOrderProcess.name == '审核完成'){
//							CheckHtmlList +='<li class="step_item">'+
//								'<div class="step_num">'+ step +' </div>'+
//								'<div class="step_num_txt">'+
//									'<div class="mui-input-row">'+
//								        '<label>当前状态:</label>'+
//								        '<input type="text" value="订单支出信息审核 " class="mui-input-clear" disabled>'+
//								    '</div>'+
//								'</div>'+
//							'</li>'
//						}
					}
				});
				if(data.bizRequestHeader.bizPoHeader != "") {
					$.each(data.bizRequestHeader.bizPoHeader.commonProcessList, function(a, items) {
						var len = data.bizRequestHeader.bizPoHeader.commonProcessList.length;
						var totalStep = auditLen + a;
//						if(len-a != 1) {
//							CheckHtmlList +='<li class="step_item">'+
//							'<div class="step_num">'+ totalStep +' </div>'+
//							'<div class="step_num_txt">'+
//								'<div class="mui-input-row">'+
//							        '<label>批注:</label>'+
//							        '<input type="text" value="'+ items.description +'" class="mui-input-clear" disabled>'+
//							    	'<label>审批人:</label>'+
//							        '<input type="text" value=" '+ items.user.name +' " class="mui-input-clear" disabled>'+
//							        '<label>时间:</label>'+
//							        '<input type="text" value=" '+ _this.formatDateTime(items.updateTime) +' " class="mui-input-clear" disabled>'+
//							    '</div>'+
//							'</div>'+
//						'</li>'
//						}
//						if(len-a == 1) {
//							CheckHtmlList +='<li class="step_item">'+
//							'<div class="step_num">'+ totalStep +' </div>'+
//							'<div class="step_num_txt">'+
//								'<div class="mui-input-row">'+
//							        '<label>当前状态:</label>'+
//							        '<input type="text" value="'+ items.purchaseOrderProcess.name +'" class="mui-input-clear" disabled>'+
//							    '</div>'+
//							'</div>'+
//						'</li>'
//						}
						if(a == 0 && len > 1) {
							CheckHtmlList += '<li class="step_item">' +
								'<div class="step_num">' + totalStep + ' </div>' +
								'<div class="step_num_txt">' +
								'<div class="mui-input-row">' +
								'<label>批注:</label>' +
								'<input type="text" value="' + items.description + '" class="mui-input-clear" disabled>' +
								'<label>审批人:</label>' +
								'<input type="text" value=" ' + items.user.name + ' " class="mui-input-clear" disabled>' +
								'<label>时间:</label>' +
								'<input type="text" value=" ' + _this.formatDateTime(items.updateTime) + ' " class="mui-input-clear" disabled>' +
								'</div>' +
								'</div>' +
								'</li>'
						}
						if(a > 0 && a < len - 1) {
							CheckHtmlList += '<li class="step_item">' +
								'<div class="step_num">' + totalStep + ' </div>' +
								'<div class="step_num_txt">' +
								'<div class="mui-input-row">' +
								'<label>批注:</label>' +
								'<input type="text" value="' + items.description + '" class="mui-input-clear" disabled>' +
								'<label>审批人:</label>' +
								'<input type="text" value=" ' + items.user.name + ' " class="mui-input-clear" disabled>' +
								'<label>时间:</label>' +
								'<input type="text" value=" ' + _this.formatDateTime(items.updateTime) + ' " class="mui-input-clear" disabled>' +
								'</div>' +
								'</div>' +
								'</li>'
						}
						if(a == len - 1) {
							CheckHtmlList += '<li class="step_item">' +
								'<div class="step_num">' + totalStep + ' </div>' +
								'<div class="step_num_txt">' +
								'<div class="mui-input-row">' +
								'<label>当前状态:</label>' +
								'<input type="text" value="' + items.purchaseOrderProcess.name + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'</div>' +
								'</li>'
						}
					});
				}
				$("#inapprovalAddMenu").html(CheckHtmlList);
			} else {
				$("#inapprovalAddMenu").parent().hide();
			}
		},
		//备货商品
		commodityHtml: function(data) {
			var _this = this;
			var htmlCommodity = '';
			$.each(data.reqDetailList, function(i, item) {
				htmlCommodity +=
					'<div class="mui-row app_bline" id="' + item.id + '">' +
					'<input style="display:none;" name="" class="skuinfo_check" id="' + item.skuInfo.id + '" type="checkbox">' +
					'<div class="photoParents mui-pull-left app_pa">' +
					'<img class="app_pa" src="' + item.skuInfo.productInfo.imgUrl + '">' +
					'</div>' +
					'<div class="mui-row lineStyle">' +
					'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
					'<div class="mui-col-sm-10 mui-col-xs-10">' +
					'<li class="mui-table-view-cell app_bline3">' +
					'<div class="mui-input-row">' +
					'<label>商品名称:</label>' +
					'<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.name + '" disabled></div></li></div></div>' +

					'<div class="mui-row lineStyle">' +
					'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
					'<div class="mui-col-sm-10 mui-col-xs-10">' +
					'<li class="mui-table-view-cell app_bline3">' +
					'<div class="mui-input-row">' +
					'<label>商品货号:</label>' +
					'<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div></div>' +

					'<div class="mui-row lineStyle">' +
					'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
					'<div class="mui-col-sm-10 mui-col-xs-10">' +
					'<li class="mui-table-view-cell app_bline3">' +
					'<div class="mui-input-row ">' +
					'<label class="">商品编码:</label>' +
					'<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.partNo + '" disabled>' +
					'</div></li></div></div>' +

					'<div class="mui-row">' +
					'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
					'<div class="mui-col-sm-5 mui-col-xs-5">' +
					'<li class="mui-table-view-cell app_bline3">' +
					'<div class="mui-input-row">' +
					'<label>品牌名称:</label>' +
					'<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.productInfo.brandName + '" disabled></div></li></div>' +
					'<div class="mui-col-sm-5 mui-col-xs-5">' +
					'<li class="mui-table-view-cell app_bline3">' +
					'<div class="mui-input-row">' +
					'<label>供应商:</label>' +
					'<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.productInfo.office.name + '" disabled></div></li></div></div>' +

					'<div class="mui-row inAddFont">' +
					'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
					'<div class="mui-col-sm-5 mui-col-xs-5">' +
					'<li class="mui-table-view-cell app_bline3">' +
					'<div class="mui-input-row">' +
					'<label>结算价:</label>' +
					'<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div>' +
					'<div class="mui-col-sm-5 mui-col-xs-5">' +
					'<li class="mui-table-view-cell app_bline3">' +
					'<div class="mui-input-row">' +
					'<label>申报数量:</label>' +
					'<input type="text" class="mui-input-clear inDeclareNum" id="reqQty_' + item.skuInfo.id + '" value="' + item.reqQty + '">' +
					'<font>*</font>' +
					'</div></li></div></div>';
				htmlCommodity += '</div>';
			});
			$("#commodityMenu").html(htmlCommodity);
		},
		//商品库存
		stockGoodsHtml: function(data) {
			var _this = this;
			var htmlstockGoods = '';
			//库存类型
	        $.each(data.inventorySkuList,function(i,items){
	            var iteminvType=items.invType;	
	            var invInfostatusTxt = '';
			    $.ajax({
	                type: "GET",
	                url: "/a/sys/dict/listData",
	                data: {type:"inv_type"},		                
	                dataType: "json",
	                success: function(reslts){
	                	$.each(reslts,function(i,item){
	                		if(item.value==iteminvType){
	                		 	invInfostatusTxt = item.label 
	                		}
	                		if(iteminvType == null||iteminvType == "") {
			                	invInfostatusTxt = "未知状态";
			                }
	                	})
	                	$('#invType').val(invInfostatusTxt);
					}
				});
	        })
			$.each(data.inventorySkuList, function(i, item) {
				htmlstockGoods +=
					'<div class="mui-row app_bline" id="' + item.id + '">' +

					'<div class="mui-row lineStyle">' +
						'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
						'<div class="mui-col-sm-10 mui-col-xs-10">' +
					        '<li class="mui-table-view-cell app_bline3">' +
					            '<div class="mui-input-row">' +
					                '<label>库存类型:</label>' +
					                '<input type="text" class="mui-input-clear commodityTxt" id="invType" value="' +  + '" disabled>'+
					            '</div>'+
					        '</li>'+
					    '</div>'+
					'</div>' +

					'<div class="mui-row lineStyle">' +
						'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
						'<div class="mui-col-sm-10 mui-col-xs-10">' +
					        '<li class="mui-table-view-cell app_bline3">' +
					            '<div class="mui-input-row">' +
					                '<label>仓库名称:</label>' +
					                '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.invInfo.name + '" disabled>'+
					            '</div>'+
					        '</li>'+
					    '</div>'+
					'</div>' +

					'<div class="mui-row lineStyle">' +
						'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
						'<div class="mui-col-sm-10 mui-col-xs-10">' +
					        '<li class="mui-table-view-cell app_bline3">' +
					            '<div class="mui-input-row">' +
					                '<label>采购中心:</label>' +
					                '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.customer.name + '" disabled>'+
					            '</div>'+
					        '</li>'+
					    '</div>'+
					'</div>' +

					'<div class="mui-row lineStyle">' +
						'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
						'<div class="mui-col-sm-10 mui-col-xs-10">' +
					        '<li class="mui-table-view-cell app_bline3">' +
					            '<div class="mui-input-row">' +
					                '<label>商品名称:</label>' +
					                '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.name + '" disabled>'+
					            '</div>'+
					        '</li>'+
					    '</div>'+
					'</div>' +
					
					'<div class="mui-row lineStyle">' +
						'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
						'<div class="mui-col-sm-10 mui-col-xs-10">' +
					        '<li class="mui-table-view-cell app_bline3">' +
					            '<div class="mui-input-row">' +
					                '<label>商品货号:</label>' +
					                '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.itemNo + '" disabled>'+
					            '</div>'+
					        '</li>'+
					    '</div>'+
					'</div>' +

					'<div class="mui-row lineStyle">' +
						'<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
						'<div class="mui-col-sm-10 mui-col-xs-10">' +
					        '<li class="mui-table-view-cell app_bline3">' +
					            '<div class="mui-input-row">' +
					                '<label>商品数量:</label>' +
					                '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.stockQty + '" disabled>'+
					            '</div>'+
					        '</li>'+
					    '</div>'+
					'</div>' 
			});
			$("#Goodsstock").html(htmlstockGoods);
		},
		comfirDialig: function() {
			var _this = this;
			document.getElementById("inRejectBtn").addEventListener('tap', function() {
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
									_this.rejectData(rejectTxt, 2)
								}
							} else {}
						})
					} else {}
				})
			});
			document.getElementById("inCheckBtn").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
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
									_this.ajaxData(inText, 1)
								} else {}
							})
						}
					} else {}
				})
			});
		},
		ajaxData: function(inText, num) {
			var _this = this;
			var lastDateTxt = '';
			if($('#createPo').val() == 'yes') {
				lastDateTxt = $('#lastDate').val() + ' 00:00:00'
			}
			$.ajax({
				type: "GET",
				url: "/a/biz/request/bizRequestHeaderForVendor/audit",
				data: {
					id: _this.userInfo.inListId,
					currentType: $('#currentType').val(),
					createPo: $('#createPo').val(),
					lastPayDateVal: lastDateTxt,
					auditType: num,
					description: inText
				},
				dataType: "json",
				success: function(res) {
					if(res.ret == true) {
						mui.toast('操作成功!')
						GHUTILS.OPENPAGE({
							url: "../../html/inventoryMagmetHtml/inventoryList.html",
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
		rejectData: function(rejectTxt, num) {
			var _this = this;
			var lastDateTxt = '';
			if($('#createPo').val() == 'yes') {
				lastDateTxt = $('#lastDate').val() + ' 00:00:00'
			}
			$.ajax({
				type: "GET",
				url: "/a/biz/request/bizRequestHeaderForVendor/audit",
				data: {
					id: _this.userInfo.inListId,
					currentType: $('#currentType').val(),
					createPo: $('#createPo').val(),
					lastPayDateVal: lastDateTxt,
					auditType: num,
					description: rejectTxt
				},
				dataType: "json",
				success: function(res) {
					if(res.ret == true) {
						mui.toast('操作成功!')
						GHUTILS.OPENPAGE({
							url: "../../html/inventoryMagmetHtml/inventoryList.html",
							extras: {}
						})
					}
					if(res.ret == false) {
						mui.toast(res.errmsg)
					}
				}
			});
		},
		comfirDialigs: function() {
			var _this = this;
			document.getElementById("inRejectBtn").addEventListener('tap', function() {
				var id= $(this).attr('poids');
				var currentType= $('#currentType').val();
//				console.log(id)
//				console.log(currentType)
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
									_this.rejectDatas(id,rejectTxt, 2,currentType)
								}
							} else {}
						})
					} else {}
				})
			});
			document.getElementById("inCheckBtn").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
				var id= $(this).attr('poid');
//				console.log(id);
				var currentType= $('#currentType').val();
//				console.log(currentType);
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
									_this.ajaxDatas(id,inText, 1,currentType)
								} else {}
							})
						}
					} else {}
				})
			});
		},
		ajaxDatas: function(id,inText, num,currentType) {
			var _this = this;
			var lastDateTxt = '';
			if($('#createPo').val() == 'yes') {
				lastDateTxt = $('#lastDate').val() + ' 00:00:00'
			}
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/audit",
				data: {
					id: id,
					currentType: currentType,
					fromPage: 'requestHeader',
					auditType: num,
					description: inText
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
					if(res.ret == true) {
						mui.toast('操作成功!')
						GHUTILS.OPENPAGE({
							url: "../../html/inventoryMagmetHtml/inventoryList.html",
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
		rejectDatas: function(id,rejectTxt, num,currentType) {
			var _this = this;
			var lastDateTxt = '';
			if($('#createPo').val() == 'yes') {
				lastDateTxt = $('#lastDate').val() + ' 00:00:00'
			}
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/audit",
				data: {
					id: id,
					currentType: currentType,
					fromPage: 'requestHeader',
					auditType: num,
					description: rejectTxt
				},
				dataType: "json",
				success: function(res) {
					if(res.ret == true) {
						mui.toast('操作成功!')
						GHUTILS.OPENPAGE({
							url: "../../html/inventoryMagmetHtml/inventoryList.html",
							extras: {}
						})
					}
					if(res.ret == false) {
						mui.toast(res.errmsg)
					}
				}
			});
		},
//给时间插件赋值
		newData: function(da) {
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
		formatDateTime: function(unix) {

			var now = new Date(parseInt(unix) * 1);
			now = now.toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
			if(now.indexOf("下午") > 0) {
				if(now.length == 18) {
					var temp1 = now.substring(0, now.indexOf("下午")); //2014/7/6
					var temp2 = now.substring(now.indexOf("下午") + 2, now.length); // 5:17:43
					var temp3 = temp2.substring(0, 1); //  5
					var temp4 = parseInt(temp3); // 5
					temp4 = 12 + temp4; // 17
					var temp5 = temp4 + temp2.substring(1, temp2.length); // 17:17:43
					//	                now = temp1 + temp5; // 2014/7/6 17:17:43
					//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
					now = now.replace("-"); //  2014-7-6 17:17:43
				} else {
					var temp1 = now.substring(0, now.indexOf("下午")); //2014/7/6
					var temp2 = now.substring(now.indexOf("下午") + 2, now.length); // 5:17:43
					var temp3 = temp2.substring(0, 2); //  5
					if(temp3 == 12) {
						temp3 -= 12;
					}
					var temp4 = parseInt(temp3); // 5
					temp4 = 12 + temp4; // 17
					var temp5 = temp4 + temp2.substring(2, temp2.length); // 17:17:43
					//	                now = temp1 + temp5; // 2014/7/6 17:17:43
					//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
					now = now.replace("-"); //  2014-7-6 17:17:43
				}
			} else {
				var temp1 = now.substring(0, now.indexOf("上午")); //2014/7/6
				var temp2 = now.substring(now.indexOf("上午") + 2, now.length); // 5:17:43
				var temp3 = temp2.substring(0, 1); //  5
				var index = 1;
				var temp4 = parseInt(temp3); // 5
				if(temp4 == 0) { //  00
					temp4 = "0" + temp4;
				} else if(temp4 == 1) { // 10  11  12
					index = 2;
					var tempIndex = temp2.substring(1, 2);
					if(tempIndex != ":") {
						temp4 = temp4 + "" + tempIndex;
					} else { // 01
						temp4 = "0" + temp4;
					}
				} else { // 02 03 ... 09
					temp4 = "0" + temp4;
				}
				var temp5 = temp4 + temp2.substring(index, temp2.length); // 07:17:43
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
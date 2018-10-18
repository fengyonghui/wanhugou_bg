(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData();//获取数据			
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
		},
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeaderForVendor/form4MobileNew",
                data: {id:_this.userInfo.inListId,str:'detail'},
                dataType: "json",
                success: function(res){
                	console.log(res)
                	//调取供应商信息
                	if(res.data.bizRequestHeader.bizVendInfo){
                		var officeId = res.data.bizRequestHeader.bizVendInfo.office.id;
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
                	/*当前用户信息*/
					var userId = '';
					$.ajax({
		                type: "GET",
		                url: "/a/getUser",
		                dataType: "json",
		                async:false,
		                success: function(user){                 
							userId = user.data.id
		                }
		           });
					/*业务状态*/
					if(userId!=""&&userId==1){		            				       			       
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
		            }else{
		            	$('#inPoDizstatus').parent().hide();
		            }
				    //排产状态
				    if(res.data.bizRequestHeader.bizPoHeader){
				    	var itempoSchType=res.data.bizRequestHeader.bizPoHeader.poSchType;
					    var SchedulstatusTxt = '';
					    $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                data: {type:"poSchType"},		                
			                dataType: "json",
			                success: function(reslt){
			                	$.each(reslt,function(i,item){
			                		if(item.value==itempoSchType){
			                		 	SchedulstatusTxt = item.label 
			                		}
			                		if(itempoSchType == null||itempoSchType == "") {
					                	SchedulstatusTxt = "未排产";
					                }
			                	})
			                	$('#inSchedulstatus').val(SchedulstatusTxt);
							}
						});
				    }else{
		                $('#inSchedulstatus').val("未排产");
				    };				    
					$('#inPoordNum').val(res.data.bizRequestHeader.reqNo);//备货单编号	
					//备货单类型
					$.ajax({
						type: 'GET',
						url: '/a/sys/dict/listData',
						data: {type:'req_header_type'},
						dataType: 'json',
						success: function(restype) {
							$.each(restype,function(n,v){
								if(res.data.bizRequestHeader.headerType==v.value){
								    $('#headerType').val(v.label);
								}
								if(res.data.bizRequestHeader.headerType==v.value){
									$('#headerType').val(v.label);
								}
							})							
						}
					});					
					//备货方
                    if(res.data.bizRequestHeader.fromType==1){
						$('#fromType1').attr('checked','checked');
						$('#fromType2').removeAttr('checked');
					}
					if(res.data.bizRequestHeader.fromType==2){
						$('#fromType1').removeAttr('checked');
						$('#fromType2').attr('checked','checked');						
					}	            	
					$('#inOrordNum').val(res.data.bizRequestHeader.fromOffice.name);//采购中心
					$('#inPototal').val(res.data.bizRequestHeader.totalMoney.toFixed(2));//应付金额
					$('#inMoneyReceive').val(res.data.bizRequestHeader.recvTotal.toFixed(2));//已收保证金
					$('#inMarginLevel').val((res.data.bizRequestHeader.recvTotal*100/res.data.bizRequestHeader.totalMoney) .toFixed(2)+ '%');//保证金比例
					if(res.data.bizRequestHeader.bizPoHeader==""){
						$('#inMoneyPay').val();
					}else{
						$('#inMoneyPay').val(res.data.bizRequestHeader.bizPoHeader.payTotal.toFixed(2));//已支付厂商保证金
					}					
					$('#inPoLastDa').val(_this.newData(res.data.bizRequestHeader.recvEta));//期望收货时间
					$('#inPoRemark').val(res.data.bizRequestHeader.remark);//备注
					_this.commodityHtml(res.data);//备货商品
					_this.statusListHtml(res.data);//状态流程					
					_this.paylistHtml(res.data);//支付列表
					_this.checkProcessHtml(res.data);//审批流程
					//排产信息
					if(res.data.bizRequestHeader.str=='detail'){
						var poheaderId = res.data.bizRequestHeader.bizPoHeader.id;
		                if (poheaderId == null || poheaderId == "") {
		                    $("#inSchedultype").val("未排产")
		                    $("#stockGoods").hide();
		                    $("#schedulingPlan_forHeader").hide();
		                    $("#schedulingPlan_forSku").hide();
		                }
		                if (poheaderId != null && poheaderId != "") {
		                	_this.scheduling(poheaderId);
		                }
					}					
                }
            });
		},
		
		//供应商信息
		supplier:function(supplierId){						
			$.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeaderForVendor/selectVendInfo",
                data: {vendorId:supplierId},		                
                dataType: "json",
                success: function(rest){
                	if(rest){
                		$('#insupplier').val(rest.vendName);//供应商
						$('#insupplierNum').val(rest.cardNumber);//供应商卡号
						$('#insupplierMoney').val(rest.payee);//供应商收款人
						$('#insupplierBank').val(rest.bankName);//供应商开户行
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
						$('#insuppliercontract').parent().hide();
						$('#insuppliercardID').parent().hide();
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
									'<div class="mui-input-row">'+
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
									'<div class="mui-input-row">'+
										'<label>结算价：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.unitPrice +'" disabled>'+
									'</div>'+
									'<div class="mui-input-row">'+
										'<label>总金额：</label>'+
										'<input type="text" class="mui-input-clear" value="'+ v.ordQty * v.unitPrice +'" disabled>'+
									'</div>'+
								'</div>'+
							'</li>'	+
							'<div class="mui-row app_bline2">'+
								'<label class="app_pr0 app_f13">排产记录:</label>'+
								'<ul id="schedulingHeaders" class="schedulingHeaders mui-table-view app_fr app_w70p">'+_this.eachCompletePaln(v)+'</ul>'+
							'</div>'
                        });
                        $("#purchaseMenus").append(poDetailHtmls);
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
        //支付列表
        paylistHtml:function(data){
        	var _this = this;
        	var htmlPaylist = '';
        	if(data.paymentOrderList != null && data.paymentOrderList.length > 0){
        		$.each(data.paymentOrderList, function(i, item) {
//					console.log(item)						
					if(item.payTime){
						var realitypayTime="";
						var realitypayTime=_this.formatDateTime(item.payTime);
					}else{
						var realitypayTime="";
					}
					htmlPaylist +='<li class="mui-table-view-cell mui-media payList">'+
						'<div class="mui-media-body">'+
							'<div class="mui-input-row">'+
								'<label>付款金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.total.toFixed(2) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>实际付款金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.payTotal.toFixed(2) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>最后付款时间：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.deadline) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>实际付款时间：</label>'+
								'<input type="text" class="mui-input-clear realitypayTime" value="'+ realitypayTime +'" disabled>'+
							'</div>'+
						'</div>'+
					'</li>'
			    });
			    $("#inPaylist").html(htmlPaylist);
        	}else{
        		$('#inPaylistbox').hide();
        	}       	
        },		
		//状态流程
		statusListHtml:function(data){
			var _this = this;
			var statusLen = data.auditStatusList.length;
			if(statusLen > 0) {
				var pHtmlList = '';
				$.each(data.auditStatusList, function(i, item) {
					if(i!=statusLen-1){
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
								        '<input type="text" value="'+ data.stateDescMap
			[item.bizStatus] +'" class="mui-input-clear" disabled>'+
								    	'<label>时间:</label>'+
								        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
					}
					if(i===statusLen-1){
//						console.log(i)
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
								        '<input type="text" value="'+ data.stateDescMap
			[item.bizStatus] +'" class="mui-input-clear" disabled>'+
								    	'<label>时间:</label>'+
								        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
					}
					$("#inCheckAddMenu").html(pHtmlList);
				});

			}
		},
		//审批流程
		checkProcessHtml:function(data){
			var _this = this;
			var auditLen = data.bizRequestHeader.commonProcessList.length;
			if(data.bizRequestHeader.commonProcessList) {
				var CheckHtmlList ='';
				$.each(data.bizRequestHeader.commonProcessList, function(i, item) {
					var auditLen = data.bizRequestHeader.commonProcessList.length;
					var step = i + 1;
					if(i!=auditLen-1) {
						CheckHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
						        '<label>批注:</label>'+
						        '<input type="text" value="'+ item.description +'" class="mui-input-clear" disabled>'+
						    	'<label>审批人:</label>'+
						        '<input type="text" value=" '+ item.user.name +' " class="mui-input-clear" disabled>'+
						        '<label>时间:</label>'+
						        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
					}	
					if(i==auditLen-1 && data.bizRequestHeader.processPo != 'processPo' && item.requestOrderProcess.name != '审核完成') {
						if(item.requestOrderProcess.name != '审核完成'){
							CheckHtmlList +='<li class="step_item">'+
								'<div class="step_num">'+ step +' </div>'+
								'<div class="step_num_txt">'+
									'<div class="mui-input-row">'+
								        '<label>当前状态:</label>'+
								        '<input type="text" value="'+ item.requestOrderProcess.name +'" class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
						}
					}
				});
				if(data.bizRequestHeader.bizPoHeader!=""){
					$.each(data.bizRequestHeader.bizPoHeader.commonProcessList, function(a, items) {
						var len = data.bizRequestHeader.bizPoHeader.commonProcessList.length;
						var totalStep = auditLen + a;
                        if(a==0&&len>1){
                        	CheckHtmlList +='<li class="step_item">'+
								'<div class="step_num">'+ totalStep +' </div>'+
								'<div class="step_num_txt">'+
									'<div class="mui-input-row">'+
								        '<label>批注:</label>'+
								        '<input type="text" value="'+ items.description +'" class="mui-input-clear" disabled>'+
								    	'<label>审批人:</label>'+
								        '<input type="text" value=" '+ items.user.name +' " class="mui-input-clear" disabled>'+
								        '<label>时间:</label>'+
								        '<input type="text" value=" '+ _this.formatDateTime(items.updateTime) +' " class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
                        }
                        if(a>0&&a<len-1){
                        	CheckHtmlList +='<li class="step_item">'+
								'<div class="step_num">'+ totalStep +' </div>'+
								'<div class="step_num_txt">'+
									'<div class="mui-input-row">'+
								        '<label>批注:</label>'+
								        '<input type="text" value="'+ items.description +'" class="mui-input-clear" disabled>'+
								    	'<label>审批人:</label>'+
								        '<input type="text" value=" '+ items.user.name +' " class="mui-input-clear" disabled>'+
								        '<label>时间:</label>'+
								        '<input type="text" value=" '+ _this.formatDateTime(items.updateTime) +' " class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
                        }
                        if(a==len-1){
                        	CheckHtmlList +='<li class="step_item">'+
								'<div class="step_num">'+ totalStep +' </div>'+
								'<div class="step_num_txt">'+
									'<div class="mui-input-row">'+
								        '<label>当前状态:</label>'+
								        '<input type="text" value="'+ items.purchaseOrderProcess.name +'" class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
                        }
					});
				}					
				$("#inapprovalAddMenu").html(CheckHtmlList);
			}else{
				$("#inapprovalAddMenu").parent().hide();
			}
		},
		//备货商品
		commodityHtml: function(data) {
			var _this = this;
			var htmlCommodity = '';
			if(data.reqDetailList!=null){				
				$.each(data.reqDetailList, function(i, item) {
					if(data.bizRequestHeader.str=='detail'&&data.bizRequestHeader.bizStatus>=data.UNREVIEWED){
						var invNameTxt = item.invName;
						var skuInvQtyTxt= item.skuInvQty;
						var sellCountTxt= item.sellCount;
						if(data.roleChanne!="" && data.roleChanne=='channeOk'){
							var invenSkuOrdTxt= item.invenSkuOrd;
						}else{
							var invenSkuOrdTxt= "";
						}
					}
					if(data.bizRequestHeader.str=='detail'&&data.bizRequestHeader.bizStatus>=10){
						var recvQtyTxt= item.recvQty;
					}else{
						var recvQtyTxt= "";
						$('#commodityMenu #recvQtys').hide();						
					}					
					htmlCommodity +='<li class="mui-table-view-cell mui-media app_bline app_pr">'+
	//		产品图片
						'<div class="photoParent mui-pull-left app_pa">'+
							'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'">'+
						'</div>'+
	//		产品信息
						'<div class="mui-media-body app_w80p app_fr">'+
							'<div class="mui-input-row">'+
								'<label>品牌名称：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.brandName +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>供应商：</label>'+
								'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuInfo.productInfo.office.name +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>商品名称：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.name +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>商品编码：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.partNo +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>商品货号：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>结算价：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.unitPrice.toFixed(1) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>申报数量：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.reqQty +'" reqQty disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>仓库名称：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ invNameTxt +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>库存数量：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ skuInvQtyTxt +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>销售量：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ sellCountTxt +'" disabled>'+
							'</div>'+							
							'<div class="mui-input-row">'+
								'<label>总库存数量：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ invenSkuOrdTxt +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>已收货数量：</label>'+
								'<input type="text" class="mui-input-clear" id="recvQtys" value="'+ recvQtyTxt +'" disabled>'+
							'</div>'+
						'</div>'+
					'</li>'
				});
				$("#commodityMenu").html(htmlCommodity)
			}			
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

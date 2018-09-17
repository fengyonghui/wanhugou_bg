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
                	}               	
				    /*业务状态*/
				    var itemStatus=res.data.bizRequestHeader.bizStatus;
				    var bizstatusTxt = '';
				    $.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {type:"biz_req_status"},		                
		                dataType: "json",
		                success: function(res){
		                	$.each(res,function(i,item){
		                		 if(item.value==itemStatus){
		                		 	  bizstatusTxt = item.label 
		                		 }
		                	})
		                	$('#inPoDizstatus').val(bizstatusTxt);
						}
					});
				    //排产状态
				    var itempoSchType=res.data.bizRequestHeader.bizPoHeader.poSchType;
				    var SchedulstatusTxt = '';
				    $.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {type:"poSchType"},		                
		                dataType: "json",
		                success: function(res){
		                	console.log(res)
		                	$.each(res,function(i,item){
		                		 if(item.value==itempoSchType){
		                		 	  SchedulstatusTxt = item.label 
		                		 }
		                	})
		                	$('#inSchedulstatus').val(SchedulstatusTxt);
						}
					});
					$('#inPoordNum').val(res.data.bizRequestHeader.reqNo);//备货单编号	
					//备货方
					console.log(res.data.bizRequestHeader.fromType);
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
					$('#inPoLastDa').val(_this.formatDateTime(res.data.bizRequestHeader.recvEta));//期望收货时间
					$('#inPoRemark').val(res.data.bizRequestHeader.remark);//备注
					_this.commodityHtml(res.data);//备货商品
					_this.statusListHtml(res.data);//状态流程					
					_this.paylistHtml(res.data);//支付列表
					_this.checkProcessHtml(res.data);//审批流程
					//排产信息
					if(res.data.bizRequestHeader.str=='detail'){
						var poheaderId = res.data.bizRequestHeader.bizPoHeader.id;
						console.log(poheaderId)
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
                	console.log(rest)
                	if(rest){
                		$('#insupplier').val(rest.vendName);//供应商
						$('#insupplierNum').val(rest.cardNumber);//供应商卡号
						$('#insupplierMoney').val(rest.payee);//供应商收款人
						$('#insupplierBank').val(rest.bankName);//供应商开户行
                	}else{
                		$('#insupplier').parent().hide();//供应商
						$('#insupplierNum').parent().hide();//供应商卡号
						$('#insupplierMoney').parent().hide();//供应商收款人
						$('#insupplierBank').parent().hide();//供应商开户行
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
                	console.log('---')
                	console.log(res.data);
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
                        console.log(poDetailList)
                        var poDetailHtml = "";
                        $.each(poDetailList,function(n,v){
                        	console.log(v)
                        	poDetailHtml +='<li class="mui-table-view-cell mui-media app_pr">'+
								'<div class="photoParent mui-pull-left app_pa">'+
									'<img class="app_pa" src="'+v.skuInfo.productInfo.imgUrl+'">'+
								'</div>'+
								'<div class="mui-media-body app_w80p app_fr">'+
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
                        	console.log(v)
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
                        var remarkHtml = "<textarea id='schRemarkOrder' style='border:1px solid #ccc;'>" + res.data.bizPoHeader.bizSchedulingPlan.remark + "</textarea>";
                        $("#schedulingHeaderRemark").append(remarkHtml);
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
                        	console.log(v)
                        	poDetailHtmls +='<li class="mui-table-view-cell mui-media">'+
								'<div class="photoParent mui-pull-left app_pa">'+
									'<img class="app_pa" src="'+v.skuInfo.productInfo.imgUrl+'">'+
								'</div>'+
								'<div class="mui-media-body app_w80p app_fr">'+
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

							
                        });
                        $("#purchaseMenus").append(poDetailHtmls);
                        //按商品排产中的排产记录
                        console.log(res.data.bizPoHeader.poDetailList)
                        var completePalnHtml = "";
                        $.each(res.data.bizPoHeader.poDetailList,function(n,v){
                            console.log(v);
                            console.log(v.bizSchedulingPlan.completePalnList);
                            $.each(v.bizSchedulingPlan.completePalnList,function(n,v){
                            	completePalnHtml +='<li class="mui-table-view-cell mui-media app_pr">'+
								'<div class="mui-media-body app_w80p app_fr">'+
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
	                             $("#schedulingHeaders").append(completePalnHtml);
                            });
                        });
                        //按商品排产中的排产备注
                        var remarkHtmls = "<textarea id='schRemarkOrder' style='border:1px solid #ccc;'>" + res.data.bizPoHeader.bizSchedulingPlan.remark + "</textarea>";
                        $("#schedulingHeaderRemarks").append(remarkHtmls);    
                	}
				}
			})
		},
        //支付列表
        paylistHtml:function(data){
        	var _this = this;
        	var htmlPaylist = '';
        	console.log(data)
        	if(data.paymentOrderList != null && data.paymentOrderList.length > 0){
        		$.each(data.paymentOrderList, function(i, item) {
					console.log(item)			
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
								'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.payTime) +'" disabled>'+
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
						console.log(i)
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
//					$("#inCheckAddMenu").html(pHtmlList);
					if(i===statusLen-1){
						console.log(i)
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
					console.log(item)
					var auditLen = data.bizRequestHeader.commonProcessList.length;
					console.log(auditLen-1)
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
//auditLen = 1&& data.bizRequestHeader.bizPoHeader.commonProcessList == null
//
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
				if(data.bizRequestHeader.bizPoHeader!=""){
					$.each(data.bizRequestHeader.bizPoHeader.commonProcessList, function(a, items) {
						console.log(items)
						var len = data.bizRequestHeader.bizPoHeader.commonProcessList.length;
						console.log(len)
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
								'<input type="text" class="mui-input-clear" value="'+ item.invName +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>库存数量：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.skuInvQty +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>总库存数量：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.invenSkuOrd +'" disabled>'+
							'</div>'+
						'</div>'+
					'</li>'
				});
				$("#commodityMenu").html(htmlCommodity)
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

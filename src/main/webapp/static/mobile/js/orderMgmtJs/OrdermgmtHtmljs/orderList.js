(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.staOrdFlag = "false";
		this.staOrdauditFlag = "false";
		this.staOrdsupplyFlag = "false";
		this.staOrdeditFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
			this.getPermissionList('biz:order:bizOrderHeader:view','staOrdFlag')//true 操作
			this.getPermissionList1('biz:order:bizOrderHeader:audit','staOrdauditFlag')//false审核
			this.getPermissionList2('biz:order:bizOrderHeader:supplying','staOrdsupplyFlag')//false出库确认
			this.getPermissionList3('biz:order:bizOrderHeader:edit','staOrdeditFlag')//修改、删除true
			if(this.userInfo.isFunc){
				this.seachFunc()
			}else{
				this.pageInit(); //页面初始化
			}
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			var pager = {};//分页 
		    var totalPage;//总页码
		    pullRefresh(pager);//启用上拉下拉 
		    function pullRefresh(){
		        mui("#refreshContainer").pullRefresh({
			        up:{
			            contentnomore:'没 有 更 多 数 据 了',
			            callback:function(){			            	
			                window.setTimeout(function(){
			                    getData(pager);
			                },100);
			            }
			         },
			        down : {
			            height:50,
			            auto: true,
			            contentdown : "",
			            contentover : "",
			            contentrefresh : "正在加载...",
			            callback :function(){ 
			                    pager['size']= 20;
			                    pager['pageNo'] = 1;
				                var f = document.getElementById("orderList");
				                var childs = f.childNodes;
				                for(var i = childs.length - 1; i >= 0; i--) {
				                    f.removeChild(childs[i]);
				                }			                
				                $('.mui-pull-caption-down').html('');				                
				                getData(pager);
			            }
			        }
			    })
		    }
		    function getData(params){
		    	var staffHtmlList = '';
		    	var ass=[];
		        mui.ajax("/a/biz/order/bizOrderHeader/listData4mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	console.log(res)
		            	var dataRow = res.data.roleSet;
		            	//订单类型
		          	    $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "biz_order_type"},
			                async:false,
			                success: function(res){                 
				                ass=res;
			                }
			            });	
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
						var arrLen = res.data.page.list.length;
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true)
						}
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
                            	console.log(item)
                            	var ProcessName = '';
                            	var objectName = item.commonProcess.objectName; 
                            	var commonProcess = item.commonProcess;
								if(item.bizStatus != res.data.CANCLE && item.bizStatus != res.data.DELETE && item.bizStatus != res.data.UNAPPROVE) {
									if(item.bizStatus < res.data.SUPPLYING) {
										ProcessName = '待客户专员审核'
									}
									if(item.orderType == res.data.PURCHASE_ORDER && item.bizStatus >= res.data.SUPPLYING) {
										if(objectName == 'biz_order_header') {
											if(commonProcess.doOrderHeaderProcessFifth.name != '审批完成') {
												ProcessName = commonProcess.doOrderHeaderProcessFifth.name
											}
											if(commonProcess.doOrderHeaderProcessFifth.name == '审批完成') {
												ProcessName = '订单支出信息审核'
											}
										}
									}
									if(item.orderType == res.data.ORDINARY_ORDER && item.bizStatus >= res.data.SUPPLYING) {
										if(objectName == 'ORDER_HEADER_SO_LOCAL') {
											ProcessName = commonProcess.jointOperationLocalProcess.name
										}
										if(objectName == 'ORDER_HEADER_SO_ORIGIN') {
											if(commonProcess.jointOperationOriginProcess.name != '审批完成') {
												ProcessName = commonProcess.jointOperationOriginProcess.name
											}
											if(commonProcess.jointOperationOriginProcess.name == '审批完成') {
												ProcessName = '订单支出信息审核'
											}
										}
									}
								}
	                        	$('#consultantIda').val(item.consultantId);
								$('#statu').val(item.statu);
								$('#source').val(item.source);
	                        	//订单类型  1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单 5.代采订单 6.拍照下单
	                            var orderTypeTxt = '';
	                            $.each(ass,function(i,items){
		                        	if(item.orderType==items.value) {
		                        		orderTypeTxt = items.label
		                        	}
	                            })
//	                        	c:if test="${orderHeader.bizStatus != OrderHeaderBizStatusEnum.CANCLE.state}">
//									<c:if test="${orderHeader.orderType == BizOrderTypeEnum.PURCHASE_ORDER.state && orderHeader.bizStatus >= OrderHeaderBizStatusEnum.SUPPLYING.state}">
//										<shiro:hasPermission name="biz:order:bizOrderHeader:audit">
//											<c:if test="${(fns:hasRole(roleSet, orderHeader.commonProcess.doOrderHeaderProcessFifth.roleEnNameEnum) || fns:getUser().isAdmin())
//													&& orderHeader.commonProcess.doOrderHeaderProcessFifth.name != '驳回'
//													&& orderHeader.commonProcess.doOrderHeaderProcessFifth.code != auditFithStatus
//													}">
//												<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&str=audit">审核</a>
//											</c:if>
//				
//											<c:if test="${fns:hasRole(roleSet, orderHeader.commonProcess.jointOperationOriginProcess.roleEnNameEnum) && orderHeader.commonProcess.jointOperationOriginProcess.name != '驳回' && orderHeader.commonProcess.jointOperationOriginProcess.code != auditStatus
//												 && orderHeader.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state}">
//												<a href="${ctx}/biz/order/bizORderHeader/form?id=${orderHeader.id}&str=audit&suplys=${orderHeader.suplys}">审核</a>
//											</c:if>
//										</shiro:hasPermission>
//									</c:if >
//									<shiro:hasPermission name="biz:order:bizOrderHeader:audit">
//										<c:if test="${orderHeader.commonProcess != null && orderHeader.commonProcess.id != null
//											&& orderHeader.commonProcess.purchaseOrderProcess.name != '驳回'
//											&& orderHeader.commonProcess.purchaseOrderProcess.name != '审批完成'
//											&& (fns:hasRoleByProcess(roleSet, orderHeader.commonProcess.jointOperationLocalProcess)
//											 	|| fns:hasRoleByProcess(roleSet, orderHeader.commonProcess.jointOperationOriginProcess)
//											 	 || fns:getUser().isAdmin())
//											}">
//											<c:if test="${orderHeader.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state && orderHeader.bizStatus >= OrderHeaderBizStatusEnum.SUPPLYING.state}">
//												<%--<c:if test="${orderHeader.bizStatus < OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.state}">--%>
//													<c:if test="${orderHeader.suplys == 0 }">
//														<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&str=audit&type=0">审核</a>
//													</c:if>
//													<c:if test="${orderHeader.suplys != 0 }">
//														<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&str=audit&type=1">审核</a>
//													</c:if>
//												<%--</c:if>--%>
//											</c:if>
//										</c:if>
//									</shiro:hasPermission>
//									<shiro:hasPermission name="biz:order:bizOrderHeader:supplying">
//										<c:if test="${orderHeader.bizStatus >= OrderHeaderBizStatusEnum.SUPPLYING.state && orderHeader.bizStatus <= OrderHeaderBizStatusEnum.STOCKING.state && orderHeader.suplys != 0 && orderHeader.suplys != 721}">
//											<c:if test="${fn:length(orderHeader.bizInvoiceList) <= 0}">
//												<a href="${ctx}/biz/inventory/bizInvoice/formV2?id=${orderHeader.id}&type=1">出库确认</a>
//											</c:if>
//										</c:if>
//									</shiro:hasPermission>
//								</c:if >
								var ordCheckBtn = '';
	                        	var staCheckBtnTxt = '';
	                        	var ordSupplyBtn = '';
	                        	var staSupplyBtnTxt = '';
				                if(_this.staOrdFlag == true) {
				                	//审核
                                    if(item.bizStatus != res.data.CANCLE){
                                        if(item.orderType == res.data.PURCHASE_ORDER && item.bizStatus >= res.data.SUPPLYING){
                                        	console.log('审核1')
                                         	if(_this.staOrdauditFlag == true){
												var DataRoleGener = '';
												if(commonProcess) {
													DataRoleGener = commonProcess.doOrderHeaderProcessFifth.roleEnNameEnum;
												}
												console.log(DataRoleGener)
												var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));	
                                         		if((fileRoleData || userId==1) && commonProcess.doOrderHeaderProcessFifth.name != '驳回'
                                         		&& commonProcess.doOrderHeaderProcessFifth.code != res.data.auditFithStatus){
                                         			staCheckBtnTxt="审核";
                                         			ordCheckBtn = 'ordCheckBtn';
                                         		}
//												var DataRoleGeners = '';
//												if(commonProcess) {
//													console.log(commonProcess)
//													DataRoleGeners = commonProcess.jointOperationOriginProcess.roleEnNameEnum;
//												}
//												console.log(DataRoleGeners)
//												var fileRoleDatas = dataRow.filter(v => DataRoleGeners.includes(v));
//												console.log(res.data.auditStatus)
//												var auditStatu = '';
//					                            $.each(res.data.auditStatus,function(q,s){
//					                            	console.log(s)
//						                        	auditStatu=s
//					                            })
//					                            console.log(auditStatu)
//                                       		if(fileRoleDatas && commonProcess.jointOperationOriginProcess.name != '驳回'
//                                       		&& commonProcess.jointOperationOriginProcess.code != auditStatu
//                                       		&& item.orderType == res.data.ORDINARY_ORDER){
//                                       			staCheckBtnTxt="审核";
//													ordCheckBtn = 'ordCheckBtn';
//                                       		}
                                         	}
                                        }
                                        if(_this.staOrdauditFlag == true){
                                        	console.log('审核2')
                                            var DataRole = '';
											if(commonProcess) {
												DataRole = commonProcess.jointOperationLocalProcess;
											}
											console.log(DataRole)
//											var fileRole = dataRow.filter(v => DataRole.includes(v));
											var DataRoles = '';
											if(commonProcess) {
												DataRoles = commonProcess.jointOperationOriginProcess;
											}
											console.log(DataRoles)
//											var fileRoles = dataRow.filter(v => DataRoles.includes(v));
                                            if(commonProcess != null && commonProcess.id != null
                                            	&& commonProcess.purchaseOrderProcess.name != '驳回' 
                                            	&& commonProcess.purchaseOrderProcess.name != '审批完成'
                                            	&& DataRole.name != "全额支付不需要审批"
                                            	&& (DataRole||DataRoles||userId==1)){
                                                if(item.orderType == res.data.ORDINARY_ORDER && item.bizStatus >= res.data.SUPPLYING){
                                                    if(item.suplys == 0 ){
                                                     	staCheckBtnTxt="审核";
                                                     	ordCheckBtn = 'ordCheckBtn';
                                                    }
                                                    if(item.suplys != 0 ){
                                                     	staCheckBtnTxt="审核";
                                                     	ordCheckBtn = 'ordCheckBtn';
                                                    }
                                                }
                                        	}
                                        }
                                        //出库确认
                                        if(_this.staOrdsupplyFlag == true){
                                        	if(item.bizStatus >= res.data.SUPPLYING && item.bizStatus <= res.data.STOCKING && item.suplys != 0 && item.suplys != 721){
                                        		if(item.bizInvoiceList.length <= 0){
                                        			staSupplyBtnTxt="出库";
                                        			ordSupplyBtn = 'ordSupplyBtn';
                                        		}
                                        	}
                                        }
                                    }
                                    var ordwaterCourseBtn = '';
                                	var ordwaterCourseBtnTxt ="";
                                	var ordAmendBtn = '';
                                    var staAmendTxt ="";
                                    var ordDeleteBtn = '';
                                    var staDeleteTxt ="";
                                    var ordDetailBtn = '';
                                    var staDetailBtnTxt ="";
                                    if(item.delFlag!=null && item.delFlag == '1'){
                                    	//支付流水
                                    	if(item.bizStatus != res.data.CANCLE){
                                    		if(item.statu == 'unline' || userId==1){
                                    			ordwaterCourseBtnTxt ="流水";
                                    			ordwaterCourseBtn = 'ordwaterCourseBtn';
                                    		}
                                    	}
                                    	//详情
	                                    if(item.orderType != res.data.PHOTO_ORDER){
	                                    	staDetailBtnTxt="详情";
	                                    	ordDetailBtn = 'ordDetailBtn';
	                                    }
	                                    if(item.orderType == res.data.PHOTO_ORDER){
	                                    	staDetailBtnTxt="详情";
	                                    	ordDetailBtn = 'ordDetailBtn';
	                                    }
	                                    //修改、删除
	                                    if(item.bizStatus != res.data.CANCLE){
	                                    	if(_this.staOrdeditFlag==true){
	                                    		if(item.orderType != res.data.PHOTO_ORDER && (item.bizStatus < res.data.SUPPLYING || userId==1)){
	                                    			staAmendTxt ="修改";
	                                    			ordAmendBtn = 'ordAmendBtn';
	                                    		}
	                                    		if(item.orderType == res.data.PHOTO_ORDER && (item.bizStatus < res.data.SUPPLYING || userId==1)){
	                                    			staAmendTxt ="修改";
	                                    			ordAmendBtn = 'ordAmendBtn';
	                                    		}
	                                    		if(userId==1){
	                                    			staDeleteTxt ="删除";
	                                    			ordDeleteBtn = 'ordDeleteBtn';
	                                    		}
	                                    	}
	                                    }
	                                }
	                                //详情另一种情况
									var ordRecoveryBtn = '';
	                                var staRecoveryBtnTxt ="";//h恢复
	                                if(_this.staOrdeditFlag==true){
	                                	if(item.delFlag!=null && item.delFlag == '0'){
	                                		staDetailBtnTxt ="详情";
	                                		ordDetailBtn = 'ordDetailBtn';
	                                		staRecoveryBtnTxt ="恢复";
	                                		ordRecoveryBtn = 'ordRecoveryBtn';
	                                	}
	                                }
                                    
				                }
	                        	var staCheckSucBtn = '';
	                        	var staCheckSuc = '';
									staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row">' +
											'<label>订单编号:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderNum+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>经销店:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.customer.name+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>订单类型:</label>' +
											'<input type="text" class="mui-input-clear orderTypeTxt" disabled="disabled" value=" '+orderTypeTxt+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>审核状态:</label>' +
											'<input type="text" class="mui-input-clear" value=" '+ ProcessName +' " disabled="disabled">' + 
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>创建时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>更新时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
										'</div>' +
										//业务状态需要添加权限 mui-col-xs-2 
//										'<div class="mui-input-row">' +
//											'<label>业务状态:</label>' +
//											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
//										'</div>' +
										'<div class="app_color40 mui-row app_text_center content_part operation " id="foot">' +
											'<div class="'+ ordCheckBtn +'" staOrdId="'+ item.id +'">' +
												 staCheckBtnTxt +
											'</div>'+
											'<div class="'+ordSupplyBtn+'"  staOrdId="'+ item.id +'">' +
												staSupplyBtnTxt +
											'</div>'+
											'<div class="'+ordwaterCourseBtn+'"  staOrdId="'+ item.id +'">' +
												 ordwaterCourseBtnTxt +
											'</div>'+
											'<div class="'+ordAmendBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ item.statu +'" ordsource="'+ item.source +'">' +
												staAmendTxt +
											'</div>'+
											'<div class="'+ordDeleteBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ item.statu +'" ordsource="'+ item.source +'">' +
												staDeleteTxt +
											'</div>'+
											'<div class="'+ordDetailBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ item.statu +'" ordsource="'+ item.source +'">' +
												staDetailBtnTxt +
											'</div>'+
											'<div class="'+ordRecoveryBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ item.statu +'" ordsource="'+ item.source +'">' +
												staRecoveryBtnTxt +
											'</div>'+
										'</div>' +
									'</div>'
								});
								$('#orderList').append(staffHtmlList);
								_this.stOrdHrefHtml();
					} else {
								$('.mui-pull-bottom-pocket').html('');
								$('#orderList').append('<p class="noneTxt">暂无数据</p>');
								mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);							
						}
		                if(res.data.page.totalPage==pager.pageNo){		                	
			                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);			                
			            }else{
			                pager.pageNo++;
			                mui('#refreshContainer').pullRefresh().refresh(true);
			            } 			           
			        },
		            error:function(xhr,type,errorThrown){
			            console.log(type);
		            }
		        })
		    }
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
                    _this.staOrdFlag = res.data;
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
//              	console.log(res.data) //false
                    _this.staOrdauditFlag = res.data;
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
//              	console.log(res.data) //false
                    _this.staOrdsupplyFlag = res.data;
                }
            });
        },
        getPermissionList3: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                	console.log(res.data) //false
                    _this.staOrdeditFlag = res.data;
                }
            });
        },
		stOrdHrefHtml: function() {
			var _this = this;
		/*查询*/
			$('.app_header').on('tap', '#OrdSechBtn', function() {
				alert(1)
				var url = $(this).attr('url');
//				var staListIds = $('#consultantId').val();
//				var staListIdTxts = $('#staListIdTxt').val(); 
//				var conId = '';
//				if(staListIdTxts) {
//					conId = $('#staListIdTxt').val();
//				}
//				if(staListIds) {
//					conId = $('#consultantId').val();
//				}
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderListSeach.html",
						extras:{
//							staListId: conId,
						}
					})
				}
			}),
		/*首页*/
			$('#nav').on('tap','.staHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			}),	
		 /*审核*/
	       $('.content_part').on('tap', '.ordCheckBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');//订单 ID
//				var stcheckIdTxt = '';
//				if(staListIdTxts) {
//					stcheckIdTxt = staListIdTxts
//				}
//				if(consultantIda) {
//					stcheckIdTxt = consultantIda
//				}
//				console.log(staListIdTxts)
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderCheck.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			}),
		/*修改*/
	       $('.content_part').on('tap', '.ordAmendBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				var statu=$(this).attr('ordstatu');
				var source=$(this).attr('ordsource');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
						extras: {
							staOrdId: staOrdId,
							statu:statu,
							source:source,
						}
					})
				}
			}),	
		/*详情*/
			$('.content_part').on('tap', '.ordDetailBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				var orderDetail="details";
				var statu=$(this).attr('ordstatu');
				var source=$(this).attr('ordsource');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordDetail.html",
						extras: {
							staOrdId: staOrdId,
							orderDetails:orderDetail,
							statu:statu,
							source:source,
						}
					})
				}
			}),
			//删除
			$('.content_part').on('tap', '.ordDeleteBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
//				console.log(staOrdId)
				var statu=$(this).attr('ordstatu');
				var source=$(this).attr('ordsource');
				
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认删除该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderHeader/delete4Mobile",
				                data: {id:staOrdId,statu:statu,source:source},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
											extras: {
	//												inListId:inListId,
											}
										})
					                },300);
				                	
			                	}
			            	})
						}else {
							
						}
					})
				}
			}),
			//恢复
			$('.content_part').on('tap', '.ordRecoveryBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				var statu=$(this).attr('ordstatu');				
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认恢复该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderHeader/recovery4Mobile",
				                data: {id:staOrdId,statu:statu},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
											extras: {
	//												inListId:inListId,
											}
										})
					                },300);
				                	
			                	}
			            	})
						}else {
							
						}
					})
				}
			})
			//出库 Stock- Out /
			$('.content_part').on('tap', '.ordSupplyBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');			
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
                    GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderstockOut.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
			//支付流水
			$('.content_part').on('tap', '.ordwaterCourseBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');			
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
                    GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordwaterCourse.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
        },
        //时间戳转化方法：
		formatDateTime: function(unix) {
			var _this = this;
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
		},
		//查询过来渲染页面:
		seachFunc:function(){
			var _this = this;
			var staffHtmlList = '';
//			var nameTxt = '';
//			if(_this.userInfo.checkStatus) {
//				nameTxt = decodeURIComponent(_this.userInfo.checkStatus)
//			}else {
//				nameTxt = ''
//			}
			var nameTxts = '';
			if(_this.userInfo.Purchasing) {
				nameTxts = decodeURIComponent(_this.userInfo.Purchasing)
			}else {
				nameTxts = ''
			}
			$.ajax({
				type: 'GET',
                url: '/a/biz/order/bizOrderHeader/listData4mobile',
				data: {
					pageNo: 1,
					orderNum: _this.userInfo.orderNum,//订单编号
					bizStatus: _this.userInfo.bizStatus,//订单状态
                    selectAuditStatus: _this.userInfo.selectAuditStatus,//审核状态
                    retainage: _this.userInfo.retainage,//尾款
                    'customer.phone': _this.userInfo.customerPhone,//经销商电话
                    itemNo: _this.userInfo.itemNo,//商品货号
                    'customer.id': _this.userInfo.customerName,//经销店名称
                    centersName: _this.userInfo.centersName,//采购中心
                    'con.name': _this.userInfo.conName,//客户专员
					mobileAuditStatus: _this.userInfo.mobileAuditStatus,//待同意发货
					waitShipments: _this.userInfo.waitShipments,//待发货
					waitOutput: _this.userInfo.waitOutput,//待出库
					includeTestData: _this.userInfo.includeTestData//测试数据
				},
				dataType: 'json',
				success: function(res) {
					var dataRow = res.data.roleSet;
					$('#flag').val(_this.userInfo.flagTxt)
					$('#staListIdTxt').val(_this.userInfo.staListSehId)//查询出来的客户专员 ID
					$.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "biz_order_type"},
			                async:false,
			                success: function(res){                 
				                ass=res;
			                }
			        });
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
		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						var arrLen = res.data.page.list.length;	
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
                            	console.log(item)
                            	var ProcessName = '';
                            	var objectName = item.commonProcess.objectName; 
                            	var commonProcess = item.commonProcess;
								if(item.bizStatus != res.data.CANCLE && item.bizStatus != res.data.DELETE && item.bizStatus != res.data.UNAPPROVE) {
									if(item.bizStatus < res.data.SUPPLYING) {
										ProcessName = '待客户专员审核'
									}
									if(item.orderType == res.data.PURCHASE_ORDER && item.bizStatus >= res.data.SUPPLYING) {
										if(objectName == 'biz_order_header') {
											if(commonProcess.doOrderHeaderProcessFifth.name != '审批完成') {
												ProcessName = commonProcess.doOrderHeaderProcessFifth.name
											}
											if(commonProcess.doOrderHeaderProcessFifth.name == '审批完成') {
												ProcessName = '订单支出信息审核'
											}
										}
									}
									if(item.orderType == res.data.ORDINARY_ORDER && item.bizStatus >= res.data.SUPPLYING) {
										if(objectName == 'ORDER_HEADER_SO_LOCAL') {
											ProcessName = commonProcess.jointOperationLocalProcess.name
										}
										if(objectName == 'ORDER_HEADER_SO_ORIGIN') {
											if(commonProcess.jointOperationOriginProcess.name != '审批完成') {
												ProcessName = commonProcess.jointOperationOriginProcess.name
											}
											if(commonProcess.jointOperationOriginProcess.name == '审批完成') {
												ProcessName = '订单支出信息审核'
											}
										}
									}
								}
	                        	$('#consultantIda').val(item.consultantId);
								$('#statu').val(item.statu);
								$('#source').val(item.source);
	                        	//订单类型  1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单 5.代采订单 6.拍照下单
	                            var orderTypeTxt = '';
	                            $.each(ass,function(i,items){
		                        	if(item.orderType==items.value) {
		                        		orderTypeTxt = items.label
		                        	}
	                            })
								var ordCheckBtn = '';
	                        	var staCheckBtnTxt = '';
	                        	var ordSupplyBtn = '';
	                        	var staSupplyBtnTxt = '';
				                if(_this.staOrdFlag == true) {
				                	//审核
                                    if(item.bizStatus != res.data.CANCLE){
                                        if(item.orderType == res.data.PURCHASE_ORDER && item.bizStatus >= res.data.SUPPLYING){
                                        	console.log('审核1')
                                         	if(_this.staOrdauditFlag == true){
												var DataRoleGener = '';
												if(commonProcess) {
													DataRoleGener = commonProcess.doOrderHeaderProcessFifth.roleEnNameEnum;
												}
												console.log(DataRoleGener)
												var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));	
                                         		if((fileRoleData || userId==1) && commonProcess.doOrderHeaderProcessFifth.name != '驳回'
                                         		&& commonProcess.doOrderHeaderProcessFifth.code != res.data.auditFithStatus){
                                         			staCheckBtnTxt="审核";
                                         			ordCheckBtn = 'ordCheckBtn';
                                         		}
//												var DataRoleGeners = '';
//												if(commonProcess) {
//													console.log(commonProcess)
//													DataRoleGeners = commonProcess.jointOperationOriginProcess.roleEnNameEnum;
//												}
//												console.log(DataRoleGeners)
//												var fileRoleDatas = dataRow.filter(v => DataRoleGeners.includes(v));
//												console.log(res.data.auditStatus)
//												var auditStatu = '';
//					                            $.each(res.data.auditStatus,function(q,s){
//					                            	console.log(s)
//						                        	auditStatu=s
//					                            })
//					                            console.log(auditStatu)
//                                       		if(fileRoleDatas && commonProcess.jointOperationOriginProcess.name != '驳回'
//                                       		&& commonProcess.jointOperationOriginProcess.code != auditStatu
//                                       		&& item.orderType == res.data.ORDINARY_ORDER){
//                                       			staCheckBtnTxt="审核";
//													ordCheckBtn = 'ordCheckBtn';
//                                       		}
                                         	}
                                        }
                                        if(_this.staOrdauditFlag == true){
                                        	console.log('审核2')
                                            var DataRole = '';
											if(commonProcess) {
												DataRole = commonProcess.jointOperationLocalProcess;
											}
											console.log(DataRole)
//											var fileRole = dataRow.filter(v => DataRole.includes(v));
											var DataRoles = '';
											if(commonProcess) {
												DataRoles = commonProcess.jointOperationOriginProcess;
											}
											console.log(DataRoles)
//											var fileRoles = dataRow.filter(v => DataRoles.includes(v));
                                            if(commonProcess != null && commonProcess.id != null
                                            	&& commonProcess.purchaseOrderProcess.name != '驳回' 
                                            	&& commonProcess.purchaseOrderProcess.name != '审批完成'
                                            	&& DataRole.name != "全额支付不需要审批"
                                            	&& (DataRole||DataRoles||userId==1)){
                                                if(item.orderType == res.data.ORDINARY_ORDER && item.bizStatus >= res.data.SUPPLYING){
                                                    if(item.suplys == 0 ){
                                                     	staCheckBtnTxt="审核";
                                                     	ordCheckBtn = 'ordCheckBtn';
                                                    }
                                                    if(item.suplys != 0 ){
                                                     	staCheckBtnTxt="审核";
                                                     	ordCheckBtn = 'ordCheckBtn';
                                                    }
                                                }
                                        	}
                                        }
                                        //出库确认
                                        if(_this.staOrdsupplyFlag == true){
                                        	if(item.bizStatus >= res.data.SUPPLYING && item.bizStatus <= res.data.STOCKING && item.suplys != 0 && item.suplys != 721){
                                        		if(item.bizInvoiceList.length <= 0){
                                        			staSupplyBtnTxt="出库";
                                        			ordSupplyBtn = 'ordSupplyBtn';
                                        		}
                                        	}
                                        }
                                    }
                                    var ordwaterCourseBtn = '';
                                	var ordwaterCourseBtnTxt ="";
                                	var ordAmendBtn = '';
                                    var staAmendTxt ="";
                                    var ordDeleteBtn = '';
                                    var staDeleteTxt ="";
                                    var ordDetailBtn = '';
                                    var staDetailBtnTxt ="";
                                    if(item.delFlag!=null && item.delFlag == '1'){
                                    	//支付流水
                                    	if(item.bizStatus != res.data.CANCLE){
                                    		if(item.statu == 'unline' || userId==1){
                                    			ordwaterCourseBtnTxt ="流水";
                                    			ordwaterCourseBtn = 'ordwaterCourseBtn';
                                    		}
                                    	}
                                    	//详情
	                                    if(item.orderType != res.data.PHOTO_ORDER){
	                                    	staDetailBtnTxt="详情";
	                                    	ordDetailBtn = 'ordDetailBtn';
	                                    }
	                                    if(item.orderType == res.data.PHOTO_ORDER){
	                                    	staDetailBtnTxt="详情";
	                                    	ordDetailBtn = 'ordDetailBtn';
	                                    }
	                                    //修改、删除
	                                    if(item.bizStatus != res.data.CANCLE){
	                                    	if(_this.staOrdeditFlag==true){
	                                    		if(item.orderType != res.data.PHOTO_ORDER && (item.bizStatus < res.data.SUPPLYING || userId==1)){
	                                    			staAmendTxt ="修改";
	                                    			ordAmendBtn = 'ordAmendBtn';
	                                    		}
	                                    		if(item.orderType == res.data.PHOTO_ORDER && (item.bizStatus < res.data.SUPPLYING || userId==1)){
	                                    			staAmendTxt ="修改";
	                                    			ordAmendBtn = 'ordAmendBtn';
	                                    		}
	                                    		if(userId==1){
	                                    			staDeleteTxt ="删除";
	                                    			ordDeleteBtn = 'ordDeleteBtn';
	                                    		}
	                                    	}
	                                    }
	                                }
	                                //详情另一种情况
									var ordRecoveryBtn = '';
	                                var staRecoveryBtnTxt ="";//h恢复
	                                if(_this.staOrdeditFlag==true){
	                                	if(item.delFlag!=null && item.delFlag == '0'){
	                                		staDetailBtnTxt ="详情";
	                                		ordDetailBtn = 'ordDetailBtn';
	                                		staRecoveryBtnTxt ="恢复";
	                                		ordRecoveryBtn = 'ordRecoveryBtn';
	                                	}
	                                }
                                    
				                }
	                        	var staCheckSucBtn = '';
	                        	var staCheckSuc = '';
									staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row">' +
											'<label>订单编号:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderNum+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>经销店:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.customer.name+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>订单类型:</label>' +
											'<input type="text" class="mui-input-clear orderTypeTxt" disabled="disabled" value=" '+orderTypeTxt+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>审核状态:</label>' +
											'<input type="text" class="mui-input-clear" value=" '+ ProcessName +' " disabled="disabled">' + 
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>创建时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>更新时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
										'</div>' +
										//业务状态需要添加权限 mui-col-xs-2 
//										'<div class="mui-input-row">' +
//											'<label>业务状态:</label>' +
//											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
//										'</div>' +
										'<div class="app_color40 mui-row app_text_center content_part operation " id="foot">' +
											'<div class="'+ ordCheckBtn +'" staOrdId="'+ item.id +'">' +
												 staCheckBtnTxt +
											'</div>'+
											'<div class="'+ordSupplyBtn+'"  staOrdId="'+ item.id +'">' +
												staSupplyBtnTxt +
											'</div>'+
											'<div class="'+ordwaterCourseBtn+'"  staOrdId="'+ item.id +'">' +
												 ordwaterCourseBtnTxt +
											'</div>'+
											'<div class="'+ordAmendBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ item.statu +'" ordsource="'+ item.source +'">' +
												staAmendTxt +
											'</div>'+
											'<div class="'+ordDeleteBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ item.statu +'" ordsource="'+ item.source +'">' +
												staDeleteTxt +
											'</div>'+
											'<div class="'+ordDetailBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ item.statu +'" ordsource="'+ item.source +'">' +
												staDetailBtnTxt +
											'</div>'+
											'<div class="'+ordRecoveryBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ item.statu +'" ordsource="'+ item.source +'">' +
												staRecoveryBtnTxt +
											'</div>'+
										'</div>' +
									'</div>'
								});
								$('#orderList').append(staffHtmlList);
								_this.stOrdHrefHtml()
					}else{
						$('#orderList').append('<p class="noneTxt">暂无数据</p>');
						$('#staOrdSechBtn').hide();
					}
				}
			});
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

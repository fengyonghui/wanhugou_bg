(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.commOrdFlag = "false";
		this.commAuditFlag = "false";
		this.commEditFlag = "false";
		this.commRefundFlag = "false";
		this.commDoRefundFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
			this.getPermissionList1('biz:order:bizOrderHeader:view','commOrdFlag')//true 操作
			this.getPermissionList2('biz:order:bizOrderHeader:audit','commAuditFlag')//true 审核
			this.getPermissionList3('biz:order:bizOrderHeader:edit','commEditFlag')//true  待审核、修改、删除
			this.getPermissionList4('biz:order:bizOrderHeader:refund','commRefundFlag')//true 退款
			this.getPermissionList5('biz:order:bizOrderHeader:doRefund','commDoRefundFlag')//true 线下退款
			
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
//			console.log(_this.commOrdFlag)
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
							//查询页面传过来的值
							if(_this.userInfo.orderNum==undefined){
								_this.userInfo.orderNum="";
							}
							if(_this.userInfo.serllerName==undefined){
								_this.userInfo.serllerName="";
							}
							if(_this.userInfo.serllerPhone==undefined){
								_this.userInfo.serllerPhone="";
							}
							if(_this.userInfo.commissionStatus==undefined){
								_this.userInfo.commissionStatus="";
							}	
							if(_this.userInfo.customerPhone==undefined){
								_this.userInfo.customerPhone="";
							}
							if(_this.userInfo.itemNo==undefined){
								_this.userInfo.itemNo="";
							}
							if(_this.userInfo.customerName==undefined){
								_this.userInfo.customerName="";
							}
							if(_this.userInfo.centersName==undefined){
								_this.userInfo.centersName="";
							}
							if(_this.userInfo.includeTestData==undefined){
								_this.userInfo.includeTestData="";
							}
		            	    var statu = _this.userInfo.statu;	
			                var f = document.getElementById("commList");
			                var childs = f.childNodes;
			                for(var i = childs.length - 1; i >= 0; i--) {
			                    f.removeChild(childs[i]);
			                }			                
			                $('.mui-pull-caption-down').html('');
			                if(_this.userInfo.isFunc){
		                	    //查询过来传的参数
		                    	pager['size']= 20;
		                    	pager['pageNo'] = 1;
		                    	pager['targetPage'] = 'COMMISSION_ORDER';
		                    	pager['orderNum'] = _this.userInfo.orderNum;//订单编号
		                    	pager['serllerName'] = _this.userInfo.serllerName;//零售商名称
		                    	pager['serllerPhone'] = _this.userInfo.serllerPhone;//零售商电话
		                    	pager['commissionStatus'] = _this.userInfo.commissionStatus,//结佣状态
		                    	pager['customer.phone'] =  _this.userInfo.customerPhone,//经销商电话
		                    	pager['itemNo'] = _this.userInfo.itemNo,//商品货号
		                    	pager['customer.id'] = _this.userInfo.customerName,//经销店名称
		                    	pager['centersName'] = _this.userInfo.centersName,//采购中心
		                    	pager['con.name'] = nameTxts,//客户专员
		                    	pager['page.includeTestData'] = _this.userInfo.includeTestData;//测试数据
		                    	getData(pager);
		                    }else{
		                    	//直接进来的参数数据
		                    	pager['size']= 20;
			                    pager['pageNo'] = 1;
			                    pager['targetPage'] = 'COMMISSION_ORDER';
			                    getData(pager);
		                    }				                
			            }
			        }
			    })
		    }
		    function getData(params){
		    	var commHtmlList = '';
		    	var ass=[];
		        mui.ajax("/a/biz/order/bizOrderHeader/listData4mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	console.log(res)
		            	var dataRow = res.data.roleSet;
						var arrLen = res.data.page.list.length;
						if(arrLen <20){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						}
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
//								console.log(item)
								//订单类型
								var orderTypeTxt = '';
				          	    $.ajax({
					                type: "GET",
					                url: "/a/sys/dict/getDictLabel4Mobile",
					                dataType: "json",
					                data: {
					                	value:item.orderType,
					                	type: "biz_order_type",
					                	defaultValue:'未知状态'
					                },
					                async:false,
					                success: function(hz){ 
					                	orderTypeTxt = hz.data.dictLabel;
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
//								if(bizOrderHeader.flag=='check_pending) {
//									if(item.bizStatus >= res.data.SUPPLYING && item.orderType != res.data.PHOTO_ORDER) {
//										
//									}
//									if(item.bizStatus >= res.data.SUPPLYING && item.orderType == res.data.PHOTO_ORDER) {
//									
//									}
//									if(item.bizStatus < res.data.SUPPLYING && item.orderType != res.data.PHOTO_ORDER) {
//									
//									}
//									if(item.bizStatus < res.data.SUPPLYING && item.orderType == res.data.PHOTO_ORDER) {
//									
//									}
//								}
								/*审核*/
								var commCheckBtn = '';
								var commCheckBtnTxt = '';
								/*已结佣*/
//								var alreadyKnotBtn = '';
//								var alreadyKnotBtnTxt = '';
								/*申请结佣*/
								var applyKnotBtn = '';
								var applyKnotBtnTxt = '';
								/*结佣详情*/
								var commDetailBtn = '';
								var commDetailBtnTxt = '';
								/*删除*/
								var commDeleteBtn = '';
								var commDeleteBtnTxt = '';
								/*修改*/
								var commAmendBtn = '';
								var commAmendBtnTxt = '';
								/*待审核、审核成功*/
								var commWaCheckBtn = '';
								var commWaCheckBtnTxt = '';
								/*支付流水*/
								var waterCouBtn = '';
								var waterCouBtnTxt = '';
								/*恢复*/
								var commRecoverBtn = '';
								var commRecoverBtnTxt = '';
								
								/*同意退款*/
								/*驳回*/
								/*线下退款*/
								/*退款状态*/
								
								if(_this.commOrdFlag == true) {
									if(item.bizStatus != res.data.CANCLE) {
										var DataRoleGener = '';
										var fileRoleData = '';
										var commName = '';
										var commCode = '';
										if(item.commonProcess.doOrderHeaderProcessFifth) {
											DataRoleGener = item.commonProcess.doOrderHeaderProcessFifth.roleEnNameEnum;
											fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
											commName = item.commonProcess.doOrderHeaderProcessFifth.name;
											commCode = item.commonProcess.doOrderHeaderProcessFifth.code;
										}
										var OriginDataRole = '';
										var OriginRoleData = '';
										var commOriginName = '';
										var commOriginCode = '';
		                            	if(item.commonProcess.jointOperationOriginProcess) {
		                            		OriginDataRole = item.commonProcess.jointOperationOriginProcess.roleEnNameEnum;
											OriginRoleData = dataRow.filter(v => DataRoleGener.includes(v));
											commOriginName = item.commonProcess.jointOperationOriginProcess.name;
											commOriginCode = item.commonProcess.jointOperationOriginProcess.code;
		                            	}
		                            	var LocalDataRole = '';
										var LocalRoleData = '';
										var commLocalName = '';
										var commLocalCode = '';
			                        	if(item.commonProcess) {
			                        		var purchName ='';
			                        		if(item.commonProcess.purchaseOrderProcess) {
			                        			purchName = item.commonProcess.purchaseOrderProcess.name;
			                        		}
			                        		if(item.commonProcess.jointOperationLocalProcess) {
			                        			LocalDataRole = item.commonProcess.jointOperationLocalProcess.roleEnNameEnum;
												LocalRoleData = dataRow.filter(v => DataRoleGener.includes(v));
			                        		}
			                        	}
				                        if(item.orderType == res.data.PURCHASE_ORDER && item.bizStatus >= res.data.SUPPLYING) {
				                            if(_this.commAuditFlag == true) {
				                            	if(fileRoleData.length != 0 || userId==1 && commName != '驳回'&& commCode != 'auditFithStatus') {
				                                    commCheckBtn = 'commCheckBtn';
													commCheckBtnTxt = '审核';
				                                }
				                            	if(OriginRoleData.length != 0 && commOriginName != '驳回' && commOriginCode != 'auditStatus'
												&& item.orderType == res.data.ORDINARY_ORDER) {
				                                    commCheckBtn = 'commCheckBtn';
													commCheckBtnTxt = '审核';
				                                }
				                            }
				                        }
				                        if(_this.commAuditFlag == true) {
				                        	if(item.commonProcess && item.commonProcess.id && purchName != '驳回'&& purchName != '审批完成'
											&& (LocalRoleData.length != 0 || OriginRoleData.length != 0 || userId==1)) {
												if((item.orderType == res.data.ORDINARY_ORDER || item.orderType == res.data.COMMISSION_ORDER)
												&& item.bizStatus >= res.data.SUPPLYING) {
													if(item.suplys == 0) {
														commCheckBtn = 'commCheckBtn';
														commCheckBtnTxt = '审核';
													}
													if(item.suplys != 0) {
														commCheckBtn = 'commCheckBtn';
														commCheckBtnTxt = '审核';
													}
												}
											}
										}
										if(item.delFlag!=null && item.delFlag == '1') {
//											需要加个参数res.data.bizOrderHeader.flag
//									    	if(bizOrderHeader.flag=='check_pending') {
									    		if(_this.commEditFlag == true) {
									    			if(item.bizStatus != res.data.CANCLE) {
									    				if(item.orderType != res.data.PHOTO_ORDER) {
//									                    	<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}&source=${source}">
									                    }
									    				if(item.orderType == res.data.PHOTO_ORDER) {
//									                    	<a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}&source=${source}">
									                    }
									                    if(item.bizStatus==0 || item.bizStatus==5 || item.bizStatus==10) {
									                        commWaCheckBtn = 'commWaCheckBtn';
															commWaCheckBtnTxt = '待审核';
									                    	if(item.orderType != res.data.PHOTO_ORDER) {
									                    		commAmendBtn = 'commAmendBtn';
									                    		commAmendBtnTxt = '修改';
									                        }
									                    }
									                    if(item.bizStatus==res.data.UNAPPROVE) {
									                        commWaCheckBtnTxt = '审核失败';
									                        if(item.orderType != res.data.PHOTO_ORDER && userId==1) {
									                            commAmendBtn = 'commAmendBtn';
									                    		commAmendBtnTxt = '修改';
									                        }
									                    }
//									                </a>
									                	if(item.bizStatus==res.data.SUPPLYING) {
									//                      <c:choose>
									//							if(userId==1) {
									//	                        <a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}&source=${source}">
									//	                            审核成功</a>
									//	                        </c:when>
									//	                        <c:otherwise>审核成功</c:otherwise>
									//                      </c:choose>
																if(userId==1) {
										                        	commWaCheckBtnTxt = '审核成功';
										                        }else {
										                        	commWaCheckBtnTxt = '审核成功';
										                        }
									                    }
									                }
									    			if(item.orderType != res.data.PHOTO_ORDER) {
									    				commDetailBtn = 'commDetailBtn';
									    				commDetailBtnTxt = '详情';
									                }
									    			if(item.orderType == res.data.PHOTO_ORDER) {
									                	commDetailBtn = 'commDetailBtn';
									    				commDetailBtnTxt = '详情';
									                }
									            }
//									 		}else {
												if(item.bizStatus != res.data.CANCLE) {
													if(res.data.statu == 'unline' || userId==1) {
														waterCouBtn = 'waterCouBtn';
														waterCouBtnTxt = '支付流水';
									                }
									            }
												if(item.orderType != res.data.PHOTO_ORDER) {
									//				pc端这里写的是查看详情,但是路径和参数都一样
													commDetailBtn = 'commDetailBtn';
													commDetailBtnTxt = '详情';
									            }
												if(item.orderType == res.data.PHOTO_ORDER) {
									            	commDetailBtn = 'commDetailBtn';
													commDetailBtnTxt = '详情';
									            }
												if(item.bizStatus != res.data.CANCLE) {
													if(_this.commEditFlag == true) {
														if(item.orderType != res.data.PHOTO_ORDER && (item.bizStatus < res.data.SUPPLYING || userId==1)) {
									                    	commAmendBtn = 'commAmendBtn';
									                		commAmendBtnTxt = '修改';
									                    }
														if(item.orderType == res.data.PHOTO_ORDER && (item.bizStatus < res.data.SUPPLYING || userId==1)) {
									                    	commAmendBtn = 'commAmendBtn';
									                		commAmendBtnTxt = '修改';
									                    }
														if(userId==1) {
										                    commDeleteBtn = 'commDeleteBtn';
															commDeleteBtnTxt = '删除';
									                    }
									                }
													if(_this.commRefundFlag == true) {
									                    /*退款增加*/
									                    if(item.drawBack.drawbackStatus==res.data.REFUND) {
//										                    <a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details&statu=${statu}&drawbackStatus=${OrderHeaderDrawBackStatusEnum.REFUND.state}&refundSkip=refundSkip">同意退款</a>
//										                    <a href="javascript:checkInfo('${OrderHeaderDrawBackStatusEnum.REFUNDREJECT.state}','退款驳回','${orderHeader.id}')">驳回</a>
									                    }
									                    if(item.drawBack.drawbackStatus==res.data.REFUNDING) {
									                    	/*退款中*/
									                    }
									                    if(item.drawBack.drawbackStatus==res.data.REFUNDREJECT) {
									                    	/*退款驳回*/
									                    }
									                }
													if(_this.commDoRefundFlag == true) {
														if(item.drawBack.drawbackStatus==res.data.REFUNDING) {
//									                    	<a href="${ctx}/biz/order/bizOrderHeader/refund?id=${orderHeader.id}&drawbackStatus=${OrderHeaderDrawBackStatusEnum.REFUNDED.state}">线下退款</a>
									                    }
									                }
													if(_this.commOrdFlag == true) {
														if(item.drawBack.drawbackStatus==res.data.REFUNDED) {
									                   		 /*退款完成*/
									                    }
									                }
									            }
//											}
											if(_this.commEditFlag == true) {
												if(item.delFlag!=null && item.delFlag == '0') {
												commDetailBtn = 'commDetailBtn';
												commDetailBtnTxt = '详情';
												commRecoverBtn = 'commRecoverBtn';
												commRecoverBtnTxt = '恢复';
												}
											}
										}
									}
								}
							commHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group" id="rodiv_' + item.orderType + '">'+
								'<div class="mui-input-row">' +
									'<label>订单编号:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.orderNum+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>订单类型:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+orderTypeTxt+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>经销店:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.customer.name+' ">' +
								'</div>' +
//								'<div class="mui-input-row">' +
//									'<label>佣金:</label>' +
//									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.id +' ">' + 
//								'</div>' +
								'<div class="mui-input-row">' +
									'<label>创建时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>更新时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
								'</div>' +
								'<div class="app_color40 mui-input-row app_text_center content_part operation " id="foot">' +
//					/*已结佣*/		'<div class="'+ alreadyKnotBtn +'" staOrdId="'+ item.id +'">' +
//										alreadyKnotBtnTxt +
//									'</div>'+
					/*审核*/			'<div class="'+commCheckBtn+'" staOrdId="'+ item.id +'">' +
										commCheckBtnTxt +
									'</div>'+
					/*详情*/			'<div class="'+commDetailBtn+'" staOrdId="'+ item.id +'">' +
										commDetailBtnTxt +
									'</div>'+
			/*待审核、审核成功*/		'<div class="'+commWaCheckBtn+'" staOrdId="'+ item.id +'">' +
										commWaCheckBtnTxt +
									'</div>'+									
					/*申请结佣*/		'<div class="'+applyKnotBtn+'" staOrdId="'+ item.id +'">' +
										applyKnotBtnTxt +
									'</div>'+
								'</div>'+	
								'<div class="app_color40 mui-row app_text_center content_part operation " id="foot">' +
					/*删除*/			'<div class="'+commDeleteBtn+'" staOrdId="'+ item.id +'">' +
										commDeleteBtnTxt +
									'</div>'+
					/*恢复*/			'<div class="'+commRecoverBtn+'" staOrdId="'+ item.id +'">' +
										commRecoverBtnTxt +
									'</div>'+
					/*修改*/			'<div class="'+commAmendBtn+'" staOrdId="'+ item.id +'">' +
										commAmendBtnTxt +
									'</div>'+	
					/*支付流水*/		'<div class="'+waterCouBtn+'" staOrdId="'+ item.id +'">' +
										waterCouBtnTxt +
									'</div>'+									
								'</div>' +
							'</div>'

								});
								$('#commList').append(commHtmlList);
//								_this.stOrdHrefHtml();
								//先隐藏Ro订单
//								var RoList=$('#refreshContainer div[id^=rodiv_8]');
//								$(RoList).hide();
								
					}else{
						$('.mui-pull-bottom-pocket').html('');
						$('#orderList').append('<p class="noneTxt">暂无数据</p>');
						$('#OrdSechBtn').hide();
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
		    _this.ordHrefHtml();
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
                    _this.commOrdFlag = res.data;
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
                    _this.commAuditFlag = res.data;
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
                    _this.commEditFlag = res.data;
                }
            });
        },
        getPermissionList4: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.commRefundFlag = res.data;
                }
            });
        },
        getPermissionList5: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.commDoRefundFlag = res.data;
                }
            });
        },
        ordHrefHtml: function() {
        	var _this = this;
        	/*查询*/
        	var myStatu = $('#myStatu').val();
			$('.app_header').on('tap', '#OrdSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderListSeach.html",
						extras:{
							statu: myStatu,
						}
					})
				}
			});
		/*首页*/
			$('#nav').on('tap','.staHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			})
        },
		stOrdHrefHtml: function() {
			var _this = this;
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
		}		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

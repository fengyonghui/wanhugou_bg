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
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
//			console.log(_this.staOrdFlag)
//			console.log(_this.staOrdauditFlag)
//			console.log(_this.staOrdsupplyFlag)
//			console.log(_this.staOrdeditFlag)
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
			            	var nameTxt = '';
							if(_this.userInfo.selectAuditStatus) {
								nameTxt = decodeURIComponent(_this.userInfo.selectAuditStatus)
							}else {
								nameTxt = ''
							}
							var nameTxts = '';
							if(_this.userInfo.conName) {
								nameTxts = decodeURIComponent(_this.userInfo.conName)
							}else {
								nameTxts = ''
							}
							//查询页面传过来的值
							if(_this.userInfo.orderNum==undefined){
								_this.userInfo.orderNum="";
							}
							if(_this.userInfo.bizStatus==undefined){
								_this.userInfo.bizStatus="";
							}
							if(nameTxt==undefined){
								nameTxt="";
							}
							if(nameTxts==undefined){
								nameTxts="";
							}							
							if(_this.userInfo.retainage==undefined){
								_this.userInfo.retainage="";
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
							if(_this.userInfo.conName==undefined){
								_this.userInfo.conName="";
							}
							if(_this.userInfo.mobileAuditStatus==undefined){
								_this.userInfo.mobileAuditStatus="";
							}
							if(_this.userInfo.waitShipments==undefined){
								_this.userInfo.waitShipments="";
							}
							if(_this.userInfo.waitOutput==undefined){
								_this.userInfo.waitOutput="";
							}
							if(_this.userInfo.includeTestData==undefined){
								_this.userInfo.includeTestData="";
							}
		            	    var statu = _this.userInfo.statu;	
		            	    console.log(statu)
			                var f = document.getElementById("orderList");
			                var childs = f.childNodes;
			                for(var i = childs.length - 1; i >= 0; i--) {
			                    f.removeChild(childs[i]);
			                }			                
			                $('.mui-pull-caption-down').html('');
			                if(_this.userInfo.isFunc){
		                	    //查询过来传的参数
		                    	pager['size']= 20;
		                    	pager['pageNo'] = 1;
		                    	pager['statu'] = statu;
		                    	pager['orderNum'] = _this.userInfo.orderNum;//订单编号
		                    	pager['bizStatus'] = _this.userInfo.bizStatus;//订单状态
		                    	pager['selectAuditStatus'] = nameTxt;//审核状态
		                    	pager['retainage'] = _this.userInfo.retainage,//尾款
		                    	pager['customer.phone'] =  _this.userInfo.customerPhone,//经销商电话
		                    	pager['itemNo'] = _this.userInfo.itemNo,//商品货号
		                    	pager['customer.id'] = _this.userInfo.customerName,//经销店名称
		                    	pager['centersName'] = _this.userInfo.centersName,//采购中心
		                    	pager['con.name'] = nameTxts,//客户专员
		                    	pager['mobileAuditStatus'] = _this.userInfo.mobileAuditStatus,//待同意发货
		                    	pager['waitShipments'] = _this.userInfo.waitShipments,//待发货
								pager['waitOutput'] = _this.userInfo.waitOutput,//待出库
		                    	pager['includeTestData'] = _this.userInfo.includeTestData;//测试数据
		                    	getData(pager);
		                    }else{
		                    	//直接进来的参数数据
		                    	pager['size']= 20;
			                    pager['pageNo'] = 1;			                    
			                    if(statu == '' || statu == undefined) {
			                    	pager['statu'] = '';
			                    	$('#myStatu').val('');
			                    }
			                    if(statu == statu) {
			                    	pager['statu'] = statu;
			                    	$('#myStatu').val(statu);
			                    }
			                    getData(pager);
		                    }				                
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
						if(arrLen <20){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						}
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
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
                                         	if(_this.staOrdauditFlag == true){
												var DataRoleGener = '';
												var fileRoleData = '';
												var doOrderHeaderProcessFifthName = '';
												var doOrderHeaderProcessFifthCode = '';
												if(commonProcess.doOrderHeaderProcessFifth) {
													doOrderHeaderProcessFifthName = commonProcess.doOrderHeaderProcessFifth.name;
													doOrderHeaderProcessFifthCode = commonProcess.doOrderHeaderProcessFifth.code
													DataRoleGener = commonProcess.doOrderHeaderProcessFifth.roleEnNameEnum;
													fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
												}
												if((fileRoleData.length != 0 || userId==1) && doOrderHeaderProcessFifthName != '驳回'
                                         			&& doOrderHeaderProcessFifthCode != res.data.auditFithStatus){
                                         			staCheckBtnTxt="审核";
                                         			ordCheckBtn = 'ordCheckBtn';
                                         		}
												var DataRoleGeners = '';
												var fileRoleDatas = '';
												var jointOperationOriginName = '';
												var jointOperationOriginCode = '';
												if(commonProcess.jointOperationOriginProcess) {
													jointOperationOriginName = commonProcess.jointOperationOriginProcess.name;
													jointOperationOriginCode = commonProcess.jointOperationOriginProcess.code;
													DataRoleGeners = commonProcess.jointOperationOriginProcess.roleEnNameEnum;
													fileRoleDatas = dataRow.filter(v => DataRoleGeners.includes(v));
												}
												var auditStatu = '';
					                            $.each(res.data.auditStatus,function(q,s){
						                        	auditStatu=s
					                            })
                                         		if(fileRoleDatas.length != 0 && jointOperationOriginName != '驳回'
                                         		&& jointOperationOriginCode != auditStatu
                                         		&& item.orderType == res.data.ORDINARY_ORDER){
                                         			staCheckBtnTxt="审核";
													ordCheckBtn = 'ordCheckBtn';
                                         		}
                                         	}
                                        }
                                        if(_this.staOrdauditFlag == true){
                                            var DataRole = '';
                                            var fileRole = '';
											if(commonProcess.jointOperationLocalProcess) {
												DataRole = commonProcess.jointOperationLocalProcess.roleEnNameEnum;
												fileRole = dataRow.filter(v => DataRole.includes(v));
											}
											var DataRoles = '';
											var fileRoles = '';
											if(commonProcess.jointOperationOriginProcess) {
												DataRoles = commonProcess.jointOperationOriginProcess.roleEnNameEnum;
												fileRoles = dataRow.filter(v => DataRoles.includes(v));
											}
											var purchaseOrdName = '';
											if(commonProcess.purchaseOrderProcess) {
												purchaseOrdName = commonProcess.purchaseOrderProcess.name;
											}
											if((commonProcess != null || commonProcess != '')
												&& (commonProcess.id != null || commonProcess.id != '')
                                            	&& purchaseOrdName != '驳回'
                                            	&& purchaseOrdName != '审批完成'
                                            	&& (fileRole.length !=0 || fileRoles.length !=0 || userId==1)){
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
//                                      if(_this.staOrdsupplyFlag == true){
//                                      	if(item.bizStatus >= res.data.SUPPLYING && item.bizStatus <= res.data.STOCKING && item.suplys != 0 && item.suplys != 721){
//                                      		if(item.bizInvoiceList.length <= 0){
//                                      			staSupplyBtnTxt="出库";
//                                      			ordSupplyBtn = 'ordSupplyBtn';
//                                      		}
//                                      	}
//                                      }
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
	                                	if((item.delFlag!=null || item.delFlag!='') && item.delFlag == '0'){
	                                		staDetailBtnTxt ="详情";
	                                		ordDetailBtn = 'ordDetailBtn';
	                                		staRecoveryBtnTxt ="恢复";
	                                		ordRecoveryBtn = 'ordRecoveryBtn';
	                                	}
	                                }
                                    
				                }
	                        	var staCheckSucBtn = '';
	                        	var staCheckSuc = '';

									staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group" id="rodiv_' + item.orderType + '">'+
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
											'<div class="'+ordwaterCourseBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'">' +
												 ordwaterCourseBtnTxt +
											'</div>'+
											'<div class="'+ordAmendBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
												staAmendTxt +
											'</div>'+
											'<div class="'+ordDeleteBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
												staDeleteTxt +
											'</div>'+
											'<div class="'+ordDetailBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
												staDetailBtnTxt +
											'</div>'+
											'<div class="'+ordRecoveryBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
												staRecoveryBtnTxt +
											'</div>'+
										'</div>' +
									'</div>'

								});
								$('#orderList').append(staffHtmlList);
								_this.stOrdHrefHtml();
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
                    _this.staOrdeditFlag = res.data;
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
		 /*审核*/
	       $('.content_part').on('tap', '.ordCheckBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');//订单 ID
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
				var statu=$(this).attr('ordstatu');		
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
                    GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordwaterCourseList.html",
						extras: {
							staOrdId: staOrdId,
							statu: statu,	
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

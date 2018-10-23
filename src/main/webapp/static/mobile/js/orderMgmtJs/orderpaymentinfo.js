(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.OrdFlaginfo = "false";
		this.OrdFlagaudit = "false";
		this.OrdFlagpay = "false";
		this.OrdFlagstartAudit = "false";
		this.OrdFlagScheduling = "false";
		this.creatPayFlag = "false";
		this.ordCreatPayFlag = "false";
		this.poCreatPayFlag = "false";
		this.orCancAmenFlag = "false";
		this.affirmSchedulingFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
//			biz:order:bizOrderHeader:view  		操作
			this.getPermissionList('biz:po:bizPoHeader:view','OrdFlaginfo');//操作总权限
			this.getPermissionList1('biz:po:bizPoHeader:audit','OrdFlagaudit');
			this.getPermissionList2('biz:po:pay:list','OrdFlagpay');
			this.getPermissionList3('biz:po:bizPoHeader:startAuditAfterReject','OrdFlagstartAudit');
			this.getPermissionList4('biz:po:bizPoHeader:addScheduling','OrdFlagScheduling');
			this.getPermissionList5('biz:request:bizRequestHeader:createPayOrder','creatPayFlag')//申请付款re
			this.getPermissionList6('biz:request:bizOrderHeader:createPayOrder','ordCreatPayFlag')//申请付款or
			this.getPermissionList7('biz:po:bizPoHeader:createPayOrder','poCreatPayFlag')//申请付款po
			this.getPermissionList8('biz:po:bizPoHeader:edit','orCancAmenFlag')//修改、取消
			this.getPermissionList9('biz:po:bizPoHeader:confirmScheduling','affirmSchedulingFlag')

			if(this.userInfo.isFunc){
				this.seachFunc()
			}else{
				
				this.pageInit(); //页面初始化
				
			}
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
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
                	_this.OrdFlaginfo = res.data;
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
                    _this.OrdFlagaudit = res.data;
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
					_this.OrdFlagpay = res.data;
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
                    _this.OrdFlagstartAudit = res.data;
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
                    _this.OrdFlagScheduling = res.data;
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
                    _this.creatPayFlag = res.data;
                }
            });
        },
        getPermissionList6: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.ordCreatPayFlag = res.data;
                }
            });
        },
        getPermissionList7: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.poCreatPayFlag = res.data;
                }
            });
        },
        getPermissionList8: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.orCancAmenFlag = res.data;
                }
            });
        },
        getPermissionList9: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.affirmSchedulingFlag = res.data;
                }
            });
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
			                getData(pager);
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
				                var f = document.getElementById("orderinfoList");
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
		    	var orderHtmlList = '';
		    	var ass=[];
		    	var postatus=[];
		        mui.ajax("/a/biz/po/bizPoHeader/listV2Data4Mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
//		            	console.log (res)
		            	var dataRow = res.data.roleSet;
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
                        //订单支出状态                        
                        $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "biz_po_status"},
			                async:false,
			                success: function(res){
				                postatus=res;
			                }
			            });	
						var arrLen = res.data.page.list.length;
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true)
						}
                        var that=this;
//                      console.log(_this.OrdFlaginfo)
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
//                          	console.log(item)
								//订单/备货单号								
                                var poNumTxt="";  
                                var itemId="";
	                        	if(item.bizOrderHeader){
	                        		poNumTxt=item.bizOrderHeader.orderNum;
	                        		itemId=item.bizOrderHeader.id;
	                        	}
	                        	if(item.bizRequestHeader){
	                        		poNumTxt=item.bizRequestHeader.reqNo;
	                        		itemId=item.bizRequestHeader.id;
	                        		
	                        	}
	                        	//排产状态
	                            var poSchTypeTxt = '';
	                            if(item.poSchType == 0 || item.poSchType == null){
	                            	poSchTypeTxt = res.data.SCHEDULING_NOT;
	                            }else{
	                            	if(item.poSchType == 1){
	                            		poSchTypeTxt = res.data.SCHEDULING_PLAN;
	                            	}
	                            	if(item.poSchType == 2){
	                            		poSchTypeTxt = res.data.SCHEDULING_DONE;
	                            	}
	                            }
	                            //订单支出状态
	                            var postatusTxt = '';
	                            $.each(postatus,function(i,items){
		                        	if(item.bizStatus==items.value) {
		                        		postatusTxt = items.label
		                        	}
	                            })
	                            //订单支出审核状态
	                            var paycheckTxt = '';
	                            if(item.commonProcess){
	                            	if(item.commonProcess.purchaseOrderProcess.name){
	                            		paycheckTxt=item.commonProcess.purchaseOrderProcess.name;
	                            	}
	                            }else{
	                            	paycheckTxt='当前无审批流程';
	                            }
					            if(_this.OrdFlaginfo == true) {
					            	if(item.bizStatus != 10) {
					            		//申请付款
										var creatPayBt = '';
										var creatPay = '';
										if(item.bizOrderHeader != null || item.bizOrderHeader != '' 
										|| item.bizRequestHeader != null || item.bizRequestHeader != '') {
											if(_this.ordCreatPayFlag == true) {
												if(item.bizOrderHeader != null || item.bizRequestHeader != null){
													if(_this.ordCreatPayFlag == true) {
							                        	if(item.bizOrderHeader != null || item.bizOrderHeader != '') {
							                        		if((item.currentPaymentId == null || item.currentPaymentId == '') 
							                        		&& item.commonProcess.purchaseOrderProcess.name == '审批完成'
							                        		&& ((item.payTotal == null || item.payTotal == '') ? 0 : item.payTotal) < item.bizOrderHeader.totalDetail) {
//							                        			console.log('订单申请付款')
							                        			creatPay = '申请付款'
																creatPayBt = 'creatPayBtn'
							                        		}
							                        	}
							                        }
													if(_this.creatPayFlag == true) {
														if(item.bizRequestHeader != null || item.bizRequestHeader != '') {
															if((item.currentPaymentId == null || item.currentPaymentId == '') 
															&& item.bizRequestHeader.bizStatus >= 5 
															&& item.bizRequestHeader.bizStatus < 37 
															&& ((item.bizRequestHeader.bizPoHeader.payTotal == null || item.bizRequestHeader.bizPoHeader.payTotal == '') ? 0 : item.payTotal) < item.bizRequestHeader.totalDetail) {
//																console.log('备货单申请付款')
																creatPay = '申请付款'
																creatPayBt = 'creatPayBtn'
															}
														}
													}
												}else {
													if(_this.poCreatPayFlag == true) {
														var values = item.bizStatus;
														var dictLabel = '';
														$.ajax({
											                type: "GET",
											                url: "/a/sys/dict/getDictLabel4Mobile",
											                dataType: "json",
											                data: {
											                	value:values,
											                	type: "biz_po_status",
											                	defaultValue: '未知类型'
										                	},
											                async:false,
											                success: function(res){
//												                console.log(res.data.dictLabel)
												                dictLabel = res.data.dictLabel;
											                }
											            });
														if(item.bizPoPaymentOrder.id == null
														&& item.commonProcess.purchaseOrderProcess.name == '审批完成'
														&& dictLabel != '全部支付'
														&& item.payTotal < (item.totalDetail+item.totalExp)) {
															creatPay = '申请付款'
															creatPayBt = 'creatPayBtn'
														}
													}
												}
											}
										}
										//审核
					                	var staCheckBtn = '';
					                	var staCheckbtns = '';
					                	var staCheckBtnTxt = '';
					                	var staCheckBtns = '';
						                if(_this.OrdFlagaudit == true) {   
											var DataRoleGener = '';
											if(item.commonProcess) {
												DataRoleGener = item.commonProcess.purchaseOrderProcess.roleEnNameEnum;
											}
											var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
					                        if(item.commonProcess.id != null
					                        	&& item.commonProcess.purchaseOrderProcess.name != '驳回'
					                        	&& item.commonProcess.purchaseOrderProcess.name != '审批完成'
					                        	&& (fileRoleData.length>0 || userId==1))             {
					                        	if(item.bizOrderHeader != null || item.bizRequestHeader != null){
					                        		//订单审核
					                        	   	if(item.bizOrderHeader != ""){
					                        	   		staCheckBtnTxt = '审核';
					                        	   		staCheckBtns = 'staCheckBtns';
					                        	   		staCheckbtns = item.bizOrderHeader.id;
					                        	   	}
					                        	   	//备货单审核
					                        	   	if(item.bizRequestHeader != ""){
					                        	   		staCheckBtnTxt = '审核';
					                        	   		staCheckBtns = 'staCheckBtns';
					                        	   		staCheckBtn = item.bizRequestHeader.id;
					                        	   	}
					                        	}else{
					                        		//采购单审核
					                        		staCheckBtnTxt = '审核';
					                        		staCheckBtns = 'staCheckBtns';
					                        		staCheckBtn = item.id;
					                        	}
					                        }
						                }
					                	//支付申请列表
					                	var staPayBtnTxt = '';
					                	var staPayBtnes = '';
					                	if(item.commonProcess.type != -1){
					                		if(item.bizOrderHeader != null){
					                			//订单
					                			if(_this.OrdFlagpay == true){
					                				staPayBtnTxt = '支付列表';
					                				staPayBtnes = 'staPayBtnes';
					                			}
					                		}
					                		if(item.bizRequestHeader != null){
					                			//备货单
					                			if(_this.OrdFlagpay == true){
					                				staPayBtnTxt = '支付列表';
					                				staPayBtnes = 'staPayBtnes';
					                			}
					                		}
					                	}
										//开启审核
					                	var stastartCheckBtn = '';
					                	var stastartCheckBtnTxt = '';
					                	if(_this.OrdFlagstartAudit==true){
					                		if(item.commonProcess.type == -1){
					                			if(item.bizOrderHeader != null) {
					                				stastartCheckBtnTxt = '开启审核';
					                				stastartCheckBtn = 'stastartCheckBtn'
					                			}
					                			if(item.bizRequestHeader != null){
					                				stastartCheckBtnTxt = '开启审核';
					                				stastartCheckBtn = 'stastartCheckBtn'
					                			}
					                		}
					                	}
										//排产
					                	var SchedulingBtnTxt = '';
					                	var SchedulingBtn = '';
					                	if(_this.OrdFlagScheduling==true){
					                		SchedulingBtnTxt = '排产';
					                		SchedulingBtn = 'SchedulingBtn';
					                	}
					                	//修改
					                	var orAmendBtn = '';
					                	var orAmendBtnTxt = '';
					                	//取消
										var orCancleBtn = '';
										var orCancleBtnTxt = '';
					                	if(_this.orCancAmenFlag == true) {
					                		if(item.commonProcess.purchaseOrderProcess.name == null
				                			|| item.commonProcess.purchaseOrderProcess.name == '驳回') {
					                			orAmendBtn = 'orAmendBtn';
					                			orAmendBtnTxt = '修改';
					                		}
				                			orCancleBtn = 'orCancleBtn';
											orCancleBtnTxt = '取消';
					                	}
					            		//详情
										var orDetailBtn = '';
										var orDetailBtnTxt = '';
					                	if(_this.OrdFlaginfo == true) {
											var orDetailBtn = 'orDetailBtn';
											var orDetailBtnTxt = '详情';
					                	}
					                	//确认排产
										var affirmSchedulingBtn = '';
										var affirmSchedulingBtnTxt = '';
					                	if(_this.affirmSchedulingFlag == true) {
					                		affirmSchedulingBtn = 'affirmSchedulingBtn';
											affirmSchedulingBtnTxt = '确认排产';
					                	}
					            	}
					            }
	                        	//隐藏信息
				                var staCheckSucBtn = "";
			                	var staCheckSuc = "";
			                	var staCheckSuc = item.bizOrderHeader.id;
			                	var staCheck = item.bizRequestHeader.id;
								orderHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row" style="display:none">' +
										    '<label>备货单号:</label>' +
											'<input type="text" class="mui-input-clear staPayBtns" disabled="disabled" podid="'+ staCheckSuc +'" podids="'+ staCheck +'">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>订单/备货单号:</label>' +
											'<input type="text" class="mui-input-clear dd" disabled="disabled" value=" '+ poNumTxt+' " id="poNum_' + itemId + '">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>供应商:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.vendOffice.name +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>采购总价:</label>' +
											'<input type="text" class="mui-input-clear orderTypeTxt" disabled="disabled" value=" '+ item.totalDetail.toFixed(1)+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>支出状态:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+postatusTxt +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>审核状态:</label>' +
											'<input type="text" class="mui-input-clear" id="paycheckSta" disabled="disabled" value=" '+ paycheckTxt +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>排产状态:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ poSchTypeTxt +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>创建时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ _this.formatDateTime(item.createDate)+' ">' +
										'</div>' +
										'<div class="mui-input-row app_color40 app_text_center content_part operation " id="foot">' +
							/*kong*/		'<div name="commBtn" class="" staordid="'+ item.id +'">' +
												'<div class="" ></div></div>' +
							/*修改*/			'<div name="commBtn" class="'+orAmendBtn+'" staOrdId="'+ item.id +'">' +
												'<div class="" >'+orAmendBtnTxt+'</div></div>' +
							/*审核*/			'<div name="commBtn" class="'+staCheckBtns+'" staordid="'+ staCheckBtn +'" staordids="'+ staCheckbtns +'">' +
												'<div class="" >'+staCheckBtnTxt+'</div></div>' +
							/*申请付款*/		'<div name="commBtn" class="'+creatPayBt+'" paymentId="'+item.bizRequestHeader.id+'" payOrderId="'+item.bizOrderHeader.id+'">' +
												'<div class="" >'+creatPay+'</div></div>' +
							/*开启审核*/		'<div name="commBtn" class="'+stastartCheckBtn+'" paymentId="'+item.bizRequestHeader.id+'" payOrderId="'+item.bizOrderHeader.id+'" statu="'+item.bizOrderHeader.statu+'" source="'+item.bizOrderHeader.source+'">' +
												'<div class="" >'+stastartCheckBtnTxt+'</div></div>' +
										'</div>' +
										'<div class="app_color40 mui-row app_text_center content_part operation " id="foot">' +
							/*取消*/			'<div name="commBtn" class="'+orCancleBtn+'" staordid="'+ item.id +'">' +
												'<div class="" >'+orCancleBtnTxt+'</div></div>' +				
							/*详情*/			'<div name="commBtn" class="'+orDetailBtn+'" staOrdId="'+ item.id +'">' +
												'<div class="" >'+orDetailBtnTxt+'</div></div>' +
							/*排产*/			'<div name="commBtn" class="'+SchedulingBtn+'" staordid="'+ item.id +'">' +
												'<div class="" >'+SchedulingBtnTxt+'</div></div>' +
							/*支付列表*/		'<div name="commBtn" class="'+staPayBtnes+'" staordid="'+ item.id +'" staOrderId="'+ item.bizOrderHeader.id +'" staInvenId="'+ item.bizRequestHeader.id +'">' +
												'<div class="" >'+staPayBtnTxt+'</div></div>' +														
							/*确认排产*/		'<div name="commBtn" class="'+affirmSchedulingBtn+'" staordid="'+ item.id +'">' +
												'<div class="" >'+affirmSchedulingBtnTxt+'</div></div>' +
										'</div>' +
									'</div>'
							});
							$('#orderinfoList').append(orderHtmlList);
							_this.stOrdHrefHtml();
								//先隐藏订单信息
//								var pos=$(".ctn_show_row .dd");
//								var posd=$(".ctn_show_row .staPayBtns");
//								$.each(pos,function(n,v){
//	                            	var poNumid=$(this).attr('id').substr(6);
//	                            	$.each(posd,function(n,v){
//	                            		var that=this;
//                                      var y=$(that).attr('podid');
//		                            	var divs=$("#poNum_"+poNumid);
//		                            	if(poNumid==y){
//		                            		divs.parent().parent().hide()
//		                            	}
//		                            })
//	                            })
					    }else {
							$('.mui-pull-bottom-pocket').html('');
							$('#orderinfoList').append('<p class="noneTxt">暂无数据</p>');
							$('#staOrdSechBtn').hide();
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
		            }
		        })
		    }
	    },
        stOrdHrefHtml: function() {
            var _this = this;
            /*取消*/
            $('.content_part').on('tap','.orCancleBtn',function(){
                var url = $(this).attr('url');
                var staordid = $(this).attr('staordid');
                if(url) {
                    mui.toast('子菜单不存在')
                }else if(staordid==staordid) {
                    var btnArray = ['取消', '确定'];
                    mui.confirm('您确认要取消吗？', '系统提示！', btnArray, function(choice) {
                        if(choice.index == 1) {
                            $.ajax({
                                type: "GET",
                                url: "/a/biz/po/bizPoHeader/cancel4Mobile",
                                data: {id:staordid},
                                dataType: "json",
                                success: function(res){
                                    mui.toast('操作成功！')
                                    GHUTILS.OPENPAGE({
                                        url: "../../html/orderMgmtHtml/orderpaymentinfo.html",
                                        extras: {

                                        }
                                    })
                                }
                            })
                        }else {

                        }
                    })
                }
			}),
			/*查询*/
			$('.app_header').on('tap', '#OrdSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/orSearch.html",
						extras:{
						}
					})
				}
			}),
		    /*首页*/
			$('#nav').on('tap','.staHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			}),	
		    /*审核*/
	        $('.content_part').on('tap', '.staCheckBtns', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staordid');//备货单 ID
				var staOrdIdd = $(this).attr('staordids');//订单 ID
				var audit = 'audit', processPo = 'processPo';
				var baseURL='../../html/inventoryMagmetHtml/inCheck.html';
				var baseURLs='../../html/orderMgmtHtml/OrdermgmtHtml/orderCheck.html';
				if(url) {
					mui.toast('子菜单不存在')
				}
				else if(staOrdId) {
					//备货单
					GHUTILS.OPENPAGE({
						url: baseURL,
						extras: {
							staOrdIds: staOrdId,
							audits:audit,
							processPos:processPo
						}
					})
				}
				else if(staOrdIdd){
					GHUTILS.OPENPAGE({
						url: baseURLs,
						extras: {
							staOrdId: staOrdIdd,
							str:'audit'//checkType: "auditSo"
						}
					})
				}
			}),
			//支付申请列表
			$('.content_part').on('tap', '.staPayBtnes', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staordid');//采购单id
                var staOrderId = $(this).attr('staOrderId');//订单id
                var staInvenId = $(this).attr('staInvenId');//备货单id
				if(staInvenId!=""&&staOrderId=='undefined'){
					GHUTILS.OPENPAGE({
						//备货单						
						url: "../../html/orderMgmtHtml/payApplyList.html",
						extras: {
							staOrdId: staOrdId,
							staInvenId:staInvenId,
							fromPage: 'requestHeader',
						}
					})
				}
				if(staOrderId!=""&&staInvenId=='undefined') {
					GHUTILS.OPENPAGE({
						//订单						
						url: "../../html/orderMgmtHtml/payApplyList.html",
						extras: {
							staOrdId: staOrdId,
							staOrderId:staOrderId,
							fromPage: 'orderHeader',
						}
					})
				}
			}),
		    /*开启审核*/
	        $('.content_part').on('tap', '.stastartCheckBtn', function() {
				var url = $(this).attr('url');
				var paymentId = $(this).attr('paymentId');
				var staOrdId = $(this).attr('payorderid');
				var statu = $(this).attr('statu');
				var source = $(this).attr('source');
//				console.log(paymentId)
//				console.log(staOrdId)
				if(url) {
					mui.toast('子菜单不存在')
				} else if(paymentId!=""&&staOrdId=='undefined') {
					GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inventoryAmend.html",
						extras: {
							paymentId: paymentId,
							starStr: 'startAudit',
						}
					})
				}else if(staOrdId!=""&&paymentId=='undefined'){
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
						extras: {
							staOrdId: staOrdId,
							starStr: 'startAudit',
							statu:statu,
							source:source
						}
					})
				}
			}),
			/*详情*/
			$('.content_part').on('tap', '.orDetailBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/orDetails.html",
						extras: {
							staOrdId: staOrdId,
							str: 'detail',
							fromPage: 'orderHeader',
						}
					})
				}
			})
			/*排产*/
			$('.content_part').on('tap', '.SchedulingBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staordid');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/orScheduling.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
			/*确认排产*/
			$('.content_part').on('tap', '.affirmSchedulingBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staordid');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/affirmScheduling.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
			/*申请付款*/
			$('.content_part').on('tap', '.creatPayBtn', function() {
				var url = $(this).attr('url');
				var paymentId = $(this).attr('paymentId');
				var staOrdId = $(this).attr('payorderid');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(paymentId!=""&&staOrdId=='undefined') {
					GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inventoryAmend.html",
						extras: {
							paymentId: paymentId,
							createPayStr: 'createPay',
						}
					})
				}else if(staOrdId!=""&&paymentId=='undefined'){
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
						extras: {
							staOrdId: staOrdId,
							createPayStr: 'createPay',
						}
					})
				}
			})
			/*修改*/
			$('.content_part').on('tap', '.orAmendBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/orAmend.html",
						extras: {
							staOrdId: staOrdId,
							str: 'detail',
							fromPage: 'orderHeader',
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
			var orderHtmlList = '';
			var nameTxt = '';
			if(_this.userInfo.processTypeStr) {
				nameTxt = decodeURIComponent(_this.userInfo.processTypeStr)
			}else {
				nameTxt = ''
			}
			$.ajax({
				type: 'GET',
                url: '/a/biz/po/bizPoHeader/listV2Data4Mobile',
				data: {
					pageNo: 1,
					num: _this.userInfo.num ,//备货单号
					'vendOffice.id': _this.userInfo.vendOffice ,//供应商
					invStatus: _this.userInfo.invStatus , //发票状态
					bizStatus: _this.userInfo.bizStatus ,//业务状态
					processTypeStr:nameTxt ,//审核状态
					poSchType:_this.userInfo.poSchType ,//排产状态
                    waitPay: _this.userInfo.waitPay ,//待支付
                    applyPayment: _this.userInfo.applyPayment ,//可申请付款
                    includeTestData: _this.userInfo.includeTestData ,//测试数据
//                  isFunc: true
				},
				dataType: 'json',
				success: function(res) {
//					console.log(res)
		            	var dataRow = res.data.roleSet;
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
                        //订单支出状态                        
                        $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "biz_po_status"},
			                async:false,
			                success: function(res){
				                postatus=res;
			                }
			            });	
						var arrLen = res.data.page.list.length;
		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
//                          	console.log(item)
								//订单/备货单号								
                                var poNumTxt="";  
                                var itemId="";
	                        	if(item.bizOrderHeader){
	                        		poNumTxt=item.bizOrderHeader.orderNum;
	                        		itemId=item.bizOrderHeader.id;
	                        	}
	                        	if(item.bizRequestHeader){
	                        		poNumTxt=item.bizRequestHeader.reqNo;
	                        		itemId=item.bizRequestHeader.id;
	                        		
	                        	}
	                        	//排产状态
	                            var poSchTypeTxt = '';
	                            if(item.poSchType == 0 || item.poSchType == null){
	                            	poSchTypeTxt = res.data.SCHEDULING_NOT;
	                            }else{
	                            	if(item.poSchType == 1){
	                            		poSchTypeTxt = res.data.SCHEDULING_PLAN;
	                            	}
	                            	if(item.poSchType == 2){
	                            		poSchTypeTxt = res.data.SCHEDULING_DONE;
	                            	}
	                            }
	                            //订单支出状态
	                            var postatusTxt = '';
	                            $.each(postatus,function(i,items){
		                        	if(item.bizStatus==items.value) {
		                        		postatusTxt = items.label
		                        	}
	                            })
	                            //订单支出审核状态
	                            var paycheckTxt = '';
	                            if(item.commonProcess){
	                            	if(item.commonProcess.purchaseOrderProcess.name){
	                            		paycheckTxt=item.commonProcess.purchaseOrderProcess.name;
	                            	}
	                            }else{
	                            	paycheckTxt='当前无审批流程';
	                            }
					            if(_this.OrdFlaginfo == true) {
					            	if(item.bizStatus != 10) {
					            		//申请付款
										var creatPayBt = '';
										var creatPay = '';
										if(item.bizOrderHeader != null || item.bizOrderHeader != '' 
										|| item.bizRequestHeader != null || item.bizRequestHeader != '') {
											if(_this.ordCreatPayFlag == true) {
					                        	if(item.bizOrderHeader != null || item.bizOrderHeader != '') {
					                        		if((item.currentPaymentId == null || item.currentPaymentId == '') 
					                        		&& item.commonProcess.purchaseOrderProcess.name == '审批完成'
					                        		&& ((item.payTotal == null || item.payTotal == '') ? 0 : item.payTotal) < item.bizOrderHeader.totalDetail) {
//					                        			console.log('订单申请付款')
					                        			creatPay = '申请付款'
														creatPayBt = 'creatPayBtn'
					                        		}
					                        	}
					                        }
											if(_this.creatPayFlag == true) {
												if(item.bizRequestHeader != null || item.bizRequestHeader != '') {
													if((item.currentPaymentId == null || item.currentPaymentId == '') 
													&& item.bizRequestHeader.bizStatus >= 5 
													&& item.bizRequestHeader.bizStatus < 37 
													&& ((item.bizRequestHeader.bizPoHeader.payTotal == null || item.bizRequestHeader.bizPoHeader.payTotal == '') ? 0 : item.payTotal) < item.bizRequestHeader.totalDetail) {
//														console.log('备货单申请付款')
														creatPay = '申请付款'
														creatPayBt = 'creatPayBtn'
													}
												}
											}
										}else {
											if(_this.poCreatPayFlag == true) {
												var values = item.bizStatus;
												var dictLabel = '';
												$.ajax({
									                type: "GET",
									                url: "/a/sys/dict/getDictLabel4Mobile",
									                dataType: "json",
									                data: {
									                	value:values,
									                	type: "biz_po_status",
									                	defaultValue: '未知类型'
								                	},
									                async:false,
									                success: function(res){
//										                console.log(res.data.dictLabel)
										                dictLabel = res.data.dictLabel;
									                }
									            });
												if(item.bizPoPaymentOrder.id == null
												&& item.commonProcess.purchaseOrderProcess.name == '审批完成'
												&& dictLabel != '全部支付'
												&& item.payTotal < (item.totalDetail+item.totalExp)) {
													creatPay = '申请付款'
													creatPayBt = 'creatPayBtn'
												}
											}
										}
										//审核
					                	var staCheckBtn = '';
					                	var staCheckbtns = '';
					                	var staCheckBtnTxt = '';
					                	var staCheckBtns = '';
						                if(_this.OrdFlagaudit == true) {   
											var DataRoleGener = '';
											if(item.commonProcess) {
												DataRoleGener = item.commonProcess.purchaseOrderProcess.roleEnNameEnum;
											}
											var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
					                        if(item.commonProcess.id != null
					                        	&& item.commonProcess.purchaseOrderProcess.name != '驳回'
					                        	&& item.commonProcess.purchaseOrderProcess.name != '审批完成'
					                        	&& (fileRoleData.length>0 || userId==1))             {
					                        	if(item.bizOrderHeader != null || item.bizRequestHeader != null){
					                        		//订单审核
					                        	   	if(item.bizOrderHeader != ""){
					                        	   		staCheckBtnTxt = '审核';
					                        	   		staCheckBtns = 'staCheckBtns';
					                        	   		staCheckbtns = item.bizOrderHeader.id;
					                        	   	}
					                        	   	//备货单审核
					                        	   	if(item.bizRequestHeader != ""){
					                        	   		staCheckBtnTxt = '审核';
					                        	   		staCheckBtns = 'staCheckBtns';
					                        	   		staCheckBtn = item.bizRequestHeader.id;
					                        	   	}
					                        	}else{
					                        		//采购单审核
					                        		staCheckBtnTxt = '审核';
					                        		staCheckBtns = 'staCheckBtns';
					                        		staCheckBtn = item.id;
					                        	}
					                        }
						                }
					                	//支付申请列表
					                	var staPayBtnTxt = '';
					                	var staPayBtnes = '';
					                	if(item.commonProcess.type != -1){
					                		if(item.bizOrderHeader != null){
					                			//订单
					                			if(_this.OrdFlagpay == true){
					                				staPayBtnTxt = '支付列表';
					                				staPayBtnes = 'staPayBtnes';
					                			}
					                		}
					                		if(item.bizRequestHeader != null){
					                			//备货单
					                			if(_this.OrdFlagpay == true){
					                				staPayBtnTxt = '支付列表';
					                				staPayBtnes = 'staPayBtnes';
					                			}
					                		}
					                	}
										//开启审核
					                	var stastartCheckBtn = '';
					                	var stastartCheckBtnTxt = '';
					                	if(_this.OrdFlagstartAudit==true){
					                		if(item.commonProcess.type == -1){
					                			if(item.bizOrderHeader != null) {
					                				stastartCheckBtnTxt = '开启审核';
					                				stastartCheckBtn = 'stastartCheckBtn'
					                			}
					                			if(item.bizRequestHeader != null){
					                				stastartCheckBtnTxt = '开启审核';
					                				stastartCheckBtn = 'stastartCheckBtn'
					                			}
					                		}
					                	}
										//排产
					                	var SchedulingBtnTxt = '';
					                	var SchedulingBtn = '';
					                	if(_this.OrdFlagScheduling==true){
					                		SchedulingBtnTxt = '排产';
					                		SchedulingBtn = 'SchedulingBtn';
					                	}
					                	//修改
					                	var orAmendBtn = '';
					                	var orAmendBtnTxt = '';
					                	//取消
										var orCancleBtn = '';
										var orCancleBtnTxt = '';
					                	if(_this.orCancAmenFlag == true) {
					                		if(item.commonProcess.purchaseOrderProcess.name == null 
				                			|| item.commonProcess.purchaseOrderProcess.name == '驳回') {
					                			orAmendBtn = 'orAmendBtn';
					                			orAmendBtnTxt = '修改';
					                		}
				                			orCancleBtn = 'orCancleBtn';
											orCancleBtnTxt = '取消';
					                	}
					            		//详情
										var orDetailBtn = '';
										var orDetailBtnTxt = '';
					                	if(_this.OrdFlaginfo == true) {
											var orDetailBtn = 'orDetailBtn';
											var orDetailBtnTxt = '详情';
					                	}
					                	//确认排产
										var affirmSchedulingBtn = '';
										var affirmSchedulingBtnTxt = '';
					                	if(_this.affirmSchedulingFlag == true) {
					                		affirmSchedulingBtn = 'affirmSchedulingBtn';
											affirmSchedulingBtnTxt = '确认排产';
					                	}
					            	}
					            }
	                        	//隐藏信息
				                var staCheckSucBtn = "";
			                	var staCheckSuc = "";
			                	var staCheckSuc = item.bizOrderHeader.id;
			                	var staCheck = item.bizRequestHeader.id;
								orderHtmlList +='<div class="ctn_show_row app_li_text_center app_bline2 app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row" style="display:none">' +
										    '<label>备货单号:</label>' +
											'<input type="text" class="mui-input-clear staPayBtns" disabled="disabled" podid="'+ staCheckSuc +'" podids="'+ staCheck +'">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>订单/备货单号:</label>' +
											'<input type="text" class="mui-input-clear dd" disabled="disabled" value=" '+ poNumTxt+' " id="poNum_' + itemId + '">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>供应商:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.vendOffice.name +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>采购总价:</label>' +
											'<input type="text" class="mui-input-clear orderTypeTxt" disabled="disabled" value=" '+ item.totalDetail.toFixed(1)+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>支出状态:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+postatusTxt +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>审核状态:</label>' +
											'<input type="text" class="mui-input-clear" id="paycheckSta" disabled="disabled" value=" '+ paycheckTxt +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>排产状态:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ poSchTypeTxt +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>创建时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ _this.formatDateTime(item.createDate)+' ">' +
										'</div>' +
										'<div class="mui-input-row app_color40 app_text_center content_part operation " id="foot">' +
							/*kong*/		'<div name="commBtn" class="" staordid="'+ item.id +'">' +
												'<div class="" ></div></div>' +
							/*修改*/			'<div name="commBtn" class="'+orAmendBtn+'" staOrdId="'+ item.id +'">' +
												'<div class="" >'+orAmendBtnTxt+'</div></div>' +
							/*审核*/			'<div name="commBtn" class="'+staCheckBtns+'" staordid="'+ staCheckBtn +'" staordids="'+ staCheckbtns +'">' +
												'<div class="" >'+staCheckBtnTxt+'</div></div>' +
							/*申请付款*/		'<div name="commBtn" class="'+creatPayBt+'" paymentId="'+item.bizRequestHeader.id+'" payOrderId="'+item.bizOrderHeader.id+'">' +
												'<div class="" >'+creatPay+'</div></div>' +
							/*开启审核*/		'<div name="commBtn" class="'+stastartCheckBtn+'" paymentId="'+item.bizRequestHeader.id+'" payOrderId="'+item.bizOrderHeader.id+'" statu="'+item.bizOrderHeader.statu+'" source="'+item.bizOrderHeader.source+'">' +
												'<div class="" >'+stastartCheckBtnTxt+'</div></div>' +
										'</div>' +
										'<div class="app_color40 mui-row app_text_center content_part operation " id="foot">' +
							/*取消*/			'<div name="commBtn" class="'+orCancleBtn+'" staordid="'+ item.id +'">' +
												'<div class="" >'+orCancleBtnTxt+'</div></div>' +
							/*详情*/			'<div name="commBtn" class="'+orDetailBtn+'" staOrdId="'+ item.id +'">' +
												'<div class="" >'+orDetailBtnTxt+'</div></div>' +
							/*排产*/			'<div name="commBtn" class="'+SchedulingBtn+'" staordid="'+ item.id +'">' +
												'<div class="" >'+SchedulingBtnTxt+'</div></div>' +
							/*支付列表*/		'<div name="commBtn" class="'+staPayBtnes+'" staordid="'+ item.id +'" staOrderId="'+ item.bizOrderHeader.id +'" staInvenId="'+ item.bizRequestHeader.id +'">' +
												'<div class="" >'+staPayBtnTxt+'</div></div>' +
							/*确认排产*/		'<div name="commBtn" class="'+affirmSchedulingBtn+'" staordid="'+ item.id +'">' +
												'<div class="" >'+affirmSchedulingBtnTxt+'</div></div>' +
										'</div>' +
									'</div>'
								});
								$('#orderinfoList').append(orderHtmlList);
								_this.stOrdHrefHtml();
								//先隐藏订单信息
//								var pos=$(".ctn_show_row .dd");
//								var posd=$(".ctn_show_row .staPayBtns");
//								$.each(pos,function(n,v){
//	                            	var poNumid=$(this).attr('id').substr(6);
//	                            	$.each(posd,function(n,v){
//	                            		var that=this;
//		                            	var y=$(that).attr('podid');
//		                            	var divs=$("#poNum_"+poNumid);
//		                            	if(poNumid==y){
//		                            		divs.parent().parent().hide()
//		                            	}
//		                            })
//	                            })
					}else{
						$('#orderinfoList').append('<p class="noneTxt">暂无数据</p>');
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

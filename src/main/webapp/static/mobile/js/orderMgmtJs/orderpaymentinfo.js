(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
//		this.OrdFlaginfo = "false";
		this.OrdFlagaudit = "false";
		this.OrdFlagpay = "false";
		this.OrdFlagstartAudit = "false";
		this.OrdFlagScheduling = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
//			biz:order:bizOrderHeader:view		操作
//			this.getPermissionList('biz:po:bizPoHeader:view','OrdFlaginfo');
			this.getPermissionList('biz:po:bizPoHeader:audit','OrdFlagaudit');
			this.getPermissionList1('biz:po:pay:list','OrdFlagpay');
			this.getPermissionList2('biz:po:bizPoHeader:startAuditAfterReject','OrdFlagstartAudit');
			this.getPermissionList3('biz:po:bizPoHeader:addScheduling','OrdFlagScheduling');
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
//			                    pager['flag'] = "check_pending";
//			                    pager['consultantId'] = _this.userInfo.staListId;
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
		            	console.log(res)
		            	var dataRow = res.data.roleSet;
		            	/*当前用户信息*/
						var userId = '';
						$.ajax({
			                type: "GET",
			                url: "/a/getUser",
			                dataType: "json",
			                async:false,
			                success: function(user){                 
					            console.log(user)
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
				                console.log(res);
				                postatus=res;
			                }
			            });	
						var arrLen = res.data.page.list.length;
						console.log(arrLen)
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true)
						}
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
                            	console.log(item)
								//订单/备货单号
								var bizOrderId=item.id;
	                        	console.log(bizOrderId)
//	                        	$.ajax({
//					                type: "GET",
//					                url: "/a/biz/po/bizPoHeader/form",
//					                dataType: "json",
//					                data: {id:bizOrderId,str:'detail'},
//					                async:false,
//					                success: function(res){
//					                	console.log(res)
//						                ass=res;
//					                }
//					            });	
//	                        	if(item.bizOrderHeader != null){
//	                        		
//	                        	}
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
	                        	//审核
	                        	var staCheckBtn = '';
	                        	var staCheckbtns = '';
	                        	var staCheckBtnTxt = '';
	                        	console.log(_this.OrdFlagaudit)
				                if(_this.OrdFlagaudit == true) {   
									var DataRoleGener = '';
									if(item.commonProcess) {
										DataRoleGener = item.commonProcess.purchaseOrderProcess.roleEnNameEnum;
									}
									var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));	
									//&& item.commonProcess.purchaseOrderProcess.code != payStatus
	                                if(item.commonProcess.id != null&& item.commonProcess.purchaseOrderProcess.name != '驳回'&& item.commonProcess.purchaseOrderProcess.name != '审批完成'&& (fileRoleData.length>0 || userId==1))             {
	                                	if(item.bizOrderHeader != null || item.bizRequestHeader != null){
	                                		//订单审核
	                                		console.log(item.bizOrderHeader.id)
	                                	   	if(item.bizOrderHeader != ""){
	                                	   		console.log('nnnn')
	                                	   		staCheckBtnTxt = '审核';
	                                	   		staCheckbtns = item.bizOrderHeader.id;
	                                	   	}
	                                	   	//备货单审核
	                                	   	if(item.bizRequestHeader != ""){
	                                	   		console.log('mmmm')
	                                	   		staCheckBtnTxt = '审核';
	                                	   		staCheckBtn = item.bizRequestHeader.id;
	                                	   	}
	                                	}else{
	                                		//采购单审核
	                                		staCheckBtnTxt = '审核';
	                                		staCheckBtn = item.id;
	                                	}
	                                }
				                }else {
				                	staCheckBtnTxt = ''
				                }
	                        	var staCheckSucBtn = '';	                        		                    
	                        	var staCheckSuc = '';
	                        	//支付申请列表
	                        	var staPayBtn = '';
	                        	var staPayBtnTxt = '';
	                        	if(item.commonProcess.type != -1){
	                        		if(item.bizOrderHeader != null){
	                        			console.log(_this.OrdFlagpay)
	                        			if(_this.OrdFlagpay==false){
	                        				staPayBtnTxt = '支付申请列表';
	                        			}	                        			
	                        		}
	                        		if(item.bizRequestHeader != null){
	                        			if(_this.OrdFlagpay==false){
	                        				staPayBtnTxt = '支付申请列表';
	                        			}	                        			
	                        		}
	                        	}
	                        	//开启审核
	                        	var stastartCheckBtn = '';
	                        	var stastartCheckBtnTxt = '';
	                        	if(_this.OrdFlagstartAudit==false){
	                        		if(item.commonProcess.type == -1){
	                        			if(item.bizOrderHeader != null){
	                        				stastartCheckBtnTxt = '开启审核';
	                        			}
	                        			if(item.bizRequestHeader != null){
	                        				stastartCheckBtnTxt = '开启审核';
	                        			}
	                        		}
	                        	}
	                        	//排产
	                        	var SchedulingBtn = '';
	                        	var SchedulingBtnTxt = '';
	                        	if(_this.OrdFlagScheduling==false){
	                        		SchedulingBtnTxt = '排产';
	                        	}
								orderHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row">' +
											'<label>订单/备货单号:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ +' ">' +
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
											'<label>订单支出状态:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+postatusTxt +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>订单支出审核状态:</label>' +
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
										'<div class="app_color40 mui-row app_text_center content_part operation">' +
											'<div class="mui-col-xs-3 staCheckBtns" staordid="'+ staCheckBtn +'" staordids="'+ staCheckbtns +'">' +
												'<li class="mui-table-view-cell">'+ staCheckBtnTxt +'</li>' +
											'</div>'+
											'<div class="mui-col-xs-3 staPayBtn" staordid="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+ staPayBtnTxt +'</li>' +
											'</div>'+
											'<div class="mui-col-xs-3 '+ stastartCheckBtn +'" staordid="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+ stastartCheckBtnTxt +'</li>' +
											'</div>'+
											'<div class="mui-col-xs-3 SchedulingBtn" staordid="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+ SchedulingBtnTxt +'</li>' +
											'</div>'+
//											'<div class="mui-col-xs-3 staOrDetailBtn" staOrdId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">详情</li>' +
//											'</div>'+
										'</div>' +
									'</div>'
								});
								$('#orderinfoList').append(orderHtmlList);
								_this.stOrdHrefHtml()
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
//			            console.log(type);
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
                	
//                  _this.OrdFlaginfo = res.data;
//                  console.log(_this.OrdFlaginfo)
                    _this.OrdFlagaudit = res.data;
                    console.log(_this.OrdFlagaudit)
//                  _this.OrdFlagpay =  res.data;
//                  console.log(_this.OrdFlagpay)
//                  _this.OrdFlagstartAudit = res.data;
//                  console.log(_this.OrdFlagstartAudit)
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
                	
//                  _this.OrdFlaginfo = res.data;
//                  console.log(_this.OrdFlaginfo)
                    _this.OrdFlagpay =  res.data;
                    console.log(_this.OrdFlagpay)
//                  _this.OrdFlagstartAudit = res.data;
//                  console.log(_this.OrdFlagstartAudit)
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
                	
//                  _this.OrdFlaginfo = res.data;
//                  console.log(_this.OrdFlaginfo)
//                  _this.OrdFlagpay =  res.data;
//                  console.log(_this.OrdFlagpay)
                    _this.OrdFlagstartAudit = res.data;
                    console.log(_this.OrdFlagstartAudit)
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
                    _this.OrdFlagScheduling = res.data;
                    console.log(_this.OrdFlagScheduling)
                }
            });
        },
		stOrdHrefHtml: function() {
			var _this = this;
		/*查询*/
			$('.app_header').on('tap', '#OrdSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/orSearch.html",
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
				var baseURLs='../../html/staffMgmtHtml/orderHtml/staOrdCheck.html';
//				console.log(staOrdId)
//				console.log(audit)
//				console.log(processPo)
//				console.log(staOrdIdd)
				if(url) {
					mui.toast('子菜单不存在')
				}
				else if(staOrdId) {
					alert(1)
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
					alert(2)
					GHUTILS.OPENPAGE({
						url: baseURLs,
						extras: {
							staOrdIds: staOrdId,
						}
					})
				}
			}),
			//支付申请列表
			 $('.content_part').on('tap', '.staPayBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/payApplyList.html",
//						extras: {
//							staOrdId: staOrdId,
//						}
					})
				}
			}),
		/*修改*/
//	       $('.content_part').on('tap', '.staOraAmendBtn', function() {
//				var url = $(this).attr('url');
//				var staOrdId = $(this).attr('staOrdId');
//				if(url) {
//					mui.toast('子菜单不存在')
//				} else if(staOrdId == staOrdId) {
//					GHUTILS.OPENPAGE({
//						url: "../../../html/staffMgmtHtml/orderHtml/staOrdAmend.html",
//						extras: {
//							staOrdId: staOrdId,
//						}
//					})
//				}
//			}),
		/*详情*/
			$('.content_part').on('tap', '.staOrDetailBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdDetail.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
			/*排产*/
			$('.content_part').on('tap', '.SchedulingBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
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
					'pageNo': 1,
					'orderNum' : _this.userInfo.staOrder,
                    'centersName': nameTxts,
                    'customer.phone': _this.userInfo.OrdMobile,
                    'itemNo': _this.userInfo.OrdNumbers,
                    'bizStatus': _this.userInfo.orderStatus,
//                  'selectAuditStatus': nameTxt, //originConfigMap
                    'customer.id':_this.userInfo.newinput,
                    consultantId: _this.userInfo.staListSehId,
					includeTestData: _this.userInfo.includeTestData,
					mobileAuditStatus: _this.userInfo.mobileAuditStatus,
					flag: _this.userInfo.flagTxt
				},
				dataType: 'json',
				success: function(res) {
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
		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						var arrLen = res.data.page.list.length;	
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
//	                        	console.log(item)
								$('#statu').val(item.statu);
								$('#source').val(item.source);
	                        	//订单类型  1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单 5.代采订单 6.拍照下单
	                            var orderTypeTxt = '';
	                            $.each(ass,function(i,items){
		                        	if(item.orderType==items.value) {
		                        		orderTypeTxt = items.label
		                        	}
	                            })
								//审核
	                        	var staCheckBtn = '';
	                        	var staCheckBtnTxt = '';
	                        	console.log(_this.OrdFlagaudit)
				                if(_this.OrdFlagaudit == true) {
									var DataRoleGener = '';
									if(item.commonProcess) {
										DataRoleGener = item.commonProcess.purchaseOrderProcess.roleEnNameEnum;
									}
									var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
									//&& item.commonProcess.purchaseOrderProcess.code != payStatus
	                                if(item.commonProcess.id != null&& item.commonProcess.purchaseOrderProcess.name != '驳回'&& item.commonProcess.purchaseOrderProcess.name != '审批完成'&& (fileRoleData.length>0 || userId==1))             {
	                                	if(item.bizOrderHeader != null || item.bizRequestHeader != null){
	                                		//订单审核
	                                	   	if(item.bizOrderHeader != null){
	                                	   		staCheckBtnTxt = '审核';
	                                	   		staCheckBtn = item.bizOrderHeader.id;
	                                	   	}
	                                	   	//备货单审核
	                                	   	if(item.bizRequestHeader != null){
	                                	   		staCheckBtnTxt = '审核';
	                                	   		staCheckBtn = item.bizRequestHeader.id;
	                                	   	}
	                                	}else{
	                                		//采购单审核
	                                		staCheckBtnTxt = '审核';
	                                		staCheckBtn = item.id;
	                                	}
	                                }
				                }else {
				                	staCheckBtnTxt = ''
				                }
	                        	var staCheckSucBtn = '';
	                        	var staCheckSuc = '';
	                             var staffHtmlList = '';
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
											'<label>创建时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>更新时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
										'</div>' +
										//业务状态需要添加权限
//										'<div class="mui-input-row">' +
//											'<label>业务状态:</label>' +
//											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
//										'</div>' +
										'<div class="app_color40 mui-row app_text_center content_part operation">' +
											'<div class="mui-col-xs-6 '+staCheckBtn+'" staOrdId="'+ item.id +'">' +
												'<li class="mui-table-view-cell" id="flagid">'+ staCheckBtnTxt +'</li>' +
											'</div>'+
//											'<div class="mui-col-xs-3"  staOrdId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">出库确认</li>' +
//											'</div>'+
//											'<div class="mui-col-xs-3"  staOrdId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">审核成功</li>' +
//											'</div>'+
											'<div class="mui-col-xs-6 staOrDetailBtn" staOrdId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">详情</li>' +
											'</div>'+
										'</div>' +
									'</div>'
								});
								$('#orderinfoList').append(staffHtmlList);
								_this.stOrdHrefHtml()
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

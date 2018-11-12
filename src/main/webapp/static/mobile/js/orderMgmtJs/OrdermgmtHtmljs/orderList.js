(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.staOrdFlag = "false";
		this.staOrdauditFlag = "false";
		this.staOrdsupplyFlag = "false";
		this.staOrdeditFlag = "false";
		this.OrdFlaginfo = "false";
		this.OrdFlagaudit = "false";
		this.ordCreatPayFlag = "false";
		this.reCreatPayFlag = "false";
		this.poCreatPayFlag = "false";
		this.OrdFlagstartAudit = "false";
		this.OrdFlagScheduling = "false";
		this.orCancAmenFlag = "false";
		this.affirmSchedulingFlag = "false";
		this.OrdFlagpay = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
			this.getPermissionList('biz:order:bizOrderHeader:view','staOrdFlag')//true 操作
			this.getPermissionList1('biz:order:bizOrderHeader:audit','staOrdauditFlag')//false审核
			this.getPermissionList2('biz:order:bizOrderHeader:supplying','staOrdsupplyFlag')//false出库确认
			this.getPermissionList3('biz:order:bizOrderHeader:edit','staOrdeditFlag')//修改、删除true
			this.getPermissionList4('biz:po:bizPoHeader:view','OrdFlaginfo');//支出信息列表操作总权限
			this.getPermissionList5('biz:po:bizPoHeader:audit','OrdFlagaudit');//付款单审核
			this.getPermissionList6('biz:request:bizOrderHeader:createPayOrder','ordCreatPayFlag')//申请付款or
			this.getPermissionList7('biz:request:bizRequestHeader:createPayOrder','reCreatPayFlag')//申请付款re
			this.getPermissionList8('biz:po:bizPoHeader:createPayOrder','poCreatPayFlag')//申请付款po
			this.getPermissionList9('biz:po:bizPoHeader:startAuditAfterReject','OrdFlagstartAudit');//开启审核
			this.getPermissionList01('biz:po:bizPoHeader:addScheduling','OrdFlagScheduling');//排产
			this.getPermissionList02('biz:po:bizPoHeader:edit','orCancAmenFlag')//修改、取消 false
			this.getPermissionList03('biz:po:bizPoHeader:confirmScheduling','affirmSchedulingFlag')//确认排产
			this.getPermissionList04('biz:po:pay:list','OrdFlagpay');//支付单列表
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
			            	//订单查询页面传过来的值
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
							var namePoTxt = '';
							if(_this.userInfo.processTypeStr) {
								namePoTxt = decodeURIComponent(_this.userInfo.processTypeStr)
							}else {
								namePoTxt = ''
							}
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
							if(namePoTxt==undefined){
								namePoTxt="";
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
							if(_this.userInfo.poBizStatus==undefined){
								_this.userInfo.poBizStatus="";
							}
							if(_this.userInfo.poSchType==undefined){
								_this.userInfo.poSchType="";
							}
							if(_this.userInfo.poWaitPay==undefined){
								_this.userInfo.poWaitPay="";
							}
							//客户专员查询
							var nameTxtss = '';
							if(_this.userInfo.Purchasing) {
								nameTxtss = decodeURIComponent(_this.userInfo.Purchasing)
							}else {
								nameTxtss = ''
							}
							if(_this.userInfo.staOrder==undefined){
								_this.userInfo.staOrder="";
							}
							if(_this.userInfo.OrdMobile==undefined){
								_this.userInfo.OrdMobile="";
							}
							if(_this.userInfo.OrdNumbers==undefined){
								_this.userInfo.OrdNumbers="";
							}
							if(_this.userInfo.orderStatus==undefined){
								_this.userInfo.orderStatus="";
							}
							if(_this.userInfo.newinput==undefined){
								_this.userInfo.newinput="";
							}
							if(_this.userInfo.staListSehId==undefined){
								_this.userInfo.staListSehId="";
							}
							if(_this.userInfo.includeTestData==undefined){
								_this.userInfo.includeTestData="";
							}
							if(_this.userInfo.mobileAuditStatus==undefined){
								_this.userInfo.mobileAuditStatus="";
							}
							if(_this.userInfo.flagTxt==undefined){
								_this.userInfo.flagTxt="";
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
								pager['poBizStatus'] = _this.userInfo.poBizStatus,//付款单业务状态
								pager['processTypeStr'] = namePoTxt,//付款单审核状态
								pager['poSchType'] = _this.userInfo.poSchType,//付款单排产状态
								pager['poWaitPay'] = _this.userInfo.poWaitPay,//付款单待支付
		                    	pager['includeTestData'] = _this.userInfo.includeTestData;//测试数据
		                    	if(statu == 'unline') {
			                    	$('#myStatu').val(statu);
			                    	$('#listName').html('线下支付订单列表');
			                    }
		                    	if(statu != 'unline') {
			                    	$('#myStatu').val('');
			                    	$('#listName').html('订单列表');
			                    }
		                    	getData(pager);
		                    }else if(_this.userInfo.isFind){
		                    	pager['size']= 20;
			                    pager['pageNo'] = 1;
								pager['orderNum'] = _this.userInfo.staOrder;//订单编号
								pager['centersName'] = nameTxtss;//采购中心
								pager['customer.phone'] = _this.userInfo.OrdMobile;//经销店电话
								pager['itemNo'] = _this.userInfo.OrdNumbers;//商品货号
								pager['bizStatus'] = _this.userInfo.orderStatus;//订单状态
								pager['customer.id'] = _this.userInfo.newinput;//经销店名称
								pager['consultantId'] = _this.userInfo.staListSehId;//客户专员id
								pager['includeTestData'] = _this.userInfo.includeTestData;//测试数据
								pager['mobileAuditStatus'] = _this.userInfo.mobileAuditStatus;//待审核、审核成功
								pager['flag'] = _this.userInfo.flagTxt;
								getData(pager);
								$('#listName').html('订单列表');
		                    }
			                else{
		                    	//直接进来的参数数据
		                    	pager['size']= 20;
			                    pager['pageNo'] = 1;
			                    if(statu != 'unline') {
//			                    	console.log(1)
			                    	pager['statu'] = '';
			                    	$('#myStatu').val('');
			                    	$('#listName').html('订单列表');
			                    }
			                    if(statu == 'unline') {
//			                    	console.log(2)
			                    	pager['statu'] = statu;
			                    	$('#myStatu').val(statu);
			                    	$('#listName').html('线下支付订单列表');
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
								$('#userId').val(userId)
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
//                          	console.log(item)
                            	console.log(item.bizPoHeader)
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
                                                ProcessName = item.bizPoHeader.commonProcess.purchaseOrderProcess.name
											}
										}
									}
									if((item.orderType == res.data.ORDINARY_ORDER||item.orderType == res.data.COMMISSION_ORDER) && item.bizStatus >= res.data.SUPPLYING) {
										if(objectName == 'ORDER_HEADER_SO_LOCAL') {
											ProcessName = commonProcess.jointOperationLocalProcess.name
										}
										if(objectName == 'ORDER_HEADER_SO_ORIGIN') {
											if(commonProcess.jointOperationOriginProcess.name != '审批完成') {
												ProcessName = commonProcess.jointOperationOriginProcess.name
											}
											if(commonProcess.jointOperationOriginProcess.name == '审批完成') {
												ProcessName = item.bizPoHeader.commonProcess.purchaseOrderProcess.name
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
				                if(_this.staOrdFlag == true) {				                	                                    					                //客户专员审核
                                    if(userId == item.consultantId||userId==1){
                                    	if(userId == item.consultantId){
                                    		$('#OrdSechBtn').attr('id','SearchBtn')
                                    	}
                                    	if(item.bizStatus < 15) {
					                		staCheckBtnTxt = "待审核"
					                		ordCheckBtn = 'waitCheckBtn';
					                	}
					                	if(item.bizStatus==45) {
					                		staCheckBtnTxt = "发货失败";
					                	}
					                	if(item.bizStatus==15) {
					                		staCheckBtnTxt = "发货成功";
					                	}
                                    }				                	
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
                                                if((item.orderType == res.data.ORDINARY_ORDER||item.orderType==res.data.COMMISSION_ORDER) && item.bizStatus >= res.data.SUPPLYING){
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
                                    		if(res.data.statu == 'unline' || userId==1){
                                    			ordwaterCourseBtnTxt ="支付流水";
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
//				---合并---
				                //订单支出审核状态
//	                            var paycheckTxt = '';
//	                            if(item.commonProcess){
//	                            	if(item.commonProcess.purchaseOrderProcess.name){
//	                            		paycheckTxt=item.commonProcess.purchaseOrderProcess.name;
//	                            	}
//	                            }else{
//	                            	paycheckTxt='当前无审批流程';
//	                            }
//				                                待发货.发货成功/发货失败    和上面用同一个按钮
								var bizReId = '';
								var bizOrId = '';
								/*付款单列表*/
								var applyListBtn = '';
								var applyListBtnTxt = '';
//								付款单审核  
								var applyCheckBtn = '';
								var applyCheckBtnTxt = '';
								/*付款单取消*/
								var applyCandelBtn = '';
								var applyCandelBtnTxt = '';
								/*付款单详情*/
								var applyDetailBtn = '';
								var applyDetailBtnTxt = '';
								/*排产*/
								var scheduBtn = '';
								var scheduBtnTxt = '';
								/*确认排产*/
								var affirmScheduBtn = '';
								var affirmScheduBtnTxt = '';
								/*申请付款*/
								var creatPay = '';
								var	creatPayBtn = '';
								/*开启审核*/
								var stastartCheckBtnTxt = '';
                				var stastartCheckBtn = '';
                				var bizPoHeader = '';
                				//支出信息页面过来的按钮：
								if(item.bizPoHeader) {
									bizPoHeader = item.bizPoHeader;
									console.log(bizPoHeader)
	                                var paycheckTxt = '';
		                            if(bizPoHeader.commonProcess){
		                            	if(bizPoHeader.commonProcess.purchaseOrderProcess.name){
		                            		paycheckTxt=bizPoHeader.commonProcess.purchaseOrderProcess.name;
		                            	}
		                            }else{
		                            	paycheckTxt='当前无审批流程';
		                            }
									if(_this.OrdFlaginfo == true) {
						            	if(bizPoHeader.bizStatus != 10) {
						            		//申请付款
											if(bizPoHeader.bizOrderHeader != null || bizPoHeader.bizOrderHeader != '' 
											|| bizPoHeader.bizRequestHeader != null || bizPoHeader.bizRequestHeader != '') {
												if(_this.ordCreatPayFlag == true) {
													if(bizPoHeader.bizOrderHeader != null || bizPoHeader.bizRequestHeader != null
													|| bizPoHeader.bizOrderHeader != '' || bizPoHeader.bizRequestHeader != ''){
														if(_this.ordCreatPayFlag == true) {
								                        	if(bizPoHeader.bizOrderHeader != null || bizPoHeader.bizOrderHeader != '') {
								                        		if((bizPoHeader.currentPaymentId == null || bizPoHeader.currentPaymentId == '') 
								                        		&& paycheckTxt == '审批完成'
								                        		&& ((bizPoHeader.payTotal == null || bizPoHeader.payTotal == '') ? 0 : bizPoHeader.payTotal) < bizPoHeader.bizOrderHeader.totalDetail) {
	//							                        			console.log('订单申请付款')
								                        			creatPay = '申请付款';
																	creatPayBtn = 'creatPayBtn';
																	bizOrId = bizPoHeader.bizOrderHeader.id;
								                        		}
								                        	}
								                        }
														if(_this.reCreatPayFlag == true) {
															if(bizPoHeader.bizRequestHeader != null || bizPoHeader.bizRequestHeader != '') {
																if((bizPoHeader.currentPaymentId == null || bizPoHeader.currentPaymentId == '') 
																&& bizPoHeader.bizRequestHeader.bizStatus >= 5 
																&& bizPoHeader.bizRequestHeader.bizStatus < 37 
																&& ((bizPoHeader.bizRequestHeader.bizPoHeader.payTotal == null || bizPoHeader.bizRequestHeader.bizPoHeader.payTotal == '') ? 0 : bizPoHeader.payTotal) < bizPoHeader.bizRequestHeader.totalDetail) {
	//																console.log('备货单申请付款')
																	creatPay = '申请付款';
																	creatPayBtn = 'creatPayBtn';
																	bizReId = bizPoHeader.bizRequestHeader.id;
																}
															}
														}
													}else {
														if(_this.poCreatPayFlag == true) {
															var values = bizPoHeader.bizStatus;
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
															if((bizPoHeader.bizPoPaymentOrder.id == null || bizPoHeader.bizPoPaymentOrder.id == '')
															&& paycheckTxt == '审批完成'
															&& dictLabel != '全部支付'
															&& bizPoHeader.payTotal < (bizPoHeader.totalDetail+bizPoHeader.totalExp)) {
																creatPay = '申请付款';
																creatPayBtn = 'creatPayBtn';
																bizOrId = bizPoHeader.bizOrderHeader.id;
																bizReId = bizPoHeader.bizRequestHeader.id;
															}
														}
													}
												}
											}
											//付款单审核
											console.log(_this.OrdFlagaudit)
							                if(_this.OrdFlagaudit == true) {
												var DataRoleGener = '';
												if(bizPoHeader.commonProcess) {
													DataRoleGener = bizPoHeader.commonProcess.purchaseOrderProcess.roleEnNameEnum;
												}
												var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
												console.log(paycheckTxt)
						                        if((bizPoHeader.commonProcess.id)
						                        	&& paycheckTxt != '驳回'
						                        	&& paycheckTxt != '审批完成'
						                        	&& (fileRoleData || userId==1)){
						                        		console.log(bizPoHeader.bizOrderHeader)
						                        	if(bizPoHeader.bizOrderHeader){
						                        		//订单审核
					                        	   		console.log(1)
					                        	   		applyCheckBtnTxt = '付款单审核';
					                        	   		applyCheckBtn = 'applyCheckBtn';
					                        	   		bizOrId = item.id;
						                        	}else{
						                        		console.log(3)
						                        		//采购单审核
						                        		applyCheckBtnTxt = '付款单审核';
						                        		applyCheckBtn = 'applyCheckBtn';
						                        		bizReId = bizPoHeader.id;
						                        	}
						                        }
							                }
						                	//支付申请列表
						                	if(bizPoHeader.commonProcess.type != -1){
						                		if(bizPoHeader.bizOrderHeader != ''){
						                			//订单
						                			if(_this.OrdFlagpay == true){
						                				applyListBtn = 'applyListBtn';
														applyListBtnTxt = '付款单列表';
														bizOrId = bizPoHeader.bizOrderHeader.id; 
						                			}
						                		}
						                		if(bizPoHeader.bizRequestHeader != null || bizPoHeader.bizRequestHeader != ''){
						                			//备货单
						                			if(_this.OrdFlagpay == true){
						                				applyListBtn = 'applyListBtn';
														applyListBtnTxt = '付款单列表';
														bizReId = bizPoHeader.bizRequestHeader.id;
						                			}
						                		}
						                	}
											//开启审核
						                	if(_this.OrdFlagstartAudit==true){
						                		if(bizPoHeader.commonProcess.type == -1){
						                			var statu = bizPoHeader.bizOrderHeader.statu;
						                			var source = bizPoHeader.bizOrderHeader.source;
						                			if(bizPoHeader.bizOrderHeader != null || bizPoHeader.bizOrderHeader != '') {
						                				stastartCheckBtnTxt = '开启审核';
						                				stastartCheckBtn = 'stastartCheckBtn';
						                				bizOrId = bizPoHeader.bizOrderHeader.id;
						                			}
						                			if(bizPoHeader.bizRequestHeader != null || bizPoHeader.bizRequestHeader != ''){
						                				stastartCheckBtnTxt = '开启审核';
						                				stastartCheckBtn = 'stastartCheckBtn';
						                				bizReId = bizPoHeader.bizRequestHeader.id;
						                			}
						                		}
						                	}
											//排产
						                	if(_this.OrdFlagScheduling==true){
						                		scheduBtn = 'scheduBtn';
												scheduBtnTxt = '排产';
						                	}
						                	//修改
						                	//付款单取消
						                	if(_this.orCancAmenFlag == true) {
						                		if(paycheckTxt == null || paycheckTxt == ''
					                			|| paycheckTxt == '驳回') {
						                			orAmendBtn = 'orAmendBtn';
						                			orAmendBtnTxt = '修改';
						                		}
					                			applyCandelBtn = 'applyCandelBtn';
												applyCandelBtnTxt = '付款单取消';
						                	}
						            		//付款单详情
						                	if(_this.OrdFlaginfo == true) {
												applyDetailBtn = 'applyDetailBtn';
												applyDetailBtnTxt = '付款单详情';
						                	}
						                	//确认排产
						                	if(_this.affirmSchedulingFlag == true) {
						                		affirmScheduBtn = 'affirmScheduBtn';
												affirmScheduBtnTxt = '确认排产';
						                	}
						            	}
						            }
								}
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
										'<div class="mui-input-row app_color40 app_text_center content_part operation" id="foot">' +
											'<div class="'+ordDetailBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
							/*详情*/				staDetailBtnTxt +'</div>'+
											'<div class="'+ ordCheckBtn +'" staOrdId="'+ item.id +'">' +
							/*审核*/				staCheckBtnTxt +'</div>'+										
											'<div class="'+scheduBtn+'" poheaderId="'+ bizPoHeader.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
							/*排产*/				scheduBtnTxt +'</div>'+
											'<div class="'+creatPayBtn+'" bizOrIdTxt="'+ bizOrId +'" bizReIdTxt="'+ bizReId +'">' +
							/*申请付款*/			creatPay +'</div>'+	
											'<div class="'+stastartCheckBtn+'" bizOrIdTxt="'+ bizOrId +'" bizReIdTxt="'+ bizReId +'" statuTxt="'+statu+'" sourceTxt="'+source+'">'+
							/*开启审核*/			stastartCheckBtnTxt +'</div>'+								
										'</div>' +
										'<div class="mui-input-row app_color40 app_text_center content_part operation" id="foot">' +
											'<div class="'+ordRecoveryBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
							/*恢复*/				staRecoveryBtnTxt +'</div>'+										
											'<div class="'+ordAmendBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
							/*修改*/				staAmendTxt +'</div>'+
											'<div class="'+ordDeleteBtn+'" staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
							/*删除*/				staDeleteTxt +'</div>'+
											'<div class="'+ordwaterCourseBtn+'"  staOrdId="'+ item.id +'" ordstatu="'+ res.data.statu +'">' +
							/*支付流水*/			ordwaterCourseBtnTxt +'</div>'+	
											'<div class="'+affirmScheduBtn+'" poheaderId="'+ bizPoHeader.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
							/*确认排产*/			affirmScheduBtnTxt +'</div>'+
										'</div>' +
										'<div class="mui-row app_color40 app_text_center content_part operation" id="foot">' +
											'<div class="'+applyListBtn+'" poheaderId="'+ bizPoHeader.id +'" bizoridtxt="'+ item.id +'" bizReIdTxt="'+ bizReId +'">' +
							/*付款单列表*/		applyListBtnTxt +'</div>'+
											'<div class="'+applyCheckBtn+'" poheaderId="'+ bizPoHeader.id +'" bizoridtxt="'+ item.id +'" bizReIdTxt="'+ bizReId +'">' +
							/*付款单审核*/		applyCheckBtnTxt +'</div>'+	
											'<div class="'+applyDetailBtn+'" poheaderId="'+ bizPoHeader.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
							/*付款单详情*/		applyDetailBtnTxt +'</div>'+
											'<div class="'+applyCandelBtn+'" poheaderId="'+ bizPoHeader.id +'" ordstatu="'+ res.data.statu +'">' +
							/*付款单取消*/		applyCandelBtnTxt +'</div>'+
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
        getPermissionList4: function (markVal,flag) {
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
        getPermissionList5: function (markVal,flag) {
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
                    _this.reCreatPayFlag = res.data;
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
                    _this.poCreatPayFlag = res.data;
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
                    _this.OrdFlagstartAudit = res.data;
                }
            });
        },
        getPermissionList01: function (markVal,flag) {
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
        getPermissionList02: function (markVal,flag) {
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
        getPermissionList03: function (markVal,flag) {
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
        getPermissionList04: function (markVal,flag) {
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
        ordHrefHtml: function() {
        	var _this = this;
			/*订单列表查询*/
    		var myStatu = $('#myStatu').val();
    		console.log(myStatu)
    		if(myStatu=='unline'){
    			/*客户专员线下支付查询*/
    			$('.app_header').on('tap', '#SearchBtn', function() {
    				alert('客户专员线下支付查询')
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
				/*线下支付查询*/
				$('.app_header').on('tap', '#OrdSechBtn', function() {
    				alert('线下支付查询')
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
    		}else{
    			/*客户专员订单查询*/
	    		$('.app_header').on('tap', '#SearchBtn', function() {
	    			alert('客户专员订单查询')
					var url = $(this).attr('url');
					if(url) {
						mui.toast('子菜单不存在')
					} else {
						GHUTILS.OPENPAGE({
							url: "../../../html/staffMgmtHtml/orderHtml/staOrdSech.html",
							extras:{
							}
						})
					}
				});
				/*订单查询*/
    			$('.app_header').on('tap', '#OrdSechBtn', function() {
    				alert('订单查询')
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
    		}

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
			})
			//客户专员审核
			$('.content_part').on('tap', '.waitCheckBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');//订单 ID
				console.log(staOrdId)
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdCheck.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
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
			})
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
			})
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
			/*付款单审核*/
	        $('.content_part').on('tap', '.applyCheckBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('bizReIdTxt');//备货单 ID
				var staOrdIdd = $(this).attr('bizoridtxt');//订单 ID
				var audit = 'audit', processPo = 'processPo';
				if(url) {
					mui.toast('子菜单不存在')
				}else if(staOrdIdd){
					GHUTILS.OPENPAGE({
						url: '../../../html/orderMgmtHtml/OrdermgmtHtml/orderCheck.html',
						extras: {
							staOrdId: staOrdIdd,
							str:'audit'//checkType: "auditSo"
						}
					})
				}
			})
			//付款单列表
			$('.content_part').on('tap', '.applyListBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('poheaderId');//采购单id
                var staOrderId = $(this).attr('bizoridtxt');//订单id
				if(staOrdId&&staOrderId) {
					GHUTILS.OPENPAGE({
						//订单						
						url: "../../../html/orderMgmtHtml/payApplyList.html",
						extras: {
							staOrdId: staOrdId,
							staOrderId:staOrderId,
							fromPage: 'orderHeader',
						}
					})
				}
			})
			/*付款单详情*/
			$('.content_part').on('tap', '.applyDetailBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('poheaderId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/orDetails.html",
						extras: {
							staOrdId: staOrdId,
							str: 'detail',
							fromPage: 'orderHeader',
						}
					})
				}
			})
			/*申请付款*/
			$('.content_part').on('tap', '.creatPayBtn', function() {
				var url = $(this).attr('url');
				var bizOrdId = $(this).attr('bizOrIdTxt');
				if(url) {
					mui.toast('子菜单不存在')
				}else if(bizOrdId == bizOrdId){
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
						extras: {
							staOrdId: bizOrdId,
							createPayStr: 'createPay',
						}
					})
				}
			})
			/*开启审核*/
	        $('.content_part').on('tap', '.stastartCheckBtn', function() {
				var url = $(this).attr('url');
				var bizOrdId = $(this).attr('bizOrIdTxt');
				var statuTxt = $(this).attr('statuTxt');
				var sourceTxt = $(this).attr('sourceTxt');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(bizOrdId == bizOrdId){
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
						extras: {
							staOrdId: bizOrdId,
							starStr: 'startAudit',
							statu:statuTxt,
							source:sourceTxt
						}
					})
				}
			})
			/*排产*/
			$('.content_part').on('tap', '.scheduBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('poheaderId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/orScheduling.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
			/*确认排产*/
			$('.content_part').on('tap', '.affirmScheduBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('poheaderId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/affirmScheduling.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
			/*付款单取消*/
            $('.content_part').on('tap','.applyCandelBtn',function(){
                var url = $(this).attr('url');
                var staordid = $(this).attr('poheaderId');
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
                                        url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
                                        extras: {

                                        }
                                    })
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

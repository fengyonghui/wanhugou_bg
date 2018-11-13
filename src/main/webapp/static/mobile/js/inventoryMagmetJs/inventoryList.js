
(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.detileFlag = "false";
		this.cancelAmendPayFlag = "false";
		this.cancelFlag = "false";
//		this.payFlag = "false";
		this.checkFlag = "false";
		this.OrdFlaginfo = "false";
		this.ordCreatPayFlag = "false";
		this.reCreatPayFlag = "false";
		this.poCreatPayFlag = "false";
		this.OrdFlagaudit = "false";
		this.OrdFlagpay = "false";
		this.OrdFlagstartAudit = "false";
		this.OrdFlagScheduling = "false";
		this.orCancAmenFlag = "false";
		this.affirmSchedulingFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
			this.getPermissionList('biz:request:bizRequestHeader:view','detileFlag')/*详情、订单支出信息总权限*/
			this.getPermissionList2('biz:request:bizRequestHeader:edit','cancelAmendPayFlag')/*备货单添加、取消、修改、付款、删除、恢复*/
			this.getPermissionList3('biz:request:bizRequestHeader:audit','checkFlag')/*审核*/
			this.getPermissionList4('biz:request:bizRequestHeader:delete','cancelFlag')/*删除*/
//			this.getPermissionList('biz:requestHeader:pay','payFlag')/*付款*/
			this.getPermissionList5('biz:po:bizPoHeader:view','OrdFlaginfo');//支出信息列表操作总权限
			this.getPermissionList6('biz:request:bizOrderHeader:createPayOrder','ordCreatPayFlag')//申请付款or
			this.getPermissionList7('biz:request:bizRequestHeader:createPayOrder','reCreatPayFlag')//申请付款re
			this.getPermissionList8('biz:po:bizPoHeader:createPayOrder','poCreatPayFlag')//申请付款po
			this.getPermissionList9('biz:po:bizPoHeader:audit','OrdFlagaudit');//付款单审核
			this.getPermissionList01('biz:po:pay:list','OrdFlagpay');//支付单列表
			this.getPermissionList02('biz:po:bizPoHeader:startAuditAfterReject','OrdFlagstartAudit');//开启审核
			this.getPermissionList03('biz:po:bizPoHeader:addScheduling','OrdFlagScheduling');//排产
			this.getPermissionList04('biz:po:bizPoHeader:edit','orCancAmenFlag')//修改、取消 false
			this.getPermissionList05('biz:po:bizPoHeader:confirmScheduling','affirmSchedulingFlag')//确认排产
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit:function(){
			var _this = this;
			_this.inInitHrefHtml();
			if(_this.cancelAmendPayFlag == false) {
				$('.inAddBtn').hide();
			}
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
			                },500);
			            }
			         },
			        down : {
			            height:50,
			            auto: true,
			            contentdown : "",
			            contentover : "",
			            contentrefresh : "正在加载...",
			            callback :function(){ 			            	
			            	var f = document.getElementById("list");
			                var childs = f.childNodes;
			                for(var i = childs.length - 1; i >= 0; i--) {
			                    f.removeChild(childs[i]);
			                }
			            	if(_this.userInfo.isFunc){
			            		if(_this.userInfo.reqNo==undefined){
									_this.userInfo.reqNo="";
								}
			            		var nameTxt = '';
								if(_this.userInfo.name) {
									nameTxt = decodeURIComponent(_this.userInfo.name)
								}else {
									nameTxt = ''
								}
			            		if(_this.userInfo.fromType==undefined){
									_this.userInfo.fromType="";
								}
			            		if(_this.userInfo.fromOffice==undefined){
									_this.userInfo.fromOffice="";
								}
			            		if(_this.userInfo.bizStatusid==undefined){
									_this.userInfo.bizStatusid="";
								}
			            		if(_this.userInfo.varietyInfoid==undefined){
									_this.userInfo.varietyInfoid="";
								}
			            		if(_this.userInfo.process==undefined){
									_this.userInfo.process="";
								}
			            		if(_this.userInfo.includeTestData==undefined){
									_this.userInfo.includeTestData="";
								}
			            		var namePoTxts = '';
								if(_this.userInfo.processTypeStr) {
									namePoTxts = decodeURIComponent(_this.userInfo.processTypeStr)
								}else {
									namePoTxts = ''
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
			            		pager['size']= 20;
		                    	pager['pageNo'] = 1;
		                    	pager['reqNo'] = _this.userInfo.reqNo;//备货单号
		                    	pager['name'] = nameTxt;//供应商
		                    	pager['fromType'] = _this.userInfo.fromType;//备货方
		                    	pager['fromOffice.id'] = _this.userInfo.fromOffice;//采购中心
		                    	pager['bizStatus'] = _this.userInfo.bizStatusid;//业务状态
		                    	pager['varietyInfo.id'] = _this.userInfo.varietyInfoid;//品类名称
		                    	pager['process'] = _this.userInfo.process;//审核状态
		                    	pager['poBizStatus'] = _this.userInfo.poBizStatus,//付款单业务状态
								pager['processTypeStr'] = namePoTxts,//付款单审核状态
								pager['poSchType'] = _this.userInfo.poSchType,//付款单排产状态
								pager['poWaitPay'] = _this.userInfo.poWaitPay,//付款单待支付
		                    	pager['includeTestData'] = _this.userInfo.includeTestData;//测试数据
		                    	getData(pager);
			            	}else{
			            		pager['size']= 20;
			                    pager['pageNo'] = 1;				                
				                $('.mui-pull-caption-down').html('');				                
				                getData(pager);
			            	}
			                    
			            }
			        }
			    })
		    }
		    function getData(params){
		    	var inPHtmlList = '';
		        mui.ajax("/a/biz/request/bizRequestHeaderForVendor/list4MobileNew",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	console.log(res)
			            var returnData = res.data.page.list;
			            var dataRow = res.data.roleSet;
						var arrLen = res.data.page.list.length; 
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
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
						//备货方:
						var arrbss = [];
						var stock = '';
						$.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "req_from_type"},
			                async:false,
			                success: function(bss){                 
								arrbss = bss
			                }
			            });	
						/*业务状态*/
						var arrass = [];
						var bizstatusTxt = '';
						$.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "biz_req_status"},
			                async:false,
			                success: function(ass){                 
					            arrass = ass
			                }
			            });
                        if(arrLen > 0) {
							$.each(returnData, function(i, item) {
								$.each(arrbss, function(b, bs) {
									if(bs.value==item.fromType) {
										stock = bs.label
									}
								})
								$.each(arrass, function(a, as) {
					               	if(as.value==item.bizStatus) {
					               		bizstatusTxt = as.label
					               	}
				               	});
								/*审核按钮*/	
								var inCheck = '';
								var inCheckBtn='';
								var requestOrderProcess = '';
								if(item.commonProcess.requestOrderProcess) {
									requestOrderProcess = item.commonProcess.requestOrderProcess
								}
								var purchaseOrderProcess = '';
								if(item.bizPoHeader.commonProcess) {
									purchaseOrderProcess = item.bizPoHeader.commonProcess.purchaseOrderProcess
								}
								if(_this.checkFlag == true) {
									var DataRoleGener = '';
									if(item.commonProcess) {
										DataRoleGener = requestOrderProcess.roleEnNameEnum;
									}
									var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
									if(item.commonProcess && fileRoleData.length>0 && requestOrderProcess.name != '驳回') {
										inCheck = '审核'
										inCheckBtn='inCheckBtn'
									}else {
										inCheck = ''
										inCheckBtn=''
									}
								}
//                              付款
//								var inPay = '';
//								var inPayBtn = '';
								/*修改*/
								var inAmend = '';
								var inAmendBtn = '';
								/*取消*/
								var inCancel = '';
								var inCancelBtn = '';
								/*删除*/
								var reDeleteBtn	= '';
								var reDeleteBtnTxt = '';
								/*恢复*/
								var recoverBtn = '';
								var recoverBtnTxt = '';
								if(_this.cancelAmendPayFlag == true){
									if(userId == 1) {
										/*修改按钮*/
										if(item.delFlag!=null && item.delFlag!=0) {
											inAmend = '修改'
											inAmendBtn = 'inAmendBtn'
											/*删除*/
											reDeleteBtn = 'reDeleteBtn'
											reDeleteBtnTxt = '删除'
											/*恢复*/
											if(item.delFlag!=null && item.delFlag==0) {
												recoverBtn = 'recoverBtn';
												recoverBtnTxt = '恢复';
											}
											/*取消按钮*/
											if(item.bizStatus !=40) {
												inCancel = '取消'
												inCancelBtn = 'inCancelBtn'
											}else {
												inCancel = ''
												inCancelBtn = ''
											}
										}else {
											inAmendBtn = ''
											inAmend = ''
										}
									}
									if(userId != 1 && item.bizStatus < 4 ||(requestOrderProcess.name == '驳回' && userId == item.createBy.id) ||(purchaseOrderProcess.name == '驳回' && userId == item.createBy.id)) {
										inAmend = '修改'
										inAmendBtn = 'inAmendBtn'
										/*删除按钮*/	
										if(_this.cancelFlag == true) {
											reDeleteBtn = 'reDeleteBtn'
											reDeleteBtnTxt = '删除'
										}
										inCancel = '取消'
										inCancelBtn = 'inCancelBtn'
									}
									/*付款按钮*/
//									if(_this.payFlag == true) {
////									<c:if test="${requestHeader.bizStatus!=ReqHeaderStatusEnum.CLOSE.state && requestHeader.totalDetail != requestHeader.recvTotal}">
//										if(item.bizStatus !=40 && item.totalDetail != item.recvTotal) {
//											inPay = '付款'
//											inPayBtn = 'inPayBtn'
//										}else {
//											inPay = ''
//											inPayBtn = ''
//										}								
//									}
								}
							//详情
								var inDetail = '';
								var inDetailBtn = '';
								if(_this.detileFlag == true) {
									inDetail = '详情'
									inDetailBtn = 'inDetailBtn'
								}else {
									inDetail = ''
									inDetailBtn = ''
								}
							/*品类名称*/	
								var varietyInfoName = '';
								if(item.varietyInfo.name) {
									varietyInfoName = item.varietyInfo.name
								}else {
									varietyInfoName = ''
								}
							/*审核状态*/		
								var checkStatus = '';
								if(item.bizStatus != res.data.closeState) {
									if(requestOrderProcess.name != '审核完成') {
										checkStatus = requestOrderProcess.name
									}
									if(requestOrderProcess.name == '审核完成') {
										if(purchaseOrderProcess==""){
											checkStatus=""
										}else{
											checkStatus = purchaseOrderProcess.name
										}										
									}
								}
					/*----------订单支出信息合并----------*/
								var bizReId = '';
								/*付款单列表*/
								var applyListBtn = '';
								var applyListBtnTxt = '';
								/*付款单审核*/  
								var applyCheckBtn = '';
								var applyCheckBtnTxt = '';
								/*付款单取消*/
								var applyCandelBtn = '';
								var applyCandelBtnTxt = '';
								/*付款单修改*/
								var orAmendBtn = '';
								var orAmendBtnTxt = '';
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
                				var bizPoHeader = item.bizPoHeader;
                				if(_this.OrdFlaginfo == true) {
	                				if(_this.detileFlag == true) {
	//              					console.log(bizPoHeader)
	                					if(bizPoHeader.id) {
	                						var paycheckTxt = '';
			                                var purchCode = '';
				                            if(bizPoHeader.commonProcess){
				                            	if(bizPoHeader.commonProcess.purchaseOrderProcess.name){
				                            		paycheckTxt=bizPoHeader.commonProcess.purchaseOrderProcess.name;
				                            		purchCode = bizPoHeader.commonProcess.purchaseOrderProcess.code;
				                            	}
				                            }
	                						/*财务审核采购单按钮控制*/
	                						if(_this.OrdFlagaudit == true) {
												var DataRoleGener = '';
												if(bizPoHeader.commonProcess) {
													DataRoleGener = bizPoHeader.commonProcess.purchaseOrderProcess.roleEnNameEnum;
												}
												var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
						                        if((bizPoHeader.commonProcess.id) && paycheckTxt != '驳回'
						                        	&& paycheckTxt != '审批完成' && purchCode != res.data.payStatus
						                        	&& (fileRoleData || userId==1)){
					                        	   		applyCheckBtnTxt = '付款单审核';
					                        	   		applyCheckBtn = 'applyCheckBtn';
					                        	   		bizReId = item.id;
						                        }
							                }
	                						/*支付申请列表获取*/
	                						if(bizPoHeader.commonProcess.type != -1){
					                			if(_this.OrdFlagpay == true){
					                				applyListBtn = 'applyListBtn';
													applyListBtnTxt = '付款单列表';
													bizReId = bizPoHeader.bizRequestHeader.id;
					                			}
						                	}
	                						/*驳回的单子再次开启审核*/
							                if(_this.OrdFlagstartAudit==true){
						                		if(bizPoHeader.commonProcess.type == -1){
						                			if(bizPoHeader.bizRequestHeader) {
						                				stastartCheckBtnTxt = '开启审核';
						                				stastartCheckBtn = 'stastartCheckBtn';
						                				bizReId = bizPoHeader.bizRequestHeader.id;
						                			}
						                		}
						                	}
							                /*付款单修改、取消*/
							               console.log(_this.orCancAmenFlag)
	                						if(_this.orCancAmenFlag == true) {
						                		if(paycheckTxt == null || paycheckTxt == ''
					                			|| paycheckTxt == '驳回') {
						                			orAmendBtn = 'orAmendBtn';
						                			orAmendBtnTxt = '付款单修改';
						                		}
					                			applyCandelBtn = 'applyCandelBtn';
												applyCandelBtnTxt = '付款单取消';
						                	}
	                						//付款单详情
						                	if(_this.OrdFlaginfo == true) {
												applyDetailBtn = 'applyDetailBtn';
												applyDetailBtnTxt = '付款单详情';
												/*排产，确认排产*/
												if(_this.OrdFlagScheduling==true) {
													scheduBtn = 'scheduBtn';
													scheduBtnTxt = '排产';
												}
												if(_this.affirmSchedulingFlag == true) {
							                		affirmScheduBtn = 'affirmScheduBtn';
													affirmScheduBtnTxt = '确认排产';
							                	}
											}
	                					}
	                				}
	                			}
								inPHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
									'<div class="mui-input-row">' +
										'<label>备货单号:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.reqNo+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>业务状态:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+bizstatusTxt+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>审核状态:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>备货方:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+stock+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>供应商:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.name +' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>下单时间:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>品类名称:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+varietyInfoName+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>申请人:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.createBy.name+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>更新时间:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
									'</div>' +
									'<div class="mui-input-row app_color40 app_text_center content_part operation" id="foot">' +

							/*审核*/		'<div class="'+ inCheckBtn +'" inListId="'+ item.id +'" bizStatus="'+item.bizStatus+'">' +
											inCheck +'</div>'+										
							/*取消*/		'<div class="'+inCancelBtn+'" inListId="'+ item.id +'">' +
											inCancel +'</div>'+										
							/*修改*/		'<div class="'+inAmendBtn+'" inListId="'+ item.id +'">' +
											inAmend +'</div>'+
							/*删除*/		'<div class="'+reDeleteBtn+'" staOrdId="'+ item.id +'">' +
											reDeleteBtnTxt +'</div>'+
							/*恢复*/		'<div class="'+recoverBtn+'" staOrdId="'+ item.id +'">' +
											recoverBtnTxt +'</div>'+
							/*排产*/		'<div class="'+scheduBtn+'" poheaderId="'+ bizPoHeader.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
											scheduBtnTxt +'</div>'+											
							/*详情*/		'<div class="'+inDetailBtn+'" inListId="'+ item.id +'">' +
											inDetail +'</div>'+											
									'</div>' +
									'<div class="mui-input-row app_color40 app_text_center content_part operation" id="foot">' +
						/*付款单修改*/	'<div class="'+orAmendBtn+'" poheaderId="'+ bizPoHeader.id +'">' +
											orAmendBtnTxt +'</div>'+										
							/*申请付款*/	'<div class="'+creatPayBtn+'" bizReIdTxt="'+ bizReId +'">' +
											creatPay +'</div>'+	
							/*确认排产*/	'<div class="'+affirmScheduBtn+'" poheaderId="'+ bizPoHeader.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
											affirmScheduBtnTxt +'</div>'+											
							/*开启审核*/	'<div class="'+stastartCheckBtn+'" bizReIdTxt="'+ bizReId +'">'+
											stastartCheckBtnTxt +'</div>'+											
									'</div>' +
									'<div class="mui-row app_color40 app_text_center content_part operation" id="foot">' +
						/*付款单审核*/	'<div class="'+applyCheckBtn+'" bizReIdTxt="'+ item.id +'">' +
											applyCheckBtnTxt +'</div>'+										
						/*付款单列表*/	'<div class="'+applyListBtn+'" poheaderId="'+ bizPoHeader.id +'" bizReIdTxt="'+ item.id +'">' +
											applyListBtnTxt +'</div>'+
						/*付款单详情*/	'<div class="'+applyDetailBtn+'" poheaderId="'+ bizPoHeader.id +'" ordstatu="'+ res.data.statu +'" ordsource="'+ item.source +'">' +
											applyDetailBtnTxt +'</div>'+
						/*付款单取消*/	'<div class="'+applyCandelBtn+'" poheaderId="'+ bizPoHeader.id +'" ordstatu="'+ res.data.statu +'">' +
											applyCandelBtnTxt +'</div>'+
									'</div>' +
								'</div>'
							});
							$('#list').append(inPHtmlList);
							_this.inHrefHtml()
						} else {
							$('.mui-pull-caption').html('');
							$('#list').append('<p class="noneTxt">暂无数据</p>');
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
                    _this.detileFlag = res.data;
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
					_this.cancelAmendPayFlag = res.data;
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
					_this.checkFlag = res.data;
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
					_this.cancelFlag = res.data;
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
                    _this.OrdFlaginfo = res.data;
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
                    _this.OrdFlagaudit = res.data;
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
                    _this.OrdFlagpay = res.data;
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
                    _this.OrdFlagstartAudit = res.data;
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
                    _this.OrdFlagScheduling = res.data;
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
                    _this.orCancAmenFlag = res.data;
                }
            });
        },
        getPermissionList05: function (markVal,flag) {
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
        inInitHrefHtml: function() {
        	var _this = this;
        	/*备货单添加*/
			$('#nav').on('tap','.inAddBtn', function() {
				if(_this.cancelAmendPayFlag == true) {
					var url = $(this).attr('url');
					GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inventoryAddList.html",
						extras: {
							
						}
					})
				}
			})
			/*查询*/
			$('.app_header').on('tap', '#searchBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inSearch.html",
						extras:{
						}
					})
				}
			})
			/*首页*/
			$('#nav').on('tap','.inHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			})
        },
		inHrefHtml: function() {
			var _this = this;
		/*详情*/
			$('.content_part').on('tap', '.inDetailBtn', function() {
				var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(inListId == inListId) {
					GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inventoryDetails.html",
						extras: {
							inListId: inListId,
						}
					})
				}
			})
		/*修改*/
            $('.content_part').on('tap','.inAmendBtn', function() {
				var url = $(this).attr('url');
                var reqId = $(this).attr('inListId');
				GHUTILS.OPENPAGE({
					url: "../../html/inventoryMagmetHtml/inventoryAmend.html",
					extras: {
                        reqId: reqId,
					}
				})
			})
        /*付款*/
//	       $('.content_part').on('tap', '.inPayBtn', function() {
//					var url = $(this).attr('url');
//					var inListId = $(this).attr('inListId');
//					if(url) {
//						mui.toast('子菜单不存在')
//					} else if(inListId == inListId) {
//						GHUTILS.OPENPAGE({
//							url: "../../html/inventoryMagmetHtml/inPay.html",
//							extras: {
//								inListId: inListId,
//							}
//						})
//					}
//				}),
        /*审核*/
            $('.content_part').on('tap','.inCheckBtn',function(){
            	var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				var bizStatus = $(this).attr('bizStatus');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(inListId==inListId) {
                	GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inCheck.html",
						extras: {
								inListId:inListId,
								bizStatus:bizStatus,
						}
					})
                }
			})
		/*取消*/	
            $('.content_part').on('tap','.inCancelBtn',function(){
            	var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(inListId==inListId) {
                	var btnArray = ['取消', '确定'];
					mui.confirm('您确认取消该备货单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "POST",
				                url: "/a/biz/request/bizRequestHeaderForVendor/saveInfo",
				                data: {id:inListId,checkStatus:'40'},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	GHUTILS.OPENPAGE({
										url: "../../html/inventoryMagmetHtml/inventoryList.html",
										extras: {
												inListId:inListId,
										}
									})
			                	}
			            	})
						}else {
							
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
						url: "../../html/orderMgmtHtml/orScheduling.html",
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
						url: "../../html/orderMgmtHtml/affirmScheduling.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})
			/*付款单审核*/
	        $('.content_part').on('tap', '.applyCheckBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('bizReIdTxt');//备货单 ID
				var audit = 'audit', processPo = 'processPo';
				if(url) {
					mui.toast('子菜单不存在')
				}else if(staOrdId) {
					//备货单
					GHUTILS.OPENPAGE({
						url: '../../html/inventoryMagmetHtml/inCheck.html',
						extras: {
							staOrdIds: staOrdId,
							audits:audit,
							processPos:processPo
						}
					})
				}
			})
			//付款单列表
			$('.content_part').on('tap', '.applyListBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('poheaderId');//采购单id
                var staInvenId = $(this).attr('bizReIdTxt');//备货单id
				if(staInvenId == staInvenId){
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
			})
			/*付款单详情*/
			$('.content_part').on('tap', '.applyDetailBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('poheaderId');
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
			/*付款单修改*/
			$('.content_part').on('tap', '.orAmendBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('poheaderId');
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
                                        url: "../../html/inventoryMagmetHtml/inventoryList.html",
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
			/*申请付款*/
			$('.content_part').on('tap', '.creatPayBtn', function() {
				var url = $(this).attr('url');
				var bizReqId = $(this).attr('bizReIdTxt');
				if(url) {
					mui.toast('子菜单不存在')
				}else if(bizReqId == bizReqId) {
					GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inventoryAmend.html",
						extras: {
							staOrdId: bizReqId,
							createPayStr: 'createPay',
						}
					})
				}
			})
			/*开启审核*/
	        $('.content_part').on('tap', '.stastartCheckBtn', function() {
				var url = $(this).attr('url');
				var bizReqId = $(this).attr('bizReIdTxt');
				if(url) {
					mui.toast('子菜单不存在')
				}else if(bizReqId == bizReqId) {
					GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inventoryAmend.html",
						extras: {
							staOrdId: bizReqId,
							starStr: 'startAudit',
						}
					})
				}
			})
	        /*恢复*/
			$('.content_part').on('tap', '.recoverBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认删除该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/request/bizRequestHeader/recovery4Mobile",
				                data: {id:staOrdId},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../html/inventoryMagmetHtml/inventoryList.html",
											extras: {
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
	        //删除
			$('.content_part').on('tap', '.reDeleteBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认删除该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/request/bizRequestHeader/delete4Mobile",
				                data: {id:staOrdId},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../html/inventoryMagmetHtml/inventoryList.html",
											extras: {
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
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);


(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.detileFlag = "false"
		this.cancelAmendPayFlag = "false"
		this.cancelFlag = "false"
//		this.payFlag = "false"
		this.checkFlag = "false"
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
//			biz:request:bizRequestHeader:view   详情
//			biz:request:bizRequestHeader:edit   备货单添加、取消、修改、付款、删除、恢复
//			biz:request:bizRequestHeader:delete  删除、
//			biz:requestHeader:pay		付款
//			biz:request:bizRequestHeader:audit		审核
			this.getPermissionList('biz:request:bizRequestHeader:view','detileFlag')
			this.getPermissionList2('biz:request:bizRequestHeader:edit','cancelAmendPayFlag')
//			this.getPermissionList('biz:requestHeader:pay','payFlag')
			this.getPermissionList3('biz:request:bizRequestHeader:audit','checkFlag')
//			this.getPermissionList('biz:request:bizRequestHeader:delete','cancelFlag')
			if(this.userInfo.isFunc){
				this.seachFunc()
			}else{
				this.pageInit(); //页面初始化
			}
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit:function(){
			var _this = this;
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
			                    pager['size']= 20;
			                    pager['pageNo'] = 1;
				                var f = document.getElementById("list");
				                var childs = f.childNodes;
				                for(var i = childs.length - 1; i >= 0; i--) {
				                    f.removeChild(childs[i]);
				                }
//				                console.log('222')
//				                console.log(pager)
				                $('.mui-pull-caption-down').html('');				                
				                getData(pager);
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
//		          	    console.log(res)
//		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
			            var returnData = res.data.page.list;
			            var dataRow = res.data.roleSet;
						var arrLen = res.data.page.list.length; 
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
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
//					            console.log(user)
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
//					            console.log(bss)
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
//					            console.log(ass)
					            arrass = ass
			                }
			            });
                        if(arrLen > 0) {
							$.each(returnData, function(i, item) {
//								console.log(item.commonProcess.requestOrderProcess)
//								console.log(item)
								$.each(arrbss, function(b, bs) {
									if(bs.value==item.fromType) {
										stock = bs.label
									}
								})
//								console.log(arrass)
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
//								<c:if test="${(fns:hasRole(roleSet, requestHeader.commonProcess.requestOrderProcess.roleEnNameEnum)) && requestHeader.commonProcess.requestOrderProcess.name != '驳回'
//							&& requestHeader.commonProcess.requestOrderProcess.code != auditStatus
//							}">
									var DataRoleGener = '';
									if(item.commonProcess) {
										DataRoleGener = requestOrderProcess.roleEnNameEnum;
									}
									var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
//									console.log(fileRoleData)
									if(item.commonProcess && fileRoleData.length>0 && requestOrderProcess.name != '驳回') {
										inCheck = '审核'
										inCheckBtn='inCheckBtn'
									}else {
										inCheck = ''
										inCheckBtn=''
									}
								}
								//取消、修改、付款
//								var inPay = '';
//								var inPayBtn = '';
								var inAmend = '';
								var inAmendBtn = '';
								var inCancel = '';
								var inCancelBtn = '';
								if(_this.cancelAmendPayFlag == true){
									if(userId == 1) {
										/*修改按钮*/
//								requestHeader.delFlag!=null && requestHeader.delFlag!=0
										if(item.delFlag!=null && item.delFlag!=0) {
											inAmend = '修改'
											inAmendBtn = 'inAmendBtn'
											/*删除*/
											/*恢复*/
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
//										if(_this.cancelFlag == true) {
//										}else {
//										}
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
								if(requestOrderProcess) {
									if(requestOrderProcess.name != '审核完成') {
										checkStatus = requestOrderProcess.name
									}
									if(requestOrderProcess.name == '审核完成') {
										checkStatus = '订单支出信息审核'
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
									'<div class="app_color40 mui-row app_text_center operation">' +
										'<div class="mui-col-xs-2">' +
											'<li class="mui-table-view-cell" ></li>' +
										'</div>'+
										'<div class="mui-col-xs-2 '+inDetailBtn+'" inListId="'+ item.id +'">' +
											'<li class="mui-table-view-cell" >'+inDetail+'</li>' +
										'</div>' +
										'<div class="mui-col-xs-2 '+inAmendBtn+'" inListId="'+ item.id +'">' +
											'<li class="mui-table-view-cell">'+inAmend+'</li>' +
										'</div>' +
										'<div class="mui-col-xs-2 '+inCancelBtn+'" inListId="'+ item.id +'">' +
											'<li class="mui-table-view-cell"> '+inCancel+'</li>' +
										'</div>'+
										'<div class="mui-col-xs-2"  inListId="'+ item.id +'">' +
											'<li class="mui-table-view-cell"></li>' +
										'</div>'+
										'<div class="mui-col-xs-2 '+inCheckBtn+'" inListId="'+ item.id +'"  bizStatus="'+item.bizStatus+'">' +
											'<li class="mui-table-view-cell">'+ inCheck +'</li>' +
										'</div>'+
									'</div>' +
								'</div>'
							});
							$('#list').append(inPHtmlList);
							_this.inHrefHtml()
						} else {
							$('.mui-pull-caption').html('');
							$('#list').append('<p class="noneTxt">暂无数据</p>');
						}
//						totalPage = res.data.page.count%pager.size!=0?
//		                parseInt(res.data.page.count/pager.size)+1:
//		                res.data.page.count/pager.size;
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
//					console.log(_this.cancelAmendPayFlag)
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
		inHrefHtml: function() {
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
		/*详情*/
			$('#list').on('tap', '.inDetailBtn', function() {
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
            $('#list').on('tap','.inAmendBtn', function() {
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
//	       $('#list').on('tap', '.inPayBtn', function() {
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
        /* 审核*/
            $('#list').on('tap','.inCheckBtn',function(){
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
            $('#list').on('tap','.inCancelBtn',function(){
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
		seachFunc:function(){
			var _this = this;
			var inPHtmlList = '';
			//解码
			var nameTxt = '';
			if(_this.userInfo.name) {
				nameTxt = decodeURIComponent(_this.userInfo.name)
			}else {
				nameTxt = ''
			}
			
			$.ajax({
				type: 'GET',
				url: '/a/biz/request/bizRequestHeaderForVendor/list4MobileNew',
				data: {
					pageNo: 1,
					reqNo:_this.userInfo.reqNo,
					name:nameTxt,
					fromType:_this.userInfo.fromType,
					'fromOffice.id':_this.userInfo.fromOffice,
					bizStatus:_this.userInfo.bizStatusid,
					'varietyInfo.id':_this.userInfo.varietyInfoid,
					includeTestData: _this.userInfo.includeTestData,
					process:_this.userInfo.process
				},
				dataType: 'json',
				success: function(res) {
					console.log(res)
//		            mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
		            var returnData = res.data.page.list;
		            var dataRow = res.data.roleSet;
					var arrLen = res.data.page.list.length; 
					if(arrLen <20 ){
						mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
					}else{
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
				            console.log(user)
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
//					            console.log(bss)
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
//					            console.log(ass)
				            arrass = ass
		                }
		            });
                    if(arrLen > 0) {
						$.each(returnData, function(i, item) {
//								console.log(item)
//								console.log(arrbss)
							$.each(arrbss, function(b, bs) {
								if(bs.value==item.fromType) {
									stock = bs.label
								}
							})
//								console.log(arrass)
							$.each(arrass, function(a, as) {
				               	if(as.value==item.bizStatus) {
				               		bizstatusTxt = as.label
				               	}
			               	});
							/*审核按钮*/	
							var inCheck = '';
							var inCheckBtn='';
							if(_this.checkFlag == true) {
								var requestOrderProcess = '';
								if(item.commonProcess.requestOrderProcess) {
									requestOrderProcess = item.commonProcess.requestOrderProcess
								}
								var purchaseOrderProcess = '';
								if(item.bizPoHeader) {
									purchaseOrderProcess = item.bizPoHeader.commonProcess.purchaseOrderProcess
								}
	//								<c:if test="${(fns:hasRole(roleSet, requestHeader.commonProcess.requestOrderProcess.roleEnNameEnum)) && requestHeader.commonProcess.requestOrderProcess.name != '驳回'
	//							&& requestHeader.commonProcess.requestOrderProcess.code != auditStatus
	//							}">
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
							//取消、修改、付款
//							var inPay = '';
//							var inPayBtn = '';
							var inAmend = '';
							var inAmendBtn = '';
							var inCancel = '';
							var inCancelBtn = '';
							if(_this.cancelAmendPayFlag == true){
								if(userId == 1) {
									/*修改按钮*/
	//								requestHeader.delFlag!=null && requestHeader.delFlag!=0
									if(item.delFlag!=null && item.delFlag!=0) {
										inAmend = '修改'
										inAmendBtn = 'inAmendBtn'
										/*删除*/
										/*恢复*/
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
	//										if(_this.cancelFlag == true) {
	//										}else {
	//										}
									inCancel = '取消'
									inCancelBtn = 'inCancelBtn'
								}
								/*付款按钮*/
//								if(_this.payFlag == true) {
//	//									<c:if test="${requestHeader.bizStatus!=ReqHeaderStatusEnum.CLOSE.state && requestHeader.totalDetail != requestHeader.recvTotal}">
//									if(item.bizStatus !=40 && item.totalDetail != item.recvTotal) {
//										inPay = '付款'
//										inPayBtn = 'inPayBtn'
//									}else {
//										inPay = ''
//										inPayBtn = ''
//									}								
//								}
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
							if(requestOrderProcess) {
								if(requestOrderProcess.name != '审核完成') {
									checkStatus = requestOrderProcess.name
								}
								if(requestOrderProcess.name == '审核完成') {
									checkStatus = '订单支出信息审核'
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
							'<div class="app_color40 mui-row app_text_center operation">' +
								'<div class="mui-col-xs-2">' +
									'<li class="mui-table-view-cell" ></li>' +
								'</div>'+
								'<div class="mui-col-xs-2 '+inDetailBtn+'" inListId="'+ item.id +'">' +
									'<li class="mui-table-view-cell" >'+inDetail+'</li>' +
								'</div>' +
								'<div class="mui-col-xs-2 '+inAmendBtn+'" inListId="'+ item.id +'">' +
									'<li class="mui-table-view-cell">'+inAmend+'</li>' +
								'</div>' +
								'<div class="mui-col-xs-2 '+inCancelBtn+'" inListId="'+ item.id +'">' +
									'<li class="mui-table-view-cell"> '+inCancel+'</li>' +
								'</div>'+
								'<div class="mui-col-xs-2"  inListId="'+ item.id +'">' +
									'<li class="mui-table-view-cell"></li>' +
								'</div>'+
								'<div class="mui-col-xs-2 '+inCheckBtn+'" inListId="'+ item.id +'"  bizStatus="'+item.bizStatus+'">' +
									'<li class="mui-table-view-cell">'+ inCheck +'</li>' +
								'</div>'+
							'</div>' +
						'</div>'
						});
						$('#list').append(inPHtmlList);
					}else{
						$('#list').append('<p class="noneTxt">暂无数据</p>');
					}
				}
			});
		_this.inHrefHtml()	
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

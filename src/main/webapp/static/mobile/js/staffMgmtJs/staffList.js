(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.staffFlag = "false"
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
//	权限		sys:user:edit   用户列表,用户添加，关联经销店,订单管理,修改,删除,恢复
			this.getPermissionList('sys:user:edit','staffFlag')
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
//			                window.setTimeout(function(){
			                    getData(pager);
//			                },500);
			            }
			         },
			        down : {
			            height:50,
			            auto: true,
			            contentdown : "",
			            contentover : "",
			            contentrefresh : "正在加载...",
			            callback :function(){ 
		                    pager['size']= 10;
		                    pager['pageNo'] = 1;
		                    pager['company.type'] = 8;
		                    pager['company.customerTypeTen'] = 10;
		                    pager['company.customerTypeEleven'] = 11;
		                    pager['conn'] ="connIndex"
			                var f = document.getElementById("staffList");
			                var childs = f.childNodes;
			                for(var i = childs.length - 1; i >= 0; i--) {
			                    f.removeChild(childs[i]);
			                }
//			                console.log('222')
//			                console.log(pager)
			                $('.mui-pull-caption-down').html('');
			                if(_this.staffFlag == true) {
			                	getData(pager);
			                }
			            }
			        }
			    })
		    }
	    	function getData(params){
		    	var staffHtmlList = '';
		        mui.ajax("/a/sys/user/listData4mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	console.log(res)
		                mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						var arrLen = res.data.page.list.length;						
                        if(arrLen > 0) {
                        $.each(res.data.page.list, function(i, item) {
									staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row">' +
											'<label>归属公司:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.company.name+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>归属部门:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.office.name+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>登录名:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.loginName+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>姓名:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.name +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>手机:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.mobile+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>洽谈数:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value="'+item.userOrder.officeChatRecord+'">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>新增订单量:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value="'+item.userOrder.orderCount+'">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>新增回款额:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value="'+item.userOrder.userOfficeReceiveTotal+'">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>新增会员:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value="'+item.userOrder.officeCount+'">' +
										'</div>' +
										'<div class="app_font_cl content_part mui-row app_text_center">' +
											'<div class="mui-col-xs-6 staRelevanBtn" staListId="'+ item.id +'" dptmtId="'+ item.office.id +'">' +
												'<li class="mui-table-view-cell">关联经销店</li>' +
											'</div>' +
											'<div class="mui-col-xs-6 staOrdBtn" staListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">订单管理</li>' +
											'</div>'+
//											'<div class="mui-col-xs-3 staAmendBtn" staListId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">修改</li>' +
//											'</div>'+
//											'<div class="mui-col-xs-2 staDeletBtn" staListId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">删除</li>' +
//											'</div>'+
										'</div>' +
									'</div>'
								});
								$('#staffList').append(staffHtmlList);
								_this.stHrefHtml()
						} else {
								$('.mui-pull-caption').html('');
							}
						totalPage = res.data.page.count%pager.size!=0?
		                parseInt(res.data.page.count/pager.size)+1:
		                res.data.page.count/pager.size;
		                if(totalPage==pager.pageNo){		                	
			                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
//			                mui('#refreshContainer').pullRefresh().disablePullupToRefresh();
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
                    _this.staffFlag = res.data;
                }
            });
        },
		stHrefHtml: function() {
			var _this = this;
		/*查询*/
			$('.app_header').on('tap', '#staffSearchBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../html/staffMgmtHtml/staSearch.html",
						extras:{
						}
					})
				}
			}),
		/*用户添加*/
//			$('#nav').on('tap','.staAddBtn', function() {
//				var url = $(this).attr('url');
//				GHUTILS.OPENPAGE({
//					url: "../../html/staffMgmtHtml/staffAdd.html",
//					extras: {
//						
//					}
//				})
//			}),
		/*首页*/
			$('#nav').on('tap','.staHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			}),
		/*关联经销店*/
			$('.app_content').on('tap', '.staRelevanBtn', function() {
				var url = $(this).attr('url');
				var staListId = $(this).attr('staListId');
				var dptmtId = $(this).attr('dptmtId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staListId == staListId) {
					GHUTILS.OPENPAGE({
						url: "../../html/staffMgmtHtml/relevanceHtml/relList.html",
						extras: {
							staListId: staListId,
							dptmtId: dptmtId,
						}
					})
				}
			}),
		/*订单管理*/
            $('.app_content').on('tap','.staOrdBtn', function() {
				var url = $(this).attr('url');
                var staListId = $(this).attr('staListId');
				GHUTILS.OPENPAGE({
					url: "../../html/staffMgmtHtml/orderHtml/staOrderList.html",
					extras: {
                        staListId: staListId,
					}
				})
			})
        /*修改*/
//	       $('.app_content').on('tap', '.staAmendBtn', function() {
//					var url = $(this).attr('url');
//					var staListId = $(this).attr('staListId');
//					if(url) {
//						mui.toast('子菜单不存在')
//					} else if(staListId == staListId) {
//						GHUTILS.OPENPAGE({
//							url: "../../html/staffMgmtHtml/staffAmend.html",
//							extras: {
//								staListId: staListId,
//							}
//						})
//					}
//				}),
		/*删除*/	
//          $('.app_content').on('tap','.staDeletBtn',function(){
//          	var url = $(this).attr('url');
//				var staListId = $(this).attr('staListId');
//              if(url) {
//              	mui.toast('子菜单不存在')
//              }else if(staListId==staListId) {
//              	var btnArray = ['取消', '确定'];
//					mui.confirm('您确定要删除该用户吗？', '系统提示！', btnArray, function(choice) {
//						if(choice.index == 1) {
//							$.ajax({
//				                type: "GET",
//				                url: "",
//				                data: {id:staListId},
//				                dataType: "json",
//				                success: function(res){
//				                	alert('操作成功！')
//				                	GHUTILS.OPENPAGE({
//										url: "../../html/staffMgmtHtml/staffList.html",
//										extras: {
//												staListId:staListId,
//										}
//									})
//			                	}
//			            	})
//						}else {
//							
//						}
//					})	
//              }
//			})
        },
		seachFunc:function(){
			var _this = this;
			var staffHtmlList = '';
			var nameTxt = '';
			if(_this.userInfo.name) {
				nameTxt = decodeURIComponent(_this.userInfo.name)
			}else {
				nameTxt = ''
			}
			$.ajax({
				type: 'GET',
				url: '/a/sys/user/listData4mobile',
				data: {
					pageNo: 1,
					"company.id": _this.userInfo.companyId,
					"company.name": _this.userInfo.companyName, 
					loginName:_this.userInfo.loginName,
					name: nameTxt,
					mobile:_this.userInfo.mobile,
					"company.type": 8,  
					"company.customerTypeTypeTen": 10,
					"company.customerTypeEypeEleven": 11,
					conn: "connIndex",
					isFunc: true
				},
				dataType: 'json',
				success: function(res) {
					mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
					var arrLen = res.data.page.list.length;
					if(arrLen > 0) {
                        $.each(res.data.page.list, function(i, item) {
//                      	console.log(item)
                        	var officeChatRecord = '';
                        	if(item.userOrder.officeChatRecord) {
                        		officeChatRecord = item.userOrder.officeChatRecord
                        	}
                        	var orderCount = '';
                        	if(item.userOrder.orderCount) {
                        		orderCount = item.userOrder.orderCount
                        	}
                        	var userOfficeReceiveTotal = '';
                        	if(item.userOrder.userOfficeReceiveTotal) {
                        		userOfficeReceiveTotal = item.userOrder.userOfficeReceiveTotal
                        	}
                        	var officeCount = '';
                        	if(item.userOrder.officeCount) {
                        		officeCount = item.userOrder.officeCount
                        	}
							staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
								'<div class="mui-input-row">' +
									'<label>归属公司:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.company.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>归属部门:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.office.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>登录名:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.loginName+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>姓名:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.name +' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>手机:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.mobile+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>洽谈数:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value="'+officeChatRecord+'">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>新增订单量:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value="'+orderCount+'">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>新增回款额:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value="'+userOfficeReceiveTotal+'">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>新增会员:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value="'+officeCount+'">' +
								'</div>' +
								'<div class="app_font_cl content_part mui-row app_text_center">' +
									'<div class="mui-col-xs-6 staRelevanBtn" staListId="'+ item.id +'" dptmtId="'+ item.office.id +'">' +
										'<li class="mui-table-view-cell">关联经销店</li>' +
									'</div>' +
									'<div class="mui-col-xs-6 staOrdBtn" staListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">订单管理</li>' +
									'</div>'+
//									'<div class="mui-col-xs-3 staAmendBtn" staListId="'+ item.id +'">' +
//										'<li class="mui-table-view-cell">修改</li>' +
//									'</div>'+
//									'<div class="mui-col-xs-2 staDeletBtn" staListId="'+ item.id +'">' +
//										'<li class="mui-table-view-cell">删除</li>' +
//									'</div>'+
								'</div>' +
							'</div>'
						});
						$('#staffList').append(staffHtmlList);
					}else{
						$('#staffList').append('<p class="noneTxt">暂无数据</p>');
					}
				}
			});
			_this.stHrefHtml();
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

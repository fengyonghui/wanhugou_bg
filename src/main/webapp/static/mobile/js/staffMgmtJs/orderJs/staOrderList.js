(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.detileFlag = "false"
		this.cancelAmendPayFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
//			biz:order:bizOrderHeader:edit     审核
			this.getPermissionList('biz:order:bizOrderHeader:edit','staCheckFlag')
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
			                    pager['size']= 10;//条数
			                    pager['pageNo'] = 1;
			                    pager['flag'] = "check_pending";
			                    pager['consultantId'] = _this.userInfo.staListId;
				                var f = document.getElementById("staOrdList");
				                var childs = f.childNodes;
				                for(var i = childs.length - 1; i >= 0; i--) {
				                    f.removeChild(childs[i]);
				                }
//				                console.log('222')
//				                console.log(pager)
				                $('#consultantId').val(pager.consultantId);
				                $('#flag').val(pager.flag);
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
//		          	    console.log(res)
		          	    $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "biz_order_type"},
			                async:false,
			                success: function(res){                 
				                ass=res;
//				                console.log(ass)
			                }
			            });
		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						var arrLen = res.data.page.list.length;	
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
//                      	console.log(item)
							$('#statu').val(item.statu);
							$('#source').val(item.source);
                        	//订单类型  1: 普通订单 ; 2:帐期采购 3:配资采购 4:微商订单 5.代采订单 6.拍照下单
                            var orderTypeTxt = '';
                            $.each(ass,function(i,items){
	                        	if(item.orderType==items.value) {
	                        		orderTypeTxt = items.label
	                        	}else if(item.orderType==items.value) {
	                        		orderTypeTxt = items.label
	                        	}else if(item.orderType==items.value) {
	                        		orderTypeTxt = items.label
	                        	}else if(item.orderType==items.value) {
	                        		orderTypeTxt = items.label
	                        	}else if(item.orderType==items.value) {
	                        		orderTypeTxt = items.label
	                        	}else if(item.orderType==items.value) {
	                        		orderTypeTxt = items.label
	                        	}
                           })

                        	//审核
                        	var staCheckBtn = '';
                        	var staCheckBtnTxt = '';
			                if(_this.staCheckFlag == true) {
			                	if(item.bizStatus==0 || item.bizStatus==5 || item.bizStatus==10) {
			                		staCheckBtn = 'waitCheckBtn'
			                		staCheckBtnTxt = "待审核"
			                	}
			                	if(item.bizStatus==45) {
			                		staCheckBtnTxt = "审核失败"
			                	}
			                	if(item.bizStatus==15) {
			                		staCheckBtnTxt = "审核成功"
			                	}
			                }
			                else {
			                	staCheckBtnTxt = ''
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
										'<div class="app_font_cl content_part mui-row app_text_center">' +
											'<div class="mui-col-xs-6 '+staCheckBtn+'" staOrdListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+ staCheckBtnTxt +'</li>' +
											'</div>'+
//											'<div class="mui-col-xs-3"  staOrdListId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">出库确认</li>' +
//											'</div>'+
//											'<div class="mui-col-xs-3"  staOrdListId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">审核成功</li>' +
//											'</div>'+
											'<div class="mui-col-xs-6 staOrDetailBtn" staOrdListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">详情</li>' +
											'</div>'+
										'</div>' +
									'</div>'
								});
								$('#staOrdList').append(staffHtmlList);
								_this.stOrdHrefHtml()
					} else {
								$('.mui-pull-bottom-pocket').html('');
								mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);							
							}
						totalPage = res.data.page.count%pager.size!=0?
		                parseInt(res.data.page.count/pager.size)+1:
		                res.data.page.count/pager.size;
		                if(totalPage==pager.pageNo){		                	
			                mui('#refreshContainer').pullRefresh().endPullupToRefresh();
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
                    _this.staCheckFlag = res.data;
//                  console.log(_this.staCheckFlag)
                }
            });
        },
		stOrdHrefHtml: function() {
			var _this = this;
		/*查询*/
			$('.header').on('tap', '#staOrdSechBtn', function() {
				var url = $(this).attr('url');
				var staListId = $('#consultantId').val();
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdSech.html",
						extras:{
							staListId: staListId,
						}
					})
				}
			}),
		/*客户专员列表*/
			$('#nav').on('tap','.staOrdStaBtn', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../../html/staffMgmtHtml/staffList.html",
					extras: {
						
					}
				})
			}),	
		/*首页*/
			$('#nav').on('tap','.inHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			}),
		 /*待审核*/
	       $('#staOrdList').on('tap', '.waitCheckBtn', function() {
				var url = $(this).attr('url');
				var staOrdListId = $(this).attr('staOrdListId');
				var flagTxt = $('#flag').val();
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdListId == staOrdListId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdCheck.html",
						extras: {
							staOrdListId: staOrdListId,
							flagTxt: flagTxt,
						}
					})
				}
			}),
		/*修改*/
	       $('.listBlue').on('tap', '.staOraAmendBtn', function() {
				var url = $(this).attr('url');
				var staOrdListId = $(this).attr('staOrdListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdListId == staOrdListId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdAmend.html",
						extras: {
							staOrdListId: staOrdListId,
						}
					})
				}
			}),	
		/*详情*/
			$('.listBlue').on('tap', '.staOrDetailBtn', function() {
				var url = $(this).attr('url');
				var staOrdListId = $(this).attr('staOrdListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdListId == staOrdListId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdDetail.html",
						extras: {
							staOrdListId: staOrdListId,
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
			var nameTxt = '';
			if(_this.userInfo.newinput) {
				nameTxt = decodeURIComponent(_this.userInfo.checkStatus)
			}else {
				nameTxt = ''
			}
			
			$.ajax({
				type: 'GET',
                url: '/a/biz/order/bizOrderHeader/listData4mobile',
				data: {
					'pageNo': 1,
					'orderNum' : _this.userInfo.staOrder,
                    'centersName': _this.userInfo.Purchasing,
                    'customer.phone': _this.userInfo.OrdMobile,
                    'itemNo': _this.userInfo.OrdNumbers,
                    'con.name': _this.userInfo.OrdClient,
                    'bizStatus': _this.userInfo.orderStatus,
                    'selectAuditStatus': nameTxt, //originConfigMap
                    'customer.id':_this.userInfo.newinput
				},
				dataType: 'json',
				success: function(res) {
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
		                        	}else if(item.orderType==items.value) {
		                        		orderTypeTxt = items.label
		                        	}else if(item.orderType==items.value) {
		                        		orderTypeTxt = items.label
		                        	}else if(item.orderType==items.value) {
		                        		orderTypeTxt = items.label
		                        	}else if(item.orderType==items.value) {
		                        		orderTypeTxt = items.label
		                        	}else if(item.orderType==items.value) {
		                        		orderTypeTxt = items.label
		                        	}
	                            })
								var staCheckBtn = '';
	                        	var staCheckBtnTxt = '';
				                if(_this.staCheckFlag == true) {
				                	if(item.bizStatus==0 || item.bizStatus==5 || item.bizStatus==10) {
				                		staCheckBtn = 'waitCheckBtn'
				                		staCheckBtnTxt = "待审核"
				                	}
				                	if(item.bizStatus==45) {
				                		staCheckBtnTxt = "审核失败"
				                	}
				                	if(item.bizStatus==15) {
				                		staCheckBtnTxt = "审核成功"
				                	}
				                }
				                else {
				                	staCheckBtnTxt = ''
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
										'<div class="app_font_cl content_part mui-row app_text_center">' +
											'<div class="mui-col-xs-6 '+staCheckBtn+'" staOrdListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+ staCheckBtnTxt +'</li>' +
											'</div>'+
//											'<div class="mui-col-xs-3"  staOrdListId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">出库确认</li>' +
//											'</div>'+
//											'<div class="mui-col-xs-3"  staOrdListId="'+ item.id +'">' +
//												'<li class="mui-table-view-cell">审核成功</li>' +
//											'</div>'+
											'<div class="mui-col-xs-6 staOrDetailBtn" staOrdListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">详情</li>' +
											'</div>'+
										'</div>' +
									'</div>'
								});
								$('#staOrdList').append(staffHtmlList);
								_this.stOrdHrefHtml()
					}else{
						$('#staOrdList').append('<p class="noneTxt">暂无数据</p>');
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

(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.staOrdFlag = "false"
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
//			biz:order:bizOrderHeader:view		操作
			this.getPermissionList('biz:order:bizOrderHeader:view','staOrdFlag')
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
			                    pager['size']= 20;
			                    pager['pageNo'] = 1;
//			                    pager['flag'] = "check_pending";
//			                    pager['consultantId'] = _this.userInfo.staListId;
				                var f = document.getElementById("orderList");
				                var childs = f.childNodes;
				                for(var i = childs.length - 1; i >= 0; i--) {
				                    f.removeChild(childs[i]);
				                }
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
		            	console.log(res)
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
						var arrLen = res.data.page.list.length;
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true)
						}
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
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
	                        	//审核
	                        	var staCheckBtn = '';
	                        	var staCheckBtnTxt = '';
				                if(_this.staOrdFlag == true) {
				                	if(item.bizStatus < 15) {
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
										'<div class="app_color40 mui-row app_text_center content_part operation">' +
											'<div class="mui-col-xs-6 '+staCheckBtn+'" staOrdId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+ staCheckBtnTxt +'</li>' +
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
								$('#orderList').append(staffHtmlList);
								_this.stOrdHrefHtml()
					} else {
								$('.mui-pull-bottom-pocket').html('');
								$('#orderList').append('<p class="noneTxt">暂无数据</p>');
								mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);							
							}
						totalPage = res.data.page.count%pager.size!=0?
		                parseInt(res.data.page.count/pager.size)+1:
		                res.data.page.count/pager.size;
//		                console.log(totalPage)
		                if(totalPage==pager.pageNo){		                	
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
                    _this.staOrdFlag = res.data;
                }
            });
        },
		stOrdHrefHtml: function() {
			var _this = this;
		/*查询*/
			$('.app_header').on('tap', '#staOrdSechBtn', function() {
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
		 /*待审核*/
	       $('.content_part').on('tap', '.waitCheckBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');//订单 ID
				var flagTxt = $('#flag').val();
				var staListIdTxts = $('#staListIdTxt').val();//查询出来的客户专员 ID
				var consultantIda = $('#consultantIda').val();//客户专员 ID
				var stcheckIdTxt = '';
				if(staListIdTxts) {
					stcheckIdTxt = staListIdTxts
				}
				if(consultantIda) {
					stcheckIdTxt = consultantIda
				}
//				console.log(staListIdTxts)
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdCheck.html",
						extras: {
							staOrdId: staOrdId,
							flagTxt: flagTxt,
							stcheckIdTxt: stcheckIdTxt,
						}
					})
				}
			}),
		/*修改*/
	       $('.content_part').on('tap', '.staOraAmendBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdAmend.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			}),	
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
                    'customer.name': _this.userInfo.customerName,//经销店名称
                    centersName: _this.userInfo.centersName,//采购中心
                    'con.name': _this.userInfo.conName,//客户专员
					mobileAuditStatus: _this.userInfo.mobileAuditStatus,//待同意发货
					waitShipments: _this.userInfo.waitShipments,//待发货
					waitOutput: _this.userInfo.waitOutput,//待出库
					includeTestData: _this.userInfo.includeTestData//测试数据
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
								var staCheckBtn = '';
	                        	var staCheckBtnTxt = '';
				                if(_this.staOrdFlag == true) {
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

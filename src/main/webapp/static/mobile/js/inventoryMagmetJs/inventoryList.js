
(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.btnshow()
			//this.getData()
			//			this.getKey()
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			// 页数
			var page = 0;
			// 每页展示10个
			var size = 10;
			//下拉刷新
			$('.inListAdd').dropload({
				scrollArea: window,
				domDown: {
					domClass: 'dropload-down',
					domRefresh: '<div class="dropload-refresh">↑上拉加载更多内容</div>',
					domLoad: '<div class="dropload-load"><span class="loading"></span>加载中...</div>',
					domNoData: '<div class="dropload-noData">暂无更多内容</div>'
				},
				loadDownFn: function(me) {
				    page++;
					// 拼接HTML
					//'/a/biz/request/bizRequestHeader/list4Mobile?page2='+page+'&size='+size
					var inPHtmlList = '';
					$.ajax({
						type: 'GET',
						url: '/a/biz/request/bizRequestHeader/list4Mobile?page2='+page+'&size='+size,
						data: {
							parentId: _this.userInfo.purchId,
							pageNo: page
						},
						dataType: 'json',
						success: function(res) {
							console.log(res)
							var arrLen = res.data.page.list.length;
							
							if(arrLen > 0) {
								$.each(res.data.page.list, function(i, item) {
									console.log(item)
									
								/*业务状态*/
									var bizstatus = item.bizStatus;
									var bizstatusTxt = '';
									var inCheckBtn = '';
									if(bizstatus==0) {
										bizstatusTxt = "未审核"
										inCheckBtn = "审核"
									}else if(bizstatus==5) {
										bizstatusTxt = "审核通过"
									}else if(bizstatus==10) {
										bizstatusTxt = "采购中"
									}else if(bizstatus==15) {
										bizstatusTxt = "采购完成"
									}else if(bizstatus==20) {
										bizstatusTxt = "备货中"
									}else if(bizstatus==25) {
										bizstatusTxt = "供货完成"
									}else if(bizstatus==30) {
										bizstatusTxt = "收货完成"
									}else if(bizstatus==35) {
										bizstatusTxt = "关闭"
									}
								/*付款按钮*/
									var inPayBtn = '';
									var inCancelBtn = '';
									if(bizstatus==35) {
										inPayBtn = ''
										inCancelBtn = ''
									}else {
										inPayBtn = '付款'
										inCancelBtn = '取消'
									}
								/*品类名称*/	
									var varietyInfoName = '';
									if(item.varietyInfo.name) {
										varietyInfoName = item.varietyInfo.name
									}else {
										varietyInfoName = ''
									}
									var checkStatus = '';
									if(item.commonProcess) {
										checkStatus = item.commonProcess.requestOrderProcess.name
									}else {
										checkStatus = ''
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
											'<label>品类名称:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+varietyInfoName+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>下单时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>申请人:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.createBy.name+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>更新时间:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
										'</div>' +
										'<div class="app_font_cl content_part mui-row app_text_center">' +
											'<div class="mui-col-xs-2 inAddBtn">' +
												'<li class="mui-table-view-cell">添加</li>' +
											'</div>'+
											'<div class="mui-col-xs-2 inDetailBtn">' +
												'<li class="mui-table-view-cell" inListId="'+ item.id +'">详情</li>' +
											'</div>' +
											'<div class="mui-col-xs-2 inAmendBtn">' +
												'<li class="mui-table-view-cell paying" inListId="'+ item.id +'" poId="'+ item.id +'">修改</li>' +
											'</div>' +
											'<div class="mui-col-xs-2" inListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell"> '+inCancelBtn+'</li>' +
											'</div>'+
											'<div class="mui-col-xs-2" inListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+inPayBtn+'</li>' +
											'</div>'+
											'<div class="mui-col-xs-2" inListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+inCheckBtn+'</li>' +
											'</div>'+
										'</div>' +
									'</div>'
								});
							} else {
								// 锁定
								me.lock();
								// 无数据
								me.noData();
							}
							// 为了测试，延迟1秒加载
							setTimeout(function() {
								// 插入数据到页面，放到最后面
								$('.inListAdd').append(inPHtmlList);
								_this.inHrefHtml()
								// 每次数据插入，必须重置
								me.resetload();
							}, 1000);
						},
						error: function(xhr, type) {
							alert('Ajax error!222222');
							// 即使加载出错，也得重置
							me.resetload();
						}
					});
				},
				threshold: 50
			});
			
		},
		//		getKey: function() {
		//
		//		},
		inHrefHtml: function() {
			var _this = this;
		/*备货单添加*/
			$('.content').on('tap', '.inAddBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(poId == poId) {
					GHUTILS.OPENPAGE({
						url: "../../mobile/html/inventoryMagmetHtml/inventoryAddList.html",
						extras: {
							poId: poId,
						}
					})
				}
			}),
		/*详情*/
			$('.content').on('tap', '.inDetailBtn', function() {
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
			}),
		/*修改*/
            $('#invetyAddBtn').on('tap','.inAmendBtn',function(){
            	var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				var poId = $(this).attr('poId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(inListId==inListId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/inventoryMagmetHtml/inventoryAmend.html",
						extras: {
								poId:poId,
								inListId:inListId,
						}
					})
                }
			}),
		/*取消*/
            $('#invetyAddBtn').on('tap','.inCancelBtn',function(){
            	var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(inListId==inListId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/details.html",
						extras: {
								inListId:inListId,
						}
					})
                }
			})
            /*付款*/
            $('#invetyAddBtn').on('tap','.inPayBtn',function(){
            	var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				var codeId = $(this).attr('codeId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(inListId==inListId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/check.html",
						extras: {
								inListId:inListId,
								codeId:codeId,
						}
					})
                }
			})
           /* 审核*/
            $('#invetyAddBtn').on('tap','.inCheckBtn',function(){
            	var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(inListId==inListId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/startCheck.html",
						extras: {
								inListId:inListId,
						}
					})
                }
			})
        },
		//		
		btnshow: function() {
			$('#search_btn').on('tap', function() {
				mui('.mui-off-canvas-wrap').offCanvas().toggle()
			})
			$('.closeBtn').on('tap', function() {
				mui('.mui-off-canvas-wrap').offCanvas().toggle()
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
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

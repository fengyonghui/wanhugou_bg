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
			$('.listBlue').dropload({
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
					var pHtmlList = '';
					$.ajax({
						type: 'GET',
						url: '/a/biz/po/bizPoHeader/listData4Mobile?page2='+page+'&size='+size,
						data: {
							parentId: _this.userInfo.poId,
							pageNo: page
						},
						dataType: 'json',
						success: function(res) {
							var arrLen = res.data.resultList.length;
							var dataRow = res.data.roleSet;
							console.log(res)
							if(arrLen > 0) {
								$.each(res.data.resultList, function(i, item) {
									console.log(item)
									var startBtn = '';
									var classBtn = '';
									var payBtn = '';
									/*有没有开启审核*/
									if(item.process) {
										var code = item.process.purchaseOrderProcess.code;
										if(item.process.purchaseOrderProcess.roleEnNameEnum) {
											var DataRoleGener = item.process.purchaseOrderProcess.roleEnNameEnum;
											var fileRoleData =  dataRow.filter(v => DataRoleGener.includes(v));
											if(item.process && fileRoleData.length>0) {
												startBtn = '审核'
												classBtn = 'shenHe'
											}
										}else {
											startBtn = ''
										}
									} else {
										startBtn = '开启审核'
										classBtn = 'startShenhe'
									}
									
									var bizStatus = item.bizStatus;
									var payment = item.currentPaymentId;
									/*审核流程*/
									
									var applyStatus = item.process.bizStatus;
									/*有没有申请支付单*/
									if((code == 7 && applyStatus == 1 && bizStatus == '部分支付') || (code == 7 && payment == '')) {
										payBtn = '申请付款';
									} else {
										payBtn = ''
									}
									pHtmlList += '<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">' +
										'<div class="mui-input-row">' +
										'<label>采购单号:</label>' +
										'<input id="orderNum" name="orderNum" type="text" class="mui-input-clear" disabled="disabled" value=" ' + item.orderNum + ' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
										'<label>供应商:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" ' + item.vendOffice + ' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
										'<label>订单状态:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" ' + item.bizStatus + ' ">' +
										'</div>' +
										'<div class="app_font_cl content_part mui-row app_text_center">' +
										'<div class="mui-col-xs-3">' +
										'<li class="mui-table-view-cell ' + classBtn + '"  listId="' + item.id + '" codeId="' + code + '">' + startBtn + '</li>' +
										'</div>' +
										'<div class="mui-col-xs-3 ApplyPayListBtn">' +
										'<li class="mui-table-view-cell paying" listId="' + item.id + '" poId="' + item.id + '">' + payBtn + '</li>' +
										'</div>' +
										'<div class="mui-col-xs-4 payListBtn" listId="' + item.id + '">' +
										'<li class="mui-table-view-cell">支付申请列表</li>' +
										'</div>' +
										'<div class="mui-col-xs-2 detailBtn" listId="' + item.id + '">' +
										'<li class="mui-table-view-cell">详情</li>' +
										'</div>' +
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
								$('.listBlue').append(pHtmlList);
								_this.hrefHtml()
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
		hrefHtml: function() {
			var _this = this;
			/*申请付款*/
			//$('.listBlue').on('tap', function() {
			$('.listBlue').on('tap', '.paying', function() {
					var url = $(this).attr('url');
					var poId = $(this).attr('poId');
					if(url) {
						mui.toast('子菜单不存在')
					} else if(poId == poId) {
						GHUTILS.OPENPAGE({
							url: "../../mobile/html/applyPay.html",
							extras: {
								poId: poId,
							}
						})
					}
				}),
				/*支付单列表*/
				$('.listBlue').on('tap', '.payListBtn', function() {
					var url = $(this).attr('url');
					var listId = $(this).attr('listId');
					var poId = $(this).attr('poId');
					if(url) {
						mui.toast('子菜单不存在')
					} else if(listId == listId) {
						GHUTILS.OPENPAGE({
							url: "../../mobile/html/ApplyPayList.html",
							extras: {
								poId: poId,
								listId: listId,
							}
						})
					}
				}),
				/*详情*/
				$('.listBlue').on('tap', '.detailBtn', function() {
					var url = $(this).attr('url');
					var listId = $(this).attr('listId');
					if(url) {
						mui.toast('子菜单不存在')
					} else if(listId == listId) {
						GHUTILS.OPENPAGE({
							url: "../../mobile/html/details.html",
							extras: {
								listId: listId,
							}
						})
					}
				})
			//审核
			$('.listBlue').on('click', '.shenHe', function() {
				var url = $(this).attr('url');
				var listId = $(this).attr('listId');
				var codeId = $(this).attr('codeId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(listId == listId) {
					GHUTILS.OPENPAGE({
						url: "../../mobile/html/check.html",
						extras: {
							listId: listId,
							codeId: codeId,
						}
					})
				}
			})
			//开启审核
			$('.listBlue').on('tap', '.startShenhe', function() {
				var url = $(this).attr('url');
				var listId = $(this).attr('listId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(listId == listId) {
					GHUTILS.OPENPAGE({
						url: "../../mobile/html/startCheck.html",
						extras: {
							listId: listId,
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
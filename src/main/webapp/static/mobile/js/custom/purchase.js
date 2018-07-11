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
			this.getData()
//			this.getKey()
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			//下拉刷新
			mui.init({
				pullRefresh: {
					indicators: false,
					container: "#refreshContainer", //下拉刷新容器标识，querySelector能定位的css选择器均可，比如：id、.class等
					up: {
						contentdown: "下拉可以刷新", //可选，在下拉可刷新状态时，下拉刷新控件上显示的标题内容
						contentover: "释放立即刷新", //可选，在释放可刷新状态时，下拉刷新控件上显示的标题内容
						contentrefresh: "正在刷新...", //可选，正在刷新状态时，下拉刷新控件上显示的标题内容
						callback: function() {
							console.log(22)
								setTimeout(function() {
									mui('#refreshContainer').pullRefresh().endPulldownToRefresh();
								}, 300);
								setTimeout(function() {
									GHUTILS.getUserInfo(function() {
										_this.getData(true);
									});
								}, 500);
							} //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
					}
				}
			});		
		},
//		getKey: function() {
//			var _this = this;
//			if
//			request.getAttribute("key");
//			$("#inTex").html("审核")
//		},
		
		getData: function() {
			var _this = this;
			var url = $(this).attr('url');
			var i=0
			i++
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/listData4Mobile",
				data: {
					parentId:_this.userInfo.purchId,
					pageNo:i
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
					$('#pageId').html(res.data.page.html)
					var pHtmlList = '';
					$.each(res.data.resultList, function(i, item) {
						console.log(item)
						var startBtn = '';
						var classBtn = '';
						if(item.process) {
							startBtn = '审核';
							classBtn = 'shenHe'
						}else {
							startBtn = '开启审核'
							classBtn = 'startShenhe'
						}
						if(item.process){
							var purchaseOrderProcess = item.process
							if(purchaseOrderProcess){
								var code = item.process.purchaseOrderProcess.code
								console.log(code)
								if(code==7 || code==-1) {
									startBtn = ''
								}
							}
							
						}
						pHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
							'<div class="mui-input-row">' +
								'<label>采购单号</label>' +
								'<input id="orderNum" name="orderNum" type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderNum+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>供应商</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.vendOffice+' ">' +
							'</div>' +
							'<div class="app_font_cl content_part mui-row app_text_center">' +
								'<div class="mui-col-xs-4 startCheckBtn">' +
									'<li class="mui-table-view-cell '+classBtn+'"  listId="'+ item.id +'" codeId="'+ code +'">'+startBtn+'</li>' +
								'</div>' +
								'<div class="mui-col-xs-4 ApplyPayListBtn" poId="'+ item.id +'">' +
									'<li class="mui-table-view-cell">支付申请列表</li>' +
								'</div>' +
								'<div class="mui-col-xs-4 detailBtn" listId="'+ item.id +'">' +
									'<li class="mui-table-view-cell">详情</li>' +
								'</div>'+
							'</div>' +
						'</div>'
					});
					$("#addBtn").html(pHtmlList)
				}
			});
		_this.hrefHtml()
		},

        hrefHtml: function() {
			var _this = this;
		
			$('#addBtn').on('tap','.startCheckBtn',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(listId==listId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/startCheck.html",
						extras: {
								listId:listId,
						}
					})
                }
			}),
            $('#addBtn').on('tap','.ApplyPayListBtn',function(){
            	var url = $(this).attr('url');
				var poId = $(this).attr('poId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(poId==poId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/ApplyPayList.html",
						extras: {
								poId:poId,
						}
					})
                }
			}),
            $('#addBtn').on('tap','.detailBtn',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(listId==listId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/details.html",
						extras: {
								listId:listId,
						}
					})
                }
			})
            //审核
            $('#addBtn').on('tap','.shenHe',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
				var codeId = $(this).attr('codeId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(listId==listId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/check.html",
						extras: {
								listId:listId,
								codeId:codeId,
						}
					})
                }
			})
            //开启审核
            $('#addBtn').on('tap','.startShenhe',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(listId==listId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/startCheck.html",
						extras: {
								listId:listId,
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
		}
//		windOload:function(){
//			var _this= this;
//			
//		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
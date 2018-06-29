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
			this.getData(); //获取数据
			this.btnshow()
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
	
		},
		getData: function() {
			var _this = this;
			console.log(this.userInfo.wy)
			//运营管理
			$("#details").off().on("tap", function() {
					GHUTILS.OPENPAGE({
						url: "../html/details.html",
						extras: {
								actionUrl:'FFFF'
							}
					})
			});
		},
		btnshow:function(){
			$('#search_btn').on('tap',function(){
					mui('.mui-off-canvas-wrap').offCanvas().toggle()
			})
			$('.closeBtn').on('tap',function(){
					mui('.mui-off-canvas-wrap').offCanvas().toggle()
			})
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

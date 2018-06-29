(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = null;
		this.historySY = '--';
		this.totalVal = '--';
		this.yincome = '--';
		this.t0 = '--';
		this.tn = '--';
		this.canuse = 0;
		this.invitecode = null;
		this.expTipNum = 0;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData(); //获取数据
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
	
		},
		getData: function() {
			var _this = this;
			//运营管理后台
			$("#management").off().on("tap", function() {
					GHUTILS.OPENPAGE({
						url: "../html/management.html",
						extras: {
								actionUrl:'FFFF',
								wy:'今天'
							}
					})
			});
			//消息
			$("#app_mymessage").off().on("tap", function(){
				GHUTILS.OPENPAGE({
					url: "../../html/account/account-message.html"
				})
			})
			//设置
			$("#app_setting").off().on("tap", function() {
//				if(GHUTILS.getloginStatus(true)){
					GHUTILS.OPENPAGE({
						url: "../../html/setting/setting.html"
					})
//				}
			})
		},
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

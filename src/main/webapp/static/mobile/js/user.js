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
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.userComfirDialig()
		},
        userComfirDialig: function() {
//			var rejectBtn = document.getElementById("rejectBtn");
			document.getElementById("appQuit").addEventListener('tap', function() {
				var btnArray = ['取消', '确定'];
				mui.confirm('确定要注销当前账号？', '确定注销', btnArray, function(choice) {
					if(choice.index == 1) {
                        window.location.href = "/a/logout";
					} else {
						
					}
				})
			});
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

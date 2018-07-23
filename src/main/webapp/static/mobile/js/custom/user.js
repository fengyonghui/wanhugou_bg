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
			var _this = this;
//			var rejectBtn = document.getElementById("rejectBtn");
			document.getElementById("appQuit").addEventListener('tap', function() {
				var btnArray = ['取消', '确定'];
				mui.confirm('确定要注销当前账号？', '确定注销', btnArray, function(choice) {
					if(choice.index == 1) {
						_this.logoutData()
					} else {
						
					}
				})
			});
		},
        logoutData: function() {
			var _this = this;
            
            $.ajax({
                type: "GET",
                url: "",
                data: {parentId:dataId},
                dataType: "json",
                success: function(res){
                    console.log(res)
                    var url = $(this).attr('url');
					var purchId = $(this).attr('purchId');
					GHUTILS.OPENPAGE({
						url: "../html/login.html",
						extras: {
							purchId:purchId,
						}
					})
                }
            });
        }
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

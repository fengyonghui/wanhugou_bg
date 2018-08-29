(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
			this.getData();
		},
		pageInit: function() {
			var _this = this;
			
		},
		getData: function() {
			var _this = this;
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeListByPhone',
				data: {
					type: 6,
					source: 'con',
					phone: $('#staRelAddMobeil').val()
				},
				dataType: 'json',
				success: function(res) {
					console.log(res)
					
//					$('.input_div').html(htmlList)
				}
			});
		},
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
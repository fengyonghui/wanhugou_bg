mui.init();
(function($) {

	var LOGIN = function() {
		this.tips = "#app_tips_login";
		this.mobileLogin = false;
		return this;
	}
	LOGIN.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.bindEvent(); //事件绑定
			this.getData(); //获取数据
		},
		pageInit: function() {
			var _this = this;
		},
		getData: function() {
			var _this = this;
		},
		bindEvent: function() {
			var _this = this;
			$("#app-login").off().on("tap", function() {
				$(_this.tips).html("&nbsp;");
				if(!$(this).hasClass("app_loading") && GHUTILS.validate("app_stepOne")) {
					_this.doLogin($("#app-userAcc2").val(),$("#app-userPwd").val())
				}
			})

			$("#app-userPwd").on('focus', function() {
				$(document).on('keyup', function(e) {
					if(e.keyCode === 13) {
						$(_this.tips).html("&nbsp;");
						if(!$(this).hasClass("app_loading") && GHUTILS.validate("app_stepTwo")) {
							_this.doLogin($("#app-userAcc2").val(), $("#app-userPwd").val(), "")
						}
					}
				}.bind(this))
			}).on('blur', function() {
				$(document).off('keyup')
			})
		},
		doLogin: function(username, password) {
            alert(1);
			var _this = this;
			$(_this.tips).html("&nbsp;");
			$("#app-login").addClass("app_loading");

            var loginForm = $("#loginForm");
            var url = loginForm.attr('action');
            alert(url);
            $.post(loginForm.attr('action'), loginForm.serializeArray(), function (data) {
                if (data && data.sessionid) {
                    sessionid = data.sessionid;
                    GHUTILS.toast("登录成功!");
                    GHUTILS.OPENPAGE({
                        url: "backstagemgmt.html",
                    })
                } else {
                    GHUTILS.toast("登录失败!");
                }
            });
            return false;
			
//			GHUTILS.LOAD({
//				url: GHUTILS.API.USER.doLogin,
//				data: {
//					userAcc: username,
//					userPwd: password,
//				},
//				type: "post",
//				sw: true,
//				callback: function(result) {
//					consle.log(result)
//	$('#errMesg').html(result.errMesg)
//if(result.errMesg !=0){
	//mui.toast(result.errMesg))
//}else{
//		GHUTILS.OPENPAGE({
//					url: "../html/backstagemgmt.html",
//				})
// }
//
//					$("#app-login").removeClass("app_loading");
//					if(GHUTILS.checkErrorCode(result, _this.tips)) {
//						GHUTILS.OPENPAGE({
//					url: "../html/backstagemgmt.html",
//				})
//					}
//				},
//				errcallback: function() {
//					$("#app-login").removeClass("app_loading");
//				}
//			});
		},
	}
	$(function() {
		var log = new LOGIN();
		log.init();
	});
})(Zepto);
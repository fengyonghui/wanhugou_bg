mui.init();
(function($) {

	var REGIST = function(){
		this.getVcode = $("#app-message");
		this.tips = $("#app_tips_reg");
		this.imgConfirmed = false;
		return this;
	}
	REGIST.prototype = {
		init:function(){
			this.pageInit();//页面初始化
			this.bindEvent();//事件绑定
			this.getData();//获取数据
		},
		pageInit:function(){
			var _this = this;
		},
		getData:function(){
			var _this = this;
		},
		bindEvent:function(){
			var _this = this;
			
			$('.app_icon_checked input[type="checkbox"]').on("change", function(){
				if($(this).is(':checked')){
					$(this).parent('span').removeClass('app_icon_checknone').addClass('app_icon_checked')
				}else{
					$(this).parent('span').removeClass('app_icon_checked').addClass('app_icon_checknone')
				}
			})
			
			_this.getVcode.off().on("tap",function(){
				if(_this.getVcode.hasClass("app_btn_loading")){
					return
				}
				_this.tips.html('&nbsp;');
				if(GHUTILS.validate("app-phoneDiv")){
					_this.getVericode();
				}
			});

			$("#app-stepOneNext").on("tap",function(){
				if($(this).hasClass("app_loading")){
					return
				}
				if(GHUTILS.validate("app-regForm") && _this.isValid()){
					_this.tips.html('&nbsp;');
					_this.register($("#app-phoneNo").val());
				}
			})

			$("#app_login").off().on("tap",function(){
				GHUTILS.OPENPAGE({
					url:"../../html/usermgmt/usermgmt-login.html"
				})
			})

			$('.app_account').on('tap',function(){
//				var wg = plus.webview.getLaunchWebview();
//				mui.fire(wg, "showtab", {
//					tabindex: 2
//				});
//				if(_this.ws.opener()){
//					_this.ws.opener().close();
//				}
//				_this.ws.close();
				
				setTimeout(function(){
					GHUTILS.OPENPAGE({
						url:"../../html/account/account.html"
					})
				},2000)
				
				//用户签到页面
//				setTimeout(function(){
//					_this.doSign();
//				},500);
				
				
//				plus.nativeUI.closeWaiting();
			});

			$('#app_person').on('tap',function(){
				_this.getProtocolInfo();
			});
		},
		isValid: function(){
			var _this = this;
			var valid = true;
			if(!$('#app_aggrement').is(':checked')){
				GHUTILS.showError("您必须同意并遵守《国槐科技平台服务协议》才能注册",_this.tips);
				valid = false
			}
			return valid
		},
		//获取协议信息
		getProtocolInfo: function(){
			var _this = this;
			GHUTILS.OPENPAGE({
				url:"../../html/index/content_pages.html",
				extras:{
					title: "国槐科技平台服务协议",
					typeId: "PLATFORM"
				}
			})
//			GHUTILS.LOAD({
//				url: GHUTILS.API.CMS.getProtocolInfo+"?typeId=PLATFORM",
//				type: "post",
//				sw: true,
//				callback: function(result) {
//					if(GHUTILS.checkErrorCode(result, _this.tips)){
//						GHUTILS.OPENPAGE({
//							url:"../../html/index/content_pages.html",
//							extras:{
//								title: "国槐科技平台服务协议",
//								content: encodeURIComponent(result.content)
//							}
//						})
//					}
//				}
//			})
		},
		//获取验证码
		getVericode:function(){
			var _this = this;
			_this.getVcode.addClass("app_btn_loading")
			GHUTILS.LOAD({
				url: GHUTILS.API.USER.sendverifyv1,
				data: {
					phone: $("#app-phoneNo").val(),
					smsType: "regist",
					values: ["", 2]
				},
				type: "post",
				sw: true,
				callback: function(result) {
					console.log(JSON.stringify(result))
					if(GHUTILS.checkErrorCode(result,_this.tips)){
						GHUTILS.btnTime(_this.getVcode);
					}else{
						_this.getVcode.removeClass("app_btn_loading")
					}
				},
				errcallback: function(){
					_this.getVcode.removeClass("app_btn_loading")
				}
			});
		},
		//注册
		register: function(mobile){
			//友盟监听
			//plus.statistic.eventTrig( "app_register", "app_register" );
			var _this = this;
			_this.tips.html("&nbsp;")
			$("#app-stepOneNext").addClass("app_loading");
			GHUTILS.LOAD({
				url: GHUTILS.API.USER.register,
				data: {
					userAcc: mobile,
					vericode: $("#app-vericode").val(),
					userPwd: "",
					sceneId: "",
					platform: "wx",
					channelid: "yingyongbao",
					wxopenid: GHUTILS.GHLocalStorage.getRaw("moneyopenid")
//					clientId: plus.push.getClientInfo().clientid
				},
				type: "post",
				sw: true,
				callback: function(result) {
					console.log(JSON.stringify(result))
					
					
					
					$("#app-stepOneNext").removeClass("app_loading");
					if (GHUTILS.checkErrorCode(result, _this.tips)) {
						localStorage.setItem("userID", mobile);
						
						
						
						GHUTILS.getUserInfo(function(){
							setTimeout(function(){
								$(".steps").hide();
								$("#app-stepTwo").show();
							},100);
						});
					}
				},
				errcallback: function(){
					$("#app-stepOneNext").removeClass("app_loading");
				}
			});
		},
		//签到弹窗
		doSign: function() {
			//签到活动是否存在
			GHUTILS.LOAD({
				url:GHUTILS.API.CHA.getEventInfo,
				data:{
					eventType:'sign',
					couponType:"coupon"
				},
				type:'post',
				callback:function(result){
					if(result.errorCode == 0 && result.money !=null){
						//签到弹窗方法
						GHUTILS.LOAD({
							url: GHUTILS.API.signIn.checkSign,
							data: {},
							type: "post",
							//sw: true,
							callback: function(result) {
								console.log(JSON.stringify())
								if(result.errorCode == 0) {
									//？？？？？？？？？？？？？？？？？？？？///
//									var sub = plus.webview.create("/html/index/index-shortcut/index-signin.html","SIGNIN_DIALOG", {
//										background: "transparent",
//										top: '0',
//										bottom: '0'
//									});
//									sub.show();
									
									mui.toast("代金券领取成功，请到我的账户查看。")
									
									setTimeout(function(){
										GHUTILS.OPENPAGE({
											url:"../../html/account/account.html"
										})
									},2000)

								} else {
								}
							}
						})
					}
				}
			})
		}
	}
	$(function(){
		var reg = new REGIST();
			reg.init();
	});
})(Zepto);

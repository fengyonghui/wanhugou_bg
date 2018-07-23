//定义公共信息
var GHUTILS = {};
//请求地址相关配置
var HOST = "";
//用户基础数据
GHUTILS.userInfo = null;
GHUTILS = {
	//请求接口
	API: {
		//用户相关
		USER: {
			doLogin:         HOST + '/mimosa/client/investor/baseaccount/login',	//登录
		},
	},
	//打开新页面
	OPENPAGE :function(op) {
		//op -- >url,extras.
		if (!op.url) {
			return
		}
		window.location.href = GHUTILS.buildQueryUrl(op.url,op.extras);
	},

	/**
	 * 公共ajax请求方法
	 * 参数说明：{}
	 * url: 请求地址 (string)
	 * params: 请求参数 (string || object)
	 * callback: 成功回调 function
	 * errcallback: 错误回调(错误提示类型tipscode,错误代码errcode, 错误回调 errcb ) (0 || object)
	 * type: 请求类型，"GET" || "POST" 默认是"POST"。(string)
	 * contentType: 请求内容类型，默认为"application/json"。
	 */
	LOAD : function(op) {
		if (!op || !op.url) {
			return;
		}

		if (op.params) {
			op.url = op.url + "?" + decodeURIComponent($.param(op.params));
		}

		if (op.sw && window.plus) {
			GHUTILS.nativeUI.showWaiting();
		}
				
		var _async = op.async == false ? false : true;

		var options = {
			url: op.url,
			data: JSON.stringify(op.data) || "",
			type: op.type || "POST",//HTTP请求类型
			async: _async,
			contentType:op.contentType || "application/json",
			dataType: "json",
			timeout: op.timeout || 30000,
			success: function(d) {
				//console.log(window.location.href)
				//console.log("load success:"+op.url)
				if (op.callback && typeof(op.callback) == 'function') {
					op.callback.apply(null, arguments);
				}

			},
			error: function(XMLHttpRequest, textStatus, errorThrown) {
				GHUTILS.nativeUI.closeWaiting();
				if (op.errcallback) {
					op.errcallback();
				}

				if(op.url != GHUTILS.API.LOGS.slog){
					GHUTILS.LOAD({
						url: GHUTILS.API.LOGS.slog,
						data: {
							reqUri: op.url,
							params: JSON.stringify(op.data) || ""
						},
						sw: false
					})
				}
				console.log(op.url);
				console.log(XMLHttpRequest.status)
				console.log(JSON.stringify(textStatus))
			}
		};
		try {
			mui.ajax(options);
		} catch (e) {
		 	console.log("网络错误，请稍后再试");
		 	if( window.plus){
				GHUTILS.nativeUI.closeWaiting();
				setTimeout(function() {
					plus.webview.currentWebview().endPullToRefresh();
				}, 200);
			}
		}

	},
	
	/**
	 * 将对象转换成带参数的形式 &a=1&b=2
	 */
	buildQueryUrl: function(url, param) {
		var x = url
		var ba = true;
		var allurl = '';
		if (x.indexOf('?') != -1) {
			if (x.indexOf('?') == url.length - 1) {
				ba = false
			} else {
				ba = true
			}
		} else {
			//x = x + '?'
			ba = false
		}
		var builder = ''
		for (var i in param) {
			var p = '&' + i + '='
			if (param[i] || (param[i]+'' == '0')) {
				var v = param[i]
				if (Object.prototype.toString.call(v) === '[object Array]') {
					for (var j = 0; j < v.length; j++) {
						builder = builder + p + encodeURIComponent(v[j])
					}
				} else if (typeof(v) == "object" && Object.prototype.toString.call(v).toLowerCase() == "[object object]" && !v.length) {
					builder = builder + p + encodeURIComponent(JSON.stringify(v))
				} else {
					builder = builder + p + encodeURIComponent(v)
				}
			}
		}
		if (!ba) {
			builder = builder.substring(1)
		}
		
		if(builder){
			x = x + '?'
		}

		return x + builder
	},

	//验证接口返回ErrorCode是否为0
	checkErrorCode:function(result,tips){
		var tips = tips || false;
		var icon = '<span class="app-icon app-icon-clear"></span>';
		if (result.errorCode == 0) {
			return true;
		}else {
			var _msg = '';
			if ( result.errorCode == 502 ||  result.errorCode == 404 ){
				_msg = '请求错误,请稍后重试';

			}else{
				_msg = result.errorMessage
				if(_msg && _msg.indexOf("(CODE") > 0){
					_msg = _msg.substr(0, _msg.indexOf("(CODE"))
				}
			}
			if((result.errorCode == '10002'|| result.errorCode == '20005')){
				GHUTILS.openLogin();
			}
				GHUTILS.toast(_msg || "数据更新中，请耐心等待");
			GHUTILS.nativeUI.closeWaiting();

			return false;
		}
	},

	//获取浏览器参数
	parseUrlParam : function(url) {
		if (!url) {
			url=window.location.href;
		}
		var urlParam = {};
		if (url.indexOf("?") < 0) {
			return urlParam;
		}
		var params = url.substring(url.indexOf("?") + 1).split("&");
		for (var i = 0; i < params.length; i++) {
			var k = params[i].substring(0,params[i].indexOf("="));
			var v = params[i].substring(params[i].indexOf("=")+1);
			if (v.indexOf("#") > 0) {
				v = v.substring(0, v.indexOf("#"));
			}
			urlParam[k] = v;
		}
		return urlParam;
	},
	//获取用户信息
	getUserInfo:function(cb,gologin){
		GHUTILS.LOAD({
			url: GHUTILS.API.USER.userinfo,
			type: "post",
			sw:false,
			callback: function(result) {
				console.log(JSON.stringify(result))
				if (result.errorCode == 0 &&　result.islogin) {

					GHUTILS.userInfo = result;
				}else if( result.errorCode == 10002 ){
					GHUTILS.nativeUI.closeWaiting()
					if(gologin){
						GHUTILS.OPENPAGE({
							url: "../../html/usermgmt/usermgmt-login.html",
							extras: {
								actionUrl:encodeURIComponent(window.location.href)
							}
						});
					}
					
				}else{
					GHUTILS.nativeUI.closeWaiting()
					var _msg = result.errorMessage
					if(_msg && _msg.indexOf("(CODE") > 0){
						_msg = _msg.substr(0, _msg.indexOf("(CODE"))
					}
					mui.toast(_msg || "数据更新中，请耐心等待");
				}
				//回调
				if (cb && typeof(cb) == 'function') {
					cb.apply(null, arguments);
				}
				
			},
			errcallback: function() {
				GHUTILS.nativeUI.closeWaiting()
				mui.toast("网络错误，请稍后再试")
			}
		});
	},
	//获取本地用户信息：传key返回指定key的值。不传key返回所有信息
	getLocalUserInfo:function(key){
//		var userInfo = localStorage.getItem("userInfo") || '[]';
//		userInfo = JSON.parse(userInfo);
//		if(!GHUTILS.userInfo){
//			GHUTILS.getUserInfo(null,true,false)
//		}
		var userInfo = GHUTILS.userInfo || [];
		var info = null;
		if(key){
			$.each(userInfo, function(m,n) {
				if(key == m){
					info = n;
				}
			});
		}else{
			info = userInfo;
		}
		return info
	},
	//获取登录状态 gologin:是否打开登录页面
	getloginStatus:function(gologin){
//		var userInfo = localStorage.getItem("userInfo") || '{"loginStatus":false}';
//			userInfo = JSON.parse(userInfo);
			//console.log(gologin)
		if(!GHUTILS.userInfo){
			GHUTILS.getUserInfo(null,true,false)
		}
		var userInfo = GHUTILS.userInfo || [];
		if(gologin && !userInfo.islogin){
			mui.toast("请先登录");
			mui.fire("", "login",{});
		}
		return userInfo.islogin;
	},

	//获取本地信息
	getLocalCfg:function(key){
		var userId = localStorage.getItem("userID") || '';
		var uConfigList = localStorage.getItem("userConfigList") || '[]',
		uConfigList = JSON.parse(uConfigList);
		var cfg = null;
		
		for (var i = 0; i < uConfigList.length; i++) {
			if(uConfigList[i].mobile == userId){
				if(key){
					mui.each(uConfigList[i], function(m,n) {
						if(key == m){
							cfg = n;
						}
					});
				}else{
					cfg = uConfigList[i];
				}
			}
				
		}
		return cfg
	},
	GHLocalStorage : {
		put: function(key, value) {
			if (!key) {
				return;
			}
			if(typeof(value) == "object") {
				localStorage.setItem(key, JSON.stringify(value));
			}else{
				localStorage.setItem(key, value);
			}
		},
		get: function(key) {
			return JSON.parse(localStorage.getItem(key));
		},
		getRaw: function(key) {
			return localStorage.getItem(key) || null;
		},
		remove: function(Key) {
			localStorage.removeItem(key);
		},
		clear: function() {
			localStorage.clear();
		}
	},
	//转几位小数点（按位数截取），带后缀（单位）GHUTILS.toFixed(10000,4,"元") = 10000.0000元
	toFixeds: function(numb, digital,suffix) {
		var digital = digital ? digital : 0;
		var fixed = 1;
		for (var i = 0; i < digital; i++) {
			fixed = fixed * 10;
		}

		if (numb == undefined || numb.length == 0) {
			return "--";
		}else {
			var numb = GHUTILS.Fmul(Number(numb), fixed);
			return (parseInt(numb)/fixed).toFixed(digital) + (suffix ? suffix : "");
		}
	},

	//转几位小数点（四舍五入），带后缀（单位）GHUTILS.toDecimal(10000,4,"元") = 10000.0000元
	toDecimal: function(numb, digital,suffix) {
		var digital = digital ? digital : 0;
		var fixed = 1;
		for (var i = 0; i < digital; i++) {
			fixed =fixed * 10;
		}
		if (numb == undefined || numb.length == 0) {
			return "--";
		}else {
			var numb = GHUTILS.Fmul(Number(numb), fixed);
			return (Math.round(numb)/fixed).toFixed(digital) + (suffix ? suffix : "");
		}
	},

	//数值单位转换 GHUTILS.NumbF100(10000,4) = 1.0000万
	NumbF0: function(v,digital) {
		if (v == undefined || v.length == 0) {
			 return "--";
		}
		var fixed= digital ? digital : 0;
		var d=v<0?"-":"";
		v = Math.abs(v);
		if (v < 10) {
			v = GHUTILS.toDecimal(v,fixed);
		}else if (v > 10000 * 10000) {
			v = GHUTILS.toDecimal(v / 10000 / 10000,fixed) + '亿';
		}else if (v > 10000) {
			v = GHUTILS.toDecimal(v / 10000,fixed) + '万';
		} else {
			var x = (v / 1000).toFixed(fixed);
			v = (x >= 10) ? '1万' : (v).toFixed(fixed);
		}
		return d+v;
	},

	//日期时间 0000-00-00 00:00:00
	currentDate: function(t) {
		var nowdata = t ? new Date(t) : new Date();
		var y = nowdata.getFullYear(),
			m = nowdata.getMonth() + 1,
			d = nowdata.getDate(),
			h = nowdata.getHours(),
			min = nowdata.getMinutes(),
			s = nowdata.getSeconds(),
			time = null;
		var totr = function(t) {
			t < 10 ? t = '0' + t : t;
			return t;
		};

		time = y + '-' + totr(m) + '-' + totr(d) + ' ' + totr(h) + ':' + totr(min) + ':' + totr(s)

		return time

	},

	//格式化数字 GHUTILS.fmnum(10000,2) = 10,000.00
	fmnum:function(s, n){
		   n = n > 0 && n <= 20 ? n : 2;
		   s = Number(GHUTILS.toFixeds(parseFloat((s + "").replace(/[^\d\.-]/g, "")),n)).toFixed(n) + "";
		   var l = s.split(".")[0].split("").reverse(),
		   r = s.split(".")[1];
		   t = "";
		   for(i = 0; i < l.length; i ++ )
		   {
		      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");
		   }
		   return t.split("").reverse().join("") + "." + r;
	},
	checkInput :function(obj, regnum){
		var regTel = /^[0-9]{0,11}$/g; //电话号码(0)
		var regPwd = /^([\x21-\x7e]|[a-zA-Z0-9]){0,16}$/g; //密码(1)
		var regNum = /^\d+$/g; //纯数字(2)
		var regNump = /^(([1-9]\d*)|0)(\.\d{0,2})?$/g; //含两位小数数字(3)
		var regNumId = /^\d{0,17}(\d|X)$/g; //身份验证(4)
		var regYzm = /^\d{0,6}$/g; //纯数字验证码(5)
		var regMoney = /^((([1-9]{1}\d{0,7}))|(100000000))?$/;//1亿以内整数金额(6)
		//var	regTxt    = /^[\u4E00-\u9FA5]$/g;//汉字(4)

		var value = obj.value;
		if (3 == regnum || 6 == regnum) {
			value=value.replace(",","");
		}
		var regs = [regTel, regPwd, regNum, regNump, regNumId, regYzm, regMoney];

		if (value && !value.match(regs[regnum])) {
			obj.value = obj.getAttribute("app_backvalue");
		}else{
			obj.setAttribute("app_backvalue",value);
		}
	},

	//验证文本框
	testInput:{
		tel: function(t) {
			var re = /^1[3|4|5|7|8][0-9]{9}$/;
			var test = re.test(t) ? true : false;
			return test;
		},
		pw: function(t) {
			var re = /^([\x21-\x7e]|[a-zA-Z0-9]){8,20}$/;
			var test = re.test(t) ? true : false;
			return test;
		},
		txt: function(t) {
			var re = /^[\u4E00-\u9FA5]+$/;
			var test = re.test(t) ? true : false;
			return test;
		},
		IDnum: function(t) {
			var re = /^\d{15}$|\d{17}(\d|x|X)$/;
			var test = re.test(t) ? true : false;
			return test;
		},
		num: function(t) {
			var re = /^[0-9]{6}$/;//安全码和验证码都是6位数字
			var test = re.test(t) ? true : false;
			return test;
		},
		floatNum: function(t) {
			var re = /^(([1-9]\d*)|0)(\.\d{0,2})?$/;//两位小数
			var test = re.test(t) ? true : false;
			return test;
		},
		cardNum: function(t) {
			var re = /^(\d{16,19})$/;
			var test = re.test(t) ? true : false;
			return test;
		}
	},
	//登出
	loginOut:function(callback){

		GHUTILS.LOAD({
				url: GHUTILS.API.USER.doLogout,
//				data: {},
				type: "post",
				async: false,
				callback: function(result) {
					if (result.errorCode == '0') {
						//GHUTILS.getUserInfo(function(){
							if(callback){
								callback();
							}else{
								mui.toast("已经退出登录");
							}
							
						//});
					} else {
						var _msg = result.errorMessage
						if(_msg && _msg.indexOf("(CODE") > 0){
							_msg = _msg.substr(0, _msg.indexOf("(CODE"))
						}
						mui.toast(_msg || "数据更新中，请耐心等待")
					}
				}
			});
	},
	//表单提交验证
	validate:function(scope){
		var result = true;
		$(scope ? "#" + scope + " input,select" : "input,select").forEach(function(d, i) {
			var dom = $(d);
			var valid = dom.attr("valid");
			if (valid && result) {
				var ops = JSON.parse(valid);

				var tips = ops.tipsbox || false;
				if (ops.required || dom.val()) {
					if (!dom.val()) {
						GHUTILS.showError(ops.msg,tips);
						result = false;
						return;
					}
					if (ops.subvalid){
						for (var i = 0; i < ops.subvalid.length; i++) {
							var e = ops.subvalid[i];
							if (e.minLength) {
								if (dom.val().length < e.minLength) {
									GHUTILS.showError(e.msg,tips);
									result = false;
									return;
								}
							}
							if (e.maxLength) {
								if (dom.val().length > e.maxLength) {
									GHUTILS.showError(e.msg,tips);
									result = false;
									return;
								}
							}
							if (e.between) {
								if (dom.val().length < e.between[0] || dom.val().length > e.between[1]) {
									GHUTILS.showError(e.msg,tips);
									result = false;
									return;
								}
							}
							if (e.finalLength) {
								if (dom.val().length != e.finalLength) {
									GHUTILS.showError(e.msg,tips);
									result = false;
									return;
								}
							}
							if (e.equals) {
								if (dom.val() != $("#" + e.equals).val()) {
									GHUTILS.showError(e.msg,tips);
									result = false;
									return;
								}
							}

							if (e.mobilePhone) {
								if (!dom.val().match("^1[3|4|5|7|8][0-9]{9}$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							} else if (e.identityCard) {
								if (!dom.val().match("^\\d{17}[X|\\d|x]$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							} else if (e.passWord) {
								if (!dom.val().match("^([\x21-\x7e]|[a-zA-Z0-9]){0,16}$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							} else if (e.debitCard) {
								if (!dom.val().match("^\\d{16,19}$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							} else if (e.positiveInteger) {
								if (!dom.val().match("^[0-9]+\\d*$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							} else if (e.positiveNumber) {
								if (!dom.val().match("^[0-9]+\.?[0-9]*$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							} else if (e.floatNum) {
								if (!dom.val().match("^(([1-9]\\d*)|0)(\.\\d{0,2})?$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							}else if (e.nickName) {
								if (!dom.val().match("^[\u4E00-\u9FA5A-Za-z0-9_]{2,15}$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							}else if (e.invitationNum) {
								if (!dom.val().match("^[A-Za-z0-9]{7}$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							}else if (e.realName) {
								if (!dom.val().match("^[\u4E00-\u9FA5A-Za-z_·]{1,20}$")) {
									GHUTILS.showError(e.msg,tips);
									result = false;
								}
							}
						};
					}
				}
			}
		});
		return result;
	},
	//格式化时间格式
	/*param{
	 * data:time,
	 * type:0,
	 * showtime:"true"
	 * }
	 * */
	formatTimestamp : function(param) {
		var d = new Date();
		d.setTime(param && param.time || d);
		var datetime = null;
		var x = d.getFullYear() + "-" + (d.getMonth() < 9 ? "0" : "") + (d.getMonth() + 1) + "-" + (d.getDate() < 10 ? "0" : "") + d.getDate();
		var y = (d.getHours() < 10 ? " 0" : " ") + d.getHours() + ":" + (d.getMinutes() < 10 ? "0" : "") + d.getMinutes() + ":" + (d.getSeconds() < 10 ? "0" : "") + d.getSeconds();

		if (param.showtime == "false") {
			datetime = x + y;
		} else {
			datetime = x;
		}
		return datetime;
	},

	//错误提示
	toast:function(content){
		mui.toast(content);
	},

	showError:function(content,tips){
//		if(tips){
//			var icon = '<span class="app-icon app-icon-clear"></span>';
//			$(tips).html(icon + content);
//			return
//		}
		if(content){
			GHUTILS.toast(content);
		}
	},

	popupBox:{
		show:function(obj){
			$(obj).show();
			$(".mui-popup-backdrop").show();
			setTimeout(function(){
				$(obj).addClass("mui-popup-in");
				$(".mui-popup-backdrop").addClass("mui-active");
			},100)
		},
		hide:function(obj){
			$(obj).removeClass("mui-popup-in");
			$(".mui-popup-backdrop").removeClass("mui-active");
			setTimeout(function(){
				$(obj).hide();
				$(".mui-popup-backdrop").hide();
			},400);
		}
	},
	silderBox:{
		show:function(obj){
			$(obj).addClass("app_active");
			setTimeout(function(){
				$(obj).addClass("app_show");
			},50);
			$(obj).off().on("tap",function(e){

				if("#"+e.target.id == obj){
					GHUTILS.silderBox.hide(obj);
				}
			})
		},
		hide:function(obj){
			$(obj).removeClass("app_show");
			setTimeout(function(){
				$(obj).removeClass("app_active");
			},500);
		}
	},
	listLinks:function(){
		var $ = Zepto || false;
		if($){
			$('.app_links').off().on('tap',function(){
				var actionurl = $(this).attr("data-url");
				var checklogin = $(this).attr("data-checklogin");
				
				if(checklogin == "true"){
					GHUTILS.isLogin(function(){
						window.location.href = actionurl
					},actionurl)
				}else{
					window.location.href = actionurl
				}
				
			});
		}
	},
	
	//拨打电话 num 为电话号码
	phoneCall:function(num){
		var btnArray = ['否', '是'];
		mui.confirm('是否要打电话询问客服？', '提示', btnArray, function(e) {
			if (e.index == 1) {
				location.href = 'tel:' + num;
			}
		})
	},
	/**
	 * get cookie
	 * @param {Object} cookie
	 * @param {Object} name
	 */
	getCookie: function(cookie, name) {
		var str = "; " + cookie + "; ",
			index = str.indexOf("; " + name + "=");
		if (index != -1) {
			var tempStr = str.substring(index + name.length + 3, str.length),
				target = tempStr.substring(0, tempStr.indexOf("; "));
			return decodeURIComponent(target);
		}
		return null;
	},
	nativeUI:{
		showWaiting: function(){
			if($(".app_loading_box").length == 0){
				var appLoadingBox = '<div class="app_loading_box"><div class="mui-loading"><div class="mui-spinner"></div></div></div>'
				$("body").append(appLoadingBox);
			}
			$(".app_loading_box").addClass("app_active");
			setTimeout(function(){
				$(".app_loading_box").addClass("app_show");
			},100);

		},
		closeWaiting:function(){
			$(".app_loading_box").removeClass("app_show");
			setTimeout(function(){
				$(".app_loading_box").removeClass("app_active");
			},200)
		}
	},
	dialogBox:{
		show: function(){
			$(".app_dialog_warp").addClass("app_active");
			setTimeout(function(){
				$(".app_dialog_warp").addClass("app_show");
			},100);
			
			$(".app_close_box").off().on("click",function(){
				GHUTILS.dialogBox.hide();
			})
			
		},
		hide:function(){
			$(".app_dialog_warp").removeClass("app_show");
			setTimeout(function(){
				$(".app_dialog_warp").removeClass("app_active");
			},200)
		}
	}
};


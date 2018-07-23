<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no"/>
	<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	<title>登录</title>
	<link href="/static/mobile/css/mui.min.css" rel="stylesheet"/>
	<link href="/static/mobile/css/app.css" rel="stylesheet"/>
	<style type="text/css">
		html, body {
			height: 100%;
		}

		.mui-input-row input.app_login_input {
			padding: 28px 15px 10px;
		}

		#app-forgetpwd {
			width: 25% !important;
		}

		.mui-icon-eye {
			right: 5% !important;
		}

		.app_h60 {
			height: 240px;
			text-align: center;
		}

		.app_hl {
			width: 50%;
			float: left;
			position: relative;
		}

		.app_hr {
			width: 50%;
			float: right;
			position: relative;
		}

		.app_hl_sp {
			width: 112px;
			height: 30px;
			line-height: 30px;
			position: absolute;
			top: 15px;
			right: 0;
			border: 1px solid #0d84f2;
			text-align: center;
			border-top-left-radius: 8px;
			border-bottom-left-radius: 8px;
		}

		.app_hr_sp {
			width: 112px;
			height: 30px;
			line-height: 30px;
			position: absolute;
			top: 15px;
			left: 0;
			border: 1px solid #0d84f2;
			text-align: center;
			border-top-right-radius: 8px;
			border-bottom-right-radius: 8px;
		}

		.app_hb_sp {
			background-color: #0d84f2;
			color: #fff;
		}

		.app_hw_sp {
			background-color: #efeff4;
			color: #0d84f2;
		}

		.mui-content {
			position: relative;
			min-height: 100%;
		}

		.textcenter {
			line-height: 60px;
			font-size: 30px;
			color: #3BBDB1;
			font-family: cursive;
		}

		body {
			padding-bottom: 100px;
			background-color: #374761;
		}

		.app_pa_bottom {
			bottom: -100px;
		}

		.mui-input-row label.login-icon {
			padding: 7px;
		}

		.app_bgcolor {
			background-color: #374761;
			box-shadow: none;
		}

		#app-login {
			background-color: #3BBDB1;
			border: none;
			border-radius: 8px;
		}
		.app_login_logo {padding: 0;}
		.mui-input-group div.mui-input-row {
			height: 40px;
		}
		.mui-input-group div.mui-input-row input {padding: 10px 15px;}
		.mui-bar-nav~.mui-content.app_content_bgc_login {background-color: #374761;}
		.mui-content .form-signin .mui-input-row {background-color: #rgb(250, 255, 189);}
		.mui-input-row .mui-input-clear~.mui-icon-clear {top: 5px;}
		.app_input_group .mui-input-row span {color: #374761;}
	</style>
</head>
<body>
<header class="mui-bar mui-bar-nav app_header app_nbborder app_bgcolor">
	<span id="app_close" class="mui-pull-left" style="font-size:18px;"></span>
	<h1 id="app_title" class="mui-title"></h1>
</header>
<div class="app_content_bgc_login mui-content">
	<div class="app_h60" id="app_title_buttons">
		<div class="">
			<div class="app_login_logo">
				<img src="/static/mobile/images/wht-logo.png" alt=""/>
			</div>
			<span class="textcenter" id="">
							WanHuTong
					</span>
		</div>
		<section id="login_section" class="active">
			<article data-scroll="true" id="login_article">
			<form id="loginForm" class="form-signin" action="#" method="post">
				<input id="mobileLogin" type="hidden" class="app_login_input mui-input-clear" value="true" name="mobileLogin"/>
				<div class="mui-input-group app_input_group steps app_mb30" id="app_stepOne">
					<div class="mui-input-row app_mb30">
						<label class="login-icon"><span class="mui-icon mui-icon-person"></span></label>
						<input id="app-userAcc2" type="text" class="app_login_input mui-input-clear" value="" name="username"
							   placeholder="请输入用户名" valid='{"required":true,"tipsbox":"#app_tips_login","msg":"用户名不能为空"}'
							   maxlength="32"/>
					</div>
					<div class="mui-input-row">
						<label class="login-icon"><span class="mui-icon mui-icon-locked"></span></label>
						<input id="app-userPwd" type="password" class="app_login_input" value="" placeholder="请输入登录密码" name="password"
							   valid='{"required":true,"tipsbox":"#app_tips_login","msg":"密码不能为空"}' maxlength="32"/>
					</div>
					<div class="input-group" id="validateCodeDiv" style="display:none;">
						<div class="input-row">
							<label class="input-label mid" for="validateCode">验证码</label>
							<sys:validateCode name="validateCode" inputCssStyle="margin-bottom:0;"
											  imageCssStyle="padding-top:7px;"/>
						</div>
					</div>
				</div>
				<input type="hidden" name="mobileLogin" value="true">
			</form>
				<div class="app_ml25 app_mr25">
					<button onclick="submitForm();" class="mui-btn mui-btn-primary mui-btn-block">
						登录
					</button>
				</div>
			</article>
		</section>
	</div>

</div>

<%--<script src="/static/mobile/js/component/mui.min.js"></script>--%>
<%--<script src="/static/mobile/js/component/zepto.min.js"></script>--%>
<%--<script src="/static/mobile/js/component/GHutils.js"></script>--%>
<script type="text/javascript">var ctx = '${ctx}';</script>
<script type="text/javascript" src="${ctxStatic}/jingle/js/lib/zepto.js"></script>
<script type="text/javascript" src="${ctxStatic}/jingle/js/li b/iscroll.js"></script>
<script type="text/javascript" src="${ctxStatic}/jingle/js/lib/Jingle.debug.js"></script>
<script type="text/javascript" src="${ctxStatic}/jingle/js/lib/zepto.touch2mouse.js"></script>
<script type="text/javascript" src="${ctxStatic}/jingle/js/app/app.js"></script>
<script type="application/javascript" src="${ctxStatic}/common/base.js"></script>

<script type="application/javascript">
    var sessionid = '${not empty fns:getPrincipal() ? fns:getPrincipal().sessionid : ""}';
    alert(sessionid);
    if (!$String.isNullOrBlank(sessionid)) {
        var targetHash = location.hash;
        if (targetHash == '#login_section') {
            //J.showToast('你已经登录！', 'success');
            // J.Router.goTo('#index_section?index');
            window.location.href = "${ctx}?index=1";
        }
    } else {
        $('#login_article').addClass('active');
    }

    function submitForm(){
        var username = $('#app-userAcc2').val();
        var password = $('#app-userPwd').val();
        var check = true;
        if ($String.isNullOrBlank(username)){
            check = false;
            alert('请填写账号');
            return false;
        }else if ($String.isNullOrBlank(password)){
            check = false;
            alert('请填写密码');
            return false;
        }else if ($('#validateCodeDiv').is(':visible') && $String.isNullOrBlank($('#validateCode').val())){
            check = false;
            alert('请填写验证码');
            return false;
        }
        submitActive(check);
        return false;
    }

    function submitActive(check) {
        if (!check) {
            return false;
		}
        var loginForm = $("#loginForm");
        $.post("${ctx}/login?version=mobile&login=1", loginForm.serializeArray(), function(data){
            if (data && data.sessionid){
                sessionid = data.sessionid;
                alert('登录成功！');
                <%--J.Router.goTo('${ctx}/login?login=1');--%>
				window.location.href = "${ctx}?index=1";
            }else{
                alert(data.message);
                if (data.shiroLoginFailure == 'org.apache.shiro.authc.AuthenticationException'){
                    $('#validateCodeDiv').show();
                }
                $('#validateCodeDiv a').click();
            }
        });
    }
</script>
</body>
</html>

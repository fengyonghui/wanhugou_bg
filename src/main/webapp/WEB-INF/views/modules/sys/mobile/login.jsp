<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
		<title>登录</title>
		<link href="${ctxStatic}/mobile/css/mui.min.css" rel="stylesheet" />
		<link href="${ctxStatic}/mobile/css/app.css" rel="stylesheet" />
    	<style type="text/css">/** app.css ==> app-001 **/</style>
    	<style type="text/css">
    		html, body{height: 100%;}
    		#app-userPwd {width: 80% !important;}
    		#app-forgetpwd {width: 25% !important;}
    		.mui-icon-eye {right: 5% !important;}
    		.app_h60 {height: 240px;text-align: center;}
    		.app_hl {width: 50%;float: left;position: relative;}
    		.app_hr {width: 50%;float: right;position: relative;}
    		.app_hl_sp {width: 112px;height: 30px;line-height: 30px;position: absolute;top: 15px;right: 0;border: 1px solid #0d84f2;text-align: center;border-top-left-radius: 8px;border-bottom-left-radius: 8px;}
    		.app_hr_sp {width: 112px;height: 30px;line-height: 30px;position: absolute;top: 15px;left: 0;border: 1px solid #0d84f2;text-align: center;border-top-right-radius: 8px;border-bottom-right-radius: 8px;}
    		.app_hb_sp {background-color: #0d84f2;color: #fff;}
    		.app_hw_sp {background-color: #efeff4;color: #0d84f2;}
    		.mui-content{position: relative;min-height: 100%;}
    		.textcenter{line-height: 60px;font-size: 30px;color: #3BBDB1;font-family: cursive;}
    		body{padding-bottom:100px;background-color: #374761;}
    		.app_pa_bottom{bottom:-100px;}
    	</style>
		<script type="text/javascript">
            $(document).ready(function() {
                $("#loginForm").validate({
                    rules: {
                        validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
                    },
                    messages: {
                        username: {required: "请填写用户名."},password: {required: "请填写密码."},
                        validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
                    },
                    errorLabelContainer: "#messageBox",
                    errorPlacement: function(error, element) {
                        error.appendTo($("#loginError").parent());
                    }
                });
            });
            // 如果在框架或在对话框中，则弹出提示并跳转到首页
            if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.jbox').length > 0){
                alert('未登录或登录超时。请重新登录，谢谢！');
                top.location = "${ctx}";
            }
		</script>
	</head>
	<body>
		<header class="mui-bar mui-bar-nav app_header app_nbborder">
			<span id="app_close" class="mui-pull-left" style="font-size:18px;"></span>
			<h1 id="app_title" class="mui-title"></h1>
		</header>
		<div class="mui-content">
			<div class="app_h60" id="app_title_buttons">
				<div class="app_h60">
					<div class="app_login_logo">
						<!--WanHuTong-->
						<img src="${ctxStatic}/mobile/images/wht-logo.png" alt="" />
					</div>
					<span class="textcenter" id="">
							WanHuTong
					</span>
				</div>
			</div>
			<form id="loginForm" class="form-signin" action="${ctx}/login?version=h5" method="post">
				<div class="mui-input-group app_input_group steps" id="app_stepOne">
					<div class="mui-input-row">
						<label><span class="mui-icon mui-icon-person"></span></label>
						<input id="app-userAcc2" name="username" type="text" class="mui-input-clear" value=""
							   placeholder="请输入用户名" maxlength="32"/>
					</div>
					<div class="mui-input-row">
						<label><span class="mui-icon mui-icon-locked"></span></label>
						<!--<a href="javascript:;" id="app-forgetpwd" class="mui-btn app_btn_line"></a>-->
						<input id="app-userPwd" name="password" type="password" class="mui-input-password" value=""
							   placeholder="请输入登录密码" maxlength="32"/>
					</div>
					<div class="mui-input-row"></div>

				</div>
				<div class="app_tips_txt app_ml15 app_mt15" id="app_tips_login">&nbsp;</div>
				<div class="app_ml25 app_mr25">
					<button id="app-login" type="submit" class="mui-btn mui-btn-primary mui-btn-block">登录</button>
				</div>
			</form>
		</div>
		
	</body>
</html>
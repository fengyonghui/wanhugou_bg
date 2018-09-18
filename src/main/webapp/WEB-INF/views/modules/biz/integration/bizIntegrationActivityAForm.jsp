<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>注册送</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		    //补充注册送信息
            $.ajax({
                url:"${ctx}/biz/integration/bizIntegrationActivity/systemActivity?code=ZCS",
                type:"get",
                data:'',
                contentType:"application/json;charset=utf-8",
                success:function(data){
                    $("#integrationId").val(data.id);
                    $("#integrationNum").val(data.integrationNum);
                    $("[name='status'][value="+data.status+"]").prop('checked','true');
                }
            });
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		function radioDefault() {
			   alert("请选择指定用户！");
        }

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/integration/bizIntegrationActivity/">积分活动列表</a></li>
		<li class="active"><a href="${ctx}/biz/integration/bizIntegrationActivity/form?id=${bizIntegrationActivity.id}">积分活动<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">${not empty bizIntegrationActivity.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:integration:bizIntegrationActivity:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizIntegrationActivity" action="${ctx}/biz/integration/bizIntegrationActivity/save" method="post" class="form-horizontal">
		<form:hidden id="integrationId" path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">说明：</label>
			<div class="controls">
				手机号注册成功，即送;
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优惠工具：</label>
			<div class="controls">
				<input type="checkbox" value="万户币" checked="checked">万户币
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">赠送：</label>
			<div class="controls">
				注册成功，赠送：
				<form:input path="integrationNum" id="integrationNum" htmlEscape="false" maxlength="10" class="input-xlarge  digits"/>
				个万户币
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开启状态：</label>
			<div class="controls">
				<form:radiobutton path="status" name="status" onclick="radioDefault" value="1" checked="true"/>开启
				<form:radiobutton path="status" name="status" value="0"/>关闭
			</div>
		</div>

		<div class="form-actions">
			<%--<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>保存
			</shiro:hasPermission>--%>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="更新"/>
		</div>
	</form:form>
</body>
</html>
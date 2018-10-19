<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>佣金付款表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizCommission/">佣金付款表列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizCommission/save?id=${bizCommission.id}">佣金付款表<shiro:hasPermission name="biz:order:bizCommission:edit">${not empty bizCommission.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizCommission:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizCommission" action="${ctx}/biz/order/bizCommission/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">总的待付款金额：</label>
			<div class="controls">
				<form:input path="totalCommission" readonly="true" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款金额：</label>
			<div class="controls">
				<form:input path="payTotal" htmlEscape="false" value="${bizCommission.totalCommission}" readonly="true" class="input-xlarge"/>
				&nbsp;&nbsp;<span style="color: red">※：付款金额为总的待付金额，禁止禁止修改</span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" maxlength="200" class="input-xlarge "/>
			</div>
		</div>

		<div class="form-actions">
			<%--<shiro:hasPermission name="biz:order:bizCommission:edit">--%>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<%--</shiro:hasPermission>--%>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
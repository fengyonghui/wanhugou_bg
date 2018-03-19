<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>钱包管理</title>
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
		<li><a href="${ctx}/biz/cust/bizCustCredit/">钱包列表</a></li>
		<li class="active"><a href="${ctx}/biz/cust/bizCustCredit/form?id=${bizCustCredit.customer.id}">钱包<shiro:hasPermission name="biz:cust:bizCustCredit:edit">${not empty bizCustCredit.customer.id?'充值':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:cust:bizCustCredit:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizCustCredit" action="${ctx}/biz/cust/bizCustCredit/save" method="post" class="form-horizontal">
		<form:hidden path="customer.id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">客户名称：</label>
			<div class="controls">
				<form:input path="customer.name" readonly="true" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户钱包：</label>
			<div class="controls">
				<form:input path="wallet" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">万户币：</label>
			<div class="controls">
				<form:input path="money" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:cust:bizCustCredit:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
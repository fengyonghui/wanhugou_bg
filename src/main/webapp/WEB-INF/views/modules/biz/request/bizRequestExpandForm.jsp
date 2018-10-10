<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货单扩展管理</title>
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
		<li><a href="${ctx}/biz/request/bizRequestExpand/">备货单扩展列表</a></li>
		<li class="active"><a href="${ctx}/biz/request/bizRequestExpand/form?id=${bizRequestExpand.id}">备货单扩展<shiro:hasPermission name="biz:request:bizRequestExpand:edit">${not empty bizRequestExpand.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:request:bizRequestExpand:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizRequestExpand" action="${ctx}/biz/request/bizRequestExpand/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">bizRequestHeader.id：</label>
			<div class="controls">
				<form:input path="requestHeader" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备货方：1.采购中心备货；2.供应商备货：</label>
			<div class="controls">
				<form:input path="formType" htmlEscape="false" maxlength="4" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">供应商备货，对应的供应商ID：</label>
			<div class="controls">
				<form:input path="vendor" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">当前支付单ID：</label>
			<div class="controls">
				<form:input path="bizPoPaymentOrder" htmlEscape="false" maxlength="11" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">与供应商结算的金额：</label>
			<div class="controls">
				<form:input path="balanceTotal" htmlEscape="false" class="input-xlarge  number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">版本管理：</label>
			<div class="controls">
				<form:input path="uVersion" htmlEscape="false" maxlength="3" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:request:bizRequestExpand:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
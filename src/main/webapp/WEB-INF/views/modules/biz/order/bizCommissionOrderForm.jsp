<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>佣金付款订单关系表管理</title>
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
		<li><a href="${ctx}/biz/order/bizCommissionOrder/">佣金付款订单关系表列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizCommissionOrder/form?id=${bizCommissionOrder.id}">佣金付款订单关系表<shiro:hasPermission name="biz:order:bizCommissionOrder:edit">${not empty bizCommissionOrder.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizCommissionOrder:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizCommissionOrder" action="${ctx}/biz/order/bizCommissionOrder/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">订单id,biz_order_header.id：</label>
			<div class="controls">
				<form:input path="orderId" htmlEscape="false" maxlength="11" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">佣金付款单id,biz_commission_pay.id：</label>
			<div class="controls">
				<form:input path="commId" htmlEscape="false" maxlength="11" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">佣金：</label>
			<div class="controls">
				<form:input path="commission" htmlEscape="false" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">版本控制：</label>
			<div class="controls">
				<form:input path="uVersion" htmlEscape="false" maxlength="4" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizCommissionOrder:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>支付申请管理</title>
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
		<li><a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/">支付申请列表</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizPoPaymentOrder" action="${ctx}/biz/po/bizPoPaymentOrder/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="poHeaderId"/>
		<form:hidden path="orderType"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">订单总金额：</label>
			<div class="controls">
				<input class="totalDetail" value="${totalDetailResult}" htmlEscape="false" disabled="disabled" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款金额：</label>
			<div class="controls">
				<form:input path="total" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" maxlength="200" class="input-xlarge "/>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
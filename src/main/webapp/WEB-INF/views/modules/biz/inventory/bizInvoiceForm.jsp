<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发货单管理</title>
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
		<li><a href="${ctx}/biz/inventory/bizInvoice/">发货单列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInvoice/form?id=${bizInvoice.id}">发货单<shiro:hasPermission name="biz:inventory:bizInvoice:edit">${not empty bizInvoice.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInvoice:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		

		<div class="control-group">
			<label class="control-label">物流商：</label>
			<div class="controls">
				<select id="bizLogistics" name="bizLogistics.id" onmouseout="" class="input-medium">
					<c:forEach items="${logisticsList}" var="bizLogistics">
						<option value="${bizLogistics.id}"/>${bizLogistics.name}
					</c:forEach>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流信息图：</label>
			<div class="controls">
				<form:input path="imgUrl" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货值：</label>
			<div class="controls">
				<form:input path="valuePrice" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">操作费：</label>
			<div class="controls">
				<form:input path="operation" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">运费：</label>
			<div class="controls">
				<form:input path="freight" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承运人：</label>
			<div class="controls">
				<form:input path="carrier" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流结算方式：</label>
			<div class="controls">
				<select id="settlementStatus" path="settlementStatus" onmouseout="" class="input-xlarge">
					<c:forEach items="${fns:getDictList('biz_settlement_status')}" var="settlementStatus">
						<option value="${settlementStatus.value}">${settlementStatus.label}</option>
						<%--<option <c:if test="${settlementStatus eq '现结'}"><c:out value="1"/></c:if><c:if test="${settlementStatus eq '账期'}"><c:out value="2"/></c:if> onclick="chenge(settlementStatus)">${settlementStatus}</option>--%>
					</c:forEach>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
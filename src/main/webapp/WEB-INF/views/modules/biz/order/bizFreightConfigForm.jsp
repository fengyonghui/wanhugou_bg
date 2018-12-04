<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费设置管理</title>
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
		<li><a href="${ctx}/biz/order/bizFreightConfig/">服务费设置列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizFreightConfig/form?office.id=${bizFreightConfig.office.id}&varietyInfo.id=${bizFreightConfig.varietyInfo.id}">服务费设置<shiro:hasPermission name="biz:order:bizFreightConfig:edit">${not empty bizFreightConfig.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizFreightConfig:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizFreightConfig" action="${ctx}/biz/order/bizFreightConfig/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<form:select path="office.id" class="input-medium required">
					<form:option value="" label="请选择"/>
					<c:forEach items="${centerList}" var="center">
						<form:option value="${center.id}" label="${center.name}"/>
					</c:forEach>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">品类：</label>
			<div class="controls">
				<form:select path="varietyInfo.id" class="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('service_vari')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">服务费：</label>
			<div class="controls">
				<table>
					<c:forEach items="${typeList}" var="type" varStatus="i">
						<tr>
							<td>${type.label}<input type="hidden" value="${type.value}"/></td>
							<td><input type="number" min="0" class="input-mini"/>元/支(元/套)/td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开启状态：</label>
			<div class="controls">
				<input type="checkbox" name="usable" value="1"/>开启
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizFreightConfig:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
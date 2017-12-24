<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>地址信息管理</title>
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
		<li><a href="${ctx}/sys/office/sysOfficeAddress/">地址信息列表</a></li>
		<li class="active"><a href="${ctx}/sys/office/sysOfficeAddress/form?id=${sysOfficeAddress.id}">地址信息<shiro:hasPermission name="sys:office:sysOfficeAddress:edit">${not empty sysOfficeAddress.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:office:sysOfficeAddress:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="sysOfficeAddress" action="${ctx}/sys/office/sysOfficeAddress/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">地址一：</label>
			<div class="controls">
                 <sys:treeselect id="office" name="office.id" value="${sysOfficeAddress.office.id}" labelName="office.name" labelValue="${sysOfficeAddress.office.name}"
                        title="名称" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址二：</label>
			<div class="controls">
			    <input type="text" value="${sysOfficeAddress.addrLocation}">
				<form:input path="addrLocation" value="${office.id}" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址类型：</label>
			<div class="controls">
				 <form:select path="type" class="input-medium required">
                            <form:option value="" label="请选择"/>
                            <form:options items="${fns:getDictList('office_type')}" itemLabel="label" itemValue="value"
                            htmlEscape="false"/></form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">初始值：</label>
			<div class="controls">
				<form:input path="deFault" htmlEscape="false" maxlength="1" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="sys:office:sysOfficeAddress:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
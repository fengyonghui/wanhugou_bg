<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标签属性管理</title>
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
		function  dictButton() {
			$("#dictID").css("display","block");//div
            $("#dictVal").addClass("required");//键值
            $("#name").addClass("required");//键值
            $("#dict.type").addClass("required");//键值
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/tag/tagInfo/">标签属性列表</a></li>
		<li class="active"><a href="${ctx}/sys/tag/tagInfo/form?id=${tagInfo.id}">标签属性<shiro:hasPermission name="sys:tag:tagInfo:edit">${not empty tagInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:tag:tagInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="tagInfo" action="${ctx}/sys/tag/tagInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">标签名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">字典表类型：</label>
			<div class="controls">
				<form:input path="dict.type" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">标签类型：</label>
			<div class="controls">
				<form:select path="level"  class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('level')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<input type="button" onclick="dictButton();" class="btn btn-primary" value="标签属性添加"/>
			<div id="dictID" style="display: none">
				<%--保存字典--%>
					<div class="control-group">
						<label class="control-label">键值:</label>
						<div class="controls">
							<form:input path="dict.value" id="dictVal" htmlEscape="false" maxlength="50" class="input-xlarge"/>
							<span class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">标签:</label>
						<div class="controls">
							<form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge"/>
							<span class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">类型:</label>
						<div class="controls">
							<form:input path="dict.type" htmlEscape="false" maxlength="50" class="input-xlarge abc"/>
							<span class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">描述:</label>
						<div class="controls">
							<form:input path="dict.description" htmlEscape="false" maxlength="50" class="input-xlarge"/>
							<span class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">排序:</label>
						<div class="controls">
							<form:input path="dict.sort" htmlEscape="false" maxlength="11" class="input-xlarge digits"/>
							<span class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">备注:</label>
						<div class="controls">
							<form:textarea path="dict.remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
						</div>
					</div>
				<%--end--%>
			</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:tag:tagInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
		<div class="">
	</form:form>
</body>
</html>
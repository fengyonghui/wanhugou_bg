<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分类与品类主管 关联管理</title>
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
		<li><a href="${ctx}/biz/variety/bizVarietyUserInfo/">分类与品类主管 关联列表</a></li>
		<li class="active"><a href="${ctx}/biz/variety/bizVarietyUserInfo/form?id=${bizVarietyUserInfo.id}">分类与品类主管 关联<shiro:hasPermission name="biz:variety:bizVarietyUserInfo:edit">${not empty bizVarietyUserInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:variety:bizVarietyUserInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizVarietyUserInfo" action="${ctx}/biz/variety/bizVarietyUserInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">分类名称：</label>
			<div class="controls">
				<form:select id="varietyInfo" path="varietyInfo.id" class="input-medium required">
					<form:option label="请选择" value=""/>
					<form:options items="${varietyList}" itemLabel="name" itemValue="id"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">品类主管：</label>
			<div class="controls">
				<form:select about="choose" path="user.id" class="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${usersList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:variety:bizVarietyUserInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
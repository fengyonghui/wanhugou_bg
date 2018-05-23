<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>沟通记录管理</title>
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
		<li><a href="${ctx}/biz/chat/bizChatRecord/">沟通记录列表</a></li>
		<li class="active"><a href="${ctx}/biz/chat/bizChatRecord/form?id=${bizChatRecord.id}">沟通记录
			<shiro:hasPermission name="biz:chat:bizChatRecord:edit">${not empty bizChatRecord.id?'修改':'添加'}</shiro:hasPermission>
			<shiro:lacksPermission name="biz:chat:bizChatRecord:edit">查看</shiro:lacksPermission></a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizChatRecord" action="${ctx}/biz/chat/bizChatRecord/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">机构名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${bizChatRecord.office.id}" labelName="office.name" labelValue="${bizChatRecord.office.name}"
					title="部门" url="/sys/office/queryTreeList?type=6"
					cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户名称：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${bizChatRecord.user.id}" labelName="user.name" labelValue="${bizChatRecord.user.name}"
					title="用户" url="/sys/wx/sysWxPersonalUser/userTreeData" cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">沟通记录：</label>
			<div class="controls">
				<form:input path="chatRecord" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:chat:bizChatRecord:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
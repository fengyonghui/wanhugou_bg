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
		<li><a href="${ctx}/biz/chat/bizChatRecord/list?office.id=${bizChatRecord.office.id}&source=${bizChatRecord.source}">沟通记录列表</a></li>
		<li class="active"><a href="${ctx}/biz/chat/bizChatRecord/form?id=${bizChatRecord.id}&office.type=7&office.parent.id=12&office.id=${bizChatRecord.office.id}&source=${bizChatRecord.source}">沟通记录
			<shiro:hasPermission name="biz:chat:bizChatRecord:edit">${not empty bizChatRecord.id?'修改':'添加'}</shiro:hasPermission>
			<shiro:lacksPermission name="biz:chat:bizChatRecord:edit">查看</shiro:lacksPermission></a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizChatRecord" action="${ctx}/biz/chat/bizChatRecord/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">供应商名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${bizChatRecord.office.id}" labelName="office.name" labelValue="${bizChatRecord.office.name}"
					title="供应商" url="/sys/office/queryTreeList?type=7"
					cssClass="input-medium required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
				<c:if test="${bizChatRecord.office.type==7}">
					<button class="btn btn-primary btn-lg" data-toggle="modal"
							onclick="window.location.href='${ctx}/sys/office/supplierForm?parent.id=${bizChatRecord.office.parent.id}&type=${bizChatRecord.office.type}&gysFlag=chatRecordSave';">添加新的供应商</button>
				</c:if>
				<font color="red">没有需要的供应商时，请点击按钮，不添加请点击左侧放大镜选择供应商</font>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">品类主管或客户专员：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${bizChatRecord.user.id}" labelName="user.name" labelValue="${bizChatRecord.user.name}"
					title="品类主管或客户专员" url="/sys/user/userSelectTreeData" cssClass="input-medium required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">沟通记录：</label>
			<div class="controls">
				<textarea name="chatRecord" placeholder="请输入沟通记录内容" class="required" style="width: 360px;height: 80px;">${bizChatRecord.chatRecord}</textarea>
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
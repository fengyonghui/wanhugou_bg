<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商管理</title>
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
		<li><a href="${ctx}/biz/vend/bizVendMark/">供应商列表</a></li>
		<li class="active"><a href="${ctx}/biz/vend/bizVendMark/form?id=${bizVendMark.id}">供应商<shiro:hasPermission name="biz:vend:bizVendMark:edit">${not empty bizVendMark.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:vend:bizVendMark:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizVendMark" action="${ctx}/biz/vend/bizVendMark/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${bizVendMark.user.id}" labelName="user.name" labelValue="${bizVendMark.user.name}"
					title="用户" url="/sys/user/treeData?type=6&officeId=${office.id}" cssClass="required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">供应商名称：</label>
			<div class="controls">
				<sys:treeselect id="vendor" name="vendor.id" value="${bizVendMark.vendor.id}" labelName="vendor.name" labelValue="${bizVendMark.vendor.name}"
								title="供应商" url="/sys/office/queryTreeList?type=7" cssClass="required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:vend:bizVendMark:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
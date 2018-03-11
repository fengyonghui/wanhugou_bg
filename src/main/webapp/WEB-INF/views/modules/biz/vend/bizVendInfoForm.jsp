<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商拓展表管理</title>
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
		<li><a href="${ctx}/biz/vend/bizVendInfo/">供应商拓展表列表</a></li>
		<li class="active"><a href="${ctx}/biz/vend/bizVendInfo/form?id=${bizVendInfo.id}">供应商拓展表<shiro:hasPermission name="biz:vend:bizVendInfo:edit">${not empty bizVendInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:vend:bizVendInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizVendInfo" action="${ctx}/biz/vend/bizVendInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">机构：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${bizVendInfo.office.id}" labelName="office.name" labelValue="${bizVendInfo.office.name}"
					title="部门" url="/sys/office/queryTreeList?type=7" cssClass="input-xlarge required" allowClear="true"  notAllowSelectParent="true" dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">供应商名称：</label>
			<div class="controls">
				<form:input path="vendName" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分类：</label>
			<div class="controls">
				<sys:treeselect id="bizCategoryInfo" name="bizCategoryInfo.id" value="${bizVendInfo.bizCategoryInfo.id}" labelName="bizCategoryInfo.name" labelValue="${bizVendInfo.bizCategoryInfo.name}"
								title="分类" url="/biz/category/bizCategoryInfo/treeData" extId="${bizVendInfo.bizCategoryInfo.id}"
								cssClass="input-xlarge required" allowClear="${bizCategoryInfo.currentUser.admin}" dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分类名称：</label>
			<div class="controls">
				<form:input path="cateName" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">供应商代码：</label>
			<div class="controls">
				<form:input path="code" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:vend:bizVendInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
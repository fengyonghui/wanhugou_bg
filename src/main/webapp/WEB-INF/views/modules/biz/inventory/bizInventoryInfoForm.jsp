<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<html>
<head>
	<title>仓库信息管理</title>
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
		<li><a href="${ctx}/biz/inventory/bizInventoryInfo?zt=${zt}">仓库信息列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInventoryInfo/form?id=${bizInventoryInfo.id}&zt=${zt}">仓库信息<shiro:hasPermission name="biz:inventory:bizInventoryInfo:edit">${not empty bizInventoryInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInventoryInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInventoryInfo" action="${ctx}/biz/inventory/bizInventoryInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="zt" name="zt" value="${zt}" type="hidden"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">仓库名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">仓库描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" maxlength="200" class="input-xlarge "/>
			</div>
		</div>
		<biz:selectLocationForm/>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls"><%-- notAllowSelectRoot="true" 不能选中根节点 --%>
				<sys:treeselect id="centerOffice" name="customer.id" value="${bizInventoryInfo.customer.id}" labelName="customer.name"
								labelValue="${bizInventoryInfo.customer.name}"  notAllowSelectParent="true"
								title="采购中心"  url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type},${OfficeTypeEnum.WITHCAPITAL.type},${OfficeTypeEnum.NETWORKSUPPLY.type}" extId="${centerOffice.id}"
								cssClass="input-xlarge required"
								allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>

			</div>
		</div>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">仓库地址：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="commonLocation.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>--%>
				<%--<span class="help-inline"><font color="red">*</font> </span>--%>
			<%--</div>--%>
		<%--</div>--%>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizInventoryInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
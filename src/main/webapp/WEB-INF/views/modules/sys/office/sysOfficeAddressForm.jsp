<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
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
		<li><a href="${ctx}/sys/office/sysOfficeAddress/list?office.type=${sysOfficeAddress.office.type}&office.customerTypeTen=${sysOfficeAddress.office.customerTypeTen}&office.customerTypeEleven=${sysOfficeAddress.office.customerTypeEleven}&source=${sysOfficeAddress.office.source}">地址信息列表</a></li>
		<li class="active"><a href="${ctx}/sys/office/sysOfficeAddress/form?id=${sysOfficeAddress.id}office.type=${sysOfficeAddress.office.type}&office.customerTypeTen=${sysOfficeAddress.office.customerTypeTen}&office.customerTypeEleven=${sysOfficeAddress.office.customerTypeEleven}&source=${sysOfficeAddress.office.source}">地址信息<shiro:hasPermission name="sys:office:sysOfficeAddress:edit">${not empty sysOfficeAddress.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:office:sysOfficeAddress:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>

	<form:form id="inputForm" modelAttribute="sysOfficeAddress" action="${ctx}/sys/office/sysOfficeAddress/save" method="post" class="form-horizontal">

		<form:hidden path="id"/>
		<form:hidden path="ohId"/>
		<form:hidden path="flag"/>
		<form:hidden path="office.type"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<c:if test="${sysOfficeAddress.office.type == OfficeTypeEnum.CUSTOMER.type}">
				<label class="control-label">经销店名称：</label>
			</c:if>
			<c:if test="${sysOfficeAddress.office.type != OfficeTypeEnum.CUSTOMER.type}">
				<label class="control-label">采购中心：</label>
			</c:if>
				<div class="controls">
					<sys:treeselect id="office" name="office.id" value="${entity.office.id}"  labelName="office.name"
									labelValue="${entity.office.name}" notAllowSelectParent="true"
									title="经销店"  url="/sys/office/queryTreeList?type=${sysOfficeAddress.office.type}&customerTypeTen=${sysOfficeAddress.office.customerTypeTen}&customerTypeEleven=${sysOfficeAddress.office.customerTypeEleven}&source=${sysOfficeAddress.office.source}"
									cssClass="input-xlarge required"
									allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址类型：</label>
			<div class="controls">
				 <form:select path="type" class="input-xlarge required" onchange="clickOfficeAdd();">
                            <form:option value="" label="请选择"/>
                            <form:options items="${fns:getDictList('office_type')}" itemLabel="label" itemValue="value"
                            htmlEscape="false"/></form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
            <label class="control-label">联系人：</label>
            <div class="controls">
                <form:input path="receiver" htmlEscape="false" maxlength="20" placeholder="请输入联系人名称" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">联系电话：</label>
            <div class="controls">
                <form:input path="phone" htmlEscape="false" maxlength="11" onkeyup="value=value.replace(/[^\d]/g,'')" placeholder="请输入联系电话" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
		 <biz:selectLocationForm/>
		<div class="control-group">
			<label class="control-label">是否默认：</label>
			<div class="controls">
			    <label><input name="deFaultStatus" type="radio" checked="checked" value="1" />默认</label>
			    <label><input name="deFaultStatus" type="radio" value="0" />非默认</label>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group" style="display:none">
            <div class="controls">
                <input type="text" name="idd" value="${param.idd}" htmlEscape="false" maxlength="1" class="input-xlarge required"/>
            </div>
        </div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:office:sysOfficeAddress:edit">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
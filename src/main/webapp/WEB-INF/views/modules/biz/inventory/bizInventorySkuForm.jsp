<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品库存详情管理</title>
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
		<li><a href="${ctx}/biz/inventory/bizInventorySku/">商品库存详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku/form">商品库存详情<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">${not empty bizInventorySku.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInventorySku:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInventorySku" action="${ctx}/biz/inventory/bizInventorySku/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">仓库名称：</label>
			<div class="controls">
				<input value="${bizInventorySku.invInfo.name}" disabled="true" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<form:hidden path="invInfo.id"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">sku商品名称：</label>
			<div class="controls">
				<sys:treeselect id="skuInfo" name="skuInfo.id" value="${bizInventorySku.skuInfo.id}" labelName="skuInfo.id"
								labelValue="${bizInventorySku.skuInfo.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="sku名称"  url="/biz/product/bizProductInfo/querySkuTreeList" extId="${skuInfo.id}"
								cssClass="input-xlarge required" onchange="clickSku();"
								allowClear="${skuInfo.currentUser.admin}"  dataMsgRequired="必填信息">
				</sys:treeselect>
				<%--<form:input path="skuId.name" htmlEscape="false" maxlength="11" class="input-xlarge required"/>--%>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">库存类型：</label>
			<div class="controls">
				<form:select path="invType" class="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('inv_type')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/></form:select>
				<%--<form:input path="invType" htmlEscape="false" maxlength="4" class="input-xlarge required"/>--%>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">库存数量：</label>
			<div class="controls">
				<form:input path="stockQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">销售订单数量：</label>
			<div class="controls">
				<form:input path="stockOrdQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调入数量：</label>
			<div class="controls">
				<form:input path="transInQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调出数量：</label>
			<div class="controls">
				<form:input path="transOutQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">库存专属客户名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="customer.id" value="${entity.customer.id}"  labelName="customer.name"
								labelValue="${entity.customer.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="客户"  url="/sys/office/queryTreeList?type=6" cssClass="input-xlarge required"
								allowClear="${office.currentUser.admin}" onchange="clickBut();" dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>库存调拨详情管理</title>
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
		<li><a href="${ctx}/biz/inventory/bizSkuTransferDetail/">库存调拨详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizSkuTransferDetail/form?id=${bizSkuTransferDetail.id}">库存调拨详情<shiro:hasPermission name="biz:inventory:bizSkuTransferDetail:edit">${not empty bizSkuTransferDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizSkuTransferDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizSkuTransferDetail" action="${ctx}/biz/inventory/bizSkuTransferDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">transfer_id：</label>
			<div class="controls">
				<form:input path="transfer.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">biz_sku_info.id：</label>
			<div class="controls">
				<form:input path="skuInfo.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品调拨数量：</label>
			<div class="controls">
				<form:input path="transQty" htmlEscape="false" maxlength="11" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出库人：</label>
			<div class="controls">
				<form:input path="fromInvOp.id" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">出库时间：</label>
			<div class="controls">
				<input name="fromInvOpTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${bizSkuTransferDetail.fromInvOpTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">入库人：</label>
			<div class="controls">
				<form:input path="toInvOp.id" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">入库时间：</label>
			<div class="controls">
				<input name="toInvOpTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${bizSkuTransferDetail.toInvOpTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">已出库数量：</label>
			<div class="controls">
				<form:input path="outQty" htmlEscape="false" maxlength="11" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">已入库数量：</label>
			<div class="controls">
				<form:input path="inQty" htmlEscape="false" maxlength="11" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">u_version：</label>
			<div class="controls">
				<form:input path="uVersion" htmlEscape="false" maxlength="4" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizSkuTransferDetail:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
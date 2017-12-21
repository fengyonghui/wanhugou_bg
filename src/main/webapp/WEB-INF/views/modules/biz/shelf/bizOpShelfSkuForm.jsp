<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品上架管理</title>
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
		<li><a href="${ctx}/biz/shelf/bizOpShelfSku/">商品上架列表</a></li>
		<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfSku/form?id=${bizOpShelfSku.id}">商品上架<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit">${not empty bizOpShelfSku.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizOpShelfSku:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOpShelfSku" action="${ctx}/biz/shelf/bizOpShelfSku/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">货架ID：</label>
			<div class="controls">
				<form:input path="opShelfInfo.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上架商品id：</label>
			<div class="controls">
				<form:input path="skuInfo.name" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购中心ID( 0:代表平台商品)：</label>
			<div class="controls">
				<%--<form:input path="centerOffice.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>--%>
					<form:select path="centerOffice.id" class="input-xlarge required">
						<form:option value="" label="请选择"/>
						<form:options items="${centerOffice.id}" itemLabel="label" itemValue="value"
									  htmlEscape="false"/>
					</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上架人：</label>
			<div class="controls">
				<form:input path="createBy.name" htmlEscape="false" maxlength="11" class="input-xlarge" readonly="true"/>
				<%--<span class="help-inline"><font color="red">*</font> </span>--%>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上架数量：</label>
			<div class="controls">
				<form:input path="shelfQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">原价：</label>
			<div class="controls">
				<form:input path="orgPrice" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">销售单价-现价：</label>
			<div class="controls">
				<form:input path="salePrice" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">此单价所对应的最低销售数量；：</label>
			<div class="controls">
				<form:input path="minQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">此单价所对应的最高销售数量；0：不限制：</label>
			<div class="controls">
				<form:input path="maxQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上架时间：</label>
			<div class="controls">
				<input name="shelfTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					value="<fmt:formatDate value="${bizOpShelfSku.shelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">下架人：</label>
			<div class="controls">
				<form:input path="unshelfUser" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">下架时间：</label>
			<div class="controls">
				<input name="unshelfTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${bizOpShelfSku.unshelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">显示次序：</label>
			<div class="controls">
				<form:input path="priority" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
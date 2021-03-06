<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>经销店店铺管理</title>
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
		<li><a href="${ctx}/sys/sysCustDetails/">经销店店铺列表</a></li>
		<li class="active"><a href="${ctx}/sys/sysCustDetails/form?id=${sysCustDetails.id}">经销店店铺<shiro:hasPermission name="sys:sysCustDetails:edit">${not empty sysCustDetails.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:sysCustDetails:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="sysCustDetails" action="${ctx}/sys/sysCustDetails/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">经销店：</label>
			<sys:treeselect id="cust" name="cust.id" value="${sysCustDetails.cust.id}"  labelName="cust.name"
							labelValue="${sysCustDetails.cust.name}" notAllowSelectParent="true"
							title="经销店"  url="/sys/office/queryTreeList?type=6"
							cssClass="input-medium required"
							allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
		<div class="control-group">
			<label class="control-label">商铺面积：</label>
			<div class="controls">
				<form:input path="custAcreage" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商铺类型：</label>
			<div class="controls">
				<form:select path="custType" class="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('custType')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商铺类别：</label>
			<div class="controls">
				<form:input path="custCate" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:sysCustDetails:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收藏微店管理</title>
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
		function officeShop(){
			<%-- 用于选择客户 同时默认填写商铺名 --%>
			$("#shopName").val($("#officeName").val());
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/shop/bizWeShopMark/">收藏微店列表</a></li>
		<li class="active"><a href="${ctx}/biz/shop/bizWeShopMark/form?id=${bizWeShopMark.id}">收藏微店<shiro:hasPermission name="biz:shop:bizWeShopMark:edit">${not empty bizWeShopMark.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shop:bizWeShopMark:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizWeShopMark" action="${ctx}/biz/shop/bizWeShopMark/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户名：</label>
			<div class="controls"><%--查询C端已注册的用户url  /sys/wx/sysWxPersonalUser/userTreeData --%>
				<sys:treeselect id="user" name="user.id" value="${bizWeShopMark.user.id}" labelName="user.name" labelValue="${bizWeShopMark.user.name}"
					title="用户" url="/sys/user/treeData?officeId=${bizWeShopMark.shopCust.id}" cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="shopCust.id" value="${bizWeShopMark.shopCust.id}"  labelName="shopCust.name"
								labelValue="${bizWeShopMark.shopCust.name}" notAllowSelectParent="true" onchange="officeShop();"
								title="客户名称"  url="/sys/office/treeData" cssClass="input-xlarge required"
								allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商铺名：</label>
			<div class="controls">
				<form:input path="shopName" htmlEscape="false" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shop:bizWeShopMark:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>购物车管理</title>
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
		<%--选择货架--%>
		var opShelfId=$("#skuShelfinfo").val();
		$.ajax({
			type:"post",
			url:"${ctx}/biz/shelf/bizOpShelfInfo/findShelf",
			success:function (data) {
				$.each(data,function(index,shelfInfo) {
					if(opShelfId==shelfInfo.id){
						$("#s2id_shelfInfoId").find("span").eq(0).text(shelfInfo.name);
						$("#skuShelfinfo").append("<option selected='selected' value='"+shelfInfo.id+"'>"+shelfInfo.name+"</option>")
					}else {
						$("#skuShelfinfo").append("<option value='"+shelfInfo.id+"'>"+shelfInfo.name+"</option>")
					}
				})
			}
		});
    });
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/shop/bizShopCart/">购物车列表</a></li>
		<li class="active"><a href="${ctx}/biz/shop/bizShopCart/form?id=${bizShopCart.id}">购物车<shiro:hasPermission name="biz:shop:bizShopCart:edit">${not empty bizShopCart.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shop:bizShopCart:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizShopCart" action="${ctx}/biz/shop/bizShopCart/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">货架名称：</label>
			<div class="controls">
				<select id="skuShelfinfo" name="skuShelfinfo.id" class="input-xlarge required" style="text-align: center;">
					<option value="">请选择</option>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购商名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${bizShopCart.office.id}" labelName="office.name" labelValue="${bizShopCart.office.name}"
					title="采购商" url="/sys/office/treeData?type=2" cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
			    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购顾问：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${bizShopCart.user.id}" labelName="user.name" labelValue="${bizShopCart.user.name}"
					title="顾问" url="/sys/office/treeData?type=3" cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">sku数量：</label>
			<div class="controls">
				<form:input path="skuQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shop:bizShopCart:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	
	
<%--测试--%>


</body>
</html>
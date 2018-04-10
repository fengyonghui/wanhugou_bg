<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<html>
<head>
	<title>收货地址管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
				var phone = document.getElementById("phone").value;
                   if(!(/^1[34578]\d{9}$/.test(phone))){
                        alert("手机号码有误，请输入11位有效手机号");
                        return false;
                    }
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
	<script type="text/javascript">
        function SubmitPhone(){
            var phone = document.getElementById("phone").value;
            if(!(/^1[34578]\d{9}$/.test(phone))){
                alert("手机号码有误，请输入11位有效手机号");
                return false;
            }
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/shop/bizShopReceiverAddress/">收货地址列表</a></li>
		<li class="active"><a href="${ctx}/biz/shop/bizShopReceiverAddress/form?id=${bizShopReceiverAddress.id}">收货地址<shiro:hasPermission name="biz:shop:bizShopReceiverAddress:edit">${not empty bizShopReceiverAddress.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shop:bizShopReceiverAddress:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizShopReceiverAddress" action="${ctx}/biz/shop/bizShopReceiverAddress/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户名称：</label>
			<div class="controls"><%--查询C端已注册的用户url  /sys/wx/sysWxPersonalUser/userTreeData --%>
				<sys:treeselect id="user" name="user.id" value="${bizShopReceiverAddress.user.id}" labelName="user.name" labelValue="${bizShopReceiverAddress.user.name}"
					title="用户" url="/sys/user/treeData" cssClass="required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<biz:selectLocationForm/>
		<div class="control-group">
			<label class="control-label">联系人：</label>
			<div class="controls">
				<form:input path="receiver" htmlEscape="false" placeholder="请输入名称" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">手机号：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" onblur="SubmitPhone();" placeholder="请输入11位有效手机号" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否默认：</label>
			<div class="controls">
				<label><input name="defaultStatus" type="radio" checked="checked" value="1" />默认</label>
				<label><input name="defaultStatus" type="radio" value="0" />非默认</label>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shop:bizShopReceiverAddress:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
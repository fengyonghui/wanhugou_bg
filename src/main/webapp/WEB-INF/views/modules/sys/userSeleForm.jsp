<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<html>
<head>
	<title>品类主管管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			var boxObj = $("input:checkbox[name='varIdList']"); //获取所有的复选框值 品类名称
    		var expresslist = '${supplierExpressids}'; //用el表达式获取在控制层存放的复选框的值为字符串类型
			var express = expresslist.split(',');
			 $.each(express, function(index, expressId){
			   boxObj.each(function () {
					if($(this).val() == expressId) {
					   $(this).attr("checked",true);
					}
				});
			});
			$("#no").focus();
			$("#inputForm").validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')}
				},
				messages: {
					loginName: {remote: "用户登录名已存在"},
					confirmNewPassword: {equalTo: "输入与上面相同的密码"}
				},
				submitHandler: function(form){
				    var userId = $("#userId").val();
				    var companyId = $("#companyId").val();
				    var userRoleIds = "";
                    $("input[type=checkbox]:checked").each(function (index) {
                        userRoleIds += $(this).val() + ",";
                    });
				    $.ajax({
						type:"post",
						url:"${ctx}/sys/user/selectConsultant",
						data:{userId:userId,companyId:companyId,userRoleIds:userRoleIds},
						success:function (data) {
							if (data=="false"){
							    alert("该采购专员下关联有经销店，请交接后再修改");
							    return false;
							}
                            loading('正在提交，请稍等...');
                            form.submit();
                        }
					});
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
	<li><a href="${ctx}/sys/user/seleIndexList">品类主管列表</a></li>
	<li class="active"><a href="${ctx}/sys/user/userSeleForm?id=${user.id}&conn=selectIndex">品类主管<shiro:hasPermission name="sys:user:edit">${not empty user.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:user:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="user" action="${ctx}/sys/user/userInfosave" method="post" class="form-horizontal">
	<form:hidden id="userId" path="id"/>
	<form:hidden path="conn"/>
	<sys:message content="${message}"/>
	<div class="control-group">
		<label class="control-label">头像:</label>
		<div class="controls">
			<form:hidden id="nameImage" path="photo" htmlEscape="false" maxlength="255" class="input-xlarge"/>
			<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">归属公司:</label>
		<div class="controls">
			<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
							title="公司" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&customerTypeThirteen=${OfficeTypeEnum.NETWORK.type}&source=officeConnIndex" cssClass="required"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">归属部门:</label>
		<div class="controls">
			<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
							title="部门" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&customerTypeThirteen=${OfficeTypeEnum.NETWORK.type}&source=officeConnIndex" cssClass="required" notAllowSelectParent="true"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">工号:</label>
		<div class="controls">
			<form:input path="no" htmlEscape="false" maxlength="50" class="required"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">姓名:</label>
		<div class="controls">
			<form:input path="name" htmlEscape="false" maxlength="50" class="required"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">登录名:</label>
		<div class="controls">
			<input id="oldLoginName" name="oldLoginName" type="hidden" value="${user.loginName}">
			<form:input path="loginName" htmlEscape="false" maxlength="50" class="required userName"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">密码:</label>
		<div class="controls">
			<input id="newPassword" name="newPassword" type="password" value="" maxlength="50" minlength="3" class="${empty user.id?'required':''}"/>
			<c:if test="${empty user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
			<c:if test="${not empty user.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">确认密码:</label>
		<div class="controls">
			<input id="confirmNewPassword" name="confirmNewPassword" type="password" value="" maxlength="50" minlength="3" equalTo="#newPassword"/>
			<c:if test="${empty user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">分类名称：</label>
		<div class="controls">
			<form:checkboxes id="" path="varIdList" items="${varietyList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
			<span class="help-inline">不选择此项，代表不添加分类与品类主管关联</span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">邮箱:</label>
		<div class="controls">
			<form:input path="email" htmlEscape="false" maxlength="100" class="email"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">电话:</label>
		<div class="controls">
			<form:input path="phone" htmlEscape="false" maxlength="100"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">手机:</label>
		<div class="controls">
			<form:input path="mobile" htmlEscape="false" maxlength="100"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">是否允许登录:</label>
		<div class="controls">
			<form:select path="loginFlag">
				<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</form:select>
			<span class="help-inline"><font color="red">*</font> “是”代表此账号允许登录，“否”则表示此账号不允许登录</span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">用户类型:</label>
		<div class="controls">
			<form:select path="userType" class="input-xlarge">
				<form:option value="" label="请选择"/>
				<form:options items="${fns:getDictList('sys_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</form:select>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">用户角色:</label>
		<div class="controls">
			<form:checkboxes id="userRoleIds" path="roleIdList" items="${allRoles}" itemLabel="name" itemValue="id" htmlEscape="false" class="required"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">备注:</label>
		<div class="controls">
			<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
		</div>
	</div>
	<c:if test="${not empty user.id}">
		<div class="control-group">
			<label class="control-label">创建时间:</label>
			<div class="controls">
				<label class="lbl"><fmt:formatDate value="${user.createDate}" type="both" dateStyle="full"/></label>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最后登陆:</label>
			<div class="controls">
				<label class="lbl">IP: ${user.loginIp}&nbsp;&nbsp;&nbsp;&nbsp;时间：<fmt:formatDate value="${user.loginDate}" type="both" dateStyle="full"/></label>
			</div>
		</div>
	</c:if>
	<div class="form-actions">
		<shiro:hasPermission name="sys:user:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</form:form>
</body>
</html>
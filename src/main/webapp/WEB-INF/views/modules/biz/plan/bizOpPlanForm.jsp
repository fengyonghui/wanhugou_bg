<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运营计划管理</title>
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
	<script type="text/javascript">
		function che() {
            var centId = $("#centerOffice.id").val();
            alert(centId);
        }


	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/plan/bizOpPlan/">运营计划列表</a></li>
		<li class="active"><a href="${ctx}/biz/plan/bizOpPlan/form?id=${bizOpPlan.id}">运营计划<shiro:hasPermission name="biz:plan:bizOpPlan:edit">${not empty bizOpPlan.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:plan:bizOpPlan:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOpPlan" action="${ctx}/biz/plan/bizOpPlan/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls" onclick="che()">
				<sys:treeselect id="centerOffice" name="centerOffice.id" value="${centerOffice.id}" labelName="centerOffice.name"
								labelValue="${centerOffice.name}"  notAllowSelectParent="true"
								title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-medium required" dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购专员：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${user.id}"  labelName="user.name"
								labelValue="${user.name}" notAllowSelectParent="true"
								title="采购专员"  url="/sys/user/treeData?officeId=${centerOffice.id}&type=${centerOffice.type}"
								cssClass="input-xlarge"
								allowClear="${office.currentUser.admin}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">计划所在年份：</label>
			<div class="controls">
				<form:input path="year" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font>(在选择年计划时， 月份为0，日为0)</font><font color="red"> *</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">计划所在月份：</label>
			<div class="controls">
				<form:input path="month" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font>(选择月计划时，日为0)</font><font color="red"> *</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">计划所在日：</label>
			<div class="controls">
				<form:input path="day" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red"> *</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">总额：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:plan:bizOpPlan:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
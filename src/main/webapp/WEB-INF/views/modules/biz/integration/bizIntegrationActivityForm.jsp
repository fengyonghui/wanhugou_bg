<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>积分活动管理</title>
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
		function radioDefault() {
			   alert("请选择指定用户！");
        }

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/integration/bizIntegrationActivity/">积分活动列表</a></li>
		<li class="active"><a href="${ctx}/biz/integration/bizIntegrationActivity/form?id=${bizIntegrationActivity.id}">积分活动<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">${not empty bizIntegrationActivity.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:integration:bizIntegrationActivity:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizIntegrationActivity" action="${ctx}/biz/integration/bizIntegrationActivity/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">活动名称：</label>
			<div class="controls">
				<form:input path="activityName" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优惠工具：</label>
			<div class="controls">
				<input type="checkbox" value="万户币" checked="checked">万户币
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发送时间：</label>
			<div class="controls">
				<input name="sendTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${bizIntegrationActivity.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发送范围：</label>
			 <div class="controls">
				<form:radiobutton path="sendScope" onclick="radioDefault" value="1" checked="true"/>指定用户
				<form:radiobutton path="sendScope" value="2"/>已下单用户
				<form:radiobutton path="sendScope" value="2"/>未下单用户
				<form:radiobutton path="sendScope" value="2"/>全部用户
			 </div>
		</div>
		<div class="control-group">
			<div class="controls">
				<sys:treeselect id="office"  name="office.id" value="${bizShopCart.office.id}" labelName="office.name" labelValue="${bizShopCart.office.name}"
								title="经销店" url="/sys/office/queryTreeList?type=6" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">发送人数：</label>
			<div class="controls">
				<form:input path="sendNum" htmlEscape="false" maxlength="10" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">每人赠送积分：</label>
			<div class="controls">
				<form:input path="integrationNum" htmlEscape="false" maxlength="10" class="input-xlarge  digits"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备注说明：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>

		<div class="form-actions">
			<%--<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>保存
			</shiro:hasPermission>--%>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
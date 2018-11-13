<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发送站内信管理</title>
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
		
		function btnSaveType(saveType) {
            $("#inputForm").attr("action", "${ctx}/biz/message/bizMessageInfo/save?saveType=" + saveType);
            $("#inputForm").submit();
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/message/bizMessageInfo/">站内信列表</a></li>
		<li class="active"><a href="${ctx}/biz/message/bizMessageInfo/form?id=${bizMessageInfo.id}">发送站内信<shiro:hasPermission name="biz:message:bizMessageInfo:edit">${not empty bizMessageInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:message:bizMessageInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizMessageInfo" action="${ctx}/biz/message/bizMessageInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">标题：</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="128" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">选择发送用户：</label>
			<div class="controls">
				<input title="num" name="${orderDetail.orderHeader.orderNum}" type="checkbox"
					   onclick="selectOrder('${orderDetail.orderHeader.orderNum}')"/>全部零售商
				&nbsp;&nbsp;
				<input title="num" name="${orderDetail.orderHeader.orderNum}" type="checkbox"
					   onclick="selectOrder('${orderDetail.orderHeader.orderNum}')"/>全部代销商
				&nbsp;&nbsp;
				<input title="num" name="${orderDetail.orderHeader.orderNum}" type="checkbox"
					   onclick="selectOrder('${orderDetail.orderHeader.orderNum}')"/>全部经销商
				&nbsp;&nbsp;
				<input title="num" name="${orderDetail.orderHeader.orderNum}" type="checkbox"
					   onclick="selectOrder('${orderDetail.orderHeader.orderNum}')"/>全部供应商
				&nbsp;&nbsp;
				<input title="num" name="${orderDetail.orderHeader.orderNum}" type="checkbox"
					   onclick="selectOrder('${orderDetail.orderHeader.orderNum}')"/>部分用户
				&nbsp;&nbsp;

				<sys:treeselect id="company" name="companyId" value="${companyId}" labelName="company.name" labelValue="${company.id}"
								title="公司" url="/sys/office/treeData?isAll=true" cssClass="input-small" allowClear="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容：</label>
			<div class="controls">
				<form:textarea path="content" htmlEscape="false" rows="4" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">url：</label>
			<div class="controls">
				<form:input path="url" htmlEscape="false" maxlength="128" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发布时间：</label>
			<div class="controls">
				<input name="releaseTime" id="releaseTime" type="text" readonly="readonly"
					   maxlength="20"
					   class="input-medium Wdate required"
					   value="<fmt:formatDate value="${bizMessageInfo.releaseTime}"  pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
					   placeholder="必填！"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:message:bizMessageInfo:edit"><input id="btnSave" class="btn btn-primary" type="button" value="保存暂不发送" onclick="btnSaveType('save')"/>&nbsp;</shiro:hasPermission>
			<shiro:hasPermission name="biz:message:bizMessageInfo:edit"><input id="btnSaveAndSend" class="btn btn-primary" type="button" value="保存并发送" onclick="btnSaveType('saveAndSend')"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
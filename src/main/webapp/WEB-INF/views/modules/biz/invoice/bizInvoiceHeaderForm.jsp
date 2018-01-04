<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票抬头管理</title>
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
<script>
function dia2(){
       alert("打开dialog显示选择");
   }
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/invoice/bizInvoiceHeader/">发票抬头列表</a></li>
		<li class="active"><a href="${ctx}/biz/invoice/bizInvoiceHeader/form?id=${bizInvoiceHeader.id}">发票抬头<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit">${not empty bizInvoiceHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:invoice:bizInvoiceHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoiceHeader" action="${ctx}/biz/invoice/bizInvoiceHeader/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">采购商名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${bizInvoiceHeader.office.id}" labelName="office.name" labelValue="${bizInvoiceHeader.office.name}"
                    title="采购商" url="/sys/office/treeData?type=2" onchange="makeUser();" cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
                <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票抬头：</label>
			<div class="controls">
				<form:input path="invTitle" htmlEscape="false" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票类型：</label>
			<div class="controls">
				<form:select path="invType" class="input-xlarge required">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getDictList('invType')}" itemLabel="label" itemValue="value"
                        htmlEscape="false"/></form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票内容：</label>
			<div class="controls">
				<form:textarea path="invContent" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票数额：</label>
			<div class="controls">
				<form:input path="invTotal" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
            <label class="control-label">选择订单：</label>
            <div class="controls">
                <sys:treeselect id="Dialog2" name="office.id" value="${bizInvoiceHeader.office.id}" labelName="office.name" labelValue="${bizInvoiceHeader.office.name}"
                    title="订单" url="/sys/office/treeData" cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
                   
                    <input type="button" id="dialogDiv" onclick="dia2();" value="选择订单"/>
                     <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>



 <%--订单表--%>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>采购商</th>
        <th>订单编号</th>
        <th>订单总费用</th>
        <th>运费</th>
        <th>创建人</th>
        <th>创建时间</th>
        <th>操作</th>
    </tr>
    </thead>
</table>

</body>
</html>
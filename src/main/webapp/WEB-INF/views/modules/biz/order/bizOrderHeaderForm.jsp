<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<html>
<head>
	<title>订单信息管理</title>
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
		 function clickBut(){
		 alert("---测试");
		 var officeId=$('#officeId').val();
		 alert(officeId)
            $.ajax({
                type:"post",
                url:"${ctx}/sys/office/sysOfficeAddress/findAddrByOffice?office.id="+officeId,
                success:function(data){
                    console(data);
                }
            });
         }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}">订单信息<shiro:hasPermission name="biz:order:bizOrderHeader:edit">${not empty bizOrderHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizOrderHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">订单编号：</label>
			<div class="controls">
				<form:input path="orderNum" disabled="true" placeholder="由系统自动生成" htmlEscape="false" maxlength="30" class="input-xlarge required"/>
			</div>
		</div>
               <%-- <div class="control-group">
                    <label class="control-label">商品详情：</label>
                    <div class="controls">
                            <a href="${ctx}/biz/order/bizOrderDetail/form">
                            <form:input path="totalDetail" value="选择商品" type="button"  htmlEscape="false" class="input-xlarge required"/></a>
                    </div>
                </div>--%>
		<div class="control-group">
			<label class="control-label">订单类型：</label>
			<div class="controls">
                <form:select path="orderType" class="input-medium required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_type')}" itemLabel="label" itemValue="value"
                            htmlEscape="false"/></form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div></div>
		<div class="control-group">
			<label class="control-label">客户名称：</label>
			<div class="controls">
                <sys:treeselect id="office" name="customer.id" value="${entity.customer.id}"  labelName="customer.name"
                                labelValue="${entity.customer.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
                                title="客户"  url="/sys/office/queryTreeList?type=6" cssClass="input-xlarge required"
                                allowClear="${office.currentUser.admin}" onchange="clickBut();" dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单详情总价：</label>
			<div class="controls">
				<form:input path="totalDetail" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单总费用：</label>
			<div class="controls">
				<form:input path="totalExp" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">运费：</label>
			<div class="controls">
				<form:input path="freight" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票状态：</label>
			<div class="controls">
                 <form:select path="invStatus" class="input-medium required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value"
                            htmlEscape="false"/></form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务状态：</label>
			<div class="controls">
                <form:select path="bizStatus" class="input-medium required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
                            htmlEscape="false"/></form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单来源；</label>
			<div class="controls">
				<form:input path="platformInfo.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%--<biz:selectLocationForm/>地区--%>
        <div class="control-group">
            <label class="control-label">收货地址；</label>
            <div class="controls">
                <form:input path="platformInfo.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="选购商品"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
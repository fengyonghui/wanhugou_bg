<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票详情管理</title>
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
		<li><a href="${ctx}/biz/invoice/bizInvoiceDetail/">发票详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/invoice/bizInvoiceDetail/form?id=${bizInvoiceDetail.id}">发票详情<shiro:hasPermission name="biz:invoice:bizInvoiceDetail:edit">${not empty bizInvoiceDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:invoice:bizInvoiceDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoiceDetail" action="${ctx}/biz/invoice/bizInvoiceDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">发票行号：</label>
			<div class="controls">
				<form:input path="lineNo"  disabled="true" placeholder="${bizInvoiceDetail.maxLineNo}" htmlEscape="false" class="input-xlarge required"/>
                <input name="lineNo" value="${bizInvoiceDetail.maxLineNo}" htmlEscape="false" type="hidden" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">销售订单.id：</label>
			<div class="controls">
				<form:input path="orderHead.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票数额：</label>
			<div class="controls">
				<form:input path="invAmt" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:invoice:bizInvoiceDetail:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	
	

 <%--订单表--%>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>ID</th>
        <th>订单编号</th>
        <th>采购商</th>
        <th>商品单价</th>
        <th>采购数量</th>
        <th>发货数量</th>
        <th>创建时间</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
  <c:forEach items="${bizInvoiceDetail.orderHeaderList}" var="orderHeader">
    <tr>
        <td><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}">
                ${orderHeader.id}</a>
        </td>
        <td>
                ${orderHeader.orderNum}
        </td>
        <td>
                ${orderHeader.customer.name}
        </td>
        <td>
                ${bizOrderDetail.unitPrice}
        </td>
        <td>
                ${bizOrderDetail.ordQty}
        </td>
        <td>
                ${bizOrderDetail.sentQty}
        </td>
        <td>
                <fmt:formatDate value="${bizOrderDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </td>
        <shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><td>
            <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}">修改</a>
            <a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}&sign=1" onclick="return confirmx('确认要删除该sku商品吗？', this.href)">删除</a>
        </td></shiro:hasPermission>
    </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
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
	
	
function makeShelfSelect(){
    console.log("--111--");
     var officeId=$('#officeId').val();
     $.ajax({
           type:"post", 
           url:"${ctx}/biz/order/bizOrderHeader/findByOrder?office.id="+officeId,
           dataType:"json",
           success:function(data){
                console.log(data+"--22--");
                 $.each(data,function(index,off){
                     console.log(add+"--33--");   
                     
                 });
           }
     });
}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/invoice/bizInvoiceHeader/">发票详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/invoice/bizInvoiceDetail/form?id=${bizInvoiceDetail.id}">发票详情<shiro:hasPermission name="biz:invoice:bizInvoiceDetail:edit">${not empty bizInvoiceDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:invoice:bizInvoiceDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoiceDetail" action="${ctx}/biz/invoice/bizInvoiceDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>	
		<div class="control-group">
            <label class="control-label">发票行号：</label>
            <div class="controls">
                 <form:input path="lineNo" htmlEscape="false" class="input-xlarge required"/>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">选择订单编号：</label>
			<div class="controls">
                 <select id="orderHead" class="input-xlarge required" name="orderHead.id" onclick="makeShelfSelect();" style="text-align: center;">
                    <option value="1"> ——订单编号1—— </option></select>
                    <span class="help-inline"><font color="red">*</font></span>
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
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="window.location.href='${ctx}/biz/invoice/bizInvoiceHeader/'"/>
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
        <th>订单总费用</th>
        <th>运费</th>
        <th>创建时间</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
  <c:forEach items="${bizInvoiceDetail.orderHeaderList}" var="orderHeader">
    <tr>
        <td>
                ${orderHeader.id}
        </td>
        <td>
                ${orderHeader.orderNum}
        </td>
        <td>
                ${orderHeader.customer.name}
        </td>
        <td>
                ${orderHeader.totalExp}
        </td>
        <td>
                ${orderHeader.freight}
        </td>
        <td>
                <fmt:formatDate value="${orderHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </td>
        <shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><td>
           <%--<a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}">修改</a>--%>
            <a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}&sign=1" onclick="return confirmx('确认要删除该sku商品吗？', this.href)">删除</a>
        </td></shiro:hasPermission>
    </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
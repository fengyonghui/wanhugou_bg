<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发货单和订单详情关系管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz/inventory/bizDetailInvoice/">发货单和订单详情关系列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizDetailInvoice:edit"><li><a href="${ctx}/biz/inventory/bizDetailInvoice/form">发货单和订单详情关系添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizDetailInvoice" action="${ctx}/biz/inventory/bizDetailInvoice/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>发货单ID，biz_invoice.id：</label>
				<form:input path="invoice" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>销售单详情ID，biz_order_detail.id：</label>
				<form:input path="orderDetail" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>备货单详情ID，biz_request_detail.id：</label>
				<form:input path="requestDetail" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:inventory:bizDetailInvoice:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizDetailInvoice">
			<tr>
				<shiro:hasPermission name="biz:inventory:bizDetailInvoice:edit"><td>
    				<a href="${ctx}/biz/inventory/bizDetailInvoice/form?id=${bizDetailInvoice.id}">修改</a>
					<a href="${ctx}/biz/inventory/bizDetailInvoice/delete?id=${bizDetailInvoice.id}" onclick="return confirmx('确认要删除该发货单和订单详情关系吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
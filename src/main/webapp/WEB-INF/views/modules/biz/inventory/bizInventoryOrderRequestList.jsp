<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单备货单出库关系管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizInventoryOrderRequest/">订单备货单出库关系列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizInventoryOrderRequest:edit"><li><a href="${ctx}/biz/inventory/bizInventoryOrderRequest/form">订单备货单出库关系添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInventoryOrderRequest" action="${ctx}/biz/inventory/bizInventoryOrderRequest/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>订单ID：</label>
				<form:input path="orderHeader.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>备货单ID：</label>
				<form:input path="requestHeader.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:inventory:bizInventoryOrderRequest:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInventoryOrderRequest">
			<tr>
				<shiro:hasPermission name="biz:inventory:bizInventoryOrderRequest:edit"><td>
    				<a href="${ctx}/biz/inventory/bizInventoryOrderRequest/form?id=${bizInventoryOrderRequest.id}">修改</a>
					<a href="${ctx}/biz/inventory/bizInventoryOrderRequest/delete?id=${bizInventoryOrderRequest.id}" onclick="return confirmx('确认要删除该订单备货单出库关系吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
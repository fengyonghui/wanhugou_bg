<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购商供应商关联关系管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizPurchaserVendor/">采购商供应商关联关系列表</a></li>
		<shiro:hasPermission name="biz:order:bizPurchaserVendor:edit"><li><a href="${ctx}/biz/order/bizPurchaserVendor/form">采购商供应商关联关系添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPurchaserVendor" action="${ctx}/biz/order/bizPurchaserVendor/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>供应商：</label>
				<form:input path="vendor.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>采购商：</label>
				<form:input path="purchaser.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>采购商</th>
				<th>供应商</th>
				<shiro:hasPermission name="biz:order:bizPurchaserVendor:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPurchaserVendor">
			<tr>
				<td>${bizPurchaserVendor.purchaser.name}</td>
				<td>${bizPurchaserVendor.vendor.name}</td>
				<shiro:hasPermission name="biz:order:bizPurchaserVendor:edit"><td>
    				<a href="${ctx}/biz/order/bizPurchaserVendor/form?id=${bizPurchaserVendor.id}">修改</a>
					<a href="${ctx}/biz/order/bizPurchaserVendor/delete?id=${bizPurchaserVendor.id}" onclick="return confirmx('确认要删除该采购商供应商关联关系吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购商商品价格管理</title>
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
		<li class="active"><a href="${ctx}/biz/sku/bizCustSku/">采购商商品价格列表</a></li>
		<shiro:hasPermission name="biz:sku:bizCustSku:edit"><li><a href="${ctx}/biz/sku/bizCustSku/form">采购商商品价格添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCustSku" action="${ctx}/biz/sku/bizCustSku/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>采购商：</label>
				<form:input path="customer.name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>商品货号：</label>
				<form:input path="skuInfo.itemNo" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>商品编号：</label>
				<form:input path="skuInfo.partNo" htmlEscape="false" maxlength="50" class="input-medium"/>
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
				<th>商品名称</th>
				<th>商品货号</th>
				<th>商品编号</th>
				<th>价格</th>
				<shiro:hasPermission name="biz:sku:bizCustSku:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCustSku">
			<tr>
				<td>${bizCustSku.customer.name}</td>
				<td>${bizCustSku.skuInfo.name}</td>
				<td>${bizCustSku.skuInfo.itemNo}</td>
				<td>${bizCustSku.skuInfo.partNo}</td>
				<td>${bizCustSku.unitPrice}</td>
				<shiro:hasPermission name="biz:sku:bizCustSku:edit"><td>
    				<a href="${ctx}/biz/sku/bizCustSku/form?id=${bizCustSku.id}">修改</a>
					<a href="${ctx}/biz/sku/bizCustSku/delete?id=${bizCustSku.id}" onclick="return confirmx('确认要删除该采购商商品价格吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
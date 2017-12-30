<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品库存详情管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku/">商品库存详情列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><li><a href="${ctx}/biz/inventory/bizInventorySku/form?invInfo.id=${bizInventorySku.invInfo.id}">商品库存详情添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInventorySku" action="${ctx}/biz/inventory/bizInventorySku/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>仓库名称：</label>
				<form:input path="invInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>库存类型：</label>
				<form:input path="invType" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>库存类型</th>
				<th>仓库名称</th>
				<th>商品名称</th>
				<th>库存数量</th>
				<th>销售订单数量</th>
				<th>调入数量</th>
				<th>调出数量</th>
				<th>专属库存的客户</th>
				<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInventorySku">
			<tr>
				<td><a href="${ctx}/biz/inventory/bizInventorySku/form?id=${bizInventorySku.id}">
					${bizInventorySku.invType}
				</a></td>
				<td>
					${bizInventorySku.invInfo.name}
				</td>
				<td>
					${bizInventorySku.skuInfo.name}
				</td>
				<td>
					${bizInventorySku.stockQty}
				</td>
				<%--<td>--%>
					<%--${bizInventorySku.sOrdQty}--%>
				<%--</td>--%>
				<td>
					${bizInventorySku.transInQty}
				</td>
				<td>
					${bizInventorySku.transOutQty}
				</td>
				<td>
					${bizInventorySku.customer.name}
				</td>
				<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><td>
    				<a href="${ctx}/biz/inventory/bizInventorySku/form?id=${bizInventorySku.id}">修改</a>
					<a href="${ctx}/biz/inventory/bizInventorySku/delete?id=${bizInventorySku.id}" onclick="return confirmx('确认要删除该商品库存详情吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
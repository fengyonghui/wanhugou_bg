<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单详情管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizOrderDetail/">订单详情列表</a></li>
		<shiro:hasPermission name="biz:order:bizOrderDetail:edit"><li><a href="${ctx}/biz/order/bizOrderDetail/form">订单详情添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderDetail" action="${ctx}/biz/order/bizOrderDetail/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>biz_order_header.id：</label>
				<form:input path="orderHeader" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>订单详情行号：</label>
				<form:input path="lineNo" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>bom产品 kit：</label>
				<form:input path="pLineNo" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>biz_sku_info.id：</label>
				<form:input path="skuInfo.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>

			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>biz_order_header.id</th>
				<th>订单详情行号</th>
				<th>bom产品 kit</th>
				<th>biz_sku_info.id</th>
				<th>商品编号</th>
				<th>商品名称</th>
				<th>商品单价</th>
				<th>采购数量</th>
				<shiro:hasPermission name="biz:order:bizOrderDetail:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOrderDetail">
			<tr>
				<td><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">
					${bizOrderDetail.orderHeader.id}
				</a></td>
				<td>
					${bizOrderDetail.lineNo}
				</td>
				<td>
					${bizOrderDetail.pLineNo}
				</td>
				<td>
					${bizOrderDetail.skuInfo.id}
				</td>
				<td>
					${bizOrderDetail.partNo}
				</td>
				<td>
					${bizOrderDetail.skuName}
				</td>
				<td>
					${bizOrderDetail.unitPrice}
				</td>
				<td>
					${bizOrderDetail.ordQty}
				</td>
				<shiro:hasPermission name="biz:order:bizOrderDetail:edit"><td>
    				<a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">修改</a>
					<a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}" onclick="return confirmx('确认要删除该订单详情吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
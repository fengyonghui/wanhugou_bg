<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单详细信息管理</title>
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
		<li class="active"><a href="${ctx}/biz/po/bizPoDetail/">采购订单详细信息列表</a></li>
		<shiro:hasPermission name="biz:po:bizPoDetail:edit"><li><a href="${ctx}/biz/po/bizPoDetail/form">采购订单详细信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPoDetail" action="${ctx}/biz/po/bizPoDetail/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品编号：</label>
				<form:input path="partNo" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>商品名称：</label>
				<form:input path="skuName" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>订单详情行号</th>
				<th>biz_sku_info.id</th>
				<th>商品编号</th>
				<th>商品名称</th>
				<th>商品单价</th>
				<th>采购数量</th>
				<shiro:hasPermission name="biz:po:bizPoDetail:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPoDetail">
			<tr>
				<td><a href="${ctx}/biz/po/bizPoDetail/form?id=${bizPoDetail.id}">
					${bizPoDetail.lineNo}
				</a></td>
				<td>
					${bizPoDetail.skuNo}
				</td>
				<td>
					${bizPoDetail.partNo}
				</td>
				<td>
					${bizPoDetail.skuName}
				</td>
				<td>
					${bizPoDetail.unitPrice}
				</td>
				<td>
					${bizPoDetail.ordQty}
				</td>
				<shiro:hasPermission name="biz:po:bizPoDetail:edit"><td>
    				<a href="${ctx}/biz/po/bizPoDetail/form?id=${bizPoDetail.id}">修改</a>
					<a href="${ctx}/biz/po/bizPoDetail/delete?id=${bizPoDetail.id}" onclick="return confirmx('确认要删除该采购订单详细信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
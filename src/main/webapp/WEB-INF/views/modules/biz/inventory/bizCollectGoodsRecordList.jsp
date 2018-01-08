<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收货记录管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizCollectGoodsRecord/">收货记录列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizCollectGoodsRecord:edit"><li><a href="${ctx}/biz/inventory/bizCollectGoodsRecord/form">收货记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCollectGoodsRecord" action="${ctx}/biz/inventory/bizCollectGoodsRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>仓库名称：</label>
				<form:input path="invInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>订单号：</label>
				<form:input path="orderNum" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>仓库名称</th>
				<th>商品名称</th>
				<th>订单号</th>
				<th>收货数量</th>
				<th>收货时间</th>
				<shiro:hasPermission name="biz:inventory:bizCollectGoodsRecord:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCollectGoodsRecord">
			<tr>
				<td><a href="${ctx}/biz/inventory/bizCollectGoodsRecord/form?id=${bizCollectGoodsRecord.id}">
					${bizCollectGoodsRecord.invInfo.name}
				</a></td>
				<td>
					${bizCollectGoodsRecord.skuInfo.name}
				</td>
				<td>
					${bizCollectGoodsRecord.orderNum}
				</td>
				<td>
					${bizCollectGoodsRecord.receiveNum}
				</td>
				<td>
					<fmt:formatDate value="${bizCollectGoodsRecord.receiveDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:inventory:bizCollectGoodsRecord:edit"><td>
    				<a href="${ctx}/biz/inventory/bizCollectGoodsRecord/form?id=${bizCollectGoodsRecord.id}">修改</a>
					<a href="${ctx}/biz/inventory/bizCollectGoodsRecord/delete?id=${bizCollectGoodsRecord.id}" onclick="return confirmx('确认要删除该收货记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
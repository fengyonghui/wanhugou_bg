<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供货记录管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizSendGoodsRecord/">供货记录列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizSendGoodsRecord:edit"><li><a href="${ctx}/biz/inventory/bizSendGoodsRecord/form">供货记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSendGoodsRecord" action="${ctx}/biz/inventory/bizSendGoodsRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>订单编号：</label>
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
				<th>商品名称</th>
				<th>订单号</th>
				<th>供货数量</th>
				<th>客户</th>
				<th>供货时间</th>
				<shiro:hasPermission name="biz:inventory:bizSendGoodsRecord:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSendGoodsRecord">
			<tr>
				<td><a href="${ctx}/biz/inventory/bizSendGoodsRecord/form?id=${bizSendGoodsRecord.id}">
				</a>${bizSendGoodsRecord.skuInfo.name}
				</td>
				<td>
						${bizSendGoodsRecord.orderNum}
				</td>
				<td>
					${bizSendGoodsRecord.sendNum}
				</td>
				<td>
					${bizSendGoodsRecord.customer.name}
				</td>
				<td>
					<fmt:formatDate value="${bizSendGoodsRecord.sendDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:inventory:bizSendGoodsRecord:edit"><td>
    				<a href="${ctx}/biz/inventory/bizSendGoodsRecord/form?id=${bizSendGoodsRecord.id}">修改</a>
					<a href="${ctx}/biz/inventory/bizSendGoodsRecord/delete?id=${bizSendGoodsRecord.id}" onclick="return confirmx('确认要删除该供货记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
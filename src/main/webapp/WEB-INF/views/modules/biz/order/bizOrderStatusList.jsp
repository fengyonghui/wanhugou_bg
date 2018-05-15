<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单状态管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizOrderStatus/">订单状态列表</a></li>
		<shiro:hasPermission name="biz:order:bizOrderStatus:edit"><li><a href="${ctx}/biz/order/bizOrderStatus/form">订单状态添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderStatus" action="${ctx}/biz/order/bizOrderStatus/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>订单编号：</label>
				<form:input path="orderId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>业务状态：</label>
				<form:select path="bizStatus" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/></form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>订单编号</th>
				<th>订单状态</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:order:bizOrderStatus:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOrderStatus">
			<tr>
				<td><a href="${ctx}/biz/order/bizOrderStatus/form?id=${bizOrderStatus.id}">
					${bizOrderStatus.orderHeader.id}</a>
				</td>
				<td>
					${fns:getDictLabel(bizOrderStatus.bizStatus, 'biz_order_status', '未知状态')}
				</td>
				<td>
					${bizOrderStatus.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizOrderStatus.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bizOrderStatus.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:order:bizOrderStatus:edit"><td>
    				<a href="${ctx}/biz/order/bizOrderStatus/form?id=${bizOrderStatus.id}">修改</a>
					<a href="${ctx}/biz/order/bizOrderStatus/delete?id=${bizOrderStatus.id}" onclick="return confirmx('确认要删除该订单状态吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
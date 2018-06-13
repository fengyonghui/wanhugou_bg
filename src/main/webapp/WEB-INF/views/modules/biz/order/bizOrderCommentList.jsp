<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单备注管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizOrderComment/">订单备注列表</a></li>
		<shiro:hasPermission name="biz:order:bizOrderComment:edit"><li><a href="${ctx}/biz/order/bizOrderComment/form">订单备注添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderComment" action="${ctx}/biz/order/bizOrderComment/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>biz_order_header.id：</label>
				<form:input path="orderId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>订单备注：</label>
				<form:input path="comments" htmlEscape="false" maxlength="500" class="input-medium"/>
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
				<th>订单备注</th>
				<th>create_id</th>
				<th>create_time</th>
				<th>update_time</th>
				<shiro:hasPermission name="biz:order:bizOrderComment:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOrderComment">
			<tr>
				<td><a href="${ctx}/biz/order/bizOrderComment/form?id=${bizOrderComment.id}">
					${bizOrderComment.orderId}
				</a></td>
				<td>
					${bizOrderComment.comments}
				</td>
				<td>
					${bizOrderComment.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizOrderComment.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bizOrderComment.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:order:bizOrderComment:edit"><td>
    				<a href="${ctx}/biz/order/bizOrderComment/form?id=${bizOrderComment.id}">修改</a>
					<a href="${ctx}/biz/order/bizOrderComment/delete?id=${bizOrderComment.id}" onclick="return confirmx('确认要删除该订单备注吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
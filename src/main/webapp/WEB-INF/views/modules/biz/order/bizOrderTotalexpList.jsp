<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费记录表管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizOrderTotalexp/">服务费记录表列表</a></li>
		<shiro:hasPermission name="biz:order:bizOrderTotalexp:edit"><li><a href="${ctx}/biz/order/bizOrderTotalexp/form">服务费记录表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderTotalexp" action="${ctx}/biz/order/bizOrderTotalexp/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:order:bizOrderTotalexp:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOrderTotalexp">
			<tr>
				<shiro:hasPermission name="biz:order:bizOrderTotalexp:edit"><td>
    				<a href="${ctx}/biz/order/bizOrderTotalexp/form?id=${bizOrderTotalexp.id}">修改</a>
					<a href="${ctx}/biz/order/bizOrderTotalexp/delete?id=${bizOrderTotalexp.id}" onclick="return confirmx('确认要删除该服务费记录表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
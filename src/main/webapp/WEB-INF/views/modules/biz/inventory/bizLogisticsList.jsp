<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>物流商管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizLogistics/">物流商列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizLogistics:edit"><li><a href="${ctx}/biz/inventory/bizLogistics/form">物流商添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizLogistics" action="${ctx}/biz/inventory/bizLogistics/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>物流商名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>承运人：</label>
				<form:input path="carrier" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>物流商名称</th>
				<th>承运人</th>
				<th>电话</th>
				<shiro:hasPermission name="biz:inventory:bizLogistics:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizLogistics">
			<tr>
				<td>${bizLogistics.name}</td>
				<td>${bizLogistics.carrier}</td>
				<td>${bizLogistics.phone}</td>
				<shiro:hasPermission name="biz:inventory:bizLogistics:edit"><td>
    				<a href="${ctx}/biz/inventory/bizLogistics/form?id=${bizLogistics.id}">修改</a>
					<a href="${ctx}/biz/inventory/bizLogistics/delete?id=${bizLogistics.id}" onclick="return confirmx('确认要删除该物流商吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
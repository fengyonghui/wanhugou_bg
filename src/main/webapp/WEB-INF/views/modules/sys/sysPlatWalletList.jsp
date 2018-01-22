<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>金额管理</title>
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
		<li class="active"><a href="${ctx}/sys/sysPlatWallet/">金额列表</a></li>
		<shiro:hasPermission name="sys:sysPlatWallet:edit"><li><a href="${ctx}/sys/sysPlatWallet/form">金额添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysPlatWallet" action="${ctx}/sys/sysPlatWallet/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>id</th>
				<shiro:hasPermission name="sys:sysPlatWallet:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysPlatWallet">
			<tr>
				<td><a href="${ctx}/sys/sysPlatWallet/form?id=${sysPlatWallet.id}">
					${sysPlatWallet.id}
				</a></td>
				<shiro:hasPermission name="sys:sysPlatWallet:edit"><td>
    				<a href="${ctx}/sys/sysPlatWallet/form?id=${sysPlatWallet.id}">修改</a>
					<a href="${ctx}/sys/sysPlatWallet/delete?id=${sysPlatWallet.id}" onclick="return confirmx('确认要删除该金额吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
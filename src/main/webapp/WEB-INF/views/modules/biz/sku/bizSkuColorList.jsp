<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>颜色管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
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
		<li class="active"><a href="${ctx}/biz/sku/color">颜色列表</a></li>
		<shiro:hasPermission name="biz:sku:color:edit"><li><a href="${ctx}/biz/sku/color/form?">颜色添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="dict" action="${ctx}/biz/sku/color" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>颜色名称：</label>
		<form:input path="label" htmlEscape="false" maxlength="50" class="input-medium" value=""/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>序号</th><th>颜色名称</th><th>类型</th><th>描述</th><th>排序</th><td>备注</td><shiro:hasPermission name="biz:sku:color:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="dict" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td><a href="${ctx}/biz/sku/color/form?id=${dict.id}">${dict.label}</a></td>
				<td><a href="javascript:" onclick="$('#type').val('${dict.type}');$('#searchForm').submit();return false;">${dict.type}</a></td>
				<td>${dict.description}</td>
				<td>${dict.sort}</td>
				<td>${dict.remarks}</td>
				<shiro:hasPermission name="biz:sku:color:edit"><td>
    				<a href="${ctx}/biz/sku/color/form?id=${dict.id}">修改</a>
					<a href="${ctx}/biz/sku/color/delete?id=${dict.id}&type=${dict.type}" onclick="return confirmx('确认要删除该字典吗？', this.href)">删除</a>
    				<%--<a href="<c:url value='${fns:getAdminPath()}/biz/sku/color/form?type=${dict.type}&sort=${dict.sort+10}'><c:param name='description' value='${dict.description}'/></c:url>">添加键值</a>--%>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
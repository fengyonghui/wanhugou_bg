<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标签属性管理</title>
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
		<li class="active"><a href="${ctx}/sys/attribute/attributeInfo/">标签属性列表</a></li>
		<shiro:hasPermission name="sys:attribute:attributeInfo:edit"><li><a href="${ctx}/sys/attribute/attributeInfo/form">标签属性添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="attributeInfo" action="${ctx}/sys/attribute/attributeInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>标签名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>标签名称</th>
				<shiro:hasPermission name="sys:attribute:attributeInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="attributeInfo">
			<tr>
				<td><a href="${ctx}/sys/attribute/attributeInfo/form?id=${attributeInfo.id}">
					${attributeInfo.name}
				</a></td>
				<shiro:hasPermission name="sys:attribute:attributeInfo:edit"><td>
    				<a href="${ctx}/sys/attribute/attributeInfo/form?id=${attributeInfo.id}">修改</a>
					<a href="${ctx}/sys/attribute/attributeInfo/delete?id=${attributeInfo.id}" onclick="return confirmx('确认要删除该标签属性吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
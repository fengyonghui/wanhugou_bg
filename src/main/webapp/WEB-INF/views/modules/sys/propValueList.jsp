<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统属性值管理</title>
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
		<li class="active"><a href="${ctx}/sys/propValue/">系统属性值列表</a></li>
		<shiro:hasPermission name="sys:propValue:edit"><li><a href="${ctx}/sys/propValue/form">系统属性值添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="propValue" action="${ctx}/sys/propValue/" method="post" class="breadcrumb form-search">
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
				<th>biz_cate_property_info.id</th>
				<th>记录该属性值</th>
				<th>1:active 0:inactive</th>
				<shiro:hasPermission name="sys:propValue:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="propValue">
			<tr>
				<td><a href="${ctx}/sys/propValue/form?id=${propValue.id}">
					${propValue.propId}
				</a></td>
				<td>
					${propValue.value}
				</td>
				<td>
					${fns:getDictLabel(propValue.status, 'status', '')}
				</td>
				<shiro:hasPermission name="sys:propValue:edit"><td>
    				<a href="${ctx}/sys/propValue/form?id=${propValue.id}">修改</a>
					<a href="${ctx}/sys/propValue/delete?id=${propValue.id}" onclick="return confirmx('确认要删除该系统属性值吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>该分类属性管理</title>
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
		<li class="active"><a href="${ctx}/biz/category/bizCatePropertyInfo/">该分类属性列表</a></li>
		<shiro:hasPermission name="biz:category:bizCatePropertyInfo:edit"><li><a href="${ctx}/biz/category/bizCatePropertyInfo/form">该分类属性添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCatePropertyInfo" action="${ctx}/biz/category/bizCatePropertyInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>biz_category_info.id：</label>
				<form:input path="catId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>分类名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>分类名称</th>
				<th>分类描述</th>
				<shiro:hasPermission name="biz:category:bizCatePropertyInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCatePropertyInfo">
			<tr>
				<td><a href="${ctx}/biz/category/bizCatePropertyInfo/form?id=${bizCatePropertyInfo.id}">
					${bizCatePropertyInfo.name}
				</a></td>
				<td>
					${bizCatePropertyInfo.discription}
				</td>
				<shiro:hasPermission name="biz:category:bizCatePropertyInfo:edit"><td>
    				<a href="${ctx}/biz/category/bizCatePropertyInfo/form?id=${bizCatePropertyInfo.id}">修改</a>
					<a href="${ctx}/biz/category/bizCatePropertyInfo/delete?id=${bizCatePropertyInfo.id}" onclick="return confirmx('确认要删除该该分类属性吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
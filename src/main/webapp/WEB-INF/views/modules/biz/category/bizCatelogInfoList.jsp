<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>目录分类表管理</title>
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
		<li class="active"><a href="${ctx}/biz/category/bizCatelogInfo/">目录分类表列表</a></li>
		<shiro:hasPermission name="biz:category:bizCatelogInfo:edit"><li><a href="${ctx}/biz/category/bizCatelogInfo/form">目录分类表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCatelogInfo" action="${ctx}/biz/category/bizCatelogInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>产品目录名称：</label>
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
				<th>产品目录名称</th>
				<shiro:hasPermission name="biz:category:bizCatelogInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCatelogInfo">
			<tr>
				<td><a href="${ctx}/biz/category/bizCatelogInfo/form?id=${bizCatelogInfo.id}">
					${bizCatelogInfo.name}
				</a></td>
				<shiro:hasPermission name="biz:category:bizCatelogInfo:edit"><td>
    				<a href="${ctx}/biz/category/bizCatelogInfo/form?id=${bizCatelogInfo.id}">修改</a>
					<a href="${ctx}/biz/category/bizCatelogInfo/delete?id=${bizCatelogInfo.id}" onclick="return confirmx('确认要删除该目录分类表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
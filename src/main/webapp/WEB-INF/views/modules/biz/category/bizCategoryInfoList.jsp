<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品类别管理</title>
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
		<li class="active"><a href="${ctx}/biz/category/bizCategoryInfo/">商品类别列表</a></li>
		<shiro:hasPermission name="biz:category:bizCategoryInfo:edit"><li><a href="${ctx}/biz/category/bizCategoryInfo/form">商品类别添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCategoryInfo" action="${ctx}/biz/category/bizCategoryInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">

			<%--<li><label>父ID：</label>--%>
				<%--<form:input path="pId" htmlEscape="false" maxlength="11" class="input-medium"/>--%>
			<%--</li>--%>
			<li><label>分类名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
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
				<th>创建人</th>
				<th>创建时间</th>
				<th>数据状态</th>
				<shiro:hasPermission name="biz:category:bizCategoryInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCategoryInfo">
			<tr>

				<td><a href="${ctx}/biz/category/bizCategoryInfo/form?id=${bizCategoryInfo.id}">
						${bizCategoryInfo.name}</a>
				</td>
				<td>
					${bizCategoryInfo.description}
				</td>
				<td>
					${bizCategoryInfo.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizCategoryInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(bizCategoryInfo.status, 'status', '')}
				</td>
				<shiro:hasPermission name="biz:category:bizCategoryInfo:edit"><td>
    				<a href="${ctx}/biz/category/bizCategoryInfo/form?id=${bizCategoryInfo.id}">修改</a>
					<a href="${ctx}/biz/category/bizCategoryInfo/delete?id=${bizCategoryInfo.id}" onclick="return confirmx('确认要删除该商品类别吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
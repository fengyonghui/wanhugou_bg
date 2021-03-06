<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多管理</title>
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
		<li class="active"><a href="${ctx}/product/bizProdCate/">产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多列表</a></li>
		<shiro:hasPermission name="product:bizProdCate:edit"><li><a href="${ctx}/product/bizProdCate/form">产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizProdCate" action="${ctx}/product/bizProdCate/" method="post" class="breadcrumb form-search">
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
				<th>biz_product_info.id</th>
				<th>biz_category_info.id</th>
				<shiro:hasPermission name="product:bizProdCate:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizProdCate">
			<tr>
				<td><a href="${ctx}/product/bizProdCate/form?id=${bizProdCate.id}">
					${bizProdCate.prodId}
				</a></td>
				<td>
					${bizProdCate.catId}
				</td>
				<shiro:hasPermission name="product:bizProdCate:edit"><td>
    				<a href="${ctx}/product/bizProdCate/form?id=${bizProdCate.id}">修改</a>
					<a href="${ctx}/product/bizProdCate/delete?id=${bizProdCate.id}" onclick="return confirmx('确认要删除该产品分类表\n产品 &lt;&mdash;&gt; 分类 多对多吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
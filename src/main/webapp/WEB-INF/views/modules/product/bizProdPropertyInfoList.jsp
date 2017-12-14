<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>属性表管理</title>
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
		<li class="active"><a href="${ctx}/product/bizProdPropertyInfo/">属性表列表</a></li>
		<shiro:hasPermission name="product:bizProdPropertyInfo:edit"><li><a href="${ctx}/product/bizProdPropertyInfo/form">属性表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizProdPropertyInfo" action="${ctx}/product/bizProdPropertyInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>属性名称：</label>
				<form:input path="propName" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>biz_product_info.id</th>
				<th>属性名称</th>
				<th>属性描述</th>
				<shiro:hasPermission name="product:bizProdPropertyInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizProdPropertyInfo">
			<tr>
				<td><a href="${ctx}/product/bizProdPropertyInfo/form?id=${bizProdPropertyInfo.id}">
					${bizProdPropertyInfo.prodId}
				</a></td>
				<td>
					${bizProdPropertyInfo.propName}
				</td>
				<td>
					${bizProdPropertyInfo.propDescription}
				</td>
				<shiro:hasPermission name="product:bizProdPropertyInfo:edit"><td>
    				<a href="${ctx}/product/bizProdPropertyInfo/form?id=${bizProdPropertyInfo.id}">修改</a>
					<a href="${ctx}/product/bizProdPropertyInfo/delete?id=${bizProdPropertyInfo.id}" onclick="return confirmx('确认要删除该属性表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
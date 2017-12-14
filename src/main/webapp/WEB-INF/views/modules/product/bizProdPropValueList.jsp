<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>记录产品所有属性值管理</title>
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
		<li class="active"><a href="${ctx}/product/bizProdPropValue/">记录产品所有属性值列表</a></li>
		<shiro:hasPermission name="product:bizProdPropValue:edit"><li><a href="${ctx}/product/bizProdPropValue/form">记录产品所有属性值添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizProdPropValue" action="${ctx}/product/bizProdPropValue/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>属性名称：</label>
				<form:input path="propName" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="product:bizProdPropValue:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizProdPropValue">
			<tr>
				<shiro:hasPermission name="product:bizProdPropValue:edit"><td>
    				<a href="${ctx}/product/bizProdPropValue/form?id=${bizProdPropValue.id}">修改</a>
					<a href="${ctx}/product/bizProdPropValue/delete?id=${bizProdPropValue.id}" onclick="return confirmx('确认要删除该记录产品所有属性值吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
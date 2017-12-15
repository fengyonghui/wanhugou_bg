<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>sku属性管理</title>
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
		<li class="active"><a href="${ctx}/biz/sku/bizSkuPropValue/">sku属性列表</a></li>
		<shiro:hasPermission name="biz:sku:bizSkuPropValue:edit"><li><a href="${ctx}/biz/sku/bizSkuPropValue/form">sku属性添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSkuPropValue" action="${ctx}/biz/sku/bizSkuPropValue/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>biz_sku_info.id：</label>
				<form:input path="skuId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>prop_name：</label>
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
				<shiro:hasPermission name="biz:sku:bizSkuPropValue:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSkuPropValue">
			<tr>
				<shiro:hasPermission name="biz:sku:bizSkuPropValue:edit"><td>
    				<a href="${ctx}/biz/sku/bizSkuPropValue/form?id=${bizSkuPropValue.id}">修改</a>
					<a href="${ctx}/biz/sku/bizSkuPropValue/delete?id=${bizSkuPropValue.id}" onclick="return confirmx('确认要删除该sku属性吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分类下的属性值管理</title>
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
		<li class="active"><a href="${ctx}/biz/category/bizCatePropValue/">分类下的属性值列表</a></li>
		<shiro:hasPermission name="biz:category:bizCatePropValue:edit"><li><a href="${ctx}/biz/category/bizCatePropValue/form">分类下的属性值添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCatePropValue" action="${ctx}/biz/category/bizCatePropValue/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>biz_cate_property_info.id：</label>
				<form:input path="propId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>1:active 0:inactive：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>记录该属性值</th>
				<th>create_id</th>
				<th>create_time</th>
				<th>1:active 0:inactive</th>
				<shiro:hasPermission name="biz:category:bizCatePropValue:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCatePropValue">
			<tr>
				<td><a href="${ctx}/biz/category/bizCatePropValue/form?id=${bizCatePropValue.id}">
					${bizCatePropValue.value}
				</a></td>
				<td>
					${bizCatePropValue.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizCatePropValue.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(bizCatePropValue.status, 'status', '')}
				</td>
				<shiro:hasPermission name="biz:category:bizCatePropValue:edit"><td>
    				<a href="${ctx}/biz/category/bizCatePropValue/form?id=${bizCatePropValue.id}">修改</a>
					<a href="${ctx}/biz/category/bizCatePropValue/delete?id=${bizCatePropValue.id}" onclick="return confirmx('确认要删除该分类下的属性值吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标签属性值管理</title>
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
		<li class="active"><a href="${ctx}/sys/attribute/attributeValue/">标签属性值列表</a></li>
		<shiro:hasPermission name="sys:attribute:attributeValue:edit"><li><a href="${ctx}/sys/attribute/attributeValue/form">标签属性值添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="attributeValue" action="${ctx}/sys/attribute/attributeValue/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>标签Id：</label>
				<form:input path="tagId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>对象名称，表名称：</label>
				<form:input path="objectName" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>对应表的主键：</label>
				<form:input path="objectId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>标签Id</th>
				<th>对象名称，表名称</th>
				<th>对应表的主键</th>
				<th>记录该属性值</th>
				<th>code</th>
				<shiro:hasPermission name="sys:attribute:attributeValue:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="attributeValue">
			<tr>
				<td><a href="${ctx}/sys/attribute/attributeValue/form?id=${attributeValue.id}">
					${attributeValue.tagId}
				</a></td>
				<td>
					${attributeValue.objectName}
				</td>
				<td>
					${attributeValue.objectId}
				</td>
				<td>
					${attributeValue.value}
				</td>
				<td>
					${attributeValue.code}
				</td>
				<shiro:hasPermission name="sys:attribute:attributeValue:edit"><td>
    				<a href="${ctx}/sys/attribute/attributeValue/form?id=${attributeValue.id}">修改</a>
					<a href="${ctx}/sys/attribute/attributeValue/delete?id=${attributeValue.id}" onclick="return confirmx('确认要删除该标签属性值吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
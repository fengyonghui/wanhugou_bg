<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品种类管理管理</title>
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
		<li class="active"><a href="${ctx}/biz/category/bizVarietyInfo/">产品种类管理列表</a></li>
		<shiro:hasPermission name="biz:category:bizVarietyInfo:edit"><li><a href="${ctx}/biz/category/bizVarietyInfo/form">产品种类管理添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizVarietyInfo" action="${ctx}/biz/category/bizVarietyInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
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
				<th>名称</th>
				<th>英文编码</th>
				<th>分类描述</th>
				<shiro:hasPermission name="biz:category:bizVarietyInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizVarietyInfo">
			<tr>
				<td><a href="${ctx}/biz/category/bizVarietyInfo/form?id=${bizVarietyInfo.id}">
					${bizVarietyInfo.name}
				</a></td>
				<td>
					${bizVarietyInfo.code}
				</td>
				<td>
					${bizVarietyInfo.description}
				</td>
				<shiro:hasPermission name="biz:category:bizVarietyInfo:edit"><td>
    				<a href="${ctx}/biz/category/bizVarietyInfo/form?id=${bizVarietyInfo.id}">修改</a>
					<a href="${ctx}/biz/category/bizVarietyInfo/delete?id=${bizVarietyInfo.id}" onclick="return confirmx('确认要删除该产品种类管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
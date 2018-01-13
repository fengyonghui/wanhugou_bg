<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>货架信息管理</title>
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
		<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfInfo/">货架信息列表</a></li>
		<shiro:hasPermission name="biz:shelf:bizOpShelfInfo:edit"><li><a href="${ctx}/biz/shelf/bizOpShelfInfo/form">货架信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOpShelfInfo" action="${ctx}/biz/shelf/bizOpShelfInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>货架名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>货架名称</th>
				<%--<th>货架描述</th>--%>
				<th>创建时间</th>
				<th>更新人</th>
				<th>更新时间</th>
				<%--<shiro:hasPermission name="biz:shelf:bizOpShelfInfo:edit">--%>
					<th>操作</th>
				<%--</shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOpShelfInfo">
			<tr>
				<td><a href="${ctx}/biz/shelf/bizOpShelfInfo/form?id=${bizOpShelfInfo.id}">
					${bizOpShelfInfo.name}
				</a></td>
				<%--<td>
					${description}
				</td>--%>
				<td>
					<fmt:formatDate value="${bizOpShelfInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizOpShelfInfo.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizOpShelfInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:shelf:bizOpShelfInfo:edit"><td>
    				<a href="${ctx}/biz/shelf/bizOpShelfInfo/form?id=${bizOpShelfInfo.id}">修改</a>
					<a href="${ctx}/biz/shelf/bizOpShelfInfo/delete?id=${bizOpShelfInfo.id}" onclick="return confirmx('确认要删除该货架信息吗？', this.href)">删除</a>
				</td>
				</shiro:hasPermission>
				<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><td>
					<a href="${ctx}/biz/shelf/bizOpShelfInfo/form?id=${bizOpShelfInfo.id}">商品上下架管理</a>
					<a href="${ctx}/biz/shelf/bizOpShelfInfo/shelfManagementList?id=${bizOpShelfInfo.id}">货架管理员</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
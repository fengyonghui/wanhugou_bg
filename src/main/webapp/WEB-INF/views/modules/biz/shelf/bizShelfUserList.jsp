<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>货架用户中间表管理</title>
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
		<li class="active"><a href="${ctx}/biz/shelf/bizShelfUser/">货架用户中间表列表</a></li>
		<shiro:hasPermission name="biz:shelf:bizShelfUser:edit"><li><a href="${ctx}/biz/shelf/bizShelfUser/form">货架用户中间表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizShelfUser" action="${ctx}/biz/shelf/bizShelfUser/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>货架ID，biz_op_shelf_info：</label>
				<form:input path="shelfInfo" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>用户ID,sys_user.id：</label>
				<sys:treeselect id="user" name="user" value="${bizShelfUser.user}" labelName="" labelValue="${bizShelfUser.}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:shelf:bizShelfUser:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizShelfUser">
			<tr>
				<shiro:hasPermission name="biz:shelf:bizShelfUser:edit"><td>
    				<a href="${ctx}/biz/shelf/bizShelfUser/form?id=${bizShelfUser.id}">修改</a>
					<a href="${ctx}/biz/shelf/bizShelfUser/delete?id=${bizShelfUser.id}" onclick="return confirmx('确认要删除该货架用户中间表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
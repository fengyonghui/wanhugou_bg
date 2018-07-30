<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发送站内信管理</title>
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
		<li class="active"><a href="${ctx}/biz/message/bizMessageInfo/">站内信列表</a></li>
		<shiro:hasPermission name="biz:message:bizMessageInfo:edit"><li><a href="${ctx}/biz/message/bizMessageInfo/form">站内信添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizMessageInfo" action="${ctx}/biz/message/bizMessageInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>标题：</label>
				<form:input path="title" htmlEscape="false" maxlength="128" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>标题</th>
				<th>内容</th>
				<shiro:hasPermission name="biz:message:bizMessageInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizMessageInfo">
			<tr>
				<td><a href="${ctx}/biz/message/bizMessageInfo/form?id=${bizMessageInfo.id}">
					${bizMessageInfo.title}
				</a></td>
				<td><a href="${ctx}/biz/message/bizMessageInfo/form?id=${bizMessageInfo.id}">
					${bizMessageInfo.content}
				</a></td>
				<shiro:hasPermission name="biz:message:bizMessageInfo:edit"><td>
    				<a href="${ctx}/biz/message/bizMessageInfo/form?id=${bizMessageInfo.id}">修改</a>
					<a href="${ctx}/biz/message/bizMessageInfo/delete?id=${bizMessageInfo.id}" onclick="return confirmx('确认要删除该发送站内信吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
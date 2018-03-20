<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>意见管理</title>
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
		<li class="active"><a href="${ctx}/biz/feedback/bizFeedback/">意见列表</a></li>
		<shiro:hasPermission name="biz:feedback:bizFeedback:edit"><li><a href="${ctx}/biz/feedback/bizFeedback/form">意见添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizFeedback" action="${ctx}/biz/feedback/bizFeedback/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>意见：</label>
				<form:input path="userFeedback" htmlEscape="false" maxlength="200" class="input-medium	"/>
			</li>
			<li><label>创建人：</label>
				<form:input path="createBy.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>意见</th>
				<th>创建人</th>
				<th>创建时间</th>
				<shiro:hasPermission name="biz:feedback:bizFeedback:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizFeedback">
			<tr>
				<td><a href="${ctx}/biz/feedback/bizFeedback/form?id=${bizFeedback.id}">
					${bizFeedback.userFeedback}
				</a></td>
				<td>
					${bizFeedback.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizFeedback.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:feedback:bizFeedback:edit"><td>
    				<a href="${ctx}/biz/feedback/bizFeedback/form?id=${bizFeedback.id}">修改</a>
					<a href="${ctx}/biz/feedback/bizFeedback/delete?id=${bizFeedback.id}" onclick="return confirmx('确认要删除该意见吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
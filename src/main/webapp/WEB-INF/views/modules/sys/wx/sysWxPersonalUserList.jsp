<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>注册用户管理</title>
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
		<li class="active"><a href="${ctx}/sys/wx/sysWxPersonalUser/">注册用户列表</a></li>
		<%--<shiro:hasPermission name="sys:wx:sysWxPersonalUser:edit"><li><a href="${ctx}/sys/wx/sysWxPersonalUser/form">注册用户添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="sysWxPersonalUser" action="${ctx}/sys/wx/sysWxPersonalUser/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户名：</label>
				<form:input path="user.name" htmlEscape="false" maxlength="60" class="input-medium"/>
			</li>
			<li><label>昵称：</label>
				<form:input path="nickname" htmlEscape="false" maxlength="60" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户名</th>
				<th>昵称</th>
				<th>头像</th>
				<%--<th>openid</th>--%>
				<%--<th>subscribe</th>--%>
				<th>性别</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="sys:wx:sysWxPersonalUser:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysWxPersonalUser">
			<tr>
				<td>
					<%--<a href="${ctx}/sys/wx/sysWxPersonalUser/form?id=${sysWxPersonalUser.id}">--%>
					${sysWxPersonalUser.user.name}
					<%--</a>--%>
				</td>
				<td>
					${sysWxPersonalUser.nickname}
				</td>
				<td>
					<img src="${sysWxPersonalUser.headImgUrl}" width="50px" height="50px"/>
				</td>
				<%--<td>--%>
					<%--${sysWxPersonalUser.openid}--%>
				<%--</td>--%>
				<%--<td>--%>
					<%--${sysWxPersonalUser.subscribe}--%>
				<%--</td>--%>
				<td>
					${sysWxPersonalUser.sex}
				</td>
				<td>
					<fmt:formatDate value="${sysWxPersonalUser.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${sysWxPersonalUser.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sys:wx:sysWxPersonalUser:edit"><td>
    				<%--<a href="${ctx}/sys/wx/sysWxPersonalUser/form?id=${sysWxPersonalUser.id}">修改</a>--%>
					<a href="${ctx}/sys/wx/sysWxPersonalUser/delete?id=${sysWxPersonalUser.id}" onclick="return confirmx('确认要删除该注册用户吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>会员搜索</title>
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
		<li class="active"><a href="${ctx}/sys/user/contact">联系人列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/contact" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>联系人姓名：</label>
				<form:input path="name" htmlEscape="false" maxlength="11" class="input-medium"/>
                <%--<input id="id" type="hidden" name="id" value="${id}"/>--%>
			</li>
			<li><label>联系人电话：</label>
				<form:input path="mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>会员名称：</label>
				<form:input path="company.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>会员名称</th>
				<th>联系人姓名</th>
				<th>联系人电话</th>
				<th>客户专员</th>
				<th>采购中心</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="user">
			<tr>
				<td><a href="${ctx}/sys/office/form?id=${user.company.id}&flag=${flag}">
						${user.company.name}</a>
				</td>
				<td><a href="${ctx}/sys/user/form?id=${user.id}&flag=${flag}">
						${user.name}</a>
				</td>
				<td>
					${user.mobile}
				</td>
				<td><a href="${ctx}/sys/user/form?id=${user.user.id}&flag=${flag}">
						${user.user.name}</a>
				</td>
				<td><a href="${ctx}/sys/office/form?id=${user.cent.id}&flag=${flag}">
						${user.cent.name}</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
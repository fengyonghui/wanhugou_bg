<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备管理</title>
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
		<li class="active"><a href="${ctx}/biz/paltform/bizPlatformInfo/">设备列表</a></li>
		<shiro:hasPermission name="biz:paltform:bizPlatformInfo:edit"><li><a href="${ctx}/biz/paltform/bizPlatformInfo/form">设备添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPlatformInfo" action="${ctx}/biz/paltform/bizPlatformInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>产品平台：</label>
				<form:input path="name" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>产品平台</th>
				<th>产品平台描述</th>
				<th>上线日期</th>
				<th>最后版本</th>
				<shiro:hasPermission name="biz:paltform:bizPlatformInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPlatformInfo">
			<tr>
				<td>
					<a href="${ctx}/biz/paltform/bizPlatformInfo/form?id=${bizPlatformInfo.id}">
					${bizPlatformInfo.name}</a>
				</td>
				<td>
					${bizPlatformInfo.description}
				</td>
				<td>
					<fmt:formatDate value="${bizPlatformInfo.onlineDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizPlatformInfo.lastVersion}
				</td>
				<shiro:hasPermission name="biz:paltform:bizPlatformInfo:edit"><td>
    				<a href="${ctx}/biz/paltform/bizPlatformInfo/form?id=${bizPlatformInfo.id}">修改</a>
					<a href="${ctx}/biz/paltform/bizPlatformInfo/delete?id=${bizPlatformInfo.id}" onclick="return confirmx('确认要删除该设备吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
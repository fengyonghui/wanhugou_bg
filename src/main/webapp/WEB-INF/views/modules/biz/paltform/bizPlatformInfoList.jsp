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
			<li><label>产品平台名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>create_id：</label>
				<form:input path="createId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>update_id：</label>
				<form:input path="updateId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>产品平台名称</th>
				<th>产品平台描述</th>
				<th>产品版本</th>
				<th>create_id</th>
				<th>create_time</th>
				<th>status</th>
				<th>update_id</th>
				<th>update_time</th>
				<shiro:hasPermission name="biz:paltform:bizPlatformInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPlatformInfo">
			<tr>
				<td><a href="${ctx}/biz/paltform/bizPlatformInfo/form?id=${bizPlatformInfo.id}">
					${bizPlatformInfo.name}
				</a></td>
				<td>
					${bizPlatformInfo.description}
				</td>
				<td>
					${bizPlatformInfo.uVersion}
				</td>
				<td>
					${bizPlatformInfo.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizPlatformInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(bizPlatformInfo.status, 'status', '')}
				</td>
				<td>
					${bizPlatformInfo.updateId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizPlatformInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
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
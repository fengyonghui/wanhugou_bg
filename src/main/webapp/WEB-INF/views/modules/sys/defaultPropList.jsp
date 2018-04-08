<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统属性默认表管理</title>
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
		<li class="active"><a href="${ctx}/sys/defaultProp/">系统属性默认表列表</a></li>
		<shiro:hasPermission name="sys:defaultProp:edit"><li><a href="${ctx}/sys/defaultProp/form">系统属性默认表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="defaultProp" action="${ctx}/sys/defaultProp/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>属性标签：</label>
				<form:input path="propKey" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>系统属性标签</th>
				<th>系统属性值(ID)</th>
				<th>属性描述</th>
				<shiro:hasPermission name="sys:defaultProp:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="defaultProp">
			<tr>
				<td><a href="${ctx}/sys/defaultProp/form?id=${defaultProp.id}">
					${defaultProp.propKey}
				</a></td>
				<td>
					${defaultProp.propValue}
				</td>
				<td>
					${defaultProp.propDesc}
				</td>
				</td>
				<shiro:hasPermission name="sys:defaultProp:edit"><td>
    				<a href="${ctx}/sys/defaultProp/form?id=${defaultProp.id}">修改</a>
					<a href="${ctx}/sys/defaultProp/delete?id=${defaultProp.id}" onclick="return confirmx('确认要删除该系统属性默认表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
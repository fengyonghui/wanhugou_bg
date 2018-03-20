<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>标签属性管理</title>
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
		<li class="active"><a href="${ctx}/sys/tag/tagInfo/">标签属性列表</a></li>
		<shiro:hasPermission name="sys:tag:tagInfo:edit"><li><a href="${ctx}/sys/tag/tagInfo/form">标签属性添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="tagInfo" action="${ctx}/sys/tag/tagInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>标签名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>标签类型：</label>
				<form:select path="level" class="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('level')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>标签名称</th>
				<th>字典表类型</th>
				<th>标签类型</th>
				<shiro:hasPermission name="sys:tag:tagInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="tagInfo">
			<tr>
				<td><a href="${ctx}/sys/tag/tagInfo/form?id=${tagInfo.id}">
					${tagInfo.name}
				</a></td>
				<td>
					<c:choose>
					<c:when test="${tagInfo.dict.type != ''}">
					 	${tagInfo.dict.type}
					</c:when>
					<c:otherwise>
						<font>未知值,请进行输入</font>
					</c:otherwise>
					</c:choose>
				</td>
				<td>
					${fns:getDictLabel(tagInfo.level, 'level', '未知类型')}
				</td>
				<shiro:hasPermission name="sys:tag:tagInfo:edit"><td>
    				<a href="${ctx}/sys/tag/tagInfo/form?id=${tagInfo.id}">修改</a>
					<a href="${ctx}/sys/tag/tagInfo/delete?id=${tagInfo.id}" onclick="return confirmx('确认要删除该标签属性吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
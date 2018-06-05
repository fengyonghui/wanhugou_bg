<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分类与品类主管 关联管理</title>
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
		<li class="active"><a href="${ctx}/biz/variety/bizVarietyUserInfo/">分类与品类主管 关联列表</a></li>
		<%--<shiro:hasPermission name="biz:variety:bizVarietyUserInfo:edit"><li><a href="${ctx}/biz/variety/bizVarietyUserInfo/form">分类与品类主管 关联添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizVarietyUserInfo" action="${ctx}/biz/variety/bizVarietyUserInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>分类名称：</label>
				<form:input path="varietyInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>类别名称</th>
				<th>品类主管</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:variety:bizVarietyUserInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="varietyUserInfo">
			<tr>
				<td><a href="${ctx}/biz/variety/bizVarietyUserInfo/form?id=${varietyUserInfo.id}">
						${varietyUserInfo.varietyInfo.name}
				</a></td>
				<td>
					${varietyUserInfo.user.name}
				</td>
				<td>
					${varietyUserInfo.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${varietyUserInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${varietyUserInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:variety:bizVarietyUserInfo:edit"><td>
    				<%--<a href="${ctx}/biz/variety/bizVarietyUserInfo/form?id=${varietyUserInfo.id}">修改</a>--%>
					<a href="${ctx}/biz/variety/bizVarietyUserInfo/delete?id=${varietyUserInfo.id}" onclick="return confirmx('确认要删除该品类与用户 关联吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
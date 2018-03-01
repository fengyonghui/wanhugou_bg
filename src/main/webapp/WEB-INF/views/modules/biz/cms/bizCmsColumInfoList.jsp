<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>栏目管理</title>
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
		<li class="active"><a href="${ctx}/biz/cms/bizCmsColumInfo/">栏目列表</a></li>
		<shiro:hasPermission name="biz:cms:bizCmsColumInfo:edit"><li><a href="${ctx}/biz/cms/bizCmsColumInfo/form">栏目添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCmsColumInfo" action="${ctx}/biz/cms/bizCmsColumInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>产品页面：</label>
				<form:select path="pageInfo.id" class="input-xlarge required" id="pageInfoId">
					<form:option value="" label="请选择"/>
					<form:options items="${pageInfoList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>栏目标题：</label>
				<form:input path="title" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>产品页面</th>
				<th>栏目标题</th>
				<th>货架名称</th>
				<th>栏目排序</th>
				<th>栏目描述</th>
				<shiro:hasPermission name="biz:cms:bizCmsColumInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCmsColumInfo">
			<tr>
				<td>
					<%--<a href="${ctx}/biz/cms/bizCmsPageInfo/form?id=${bizCmsColumInfo.id}"></a>--%>
					${bizCmsColumInfo.pageInfo.name}
				</td>
				<td>
					${bizCmsColumInfo.title}
				</td>
				<td>
					${bizCmsColumInfo.shelfInfo.name}
				</td>
				<td>
					${bizCmsColumInfo.setOrder}
				</td>
				<td>
					${bizCmsColumInfo.description}
				</td>
				<shiro:hasPermission name="biz:cms:bizCmsColumInfo:edit"><td>
    				<a href="${ctx}/biz/cms/bizCmsColumInfo/form?id=${bizCmsColumInfo.id}">修改</a>
					<a href="${ctx}/biz/cms/bizCmsColumInfo/delete?id=${bizCmsColumInfo.id}" onclick="return confirmx('确认要删除该栏目吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
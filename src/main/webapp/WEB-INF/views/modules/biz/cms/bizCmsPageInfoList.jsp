<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品页面管理</title>
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
		<li class="active"><a href="${ctx}/biz/cms/bizCmsPageInfo/">产品页面列表</a></li>
		<shiro:hasPermission name="biz:cms:bizCmsPageInfo:edit"><li><a href="${ctx}/biz/cms/bizCmsPageInfo/form">产品页面添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCmsPageInfo" action="${ctx}/biz/cms/bizCmsPageInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>产品名称：</label>
				<form:select path="platInfo.id" class="input-xlarge required" id="platInfoId">
					<form:option value="" label="请选择"/>
					<form:options items="${platList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>页面名称：</label>
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
				<th>页面名称</th>
				<th>产品名称</th>
				<th>页面描述</th>
				<shiro:hasPermission name="biz:cms:bizCmsPageInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCmsPageInfo">
			<tr>
				<td><a href="${ctx}/biz/cms/bizCmsPageInfo/form?id=${bizCmsPageInfo.id}">
					${bizCmsPageInfo.name}</a>
				</td>
				<td>
					${bizCmsPageInfo.platInfo.name}
				</td>
				<td>
					${bizCmsPageInfo.description}
				</td>
				<shiro:hasPermission name="biz:cms:bizCmsPageInfo:edit"><td>
    				<a href="${ctx}/biz/cms/bizCmsPageInfo/form?id=${bizCmsPageInfo.id}">修改</a>
					<a href="${ctx}/biz/cms/bizCmsPageInfo/delete?id=${bizCmsPageInfo.id}" onclick="return confirmx('确认要删除该产品页面吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
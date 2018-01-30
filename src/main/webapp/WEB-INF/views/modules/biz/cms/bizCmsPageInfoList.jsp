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
			<li><label>id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>biz_platform_info.id：</label>
				<form:input path="platId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>页面名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>页面描述：</label>
				<form:input path="description" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<li><label>1active ; 0 inactive：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li><label>create_id：</label>
				<form:input path="createId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>create_time：</label>
				<input name="createTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizCmsPageInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>u_version：</label>
				<form:input path="uVersion" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>update_id：</label>
				<form:input path="updateId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>update_time：</label>
				<input name="updateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizCmsPageInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>id</th>
				<th>biz_platform_info.id</th>
				<th>页面名称</th>
				<th>页面描述</th>
				<th>1active ; 0 inactive</th>
				<th>create_id</th>
				<th>create_time</th>
				<th>u_version</th>
				<th>update_id</th>
				<th>update_time</th>
				<shiro:hasPermission name="biz:cms:bizCmsPageInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCmsPageInfo">
			<tr>
				<td><a href="${ctx}/biz/cms/bizCmsPageInfo/form?id=${bizCmsPageInfo.id}">
					${bizCmsPageInfo.id}
				</a></td>
				<td>
					${bizCmsPageInfo.platId}
				</td>
				<td>
					${bizCmsPageInfo.name}
				</td>
				<td>
					${bizCmsPageInfo.description}
				</td>
				<td>
					${fns:getDictLabel(bizCmsPageInfo.status, 'status', '')}
				</td>
				<td>
					${bizCmsPageInfo.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizCmsPageInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizCmsPageInfo.uVersion}
				</td>
				<td>
					${bizCmsPageInfo.updateId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizCmsPageInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
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
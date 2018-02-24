<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商拓展表管理</title>
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
		<li class="active"><a href="${ctx}/biz/vend/bizVendInfo/">供应商拓展表列表</a></li>
		<shiro:hasPermission name="biz:vend:bizVendInfo:edit"><li><a href="${ctx}/biz/vend/bizVendInfo/form">供应商拓展表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizVendInfo" action="${ctx}/biz/vend/bizVendInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>vend_name：</label>
				<form:input path="vendName" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>cate_name：</label>
				<form:input path="cateName" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>vend_name</th>
				<th>cate_id</th>
				<th>cate_name</th>
				<th>code</th>
				<th>创建时间</th>
				<shiro:hasPermission name="biz:vend:bizVendInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizVendInfo">
			<tr>
				<td><a href="${ctx}/biz/vend/bizVendInfo/form?id=${bizVendInfo.id}">
					${bizVendInfo.vendName}
				</a></td>
				<td>
					${bizVendInfo.cateId}
				</td>
				<td>
					${bizVendInfo.cateName}
				</td>
				<td>
					${bizVendInfo.code}
				</td>
				<td>
					<fmt:formatDate value="${bizVendInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:vend:bizVendInfo:edit"><td>
    				<a href="${ctx}/biz/vend/bizVendInfo/form?id=${bizVendInfo.id}">修改</a>
					<a href="${ctx}/biz/vend/bizVendInfo/delete?id=${bizVendInfo.id}" onclick="return confirmx('确认要删除该供应商拓展表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分类属性中间表管理</title>
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
		<li class="active"><a href="${ctx}/biz/product/bizVarietyAttr/">分类属性中间表列表</a></li>
		<shiro:hasPermission name="biz:product:bizVarietyAttr:edit"><li><a href="${ctx}/biz/product/bizVarietyAttr/form">分类属性中间表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizVarietyAttr" action="${ctx}/biz/product/bizVarietyAttr/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>分类：</label>
				<form:input path="varietyInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>属性：</label>
				<form:input path="attributeInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>分类</th>
				<th>属性</th>
				<shiro:hasPermission name="biz:product:bizVarietyAttr:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizVarietyAttr">
			<tr>
				<td>${bizVarietyAttr.varietyInfo.name}</td>
				<td>${bizVarietyAttr.attributeInfo.name}</td>
				<shiro:hasPermission name="biz:product:bizVarietyAttr:edit"><td>
    				<a href="${ctx}/biz/product/bizVarietyAttr/form?id=${bizVarietyAttr.id}">修改</a>
					<a href="${ctx}/biz/product/bizVarietyAttr/delete?id=${bizVarietyAttr.id}" onclick="return confirmx('确认要删除该分类属性中间表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
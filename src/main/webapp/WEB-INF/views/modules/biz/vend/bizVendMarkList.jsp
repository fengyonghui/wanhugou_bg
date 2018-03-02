<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商管理</title>
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
		<li class="active"><a href="${ctx}/biz/vend/bizVendMark/">供应商列表</a></li>
		<shiro:hasPermission name="biz:vend:bizVendMark:edit"><li><a href="${ctx}/biz/vend/bizVendMark/form">供应商添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizVendMark" action="${ctx}/biz/vend/bizVendMark/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${bizVendMark.user.id}" labelName="user.name" labelValue="${bizVendMark.user.name}"
					title="用户" url="/sys/user/treeData?type=6" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>供应商：</label>
				<form:input path="vendor.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户</th>
				<th>供应商名称</th>
				<th>创建人</th>
				<th>更新人</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:vend:bizVendMark:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizVendMark">
			<tr>
				<td><a href="${ctx}/biz/vend/bizVendMark/form?id=${bizVendMark.id}">
					${bizVendMark.user.name}
				</a></td>
				<td>
					${bizVendMark.vendor.name}
				</td>
				<td>
					${bizVendMark.createBy.name}
				</td>
				<td>
					${bizVendMark.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizVendMark.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:vend:bizVendMark:edit"><td>
    				<a href="${ctx}/biz/vend/bizVendMark/form?id=${bizVendMark.id}">修改</a>
					<a href="${ctx}/biz/vend/bizVendMark/delete?id=${bizVendMark.id}" onclick="return confirmx('确认要删除该供应商吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收藏微店管理</title>
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
		<li class="active"><a href="${ctx}/biz/shop/bizWeShopMark/">收藏微店列表</a></li>
		<shiro:hasPermission name="biz:shop:bizWeShopMark:edit"><li><a href="${ctx}/biz/shop/bizWeShopMark/form">收藏微店添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizWeShopMark" action="${ctx}/biz/shop/bizWeShopMark/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户名称：</label>
				<form:input path="user.name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>客户名称：</label>
				<form:input path="shopCust.name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>商铺名：</label>
				<form:input path="shopName" htmlEscape="false" maxlength="300" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户名称</th>
				<th>客户名称</th>
				<th>商铺名</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新人</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:shop:bizWeShopMark:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizWeShopMark">
			<tr>
				<td><a href="${ctx}/biz/shop/bizWeShopMark/form?id=${bizWeShopMark.id}">
						${bizWeShopMark.user.name}</a>
				</td>
				<td>
						${bizWeShopMark.shopCust.name}
				</td>
				<td>
						${bizWeShopMark.shopName}
				</td>
				<td>
						${bizWeShopMark.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizWeShopMark.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
						${bizWeShopMark.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizWeShopMark.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:shop:bizWeShopMark:edit"><td>
    				<a href="${ctx}/biz/shop/bizWeShopMark/form?id=${bizWeShopMark.id}">修改</a>
					<a href="${ctx}/biz/shop/bizWeShopMark/delete?id=${bizWeShopMark.id}" onclick="return confirmx('确认要删除该收藏微店吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
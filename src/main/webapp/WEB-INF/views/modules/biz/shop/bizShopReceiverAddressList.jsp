<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收货地址管理</title>
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
		<li class="active"><a href="${ctx}/biz/shop/bizShopReceiverAddress/">收货地址列表</a></li>
		<shiro:hasPermission name="biz:shop:bizShopReceiverAddress:edit"><li><a href="${ctx}/biz/shop/bizShopReceiverAddress/form">收货地址添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizShopReceiverAddress" action="${ctx}/biz/shop/bizShopReceiverAddress/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户名称：</label>
				<form:input path="user.name" htmlEscape="false" maxlength="80" class="input-medium"/>
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
				<th>收货地址</th>
				<th>是否默认</th>
				<th>联系人</th>
				<th>联系电话</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新人</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:shop:bizShopReceiverAddress:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizShopReceiverAddress">
			<tr>
				<td><a href="${ctx}/biz/shop/bizShopReceiverAddress/form?id=${bizShopReceiverAddress.id}">
						${bizShopReceiverAddress.user.name}</a>
				</td>
				<td>
						${bizShopReceiverAddress.bizLocation.pcrName}${bizShopReceiverAddress.bizLocation.address}
				</td>
				<td>
						${fns:getDictLabel(bizShopReceiverAddress.defaultStatus, 'sysadd_deFault', '未知状态')}
				</td>
				<td>
						${bizShopReceiverAddress.receiver}
				</td>
				<td>
						${bizShopReceiverAddress.phone}
				</td>
				<td>
						${bizShopReceiverAddress.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizShopReceiverAddress.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
						${bizShopReceiverAddress.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizShopReceiverAddress.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:shop:bizShopReceiverAddress:edit"><td>
    				<a href="${ctx}/biz/shop/bizShopReceiverAddress/form?id=${bizShopReceiverAddress.id}">修改</a>
					<a href="${ctx}/biz/shop/bizShopReceiverAddress/delete?id=${bizShopReceiverAddress.id}" onclick="return confirmx('确认要删除该收货地址吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
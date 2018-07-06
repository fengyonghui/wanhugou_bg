<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>购物车管理</title>
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
		<li class="active"><a href="${ctx}/biz/shop/bizShopCart/CendList">购物车列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="bizShopCart" action="${ctx}/biz/shop/bizShopCart/CendList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${bizShopCart.user.id}"  labelName="user.name"
						labelValue="${bizShopCart.user.name}" notAllowSelectParent="true"
						title="用户"  url="/sys/wx/sysWxPersonalUser/userTreeData" cssClass="input-medium"
						allowClear="true"  dataMsgRequired="必填信息"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>商品货架</th>
				<th>用户名称</th>
                <th>sku名称</th>
				<th>sku数量</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:shop:bizShopCart:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizShopCart">
			<tr>
				<td>
					${bizShopCart.skuShelfinfo.opShelfInfo.name}
                </td>
				<td>
					${bizShopCart.office.name}
				</td>
                <td>
                    ${bizShopCart.skuShelfinfo.skuInfo.name}
                </td>
				<td>
					${bizShopCart.skuQty}
				</td>
				<td>
					${bizShopCart.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizShopCart.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bizShopCart.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:shop:bizShopCart:edit"><td>
					<c:if test="${bizShopCart.delFlag!=null && bizShopCart.delFlag==1}">
						<a href="${ctx}/biz/shop/bizShopCart/delete?id=${bizShopCart.id}&cendDele=cendShopDele" onclick="return confirmx('确认要删除该购物车吗？', this.href)">删除</a>
					</c:if>
						<c:if test="${bizShopCart.delFlag!=null && bizShopCart.delFlag==0}">
							<a href="${ctx}/biz/shop/bizShopCart/recovery?id=${bizShopCart.id}&cendDele=cendShopDele" onclick="return confirmx('确认要恢复该购物车吗？', this.href)">恢复</a>
						</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
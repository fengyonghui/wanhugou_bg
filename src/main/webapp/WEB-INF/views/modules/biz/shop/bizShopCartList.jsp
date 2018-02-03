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
		<li class="active"><a href="${ctx}/biz/shop/bizShopCart/">购物车列表</a></li>
		<%--<shiro:hasPermission name="biz:shop:bizShopCart:edit"><li><a href="${ctx}/biz/shop/bizShopCart/form">购物车添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizShopCart" action="${ctx}/biz/shop/bizShopCart/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品货架：</label>
				<form:input path="skuShelfinfo.opShelfInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>采购商名称：</label>
				<sys:treeselect id="office" name="office.id" value="${bizShopCart.office.id}" labelName="office.name" labelValue="${bizShopCart.office.name}"
					title="部门" url="/sys/office/queryTreeList?type=6" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>采购商电话：</label>
				<form:input path="user.mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<%--<li><label>客户专员：</label>--%>
				<%--<sys:treeselect id="user" name="user.id" value="${bizShopCart.user.id}" labelName="user.name" labelValue="${bizShopCart.user.name}"--%>
                    <%--title="顾问" url="/sys/office/queryTreeList?type=8" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>--%>
				<%--<form:input path="user.name" htmlEscape="false" maxlength="11" class="input-medium"/>--%>
			<%--</li>--%>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>商品货架</th>
				<th>采购商名称</th>
				<%--<th>客户专员</th>--%>
				<th>采购商电话</th>
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
                    <%--<a href="${ctx}/biz/shop/bizShopCart/form?id=${bizShopCart.id}">--%>
					${bizShopCart.skuShelfinfo.opShelfInfo.name}
				<%--</a>--%>
                </td>
				<td>
					${bizShopCart.office.name}
				</td>
				<td>
						${bizShopCart.user.mobile}
				</td>
                <td>
                    ${bizShopCart.skuShelfinfo.skuInfo.name}
                </td>
				<%--<td>--%>
					<%--${bizShopCart.user.name}--%>
				<%--</td>--%>
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
    				<%--<a href="${ctx}/biz/shop/bizShopCart/form?id=${bizShopCart.id}">修改</a>--%>
					<a href="${ctx}/biz/shop/bizShopCart/delete?id=${bizShopCart.id}" onclick="return confirmx('确认要删除该购物车吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
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
	</ul>
	<form:form id="searchForm" modelAttribute="bizShopCart" action="${ctx}/biz/shop/bizShopCart/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品货架：</label>
				<form:input path="skuShelfinfo.opShelfInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>经销店名称：</label>
				<sys:treeselect id="office" name="office.id" value="${bizShopCart.office.id}" labelName="office.name" labelValue="${bizShopCart.office.name}"
					title="经销店" url="/sys/office/queryTreeList?type=6" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>经销店电话：</label>
				<form:input path="user.mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>客户端：</label>
				<form:select path="custType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_shop_cartType')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/></form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>客户端</th>
				<th>商品货架</th>
				<c:choose>
					<c:when test="${bizShopCart.custType == 2 || fn:contains(bizShopCart.skuShelfinfo.opShelfInfo.name,'微')}">
						<th>用户名称</th>
					</c:when>
					<c:otherwise>
						<th>经销店名称</th>
						<th>经销店电话</th>
					</c:otherwise>
				</c:choose>
                <th>sku名称</th>
				<th>sku数量</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间-${bizShopCart.custType}--${bizShopCart.skuShelfinfo.opShelfInfo.name}</th>
				<shiro:hasPermission name="biz:shop:bizShopCart:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="shopCart" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
					${fns:getDictLabel(shopCart.custType, 'biz_shop_cartType', '未知状态')}
				</td>
				<td>
					${shopCart.skuShelfinfo.opShelfInfo.name}
                </td>
				<c:choose>
					<c:when test="${bizShopCart.custType == 2 || fn:contains(bizShopCart.skuShelfinfo.opShelfInfo.name,'微')}">
						<td>${shopCart.office.name}</td>
						<c:if test="${bizShopCart.custType ==null && !fn:contains(bizShopCart.skuShelfinfo.opShelfInfo.name,'微')}">
							<td></td>
						</c:if>
					</c:when>
					<c:otherwise>
						<td>${shopCart.office.name}</td>
						<td>${shopCart.user.mobile}</td>
					</c:otherwise>
				</c:choose>

                <td>
                    ${shopCart.skuShelfinfo.skuInfo.name}
                </td>
				<td>
					${shopCart.skuQty}
				</td>
				<td>
					${shopCart.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${shopCart.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${shopCart.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:shop:bizShopCart:edit"><td>
    				<%--<a href="${ctx}/biz/shop/bizShopCart/form?id=${bizShopCart.id}">修改</a>--%>
					<c:if test="${shopCart.delFlag!=null && shopCart.delFlag==1}">
						<a href="${ctx}/biz/shop/bizShopCart/delete?id=${shopCart.id}" onclick="return confirmx('确认要删除该购物车吗？', this.href)">删除</a>
					</c:if>
						<c:if test="${shopCart.delFlag!=null && shopCart.delFlag==0}">
							<a href="${ctx}/biz/shop/bizShopCart/recovery?id=${shopCart.id}" onclick="return confirmx('确认要恢复该购物车吗？', this.href)">恢复</a>
						</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品管理</title>
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
		<li class="active"><a href="${ctx}/biz/sku/bizSkuInfo/">商品列表</a></li>
		<%--<shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><li><a href="${ctx}/biz/sku/bizSkuInfo/form">商品sku添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSkuInfo" action="${ctx}/biz/sku/bizSkuInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
            <li><label>商品类型：</label>
                <form:select path="skuType" class="input-xlarge required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('skuType')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/>
                </form:select>
            </li>
            <li><label>商品名称：</label>
                <form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
            </li>
			<li><label>商品编码：</label>
				<form:input path="partNo" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>商品图片</th>
				<th>商品名称</th>
				<th>商品类型</th>
				<th>产品名称</th>
				<th>商品编码</th>
				<%--<th>创建人</th>--%>
				<th>基础售价</th>
				<th>采购价格</th>
				<th>创建时间</th>
				<th>更新人</th>
				<%--<th>更新时间</th>--%>
				<%--<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">--%>
					<th>操作</th>
				<%--</shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSkuInfo">
			<tr>
				<td>
						<img src="${bizSkuInfo.defaultImgUrl}" width="120px" height="120px"/>
				</td>
				<td style="font-size: larger"><a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}&str=detail">
						${bizSkuInfo.name}</a>
					</td>
					<td>
                        ${fns:getDictLabel(bizSkuInfo.skuType, 'skuType', '未知类型')}
					</td>
					<td>
						${bizSkuInfo.productInfo.name}
					</td>
				    <td>
						<input name="partNo" value="${bizSkuInfo.partNo}" type="hidden"/>
						${bizSkuInfo.partNo}
					</td>
					<td>
						<input name="basePrice" value="${bizSkuInfo.basePrice}" type="hidden"/>
						${bizSkuInfo.basePrice}
					</td>
					<td>
						<input name="buyPrice" value="${bizSkuInfo.buyPrice}" type="hidden"/>
						${bizSkuInfo.buyPrice}
					</td>
					<%--<td>--%>
						<%--${bizSkuInfo.createBy.id}--%>
					<%--</td>--%>
					<td>
						<fmt:formatDate value="${bizSkuInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						${bizSkuInfo.updateBy.name}
					</td>
					<%--<td>--%>
						<%--<fmt:formatDate value="${bizSkuInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>--%>
					<%--</td>--%>
				<shiro:hasPermission name="biz:sku:bizSkuInfo:view"><td>
					<%--<a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}">修改</a>--%>
							<a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}&str=detail">详情</a>
						<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
							<a href="${ctx}/biz/sku/bizSkuInfo/delete?id=${bizSkuInfo.id}&sign=0" onclick="return confirmx('确认要删除该商品sku吗？', this.href)">删除</a>
						</shiro:hasPermission>
				</td></shiro:hasPermission>

			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
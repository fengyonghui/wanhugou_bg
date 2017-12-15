<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品信息表管理</title>
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
		<li class="active"><a href="${ctx}/product/bizProductInfo/">产品信息表列表</a></li>
		<shiro:hasPermission name="biz:product:bizProductInfo:edit"><li><a href="${ctx}/biz/product/bizProductInfo/form">产品信息表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizProductInfo" action="${ctx}/biz/product/bizProductInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品名称：</label>
				<form:input path="name" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>商品代码：</label>
				<form:input path="prodCode" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>品牌名称：</label>
				<form:input path="brandName" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<%--<li><label>工厂id：</label>--%>
				<%--<form:input path="vendorId" htmlEscape="false" maxlength="50" class="input-medium"/>--%>
			<%--</li>--%>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>商品名称</th>
				<th>商品代码</th>
				<th>品牌名称</th>
				<th>商品描述</th>
				<th>采购商</th>
				<th>最低售价</th>
				<th>最高售价</th>
				
				<shiro:hasPermission name="biz:product:bizProductInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizProductInfo">
			<tr>
				<td><a href="${ctx}/biz/product/bizProductInfo/form?id=${bizProductInfo.id}">
					${bizProductInfo.name}
				</a></td>
				<td>
					${bizProductInfo.prodCode}
				</td>
				<td>
					${bizProductInfo.brandName}
				</td>
				<td>
					${bizProductInfo.description}
				</td>
				<td>
					${bizProductInfo.office.name}
				</td>
				<td>
					${bizProductInfo.minPrice}
				</td>
				<td>
					${bizProductInfo.maxPrice}
				</td>
				<shiro:hasPermission name="biz:product:bizProductInfo:edit">
					<td>
    				<a href="${ctx}/biz/product/bizProductInfo/form?id=${bizProductInfo.id}">修改</a>
					<a href="${ctx}/biz/product/bizProductInfo/delete?id=${bizProductInfo.id}" onclick="return confirmx('确认要删除该产品信息表吗？', this.href)">删除</a>
						<a href="${ctx}/biz/product/bizProductInfo/form?id=${bizProductInfo.id}">sku商品管理</a>
 				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
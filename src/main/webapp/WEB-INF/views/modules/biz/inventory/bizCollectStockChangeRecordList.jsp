<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>库存变更记录管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizCollectGoodsRecord/stockChangeList">库存变更记录列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCollectGoodsRecord" action="${ctx}/biz/inventory/bizCollectGoodsRecord/stockChangeList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>查询类别：</label>
				<form:select path="queryClass" class="input-xlarge">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('change_record')}" itemLabel="label" itemValue="value"
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
				<th>仓库名称</th>
				<th>商品名称</th>
				<th>商品编号</th>
				<th>备货单号/订单号</th>
				<th>变更记录</th>
				<th>原库存数</th>
				<th>变更数量</th>
				<th>客户名称</th>
				<th>变更时间</th>
			</tr>
		</thead>
		<tbody>
		<%--出库记录--%>
		<c:forEach items="${pageSend.list}" var="collectGoods">
			<tr>
				<td>
				<%--<td><a href="${ctx}/biz/inventory/bizSendGoodsRecord/form?id=${collectGoods.id}"></a>--%>
					${collectGoods.invInfo.name}
				</td>
				<td>
						${collectGoods.skuInfo.name}
				</td>
				<td>
						${collectGoods.skuInfo.partNo}
				</td>
				<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${collectGoods.bizOrderHeader.id}&source=ghs">
						${collectGoods.orderNum}</a>
				</td>
				<td><font color="#B40404">出库记录</font></td>
				<td>${collectGoods.invOldNum}</td>
				<td>
					<font color="#B40404">-</font>${collectGoods.sendNum}
				</td>
				<td>
						${collectGoods.customer.name}
				</td>
				<td>
					<fmt:formatDate value="${collectGoods.sendDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
					<%--<shiro:hasPermission name="biz:inventory:bizSendGoodsRecord:edit"><td>
                        <%--<a href="${ctx}/biz/inventory/bizSendGoodsRecord/form?id=${bizSendGoodsRecord.id}">修改</a>--%>
					<%--<a href="${ctx}/biz/inventory/bizSendGoodsRecord/delete?id=${bizSendGoodsRecord.id}" onclick="return confirmx('确认要删除该供货记录吗？', this.href)">删除</a>--%>
					<%--</td></shiro:hasPermission>&ndash;%&gt;--%>
			</tr>
		</c:forEach>
		<%--入库记录--%>
		<c:forEach items="${pageGods.list}" var="collectGoodsRecord">
			<tr>
				<td>
						<%--<a href="${ctx}/biz/inventory/bizCollectGoodsRecord/form?id=${bizCollectGoodsRecord.id}"></a>--%>
						${collectGoodsRecord.invInfo.name}
				</td>
				<td>
						${collectGoodsRecord.skuInfo.name}
				</td>
				<td>
						${collectGoodsRecord.skuInfo.partNo}
				</td>
				<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${collectGoodsRecord.bizRequestHeader.id}&source=gh">
						${collectGoodsRecord.orderNum}</a>
				</td>
				<td><font color="#0B610B">入库记录</font></td>
				<td>${collectGoodsRecord.invOldNum}</td>
				<td>
					<font color="#0B610B">+</font>${collectGoodsRecord.receiveNum}
				</td>
				<td>
					${collectGoodsRecord.customer.id}
				</td>
				<td>
					<fmt:formatDate value="${collectGoodsRecord.receiveDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
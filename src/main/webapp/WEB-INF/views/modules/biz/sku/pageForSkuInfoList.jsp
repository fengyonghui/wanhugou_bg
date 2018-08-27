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
		<li class="active"><a href="${ctx}/biz/sku/bizSkuInfo/findPageForSkuInfo">商品订单列表</a></li>
		<%--<shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><li><a href="${ctx}/biz/sku/bizSkuInfo/form">商品sku添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSkuInfo" action="${ctx}/biz/sku/bizSkuInfo/findPageForSkuInfo" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>商品货号：</label>
				<form:input path="itemNo" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>订单号：</label>
				<form:input path="orderNum" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>采购中心：</label>
				<form:input path="centersName" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>创建日期：</label>
				<input name="orderCreatStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizSkuInfo.orderCreatStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				至
				<input name="orderCreatEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizSkuInfo.orderCreatEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>商品名称</th>
				<th>商品货号</th>
				<th>结算价</th>
				<th>(订单)销售价</th>
				<th>订单数量</th>
				<th>应付金额</th>
				<th>客户专员</th>
				<th>订单号</th>
				<th>采购中心</th>
				<th>现有库存</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSkuInfo" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
						${bizSkuInfo.name}
				</td>
				<td>
						${bizSkuInfo.itemNo}
				</td>
				<td>
						${bizSkuInfo.orderDetail.buyPrice}
				</td>
				<td>
						${bizSkuInfo.orderDetail.unitPrice}
				</td>
				<td>
						${bizSkuInfo.orderDetail.ordQty}
				</td>
				<td>
					<c:if test="${bizSkuInfo.orderDetail.unitPrice !=null && bizSkuInfo.orderDetail.ordQty !=null}">
						<fmt:formatNumber type="number" value=" ${bizSkuInfo.orderDetail.unitPrice * bizSkuInfo.orderDetail.ordQty}" pattern="0.00"/>
					</c:if>
				</td>
				<td>
						${bizSkuInfo.custName}
				</td>
				<td>
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizSkuInfo.orderId}&orderDetails=details&statu=${statu}">
								${bizSkuInfo.orderNum}</a>
				</td>
				<td>
						${bizSkuInfo.centersName}
				</td>
				<td>
						${bizSkuInfo.inventorySku.stockQty}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
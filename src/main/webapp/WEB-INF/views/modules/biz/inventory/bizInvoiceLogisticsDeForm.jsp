<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发货单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            });


	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li>
			<a href="${ctx}/biz/inventory/bizInvoice?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}&targetPage=logistics">物流单列表</a>
		</li>
		<li class="active">
			<a href="${ctx}/biz/inventory/bizInvoice/logisticsOrderDetail?id=${bizInvoice.id}&source=xq">物流单详情</a>
		</li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/save?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<th>发货号</th>
				<th>订单编号</th>
				<th>商品名称</th>
				<th>商品编号</th>
				<th>商品货号</th>
				<%--<th>已生成的采购单</th>--%>
				<th>商品出厂价</th>
				<th>供应商</th>
				<%--<th>供应商电话</th>--%>
				<th>商品单价</th>
				<th>采购数量</th>
				<c:if test="${bizOrderDetail.unitPrice !=null && bizOrderDetail.ordQty !=null}">
					<th>总 额</th>
				</c:if>
				<th>已发货数量</th>
				<%--<c:if test="${bizOrderDetail.orderHeader.bizStatus>=15 && bizOrderDetail.orderHeader.bizStatus!=45}">
					<th>发货方</th>
				</c:if>--%>
				<th>创建时间</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${logisticsDetailList}" var="bizOrderDetail">
				<tr>
					<td>
						<a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizOrderDetail.orderHeader.sendId}&source=xq">${bizOrderDetail.orderHeader.sendNum}</a>
					</td>
					<td>
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderDetail.orderHeader.id}&orderDetails=details">${bizOrderDetail.orderHeader.orderNum}</a>
					</td>
					<td>
							${bizOrderDetail.skuName}
					</td>
					<td>
							${bizOrderDetail.partNo}
					</td>
					<td>
							${bizOrderDetail.skuInfo.itemNo}
					</td>
					<%--<td>
						<a href="${ctx}/biz/po/bizPoHeader/form?id=${detailIdMap.get(bizOrderDetail.getLineNo())}">${orderNumMap.get(bizOrderDetail.getLineNo())}</a>
					</td>--%>
					<td>
							${bizOrderDetail.buyPrice}
					</td>
					<td>
							${bizOrderDetail.vendor.name}
					</td>
					<%--<td>
							${bizOrderDetail.primary.mobile}
					</td>--%>
					<td>
							${bizOrderDetail.unitPrice}
					</td>
					<td>
							${bizOrderDetail.ordQty}
					</td>
					<td>
						<c:if test="${bizOrderDetail.unitPrice !=null && bizOrderDetail.ordQty !=null}">
							<fmt:formatNumber type="number" value=" ${bizOrderDetail.unitPrice * bizOrderDetail.ordQty}" pattern="0.00"/>
						</c:if>
					</td>
					<td>
							${bizOrderDetail.sentQty}
					</td>
					<%--<c:if test="${bizOrderDetail.orderHeader.bizStatus>=15 && bizOrderDetail.orderHeader.bizStatus!=45}">
						<td>
								${bizOrderDetail.suplyis.name}
						</td>
					</c:if>--%>
					<td>
						<fmt:formatDate value="${bizOrderDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>

				</tr>
			</c:forEach>
			</tbody>
		</table>
		<div class="form-actions">
			<input onclick="window.print();" type="button" class="btn btn-primary" value="打印发货单" style="background:#F78181;"/>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
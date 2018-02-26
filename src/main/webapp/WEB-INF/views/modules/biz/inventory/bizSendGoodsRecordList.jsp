<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供货记录管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizSendGoodsRecord?bizStatus=${bizStatus}">供货记录列表</a></li>
		 <%--<shiro:hasPermission name="biz:inventory:bizSendGoodsRecord:edit"><li><a href="${ctx}/biz/inventory/bizSendGoodsRecord/form">供货记录添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSendGoodsRecord" action="${ctx}/biz/inventory/bizSendGoodsRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="bizStatus" name="bizStatus" type="hidden" value="${bizStatus}"/>
		<ul class="ul-form">
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>订单编号：</label>
				<form:input path="orderNum" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>商品编号：</label>
				<form:input path="skuInfo.partNo" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>仓库名称：</label>
				<form:input path="invInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<c:if test="${bizStatus==0}">
					<th>仓库名称</th>
				</c:if>
                <td>供货单号</td>
				<th>商品名称</th>
				<th>商品编号</th>
				<th>订单号</th>
				<th>供货数量</th>
				<th>客户</th>
				<c:if test="${bizStatus==1}">
					<th>物流商</th>
					<th>承运人</th>
					<th>运费</th>
					<th>操作费</th>
					<th>货值</th>
					<th>运费/货值</th>
					<th>物流结算方式</th>
					<th>物流信息图</th>
				</c:if>
				<th>供货时间</th>
				<c:if test="${fns:getUser().isAdmin()}">
				<shiro:hasPermission name="biz:inventory:bizSendGoodsRecord:edit"><th>操作</th></shiro:hasPermission>
				</c:if>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSendGoodsRecord">
			<tr>
				<c:if test="${bizStatus==0}">
					<td><a href="${ctx}/biz/inventory/bizSendGoodsRecord/form?id=${bizSendGoodsRecord.id}">
					</a>${bizSendGoodsRecord.invInfo.name}
					</td>
				</c:if>
                <td>
                    ${bizSendGoodsRecord.sendNumber}
                </td>
				<td>
					${bizSendGoodsRecord.skuInfo.name}
				</td>
				<td>
					${bizSendGoodsRecord.skuInfo.partNo}
				</td>
				<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${bizSendGoodsRecord.bizOrderHeader.id}&source=ghs">
					${bizSendGoodsRecord.orderNum}</a>
				</td>
				<td>
					${bizSendGoodsRecord.sendNum}
				</td>
				<td>
					${bizSendGoodsRecord.customer.name}
				</td>
				<c:if test="${bizStatus==1}">
					<td>
						${bizSendGoodsRecord.bizLogistics.name}
					</td>
					<td>
						${bizSendGoodsRecord.carrier}
					</td>
					<td>
						${bizSendGoodsRecord.freight}
					</td>
					<td>
						${bizSendGoodsRecord.operation}
					</td>
					<td>
						${bizSendGoodsRecord.valuePrice}
					</td>
					<td>
						<c:if test="${bizSendGoodsRecord.valuePrice != 0}">
						<fmt:formatNumber type="number" value="${bizSendGoodsRecord.freight*100/bizSendGoodsRecord.valuePrice}" maxFractionDigits="0"/>%
						</c:if>
					</td>
					<td>
						${fns:getDictLabel(bizSendGoodsRecord.settlementStatus, 'biz_settlement_status', '未知状态')}
					</td>
					<td>
						<img src="${bizSendGoodsRecord.imgUrl}"style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/>
					</td>
				</c:if>
				<td>
					<fmt:formatDate value="${bizSendGoodsRecord.sendDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<c:if test="${fns:getUser().isAdmin()}">
				<shiro:hasPermission name="biz:inventory:bizSendGoodsRecord:edit"><td>
    				<%--<a href="${ctx}/biz/inventory/bizSendGoodsRecord/form?id=${bizSendGoodsRecord.id}">修改</a>--%>
					<a href="${ctx}/biz/inventory/bizSendGoodsRecord/delete?id=${bizSendGoodsRecord.id}" onclick="return confirmx('确认要删除该供货记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
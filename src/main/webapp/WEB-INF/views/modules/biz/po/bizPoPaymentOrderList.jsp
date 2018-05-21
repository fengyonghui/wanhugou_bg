<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>支付申请管理</title>
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
		<li class="active"><a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/">支付申请列表</a></li>
	</ul>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>id</th>
				<th>付款金额</th>
				<th>实际付款金额</th>
				<th>最后付款时间</th>
				<th>当前状态</th>
				<shiro:hasPermission name="biz.po:bizpopaymentorder:bizPoPaymentOrder:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPoPaymentOrder">
			<tr>
				<td>
					${bizPoPaymentOrder.id}
				</td>
				<td>
					${bizPoPaymentOrder.total}
				</td>
				<td>
					${bizPoPaymentOrder.payTotal}
				</td>
				<td>
					${bizPoPaymentOrder.deadline}
				</td>
				<td>
					${bizPoPaymentOrder.bizStatus == 0 ? '未支付' : '已支付'}
				</td>
				<shiro:hasPermission name="biz.po:bizpopaymentorder:bizPoPaymentOrder:edit"><td>
					<c:if test="${bizPoPaymentOrder.id == bizPoHeader.bizPoPaymentOrder.id && bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'}">
						<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=pay">确认付款</a>
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div ><input type="button" class="btn" onclick="window.history.go(-1);" value="返回"/></div>
	<div class="pagination">${page}</div>
</body>
</html>
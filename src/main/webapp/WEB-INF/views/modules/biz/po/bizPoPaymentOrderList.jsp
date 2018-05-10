<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>BizPoPaymentOrder管理</title>
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
		<li class="active"><a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/">BizPoPaymentOrder列表</a></li>
		<shiro:hasPermission name="biz.po:bizpopaymentorder:bizPoPaymentOrder:edit"><li><a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/form">BizPoPaymentOrder添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPoPaymentOrder" action="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>采购单ID：</label>
				<form:input path="poHeaderId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>付款金额：</label>
				<form:input path="total" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>当前审核状态ID：</label>
				<form:input path="processId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>id</th>
				<th>采购单ID</th>
				<th>付款金额</th>
				<th>当前审核状态ID</th>
				<shiro:hasPermission name="biz.po:bizpopaymentorder:bizPoPaymentOrder:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPoPaymentOrder">
			<tr>
				<td><a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/form?id=${bizPoPaymentOrder.id}">
					${bizPoPaymentOrder.id}
				</a></td>
				<td>
					${bizPoPaymentOrder.poHeaderId}
				</td>
				<td>
					${bizPoPaymentOrder.total}
				</td>
				<td>
					${bizPoPaymentOrder.processId}
				</td>
				<shiro:hasPermission name="biz.po:bizpopaymentorder:bizPoPaymentOrder:edit"><td>
    				<a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/form?id=${bizPoPaymentOrder.id}">修改</a>
					<a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/delete?id=${bizPoPaymentOrder.id}" onclick="return confirmx('确认要删除该BizPoPaymentOrder吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
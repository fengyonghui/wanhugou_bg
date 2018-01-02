<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票详情管理</title>
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
		<li class="active"><a href="${ctx}/biz/invoice/bizInvoiceDetail/">发票详情列表</a></li>
		<shiro:hasPermission name="biz:invoice:bizInvoiceDetail:edit"><li><a href="${ctx}/biz/invoice/bizInvoiceDetail/form">发票详情添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInvoiceDetail" action="${ctx}/biz/invoice/bizInvoiceDetail/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>发票行号：</label>
				<form:input path="lineNo" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>销售订单.id：</label>
				<form:input path="orderHead.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>发票行号</th>
				<th>销售订单.id</th>
				<th>发票数额：</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:invoice:bizInvoiceDetail:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInvoiceDetail">
			<tr>
				<td><a href="${ctx}/biz/invoice/bizInvoiceDetail/form?id=${bizInvoiceDetail.id}">
					${bizInvoiceDetail.lineNo}
				</a></td>
				<td>
					${bizInvoiceDetail.orderHead.id}
				</td>
				<td>
					${bizInvoiceDetail.invAmt}
				</td>
				<td>
					${bizInvoiceDetail.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizInvoiceDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bizInvoiceDetail.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:invoice:bizInvoiceDetail:edit"><td>
    				<a href="${ctx}/biz/invoice/bizInvoiceDetail/form?id=${bizInvoiceDetail.id}">修改</a>
					<a href="${ctx}/biz/invoice/bizInvoiceDetail/delete?id=${bizInvoiceDetail.id}" onclick="return confirmx('确认要删除该发票详情吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
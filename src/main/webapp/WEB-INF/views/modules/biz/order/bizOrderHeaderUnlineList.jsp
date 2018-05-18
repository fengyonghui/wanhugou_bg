<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>线下支付订单管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizOrderHeaderUnline/">线下支付流水列表</a></li>
		<%--<shiro:hasPermission name="biz:order:bizOrderHeaderUnline:edit"><li><a href="${ctx}/biz/order/bizOrderHeaderUnline/form">线下支付订单添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderHeaderUnline" action="${ctx}/biz/order/bizOrderHeaderUnline/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>订单号：</label>
				<form:input path="orderHeader.orderNum" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>流水号：</label>
				<form:input path="serialNum" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><a href="${ctx}/biz/order/bizOrderHeader?statu=unline"><input id="btnCancel" class="btn" type="button" value="返 回"/></a></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>订单号</th>
				<th>流水号</th>
				<th>线下支付金额</th>
				<th>实收金额</th>
				<th>流水状态</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:order:bizOrderHeaderUnline:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOrderHeaderUnline">
			<tr>
				<td>${bizOrderHeaderUnline.orderHeader.orderNum}</td>
				<td>${bizOrderHeaderUnline.serialNum}</td>
				<td>${bizOrderHeaderUnline.unlinePayMoney}</td>
				<td>${bizOrderHeaderUnline.realMoney}</td>
				<c:if test="${bizOrderHeaderUnline.bizStatus == 0}">
					<td>未审核</td>
				</c:if>
				<c:if test="${bizOrderHeaderUnline.bizStatus == 1}">
					<td>审核通过</td>
				</c:if>
				<c:if test="${bizOrderHeaderUnline.bizStatus == 2}">
					<td>驳回</td>
				</c:if>
				<td><fmt:formatDate value="${bizOrderHeaderUnline.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${bizOrderHeaderUnline.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="biz:order:bizOrderHeaderUnline:view"><td>
					<a href="${ctx}/biz/order/bizOrderHeaderUnline/form?id=${bizOrderHeaderUnline.id}&source=detail">详情</a>
					<shiro:hasPermission name="biz:order:bizOrderHeaderUnline:edit">
						<c:if test="${bizOrderHeaderUnline.bizStatus != 1}">
							<a href="${ctx}/biz/order/bizOrderHeaderUnline/form?id=${bizOrderHeaderUnline.id}&source=examine">审核</a>
						</c:if>
					</shiro:hasPermission>
					<%--<a href="${ctx}/biz/order/bizOrderHeaderUnline/delete?id=${bizOrderHeaderUnline.id}" onclick="return confirmx('确认要删除该线下支付订单吗？', this.href)">删除</a>--%>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
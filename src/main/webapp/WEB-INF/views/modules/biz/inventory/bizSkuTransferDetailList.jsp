<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>库存调拨详情管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizSkuTransferDetail/">库存调拨详情列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizSkuTransferDetail:edit"><li><a href="${ctx}/biz/inventory/bizSkuTransferDetail/form">库存调拨详情添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSkuTransferDetail" action="${ctx}/biz/inventory/bizSkuTransferDetail/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>transfer_id：</label>
				<form:input path="transfer.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>biz_sku_info.id：</label>
				<form:input path="skuInfo.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>出库人：</label>
				<form:input path="fromInvOp.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>出库时间：</label>
				<input name="fromInvOpTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizSkuTransferDetail.fromInvOpTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>入库人：</label>
				<form:input path="toInvOp.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>入库时间：</label>
				<input name="toInvOpTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizSkuTransferDetail.toInvOpTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:inventory:bizSkuTransferDetail:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSkuTransferDetail">
			<tr>
				<shiro:hasPermission name="biz:inventory:bizSkuTransferDetail:edit"><td>
    				<a href="${ctx}/biz/inventory/bizSkuTransferDetail/form?id=${bizSkuTransferDetail.id}">修改</a>
					<a href="${ctx}/biz/inventory/bizSkuTransferDetail/delete?id=${bizSkuTransferDetail.id}" onclick="return confirmx('确认要删除该库存调拨详情吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
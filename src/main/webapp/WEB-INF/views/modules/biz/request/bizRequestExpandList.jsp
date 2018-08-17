<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货单扩展管理</title>
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
		<li class="active"><a href="${ctx}/biz/request/bizRequestExpand/">备货单扩展列表</a></li>
		<shiro:hasPermission name="biz:request:bizRequestExpand:edit"><li><a href="${ctx}/biz/request/bizRequestExpand/form">备货单扩展添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizRequestExpand" action="${ctx}/biz/request/bizRequestExpand/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>bizRequestHeader.id：</label>
				<form:input path="requestHeader" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>备货方：1.采购中心备货；2.供应商备货：</label>
				<form:input path="formType" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>供应商备货，对应的供应商ID：</label>
				<form:input path="vendor" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>当前支付单ID：</label>
				<form:input path="bizPoPaymentOrder" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:request:bizRequestExpand:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizRequestExpand">
			<tr>
				<shiro:hasPermission name="biz:request:bizRequestExpand:edit"><td>
    				<a href="${ctx}/biz/request/bizRequestExpand/form?id=${bizRequestExpand.id}">修改</a>
					<a href="${ctx}/biz/request/bizRequestExpand/delete?id=${bizRequestExpand.id}" onclick="return confirmx('确认要删除该备货单扩展吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单详细信息管理</title>
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
		<li class="active"><a href="${ctx}/biz/request/bizRequestDetail/">备货清单详细信息列表</a></li>
		<shiro:hasPermission name="biz:request:bizRequestDetail:edit"><li><a href="${ctx}/biz/request/bizRequestDetail/form">备货清单详细信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizRequestDetail" action="${ctx}/biz/request/bizRequestDetail/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>biz_sku_info.id：</label>
				<form:input path="skuId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>biz_request_header.id</th>
				<th>行号</th>
				<th>biz_sku_info.id</th>
				<th>请求数量</th>
				<th>收货数量</th>
				<shiro:hasPermission name="biz:request:bizRequestDetail:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizRequestDetail">
			<tr>
				<td><a href="${ctx}/biz/request/bizRequestDetail/form?id=${bizRequestDetail.id}">
					${bizRequestDetail.headerId}
				</a></td>
				<td>
					${bizRequestDetail.lineNo}
				</td>
				<td>
					${bizRequestDetail.skuId}
				</td>
				<td>
					${bizRequestDetail.reqQty}
				</td>
				<td>
					${bizRequestDetail.recvQty}
				</td>
				<shiro:hasPermission name="biz:request:bizRequestDetail:edit"><td>
    				<a href="${ctx}/biz/request/bizRequestDetail/form?id=${bizRequestDetail.id}">修改</a>
					<a href="${ctx}/biz/request/bizRequestDetail/delete?id=${bizRequestDetail.id}" onclick="return confirmx('确认要删除该备货清单详细信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>销售采购备货中间表管理</title>
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
		<li class="active"><a href="${ctx}/biz/request/bizPoOrderReq/">销售采购备货中间表列表</a></li>
		<shiro:hasPermission name="biz:request:bizPoOrderReq:edit"><li><a href="${ctx}/biz/request/bizPoOrderReq/form">销售采购备货中间表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPoOrderReq" action="${ctx}/biz/request/bizPoOrderReq/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>销售订单：</label>
				<form:input path="orderHeader" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>备货单：</label>
				<form:input path="requestHeader" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>采购单：</label>
				<form:input path="poHeader" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:request:bizPoOrderReq:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPoOrderReq">
			<tr>
				<shiro:hasPermission name="biz:request:bizPoOrderReq:edit"><td>
    				<a href="${ctx}/biz/request/bizPoOrderReq/form?id=${bizPoOrderReq.id}">修改</a>
					<a href="${ctx}/biz/request/bizPoOrderReq/delete?id=${bizPoOrderReq.id}" onclick="return confirmx('确认要删除该销售采购备货中间表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
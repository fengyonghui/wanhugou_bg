<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>退款记录管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizDrawBack/">退款记录列表</a></li>
		<shiro:hasPermission name="biz:order:bizDrawBack:edit"><li><a href="${ctx}/biz/order/bizDrawBack/form">退款记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizDrawBack" action="${ctx}/biz/order/bizDrawBack/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>biz_order_detail：</label>
				<form:input path="orderId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>biz_order_header.biz_status：</label>
				<form:input path="bizStatus" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>创建时间：</label>
				<input name="createDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizDrawBack.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>退款状态- 0-申请 1审核通过 2驳回 3撤销申请退款：</label>
				<form:input path="drawbackStatus" htmlEscape="false" maxlength="2" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:order:bizDrawBack:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizDrawBack">
			<tr>
				<shiro:hasPermission name="biz:order:bizDrawBack:edit"><td>
    				<a href="${ctx}/biz/order/bizDrawBack/form?id=${bizDrawBack.id}">修改</a>
					<a href="${ctx}/biz/order/bizDrawBack/delete?id=${bizDrawBack.id}" onclick="return confirmx('确认要删除该退款记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
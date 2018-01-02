<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票抬头管理</title>
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
		<li class="active"><a href="${ctx}/biz/invoice/bizInvoiceHeader/">发票抬头列表</a></li>
		<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit"><li><a href="${ctx}/biz/invoice/bizInvoiceHeader/form">发票抬头添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInvoiceHeader" action="${ctx}/biz/invoice/bizInvoiceHeader/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>采购商名称：</label>
				<form:input path="custOff.id" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>发票类型：</label>
				<form:select path="invType" class="input-medium required">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getDictList('invType')}" itemLabel="label" itemValue="value"
                        htmlEscape="false"/></form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>采购商名称</th>
				<th>发票抬头</th>
				<th>发票类型</th>
				<th>发票内容</th>
				<th>发票数额</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInvoiceHeader">
			<tr>
				<td><a href="${ctx}/biz/invoice/bizInvoiceHeader/form?id=${bizInvoiceHeader.id}">
					${bizInvoiceHeader.custOff.name}
				</a></td>
				<td>
					${bizInvoiceHeader.invTitle}
				</td>
				<td>
					${fns:getDictLabel(bizInvoiceHeader.invType, 'invType', '未知状态')}
				</td>
				<td>
					${bizInvoiceHeader.invContent}
				</td>
				<td>
					${bizInvoiceHeader.invTotal}
				</td>
				<td>
					${bizInvoiceHeader.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizInvoiceHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
                    <fmt:formatDate value="${bizInvoiceHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
				<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit"><td>
    				<a href="${ctx}/biz/invoice/bizInvoiceHeader/form?id=${bizInvoiceHeader.id}">修改</a>
					<a href="${ctx}/biz/invoice/bizInvoiceHeader/delete?id=${bizInvoiceHeader.id}" onclick="return confirmx('确认要删除该发票抬头吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
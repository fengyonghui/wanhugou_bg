<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票信息管理</title>
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
		<li class="active"><a href="${ctx}/biz/invoice/bizInvoiceInfo/">发票信息列表</a></li>
		<shiro:hasPermission name="biz:invoice:bizInvoiceInfo:edit"><li><a href="${ctx}/biz/invoice/bizInvoiceInfo/form">发票信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInvoiceInfo" action="${ctx}/biz/invoice/bizInvoiceInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>采购商名称：</label>
				<sys:treeselect id="office" name="office.id" value="" labelName="office.name" labelValue=""
					title="采购商" url="/sys/office/treeData?type=2" cssClass="input-xlarge" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>电话：</label>
                <form:input path="tel" htmlEscape="false" maxlength="11" class="input-xlarge"/>
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
				<th>税号</th>
				<th>开户行</th>
				<th>邮寄地址</th>
				<th>电话</th>
				<th>发票账号</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:invoice:bizInvoiceInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInvoiceInfo">
			<tr>
				<td><a href="${ctx}/biz/invoice/bizInvoiceInfo/form?id=${bizInvoiceInfo.id}">
					${bizInvoiceInfo.office.name}
				</a></td>
				<td>
					${bizInvoiceInfo.invName}
				</td>
				<td>
					${bizInvoiceInfo.taxNo}
				</td>
				<td>
					${bizInvoiceInfo.bankName}
				</td>
				<td>
					${bizInvoiceInfo.bizLocation.pcrName}${bizInvoiceInfo.bizLocation.address}  
				</td>
				<td>
					${bizInvoiceInfo.tel}
				</td>
				<td>
					${bizInvoiceInfo.account}
				</td>
				<td>
					${bizInvoiceInfo.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizInvoiceInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bizInvoiceInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:invoice:bizInvoiceInfo:edit"><td>
    				<a href="${ctx}/biz/invoice/bizInvoiceInfo/form?id=${bizInvoiceInfo.id}">修改</a>
					<a href="${ctx}/biz/invoice/bizInvoiceInfo/delete?id=${bizInvoiceInfo.id}" onclick="return confirmx('确认要删除该发票信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
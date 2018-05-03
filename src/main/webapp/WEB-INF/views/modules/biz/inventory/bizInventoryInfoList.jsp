<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<html>
<head>
	<title>仓库信息管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizInventoryInfo?zt=${zt}">仓库信息列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizInventoryInfo:edit"><li><a href="${ctx}/biz/inventory/bizInventoryInfo/form?zt=${zt}">仓库信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInventoryInfo" action="${ctx}/biz/inventory/bizInventoryInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="zt" type="hidden" name="zt" value="${zt}"/>
		<ul class="ul-form">
			<li><label>仓库名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>采购中心</label>
				<sys:treeselect id="centerOffice" name="customer.id" value="" labelName="customer.name"
								labelValue="" notAllowSelectParent="true"
								title="采购中心"  url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&source=officeConnIndex" extId="${centerOffice.id}"
								cssClass="input-medium"
								allowClear="${office.currentUser.admin}">
				</sys:treeselect>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>仓库名称</th>
				<th>采购中心</th>
				<shiro:hasPermission name="biz:inventory:bizInventoryInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInventoryInfo" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
					<%--<a href="${ctx}/biz/inventory/bizInventoryInfo/form?id=${bizInventoryInfo.id}">--%>
					${bizInventoryInfo.name}
				</a></td>
				<td>
					${bizInventoryInfo.customer.name}
				</td>
				<shiro:hasPermission name="biz:inventory:bizInventoryInfo:edit"><td>
					<a href="${ctx}/biz/inventory/bizInventorySku/list?invInfo.id=${bizInventoryInfo.id}&zt=${zt}">库存详情</a>
							<a href="${ctx}/biz/inventory/bizInventoryInfo/form?id=${bizInventoryInfo.id}&zt=${zt}">修改</a>
							<a href="${ctx}/biz/inventory/bizInventoryInfo/delete?id=${bizInventoryInfo.id}&zt=${zt}" onclick="return confirmx('确认要删除该仓库信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
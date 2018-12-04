<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费设置管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizFreightConfig/">服务费设置列表</a></li>
		<shiro:hasPermission name="biz:order:bizFreightConfig:edit"><li><a href="${ctx}/biz/order/bizFreightConfig/form">服务费设置添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizFreightConfig" action="${ctx}/biz/order/bizFreightConfig/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>采购中心：</label>
				<form:select path="office.id" class="input-medium">
					<form:option value="" label="请选择"/>
					<c:forEach items="${centerList}" var="center">
						<form:option value="${center.id}" label="${center.name}"/>
					</c:forEach>
				</form:select>
			</li>
			<li><label>品类：</label>
				<form:select path="varietyInfo.id" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('service_vari')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>序号</th>
				<th>类型</th>
				<th>采购中心</th>
				<th>品类</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:order:bizFreightConfig:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizFreightConfig" varStatus="i">
			<tr>
				<td>${i.index}</td>
				<td>订单</td>
				<td>${bizFreightConfig.office.name}</td>
				<td>${bizFreightConfig.varietyInfo.name == null ? "非拉杆箱" : "bizFreightConfig.varietyInfo.name"}</td>
				<td>${bizFreightConfig.createBy.name}</td>
				<td><fmt:formatDate value="${bizFreightConfig.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${bizFreightConfig.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<shiro:hasPermission name="biz:order:bizFreightConfig:edit"><td>
    				<a href="${ctx}/biz/order/bizFreightConfig/form?office.id=${bizFreightConfig.offfice.id}&varietyInfo.id=${bizFreightConfig.varietyInfo.id}">修改</a>
					<a href="${ctx}/biz/order/bizFreightConfig/delete?office.id=${bizFreightConfig.offfice.id}&varietyInfo.id=${bizFreightConfig.varietyInfo.id}" onclick="return confirmx('确认要删除该服务费设置吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
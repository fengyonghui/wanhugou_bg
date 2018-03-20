<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购商店铺管理</title>
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
		<li class="active"><a href="${ctx}/sys/sysCustDetails/">采购商店铺列表</a></li>
		<shiro:hasPermission name="sys:sysCustDetails:edit"><li><a href="${ctx}/sys/sysCustDetails/form">采购商店铺添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysCustDetails" action="${ctx}/sys/sysCustDetails/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>采购商：</label>
				<form:input path="cust.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>商铺类型：</label>
				<form:select path="custType" class="input-medium ">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('custType')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/>
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
				<th>采购商</th>
				<th>商铺面积</th>
				<th>商铺类型</th>
				<th>商铺类别</th>
				<shiro:hasPermission name="sys:sysCustDetails:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysCustDetails">
			<tr>
				<td><a href="${ctx}/sys/sysCustDetails/form?id=${sysCustDetails.id}">
					${sysCustDetails.cust.name}
				</a></td>
				<td>
					${sysCustDetails.custAcreage}
				</td>
				<td>
					${fns:getDictLabel(sysCustDetails.custType, 'custType', '未知类别')}
				</td>
				<td>
					${sysCustDetails.custCate}
				</td>
				<shiro:hasPermission name="sys:sysCustDetails:edit"><td>
    				<a href="${ctx}/sys/sysCustDetails/form?id=${sysCustDetails.id}">修改</a>
					<a href="${ctx}/sys/sysCustDetails/delete?id=${sysCustDetails.id}" onclick="return confirmx('确认要删除该采购商店铺吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
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
		<li class="active"><a href="${ctx}/biz/order/bizServiceCharge/">服务费设置列表</a></li>
		<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><li><a href="${ctx}/biz/order/bizServiceCharge/form">服务费设置添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizServiceCharge" action="${ctx}/biz/order/bizServiceCharge/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>订单类型：</label>
				<form:select path="serviceLine.orderType" class="input-medium" htmlEscape="false">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('service_order_type')}" itemValue="value" itemLabel="label"/>
				</form:select>
			</li>
			<li><label>分类：</label>
				<form:select path="varietyInfo.id" htmlEscape="false" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('service_vari')}" itemLabel="label" itemValue="value"/>
				</form:select>
			</li>
			<li><lable>服务方式：</lable>
				<form:select path="serviceMode" class="input-mini" htmlEscape="false">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('service_cha')}" itemValue="value" itemLabel="label"/>
				</form:select>
			</li>
			<li><label>是否启用：</label>
				<form:select path="serviceLine.usable" htmlEscape="false" class="input-mini">
					<form:option value="" label="请选择"/>
					<form:option value="1" label="启用"/>
					<form:option value="0" label="不启用"/>
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
				<th>开始地</th>
				<th>到达地</th>
				<th>品类</th>
				<th>服务方式</th>
				<th>元/支(元/套)</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<th>是否开启</th>
				<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizServiceCharge" varStatus="index">
			<tr>
				<td>${index.index + 1}</td>
				<td>${fns:getDictLabel(bizServiceCharge.serviceLine.orderType,'service_order_type','')}</td>
				<td>${bizServiceCharge.serviceLine.province.name}${bizServiceCharge.serviceLine.city.name}${bizServiceCharge.serviceLine.region.name}</td>
				<td>${bizServiceCharge.serviceLine.toProvince.name}${bizServiceCharge.serviceLine.toCity.name}${bizServiceCharge.serviceLine.toRegion.name}</td>
				<td>
					${bizServiceCharge.varietyInfo.name == null ? '非拉杆箱' : bizServiceCharge.varietyInfo.name}
				</td>
				<td>${fns:getDictLabel(bizServiceCharge.serviceMode, 'service_cha', '')}</td>
				<td><fmt:formatNumber value="${bizServiceCharge.servicePrice}" pattern="0.00"/></td>
				<td>${bizServiceCharge.createBy.name}</td>
				<td><fmt:formatDate value="${bizServiceCharge.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${bizServiceCharge.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${bizServiceCharge.serviceLine.usable == 1 ? '是' : '否'}</td>
				<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><td>
    				<a href="${ctx}/biz/order/bizServiceCharge/form?id=${bizServiceCharge.id}">修改</a>
					<a href="${ctx}/biz/order/bizServiceCharge/delete?id=${bizServiceCharge.id}" onclick="return confirmx('确认要删除该服务费--配送方式吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
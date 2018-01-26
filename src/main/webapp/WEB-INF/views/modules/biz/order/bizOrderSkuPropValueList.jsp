<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>属性管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizOrderSkuPropValue/">属性列表</a></li>
		<shiro:hasPermission name="biz:order:bizOrderSkuPropValue:edit"><li><a href="${ctx}/biz/order/bizOrderSkuPropValue/form">属性添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderSkuPropValue" action="${ctx}/biz/order/bizOrderSkuPropValue/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>订单详情ID：</label>
				<form:input path="orderDetailId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>spu属性ID（biz_prod_prop_value.prop_id）：</label>
				<form:input path="propInfoId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>sku属性名称：</label>
				<form:input path="propName" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>sku属性值（biz_sku_prop_value.prop_value）：</label>
				<form:input path="propValue" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>记录状态 1:active 0:inactive：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li><label>create_id：</label>
				<form:input path="createId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>create_time：</label>
				<input name="createTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizOrderSkuPropValue.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>版本控制：</label>
				<form:input path="uVersion" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>update_id：</label>
				<form:input path="updateId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>update_time：</label>
				<input name="updateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizOrderSkuPropValue.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>id</th>
				<th>订单详情ID</th>
				<th>spu属性ID（biz_prod_prop_value.prop_id）</th>
				<th>sku属性名称</th>
				<th>sku属性值（biz_sku_prop_value.prop_value）</th>
				<th>记录状态 1:active 0:inactive</th>
				<th>create_id</th>
				<th>create_time</th>
				<th>版本控制</th>
				<th>update_id</th>
				<th>update_time</th>
				<shiro:hasPermission name="biz:order:bizOrderSkuPropValue:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOrderSkuPropValue">
			<tr>
				<td><a href="${ctx}/biz/order/bizOrderSkuPropValue/form?id=${bizOrderSkuPropValue.id}">
					${bizOrderSkuPropValue.id}
				</a></td>
				<td>
					${bizOrderSkuPropValue.orderDetailId}
				</td>
				<td>
					${bizOrderSkuPropValue.propInfoId}
				</td>
				<td>
					${bizOrderSkuPropValue.propName}
				</td>
				<td>
					${bizOrderSkuPropValue.propValue}
				</td>
				<td>
					${fns:getDictLabel(bizOrderSkuPropValue.status, 'status', '')}
				</td>
				<td>
					${bizOrderSkuPropValue.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizOrderSkuPropValue.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizOrderSkuPropValue.uVersion}
				</td>
				<td>
					${bizOrderSkuPropValue.updateId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizOrderSkuPropValue.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:order:bizOrderSkuPropValue:edit"><td>
    				<a href="${ctx}/biz/order/bizOrderSkuPropValue/form?id=${bizOrderSkuPropValue.id}">修改</a>
					<a href="${ctx}/biz/order/bizOrderSkuPropValue/delete?id=${bizOrderSkuPropValue.id}" onclick="return confirmx('确认要删除该属性吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
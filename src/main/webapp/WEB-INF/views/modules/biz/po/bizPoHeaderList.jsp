<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
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
		<li class="active"><a href="${ctx}/biz/po/bizPoHeader/">采购订单列表</a></li>
		<shiro:hasPermission name="biz:po:bizPoHeader:edit"><li><a href="${ctx}/biz/po/bizPoHeader/form">采购订单添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPoHeader" action="${ctx}/biz/po/bizPoHeader/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>供应商</label>
				<sys:treeselect id="vendOffice" name="vendOffice.id" value="${entity.vendOffice.id}" labelName="vendOffice.name"
								labelValue="${entity.vendOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="供应商"  url="/sys/office/queryTreeList?type=7" cssClass="input-medium" dataMsgRequired="必填信息">
				</sys:treeselect>
			</li>
			<li><label>发票状态：</label>
				<form:select path="invStatus" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>业务状态：</label>
				<form:select path="bizStatus" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_po_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>订单来源：</label>

				<form:select path="plateformInfo.id" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getPlatformInfoList()}" itemLabel="name" itemValue="id" htmlEscape="false"/>
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
				<th>订单编号</th>
				<th>供应商</th>
				<th>订单详情总价</th>
				<th>订单总费用</th>
				<th>运费</th>
				<th>发票状态</th>
				<th>业务状态</th>
				<th>订单来源</th>
				<shiro:hasPermission name="biz:po:bizPoHeader:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPoHeader">
			<tr>
				<td><a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">
					${bizPoHeader.orderNum}
				</a></td>
				<td>
					${bizPoHeader.vendOffice.name}
				</td>
				<td>
					${bizPoHeader.totalDetail}
				</td>
				<td>
					${bizPoHeader.totalExp}
				</td>
				<td>
					${bizPoHeader.freight}
				</td>
				<td>
						${fns:getDictLabel(bizPoHeader.invStatus, 'biz_order_invStatus', '未知类型')}

				</td>
				<td>
						${fns:getDictLabel(bizPoHeader.bizStatus, 'biz_po_status', '未知类型')}

				</td>
				<td>
						${fns:getPlatFormName(bizPoHeader.plateformInfo.id, '未知平台')}
					<%--${bizPoHeader.plateformInfo.id}--%>
				</td>
				<shiro:hasPermission name="biz:po:bizPoHeader:edit"><td>
    				<%--<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">修改</a>--%>
					<%--<a href="${ctx}/biz/po/bizPoHeader/delete?id=${bizPoHeader.id}" onclick="return confirmx('确认要删除该采购订单吗？', this.href)">删除</a>--%>
					<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">详情</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
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
	</ul>
	<form:form id="searchForm" modelAttribute="bizPoHeader" action="${ctx}/biz/po/bizPoHeader/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>采购单号</label>
				<form:input path="orderNum" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><span style="margin-left: 10px"><label>订单/备货清单编号</label></span>
				<form:input path="num"  htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>供应商</label>
				<sys:treeselect id="vendOffice" name="vendOffice.id" value="${bizPoHeader.vendOffice.id}" labelName="vendOffice.name"
								labelValue="${bizPoHeader.vendOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true" allowClear="true"
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
				<td>序号</td>
				<th>采购单号</th>
				<th>供应商</th>
				<th>订单总价</th>
				<th>交易费用</th>
				<th>应付金额</th>
				<th>支付比例</th>
				<th>订单状态</th>
				<th>订单来源</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPoHeader" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td><a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&str=detail">
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
					${bizPoHeader.totalDetail+bizPoHeader.totalExp}
				</td>
				<td>
					<fmt:formatNumber value="${bizPoHeader.initialPay/(bizPoHeader.totalDetail+bizPoHeader.totalExp)}" pattern="0.00"/>%
				</td>
				<td>
						${fns:getDictLabel(bizPoHeader.bizStatus, 'biz_po_status', '未知类型')}

				</td>
				<div style="display:none;">
					<td style="display:none;">
						<fmt:formatDate value="${bizPoHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				</div>
				<td>
					${fns:getPlatFormName(bizPoHeader.plateformInfo.id, '未知平台')}
				</td>
				<td>
					<fmt:formatDate value="${bizPoHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				</td>
				<td>
				<shiro:hasPermission name="biz:po:bizPoHeader:edit">
    				<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">修改</a>
				</shiro:hasPermission>
				<shiro:hasPermission name="biz:po:bizPoHeader:view">
					<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&str=detail">详情</a>
				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
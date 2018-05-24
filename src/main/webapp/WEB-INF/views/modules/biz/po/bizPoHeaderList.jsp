<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s,t){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
            $("#includeTestData").val(t);
			$("#searchForm").submit();
        	return false;
        }
        function testData(checkbox) {
            $("#includeTestData").val(checkbox.checked);
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
        <input id="includeTestData" name="includeTestData" type="hidden" value="${page.includeTestData}"/>
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
			<li><label>审核状态：</label>
				<form:select path="commonProcess.type" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${processList}" itemLabel="name" itemValue="code" htmlEscape="false"/>
				</form:select>
			</li>
            <li><label>测试数据</label>
                <form:checkbox path="page.includeTestData" htmlEscape="false" maxlength="100" class="input-medium" onclick="testData(this)"/>
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
				<th>采购总价</th>
				<th>交易费用</th>
				<th>应付金额</th>
				<th>支付比例</th>
				<th>订单状态</th>
				<th>采购单来源</th>
				<th>创建时间</th>
				<th>累积支付金额</th>
				<th>审核状态</th>
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
					<c:if test="${bizPoHeader.totalDetail+bizPoHeader.totalExp == 0 || bizPoHeader.totalDetail+bizPoHeader.totalExp == ''}">0</c:if>
					<c:if test="${bizPoHeader.totalDetail+bizPoHeader.totalExp > 0}">
						<fmt:formatNumber value="${bizPoHeader.payTotal == 0 ? 0 : bizPoHeader.payTotal/(bizPoHeader.totalDetail+bizPoHeader.totalExp)*100}" pattern="0.00"/>%
					</c:if>
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
					<fmt:formatDate value="${bizPoHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
						${bizPoHeader.payTotal}
				</td>
				<td>
						${bizPoHeader.commonProcess.purchaseOrderProcess.name == null ?
						 '当前无审批流程' : bizPoHeader.commonProcess.purchaseOrderProcess.name}
				</td>
				<shiro:hasPermission name="biz:po:bizPoHeader:view">
					<td>
						<shiro:hasPermission name="biz:po:bizPoHeader:createPayOrder">
							<c:if test="${bizPoHeader.bizPoPaymentOrder.id == null
							&& bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'
							&& fns:getDictLabel(bizPoHeader.bizStatus, 'biz_po_status', '未知类型') != '全部支付'
							}">
								<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=createPay">申请付款</a>
							</c:if>
						</shiro:hasPermission>
						<shiro:hasPermission name="biz:po:bizPoHeader:startAudit">
							<c:if test="${bizPoHeader.commonProcess.id == null && bizPoHeader.commonProcess.purchaseOrderProcess.name != '审批完成'}">
								<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=startAudit">开启审核</a>
							</c:if>
						</shiro:hasPermission>
						<shiro:hasPermission name="biz:po:bizPoHeader:audit">
							<c:if test="${bizPoHeader.commonProcess.id != null
							&& bizPoHeader.commonProcess.purchaseOrderProcess.name != '驳回'
							&& bizPoHeader.commonProcess.purchaseOrderProcess.name != '审批完成'
							&& bizPoHeader.commonProcess.purchaseOrderProcess.code != payStatus}">
								<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=audit">审核</a>
							</c:if>
							<a href="${ctx}/biz/po/bizPoPaymentOrder/list?poId=${bizPoHeader.id}">支付申请列表</a>
						</shiro:hasPermission>
						<shiro:hasPermission name="biz:po:bizPoHeader:edit">
							<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">修改</a>
						</shiro:hasPermission>
						<shiro:hasPermission name="biz:po:bizPoHeader:view">
						    <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&str=detail">详情</a>
						</shiro:hasPermission>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
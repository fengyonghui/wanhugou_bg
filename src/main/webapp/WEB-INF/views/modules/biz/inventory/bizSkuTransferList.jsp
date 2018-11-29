<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.TransferStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.RoleEnNameEnum" %>
<html>
<head>
	<title>库存调拨管理</title>
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
		<li class="active"><a href="${ctx}/biz/inventory/bizSkuTransfer?source=${source}">库存调拨列表</a></li>
		<c:if test="${source ne 'out' && source ne 'in'}">
			<shiro:hasPermission name="biz:inventory:bizSkuTransfer:edit"><li><a href="${ctx}/biz/inventory/bizSkuTransfer/form">库存调拨添加</a></li></shiro:hasPermission>
		</c:if>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSkuTransfer" action="${ctx}/biz/inventory/bizSkuTransfer?source=${source}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>调拨单号</label>
				<from:input path="transferNo" cssClass="input-medium"/>
			</li>
			<li><label>原仓库：</label>
				<form:select path="fromInv.id" class="input-medium">
					<form:option value="" label="请选择"/>
					<c:forEach items="${fromInvList}" var="inv">
						<form:option label="${inv.name}" value="${inv.id}" htmlEscape="false"/>
					</c:forEach>
				</form:select>
			</li>
			<li><label>目标仓库：</label>
				<form:select path="toInv.id" class="input-medium">
					<form:option value="" label="请选择"/>
					<c:forEach items="${fromInvList}" var="inv">
						<form:option label="${inv.name}" value="${inv.id}" htmlEscape="false"/>
					</c:forEach>
				</form:select>
			</li>
			<li><label>业务状态：</label>
				<form:select path="bizStatus" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('transfer_bizStatus')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>审核状态：</label>
				<form:select class="input-medium" path="commonProcess.type">
					<form:option value="" label="请选择"/>
					<form:options items="${transferMap}" htmlEscape="false"/>
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
				<th>调拨单号</th>
				<th>起调仓库</th>
				<th>调往仓库</th>
				<th>期望收货时间</th>
				<th>备注</th>
				<th>业务状态</th>
				<th>审核状态</th>
				<th>申请时间</th>
				<shiro:hasPermission name="biz:inventory:bizSkuTransfer:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSkuTransfer">
			<tr>
				<td>${bizSkuTransfer.transferNo}</td>
				<td>${bizSkuTransfer.fromInv.name}</td>
				<td>${bizSkuTransfer.toInv.name}</td>
				<td><fmt:formatDate value="${bizSkuTransfer.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${bizSkuTransfer.remark}</td>
				<td>${fns:getDictLabel(bizSkuTransfer.bizStatus,'transfer_bizStatus' ,'未知状态' )}</td>
				<td>${bizSkuTransfer.commonProcess.transferProcess.name}</td>
				<td><fmt:formatDate value="${bizSkuTransfer.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>
					<c:if test="${source ne 'in' && source ne 'out'}">
						<a href="${ctx}/biz/inventory/bizSkuTransfer/form?id=${bizSkuTransfer.id}&str=detail">详情</a>

						<shiro:hasPermission name="biz:inventory:bizSkuTransfer:edit">
							<c:if test="${bizSkuTransfer.commonProcess.id == null || bizSkuTransfer.commonProcess.transferProcess.name == '驳回'}">
								<a href="${ctx}/biz/inventory/bizSkuTransfer/form?id=${bizSkuTransfer.id}">修改</a>
								<a href="${ctx}/biz/inventory/bizSkuTransfer/delete?id=${bizSkuTransfer.id}" onclick="return confirmx('确认要删除该库存调拨吗？', this.href)">删除</a>
							</c:if>
						</shiro:hasPermission>
						<shiro:hasPermission name="biz:inventory:bizSkuTransfer:audit">
							<c:if test="${bizSkuTransfer.commonProcess.id != null
										&& bizSkuTransfer.commonProcess.transferProcess.name != '审批完成'
										&& bizSkuTransfer.commonProcess.transferProcess.name != '驳回'
										&& (fns:hasRole(roleSet, bizSkuTransfer.commonProcess.transferProcess.roleEnNameEnum) || fns:getUser().isAdmin())
										}">
								<c:choose>
									<c:when test="${fns:getUser().getCompany().id == bizSkuTransfer.fromInv.customer.id && fns:hasRole(roleSet, RoleEnNameEnum.WAREHOUSESPECIALIST)}">
										<a href="${ctx}/biz/inventory/bizSkuTransfer/form?id=${bizSkuTransfer.id}&str=audit">审核</a>
									</c:when>
									<c:when test="${!(fns:hasRole(roleSet, RoleEnNameEnum.WAREHOUSESPECIALIST))}">
										<a href="${ctx}/biz/inventory/bizSkuTransfer/form?id=${bizSkuTransfer.id}&str=audit">审核</a>
									</c:when>
								</c:choose>
							</c:if>
						</shiro:hasPermission>
						<%--<c:if test="${bizSkuTransfer.commonProcess.id != null
										&& bizSkuTransfer.commonProcess.transferProcess.name != '审批完成'
										&& bizSkuTransfer.commonProcess.transferProcess.name != '驳回'
										&& (fns:hasRole(roleSet, bizSkuTransfer.commonProcess.transferProcess.roleEnNameEnum) || fns:getUser().isAdmin())
										&& (fns:getUser().getCompany().id == bizSkuTransfer.fromInv.customer.id)}">
							<shiro:hasPermission name="biz:inventory:bizSkuTransfer:audit">
								<a href="${ctx}/biz/inventory/bizSkuTransfer/form?id=${bizSkuTransfer.id}&str=audit">审核</a>
							</shiro:hasPermission>
						</c:if>
						<c:if test="${bizSkuTransfer.commonProcess.id != null
										&& bizSkuTransfer.commonProcess.transferProcess.name != '审批完成'
										&& bizSkuTransfer.commonProcess.transferProcess.name != '驳回'
										&& (fns:hasRole(roleSet, bizSkuTransfer.commonProcess.transferProcess.roleEnNameEnum) || fns:getUser().isAdmin())
										&& (fns:getUser().getCompany().id == bizSkuTransfer.fromInv.customer.id)}">
							<shiro:hasPermission name="biz:inventory:bizSkuTransfer:audit">
								<a href="${ctx}/biz/inventory/bizSkuTransfer/form?id=${bizSkuTransfer.id}&str=audit">审核</a>
							</shiro:hasPermission>
						</c:if>--%>
					</c:if>
					<c:if test="${source eq 'out' && bizSkuTransfer.bizStatus >= TransferStatusEnum.APPROVE.state && bizSkuTransfer.bizStatus <= TransferStatusEnum.OUTING_WAREHOUSE.state}">
						<shiro:hasPermission name="biz:inventory:bizSkuTransfer:outTreasury">
							<c:choose>
								<c:when test="${fns:hasRole(roleSet, RoleEnNameEnum.WAREHOUSESPECIALIST) && fns:getUser().getCompany().id == bizSkuTransfer.fromInv.customer.id}">
									<a href="${ctx}/biz/inventory/bizSkuTransfer/outTreasuryForm?id=${bizSkuTransfer.id}&source=${source}">出库</a>
								</c:when>
								<c:when test="${!(fns:hasRole(roleSet, RoleEnNameEnum.WAREHOUSESPECIALIST))}">
									<a href="${ctx}/biz/inventory/bizSkuTransfer/outTreasuryForm?id=${bizSkuTransfer.id}&source=${source}">出库</a>
								</c:when>
							</c:choose>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${source eq 'out'}">
						<a href="${ctx}/biz/inventory/bizSkuTransfer/outTreasuryForm?id=${bizSkuTransfer.id}&source=${source}&str=detail">出库详情</a>
					</c:if>
					<c:if test="${source eq 'in' && bizSkuTransfer.bizStatus >= TransferStatusEnum.ALREADY_OUT_WAREHOUSE.state && bizSkuTransfer.bizStatus <= TransferStatusEnum.INING_WAREHOUSE.state}">
						<shiro:hasPermission name="biz:inventory:bizSkuTransfer:outTreasury">
							<c:choose>
								<c:when test="${fns:hasRole(roleSet, RoleEnNameEnum.WAREHOUSESPECIALIST) && fns:getUser().getCompany().id == bizSkuTransfer.toInv.customer.id}">
									<a href="${ctx}/biz/inventory/bizSkuTransfer/inTreasuryForm?id=${bizSkuTransfer.id}&source=${source}">入库</a>
								</c:when>
								<c:when test="${!(fns:hasRole(roleSet, RoleEnNameEnum.WAREHOUSESPECIALIST))}">
									<a href="${ctx}/biz/inventory/bizSkuTransfer/inTreasuryForm?id=${bizSkuTransfer.id}&source=${source}">入库</a>
								</c:when>
							</c:choose>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${source eq 'in'}">
						<a href="${ctx}/biz/inventory/bizSkuTransfer/inTreasuryForm?id=${bizSkuTransfer.id}&source=${source}&str=detail">入库详情</a>
					</c:if>
                </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发货单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#invoiceExport").click(function(){
				top.$.jBox.confirm("确认要导出订单发货信息数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/inventory/bizInvoice/exportList?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/inventory/bizInvoice/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
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
		<li class="active"><a href="${ctx}/biz/inventory/bizInvoice?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单列表</a></li>
		<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><li><a href="${ctx}/biz/inventory/bizInvoice/form?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="pageSize" name="ship" type="hidden" value="${bizInvoice.ship}"/>
		<input id="pageNo" name="bizStatus" type="hidden" value="${bizInvoice.bizStatus}"/>
		<ul class="ul-form">
			<li><label>发货单号：</label>
				<form:input path="sendNumber" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>物流商：</label>
				<form:input path="logistics.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>发货人：</label>
				<form:input path="carrier" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input id="invoiceExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>发货号</th>
				<th>物流商</th>
				<th>运费</th>
				<th>操作费</th>
				<th>货值</th>
				<th>运费/货值</th>
				<th>发货人</th>
				<th>物流结算方式</th>
				<th>发货时间</th>
				<th>物流信息图</th>
				<th>操作</th>
				<%--<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><th>操作</th></shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInvoice">
			<tr>
				<td>${bizInvoice.sendNumber}</td>
				<td>${bizInvoice.logistics.name}</td>
				<td>${bizInvoice.freight}</td>
				<td>${bizInvoice.operation}</td>
				<td>${bizInvoice.valuePrice}</td>
				<td>
					<c:if test="${bizInvoice.valuePrice != 0}">
					<fmt:formatNumber type="number" value="${bizInvoice.freight*100/bizInvoice.valuePrice}" maxFractionDigits="0"/>%
					</c:if>
				</td>
				<td>${bizInvoice.carrier}</td>
				<td>${fns:getDictLabel(bizInvoice.settlementStatus, 'biz_settlement_status', '未知状态')}</td>
				<td>
					<fmt:formatDate value="${bizInvoice.sendDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td><img src="${bizInvoice.imgUrl}"style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/></td>
				<td>
					<c:if test="${bizInvoice.delFlag!=null && bizInvoice.delFlag!=0}">
						<c:if test="${bizInvoice.ship==0}">
						<shiro:hasPermission name="biz:inventory:bizInvoice:edit">
							<a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}">修改</a>
						</shiro:hasPermission>
						<a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&source=xq">发货单详情</a>
					</c:if>
						<c:if test="${bizInvoice.ship==1}">
						<shiro:hasPermission name="biz:inventory:bizInvoice:edit">
							<a href="${ctx}/biz/inventory/bizInvoice/invoiceRequestDetail?id=${bizInvoice.id}">修改</a>
						</shiro:hasPermission>
						<a href="${ctx}/biz/inventory/bizInvoice/invoiceRequestDetail?id=${bizInvoice.id}&source=xq">发货单详情</a>
					</c:if>
					</c:if>
					<c:if test="${fns:getUser().isAdmin()}">
						<c:if test="${bizInvoice.delFlag!=null && bizInvoice.delFlag!=0}">
							<a href="${ctx}/biz/inventory/bizInvoice/delete?id=${bizInvoice.id}" onclick="return confirmx('确认要删除该发货单吗？', this.href)">删除</a>
						</c:if>
						<c:if test="${bizInvoice.delFlag!=null && bizInvoice.delFlag==0}">
							<a href="${ctx}/biz/inventory/bizInvoice/recovery?id=${bizInvoice.id}" onclick="return confirmx('确认要恢复该发货单吗？', this.href)">恢复</a>
						</c:if>
					</c:if>
				</td>
				<%--<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><td>
    				<a href="${ctx}/biz/inventory/bizInvoice/form?id=${bizInvoice.id}">修改</a>
					<a href="${ctx}/biz/inventory/bizInvoice/delete?id=${bizInvoice.id}" onclick="return confirmx('确认要删除该发货单吗？', this.href)">删除</a>
				<td><td></shiro:hasPermission>--%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
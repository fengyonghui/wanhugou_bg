<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发货单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			if($("#ship").val()!=null && $("#ship").val()!="" && $("#ship").val()==1){
				$("#invoiceExport").click(function(){
					top.$.jBox.confirm("确认要导出备货单发货信息数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/biz/inventory/bizInvoice/exportList?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}");
							$("#searchForm").submit();
							$("#searchForm").attr("action","${ctx}/biz/inventory/bizInvoice/");
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
			}else{
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
			}
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
		<shiro:hasPermission name="biz:inventory:bizInvoice:edit">
			<li><a href="${ctx}/biz/inventory/bizInvoice/form?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}&creOrdLogistics=yes">发货单添加</a></li>
		</shiro:hasPermission>
		<li class=""><a href="${ctx}/biz/order/bizOrderHeader/list">订单列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ship" name="ship" type="hidden" value="${bizInvoice.ship}"/>
		<input id="bizStatus" name="bizStatus" type="hidden" value="${bizInvoice.bizStatus}"/>
		<ul class="ul-form">
			<li><label>发货单号：</label>
				<form:input path="sendNumber" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<c:if test="${bizInvoice.ship==0}">
				<li><label>订单号：</label>
					<form:input path="orderNum" htmlEscape="false" maxlength="30" class="input-medium"/>
				</li>
			</c:if>
			<c:if test="${bizInvoice.ship==1}">
				<li><label>备货单号：</label>
					<form:input path="reqNo" htmlEscape="false" maxlength="30" class="input-medium"/>
				</li>
			</c:if>
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
				<td>序号</td>
				<th>发货号</th>
				<th>物流单号</th>
				<th>运费</th>
				<th>货值</th>
				<th>运费/货值</th>
				<th>发货人</th>
				<th>验货员</th>
				<th>物流结算方式</th>
				<th>物流信息图</th>
				<th>备注</th>
				<th>发货时间</th>
				<th>操作</th>
				<%--<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><th>操作</th></shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInvoice" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<c:if test="${bizInvoice.ship==0}">
					<td><a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&source=xq">${bizInvoice.sendNumber}</a></td>
				</c:if>
				<c:if test="${bizInvoice.ship==1}">
					<td><a href="${ctx}/biz/inventory/bizInvoice/invoiceRequestDetail?id=${bizInvoice.id}&source=xq">${bizInvoice.sendNumber}</a></td>
				</c:if>
				<td>${bizInvoice.trackingNumber}</td>
				<td>${bizInvoice.freight}</td>
				<td>${bizInvoice.valuePrice}</td>
				<td>
					<c:if test="${bizInvoice.valuePrice != 0}">
					<fmt:formatNumber type="number" value="${bizInvoice.freight*100/bizInvoice.valuePrice}" maxFractionDigits="0"/>%
					</c:if>
				</td>
				<td>
					<c:if test="${bizInvoice.isConfirm != 0}">
						${bizInvoice.carrier}
					</c:if>
				</td>
				<td>
					${bizInvoice.inspector.name}
				</td>
				<td>${fns:getDictLabel(bizInvoice.settlementStatus, 'biz_settlement_status', '未知状态')}</td>
				<td><img src="${bizInvoice.imgUrl}"style="max-width:150px;max-height:150px;_height:150px;border:0;padding:3px;"/></td>

				<td>${bizInvoice.remarks}</td>
				<td>
					<fmt:formatDate value="${bizInvoice.sendDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<c:if test="${bizInvoice.ship==0}">
						<shiro:hasPermission name="biz:inventory:bizInvoice:edit">
                            <c:if test="${bizInvoice.isConfirm == 0}">
							    <a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&str=audit&creOrdLogistics=yes">确认发货单</a>
                            </c:if>
							<c:if test="${bizInvoice.freight == '0.00'}">
								<a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&str=freight">添加运费</a>
							</c:if>
						</shiro:hasPermission>
						<a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&source=xq">发货单详情</a>
					</c:if>
					<c:if test="${bizInvoice.ship==1}">
						<shiro:hasPermission name="biz:inventory:bizInvoice:edit">
                            <c:if test="${bizInvoice.isConfirm == 0}">
							    <a href="${ctx}/biz/inventory/bizInvoice/invoiceRequestDetail?id=${bizInvoice.id}&str=audit&creOrdLogistics=yes">确认发货单</a>
                            </c:if>
							<c:if test="${bizInvoice.freight == '0.00'}">
                            	<a href="${ctx}/biz/inventory/bizInvoice/invoiceRequestDetail?id=${bizInvoice.id}&str=freight">添加运费</a>
							</c:if>
						</shiro:hasPermission>
						<a href="${ctx}/biz/inventory/bizInvoice/invoiceRequestDetail?id=${bizInvoice.id}&source=xq">发货单详情</a>
					</c:if>
					<a href="${ctx}/biz/inventory/bizInvoice/logisticDetail?trackingNumber=${bizInvoice.trackingNumber}">运单信息</a>
					<c:if test="${fns:getUser().isAdmin()}">
						<a href="${ctx}/biz/inventory/bizInvoice/delete?id=${bizInvoice.id}" onclick="return confirmx('确认要删除该发货单吗？', this.href)">删除</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page import="com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader" %>
<%@ page import="com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发货单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            });


	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/inventory/bizInvoice?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单<shiro:hasPermission name="biz:inventory:bizInvoice:edit">${not empty bizInvoice.id?'详情':'详情'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInvoice:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/save?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<%--<c:if test="${bizInvoice.id != null && bizInvoice.id != ''}">
			<div class="control-group">
				<label class="control-label">发货单号：</label>
				<div class="controls">
					<form:input path="sendNumber" htmlEscape="false" disabled="true" class="input-xlarge "/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">物流单号：</label>
			<div class="controls">
				<form:input path="trackingNumber" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流商：</label>
			<div class="controls">
				&lt;%&ndash;<form:input path="logistics.name" htmlEscape="false" class="input-xlarge "/>&ndash;%&gt;
				<form:select id="bizLogistics" path="logistics.id" onmouseout="" class="input-medium">
					&lt;%&ndash;<c:forEach items="${logisticsList}" var="bizLogistics">&ndash;%&gt;
						<form:option value="${bizLogistics.id}"/>${bizLogistics.name}
						<form:options items="${logisticsList}" itemLabel="name" itemValue="id"/>
					&lt;%&ndash;</c:forEach>&ndash;%&gt;
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流信息图：</label>
			<div class="controls">
				&lt;%&ndash;<img src="${imgUrl}"style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/>&ndash;%&gt;
					<form:hidden path="imgUrl" htmlEscape="false" maxlength="255" class="input-xlarge"/>
					<sys:ckfinder input="imgUrl" type="images" uploadPath="/logistics/info" selectMultiple="false" maxWidth="100"
								  maxHeight="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货值：</label>
			<div class="controls">
                <form:input id="valuePrice" path="valuePrice"  htmlEscape="false" value="" readonly="true" class="input-xlarge required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">操作费：</label>
			<div class="controls">
				<form:input path="operation" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">运费：</label>
			<div class="controls">
				<form:input path="freight" htmlEscape="false" class="input-xlarge required"/>
			</div>
		</div>
		<c:if test="${userList==null}">
			<div class="control-group">
				<label class="control-label">发货人：</label>
				<div class="controls">
					<form:input about="choose" readonly="true" path="carrier" class="input-medium required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<c:if test="${userList!=null}">
		<div class="control-group">
			<label class="control-label">发货人：</label>
			<div class="controls">
				<form:select about="choose" path="carrier" class="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${userList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">发货时间：</label>
			<div class="controls">
				<input name="sendDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
						   value="<fmt:formatDate value="${bizInvoice.sendDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流结算方式：</label>
			<div class="controls">
				<form:select id="settlementStatus" path="settlementStatus" onmouseout="" class="input-xlarge">
					<c:forEach items="${fns:getDictList('biz_settlement_status')}" var="settlementStatus">
						<option value="${settlementStatus.value}">${settlementStatus.label}</option>
					</c:forEach>
				</form:select>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">已发货详情：</label>
			<div class="controls">
				<table id="contentTable"  class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>订单编号</th>
						<th>经销店名称</th>
						<th>业务状态</th>
						<th>商品名称</th>
						<th>商品货号</th>
						<th>采购数量</th>
						<th>已发货数量</th>
					</tr>
					</thead>
					<tbody id="prodInfo">
						&lt;%&ndash;<c:if test="${orderHeaderList!=null && orderHeaderList.size()>0}">&ndash;%&gt;
							&lt;%&ndash;<c:forEach items="${orderHeaderList}" var="orderHeader">&ndash;%&gt;
                            	&lt;%&ndash;<c:set var="flag" value="true"></c:set>&ndash;%&gt;
                                &lt;%&ndash;<c:forEach items="${orderHeader.orderDetailList}" var="orderDetail" varStatus="index">&ndash;%&gt;
                                    &lt;%&ndash;<tr>&ndash;%&gt;
                                        &lt;%&ndash;<c:if test="${flag}">&ndash;%&gt;
                                            &lt;%&ndash;<td rowspan="${fn:length(orderHeader.orderDetailList)}">${orderHeader.orderNum}</td>&ndash;%&gt;
                                            &lt;%&ndash;<td rowspan="${fn:length(orderHeader.orderDetailList)}">${orderHeader.customer.name}</td>&ndash;%&gt;
                                            &lt;%&ndash;<td rowspan="${fn:length(orderHeader.orderDetailList)}">${fns:getDictLabel(orderHeader.bizStatus,"biz_order_status",'' )}</td>&ndash;%&gt;
                                        &lt;%&ndash;</c:if>&ndash;%&gt;
                                        &lt;%&ndash;<td>${orderDetail.skuName}</td>&ndash;%&gt;
                                        &lt;%&ndash;<td>${orderDetail.skuInfo.itemNo}</td>&ndash;%&gt;
                                        &lt;%&ndash;<td>${orderDetail.ordQty}</td>&ndash;%&gt;
                                        &lt;%&ndash;<td>${orderDetail.sentQty}</td>&ndash;%&gt;
                                    &lt;%&ndash;</tr>&ndash;%&gt;
                                    &lt;%&ndash;<c:if test="${fn:length(orderHeader.orderDetailList)>1}">&ndash;%&gt;
                                        &lt;%&ndash;<c:set var="flag" value="false"></c:set>&ndash;%&gt;
                                    &lt;%&ndash;</c:if>&ndash;%&gt;
                                &lt;%&ndash;</c:forEach>&ndash;%&gt;
							&lt;%&ndash;</c:forEach>&ndash;%&gt;
						&lt;%&ndash;</c:if>&ndash;%&gt;
						<c:set var="flag" value="true"></c:set>
						<c:forEach items="${orderDetailList}" var="orderDetail">
							<tr>
								<c:if test="${flag}">
									<td rowspan="${fn:length(orderDetailList)}">${orderDetail.orderHeader.orderNum}</td>
									<td rowspan="${fn:length(orderDetailList)}">${orderDetail.cust.name}</td>
									<td rowspan="${fn:length(orderDetailList)}">${fns:getDictLabel(orderDetail.orderHeader.bizStatus,"biz_order_status",'' )}</td>
								</c:if>
									<td>${orderDetail.skuInfo.name}</td>
									<td>${orderDetail.skuInfo.itemNo}</td>
									<td>${orderDetail.ordQty}</td>
									<td>${orderDetail.sentQty}</td>
							</tr>
							<c:if test="${fn:length(orderDetailList)>1}">
							<c:set var="flag" value="false"></c:set>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" maxlength="200" class="input-xlarge "/>
			</div>
		</div>--%>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<%--<th>行号</th>--%>
				<th>发货号</th>
				<th>订单编号</th>
				<th>商品名称</th>
				<th>商品编号</th>
				<th>商品货号</th>
				<%--<th>已生成的采购单</th>--%>
				<th>商品出厂价</th>
				<th>供应商</th>
				<%--<th>供应商电话</th>--%>
				<th>商品单价</th>
				<th>采购数量</th>
				<th>总 额</th>
				<th>已发货数量</th>
				<c:if test="${bizOrderHeader.bizStatus>=15 && bizOrderHeader.bizStatus!=45}">
					<th>发货方</th>
				</c:if>
				<th>创建时间</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${orderDetailList}" var="bizOrderDetail">
				<tr>
					<%--<td>
							${bizOrderDetail.lineNo}
					</td>--%>
					<td>
						<a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizOrderDetail.orderHeader.sendId}&source=xq">${bizOrderDetail.orderHeader.sendNum}</a>
					</td>
					<td>
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderDetail.orderHeader.id}&orderDetails=details">${bizOrderDetail.orderHeader.orderNum}</a>
					</td>
					<td>
							${bizOrderDetail.skuName}
					</td>
					<td>
							${bizOrderDetail.partNo}
					</td>
					<td>
							${bizOrderDetail.skuInfo.itemNo}
					</td>
					<%--<td>
						<a href="${ctx}/biz/po/bizPoHeader/form?id=${detailIdMap.get(bizOrderDetail.getLineNo())}">${orderNumMap.get(bizOrderDetail.getLineNo())}</a>
					</td>--%>
					<td>
							${bizOrderDetail.buyPrice}
					</td>
					<td>
							${bizOrderDetail.vendor.name}
					</td>
					<%--<td>
							${bizOrderDetail.primary.mobile}
					</td>--%>
					<td>
							${bizOrderDetail.unitPrice}
					</td>
					<td>
							${bizOrderDetail.ordQty}
					</td>
					<td>
						<c:if test="${bizOrderDetail.unitPrice !=null && bizOrderDetail.ordQty !=null}">
							<fmt:formatNumber type="number" value=" ${bizOrderDetail.unitPrice * bizOrderDetail.ordQty}" pattern="0.00"/>
						</c:if>
					</td>
					<td>
							${bizOrderDetail.sentQty}
					</td>
					<c:if test="${bizOrderHeader.bizStatus>=15 && bizOrderHeader.bizStatus!=45}">
						<td>
								${bizOrderDetail.suplyis.name}
						</td>
					</c:if>
					<td>
						<fmt:formatDate value="${bizOrderDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>

				</tr>
			</c:forEach>
			</tbody>
		</table>
		<div class="form-actions">
			<input onclick="window.print();" type="button" class="btn btn-primary" value="打印发货单" style="background:#F78181;"/>
			<c:if test="${source ne 'xq'}">
				<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<%--<form:form id="searchForm" modelAttribute="bizOrderHeader">
		<form:hidden id="orderNumCopy" path="orderNum"/>
		<form:hidden id="skuItemNoCopy" path="itemNo"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>

	</form:form>--%>
</body>
</html>
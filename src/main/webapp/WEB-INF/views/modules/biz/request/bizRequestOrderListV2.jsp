<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.BizOrderTypeEnum" %>
<html>
<head>
	<title>备货清单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
            $('#select_all').live('click',function(){
                var choose=$("input[title='orderIds']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
            $("#requesthExport").click(function(){
				top.$.jBox.confirm("确认要导出备货清单数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestOrder/ExportList");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestOrder/listV2");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function saveOrderIds() {
            if($("input[title='orderIds']:checked").length <= 0){
                alert("请选择需要备货的清单！");
			}else {
                $("#myForm").submit();
			}
        }
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
		<c:if test="${source eq 'bhgh'}">
			<li class="active"><a href="${ctx}/biz/request/bizRequestOrder/listV2?source=${source}">备货清单列表</a></li>
		</c:if>
		<c:if test="${source eq 'xsgh'}">
			<li class="active"><a href="${ctx}/biz/request/bizRequestOrder/listV2?source=${source}">销售清单列表</a></li>
		</c:if>
	</ul>
	<c:if test="${requestHeaderPage!=null}">
		<form:form id="searchForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestOrder/listV2" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>

			<input type="hidden" name="source" value="${source}"/>
			<input id="includeTestData" name="includeTestData" type="hidden" value="${page.includeTestData}"/>
			<ul class="ul-form">
				<li><label>备货单号：</label>
					<form:input path="reqNo" htmlEscape="false" maxlength="20" class="input-medium"/>
				</li>
				<li><label>采购中心：</label>
					<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
									labelValue="${entity.fromOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true" allowClear="true"
									title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-medium required" dataMsgRequired="必填信息">
					</sys:treeselect>
				</li>
				<%--<li><span><label>期望收货时间：</label></span>--%>
					<%--<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"--%>
						   <%--value="<fmt:formatDate value="${bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"--%>
						   <%--onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>--%>
					<%--<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"--%>
						   <%--value="<fmt:formatDate value="${bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"--%>
						   <%--onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>--%>
				<%--</li>--%>
				<li><label>业务状态：</label>
					<form:select path="bizStatus" class="input-medium">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('biz_req_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</li>

				<li><label>测试数据</label>
					<form:checkbox path="page.includeTestData" htmlEscape="false" maxlength="100" class="input-medium" onclick="testData(this)"/>
				</li>

				<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<li class="btns"><input id="requesthExport" class="btn btn-primary" type="button" value="导出"/></li>
				<li class="clearfix"></li>
			</ul>
		</form:form>
	</c:if>
	<c:if test="${orderHeaderPage!=null}">
		<form:form id="searchForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/request/bizRequestOrder/listV2" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input type="hidden" name="source" value="${source}"/>
			<input id="includeTestData" name="includeTestData" type="hidden" value="${page.includeTestData}"/>
			<form:hidden path="consultantId"/>
			<ul class="ul-form">
				<li><label>订单编号：</label>
					<form:input path="orderNum" htmlEscape="false" maxlength="30" class="input-medium"/>
				</li>
					<%--<li><label>订单类型：</label>--%>
					<%--<form:select path="orderType" class="input-medium required">--%>
					<%--<form:option value="" label="请选择"/>--%>
					<%--<form:options items="${fns:getDictList('biz_order_type')}" itemLabel="label" itemValue="value"--%>
					<%--htmlEscape="false"/></form:select></li>--%>
				<li><label>经销店名称：</label>
					<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
						<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
										labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
										title="经销店"  url="/sys/office/queryTreeList?type=6"
										cssClass="input-medium required"
										allowClear="true"  dataMsgRequired="必填信息"/>
						<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}">
						<input type="hidden" name="flag" value="${bizOrderHeader.flag}">
					</c:if>
					<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
						<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
										labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
										title="经销店"  url="/sys/office/queryTreeList?type=6"
										cssClass="input-medium required"
										allowClear="true"  dataMsgRequired="必填信息"/>
					</c:if>
				</li>
				<li><label>测试数据</label>
					<form:checkbox path="page.includeTestData" htmlEscape="false" maxlength="100" class="input-medium" onclick="testData(this)"/>
				</li>

				<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<li class="btns"><input id="requesthExport" class="btn btn-primary" type="button" value="导出"/></li>
				<c:if test="${bizOrderHeader.flag=='check_pending'}">
					<li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/></li>

				</c:if>
				<li class="clearfix"></li>
			</ul>
		</form:form>

	</c:if>
	<sys:message content="${message}"/>
	<c:if test="${requestHeaderPage!=null}">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>序号</th>
				<%--<c:if test="${source=='gh'}">--%>
				<%--<th><input id="select_all" type="checkbox" /></th>--%>
				<%--</c:if>--%>
				<th>备货单号</th>
				<th>采购中心</th>
				<th>期望收货时间</th>
				<th>备货商品数量</th>
				<th>已到货数量</th>
				<th>备注</th>
				<th>业务状态</th>
				<th>申请人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasAnyPermissions name="biz:request:bizRequestHeader:edit,biz:request:bizRequestHeader:view"><th>操作</th></shiro:hasAnyPermissions>
			</tr>
		</thead>
		<tbody>
		<%--<form id="myForm" action="${ctx}/biz/request/bizRequestAll/genSkuOrder">--%>

			<c:forEach items="${page.list}" var="requestHeader" varStatus="state">
				<tr>
					<td>
						${state.index+1}
					</td>
					<%--<c:if test="${source=='gh'}">--%>
					<%--<td><input name="reqIds" title="orderIds" type="checkbox" value="${requestHeader.id}" /></td>--%>
					<%--</c:if>--%>
					<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}">
						${requestHeader.reqNo}
					</a></td>
						<td>${requestHeader.fromOffice.name}</td>
					<td>
						<fmt:formatDate value="${requestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
						<td>${requestHeader.reqQtys}</td>
						<td>${requestHeader.recvQtys}</td>
						<td>
								${requestHeader.remark}
						</td>
					<td>
						${fns:getDictLabel(requestHeader.bizStatus, 'biz_req_status', '未知类型')}
					</td>
					<td>
						${requestHeader.createBy.name}
					</td>
					<td>
						<fmt:formatDate value="${requestHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						<fmt:formatDate value="${requestHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
						<td>
							<shiro:hasPermission name="biz:request:bizRequestHeader:view">
								<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}&str=detail">详情</a>
							</shiro:hasPermission>
							<shiro:hasPermission name="biz:request:selecting:supplier:edit">
									<a href="${ctx}/biz/request/bizRequestOrder/goList?reqIds=${requestHeader.reqDetailIds}&ordIds=&vendorId=${requestHeader.onlyVendor}">采购</a>
							</shiro:hasPermission>
						</td>
				</tr>
			</c:forEach>

		<%--</form>--%>
		</tbody>
	</table>
	</c:if>
	<c:if test="${orderHeaderPage!=null}">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<th>序号</th>
				<th>订单编号</th>
				<th>订单类型</th>
				<th>经销店名称</th>
				<th>采购中心</th>
				<th>订单详情总价</th>
				<th>订单总费用</th>
				<th>运费</th>
				<th>发票状态</th>
				<th>业务状态</th>
				<th>订单来源</th>
				<th>订单收货地址</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasAnyPermissions name="biz:request:bizRequestHeader:edit,biz:request:bizRequestHeader:view"><th>操作</th></shiro:hasAnyPermissions>
			</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="orderHeader" varStatus="state">
				<tr>
					<td>
						${state.index+1}
					</td>
					<td><a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details">
									${orderHeader.orderNum}</a>
					</td>
					<td>${fns:getDictLabel(orderHeader.bizType, 'order_biz_type', '未知状态')}</td>
							<td>
									${orderHeader.customer.name}
							</td>
							<td>
									${orderHeader.centersName}
							</td>
							<td>
									${orderHeader.totalDetail}
							</td>
							<td>
									${orderHeader.totalExp}
							</td>
							<td>
									${orderHeader.freight}
							</td>
							<td>
									${fns:getDictLabel(orderHeader.invStatus, 'biz_order_invStatus', '未知状态')}
							</td>
							<td>
									${fns:getDictLabel(orderHeader.bizStatus, 'biz_order_status', '未知状态')}
							</td>
							<td>
									${orderHeader.platformInfo.name}
							</td>
					<td>
						${orderHeader.bizLocation.province.name}${orderHeader.bizLocation.city.name}
						${orderHeader.bizLocation.region.name}${orderHeader.bizLocation.address}
					</td>
					<td>
							${orderHeader.createBy.name}
					</td>
					<td>
						<fmt:formatDate value="${orderHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						<fmt:formatDate value="${orderHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						<shiro:hasPermission name="biz:order:bizOrderHeader:view">
							<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details">查看详情</a>
						</shiro:hasPermission>
						<shiro:hasPermission name="biz:request:selecting:supplier:edit">
							<c:if test="${orderHeader.orderType != BizOrderTypeEnum.PHOTO_ORDER.state}">
								<a href="${ctx}/biz/request/bizRequestOrder/goList?reqIds=&ordIds=${orderHeader.orderDetails}&vendorId=${orderHeader.onlyVendor}">采购</a>
							</c:if>
							<c:if test="${orderHeader.orderType == BizOrderTypeEnum.PHOTO_ORDER.state}">
								<a href="${ctx}/biz/request/bizRequestOrder/goListForPhotoOrder?reqIds=&ordIds=${orderHeader.id}&vendorId=${orderHeader.onlyVendor}">采购</a>
							</c:if>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</c:if>
	<div class="pagination">${page}</div>
</body>
</html>
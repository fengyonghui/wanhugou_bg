<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
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
		});
		function saveOrderIds() {
            if($("input[title='orderIds']:checked").length <= 0){
                alert("请选择需要备货的清单！");
			}else {
                $("#myForm").submit();
			}
        }
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
		<c:if test="${source eq 'bhgh'}">
			<li class="active"><a href="${ctx}/biz/request/bizRequestOrder/list?source=${source}">备货清单列表</a></li>
		</c:if>
		<c:if test="${source eq 'xsgh'}">
			<li class="active"><a href="${ctx}/biz/request/bizRequestOrder/list?source=${source}">销售清单列表</a></li>
		</c:if>
	</ul>
	<c:if test="${requestHeaderList!=null}">
		<form:form id="searchForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestOrder/list" method="post" class="breadcrumb form-search">
			<input type="hidden" name="source" value="${source}"/>
			<ul class="ul-form">
				<li><label>备货单号：</label>
					<form:input path="reqNo" htmlEscape="false" maxlength="20" class="input-medium"/>
				</li>
				<li><label>采购中心：</label>
					<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
									labelValue="${entity.fromOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
									title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-medium required" dataMsgRequired="必填信息">
					</sys:treeselect>
				</li>
				<li><span><label>期望收货时间：</label></span>
					<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
						   value="<fmt:formatDate value="${bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				</li>
				<li><label>业务状态：</label>
					<form:select path="bizStatus" class="input-medium">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('biz_req_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</li>
				<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<li class="clearfix"></li>
			</ul>
		</form:form>
	</c:if>
	<c:if test="${orderHeaderList!=null}">
		<form:form id="searchForm2" modelAttribute="bizOrderHeader" action="${ctx}/biz/request/bizRequestOrder/list" method="post" class="breadcrumb form-search">
			<input type="hidden" name="source" value="${source}"/>

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
				<li><label>采购商名称：</label>
					<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
						<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
										labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
										title="采购商"  url="/sys/office/queryTreeList?type=6"
										cssClass="input-medium required"
										allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
						<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}">
						<input type="hidden" name="flag" value="${bizOrderHeader.flag}">
					</c:if>
					<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
						<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
										labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
										title="采购商"  url="/sys/office/queryTreeList?type=6"
										cssClass="input-medium required"
										allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
					</c:if>
				</li>
				<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<c:if test="${bizOrderHeader.flag=='check_pending'}">
					<li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/></li>

				</c:if>
				<li class="clearfix"></li>
			</ul>
		</form:form>

	</c:if>
	<sys:message content="${message}"/>
	<c:if test="${requestHeaderList!=null}">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
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
				<th>更新时间</th>
				<shiro:hasAnyPermissions name="biz:request:bizRequestHeader:edit,biz:request:bizRequestHeader:view"><th>操作</th></shiro:hasAnyPermissions>
			</tr>
		</thead>
		<tbody>
		<%--<form id="myForm" action="${ctx}/biz/request/bizRequestAll/genSkuOrder">--%>

			<c:forEach items="${requestHeaderList}" var="requestHeader">
				<tr>
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
						<fmt:formatDate value="${requestHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
						<shiro:hasPermission name="biz:request:bizRequestHeader:view">
						<td>
							<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}&str=detail">详情</a>
						</td>
						</shiro:hasPermission>

							<%--<shiro:hasPermission name="biz:request:bizRequestHeader:edit"><td>--%>
						<%--<c:choose>--%>
							<%--<c:when test="${source=='gh'}">--%>
								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}">详情</a>--%>
							<%--</c:when>--%>
							<%--<c:when test="${source=='sh'}">--%>
								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=gh">备货单详情</a>--%>
								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}">收货</a>--%>
							<%--</c:when>--%>
							<%--<c:otherwise>--%>

								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}">供货</a>--%>
							<%--</c:otherwise>--%>
						<%--</c:choose>--%>

					<%--</td></shiro:hasPermission>--%>
				</tr>
			</c:forEach>

		<%--</form>--%>
		</tbody>
	</table>
	</c:if>
	<c:if test="${orderHeaderList!=null}">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<th>订单编号</th>
				<th>订单类型</th>
				<th>采购商名称</th>
				<th>订单详情总价</th>
				<th>订单总费用</th>
				<th>运费</th>
				<th>发票状态</th>
				<th>业务状态</th>
				<th>订单来源</th>
					<%--<th>订单收货地址</th>--%>
				<th>创建人</th>
				<th>更新时间</th>
				<shiro:hasAnyPermissions name="biz:request:bizRequestHeader:edit,biz:request:bizRequestHeader:view"><th>操作</th></shiro:hasAnyPermissions>
			</tr>
			</thead>
			<tbody>
				<%--<form id="myForm" action="${ctx}/biz/request/bizRequestAll/genSkuOrder">--%>

			<c:forEach items="${orderHeaderList}" var="orderHeader">
				<tr>
						<%--<c:if test="${source=='gh'}">--%>
						<%--<td><input name="reqIds" title="orderIds" type="checkbox" value="${requestHeader.id}" /></td>--%>
						<%--</c:if>--%>
					<td><a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details">
									${orderHeader.orderNum}</a>
					</td>
					<td>${fns:getDictLabel(orderHeader.bizType, 'order_biz_type', '未知状态')}</td>
							<td>
									${orderHeader.customer.name}
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
							${orderHeader.createBy.name}
					</td>
					<td>
						<fmt:formatDate value="${orderHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
							<shiro:hasPermission name="biz:order:bizOrderHeader:view"><td>
							<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details">查看详情</a>
								</td>
							</shiro:hasPermission>

				</tr>
			</c:forEach>


				<%--</form>--%>
			</tbody>
		</table>
	</c:if>





	<%--<div class="form-actions">

			<shiro:hasPermission name="biz:request:selecting:supplier:edit">
				<input type="button" onclick="saveOrderIds();" class="btn btn-primary" value="合单采购"/>
			</shiro:hasPermission>
	</div>--%>
</body>
</html>
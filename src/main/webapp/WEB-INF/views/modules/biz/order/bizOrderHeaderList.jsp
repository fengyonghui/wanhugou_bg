<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单信息管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
		<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><li><a href="${ctx}/biz/order/bizOrderHeader/form">订单信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>订单编号：</label>
				<form:input path="orderNum" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			 <%--<li><label>订单类型：</label>--%>
                <%--<form:select path="orderType" class="input-medium required">--%>
                    <%--<form:option value="" label="请选择"/>--%>
                    <%--<form:options items="${fns:getDictList('biz_order_type')}" itemLabel="label" itemValue="value"--%>
                            <%--htmlEscape="false"/></form:select></li>--%>
			<li><label>客户名称：</label>
			     <sys:treeselect id="office" name="customer.id" value="${entity.customer.id}"  labelName="customer.name"
                                                labelValue="${entity.customer.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
                                                title="客户"  url="/sys/office/queryTreeList?type=6"
                                                cssClass="input-medium required"
                                                allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
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
				<%--<th>订单类型</th>--%>
				<th>商品类型</th>
				<th>客户名称</th>
				<th>订单详情总价</th>
				<th>订单总费用</th>
				<th>运费</th>
				<th>发票状态</th>
				<th>业务状态</th>
				<th>订单来源</th>
				<%--<th>订单收货地址</th>--%>
				<th>创建人</th>
				<th>订单创建时间</th>
				<th>更新人</th>
				<th>订单更新时间</th>
				<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOrderHeader">
			<tr>
				<td><a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}">
					${bizOrderHeader.orderNum}
				</a></td>
				<%--<td>--%>
					<%--${fns:getDictLabel(bizOrderHeader.orderType, 'biz_order_type', '未知状态')}--%>
				<%--</td>--%>
				<%---start----%>
				<td><c:if test="${bizOrderHeader.bizType ==1}">
						专营
					</c:if>
					<c:if test="${bizOrderHeader.bizType ==2}">
						非专营
					</c:if><c:if test="${bizOrderHeader.bizType ==0 || bizOrderHeader.bizType ==null || bizOrderHeader.bizType =='' || bizOrderHeader.bizType >3}">
						未知
					</c:if>
				</td>
				<%----end---%>
				<td>
					${bizOrderHeader.customer.name}
				</td>
				<td>
					${bizOrderHeader.totalDetail}
				</td>
				<td>
					${bizOrderHeader.totalExp}
				</td>
				<td>
					${bizOrderHeader.freight}
				</td>
				<td>
					${fns:getDictLabel(bizOrderHeader.invStatus, 'biz_order_invStatus', '未知状态')}
				</td>
				<td>
					${fns:getDictLabel(bizOrderHeader.bizStatus, 'biz_order_status', '未知状态')}
				</td>
				<td>
					${bizOrderHeader.platformInfo.name}
				</td>
				<%--<td>--%>
					<%--${bizOrderHeader.bizLocation.pcrName}${bizOrderHeader.bizLocation.address}--%>
				<%--</td>--%>
				<td>
					${bizOrderHeader.createBy.name}
				</td>
				<td>
                    <fmt:formatDate value="${bizOrderHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
				<td>
					${bizOrderHeader.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizOrderHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><td>
					<c:choose>
					<c:when test="${bizOrderHeader.flag=='check_pending'}">
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&bizOrderHeader.flag=${bizOrderHeader.flag}">待审核</a>
					</c:when>
					<c:otherwise>
						<c:if test="${bizOrderHeader.bizStatus==0 || bizOrderHeader.bizStatus==5 ||
									bizOrderHeader.totalDetail+bizOrderHeader.totalExp+bizOrderHeader.freight!=bizOrderHeader.receiveTotal}">
							<a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&orderNoEditable=editable">待支付</a>
						</c:if>
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&orderNoEditable=editable">查看详情</a>
						<c:if test="${bizOrderHeader.bizStatus==18 || bizOrderHeader.bizStatus==19 || bizOrderHeader.bizStatus==17|| bizOrderHeader.bizStatus==16 ||
						  bizOrderHeader.bizStatus==15 || bizOrderHeader.bizStatus==10 || bizOrderHeader.bizStatus==5 || bizOrderHeader.bizStatus==0}">
							<a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}">修改</a>
							<a href="${ctx}/biz/order/bizOrderHeader/delete?id=${bizOrderHeader.id}" onclick="return confirmx('确认要删除该订单信息吗？', this.href)">删除</a>
						</c:if>
						<%--<a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}">修改</a>--%>
						<%--<a href="${ctx}/biz/order/bizOrderHeader/delete?id=${bizOrderHeader.id}" onclick="return confirmx('确认要删除该订单信息吗？', this.href)">删除</a>--%>
					</c:otherwise>
					</c:choose>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
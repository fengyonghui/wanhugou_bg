<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
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
		<li class="active"><a href="${ctx}/biz/request/bizRequestHeader/">备货清单列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestAll/list" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>订单号：</label>
				<form:input path="reqNo" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>采购客户：</label>
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
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>订单号</th>
				<th>订单类型</th>
				<th>采购客户</th>
				<th>期望收货时间</th>
				<th>备注</th>
				<th>业务状态</th>
				<th>更新人</th>
				<th>更新时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${requestHeaderList}" var="requestHeader">
			<tr>
				<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}">
					${requestHeader.reqNo}
				</a></td>
				<td>
					${fns:getDictLabel(requestHeader.reqType, 'biz_req_type', '未知类型')}
				</td>
				<td>
					${requestHeader.fromOffice.name}
				</td>

				<td>
					<fmt:formatDate value="${requestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${requestHeader.remark}
				</td>
				<td>
					${fns:getDictLabel(requestHeader.bizStatus, 'biz_req_status', '未知类型')}
				</td>
				<td>
					${requestHeader.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${requestHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:request:bizRequestHeader:edit"><td>
    				<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}">修改</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>


		<c:forEach items="${orderHeaderList}" var="orderHeader">
			<tr>
				<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=${source}">
						${orderHeader.orderNum}
				</a></td>
				<td>
						销售订单
				</td>
				<td>
						${orderHeader.customer.name}
				</td>
				<td>
					<%--<fmt:formatDate value="${orderHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>--%>
				</td>
				<td>
						<%--${orderHeader.remark}--%>
				</td>
				<td>
						${fns:getDictLabel(orderHeader.bizStatus, 'biz_order_status', '未知类型')}
				</td>
				<td>
						${orderHeader.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${orderHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:request:bizRequestHeader:edit"><td>
					<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=${source}">修改</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
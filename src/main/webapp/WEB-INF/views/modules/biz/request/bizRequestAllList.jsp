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
		<c:if test="${source eq 'sh'}">
			<li class="active"><a href="${ctx}/biz/request/bizRequestAll?source=${source}">收货清单列表</a></li>
		</c:if>
		<c:if test="${source eq 'kc'}">
			<li class="active"><a href="${ctx}/biz/request/bizRequestAll?source=${source}&bizStatu=${bizStatu}&ship=${ship}">供货清单列表</a></li>
		</c:if>
	</ul>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<c:if test="${source=='gh'}">
					<th><input id="select_all" type="checkbox" /></th>
				</c:if>
				<th>销售单号</th>
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
		<form id="myForm" action="${ctx}/biz/request/bizRequestAll/genSkuOrder">
		<c:if test="${source == 'sh' || source=='gh' || bizStatu==1 && ship=='bh'}">
			<c:forEach items="${requestHeaderList}" var="requestHeader">
				<tr>
					<c:if test="${source=='gh'}">
					<td><input name="reqIds" title="orderIds" type="checkbox" value="${requestHeader.id}" /></td>
					</c:if>
					<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}&bizStatu=${bizStatu}&ship=bh">
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
						<c:choose>
							<c:when test="${source=='gh'}">
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}">详情</a>
							</c:when>
							<c:when test="${source=='sh'}">
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=gh">备货详情</a>
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}">收货</a>
							</c:when>
							<c:when test="${bizStatu=='1'}">
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=gh">备货详情</a>
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}&bizStatu=${bizStatu}&ship=bh">供货</a>
							</c:when>
						</c:choose>

					</td></shiro:hasPermission>
				</tr>
			</c:forEach>
		</c:if>

		<c:if test="${source == 'kc' && ship=='xs' || source=='gh'}">
			<c:forEach items="${orderHeaderList}" var="orderHeader">
				<tr>
					<c:if test="${source=='gh'}">
						<td><input name="orderIds" title="orderIds" type="checkbox" value="${orderHeader.id}" /></td>
					</c:if>
					<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=${source}&bizStatu=${bizStatu}&ship=xs">
							${orderHeader.orderNum}
					</a></td>
					<td>
							销售订单
					</td>
					<td>
							${orderHeader.customer.name}
					</td>
					<td>
						<fmt:formatDate value="${orderHeader.deliveryDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
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
						<c:choose>
							<c:when test="${source=='gh'}">
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=${source}">详情</a>
							</c:when>
							<c:otherwise>
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=ghs">供货详情</a>
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=${source}&bizStatu=${bizStatu}&ship=xs">供货</a>
							</c:otherwise>
						</c:choose>
					</td></shiro:hasPermission>
				</tr>
			</c:forEach>
		</c:if>
		</form>
		</tbody>
	</table>
	<%--<div class="form-actions">

			<shiro:hasPermission name="biz:request:selecting:supplier:edit">
				<input type="button" onclick="saveOrderIds();" class="btn btn-primary" value="合单采购"/>
			</shiro:hasPermission>
	</div>--%>
</body>
</html>
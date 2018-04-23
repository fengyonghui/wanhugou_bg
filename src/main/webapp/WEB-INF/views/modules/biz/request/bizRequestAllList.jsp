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

	<c:if test="${ship eq 'bh'}">
		<form:form id="searchForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestAll/" method="post" class="breadcrumb form-search">
			<ul class="ul-form">
				<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
				<input name="source" value="${source}" type="hidden"/>
				<input name="ship" value="${ship}" type="hidden"/>
				<input name="bizStatu" value="${bizStatu}" type="hidden"/>
				<li><label>备货单号：</label>
					<form:input path="reqNo" htmlEscape="false" maxlength="20" class="input-medium"/>
				</li>
				<li><label>采购中心：</label>
					<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
									labelValue="${entity.fromOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
									title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-medium required" dataMsgRequired="必填信息">
					</sys:treeselect>
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
	<c:if test="${ship eq 'xs'}">
		<form:form id="searchForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/request/bizRequestAll/" method="post" class="breadcrumb form-search">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderNum" name="bizOrderHeader.orderNum" type="hidden" value="${bizOrderHeader.orderNum}"/>
			<input name="source" value="${source}" type="hidden"/>
			<input name="ship" value="${ship}" type="hidden"/>
			<input name="bizStatu" value="${bizStatu}" type="hidden"/>
			<form:hidden path="consultantId"/>
			<ul class="ul-form">
				<li><label>订单编号：</label>
					<form:input path="orderNum" htmlEscape="false" maxlength="30" class="input-medium"/>
				</li>
				<li><label>订单状态：</label>
					<form:select path="bizStatus" class="input-medium">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
									  htmlEscape="false"/></form:select>
				</li>
				<li><label>采购商名称：</label>
					<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
						<sys:treeselect id="office" name="customer.id" value=""  labelName="customer.name"
										labelValue="" notAllowSelectParent="true"
										title="采购商"  url="/sys/office/queryTreeList?type=6"
										cssClass="input-medium required"
										allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
						<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}">
						<input type="hidden" name="flag" value="${bizOrderHeader.flag}">
					</c:if>
					<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
						<sys:treeselect id="office" name="customer.id" value=""  labelName="customer.name"
										labelValue="" notAllowSelectParent="true"
										title="采购商"  url="/sys/office/queryTreeList?type=6&source=cgs"
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
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<c:if test="${source eq 'gh'}">
					<th><input id="select_all" type="checkbox" /></th>
				</c:if>
				<c:if test="${ship eq 'bh'}">
					<th>备货单号</th>
				</c:if>
				<c:if test="${ship eq 'xs'}">
					<th>销售单号</th>
				</c:if>
				<th>类型</th>
				<c:if test="${ship eq 'bh'}">
					<th>采购中心</th>
				</c:if>
				<c:if test="${ship eq 'xs'}">
					<th>采购客户</th>
				</c:if>
				<th>期望收货时间</th>
				<th>备注</th>
				<th>业务状态</th>
				<th>更新人</th>
				<th>发货时间</th>
				<th>更新时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<form id="myForm" action="${ctx}/biz/request/bizRequestAll/genSkuOrder">
		<c:if test="${source == 'sh' || source=='gh' || bizStatu==1 && ship=='bh'}">
			<c:forEach items="${page.list}" var="requestHeader">
				<tr>
					<c:if test="${source=='gh'}">
					<td><input name="reqIds" title="orderIds" type="checkbox" value="${requestHeader.id}" /></td>
					</c:if>
					<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=gh">
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
						<fmt:formatDate value="${requestHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
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
								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}&bizStatu=${bizStatu}&ship=bh">发货</a>--%>
							</c:when>
						</c:choose>

					</td></shiro:hasPermission>
				</tr>
			</c:forEach>
		</c:if>

		<c:if test="${source == 'kc' && ship=='xs'||bizStatu == 0 || source=='gh'}">
			<c:forEach items="${page.list}" var="orderHeader">
				<tr>
					<c:if test="${source=='gh'}">
						<td><input name="orderIds" title="orderIds" type="checkbox" value="${orderHeader.id}" /></td>
					</c:if>
					<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=ghs">
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
						<fmt:formatDate value="${orderHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
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
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=ghs">发货详情</a>
								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=${source}&bizStatu=${bizStatu}&ship=xs">发货</a>--%>
							</c:otherwise>
						</c:choose>
					</td></shiro:hasPermission>
				</tr>
			</c:forEach>
		</c:if>
		</form>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<%--<div class="form-actions">

			<shiro:hasPermission name="biz:request:selecting:supplier:edit">
				<input type="button" onclick="saveOrderIds();" class="btn btn-primary" value="合单采购"/>
			</shiro:hasPermission>
	</div>--%>
</body>
</html>
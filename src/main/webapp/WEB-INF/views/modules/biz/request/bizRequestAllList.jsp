<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<html>
<head>
	<title>备货清单管理</title>
	<%--<meta name="decorator"/>--%>
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
            $("#requestAllExport").click(function(){
				top.$.jBox.confirm("确认要导出备货单收货数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestAll/listExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestAll/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#orderExport").click(function(){
				top.$.jBox.confirm("确认要导出订单出库数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestAll/listExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestAll/");
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
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>

	<script language="JavaScript">
        function myrefresh()
        {
            window.location.reload();
        }
        setTimeout('myrefresh()',180000); //指定3分钟刷新一次
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:if test="${source eq 'sh'}">
			<li class="active"><a href="${ctx}/biz/request/bizRequestAll?source=${source}&ship=${ship}&bizStau=${bizStatu}">收货清单列表</a></li>
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
				<input id="previousPage" name="previousPage" type="hidden" value="${bizRequestHeader.previousPage}"/>
				<input name="ship" value="${ship}" type="hidden"/>
				<input name="bizStatu" value="${bizStatu}" type="hidden"/>
				<li><label>备货单号：</label>
					<form:input path="reqNo" htmlEscape="false" maxlength="20" class="input-medium"/>
				</li>
				<li><label>采购中心：</label>
					<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
									labelValue="${entity.fromOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true" allowClear="true"
									title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-medium required" dataMsgRequired="必填信息">
					</sys:treeselect>
				</li>
				<li><label>业务状态：</label>
					<form:select path="bizStatus" class="input-medium">
						<form:option value="" label="请选择"/>
						<form:options items="${fns:getDictList('biz_req_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					</form:select>
				</li>
                <li><label>需要入库：</label>
                    <form:select path="needIn" cssClass="input-mini">
                        <form:option value="" label="请选择"/>
                        <form:option value="1" label="是"/>
                    </form:select>
                </li>
				<li><label>收货时间：</label>
					<input name="recvEtaStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
						   value="${bizRequestHeader.recvEtaStartTime}"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
					至
					<input name="recvEtaEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
						   value="${bizRequestHeader.recvEtaEndTime}"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				</li>

				<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<li class="btns">
					<%--备货单收货--%>
					<input id="requestAllExport" class="btn btn-primary" type="button" value="导出"/>
				</li>
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
				<li><label>经销店名称：</label>
					<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
						<sys:treeselect id="office" name="customer.id" value=""  labelName="customer.name"
										labelValue="" notAllowSelectParent="true"
										title="经销店"  url="/sys/office/queryTreeList?type=6"
										cssClass="input-medium required"
										allowClear="true" dataMsgRequired="必填信息"/>
						<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}">
						<input type="hidden" name="flag" value="${bizOrderHeader.flag}">
					</c:if>
					<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
						<sys:treeselect id="office" name="customer.id" value=""  labelName="customer.name"
										labelValue="" notAllowSelectParent="true"
										title="经销店"  url="/sys/office/queryTreeList?type=6&source=cgs"
										cssClass="input-medium required"
										allowClear="true"  dataMsgRequired="必填信息"/>
					</c:if>
				</li>
                <li><label>需要出库：</label>
                    <form:select path="needOut" cssClass="input-mini">
                        <form:option value="" label="请选择"/>
                        <form:option value="1" label="是"/>
                    </form:select>
                </li>
				<li><label>出库时间：</label>
					<input name="needOutStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
						   value="${bizOrderHeader.needOutStartTime}"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
					至
					<input name="needOutEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
						   value="${bizOrderHeader.needOutEndTime}"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				</li>
				<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
				<li class="btns">
					<%--订单出库--%>
					<input id="orderExport" class="btn btn-primary" type="button" value="导出"/>
				</li>
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
				<th>序号</th>
				<c:if test="${source eq 'gh'}">
					<th><input id="select_all" type="checkbox" /></th>
				</c:if>
				<c:if test="${ship eq 'bh'}">
					<th>备货单号</th>
				</c:if>
				<c:if test="${ship eq 'xs'}">
					<th>销售单号</th>
				</c:if>
				<c:if test="${ship eq 'bh'}">
					<th>备货方</th>
					<th>备货单类型</th>
				</c:if>
				<th>类型</th>
				<c:if test="${ship eq 'bh'}">
					<th>采购中心</th>
				</c:if>
				<c:if test="${ship eq 'xs'}">
					<th>采购客户</th>
				</c:if>
				<c:if test="${ship eq 'bh'}">
					<th>期望收货时间</th>
				</c:if>
				<c:if test="${ship eq 'xs'}">
					<th>收货地址</th>
				</c:if>
				<c:if test="${ship eq 'bh'}">
					<th>备注</th>
				</c:if>
				<th>业务状态</th>
				<th>更新人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<form id="myForm" action="${ctx}/biz/request/bizRequestAll/genSkuOrder">
		<c:if test="${source == 'sh' || source=='gh' || bizStatu==1 && ship=='bh'}">
			<c:forEach items="${page.list}" var="requestHeader" varStatus="state">
				<tr>
					<td>${state.index+1}</td>
					<c:if test="${source=='gh'}">
					<td><input name="reqIds" title="orderIds" type="checkbox" value="${requestHeader.id}" /></td>
					</c:if>
					<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=gh&bizStatu=${bizStatu}">
						${requestHeader.reqNo}
					</a></td>
					<td>${fns:getDictLabel(requestHeader.fromType, 'req_from_type', '未知')}</td>
					<td>${fns:getDictLabel(requestHeader.headerType, 'req_header_type', '未知')}</td>
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
					<td>
						<c:choose>
							<c:when test="${source=='gh'}">
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}&bizStatu=${bizStatu}">详情</a>
							</c:when>
							<c:when test="${source=='sh'}">
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=gh&bizStatu=${bizStatu}">备货详情</a>
								<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
								<c:if test="${requestHeader.bizStatus < ReqHeaderStatusEnum.COMPLETE.state}">
									<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}&bizStatu=${bizStatu}">入库</a>
								</c:if>
								</shiro:hasPermission>
							</c:when>
							<c:when test="${bizStatu=='1'}">
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=gh&bizStatu=${bizStatu}">备货详情</a>
								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${requestHeader.id}&source=${source}&bizStatu=${bizStatu}&ship=bh">发货</a>--%>
							</c:when>
						</c:choose>

					</td>
				</tr>
			</c:forEach>
		</c:if>

		<c:if test="${(source == 'kc' && ship=='xs' && bizStatu == 0) || source=='gh'}">
			<c:forEach items="${page.list}" var="orderHeader" varStatus="state">
				<tr>
					<td>${state.index+1}</td>
					<c:if test="${source=='gh'}">
						<td><input name="orderIds" title="orderIds" type="checkbox" value="${orderHeader.id}" /></td>
					</c:if>
					<td><a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=ghs&bizStatu=${bizStatu}">
							${orderHeader.orderNum}
					</a></td>
					<td>
							销售订单
					</td>
					<td>
							${orderHeader.customer.name}
					</td>
					<c:if test="${ship eq 'xs'}">
						<td>
							${orderHeader.bizLocation.province.name}${orderHeader.bizLocation.city.name}
							${orderHeader.bizLocation.region.name}${orderHeader.bizLocation.address}
						</td>
					</c:if>
					<%--<td>--%>
							<%--&lt;%&ndash;${orderHeader.remark}&ndash;%&gt;--%>
					<%--</td>--%>
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
					<td>
						<c:choose>
							<c:when test="${source=='gh'}">
								<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=${source}&bizStatu=${bizStatu}">详情</a>
							</c:when>
							<c:otherwise>
								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=ghs&bizStatu=${bizStatu}&ship=${ship}">发货详情</a>--%>
								<a href="${ctx}/biz/request/bizRequestAll/confirmOut?orderHeaderId=${orderHeader.id}&source=detail">出库详情</a>
								<shiro:hasPermission name="biz:request:confirmOut:view">
								<c:if test="${orderHeader.bizStatus < OrderHeaderBizStatusEnum.SEND.state &&
										(orderHeader.commonProcess.type == '666' || orderHeader.commonProcess.type == '777')}">
									<a href="${ctx}/biz/request/bizRequestAll/confirmOut?orderHeaderId=${orderHeader.id}">出库</a>
								</c:if>
								</shiro:hasPermission>
								<%--<a href="${ctx}/biz/request/bizRequestAll/form?id=${orderHeader.id}&source=${source}&bizStatu=${bizStatu}&ship=xs">发货</a>--%>
							</c:otherwise>
						</c:choose>
					</td>
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
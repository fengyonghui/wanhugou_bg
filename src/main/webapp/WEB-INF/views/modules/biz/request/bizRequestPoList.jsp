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

			<li class="active"><a href="${ctx}/biz/request/bizRequestOrder/form?source=${source}">供货需求汇总</a></li>

	</ul>
	<form:form id="searchForm" modelAttribute="office" action="${ctx}/biz/request/bizRequestOrder/form?source=gh" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>供应商名称：</label>
				<sys:treeselect id="office" name="id" value="${office.id}" labelName="name"
								labelValue="" notAllowSelectParent="true"
								title="供应商" url="/sys/office/queryTreeList?type=7" cssClass="input-medium"
								dataMsgRequired="必填信息"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
		<%--<form:form id="searchForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestOrder/" method="post" class="breadcrumb form-search">--%>
			<%--<input type="hidden" name="source" value="${source}"/>--%>
			<%--<ul class="ul-form">--%>
				<%--<li><label>备货单号：</label>--%>
					<%--<form:input path="reqNo" htmlEscape="false" maxlength="20" class="input-medium"/>--%>
				<%--</li>--%>
				<%--<li><label>采购中心：</label>--%>
					<%--<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"--%>
									<%--labelValue="${entity.fromOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true"--%>
									<%--title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-medium required" dataMsgRequired="必填信息">--%>
					<%--</sys:treeselect>--%>
				<%--</li>--%>
				<%--<li><span><label>期望收货时间：</label></span>--%>
					<%--<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"--%>
						   <%--value="<fmt:formatDate value="${bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"--%>
						   <%--onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>--%>
				<%--</li>--%>
				<%--<li><label>业务状态：</label>--%>
					<%--<form:select path="bizStatus" class="input-medium">--%>
						<%--<form:option value="" label="请选择"/>--%>
						<%--<form:options items="${fns:getDictList('biz_req_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>--%>
					<%--</form:select>--%>
				<%--</li>--%>
				<%--<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>--%>
				<%--<li class="clearfix"></li>--%>
			<%--</ul>--%>
		<%--</form:form>--%>
	<sys:message content="${message}"/>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<th>供应商</th>
				<th>待供货商品数量</th>
				<th>备货清单供货数量</th>
				<th>销售订单供货数量</th>
				<shiro:hasPermission name="biz:request:selecting:supplier:edit">
				<th>操作</th>
				</shiro:hasPermission>
			</tr>
			</thead>
			<tbody>
				<%--<form id="myForm" action="${ctx}/biz/request/bizRequestAll/genSkuOrder">--%>

			<c:forEach items="${map}" var="orderMap">
				<tr>
					<td>
						${orderMap.vendorName}
					</td>
					<td>

							${orderMap.totalReqQty + orderMap.totalOrdQty}
					</td>
					<td>
							${orderMap.totalReqQty}
					</td>
					<td>
							${orderMap.totalOrdQty}
					</td>
					<shiro:hasPermission name="biz:request:selecting:supplier:edit">
						<td>
							<a href="${ctx}/biz/request/bizRequestOrder/goList?reqIds=${orderMap.reqDetailIds}&ordIds=${orderMap.detailIds}&vendorId=${orderMap.vendorId}">供货</a>
						</td>
					</shiro:hasPermission>
				</tr>
			</c:forEach>


				<%--</form>--%>
			</tbody>
		</table>






	<%--<div class="form-actions">

			<shiro:hasPermission name="biz:request:selecting:supplier:edit">
				<input type="button" onclick="saveOrderIds();" class="btn btn-primary" value="合单采购"/>
			</shiro:hasPermission>
	</div>--%>
</body>
</html>
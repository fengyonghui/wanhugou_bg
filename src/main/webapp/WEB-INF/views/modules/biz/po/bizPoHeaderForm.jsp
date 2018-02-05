<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){

                        loading('正在提交，请稍等...');
                        form.submit();

				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		function saveMon() {
			$("#inputForm").attr("action","${ctx}/biz/po/bizPoHeader/savePoHeader");
            $("#inputForm").submit();
        }
        function savePoOrder(){
		    var us=$("input[name='unitPrices']").val();
		    if(us==''){
		        alert("价钱不能为空！");
		        return;
			}
            if(confirm("确认生成采购订单吗？")){
                $("#inputForm").submit();
            }
		}

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/po/bizPoHeader/">采购订单列表</a></li>
		<li class="active"><a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">采购订单<shiro:hasPermission name="biz:po:bizPoHeader:edit">${not empty bizPoHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:po:bizPoHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizPoHeader" action="${ctx}/biz/po/bizPoHeader/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<input type="hidden" name="vendOffice.id" value="${vendorId}">
		<c:if test="${bizPoHeader.id!=null}">
			<div class="control-group">
			<label class="control-label">订单编号：</label>
			<div class="controls">
				<form:input disabled="true" path="orderNum" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">订单来源：</label>
			<div class="controls">
				<c:forEach items="${bizPoHeader.poOrderReqList}" var="so">
					<c:if test="${so.orderHeader!=null}">
						<input type="text" style="margin-bottom: 10px" disabled="disabled" value="${so.orderHeader.orderNum}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
						<br/>
					</c:if>
					<c:if test="${so.requestHeader!=null}">
						<input type="text" style="margin-bottom: 10px" disabled="disabled" value="${so.requestHeader.reqNo}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
						<br/>
					</c:if>

				</c:forEach>

			</div>
		</div>

		<div class="control-group">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<form:input disabled="true" path="vendOffice.name" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">订单商品总价：</label>
			<div class="controls">
				<input type="text" disabled="disabled" value="${bizPoHeader.totalDetail}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单总费用：</label>
			<div class="controls">
				<form:input path="totalExp"  htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">运费：</label>
			<div class="controls">
				<form:input path="freight"  htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">订单总价：</label>
			<div class="controls">
				<input type="text" disabled="disabled" value="${bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">发票状态：</label>
			<div class="controls">
				<input type="text" disabled="disabled" value="${fns:getDictLabel(bizPoHeader.invStatus, 'biz_order_invStatus', '未知类型')}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">业务状态：</label>
			<div class="controls">
				<input type="text" disabled="disabled" value="${fns:getDictLabel(bizPoHeader.bizStatus, 'biz_po_status', '未知类型')}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>
		</c:if>
		<c:if test="${bizPoHeader.poDetailList!=null}">
		<div class="form-actions">
			<shiro:hasPermission name="biz:po:bizPoHeader:edit">

					<input id="btnSubmit" type="button" onclick="saveMon()"  class="btn btn-primary"  value="保存"/>

				&nbsp;</shiro:hasPermission>
		</div>
	</c:if>
		<table id="contentTable"  class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<th>产品图片</th>
				<th>品牌名称</th>
				<th>商品名称</th>
				<th>商品编码</th>
				<th>商品属性</th>
				<c:if test="${bizPoHeader.id==null}">
					<th>申报数量</th>
				</c:if>
				<th>采购数量</th>
				<c:if test="${bizPoHeader.id!=null}">
					<th>已供货数量</th>
				</c:if>
				<th>商品单价</th>


			</tr>
			</thead>
			<tbody id="prodInfo">
					<c:if test="${bizPoHeader.poDetailList!=null}">
						<c:forEach items="${bizPoHeader.poDetailList}" var="poDetail">
							<tr>
								<td><img style="width: 20%" src="${poDetail.skuInfo.productInfo.imgUrl}"/></td>
								<td>${poDetail.skuInfo.productInfo.brandName}</td>
								<td>${poDetail.skuInfo.name}</td>
								<td>${poDetail.skuInfo.partNo}</td>
								<td>${poDetail.skuInfo.skuPropertyInfos}</td>
								<td>${poDetail.ordQty}</td>
								<td>${poDetail.sendQty}</td>
								<td>${poDetail.unitPrice}</td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${bizPoHeader.poDetailList==null}">
						<c:forEach items="${skuInfoMap}" var="map">
							<tr>
					<td><img style="width: 20%" src="${map.key.productInfo.imgUrl}"/></td>
					<td>${map.key.productInfo.brandName}</td>
					<td>${map.key.name}</td>
					<td>${map.key.partNo}</td>
					<td>${map.key.skuPropertyInfos}</td>
					<td>${map.value.reqQty}
						<input type='hidden' name='reqDetailIds' value='${map.value.reqDetailIds}'/>
						<input type='hidden' name='skuInfoIds' value='${map.key.id}'/>
						<input type='hidden' name='orderDetailIds' value='${map.value.orderDetailIds}'/>
					<%--<input  type='hidden' name='lineNos' value='${reqDetail.lineNo}'/>--%>
					<%--<input name='reqQtys'  value="${reqDetail.reqQty}" class="input-mini" type='text'/>--%>
					</td>
					<%--<td>${reqDetail.recvQty}</td>--%>
					<td><input  name="ordQtys" readonly="readonly"  value="${map.value.reqQty}" class="input-mini" type='text'/></td>
					<td>
					<input type="text" name="unitPrices" class="input-mini">
					</td>

					</tr>
					</c:forEach>
					</c:if>
			</tbody>
		</table>


		<div class="form-actions">
			<shiro:hasPermission name="biz:po:bizPoHeader:edit">
				<c:if test="${bizPoHeader.poDetailList==null}">
					<input id="btnSubmit" type="button" onclick="savePoOrder()"  class="btn btn-primary"  value="生成采购单"/>
				</c:if>
				&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

</body>
</html>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单收货</title>
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

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a>销售单详情<shiro:lacksPermission name="biz:request:bizRequestHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<%--@elvariable id="bizSendGoodsRecord" type="com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord"--%>
	<form:form id="inputForm" modelAttribute="bizSendGoodsRecord" action="${ctx}/biz/inventory/bizSendGoodsRecord/save" method="post" class="form-horizontal">
		<%--<form:hidden path="id"/>--%>
		<sys:message content="${message}"/>
		<input name="bizRequestHeader.id" value="${bizRequestHeader==null?0:bizRequestHeader.id}" type="hidden"/>
		<input name="bizOrderHeader.id" value="${bizOrderHeader==null?0:bizOrderHeader.id}" type="hidden"/>

		<div class="control-group">
			<label class="control-label">${bizRequestHeader==null?'销售订单编号':'备货清单编号'}：</label>
			<div class="controls">
				<input type="text" class="input-xlarge" readonly="readonly" value="${bizRequestHeader==null?bizOrderHeader.orderNum:bizRequestHeader.reqNo}"/>

				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">${bizRequestHeader==null?'采购商':'采购中心'}：</label>
			<div class="controls">
				<input type="text" class="input-xlarge" readonly="readonly" value="${bizRequestHeader==null?bizOrderHeader.customer.name:bizRequestHeader.fromOffice.name}"/>
				<input type="hidden" name="customer.id" value="${bizRequestHeader==null?bizOrderHeader.customer.id:bizRequestHeader.fromOffice.id}">
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">收货地址：</label>
			<div class="controls">
				<input type="text" class="input-xlarge" readonly="readonly" value="${bizOrderHeader.bizLocation.fullAddress}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">联系人：</label>
			<div class="controls">
				<input type="text" class="input-xlarge" readonly="readonly" value="${bizOrderHeader.bizLocation.receiver}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">联系电话：</label>
			<div class="controls">
				<input type="text" class="input-xlarge" readonly="readonly" value="${bizOrderHeader.bizLocation.phone}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">期望收货时间：</label>
			<div class="controls">
				<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate required"
						<c:if test="${bizRequestHeader != null}">
							value="<fmt:formatDate value="${bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
						</c:if>
						<c:if test="${bizOrderHeader != null}"	>
							value="<fmt:formatDate value="${bizOrderHeader.deliveryDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
						</c:if>
					<%--value="<fmt:formatDate value="${bizRequestHeader==null?'':bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"--%>
				/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>



		<div class="control-group">
			<label class="control-label">订单商品：</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>产品图片</th>
						<th>产品分类</th>
						<th>商品名称</th>
						<th>商品货号</th>
						<th>供应商</th>
						<th>品牌</th>
						<th>申报数量</th>
						<th>已供数量</th>
						<c:if test="${bizStatu==0}">
							<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
								<th>供货仓库</th>
							</shiro:hasPermission>
						</c:if>
					</tr>
					</thead>
					<tbody id="prodInfo">

					<c:if test="${ordDetailList!=null && ordDetailList.size()>0}">
						<c:forEach items="${ordDetailList}" var="ordDetail" varStatus="ordStatus">
							<tr id="${ordDetail.id}" class="ordDetailList">
							<td><img style="max-width: 120px" src="${ordDetail.skuInfo.productInfo.imgUrl}"/></td>
							<td>${ordDetail.skuInfo.productInfo.bizVarietyInfo.name}</td>
							<td>${ordDetail.skuInfo.name}</td>
							<td>${ordDetail.skuInfo.itemNo}</td>
							<td>
									${ordDetail.skuInfo.productInfo.office.name}
										<%--<input name="bizSendGoodsRecord.vend.id" value="${reqDetail.skuInfo.productInfo.office.id}" type="hidden"/>--%>
							</td>
							<td>${ordDetail.skuInfo.productInfo.brandName}</td>

							<td>
								<input type='hidden' name='bizSendGoodsRecordList[${ordStatus.index}].skuInfo.id' value='${ordDetail.skuInfo.id}'/>
								<input type='hidden' name='bizSendGoodsRecordList[${ordStatus.index}].skuInfo.name' value='${ordDetail.skuInfo.name}'/>
								<input id="ordQty${ordStatus.index}" name='bizSendGoodsRecordList[${ordStatus.index}].bizOrderDetail.ordQty' readonly="readonly" value="${ordDetail.ordQty}" type='text'/>
								<input name="bizSendGoodsRecordList[${ordStatus.index}].bizOrderDetail.id" value="${ordDetail.id}" type="hidden"/>
								<input name="bizSendGoodsRecordList[${ordStatus.index}].bizOrderDetail.orderHeader.id" value="${ordDetail.orderHeader.id}" type="hidden"/>
								<c:if test="${ordDetail.orderHeader.orderNum != null}">
									<input name="bizSendGoodsRecordList[${ordStatus.index}].orderNum" value="${ordDetail.orderHeader.orderNum}" type="hidden"/>
								</c:if>

							</td>
							<td>
								<input id="sentQty${ordStatus.index}" name='bizSendGoodsRecordList[${ordStatus.index}].bizOrderDetail.sentQty' readonly="readonly" value="${ordDetail.sentQty}" type='text'/>
							</td>
							<c:if test="${bizStatu==0}">
								<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
									<td>
										<select disabled="disabled" name="invInfo.id" class="input-medium">
											<c:forEach  items="${invInfoList}" var="invInfo">
												<option value="${invInfo.id}"/>${invInfo.name}
											</c:forEach>
										</select>
									</td>
								</shiro:hasPermission>
							</c:if>
							</tr>
						</c:forEach>
					</c:if>
					</tbody>
				</table>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">

				<textarea  class="input-xlarge " readonly="readonly">${entity.remark}</textarea>
			</div>
		</div>

		<div class="form-actions">
			<input onclick="window.print();" type="button" class="btn btn-primary" value="打印供货清单发货" style="background:#F78181;"/>
			&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
		</div>

	</form:form>

</body>
</html>
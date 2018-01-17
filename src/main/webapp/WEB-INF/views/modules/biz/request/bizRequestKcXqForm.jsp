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
		<li class="active">销售单详情<shiro:lacksPermission name="biz:request:bizRequestHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<%--@elvariable id="bizSendGoodsRecord" type="com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord"--%>
	<form:form id="inputForm" modelAttribute="bizSendGoodsRecord" action="${ctx}/biz/inventory/bizSendGoodsRecord/save" method="post" class="form-horizontal">
		<%--<form:hidden path="id"/>--%>
		<sys:message content="${message}"/>
		<input name="bizRequestHeader.id" value="${bizRequestHeader==null?0:bizRequestHeader.id}" type="hidden"/>
		<input name="bizOrderHeader.id" value="${bizOrderHeader==null?0:bizOrderHeader.id}" type="hidden"/>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<input readonly="readonly" value="${bizRequestHeader==null?bizOrderHeader.customer.name:bizRequestHeader.fromOffice.name}"/>
				<input type="hidden" name="customer.id" value="${bizRequestHeader==null?bizOrderHeader.customer.id:bizRequestHeader.fromOffice.id}">
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
			<label class="control-label">备货商品：</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>商品图片</th>
						<th>商品名称</th>
						<th>商品分类</th>
						<th>商品代码</th>
						<th>品牌名称</th>
						<th>供应商</th>
						<th>SKU</th>
						<th>SKU编号</th>
						<th>申报数量</th>
						<th>已供数量</th>

						<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
							<th>供货仓库</th>
						</shiro:hasPermission>

					</tr>
					</thead>
					<tbody id="prodInfo">

					<c:if test="${ordDetailList!=null && ordDetailList.size()>0}">
						<c:forEach items="${ordDetailList}" var="ordDetail" varStatus="ordStatus">
							<tr id="${ordDetail.id}" class="ordDetailList">
							<td><img src="${ordDetail.skuInfo.productInfo.imgUrl}"/></td>
							<td>${ordDetail.skuInfo.productInfo.name}</td>
							<td>
								<c:forEach items="${ordDetail.skuInfo.productInfo.categoryInfoList}" var="cate" varStatus="cateIndex" >
									${cate.name}
									<c:if test="${!cateIndex.last}">
										/
									</c:if>

								</c:forEach>
							</td>
							<td>${ordDetail.skuInfo.productInfo.prodCode}</td>
							<td>${ordDetail.skuInfo.productInfo.brandName}</td>
							<td>
									${ordDetail.skuInfo.productInfo.office.name}
									<%--<input name="bizSendGoodsRecord.vend.id" value="${reqDetail.skuInfo.productInfo.office.id}" type="hidden"/>--%>
							</td>
							<td>${ordDetail.skuInfo.name}</td>
							<td>${ordDetail.skuInfo.partNo}</td>
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

							<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
								<td>
									<select disabled="disabled" name="invInfo.id" class="input-medium">
										<c:forEach  items="${invInfoList}" var="invInfo">
											<option value="${invInfo.id}"/>${invInfo.name}
										</c:forEach>
									</select>
								</td>
							</shiro:hasPermission>
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
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>

	</form:form>

</body>
</html>
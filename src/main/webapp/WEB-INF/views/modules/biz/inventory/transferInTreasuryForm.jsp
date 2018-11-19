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
			if ($("#str").val() == 'detail') {
			    $("#invInfo").attr("readonly","readonly");
			}
            $("#inputForm").validate({
                submitHandler: function(form){
                    if(window.confirm('你确定要收货吗？')){
                        loading('正在提交，请稍等...');
                        form.submit();
                        return true;

                    }else{
                        //alert("取消");
                        return false;
                    }
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
        function checkout(obj) {
            var transQty = $("#transQty"+obj).val();	//调拨数量
            var receiveNum = $("#receiveNum"+obj).val();		//收货数量
            var inQty = $("#inQty"+obj).val();		//已收货数量
            // var outQty = $("#outQty"+obj).val();		//已供货数量
            var sum = parseInt(receiveNum) + parseInt(inQty);
            if (parseInt(sum) > parseInt(transQty)){
                alert("收货数太大，已超过申报数量，请重新调整收货数量！");
                $("#receiveNum"+obj).val(0);
                return false;
            }
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/biz/inventory/bizSkuTransfer?source=${bizSkuTransfer.source}&str=${bizSkuTransfer.str}">调拨单列表</a></li>
</ul><br/>
<%--@elvariable id="bizSendGoodsRecord" type="com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord"--%>
<form:form id="inputForm" modelAttribute="bizSkuTransfer" action="${ctx}/biz/inventory/bizSkuTransfer/inTreasury" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<sys:message content="${message}"/>
	<input type="hidden" name="str" id="str" value="${bizSkuTransfer.str}"/>
	<input type="hidden" name="source" id="source" value="${bizSkuTransfer.source}"/>
	<div class="control-group">
		<label class="control-label">入库单：</label>
		<div class="controls">
			<input readonly="readonly" name="collectNo" type="text" class="input-xlarge" value="${collectNo}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">调拨单：</label>
		<div class="controls">
			<input readonly="readonly" type="text" class="input-xlarge" value="${bizSkuTransfer.transferNo}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">发货单号：</label>
		<div class="controls">
			<c:forEach items="${invoiceList}" var="invoice">
				<input readonly="readonly" type="text" class="input-xlarge" value="${invoice.sendNumber}"/>
			</c:forEach>
		</div>
	</div>
	<%--<div class="control-group">--%>
		<%--<label class="control-label">采购中心：</label>--%>
		<%--<div class="controls">--%>
			<%--<input readonly="readonly" type="text" class="input-xlarge" value="${bizRequestHeader==null?bizOrderHeader.customer.name:bizRequestHeader.fromOffice.name}"/>--%>
			<%--<input type="hidden" name="customer.id" value="${bizRequestHeader==null?bizOrderHeader.customer.id:bizRequestHeader.fromOffice.id}">--%>
			<%--<span class="help-inline"><font color="red">*</font> </span>--%>
		<%--</div>--%>
	<%--</div>--%>
	<div class="control-group">
		<label class="control-label">起始采购中心仓库：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizSkuTransfer.fromInv.name}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">目标采购中心仓库：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizSkuTransfer.toInv.name}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">期望收货时间：</label>
		<div class="controls">
			<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate required"
				   value="<fmt:formatDate value="${bizSkuTransfer==null?'':bizSkuTransfer.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
			/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">调拨商品：</label>
		<div class="controls">
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th>产品图片</th>
					<th>产品分类</th>
					<th>商品名称</th>
					<th>商品货号</th>
					<th>颜色</th>
					<th>尺寸</th>
					<th>供应商</th>
					<th>品牌</th>
					<th>调拨数量</th>
					<th>本次发货数量</th>
					<th>累计发货数量</th>
					<th>已入库数量</th>
					<c:if test="${bizSkuTransfer.str != 'detail'}">
						<shiro:hasPermission name="biz:inventory:bizSkuTransfer:inTreasury">
							<th>入库数量</th>
						</shiro:hasPermission>
					</c:if>
					<shiro:hasPermission name="biz:inventory:bizSkuTransfer:inTreasury">
						<th>收货仓库</th>
					</shiro:hasPermission>

				</tr>
				</thead>
				<tbody id="prodInfo">
				<c:if test="${transferDetailList!=null && transferDetailList.size()>0}">
					<c:forEach items="${transferDetailList}" var="transferDetail" varStatus="transferStatus">
						<tr id="${transferDetail.id}" class="transferDetailList">
							<td><img style="max-width: 120px" src="${transferDetail.skuInfo.productInfo.imgUrl}"/></td>
							<td>${transferDetail.skuInfo.productInfo.bizVarietyInfo.name}</td>
							<td>${transferDetail.skuInfo.name}</td>
							<td>${transferDetail.skuInfo.itemNo}</td>
							<td>${transferDetail.skuInfo.color}</td>
							<td>${transferDetail.skuInfo.size}</td>
							<td>${transferDetail.skuInfo.productInfo.vendorName}</td>
							<td>${transferDetail.skuInfo.productInfo.brandName}</td>
							<td>
								<input type='hidden' name='bizCollectGoodsRecordList[${transferStatus.index}].skuInfo.id' value='${transferDetail.skuInfo.id}'/>
								<input type='hidden' name='bizCollectGoodsRecordList[${transferStatus.index}].skuInfo.name' value='${transferDetail.skuInfo.name}'/>
								<input id="transQty${transferStatus.index}" name='bizCollectGoodsRecordList[${transferStatus.index}].transferDetail.transQty' readonly="readonly" value="${transferDetail.transQty}" type='text'/>
								<input name="bizCollectGoodsRecordList[${transferStatus.index}].transferDetail.id" value="${transferDetail.id}" type="hidden"/>
								<input name="bizCollectGoodsRecordList[${transferStatus.index}].transferDetail.transfer.id" value="${transferDetail.transfer.id}" type="hidden"/>
								<c:if test="${transferDetail.transfer.transferNo != null}">
									<input name="bizCollectGoodsRecordList[${transferStatus.index}].orderNum" value="${transferDetail.transfer.transferNo}" type="hidden"/>
								</c:if>
							</td>
							<td>
								<input readonly="readonly" value="${transferDetail.outQty - transferDetail.inQty}" type='text'/>
							</td>
							<td>
							<input readonly="readonly" value="${transferDetail.outQty}" type='text'/>
							</td>
							<td>
								<input id="inQty${transferStatus.index}" name='bizCollectGoodsRecordList[${transferStatus.index}].transferDetail.inQty' readonly="readonly" value="${transferDetail.inQty}" type='text'/>
							</td>

							<c:if test="${bizSkuTransfer.str ne 'detail'}">
								<shiro:hasPermission name="biz:inventory:bizSkuTransfer:inTreasury">
									<td>
										<input id="receiveNum${transferStatus.index}" name="bizCollectGoodsRecordList[${transferStatus.index}].receiveNum" <c:if test="${transferDetail.inQty==transferDetail.transQty}">readonly="readonly"</c:if> value="0" type="number" min="0" class="input-medium requried" onblur="checkout(${transferStatus.index})"/>
									</td>
								</shiro:hasPermission>
							</c:if>
							<shiro:hasPermission name="biz:inventory:bizSkuTransfer:inTreasury">
								<td>
									<select id="invInfo" name="bizCollectGoodsRecordList[${transferStatus.index}].invInfo.id" class="input-medium required">
										<c:forEach items="${invInfoList}" var="invInfo">
											<option value="${invInfo.id}"/>${invInfo.name}
										</c:forEach>
									</select>
										<%--<input id="invInfo.id${transferStatus.index}" name="bizCollectGoodsRecordList[${transferStatus.index}].invInfo.id" <c:if test="${transferDetail.inQty==transferDetail.transQty}">readonly="readonly"</c:if> value="" type="text"/>--%>
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
			<textarea  class="input-xlarge " readonly="readonly">${bizSkuTransfer.remark}</textarea>
		</div>
	</div>

	<div class="form-actions">
		<c:if test="${bizSkuTransfer.str ne 'detail'}">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="确认收货"/>&nbsp;</shiro:hasPermission>
		</c:if>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
	</div>

</form:form>

</body>
</html>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
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
	<li><a href="${ctx}/biz/request/bizRequestHeader/">备货清单列表</a></li>
</ul><br/>
<form:form id="inputForm"  method="post" class="form-horizontal">
	<div class="control-group">
		<label class="control-label">采购中心：</label>
		<div class="controls">
			<input readonly="readonly" value="${bizRequestHeader==null?bizOrderHeader.customer.name:bizRequestHeader.fromOffice.name}"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">期望收货时间：</label>
		<div class="controls">
			<input type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate required"
				   value="<fmt:formatDate value="${bizRequestHeader==null?bizOrderHeader.deliveryDate:bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
			/>			<span class="help-inline"><font color="red">*</font> </span>
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
					<th>已供货数量</th>
					<th>操作</th>
				</tr>
				</thead>
				<tbody id="prodInfo">
				<c:if test="${reqDetailList!=null && reqDetailList.size()>0}">
					<c:forEach items="${reqDetailList}" var="reqDetail" varStatus="reqStatus">
						<tr id="${reqDetail.id}">
							<td><img src="${reqDetail.skuInfo.productInfo.imgUrl}"/></td>
							<td>${reqDetail.skuInfo.productInfo.name}</td>
							<td>
								<c:forEach items="${reqDetail.skuInfo.productInfo.categoryInfoList}" var="cate" varStatus="cateIndex" >
									${cate.name}
									<c:if test="${!cateIndex.last}">
										/
									</c:if>

								</c:forEach>
							</td>
							<td>${reqDetail.skuInfo.productInfo.prodCode}</td>
							<td>${reqDetail.skuInfo.productInfo.brandName}</td>
							<td>${reqDetail.skuInfo.productInfo.office.name}</td>
							<td>${reqDetail.skuInfo.name}</td>
							<td>${reqDetail.skuInfo.partNo}</td>
							<td>
								<input   value="${reqDetail.reqQty}" readonly="readonly" class="input-medium" type='text'/>
							</td>
							<td>
								<input  value="${reqDetail.recvQty}" readonly="readonly" class="input-medium" type='text'/>
							</td>
						</tr>
					</c:forEach>
				</c:if>

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
								<input name='bizSendGoodsRecordList[${ordStatus.index}].bizRequestDetail.reqQty' readonly="readonly" value="${ordDetail.ordQty-ordDetail.sentQty}" type='text'/>

							</td>

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
			<form:textarea path="remark" htmlEscape="false" maxlength="200" class="input-xlarge "/>
		</div>
	</div>

	<div class="form-actions">
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>

</form:form>

</body>
</html>
<%@ taglib prefix="s" uri="http://www.opensymphony.com/sitemesh/decorator" %>
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
	<li class="active">
		<a href="">备货清单收货详情</a>
	</li>
</ul><br/>
<form:form id="inputForm"  method="post" class="form-horizontal">

	<div class="control-group">
		<label class="control-label">入库单：</label>
		<div class="controls">
			<input readonly="readonly" name="collectNo" type="text" class="input-xlarge" value="${collectGoodsRecord.collectNo}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">备货清单号：</label>
		<div class="controls">
			<input readonly="readonly" type="text" class="input-xlarge" value="${bizRequestHeader.reqNo}"/>
			<span class="help-inline"><font color="red">*</font></span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">备货清类型：</label>
		<div class="controls">
			<input readonly="readonly" type="text" class="input-xlarge" value="${fns:getDictLabel(bizRequestHeader.headerType,'req_header_type','未知')}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">发货单号：</label>
		<div class="controls">
			<c:forEach items="${deliverNoList}" var="deliverNo">
				<input readonly="readonly" type="text" class="input-xlarge" value="${deliverNo}"/>
			</c:forEach>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">采购中心：</label>
		<div class="controls">
			<input readonly="readonly" type="text" class="input-xlarge" value="${bizRequestHeader==null?bizOrderHeader.customer.name:bizRequestHeader.fromOffice.name}"/>
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
	<c:if test="${invoiceList != null && bizStatu != 0}">
		<div class="control-group">
			<label class="control-label">集货信息：</label>
			<div class="controls">
				<table class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th>集货地点</th>
							<th>验货员</th>
							<th>验货时间</th>
							<th>验货备注</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${invoiceList}" var="invoice">
							<tr>
								<td>${fns:getDictLabel(invoice.collLocate, 'coll_locate', '')}</td>
								<td>${invoice.inspector.name}</td>
								<td><fmt:formatDate value="${invoice.inspectDate}"  pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<td><textarea>${invoice.inspectRemark}</textarea></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</c:if>
	<div class="control-group">
		<label class="control-label">备货商品：</label>
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
					<th>供应商电话</th>
					<th>品牌</th>
					<th>品类主管</th>
					<th>申报数量</th>
					<th>本次发货数量</th>
					<th>累计发货数量</th>
					<c:if test="${bizStatu == 0}">
						<th>已入库数量</th>
					</c:if>
					<c:if test="${bizStatu != 0}">
						<th>已供货数量</th>
					</c:if>
					<c:if test="${not empty source && source eq 'gh'}">
						<%--该备货单已生成采购单就显示--%>
						<%--<c:if test="${empty bizRequestHeader.poSource}">--%>
							<%--<th>已生成的采购单</th>--%>
							<%--<th>采购数量</th>--%>
						<%--</c:if>--%>
					</c:if>
				</tr>
				</thead>
				<tbody id="prodInfo">
				<c:if test="${reqDetailList!=null && reqDetailList.size()>0}">
					<c:forEach items="${reqDetailList}" var="reqDetail" varStatus="reqStatus">
						<tr id="${reqDetail.id}">
							<td>
								<img style="max-width: 80px" src="${reqDetail.skuInfo.productInfo.imgUrl}"/>
							</td>
							<td>${reqDetail.skuInfo.productInfo.bizVarietyInfo.name}</td>

							<td>${reqDetail.skuInfo.name}</td>
							<td>${reqDetail.skuInfo.itemNo}</td>
							<td>${reqDetail.skuInfo.color}</td>
							<td>${reqDetail.skuInfo.size}</td>
							<td><a href="${ctx}/sys/office/supplierForm?id=${reqDetail.skuInfo.productInfo.office.id}&gysFlag=onlySelect">
								${reqDetail.skuInfo.productInfo.office.name}</a></td>
							<td>${reqDetail.skuInfo.productInfo.office.user.mobile}</td>
							<td>${reqDetail.skuInfo.productInfo.brandName}</td>
							<td>${reqDetail.varietyUser.name}</td>
							<td>
								<input   value="${reqDetail.reqQty}" readonly="readonly" class="input-medium" type='text'/>
							</td>
							<td>
								<input readonly="readonly" value="${reqDetail.sendQty - reqDetail.recvQty}" type='text'/>
							</td>
							<td>
								<input readonly="readonly" value="${reqDetail.sendQty}" type='text'/>
							</td>
							<c:if test="${bizStatu == 0}">
								<td>
									<input  value="${reqDetail.recvQty}" readonly="readonly" class="input-medium" type='text'/>
								</td>
							</c:if>
							<c:if test="${bizStatu != 0}">
								<td>
									<input  value="${reqDetail.sendQty}" readonly="readonly" class="input-medium" type='text'/>
								</td>
							</c:if>

							<c:if test="${not empty source && source eq 'gh'}">
								<%--该备货单已生成采购单就显示--%>
								<%--<c:if test="${reqDetail.bizPoHeader!=null}">--%>
									<%--<td><a href="${ctx}/biz/po/bizPoHeader/form?id=${reqDetail.bizPoHeader.id}&str=detail">${reqDetail.bizPoHeader.orderNum}</a></td>--%>
									<%--<td>${reqDetail.reqQty}</td>--%>
								<%--</c:if>--%>
							</c:if>

						</tr>
					</c:forEach>
				</c:if>

				<c:if test="${ordDetailList!=null && ordDetailList.size()>0}">
					<c:forEach items="${ordDetailList}" var="ordDetail" varStatus="ordStatus">
						<tr id="${ordDetail.id}" class="ordDetailList">
							<td><img style="max-width: 120px" src="${ordDetail.skuInfo.productInfo.imgUrl}"/></td>
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
								<input  value="${ordDetail.ordQty}" readonly="readonly" class="input-medium" type='text'/>
							</td>
							<td>
								<input  value="${ordDetail.sentQty}" readonly="readonly" class="input-medium" type='text'/>
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
			<textarea readonly="readonly"  class="input-xlarge ">${bizRequestHeader.remark}</textarea>
		</div>
	</div>

	<div class="form-actions">
		<input onclick="window.print();" type="button" class="btn btn-primary" value="打印备货清单收货" style="background:#F78181;"/>
		&nbsp;&nbsp;&nbsp;
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
	</div>

</form:form>

</body>
</html>
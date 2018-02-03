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
		<table id="contentTable"  class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<th>产品图片</th>
				<th>品牌名称</th>
				<th>商品名称</th>
				<th>商品编码</th>
				<th>商品属性</th>
				<th>申报数量</th>
				<th>采购数量</th>
				<th>商品单价</th>


			</tr>
			</thead>
			<tbody id="prodInfo">
					<c:forEach items="${skuInfoMap}" var="map">
						<tr>
					<td><img style="width: 160px;height: 160px" src="${map.key.productInfo.imgUrl}"/></td>
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
			</tbody>
		</table>


		<div class="form-actions">
			<shiro:hasPermission name="biz:po:bizPoHeader:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="生成采购单"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

</body>
</html>
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
	<li><a href="${ctx}/biz/request/bizRequestHeader?source=gh">备货清单列表</a></li>
</ul><br/>
<form:form id="inputForm"  method="post" action="${ctx}/biz/po/bizPoHeader/savePoHeaderDetail" class="form-horizontal">

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
					<th>供应商</th>
					<th>购买数量</th>
					<th>价钱</th>
				</tr>
				</thead>
				<tbody id="prodInfo">

					<c:forEach items="${skuInfoMap}" var="skuInfo" varStatus="skuStatus">
						<c:if test="${skuInfo.value.reqQty!=skuInfo.value.sentQty}">
						<tr>
							<td><img src="${skuInfo.key.productInfo.imgUrl}"/></td>
							<td>${skuInfo.key.productInfo.name}</td>
							<td>
								<c:forEach items="${skuInfo.key.productInfo.categoryInfoList}" var="cate" varStatus="cateIndex" >
									${cate.name}
									<c:if test="${!cateIndex.last}">
										/
									</c:if>

								</c:forEach>
							</td>
							<td>${skuInfo.key.productInfo.prodCode}</td>
							<td>${skuInfo.key.productInfo.brandName}</td>
							<td>${skuInfo.key.productInfo.office.name}</td>
							<td>${skuInfo.key.name}</td>
							<td>${skuInfo.key.partNo}</td>
							<td>
								<input   value="${skuInfo.value.reqQty}" readonly="readonly" class="input-medium" type='text'/>
							</td>
							<td>
								<input  value="${skuInfo.value.sentQty}" readonly="readonly" class="input-medium" type='text'/>


							</td>
							<td>
								<input type='hidden' name='poDetailList[${skuStatus.index}].skuInfo.id' value='${skuInfo.key.id}'/>
								<input type='hidden' name='poDetailList[${skuStatus.index}].skuInfo.orderIds' value='${skuInfo.value.orderIds}'/>
								<input type='hidden' name='poDetailList[${skuStatus.index}].skuInfo.reqIds' value='${skuInfo.value.reqIds}'/>

									<sys:treeselect id="vendOffice" name="poDetailList[${skuStatus.index}].poHeader.vendOffice.id" value="${skuInfo.key.productInfo.office.id}" labelName="poDetailList[${skuStatus.index}].poHeader.vendOffice.name"
									labelValue="${skuInfo.key.productInfo.office.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
									title="供应商"  url="/sys/office/queryTreeList?type=7" cssClass="input-medium required" dataMsgRequired="必填信息">
									</sys:treeselect>
							</td>
							<td>
								<input name="poDetailList[${skuStatus.index}].ordQty" class="input-medium" value=""/>
							</td>
							<td>
								<input name="poDetailList[${skuStatus.index}].unitPrice" class="input-medium" value=""/>

							</td>
						</tr>
						</c:if>
					</c:forEach>


				</tbody>
			</table>


	<div class="form-actions">
		<shiro:hasPermission name="biz:po:bizPoDetail:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="生成采购订单"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>

</form:form>

</body>
</html>
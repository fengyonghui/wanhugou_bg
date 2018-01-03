<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
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
		<li class="active"><a href="${ctx}/biz/request/bizRequestHeader/form?id=${bizRequestHeader.id}">备货清单<shiro:hasPermission name="biz:request:bizRequestHeader:edit">${not empty bizRequestHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:request:bizRequestHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="" action="" method="post" class="form-horizontal">
		<%--<form:hidden path="id"/>--%>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<input readonly="readonly" value="${entity.fromOffice.name}"/>
				<%--<sys:treeselect id="fromOffice" name="fromOffice.id"  value="${entity.fromOffice.id}" labelName="fromOffice.name"--%>
								<%--labelValue="${entity.fromOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true"--%>
								<%--title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-xlarge required" dataMsgRequired="必填信息">--%>
				<%--</sys:treeselect>--%>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">期望收货时间：</label>
			<div class="controls">
				<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate required"
					value="<fmt:formatDate value="${entity.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
						<th>申报数量</th>
						<c:if test="${entity.bizStatus==ReqHeaderStatusEnum.APPROVE.ordinal()}">
							<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
								<th>供应数量</th>
							</shiro:hasPermission>
						</c:if>
					</tr>
					</thead>
					<tbody id="prodInfo">
					<c:if test="${reqDetailList!=null}">
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
								<td>
									<input type='hidden' name='' value='${reqDetail.id}'/>
									<input type='hidden' name='' value='${reqDetail.skuInfo.id}'/>
									<input name='' readonly="readonly" value="${reqDetail.reqQty}" type='text'/>
								</td>
								<c:if test="${entity.bizStatus==ReqHeaderStatusEnum.APPROVE.ordinal()}">
								<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
								<td>
									<input type='hidden' name='' value='${reqDetail.skuInfo.id}'/>
									<input name=""/>
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
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>

	</form:form>

</body>
</html>
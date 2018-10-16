<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>库存盘点记录管理</title>
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
		<li><a href="${ctx}/biz/inventoryviewlog/bizInventoryViewLog/">库存盘点记录列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventoryviewlog/bizInventoryViewLog/form?id=${bizInventoryViewLog.id}">库存盘点记录<shiro:hasPermission name="biz:inventoryviewlog:bizInventoryViewLog:edit">${not empty bizInventoryViewLog.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventoryviewlog:bizInventoryViewLog:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInventoryViewLog" action="${ctx}/biz/inventoryviewlog/bizInventoryViewLog/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">备货单号：</label>
			<div class="controls">
				<input type="text"  class="input-medium" name="" readonly="readonly" value="${bizInventoryViewLog.requestHeader.reqNo}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<input type="text" name="" class="input-medium" readonly="readonly" value="${bizInventoryViewLog.requestHeader.requestDetailList[0].skuInfo.vendorName}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<input type="text" name="" class="input-medium" readonly="readonly" value="${bizInventoryViewLog.requestHeader.fromOffice.name}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属仓库：</label>
			<div class="controls">
				<input type="text" name="" class="input-medium" readonly="readonly" value="${bizInventoryViewLog.invInfo.name}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">盘点日期：</label>
			<div class="controls">
				<input type="text" name="" class="input-medium" readonly="readonly" value="<fmt:formatDate value="${bizInventoryViewLog.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">盘点人：</label>
			<div class="controls">
				<input type="text" name="" class="input-medium" readonly="readonly" value="${bizInventoryViewLog.createBy.name}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品信息：</label>
			<div class="controls">
				<table id="contentTable"  class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>商品名称</th>
						<th>商品货号</th>
						<th>颜色</th>
						<th>尺寸</th>
						<th>库存类型</th>
						<th>结算价</th>
						<th>图片</th>
						<th>现有库存数</th>
						<th>实际库存数</th>
					</tr>
					</thead>
					<tbody id="invReq">
					<c:forEach items="${bizInventoryViewLog.requestHeader.requestDetailList}" var="requestDetail" varStatus="v">
						<tr>
							<input name="reqDetailId" value="${requestDetail.id}" type="hidden"/>
							<td>${requestDetail.skuInfo.name}</td>
							<td>${requestDetail.skuInfo.itemNo}</td>
							<td>${requestDetail.skuInfo.color}</td>
							<td>${requestDetail.skuInfo.size}</td>
							<td>${fns:getDictLabel(requestHeader.headerType,'req_header_type','未知')}</td>
							<td>${requestDetail.skuInfo.buyPrice}</td>
							<td style='width: 200px'><img style='width: 200px' src="${requestDetail.skuInfo.skuImgUrl}"></td>
							<td>${requestDetail.recvQty - requestDetail.outQty}</td>

							<td><input name="actualQtys" readonly="readonly" title="${v.index}" value="${requestDetail.actualQty}" type="number" class="input-mini"/></td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventoryviewlog:bizInventoryViewLog:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
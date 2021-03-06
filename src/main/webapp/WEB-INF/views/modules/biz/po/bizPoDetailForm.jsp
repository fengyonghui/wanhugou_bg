<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单详细信息管理</title>
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
        function clickSku(){
            var skuInfoId=$('#skuInfoId').val();
            $("#partNo").empty();
            $("#unitPrice").empty();
            $.ajax({
                type:"post",
                url:"${ctx}/biz/sku/bizSkuInfo/findSysBySku?skuId="+skuInfoId,
                dataType:"json",
                success:function(data){
                    $("#partNo").val(data.partNo);
                    $("#unitPrice").val(data.buyPrice);
                }
            });
//            $("#partNo").change();
//            $("#unitPrice").change();
        }

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/po/bizPoDetail/">采购订单详细信息列表</a></li>
		<li class="active"><a href="${ctx}/biz/po/bizPoDetail/form?id=${bizPoDetail.id}">采购订单详细信息<shiro:hasPermission name="biz:po:bizPoDetail:edit">${not empty bizPoDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:po:bizPoDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizPoDetail" action="${ctx}/biz/po/bizPoDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="poHeader.id"/>
		<sys:message content="${message}"/>		
		<%--<div class="control-group">--%>
			<%--<label class="control-label">biz_po_header.id：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="orderId" htmlEscape="false" maxlength="11" class="input-xlarge required"/>--%>
				<%--<span class="help-inline"><font color="red">*</font> </span>--%>
			<%--</div>--%>
		<%--</div>--%>
		<div class="control-group">
			<label class="control-label">订单详情行号：</label>
			<div class="controls">
				<form:input readonly="true" path="lineNo" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品名称：</label>
			<div class="controls">
				<sys:treeselect id="skuInfo" name="skuInfo.id" value="${bizPoDetail.skuInfo.id}" labelName="skuInfo.name"
								labelValue="${bizPoDetail.skuInfo.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="sku名称"  url="/biz/product/bizProductInfo/querySkuTreeList" extId="${skuInfo.id}"
								cssClass="input-xlarge required" onchange="clickSku();" allowClear="${skuInfo.currentUser.admin}"  dataMsgRequired="必填信息">
				</sys:treeselect>
				<%--<form:input path="skuName" htmlEscape="false" maxlength="30" class="input-xlarge required"/>--%>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品编号：</label>
			<div class="controls">
				<form:input path="partNo" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">商品单价：</label>
			<div class="controls">
				<form:input path="unitPrice" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购数量：</label>
			<div class="controls">
				<form:input path="ordQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:po:bizPoDetail:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
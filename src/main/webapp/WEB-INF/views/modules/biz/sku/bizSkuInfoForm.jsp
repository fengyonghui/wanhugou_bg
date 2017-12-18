<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品sku管理</title>
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
			if($("#id").val()!=''){
                var prodId=$("#prodId").val();
                ajaxGetProdPropInfo(prodId);
			}

            /***
			 * 通过产品Id获取属性
             * @param prodId
             */
            function ajaxGetProdPropInfo(prodId) {
                $.post("${ctx}/biz/product/bizProdPropertyInfo/findProdPropertyList",
                    {prodId:prodId,ranNum:Math.random()},
                    function(data,status) {
                        $.each(data, function (index, prodPropertyInfo) {
                            $.each(prodPropertyInfo.prodPropValueList, function (index, prodPropValue) {
//                                $("#"+prodPropValue.propertyInfoId).attr('checked',true)
//                                $("#value_"+prodPropValue.propertyValueId).attr('checked',true)
                            });
                        });
                    });
            }
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/sku/bizSkuInfo/">商品sku列表</a></li>
		<li class="active"><a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}">商品sku<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">${not empty bizSkuInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:sku:bizSkuInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizSkuInfo" action="${ctx}/biz/sku/bizSkuInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden id="prodId" path="productInfo.id"/>
		<sys:message content="${message}"/>
		<%--<div class="control-group">
			<label class="control-label">所属产品id：</label>
			<div class="controls">
				<form:input path="bizSkuInfo.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>--%>
		<div class="control-group">
			<label class="control-label">sku类型：</label>
			<div class="controls">
                    <form:select path="skuType" class="input-xlarge required">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('skuType')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/>
                    </form:select>
                    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品编码：</label>
			<div class="controls">
				<form:input path="partNo" htmlEscape="false" maxlength="30" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">基础售价：</label>
			<div class="controls">
				<form:input path="basePrice" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购价格：</label>
			<div class="controls">
				<form:input path="buyPrice" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品属性：</label>
			<div  id ="cateProp" class="controls">
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
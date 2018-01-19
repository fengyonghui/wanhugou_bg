<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品sku管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            var str=$("#str").val();
            if(str=='detail'){
                $("#inputForm").find("input[type!='button']").attr("disabled","disabled") ;
                $("#inputForm").find("select").attr("disabled","disabled") ;
                $("#inputForm").find("a[class='btn']").hide()
                //todo 图片选
            }
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
            $('.select_all').live('click',function(){
                var obj=$(this).attr("id");
                var choose=$(".value_"+obj);
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
			if($("#id").val()!=''){
                ajaxGetSkuPropInfo($("#id").val());
			}

            /***
			 * 通过产品Id获取属性
             * @param prodId
             */
            function ajaxGetSkuPropInfo(skuId) {
                $.post("${ctx}/biz/sku/bizSkuPropValue/findList",
                    {skuId:skuId,ranNum:Math.random()},
                    function(data,status) {
                        $.each(data, function (index, skuPropValue) {
                            $("#"+skuPropValue.prodPropertyInfo.id).attr('checked',true);
                            $("#value_"+skuPropValue.prodPropValue.id).attr('checked',true);
                        });
                    });
            }
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/sku/bizSkuInfo/">商品sku列表</a></li>
		<li class="active"><a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}">商品sku<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">${not empty bizSkuInfo.id?'详情':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:sku:bizSkuInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizSkuInfo" action="${ctx}/biz/sku/bizSkuInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="str" type="hidden"  value="${bizSkuInfo.str}"/>
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
			<label class="control-label">SKU类型：</label>
			<div class="controls">
                    <form:select path="skuType"  class="input-xlarge required">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('skuType')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/>
                    </form:select>
                    <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">SKU商品名称：</label>
			<div class="controls">
				<form:input path="name"  htmlEscape="false" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">SKU商品编码：</label>
			<div class="controls">
				<form:input path="partNo"  htmlEscape="false" maxlength="30" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">SKU商品图片:</label>
			<div class="controls">
				<form:hidden id="prodImg"  path="photos" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="prodImg" type="images" uploadPath="/sku/item" selectMultiple="true" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">基础售价：</label>
			<div class="controls">
				<form:input path="basePrice"  htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购价格：</label>
			<div class="controls">
				<form:input path="buyPrice"  htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">选择SKU属性：</label>
			<div class="controls">
				<c:forEach items="${prodPropInfoList}" var="propertyInfo">
					<input  disabled="disabled" class="select_all" id="${propertyInfo.id}" type="checkbox" name="prodPropertyInfos" value="${propertyInfo.id}"/> ${propertyInfo.propName}：
					<c:forEach items="${map[propertyInfo.id]}" var="propValue">
						<input disabled="disabled" class="value_${propertyInfo.id}" id="value_${propValue.id}" type="checkbox" name="prodPropMap[${propertyInfo.id}].prodPropertyValues" value="${propValue.id}"/> ${propValue.propValue}
					</c:forEach>
					<br/>
				</c:forEach>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
				<c:if test="${bizSkuInfo.str!='detail'}">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
					</c:if>
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
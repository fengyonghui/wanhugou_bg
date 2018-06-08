<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

            var str=$("#str").val();
            if(str=='detail'){
                $("#inputForm").find("input[type!='button']").attr("disabled","disabled") ;
                $("#inputForm").find("select").attr("disabled","disabled") ;
                $("#inputForm").find("a[class='btn']").hide()

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
            // $('.select_all').live('click',function(){
            //     var obj=$(this).attr("id");
            //     var choose=$(".value_"+obj);
            //     if($(this).attr('checked')){
            //         choose.attr('checked',true);
            //     }else{
            //         choose.attr('checked',false);
            //     }
            // });
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
                            console.log(skuPropValue);
                            $("#"+skuPropValue.prodPropertyInfo.id).attr('checked',true);
                            $("#value_"+skuPropValue.prodPropValue.id).attr('checked',true);
                            checkOnly(skuPropValue.prodPropertyInfo.id)
                        });
                    });
            }

		});
		function checkOnly(obj) {
			$(".value_"+obj).click(function () {
                if($(this).is(':checked')) {
                    $(".value_" + obj).attr('checked', false);
                    $(this).attr('checked', true);
                }
            })
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/sku/bizSkuInfo?productInfo.prodType=${prodType}">商品列表</a></li>
		<li class="active"><a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}&productInfo.prodType=${prodType}">商品<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">${not empty bizSkuInfo.id?'详情':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:sku:bizSkuInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizSkuInfo" action="${ctx}/biz/sku/bizSkuInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="str" type="hidden"  value="${bizSkuInfo.str}"/>
		<form:hidden id="sort" path="sort"/>
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
		<c:if test="${bizSkuInfo.id!=null}">
			<div class="control-group">
				<label class="control-label">SKU商品编码：</label>
				<div class="controls">
					<form:input path="partNo" disabled="true"  htmlEscape="false" maxlength="30" class="input-xlarge"/>
				</div>
			</div>


		</c:if>

		<div class="control-group">
			<label class="control-label">SKU商品图片:</label>
			<div class="controls">
				<form:hidden id="prodImg"  path="photos" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="prodImg" type="images" uploadPath="/sku/item" selectMultiple="true" maxWidth="100" maxHeight="100"/>
			</div>
		</div>
		<%--<div class="control-group">
			<label class="control-label">基础售价：</label>
			<div class="controls">
				<form:input path="basePrice"  htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>--%>
		</div>
		<div class="control-group">
			<label class="control-label">工厂价格：</label>
			<div class="controls">
				<form:input path="buyPrice"  htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<%--<div class="control-group">--%>
			<%--<label class="control-label">货号：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="itemNo"  htmlEscape="false" maxlength="20" class="input-xlarge"/>--%>

			<%--</div>--%>
		<%--</div>--%>


		<div class="control-group">
			<label class="control-label">选择SKU属性：</label>
			<div class="controls">
				<c:forEach items="${attributeValueList}" var="attrValue">
					${attrValue.attributeInfo.name} : ${attrValue.value}
					<br/>
				</c:forEach>
				<%--<c:forEach items="${map}" var="propertyInfo">--%>
					<%--<c:set value="${ fn:split(propertyInfo.key, ',') }" var="info" />--%>

					<%--<input   class="select_all" id="${info[0]}" type="checkbox" name="prodPropertyInfos" value="${info[0]}"/> ${info[1]}：--%>
					<%--<c:forEach items="${propertyInfo.value}" var="propValue" varStatus="propValueStatus">--%>

						<%--<c:choose>--%>
							<%--<c:when test="${propValue.sysPropValue.id==0}">--%>


								<%--<c:if test="${propertyInfo.value.size()==1}">--%>
									<%--<input checked="checked"  onclick="return false;"  class="value_${info[0]}" id="value_${propValue.id}" type="checkbox" name="prodPropMap[${info[0]}].prodPropertyValues" value="${propValue.id}-${propValue.source}"/> ${propValue.propValue}--%>

								<%--</c:if>--%>

								<%--<c:if test="${propertyInfo.value.size()!=1}">--%>
									<%--<input  onclick="checkOnly(${info[0]})"  class="value_${info[0]}" id="value_${propValue.id}" type="checkbox" name="prodPropMap[${info[0]}].prodPropertyValues" value="${propValue.id}-${propValue.source} "/> ${propValue.propValue}--%>

								<%--</c:if>--%>
							<%--</c:when>--%>
						<%--<c:otherwise>--%>


							<%--<c:if test="${propertyInfo.value.size()==1}">--%>
								<%--<input  checked="checked" onclick="return false;" class="value_${info[0]}" id="value_${propValue.sysPropValue.id}" type="checkbox" name="prodPropMap[${info[0]}].prodPropertyValues" value="${propValue.sysPropValue.id}-sys"/> ${propValue.propValue}--%>

							<%--</c:if>--%>

							<%--<c:if test="${propertyInfo.value.size()!=1}">--%>
								<%--<input onclick="checkOnly(${info[0]})" class="value_${info[0]}" id="value_${propValue.sysPropValue.id}" type="checkbox" name="prodPropMap[${info[0]}].prodPropertyValues" value="${propValue.sysPropValue.id}-sys"/> ${propValue.propValue}--%>

							<%--</c:if>--%>
						<%--</c:otherwise>--%>
						<%--</c:choose>--%>
					<%--</c:forEach>--%>
					<%--<br/>--%>
				<%--</c:forEach>--%>
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
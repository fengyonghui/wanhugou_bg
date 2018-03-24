<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>产品信息表管理</title>
    <meta name="decorator" content="default"/>
    <link rel="stylesheet" href="${ctxStatic}/tree-multiselect/dist/jquery.tree-multiselect.min.css">

    <script type="text/javascript">
        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        })


    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/product/bizProductInfo/">产品信息表列表</a></li>
    <li class="active"><a
            href="${ctx}/biz/product/bizProductInfo/form?id=${bizProductInfo.id}">产品信息表<shiro:hasPermission
            name="product:bizProductInfo:edit">${not empty bizProductInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="biz:product:bizProductInfo:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<%--@elvariable id="bizProductInfo" type="com.wanhutong.backend.modules.biz.entity.product.BizProductInfo"--%>
<form:form id="inputForm" modelAttribute="bizProductInfo" action="${ctx}/biz/product/bizProductInfo/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <input type="hidden" id="brandDefId" value="${DefaultPropEnum.PROPBRAND.getPropValue()}"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">产品名称：</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">请选择品牌:</label>
        <div class="controls">
            <%--<form:select path="propValue.id" class="input-xlarge required" id="propValueId">--%>
                <%--<form:option value="" label="请选择品牌"/>--%>
                <%--<form:options items="${propValueList}" itemLabel="value" itemValue="id" htmlEscape="false"/>--%>
            <%--</form:select>--%>
                <form:select path="brandId" class="js-example-basic-multiple">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('brand')}" itemLabel="label" itemValue="id"
                                  htmlEscape="false"/></form:select>

            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">上市时间：</label>
        <div class="controls">
            <input name="marketingDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                   value="<fmt:formatDate value="${entity.marketingDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品主图:</label>
        <div class="controls">
            <form:hidden id="prodMaxImg" path="photos" htmlEscape="false" maxlength="255" class="input-xlarge"/>
            <sys:ckfinder input="prodMaxImg" type="images" uploadPath="/prod/main" selectMultiple="true" maxWidth="100"
                          maxHeight="100"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品列表图:</label>
        <div class="controls">
            <form:hidden id="prodListImg" path="photoLists" htmlEscape="false" maxlength="255" class="input-xlarge"/>
            <sys:ckfinder input="prodListImg" type="images" uploadPath="/prod/item" selectMultiple="true" maxWidth="100"
                          maxHeight="100"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品货号：</label>
        <div class="controls">
            <form:input path="itemNo" htmlEscape="false" maxlength="10" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品描述：</label>
        <div class="controls">
            <form:textarea path="description" htmlEscape="false" class="input-xlarge "/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品详情图片:</label>
        <div class="controls">
            <form:hidden id="prodDetailImg" path="photoDetails" htmlEscape="false" maxlength="255"
                         class="input-xlarge"/>
            <sys:ckfinder input="prodDetailImg" type="images" uploadPath="/prod/detail" selectMultiple="true"
                          maxWidth="100" maxHeight="100"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">请选择供应商：</label>
        <div class="controls">
            <sys:treeselect id="office" name="office.id" value="${entity.office.id}" labelName="office.id"
                            labelValue="${entity.office.id}" notAllowSelectRoot="true" notAllowSelectParent="true"
                            title="供应商" url="/sys/office/queryTreeList?type=7" extId="${office.id}"
                            cssClass="input-xlarge required"
                            allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">请选择产品分类：</label>
        <div class="controls">
            <form:select path="bizVarietyInfo.id" class="input-medium required">
                <form:option value="" label="请选择"/>
                <form:options items="${varietyInfoList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">请选择产品标签：</label>
        <div class="controls">
            <select id="test-select-2" multiple="multiple" class="input-medium">
                <c:forEach items="${cateList}" var="cate">
                    <option data-section="${cate.parentNames}" value="${cate.id}">${cate.id}-${cate.name}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">产品属性：</label>
        <div id="cateProp" class="controls">
            <c:forEach items="${prodTagList}" var="tagInfo">
                <div  style="width: 100%;display: inline-block">
                    <span  style="float:left;width:60px;padding-top:3px">${tagInfo.name}：</span>
                    <c:choose>
                        <c:when test="${tagInfo.dictList!=null}">
                            <form:select path="textureStr" class="input-medium required">
                                <form:option value="" label="请选择"/>
                                <form:options items="${tagInfo.dictList}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                            </form:select>
                        </c:when>
                        <c:otherwise>
                            <input type="text" class="input-medium"/>
                        </c:otherwise>
                    </c:choose>

                </div>
            </c:forEach>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">商品属性：</label>
        <div class="controls">
            <c:forEach items="${skuTagList}" var="tagInfo">
                <div  style="width: 100%;display: inline-block">
                    <span  style="float:left;width:60px;padding-top:3px">${tagInfo.name}：</span>
                    <c:choose>
                        <c:when test="${tagInfo.dictList!=null}">
                            <div style="float: left">

                                <select  title="search"  id="search_${tagInfo.id}" class="input-xlarge" multiple="multiple" size="8">
                                    <c:forEach items="${tagInfo.dictList}" var="dict">
                                        <%--<input class="value_${propertyInfo.id}" id="value_${propValue.id}" type="checkbox" name="propertyMap[${propertyInfo.id}].catePropertyValues" value="${propValue.id}"/> ${propValue.value}--%>
                                        <option value="${dict.value}">${dict.label}</option>
                                    </c:forEach>


                                </select>

                            </div>
                            <div  style="width: 20%;margin-left:10px;float: left">
                                <button type="button" id="search_${tagInfo.id}_rightAll" class="btn-block"><i class="icon-forward"></i></button>

                                <button type="button" id="search_${tagInfo.id}_rightSelected" class="btn-block"><i class="icon-chevron-right"></i></button>

                                <button type="button" id="search_${tagInfo.id}_leftSelected" class="btn-block"><i class="icon-chevron-left"></i></button>

                                <button type="button" id="search_${tagInfo.id}_leftAll" class="btn-block"><i class="icon-backward"></i></button>
                            </div>

                            <div style="margin-left:10px;float: left">

                                <select name="aaa"  id="search_${tagInfo.id}_to" class="input-xlarge" size="8" multiple="multiple"></select>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <input style="margin-bottom: 5px" type="text" class="input-medium" onchange="skuAttrChange(this)" customType="skuAttr"/>
                        </c:otherwise>
                    </c:choose>

                </div>
            </c:forEach>

        </div>
    </div>
    <div class="control-group">
        <label class="control-label">销售规格：</label>
        <div class="controls">
            <input class="btn" type="button" value="预 览" onclick="initSkuTable()"/>
            批量设置价格:
            <input type="text" value="" id="batchPrice"/>
            <input onclick="setBatchPrice()" class="btn" type="button" value="确 定"/>
        </div>
        <br/>
        <div class="controls">
           <table class="table table-striped table-bordered table-condensed" id="skuTable">
               <thead>
               <tr>
                    <th>尺寸</th>
                    <th>颜色</th>
                    <th>价格</th>
                    <th>类型</th>
                    <th>操作</th>
               </tr>
               </thead>
               <tbody id="skuTableData">
               <tr>
               </tr>
               </tbody>
           </table>

        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="biz:product:bizProductInfo:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                           type="button"
                                                                           value="保 存" onclick="submitCustomForm()"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>
<script src="${ctxStatic}/bootstrap/multiselect.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/tree-multiselect/dist/jquery.tree-multiselect.js"></script>
<script src="${ctxStatic}/jquery-select2/3.5.3/select2.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>

<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>

<script type="text/javascript">
    function initSkuTable() {
        var skuTableData = $("#skuTableData");
        skuTableData.empty();

        var typeSelector = "<th><select customInput=\"skuTypeSelect\">";
        var typeSelectorItem = "<option value='$value' label='$label'>$text</option>";
        typeSelector += typeSelectorItem.replace("$value", "1").replace("$label", "自选商品").replace("$text", "自选商品");
        typeSelector += typeSelectorItem.replace("$value", "2").replace("$label", "定制商品").replace("$text", "定制商品");
        typeSelector += typeSelectorItem.replace("$value", "3").replace("$label", "非自选商品").replace("$text", "非自选商品");
        typeSelector += "</select></th>";

        // OWN_PRODUCT((byte)1,"自选商品"),
        // MADE_PRODUCT((byte)2,"定制商品"),
        // COMMON_PRODUCT((byte)3,"非自选商品");

        var tableHtml = "<tr customType=\"skuTr\">" +
            "                   <td><input type=\"text\" value=\"$size\" customInput=\"sizeInput\" readonly/></td>" +
            "                   <td><input type=\"text\" value=\"$color\" customInput=\"colorInput\" readonly/></td>" +
            "                   <td><input type=\"text\" value=\"$price\" customInput=\"priceInput\"/></td>" +
            typeSelector +
            "                   <td onclick='deleteParentEle(this)'><input class=\"btn\" type=\"button\" value=\"删除\"/></td>" +
            "               </tr>";

        var search_3_to = $("#search_3_to");
        var selectedColorArr = [];
        search_3_to.find("option").each(function(){
            selectedColorArr.push($(this).text());
        });

        var customTypeAttr = $("[customType]");
        var selectedSizeArr = [];

        customTypeAttr.each(function () {
            if ($(this).val() != null && $(this).val() != "") {
                selectedSizeArr.push($(this).val());
            }
        });

        for (var i = 0; i < selectedSizeArr.length; i ++) {
            for (var j = 0; j < selectedColorArr.length; j ++) {
                skuTableData.append(tableHtml.replace("$size", selectedSizeArr[i]).replace("$color", selectedColorArr[j]).replace("$price", ""));
            }
        }

    }
    
    function submitCustomForm() {
        var skuTrArr = $("[customType='skuTr']");
        var inputForm = $("#inputForm");
        var skuFormHtml = "<input name='skuAttrStrList' type='hidden' value='$value'/>";
        skuTrArr.each(function () {
            var sizeInput = $($(this).find("[customInput = 'sizeInput']")[0]).attr("value");
            var colorInput = $($(this).find("[customInput = 'colorInput']")[0]).attr("value");
            var priceInput = $($(this).find("[customInput = 'priceInput']")[0]).attr("value");
            var skuTypeSelect = $($(this).find("[customInput = 'skuTypeSelect']")[0]).find("option:selected").attr("value");
            inputForm.append(skuFormHtml.replace("$value", sizeInput + "|" + colorInput + "|" + priceInput + "|" + skuTypeSelect));
        });

        var tagFormHtml = "<input name='tagStr' type='hidden' value='$value'/>";
        var testSelect2 = $("#test-select-2");
        var tagSelected = testSelect2.parent().children(".tree-multiselect").children(".selected").children("div");
        tagSelected.each(function () {
            inputForm.append(tagFormHtml.replace("$value", $(this).attr("data-value")));
        });

        inputForm.submit();
    }
    
    function deleteParentEle(that) {
        $(that).parent().remove();
    }

    function setBatchPrice() {
        var priceInput = $("[customInput = 'priceInput']");
        priceInput.val($("#batchPrice").val());
        priceInput.attr("value", $("#batchPrice").val());
    }

    function skuAttrChange(that) {
        var skuAttrHtmlText = " <input style=\"margin-bottom: 5px\" type=\"text\" class=\"input-medium\" onchange=\"skuAttrChange(this)\" customType=\"skuAttr\"/>";
        var parent = $(that).parent();
        var childArr = parent.children("input");
        childArr.each(function () {
            if ($(this).val() == null || $(this).val() == "") {
                $(this).remove();
            }
        });
        parent.append(skuAttrHtmlText);
    }

    $(document).ready(function() {

        <%--window.prettyPrint && prettyPrint();--%>

        $('select[title="search"]').multiselect({
            search: {
                left: '<input type="text" name="q" style="display: block;width: 95%"  placeholder="Search..." />',
                right: '<input type="text" name="q" style="display: block;width: 95%" class="input-large" placeholder="Search..." />',
            }
            ,
            fireSearch: function(value) {
                return value.length >=1 ;
            }
        });
        var tree2 = $("#test-select-2").treeMultiselect({
            searchable: true
        });

        initSkuTable();

    });

</script>

</body>
</html>
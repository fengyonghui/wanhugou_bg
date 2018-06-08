<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>产品信息表管理</title>
    <meta name="decorator" content="default"/>
    <link rel="stylesheet" href="${ctxStatic}/tree-multiselect/dist/jquery.tree-multiselect.min.css">
    <link rel="stylesheet" href="${ctxStatic}/jquery-plugin/jquery.searchableSelect.css">

    <script type="text/javascript">
        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({

                submitHandler: function (form) {
                    var brandId =$("#brandId").val();
                    if(brandId==''){
                        alert("请选择品牌");
                        return;
                    }
                   var  varietyInfoId=$("#varietyInfoId").val();
                    if(varietyInfoId==''){
                        alert("请选择分类");
                        return;
                    }
                    var aa = true;
                    $("input[name='imgDetailSorts']").each(function () {
                        if ($(this).val()=='') {
                            aa = false;
                            return;
                        }
                    });
                    var bb = true;
                    $("input[name='imgPhotosSorts']").each(function () {
                        if ($(this).val()=='') {
                            bb = false;
                            return;
                        }
                    });
                    if (aa && bb) {
                        loading('正在提交，请稍等...');
                        form.submit();
                    } else {
                        alert("主图和列表图的序号不能为空");
                    }
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
    <li><a href="${ctx}/biz/product/bizProductInfoV3?prodType=${entity.prodType}">产品信息表列表</a></li>
    <li class="active"><a
            href="${ctx}/biz/product/bizProductInfoV3/form?id=${bizProductInfo.id}&prodType=${entity.prodType}">产品信息表<shiro:hasPermission
            name="product:bizProductInfo:edit">${not empty bizProductInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="biz:product:bizProductInfo:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<%--@elvariable id="bizProductInfo" type="com.wanhutong.backend.modules.biz.entity.product.BizProductInfo"--%>
<form:form id="inputForm" modelAttribute="bizProductInfo" action="${ctx}/biz/product/bizProductInfoV3/saveCopy" method="post"
           class="form-horizontal">
    <form:hidden path="id" id="id"/>
    <form:hidden path="prodType"/>
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
        <div style="margin-left: 180px">
            <form:select  about="choose" path="brandId" class="input-xlarge required">
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
        <label class="control-label">产品主图:
            <p style="opacity: 0.5;color: red;">*首图为列表页图</p>
            <p style="opacity: 0.5;">图片建议比例为1:1</p>
            <p style="opacity: 0.5;">点击图片删除</p>
            <p style="opacity: 0.5;color: red;">数字小的会排在前边，请不要输入重复序号</p>
        </label>
        <div class="controls">
            <input class="btn" type="file" name="productImg" onchange="submitPic('prodMainImg', true)" value="上传图片" multiple="multiple" id="prodMainImg"/>
        </div>
        <div id="prodMainImgDiv">
            <table>
                <tr id="prodMainImgImg">
                        <%--<c:if test="${entity.photos != null && entity.photos != ''}">--%>
                        <%--<c:forEach items='${fn:split(entity.photos,"|")}' var="v" varStatus="status">--%>
                    <c:forEach items="${photosMap}" var="photo" varStatus="status">
                        <td><img src="${photo.key}" customInput="prodMainImgImg" style='width: 100px' onclick="removeThis(this,'#mainImg'+${status.index});"></td>
                    </c:forEach>
                </tr>
                <tr id="imgPhotosSorts">
                    <c:forEach items="${photosMap}" var="photo" varStatus="status">
                        <td><input id="mainImg${status.index}" name="imgPhotosSorts" type="number" style="width: 100px" value="${photo.value}"/></td>
                    </c:forEach>
                </tr>
            </table>
            <%--<c:if test="${entity.photos != null && entity.photos != ''}">--%>
                <%--<c:forEach items='${fn:split(entity.photos,"|")}' var="v" varStatus="status">--%>
                    <%--<img src="${v}" customInput="prodMainImgImg" style='width: 100px' onclick="$(this).remove();">--%>
                <%--</c:forEach>--%>
            <%--</c:if>--%>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">banner大图:
            <p style="opacity: 0.5;">图片建议比例为16:9</p>
            <p style="opacity: 0.5;">非banner展示产品可不传</p>
            <p style="opacity: 0.5;">点击图片删除</p>
        </label>
        <div class="controls">
            <input class="btn" type="file" name="productImg" onchange="submitPic('prodBannerImg', false)" value="上传图片" id="prodBannerImg"/>
        </div>
        <div id="prodBannerImgDiv">
            <c:if test="${entity.imgUrl != null && entity.imgUrl != ''}">
                <img src="${entity.imgUrl}" customInput="prodBannerImgImg" style='width: 100px' onclick="$(this).remove();">
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品货号：</label>
        <div class="controls">
            <form:input id="itemNo" path="itemNo" htmlEscape="false" maxlength="30" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品描述：</label>
        <div class="controls">
            <form:textarea path="description" htmlEscape="false" class="input-xlarge "/>
            <p style="color: red">是否允许退换货，在此说明</p>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品详情图:
            <p style="opacity: 0.5;">点击图片删除</p>
            <p style="opacity: 0;color: red;">*首图为列表页图</p>
            <p style="opacity: 0;">图片建议比例为1:1</p>
            <p style="opacity: 0.5;color: red;">数字小的会排在前边，请不要输入重复序号</p>
        </label>
        <div class="controls">
            <input class="btn" type="file" name="productImg" onchange="submitPic('prodDetailImg', true)" value="上传图片" multiple="multiple" id="prodDetailImg"/>
        </div>
        <div id="prodDetailImgDiv">
            <table>
                <tr id="prodDetailImgImg">
                    <c:forEach items="${detailsMap}" var="detail" varStatus="status">
                        <td><img src="${detail.key}" customInput="prodDetailImgImg" style='width: 100px' onclick="removeThis(this,'#detailImg'+${status.index});"></td>
                    </c:forEach>
                </tr>
                <tr id="imgDetailSorts">
                    <c:forEach items="${detailsMap}" var="detail" varStatus="status">
                        <td><input id="detailImg${status.index}" name="imgDetailSorts" type="number" style="width: 100px" value="${detail.value}"/></td>
                    </c:forEach>
                </tr>
            </table>
            <%--<c:if test="${entity.photoDetails != null && entity.photoDetails != ''}">--%>
                <%--<c:forEach items='${fn:split(entity.photoDetails,"|")}' var="v">--%>
                    <%--<img src="${v}" customInput="prodDetailImgImg" style='width: 100px' onclick="$(this).remove();">--%>
                <%--</c:forEach>--%>
            <%--</c:if>--%>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">请选择供应商：</label>
        <div class="controls">
            <sys:treeselect id="office" name="office.id" value="${entity.office.id}" labelName="office.name"
                            labelValue="${entity.office.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
                            title="供应商" url="/sys/office/queryTreeList?type=7" extId="${office.id}"
                            cssClass="input-xlarge required"
                            allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div id="variety" class="control-group">
        <label class="control-label">请选择产品分类：</label>
        <div style="margin-left: 180px">
            <form:select id="varietyInfoId" about="" onclick="selectAttr(this)" path="bizVarietyInfo.id" class="input-medium required">
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
                    <option data-section="${cate.parentNames}" value="${cate.id}">${cate.name}</option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">产品属性：</label>
        <div id="cateProp" style="margin-left: 180px">
            <c:forEach items="${prodTagList}" var="tagInfo">
                <div  style="width: 100%;display: inline-block">
                    <span  style="float:left;width:60px;padding-top:3px">${tagInfo.name}：</span>
                    <c:choose>
                        <c:when test="${tagInfo.dictList!=null}">
                            <form:select about="choose" path="textureStr" class="input-medium required">
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
        <label class="control-label">商品属性：
            <p style="opacity: 0.8;color: red;">*属性中禁止使用斜线: "/" </p>
            <p style="opacity: 0.8;color: red;">*建议使用: "-" </p>
        </label>
        <div class="controls">
            <c:forEach items="${skuTagList}" var="tagInfo">
                <div  style="width: 100%;display: inline-block">
                    <span  style="float:left;width:60px;padding-top:3px">${tagInfo.name}：</span>
                    <c:choose>
                        <c:when test="${tagInfo.dictList!=null}">
                            <div style="float: left">

                                <select  title="search"  id="search_${tagInfo.id}" class="input-xlarge" multiple="multiple" size="8">
                                    <c:forEach items="${tagInfo.dictList}" var="dict">
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
                                <select name="aaa"  id="search_${tagInfo.id}_to" class="input-xlarge" size="8" multiple="multiple">
                                    <c:forEach items="${skuAttrMap[tagInfo.id]}" var="v">
                                        <option value="${v}">${v}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${skuAttrMap[tagInfo.id]}" var="v">
                                <input style="margin-bottom: 5px" type="text" class="input-medium" onchange="skuAttrChange(this)" customType="skuAttr" value="${v}"/>
                            </c:forEach>
                            <input style="margin-bottom: 5px" type="text" class="input-medium" onchange="skuAttrChange(this)" customType="skuAttr"/>
                        </c:otherwise>
                    </c:choose>

                </div>
            </c:forEach>

        </div>
    </div>
    <div class="control-group">
        <label class="control-label">上传颜色图片：</label>
        <div class="controls">
            <input class="btn" type="button" value="上传图片" onclick="uploadPic() "/>
        </div>
        <br/>
        <div class="controls">
            <table class="table  table-bordered table-condensed" id="uploadPicTable">
                <thead>
                <tr>
                    <th>颜色</th>
                    <th>图片</th>
                    <th>图片操作</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody id="uploadPicTableData"></tbody>
            </table>

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
        <div class="controls" style="overflow-x: auto; overflow-y: auto; height: 400px;">
            <table class="table  table-bordered table-condensed" id="skuTable">
                <thead>
                <tr>
                    <th style="display: none">id</th>
                    <th>货号</th>
                    <th>尺寸</th>
                    <th>颜色</th>
                    <th>价格</th>
                    <th>图片</th>
                    <th style="display: none">图片地址</th>
                    <th>类型</th>
                    <th>操作</th>
                    <%--<th>同尺寸价格</th>--%>
                </tr>
                </thead>
                <tbody id="skuTableData">
                <c:forEach items="${entity.skuInfosList}" var="v">
                    <tr customType="skuTr">
                        <td style="display: none"><input type="text" value="${v.id}" customInput="idInput" readonly/></td>
                        <td><input type="text" value="${v.itemNo}" customInput="itemNoInput" readonly/></td>
                        <td><input type="text" style="width: 70px" value="${v.attrValueMap['2'][0].value}" customInput="sizeInput" readonly/></td>
                        <td><input type="text" style="width: 120px" value="${v.attrValueMap['3'][0].value}" customInput="colorInput" readonly/></td>
                        <td><input type="text" style="width: 70px" value="${v.buyPrice}" customInput="priceInput"/></td>
                        <td><img customInput="imgInputLab" style="width: 160px" src="${v.defaultImg}"></td>
                        <td style="display: none"><input type="text" value="${v.defaultImg}" customInput="imgInput" readonly/></td>
                        <th><select style="width: 120px" customInput="skuTypeSelect">
                            <c:if test="${v.skuType == 1}">
                                <option value='1' label='自选商品'>自选商品</option>
                                <option value='2' label='定制商品'>定制商品</option>
                                <option value='3' label='非自选商品'>非自选商品</option>
                            </c:if>
                            <c:if test="${v.skuType == 2}">
                                <option value='2' label='定制商品'>定制商品</option>
                                <option value='1' label='自选商品'>自选商品</option>
                                <option value='3' label='非自选商品'>非自选商品</option>
                            </c:if>
                            <c:if test="${v.skuType == 3}">
                                <option value='3' label='非自选商品'>非自选商品</option>
                                <option value='1' label='自选商品'>自选商品</option>
                                <option value='2' label='定制商品'>定制商品</option>
                            </c:if>
                        </select></th>
                        <td>
                            <input onclick='deleteImgEle(this)' class="btn" type="button" value="删除图片"/>
                            <input onclick='deleteParentParentEle(this)' class="btn" type="button" value="删除"/>
                        </td>
                        <%--<td><input onclick='setUp(this)' class="btn" type="button" value="设置"/></td>--%>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

        </div>
    </div>
    <form:input path="photos" id="photos" cssStyle="display: none"/>
    <form:input path="photoDetails" id="photoDetails" cssStyle="display: none"/>
    <form:input path="imgUrl" id="imgUrl" cssStyle="display: none"/>
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
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery-plugin/jquery.searchableSelect.js" type="text/javascript"></script>
<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/common/base.js" type="text/javascript"></script>

<script type="text/javascript">
    function initSkuTable() {
        var skuTableData = $("#skuTableData");
        var skuTableDataTr = skuTableData.find("tr");

        var colorTrArr = $("[customType='colorTr']");
        if (colorTrArr.length <= 0) {
            uploadPic();
            initSkuTable();
            return;
        }

        var oldDataMap= $Map.Map();
        if (skuTableDataTr.length > 0) {
            skuTableDataTr.each(function () {
                var oldIdInput = $($(this).find("[customInput = 'idInput']")[0]).val();
                var oldSizeInput = $($(this).find("[customInput = 'sizeInput']")[0]).val();
                var oldColorInput = $($(this).find("[customInput = 'colorInput']")[0]).val();
                var oldPriceInput = $($(this).find("[customInput = 'priceInput']")[0]).val();
                var oldImgInput = $($(this).find("[customInput = 'imgInput']")[0]).val();
                var oldSkuTypeSelect = $($(this).find("[customInput = 'skuTypeSelect']")[0]).find("option:selected").val();
                var oldData = oldSizeInput + "|" + oldColorInput + "|" + oldPriceInput + "|" + oldImgInput + "|" + oldSkuTypeSelect + "|" + oldIdInput;
                oldDataMap.put(oldSizeInput + oldColorInput, oldData);
            });
        }
        skuTableData.empty();

        var tableHtml = "<tr customType=\"skuTr\">" +
            "                   <td style=\"display: none\"><input type=\"text\" value=\"$id\" customInput=\"idInput\" readonly/></td>" +
            "                   <td><input type=\"text\" value=\"$imteNo\" customInput=\"itemNoInput\" readonly/></td>" +
            "                   <td><input type=\"text\" style=\"width: 70px\" value=\"$size\" customInput=\"sizeInput\" readonly/></td>" +
            "                   <td><input type=\"text\" style=\"width: 120px\" value=\"$color\" customInput=\"colorInput\" readonly/></td>" +
            "                   <td><input type=\"text\" style=\"width: 70px\" value=\"$price\" customInput=\"priceInput\"/></td>" +
            "                   <td><img customInput=\"imgInputLab\" src=\"$img\" style=\"width: 100px\"></td>" +
            "                   <td style=\"display: none\"><input type=\"text\" value=\"$img\" customInput=\"imgInput\" readonly/></td>" +
            "$typeSelector" +
            "                   <td><input onclick='deleteImgEle(this)' class=\"btn\" type=\"button\" value=\"删除图片\"/>" +
            "                   <input onclick='deleteParentParentEle(this)' class=\"btn\" type=\"button\" value=\"删除\"/></td>" +
            // "                   <td><input onclick='setUp(this)' class=\"btn\" type=\"button\" value=\"设置\"/></td>" +
            "               </tr>";

        var customTypeAttr = $("[customType]");
        var selectedSizeArr = [];

        customTypeAttr.each(function () {
            if ($(this).val() != null && $(this).val() != "") {
                selectedSizeArr.push($(this).val());
            }
        });

        for (var i = 0; i < selectedSizeArr.length; i ++) {
            colorTrArr.each(function () {
                var sizeInput = selectedSizeArr[i];
                var idInput = $($(this).find("[customInput = 'idInput']")[0]).attr("value");
                var colorInput = $($(this).find("[customInput = 'colorInput']")[0]).attr("value");
                var imgInput = $($(this).find("[customInput = 'imgInput']")[0]).attr("src");
                var priceInput = "";

                var oldDataStr = oldDataMap.get(selectedSizeArr[i] + colorInput);

                var custTypeSelector = "<th><select style=\"width: 120px\" customInput=\"skuTypeSelect\">";
                var custTypeSelectorItem = "<option value='$value' label='$label'>$text</option>";


                if (!$String.isNullOrBlank(oldDataStr)) {
                    var dataArr = oldDataStr.split("|");

                    sizeInput = $String.isNullOrBlank(sizeInput) ?  dataArr[0] : sizeInput;
                    colorInput = $String.isNullOrBlank(colorInput) ?  dataArr[1] : colorInput;
                    priceInput = $String.isNullOrBlank(priceInput) ?  dataArr[2] : priceInput;
                    imgInput = $String.isNullOrBlank(imgInput) ?  dataArr[3] : imgInput;
                    idInput = $String.isNullOrBlank(idInput) ?  dataArr[5] : idInput;
                    var oldSkuTypeSelect = dataArr[4];


                    if (oldSkuTypeSelect == "2") {
                        custTypeSelector += custTypeSelectorItem.replace("$value", "2").replace("$label", "定制商品").replace("$text", "定制商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "1").replace("$label", "自选商品").replace("$text", "自选商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "3").replace("$label", "非自选商品").replace("$text", "非自选商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "4").replace("$label", "代采商品").replace("$text", "代采商品");
                    }else if (oldSkuTypeSelect == "3") {
                        custTypeSelector += custTypeSelectorItem.replace("$value", "3").replace("$label", "非自选商品").replace("$text", "非自选商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "1").replace("$label", "自选商品").replace("$text", "自选商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "2").replace("$label", "定制商品").replace("$text", "定制商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "4").replace("$label", "代采商品").replace("$text", "代采商品");
                    }else if (oldSkuTypeSelect == "4") {
                        custTypeSelector += custTypeSelectorItem.replace("$value", "4").replace("$label", "代采商品").replace("$text", "代采商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "1").replace("$label", "自选商品").replace("$text", "自选商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "2").replace("$label", "定制商品").replace("$text", "定制商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "3").replace("$label", "非自选商品").replace("$text", "非自选商品");
                    }else {
                        custTypeSelector += custTypeSelectorItem.replace("$value", "1").replace("$label", "自选商品").replace("$text", "自选商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "2").replace("$label", "定制商品").replace("$text", "定制商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "3").replace("$label", "非自选商品").replace("$text", "非自选商品");
                        custTypeSelector += custTypeSelectorItem.replace("$value", "4").replace("$label", "代采商品").replace("$text", "代采商品");
                    }

                }else {
                    custTypeSelector += custTypeSelectorItem.replace("$value", "1").replace("$label", "自选商品").replace("$text", "自选商品");
                    custTypeSelector += custTypeSelectorItem.replace("$value", "2").replace("$label", "定制商品").replace("$text", "定制商品");
                    custTypeSelector += custTypeSelectorItem.replace("$value", "3").replace("$label", "非自选商品").replace("$text", "非自选商品");
                    custTypeSelector += custTypeSelectorItem.replace("$value", "4").replace("$label", "代采商品").replace("$text", "代采商品");
                }
                custTypeSelector += "</select></th>";

                if (!imgInput) {
                    imgInput = "";
                }
                var itemNo = $("#itemNo").val();
                skuTableData.append(
                    tableHtml.replace("$imteNo", itemNo + "/" + sizeInput + "/" + colorInput)
                        .replace("$id", idInput)
                        .replace("$size", sizeInput)
                        .replace("$color", colorInput)
                        .replace("$price", priceInput)
                        .replace("$img", imgInput)
                        .replace("$img", imgInput)
                        .replace("$typeSelector", custTypeSelector)
                );
            });
        }
    }

    function deleteImgEle(that) {
        var p = $(that).parent().parent();
        var imgInput = $($(p).find("[customInput = 'imgInput']"));
        var imgInputLab = $($(p).find("[customInput = 'imgInputLab']"));

        imgInput.val("");
        imgInput.attr("value", "");
        imgInputLab.attr("src", "");
    }

    function submitCustomForm() {
        var itemNo = $("#itemNo").val();
        // var id = $("#id").val();
        var id = '${idVal}';
        var officeName = $("#officeName").val();
        $.ajax({
            url : '${ctx}/biz/product/bizProductInfoV2/getItemNoExist',
            contentType:'application/json',
            data : {"itemNo" : itemNo, "id" : id, "officeName" : officeName},
            type : 'get',
            success : function(result){
                if (result == "true") {
                    alert("货号重复,请重新输入");
                    return;
                }

                var skuTrArr = $("[customType='skuTr']");
                var inputForm = $("#inputForm");

                var skuFormHtml = "<input name='skuAttrStrList' type='hidden' value='$value'/>";
                skuTrArr.each(function () {
                    var idInput = $($(this).find("[customInput = 'idInput']")[0]).val();
                    var sizeInput = $($(this).find("[customInput = 'sizeInput']")[0]).val();
                    var colorInput = $($(this).find("[customInput = 'colorInput']")[0]).val();
                    var priceInput = $($(this).find("[customInput = 'priceInput']")[0]).val();
                    var imgInput = $($(this).find("[customInput = 'imgInput']")[0]).attr("value");
                    var skuTypeSelect = $($(this).find("[customInput = 'skuTypeSelect']")[0]).find("option:selected").attr("value");

                    if (idInput == null || idInput == '') {
                        idInput = 0;
                    }
                    inputForm.append(skuFormHtml.replace("$value", sizeInput + "|" + colorInput + "|" + priceInput + "|" + skuTypeSelect + "|"+ idInput + "|" + imgInput));
                });

                var mainImg = $("#prodMainImgDiv").find("[customInput = 'prodMainImgImg']");
                var mainImgStr = "";
                for (var i = 0; i < mainImg.length; i ++) {
                    mainImgStr += ($(mainImg[i]).attr("src") + "|");
                }
                $("#photos").val(mainImgStr);

                var bannerImg = $("#prodBannerImgDiv").find("[customInput = 'prodBannerImgImg']");
                var bannerImgStr = "";
                for (var i = 0; i < bannerImg.length; i ++) {
                    bannerImgStr += ($(bannerImg[i]).attr("src"));
                }
                $("#imgUrl").val(bannerImgStr);

                var detailImg = $("#prodDetailImgDiv").find("[customInput = 'prodDetailImgImg']");
                var detailImgStr = "";
                for (var i = 0; i < detailImg.length; i ++) {
                    detailImgStr += ($(detailImg[i]).attr("src") + "|");
                }
                $("#photoDetails").val(detailImgStr);

                var tagFormHtml = "<input name='tagStr' type='hidden' value='$value'/>";
                var testSelect2 = $("#test-select-2");
                var tagSelected = testSelect2.parent().children(".tree-multiselect").children(".selected").children("div");
                tagSelected.each(function () {
                    inputForm.append(tagFormHtml.replace("$value", $(this).attr("data-value")));
                });

                inputForm.submit();

            },
            error : function(error){
                error(error);
            }
        });
    }

    function deleteParentParentEle(that) {
        $(that).parent().parent().remove();
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

    function submitPic(id, multiple){
        var f = $("#" + id).val();
        if(f==null||f==""){
            alert("错误提示:上传文件不能为空,请重新选择文件");
            return false;
        }else{
            var extname = f.substring(f.lastIndexOf(".")+1,f.length);
            extname = extname.toLowerCase();//处理了大小写
            if(extname!= "jpeg"&&extname!= "jpg"&&extname!= "gif"&&extname!= "png"){
                $("#picTip").html("<span style='color:Red'>错误提示:格式不正确,支持的图片格式为：JPEG、GIF、PNG！</span>");
                return false;
            }
        }
        var file = document.getElementById(id).files;
        var size = file[0].size;
        if(size>2097152){
            alert("错误提示:所选择的图片太大，图片大小最多支持2M!");
            return false;
        }
        ajaxFileUploadPic(id, multiple);
    }

    var a = 0;
    var b = 0;
    function ajaxFileUploadPic(id, multiple) {
        $.ajaxFileUpload({
            url : '${ctx}/biz/product/bizProductInfoV2/saveColorImg', //用于文件上传的服务器端请求地址
            secureuri : false, //一般设置为false
            fileElementId : id, //文件上传空间的id属性  <input type="file" id="file" name="file" />
            type : 'POST',
            dataType : 'text', //返回值类型 一般设置为json
            success : function(data, status) {
                //服务器成功响应处理函数
                var msg = data.substring(data.indexOf("{"), data.indexOf("}")+1);
                var msgJSON = JSON.parse(msg);
                var imgList = msgJSON.imgList;
                var imgDiv = $("#" + id + "Div");
                var imgDivHtml = "<img src=\"$Src\" customInput=\""+ id +"Img\" style='width: 100px' onclick=\"$(this).remove();\">";
                if (imgList && imgList.length > 0 && multiple) {
                    for (var i = 0; i < imgList.length; i ++) {
                        // imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                        if (id == "prodMainImg") {
                            $("#imgPhotosSorts").append("<td><input id='"+"main" + i + "' name='imgPhotosSorts' value='"+a+"' style='width: 70px' type='number'/></td>");
                            // $("#prodMainImgImg").append(imgDivHtml.replace("$Src", imgList[i]));
                            $("#prodMainImgImg").append("<td><img src=\"" + imgList[i] + "\" customInput=\"" + id + "Img\" style='width: 100px' onclick=\"removeThis(this,"+"$('#main" + i + "'));\"></td>");
                            a += 1;
                        }
                        if (id == "prodDetailImg") {
                            $("#imgDetailSorts").append("<td><input id='"+"detail" + i + "' name='imgDetailSorts' value='"+b+"' style='width: 70px' type='number'/></td>");
                            $("#prodDetailImgImg").append("<td><img src=\"" + imgList[i] + "\" customInput=\"" + id + "Img\" style='width: 100px' onclick=\"removeThis(this,"+"$('#detail" + i + "'));\"></td>");
                            b += 1;
                        }
                    }
                }else if (imgList && imgList.length > 0 && !multiple) {
                    imgDiv.empty();
                    for (var i = 0; i < imgList.length; i ++) {
                        imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                    }
                }else {
                    var img = $("#" + id + "Img");
                    img.attr("src", msgJSON.fullName);
                }
            },
            error : function(data, status, e) {
                //服务器响应失败处理函数
                console.info(data);
                console.info(status);
                console.info(e);
                alert("上传失败");
            }
        });
        return false;
    }

    function deletePic(id) {
        var f = $("#" + id);
        f.attr("src", "");
    }

    function uploadPic() {
        var selectedColorArr = [];
        var search_3_to = $("#search_3_to");
        search_3_to.find("option").each(function(){
            selectedColorArr.push($(this).text());
        });

        var uploadPicTableData = $("#uploadPicTableData");
        uploadPicTableData.empty();
        var tableHtml = "<tr customType=\"colorTr\">" +
            "                   <td><input type=\"text\" value=\"$color\" customInput=\"colorInput\" readonly/></td>" +
            "                   <td><img id=\"colorImg$idImg\" customInput=\"imgInput\" style='width: 150px'/></td>" +
            "                   <td>" +
            "                       <input type=\"file\" name=\"colorImg\" id=\"colorImg$id\" value=\"上传\"/>" +
            "                       <input type=\"button\" value=\"上传\" onclick=\"submitPic('colorImg$id', true)\"/>" +
            "                       <input type=\"button\" value=\"删除\"  onclick=\"deletePic('colorImg$idImg')\"/>" +
            "                   </td>" +
            "                   <td><input onclick='deleteParentParentEle(this)' class=\"btn\" type=\"button\" value=\"删除\"/></td>" +
            "               </tr>";
        for (var j = 0; j < selectedColorArr.length; j ++) {
            uploadPicTableData.append(
                tableHtml.replace("$color", selectedColorArr[j])
                    .replace("$id", j + "")
                    .replace("$id", j + "")
                    .replace("$id", j + "")
                    .replace("$id", j + ""));
        }
    }

    function removeThis(obj,item) {
        $(obj).remove();
        $(item).remove();
    }

    function setUp(item) {
        var size = $(item).parent().parent().find("td input[customInput='sizeInput']").val();
        var price = $(item).parent().parent().find("td input[customInput='priceInput']").val();
        $("input[customInput='sizeInput']").each(function () {
            if ($(this).parent().parent().find("td input[customInput='sizeInput']").val()==size) {
                $(this).parent().parent().find("input[customInput='priceInput']").val(price);
            }
        });
    }

    $(document).ready(function() {

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



        var testSelect2 = $("#test-select-2");
        var treeMultiselect = testSelect2.parent().find(".tree-multiselect")[0];
        var selections = $($(treeMultiselect).find(".selections")[0]).children();

        var cateIdListArr = ${prodCategoryIdList};
        $(selections).each(function () {
            if ($(this).hasClass("item") && cateIdListArr.indexOf(Number($(this).attr("data-value"))) >= 0) {
                var that = $(this);
                $($(that).find(".option")).click();
            }
            if ($(this).hasClass("section")) {
                var itemArr = $(this).find(".item");
                itemArr.each(function () {
                    var that = $(this);
                    if (cateIdListArr.indexOf(Number($(that).attr("data-value"))) >= 0) {
                        $($(that).find(".option")).click();
                    }
                })
            }
        });
        $("#varietyInfoId").searchableSelect({
            afterSelectItem: function() {
                // alert(this.holder.text());
                // alert(this.holder.data("value"));
                var variety = this.holder.data("value");
                // var prodId = $("#id").val();
                // var variety = $(item).val();
                var prodId = '${idVal}';
                if (variety !='') {
                    // if (prodId == null) {
                    //     $("div[name='varietyAttr']").remove();
                    // }
                    // alert(variety);
                    $.ajax({
                        type:"post",
                        url:"${ctx}/biz/product/bizVarietyAttr/findAttr",
                        data:{varietyId:variety,prodId:prodId},
                        success:function (data) {
                            $("div[name='varietyAttr']").remove();
                            if (data.length==0) {
                                return;
                            }
                            var html = "";
                            $.each(data,function (index, varietyAttr) {
                                // alert(index+"--"+varietyAttr);
                                if (varietyAttr.dictList != undefined) {
                                    html += "<div name='varietyAttr' class='control-group'>";
                                    html += "        <label class='control-label'>请选择" + varietyAttr.attributeInfo.name + "：</label>";
                                    html += "        <div style='margin-left: 180px'>";
                                    html += "            <select about='choose' name='dicts' class='input-medium required'>";
                                    html += "                    <option value=''>请选择</option>";
                                    $.each(varietyAttr.dictList, function (index, dict) {
                                        if (varietyAttr.attributeValueV2List == null) {
                                            html += "<option value='" + varietyAttr.attributeInfo.id + "-" + dict.label + "'>" + dict.label + "</option>";
                                        } else {
                                            html += "<option value='" + varietyAttr.attributeInfo.id + "-" + dict.label + "'";
                                            $.each(varietyAttr.attributeValueV2List, function (index, attributeValue) {
                                                if (attributeValue.value == dict.label) {
                                                    html += "selected='selected'";
                                                    return false;
                                                }
                                            });
                                            html += ">" + dict.label + "</option>";
                                        }
                                    });
                                    html += "            </select>";
                                    html += "            <span class='help-inline'><font color='red'>*</font></span>";
                                    html += "        </div>";
                                    html += "    </div>";
                                }
                            });
                            $("#variety").after(html);

                        }
                    });
                }
            }
        });
    });
    $('select[about="choose"]').searchableSelect();

</script>

</body>
</html>
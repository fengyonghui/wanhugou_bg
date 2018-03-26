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
        <div style="margin-left: 180px">
            <%--<form:select path="propValue.id" class="input-xlarge required" id="propValueId">--%>
                <%--<form:option value="" label="请选择品牌"/>--%>
                <%--<form:options items="${propValueList}" itemLabel="value" itemValue="id" htmlEscape="false"/>--%>
            <%--</form:select>--%>
                <form:select title="choose" path="brandId" class="js-example-basic-multiple">
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
        <div style="margin-left: 180px">
            <form:select title="choose" path="bizVarietyInfo.id" class="input-medium required">
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
        <div id="cateProp" style="margin-left: 180px">
            <c:forEach items="${prodTagList}" var="tagInfo">
                <div  style="width: 100%;display: inline-block">
                    <span  style="float:left;width:60px;padding-top:3px">${tagInfo.name}：</span>
                    <c:choose>
                        <c:when test="${tagInfo.dictList!=null}">
                            <form:select title="choose" path="textureStr" class="input-medium required">
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
        <label class="control-label">上传颜色图片：</label>
        <div class="controls">
            <input class="btn" type="button" value="上传图片" onclick="uploadPic()"/>
        </div>
        <br/>
        <div class="controls">
           <table class="table table-striped table-bordered table-condensed" id="uploadPicTable">
               <thead>
               <tr>
                    <th>颜色</th>
                    <th>图片</th>
                    <th>图片操作</th>
                    <th>操作</th>
               </tr>
               </thead>
               <tbody id="uploadPicTableData">
               <tr>

               </tr>
               </tbody>
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
        <div class="controls">
           <table class="table table-striped table-bordered table-condensed" id="skuTable">
               <thead>
               <tr>
                    <th>尺寸</th>
                    <th>颜色</th>
                    <th>价格</th>
                    <th>图片地址</th>
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
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery-plugin/jquery.searchableSelect.js" type="text/javascript"></script>
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
            "                   <td><input type=\"text\" value=\"$img\" customInput=\"imgInput\" readonly/></td>" +
            typeSelector +
            "                   <td onclick='deleteParentEle(this)'><input class=\"btn\" type=\"button\" value=\"删除\"/></td>" +
            "               </tr>";

        var colorTrArr = $("[customType='colorTr']");

        var customTypeAttr = $("[customType]");
        var selectedSizeArr = [];

        customTypeAttr.each(function () {
            if ($(this).val() != null && $(this).val() != "") {
                selectedSizeArr.push($(this).val());
            }
        });

        for (var i = 0; i < selectedSizeArr.length; i ++) {
            colorTrArr.each(function () {
                var colorInput = $($(this).find("[customInput = 'colorInput']")[0]).attr("value");
                var imgInput = $($(this).find("[customInput = 'imgInput']")[0]).attr("src");
                if (!imgInput) {
                    imgInput = "";
                }
                skuTableData.append(
                    tableHtml.replace("$size", selectedSizeArr[i])
                        .replace("$color", colorInput)
                        .replace("$price", "")
                        .replace("$img", imgInput)
                );
            });
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
            var imgInput = $($(this).find("[customInput = 'imgInput']")[0]).attr("value");
            var skuTypeSelect = $($(this).find("[customInput = 'skuTypeSelect']")[0]).find("option:selected").attr("value");
            inputForm.append(skuFormHtml.replace("$value", sizeInput + "|" + colorInput + "|" + priceInput + "|" + skuTypeSelect + "|" + imgInput));
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

    //新建或编辑 保存提交
    function submitPic(id){
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
        ajaxFileUploadPic(id);
    }

    function ajaxFileUploadPic(id) {
        $.ajaxFileUpload({
            url : '${ctx}/biz/product/bizProductInfo/saveColorImg', //用于文件上传的服务器端请求地址
            secureuri : false, //一般设置为false
            fileElementId : id, //文件上传空间的id属性  <input type="file" id="file" name="file" />
            type : 'POST',
            dataType : 'text', //返回值类型 一般设置为json
            success : function(data, status) {
                //服务器成功响应处理函数
                var msg = data.substring(data.indexOf("{"), data.indexOf("}")+1);
                var msgJSON = JSON.parse(msg);

                var img = $("#" + id + "Img");
                img.attr("src", msgJSON.fullName);
            },
            error : function(data, status, e) {
                //服务器响应失败处理函数
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
            "                   <td><img id=\"colorImg$idImg\" customInput=\"imgInput\"/></td>" +
            "                   <td>" +
            "                       <input type=\"file\" name=\"colorImg\" id=\"colorImg$id\" value=\"上传\"/>" +
            "                       <input type=\"button\" value=\"提交\" onclick=\"submitPic('colorImg$id')\"/>" +
            "                       <input type=\"button\" value=\"删除\"  onclick=\"deletePic('colorImg$idImg')\"/>" +
            "                   </td>" +
            "                   <td onclick='deleteParentEle(this)'><input class=\"btn\" type=\"button\" value=\"删除\"/></td>" +
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

        $('select[title="choose"]').searchableSelect();

        initSkuTable();

    });

</script>

</body>
</html>
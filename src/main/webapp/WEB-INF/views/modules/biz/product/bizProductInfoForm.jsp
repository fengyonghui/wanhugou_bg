<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>产品信息表管理</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/treeview.jsp" %>
    <script src="${ctxStatic}/bootstrap/multiselect.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            var tree = "";
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
    <%--<input type="hidden" id="cateValueId" value="${bizProductInfo.catePropValue.id}"/>--%>
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
                <form:select path="dict.id" class="input-xlarge" disabled="true">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('brand')}" itemLabel="label" itemValue="value"
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
            <sys:treeselect id="office" name="office.id" value="${entity.office.id}" labelName="office.name"
                            labelValue="${entity.office.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
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
            tagInfoList
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">产品属性：</label>
        <div id="cateProp" class="controls">
            <c:forEach items="${tagInfoList}" var="tagInfo">
                <div  style="width: 100%;display: inline-block">
                    <span  style="float:left;width:60px;padding-top:3px">${tagInfo.name}：</span>
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

                        <select name="aaa"  id="search_${tagInfo.id}_to" class="input-xlarge" size="8" multiple="multiple">
                            <%--<c:forEach items="${bizCategoryInfo.catePropValueMap[propertyInfo.id]}" var="propValue">--%>
                                <%--&lt;%&ndash;<input class="value_${propertyInfo.id}" id="value_${propValue.id}" type="checkbox" name="propertyMap[${propertyInfo.id}].catePropertyValues" value="${propValue.id}"/> ${propValue.value}&ndash;%&gt;--%>
                                <%--<option value="${propertyInfo.id}-${propValue.propertyValueId}">${propValue.value}</option>--%>
                            <%--</c:forEach>--%>
                        </select>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="biz:product:bizProductInfo:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                           type="submit"
                                                                           value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>
<script src="${ctxStatic}/bootstrap/multiselect.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>
<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
<script type="text/javascript">
    $(document).ready(function() {

        <%--window.prettyPrint && prettyPrint();--%>

        $('select[title="search"]').multiselect({
            search: {
                left: '<input type="text" name="q" style="display: block;width: 95%"  placeholder="Search..." />',
                right: '<input type="text" name="q" style="display: block;width: 95%" class="input-large" placeholder="Search..." />',
            }
            // ,
            // fireSearch: function(value) {
            //     return value.length >=1 ;
            // }
        });
    });
</script>
</body>
</html>
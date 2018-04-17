<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
    <title>万户通平台业务数据</title>
    <meta name="decorator" content="default"/>
    <style>
        .table_text_center tr th {
            text-align: center;
            vertical-align:middle;
        }
    </style>
</head>
<body>
<span>
    <label>
        <select class="input-medium" id="purchasingId">
            <option value="0" label="全部"></option>
            <c:forEach items="${purchasingList}" var="v">
                <c:if test="${v.id == officeId}">
                    <option selected value="${v.id}" label="${v.name}">${v.name}</option>
                </c:if>
                <c:if test="${v.id != officeId}">
                    <option value="${v.id}" label="${v.name}">${v.name}</option>
                </c:if>
            </c:forEach>
        </select>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="init();"/>
        <%--<input id="exportTable"  onclick="exportTable()" class="btn btn-primary" type="submit" value="导出表格"/>--%>
    </label>
</span>
<table id="contentTable" class="table table-bordered table-condensed table_text_center">
    <thead>
    <tr>
        <th>采购商</th>
        <th>平均每月订单量</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${custList}" var="dataName">
        <tr>
            <td>${dataName}</td>
            <td>${dataMap.dataName}</td>
            <td>${dataMap[dataName]}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="application/javascript">
    function init() {
        var officeId = $("#purchasingId").val();
        window.location.href = "orderCountFrequency?&officeId=" + officeId;
    }
    function exportTable() {
        var officeId = $("#purchasingId").val();
        //定义一个form表单
        var myform = $("<form></form>");
        myform.attr('method','post');
        myform.attr('action',"${adminPath}/biz/statistics/platform/overviewDownload");

        var myOfficeId = $("<input type='hidden' name='officeId' />");
        myOfficeId.attr('value', officeId);

        myform.append(myOfficeId);

        myform.appendTo('body').submit();

    }

</script>
</body>
</html>
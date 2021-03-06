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
        <input type="text" id="startDate" value="${date}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
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
        </label>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="init();"/>
        <%--<input id="exportTable"  onclick="exportTable()" class="btn btn-primary" type="submit" value="导出表格"/>--%>
    </label>
</span>
<table id="contentTable" class="table table-bordered table-condensed table_text_center">
    <thead>
    <tr>
        <th colspan="10">万户通平台业务数据 ${date}</th>
    </tr>
    <tr>
        <th rowspan="2">姓名</th>
        <th colspan="9">目标分析</th>
    </tr>
    <tr>
        <th>月计划采购额(元)</th>
        <th>月累计销量</th>
        <th>月计划联营订单总额</th>
        <th>月联营订单总额</th>
        <th>月计划代采订单总额</th>
        <th>月代采订单总额</th>
        <th>个人服务费</th>
        <th>月销售量统计</th>
        <th>日销售额(元)</th>
        <th>达成率</th>
        <th>月累计差异</th>
        <th>剩余天数</th>
        <th>每日最低回款额</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${dataList}" var="dataItme">
    <tr>
        <c:forEach items="${dataItme.value}" var="item">
            <tr>
            <td>
                    ${item.name}
            </td>
            <td>
                    ${item.procurement}
            </td>
            <td>
                    ${item.accumulatedSalesMonth}
            </td>
            <td>
                    ${item.jointOrderPlanAmountTotal}
            </td>
            <td>
                    ${item.jointOrderAmountTotal}
            </td>
            <td>
                    ${item.purchaseOrderPlanAmountTotal}
            </td>
            <td>
                    ${item.purchaseOrderAmountTotal}
            </td>
            <td>
                    ${item.serviceCharge}
            </td>
            <td>
                    ${item.receiveTotal}
            </td>
            <td>
                    ${item.procurementDay}
            </td>
            <td>
                    ${item.yieldRate}
            </td>
            <td>
                    ${item.differenceTotalMonth}
            </td>
            <td>
                    ${item.remainingDays}
            </td>
            <td>
                    ${item.dayMinReturned}
            </td>
            </tr>
        </c:forEach>
    </tr>
    </c:forEach>
    </tbody>
</table>
<script type="application/javascript">
    function init() {
        $Mask.AddLogo("正在加载");
        var startDate = $("#startDate").val();
        var officeId = $("#purchasingId").val();
        window.location.href = "overviewSingle?date=" + startDate + "&officeId=" + officeId;
    }
    function exportTable() {
        var startDate = $("#startDate").val();
        var officeId = $("#purchasingId").val();
        //定义一个form表单
        var myform = $("<form></form>");
        myform.attr('method','post');
        myform.attr('action',"${adminPath}/biz/statistics/platform/overviewDownload");

        var myProductId = $("<input type='hidden' name='date' />");
        var myOfficeId = $("<input type='hidden' name='officeId' />");
        myProductId.attr('value', startDate);
        myOfficeId.attr('value', officeId);

        myform.append(myProductId);
        myform.append(myOfficeId);

        myform.appendTo('body').submit();

    }

</script>
</body>
</html>
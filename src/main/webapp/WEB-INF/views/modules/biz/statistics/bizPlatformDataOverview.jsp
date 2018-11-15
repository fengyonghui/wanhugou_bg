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
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="init();"/>
        <input id="exportTable"  onclick="exportTable()" class="btn btn-primary" type="submit" value="导出表格"/>
    </label>
</span>
<table id="contentTable" class="table table-bordered table-condensed table_text_center">
    <thead>
    <tr>
        <th></th>
        <th colspan="10">万户通平台业务数据 ${date}</th>
    </tr>
    <tr>
        <th rowspan="2">省份</th>
        <th rowspan="2">所属采购中心</th>
        <th colspan="9">目标分析</th>
    </tr>
    <tr>
        <th>月计划回款额(元)</th>
        <th>月销售量统计</th>
        <th>达成率</th>
        <th>月累计销量</th>
        <th>日销售额(元)</th>
        <th>月累计差异</th>
        <th>剩余天数</th>
        <th>每日最低回款额</th>
        <th>有效会员开单量</th>
        <th>新用户</th>
        <th>新用户达成率</th>
        <th>计划服务费</th>
        <th>服务费</th>
        <th>服务费收入任务比例</th>
        <th>库存金额</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${dataList}" var="dataItme">
    <tr>
        <td rowspan="${fn:length(dataItme.value) + 1}">
                ${dataItme.key}
        </td>
        <c:forEach items="${dataItme.value}" var="item">
            <tr>
            <td>
                    ${item.name}
            </td>
                <td>
                        ${item.procurement}
                </td>
                <td>
                        ${item.receiveTotal}
                </td>
                <td>
                        ${item.yieldRate}
                </td>
            <td>
                    ${item.accumulatedSalesMonth}
            </td>

            <td>
                    ${item.procurementDay}
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
            <td>
                    ${item.newUserPlan}
            </td>
            <td>
                    ${item.newUser}
            </td>
            <td>
                    ${item.newUserRate}
            </td>
                <td>
                    ${item.serviceChargePlan}
            </td>
            <td>
                    ${item.serviceCharge}
            </td>
            <td>
                    ${item.serviceChargeRate}
            </td>
            <td>
                    ${item.stockAmount}
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
        window.location.href = "overview?date=" + startDate;
    }
    function exportTable() {
        var startDate = $("#startDate").val();
        //定义一个form表单
        var myform = $("<form></form>");
        myform.attr('method','post')
        myform.attr('action',"${adminPath}/biz/statistics/platform/overviewDownload");

        var myProductId = $("<input type='hidden' name='date' />");
        myProductId.attr('value', startDate);

        myform.append(myProductId);

        myform.appendTo('body').submit();

    }

</script>
</body>
</html>
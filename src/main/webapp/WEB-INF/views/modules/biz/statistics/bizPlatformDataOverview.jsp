<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
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
<table id="contentTable" class="table table-striped table-bordered table-condensed table_text_center">
    <thead>
    <tr>
        <th></th>
        <th colspan="10">万户通平台业务数据</th>
    </tr>
    <tr>
        <th rowspan="2">省份</th>
        <th rowspan="2">所属采购中心</th>
        <th colspan="9">目标分析</th>
    </tr>
    <tr>
        <th>采购额(元)</th>
        <th>月累计销量</th>
        <th>日采购额(元)</th>
        <th>达成率</th>
        <th>月累计差异</th>
        <th>剩余天数</th>
        <th>每日最低回款额</th>
        <th>库存金额</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${dataList}" var="dataItme">
        <tr>
            <td>
                    ${dataItme.province}
            </td>
            <td>
                    ${dataItme.name}
            </td>
            <td>
                    ${dataItme.procurement}
            </td>
            <td>
                    ${dataItme.accumulatedSalesMonth}
            </td>
            <td>
                    ${dataItme.procurementDay}
            </td>
            <td>
                    ${dataItme.yieldRate}
            </td>
            <td>
                    ${dataItme.differenceTotalMonth}
            </td>
            <td>
                    ${dataItme.remainingDays}
            </td>
            <td>
                    ${dataItme.dayMinReturned}
            </td>
            <td>
                    ${dataItme.stockAmount}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="application/javascript">
    function test() {
        window.location.href = "baidu.com";
    }
</script>
</body>
</html>
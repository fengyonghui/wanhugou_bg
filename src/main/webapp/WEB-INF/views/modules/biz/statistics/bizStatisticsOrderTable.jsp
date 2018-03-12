<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>订单统计</title>
</head>
<body>
<div style="height: 50px">
    <input name="applyDate" id="applyDate" value="${month}" onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <input id="search" class="btn btn-primary" type="button" value="查询1"/>

</div>
<div>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
            <tr>
                <th>采购中心</th>
                <th>上月销售额</th>
                <th>本月销售额</th>
                <th>上月销售利润</th>
                <th>本月销售利润</th>
                <th>上月订单量</th>
                <th>本月订单量</th>
            </tr>
        </thead>
        <tbody id="orderTable">

        </tbody>
    </table>
</div>
<div>
    <input onclick="window.print();" type="button" class="btn btn-primary" value="打印采购中心订单统计" style="background:#F78181;"/>
</div>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript">

    $(document).ready(function () {
        var applyDate = $("#applyDate").val();
        $.ajax({
            type: 'post',
            url: "${adminPath}/biz/statistics/centOrderTable",
            data: {"month": applyDate},
            dataType: "json",
            success: function (msg) {
                $("#orderTable").empty();
                var orderTable = "";
                var sumUpTotalMoney = 0;
                var sumTotalMoney = 0;
                var sumUpProfitPrice = 0;
                var sumProfitPrice = 0;
                var sumUpOrderCount = 0;
                var sumOrderCount = 0;
                $.each(msg,function (key,value) {
                    orderTable += "<tr>";
                    orderTable += "<td>"+key+"</td>";
                    orderTable += "<td>"+value.upTotalMoney+"</td>";
                    orderTable += "<td>"+value.totalMoney+"</td>";
                    orderTable += "<td>"+value.upProfitPrice+"</td>";
                    orderTable += "<td>"+value.profitPrice+"</td>";
                    orderTable += "<td>"+value.upOrderCount+"</td>";
                    orderTable += "<td>"+value.orderCount+"</td>";
                    orderTable += "</tr>";
                    sumUpTotalMoney += value.upTotalMoney;
                    sumTotalMoney += value.totalMoney;
                    sumUpProfitPrice += value.upProfitPrice;
                    sumProfitPrice += value.profitPrice;
                    sumUpOrderCount += value.upOrderCount;
                    sumOrderCount += value.orderCount;
                });
                orderTable += "<tr>";
                orderTable += "<td>合计</td>";
                orderTable += "<td>"+sumUpTotalMoney+"</td>";
                orderTable += "<td>"+sumTotalMoney+"</td>";
                orderTable += "<td>"+sumUpProfitPrice+"</td>";
                orderTable += "<td>"+sumProfitPrice+"</td>";
                orderTable += "<td>"+sumUpOrderCount+"</td>";
                orderTable += "<td>"+sumOrderCount+"</td>";
                orderTable += "</tr>";
                $("#orderTable").append(orderTable);
            }
        })
    });
    $("#search").click(function () {
        var applyDate = $("#applyDate").val();
        $.ajax({
            type: 'post',
            url: "${adminPath}/biz/statistics/centOrderTable",
            data: {"month": applyDate},
            dataType: "json",
            success: function (msg) {
                $("#orderTable").empty();
                var orderTable = "";
                var sumUpTotalMoney = 0;
                var sumTotalMoney = 0;
                var sumUpProfitPrice = 0;
                var sumProfitPrice = 0;
                var sumUpOrderCount = 0;
                var sumOrderCount = 0;
                $.each(msg,function (key,value) {
                    orderTable += "<tr>";
                    orderTable += "<td>"+key+"</td>";
                    orderTable += "<td>"+value.upTotalMoney+"</td>";
                    orderTable += "<td>"+value.totalMoney+"</td>";
                    orderTable += "<td>"+value.upProfitPrice+"</td>";
                    orderTable += "<td>"+value.profitPrice+"</td>";
                    orderTable += "<td>"+value.upOrderCount+"</td>";
                    orderTable += "<td>"+value.orderCount+"</td>";
                    orderTable += "</tr>";
                    sumUpTotalMoney += value.upTotalMoney;
                    sumTotalMoney += value.totalMoney;
                    sumUpProfitPrice += value.upProfitPrice;
                    sumProfitPrice += value.profitPrice;
                    sumUpOrderCount += value.upOrderCount;
                    sumOrderCount += value.orderCount;
                });
                orderTable += "<tr>";
                orderTable += "<td>合计</td>";
                orderTable += "<td>"+sumUpTotalMoney+"</td>";
                orderTable += "<td>"+sumTotalMoney+"</td>";
                orderTable += "<td>"+sumUpProfitPrice+"</td>";
                orderTable += "<td>"+sumProfitPrice+"</td>";
                orderTable += "<td>"+sumUpOrderCount+"</td>";
                orderTable += "<td>"+sumOrderCount+"</td>";
                orderTable += "</tr>";
                $("#orderTable").append(orderTable);
            }
        })
    });
</script>
</body>
</html>
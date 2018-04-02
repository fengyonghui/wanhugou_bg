<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>订单统计</title>
</head>
<body>
<div style="height: 50px">
    <input id="startDate" value="" onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <select class="input-medium" id="barChartType">
        <option value="0" label="年">年</option>
        <option value="1" label="月">月</option>
    </select>

    <select class="input-medium" id="centerType">
        <option value="0" label="全部">全部</option>
        <option value="8" label="采购中心">采购中心</option>
        <option value="11" label="网供">网供</option>
        <option value="10" label="配资业务">配资业务</option>
    </select>
    <input id="search" onclick="initTable()" class="btn btn-primary" type="button" value="查询"/>

</div>
<div>
    <input id="showChart" onclick="showChart()" class="btn btn-primary" type="button" value="查看图表"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr id="monthCol">
            <th>数据类型</th>
        </tr>
        </thead>
        <tbody id="orderTable">
        </tbody>
    </table>

</div>
<div>
    <input onclick="window.print();" type="button" class="btn btn-primary" value="打印采购中心订单统计"
           style="background:#F78181;"/>
</div>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript" src="/static/common/base.js"></script>
<script type="application/javascript">
    function showChart() {
        var startDate = $("#startDate").val();
        $Url.go2Url('${adminPath}/biz/statistics/platform/order?startDate=' + startDate);
    }

    function initTable() {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();

        var barChartTypeEle = $("#barChartType");
        var barChartType = barChartTypeEle.find("option:selected").val();
        var barChartTypeDesc = barChartTypeEle.find("option:selected").html();


        var centerTypeEle = $("#centerType");
        var centerType = centerTypeEle.find("option:selected").val();

        if (barChartType == '1' && (startDate == '' || startDate == null)) {
            alert("请选择日期");
            return;
        }
        if($DateUtil.CompareDate('2017-09-01',startDate)) {
            alert("日期选择错误!请选择2017年9月以后的日期");
            return;
        }


        $.ajax({
            type: 'post',
            url: "${adminPath}/biz/statistics/platform/orderTableData",
            data: {"startDate" : startDate, "type" : barChartType,  "centerType" : centerType},
            dataType: "json",
            success: function (msg) {
                $("#orderTable").empty();
                $("#monthCol").empty();
                $("#monthCol").append("<th>数据类型</th>");
                var orderTable = "";
                var orderTable1 = "";
                var orderTable2 = "";
                orderTable += "<tr>";
                orderTable1 += "<tr>";
                orderTable += "<td>销售额</td>";
                orderTable1 += "<td>订单量</td>";
                orderTable2 += "<td>利润</td>";
                $.each(msg.bizOrderStatisticsDtoList, function (key1, value1) {
                    $("#monthCol").append("<th>" + value1.createDate + "</th>");
                    orderTable += "<td>" + value1.totalMoney + "</td>";
                    orderTable1 += "<td>" + value1.orderCount + "</td>";
                    orderTable2 += "<td>" + value1.profitPrice + "</td>";
                });
                orderTable += "</tr>";
                orderTable1 += "</tr>";
                orderTable2 += "</tr>";
                $("#orderTable").append(orderTable);
                $("#orderTable").append(orderTable1);
                $("#orderTable").append(orderTable2);
            }
        })
    }

    $(document).ready(function () {
        initTable();
    });
</script>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>订单统计</title>
</head>
<body>
<div style="height: 50px">
    <input id="startDate" value="${startDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <select class="input-medium" id="centerType">
        <option value="8" label="采购中心">采购中心</option>
        <option value="13" label="网供">网供</option>
        <option value="10" label="配资业务">配资业务</option>
    </select>
    <input id="search" onclick="initTable()" class="btn btn-primary" type="button" value="查询"/>

</div>
<div>
    <input id="showChart" onclick="showChart()" class="btn btn-primary" type="button" value="查看图表"/>
    <h4>销售额</h4>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr id="monthCol">
        </tr>
        </thead>
        <tbody id="orderTable">
        </tbody>
    </table>
    <h4>订单量</h4>
    <table id="contentTable1" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr id="monthCol1">
        </tr>
        </thead>
        <tbody id="orderTable1">
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
        $Url.go2Url('${adminPath}/biz/statistics/day/order?startDate=' + startDate);
    }

    function initTable() {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        var centerTypeEle = $("#centerType");
        var centerType = centerTypeEle.find("option:selected").val();

        $.ajax({
            type: 'post',
            url: "${adminPath}/biz/statistics/day/orderTableData",
            data: {"startDate": startDate, "endDate": endDate, "centerType": centerType},
            dataType: "json",
            success: function (msg) {
                $("#orderTable").empty();
                $("#orderTable1").empty();
                $("#monthCol").empty();
                $("#monthCol1").empty();
                $("#monthCol").append("<th>采购中心</th>");
                $("#monthCol1").append("<th>采购中心</th>");
                $.each(msg.dateList, function (key, value) {
                    $("#monthCol").append("<th>" + value + "</th>");
                    $("#monthCol1").append("<th>" + value + "</th>");
                });
                console.info(msg.dateList);
                var orderTable = "";
                var orderTable1 = "";
                $.each(msg.officeNameSet, function (key, value) {
                    orderTable += "<tr>";
                    orderTable1 += "<tr>";
                    orderTable += "<td>" + value + "</td>";
                    orderTable1 += "<td>" + value + "</td>";
                    $.each(msg.dateList, function (key1, value1) {
                        if (!jQuery.isEmptyObject(msg.dataMap[value1]) && !jQuery.isEmptyObject(msg.dataMap[value1][value])) {
                            var data = msg.dataMap[value1][value];
                            orderTable += "<td>" + data.totalMoney + "</td>";
                            orderTable1 += "<td>" + data.orderCount + "</td>";
                        } else {
                            orderTable += "<td>" + 0 + "</td>";
                            orderTable1 += "<td>" + 0 + "</td>";
                        }
                    });
                    orderTable += "</tr>";
                    orderTable1 += "</tr>";
                });
                $("#orderTable").append(orderTable);
                $("#orderTable1").append(orderTable1);
            }
        })
    }

    $(document).ready(function () {
        initTable();
    });
</script>
</body>
</html>
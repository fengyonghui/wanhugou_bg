<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>订单统计</title>
</head>
<body>
<div>
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
        <tbody>
            <c:forEach items="${map}" var="resultSet">
                <tr>
                    <td>${resultSet.key}</td>
                    <td>${resultSet.value.upTotalMoney}</td>
                    <td>${resultSet.value.totalMoney}</td>
                    <td>${resultSet.value.upProfitPrice}</td>
                    <td>${resultSet.value.profitPrice}</td>
                    <td>${resultSet.value.upOrderCount}</td>
                    <td>${resultSet.value.orderCount}</td>
                </tr>
            </c:forEach>
                <tr>
                    <td>合计</td>
                    <td>合计</td>
                    <td>合计</td>
                    <td>合计</td>
                    <td>合计</td>
                    <td>合计</td>
                    <td>合计</td>
                </tr>
        </tbody>
    </table>
</div>


</body>
<script type="application/javascript" src="/static/jquery/jquery-1.9.1.min.js"></script>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript">
    alert(0);
    $(document).ready(function () {
        alert(1);

    });
    $("#search").click(function () {
        var applyDate = $("#applyDate").val();
        alert("1")
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/centOrderTable",
            data: {"month": applyDate},
            dataType: "json",
            success: function (msg) {
                console.log(msg);
            }
        })
    });
</script>
</html>
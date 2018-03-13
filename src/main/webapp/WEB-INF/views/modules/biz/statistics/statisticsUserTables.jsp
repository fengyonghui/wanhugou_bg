<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>业绩统计</title>
    <script type="application/javascript">
    $(document).ready(function() {
          initChart();
    });
    function initChart() {
    var singleSalesVolumeChart = document.getElementById('userTable');

        var applyDate = $("#applyDate").val();
        var purchasingIdEle = $("#purchasingId");
        var purchasingId = purchasingIdEle.find("option:selected").val();

        var usNameEle = $("#usName");
        var usName = usNameEle.find("option:selected").val();
        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/userSaleData",
            data: {"month": applyDate, "purchasingId": purchasingId, "usName": usName},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    $("#userTable").empty();
                    return;
                }
                usNameEle.html("");
                for (var i = 0; i < msg.nameList.length; i++) {
                    var child = "<option value=\"" + msg.nameList[i] + "\" label=\"" + msg.nameList[i] + "\">"+msg.nameList[i]+"</option>";
                    usNameEle.append(child);
                }
                $("#userTable").empty();
                $.each(msg.userMonthList, function (key, values) {
                    var ary=values.split("+");
                    $("#userTable").append("<tr>"+
                            "<td>"+ary[0]+"</td>"+
                             "<td>"+ary[1]+"</td>"+
                              "<td>"+ary[2]+"</td>"+
                               "<td>"+ary[3]+"</td>"+
                               "<td>"+ary[4]+"</td>"+
                            "</tr>");
                });
            }
            <%--,error: function (XMLHttpRequest, textStatus, errorThrown) {--%>
                <%--alert("未查询到数据!");--%>
                <%--$("#userTable").empty();--%>
            <%--}--%>
        });
    }
    initChart();

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/statistics/orderTables">顾问个人订单统计</a></li>
</ul>
<div>
    <input name="applyDate" id="applyDate" value="${month}" onchange="initChart();"
           onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <label>
        <select class="input-medium" id="usName">
        </select>
    </label>
    <input onclick="initChart();" class="btn btn-primary" type="button" value="查询"/>
</div>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>月份</th>
        <th>销售额</th>
        <th>利润</th>
        <th>会员新增量</th>
        <th>会员数</th>
    </tr>
    </thead>
    <tbody id="userTable">
    <c:forEach items="${productStatisticsTableList}" var="userSaleList">
        <tr>
            <td>
                    ${userSaleList.createTime}
            </td>
            <td>
                    ${userSaleList.totalMoney}
            </td>
            <td>
                ${userSaleList.profitPrice}
            </td>
            <td>
                ${userSaleList.addCustCount}
            </td>
            <td>
                ${userSaleList.custCount}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
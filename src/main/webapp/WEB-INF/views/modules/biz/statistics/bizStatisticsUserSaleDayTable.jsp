<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>订单统计</title>
</head>
<body>
<div style="height: 50px">
    <input name="day" id="day" value="${day}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <label>
        <select class="input-medium" id="purchasingId">
            <option value="0" label="全部"></option>
            <c:forEach items="${purchasingList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <input id="search" class="btn btn-primary" type="button" onclick="initData()" value="查询"/>

</div>
<div>
    <input id="showChart" onclick="$Url.go2Url('${adminPath}/biz/statistics/day/userSale')" class="btn btn-primary" type="button" value="查看图表"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>采购专员</th>
            <th>销售额</th>
            <th>订单量</th>
        </tr>
        </thead>
        <tbody id="orderTable">
                <c:forEach items="${dataList}" var="v">
            <tr>
                <td>${v.name}</td>
                <td>${v.totalMoney}</td>
                <td>${v.orderCount}</td>
            </tr>
                </c:forEach>
        </tbody>
    </table>
</div>
<div>
    <input onclick="window.print();" type="button" class="btn btn-primary" value="打印采购中心订单统计" style="background:#F78181;"/>
</div>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript" src="/static/common/base.js"></script>
<script type="application/javascript">
    function initData() {
        var day = $("#day").val();
        var purchasingIdEle = $("#purchasingId");
        var purchasingId = purchasingIdEle.find("option:selected").val();

        window.location.href="${adminPath}/biz/statistics/day/userSaleDataTable?day=" + day + "&purchasingId=" + purchasingId;
    }
</script>
</body>
</html>
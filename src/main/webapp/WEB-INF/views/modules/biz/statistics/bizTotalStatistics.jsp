<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>统计总值</title>
</head>
<body>
<div style="height: 50px">

</div>
<div>
    <table class="table table-striped table-bordered table-condensed">
        <input type="button" onclick="downloadExcel()" value="EXCEL导出"/>
        <caption style="width:100%; height: 50px; text-align:center; color: #9d9d9d; font-size: xx-large">
            万户通平台总体情况(<fmt:formatDate value="${time}" pattern="yyyy-MM-dd"/>)
        </caption>
        <thead>
        <tr>
            <th>名称</th>
            <th>单位</th>
            <th>数量</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${totalMap}" var="totalSet">
            <tr>
                <td>${totalSet.key}</td>
                <td>${totalSet.value.unit}</td>
                <td>${totalSet.value.count}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%--<form:form id="inputForm"  action="" method="post" class="form-horizontal">

<div class="control-group">
    <label class="control-label">会员总数(人):</label>
    <div class="controls">${totalStatistics.custCount}</div>
</div>
<div class="control-group">
    <label class="control-label">采购中心数（个）：</label>
    <div class="controls">${totalStatistics.centCount}</div>
</div>
<div class="control-group">
    <label class="control-label">网供（个）：</label>
    <div class="controls">${totalStatistics.supplyCount}</div>
</div>
<div class="control-group">
    <label class="control-label">配资中心（个）：</label>
    <div class="controls">${totalStatistics.capitalCount}</div>
</div>
<div class="control-group">
    <label class="control-label">订单数量（单）：</label>
    <div class="controls">${totalStatistics.orderCount}</div>
</div>
<div class="control-group">
    <label class="control-label">总额（元）：</label>
    <div class="controls"><font style="color: green">${totalStatistics.totalMoney}</font></div>
</div>
<div class="control-group">
    <label class="control-label">已收货款（元）：</label>
    <div class="controls">${totalStatistics.receiveMoney}</div>
</div>
<div class="control-group">
    <label class="control-label">商品数量（个）：</label>
    <div class="controls">${totalStatistics.skuCount}</div>
</div>
<div class="control-group">
    <label class="control-label">平均客单价：</label>
    <div class="controls">${totalStatistics.avgPrice}</div>
</div>
</form:form>--%>
<script type="application/javascript">
    function downloadExcel() {
        window.location.href = "${ctx}/biz/statistics/bizTotalStatisticsDtoDownload";
    }
</script>
</body>
</html>
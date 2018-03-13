<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>统计总值</title>
</head>
<body>
<div class="control-group">
    <label class="control-label">会员总数：</label>
    <div class="controls">${totalStatistics.custCount}</div>
</div>
<div class="control-group">
    <label class="control-label">采购中心数：</label>
    <div class="controls">${totalStatistics.centCount}</div>
</div>
<div class="control-group">
    <label class="control-label">订单数量：</label>
    <div class="controls">${totalStatistics.orderCount}</div>
</div>
<div class="control-group">
    <label class="control-label">总额：</label>
    <div class="controls">${totalStatistics.totalMoney}</div>
</div>
<div class="control-group">
    <label class="control-label">已收货款：</label>
    <div class="controls">${totalStatistics.receiveMoney}</div>
</div>
<div class="control-group">
    <label class="control-label">商品数量：</label>
    <div class="controls">${totalStatistics.skuCount}</div>
</div>
<div class="control-group">
    <label class="control-label">平均客单价：</label>
    <div class="controls">${totalStatistics.avgPrice}</div>
</div>
<script type="application/javascript">


</script>
</body>
</html>
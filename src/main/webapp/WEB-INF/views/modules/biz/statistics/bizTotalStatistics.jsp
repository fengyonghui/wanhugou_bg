<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>统计总值</title>
</head>
<body>
<form:form id="inputForm"  action="" method="post" class="form-horizontal">

<div class="control-group">
    <label class="control-label">会员总数(人):</label>
    <div class="controls">${totalStatistics.custCount}</div>
</div>
<div class="control-group">
    <label class="control-label">采购中心数（个）：</label>
    <div class="controls">${totalStatistics.centCount}</div>
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
</form:form>
<script type="application/javascript">


</script>
</body>
</html>
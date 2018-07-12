<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>供应总额统计</title>

</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/statistics/between/vendorProductPriceTables">供应总额统计</a></li>
</ul>
<div>
    <input onclick="window.history.go(-1);" class="btn btn-primary" type="button" value="返回"/>

</div>
<%--<input id="showChart" onclick="$Url.go2Url('${ctx}/biz/statistics/between/product')" class="btn btn-primary" type="button" value="查看图表"/>--%>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>供应商名称</th>
        <th>供应数量</th>
        <th>供应金额</th>
        <th>商品货号</th>
        <th>商品名称</th>
    </tr>
    </thead>
    <tbody id="proudctTable">
    <c:forEach items="${result}" var="v">
        <tr>
            <td>
                    ${v.officeName}
            </td>
            <td>
                    ${v.orderCount}
            </td>
            <td>
                    ${v.totalMoney}
            </td>
            <td>
                    ${v.skuItemNo}
            </td>
            <td>
                    ${v.skuName}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>
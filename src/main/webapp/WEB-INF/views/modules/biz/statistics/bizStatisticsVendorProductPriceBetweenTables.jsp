<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>供应总额统计</title>

</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/statistics/productAnalysisTables">供应总额统计</a></li>
</ul>
<div>
    <input name="startDate" id="startDate" value="${startDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <input name="endDate" id="endDate" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <input onclick="initChart();" class="btn btn-primary" type="button" value="查询"/>

</div>
<%--<input id="showChart" onclick="$Url.go2Url('${ctx}/biz/statistics/between/product')" class="btn btn-primary" type="button" value="查看图表"/>--%>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>机构名称</th>
        <th>型号</th>
        <th>销售数量</th>
        <th>销售金额</th>
        <th>商品浏览量</th>
    </tr>
    </thead>
    <tbody id="proudctTable">
    <c:forEach items="${productStatisticsList}" var="productList">
        <tr>
            <td>
                    ${productList.vendorName}
            </td>
            <td>
                    ${productList.itemNo}
            </td>
            <td>
                    ${productList.count}
            </td>
            <td>
                    ${productList.totalMoney}
            </td>
            <td>
                    ${productList.clickCount}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>


</html>
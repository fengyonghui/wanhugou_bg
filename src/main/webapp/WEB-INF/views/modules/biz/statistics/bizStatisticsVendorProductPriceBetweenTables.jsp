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
    <input name="startDate" id="startDate" value="${startDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <input name="endDate" id="endDate" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <input onclick="initChart();" class="btn btn-primary" type="button" value="查询"/>

</div>
<%--<input id="showChart" onclick="$Url.go2Url('${ctx}/biz/statistics/between/product')" class="btn btn-primary" type="button" value="查看图表"/>--%>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>供应商ID</th>
        <th>供应商名称</th>
        <th>供应次数</th>
        <th>供应金额</th>
    </tr>
    </thead>
    <tbody id="proudctTable">
    <c:forEach items="${result}" var="v">
        <tr>
            <td>
                    ${v.officeId}
            </td>
            <td>
                    ${v.officeName}
            </td>
            <td>
                    ${v.orderCount}
            </td>
            <td>
                    ${v.totalMoney}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
    <script type="text/javascript">
        function initChart() {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            window.location.href = "${ctx}/biz/statistics/between/vendorProductPriceTables?startDate=" + startDate + "&endDate=" + endDate;
        }
    </script>
</body>
</html>
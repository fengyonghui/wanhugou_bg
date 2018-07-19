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
    <form:form id="searchForm" modelAttribute="office" action="${ctx}/biz/statistics/between/vendorProductPriceTables" method="post" class="breadcrumb form-search">
        <input name="startDate" id="startDate" value="${startDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
        <input name="endDate" id="endDate" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"/>
        <input name="vendName" id="vendName" value="${vendName}"/>
        <input onclick="init('query')" class="btn btn-primary" type="button" value="查询"/>
        <input onclick="init('download')" class="btn btn-primary" type="button" value="导出"/>
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    </form:form>
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
                <a href="${ctx}/biz/statistics/between/vendorSkuPriceTables?startDate=${startDate}&endDate=${endDate}&officeId=${v.officeId}">${v.officeId}</a>
            </td>
            <td>
                <a href="${ctx}/biz/statistics/between/vendorSkuPriceTables?startDate=${startDate}&endDate=${endDate}&officeId=${v.officeId}">${v.officeName}</a>
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
<div class="pagination">${page}</div>
    <script type="text/javascript">
        function init(methodType) {
            var startDate = $("#startDate").val();
            var endDate = $("#endDate").val();
            var vendName = $("#vendName").val();
            window.location.href = "${ctx}/biz/statistics/between/vendorProductPriceTables?startDate=" + startDate + "&endDate=" + endDate + "&vendName=" + vendName + "&methodType=" + methodType;
        }
        function page(n, s) {
            $("#pageNo").val(n);
            $("#searchForm").submit();
            return false;
        }
    </script>
</body>
</html>
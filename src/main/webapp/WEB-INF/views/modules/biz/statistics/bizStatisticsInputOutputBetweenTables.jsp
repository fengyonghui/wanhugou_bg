<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>供应总额统计</title>

</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/statistics/between/skuInputOutputRecord">供应总额统计</a></li>
</ul>
<div>
    <form:form id="searchForm" modelAttribute="office" action="${ctx}/biz/statistics/between/skuInputOutputRecord" method="post" class="breadcrumb form-search">
    商品编号:<input id="skuItemNo" value="${skuItemNo}" name="skuItemNo"/>
    仓库名称:<input id="invName" value="${invName}" name="invName"/>
        <select id="dataType" name="dataType">
                <%--1入库 0出库--%>
            <c:if test="${dataType != 1 && dataType != 0}">
                <option value="3">全部</option>
                <option value="1">入库</option>
                <option value="0">出库</option>
            </c:if>
            <c:if test="${dataType == 1}">
                <option value="1">入库</option>
                <option value="0">出库</option>
                <option value="3">全部</option>
            </c:if>
            <c:if test="${dataType == 0}">
                <option value="0">出库</option>
                <option value="1">入库</option>
                <option value="3">全部</option>
            </c:if>
        </select>
    <input onclick="init('query')" class="btn btn-primary" type="button" value="查询"/>
    <input onclick="init('download')" class="btn btn-primary" type="button" value="导出"/>
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    </form:form>
</div>
<%--<input id="showChart" onclick="$Url.go2Url('${ctx}/biz/statistics/between/product')" class="btn btn-primary" type="button" value="查看图表"/>--%>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>仓库名称</th>
        <th>数量</th>
        <th>类型</th>
        <th>商品名称</th>
        <th>商品编号</th>
        <th>时间</th>
    </tr>
    </thead>
    <tbody id="proudctTable">
    <c:forEach items="${result}" var="v">
        <tr style="color: ${v.dataType == 1 ? 'red' : 'green'}">
            <td>
                    ${v.invName}
            </td>
            <td>
                    ${v.countNumber}
            </td>
            <td>
                    ${v.dataType == 1 ? '入库' : '出库'}
            </td>
            <td>
                    ${v.skuName}
            </td>
            <td>
                    ${v.itemNo}
            </td>
            <td>
                <fmt:formatDate value="${v.createTime}" pattern="yyyy-MM-dd HH:mm:ss" dateStyle="full"/>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
<script type="text/javascript">
    function init(methodType) {
        var skuItemNo = $("#skuItemNo").val();
        var invName = $("#invName").val();
        var dataType = $("#dataType").val();
        window.location.href = "${ctx}/biz/statistics/between/skuInputOutputRecord?skuItemNo=" + skuItemNo + "&invName=" + invName + "&dataType=" + dataType + "&methodType=" + methodType;
    }

    function page(n, s) {
        var skuItemNo = $("#skuItemNo").val();
        var invName = $("#invName").val();
        $("#pageNo").val(n);
        $("#searchForm").submit();
        return false;
    }
</script>
</body>
</html>
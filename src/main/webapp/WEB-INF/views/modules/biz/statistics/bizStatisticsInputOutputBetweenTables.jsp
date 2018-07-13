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
    商品编号:<input id="skuItemNo" value="${skuItemNo}" required="required"/>
    仓库名称:<input id="invName" value="${invName}"/>
    <input onclick="init()" class="btn btn-primary" type="button" value="查询"/>

</div>
<%--<input id="showChart" onclick="$Url.go2Url('${ctx}/biz/statistics/between/product')" class="btn btn-primary" type="button" value="查看图表"/>--%>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>仓库名称</th>
        <th>数量</th>
        <th>类型</th>
        <th>时间</th>
    </tr>
    </thead>
    <tbody id="proudctTable">
    <c:forEach items="${result}" var="v">
        <tr>
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
                <fmt:formatDate value="${v.createTime}" pattern="yyyy-MM-dd HH:mm:ss" dateStyle="full"/>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="text/javascript">
    function init() {
        var skuItemNo = $("#skuItemNo").val();
        var invName = $("#invName").val();
        window.location.href = "${ctx}/biz/statistics/between/skuInputOutputRecord?skuItemNo=" + skuItemNo + "&invName=" + invName;
    }
</script>
</body>
</html>
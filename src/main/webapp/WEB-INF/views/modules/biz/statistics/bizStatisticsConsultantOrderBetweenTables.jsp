<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>产品统计</title>

</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/statistics/productAnalysisTables">商品分析(个人)</a></li>
</ul>
<div>
    <input name="startDate" id="startDate" value="${startDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <input name="endDate" id="endDate" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" required="required"/>
    <label>
        <select class="input-medium" id="purchasingId">
            <c:forEach items="${purchasingList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <input onclick="initChart();" class="btn btn-primary" type="button" value="查询"/>

</div>
<input id="showChart" onclick="$Url.go2Url('${adminPath}/biz/statistics/between/consultantOrder')" class="btn btn-primary" type="button" value="查看图表"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>采购中心</th>
        <th>采购专员</th>
        <th>订单数量</th>
    </tr>
    </thead>
    <tbody id="proudctTable">
    <c:forEach items="${resultList}" var="result">
        <tr>
            <td>
                    ${result.centers.name}
            </td>
            <td>
                    ${result.consultants.name}
            </td>
            <td>
                    ${result.orderCount}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="application/javascript" src="/static/common/base.js"></script>
<script type="application/javascript">
    function initChart() {
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();
        var purchasingIdEle = $("#purchasingId");
        var purchasingId = purchasingIdEle.find("option:selected").val();

        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/between/consultantOrderData",
            data: {"endDate": endDate,"startDate": startDate, "purchasingId" : purchasingId},
            dataType: "json",
            success: function (msg) {
                if (!Boolean(msg.ret)) {
                    alert("未查询到数据!");
                    $("#proudctTable").empty();
                    return;
                }else{
                    $("#proudctTable").empty();
                    $.each(msg.AllList, function (key, values) {
                        var ary=values.split("-");
                        $("#proudctTable").append("<tr>"+
                            "<td>"+ary[0]+"</td>"+
                            "<td>"+ary[1]+"</td>"+
                            "<td>"+ary[2]+"</td>"+
                            "</tr>");
                    });
                }
            }
            <%--error: function (XMLHttpRequest, textStatus, errorThrown) {--%>
            <%--alert("未查询到数据!");--%>
            <%--}--%>
        });
    }
    initChart();


</script>
</body>


</html>
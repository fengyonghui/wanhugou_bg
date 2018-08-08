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
        <select class="input-medium" id="consultantId">
            <option value="0" label="全部">全部</option>
            <c:forEach items="${userList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <input onclick="initChart();" class="btn btn-primary" type="button" value="查询"/>

</div>
<input id="showChart" onclick="$Url.go2Url('${adminPath}/biz/statistics/between/customOrder')" class="btn btn-primary" type="button" value="查看图表"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>经销店名称</th>
        <th>采购中心</th>
        <th>采购专员</th>
        <th>订单数量</th>
    </tr>
    </thead>
    <tbody id="proudctTable">
    <c:forEach items="${bizCustomCenterConsultantList}" var="customCenterConsultant">
        <tr>
            <td>
                    ${customCenterConsultant.customs.name}
            </td>
            <td>
                    ${customCenterConsultant.centers.name}
            </td>
            <td>
                    ${customCenterConsultant.consultants.name}
            </td>
            <td>
                    ${customCenterConsultant.orderCount}
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
        var consultantIdEle = $("#consultantId");
        var consultantId = consultantIdEle.find("option:selected").val();

        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/between/customOrderData",
            data: {"endDate": endDate,"startDate": startDate, "variId" : consultantId},
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
                            "<td>"+ary[3]+"</td>"+
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
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
    <title>产品统计</title>
    <script type="application/javascript">
    function initChart() {
        var applyDate = $("#applyDate").val();
        var variIdEle = $("#variId");
        var variId = variIdEle.find("option:selected").val();

        var dataTypeEle = $("#dataType");
        var dataType = dataTypeEle.find("option:selected").val();
        var dataTypeDesc = dataTypeEle.find("option:selected").html();

        var purchasingIdEle = $("#purchasingId");
        var purchasingId = purchasingIdEle.find("option:selected").val();

        $.ajax({
            type: 'GET',
            url: "${adminPath}/biz/statistics/productData",
            data: {"month": applyDate, "variId" : variId, "dataType" : dataType, "purchasingId" : purchasingId},
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
                                       "<td>"+ary[4]+"</td>"+
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
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/statistics/productAnalysisTables">商品分析(个人)</a></li>
</ul>
<div>
    <input name="applyDate" id="applyDate" value="${month}" onchange="initChart();"
           onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <label>
        <select class="input-medium" id="purchasingId">
            <option value="0" label="全部">全部</option>
            <c:forEach items="${purchasingList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <label>
        <select class="input-medium" id="variId">
            <option value="0" label="全部">全部</option>
            <c:forEach items="${varietyList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <label>
        <select class="input-medium" id="dataType">
            <option value="1" label="销售金额">销售金额</option>
            <option value="3" label="销售数量">销售数量</option>
        </select>
    </label>
    <input onclick="initChart();" class="btn btn-primary" type="button" value="查询"/>

</div>
<input id="showChart" onclick="$Url.go2Url('${adminPath}/biz/statistics/product')" class="btn btn-primary" type="button" value="查看图表"/>
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
<script type="application/javascript" src="/static/common/base.js"></script>
</body>


</html>
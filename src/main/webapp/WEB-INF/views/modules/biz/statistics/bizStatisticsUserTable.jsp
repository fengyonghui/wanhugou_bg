<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<head>
    <meta name="decorator" content="default"/>
    <title>客户专员订单统计</title>
</head>
<body>
<div style="height: 30px">
    <input name="applyDate" id="applyDate" value="${month}" onclick="WdatePicker({dateFmt:'yyyy-MM'});" required="required"/>
    <label>
        <select class="input-medium" id="purchasingId">
            <option value="0" label="全部"></option>
            <c:forEach items="${purchasingList}" var="v">
                <option value="${v.id}" label="${v.name}">${v.name}</option>
            </c:forEach>
        </select>
    </label>
    <input id="searchUser" class="btn btn-primary" type="button" value="查询"/>
</div>
<div>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>姓名</th>
            <th>归属部门</th>
            <th>订单量</th>
            <th>销售额</th>
            <th>会员数</th>
        </tr>
        </thead>
        <tbody id="userTable">

        </tbody>
    </table>
</div>
<div>
    <input onclick="window.print();" type="button" class="btn btn-primary" value="打印客户专员订单统计" style="background:#F78181;"/>
</div>
<script type="application/javascript" src="/static/My97DatePicker/WdatePicker.js"></script>
<script type="application/javascript">
    $(document).ready(function () {
        var applyDate = $("#applyDate").val();
        var purchasingId = $("#purchasingId").val();
        $.ajax({
            type: 'post',
            url: "${adminPath}/biz/statistics/centUserTable",
            data: {"month": applyDate,"purchasingId": purchasingId},
            dataType: "json",
            success: function (msg) {
                $("#userTable").empty();
                var userTable = "";
                    $.each(msg,function (index) {
                        userTable += "<tr>";
                        userTable += "<td>"+msg[index].name+"</td>";
                        userTable += "<td>"+msg[index].centName+"</td>";
                        userTable += "<td>"+msg[index].orderCount+"</td>";
                        userTable += "<td>"+msg[index].totalMoney+"</td>";
                        userTable += "<td>"+msg[index].custCount+"</td>";
                        userTable += "</tr>";
                     });
                $("#userTable").append(userTable);
            }
        });
    });
    $("#searchUser").click(function () {
        var applyDate = $("#applyDate").val();
        var purchasingId = $("#purchasingId").val();
        $.ajax({
            type: 'post',
            url: "${adminPath}/biz/statistics/centUserTable",
            data: {"month": applyDate,"purchasingId": purchasingId},
            dataType: "json",
            success: function (msg) {
                $("#userTable").empty();
                var userTable = "";
                $.each(msg,function (index) {
                    userTable += "<tr>";
                    userTable += "<td>"+msg[index].name+"</td>";
                    userTable += "<td>"+msg[index].centName+"</td>";
                    userTable += "<td>"+msg[index].orderCount+"</td>";
                    userTable += "<td>"+msg[index].totalMoney+"</td>";
                    userTable += "<td>"+msg[index].custCount+"</td>";
                    userTable += "</tr>";
                });
                $("#userTable").append(userTable);
            }
        });
    });


</script>
</body>
</html>
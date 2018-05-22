<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>发货单管理</title>
    <meta name="decorator" content="default"/>
</head>
<body>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>采购中心</th>
        <th>SKU名称</th>
        <th>数量</th>
        <th>入库时间</th>
        <th>入库时长(天)</th>
    </tr>
    </thead>
    <tbody>
    <c:set var="nowDate" value="<%=System.currentTimeMillis()%>"></c:set>
    <c:forEach items="${data.resultRecordList}" var="v">
        <tr>
            <td>${v.key.invInfo.name}</td>
            <td>${v.key.skuInfo.name}</td>
            <td>${v.value}</td>
            <td><fmt:formatDate value="${v.key.receiveDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
            <td>
                <fmt:formatNumber value="${(nowDate - v.key.receiveDate.getTime())/1000/60/60/24}" type="number" pattern="#"/>
            </td>
        </tr>
    </c:forEach>
    <div>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
    </tbody>
</table>
</body>
</html>
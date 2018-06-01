<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<html>
<head>
    <title>万户通平台业务数据</title>
    <meta name="decorator" content="default"/>
    <style>
        .table_text_center tr th {
            text-align: center;
            vertical-align:middle;
        }
    </style>
</head>
<body>
<span>
    <form:form id="searchForm" modelAttribute="office" action="${ctx}/biz/statistics/platform/orderCountFrequency" method="post" class="breadcrumb form-search">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="name" name="name" type="text" value="${office.name}" />
        <label>
        <select class="input-medium" id="purchasingId" name="centerId">
            <option value="0" label="全部"></option>
            <c:forEach items="${purchasingList}" var="v">
                <c:if test="${v.id == officeId}">
                    <option selected value="${v.id}" label="${v.name}">${v.name}</option>
                </c:if>
                <c:if test="${v.id != officeId}">
                    <option value="${v.id}" label="${v.name}">${v.name}</option>
                </c:if>
            </c:forEach>
        </select>
        <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
    </label>
    </form:form>

</span>
<table id="contentTable" class="table table-bordered table-condensed table_text_center">
    <thead>
    <tr>
        <th>经销店</th>
        <th>平均每月订单量</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${dataMap}" var="dataName">
        <tr>
            <td>${dataName.key}</td>
            <td>${dataName.value}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
<script type="text/javascript">
    function page(n,s){
        $("#pageNo").val(n);
        $("#searchForm").submit();
        return false;
    }
</script>
</body>
</html>
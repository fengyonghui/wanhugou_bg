<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>仓库信息管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function() {

        });
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/shelf/biz/shelf/bizOpShelfInfo/shelfManagementList?id=${bizOpShelfInfo.id}">货架管理员列表</a></li>
    <shiro:hasPermission name="biz:inventory:bizInventoryInfo:edit"><li><a href="${ctx}/biz/shelf/bizOpShelfInfo/shelfManagementForm?id=${bizOpShelfInfo.id}">货架管理员添加</a></li></shiro:hasPermission>
</ul>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>货架名称</th>
        <th>管理员</th>
        <shiro:hasPermission name="biz:inventory:bizInventoryInfo:edit"><th>操作</th></shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <%--<c:forEach items="${page.list}" var="bizOpShelfInfo">--%>
        <c:forEach items="${bizShelfUserList}" var="bizShelfUser">
            <tr>
                <td>
                        <%--<a href="${ctx}/biz/inventory/bizInventoryInfo/form?id=${bizInventoryInfo.id}">--%>
                        ${bizShelfUser.shelfInfo.name}
                    <%--</a>--%>
                </td>
                <td>
                        ${bizShelfUser.user.name}
                </td>
                <shiro:hasPermission name="biz:inventory:bizInventoryInfo:edit"><td>
                        <%--<a href="${ctx}/biz/shelf/bizOpShelfInfo/shelfManagementForm?id=${bizOpShelfInfo.id}">修改</a>--%>
                        <a href="${ctx}/biz/shelf/bizOpShelfInfo/deleteShelfUser?id=${bizShelfUser.id}" onclick="return confirmx('确认要删除该货架管理员吗？', this.href)">删除</a>
                </td></shiro:hasPermission>
            </tr>
        </c:forEach>
    <%--</c:forEach>--%>
    </tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
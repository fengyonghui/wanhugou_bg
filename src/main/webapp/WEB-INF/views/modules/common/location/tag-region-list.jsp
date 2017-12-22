<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>地区管理</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/treetable.jsp" %>
    <script type="text/javascript">
        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
        $(document).ready(function () {
            $(".the-icons").click(function () {
                $("#code").val($(this).text());
                $("#id").val($(this).next().text());
                var currentName = $(this).next().next().text();
//                if ("县".equals(currentName) || "市辖区".equals(currentName)){
//                    currentName = "";
//                }
                var fullName = "${regionNames}" + currentName;

                $("#name").val(fullName);
                $("#selectedId").val($(this).parent().parent().attr("id"));
            });
            $(".the-icons").dblclick(function () {
                top.$.jBox.getBox().find("button[value='ok']").trigger("click");
            });

            $("th").css({
                "vertical-align":"middle",
                "text-align":"center"
            });
        });
    </script>
</head>
<body>
<input type="hidden" id="code" value="${value}"/>
<input type="hidden" id="id" value="${value}"/>
<input type="hidden" id="name" value="${value}"/>
<input type="hidden" id="selectedId" value="${value}"/>

<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/sys/sysRegion/tag/selectRegion">地区列表</a>
    </li>
    <c:if test="${pCode!=0 }">
        <li><a href="${ctx}/sys/sysRegion/tag/selectRegion?pCode=${oldId}&regionNames=${oldName}">返回</a></li>
    </c:if>
</ul>
<font color="red">双击地区编码选中</font>
<h3 class="id">当前区域: ${regionNames}</h3>
<div id="icons">
    <table id="treeTable"
           class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>地区名称<font color="red">(点击名称显示下级地区)</font></th>
            <th>地区编码</th>
            <%--<th>地区级别</th>--%>
        </tr>
        </thead>
        <c:forEach items="${list}" var="rgregion">
        <tr id="${rgregion.id }">
            <td>
                <c:if test="${rgregion.level != 'dist'}">
                    <a href="${ctx}/sys/sysRegion/tag/selectRegion?pCode=${rgregion.code}&oldId=${pCode}&regionNames=${regionNames} &oldName=${oldName}">${rgregion.name}</a>
                </c:if>
                <c:if test="${rgregion.level == 'dist'}">
                    ${rgregion.name}
                </c:if>
            </td>
            <td>
                <li class="the-icons btn">${rgregion.code}</li>
                <li style="display: none;">${rgregion.id }</li>
                <li style="display: none;">${rgregion.name }</li>
            </td>
                <%--<td>${fns:getDictLabels(rgregion.level,'area_level','') }</td>--%>
            </c:forEach>
        </tr>
    </table>
</div>
</body>
</html>
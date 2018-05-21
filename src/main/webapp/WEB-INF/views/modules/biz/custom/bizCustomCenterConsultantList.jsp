<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>客户专员管理</title>
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
    <li class="active"><a
            href="${ctx}/biz/custom/bizCustomCenterConsultant/list?consultants.id=${bcUser.consultants.id}">经销店列表</a>
    </li>
    <li>
        <a href="${ctx}/biz/custom/bizCustomCenterConsultant/connOfficeForm?id=${bcUser.consultants.id}&office.id=${bcUser.centers.id}">经销店添加</a>
    </li>
    <%--?centers.id=${page.centers.id}&consultants.id=${page.consultants.id} <shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:edit"><li><a href="${ctx}/biz/custom/bizCustomCenterConsultant/form">客户专员添加</a></li></shiro:hasPermission>--%>
</ul>
<form:form id="searchForm" modelAttribute="bizCustomCenterConsultant"
           action="${ctx}/biz/custom/bizCustomCenterConsultant/" method="post" class="breadcrumb form-search">
    <ul class="ul-form">
        <li><label>经销店名称：</label>
            <sys:treeselect id="customs" name="customs.id" value="${entity.customs.id}" labelName="customs.name"
                            labelValue="${entity.customs.name}" notAllowSelectParent="true"
                            title="经销店" url="/sys/office/queryTreeList?type=6" cssClass="input-medium required"
                            allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
            <c:if test="${bizCustomCenterConsultant.queryCustomes!=null && bizCustomCenterConsultant.queryCustomes eq 'query_Custome'}">
                <input type="hidden" name="consultants.id" value="${bcUser.consultants.id}">
                <input type="hidden" name="queryCustomes" value="query_Custome">
            </c:if>
            <c:if test="${empty bizCustomCenterConsultant.queryCustomes}">
                <input type="hidden" name="consultants.id" value="${bizCustomCenterConsultant.consultants.id}">
                <input type="hidden" name="queryCustomes" value="query_Custome">
            </c:if>
        </li>
        <li><label>联系电话：</label>
            <form:input path="consultants.mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
        </li>
            <%--<li><label>采购中心：</label>--%>
            <%--<sys:treeselect id="centers" name="centers.id" value="${entity.centers.id}" labelName="centers.name"--%>
            <%--labelValue="${entity.centers.name}" notAllowSelectRoot="true" notAllowSelectParent="true"--%>
            <%--title="采购中心" url="/sys/office/queryTreeList?type=8" cssClass="input-medium required"--%>
            <%--allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>--%>
            <%--</li>--%>
            <%--&lt;%&ndash;<li><label>客户专员：</label>&ndash;%&gt;--%>
            <%--&lt;%&ndash;<sys:treeselect id="consultants" name="consultants.id" value="${entity.consultants.id}" labelName="consultants.name"&ndash;%&gt;--%>
            <%--&lt;%&ndash;labelValue="${entity.consultants.name}" notAllowSelectRoot="true" notAllowSelectParent="true"&ndash;%&gt;--%>
            <%--&lt;%&ndash;title="客户专员" url="/sys/office/queryTreeList?type=1" cssClass="input-medium required"&ndash;%&gt;--%>
            <%--&lt;%&ndash;allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>&ndash;%&gt;--%>
            <%--&lt;%&ndash;</li>&ndash;%&gt;--%>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回"
                                onclick="javascript:history.go(-1);"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>经销店名称</th>
        <th>电话</th>
        <th>采购中心</th>
        <th>客户专员</th>
        <%--<th>创建时间</th>--%>
        <%--<th>创建人</th>--%>
        <%--<th>更新时间</th>--%>
        <%--<th>修改人</th>--%>
        <shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.bccList}" var="bizCustomCenterConsultant">
        <tr>
            <td>
                    ${bizCustomCenterConsultant.customs.name}
            </td>
            <td>
                    ${bizCustomCenterConsultant.consultants.mobile}
            </td>
            <td>
                    ${bizCustomCenterConsultant.centers.name}
            </td>
            <td>
                    ${bizCustomCenterConsultant.consultants.name}
            </td>
                <%--<td>--%>
                <%--<fmt:formatDate value="${bizCustomCenterConsultant.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>--%>
                <%--</td>--%>
                <%--<td>--%>
                <%--${bizCustomCenterConsultant.createBy.name}--%>
                <%--</td>--%>
                <%--<td>--%>
                <%--<fmt:formatDate value="${bizCustomCenterConsultant.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>--%>
                <%--</td>--%>
                <%--<td>--%>
                <%--${bizCustomCenterConsultant.updateBy.name}--%>
                <%--</td>--%>
            <shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:edit">
                <td>
                        <%--<a href="${ctx}/biz/custom/bizCustomCenterConsultant/delete?customs.id=${bizCustomCenterConsultant.id}" onclick="return confirmx('确认要删除该客户专员吗？', this.href)">移除</a>--%>
                    <a href="${ctx}/biz/custom/bizCustomCenterConsultant/delete?customs.id=${bizCustomCenterConsultant.customs.id}&consultants.id=${bizCustomCenterConsultant.consultants.id}"
                       onclick="return confirmx('确认要移除该关联信息吗？', this.href)">移除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<%--<div class="pagination">${page}</div>--%>
</body>
</html>
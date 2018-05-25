<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>动态配置文件管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
    </script>
</head>
<body>
<shiro:hasPermission name="config:commonDynamicConfig:edit">
<div class="up_top_l clearfix">
    <input type="file" id="myFile" name="file" class="upfile">
    <input type="button" class="btn" onclick="uploadFile()" value="上传文件"/>
</div>
</shiro:hasPermission>
<br/>
<br/>
<form:form id="searchForm" modelAttribute="commonDynamicConfig" action="${ctx}/config/commonDynamicConfig/"
           method="post" class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>名称：</label>
            <form:input path="confname" htmlEscape="false" maxlength="256" class="input-medium"/>
        </li>
        <li><label>创建时间：</label>
            <input name="created" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${commonDynamicConfig.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
        </li>
        <li><label>版本号：</label>
            <form:input path="version" htmlEscape="false" maxlength="11" class="input-medium"/>
        </li>
        <li><label>更新时间：</label>
            <input name="updated" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${commonDynamicConfig.updated}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
        </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>配置文件名称</th>
        <th>创建时间</th>
        <th>状态</th>
        <th>版本号</th>
        <th>更新时间</th>
        <shiro:hasPermission name="config:commonDynamicConfig:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="commonDynamicConfig">
        <tr>
            <td>
                    ${commonDynamicConfig.confname}
            </td>
            <td>
                <fmt:formatDate value="${commonDynamicConfig.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                    ${fns:getDictLabel(commonDynamicConfig.status, 'status', '')}
            </td>
            <td>
                    ${commonDynamicConfig.version}
            </td>
            <td>
                <fmt:formatDate value="${commonDynamicConfig.updated}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                <a href="${ctx}/config/commonDynamicConfig/download?fileName=${commonDynamicConfig.confname}&version=${commonDynamicConfig.version}">下载</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
<script type="text/javascript">

    /**
     * 上传热部署文件
     */
    function uploadFile() {
        $.ajaxFileUpload({
            url : '${ctx}/config/commonDynamicConfig/upload', //用于文件上传的服务器端请求地址
            secureuri : false, //一般设置为false
            fileElementId : 'myFile', //文件上传空间的id属性  <input type="file" id="file" name="file" />
            type : 'POST',
            dataType : 'text', //返回值类型 一般设置为json
            success : function(data, status) {
                var msg = data.substring(data.indexOf("{"), data.indexOf("}")+1);
                var data = JSON.parse(msg);
                if (data.result) {
                    alert("上传成功");
                }else {
                    alert("上传失败");
                }
            },
            error : function(data, status, e) {
                //服务器响应失败处理函数
                console.info(data);
                console.info(status);
                console.info(e);
                alert("上传失败");
            }
        });
    }
</script>
</body>
</html>
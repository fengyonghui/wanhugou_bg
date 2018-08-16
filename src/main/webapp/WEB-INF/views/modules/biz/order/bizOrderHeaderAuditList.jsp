<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>

<html>
<head>
    <meta name="decorator" content="default"/>
</head>
<body>
<ul class="nav nav-tabs">
    <li>
        <a>审核列表</a>
    </li>
    <li><input type="button" onclick="window.history.go(-1)" value="返回"></li>
</ul>
<br/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <input type="hidden" id="id" value="${id}">
        <input type="hidden" id="type" value="${type}">
        <th>流程</th>
        <th>审批人</th>
        <th>批注</th>
        <th>是否当前状态</th>
        <th>时间</th>
        <th>审核</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${list}" var="v">
        <tr>
            <td>
                <c:if test="${type == 1}">
                    ${v.jointOperationLocalProcess.name}
                </c:if>
                <c:if test="${type == 0}">
                    ${v.jointOperationOriginProcess.name}
                </c:if>
            </td>
            <td>
                    ${v.user.name}
            </td>
            <td>
                    ${v.description}
            </td>
            <td>
                   <c:if test="${v.current == 1}">是</c:if>
                   <c:if test="${v.current == 0}">否</c:if>
            </td>
            <td>
                <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                <c:if test="${v.current == 1 && v.type != -1 && v.type != 666 && v.type != 777}">
                <a onclick="checkPass(${v.type})">通过</a>
                <a onclick="checkReject(${v.type})">驳回</a>
                </c:if>
            </td>

        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="text/javascript">
    function checkPass(currentType) {
        var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认审核通过吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    audit(currentType, 1, f.description);
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入通过理由:", submit: submit, loaded: function (h) {
            }
        });

    }

    function checkReject(currentType) {
        var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认驳回该流程吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    audit(currentType, 2, f.description);
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入驳回理由:", submit: submit, loaded: function (h) {
            }
        });

    }

    function audit(currentType, auditType, description) {
        var id = $("#id").val();
        var orderType = $("#type").val();
        $.ajax({
            url: '${ctx}/biz/order/bizOrderHeader/audit',
            contentType: 'application/json',
            data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description, "orderType": orderType},
            type: 'get',
            success: function (result) {
                result = JSON.parse(result);
                if(result.ret == true || result.ret == 'true') {
                    alert('操作成功!');
                    window.history.go(-1);
                }else {
                    alert(result.errmsg);
                }
            },
            error: function (error) {
                console.info(error);
            }
        });
    }
</script>
</body>
</html>

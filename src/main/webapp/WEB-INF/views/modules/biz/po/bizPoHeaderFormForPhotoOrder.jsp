<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>采购订单管理</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">
        $(document).ready(function () {

        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/po/bizPoHeader/">订单详情</a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/po/bizPoHeader/saveForPhotoOrder"
           method="post" class="form-horizontal">
        <input type="hidden" value="${bizOrderHeader.id}" path="id" id="id"/>
        <div class="control-group">
            <label class="control-label">订单总金额：</label>
            <div class="controls">
                    <input type="text" style="margin-bottom: 10px" readonly="readonly" value="${bizOrderHeader.totalDetail + bizOrderHeader.freight}"
                           htmlEscape="false" maxlength="30" class="input-xlarge "/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">订单收货地址：</label>
            <div class="controls">
                    <input type="text" style="margin-bottom: 10px" readonly="readonly" value="${bizOrderAddress.pcrName}${bizOrderAddress.address}"
                           htmlEscape="false" maxlength="30" class="input-xlarge "/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">收货人：</label>
            <div class="controls">
                    <input type="text" style="margin-bottom: 10px" readonly="readonly" value="${bizOrderAddress.receiver}"
                           htmlEscape="false" maxlength="30" class="input-xlarge "/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">收货人手机：</label>
            <div class="controls">
                    <input type="text" style="margin-bottom: 10px" readonly="readonly" value="${bizOrderAddress.phone}"
                           htmlEscape="false" maxlength="30" class="input-xlarge "/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">供应商：</label>
            <div class="controls">
                    <input type="text" style="margin-bottom: 10px" readonly="readonly" value="${vendor.name}"
                           htmlEscape="false" maxlength="30" class="input-xlarge "/>
            </div>
        </div>

    <div class="control-group">
        <label class="control-label">交货地点：</label>
        <div class="controls">
            <input type="radio"  name="deliveryStatus" value="0" checked="true" />采购中心
            <input type="radio"  name="deliveryStatus" value="1" />供应商
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">供应商卡号：</label>
        <div class="controls">
            <input type="text" readonly="readonly" value="${vendOffice.cardNumber}" htmlEscape="false" maxlength="30"
                        class="input-xlarge "/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">供应商收款人：</label>
        <div class="controls">
            <input type="text" readonly="readonly" value="${vendOffice.payee}" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">供应商开户行：</label>
        <div class="controls">
            <input type="text" readonly="readonly" value="${vendOffice.bankName}" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">供应商合同：</label>
        <div class="controls">
            <c:forEach items="${compactImgList}" var="v">
                <a href="${v.imgServer}${v.imgPath}" target="_blank"><img width="100px" src="${v.imgServer}${v.imgPath}"></a>
            </c:forEach>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">供应商身份证：</label>
        <div class="controls">
            <c:forEach items="${identityCardImgList}" var="v">
                <a href="${v.imgServer}${v.imgPath}" target="_blank"><img width="100px" src="${v.imgServer}${v.imgPath}"></a>
            </c:forEach>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">订单图片：</label>
        <div class="controls">
            <td></td>
            <c:forEach items="${imgList}" var="v">
                <a href="${v.imgServer}${v.imgPath}" target="_blank"><img style="width: 100px; height: auto" src="${v.imgServer}${v.imgPath}"/></a>
            </c:forEach>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">采购单最后付款时间：</label>
        <div class="controls">
            <input id="lastPayDate" name="lastPayDate" type="text" readonly="readonly" maxlength="20"
                   class="input-medium Wdate required"
                   value="<fmt:formatDate value="${bizPoHeader.lastPayDate}"  pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" placeholder="必填！"/>
        </div>
    </div>


    <div class="form-actions">
        <shiro:hasPermission name="biz:po:bizPoHeader:edit">
            <c:if test="${bizPoHeader.poDetailList==null}">
                <input id="btnSubmit" type="button" onclick="genPoOrder()" class="btn btn-primary" value="生成采购单"/>
            </c:if>
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
<script type="text/javascript">
    function genPoOrder() {
        var id = $("#id").val();
        var lastPayDate = $("#lastPayDate").val();
        if ($String.isNullOrBlank(lastPayDate)) {
            alert("请选择最后付款日期");
            return;
        }

        var radio = document.getElementsByName("deliveryStatus");
        var deliveryStatus = 0;
        for (i=0; i<radio.length; i++) {
            if (radio[i].checked) {
                deliveryStatus = (radio[i].value)
            }
        }
        window.location.href = "${ctx}/biz/po/bizPoHeader/saveForPhotoOrder?orderHeaderId=" + id + "&deliveryStatus=" + deliveryStatus + "&lastPayDate=" + lastPayDate;
    }

</script>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<html>
<head>
    <title>订单信息管理</title>
    <script type="text/javascript" src="${ctxStatic}/tablesMergeCell/tablesMergeCell.js"></script>
    <meta name="decorator" content="default"/>
    <style type="text/css">
        .help_step_box{background: rgba(255, 255, 255, 0.45);overflow:hidden;border-top:1px solid #FFF;width: 100%}
        .help_step_item{margin-right: 30px;width:200px;border:1px #3daae9 solid;float:left;height:150px;padding:0 25px 0 45px;cursor:pointer;position:relative;font-size:14px;font-weight:bold;}
        .help_step_num{width:19px;height:120px;line-height:100px;position:absolute;text-align:center;top:18px;left:10px;font-size:16px;font-weight:bold;color: #239df5;}
        .help_step_set{background: #FFF;color: #3daae9;}
        .help_step_set .help_step_left{width:8px;height:100px;position:absolute;left:0;top:0;}
        .help_step_set .help_step_right{width:8px;height:100px; position:absolute;right:-8px;top:0;}
    </style>
    <script type="text/javascript">
        <%--用于页面按下键盘Backspace键回退页面的问题--%>
        <%--处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外   --%>
        function banBackSpace(e){
            var ev = e || window.event;<%--获取event对象--%>
            var obj = ev.target || ev.srcElement;<%--获取事件源--%>
            var t = obj.type || obj.getAttribute('type');<%--获取事件源类型--%>
            <%--获取作为判断条件的事件类型--%>
            var vReadOnly = obj.getAttribute('readonly');
            var vEnabled = obj.getAttribute('enabled');
            <%--处理null值情况--%>
            vReadOnly = (vReadOnly == null) ? false : vReadOnly;
            vEnabled = (vEnabled == null) ? true : vEnabled;
            <%--当敲Backspace键时，事件源类型为密码或单行、多行文本的--%>
            <%--并且readonly属性为true或enabled属性为false的，则退格键失效--%>
            var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea")
                && (vReadOnly==true || vEnabled!=true))?true:false;
            <%--当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效--%>
            var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")
                ?true:false;
            <%--判断--%>
            if(flag2){
                return false;
            }
            if(flag1){
                return false;
            }
        }
        <%--禁止后退键 作用于Firefox、Opera--%>
        document.onkeypress=banBackSpace;
        <%--禁止后退键 作用于IE、Chrome--%>
        document.onkeydown=banBackSpace;
    </script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#contentTable").tablesMergeCell({
                automatic: true,
                // 是否根据内容来合并
                cols: [0]
                // rows:[0,2]
            });
            $("#name").focus();
        });

    </script>

    <script type="text/javascript">
        function saveMon(type) {
            var orderIds = '${entity.orderIds}'
            var payTotal = $("#payTotal").val();
            var lastPayDate = $('#lastPayDate').val();
            var remark = $("#remark").val();
            var sellerId = '${entity.sellerId}';
            var id = '${entity.id}'
            if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
                alert("申请金额不正确!");
                return false;
            }
            if ($String.isNullOrBlank(lastPayDate)) {
                alert("请选择最后付款时间!");
                return false;
            }

            $Mask.AddLogo("正在加载");
            window.location.href="${ctx}/biz/order/bizCommissionOrder/saveCommission?totalCommission=" + payTotal + "&deadline=" + lastPayDate
                 + "&remark=" + remark + "&orderIds=" + orderIds + "&sellerId=" + sellerId;
        }
    </script>

</head>
<body>
<ul class="nav nav-tabs">
    <c:if test="${bizOrderHeader.flag=='check_pending'}">
        <li>
            <a href="${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}&source=${source}">订单信息列表</a>
        </li>
    </c:if>
    <c:if test="${empty bizOrderHeader.flag}">
        <c:if test="${empty bizOrderHeader.clientModify}">
            <li><a href="${ctx}/biz/order/bizOrderHeader?source=${source}">订单信息列表</a></li>
        </c:if>
        <c:if test="${bizOrderHeader.clientModify eq 'client_modify'}">
            <li><a href="${ctx}/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=${bizOrderHeader.consultantId}&source=${source}">订单信息列表</a></li>
        </c:if>
    </c:if>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizCommission" action="${ctx}/biz/order/bizOrderHeader/save?statuPath=${statuPath}" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <input type="hidden" name="sellerId" value="${entity.sellerId}" />
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">订单总价：</label>
        <div class="controls">
            <form:input path="totalDetail" readonly="readonly" placeholder="由系统自动生成" htmlEscape="false" maxlength="30" class="input-xlarge"/>
        </div>
    </div>

    <div id="cardNumber" class="control-group" >
        <label class="control-label">零售商卡号：</label>
        <div class="controls">
            <input id="cardNumberInput" readonly="readonly" value="${entity.customerInfo.cardNumber}" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>
    <div id="payee" class="control-group" >
        <label class="control-label">零售商收款人：</label>
        <div class="controls">
            <input id="payeeInput" readonly="readonly" value="${entity.customerInfo.payee}" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>
    <div id="bankName" class="control-group" >
        <label class="control-label">零售商开户行：</label>
        <div class="controls">
            <input id="bankNameInput" readonly="readonly" value="${entity.customerInfo.bankName}" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">申请金额：</label>
        <div class="controls">
            <input id="payTotal" name="planPay" type="text" readonly="readonly"
                   value="${entity.totalCommission}"
                   htmlEscape="false" maxlength="30" class="input-xlarge"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">最后付款时间：</label>
        <div class="controls">
            <input name="lastPayDate" id="lastPayDate" type="text" readonly="readonly"
                   maxlength="20"
                   class="input-medium Wdate required"
                   value="<fmt:formatDate value="${entity.deadline}"  pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
                   placeholder="必填！"/>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">备注：</label>
        <div class="controls">
            <form:textarea path="remark" maxlength="200" class="input-xlarge "/>
        </div>
    </div>

    <c:if test="${fn:length(auditList) > 0}">
        <div class="control-group">
            <label class="control-label">审核流程：</label>
            <div class="controls help_wrap">
                <div class="help_step_box fa">
                    <c:forEach items="${auditList}" var="v" varStatus="stat">
                        <c:if test="${v.current != 1}" >
                            <div class="help_step_item">
                                <div class="help_step_left"></div>
                                <div class="help_step_num">${stat.index + 1}</div>
                                处理人:${v.user.name}<br/><br/>
                                批注:${v.description}<br/><br/>
                                状态:
                                    ${v.paymentOrderProcess.name}
                                <br/>
                                <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                <div class="help_step_right"></div>
                            </div>
                        </c:if>
                        <c:if test="${v.current == 1}">
                            <div class="help_step_item help_step_set">
                                <div class="help_step_left"></div>
                                <div class="help_step_num">${stat.index + 1}</div>
                                当前状态:
                                    ${v.paymentOrderProcess.name}
                                <br/>
                                <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                <div class="help_step_right"></div>
                                <input type="hidden" value="${v.type}" id="currentJoType"/>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div>
    </c:if>


    <c:choose>
        <c:when test="${bizOrderHeader.flag=='check_pending'}">


        </c:when>
        <c:otherwise>
            <div class="form-actions">
                <!-- 一单到底订单审核 -->
                <shiro:hasPermission name="biz:order:bizOrderHeader:audit">
                    <c:if test="${entity.str == 'audit' && entity.orderType == BizOrderTypeEnum.PURCHASE_ORDER.state}">
                        <c:if test="${entity.bizPoHeader.commonProcessList == null}">
                            <input id="btnSubmit" type="button" onclick="checkPass('DO')" class="btn btn-primary"
                                   value="审核通过"/>
                            <input id="btnSubmit" type="button" onclick="checkReject('DO')" class="btn btn-primary"
                                   value="审核驳回"/>
                        </c:if>
                    </c:if>
                    <%--<c:if test="${entity.str == 'audit' && type != 0 && type != 1}">--%>
                        <%--<c:if test="${entity.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state}">--%>
                            <%--<c:if test="${entity.bizPoHeader.commonProcessList == null}">--%>
                                <%--<input id="btnSubmit" type="button" onclick="checkPass('SO')" class="btn btn-primary"--%>
                                       <%--value="审核通过"/>--%>
                                <%--<input id="btnSubmit" type="button" onclick="checkReject('SO')" class="btn btn-primary"--%>
                                       <%--value="审核驳回"/>--%>
                            <%--</c:if>--%>
                        <%--</c:if>--%>
                    <%--</c:if>--%>

                    <c:if test="${entity.str == 'audit' && (type != 0 || type != 1)}">
                        <c:if test="${(entity.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state || entity.orderType == BizOrderTypeEnum.COMMISSION_ORDER.state) && currentAuditStatus.type != 777 && currentAuditStatus.type != 666}">
                                <input type="button" onclick="checkPass('JO')" class="btn btn-primary"
                                       value="通过"/>
                                <input type="button" onclick="checkReject('JO')" class="btn btn-primary"
                                       value="驳回"/>
                        </c:if>
                    </c:if>

                    <c:if test="${entity.str == 'pay'}">
                        <input id="btnSubmit" type="button" onclick="pay()" class="btn btn-primary" value="确认支付"/>
                    </c:if>

                    <%--<c:if test="${entity.str == 'createPay'}">--%>
                        <%--<input id="btnSubmit" type="button" onclick="saveMon('createPay')" class="btn btn-primary" value="申请付款"/>--%>
                    <%--</c:if>--%>
                </shiro:hasPermission>

                    <!-- 一单到底，采购单审核 -->
                <shiro:hasPermission name="biz:po:bizPoHeader:audit">
                    <c:if test="${entity.str == 'audit'}">
                    <c:if test="${orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                        <c:if test="${entity.bizPoHeader.commonProcessList != null
                        && fn:length(entity.bizPoHeader.commonProcessList) > 0
                        && (currentAuditStatus.type == 777 || currentAuditStatus.type == 666)
                        }">
                            <input id="btnSubmit" type="button" onclick="checkPass('PO')" class="btn btn-primary"
                                   value="审核通过"/>
                            <input id="btnSubmit" type="button" onclick="checkReject('PO')" class="btn btn-primary"
                                   value="审核驳回"/>
                        </c:if>
                    </c:if>

                    <c:if test="${orderType == DefaultPropEnum.PURSEHANGER.propValue}">
                        <c:if test="${entity.bizPoHeader.commonProcessList != null
                        && fn:length(entity.bizPoHeader.commonProcessList) > 0
                        }">
                            <input id="btnSubmit" type="button" onclick="checkPass('PO')" class="btn btn-primary"
                                   value="审核通过"/>
                            <input id="btnSubmit" type="button" onclick="checkReject('PO')" class="btn btn-primary"
                                   value="审核驳回"/>
                        </c:if>
                    </c:if>
                    </c:if>
                </shiro:hasPermission>

                <c:if test="${option != 'detail'}">
                    <input id="btnSubmit" type="button" onclick="saveMon('createPay')" class="btn btn-primary" value="申请付款"/>
                </c:if>
                &nbsp;&nbsp;&nbsp;
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
            </div>
        </c:otherwise>
    </c:choose>
</form:form>

<%--详情列表--%>
<sys:message content="${message}"/>
<c:if test="${fn:length(orderHeaderList) > 0}">
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>订单号</th>
        <th>商品名称</th>
        <th>商品编号</th>
        <th>商品货号</th>
        <th>采购数量</th>
        <th>总 额</th>
        <th>已发货数量</th>
        <th>发货方</th>
        <th>商品零售价</th>
        <th>佣金</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${orderHeaderList}" var="bizOrderHeader">
        <c:forEach items="${bizOrderHeader.orderDetailList}" var="bizOrderDetail">
            <tr>
                <td>
                        ${bizOrderHeader.orderNum}
                </td>
                <td>
                        ${bizOrderDetail.skuName}
                </td>
                <td>
                        ${bizOrderDetail.partNo}
                </td>
                <td>
                        ${bizOrderDetail.skuInfo.itemNo}
                </td>
                <td>
                        ${bizOrderDetail.ordQty}
                </td>
                <td>
                    <c:if test="${bizOrderDetail.unitPrice !=null && bizOrderDetail.ordQty !=null}">
                        <fmt:formatNumber type="number" value=" ${bizOrderDetail.unitPrice * bizOrderDetail.ordQty}" pattern="0.00"/>
                    </c:if>
                </td>
                <td>
                        ${bizOrderDetail.sentQty}
                </td>
                <td>
                        ${bizOrderDetail.suplyis.name}
                </td>
                <td>
                        ${bizOrderDetail.unitPrice}
                </td>
                <td>
                        ${bizOrderDetail.detailCommission}
                </td>
            </tr>
        </c:forEach>
    </c:forEach>
    </tbody>
</table>
</c:if>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.BizOrderSchedulingEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>采购订单管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#buttonBack").click(function () {
                history.go(-1);
            });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/po/bizPoHeader/listV3">付款单</a></li>
</ul>
<sys:message content="${message}"/>
<input id="fromPage" name="fromPage" type="hidden" value="${fromPage}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <td>序号</td>
        <th>订单/备货单号</th>
        <th>供应商</th>
        <th>采购总价</th>
        <%--<th>交易费用</th>--%>
        <th>应付金额</th>
        <%--<th>支付比例</th>--%>
        <th>订单支出状态</th>
        <%--<th>采购单来源</th>--%>
        <th>创建时间</th>
        <th>累积支付金额</th>
        <th>订单支出审核状态</th>
        <th>排产状态</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${listBizPoHeader}" var="bizPoHeader" varStatus="state">
        <tr>
            <td>${state.index+1}</td>
            <td>
                <c:choose>
                    <c:when test="${bizPoHeader.bizOrderHeader != null}">
                        <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizPoHeader.bizOrderHeader.id}&orderDetails=details">${bizPoHeader.bizOrderHeader.orderNum}</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizPoHeader.bizRequestHeader.id}&str=detail">${bizPoHeader.bizRequestHeader.reqNo}</a>
                    </c:otherwise>
                </c:choose>
                <c:if test="">

                </c:if>
            </a></td>
            <td>
                    ${bizPoHeader.vendOffice.name}
            </td>
            <td>
                    ${bizPoHeader.totalDetail}
            </td>
                <%--<td>--%>
                <%--${bizPoHeader.totalExp}--%>
                <%--</td>--%>
            <td>
                    ${bizPoHeader.totalDetail+bizPoHeader.totalExp}
            </td>
            <td>
                    ${fns:getDictLabel(bizPoHeader.bizStatus, 'biz_po_status', '未知类型')}
            </td>
            <td>
                <fmt:formatDate value="${bizPoHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <td>
                    ${bizPoHeader.payTotal}
            </td>
            <td>
                    ${bizPoHeader.commonProcess.purchaseOrderProcess.name == null ?
                            '当前无审批流程' : bizPoHeader.commonProcess.purchaseOrderProcess.name}
            </td>
            <td>
                <c:choose>
                    <c:when test="${bizPoHeader.poSchType == 0 || bizPoHeader.poSchType == null}">
                        ${BizOrderSchedulingEnum.SCHEDULING_NOT.desc}
                    </c:when>
                    <c:otherwise>
                        <c:if test="${bizPoHeader.poSchType == 1}">
                            ${BizOrderSchedulingEnum.SCHEDULING_PLAN.desc}
                        </c:if>
                        <c:if test="${bizPoHeader.poSchType == 2}">
                            ${BizOrderSchedulingEnum.SCHEDULING_DONE.desc}
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </td>
            <shiro:hasPermission name="biz:po:bizPoHeader:view">
                <td>
                    <c:if test="${bizPoHeader.bizStatus != 10}">
                        <c:choose>
                            <c:when test="${bizPoHeader.bizOrderHeader != null or bizPoHeader.bizRequestHeader != null}">
                                <shiro:hasPermission name="biz:request:bizOrderHeader:createPayOrder">
                                    <c:if test="${bizPoHeader.bizOrderHeader != null}">
                                        <c:if test="${bizPoHeader.currentPaymentId == null
												&& bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'
												&& (bizPoHeader.payTotal == null ? 0 : bizPoHeader.payTotal) < bizPoHeader.bizOrderHeader.totalDetail
												}">
                                            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizPoHeader.bizOrderHeader.id}&str=createPay">申请付款</a>
                                        </c:if>
                                    </c:if>
                                </shiro:hasPermission>
                                <shiro:hasPermission name="biz:request:bizRequestHeader:createPayOrder">
                                    <c:if test="${bizPoHeader.bizRequestHeader != null}">
                                        <c:if test="${bizPoHeader.currentPaymentId == null
												&& bizPoHeader.bizRequestHeader.bizStatus >= ReqHeaderStatusEnum.APPROVE.state
												&& bizPoHeader.bizRequestHeader.bizStatus < ReqHeaderStatusEnum.VEND_ALL_PAY.state
												&& bizPoHeader.payTotal < bizPoHeader.bizRequestHeader.totalDetail
												}">
                                            <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizPoHeader.bizRequestHeader.id}&str=createPay">申请付款</a>
                                        </c:if>
                                    </c:if>
                                </shiro:hasPermission>
                            </c:when>
                            <c:otherwise>
                                <shiro:hasPermission name="biz:po:bizPoHeader:createPayOrder">
                                    <c:if test="${bizPoHeader.bizPoPaymentOrder.id == null
											&& bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'
											&& fns:getDictLabel(bizPoHeader.bizStatus, 'biz_po_status', '未知类型') != '全部支付'
											&& bizPoHeader.payTotal < (bizPoHeader.totalDetail+bizPoHeader.totalExp)
											}">
                                        <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=createPay">申请付款</a>
                                    </c:if>
                                </shiro:hasPermission>
                            </c:otherwise>
                        </c:choose>
                        <shiro:hasPermission name="biz:po:bizPoHeader:audit">
                            <c:if test="${bizPoHeader.commonProcess.id != null
									&& bizPoHeader.commonProcess.purchaseOrderProcess.name != '驳回'
									&& bizPoHeader.commonProcess.purchaseOrderProcess.name != '审批完成'
									&& bizPoHeader.commonProcess.purchaseOrderProcess.code != payStatus
									&& (fns:hasRole(roleSet, bizPoHeader.commonProcess.purchaseOrderProcess.roleEnNameEnum) || fns:getUser().isAdmin())
									}">
                                <c:choose>
                                    <c:when test="${bizPoHeader.bizOrderHeader != null or bizPoHeader.bizRequestHeader != null}">
                                        <c:if test="${bizPoHeader.bizOrderHeader != null}">
                                            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizPoHeader.bizOrderHeader.id}&str=audit">审核</a>
                                        </c:if>
                                        <c:if test="${bizPoHeader.bizRequestHeader != null}">
                                            <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizPoHeader.bizRequestHeader.id}&str=audit&processPo=processPo">审核</a>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=audit">审核</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </shiro:hasPermission>
                        <c:if test="${bizPoHeader.commonProcess.type != -1}">
                            <c:if test="${bizPoHeader.bizOrderHeader != null}">
                                <shiro:hasPermission name="biz:po:pay:list">
                                    <a href="${ctx}/biz/po/bizPoPaymentOrder/list?poId=${bizPoHeader.id}&type=${PoPayMentOrderTypeEnum.PO_TYPE.type}&fromPage=orderHeader&orderId=${bizPoHeader.bizOrderHeader.id}">支付申请列表</a>
                                </shiro:hasPermission>
                            </c:if>
                            <c:if test="${bizPoHeader.bizRequestHeader != null}">
                                <shiro:hasPermission name="biz:po:pay:list">
                                    <a href="${ctx}/biz/po/bizPoPaymentOrder/list?poId=${bizPoHeader.id}&type=${PoPayMentOrderTypeEnum.PO_TYPE.type}&fromPage=requestHeader&orderId=${bizPoHeader.bizRequestHeader.id}">支付申请列表</a>
                                </shiro:hasPermission>
                            </c:if>
                        </c:if>
                        <!-- 驳回的单子再次开启审核 -->
                        <shiro:hasPermission name="biz:po:bizPoHeader:startAuditAfterReject">
                            <c:if test="${bizPoHeader.commonProcess.type == -1}">
                                <c:if test="${bizPoHeader.bizOrderHeader != null}">
                                    <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizPoHeader.bizOrderHeader.id}&str=startAudit">开启审核</a>
                                </c:if>
                                <c:if test="${bizPoHeader.bizRequestHeader != null}">
                                    <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizPoHeader.bizRequestHeader.id}&str=startAudit">开启审核</a>
                                </c:if>
                            </c:if>
                        </shiro:hasPermission>
                        <shiro:hasPermission name="biz:po:bizPoHeader:edit">
                            <c:if test="${bizPoHeader.commonProcess.purchaseOrderProcess.name == null || bizPoHeader.commonProcess.purchaseOrderProcess.name == '驳回'}">
                                <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">修改</a>
                            </c:if>
                            <a href="javascript:void(0);" onclick="cancel(${bizPoHeader.id});">取消</a>
                        </shiro:hasPermission>
                        <shiro:hasPermission name="biz:po:bizPoHeader:view">
                            <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&str=detail&fromPage=orderHeader&orderId=${requestHeader.id}">详情</a>
                        </shiro:hasPermission>
                        <%--<c:if test="${bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'}">--%>
                        <shiro:hasPermission name="biz:po:bizPoHeader:addScheduling">
                            <a href="${ctx}/biz/po/bizPoHeader/scheduling?id=${bizPoHeader.id}">排产</a>
                        </shiro:hasPermission>
                        <shiro:hasPermission name="biz:po:bizPoHeader:confirmScheduling">
                            <a href="${ctx}/biz/po/bizPoHeader/scheduling?id=${bizPoHeader.id}&forward=confirmScheduling">确认排产</a>
                        </shiro:hasPermission>
                        <%--</c:if>--%>
                    </c:if>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<c:if test="${listBizPoHeader.get(0).commonProcess.type != -1 && (listBizPoHeader.get(0).bizOrderHeader != null||listBizPoHeader.get(0).bizRequestHeader != null)}">
    <c:if test="${pagePoPaymentOrder.list != null && fn:length(pagePoPaymentOrder.list) > 0}">
    <shiro:hasPermission name="biz:po:pay:list">
    <span>支付申请信息列表</span>
    <table id="contentTable2" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>id</th>
            <th>付款金额</th>
            <th>实际付款金额</th>
            <th>最后付款时间</th>
            <th>实际付款时间</th>
            <th>当前状态</th>
            <th>单次支付审批状态</th>
            <th>备注</th>
            <th>支付凭证</th>
            <shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:edit"><th>操作</th></shiro:hasPermission>
        </tr>
        </thead>
        <tbody>
        <%--<c:forEach items="${poPaymentOrderPage.list}" var="bizPoPaymentOrder">--%>
        <c:forEach items="${pagePoPaymentOrder.list}" var="bizPoPaymentOrder">
            <tr>
                <td>
                        ${bizPoPaymentOrder.id}
                </td>
                <td>
                        ${bizPoPaymentOrder.total}
                </td>
                <td>
                        ${bizPoPaymentOrder.payTotal}
                </td>
                <td>
                    <fmt:formatDate value="${bizPoPaymentOrder.deadline}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
                <td>
                    <fmt:formatDate value="${bizPoPaymentOrder.payTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
                <td>
                        ${bizPoPaymentOrder.bizStatus == 0 ? '未支付' : '已支付'}
                </td>
                <td>
                    <c:if test="${bizPoPaymentOrder.total == '0.00' && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成'}">
                        待确认支付金额
                    </c:if>
                    <c:if test="${bizPoPaymentOrder.total != '0.00'}">
                        ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.name}
                    </c:if>
                </td>
                <td>
                        ${bizPoPaymentOrder.remark}
                </td>
                <td>
                    <c:forEach items="${bizPoPaymentOrder.imgList}" var="v">
                        <a target="_blank" href="${v.imgServer}${v.imgPath}"><img style="width: 100px" src="${v.imgServer}${v.imgPath}"/></a>
                    </c:forEach>
                </td>
                <td>
                        <shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:audit">
                        <c:if test="${bizPoPaymentOrder.hasRole == true}">
                        <c:if test="${bizPoPaymentOrder.total != '0.00'}">
                        <c:if test="${bizPoPaymentOrder.id == bizPoHeader.bizPoPaymentOrder.id && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成' && bizPoPaymentOrder.total != 0}">
                        <%--&& (fns:hasRole(roleSet, bizPoPaymentOrder.commonProcess.paymentOrderProcess.moneyRole.roleEnNameEnum))--%>
                        <a href="#" onclick="checkPass2(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.orderType})">审核pp通过</a>
                        <a href="#" onclick="checkReject2(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.orderType})">审核oo驳回</a>
                        </c:if>
                        </c:if>
                        </c:if>
                        <%--<c:if test="${bizPoPaymentOrder.id == bizRequestHeader.bizPoPaymentOrder.id && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成'}">--%>
                        <%--<a onclick="checkPass(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.type})">审核通过</a>--%>
                        <%--<a onclick="checkReject(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.type})">审核驳回</a>--%>
                        <%--</c:if>--%>
                        </shiro:hasPermission>
                        <!-- 驳回的单子再次开启审核 -->
                        <shiro:hasPermission name="biz:po:bizPoHeader:startAuditAfterReject">
                        <c:if test="${bizPoHeader.commonProcess.type == -1}">
                        <c:if test="${bizPoHeader.bizRequestHeader != null}">
                        <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizPoHeader.bizRequestHeader.id}&str=startAudit">开启审核</a>
                        </c:if>
                        </c:if>
                        </shiro:hasPermission>
                        <shiro:hasPermission name="biz:po:sure:bizPoPaymentOrder">
                        <c:if test="${bizPoPaymentOrder.total == '0.00'}">
                        <a href="${ctx}/biz/po/bizPoPaymentOrder/form?id=${bizPoPaymentOrder.id}&poHeaderId=${bizPoHeader.id}&fromPage=${fromPage}">确认支付金额</a>
                        </c:if>
                        </shiro:hasPermission>
                        <shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:edit">
                            <shiro:hasPermission name="biz:po:payment:sure:pay">
                                <c:if test="${bizPoPaymentOrder.orderType == PoPayMentOrderTypeEnum.PO_TYPE.type && bizPoPaymentOrder.id == bizPoHeader.bizPoPaymentOrder.id
						&& bizPoPaymentOrder.commonProcess.paymentOrderProcess.name == '审批完成'
						&& bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'
						}">
                                    <c:if test="${fromPage != null && fromPage == 'requestHeader'}">
                                        <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?bizPoHeader.id=${bizPoHeader.id}&str=pay">确认付款</a>
                                    </c:if>
                                    <c:if test="${fromPage != null && fromPage == 'orderHeader'}">
                                        <a href="${ctx}/biz/order/bizOrderHeader/form?bizPoHeader.id=${bizPoHeader.id}&id=${listBizPoHeader.get(0).bizOrderHeader.id}&str=pay">确认付款</a>
                                        <%--<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=pay">确认2付款</a>--%>
                                    </c:if>
                                    <c:if test="${fromPage == null}">
                                        <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=pay">确认付款</a>
                                    </c:if>
                                </c:if>
                            </shiro:hasPermission>
                        </shiro:hasPermission>









                    <a href="${ctx}/biz/po/bizPoPaymentOrder/formV2?id=${bizPoPaymentOrder.id}">详情</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    </shiro:hasPermission>
</c:if>
</c:if>
<div>
    <input id="buttonBack"  type="button" value="返回"/></span>
</div>
<script type="text/javascript">
    function cancel(id) {
        top.$.jBox.confirm("确认要取消吗？", "系统提示", function (v, h, f) {
            if (v == "ok") {
                $.ajax({
                    url: "${ctx}/biz/po/bizPoHeader/cancel?id=" + id,
                    type: "post",
                    cache: false,
                    success: function (data) {
                        alert(data);
                        if (data == "取消采购订单成功") {
                            <%--使用setTimeout（）方法设定定时600毫秒--%>
                            setTimeout(function () {
                                window.location.reload();
                            }, 600);
                        }
                    }
                });
            }
        }, {buttonsFocus: 1});
        top.$('.jbox-body .jbox-icon').css('top', '55px');
    }
    function checkPass2(poPayId, currentType, money,type) {
        var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认审核通过吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    audit2(1, f.description, poPayId, currentType, money,type);
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入通过理由:", submit: submit, loaded: function (h) {
            }
        });

    }

    function checkReject2(poPayId, currentType, money,type) {
        var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认驳回该流程吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    audit2(2, f.description, poPayId, currentType, money,type);
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入驳回理由:", submit: submit, loaded: function (h) {
            }
        });

    }

    function audit2(auditType, description, poPayId, currentType, money,type) {
        var poHeaderId = $("#poHeaderId").val();
        var orderType = $("#orderType").val();
        var fromPage =  $("#fromPage").val();
        $.ajax({
            url: '${ctx}/biz/po/bizPoHeader/auditPay',
            contentType: 'application/json',
            data: {"poPayId": poPayId, "currentType": currentType, "auditType": auditType, "description": description, "money": money},
            type: 'get',
            success: function (result) {
                result = JSON.parse(result);
                if(result.ret == true || result.ret == 'true') {
                    alert('操作成功!');
                    if(fromPage == "requestHeader"){
                        window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor/list";
                    }else{
                        window.location.href = "${ctx}/biz/order/bizOrderHeader/list";
                    }
                    <%--window.location.href = "${ctx}/biz/po/bizPoHeader/listV2";--%>
                    <%--window.location.href = "${ctx}/biz/po/bizPoPaymentOrder/list?poId=" + poHeaderId + "&orderType=" + orderType + "&fromPage=" + fromPage;--%>
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
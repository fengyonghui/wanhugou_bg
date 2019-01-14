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
            $("#buttonExport").click(function () {
                top.$.jBox.confirm("确认要导出订单数据吗？", "系统提示", function (v, h, f) {
                    if (v == "ok") {
                        $("#searchForm").attr("action", "${ctx}/biz/po/bizPoHeader/poHeaderExport?fromPage=listV3");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action", "${ctx}/biz/po/bizPoHeader/listV3");
                    }
                }, {buttonsFocus: 1});
                top.$('.jbox-body .jbox-icon').css('top', '55px');
            });
        });

        function page(n, s, t) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#includeTestData").val(t);
            $("#searchForm").submit();
            return false;
        }

        function testData(checkbox) {
            $("#includeTestData").val(checkbox.checked);
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/biz/po/bizPoHeader/listV3">订单支出信息</a></li>
</ul>
<sys:message content="${message}"/>
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
    <c:forEach items="${page.list}" var="bizPoHeader" varStatus="state">
        <tr>
            <td>${state.index+1}</td>
            <td><a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&str=detail">
                <c:choose>
                    <c:when test="${bizPoHeader.bizOrderHeader != null}">
                        ${bizPoHeader.bizOrderHeader.orderNum}
                    </c:when>
                    <c:otherwise>
                        ${bizPoHeader.bizRequestHeader.reqNo}
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
												&& (bizPoHeader.bizRequestHeader.bizPoHeader.payTotal == null ? 0 : bizPoHeader.payTotal) < bizPoHeader.bizRequestHeader.totalDetail
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

                        <%--<shiro:hasPermission name="biz:po:bizPoHeader:startAudit">--%>
                            <%--<c:if test="${bizPoHeader.commonProcess.id == null && bizPoHeader.commonProcess.purchaseOrderProcess.name != '审批完成'}">--%>
                                <%--<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=startAudit">开启审核</a>--%>
                            <%--</c:if>--%>
                        <%--</shiro:hasPermission>--%>
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
<div class="pagination">${page}</div>
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
</script>
</body>
</html>
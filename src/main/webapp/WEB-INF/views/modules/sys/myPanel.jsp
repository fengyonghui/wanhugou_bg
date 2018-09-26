<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>我的任务</title>
</head>
<style type="text/css">
    html, body, ul, li {
        padding: 0;
        margin: 0;
    }

    a {
        color: #000000;
        text-decoration: none;
    }

    ul, li {
        list-style: none;
    }

    .htmlMenu {
        padding: 10px;
    }

    .htmlMenu li {
        float: left;
        width: 150px;
        height: 80px;
        background-color: #e9e9e9;
        margin: 10px;
        position: relative;
        text-align: center;
        line-height: 80px;
    }

    .htmlMenu li > span {
        width: 30px;
        height: 25px;
        color: #fff;
        background-color: #f73f39;
        display: inline-block;
        text-align: center;
        line-height: 25px;
        position: absolute;
        top: 0;
        right: 0;
        box-shadow: 4px 4px 5px #888888;
        border-radius: 10px;
    }
</style>

<body>
<div class="contentMain">
    <ul class="htmlMenu">
        <shiro:hasAnyRoles
                name="dept,p_center_manager,buyer,channel_manager,Channel Supervisor,General manager,selection_of_specialist">
            <li>
                <a href="${ctx}/sys/myPanel/waitAudit">订单审核</a>
                <c:if test="${waitAuditCount > 0}">
                    <span>${waitAuditCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,p_center_manager,buyer,channel_manager">
            <li>
                <a href="${ctx}/sys/myPanel/hasRetainage">有尾款</a>
                <c:if test="${hasRetainageCount > 0}">
                    <span>${hasRetainageCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,p_center_manager,warehouse_specialist">
            <li>
                <a href="${ctx}/sys/myPanel/ddck">订单出库</a>
                <c:if test="${ddckCount > 0}">
                    <span>${ddckCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,p_center_manager,warehouse_specialist">
            <li>
                <a href="${ctx}/sys/myPanel/fhdrk">备货单入库</a>
                <c:if test="${fhdrkCount > 0}">
                    <span>${fhdrkCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,selection_of_specialist">
            <li>
                <a href="${ctx}/sys/myPanel/applyPayment">申请付款</a>
                <c:if test="${applyPaymentCount > 0}">
                    <span>${applyPaymentCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,selection_of_specialist,finance_director,financial_general_manager">
            <li>
                <a href="${ctx}/sys/myPanel/paymentOrderAudit">付款单审核</a>
                <c:if test="${paymentOrderAuditCount > 0}">
                    <span>${paymentOrderAuditCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,channel_manager,Channel Supervisor,selection_of_specialist">
            <li>
                <a href="${ctx}/sys/myPanel/reAudit">备货单审核</a>
                <c:if test="${reAuditCount > 0}">
                    <span>${reAuditCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,selection_of_specialist">
            <li>
                <a href="${ctx}/sys/myPanel/waitScheduling">待排产</a>
                <c:if test="${waitSchedulingCount > 0}">
                    <span>${waitSchedulingCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,teller">
            <li>
                <a href="${ctx}/sys/myPanel/orderPayment">备货单/订单付款</a>
                <c:if test="${orderPaymentCount > 0}">
                    <span>${orderPaymentCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,selection_of_specialist">
            <li>
                <a href="${ctx}/sys/myPanel/needPutaway">需上架商品</a>
                <c:if test="${needPutawayCount > 0}">
                    <span>${needPutawayCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,shipper">
            <li>
                <a href="${ctx}/sys/myPanel/reWaitShipments">备货单待发货</a>
                <c:if test="${reWaitShipmentsCount > 0}">
                    <span>${reWaitShipmentsCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,shipper">
            <li>
                <a href="${ctx}/sys/myPanel/orderWaitShipments">订单待发货</a>
                <c:if test="${orderWaitShipmentsCount > 0}">
                    <span>${orderWaitShipmentsCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,channel_manager,Channel Supervisor">
            <li>
                <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form">添加备货单</a>
            </li>
        </shiro:hasAnyRoles>
        <shiro:hasAnyRoles
                name="dept,warehouse_specialist">
            <li>
                <a href="${ctx}/sys/myPanel/checkException">盘点异常</a>
                <c:if test="${checkExceptionCount > 0}">
                    <span>${checkExceptionCount}</span>
                </c:if>
            </li>
        </shiro:hasAnyRoles>
    </ul>
</div>
</body>

</html>
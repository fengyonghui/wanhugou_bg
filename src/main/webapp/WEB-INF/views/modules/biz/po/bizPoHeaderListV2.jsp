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
                        $("#searchForm").attr("action", "${ctx}/biz/po/bizPoHeader/poHeaderExport");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action", "${ctx}/biz/po/bizPoHeader/");
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
    <li class="active"><a href="${ctx}/biz/po/bizPoHeader/listV2">订单支出信息</a></li>
</ul>
<form:form id="searchForm" modelAttribute="bizPoHeader" action="${ctx}/biz/po/bizPoHeader/listV2" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <input id="includeTestData" name="includeTestData" type="hidden" value="${page.includeTestData}"/>
    <ul class="ul-form">
            <%--<li><label>采购单号</label>--%>
            <%--<form:input path="orderNum" htmlEscape="false" maxlength="25" class="input-medium"/>--%>
            <%--</li>--%>
        <li><span style="margin-left: 10px"><label>订单/备货清单编号</label></span>
            <form:input path="num" htmlEscape="false" maxlength="25" class="input-medium"/>
        </li>
        <li><span style="margin-left: 10px"><label>起始金额</label></span>
            <form:input path="startPrice" htmlEscape="false" maxlength="25" class="input-medium"/>
        </li>
        <li><span style="margin-left: 10px"><label>结束金额</label></span>
            <form:input path="endPrice" htmlEscape="false" maxlength="25" class="input-medium"/>
        </li>
        <li><label>付款时间：</label>
            <input name="startPayTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${bizPoHeader.startPayTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
            至
            <input name="endPayTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${bizPoHeader.endPayTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
        </li>
        <li><label>供应商</label>
            <sys:treeselect id="vendOffice" name="vendOffice.id" value="${bizPoHeader.vendOffice.id}"
                            labelName="vendOffice.name"
                            labelValue="${bizPoHeader.vendOffice.name}" notAllowSelectRoot="true"
                            notAllowSelectParent="true" allowClear="true"
                            title="供应商" url="/sys/office/queryTreeList?type=7" cssClass="input-medium"
                            dataMsgRequired="必填信息">
            </sys:treeselect>
        </li>
        <li><label>发票状态：</label>
            <form:select path="invStatus" class="input-medium">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
        </li>
        <li><label>业务状态：</label>
            <form:select path="bizStatus" class="input-medium">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getDictList('biz_po_status')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
        </li>
        <li><label>订单来源：</label>
            <form:select path="plateformInfo.id" class="input-medium">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getPlatformInfoList()}" itemLabel="name" itemValue="id" htmlEscape="false"/>
            </form:select>
        </li>
        <li><label>审核状态：</label>
            <form:select path="processTypeStr" class="input-medium">
                <form:option value="" label="请选择"/>
                <form:options items="${processList}" htmlEscape="false"/>
            </form:select>
        </li>
        <li><label>排产状态：</label>
            <form:select path="poSchType" class="input-medium">
                <form:option value="" label="请选择"/>
                <form:option value="0" label="未排产"/>
                <form:option value="1" label="排产中"/>
                <form:option value="2" label="排产完成"/>
            </form:select>
        </li>
        <li><label>测试数据</label>
            <form:checkbox path="page.includeTestData" htmlEscape="false" maxlength="100" class="input-medium"
                           onclick="testData(this)"/>
        </li>

        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="btns"><input id="buttonExport" class="btn btn-primary" type="button" value="导出"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
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

                        <shiro:hasPermission name="biz:po:bizPoHeader:startAudit">
                            <c:if test="${bizPoHeader.commonProcess.id == null && bizPoHeader.commonProcess.purchaseOrderProcess.name != '审批完成'}">
                                <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=startAudit">开启审核</a>
                            </c:if>
                        </shiro:hasPermission>
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
                                            <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizPoHeader.bizRequestHeader.id}&str=audit">审核</a>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=audit">审核</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </shiro:hasPermission>

							<c:choose>
								<c:when test="${bizPoHeader.bizOrderHeader != null or bizPoHeader.bizRequestHeader != null}">
									<c:if test="${bizPoHeader.bizOrderHeader != null}" >
										<shiro:hasPermission name="biz:po:pay:list">
											<a href="${ctx}/biz/po/bizPoPaymentOrder/list?poId=${bizPoHeader.id}&type=${PoPayMentOrderTypeEnum.PO_TYPE.type}&fromPage=orderHeader&orderId=${bizPoHeader.bizOrderHeader.id}">支付申请列表</a>
										</shiro:hasPermission>
									</c:if>
									<c:if test="${bizPoHeader.bizRequestHeader != null}" >
										<shiro:hasPermission name="biz:po:pay:list">
											<a href="${ctx}/biz/po/bizPoPaymentOrder/list?poId=${bizPoHeader.id}&type=${PoPayMentOrderTypeEnum.PO_TYPE.type}&fromPage=requestHeader&orderId=${bizPoHeader.bizRequestHeader.id}">支付申请列表</a>
										</shiro:hasPermission>
									</c:if>
								</c:when>
								<c:otherwise>
									<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=audit">审核</a>
									<a href="${ctx}/biz/po/bizPoPaymentOrder/list?poId=${bizPoHeader.id}">支付申请列表</a>
								</c:otherwise>
							</c:choose>

                        <shiro:hasPermission name="biz:po:bizPoHeader:edit">
                            <c:if test="${bizPoHeader.commonProcess.purchaseOrderProcess.name == null || bizPoHeader.commonProcess.purchaseOrderProcess.name == '驳回'}">
                                <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">修改</a>
                            </c:if>
                            <a href="javascript:void(0);" onclick="cancel(${bizPoHeader.id});">取消</a>
                        </shiro:hasPermission>
                        <shiro:hasPermission name="biz:po:bizPoHeader:view">
                            <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&str=detail">详情</a>
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
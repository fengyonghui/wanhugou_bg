<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>采购订单管理</title>
    <script type="text/javascript" src="${ctxStatic}/tablesMergeCell/tablesMergeCell.js"></script>
    <meta name="decorator" content="default"/>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/po/bizPoHeader/">订单详情</a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizPoHeader" action="${ctx}/biz/po/bizPoHeader/save?prewStatus=prew"
           method="post" class="form-horizontal">
    <form:hidden path="id" id="id"/>
    <form:hidden path="bizPoPaymentOrder.id" id="paymentOrderId"/>
    <input id="prew" type="hidden" value="${prewStatus}"/>
    <input id="type" type="hidden" value="${type}"/>
    <sys:message content="${message}"/>
    <input type="hidden" name="vendOffice.id" value="${vendorId}">
    <input id="str" type="hidden" value="${bizPoHeader.str}"/>
    <input id="deliveryStatus" type="hidden" value="${bizPoHeader.deliveryStatus}"/>
    <c:if test="${bizPoHeader.id!=null}">
        <div class="control-group">
            <label class="control-label">采购单编号：</label>
            <div class="controls">
                <form:input disabled="true" path="orderNum" htmlEscape="false" maxlength="30" class="input-xlarge "/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">采购单来源：</label>
            <div class="controls">
                <c:forEach items="${bizPoHeader.orderSourceMap}" var="so">
                    <%--<c:if test="${so.orderHeader!=null}">--%>
                    <%--<input type="text" style="margin-bottom: 10px" disabled="disabled" value="${so.orderHeader.orderNum}" htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
                    <%--<br/>--%>
                    <%--</c:if>--%>
                    <%--<c:if test="${so.requestHeader!=null}">--%>
                    <input type="text" style="margin-bottom: 10px" disabled="disabled" value="${so.key}"
                           htmlEscape="false" maxlength="30" class="input-xlarge "/>
                    <br/>
                    <%--</c:if>--%>

                </c:forEach>

            </div>
        </div>

        <div class="control-group">
            <label class="control-label">订单总价：</label>
            <div class="controls">
                <input type="text" disabled="disabled" value="${bizPoHeader.totalDetail}" htmlEscape="false"
                       maxlength="30" class="input-xlarge "/>
            </div>
        </div>
        <%--<div class="control-group">--%>
        <%--<label class="control-label">交易费用：</label>--%>
        <%--<div class="controls">--%>
        <%--<form:input path="totalExp"  htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
        <%--</div>--%>
        <%--</div>--%>

        <%--<div class="control-group">--%>
        <%--<label class="control-label">运费：</label>--%>
        <%--<div class="controls">--%>
        <%--<form:input path="freight"  htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
        <%--</div>--%>
        <%--</div>--%>

        <div class="control-group">
            <label class="control-label">应付金额：</label>
            <div class="controls">
                <input type="text" disabled="disabled"
                       value="${bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight}" htmlEscape="false"
                       maxlength="30" class="input-xlarge "/>
            </div>
        </div>


        <div class="control-group">
            <label class="control-label">最后付款时间：</label>
            <div class="controls">
                <input name="lastPayDate" type="text" readonly="readonly" maxlength="20"
                       class="input-medium Wdate required"
                       value="<fmt:formatDate value="${bizPoHeader.lastPayDate}"  pattern="yyyy-MM-dd"/>"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>

            </div>
        </div>

        <div class="control-group">
            <label class="control-label">交货地点：</label>
            <div class="controls">
                <form:radiobutton id="deliveryStatus0" path="deliveryStatus" onclick="choose(this)" value="0"/>采购中心
                <form:radiobutton id="deliveryStatus1" path="deliveryStatus" checked="true" onclick="choose(this)"
                                  value="1"/>供应商
            </div>
        </div>
        <div class="control-group" id="buyCenterId" style="display:none">
            <label class="control-label">采购中心：</label>
            <div class="controls">
                <sys:treeselect id="deliveryOffice" name="deliveryOffice.id" value="${bizPoHeader.deliveryOffice.id}"
                                labelName="deliveryOffice.name"
                                labelValue="${bizPoHeader.deliveryOffice.name}" notAllowSelectParent="true"
                                title="采购中心"
                                url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex"
                                cssClass="input-xlarge " dataMsgRequired="必填信息">
                </sys:treeselect>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">备注：</label>
            <div class="controls">
                <form:textarea path="remark" htmlEscape="false" maxlength="30" class="input-xlarge "/>
            </div>
        </div>

        <%--<div class="control-group">--%>
        <%--<label class="control-label">发票状态：</label>--%>
        <%--<div class="controls">--%>
        <%--<input type="text" disabled="disabled" value="${fns:getDictLabel(bizPoHeader.invStatus, 'biz_order_invStatus', '未知类型')}" htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
        <%--</div>--%>
        <%--</div>--%>

        <div class="control-group">
            <label class="control-label">订单状态：</label>
            <div class="controls">
                <input type="text" disabled="disabled"
                       value="${fns:getDictLabel(bizPoHeader.bizStatus, 'biz_po_status', '未知类型')}" htmlEscape="false"
                       maxlength="30" class="input-xlarge "/>
            </div>
        </div>

        <div class="control-group">
            <label class="control-label">供应商：</label>
            <div class="controls">
                <a href="${ctx}/sys/office/supplierForm?id=${bizPoHeader.vendOffice.id}&gysFlag=gys_view">
                    ${bizPoHeader.vendOffice.name}
                </a>
            </div>
        </div>

            <div class="control-group">
                <label class="control-label">供应商卡号：</label>
                <div class="controls">
                    <form:input disabled="true" path="vendOffice.bizVendInfo.cardNumber" htmlEscape="false" maxlength="30"
                                class="input-xlarge "/>
                </div>
            </div>

            <div class="control-group">
                <label class="control-label">供应商收款人：</label>
                <div class="controls">
                    <form:input disabled="true" path="vendOffice.bizVendInfo.payee" htmlEscape="false" maxlength="30"
                                class="input-xlarge "/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">供应商开户行：</label>
                <div class="controls">
                    <form:input disabled="true" path="vendOffice.bizVendInfo.bankName" htmlEscape="false" maxlength="30"
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

        <c:if test="${bizPoHeader.bizPoPaymentOrder.id != null || type == 'createPay'}">
            <div class="control-group">
                <label class="control-label">申请金额：</label>
                <div class="controls">
                    <input id="payTotal" name="planPay" type="text"
                           <c:if test="${type == 'audit' || type == 'pay'}">readonly</c:if>
                           value="${bizPoHeader.bizPoPaymentOrder.id != null ?
                           bizPoHeader.bizPoPaymentOrder.total : (bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight-bizPoHeader.payTotal)}"
                           htmlEscape="false" maxlength="30" class="input-xlarge"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">本次申请付款时间：</label>
                <div class="controls">
                    <input name="payDeadline" id="payDeadline" type="text" readonly="readonly" maxlength="20"
                           class="input-medium Wdate required"
                           value="<fmt:formatDate value="${bizPoHeader.bizPoPaymentOrder.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
                            <c:if test="${type == 'createPay'}"> onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"</c:if>
                           placeholder="必填！"/>
                </div>
            </div>
        </c:if>
        <c:if test="${type == 'startAudit'}">
            <div class="control-group">
                <label class="control-label">是否同时提交支付申请：</label>
                <div class="controls">
                    <input name="meanwhilePayOrder" id="meanwhilePayOrderRadioTrue" type="radio" onclick="showTimeTotal(true);" checked/>是
                    <input name="meanwhilePayOrder" id="meanwhilePayOrderRadioFalse" type="radio" onclick="showTimeTotal(false);"/>否
                </div>
            </div>
            <div class="control-group prewTimeTotal">
                <label class="control-label">最后付款时间：</label>
                <div class="controls">
                    <input name="prewPayDeadline" id="prewPayDeadline" type="text" readonly="readonly" maxlength="20"
                           class="input-medium Wdate required"
                           value="<fmt:formatDate value="${bizPoHeader.bizPoPaymentOrder.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
                           placeholder="必填！"/>
                </div>
            </div>
            <div class="control-group prewTimeTotal">
                <label class="control-label">申请金额：</label>
                <div class="controls">
                    <input name="prewPayTotal" id="prewPayTotal" type="text"  maxlength="20" placeholder="必填！"/>
                </div>
            </div>
        </c:if>
        <c:if test="${type == 'pay'}">
            <div class="control-group">
                <label class="control-label">实际付款金额：</label>
                <div class="controls">
                    <input id="truePayTotal" name="payTotal" type="text"
                           value="${bizPoHeader.bizPoPaymentOrder.payTotal}"
                           htmlEscape="false" maxlength="30" class="input-xlarge "/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">上传付款凭证：
                    <p style="opacity: 0.5;">点击图片删除</p>
                </label>

                <div class="controls">
                    <input class="btn" type="file" name="productImg" onchange="submitPic('payImg', true)" value="上传图片" multiple="multiple" id="payImg"/>
                </div>
                <div id="payImgDiv">
                    <img src="${bizPoHeader.bizPoPaymentOrder.img}" customInput="payImgImg" style='width: 100px' onclick="$(this).remove();">
                </div>
            </div>
        </c:if>

        <c:if test="${type == 'audit' && bizPoHeader.commonProcess.id != null}">
            <div class="control-group">
                <label class="control-label">审核状态：</label>
                <div class="controls">
                    <input type="text" disabled="disabled"
                           value="${purchaseOrderProcess.name}" htmlEscape="false"
                           maxlength="30" class="input-xlarge "/>
                    <input id="currentType" type="hidden" disabled="disabled"
                           value="${purchaseOrderProcess.code}" htmlEscape="false"
                           maxlength="30" class="input-xlarge "/>
                </div>
            </div>
        </c:if>
    </c:if>
    <c:if test="${fn:length(bizPoHeader.commonProcessList) > 0}">
    <div class="control-group">
        <label class="control-label">审批流程：</label>
        <div class="controls help_wrap">
            <div class="help_step_box fa">
                <c:forEach items="${bizPoHeader.commonProcessList}" var="v" varStatus="stat">
                    <c:if test="${!stat.last}" >
                        <div class="help_step_item">
                            <div class="help_step_left"></div>
                            <div class="help_step_num">${stat.index + 1}</div>
                            批注:${v.description}<br/><br/>
                            审批人:${v.user.name}<br/>
                            <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            <div class="help_step_right"></div>
                        </div>
                    </c:if>
                    <c:if test="${stat.last}">
                        <div class="help_step_item help_step_set">
                            <div class="help_step_left"></div>
                            <div class="help_step_num">${stat.index + 1}</div>
                            当前状态:${v.purchaseOrderProcess.name}<br/><br/>
                                ${v.user.name}<br/>
                            <div class="help_step_right"></div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </div>
    </c:if>
    <c:if test="${bizPoHeader.poDetailList!=null}">
        <div class="form-actions">
            <shiro:hasPermission name="biz:po:bizPoHeader:audit">
                <c:if test="${type == 'startAudit'}">
                    <input id="btnSubmit" type="button" onclick="startAudit()" class="btn btn-primary" value="开启审核"/>
                    <input id="btnSubmit" type="button" onclick="startRejectAudit()" class="btn btn-primary" value="驳回"/>
                </c:if>
                <c:if test="${type == 'audit'}">
                    <input id="btnSubmit" type="button" onclick="checkPass()" class="btn btn-primary" value="审核通过"/>
                    <input id="btnSubmit" type="button" onclick="checkReject()" class="btn btn-primary" value="审核驳回"/>
                </c:if>
                <c:if test="${type == 'createPay'}">
                    <input id="btnSubmit" type="button" onclick="saveMon('createPay')" class="btn btn-primary" value="申请付款"/>
                </c:if>
                <c:if test="${type == 'pay'}">
                    <input id="btnSubmit" type="button" onclick="pay()" class="btn btn-primary" value="确认支付"/>
                </c:if>
            </shiro:hasPermission>
            <shiro:hasPermission name="biz:po:bizPoHeader:edit">
                <c:if test="${type != 'pay' && type != 'audit' && prewStatus == 'prew'}">
                    <input id="btnSubmit" type="button" onclick="saveMon('createPo')" class="btn btn-primary" value="确认生成"/>
                </c:if>
                <c:if test="${type != 'pay' && type != 'createPay' && type != 'audit' && type != 'startAudit' && prewStatus != 'prew'}">
                    <input id="btnSubmit" type="button" onclick="saveMon('')" class="btn btn-primary" value="保存"/>
                </c:if>

                &nbsp;</shiro:hasPermission>
        </div>
    </c:if>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <c:if test="${bizPoHeader.id==null}">
                <td><input id="select_all" type="checkbox"/>订单号/备货单号</td>
                <th>选择</th>
            </c:if>
            <th>产品图片</th>
            <th>品牌名称</th>
            <th>商品名称</th>
                <%--<th>商品编码</th>--%>
            <th>商品货号</th>
            <c:if test="${bizPoHeader.id!=null}">
                <th>所属单号</th>
            </c:if>
                <%--<th>商品属性</th>--%>
            <c:if test="${bizPoHeader.id==null}">
                <th>申报数量</th>
            </c:if>
            <th>采购数量</th>
            <c:if test="${bizPoHeader.id!=null}">
                <th>已供货数量</th>
            </c:if>
            <th>工厂价</th>


        </tr>
        </thead>
        <tbody id="prodInfo">
        <c:if test="${bizPoHeader.poDetailList!=null}">
            <c:forEach items="${bizPoHeader.poDetailList}" var="poDetail">
                <tr>
                    <td><img style="max-width: 120px" src="${poDetail.skuInfo.productInfo.imgUrl}"/></td>
                    <td>${poDetail.skuInfo.productInfo.brandName}</td>
                    <td>${poDetail.skuInfo.name}</td>
                        <%--<td>${poDetail.skuInfo.partNo}</td>--%>
                    <td>${poDetail.skuInfo.itemNo}</td>
                    <c:if test="${bizPoHeader.id!=null}">
                        <td>
                            <c:forEach items="${bizPoHeader.orderNumMap[poDetail.skuInfo.id]}" var="orderNumStr"
                                       varStatus="orderStatus">
                            <c:if test="${orderNumStr.soType==1}">
                            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderNumStr.orderHeader.id}&orderDetails=details">
                                </c:if>
                                <c:if test="${orderNumStr.soType==2}">
                                <a href="${ctx}/biz/request/bizRequestHeader/form?id=${orderNumStr.requestHeader.id}&str=detail">
                                    </c:if>
                                        ${orderNumStr.orderNumStr}
                                </a>
                                </c:forEach>

                        </td>
                    </c:if>
                        <%--<td>${poDetail.skuInfo.skuPropertyInfos}</td>--%>
                    <td>${poDetail.ordQty}</td>
                    <td>${poDetail.sendQty}</td>
                    <td>${poDetail.unitPrice}</td>
                </tr>
            </c:forEach>
        </c:if>
        <c:if test="${bizPoHeader.poDetailList==null}">
            <c:if test="${not empty reqDetailMap}">
                <c:forEach items="${reqDetailMap}" var="map">
                    <c:forEach items="${map.value}" var="reqDetail">
                        <tr>
                                <%--<c:set value="${fn:split(map.key, ',')}" var="detail"></c:set>--%>
                            <td><input title="num" name="${reqDetail.requestHeader.reqNo}" type="checkbox"
                                       onclick="selectRequest('${reqDetail.requestHeader.reqNo}')"/>${reqDetail.requestHeader.reqNo}
                            </td>
                            <td name="reqs"><input title="num" name="reqDetailIds"
                                                   about="${reqDetail.requestHeader.reqNo}" type="checkbox"
                                                   value="${reqDetail.id}" onclick="selectThis(this);"/></td>
                            <td><img style="max-width: 120px" src="${reqDetail.skuInfo.productInfo.imgUrl}"/></td>
                            <td>${reqDetail.skuInfo.productInfo.brandName}</td>
                            <td>${reqDetail.skuInfo.name}</td>
                                <%--<td>${reqDetail.skuInfo.partNo}</td>--%>
                            <td>${reqDetail.skuInfo.itemNo}</td>
                                <%--<td>${reqDetail.skuInfo.skuPropertyInfos}</td>--%>
                            <td>${reqDetail.reqQty-reqDetail.recvQty}
                                    <%--<input type='hidden' name='reqDetailIds' value='${map.value.reqDetailIds}'/>--%>
                                    <%--<input type='hidden' name='skuInfoIds' value='${map.key.id}'/>--%>
                                    <%--<input type='hidden' name='orderDetailIds' value='${map.value.orderDetailIds}'/>--%>
                            </td>
                            <td><input name="ordQtys" readonly="readonly" value="${reqDetail.reqQty-reqDetail.recvQty}"
                                       class="input-mini" type='text'/></td>
                            <td>
                                <input readonly="readonly" type="text" name="unitPrices"
                                       value="${reqDetail.skuInfo.buyPrice}" class="input-mini">
                            </td>

                        </tr>
                    </c:forEach>
                </c:forEach>
            </c:if>
            <c:if test="${not empty orderDetailMap}">
                <c:forEach items="${orderDetailMap}" var="map">
                    <c:forEach items="${map.value}" var="orderDetail">
                        <tr>
                            <td><input title="num" name="${orderDetail.orderHeader.orderNum}" type="checkbox"
                                       onclick="selectOrder('${orderDetail.orderHeader.orderNum}')"/>${orderDetail.orderHeader.orderNum}
                            </td>
                            <td name="ords"><input title="num" name="orderDetailIds"
                                                   about="${orderDetail.orderHeader.orderNum}" type="checkbox"
                                                   value="${orderDetail.id}" onclick="selectThis(this);"/></td>
                            <td><img style="max-width: 120px" src="${orderDetail.skuInfo.productInfo.imgUrl}"/></td>
                            <td>${orderDetail.skuInfo.productInfo.brandName}</td>
                            <td>${orderDetail.skuInfo.name}</td>
                                <%--<td>${orderDetail.skuInfo.partNo}</td>--%>
                            <td>${orderDetail.skuInfo.itemNo}</td>
                                <%--<td>${orderDetail.skuInfo.skuPropertyInfos}</td>--%>
                            <td>${orderDetail.ordQty-orderDetail.sentQty}
                                    <%--<input type='hidden' name='reqDetailIds' value='${map.value.reqDetailIds}'/>--%>
                                    <%--<input type='hidden' name='skuInfoIds' value='${map.key.id}'/>--%>
                                    <%--<input type='hidden' name='orderDetailIds' value='${map.value.orderDetailIds}'/>--%>
                            </td>
                            <td><input name="ordQtys" readonly="readonly"
                                       value="${orderDetail.ordQty-orderDetail.sentQty}" class="input-mini"
                                       type='text'/></td>
                            <td>
                                <input readonly="readonly" type="text" name="unitPrices"
                                       value="${orderDetail.skuInfo.buyPrice}" class="input-mini">
                            </td>

                        </tr>
                    </c:forEach>
                </c:forEach>
            </c:if>
        </c:if>
        </tbody>
    </table>


    <div class="form-actions">
        <shiro:hasPermission name="biz:po:bizPoHeader:edit">
            <c:if test="${bizPoHeader.poDetailList==null}">
                <input id="btnSubmit" type="button" onclick="savePoOrder()" class="btn btn-primary" value="采购单预览"/>
            </c:if>
            &nbsp;</shiro:hasPermission>
        <c:if test="${not empty bizPoHeader.str && bizPoHeader.str eq 'detail'}">
            <input onclick="window.print();" type="button" class="btn btn-primary" value="打印采购订单"
                   style="background:#F78181;"/>
            &nbsp;&nbsp;&nbsp;
        </c:if>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

</body>
</html>
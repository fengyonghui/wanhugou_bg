<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>采购订单管理</title>
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
        $(document).ready(function () {
            $("#contentTable").tablesMergeCell({
                // automatic: true
                // 是否根据内容来合并
                cols: [0]
                // rows:[0,2]
            });

//            $('#select_all').live('click', function () {
//                var choose = $("input[title='num']");
//                if ($(this).attr('checked')) {
//                    choose.attr('checked', true);
//                } else {
//                    choose.attr('checked', false);
//                }
//            });
//            var str = $("#str").val();
//            if (str == 'detail') {
//                $("#inputForm").find("input[type!='button']").attr("disabled", "disabled");
//                $("#btnSubmit").hide();
//            }
            $("#inputForm").validate({
                submitHandler: function (form) {
                    var ordQty = $("td[name='ordQty']");
                    var sendQty = $("td[name='sendQty']");
                    var schedulingNum = $("input[name='schedulingNum']");

                    for(var i=0; i<ordQty.length; i++){
                        alert("--")
                        var ordQtyTemp = ordQty[i].text();
                        var sendQtyTemp = sendQty[i].text();
                        var schedulingNumTemp = schedulingNum[i].value;

                        alert("ordQtyTemp=" + ordQtyTemp + "\r\n" + "sendQtyTemp=" + sendQtyTemp + "\r\n" + "schedulingNumTemp=" + schedulingNumTemp)
                    }

//                    alert("ordQty=" + ordQty + "\r\n" + "sendQty=" + sendQty + "\r\n" + "schedulingNum=" + schedulingNum)
//                    if(schedulingNum > (ordQty-sendQty)){
//                        alert("--1--")
//                    } else {
//                        alert("--2--")
//                    }

//                    var aa = 0;
//                    $("input[name='orderDetailIds'][checked='checked']").each(function () {
//                        aa += 1;
//                    });
//                    $("input[name='reqDetailIds'][checked='checked']").each(function () {
//                        aa += 1;
//                    });
//                    if (parseInt(aa) != 0) {
//                        loading('正在提交，请稍等...');
//                        form.submit();
//                    } else {
//                        var prew = $("#prew").val();
//                        var type = $("#type").val();
//                        if( prew == 'prew' || type == 'createPay' || type == '') {
//                            loading('正在提交，请稍等...');
//                            form.submit();
//                        }else {
//                            alert("请选择生成采购单的详情");
//                        }
//                    }
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/po/bizPoHeader/">采购订单列表</a></li>
    <li class="active">
        <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">采购订单
            <shiro:hasPermission name="biz:po:bizPoHeader:edit">${not empty bizPoHeader.id?'修改':'添加'}</shiro:hasPermission>
            <shiro:lacksPermission name="biz:po:bizPoHeader:edit">查看</shiro:lacksPermission></a>
    </li>
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
    <c:if test="${bizOrderHeader.orderType != 6}" >
    <label style="font-size: 17px;">排产履历：</label>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
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
            <c:if test="${bizPoHeader.id!=null}">
                <th>排产数量</th>
            </c:if>
            <th>工厂价</th>


        </tr>
        </thead>
        <tbody id="prodInfo">
        <c:if test="${bizPoHeader.poDetailList!=null}">
            <c:forEach items="${bizPoHeader.poDetailList}" var="poDetail">
                <c:forEach items="${poDetail.schedulingPlanList}" var="schedulingPlan">
                    <tr>
                        <td><img style="max-width: 120px" src="${poDetail.skuInfo.productInfo.imgUrl}"/></td>
                        <td>${poDetail.skuInfo.productInfo.brandName}</td>
                        <td>${poDetail.skuInfo.name}</td>
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
                        <td>${poDetail.ordQty}</td>
                        <td>${poDetail.sendQty}</td>
                        <td>
                            <input type="text" style="margin-bottom: 10px" value="${schedulingPlan.schedulingNum}"
                                   htmlEscape="false" maxlength="30" class="input-xlarge "/>
                        <td>${poDetail.unitPrice}</td>
                    </tr>
                </c:forEach>
            </c:forEach>
        </c:if>
        </tbody>
    </table>
    <label style="font-size: 17px;">本次排产：</label>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
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
            <th>排产数量</th>
            <c:if test="${fns:getUser().getRoleNames()}">
                <th>已完成数量</th>
            </c:if>
            <th>工厂价</th>


        </tr>
        </thead>
        <tbody id="prodInfo2">
        <c:if test="${bizPoHeader2.poDetailList!=null}">
            <c:forEach items="${bizPoHeader2.poDetailList}" var="poDetail">
                <tr>
                    <td><img style="max-width: 120px" src="${poDetail.skuInfo.productInfo.imgUrl}"/></td>
                    <td>${poDetail.skuInfo.productInfo.brandName}</td>
                    <td>${poDetail.skuInfo.name}</td>
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
                    <td name="ordQty">${poDetail.ordQty}</td>
                    <td name="sendQty">${poDetail.sendQty}</td>
                    <td>
                        <input type="text" name="schedulingNum" style="margin-bottom: 10px" value="${poDetail.ordQty - poDetail.sendQty}"
                            htmlEscape="false" maxlength="30" class="input-xlarge "/>
                    </td>
                    <td>${poDetail.unitPrice}</td>
                </tr>
            </c:forEach>
        </c:if>
        </tbody>
    </table>

    </c:if>

    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        <shiro:hasPermission name="biz:po:bizPoHeader:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;
            <input id="btnSubmit" type="button" onclick="saveMon('')" class="btn btn-primary" value="保存"/>
        </shiro:hasPermission>
    </div>
</form:form>
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
</body>
</html>
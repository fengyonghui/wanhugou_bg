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

            //$("#name").focus();
            var str = $("#str").val();
            if (str == 'detail') {
                $("#inputForm").find("input[type!='button']").attr("disabled", "disabled");
                $("#btnSubmit").hide();
            }
            $("#inputForm").validate({
                submitHandler: function (form) {

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
            var deliveryStatus = $("#deliveryStatus").val();

            if (deliveryStatus == 0) {
                $("input[name='deliveryStatus']").attr("checked", false)
                $("#deliveryStatus0").attr("checked", true);
            }

            $("#savePayment").click(function () {
                var toPayTotal=$("#toPayTotal").val();
                var payTotal = $("#payTotal").val();
                var payDeadline = $("#payDeadline").val();
                var loginName = $("#loginName").val();
                if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
                     alert("请输入申请金额!");
                     return false;
                }
                if(parseInt(payTotal)>toPayTotal){
                    alert("申请金额大于应付金额!");
                    return false;
                }
                if ($String.isNullOrBlank(payDeadline)) {
                    alert("请选择本次申请付款时间!");
                    return false;
                }
                $.ajax({
                    type:"post",
                    url:"${ctx}/sys/user/findVendorUser",
                    data:{loginName:loginName},
                    success:function (data) {
                        if (data == "false"){
                            alert("该登陆名已经存在");
                            aflag = false;
                            return false;
                        }
                        $("#primaryPersonId").val(null);
                        $("#primaryPersonName").val(name);
                        $("#myModal").modal('hide');
                    }
                });
            });

        });

        // function saveMon(type) {
        //     if (type == 'createPay') {
        //         var payTotal = $("#payTotal").val();
        //         var payDeadline = $("#payDeadline").val();
        //         if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
        //             alert("请输入申请金额!");
        //             return false;
        //         }
        //         if ($String.isNullOrBlank(payDeadline)) {
        //             alert("请选择本次申请付款时间!");
        //             return false;
        //         }
        //     }
        // }

        function choose(obj) {
            if ($(obj).val() == 0) {
                $("#buyCenterId").show();
            } else {
                $("#buyCenterId").hide();
            }

        }


        function selectRequest(obj) {
            // alert(obj);
            var flag = false;
            var aflag = false;
            if ($("input[name='" + obj + "']").attr("checked") != undefined) {
                $("input[name='reqDetailIds']").each(function () {
                    var reqNo = $(this).attr("about");
                    if (reqNo == obj) {
                        $(this).attr("checked", "checked");
                    }
                })
            } else {
                $("input[name='reqDetailIds']").each(function () {
                    var reqNo = $(this).attr("about");
                    if (reqNo == obj) {
                        $(this).removeAttr("checked");
                    }
                })
            }
        }

        function selectThis(obj) {
            if ($(obj).attr("checked")=="checked") {
                $(obj).attr("checked","checked");
            } else {
                $(obj).removeAttr("checked");
            }
        }

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/po/bizPoHeader/">采购订单列表</a></li>
    <li class="active">
        <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">采购订单
            <shiro:hasPermission name="biz:po:bizPoHeader:edit">
                ${not empty bizPoHeader.id ? ( bizPoHeader.str == 'detail' ? "详情":'修改'):'添加'}
            </shiro:hasPermission>
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
    <c:if test="${bizPoHeader.id!=null}">
        <c:if test="${bizPoHeader.fromPage == 'poHeader'}">
            <div class="control-group" >
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
        </c:if>

        <c:if test="${bizPoHeader.fromPage == 'orderHeader'}">
            <div class="control-group">
                <label class="control-label">订单/备货单号</label>
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
        </c:if>

        <div class="control-group">
            <label class="control-label">订单总价：</label>
            <div class="controls">
                <input type="text" disabled="disabled" value="${bizPoHeader.totalDetail}" htmlEscape="false"
                       maxlength="30" class="input-xlarge "/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">应付金额：</label>
            <div class="controls">
                <input id="toPayTotal" type="text" disabled="disabled"
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
            <label class="control-label">排产状态：</label>
            <div class="controls">
                <input type="text" disabled="disabled"
                       value="${fns:getDictLabel(bizPoHeader.poSchType, 'poSchType', '未排产')}" htmlEscape="false"
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
        <c:if test="${fn:length(photoOrderImgList) > 0}">
            <div class="control-group">
                <label class="control-label">订单图片：</label>
                <div class="controls">
                    <c:forEach items="${photoOrderImgList}" var="v">
                        <a target="_blank" href="${v.imgServer}${v.imgPath}"><img style="width: 100px" src="${v.imgServer}${v.imgPath}"></a>
                    </c:forEach>
                </div>
            </div>
        </c:if>
        <%--<c:if test="${bizPoHeader.bizPoPaymentOrder.id != null || type == 'createPay'}">--%>
            <%--<div class="control-group">--%>
                <%--<label class="control-label">申请金额：</label>--%>
                <%--<div class="controls">--%>
                    <%--<input id="payTotal" name="planPay" type="text"--%>
                           <%--<c:if test="${type == 'audit' || type == 'pay'}">readonly</c:if>--%>
                           <%--value="${bizPoHeader.bizPoPaymentOrder.id != null ?--%>
                           <%--bizPoHeader.bizPoPaymentOrder.total : (bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight-bizPoHeader.payTotal)}"--%>
                           <%--htmlEscape="false" maxlength="30" class="input-xlarge"/>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div class="control-group">--%>
                <%--<label class="control-label">本次申请付款时间：</label>--%>
                <%--<div class="controls">--%>
                    <%--<input name="payDeadline" id="payDeadline" type="text" readonly="readonly" maxlength="20"--%>
                           <%--class="input-medium Wdate required"--%>
                           <%--value="<fmt:formatDate value="${bizPoHeader.bizPoPaymentOrder.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"--%>
                            <%--<c:if test="${type == 'createPay'|| type=='confrim'}"> onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"</c:if>--%>
                           <%--placeholder="必填！"/>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</c:if>--%>
        <c:if test="${type == 'startAudit'}">
            <div class="control-group">
                <label class="control-label">是否同时提交支付申请：</label>
                <div class="controls">
                    <input name="meanwhilePayOrder" id="meanwhilePayOrderRadioFalse" type="radio" onclick="showTimeTotal(false);" checked/>否
                    <input name="meanwhilePayOrder" id="meanwhilePayOrderRadioTrue" type="radio" onclick="showTimeTotal(true);" />是
                </div>
            </div>
            <div class="control-group prewTimeTotal" style="display: none;">
                <label class="control-label">最后付款时间：</label>
                <div class="controls">
                    <input name="prewPayDeadline" id="prewPayDeadline" type="text" readonly="readonly" maxlength="20"
                           class="input-medium Wdate required"
                           value="<fmt:formatDate value="${bizPoHeader.bizPoPaymentOrder.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
                           placeholder="必填！"/>
                </div>
            </div>
            <%--<div class="control-group prewTimeTotal" style="display: none;">--%>
                <%--<label class="control-label">申请金额：</label>--%>
                <%--<div class="controls">--%>
                    <%--<input name="prewPayTotal" id="prewPayTotal" type="text"--%>
                           <%--value="${bizPoHeader.bizPoPaymentOrder.id != null ?--%>
                           <%--bizPoHeader.bizPoPaymentOrder.total : (bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight-bizPoHeader.payTotal)}"--%>
                           <%--maxlength="20" placeholder="必填！"/>--%>
                <%--</div>--%>
            <%--</div>--%>
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


                &nbsp;</shiro:hasPermission>
        </div>
    </c:if>
    <c:if test="${bizOrderHeader.orderType != 6}" >
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
            <th>商品货号</th>
            <c:if test="${bizPoHeader.id==null}">
                <th>申报数量</th>
            </c:if>
            <th>采购数量</th>
            <c:if test="${bizPoHeader.id!=null}">
                <th>已供货数量</th>
            </c:if>
            <!-- 隐藏结算价 -->
            <shiro:hasPermission name="biz:order:unitPrice:view">
                <th>结算价</th>
            </shiro:hasPermission>
        </tr>
        </thead>
        <tbody id="prodInfo">
        <c:if test="${bizPoHeader.poDetailList!=null}">
            <c:forEach items="${bizPoHeader.poDetailList}" var="poDetail">
                <tr>
                    <td><img style="max-width: 120px" src="${poDetail.skuInfo.productInfo.imgUrl}"/></td>
                    <td>${poDetail.skuInfo.productInfo.brandName}</td>
                    <td>${poDetail.skuInfo.name}</td>
                    <td>${poDetail.skuInfo.itemNo}</td>
                    <td>${poDetail.ordQty}</td>
                    <td>${poDetail.sendQty}</td>
                    <!-- 隐藏结算价 -->
                    <shiro:hasPermission name="biz:order:unitPrice:view">
                        <td>${poDetail.unitPrice}</td>
                    </shiro:hasPermission>
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
                            <!-- 隐藏结算价 -->
                            <shiro:hasPermission name="biz:order:unitPrice:view">
                                <td>
                                    <input readonly="readonly" type="text" name="unitPrices"
                                           value="${reqDetail.unitPrice}" class="input-mini">
                                </td>
                            </shiro:hasPermission>
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
                            <!-- 隐藏结算价 -->
                            <shiro:hasPermission name="biz:order:unitPrice:view">
                                <td>
                                    <input readonly="readonly" type="text" name="unitPrices"
                                           value="${orderDetail.buyPrice}" class="input-mini">
                                </td>
                            </shiro:hasPermission>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </c:if>
        </c:if>
        </tbody>

    </table>
        <table id="contentTable2" class="table table-striped table-bordered table-condensed">
            <thead>
            <tr>
                <th>订单号</th>
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
            <c:forEach items="${bizPoHeader.poPaymentOrderList}" var="bizPoPaymentOrder">
                <c:if test="${bizPoPaymentOrder.orderNum != null or bizPoPaymentOrder.reqNo != null}">
                    <tr>
                        <td>
                            <c:if test="${bizPoPaymentOrder.orderNum != null}">
                                <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizPoPaymentOrder.orderId}&orderDetails=details&statu=&source=">
                                        ${bizPoPaymentOrder.orderNum}
                                </a>
                            </c:if>
                            <c:if test="${bizPoPaymentOrder.reqNo != null}">
                                <a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizPoPaymentOrder.requestId}&str=detail">
                                        ${bizPoPaymentOrder.reqNo}
                                </a>
                            </c:if>
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
                            <c:if test="${bizPoPaymentOrder.poHeader.bizStatus != 10}">
                                <shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:audit">
                                    <c:if test="${bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '驳回' && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成' && bizPoPaymentOrder.total != '0.00'}">
                                        <%--&& (fns:hasRole(roleSet, bizPoPaymentOrder.commonProcess.paymentOrderProcess.moneyRole.roleEnNameEnum))--%>
                                        <a href="#" onclick="checkPass(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.orderType})">审核通过</a>
                                        <a href="#" onclick="checkReject(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.orderType})">审核驳回</a>
                                    </c:if>
                                </shiro:hasPermission>
                                <%--<shiro:hasPermission name="biz:po:sure:bizPoPaymentOrder">--%>
                                    <c:if test="${bizPoHeader.fromPage == 'requestHeader' && bizPoPaymentOrder.total == '0.00' && (requestHeader == null || requestHeader.bizStatus < ReqHeaderStatusEnum.CLOSE.state)}">
                                        <a href="${ctx}/biz/po/bizPoPaymentOrder/form?id=${bizPoPaymentOrder.id}&poHeaderId=${bizPoHeader.id}&fromPage=${fromPage}">确认支付金额222</a>
                                    </c:if>

                                    <c:if test="${bizPoHeader.fromPage == 'orderHeader' && bizPoPaymentOrder.total == '0.00'}">
                                        <a href="#" data-toggle="modal" data-target="#myModal">确认支付金额333</a>

                                        <%--<button class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal"></button>--%>

                                    </c:if>
                                <%--</shiro:hasPermission>--%>
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
                                                <a href="${ctx}/biz/order/bizOrderHeader/form?bizPoHeader.id=${bizPoHeader.id}&id=${orderId}&str=pay">确认付款</a>
                                                <%--<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=pay">确认付款</a>--%>
                                            </c:if>
                                            <c:if test="${fromPage == null}">
                                                <a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=pay">确认付款</a>
                                            </c:if>
                                        </c:if>
                                    </shiro:hasPermission>
                                </shiro:hasPermission>
                                <a href="${ctx}/biz/po/bizPoPaymentOrder/formV2?id=${bizPoPaymentOrder.id}">详情</a>
                            </c:if>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="form-actions">
        <shiro:hasPermission name="biz:po:bizPoHeader:edit">

        </shiro:hasPermission>
        <c:if test="${not empty bizPoHeader.str && bizPoHeader.str eq 'detail'}">
            <input onclick="window.print();" type="button" class="btn btn-primary" value="打印采购订单"
                   style="background:#F78181;"/>
            &nbsp;&nbsp;&nbsp;
        </c:if>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

<!-- 模态框（Modal） -->
<div class="modal fade hide" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    申请付款金额
                </h4>
            </div>
            <div class="modal-body">
                <form:form id="inputForm" modelAttribute="bizPoPaymentOrder" action="${ctx}/biz/po/bizPoHeader/save?prewStatus=prew"
                           method="post" class="form-horizontal">
                <div class="control-group">
                    <label class="control-label">付款金额：</label>
                    <div class="controls">
                        <form:input id="payTotal" path="total" htmlEscape="false" class="input-xlarge "/>
                    </div>
                </div>
                <div class="control-group">
                <label class="control-label">本次申请付款时间：</label>
                <div class="controls">
                    <input name="payDeadline" id="payDeadline" type="text" readonly="readonly" maxlength="20"
                        class="input-medium Wdate required"
                     value="<fmt:formatDate value="${bizPoHeader.bizPoPaymentOrder.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
                 onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
                placeholder="必填！"/>
                </div>
                </div>
                <div class="control-group">
                    <label class="control-label">备注：</label>
                    <div class="controls">
                        <form:textarea path="remark" maxlength="200" class="input-xlarge "/>
                    </div>
                </div>
                </form:form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default"
                        data-dismiss="modal">关闭
                </button>
                <button id="savePayment" type="button" class="btn btn-primary">
                    保存
                </button>
            </div>
        </div>
    </div>
</div>



<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
<script type="text/javascript">
    function startRejectAudit() {
        top.$.jBox.confirm("确认驳回流程吗？","系统提示",function(v,h,f){
            if(v=="ok"){
                var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
                var submit = function (v, h, f) {
                    if ($String.isNullOrBlank(f.description)) {
                        jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                        return false;
                    }
                    var id = $("#id").val();
                    var prew = false;
                    $.ajax({
                        url: '${ctx}/biz/po/bizPoHeader/startAudit',
                        contentType: 'application/json',
                        data: {"id": id, "prew":prew,  "auditType":2, "desc": f.description},
                        type: 'get',
                        success: function (result) {
                            alert(result);
                            if(result == '操作成功!') {
                                window.location.href = "${ctx}/biz/po/bizPoHeader";
                            }
                        },
                        error: function (error) {
                            console.info(error);
                        }
                    });
                    return true;
                };

                jBox(html, {
                    title: "请输入驳回理由:", submit: submit, loaded: function (h) {
                    }
                });
            }
        },{buttonsFocus:1});
    }


    function showTimeTotal(show) {
        if (show) {
            $(".prewTimeTotal").show();
            return;
        }
        $(".prewTimeTotal").hide();
    }

    function startAudit() {
        var prew = false;
        var prewPayTotal = $("#prewPayTotal").val();
        var prewPayDeadline = $("#prewPayDeadline").val();
        if ($("#meanwhilePayOrderRadioTrue").attr("checked") == "checked") {
            if ($String.isNullOrBlank(prewPayTotal)) {
                alert("请输入申请金额");
                return false;
            }
            if ($String.isNullOrBlank(prewPayDeadline)) {
                alert("请选择日期");
                return false;
            }
            prew = true;
        }
        var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认开始审核流程吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    var id = $("#id").val();
                    $.ajax({
                        url: '${ctx}/biz/po/bizPoHeader/startAudit',
                        contentType: 'application/json',
                        data: {
                            "id": id,
                            "prew": prew,
                            "prewPayTotal": prewPayTotal,
                            "prewPayDeadline": prewPayDeadline,
                            "desc": f.description
                        },
                        type: 'get',
                        success: function (result) {
                            result = JSON.parse(result);
                            if(result.ret == true || result.ret == 'true') {
                                alert('操作成功!');
                                window.location.href = "${ctx}/biz/po/bizPoHeader";
                            }else {
                                alert(result.errmsg);
                            }
                        },
                        error: function (error) {
                            console.info(error);
                        }
                    });
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入通过理由:", submit: submit, loaded: function (h) {
            }
        });


    }

    function checkPass() {
        var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认审核通过吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    audit(1, f.description);
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入通过理由:", submit: submit, loaded: function (h) {
            }
        });

    }

    function checkReject() {
        var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认驳回该流程吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    audit(2, f.description);
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入驳回理由:", submit: submit, loaded: function (h) {
            }
        });

    }

    function audit(auditType, description) {
        var id = $("#id").val();
        var currentType = $("#currentType").val();
        $.ajax({
            url: '${ctx}/biz/po/bizPoHeader/audit',
            contentType: 'application/json',
            data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description, "fromPage": "poHeader"},
            type: 'get',
            success: function (result) {
                result = JSON.parse(result);
                if(result.ret == true || result.ret == 'true') {
                    alert('操作成功!');
                    window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor";
                }else {
                    alert(result.errmsg);
                }
            },
            error: function (error) {
                console.info(error);
            }
        });
    }


    function pay() {
        var id = $("#id").val();
        var paymentOrderId = $("#paymentOrderId").val();
        var payTotal = $("#truePayTotal").val();

        var mainImg = $("#payImgDiv").find("[customInput = 'payImgImg']");
        var img = "";
        if(mainImg.length >= 2) {
            for (var i = 1; i < mainImg.length; i ++) {
                img += $(mainImg[i]).attr("src") + ",";
            }
        }
        if ($String.isNullOrBlank(payTotal)) {
            alert("错误提示:请输入支付金额");
            return false;
        }
        if ($String.isNullOrBlank(img)) {
            alert("错误提示:请上传支付凭证");
            return false;
        }

        $.ajax({
            url: '${ctx}/biz/po/bizPoHeader/payOrder',
            contentType: 'application/json',
            data: {"poHeaderId": id, "paymentOrderId": paymentOrderId, "payTotal": payTotal, "img": img},
            type: 'get',
            success: function (result) {
                alert(result);
                if(result == '操作成功!') {
                    window.location.href = "${ctx}/biz/po/bizPoHeader";
                }
            },
            error: function (error) {
                console.info(error);
            }
        });

    }

    function submitPic(id, multiple){
        var f = $("#" + id).val();
        if(f==null||f==""){
            alert("错误提示:上传文件不能为空,请重新选择文件");
            return false;
        }else{
            var extname = f.substring(f.lastIndexOf(".")+1,f.length);
            extname = extname.toLowerCase();//处理了大小写
            if(extname!= "jpeg"&&extname!= "jpg"&&extname!= "gif"&&extname!= "png"){
                $("#picTip").html("<span style='color:Red'>错误提示:格式不正确,支持的图片格式为：JPEG、GIF、PNG！</span>");
                return false;
            }
        }
        var file = document.getElementById(id).files;
        var size = file[0].size;
        if(size>2097152){
            alert("错误提示:所选择的图片太大，图片大小最多支持2M!");
            return false;
        }
        ajaxFileUploadPic(id, multiple);
    }

    function ajaxFileUploadPic(id, multiple) {
        $.ajaxFileUpload({
            url : '${ctx}/biz/product/bizProductInfoV2/saveColorImg', //用于文件上传的服务器端请求地址
            secureuri : false, //一般设置为false
            fileElementId : id, //文件上传空间的id属性  <input type="file" id="file" name="file" />
            type : 'POST',
            dataType : 'text', //返回值类型 一般设置为json
            success : function(data, status) {
                //服务器成功响应处理函数
                var msg = data.substring(data.indexOf("{"), data.indexOf("}")+1);
                var msgJSON = JSON.parse(msg);
                var imgList = msgJSON.imgList;
                var imgDiv = $("#" + id + "Div");
                var imgDivHtml = "<img src=\"$Src\" customInput=\""+ id +"Img\" style='width: 100px' onclick=\"$(this).remove();\">";
                if (imgList && imgList.length > 0 && multiple) {
                    for (var i = 0; i < imgList.length; i ++) {
                        imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                    }
                }else if (imgList && imgList.length > 0 && !multiple) {
                    imgDiv.empty();
                    for (var i = 0; i < imgList.length; i ++) {
                        imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                    }
                }else {
                    var img = $("#" + id + "Img");
                    img.attr("src", msgJSON.fullName);
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
        return false;
    }
</script>
</body>
</html>
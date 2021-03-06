<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>发货单管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript" src="${ctxStatic}/tablesMergeCell/tablesMergeCell.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $('#select_all').live('click', function () {
                var choose = $("input[type='checkbox']");
                if ($(this).attr('checked')) {
                    choose.attr('checked', true);
                } else {
                    choose.attr('checked', false);
                }
            });
            var str = $("#str").val();
            if (str != 'audit') {
                $(":input").each(function () {
                    $(this).attr("disabled","disabled");
                });
                $("select").each(function () {
                    $(this).attr("disabled","disabled");
                });
                $("textarea").each(function () {
                    $(this).attr("disabled","disabled");
                });
                $("#id").removeAttr("disabled");
                $("#btnCancel").removeAttr("disabled");
                $("#btnSubmit").removeAttr("disabled");
                $("#freight").removeAttr("disabled");
            }
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    var tt = "";
                    var flag = false;
                    var total = 0;
                    var freight = $("#freight").val();
                    if (str == 'audit') {
                        $("td[name='orderNo']").each(function (i) {
                            var t = $(this).parent().attr("id");
                            var detail = "";
                            var num = "";
                            var sObj = $("#prodInfo3").find("input[title='sent_" + t + "']");
                            var iObj = $("#prodInfo").find("select[title='invInfoId']");
                            var tObj = $("#prodInfo").find("select[title='skuType']");
                            sObj.each(function (index) {
                                total += parseInt($(this).val());
                            });
                            if (iObj.length != 0) {
                                iObj.each(function (index) {
                                    if ($(this).val() != '') {
                                        flag = true;
                                    }
                                });
                                $("#prodInfo").find("input[title='details_" + t + "']").each(function (i) {
                                    detail += $(this).val() + "-" + sObj[i].value + "-" + iObj[i].value + "-" + tObj[i].value + "*";
                                });
                            } else {
                                flag = true;
                                $("#prodInfo3").find("input[title='details_" + t + "']").each(function (i) {
                                    detail += $(this).val() + "-" + sObj[i].value + "*";

                                });
                            }
                            tt += t + "#" + detail + ",";
                        });
                        tt = tt.substring(0, tt.length - 1);
                    }
                    if (window.confirm('你确定要发货吗？')) {
                        if (total <= 0 && freight == undefined) {
                            alert("发货数量不能为0");
                            return false;
                        }
                        if (tt != '') {
                            $("#prodInfo3").append("<input name='orderHeaders' type='hidden' value='" + tt + "'>");
                        }
                        var orderHeaders = $("input[name='orderHeaders']").val();
                        var flag = orderHeaders != undefined;
                        if (${bizInvoice.bizStatus == 0 && flag}) {
                            $.ajax({
                                type: "post",
                                url: "${ctx}/biz/inventory/bizInventorySku/findInvSku?orderHeaders=" + encodeURIComponent(orderHeaders),
                                success: (function (data) {
                                    if (data == "true") {
                                        form.submit();
                                        return true;
                                        loading('正在提交，请稍等...');
                                    } else {
                                        $("input[name='orderHeaders']").each(function () {
                                            $(this).remove();
                                        });
                                        alert("库存不足！");
                                        return false;
                                    }
                                })
                            })
                        } else {
                            form.submit();
                            return true;
                            loading('正在提交，请稍等...');
                        }
                    } else {
                        return false;
                    }
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

            $("#searchData").click(function () {
                var orderNum = $("#orderNum").val();
                $("#orderNumCopy").val(orderNum);
                var skuItemNo = $("#skuItemNo").val();
                $("#skuItemNoCopy").val(skuItemNo);
                var skuCode = $("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                var bizStatus = $("#bizStatus").val();
                var name = $("#name").val();
                $("#nameCopy").val(name);
                $.ajax({
                    type: "post",
                    url: "${ctx}/biz/order/bizOrderHeader/findByOrder?flag=" + bizStatus,
                    data: $('#searchForm').serialize(),
                    success: function (data) {
                        if ($("#id").val() == '') {
                            $("#prodInfo2").empty();
                        }

                        if (bizStatus == 0) {
                            var selecttd = "<select class='input-mini' title='invInfoId'><option value='" + data.inventoryInfoList[0].id + "'>" + data.inventoryInfoList[0].name + "</option>";
                            $.each(data.inventoryInfoList, function (index, inventory) {
                                selecttd += "<option value='" + inventory.id + "'>" + inventory.name + "</option>"
                            });
                            var skuType = "<select class='input-mini' title='skuType'><option value='1'>采购中心</option><option value='2'>供应商</option></select>";
                        }
                        var tr_tds = "";
                        var bizName = "";
                        $.each(data.bizOrderHeaderList, function (index, orderHeader) {
                            if (orderHeader.bizStatus == 15) {
                                bizName = "供货中"
                            }
                            if (orderHeader.bizStatus == 17) {
                                bizName = "采购中"
                            } else if (orderHeader.bizStatus == 18) {
                                bizName = "采购完成"
                            } else if (orderHeader.bizStatus == 19) {
                                bizName = "供应商供货"
                            } else if (orderHeader.bizStatus == 20) {
                                bizName = "已发货"
                            }

                            var flag = true;
                            var deId = "";
                            var num = "";
                            $.each(orderHeader.orderDetailList, function (index, detail) {

                                tr_tds += "<tr class='tr_" + orderHeader.id + "'>";

                                if (flag) {
                                    tr_tds += "<td rowspan='" + orderHeader.orderDetailList.length + "'><input type='checkbox' value='" + orderHeader.id + "' /></td>";

                                    tr_tds += "<td rowspan='" + orderHeader.orderDetailList.length + "'><a href='${ctx}/biz/order/bizOrderHeader/form?id=" + orderHeader.id + "&orderDetails=details'> " + orderHeader.orderNum + "</a></td><td rowspan='" + orderHeader.orderDetailList.length + "'>" + orderHeader.customer.name + "</td><td rowspan='" + orderHeader.orderDetailList.length + "'>" + bizName + "</td>";
                                }
                                tr_tds += "<input title='details_" + orderHeader.id + "' name='' type='hidden' value='" + detail.id + "'>";
                                tr_tds += "<td>" + detail.skuInfo.name + "</td><td>" + detail.vendor.name + "</td><td>" + (detail.skuInfo.itemNo == undefined ? "" : detail.skuInfo.itemNo) + "</td><td>" + detail.skuInfo.partNo + "</td><td>" + detail.skuInfo.skuPropertyInfos + "</td>";
                                if (bizStatus == 0) {
                                    tr_tds += "<td>" + selecttd + "</td>";
                                    tr_tds += "<td>" + skuType + "</td>";
                                }
                                tr_tds += "<td>" + detail.ordQty + "</td><td>" + detail.sentQty + "</td>";
                                if (detail.ordQty == detail.sentQty) {
                                    tr_tds += "<td><input  type='text' readonly='readonly' title='sent_" + orderHeader.id + "' name='' value='0'></td>";
                                } else {
                                    tr_tds += "<td><input  type='text'  title='sent_" + orderHeader.id + "' name='' onchange='checkNum(" + detail.ordQty + "," + detail.sentQty + ",this)' value='" + (detail.ordQty - detail.sentQty) + "'></td>";
                                }

                                tr_tds += "</tr>";
                                if (orderHeader.orderDetailList.length > 1) {
                                    flag = false;
                                }
                            });

                        });
                        $("#prodInfo2").append(tr_tds);
                    }
                });
            });
            <%--点击确定时获取订单详情--%>
            $("#ensureData").click(function () {
                $('input:checkbox:checked').each(function (i) {
                    var t = $(this).val();
                    var ttp = $(this).parent().parent().parent();
                    var trt = ttp.find($(".tr_" + t))
                    $("#prodInfo").append(trt);
                });
                $("#select_all").removeAttr("checked");

            });

            $("#contentTable3").tablesMergeCell({
                // automatic: true
                // 是否根据内容来合并
                cols: [0, 1, 2]
                // rows:[0,2]
            });

        });

        function checkNum(ordQty, sentQty, sendQty) {
            if (parseInt(sendQty.value) + parseInt(sentQty) > parseInt(ordQty)) {
                alert("发货数量大于需求数量，请修改");
                $(sendQty).val(0);
            }
        }


    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/inventory/bizInvoice?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单列表</a>
    </li>
    <li class="active"><a
            href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单<shiro:hasPermission
            name="biz:inventory:bizInvoice:edit">${not empty bizInvoice.id?'详情':'详情'}</shiro:hasPermission><shiro:lacksPermission
            name="biz:inventory:bizInvoice:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizInvoice"
           action="${ctx}/biz/inventory/bizInvoice/save?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}"
           method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <input id="str" name="str" type="hidden" value="${bizInvoice.str}"/>
    <input id="creOrdLogistics" name="creOrdLogistics" type="hidden" value="${bizInvoice.creOrdLogistics}"/>
    <sys:message content="${message}"/>
    <c:if test="${bizInvoice.id != null && bizInvoice.id != ''}">
        <div class="control-group">
            <label class="control-label">发货单号：</label>
            <div class="controls">
                <form:input path="sendNumber" htmlEscape="false" disabled="true" class="input-xlarge "/>
            </div>
        </div>
    </c:if>
    <div class="control-group">
        <label class="control-label">物流单号：</label>
        <div class="controls">
            <form:input path="trackingNumber" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">物流信息图：</label>
        <div class="controls">
                <%--<img src="${imgUrl}"style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/>--%>
            <c:choose>
                <c:when test="${bizInvoice.str == 'freight' || source == 'xq'}">
                    <c:forEach items="${imgList}" var="img">
                        <a href="${img.imgServer}${img.imgPath}" target="_blank"><img src="${img.imgServer}${img.imgPath}" style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/></a>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <form:hidden path="imgUrl" htmlEscape="false" maxlength="255" class="input-xlarge"/>
                    <sys:ckfinder input="imgUrl" type="images" uploadPath="/logistics/info" selectMultiple="true" maxWidth="100"
                                  maxHeight="100"/>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <%--<div class="control-group">--%>
        <%--<label class="control-label">验货员：</label>--%>
        <%--<div class="controls">--%>
            <%--<form:select about="choose" path="inspector.id" class="input-medium required">--%>
                <%--<form:option value="" label="请选择"/>--%>
                <%--<form:options items="${inspectorList}" itemLabel="name" itemValue="id" htmlEscape="false"/>--%>
            <%--</form:select>--%>
            <%--<span class="help-inline"><font color="red">*</font> </span>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">验货时间：</label>--%>
        <%--<div class="controls">--%>
            <%--<input name="inspectDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"--%>
                   <%--value="<fmt:formatDate value="${bizInvoice.inspectDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>"--%>
                   <%--onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>--%>
            <%--<span class="help-inline"><font color="red">*</font> </span>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">验货备注：</label>--%>
        <%--<div class="controls">--%>
            <%--<form:textarea path="inspectRemark" htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">集货地点：</label>--%>
        <%--<div class="controls">--%>
            <%--<form:select path="collLocate" htmlEscape="false" maxlength="30" class="input-xlarge required">--%>
                <%--<form:option value="" label="请选择"/>--%>
                <%--<form:options items="${fns:getDictList('coll_locate')}" itemValue="value" itemLabel="label"/>--%>
            <%--</form:select>--%>
            <%--<span class="help-inline"><font color="red">*</font> </span>--%>
        <%--</div>--%>
    <%--</div>--%>
    <div class="control-group">
        <label class="control-label">货值：</label>
        <div class="controls">
            <form:input id="valuePrice" path="valuePrice" htmlEscape="false" value="" readonly="true"
                        class="input-xlarge required"/>
        </div>
    </div>
    <c:if test="${bizInvoice.str == 'freight' || source == 'xq'}">
        <div class="control-group">
            <label class="control-label">运费：</label>
            <div class="controls">
                <form:input path="freight" htmlEscape="false" class="input-xlarge required"/>
            </div>
        </div>
    </c:if>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">配送服务费：</label>--%>
        <%--<div class="controls">--%>
            <%--<input value="${orderHeader.freight}" htmlEscape="false" disabled="disabled" class="input-xlarge required"/>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<c:if test="${userList==null}">--%>
        <%--<div class="control-group">--%>
            <%--<label class="control-label">发货人：</label>--%>
            <%--<div class="controls">--%>
                <%--<form:input about="choose" readonly="true" path="carrier" class="input-medium required"/>--%>
                <%--<span class="help-inline"><font color="red">*</font> </span>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</c:if>--%>
    <%--<c:if test="${userList!=null}">--%>
        <%--<div class="control-group">--%>
            <%--<label class="control-label">发货人：</label>--%>
            <%--<div class="controls">--%>
                <%--<form:select about="choose" path="carrier" class="input-medium required">--%>
                    <%--<form:option value="" label="请选择"/>--%>
                    <%--<form:options items="${userList}" itemLabel="name" itemValue="name" htmlEscape="false"/>--%>
                <%--</form:select>--%>
                <%--<span class="help-inline"><font color="red">*</font> </span>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</c:if>--%>
    <div class="control-group">
        <label class="control-label">集货地点：</label>
        <div class="controls">
            <form:select path="collLocate" htmlEscape="false" maxlength="30" class="input-xlarge required">
                <form:option value="" label="请选择"/>
                <form:options items="${collLocateList}" itemValue="value" itemLabel="label"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">发货时间：</label>
        <div class="controls">
            <input name="sendDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
                   value="<fmt:formatDate value="${bizInvoice.sendDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">物流结算方式：</label>--%>
        <%--<div class="controls">--%>
            <%--<form:select id="settlementStatus" path="settlementStatus" onmouseout="" class="input-xlarge required">--%>
                <%--<c:forEach items="${fns:getDictList('biz_settlement_status')}" var="settlementStatus">--%>
                    <%--<option value="${settlementStatus.value}">${settlementStatus.label}</option>--%>
                <%--</c:forEach>--%>
            <%--</form:select>--%>
            <%--<span class="help-inline"><font color="red">*</font> </span>--%>
        <%--</div>--%>
    <%--</div>--%>
    <div class="control-group">
        <label class="control-label">备注：</label>
        <div class="controls">
            <form:textarea path="remarks" htmlEscape="false" maxlength="200" class="input-xlarge "/>
        </div>
    </div>
    <c:if test="${source ne 'xq'}">
        <c:if test="${bizInvoice.id == null}">
            <div class="control-group">
                <label class="control-label">选择订单：</label>
                <div class="controls">
                    <ul class="inline ul-form">
                        <li><label>订单编号：</label>
                            <input id="orderNum" onkeydown='if(event.keyCode==13) return false;' htmlEscape="false"
                                   maxlength="50" class="input-medium"/>
                        </li>
                        <li><label>商品货号：</label>
                            <input id="skuItemNo" onkeydown='if(event.keyCode==13) return false;' htmlEscape="false"
                                   class="input-medium"/>
                        </li>
                        <li><label>商品编码：</label>
                            <input id="skuCode" onkeydown='if(event.keyCode==13) return false;' htmlEscape="false"
                                   class="input-medium"/>
                        </li>
                        <li><label>供应商：</label>
                            <input id="name" onkeydown='if(event.keyCode==13) return false;' htmlEscape="false"
                                   class="input-medium"/>
                        </li>
                        <li class="btns"><input id="searchData" class="btn btn-primary" type="button" value="查询"/><span
                                style="color: red;">(请输入没有供货完成的订单)</span></li>
                        <li class="clearfix"></li>
                    </ul>

                </div>
            </div>

            <div class="control-group">
                <label class="control-label">待发货订单：</label>
                <div class="controls">
                    <table id="contentTable2" class="table table-striped table-bordered table-condensed">
                        <thead>
                        <tr>
                            <th><input id="select_all" type="checkbox"/></th>
                            <th>订单编号</th>
                            <th>经销店名称</th>
                            <th>业务状态</th>
                            <th>商品名称</th>
                            <th>供应商</th>
                            <th>商品货号</th>
                            <th>商品编码</th>
                            <th>商品属性</th>
                            <c:if test="${bizInvoice.bizStatus==0}">
                                <th>选择仓库</th>
                                <th>选择货权方</th>
                            </c:if>
                            <th>采购数量</th>
                            <th>已发货数量</th>
                            <th>发货数量</th>

                        </tr>
                        </thead>
                        <tbody id="prodInfo2">

                        </tbody>
                    </table>
                    <input id="ensureData" class="btn btn-primary" type="button" value="确定"/>
                </div>

                <div class="controls">
                    <table id="contentTable" class="table table-striped table-bordered table-condensed">
                        <thead>
                        <tr>
                            <th></th>
                            <th>订单编号</th>
                            <th>经销店名称</th>
                            <th>业务状态</th>
                            <th>商品名称</th>
                            <th>供应商</th>
                            <th>商品货号</th>
                            <th>商品编码</th>
                            <th>商品属性</th>
                            <c:if test="${bizInvoice.bizStatus==0}">
                                <th>选择仓库</th>
                                <th>选择货权方</th>
                            </c:if>
                            <th>采购数量</th>
                            <th>已发货数量</th>
                            <th>发货数量</th>
                        </tr>
                        </thead>
                        <tbody id="prodInfo">
                        <input name="bizStatu" value="1" type="hidden"/>
                        </tbody>
                    </table>
                        <%--<input id="ensureData" class="btn btn-primary" type="button"  value="确定"/>--%>
                </div>
            </div>
        </c:if>
    </c:if>
    <div class="control-group">
        <label class="control-label">已发货详情：</label>
        <div class="controls">
            <table id="contentTable3" class="table table-striped table-bordered table-condensed">
                <thead>
                <tr>
                    <th>订单编号</th>
                    <th>经销店名称</th>
                    <th>业务状态</th>
                    <th>商品名称</th>
                    <th>商品货号</th>
                    <th>采购数量</th>
                    <th>已发货数量</th>
                    <c:if test="${bizInvoice.str == 'audit'}">
                        <th>发货数量</th>
                    </c:if>
                </tr>
                </thead>
                <tbody id="prodInfo3">
                    <%--<c:if test="${orderHeaderList!=null && orderHeaderList.size()>0}">--%>
                    <%--<c:forEach items="${orderHeaderList}" var="orderHeader">--%>
                    <%--<c:set var="flag" value="true"></c:set>--%>
                    <%--<c:forEach items="${orderHeader.orderDetailList}" var="orderDetail" varStatus="index">--%>
                    <%--<tr>--%>
                    <%--<c:if test="${flag}">--%>
                    <%--<td rowspan="${fn:length(orderHeader.orderDetailList)}">${orderHeader.orderNum}</td>--%>
                    <%--<td rowspan="${fn:length(orderHeader.orderDetailList)}">${orderHeader.customer.name}</td>--%>
                    <%--<td rowspan="${fn:length(orderHeader.orderDetailList)}">${fns:getDictLabel(orderHeader.bizStatus,"biz_order_status",'' )}</td>--%>
                    <%--</c:if>--%>
                    <%--<td>${orderDetail.skuName}</td>--%>
                    <%--<td>${orderDetail.skuInfo.itemNo}</td>--%>
                    <%--<td>${orderDetail.ordQty}</td>--%>
                    <%--<td>${orderDetail.sentQty}</td>--%>
                    <%--</tr>--%>
                    <%--<c:if test="${fn:length(orderHeader.orderDetailList)>1}">--%>
                    <%--<c:set var="flag" value="false"></c:set>--%>
                    <%--</c:if>--%>
                    <%--</c:forEach>--%>
                    <%--</c:forEach>--%>
                    <%--</c:if>--%>
                <c:set var="flag" value="true"></c:set>
                <c:forEach items="${orderDetailList}" var="orderDetail">
                    <tr id="${orderDetail.orderHeader.id}">
                        <c:if test="${flag}">
                            <td name="orderNo" rowspan="${fn:length(orderDetailList)}"><a
                                    href="${ctx}/biz/order/bizOrderHeader/form?id=${orderDetail.orderHeader.id}&orderDetails=details">${orderDetail.orderHeader.orderNum}</a>
                            </td>
                        </c:if>
                        <%--<td name="orderNo">--%>
                            <%--<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderDetail.orderHeader.id}&orderDetails=details">${orderDetail.orderHeader.orderNum}</a>--%>
                        <%--</td>--%>
                        <td>${orderDetail.cust.name}</td>
                        <td>${fns:getDictLabel(orderDetail.orderHeader.bizStatus,"biz_order_status",'' )}</td>
                        <td>${orderDetail.skuInfo.name}</td>
                        <td>${orderDetail.skuInfo.itemNo}</td>
                        <td>${orderDetail.ordQty}</td>
                        <td>${orderDetail.sentQty}</td>
                        <c:if test="${bizInvoice.str == 'audit'}">
                            <input type="hidden" title="details_${orderDetail.orderHeader.id}" value="${orderDetail.id}"/>
                            <td><input type='text' title='sent_${orderDetail.orderHeader.id}' name='' required="required" onchange='checkNum(${orderDetail.ordQty},${orderDetail.sentQty},this)' value='0'></td>
                        </c:if>
                    </tr>
                    <c:if test="${fn:length(orderDetailList)>1}">
                        <c:set var="flag" value="false"></c:set>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="form-actions">
        <input onclick="window.print();" type="button" class="btn btn-primary" value="打印发货单"
               style="background:#F78181;"/>
        <c:if test="${source ne 'xq'}">
            <shiro:hasPermission name="biz:inventory:bizInvoice:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                             type="submit"
                                                                             value="保 存"/>&nbsp;</shiro:hasPermission>
        </c:if>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>

<form:form id="searchForm" modelAttribute="bizOrderHeader">
    <input type="hidden" id="orderNumCopy" name="orderNum" value="${bizOrderHeader.orderNum}"/>
    <input type="hidden" id="skuItemNoCopy" name="itemNo" value="${bizOrderHeader.itemNo}"/>
    <input type="hidden" id="skuCodeCopy" name="partNo" value="${bizOrderHeader.partNo}"/>
    <input type="hidden" id="nameCopy" name="name" value="${bizOrderHeader.name}"/>
</form:form>
</body>
</html>
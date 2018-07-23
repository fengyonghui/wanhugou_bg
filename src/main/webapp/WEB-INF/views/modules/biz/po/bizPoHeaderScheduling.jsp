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
            $("#contentTable2").tablesMergeCell({
                // automatic: true
                // 是否根据内容来合并
                cols: [0]
                // rows:[0,2]
            });
        });

        //批量排产
        function batchSave() {
            var poDetailList = '${bizPoHeader.poDetailList.size()}';
            var params = new Array()

            for (var i=0; i<poDetailList;i++) {
                var entity = {};
                var detailId = $(eval("detailId_" + (i+1))).text();
                var ordQty = $(eval("ordQty_" + (i+1))).text();
                var schedulingNum = $(eval("schedulingNum_" + (i+1))).val();
                var sumSchedulingNum = $(eval("sumSchedulingNum_" + (i+1))).text();
                var standard = ordQty - sumSchedulingNum;
                if (parseInt(schedulingNum) < 0 || (parseInt(schedulingNum) > parseInt(standard))){
                    alert("排产量数值设置不正确，请重新输入")
                    return false;
                }
                entity.objectId = detailId;
                entity.originalNum = ordQty;
                entity.schedulingNum = schedulingNum;
                entity.completeNum = 0;
                params[i] = entity;
            }
            if(confirm("确定执行批量排产吗？")){
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/batchSaveSchedulingPlan',
                    contentType: 'application/json',
                    data:JSON.stringify(params),
                    datatype:"json",
                    type: 'post',
                    success: function (result) {
                        if(result == true) {
                            window.location.href = "${ctx}/biz/po/bizPoHeader/scheduling?id="+${bizPoHeader.id};
                        }
                    },
                    error: function (error) {
                        console.info(error);
                    }
                });
            }
        }

        //添加排产量check
        function addSchedulingCheck(index, detailId) {
            var ordQty = $(eval("ordQty_" + index)).text();
            var schedulingNum = $(eval("schedulingNum_" + index)).val();
            var sumSchedulingNum = $(eval("sumSchedulingNum_" + index)).text();
            var standard = ordQty - sumSchedulingNum;
            if (parseInt(schedulingNum) <= 0 || (parseInt(schedulingNum) > parseInt(standard))){
                alert("排产量数值设置不正确，请重新输入")
                return false;
            }
            if(confirm("确定添加排产量为" + schedulingNum + "的排产吗？")){
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/saveSchedulingPlan',
                    contentType: 'application/json',
                    data: {"detailId": detailId, "ordQty": ordQty, "schedulingNum": schedulingNum, "completeNum": 0},
                    type: 'get',
                    success: function (result) {
                        if(result == true) {
                            window.location.href = "${ctx}/biz/po/bizPoHeader/scheduling?id="+${bizPoHeader.id};
                        }
                    },
                    error: function (error) {
                        console.info(error);
                    }
                });
            }
        }
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
<form:form id="inputForm" modelAttribute="bizPoHeader" action=""
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
    </c:if>

    <c:if test="${bizOrderHeader.orderType != 6}" >
    <table id="contentTable2" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>产品图片</th>
            <th>品牌名称</th>
            <th>商品名称</th>
            <th>商品货号</th>
            <c:if test="${bizPoHeader.id!=null}">
                <th>所属单号</th>
            </c:if>
            <th>采购数量</th>
            <th>已排产量</th>
            <th>待排产数量</th>
            <th>工厂价</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody id="prodInfo2">
        <c:if test="${bizPoHeader.poDetailList!=null}">
            <c:forEach items="${bizPoHeader.poDetailList}" var="poDetail" varStatus="state">
                <tr>
                    <td style="display: none">${state.index+1}</td>
                    <td id="detailId_${state.index+1}" style="display: none">${poDetail.id}</td>
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
                    <td id="ordQty_${state.index+1}">${poDetail.ordQty}</td>

                    <td id="sumSchedulingNum_${state.index+1}" >${poDetail.sumSchedulingNum}</td>
                    <td>
                        <input type="text" id="schedulingNum_${state.index+1}" style="margin-bottom: 10px" value="${poDetail.ordQty - poDetail.sumSchedulingNum}"
                               htmlEscape="false" maxlength="30" class="input-xlarge "/>
                    </td>
                    <td>${poDetail.unitPrice}</td>
                    <td>
                        <c:choose>
                            <c:when test="${poDetail.ordQty != poDetail.sumSchedulingNum}">
                                <input id="addScheduling" class="btn btn-primary" type="button" onclick="addSchedulingCheck('${state.index+1}','${poDetail.id}')" value="保存"/>&nbsp;
                            </c:when>
                            <c:otherwise>
                                <input id="addScheduling_alert" class="btn btn-primary" type="button" disabled="true" value="排产完成"/>&nbsp;
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </c:if>
        </tbody>
    </table>
    </c:if>

    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        &nbsp;&nbsp;
        <c:if test="${bizPoHeader.poDetailList!=null}">
            <input id="batchSubmit" class="btn btn-primary" type="button" onclick="batchSave()" value="保存"/>&nbsp;
        </c:if>
    </div>
</form:form>
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
</body>
</html>
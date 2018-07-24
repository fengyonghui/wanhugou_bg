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
            // $("#contentTable").tablesMergeCell({
            //     // automatic: true
            //     // 是否根据内容来合并
            //     cols: [0]
            //     // rows:[0,2]
            // });
            // $("#contentTable2").tablesMergeCell({
            //     // automatic: true
            //     // 是否根据内容来合并
            //     cols: [0]
            //     // rows:[0,2]
            // });
            var schedulingPlanList = JSON.parse('${schedulingPlanList}');
            var jsonLength = schedulingPlanList.length;
            if(jsonLength == 0) {
                $("#batchSubmit").hide();
            } else {
                var id = '${bizPoHeader.id}'
                checkResult(id);
            }
        });

        <%--/**--%>
         <%--* 批量保存--%>
         <%--* @returns {boolean}--%>
         <%--*/--%>
        function batchUpdate() {
            var schedulingPlanList = JSON.parse('${schedulingPlanList}');
            var params = new Array();
            var count = 0;
            for(var index in schedulingPlanList) {
                var id = schedulingPlanList[index];

                var divArray = $("[name='" + id + "']");
                for(i=0;i<divArray.length;i++){
                    var div = divArray[i];
                    var jqDiv = $(div);
                    var date = jqDiv.find("[name='" + id + "_date']").val();
                    var value = jqDiv.find("[name='" + id + "_value']").val();
                    if(date == null || date == ""){
                        alert("排产日期不能为空!")
                        return false;
                    }
                    var reg= /^[1-9]+[0-9]*]*$/;
                    if(value == null || value == "" || !reg.test(value)){
                        alert("确认值输入不正确!")
                        return false;
                    }
                    var entity = {};
                    entity.schedulingId = id;
                    entity.planDate=date;
                    entity.completeNum=value;
                    params[count]=entity;
                    count++;
                }


//                var detailId = $(eval("detailId_" + planId)).text();
//                var originalNum = $(eval("originalNum_" + planId)).text();
//                var schedulingNum = $(eval("schedulingNum_" + planId)).text();
//                var sumCompleteNum = $(eval("sumCompleteNum_" + planId)).text();
//                var completeNum = $(eval("completeNum_" + planId)).val();
//                var standard = schedulingNum - sumCompleteNum;
//                if (parseInt(completeNum) <0 || parseInt(completeNum) >  parseInt(standard)){
//                    alert("确认排产量数据不正确，请重新输入！")
//                    return false;
//                }
//                var schedulingPlanId = $(eval("schedulingPlanId_" + planId)).text();
//                completeNum =  parseInt(completeNum) + parseInt(sumCompleteNum);
//
//
//                //alert("detailId="+ detailId + "/r/n" + "ordQty=" + ordQty + "sumSchedulingNum="+ sumSchedulingNum + "/r/n" + "schedulingNum="+ schedulingNum + "/r/n");
//                entity.id = planId;
//                entity.objectId = detailId;
//                entity.originalNum = originalNum;
//                entity.schedulingNum = schedulingNum;
//                entity.completeNum = completeNum;
//                params[index] = entity;
            }

            if(confirm("确定执行批量确认吗？")) {
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/saveCompletePlan',
                    contentType: 'application/json',
                    data:JSON.stringify(params),
                    datatype:"json",
                    type: 'post',
                    success: function (result) {
                        if(result == true) {
                            window.location.href = "${ctx}/biz/po/bizPoHeader/scheduling?id="+${bizPoHeader.id}  + "&forward=confirmScheduling";
                        }
                    },
                    error: function (error) {
                        console.info(error);
                    }
                });
            }
        }

        <%--//确认排产量check--%>
        <%--function confirmSchedulingCheck(index, detailId) {--%>
            <%--var schedulingNum = $(eval("schedulingNum_" + index)).text();--%>
            <%--var sumCompleteNum = $(eval("sumCompleteNum_" + index)).text();--%>
            <%--var completeNum = $(eval("completeNum_" + index)).val();--%>
            <%--var standard = schedulingNum - sumCompleteNum;--%>
            <%--if (parseInt(completeNum) <=0 || parseInt(completeNum) >  parseInt(standard)){--%>
                <%--alert("确认排产量数据不正确，请重新输入！")--%>
                <%--return false;--%>
            <%--}--%>
            <%--var schedulingPlanId = $(eval("schedulingPlanId_" + index)).text();--%>
            <%--completeNum =  parseInt(completeNum) + parseInt(sumCompleteNum);--%>
            <%--if(confirm("确认排产量为" + completeNum + "吗？")){--%>
                <%--$Mask.AddLogo("正在加载");--%>
                <%--$.ajax({--%>
                    <%--url: '${ctx}/biz/po/bizPoHeader/updateSchedulingPlan',--%>
                    <%--contentType: 'application/json',--%>
                    <%--data: {"schedulingPlanId": schedulingPlanId, "completeNum": completeNum},--%>
                    <%--type: 'get',--%>
                    <%--success: function (result) {--%>
                        <%--if(result == true) {--%>
                            <%--window.location.href = "${ctx}/biz/po/bizPoHeader/scheduling?id="+${bizPoHeader.id} + "&forward=confirmScheduling";--%>
                        <%--}--%>
                    <%--},--%>
                    <%--error: function (error) {--%>
                        <%--console.info(error);--%>
                    <%--}--%>
                <%--});--%>
            <%--}--%>
        <%--}--%>

        function checkResult(id) {
            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/checkResult',
                contentType: 'application/json',
                data: {"id": id},
                type: 'get',
                dataType:'json',
                success: function (result) {
                    var toalSchedulingNum = result['toalSchedulingNum'];
                    var toalCompleteNum = result['toalCompleteNum'];
                    if (toalSchedulingNum != null && toalCompleteNum != null && toalSchedulingNum == toalCompleteNum) {
                        $("#batchSubmit").hide();
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }
        function saveComplete(id) {
            var divArray = $("[name='" + id + "']");
            var params = new Array();
            for(i=0;i<divArray.length;i++){
                var div = divArray[i];
                var jqDiv = $(div);
                var date = jqDiv.find("[name='" + id + "_date']").val();
                var value = jqDiv.find("[name='" + id + "_value']").val();
                if(date == null || date == ""){
                    alert("排产日期不能为空!")
                    return false;
                }
                var reg= /^[1-9]+[0-9]*]*$/;
                if(value == null || value == "" || !reg.test(value)){
                    alert("确认值输入不正确!")
                    return false;
                }
                var entity = {};
                entity.schedulingId = id;
                entity.planDate=date;
                entity.completeNum=value;
                params[i]=entity;


            }
            console.log(params)

            console.log(JSON.stringify(params))
            if(confirm("确定执行批量确认吗？")) {
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/saveCompletePlan',
                    contentType: 'application/json',
                    data:JSON.stringify(params),
                    datatype:"json",
                    type: 'post',
                    success: function (result) {
                        if(result == true) {
                            window.location.href = "${ctx}/biz/po/bizPoHeader/scheduling?id="+${bizPoHeader.id}  + "&forward=confirmScheduling";
                        }
                    },
                    error: function (error) {
                        console.info(error);
                    }
                });
            }
        }

        function addSchedulingComplete(id) {
            var appendTr = $("#" + id);
            console.log(appendTr)
            var html = '<div class="control-group" name="' + id + '"> <label>排产日期' + '：' + '</label> <input name="' + id + '_date' + '" type="text" maxlength="20" class="input-medium Wdate" ';
            html += ' onclick="' + "WdatePicker({dateFmt:'" + "yyyy-MM-dd HH:mm:ss',isShowClear" + ":" + 'true});"/>' + ' &nbsp;&nbsp; '
            html += ' <label>排产数量：</label> ';
            html += ' <input name="' + id + "_value" + '" class="input-medium" type="text" maxlength="30"/>';
            html += ' <input class="btn" type="button" value="删除" onclick="removeSchedulingComplete(this)"/></div>'
            appendTr.append(html)
        }

        function removeSchedulingComplete(btn) {
            var div = btn.parentElement.remove()
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
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>产品图片</th>
            <th>品牌名称</th>
            <th>商品名称</th>
            <th>商品货号</th>
            <c:if test="${bizPoHeader.id!=null}">
                <th>所属单号</th>
            </c:if>
            <th>原始数量</th>
            <th>排产数量</th>
            <%--<th>已确认数量</th>--%>
            <%--<th>待确认数量</th>--%>
            <th>工厂价</th>
            <%--<th>操作</th>--%>
        </tr>
        </thead>
        <tbody id="prodInfo">
        <c:if test="${bizPoHeader.poDetailList!=null}">
            <c:forEach items="${bizPoHeader.poDetailList}" var="poDetail">
                <c:forEach items="${poDetail.schedulingPlanList}" var="schedulingPlan">
                    <tr>
                        <td id="schedulingPlanId_${schedulingPlan.id}" style="display: none">${schedulingPlan.id}</td>
                        <td id="detailId_${schedulingPlan.id}" style="display: none">${poDetail.id}</td>
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
                        <td id="originalNum_${schedulingPlan.id}" >${schedulingPlan.originalNum}</td>
                        <td id="schedulingNum_${schedulingPlan.id}" >${schedulingPlan.schedulingNum}</td>
                        <%--<td id="sumCompleteNum_${schedulingPlan.id}" >${schedulingPlan.completeNum}</td>
                        <td>
                            <input type="text" id="completeNum_${schedulingPlan.id}" style="margin-bottom: 10px;" value="${schedulingPlan.schedulingNum - schedulingPlan.completeNum}"
                            htmlEscape="false" maxlength="30" class="input-xlarge "/>
                        </td>--%>
                        <td>${poDetail.unitPrice}</td>
                        <%--<td>--%>
                            <%--<c:choose>--%>
                            <%--<c:when test="${schedulingPlan.schedulingNum == 0}">--%>
                                <%--<span style="color:red; ">未排产</span>--%>
                            <%--</c:when>--%>
                            <%--<c:otherwise>--%>
                            <%--<c:if test="${roleFlag != false}">--%>
                                <%--<c:if test="${schedulingPlan.schedulingNum != schedulingPlan.completeNum}">--%>
                                    <%--<input id="confirmScheduling" class="btn btn-primary" type="button"--%>
                                           <%--onclick="confirmSchedulingCheck('${schedulingPlan.id}','${poDetail.id}')"--%>
                                           <%--value="确认"/>&nbsp;--%>
                                    <%--</c:if>--%>
                            <%--</c:if>--%>
                            <%--<c:if test="${schedulingPlan.schedulingNum == schedulingPlan.completeNum}">--%>
                                <%--<span style="color:red; ">已全部确认</span>--%>
                            <%--</c:if>--%>
                            <%--</c:otherwise>--%>
                            <%--</c:choose>--%>
                        <%--</td>--%>
                    </tr>
                    <c:forEach items="${schedulingPlan.completePalnList}" var="completePaln">
                    <tr>
                        <td colspan="8">
                            <div class="control-group">
                                <label>排产日期：</label>
                                <input name="planDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                                       value="<fmt:formatDate value="${completePaln.planDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
                                &nbsp;&nbsp;
                                <label>排产数量：</label>
                                <input name="completeNum" readonly="readonly" value="${completePaln.completeNum}" class="input-medium" type="text" value="" maxlength="30" />
                            </div>
                        </td>
                    </tr>
                    </c:forEach>
                    <tr>
                        <td colspan="8">
                            <div class="control-group">
                                <input class="btn" type="button" value="添加排产计划" onclick="addSchedulingComplete(${schedulingPlan.id})"/>
                                &nbsp;&nbsp;
                                <input id="saveSubmit" class="btn btn-primary" type="button" onclick="saveComplete(${schedulingPlan.id})" value="保存"/>&nbsp;
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8" id="${schedulingPlan.id}">
                            <div class="control-group" name="${schedulingPlan.id}">
                                <label>排产日期：</label>
                                <input name="${schedulingPlan.id}_date" type="text" maxlength="20" class="input-medium Wdate"
                                      value="" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
                                &nbsp;&nbsp;
                                <label>排产数量：</label>
                                <input name="${schedulingPlan.id}_value" class="input-medium" type="text" value="" maxlength="30" />
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </c:forEach>
        </c:if>
        </tbody>
    </table>
    </c:if>

    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        <c:if test="${roleFlag != false}">
            <c:if test="${bizPoHeader.poDetailList!=null}">
                <input id="batchSubmit" class="btn btn-primary" type="button" onclick="batchUpdate()" value="保存"/>&nbsp;
            </c:if>
        </c:if>
    </div>
</form:form>
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
</body>
</html>
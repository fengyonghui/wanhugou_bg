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

            //采购单所属单号
            var orderNum = $("#orderNumStr_1").attr('value');
            $("#orderNum").val(orderNum)


            var detailHeaderFlg = '${detailHeaderFlg}';
            var detailSchedulingFlg = '${detailSchedulingFlg}';
            if (detailHeaderFlg == 'true' || detailSchedulingFlg == 'true') {
                var input = $("#schedulingPlanRadio").find("input:radio");
                input.attr("disabled","disabled");
                input.each(function(){
                    if($(this).val()=='${entity.schedulingType}'){
                        $(this).attr("checked",true);
                    }
                });
            }

            if (detailHeaderFlg == 'true') {
                $("#stockGoods").show();
                $("#schedulingPlan_forHeader").show();
                $("#schedulingPlan_forSku").hide();
                $("#batchSubmit").hide();
            }
            if (detailSchedulingFlg == 'true') {
                $("#stockGoods").hide();
                $("#schedulingPlan_forHeader").hide();
                $("#schedulingPlan_forSku").show();
                $("#batchSubmit").show();
            }
            if (detailHeaderFlg != 'true' && detailSchedulingFlg != 'true') {
                $("#stockGoods").show();
                $("#schedulingPlan_forHeader").show();
                $("#schedulingPlan_forSku").hide();
            }


            var poDetailList = '${bizPoHeader.poDetailList.size()}';
            if(poDetailList == 0) {
                $("#batchSubmit").hide();
            } else {
                var id = '${bizPoHeader.id}'
                checkResult(id);
            }
        });

        function checkResult(id) {
            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/checkSchedulingNum',
                contentType: 'application/json',
                data: {"id": id},
                type: 'get',
                dataType:'json',
                success: function (result) {
                    var totalOrdQty = result['totalOrdQty'];
                    $("#totalOrdQty").val(totalOrdQty)

                    var toalSchedulingNumForSkuHtml = $("[name=toalSchedulingNumForSku]");
                    var toalSchedulingNumForSkuNum = 0;
                    for(i=0;i<toalSchedulingNumForSkuHtml.length;i++){
                        var schedulingNumForSkuNum = toalSchedulingNumForSkuHtml[i];
                        var scForSkuNum = $(schedulingNumForSkuNum).attr("value")
                        toalSchedulingNumForSkuNum = parseInt(toalSchedulingNumForSkuNum) + parseInt(scForSkuNum);
                    }

                    if(totalOrdQty == toalSchedulingNumForSkuNum) {
                        $("#batchSubmit").hide()
                    }

                    var totalSchedulingHeaderNum = result['totalSchedulingHeaderNum'] == null ? 0 : result['totalSchedulingHeaderNum'];
                    var totalSchedulingDetailNum = result['totalSchedulingDetailNum'] == null ? 0 : result['totalSchedulingDetailNum'];

                    $("#toalSchedulingNum").val(totalSchedulingHeaderNum)
                    $("#totalSchedulingNumToDo").val(parseInt(totalOrdQty) - parseInt(totalSchedulingHeaderNum))

                    if(totalOrdQty != null && totalSchedulingHeaderNum != null && totalOrdQty == totalSchedulingHeaderNum) {
                        $("#addSchedulingHeaderPlanBtn").hide();
                        $("#saveSubmit").hide();
                        $("#schedulingPanAlert").show();
                        $(".headerScheduling").hide()
                    }

                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function addSchedulingHeaderPlan(head, id) {
            var appendTr = $("#" + head + id);
            var html = '<tr><td><div name="' + id + '"><label>排产日期' + '：' + '</label><input name="' + id + '_date' + '" type="text" maxlength="20" class="input-medium Wdate" ';
            html += ' onclick="' + "WdatePicker({dateFmt:'" + "yyyy-MM-dd HH:mm:ss',isShowClear" + ":" + 'true});"/>' + ' &nbsp; '
            html += ' <label>排产数量：</label> ';
            html += ' <input name="' + id + "_value" + '" class="input-medium" type="text" maxlength="30"/>';
            html += ' <input class="btn" type="button" value="删除" onclick="removeSchedulingHeaderPlan(this)"/></div></td></tr>'

            appendTr.append(html)
        }

        function removeSchedulingHeaderPlan(btn) {
            btn.parentElement.parentElement.remove();
        }

        function choose(obj) {
            $(obj).attr('checked', true);
            if ($(obj).val() == 0) {
                $("#stockGoods").show();
                $("#schedulingPlan_forHeader").show();
                $("#schedulingPlan_forSku").hide();
                $("#batchSubmit").hide();
            } else {
                $("#stockGoods").hide();
                $("#schedulingPlan_forHeader").hide();
                $("#schedulingPlan_forSku").show();
                $("#batchSubmit").show();
            }
        }

        function saveComplete(schedulingType,id) {
            var trArray = $("[name='" + id + "']");
            var params = new Array();
            if (schedulingType == "0"){
                var originalNum = $("#totalOrdQty").val();
            } else {
                var originalNum = $(eval("totalOrdQtyForSku_" + id)).val();
            }
            var totalSchedulingNum = 0;
            for(i=0;i<trArray.length;i++){
                var div = trArray[i];
                var jqDiv = $(div);
                var date = jqDiv.find("[name='" + id + "_date']").val();
                var value = jqDiv.find("[name='" + id + "_value']").val();
                if(date == null || date == ""){
                    alert("排产日期不能为空!")
                    return false;
                }
                var reg= /^[0-9]+[0-9]*]*$/;
                if(value == null || value == "" || parseInt(value)<=0 || parseInt(value) > originalNum || !reg.test(value)){
                    alert("确认值输入不正确!")
                    return false;
                }
                var entity = {};
                entity.objectId = id;
                entity.originalNum = originalNum;
                entity.schedulingNum = value;
                entity.planDate=date;
                entity.schedulingType=schedulingType;
                params[i]=entity;

                totalSchedulingNum = parseInt(totalSchedulingNum) + parseInt(value);
            }
            if(parseInt(totalSchedulingNum) > parseInt(originalNum)) {
                alert("排产量总和太大，请从新输入!")
                return false
            }
            if(confirm("确定执行该排产确认吗？")) {
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/saveSchedulingPlan',
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


        function batchSave() {
            var reqDetailIdList = JSON.parse('${poDetailIdListJson}');
            var params = new Array();
            var totalSchedulingNum = 0;
            var totalOriginalNum = 0;
            var count = 1
            var ind = 0;
            for(var index in reqDetailIdList) {
                var reqDetailId = reqDetailIdList[index];
                var trArray = $("[name='" + reqDetailId + "']");

                var originalNum = $(eval("totalOrdQtyForSku_" + reqDetailId)).val();
                totalOriginalNum += parseInt(totalOriginalNum) + parseInt(originalNum);

                for(i=0;i<trArray.length;i++) {
                    var div = trArray[i];
                    var jqDiv = $(div);
                    var date = jqDiv.find("[name='" + reqDetailId + "_date']").val();
                    var value = jqDiv.find("[name='" + reqDetailId + "_value']").val();
                    if (date == null || date == "") {
                        alert("第" + count + "个商品排产日期不能为空!")
                        return false;
                    }
                    var reg = /^[0-9]+[0-9]*]*$/;
                    if (value == null || value == "" || parseInt(value) <= 0 || parseInt(value) > originalNum || !reg.test(value)) {
                        alert("第" + count + "个商品确认值输入不正确!")
                        return false;
                    }
                    var entity = {};
                    entity.objectId = reqDetailId;
                    entity.originalNum = originalNum;
                    entity.schedulingNum = value;
                    entity.planDate=date;
                    entity.schedulingType=1;

                    params[ind]=entity;

                    totalSchedulingNum = parseInt(totalSchedulingNum) + parseInt(value);

                    ind++;
                }
                count++;
            }
            if(parseInt(totalSchedulingNum) > parseInt(totalOriginalNum)) {
                alert("排产量总和太大，请从新输入!")
                return false
            }
            if(confirm("确定执行该排产确认吗？")) {
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/saveSchedulingPlan',
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
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/po/bizPoHeader/">采购订单列表</a></li>
    <li class="active">
        <a href="${ctx}/biz/po/bizPoHeader/scheduling?id=${bizPoHeader.id}">排产</a>
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
            <label class="control-label">所属单号：</label>
            <div class="controls">
                <input id="orderNum" readonly="readonly" class="input-xlarge" type='text'/>
            </div>
            <!-- 采购单编号 -->
            <div class="controls" style="display: none">
                <form:input disabled="true" path="orderNum" htmlEscape="false" maxlength="30" class="input-xlarge "/>
            </div>
        </div>
    </c:if>

    <c:if test="${bizOrderHeader.orderType != 6}">
        <div class="control-group">
            <label class="control-label">排产类型：</label>
            <div class="controls" id="schedulingPlanRadio">
                <form:radiobutton id="deliveryStatus0" path="schedulingType" checked="true" onclick="choose(this)" value="0"/>按订单排产
                <form:radiobutton id="deliveryStatus1" path="schedulingType" onclick="choose(this)"  value="1"/>按商品排产
            </div>
        </div>
        <div class="control-group" id="stockGoods">
            <label class="control-label">采购商品：</label>
            <div class="controls">
                <table id="contentTable2" style="width:60%;float:left" class="table table-striped table-bordered table-condensed">
                    <thead>
                    <tr>
                        <th>产品图片</th>
                        <th>品牌名称</th>
                        <th>商品名称</th>
                        <th>商品货号</th>
                        <c:if test="${bizPoHeader.id!=null}">
                            <th style="display: none">所属单号</th>
                        </c:if>
                        <th>采购数量</th>
                        <th>工厂价</th>
                        <th>总金额</th>
                    </tr>
                    </thead>
                    <tbody id="prodInfo">
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
                                    <td style="display: none">
                                        <c:forEach items="${bizPoHeader.orderNumMap[poDetail.skuInfo.id]}"
                                                   var="orderNumStr"
                                                   varStatus="orderStatus">
                                        <c:if test="${orderNumStr.soType==1}">
                                        <a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderNumStr.orderHeader.id}&orderDetails=details">
                                            </c:if>
                                            <c:if test="${orderNumStr.soType==2}">
                                            <a href="${ctx}/biz/request/bizRequestHeader/form?id=${orderNumStr.requestHeader.id}&str=detail">
                                                </c:if>
                                                    ${orderNumStr.orderNumStr}
                                            </a>
                                            <span id="orderNumStr_${orderStatus.index+1}" style="display:none" value="${orderNumStr.orderNumStr}" />
                                            </c:forEach>
                                    </td>
                                </c:if>
                                <td id="ordQty_${state.index+1}">${poDetail.ordQty}</td>
                                <td>${poDetail.unitPrice}</td>
                                <td>${poDetail.ordQty * poDetail.unitPrice}</td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="control-group" id="schedulingPlan_forHeader">
            <label class="control-label">按订单排产：</label>
            <div class="controls">
                <table id="schedulingForHeader_${bizPoHeader.id}" style="width:60%;float:left" class="table table-striped table-bordered table-condensed">
                    <tr>
                        <td>
                            <label>总申报数量：</label>
                            <input id="totalOrdQty" name='reqQtys' readonly="readonly" class="input-mini" type='text'/>
                            &nbsp;
                            <label>总待排产量：</label>
                            <input id="totalSchedulingNumToDo" name='reqQtys' readonly="readonly" class="input-mini"
                                   type='text'/>
                            &nbsp;
                            <label>已排产数量：</label>
                            <input id="toalSchedulingNum" name='reqQtys' readonly="readonly" class="input-mini"
                                   type='text'/>
                            &nbsp;
                            <input id="addSchedulingHeaderPlanBtn" class="btn" type="button" value="添加排产计划"
                                   onclick="addSchedulingHeaderPlan('schedulingForHeader_', ${bizPoHeader.id})"/>
                            &nbsp;
                            <input id="saveSubmit" class="btn btn-primary" type="button"
                                   onclick="saveComplete('0',${bizPoHeader.id})" value="保存"/>
                            <span id="schedulingPanAlert" style="color:red; display:none">已排产完成</span>
                        </td>
                    </tr>


                    <c:if test="${fn:length(bizCompletePalns) > 0}">
                        <tr>
                            <td>
                                <label>排产履历：</label>
                            </td>
                        </tr>
                        <c:forEach items="${bizCompletePalns}" var="bizCompletePaln" varStatus="stat">
                            <tr>
                                <td>
                                    <div>
                                        <label>排产日期：</label>
                                        <input type="text" maxlength="20" class="input-medium Wdate" readonly="readonly"
                                               value="<fmt:formatDate value="${bizCompletePaln.planDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
                                        &nbsp;
                                        <label>排产数量：</label>
                                        <input class="input-medium" type="text" readonly="readonly"
                                               value="${bizCompletePaln.completeNum}" maxlength="30"/>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:if>

                    <tr class="headerScheduling">
                        <td>
                            <label>排产计划：</label>
                        </td>
                    </tr>
                    <tr id="header_${bizPoHeader.id}" class="headerScheduling">
                        <td>
                            <div name="${bizPoHeader.id}">
                                <label>排产日期：</label>
                                <input name="${bizPoHeader.id}_date" type="text" maxlength="20"
                                       class="input-medium Wdate"
                                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> &nbsp;
                                <label>排产数量：</label>
                                <input name="${bizPoHeader.id}_value" class="input-medium" type="text" maxlength="30"/>
                            </div>
                        </td>
                    </tr>

                    <tr id="remark" >
                        <td>
                            <div>
                                <label>备注：</label>
                                    <form:textarea path="remark" htmlEscape="false" maxlength="200" class="input-xlarge "/>
                            </div>
                        </td>
                    </tr>
                </table>

            </div>
        </div>

        <div class="control-group" id="schedulingPlan_forSku" style="display: none">
            <label class="control-label">按商品排产：</label>
            <div class="controls">
                <table id="" style="width:60%;float:left" class="table table-striped table-bordered table-condensed">
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
                        <th>工厂价</th>
                        <th>总金额</th>
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
                                        <c:forEach items="${bizPoHeader.orderNumMap[poDetail.skuInfo.id]}"
                                                   var="orderNumStr"
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
                                <td>${poDetail.unitPrice}</td>
                                <td>${poDetail.ordQty * poDetail.unitPrice}</td>
                            </tr>
                            <c:if test="${state.last}">
                                <c:set var="aa" value="${state.index}" scope="page"/>
                            </c:if>

                            <tr>
                                <td colspan="10">
                                    <table id="schedulingForDetail_${poDetail.id}" style="width:100%;float:left" class="table table-striped table-bordered table-condensed">
                                        <tr>
                                            <td>
                                                <label>总申报数量：</label>
                                                <input id="totalOrdQtyForSku_${poDetail.id}"  name='reqQtys' readonly="readonly" value="${poDetail.ordQty}" class="input-mini" type='text'/>
                                                &nbsp;
                                                <label>待排产量：</label>
                                                <input id="toalSchedulingNumToDoForSku" name='reqQtys' readonly="readonly" value="${poDetail.ordQty - poDetail.sumCompleteNum}" class="input-mini" type='text'/>
                                                &nbsp;
                                                <label>已排产数量：</label>
                                                <input name="toalSchedulingNumForSku" name='reqQtys' readonly="readonly" value="${poDetail.sumCompleteNum}" class="input-mini" type='text'/>
                                                &nbsp;
                                                <c:choose>
                                                    <c:when test="${poDetail.ordQty == poDetail.sumCompleteNum}">
                                                        <span style="color:red; ">已排产完成</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input id="addSchedulingHeaderSkuBtn" class="btn" type="button" value="添加排产计划" onclick="addSchedulingHeaderPlan('schedulingForDetail_', ${poDetail.id})"/>
                                                        <input id="saveSubmitForSku" class="btn btn-primary" type="button" onclick="saveComplete('1',${poDetail.id})" value="保存"/>
                                                        <span id="schedulingPanAlertForSku" style="color:red; display:none" >已排产完成</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>

                                        <c:if test="${poDetail.bizSchedulingPlan != null}">
                                            <tr>
                                                <td>
                                                    <label>排产履历：</label>
                                                </td>
                                            </tr>
                                            <c:forEach items="${poDetail.bizSchedulingPlan.completePalnList}" var="completePaln">
                                                <tr >
                                                    <td>
                                                        <div>
                                                            <label>排产日期：</label>
                                                            <input type="text" maxlength="20" readonly="readonly" value="<fmt:formatDate value="${completePaln.planDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" class="input-medium Wdate"  /> &nbsp;
                                                            <label>排产数量：</label>
                                                            <input class="input-medium" readonly="readonly" value="${completePaln.completeNum}" type="text" maxlength="30" />
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test="${poDetail.ordQty != poDetail.sumCompleteNum}">
                                            <tr>
                                                <td>
                                                    <label>排产计划：</label>
                                                </td>
                                            </tr>
                                            <tr id="detail_${poDetail.id}" name="detailScheduling">
                                                <td>
                                                    <div name="${poDetail.id}">
                                                        <label>排产日期：</label>
                                                        <input name="${poDetail.id}_date" type="text" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});" /> &nbsp;
                                                        <label>排产数量：</label>
                                                        <input name="${poDetail.id}_value" class="input-medium" type="text" maxlength="30" />
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </table>
                                </td>
                            </tr>
                        </c:forEach>
                        <input id="aaId" value="${aa}" type="hidden"/>
                    </c:if>

                    <tr id="remark" >
                        <td colspan="10">
                            <div>
                                <label>备注：</label>
                                <form:textarea path="remark" htmlEscape="false" maxlength="200" class="input-xlarge "/>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        &nbsp;&nbsp;
        <input id="batchSubmit" class="btn btn-primary" type="button" style="display: none;" onclick="batchSave()" value="批量保存"/>&nbsp;
    </div>
</form:form>
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
</body>
</html>
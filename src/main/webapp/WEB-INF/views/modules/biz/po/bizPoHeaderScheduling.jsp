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
            var poDetailList = '${bizPoHeader.poDetailList.size()}';
            if(poDetailList == 0) {
                $("#batchSubmit").hide();
            } else {
                var id = '${bizPoHeader.id}'
                checkResult(id);
            }
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
                var reg= /^[0-9]+[0-9]*]*$/;
                if (parseInt(schedulingNum) < 0 || (parseInt(schedulingNum) > parseInt(standard)) || !reg.test(schedulingNum)){
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
            var reg= /^[0-9]+[0-9]*]*$/;
            if (parseInt(schedulingNum) <= 0 || (parseInt(schedulingNum) > parseInt(standard)) || !reg.test(schedulingNum)){
                alert("排产量数值设置不正确，请重新输入")
                return false;
            }
            if(confirm("确定添加排产量为" + schedulingNum + "的排产吗？")){
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/saveSchedulingPlan',
                    contentType: 'application/json',
                    data: {"detailId": detailId, "ordQty": ordQty, "schedulingNum": schedulingNum},
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

        function checkResult(id) {
            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/checkResult',
                contentType: 'application/json',
                data: {"id": id},
                type: 'get',
                dataType:'json',
                success: function (result) {
                    var totalOrdQty = result['totalOrdQty'];
                    var toalSchedulingNum = result['toalSchedulingNum'];
                    if (totalOrdQty != null && toalSchedulingNum != null && totalOrdQty == toalSchedulingNum) {
                        $("#batchSubmit").hide();
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function addSchedulingHeaderPlan(head, id) {
            console.log("--")
            var appendTr = $("#" + head + id);
            var html = '<tr><td><div name="' + id + '"><label>排产日期' + '：' + '</label><input name="' + id + '_date' + '" type="text" maxlength="20" class="input-medium Wdate" ';
            html += ' onclick="' + "WdatePicker({dateFmt:'" + "yyyy-MM-dd HH:mm:ss',isShowClear" + ":" + 'true});"/>' + ' &nbsp; '
            html += ' <label>排产数量：</label> ';
            html += ' <input name="' + id + "_value" + '" class="input-medium" type="text" maxlength="30"/>';
            html += ' <input class="btn" type="button" value="删除" onclick="removeSchedulingHeaderPlan(this)"/></div></td></tr>'

            appendTr.after(html)
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
            } else {
                $("#stockGoods").hide();
                $("#schedulingPlan_forHeader").hide();
                $("#schedulingPlan_forSku").show();
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

                                <td id="sumSchedulingNum_${state.index+1}">${poDetail.sumSchedulingNum}</td>
                                <td>
                                    <input type="text" id="schedulingNum_${state.index+1}" style="margin-bottom: 10px"
                                           value="${poDetail.ordQty - poDetail.sumSchedulingNum}"
                                           htmlEscape="false" maxlength="30" class="input-xlarge "/>
                                </td>
                                <td>${poDetail.unitPrice}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${poDetail.ordQty != poDetail.sumSchedulingNum}">
                                            <input id="addScheduling" class="btn btn-primary" type="button"
                                                   onclick="addSchedulingCheck('${state.index+1}','${poDetail.id}')"
                                                   value="保存"/>&nbsp;
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color:red; ">已排产完成</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
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
                <table style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
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
                                   onclick="addSchedulingHeaderPlan('header_', ${bizPoHeader.id})"/>
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
                </table>

            </div>
        </div>

        <div class="control-group" id="schedulingPlan_forSku" style="display: none">
            <label class="control-label">按商品排产：</label>
            <div class="controls">
                <table id="" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
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
                            <tr class="${reqDetail.skuInfo.productInfo.id}" >
                                <td id="detailId_${state.index+1}" style="display: none">${reqDetail.id}</td>
                                <td><img src="${reqDetail.skuInfo.productInfo.imgUrl}" width="100" height="100" /></td>
                                <td>${reqDetail.skuInfo.productInfo.brandName}</td>
                                <td><a href="${ctx}/sys/office/supplierForm?id=${reqDetail.skuInfo.productInfo.office.id}&gysFlag=onlySelect">
                                        ${reqDetail.skuInfo.productInfo.office.name}</a></td>
                                <td>${reqDetail.skuInfo.name}</td>
                                <td>${reqDetail.skuInfo.partNo}</td>
                                <td>${reqDetail.skuInfo.itemNo}</td>
                                    <%--<td>${reqDetail.skuInfo.skuPropertyInfos}</td>--%>
                                <td style="white-space: nowrap">
                                        ${reqDetail.unitPrice}
                                </td>
                                <td>
                                    <input  type='hidden' name='reqDetailIds' value='${reqDetail.id}'/>
                                    <input type='hidden' name='skuInfoIds' value='${reqDetail.skuInfo.id}'/>
                                    <input  type='hidden' name='lineNos' value='${reqDetail.lineNo}'/>
                                    <input id="reqQty_${state.index+1}" name='reqQtys' readonly="readonly" value="${reqDetail.reqQty}" class="input-mini" type='text'/>
                                </td>
                                <td>
                                        ${reqDetail.unitPrice * reqDetail.reqQty}
                                </td>
                            </tr>
                            <c:if test="${state.last}">
                                <c:set var="aa" value="${state.index}" scope="page"/>
                            </c:if>

                            <tr>
                                <td colspan="10">
                                    <table style="width:100%;float:left" class="table table-striped table-bordered table-condensed">
                                        <tr>
                                            <td>
                                                <label>总申报数量：</label>
                                                <input id="totalOrdQtyForSku_${reqDetail.id}"  name='reqQtys' readonly="readonly" value="${reqDetail.reqQty}" class="input-mini" type='text'/>
                                                &nbsp;
                                                <label>待排产量：</label>
                                                <input id="toalSchedulingNumToDoForSku" name='reqQtys' readonly="readonly" value="${reqDetail.reqQty - reqDetail.sumCompleteNum}" class="input-mini" type='text'/>
                                                &nbsp;
                                                <label>已排产数量：</label>
                                                <input name="toalSchedulingNumForSku" name='reqQtys' readonly="readonly" value="${reqDetail.sumCompleteNum}" class="input-mini" type='text'/>
                                                &nbsp;
                                                <c:choose>
                                                    <c:when test="${reqDetail.reqQty == reqDetail.sumCompleteNum}">
                                                        <span style="color:red; ">已排产完成</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input id="addSchedulingHeaderSkuBtn" class="btn" type="button" value="添加排产计划" onclick="addSchedulingHeaderPlan('detail_', ${reqDetail.id})"/>
                                                        <input id="saveSubmitForSku" class="btn btn-primary" type="button" onclick="saveComplete('1',${reqDetail.id})" value="保存"/>
                                                        <span id="schedulingPanAlertForSku" style="color:red; display:none" >已排产完成</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>

                                        <c:if test="${reqDetail.bizSchedulingPlan != null}">
                                            <tr>
                                                <td>
                                                    <label>排产履历：</label>
                                                </td>
                                            </tr>
                                            <c:forEach items="${reqDetail.bizSchedulingPlan.completePalnList}" var="completePaln">
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

                                        <c:if test="${reqDetail.reqQty != reqDetail.sumCompleteNum}">
                                            <tr>
                                                <td>
                                                    <label>排产计划：</label>
                                                </td>
                                            </tr>
                                            <tr id="detail_${reqDetail.id}" name="detailScheduling">
                                                <td>
                                                    <div name="${reqDetail.id}">
                                                        <label>排产日期：</label>
                                                        <input name="${reqDetail.id}_date" type="text" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});" /> &nbsp;
                                                        <label>排产数量：</label>
                                                        <input name="${reqDetail.id}_value" class="input-medium" type="text" maxlength="30" />
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
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <div class="form-actions">
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        &nbsp;&nbsp;
        <c:if test="${bizPoHeader.poDetailList!=null}">
            <input id="batchSubmit" class="btn btn-primary" type="button" onclick="batchSave()" value="批量保存"/>&nbsp;
        </c:if>
    </div>
</form:form>
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
</body>
</html>
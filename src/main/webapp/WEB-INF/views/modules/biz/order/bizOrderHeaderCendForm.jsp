<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>

<html>
<head>
    <title>订单信息管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function() {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    if($("#address").val()==''){
                        $("#addError").css("display","inline-block")
                        return false;
                    }
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function(error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
            if($("#bizOrderMark").val()!=""){
                clickBut();
            }
            <%--C端订单地址--%>
            if($("#id").val() !=""){
                var option2=$("<option/>").text("${orderAddress.province.name}").val(${orderAddress.province.id});
                $("#province").append(option2);
                var option3=$("<option/>").text("${orderAddress.city.name}").val(${orderAddress.city.id});
                $("#city").append(option3);
                var option4=$("<option/>").text("${orderAddress.region.name}").val(${orderAddress.region.id});
                $("#region").append(option4);
                $("#address").val("${orderAddress.address}");
                <%--C端订单收货地址--%>
                $("#province").change();
                $("#city").change();
                $("#region").change();
                $("#address").change();
            }
            $("#addAddressHref").click(function () {
                var officeId=$("#officeId").val();
                var officeName =$("#officeName").val();
                window.location.href="${ctx}/sys/office/sysOfficeAddress/form?ohId=${bizOrderHeader.id}&office.id="+officeId+"&office.name="+officeName+"&flag=order"
            });
            $("#updateMoney").click(function () {
                updateMoney();
            });
        });

        function clickBut(){
            var officeId=$("#officeId").val();
            $("#province").empty();
            $("#city").empty();
            $("#region").empty();
            $("#address").empty();
            $.ajax({
                type:"post",
                url:"${ctx}/biz/shop/bizShopReceiverAddress/findCendByAdderUser?user.id="+officeId,
                success:function(data){
                    if(data==''){
                        console.log("数据为空显示 新增地址 ");
                        $("#add1").css("display","none");
                        $("#add2").css("display","block");
                        $("#add3").css("display","none");
                    }else{
                        console.log("数据不为空隐藏 新增地址 ");
                        $("#add1").css("display","block");
                        $("#add2").css("display","none");
                        $("#add3").css("display","block");
                        $("#receCend").val(data.receiver);
                        $("#phoneCend").val(data.phone);
                        var option2=$("<option>").text(data.bizLocation.province.name).val(data.bizLocation.province.id);
                        $("#province").append(option2);
                        var option3=$("<option/>").text(data.bizLocation.city.name).val(data.bizLocation.city.id);
                        $("#city").append(option3);
                        var option4=$("<option/>").text(data.bizLocation.region.name).val(data.bizLocation.region.id);
                        $("#region").append(option4);
                        $("#address").val(data.bizLocation.address);
                        <%-- C端订单 --%>
                        $("#province").change();
                        $("#city").change();
                        $("#region").change();
                        $("#address").change();
                    }
                }
            });
        }
    </script>
    <script type="text/javascript">
        function updateMoney() {
            if(confirm("确定修改价钱吗？")){
                var totalExp=$("#totalExp").val();
                $.ajax({
                    type:"post",
                    url:" ${ctx}/biz/order/bizOrderHeader/saveBizOrderHeader",
                    data:{orderId:$("#id").val(),money:totalExp},
                    <%--"&bizLocation.receiver="+$("#bizLocation.receiver").val()+"&bizLocation.phone="+$("#bizLocation.phone").val(),--%>
                    success:function(flag){
                        if(flag=="ok"){
                            alert(" 修改成功 ");

                        }else{
                            alert(" 修改失败 ");
                        }
                    }
                });
            }
        }
    </script>
    <script type="text/javascript">
        function checkPending(obj) {
            var localSendIds= "";
            var boo="";
                $("input[name='localSendIds']").each(function () {
                    localSendIds+=$(this).val()+",";
                    if($(this).is(':checked')){
                        boo+="true,";
                    }else {
                        boo+="false,";
                    }
                });
            localSendIds= localSendIds.substring(0,localSendIds.length-1);
            boo=boo.substring(0,boo.length-1);
            if(obj==${OrderHeaderBizStatusEnum.SUPPLYING.state}){ <%--15同意发货--%>
                $("#id").val();
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/order/bizOrderHeader/Commissioner",
                    data:"id="+$("#id").val()+"&flag=${bizOrderHeader.flag}&objJsp=${OrderHeaderBizStatusEnum.SUPPLYING.state}&bizLocation.address="+$("#jhaddress").val()+"&bizLocation.appointedTime="+$("#appointedDate").val()+"&localSendIds="+localSendIds+"&boo="+boo,
                    success:function(commis){
                        if(commis=="ok"){
                            alert(" 同意发货 ");
                            window.location.href = "${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}";
                        }else{
                            alert(" 发货失败 ");
                        }
                    }
                });
            }else if(obj==${OrderHeaderBizStatusEnum.UNAPPROVE.state}){ <%--45不同意发货--%>
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/order/bizOrderHeader/Commissioner",
                    data:"id="+$("#id").val()+"&flag=${bizOrderHeader.flag}&objJsp=${OrderHeaderBizStatusEnum.UNAPPROVE.state}&bizLocation.address="+$("#jhaddress").val()+"&bizLocation.appointedTime="+$("#appointedDate").val()+"&localSendIds="+localSendIds,
                    success:function(commis){
                        if(commis=="ok"){
                            alert(" 不同意发货 ");
                            window.location.href = "${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}";
                        }else{
                            alert(" 发货失败 ");
                        }
                    }
                });
            }
        }
    </script>
    <script type="text/javascript">
        function DetailDelete(a,b){
            top.$.jBox.confirm("确认要删除该商品吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    $.ajax({
                        type:"post",
                        url:"${ctx}/biz/order/bizOrderDetail//Detaildelete",
                        data:"id="+a+"&sign="+b,
                        success:function(data){
                            if(data=="ok"){
                                $("#trRevom_"+a).remove();//主要是删除这tr
                                <%--$("#id").val("");//点击删除后把原id为空--%>
                            }
                        }
                    });
                }
            },{buttonsFocus:1});
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <c:if test="${bizOrderHeader.flag=='check_pending'}">
        <li>
            <a href="${ctx}/biz/order/bizOrderHeader/cendList?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">订单信息列表</a>
        </li>
    </c:if>
    <c:if test="${empty bizOrderHeader.flag}">
        <c:if test="${empty bizOrderHeader.clientModify}">
            <li><a href="${ctx}/biz/order/bizOrderHeader/cendList">订单信息列表</a></li>
        </c:if>
    </c:if>

    <li class="active">
        <c:if test="${entity.orderNoEditable eq 'editable'}">
            <a href="${ctx}/biz/order/bizOrderHeader/cendform?id=${bizOrderHeader.id}&orderNoEditable=${entity.orderNoEditable}">订单信息支付</a>
        </c:if>
        <c:if test="${entity.orderDetails eq 'details'}">
            <a href="${ctx}/biz/order/bizOrderHeader/cendform?id=${bizOrderHeader.id}&orderDetails=${entity.orderDetails}">订单信息详情</a>
        </c:if>
        <c:if test="${bizOrderHeader.flag eq 'check_pending'}">
            <a href="${ctx}/biz/order/bizOrderHeader/cendform?id=${bizOrderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">订单信息审核</a>
        </c:if>
        <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
            <c:if test="${empty bizOrderHeader.clientModify}">
                <a href="${ctx}/biz/order/bizOrderHeader/cendform?id=${bizOrderHeader.id}">订单信息<shiro:hasPermission
                        name="biz:order:bizOrderHeader:edit">${not empty bizOrderHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
                        name="biz:order:bizOrderHeader:edit">查看</shiro:lacksPermission></a>
            </c:if>
        </c:if>
    </li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/cendSave" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <input type="hidden" id="bizOrderMark" name="orderMark" value="${bizOrderHeader.orderMark}">
    <input type="hidden" name="clientModify" value="${bizOrderHeader.clientModify}" />
    <form:hidden path="platformInfo.id" value="6"/>
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">订单编号：</label>
        <div class="controls">
            <form:input path="orderNum" disabled="true" placeholder="由系统自动生成" htmlEscape="false" maxlength="50" class="input-xlarge"/>
        </div>
    </div>
    <c:if test="${not empty entity.orderDetails}">
        <div class="control-group">
            <label class="control-label">订单类型：</label>
            <div class="controls">
                <form:select path="orderType" class="input-xlarge" disabled="true">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_type')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>
            </div>
        </div>
    </c:if>
    <div class="control-group">
        <label class="control-label">用户名称：</label>
        <div class="controls"><%-- /sys/user/treeData --%>
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <sys:treeselect id="office" name="customer.id" value="${entity.customer.id}" labelName="customer.name"
                                labelValue="${entity.customer.name}" disabled="disabled" notAllowSelectParent="true"
                                title="用户" url="/sys/wx/sysWxPersonalUser/userTreeData" cssClass="input-xlarge"
                                allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <sys:treeselect id="office" name="customer.id" value="${entity.customer.id}" labelName="customer.name"
                                labelValue="${entity.customer.name}" notAllowSelectParent="true"
                                title="用户" url="/sys/wx/sysWxPersonalUser/userTreeData" cssClass="input-xlarge required"
                                allowClear="${office.currentUser.admin}" onchange="clickBut();" dataMsgRequired="必填信息"/>
                <span class="help-inline"><font color="red">*</font></span>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">商品总价：</label>
        <div class="controls">
            <form:input path="totalDetail" htmlEscape="false" placeholder="0.0" readOnly="true" class="input-xlarge"/>
            <input name="totalDetail" value="${entity.totalDetail}" htmlEscape="false" type="hidden"/>
            <span class="help-inline">自动计算</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">调整金额：</label>
        <div class="controls">
                <form:input path="totalExp" htmlEscape="false" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font></span>
            <c:if test="${bizOrderHeader.flag=='check_pending'}">
                <a href="#" id="updateMoney"> <span class="icon-ok-circle"/></a>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">运费：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <form:input path="freight" htmlEscape="false" disabled="true" class="input-xlarge"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:input path="freight" htmlEscape="false" placeholder="请输入运费" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </c:if>
        </div>
    </div>
    <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
        <div class="control-group">
            <label class="control-label">应付金额：</label>
            <div class="controls">
                <input type="text" value="<fmt:formatNumber type="number" value="${bizOrderHeader.totalDetail+bizOrderHeader.totalExp+bizOrderHeader.freight}" pattern="0.00"/>"
                       disabled="true" class="input-xlarge">
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">已付金额：</label>
            <div class="controls">
                <font color="#088A29">
                    <fmt:formatNumber type="percent" value="${bizOrderHeader.receiveTotal/fn:trim(bizOrderHeader.totalDetail+bizOrderHeader.totalExp+bizOrderHeader.freight)}" maxFractionDigits="2" />
                </font> (<fmt:formatNumber type="number" value="${bizOrderHeader.receiveTotal}" pattern="0.00"/>)
            </div>
        </div>
    </c:if>
    <div class="control-group">
        <label class="control-label">发票状态：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <form:select path="invStatus" class="input-xlarge" disabled="true">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:select path="invStatus" class="input-xlarge required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>
                <span class="help-inline"><font color="red">*</font>默认选择</span>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">业务状态：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <form:select path="bizStatus" class="input-xlarge" disabled="true">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_cend_orderType')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:select path="bizStatus" class="input-xlarge required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_cend_orderType')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>
                <span class="help-inline"><font color="red">*</font>默认选择</span>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">收货人：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <form:input path="bizLocation.receiver" placeholder="收货人名称" htmlEscape="false" disabled="true"
                            class="input-xlarge"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:input path="bizLocation.receiver" id="receCend" placeholder="请输入收货人名称" htmlEscape="false"
                            class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">联系电话：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <form:input path="bizLocation.phone" placeholder="请输入联系电话" htmlEscape="false" disabled="true"
                            class="input-xlarge required"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:input path="bizLocation.phone" id="phoneCend" placeholder="请输入联系电话" htmlEscape="false"
                            class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </c:if>
        </div>
    </div>
    <div class="control-group" id="add1">
        <label class="control-label">收货地址；</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <select id="province" class="input-medium" name="bizLocation.province.id" disabled="disabled"
                        style="width:150px;text-align: center;">
                    <c:if test="${bizOrderHeader.id ==null}">
                        <option value="-1">—— 省 ——</option>
                    </c:if>
                </select>
                <select id="city" class="input-medium" name="bizLocation.city.id" disabled="disabled"
                        style="width:150px;text-align: center;">
                    <c:if test="${bizOrderHeader.id ==null}">
                        <option value="-1">—— 市 ——</option>
                    </c:if>
                </select>
                <select id="region" class="input-medium" name="bizLocation.region.id" disabled="disabled"
                        style="width:150px;text-align: center;">
                    <c:if test="${bizOrderHeader.id ==null}">
                        <option value="-1">—— 区 ——</option>
                    </c:if>
                </select>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <select id="province" class="input-medium" name="bizLocation.province.id"
                        style="width:150px;text-align: center;">
                    <c:if test="${bizOrderHeader.id ==null}">
                        <option value="-1">—— 省 ——</option>
                    </c:if>
                </select>
                <select id="city" class="input-medium" name="bizLocation.city.id"
                        style="width:150px;text-align: center;">
                    <c:if test="${bizOrderHeader.id ==null}">
                        <option value="-1">—— 市 ——</option>
                    </c:if>
                </select>
                <select id="region" class="input-medium" name="bizLocation.region.id"
                        style="width:150px;text-align: center;">
                    <c:if test="${bizOrderHeader.id ==null}">
                        <option value="-1">—— 区 ——</option>
                    </c:if>
                </select>
            </c:if>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group" id="add2" style="display:none">
        <label class="control-label">收货地址；</label>
        <div class="controls">
            <input id="addAddressHref" type="button" value="新增地址" htmlEscape="false" class="input-xlarge required"/>
            <label class="error" id="addError" style="display:none;">必填信息</label>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <div class="control-group" id="add3">
        <label class="control-label">详细地址；</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <input type="text" id="address" name="bizLocation.address" htmlEscape="false" readOnly="true"
                    class="input-xlarge"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <input type="text" id="address" name="bizLocation.address" htmlEscape="false" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备&nbsp;注；</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <form:textarea path="orderComment.comments" htmlEscape="false" maxlength="200" class="input-xlarge" disabled="true"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:textarea path="orderComment.comments" htmlEscape="false" maxlength="200" class="input-xlarge"/>
            </c:if>
        </div>
    </div>

    <c:choose>
        <c:when test="${bizOrderHeader.flag=='check_pending'}">

        </c:when>
        <c:otherwise>
            <div class="form-actions">
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                    <shiro:hasPermission name="biz:order:bizOrderHeader:edit">
                        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>
                    </shiro:hasPermission>
                </c:if>
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
            </div>
        </c:otherwise>
    </c:choose>

</form:form>

<%--详情列表--%>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>详情行号</th>
        <th>货架名称</th>
        <th>商品名称</th>
        <th>商品编号</th>
        <th>商品货号</th>
        <th>供应商</th>
        <th>供应商电话</th>
        <th>商品单价</th>
        <th>采购数量</th>
        <th>总&nbsp;&nbsp;额</th>
        <th>已发货数量</th>
        <c:if test="${bizOrderHeader.flag=='check_pending'}">
            <th>本地备货</th>
        </c:if>
        <th>创建时间</th>
        <shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <th>操作</th>
            </c:if>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${bizOrderHeader.orderDetailList}" var="bizOrderDetail">
        <tr id="trRevom_${bizOrderDetail.id}">
            <td>
                    ${bizOrderDetail.lineNo}
            </td>
            <td>
                    ${bizOrderDetail.shelfInfo.opShelfInfo.name}
            </td>
            <td>
                <c:if test="${entity.orderDetails eq 'details' || entity.orderNoEditable eq 'editable' || bizOrderHeader.flag eq 'check_pending'}">
                    ${bizOrderDetail.skuName}
                </c:if>
                <c:if test="${empty entity.orderNoEditable || empty entity.orderDetails || empty bizOrderHeader.flag}">
                    <c:if test="${empty entity.orderNoEditable && empty entity.orderDetails && empty bizOrderHeader.flag && empty bizOrderHeader.clientModify}">
                        <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&detailFlag=header_save">
                                ${bizOrderDetail.skuName}</a>
                    </c:if>
                </c:if>
            </td>
            <td>
                    ${bizOrderDetail.partNo}
            </td>
            <td>
                ${bizOrderDetail.skuInfo.itemNo}
            </td>
            <td>
                    ${bizOrderDetail.vendor.name}
            </td>
            <td>
                    ${bizOrderDetail.primary.mobile}
            </td>
            <td>
                    ${bizOrderDetail.unitPrice}
            </td>
            <td>
                    ${bizOrderDetail.ordQty}
            </td>
            <td>
                <c:if test="${bizOrderDetail.unitPrice !=null && bizOrderDetail.ordQty !=null}">
                    ${bizOrderDetail.unitPrice * bizOrderDetail.ordQty}
                </c:if>
            </td>
            <td>
                    ${bizOrderDetail.sentQty}
            </td>
            <c:if test="${bizOrderHeader.flag=='check_pending'}">
                <td>
                    <c:choose>
                        <c:when test="${bizOrderDetail.suplyis.id!=0}">
                            <input type="checkbox" checked="checked" name="localSendIds" value="${bizOrderDetail.id}"/>
                        </c:when>
                        <c:otherwise>
                            <input type="checkbox" name="localSendIds" value="${bizOrderDetail.id}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
            </c:if>
            <td>
                <fmt:formatDate value="${bizOrderDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <shiro:hasPermission name="biz:order:bizOrderDetail:edit">
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                    <td>
                        <c:if test="${empty bizOrderHeader.clientModify}">
                            <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&detailFlag=header_save">修改</a>
                            <a href="javascript:void(0);" onclick="DetailDelete(${bizOrderDetail.id},'1');">删除</a>
                        </c:if>
                    </td>
                </c:if>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="form-actions">
    <c:if test="${empty entity.orderNoEditable}">
        <c:if test="${bizOrderHeader.id!=null}">
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <shiro:hasPermission name="biz:order:bizOrderDetail:edit">
                    <c:if test="${empty bizOrderHeader.clientModify}">
                    <input type="button"
                           onclick="javascript:window.location.href='${ctx}/biz/order/bizOrderDetail/form?orderHeader.id=${bizOrderHeader.id}&detailFlag=header_save';"
                           class="btn btn-primary"
                           value="订单商品信息添加"/></c:if>
                </shiro:hasPermission>
            </c:if>
            <c:if test="${not empty entity.orderDetails}">
                <input onclick="window.print();" type="button" class="btn btn-primary" value="打印订单" style="background:#F78181;"/>
            </c:if>
        </c:if>
    </c:if>
</div>

</body>
</html>

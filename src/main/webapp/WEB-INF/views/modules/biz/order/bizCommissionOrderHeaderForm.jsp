<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.DefaultPropEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderDrawBackStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.BizOrderTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderPayProportionStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.RoleEnNameEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<html>
<head>
    <title>订单信息管理</title>
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
        <%--用于页面按下键盘Backspace键回退页面的问题--%>
        <%--处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外   --%>
        function banBackSpace(e){
            var ev = e || window.event;<%--获取event对象--%>
            var obj = ev.target || ev.srcElement;<%--获取事件源--%>
            var t = obj.type || obj.getAttribute('type');<%--获取事件源类型--%>
            <%--获取作为判断条件的事件类型--%>
            var vReadOnly = obj.getAttribute('readonly');
            var vEnabled = obj.getAttribute('enabled');
            <%--处理null值情况--%>
            vReadOnly = (vReadOnly == null) ? false : vReadOnly;
            vEnabled = (vEnabled == null) ? true : vEnabled;
            <%--当敲Backspace键时，事件源类型为密码或单行、多行文本的--%>
            <%--并且readonly属性为true或enabled属性为false的，则退格键失效--%>
            var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea")
                && (vReadOnly==true || vEnabled!=true))?true:false;
            <%--当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效--%>
            var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")
                ?true:false;
            <%--判断--%>
            if(flag2){
                return false;
            }
            if(flag1){
                return false;
            }
        }
        <%--禁止后退键 作用于Firefox、Opera--%>
        document.onkeypress=banBackSpace;
        <%--禁止后退键 作用于IE、Chrome--%>
        document.onkeydown=banBackSpace;
    </script>
    <script type="text/javascript">
        $(document).ready(function() {
            // $("#contentTable").tablesMergeCell({
            //     automatic: true,
            //     // 是否根据内容来合并
            //     cols: [0]
            //     // rows:[0,2]
            // });
            //$("#name").focus();
            var bizStatus = $("#bizStatus").val();
            if (!${fns:getUser().isAdmin()}) {
                $("#bizStatus").attr("disabled","true");
                $("#invStatus").attr("disabled","true");
            }
            if (bizStatus >= ${OrderHeaderBizStatusEnum.SUPPLYING.state}) {
                $("#totalExp").attr("disabled","disabled");
            }

            $("#inputForm").validate({
                submitHandler: function(form){
                    if('${totalPayTotal}' > 0){
                        alert("该订单已付款，请与系统管理员联系")
                        return;
                    }

                    if($("#address").val()==''){
                        $("#addError").css("display","inline-block")
                        return false;
                    }

                    var bb = true;
                    $("input[name='imgPhotosSorts']").each(function () {
                        if ($(this).val() == '') {
                            bb = false;
                            return;
                        }
                    });
                    if (bb) {
                        var mainImg = $("#prodMainImgDiv").find("[customInput = 'prodMainImgImg']");
                        var mainImgStr = "";
                        for (var i = 0; i < mainImg.length; i++) {
                            mainImgStr += ($(mainImg[i]).attr("src") + "|");
                        }
                        $("#photos").val(mainImgStr);
                    } else {
                        alert("退货凭证图片序号不能为空");
                        return false;
                    }
                    var orderId = $("#id").val();
                    var totalExp = $("#totalExp").val();
                    var totalDetail = $("#totalDetail").val();
                    $.ajax({
                        type:"post",
                        url:"${ctx}/biz/order/bizOrderHeader/checkTotalExp",
                        data:{id:orderId,totalExp:totalExp,totalDetail:totalDetail},
                        success:function (data) {
                            if (data == "serviceCharge") {
                                alert("最多只能优惠服务费的50%，您优惠的价格已经超标！请修改调整金额");
                            } else if (data == "orderLoss") {
                                alert("优惠后订单金额不能低于结算价，请修改调整金额");
                            } else if (data == "orderLowest") {
                                alert("优惠后订单金额不能低于结算价的95%，请修改调整金额");
                            } else if (data == "orderLowest8") {
                                alert("优惠后订单金额不能低于结算价的80%，请修改调整金额");
                            } else if (data == "ok") {
                                loading('正在提交，请稍等...');
                                form.submit();
                            }
                        }
                    });
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
            <%--订单地址--%>
            if($("#id").val() !=""){
                var option2=$("<option/>").text("${orderAddress.province.name}").val(${orderAddress.province.id});
                $("#province").append(option2);
                var option3=$("<option/>").text("${orderAddress.city.name}").val(${orderAddress.city.id});
                $("#city").append(option3);
                var option4=$("<option/>").text("${orderAddress.region.name}").val(${orderAddress.region.id});
                $("#region").append(option4);
                $("#address").val("${orderAddress.address}");
                <%--交货地址--%>
                if(${orderAddress.type==1 }){
                    var option2=$("<option/>").text("${orderAddress.province.name}").val(${orderAddress.province.id});
                    $("#jhprovince").append(option2);
                    var option3=$("<option/>").text("${orderAddress.city.name}").val(${orderAddress.city.id});
                    $("#jhcity").append(option3);
                    var option4=$("<option/>").text("${orderAddress.region.name}").val(${orderAddress.region.id});
                    $("#jhregion").append(option4);
                    $("#jhaddress").val("${address.address}");
                    $("#appointedDate").val("<fmt:formatDate value="${address.appointedTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>");
                }else{
                    var option2=$("<option/>").text("${address.province.name}").val(${address.province.id});
                    $("#jhprovince").append(option2);
                    var option3=$("<option/>").text("${address.city.name}").val(${address.city.id});
                    $("#jhcity").append(option3);
                    var option4=$("<option/>").text("${address.region.name}").val(${address.region.id});
                    $("#jhregion").append(option4);
                    $("#jhaddress").val("${address.address}");
                    $("#appointedDate").val("<fmt:formatDate value="${address.appointedTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>");
                }
                $("#province").change();
                $("#city").change();
                $("#region").change();
                $("#address").change();
                <%--交货地址--%>
                $("#jhprovince").change();
                $("#jhcity").change();
                $("#jhregion").change();
                $("#jhaddress").change();
            }
            $("#addAddressHref").click(function () {
                var officeId=$("#officeId").val();
                var officeName =$("#officeName").val();
                window.location.href="${ctx}/sys/office/sysOfficeAddress/form?ohId=${bizOrderHeader.id}&office.id="+officeId+"&office.name="+officeName+"&flag=order"
            });

            if($("#id").val() !="" && $("#bizStatus").val()!=""){
                <%--定义订单进度状态--%>
                if($("#bizStatus").val()==0){ <%--0未支付--%>
                    $("#payment").css("display","block");
                    $("#payment0").addClass("btn-primary");
                    $("#payment5").addClass("btn-default");
                    $("#payment10").addClass("btn-default");
                }else if($("#bizStatus").val()==5){ <%--5首付款支付--%>
                    $("#payment5_1").css("display","block");
                    $("#payment5_2").addClass("btn-primary");
                    $("#payment5_3").addClass("btn-primary");
                    $("#payment5_4").addClass("btn-default");
                    $("#payment5_5").addClass("btn-default");
                }else if($("#bizStatus").val()==10){ <%--全10部支付--%>
                    $("#payment10_1").css("display","block");
                    $("#payment10_2").addClass("btn-primary");
                    $("#payment10_3").addClass("btn-primary");
                    $("#payment10_4").addClass("btn-primary");
                    $("#payment10_5").addClass("btn-default");
                }else if($("#bizStatus").val()==15){ <%--15供货中--%>
                    $("#commodity").css("display","block");
                    $("#commodity5").addClass("btn-primary");
                    $("#commodity10").addClass("btn-primary");
                    $("#commodity15").addClass("btn-primary");
                    $("#commodity17").addClass("btn-default");
                    $("#commodity20").addClass("btn-default");
                }else if($("#bizStatus").val()==17){ <%--17采购中--%>
                    $("#purchase").css("display","block");
                    $("#purchase5").addClass("btn-primary");
                    $("#purchase10").addClass("btn-primary");
                    $("#purchase15").addClass("btn-primary");
                    $("#purchase17").addClass("btn-primary");
                    $("#purchase20").addClass("btn-default");
                }else if($("#bizStatus").val()==18){ <%--18采购完成--%>
                    $("#supply_core").css("display","block");
                    $("#supply_core17").addClass("btn-primary");
                    $("#supply_core18").addClass("btn-primary");
                    $("#supply_core19").addClass("btn-default");
                }else if($("#bizStatus").val()==19){ <%--19供应中心供货--%>
                    $("#deliver_goods").css("display","block");
                    $("#deliver_goods18").addClass("btn-primary");
                    $("#deliver_goods19").addClass("btn-primary");
                    $("#deliver_goods20").addClass("btn-default");
                }else if($("#bizStatus").val()==20){ <%--20已发货--%>
                    $("#goods").css("display","block");
                    $("#goods15").addClass("btn-primary");
                    $("#goods19").addClass("btn-primary");
                    $("#goods20").addClass("btn-primary");
                    $("#goods25").addClass("btn-default");
                }else if($("#bizStatus").val()==25){ <%--25客户已收货--%>
                    $("#have_received_goods").css("display","block");
                    $("#have_received_goods20").addClass("btn-primary");
                    $("#have_received_goods25").addClass("btn-primary");
                    $("#have_received_goods10").addClass("btn-default");
                    $("#have_received_goods30").addClass("btn-default");
                }else if($("#bizStatus").val()==30){ <%--30已完成--%>
                    $("#completed").css("display","block");
                    $("#completed10").addClass("btn-primary");
                    $("#completed25").addClass("btn-primary");
                    $("#completed30").addClass("btn-primary");
                    $("#completed40").addClass("btn-default");
                }else if($("#bizStatus").val()==35){ <%--35已取消--%>
                    $("#cancel").css("display","block");
                    $("#cancel0").addClass("btn-primary");
                    $("#cancel5").addClass("btn-primary");
                    $("#cancel10").addClass("btn-primary");
                    $("#cancel35").addClass("btn-primary");
                    $("#cancel40").addClass("btn-default");
                }else if($("#bizStatus").val()==40){ <%--40已删除 隐藏--%>
                    $("#already_delete").css("display","block");
                    $("#already_delete30").addClass("btn-primary");
                    $("#already_delete35").addClass("btn-primary");
                    $("#already_delete40").addClass("btn-primary");
                }
            }


        });

        function clickBut(){
            var officeId=$("#officeId").val();
            $("#province").empty();
            $("#city").empty();
            $("#region").empty();
            $("#address").empty();
            $.ajax({
                type:"post",
                url:"${ctx}/sys/office/sysOfficeAddress/findAddrByOffice?office.id="+officeId,
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
                        var option2=$("<option>").text(data.bizLocation.province.name).val(data.bizLocation.province.id);
                        $("#province").append(option2);
                        var option3=$("<option/>").text(data.bizLocation.city.name).val(data.bizLocation.city.id);
                        $("#city").append(option3);
                        var option4=$("<option/>").text(data.bizLocation.region.name).val(data.bizLocation.region.id);
                        $("#region").append(option4);
                        $("#address").val(data.bizLocation.address);

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
        function saveMon(type) {
            var orderIds = '${entity.orderIds}'
            var payTotal = $("#payTotal").val();
            var lastPayDate = $('#lastPayDate').val();
            var remark = $("#remark").val();
            var sellerId = '${entity.sellerId}';
            var id = '${entity.id}'
            if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
                alert("申请金额不正确!");
                return false;
            }
            if ($String.isNullOrBlank(lastPayDate)) {
                alert("请选择最后付款时间!");
                return false;
            }

            window.location.href="${ctx}/biz/order/bizCommissionOrder/saveCommission?totalCommission=" + payTotal + "&deadline=" + lastPayDate
                 + "&remark=" + remark + "&orderIds=" + orderIds + "&sellerId=" + sellerId;
        }
    </script>

    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>
    <script src="${ctxStatic}/bootstrap/multiselect.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/tree-multiselect/dist/jquery.tree-multiselect.js"></script>
    <script src="${ctxStatic}/jquery-select2/3.5.3/select2.js" type="text/javascript"></script>
    <script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>
    <script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
    <script src="${ctxStatic}/jquery-plugin/jquery.searchableSelect.js" type="text/javascript"></script>
    <script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="${ctxStatic}/common/base.js" type="text/javascript"></script>

</head>
<body>
<ul class="nav nav-tabs">
    <c:if test="${bizOrderHeader.flag=='check_pending'}">
        <li>
            <a href="${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}&source=${source}">订单信息列表</a>
        </li>
    </c:if>
    <c:if test="${empty bizOrderHeader.flag}">
        <c:if test="${empty bizOrderHeader.clientModify}">
            <li><a href="${ctx}/biz/order/bizOrderHeader?source=${source}">订单信息列表</a></li>
        </c:if>
        <c:if test="${bizOrderHeader.clientModify eq 'client_modify'}">
            <li><a href="${ctx}/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=${bizOrderHeader.consultantId}&source=${source}">订单信息列表</a></li>
        </c:if>
    </c:if>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizCommission" action="${ctx}/biz/order/bizOrderHeader/save?statuPath=${statuPath}" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <input type="hidden" name="sellerId" value="${entity.sellerId}" />
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">订单总价：</label>
        <div class="controls">
            <form:input path="totalDetail" readonly="readonly" placeholder="由系统自动生成" htmlEscape="false" maxlength="30" class="input-xlarge"/>
        </div>
    </div>

    <div id="cardNumber" class="control-group" >
        <label class="control-label">零售商卡号：</label>
        <div class="controls">
            <input id="cardNumberInput" readonly="readonly" value="${entity.customerInfo.cardNumber}" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>
    <div id="payee" class="control-group" >
        <label class="control-label">零售商收款人：</label>
        <div class="controls">
            <input id="payeeInput" readonly="readonly" value="${entity.customerInfo.payee}" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>
    <div id="bankName" class="control-group" >
        <label class="control-label">零售商开户行：</label>
        <div class="controls">
            <input id="bankNameInput" readonly="readonly" value="${entity.customerInfo.bankName}" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">申请金额：</label>
        <div class="controls">
            <input id="payTotal" name="planPay" type="text" readonly="readonly"
                   value="${entity.totalCommission}"
                   htmlEscape="false" maxlength="30" class="input-xlarge"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">最后付款时间：</label>
        <div class="controls">
            <input name="lastPayDate" id="lastPayDate" type="text" readonly="readonly"
                   maxlength="20"
                   class="input-medium Wdate required"
                   value="<fmt:formatDate value="${entity.deadline}"  pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
                   placeholder="必填！"/>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">备注：</label>
        <div class="controls">
            <form:textarea path="remark" maxlength="200" class="input-xlarge "/>
        </div>
    </div>

    <c:if test="${fn:length(auditList) > 0}">
        <div class="control-group">
            <label class="control-label">审核流程：</label>
            <div class="controls help_wrap">
                <div class="help_step_box fa">
                    <c:forEach items="${auditList}" var="v" varStatus="stat">
                        <c:if test="${v.current != 1}" >
                            <div class="help_step_item">
                                <div class="help_step_left"></div>
                                <div class="help_step_num">${stat.index + 1}</div>
                                处理人:${v.user.name}<br/><br/>
                                批注:${v.description}<br/><br/>
                                状态:
                                <c:if test="${v.objectName == 'ORDER_HEADER_SO_LOCAL'}">
                                ${v.jointOperationLocalProcess.name}
                                </c:if>
                                <c:if test="${v.objectName == 'ORDER_HEADER_SO_ORIGIN'}">
                                    ${v.jointOperationOriginProcess.name}
                                </c:if>
                                <c:if test="${v.objectName == 'biz_po_header'}">
                                    ${v.purchaseOrderProcess.name}
                                </c:if>
                                <c:if test="${v.objectName == 'biz_order_header'}">
                                    ${v.doOrderHeaderProcessFifth.name}
                                    <%--<c:if test="${entity.payProportion == OrderPayProportionStatusEnum.FIFTH.state}">--%>
                                        <%--${v.doOrderHeaderProcessFifth.name}--%>
                                    <%--</c:if>--%>
                                    <%--<c:if test="${entity.payProportion == OrderPayProportionStatusEnum.ALL.state}">--%>
                                        <%--${v.doOrderHeaderProcessAll.name}--%>
                                    <%--</c:if>--%>
                                </c:if>
                                <br/>
                                <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                <div class="help_step_right"></div>
                            </div>
                        </c:if>
                        <c:if test="${v.current == 1}">
                            <div class="help_step_item help_step_set">
                                <div class="help_step_left"></div>
                                <div class="help_step_num">${stat.index + 1}</div>
                                当前状态:
                                <c:if test="${v.objectName == 'ORDER_HEADER_SO_LOCAL'}">
                                    ${v.jointOperationLocalProcess.name}
                                </c:if>
                                <c:if test="${v.objectName == 'ORDER_HEADER_SO_ORIGIN'}">
                                    ${v.jointOperationOriginProcess.name}
                                </c:if>
                                <c:if test="${v.objectName == 'biz_po_header'}">
                                    ${v.purchaseOrderProcess.name}
                                </c:if>
                                <c:if test="${v.objectName == 'biz_order_header'}">
                                    ${v.doOrderHeaderProcessFifth.name}
                                </c:if>
                                <br/>
                                <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                <div class="help_step_right"></div>
                                <input type="hidden" value="${v.type}" id="currentJoType"/>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div>
    </c:if>


    <c:choose>
        <c:when test="${bizOrderHeader.flag=='check_pending'}">


        </c:when>
        <c:otherwise>
            <div class="form-actions">
                <!-- 一单到底订单审核 -->
                <shiro:hasPermission name="biz:order:bizOrderHeader:audit">
                    <c:if test="${entity.str == 'audit' && entity.orderType == BizOrderTypeEnum.PURCHASE_ORDER.state}">
                        <c:if test="${entity.bizPoHeader.commonProcessList == null}">
                            <input id="btnSubmit" type="button" onclick="checkPass('DO')" class="btn btn-primary"
                                   value="审核通过"/>
                            <input id="btnSubmit" type="button" onclick="checkReject('DO')" class="btn btn-primary"
                                   value="审核驳回"/>
                        </c:if>
                    </c:if>
                    <%--<c:if test="${entity.str == 'audit' && type != 0 && type != 1}">--%>
                        <%--<c:if test="${entity.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state}">--%>
                            <%--<c:if test="${entity.bizPoHeader.commonProcessList == null}">--%>
                                <%--<input id="btnSubmit" type="button" onclick="checkPass('SO')" class="btn btn-primary"--%>
                                       <%--value="审核通过"/>--%>
                                <%--<input id="btnSubmit" type="button" onclick="checkReject('SO')" class="btn btn-primary"--%>
                                       <%--value="审核驳回"/>--%>
                            <%--</c:if>--%>
                        <%--</c:if>--%>
                    <%--</c:if>--%>

                    <c:if test="${entity.str == 'audit' && (type != 0 || type != 1)}">
                        <c:if test="${(entity.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state || entity.orderType == BizOrderTypeEnum.COMMISSION_ORDER.state) && currentAuditStatus.type != 777 && currentAuditStatus.type != 666}">
                                <input type="button" onclick="checkPass('JO')" class="btn btn-primary"
                                       value="通过"/>
                                <input type="button" onclick="checkReject('JO')" class="btn btn-primary"
                                       value="驳回"/>
                        </c:if>
                    </c:if>

                    <c:if test="${entity.str == 'pay'}">
                        <input id="btnSubmit" type="button" onclick="pay()" class="btn btn-primary" value="确认支付"/>
                    </c:if>

                    <%--<c:if test="${entity.str == 'createPay'}">--%>
                        <%--<input id="btnSubmit" type="button" onclick="saveMon('createPay')" class="btn btn-primary" value="申请付款"/>--%>
                    <%--</c:if>--%>
                </shiro:hasPermission>

                    <!-- 一单到底，采购单审核 -->
                <shiro:hasPermission name="biz:po:bizPoHeader:audit">
                    <c:if test="${entity.str == 'audit'}">
                    <c:if test="${orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                        <c:if test="${entity.bizPoHeader.commonProcessList != null
                        && fn:length(entity.bizPoHeader.commonProcessList) > 0
                        && (currentAuditStatus.type == 777 || currentAuditStatus.type == 666)
                        }">
                            <input id="btnSubmit" type="button" onclick="checkPass('PO')" class="btn btn-primary"
                                   value="审核通过"/>
                            <input id="btnSubmit" type="button" onclick="checkReject('PO')" class="btn btn-primary"
                                   value="审核驳回"/>
                        </c:if>
                    </c:if>

                    <c:if test="${orderType == DefaultPropEnum.PURSEHANGER.propValue}">
                        <c:if test="${entity.bizPoHeader.commonProcessList != null
                        && fn:length(entity.bizPoHeader.commonProcessList) > 0
                        }">
                            <input id="btnSubmit" type="button" onclick="checkPass('PO')" class="btn btn-primary"
                                   value="审核通过"/>
                            <input id="btnSubmit" type="button" onclick="checkReject('PO')" class="btn btn-primary"
                                   value="审核驳回"/>
                        </c:if>
                    </c:if>
                    </c:if>
                </shiro:hasPermission>

                <input id="btnSubmit" type="button" onclick="saveMon('createPay')" class="btn btn-primary" value="申请付款"/>
                &nbsp;&nbsp;&nbsp;
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
            </div>
        </c:otherwise>
    </c:choose>
</form:form>

<%--详情列表--%>
<sys:message content="${message}"/>
<c:if test="${fn:length(orderHeaderList) > 0}">
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>订单号</th>
        <th>商品名称</th>
        <th>商品编号</th>
        <th>商品货号</th>
        <th>采购数量</th>
        <th>总 额</th>
        <th>已发货数量</th>
        <th>发货方</th>
        <th>商品零售价</th>
        <th>佣金</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${orderHeaderList}" var="bizOrderHeader">
        <c:forEach items="${bizOrderHeader.orderDetailList}" var="bizOrderDetail">
            <tr>
                <td>
                        ${bizOrderHeader.orderNum}
                </td>
                <td>
                        ${bizOrderDetail.skuName}
                </td>
                <td>
                        ${bizOrderDetail.partNo}
                </td>
                <td>
                        ${bizOrderDetail.skuInfo.itemNo}
                </td>
                <td>
                        ${bizOrderDetail.ordQty}
                </td>
                <td>
                    <c:if test="${bizOrderDetail.unitPrice !=null && bizOrderDetail.ordQty !=null}">
                        <fmt:formatNumber type="number" value=" ${bizOrderDetail.unitPrice * bizOrderDetail.ordQty}" pattern="0.00"/>
                    </c:if>
                </td>
                <td>
                        ${bizOrderDetail.sentQty}
                </td>
                <td>
                        ${bizOrderDetail.suplyis.name}
                </td>
                <td>
                        ${bizOrderDetail.salePrice}
                </td>
                <td>
                        ${bizOrderDetail.detailCommission}
                </td>
            </tr>
        </c:forEach>
    </c:forEach>
    </tbody>
</table>
</c:if>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.BizOrderTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.DefaultPropEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>

<html>
<head>
    <title>订单信息管理</title>
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
            //$("#name").focus();
            var bizStatus = $("#bizStatus").val();
            if (bizStatus >= ${OrderHeaderBizStatusEnum.SUPPLYING.state}) {
                $("#totalExp").attr("disabled","disabled");
            }
            $("#inputForm").validate({
                submitHandler: function(form){
                    if($("#address").val()==''){
                        $("#addError").css("display","inline-block")
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
                            if (data == "photoOrder") {
                                alert("拍照订单优惠后，应付金额不能低于原价的90%，请修改调整金额");
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
            $("#updateMoney").click(function () {
                updateMoney();
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
        function updateMoney() {
            if(confirm("确定修改价钱吗？")){
                var orderId = $("#id").val();
                var totalExp = $("#totalExp").val();
                var totalDetail = $("#totalDetail").val();
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/order/bizOrderHeader/checkTotalExp",
                    data:{id:orderId,totalExp:totalExp,totalDetail:totalDetail},
                    success:function (data) {
                        if (data == "photoOrder") {
                            alert("拍照订单优惠后，应付金额不能低于原价的90%，请修改调整金额");
                        } else if (data == "ok") {
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
                    data:"id="+$("#id").val()+"&flag=${bizOrderHeader.flag}&objJsp=${OrderHeaderBizStatusEnum.SUPPLYING.state}&bizLocation.address="+$("#jhaddress").val()+"&bizLocation.appointedTime="+$("#appointedDate").val()+"&localSendIds="+localSendIds+"&boo="+boo
                    +"&bizLocation.province.id="+$("#jhprovince").val()+"&bizLocation.city.id="+$("#jhcity").val()+"&bizLocation.region.id="+$("#jhregion").val(),
                    success:function(commis){
                        if(commis=="ok"){
                            alert(" 同意发货 ");
                            window.location.href = "${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}";
                        }else{
                            alert(" 发货失败 ");
                            window.location.href = "${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}";
                        }
                    }
                });
            }else if(obj==${OrderHeaderBizStatusEnum.UNAPPROVE.state}){ <%--45不同意发货--%>
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/order/bizOrderHeader/Commissioner",
                    data:"id="+$("#id").val()+"&flag=${bizOrderHeader.flag}&objJsp=${OrderHeaderBizStatusEnum.UNAPPROVE.state}&bizLocation.address="+$("#jhaddress").val()+"&bizLocation.appointedTime="+$("#appointedDate").val()+"&localSendIds="+localSendIds
                    +"&bizLocation.province.id="+$("#jhprovince").val()+"&bizLocation.city.id="+$("#jhcity").val()+"&bizLocation.region.id="+$("#jhregion").val(),
                    success:function(commis){
                        if(commis=="ok"){
                            alert(" 不同意发货 ");
                            window.location.href = "${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}";
                        }else{
                            alert(" 发货失败 ");
                            window.location.href = "${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}";
                        }
                    }
                });
            }
        }
    </script>
    <style type="text/css">
        #remark,#flip,#addRemark
        {
            margin:0px;
            padding:5px;
            text-align:center;
            background:#e5eecc;
            border:solid 1px #c3c3c3;
        }
        #remark
        {
            height:120px;
            /*display:none;*/
        }
    </style>
    <script type="text/javascript">
        function saveRemark() {
            var orderId = $("#id").val();
            var remark;
            remark=prompt("请输入你要添加的备注");
            // alert(remark);
            if (remark == null) {
                return false;
            }
            $.ajax({
                type:"post",
                url:"${ctx}/biz/order/bizOrderComment/addComment",
                data:{orderId:orderId,remark:remark},
                success:function (data) {
                    if (data == "error") {
                        alert("添加订单备注失败，备注可能为空");
                    }
                    if (data == "ok") {
                        alert("添加订单备注成功");
                        window.location.reload();
                    }
                }
            });
        }
        <%--$("#addRemark").click(function () {--%>
        <%--var orderId = $("#id").val();--%>
        <%--alert(orderId);--%>
        <%--var remark;--%>
        <%--remark=prompt("请输入你要添加的备注");--%>
        <%--alert(remark);--%>
        <%--$.ajax({--%>
        <%--type:"post",--%>
        <%--url:"${ctx}/biz/order/bizOrderComment/addComment",--%>
        <%--data:{orderId:orderId,remark:remark},--%>
        <%--success:function (data) {--%>
        <%--if (data == "error") {--%>
        <%--alert("添加订单备注失败，备注可能为空");--%>
        <%--}--%>
        <%--if (data == "ok") {--%>
        <%--alert("添加订单备注成功");--%>
        <%--window.reload();--%>
        <%--}--%>
        <%--}--%>
        <%--});--%>
        <%--});--%>
    </script>
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

    <li class="active">
        <c:if test="${entity.orderNoEditable eq 'editable'}">
            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&orderNoEditable=${entity.orderNoEditable}&source=${source}">订单信息支付</a>
        </c:if>
        <c:if test="${entity.orderDetails eq 'details'}">
            <a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${bizOrderHeader.id}&orderDetails=${entity.orderDetails}&source=${source}">订单信息详情</a>
        </c:if>
        <c:if test="${bizOrderHeader.flag eq 'check_pending'}">
            <a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${bizOrderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}&source=${source}">订单信息审核</a>
        </c:if>
        <%--<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">--%>
            <%--<c:if test="${empty bizOrderHeader.clientModify}">--%>
                <%--<a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${orderHeader.id}&statu=${statu}">订单信息查看</a>--%>
            <%--</c:if>--%>
            <%--<c:if test="${bizOrderHeader.clientModify eq 'client_modify'}">--%>
                <%--<a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${bizOrderHeader.id}&flag=check_pending&consultantId=${bizOrderHeader.consultantId}">订单信息查看</a>--%>
            <%--</c:if>--%>
        <%--</c:if>--%>
    </li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <input type="hidden" name="oneOrder" value="${entity.oneOrder}">
    <input type="hidden" id="bizOrderMark" name="orderMark" value="${bizOrderHeader.orderMark}">
    <input type="hidden" name="clientModify" value="${bizOrderHeader.clientModify}" />
    <input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}" />
    <input type="hidden" id="orderType" value="${orderType}"/>
    <input type="hidden" name="source" value="${source}"/>
    <form:hidden path="platformInfo.id" value="6"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">订单编号：</label>
        <div class="controls">
            <form:input path="orderNum" disabled="true" placeholder="由系统自动生成" htmlEscape="false" maxlength="30" class="input-xlarge"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">经销店名称：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <sys:treeselect id="office" name="customer.id" value="${entity2.customer.id}" labelName="customer.name"
                                labelValue="${entity2.customer.name}" disabled="disabled"
                                notAllowSelectParent="true"
                                title="经销店" url="/sys/office/queryTreeList?type=6" cssClass="input-xlarge"
                                allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
                    <c:if test="${orderCenter.centers !=null }">
                        该经销店的采购中心： <font color="#04B404">${orderCenter.centers.name}</font>，
                        客户专员：<font color="#04B404">${orderCenter.consultants.name}(${orderCenter.consultants.mobile})</font>
                    </c:if>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <sys:treeselect id="office" name="customer.id" value="${entity2.customer.id}" labelName="customer.name"
                                labelValue="${entity2.customer.name}"
                                notAllowSelectParent="true"
                                title="经销店" url="/sys/office/queryTreeList?type=6" cssClass="input-xlarge required"
                                allowClear="${office.currentUser.admin}" onchange="clickBut();" dataMsgRequired="必填信息"/>
                    <c:if test="${orderCenter.centers !=null }">
                        该经销店的采购中心： <font color="#04B404">${orderCenter.centers.name}</font>，
                        客户专员：<font color="#04B404">${orderCenter.consultants.name}(${orderCenter.consultants.mobile})</font>
                    </c:if>
                <span class="help-inline"><font color="red">*</font></span>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">商品总价：</label>
        <div class="controls">
            <form:input path="totalDetail" htmlEscape="false" placeholder="0.0" readOnly="true" class="input-xlarge"/>
            <input name="totalDetail" value="${entity.totalDetail}" htmlEscape="false" type="hidden"/>
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
            <form:input path="freight" htmlEscape="false" disabled="true" class="input-xlarge"/>
        </div>
    </div>
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
                    <fmt:formatNumber type="percent" value="${bizOrderHeader.receiveTotal/(bizOrderHeader.totalDetail+bizOrderHeader.freight)}" maxFractionDigits="2" />
                </font> (<fmt:formatNumber type="number" value="${bizOrderHeader.receiveTotal}" pattern="0.00"/>)
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">服务费：</label>
            <div class="controls">
                <input type="text" value="${bizOrderHeader.totalExp}" disabled="true" class="input-xlarge">
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">发票状态：</label>
            <div class="controls">
                <form:select path="invStatus" class="input-xlarge" disabled="true">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">业务状态：</label>
            <div class="controls">
                <form:select path="bizStatus" class="input-xlarge" disabled="true">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>
            </div>
        </div>
    <div class="control-group">
        <label class="control-label">收货人：</label>
        <div class="controls">
            <form:input path="bizLocation.receiver" placeholder="收货人名称" htmlEscape="false" disabled="true"
                        class="input-xlarge"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">联系电话：</label>
        <div class="controls">
            <form:input path="bizLocation.phone" placeholder="请输入联系电话" htmlEscape="false" disabled="true"
                        class="input-xlarge required"/>
        </div>
    </div>
    <div class="control-group" id="add1">
        <label class="control-label">收货地址：</label>
        <div class="controls">
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
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group" id="add2" style="display:none">
        <label class="control-label">收货地址：</label>
        <div class="controls">
            <input id="addAddressHref" type="button" value="新增地址" htmlEscape="false" class="input-xlarge required"/>
            <label class="error" id="addError" style="display:none;">必填信息</label>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <div class="control-group" id="add3">
        <label class="control-label">详细地址：</label>
        <div class="controls">
            <input type="text" id="address" name="bizLocation.address" htmlEscape="false" readOnly="true"
                   class="input-xlarge required"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备&nbsp;注：</label>
        <div id="remark" class="controls" style="overflow:auto; float:left;text-align: left; width: 400px;">
            <c:forEach items="${commentList}" var="comment">
                <p class="box">${comment.comments}<br>${comment.createBy.name}&nbsp;&nbsp;<fmt:formatDate value="${comment.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
            </c:forEach>
        </div>
            <%--<span id="flip">全部备注</span>&nbsp;&nbsp;--%>
        <span id="addRemark" onclick="saveRemark()">增加备注</span>
    </div>
    <div class="control-group">
        <label class="control-label">商品信息图：</label>
        <div id="orderSkuPhoto" class="controls">
            <c:forEach items="${imgUrlList}" var="imgUrl">
                <a href="${imgUrl.imgServer}${imgUrl.imgPath}" target="view_window">
                <img src="${imgUrl.imgServer}${imgUrl.imgPath}" style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/></a>
            </c:forEach>
        </div>
    </div>

        <c:if test="${bizOrderHeader.bizStatus!=45 }">
        <div class="control-group">
            <label class="control-label">进展信息：</label>
            <div class="controls" style="width: 100%;">
                <div id="payment" style="display:none;width: 29%;float: left;">
                    <div style="float: left;padding-top: 16px;">
                        <button id="payment0" type="button" class="btn btn-arrow-left">未支付</button>
                        &nbsp;&rarr;&nbsp;
                    </div>
                    <div style="float: left;">
                        <div style="padding-bottom: 1px;">
                            <button id="payment5" type="button" class="btn btn-arrow-right">首付款支付</button>
                        </div>
                        <div style="padding-top: 1px;">
                        <button id="payment10" type="button" class="btn btn-arrow-right">全部支付</button>
                        </div>
                    </div>
                </div>
                <div id="payment5_1" style="display:none;width: 29%;">
                    <div style="float:left;padding-top: 16px;">
                        <button id="payment5_2" type="button" class="btn btn-arrow-left">未支付</button>&nbsp;&rarr;
                        <button id="payment5_3" type="button" class="btn btn-arrow-right">首付款支付</button>&nbsp;&rarr;
                    </div>
                    <div style="float:left;">
                        <div style="padding-top: 1px;">
                            <button id="payment5_4" type="button" class="btn btn-arrow-right">全部支付</button>
                        </div>
                        <div style="padding-top: 1px;">
                            <button id="payment5_5" type="button" class="btn btn-arrow-left">供货中</button>
                        </div>
                    </div>
                </div>
                <div id="payment10_1" style="display:none;width: 29%;">
                    <div style="float:left;width: 110px;">
                        <div style="padding-top: 1px;float: right;">
                            <button id="payment10_2" type="button" class="btn btn-arrow-left">未支付</button>
                        </div>
                        <div style="padding-top: 1px;float: right;">
                            <button id="payment10_3" type="button" class="btn btn-arrow-right">首付款支付</button>
                        </div>
                    </div>
                    <div style="float:left;padding-top: 16px;">
                        &rarr;&nbsp;<button id="payment10_4" type="button" class="btn btn-arrow-right">全部支付</button>
                        &nbsp;&rarr;
                        <button id="payment10_5" type="button" class="btn btn-arrow-left">供货中</button>
                    </div>
                </div>
                <div id="commodity" style="display:none;width: 29%;">
                    <div style="float:left;width: 110px;">
                        <div style="padding-top: 1px;float: right;">
                            <button id="commodity5" type="button" class="btn btn-arrow-right">首付款支付</button>
                        </div>
                        <div style="padding-top: 1px;float: right;">
                            <button id="commodity10" type="button" class="btn btn-arrow-right">全部支付</button>
                        </div>
                    </div>
                    <div style="float:left;padding-top: 16px;">
                    &rarr;&nbsp;<button id="commodity15" type="button" class="btn btn-arrow-left">供货中</button>&nbsp;&rarr;&nbsp;
                    </div>
                    <div style="float: left">
                        <div style="padding-bottom: 1px;">
                            <button id="commodity17" type="button" class="btn btn-arrow-right">采购中</button>
                        </div>
                        <div style="padding-top: 1px;">
                            <button id="commodity20" type="button" class="btn btn-arrow-right">已发货</button>
                        </div>
                    </div>
                </div>
                <div id="purchase" style="display:none;float: left;width: 29%;">
                    <div style="float:left;width: 120px;">
                        <div style="padding-bottom: 1px;float: right;">
                            <button id="purchase5" type="button" class="btn btn-arrow-left">首付款支付</button>&nbsp;&rarr;
                        </div>
                        <div style="padding-bottom: 1px;float: right;">
                            <button id="purchase10" type="button" class="btn btn-arrow-right">全部支付</button>&nbsp;&rarr;
                        </div>
                        <div style="padding-bottom: 1px;float: right;">
                            <button id="purchase15" type="button" class="btn btn-arrow-right">供货中</button>&nbsp;&rarr;
                        </div>
                    </div>
                    <div style="float: left">
                        <div style="padding-top: 31px;padding-left: 10px;">
                            <button id="purchase17" type="button" class="btn btn-arrow-right">采购中</button>&nbsp;&rarr;&nbsp;
                            <button id="purchase18" type="button" class="btn btn-arrow-right">采购完成</button>
                        </div>
                    </div>
                </div>
                <div id="supply_core" style="display:none;width: 30%;">
                    <button id="supply_core17" type="button" class="btn btn-arrow-left">采购中</button>&nbsp;&rarr;&nbsp;
                    <button id="supply_core18" type="button" class="btn btn-arrow-right">采购完成</button>&nbsp;&rarr;&nbsp;
                    <button id="supply_core19" type="button" class="btn btn-arrow-right">供应中心供货</button>
                </div>
                <div id="deliver_goods" style="display:none;width: 30%;">
                    <button id="deliver_goods18" type="button" class="btn btn-arrow-left">采购完成</button>&nbsp;&rarr;&nbsp;
                    <button id="deliver_goods19" type="button" class="btn btn-arrow-right">供应中心供货</button>&nbsp;&rarr;&nbsp;
                    <button id="deliver_goods20" type="button" class="btn btn-arrow-right">已发货</button>
                </div>
                <div id="goods" style="display:none;width: 30%;">
                    <div style="float:left;width: 120px;">
                        <div style="padding-top: 1px;float: right;">
                            <button id="goods15" type="button" class="btn btn-arrow-right">供货中</button>
                        </div>
                        <div style="padding-top: 1px;float: right;">
                            <button id="goods19" type="button" class="btn btn-arrow-right">供应中心供货</button>
                        </div>
                    </div>
                    <div style="float: left">
                        <div style="padding-bottom: 1px;padding-top: 16px;">
                            &nbsp;&rarr;&nbsp;<button id="goods20" type="button" class="btn btn-arrow-left">已发货</button>&nbsp;&rarr;&nbsp;
                            <button id="goods25" type="button" class="btn btn-arrow-right">客户已收货</button>
                        </div>
                    </div>
                </div>
                <div id="have_received_goods" style="display:none;width: 29%;">
                    <div style="float: left;padding-top: 16px;">
                        <button id="have_received_goods20" type="button" class="btn btn-arrow-left">已发货</button>&nbsp;&rarr;&nbsp;
                        <button id="have_received_goods25" type="button" class="btn btn-arrow-left">客户已收货</button>&nbsp;&rarr;&nbsp;
                    </div>
                    <div style="float: left;">
                        <div style="padding-bottom: 1px;">
                            <button id="have_received_goods10" type="button" class="btn btn-arrow-right">全部支付</button>
                        </div>
                        <div style="padding-top: 1px;">
                            <button id="have_received_goods30" type="button" class="btn btn-arrow-right">已完成</button>
                        </div>
                    </div>
                </div>
                <div id="completed" style="display:none;width: 29%;">
                    <div style="float:left;width: 110px;">
                        <div style="padding-bottom: 1px;float: right;">
                            <button id="completed10" type="button" class="btn btn-arrow-left">全部支付</button>
                        </div>
                        <div style="padding-bottom: 1px;float: right;">
                            <button id="completed25" type="button" class="btn btn-arrow-left">客户已收货</button>
                        </div>
                    </div>
                    <div style="float: left">
                        <div style="padding-bottom: 1px;padding-top: 16px;">
                            &nbsp;&rarr;&nbsp;<button id="completed30" type="button" class="btn btn-arrow-right">已完成</button>&nbsp;&rarr;&nbsp;
                            <button id="completed40" type="button" class="btn btn-arrow-right">已删除</button>
                        </div>
                    </div>
                </div>
                <div id="cancel" style="display:none;float: left;width: 29%;">
                    <div style="float:left;width: 120px;">
                        <div style="padding-bottom: 1px;float: right;">
                            <button id="cancel0" type="button" class="btn btn-arrow-left">未支付</button>&nbsp;&rarr;&nbsp;
                        </div>
                        <div style="padding-bottom: 1px;float: right;">
                            <button id="cancel5" type="button" class="btn btn-arrow-right">首付款支付</button>&nbsp;&rarr;&nbsp;
                        </div>
                        <div style="padding-bottom: 1px;float: right;">
                            <button id="cancel10" type="button" class="btn btn-arrow-right">全部支付</button>&nbsp;&rarr;&nbsp;
                        </div>
                    </div>
                    <div style="float: left">
                        <div style="padding-top: 31px;">
                            <button id="cancel35" type="button" class="btn btn-arrow-right">已取消</button>&nbsp;&rarr;&nbsp;
                            <button id="cancel40" type="button" class="btn btn-arrow-right">已删除</button>
                        </div>
                    </div>
                </div>
                <div id="already_delete" style="display:none;width: 29%;">
                    <div style="float:left;width: 90px;">
                        <div style="padding-bottom: 1px;float: right">
                            <button id="already_delete30" type="button" class="btn btn-arrow-left">已完成</button>
                        </div>
                        <div style="padding-bottom: 1px;float: right">
                            <button id="already_delete35" type="button" class="btn btn-arrow-left">已取消</button>
                        </div>
                    </div>
                    <div style="float: left;">
                        <div style="padding-top: 16px;">
                            &rarr;<button id="already_delete40" type="button" class="btn btn-arrow-right">已删除</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </c:if>
        <c:if test="${fn:length(statusList) > 0}">
            <div class="control-group">
                <label class="control-label">状态流程：</label>
                <div class="controls help_wrap">
                    <div class="help_step_box fa">
                        <c:forEach items="${statusList}" var="v" varStatus="stat">
                            <c:if test="${!stat.last}" >
                                <div class="help_step_item">
                                    <div class="help_step_left"></div>
                                    <div class="help_step_num">${stat.index + 1}</div>
                                    处理人:${v.createBy.name}<br/><br/>
                                    状态:${statusMap[v.bizStatus].desc}<br/>
                                    <fmt:formatDate value="${v.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    <div class="help_step_right"></div>
                                </div>
                            </c:if>
                            <c:if test="${stat.last}">
                                <div class="help_step_item help_step_set">
                                    <div class="help_step_left"></div>
                                    <div class="help_step_num">${stat.index + 1}</div>
                                    处理人:${v.createBy.name}<br/><br/>
                                    状态:${statusMap[v.bizStatus].desc}<br/>
                                    <fmt:formatDate value="${v.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    <div class="help_step_right"></div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${statu != '' && statu =='unline'}">
            <div class="control-group">
                <label class="control-label">支付信息:</label>
                <div class="controls">
                    <table class="table table-striped table-bordered table-condensed">
                        <thead>
                            <tr>
                                <th>流水号</th>
                                <th>支付金额</th>
                                <th>状态</th>
                                <th>创建时间</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${unlineList}" var="unline">
                                <tr>
                                    <td>${unline.serialNum}</td>
                                    <td>${unline.unlinePayMoney}</td>
                                    <td>${fns:getDictLabel(unline.bizStatus,"biz_order_unline_bizStatus" ,"未知状态" )}</td>
                                    <td><fmt:formatDate value="${unline.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
    <c:if test="${entity.orderType == BizOrderTypeEnum.PURCHASE_ORDER.state || entity.orderType == BizOrderTypeEnum.PHOTO_ORDER.state}">
    <div class="control-group">
        <label class="control-label">付款约定：</label>
        <div class="controls">
            <table class="table table-striped table-bordered">
                <thead>
                    <th>付款时间</th>
                    <th>金额</th>
                </thead>
                <tbody>
                    <c:forEach items="${appointedTimeList}" var="appointedTime">
                        <tr>
                            <td><fmt:formatDate value="${appointedTime.appointedDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                            <td>${appointedTime.appointedMoney}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
        <div class="control-group">
            <div class="controls">
                <div style="float:left;color: red;font-size: medium;margin-right: 50px">
                    甲方(经销店):${entity2.customer.name}<br>
                负责人:${custUser.name}<br>
                联系电话:${custUser.mobile}
                </div>
                <div style="float:left;color: red;font-size: medium;margin-right: 50px">
                乙方(供应商):${vendUser.vendor.name}<br>
                负责人:${vendUser.name}<br>
                联系电话:${vendUser.mobile}
                </div>
                <div style="float:left;color: red;font-size: medium;margin-right: 50px">
                丙方(万户通):万户通总公司<br>
                负责人:${orderCenter.consultants.name}<br>
                联系电话:${orderCenter.consultants.mobile}
                </div>
            </div>
        </div>
    </c:if>
    <c:choose>
        <c:when test="${bizOrderHeader.flag=='check_pending'}">
            <div class="control-group" id="jhadd1">
                <label class="control-label">交货地址:</label>
                <div class="controls">
                    <select id="jhprovince" class="input-medium" name="bizLocation.province.id" disabled="disabled"
                            style="width:150px;text-align: center;">
                        <c:if test="${bizOrderHeader.id ==null}">
                            <option value="-1">—— 省 ——</option>
                        </c:if>
                    </select>
                    <select id="jhcity" class="input-medium" name="bizLocation.city.id" disabled="disabled"
                            style="width:150px;text-align: center;">
                        <c:if test="${bizOrderHeader.id ==null}">
                            <option value="-1">—— 市 ——</option>
                        </c:if>
                    </select>
                    <select id="jhregion" class="input-medium" name="bizLocation.region.id" disabled="disabled" style="width:150px;text-align: center;">
                        <c:if test="${bizOrderHeader.id ==null}">
                            <option value="-1">—— 区 ——</option>
                        </c:if>
                    </select>
                </div>
            </div>
            <div class="control-group" id="jhadd2" style="display:none">
                <label class="control-label">交货地址:</label>
                <div class="controls">
                    <input id="addJhAddressHref" type="button" value="新增地址" htmlEscape="false"
                           class="input-xlarge"/>
                    <label class="error" id="addError" style="display:none;">必填信息</label>
                </div>
            </div>
            <div class="control-group" id="jhadd3">
                <label class="control-label">详细地址:</label>
                <div class="controls">
                    <input type="text" id="jhaddress" name="bizLocation.address" htmlEscape="false"
                           class="input-xlarge"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">交货时间:</label>
                <div class="controls">
                    <input id="appointedDate" name="bizLocation.appointedTime" type="text" readonly="readonly"
                           maxlength="20" class="input-xlarge Wdate"
                           onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="form-actions">
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                    <shiro:hasPermission name="biz:order:bizOrderHeader:edit">
                        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;
                    </shiro:hasPermission>
                </c:if>
                <c:if test="${empty entity.orderNoEditable && bizOrderHeader.flag != 'check_pending'}">
                    <c:if test="${bizOrderHeader.id!=null}">
                        <input onclick="window.print();" type="button" class="btn btn-primary" value="打印订单" style="background:#F78181;"/>
                    </c:if>
                </c:if>
                <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
            </div>
        </c:otherwise>
    </c:choose>
</form:form>

<%--<c:if test="${empty entity.orderNoEditable && bizOrderHeader.flag != 'check_pending'}">--%>
<%--<div class="form-actions">--%>
        <%--<c:if test="${bizOrderHeader.id!=null}">--%>
            <%--<input onclick="window.print();" type="button" class="btn btn-primary" value="打印订单" style="background:#F78181;"/>--%>
        <%--</c:if>--%>
<%--</div>--%>
<%--</c:if>--%>
<c:if test="${bizOrderHeader.flag=='check_pending'}">
    <div class="form-actions">
        <shiro:hasPermission name="biz:order:bizOrderHeader:edit">
            <input class="btn btn-primary" type="button"
                   onclick="checkPending(${OrderHeaderBizStatusEnum.SUPPLYING.state})" value="同意发货"/>&nbsp;
            <input class="btn btn-primary" type="button"
                   onclick="checkPending(${OrderHeaderBizStatusEnum.UNAPPROVE.state})" value="不同意发货"/>&nbsp;
        </shiro:hasPermission>
    </div>
</c:if>
<%--<div id="img-modal" class="modal fade">--%>
    <%--<div id="img-dialog" class="modal-dialog" style="width: 98%; height: 98%;text-align: center;">--%>
        <%--<div id="img-content" class="modal-content">--%>
            <%--<img id="img-zoom" src="" style="max-height: 100%; max-width: 100%;margin:10px;">--%>
        <%--</div><!-- /.modal-content -->--%>
    <%--</div><!-- /.modal-dialog -->--%>
<%--</div><!-- /.modal -->--%>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>

<html>
<head>
    <title>订单信息管理</title>
    <meta name="decorator" content="default"/>
    <%--<script type="text/javascript" src="D:\IDEA_BatisJect\wanhugou_bg\src\main\webapp\static\ckeditor\_source\core\keyboard.js">
    </script>&lt;%&ndash;用于键盘Bcackspace回退BUG问题&ndash;%&gt;--%>
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
                }else{
                    var option2=$("<option/>").text("${address.province.name}").val(${address.province.id});
                    $("#jhprovince").append(option2);
                    var option3=$("<option/>").text("${address.city.name}").val(${address.city.id});
                    $("#jhcity").append(option3);
                    var option4=$("<option/>").text("${address.region.name}").val(${address.region.id});
                    $("#jhregion").append(option4);
                    $("#jhaddress").val("${address.address}");
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
            })
        });

        function clickBut(){
            var officeId=$("#officeId").val();
            $("#province").empty();
            $("#city").empty();
            $("#region").empty();
            $("#address").empty();
            <%--交货地址--%>
            <%--$("#jhprovince").empty();--%>
            <%--$("#jhcity").empty();--%>
            <%--$("#jhregion").empty();--%>
            <%--$("#jhaddress").empty();--%>
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
                        <%--alert(data.bizLocation.province.name+"------");--%>
                        var option2=$("<option>").text(data.bizLocation.province.name).val(data.bizLocation.province.id);
                        $("#province").append(option2);
                        var option3=$("<option/>").text(data.bizLocation.city.name).val(data.bizLocation.city.id);
                        $("#city").append(option3);
                        var option4=$("<option/>").text(data.bizLocation.region.name).val(data.bizLocation.region.id);
                        $("#region").append(option4);
                        $("#address").val(data.bizLocation.address);
                        <%--交货地址--%>
                        <%--var option2=$("<option>").text(data.bizLocation.province.name).val(data.bizLocation.province.id);--%>
                        <%--$("#jhprovince").append(option2);--%>
                        <%--var option3=$("<option/>").text(data.bizLocation.city.name).val(data.bizLocation.city.id);--%>
                        <%--$("#jhcity").append(option3);--%>
                        <%--var option4=$("<option/>").text(data.bizLocation.region.name).val(data.bizLocation.region.id);--%>
                        <%--$("#jhregion").append(option4);--%>
                        <%--$("#jhaddress").val(data.bizLocation.address);--%>
                        $("#province").change();
                        $("#city").change();
                        $("#region").change();
                        $("#address").change();
                        <%--交货地址--%>
                        <%--$("#jhprovince").change();--%>
                        <%--$("#jhcity").change();--%>
                        <%--$("#jhregion").change();--%>
                        <%--$("#jhaddress").change();--%>
                    }
                }
            });
        }
    </script>
    <script type="text/javascript">
        function btnOrder(){
            var button=$("#btnOrderButton").disabled=true;
            var buttonText=$("#payMentOne").val();
            if(buttonText==""){
                alert("内容不能为空");
                button=$("#btnOrderButton").disabled=false;
                return false;
            }
            $.ajax({
                type:"post",
                url:"${ctx}/biz/order/bizOrderHeader/saveOrderHeader?payMentOne="+$("#payMentOne").val()+"&tobePaid=${entity.tobePaid}",
                data:{id:$("#id").val()},
                success:function(data){
                    if(data=="ok"){
                        alert("支付成功！");
                        window.location.href="${ctx}/biz/order/bizOrderHeader/";
                    }else{
                        alert(" 余额不足，支付失败！");
                        window.location.reload();
                    }
                }
            });
        }
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
    <%--<script type="text/javascript">--%>
    <%--function deliveryAddress(){--%>
    <%--var officeId=$("#officeId").val();--%>
    <%--$("#jhprovince").empty();--%>
    <%--$("#jhcity").empty();--%>
    <%--$("#jhregion").empty();--%>
    <%--$("#jhaddress").empty();--%>
    <%--$.ajax({--%>
    <%--type:"post",--%>
    <%--url:"${ctx}/sys/office/sysOfficeAddress/findAddrByOffice?office.id="+officeId,--%>
    <%--success:function(data){--%>
    <%--console.log(data+"-----777");--%>
    <%--if(data==''){--%>
    <%--$("#jhadd1").css("display","none");--%>
    <%--$("#jhadd2").css("display","block");--%>
    <%--$("#jhadd3").css("display","none");--%>
    <%--}else{--%>
    <%--$("#jhadd1").css("display","block");--%>
    <%--$("#jhadd2").css("display","none");--%>
    <%--$("#jhadd3").css("display","block");--%>
    <%--var option2=$("<option>").text(data.bizLocation.province.name).val(data.bizLocation.province.id);--%>
    <%--$("#jhprovince").append(option2);--%>
    <%--var option3=$("<option/>").text(data.bizLocation.city.name).val(data.bizLocation.city.id);--%>
    <%--$("#jhcity").append(option3);--%>
    <%--var option4=$("<option/>").text(data.bizLocation.region.name).val(data.bizLocation.region.id);--%>
    <%--$("#jhregion").append(option4);--%>
    <%--$("#jhaddress").val(data.bizLocation.address);--%>
    <%--&lt;%&ndash;}&ndash;%&gt;--%>
    <%--//当省份的数据加载完毕之后--%>
    <%--$("#jhprovince").change();--%>
    <%--$("#jhcity").change();--%>
    <%--$("#jhregion").change();--%>
    <%--$("#jhaddress").change();--%>
    <%--}--%>
    <%--}--%>
    <%--});--%>
    <%--}--%>


    <%--</script>--%>
    <script type="text/javascript">
        function checkPending(obj) {
            var localSendIds= "";
                $("input[name='localSendIds']:checked").each(function () {
                    localSendIds+=$(this).val()+",";
                });

            localSendIds= localSendIds.substring(0,localSendIds.length-1);

            if(obj==${OrderHeaderBizStatusEnum.SUPPLYING.state}){ <%--15同意发货--%>
                $("#id").val();
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/order/bizOrderHeader/Commissioner",
                    data:"id="+$("#id").val()+"&flag=${bizOrderHeader.flag}&objJsp=${OrderHeaderBizStatusEnum.SUPPLYING.state}&bizLocation.address="+$("#jhaddress").val()+"&localSendIds="+localSendIds,
                    <%--"&bizLocation.receiver="+$("#bizLocation.receiver").val()+"&bizLocation.phone="+$("#bizLocation.phone").val(),--%>
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
                    data:"id="+$("#id").val()+"&flag=${bizOrderHeader.flag}&objJsp=${OrderHeaderBizStatusEnum.UNAPPROVE.state}&bizLocation.address="+$("#jhaddress").val()+"&localSendIds="+localSendIds,
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
</head>
<body>
<ul class="nav nav-tabs">
    <c:if test="${bizOrderHeader.flag=='check_pending'}">
        <li>
            <a href="${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">订单信息列表</a>
        </li>
    </c:if>
    <c:if test="${empty bizOrderHeader.flag}">
        <li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
    </c:if>
    <li class="active">
        <c:if test="${entity.orderNoEditable eq 'editable'}">
            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&orderNoEditable=${entity.orderNoEditable}">订单信息支付</a>
        </c:if>
        <c:if test="${entity.orderDetails eq 'details'}">
            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&orderDetails=${entity.orderDetails}">订单信息详情</a>
        </c:if>
        <c:if test="${bizOrderHeader.flag eq 'check_pending'}">
            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">订单信息审核</a>
        </c:if>
        <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}">订单信息<shiro:hasPermission
                    name="biz:order:bizOrderHeader:edit">${not empty bizOrderHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
                    name="biz:order:bizOrderHeader:edit">查看</shiro:lacksPermission></a>
        </c:if>
    </li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <input type="hidden" name="oneOrder" value="${entity.oneOrder}">
    <form:hidden path="platformInfo.id" value="1"/>
    <sys:message content="${message}"/>

    <c:if test="${not empty entity.orderDetails}">
        <div class="control-group">
            <label class="control-label">订单编号：</label>
            <div class="controls">
                <form:input path="orderNum" disabled="true" placeholder="由系统自动生成" htmlEscape="false" maxlength="30"
                            class="input-xlarge"/>
            </div>
        </div>
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
        <label class="control-label">采购商名称：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <sys:treeselect id="office" name="customer.id" value="${entity2.customer.id}" labelName="customer.name"
                                labelValue="${entity2.customer.name}"
                                notAllowSelectParent="true"
                                title="采购商" url="/sys/office/queryTreeList?type=6" cssClass="input-xlarge required"
                                allowClear="${office.currentUser.admin}" onchange="clickBut();" dataMsgRequired="必填信息"/>
                <span class="help-inline"><font color="red">*</font></span>
                <c:if test="${entity.orderDetails eq 'details'}">
                    <c:if test="${orderCenter.centers !=null }">
                        该采购商的采购中心： <font color="#04B404">${orderCenter.centers.name}</font>，
                        客户专员：<font color="#04B404">${orderCenter.consultants.name}</font>
                    </c:if>
                </c:if>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <sys:treeselect id="office" name="customer.id" value="${entity2.customer.id}" labelName="customer.name"
                                labelValue="${entity2.customer.name}"
                                notAllowSelectParent="true"
                                title="采购商" url="/sys/office/queryTreeList?type=6" cssClass="input-xlarge required"
                                allowClear="${office.currentUser.admin}" onchange="clickBut();" dataMsgRequired="必填信息"/>
            </c:if>

        </div>
    </div>
    <div class="control-group">
        <label class="control-label">商品详情总价：</label>
        <div class="controls">
            <form:input path="totalDetail" htmlEscape="false" placeholder="0.0" readOnly="true" class="input-xlarge"/>
            <input name="totalDetail" value="${entity.totalDetail}" htmlEscape="false" type="hidden"/>
            <span class="help-inline">自动计算</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">订单总费用：</label>
        <div class="controls">
            <form:input path="totalExp" htmlEscape="false" placeholder="无费用可填 0" class="input-xlarge required"/>
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
                <form:input path="freight" htmlEscape="false" readOnly="true" class="input-xlarge required"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:input path="freight" htmlEscape="false" class="input-xlarge required"/>
            </c:if>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <c:if test="${not empty entity.orderDetails}">
        <c:if test="${fns:getUser().isAdmin()==false}">
            <div class="control-group">
                <label class="control-label">发票状态：</label>
                <div class="controls">
                    <form:select path="invStatus" class="input-medium">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label"
                                      itemValue="value"
                                      htmlEscape="false"/></form:select>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">业务状态：</label>
                <div class="controls">
                    <form:select path="bizStatus" class="input-medium">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/></form:select>
                </div>
            </div>
        </c:if>
    </c:if>
    <c:if test="${fns:getUser().isAdmin()}">
        <div class="control-group">
            <label class="control-label">发票状态：</label>
            <div class="controls">
                <form:select path="invStatus" class="input-medium required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>

                <span class="help-inline"><font color="red">*</font>默认选择</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">业务状态：</label>
            <div class="controls">
                <form:select path="bizStatus" class="input-medium required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/></form:select>
                <span class="help-inline"><font color="red">*</font>默认选择</span>
            </div>
        </div>
    </c:if>
    <div class="control-group">
        <label class="control-label">收货人：</label>
        <div class="controls">
            <form:input path="bizLocation.receiver" placeholder="请输入收货人名称" htmlEscape="false"
                        class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">联系电话：</label>
        <div class="controls">
            <form:input path="bizLocation.phone" placeholder="请输入联系电话" htmlEscape="false"
                        class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
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
                <%--<a id="addAddressHref" href="${ctx}/sys/office/sysOfficeAddress/form?ohId=${bizOrderHeader.id}&office.id=${customer.id}&flag=order">--%>
            <input id="addAddressHref" type="button" value="新增地址" htmlEscape="false" class="input-xlarge required"/>
                <%--</a>--%>
            <label class="error" id="addError" style="display:none;">必填信息</label>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
        <div class="control-group" id="add3">
            <label class="control-label">详细地址；</label>
            <div class="controls">
                <input type="text" id="address" name="bizLocation.address" htmlEscape="false" readOnly="true"
                       class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
    </c:if>
    <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
        <div class="control-group" id="add3">
            <label class="control-label">详细地址；</label>
            <div class="controls">
                <input type="text" id="address" name="bizLocation.address" htmlEscape="false"
                       class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
    </c:if>
    <c:if test="${entity.orderNoEditable eq 'editable' && empty bizOrderHeader.flag && empty entity.orderDetails}">
        <div class="form-actions">
            <shiro:hasPermission name="biz:order:bizOrderHeader:edit">
                <input type="text" id="payMentOne" placeholder="请输入支付金额">
                <input class="btn btn-primary" id="btnOrderButton" onclick="btnOrder();" type="button" value="支付"/>
                待支付费用为:<font color="red"><fmt:formatNumber type="number" value="${entity.tobePaid}" pattern="0.00"/></font>，
            </shiro:hasPermission>
            <span class="help-inline">已经支付：<font color="red">${entity.receiveTotal}</font></span>
        </div>
    </c:if>
    <c:if test="${not empty entity.orderDetails}">
        <div class="control-group">
            <label class="control-label">创建人：</label>
            <div class="controls">
                <form:input path="totalDetail" value="${bizOrderHeader.createBy.name}" readOnly="true" class="input-xlarge"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">创建时间：</label>
            <div class="controls">
                <fmt:formatDate value="${bizOrderHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">更新人：</label>
            <div class="controls">
                <form:input path="totalDetail" value="${bizOrderHeader.updateBy.name}" readOnly="true" class="input-xlarge"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">更新时间：</label>
            <div class="controls">
                <fmt:formatDate value="${bizOrderHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </div>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${bizOrderHeader.flag=='check_pending'}">
            <div class="control-group" id="jhadd1">
                <label class="control-label">交货地址；</label>
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
                    <select id="jhregion" class="input-medium" name="bizLocation.region.id" disabled="disabled"
                            style="width:150px;text-align: center;">
                        <c:if test="${bizOrderHeader.id ==null}">
                            <option value="-1">—— 区 ——</option>
                        </c:if>
                    </select>
                    <span class="help-inline"><font color="red">*</font> </span>
                </div>
            </div>
            <div class="control-group" id="jhadd2" style="display:none">
                <label class="control-label">收货地址；</label>
                <div class="controls">
                        <%--<a id="addAddressHref" href="${ctx}/sys/office/sysOfficeAddress/form?ohId=${bizOrderHeader.id}&office.id=${customer.id}&flag=order">--%>
                    <input id="addJhAddressHref" type="button" value="新增地址" htmlEscape="false"
                           class="input-xlarge required"/>
                        <%--</a>--%>
                    <label class="error" id="addError" style="display:none;">必填信息</label>
                    <span class="help-inline"><font color="red">*</font></span>
                </div>
            </div>
            <div class="control-group" id="jhadd3">
                <label class="control-label">详细地址；</label>
                <div class="controls">
                    <input type="text" id="jhaddress" name="bizLocation.address" htmlEscape="false"
                           class="input-xlarge required"/>
                    <span class="help-inline"><font color="red">*</font></span>
                </div>
            </div>

        </c:when>
        <c:otherwise>
            <div class="form-actions">
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                    <shiro:hasPermission name="biz:order:bizOrderHeader:edit"><input id="btnSubmit"
                                                                                     class="btn btn-primary"
                                                                                     type="submit"
                                                                                     value="保存"/>&nbsp;</shiro:hasPermission>
                </c:if>
                <c:if test="${not empty entity.orderDetails}">
                    待支付费用为:<font color="red"><fmt:formatNumber type="number" value="${entity.tobePaid}" pattern="0.00"/></font>，
                    <span class="help-inline">已经支付：<font color="red">${entity.receiveTotal}</font></span>
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
        <th>商品单价</th>
        <th>采购数量</th>
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
        <tr>
            <td>
                    ${bizOrderDetail.lineNo}
            </td>
            <td>
                    ${bizOrderDetail.shelfInfo.opShelfInfo.name}
            </td>
            <td>
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}">
                    </c:if>
                        ${bizOrderDetail.skuName}</a>
            </td>
            <td>
                    ${bizOrderDetail.partNo}
            </td>
            <td>
                    ${bizOrderDetail.unitPrice}
            </td>
            <td>
                    ${bizOrderDetail.ordQty}
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
            <shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                    <td>
                        <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}">修改</a>
                        <a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}&sign=1&orderHeader.oneOrder=${entity.oneOrder}"
                           onclick="return confirmx('确认要删除该sku商品吗？', this.href)">删除</a>
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
                    <input type="button"
                           onclick="javascript:window.location.href='${ctx}/biz/order/bizOrderDetail/form?orderHeader.id=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}';"
                           class="btn btn-primary"
                           value="订单商品信息添加"/></shiro:hasPermission>
            </c:if>
        </c:if>
    </c:if>
</div>
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


</body>
</html>
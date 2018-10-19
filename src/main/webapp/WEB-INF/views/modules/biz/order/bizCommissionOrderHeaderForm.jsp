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
            if (!${fns:getUser().isAdmin()}) {
                $("#bizStatus").attr("disabled","true");
                $("#invStatus").attr("disabled","true");
            }
            if (bizStatus >= ${OrderHeaderBizStatusEnum.SUPPLYING.state}) {
                $("#totalExp").attr("disabled","disabled");
            }

            if ($("#vendId").val() != "") {
                $("#vendor").removeAttr("style");
                deleteStyle();
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
                var freight = $("#freight").val();
                totalExp = Number(totalExp)
                if(totalExp < 0) {
                    var totalExpTemp = Math.abs(totalExp)
                    if (totalExpTemp >= Number(freight)) {
                        alert("调整金额要小于运费");
                        return;
                    }
                    if (totalExpTemp >= Number(totalDetail) * 1.5) {
                        alert("调整金额要小于商品总价总价的1.5倍");
                        return;
                    }
                }

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
        function pay() {
            var id = $("#poHeaderId").val();
            var paymentOrderId = $("#paymentOrderId").val();
            var payTotal = $("#truePayTotal").val();

            var mainImg = $("#payImgDiv").find("[customInput = 'payImgImg']");
            var img = "";
            if(mainImg.length >= 2) {
                for (var i = 1; i < mainImg.length; i ++) {
                    img += $(mainImg[i]).attr("src") + ",";
                }
            }

            if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
                alert("错误提示:请输入支付金额");
                return false;
            }
            if ($String.isNullOrBlank(img)) {
                alert("错误提示:请上传支付凭证");
                return false;
            }

            var paymentRemark = $("#paymentRemark").val();

            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/payOrder',
                contentType: 'application/json',
                data: {"poHeaderId": id, "paymentOrderId": paymentOrderId, "payTotal": payTotal, "img": img, "paymentRemark":paymentRemark},
                type: 'get',
                success: function (result) {
                    alert(result);
                    if (result == '操作成功!') {
                        window.location.href = "${ctx}/biz/po/bizPoHeader/listV2";
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }
    </script>

    <script type="text/javascript">
        function saveMon(type) {
            if (type == 'createPay') {
                var payTotal = $("#payTotal").val();
                var payDeadline = $("#payDeadline").val();
                var id = $("#poHeaderId").val();
                if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
                    alert("请输入申请金额!");
                    return false;
                }
                if ($String.isNullOrBlank(payDeadline)) {
                    alert("请选择本次申请付款时间!");
                    return false;
                }
            }
            var payTotal = $("#payTotal").val();
            var lastPayDate = $('#lastPayDate').val();
            var payDeadline = $('#payDeadline').val();
            var id = '${entity.id}'
            if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
                alert("请输入申请金额!");
                return false;
            }
            if ($String.isNullOrBlank(lastPayDate)) {
                alert("请选择最后付款时间!");
                return false;
            }
            if ($String.isNullOrBlank(payDeadline)) {
                alert("请选择本次申请付款时间!");
                return false;
            }


            var remark = $("#paymentApplyRemark").val();

            window.location.href="${ctx}/biz/order/bizCommissionOrder/saveCommission?totalCommission=" + payTotal + "&deadline=" + lastPayDate
                + "&payTime=" + payDeadline + "&remark=" + remark + "&orderId=" + id;

            <%--$("#inputForm").attr("action", "${ctx}/biz/po/bizPoHeaderReq/savePoHeader?type=" + type + "&id=" + id + "&fromPage=orderHeader");--%>
            <%--$("#inputForm").submit();--%>
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
    <%--<script type="text/javascript">--%>
        <%--$(document).ready(function(){--%>
            <%--$("#flip").click(function(){--%>
                <%--$("#remark").slideToggle("slow");--%>
            <%--});--%>
        <%--});--%>
    <%--</script>--%>
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
        function submitPic(id, multiple) {
            var f = $("#" + id).val();
            if (f == null || f == "") {
                alert("错误提示:上传文件不能为空,请重新选择文件");
                return false;
            } else {
                var extname = f.substring(f.lastIndexOf(".") + 1, f.length);
                extname = extname.toLowerCase();//处理了大小写
                if (extname != "jpeg" && extname != "jpg" && extname != "gif" && extname != "png") {
                    $("#picTip").html("<span style='color:Red'>错误提示:格式不正确,支持的图片格式为：JPEG、GIF、PNG！</span>");
                    return false;
                }
            }
            var file = document.getElementById(id).files;
            var size = file[0].size;
            if (size > 2097152) {
                alert("错误提示:所选择的图片太大，图片大小最多支持2M!");
                return false;
            }
            if("payImg" == id) {
                ajaxFileUploadPicForPoHeader(id, multiple)
            } else if ("prodMainImg" == id) {
                ajaxFileUploadPicForRefund(id, multiple);
            }
        }

        function ajaxFileUploadPicForPoHeader(id, multiple) {
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

        var a = 0;
        function ajaxFileUploadPicForRefund(id, multiple) {
            $.ajaxFileUpload({
                url: '${ctx}/biz/order/bizOrderHeader/saveColorImg', //用于文件上传的服务器端请求地址
                secureuri: false, //一般设置为false
                fileElementId: id, //文件上传空间的id属性  <input type="file" id="file" name="file" />
                type: 'POST',
                dataType: 'text', //返回值类型 一般设置为json
                success: function (data, status) {
                    //服务器成功响应处理函数
                    var msg = data.substring(data.indexOf("{"), data.indexOf("}") + 1);
                    var msgJSON = JSON.parse(msg);
                    var imgList = msgJSON.imgList;
                    var imgDiv = $("#" + id + "Div");
                    var imgDivHtml = "<td><img src=\"$Src\" customInput=\"" + id + "Img\" style='width: 100px' onclick=\"removeThis(this);\"></td>";
                    var imgPhotosSorts = "<td id=''><input name='imgPhotosSorts' style='width: 70px' type='number'/></td>";
                    if (imgList && imgList.length > 0 && multiple) {
                        for (var i = 0; i < imgList.length; i++) {
                            // imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                            if (id == "prodMainImg") {
                                $("#imgPhotosSorts").append("<td><input id='" + "main" + i + "' name='imgPhotosSorts' value='" + a + "' style='width: 70px' type='number'/></td>");
                                // $("#prodMainImgImg").append(imgDivHtml.replace("$Src", imgList[i]));
                                $("#prodMainImgImg").append("<td><img src=\"" + imgList[i] + "\" customInput=\"" + id + "Img\" style='width: 100px' onclick=\"removeThis(this," + "$('#main" + i + "'));\"></td>");
                                a += 1;
                            }
                        }
                    } else if (imgList && imgList.length > 0 && !multiple) {
                        imgDiv.empty();
                        for (var i = 0; i < imgList.length; i++) {
                            imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                        }
                    } else {
                        var img = $("#" + id + "Img");
                        img.attr("src", msgJSON.fullName);
                    }
                },
                error: function (data, status, e) {
                    //服务器响应失败处理函数
                    console.info(data);
                    console.info(status);
                    console.info(e);
                    alert("上传失败");
                }
            });
            return false;
        }

        function deletePic(id) {
            var f = $("#" + id);
            f.attr("src", "");
        }

        function removeThis(obj, item) {
            $(obj).remove();
            $(item).remove();
        }

        function deleteParentParentEle(that) {
            $(that).parent().parent().remove();
        }

    </script>
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

    </script>

    <script type="text/javascript">
        //代采订单：审核通过
        function checkPass(obj) {
            if('${createPo == 'yes'}'){
                var lastPayDateVal = $("#lastPayDate").val();
                if (lastPayDateVal == ""){
                    alert("请输入最后付款时间！");
                    return false;
                }
            }
            var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
            var submit = function (v, h, f) {
                top.$.jBox.confirm("确认审核通过吗？","系统提示",function(v1,h1,f1){
                    if(v1=="ok"){
                        if ($String.isNullOrBlank(f.description)) {
                            jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                            return false;
                        }
                        if (obj == "DO") {
                            audit(1, f.description);
                        }
                        if (obj == "JO") {
                            auditJo(1, f.description);
                        }
                        if (obj == "PO") {
                            poAudit(1,f.description);
                        }
                        return true;
                    }
                },{buttonsFocus:1});
            };
            jBox(html, {
                title: "请输入通过理由:", submit: submit, loaded: function (h) {
                }
            });
        }

        //代采订单：审核驳回
        function checkReject(obj) {
            var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
            var submit = function (v, h, f) {
                top.$.jBox.confirm("确认驳回该流程吗？","系统提示",function(v1,h1,f1){
                    if(v1=="ok"){
                        if ($String.isNullOrBlank(f.description)) {
                            jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                            return false;
                        }
                        if (obj == "DO" || obj == 'SO') {
                            audit(2, f.description);
                        }
                        if (obj == "JO") {
                            auditJo(2, f.description);
                        }
                        if (obj == "PO") {
                            poAudit(2,f.description);
                        }
                        return true;
                    }
                },{buttonsFocus:1});
            };

            jBox(html, {
                title: "请输入驳回理由:", submit: submit, loaded: function (h) {
                }
            });
        }

        function audit(auditType, description) {
            //判断排产数据合法性
            var createPo = $("#createPo").val();
            if(auditType == 2) {
                createPo = "no"
            }
            if(createPo == "yes") {
                var schedulingType = $('#schedulingPlanRadio input[name="bizPoHeader.schedulingType"]:checked ').val();
                if (schedulingType == 0) {
                    var checkResult = saveCompleteCheck();
                    if(checkResult == false) {
                        return;
                    }
                }
                if (schedulingType == 1) {
                    var checkResult = batchSaveCheck();
                    if(checkResult == false) {
                        return;
                    }
                }
            }

            var id = $("#id").val();
            var currentType = $("#currentJoType").val();
            var lastPayDateVal = $("#lastPayDate").val();

            $.ajax({
                url: '${ctx}/biz/order/bizOrderHeader/audit',
                contentType: 'application/json',
                data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description, "createPo": createPo, "lastPayDateVal":lastPayDateVal},
                type: 'get',
                async: false,
                success: function (result) {
                    result = JSON.parse(result);
                    if(result.ret == true || result.ret == 'true') {
                        alert('操作成功!');

                        //订单排产
                        var resultData = result.data;
                        var resultDataArr = resultData.split(",");
                        console.log(resultDataArr)
                        console.log(resultDataArr[0])
                        console.log(resultDataArr[1])
                        if(resultDataArr[0] == "采购单生成") {
                            var poId = resultDataArr[1];
                            var schedulingType = $('#schedulingPlanRadio input[name="bizPoHeader.schedulingType"]:checked ').val();
                            console.log(poId)
                            console.log(schedulingType)
                            if (schedulingType == 0) {
                                saveComplete("0", poId);
                            }
                            if (schedulingType == 1) {
                                batchSave("1", poId);
                            }
                        }

                        window.location.href = "${ctx}/biz/order/bizOrderHeader";
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        //采购单审核
        function poAudit(auditType, description) {
            var id = $("#poHeaderId").val();
            var currentType = $("#poCurrentType").val();
            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/audit',
                contentType: 'application/json',
                data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description, "fromPage": "orderHeader"},
                type: 'get',
                success: function (result) {
                    result = JSON.parse(result);
                    if(result.ret == true || result.ret == 'true') {
                        alert('操作成功!');
                        window.location.href = "${ctx}/biz/po/bizPoHeader/listV2";
                    }else {
                        alert(result.errmsg);
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function auditJo(auditType, description) {
            var id = $("#id").val();
            var currentType = $("#currentJoType").val();
            var suplys = $("#suplys").val();
            var orderType = 1;
            var createPo = $("#createPo").val();
            var lastPayDateVal = $("#lastPayDate").val();


            if(createPo == "yes") {
                var schedulingType = $('#schedulingPlanRadio input[name="bizPoHeader.schedulingType"]:checked ').val();
                if (schedulingType == 0) {
                    var checkResult = saveCompleteCheck();
                    if(checkResult == false) {
                        return;
                    }
                }
                if (schedulingType == 1) {
                    var checkResult = batchSaveCheck();
                    if(checkResult == false) {
                        return;
                    }
                }
            }

            if (suplys == 0 || suplys == 721) {
                orderType = 0;
            }
            $.ajax({
                url: '${ctx}/biz/order/bizOrderHeader/auditSo',
                contentType: 'application/json',
                data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description, "orderType": orderType, "createPo": createPo, "lastPayDateVal":lastPayDateVal},
                type: 'get',
                success: function (result) {
                    result = JSON.parse(result);
                    if(result.ret == true || result.ret == 'true') {
                        alert('操作成功!');
                        // window.history.go(-1);
                        var resultData = result.data;
                        var resultDataArr = resultData.split(",");
                        console.log(resultDataArr)
                        console.log(resultDataArr[0])
                        console.log(resultDataArr[1])
                        if(resultDataArr[0] == "采购单生成") {
                            var poId = resultDataArr[1];
                            var schedulingType = $('#schedulingPlanRadio input[name="bizPoHeader.schedulingType"]:checked ').val();
                            console.log(poId)
                            console.log(schedulingType)
                            if (schedulingType == 0) {
                                saveComplete("0", poId);
                            }
                            if (schedulingType == 1) {
                                batchSave("1", poId);
                            }
                        }
                        window.location.href = "${ctx}/biz/order/bizOrderHeader/list"
                    }else {
                        alert(result.errmsg);
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function deleteStyle() {
            //$("#remark").removeAttr("style");
            $("#cardNumber").removeAttr("style");
            $("#payee").removeAttr("style");
            $("#bankName").removeAttr("style");
            $("#compact").removeAttr("style");
            $("#identityCard").removeAttr("style");
            var vendId = $("#vendId").val();
            $.ajax({
                type:"post",
                url:"${ctx}/biz/order/bizOrderHeader/selectVendInfo?vendorId="+vendId,
                success:function (data) {
                    if (data == null) {
                        return false;
                    }
                    $("#cardNumberInput").val(data.cardNumber);
                    $("#remarkInput").val(data.remark);
                    $("#payeeInput").val(data.payee);
                    $("#bankNameInput").val(data.bankName);
                    if (data.compactImgList != undefined) {
                        $.each(data.compactImgList,function (index, compact) {
                            $("#compactImgs").append("<a href=\"" + compact.imgServer + compact.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + compact.imgServer + compact.imgPath + "\"></a>");
                        });
                    }
                    if (data.identityCardImgList != undefined) {
                        $.each(data.identityCardImgList,function (index, identity) {
                            $("#identityCards").append("<a href=\"" + identity.imgServer + identity.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + identity.imgServer + identity.imgPath + "\"></a>");
                        });
                    }
                    $("#remark").val(data.remarks);
                }
            });
        }

        function startAudit() {
            var prew = false;
            var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
            var submit = function (v, h, f) {
                if ($String.isNullOrBlank(f.description)) {
                    jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                    return false;
                }
                top.$.jBox.confirm("确认开始审核流程吗？", "系统提示", function (v1, h1, f1) {
                    if (v1 == "ok") {
                        var id = "${entity.bizPoHeader.id}";
                        $.ajax({
                            url: '${ctx}/biz/po/bizPoHeader/startAudit',
                            contentType: 'application/json',
                            data: {
                                "id": id,
                                "prew": prew,
                                "desc": f.description,
                                "action" : "startAuditAfterReject"
                            },
                            type: 'get',
                            success: function (result) {
                                result = JSON.parse(result);
                                if(result.ret == true || result.ret == 'true') {
                                    alert('操作成功!');
                                    window.location.href = "${ctx}/biz/po/bizPoHeader/listV2";
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
            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&orderDetails=${entity.orderDetails}&source=${source}">订单信息详情</a>
        </c:if>
        <c:if test="${bizOrderHeader.flag eq 'check_pending'}">
            <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}&source=${source}">订单信息审核</a>
        </c:if>
        <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
            <c:if test="${empty bizOrderHeader.clientModify}">
                <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&source=${source}">订单信息<shiro:hasPermission
                        name="biz:order:bizOrderHeader:edit">${not empty bizOrderHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
                        name="biz:order:bizOrderHeader:edit">查看</shiro:lacksPermission></a>
            </c:if>
            <c:if test="${bizOrderHeader.clientModify eq 'client_modify'}">
                <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&flag=check_pending&consultantId=${bizOrderHeader.consultantId}&source=${source}">订单信息<shiro:hasPermission
                        name="biz:order:bizOrderHeader:edit">${not empty bizOrderHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
                        name="biz:order:bizOrderHeader:edit">查看</shiro:lacksPermission></a>
            </c:if>
        </c:if>
    </li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/save?statuPath=${statuPath}" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <input type="hidden" name="oneOrder" value="${entity.oneOrder}">
    <input type="hidden" id="bizOrderMark" name="orderMark" value="${bizOrderHeader.orderMark}">
    <input type="hidden" name="clientModify" value="${bizOrderHeader.clientModify}" />
    <input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}" />
    <input type="hidden" name="source" value="${source}"/>
    <input type="hidden" id="suplys" value="${entity.suplys}"/>
    <input id="poHeaderId" type="hidden" value="${entity.bizPoHeader.id}"/>
    <input type="hidden" value="${entity.bizPoPaymentOrder.id}" id="paymentOrderId"/>
    <input type="hidden" name="receiveTotal" value="${bizOrderHeader.receiveTotal}" />
    <%--<input id="vendId" type="hidden" value="${entity.sellers.bizVendInfo.office.id}"/>--%>
    <input id="vendId" type="hidden" value="${entity.sellersId}"/>
    <input id="createPo" type="hidden" value="${createPo}"/>
    <input id="totalPayTotal" type="hidden" value="${totalPayTotal}"/>
    <%--<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}" />--%>
    <form:input path="photos" id="photos" cssStyle="display: none"/>
    <form:hidden path="platformInfo.id" value="6"/>
    <sys:message content="${message}"/>
    <c:if test="${entity.str != 'pay'}">
        <div class="control-group">
            <label class="control-label">订单总价：</label>
            <div class="controls">
                <form:input path="commissionTotalDetail" readonly="readonly" placeholder="由系统自动生成" htmlEscape="false" maxlength="30" class="input-xlarge"/>
            </div>
        </div>


    <div class="control-group">
        <label class="control-label">业务状态：</label>
        <div class="controls">
            <form:select path="bizStatus" class="input-xlarge" disabled="true">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/></form:select>
        </div>
    </div>
    </c:if>

    <div id="vendor" class="control-group" >
        <label class="control-label">供应商：</label>
        <div class="controls">
            <input type="text" id="ecpectPay" value="${entity.vendorName}" disabled="true" class="input-xlarge">
            <%--<sys:treeselect id="officeVendor" name="bizVendInfo.office.id" value="${entity.vendorId}" labelName="bizVendInfo.office.name"--%>
                            <%--labelValue="${entity.vendorName}" notAllowSelectParent="true"--%>
                            <%--title="供应商" url="/sys/office/queryTreeList?type=7" cssClass="input-medium required"--%>
                            <%--allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息" onchange="deleteStyle()"/>--%>
            <%--<span class="help-inline"><font color="red">*</font> </span>--%>
            <input id="remarkInput" type="hidden" value=""/>
        </div>
    </div>
    <div id="cardNumber" class="control-group" style="display: none">
        <label class="control-label">供应商卡号：</label>
        <div class="controls">
            <input id="cardNumberInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>
    <div id="payee" class="control-group" style="display: none">
        <label class="control-label">供应商收款人：</label>
        <div class="controls">
            <input id="payeeInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>
    <div id="bankName" class="control-group" style="display: none">
        <label class="control-label">供应商开户行：</label>
        <div class="controls">
            <input id="bankNameInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
                   class="input-xlarge "/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">申请金额：</label>
        <div class="controls">
            <input id="payTotal" name="planPay" type="text" readonly="readonly"
                   <c:if test="${entity.str == 'audit' || entity.str == 'pay'}">readonly</c:if>
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
                   value="<fmt:formatDate value="${entity.bizPoHeader.lastPayDate}"  pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
                   placeholder="必填！"/>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">本次申请付款时间：</label>
        <div class="controls">
            <input name="payDeadline" id="payDeadline" type="text" readonly="readonly" maxlength="20"
                   class="input-medium Wdate required"
                   value="<fmt:formatDate value="${entity.bizPoPaymentOrder.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
                    onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
                   placeholder="必填！"/>
        </div>
    </div>

    <c:if test="${entity.str == 'pay'}">
        <div class="control-group">
            <label class="control-label" style="color: red">实际付款金额：</label>
            <div class="controls">
                <input id="truePayTotal" name="payTotal" type="text" readonly="true"
                       value="${entity.totalCommission}"
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
                <img src="${entity.bizCommission.imgUrl}" customInput="payImgImg" style='width: 100px' onclick="$(this).remove();">
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">支付备注：</label>
            <div class="controls">
					<textarea id="paymentRemark" maxlength="200"
                              class="input-xlarge">${entity.bizPoHeader.bizPoPaymentOrder.remark}</textarea>
            </div>
        </div>
    </c:if>

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
                        <c:if test="${entity.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state && currentAuditStatus.type != 777 && currentAuditStatus.type != 666}">
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

                <c:if test="${entity.str == 'startAudit'}">
                    <input type="button" onclick="startAudit()" class="btn btn-primary" value="开启审核"/>
                </c:if>

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
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <%--<th>详情行号</th>--%>
        <th>订单号</th>
        <th>商品名称</th>
        <th>商品编号</th>
        <th>商品货号</th>
        <th>采购数量</th>
        <th>总 额</th>
        <th>已发货数量</th>
        <c:if test="${bizOrderHeader.bizStatus>=15 && bizOrderHeader.bizStatus!=45}">
            <th>发货方</th>
        </c:if>
        <th>商品零售价</th>
        <th>佣金</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${bizOrderHeader.orderDetailList}" var="bizOrderDetail">
        <tr>
            <%--<td>--%>
                    <%--${bizOrderDetail.lineNo}--%>
            <%--</td>--%>
            <td>
                    ${bizOrderHeader.orderNum}
            </td>
            <td>
                <c:if test="${entity.orderDetails eq 'details' || entity.orderNoEditable eq 'editable' || bizOrderHeader.flag eq 'check_pending'}">
                    ${bizOrderDetail.skuName}
                </c:if>
                <c:if test="${empty entity.orderNoEditable || empty entity.orderDetails || empty bizOrderHeader.flag}">
                    <c:if test="${empty entity.orderNoEditable && empty entity.orderDetails && empty bizOrderHeader.flag && empty bizOrderHeader.clientModify}">
                        <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}&orderHeader.clientModify=client_modify&orderHeader.consultantId=${bizOrderHeader.consultantId}&orderType=${orderType}">
                                ${bizOrderDetail.skuName}</a>
                    </c:if>
                    <c:if test="${not empty bizOrderHeader.clientModify && bizOrderHeader.clientModify eq 'client_modify'}">
                        <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}&orderHeader.clientModify=client_modify&orderHeader.consultantId=${bizOrderHeader.consultantId}&orderType=${orderType}">
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
            <c:if test="${bizOrderHeader.bizStatus>=15 && bizOrderHeader.bizStatus!=45}">
                <td>
                        ${bizOrderDetail.suplyis.name}
                </td>
            </c:if>
            <td>
                    ${bizOrderDetail.salePrice}
            </td>
            <td>
                    ${bizOrderDetail.detailCommission}
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>

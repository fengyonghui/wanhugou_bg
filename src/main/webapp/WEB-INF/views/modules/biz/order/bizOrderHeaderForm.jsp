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
            $("#inputForm").validate({
                submitHandler: function(form){
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
                                alert("优惠后订单金额不能低于出厂价，请修改调整金额");
                            } else if (data == "orderLowest") {
                                alert("优惠后订单金额不能低于出厂价的95%，请修改调整金额");
                            } else if (data == "orderLowest8") {
                                alert("优惠后订单金额不能低于出厂价的80%，请修改调整金额");
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
                        if (data == "serviceCharge") {
                            alert("最多只能优惠服务费的50%，您优惠的价格已经超标！请修改调整金额");
                        } else if (data == "orderLoss") {
                            alert("优惠后订单金额不能低于出厂价，请修改调整金额");
                        } else if (data == "orderLowest") {
                            alert("优惠后订单金额不能低于出厂价的95%，请修改调整金额");
                        } else if (data == "orderLowest8") {
                            alert("优惠后订单金额不能低于出厂价的80%，请修改调整金额");
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

                var r2 = document.getElementsByName("localOriginType");
                var localOriginType = "";
                for (var i = 0; i < r2.length; i++) {
                    if (r2[i].checked == true) {
                        localOriginType = r2[i].value;
                    }
                }

                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/order/bizOrderHeader/Commissioner",
                    data:"id="+$("#id").val()+"&flag=${bizOrderHeader.flag}&objJsp=${OrderHeaderBizStatusEnum.SUPPLYING.state}&bizLocation.address="+$("#jhaddress").val()+"&bizLocation.appointedTime="+$("#appointedDate").val()+"&localSendIds="+localSendIds+"&boo="+boo
                    +"&bizLocation.province.id="+$("#jhprovince").val()+"&bizLocation.city.id="+$("#jhcity").val()+"&bizLocation.region.id="+$("#jhregion").val()+"&localOriginType="+localOriginType,
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

    <script type="text/javascript">
        function checkInfo(obj, val, hid) {
            if (confirm("您确认同意该订单退款申请吗？")) {

                $.ajax({
                    type: "post",
                    url: "${ctx}/biz/order/bizOrderHeader/saveDrawStatus?statuPath=${statuPath}",
                    data: {checkStatus: obj, id: hid},
                    success: function (data) {
                        if (data) {
                            alert(val + "成功！");
                            window.location.href = "${ctx}/biz/order/bizOrderHeader?statu=${statuPath}";

                        }
                    }
                })

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
            if ($String.isNullOrBlank(payTotal)) {
                alert("错误提示:请输入支付金额");
                return false;
            }
            if ($String.isNullOrBlank(img)) {
                alert("错误提示:请上传支付凭证");
                return false;
            }

            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/payOrder',
                contentType: 'application/json',
                data: {"poHeaderId": id, "paymentOrderId": paymentOrderId, "payTotal": payTotal, "img": img},
                type: 'get',
                success: function (result) {
                    alert(result);
                    if (result == '操作成功!') {
                        window.location.href = "${ctx}/biz/order/bizOrderHeader";
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
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
            //requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.roleEnNameEnum
            // RoleEnNameEnum.SELECTION_OF_SPECIALIST.state
            // (fns:hasRole(roleSet, requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.roleEnNameEnum)) &&
            if('${(fns:hasRole(roleSet, 'SELECTION_OF_SPECIALIST')) && entity.bizStatus == OrderHeaderBizStatusEnum.SUPPLYING.state}'){
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
                        if (obj == "DO" ||  obj == 'SO') {
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
            var id = $("#id").val();
            var currentType = $("#currentType").val();

            $.ajax({
                url: '${ctx}/biz/order/bizOrderHeader/audit',
                contentType: 'application/json',
                data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description},
                type: 'get',
                success: function (result) {
                    if(result == 'ok') {
                        if(auditType==1){
                            //自动生成采购单
                            var id = $("#id").val();
                            //获取当前订单业务状态，如果订单审核完成则自动生成采购单
                            //getCurrentBizStatus(id);
                            //requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.roleEnNameEnum
                            // RoleEnNameEnum.SELECTION_OF_SPECIALIST.state
                            // (fns:hasRole(roleSet, requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.roleEnNameEnum)) &&
                            if(${(fns:hasRole(roleSet, 'SELECTION_OF_SPECIALIST')) && entity.bizStatus == OrderHeaderBizStatusEnum.SUPPLYING.state}){
                                getPoHeaderPara(id);
                            }
                            <%--if ('${OrderHeaderBizStatusEnum.APPROVEPARTONE.state}' == bizStatus) {--%>
                                <%--getPoHeaderPara(id);--%>
                            <%--}--%>
                        }
                        alert("操作成功！");
                        window.location.href = "${ctx}/biz/order/bizOrderHeader";
                    }else {
                        alert("操作失败！");
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
                        window.location.href = "${ctx}/biz/order/bizOrderHeader";
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
            if (suplys == 0 || suplys == 721) {
                orderType = 0;
            }
            $.ajax({
                url: '${ctx}/biz/order/bizOrderHeader/auditSo',
                contentType: 'application/json',
                data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description, "orderType": orderType},
                type: 'get',
                success: function (result) {
                    result = JSON.parse(result);
                    if(result.ret == true || result.ret == 'true') {
                        alert('操作成功!');
                        window.history.go(-1);
                    }else {
                        alert(result.errmsg);
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        //获取当前订单业务状态
        function getCurrentBizStatus(id) {
            var bizStatus = "";
            $.ajax({
                url: '${ctx}/biz/request/bizRequestHeaderForVendor/getCurrentBizStatus',
                contentType: 'application/json',
                data: {"id": id},
                type: 'get',
                dataType: 'json',
                success: function (result) {
                    if ('${OrderHeaderBizStatusEnum.APPROVEPARTONE.state}' == result) {
                        getPoHeaderPara(id);
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
            return bizStatus;
        }

        function getPoHeaderPara(id) {
            $.ajax({
                url: '${ctx}/biz/request/bizRequestOrder/goListAutoSave',
                contentType: 'application/json',
                data: {"orderId": id, "type": "2"},
                type: 'get',
                dataType: 'json',
                success: function (result) {
                    var reqDetailIds = result['unitPrices'];
                    if (reqDetailIds == "") {
                        alert("价钱不能为空！");
                        return;
                    }
                    savePoHeader(result);
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function savePoHeader(result) {
            var orderDetailIds = result['orderDetailIds'];
            var vendorId = result['vendorId'];
            var unitPrices = result['unitPrices'];
            var ordQtys = result['ordQtys'];
            <!-- 最后付款时间 -->
            var lastPayDateVal = $("#lastPayDate").val();
            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/autoSave',
                contentType: 'application/json',
                data: {"reqDetailIds":"", "orderDetailIds": orderDetailIds,"vendorId":vendorId, "unitPrices":unitPrices, "ordQtys":ordQtys, "lastPayDateVal": lastPayDateVal},
                type: 'get',
                success: function (res) {
                    if (res == "ok") {

                    }
                },
                error: function (error) {
                    console.info(error);
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
    <%--<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}" />--%>
    <form:input path="photos" id="photos" cssStyle="display: none"/>
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
                    <fmt:formatNumber type="percent" value="${bizOrderHeader.receiveTotal/(bizOrderHeader.totalDetail+bizOrderHeader.totalExp+bizOrderHeader.freight)}" maxFractionDigits="2" />
                </font> (<fmt:formatNumber type="number" value="${bizOrderHeader.receiveTotal}" pattern="0.00"/>)
            </div>
        </div>
    <c:if test="${source ne 'vendor'}">
        <div class="control-group">
            <label class="control-label">服务费：</label>
            <div class="controls">
                <fmt:formatNumber type="number" value="${(bizOrderHeader.totalDetail+bizOrderHeader.totalExp+bizOrderHeader.freight)-bizOrderHeader.totalBuyPrice}" pattern="0.00"/>
                <%--<input type="text" value="${(bizOrderHeader.totalDetail+bizOrderHeader.totalExp+bizOrderHeader.freight)-bizOrderHeader.totalBuyPrice}" disabled="true" class="input-xlarge">--%>
            </div>
        </div
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

        <c:if test="${entity.str == 'audit' && entity.bizPoHeader.commonProcessList == null}">
            <div class="control-group">
                <label class="control-label">审核状态：</label>
                <div class="controls">
                    <input type="text" disabled="disabled"
                           value="${orderHeaderProcess.name}" htmlEscape="false"
                           maxlength="30" class="input-xlarge "/>
                    <input id="currentType" type="hidden" disabled="disabled"
                           value="${orderHeaderProcess.code}" htmlEscape="false"
                           maxlength="30" class="input-xlarge "/>
                </div>
            </div>
        </c:if>
        <c:if test="${entity.str == 'audit' && entity.bizPoHeader.commonProcessList != null && fn:length(entity.bizPoHeader.commonProcessList) > 0}">
            <div class="control-group">
                <label class="control-label">审核状态：</label>
                <div class="controls">
                    <input type="text" disabled="disabled"
                           value="${purchaseOrderProcess.name}" htmlEscape="false"
                           maxlength="30" class="input-xlarge "/>
                    <input id="poCurrentType" type="hidden" disabled="disabled"
                           value="${purchaseOrderProcess.code}" htmlEscape="false"
                           maxlength="30" class="input-xlarge "/>
                </div>
            </div>
        </c:if>

        <div class="control-group">
            <label class="control-label">业务状态：</label>
            <div class="controls">
                <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                    <form:select path="bizStatus" class="input-xlarge" disabled="true">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/></form:select>
                </c:if>
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                    <form:select path="bizStatus" class="input-xlarge required">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
                                      htmlEscape="false"/></form:select>
                    <span class="help-inline"><font color="red">*</font>默认选择</span>
                </c:if>
            </div>
        </div>
    <c:if test="${source ne 'vendor'}">
    <div class="control-group">
        <label class="control-label">收货人：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <form:input path="bizLocation.receiver" placeholder="收货人名称" htmlEscape="false" disabled="true"
                            class="input-xlarge"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:input path="bizLocation.receiver" placeholder="请输入收货人名称" htmlEscape="false"
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
                <form:input path="bizLocation.phone" placeholder="请输入联系电话" htmlEscape="false"
                            class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </c:if>
        </div>
    </div>

        <c:if test="${entity.str == 'pay'}">
            <div class="control-group">
                <label class="control-label">实际付款金额：</label>
                <div class="controls">
                    <input id="truePayTotal" name="payTotal" type="text"
                           value="${entity.bizPoHeader.bizPoPaymentOrder.payTotal}"
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
                    <img src="${entity.bizPoHeader.bizPoPaymentOrder.img}" customInput="payImgImg" style='width: 100px' onclick="$(this).remove();">
                </div>
            </div>
        </c:if>

    <div class="control-group" id="add1">
        <label class="control-label">收货地址：</label>
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
                <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                    <input type="text" id="address" name="bizLocation.address" htmlEscape="false" readOnly="true"
                           class="input-xlarge required"/>
                </c:if>
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                    <input type="text" id="address" name="bizLocation.address" htmlEscape="false" class="input-xlarge required"/>
                    <span class="help-inline"><font color="red">*</font> </span>
                </c:if>
        </div>
    </div>

    <!-- 状态为审核中，自动生成采购单时，需要填写最后付款时间 -->
        <c:if test="${entity.orderType == BizOrderTypeEnum.PURCHASE_ORDER.state}">
            <shiro:hasPermission name="biz:order:bizOrderHeader:audit">
                <c:if test="${entity.str == 'audit'}">
                    <c:if test="${(fns:hasRole(roleSet, 'SELECTION_OF_SPECIALIST')) && entity.bizStatus == OrderHeaderBizStatusEnum.SUPPLYING.state}">
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
                    </c:if>
                </c:if>
            </shiro:hasPermission>
        </c:if>

    <div class="control-group">
        <label class="control-label">备&nbsp;注：</label>
        <div id="remark" class="controls" style="margin-left: 16px; overflow:auto; float:left;text-align: left; width: 400px;">
            <c:forEach items="${commentList}" var="comment">
                <p class="box">${comment.comments}<br>${comment.createBy.name}&nbsp;&nbsp;<fmt:formatDate value="${comment.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></>
            </c:forEach>
        </div>
        <%--<span id="flip">全部备注</span>&nbsp;&nbsp;--%>
        <input id="addRemark" class="btn" type="button" value="增加备注"onclick="saveRemark()"/>
        <%--<span id="addRemark" onclick="saveRemark()">增加备注</span>--%>
    </div>
    </c:if>

    <c:if test="${photosMap != null && photosMap.size()>0 }">
        <div class="control-group">
            <label class="control-label">退货凭证:
                <p style="opacity: 0.5;color: red;">*首图为列表页图</p>
                <p style="opacity: 0.5;">图片建议比例为1:1</p>
                <p style="opacity: 0.5;">点击图片删除</p>
                <p style="opacity: 0.5;color: red;">数字小的会排在前边，请不要输入重复序号</p>
            </label>
            <div class="controls">
                <input class="btn" type="file" name="productImg" onchange="submitPic('prodMainImg', true)" value="上传图片" multiple="multiple" id="prodMainImg"/>
            </div>
            <div id="prodMainImgDiv">
                <table>
                    <tr id="prodMainImgImg">
                        <c:forEach items="${photosMap}" var="photo" varStatus="status">
                            <td><img src="${photo.key}" customInput="prodMainImgImg" style='width: 100px' onclick="removeThis(this,'#mainImg'+${status.index});"></td>
                        </c:forEach>
                    </tr>
                    <tr id="imgPhotosSorts">
                        <c:forEach items="${photosMap}" var="photo" varStatus="status">
                            <td><input id="mainImg${status.index}" name="imgPhotosSorts" type="number" style="width: 100px" value="${photo.value}"/></td>
                        </c:forEach>
                    </tr>
                </table>
            </div>
        </div>
    </c:if>

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
  <c:if test="${fn:length(auditList) > 0}">
            <div class="control-group">
                <label class="control-label">审核流程：</label>
                <div class="controls help_wrap">
                    <div class="help_step_box fa">
                        <c:forEach items="${auditList}" var="v" varStatus="stat">
                            <c:if test="${v.current == 0}" >
                                <div class="help_step_item">
                                    <div class="help_step_left"></div>
                                    <div class="help_step_num">${stat.index + 1}</div>
                                    处理人:${v.user.name}<br/><br/>
                                    批注:${v.description}<br/><br/>
                                    状态:
                                    <c:if test="${type == 1}">
                                    ${v.jointOperationLocalProcess.name}
                                    </c:if>
                                    <c:if test="${type == 0}">
                                        ${v.jointOperationOriginProcess.name}
                                    </c:if><br/>
                                    <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    <div class="help_step_right"></div>
                                </div>
                            </c:if>
                            <c:if test="${v.current == 1}">
                                <div class="help_step_item help_step_set">
                                    <div class="help_step_left"></div>
                                    <div class="help_step_num">${stat.index + 1}</div>

                                    状态:
                                    <c:if test="${type == 1}">
                                        ${v.jointOperationLocalProcess.name}
                                    </c:if>
                                    <c:if test="${type == 0}">
                                        ${v.jointOperationOriginProcess.name}
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

        <c:if test="${fn:length(doComPList) > 0}">
            <div class="control-group">
                <label class="control-label">审核流程：</label>
                <div class="controls help_wrap">
                    <div class="help_step_box fa">
                        <c:forEach items="${doComPList}" var="v" varStatus="stat">
                            <c:if test="${!stat.last}">
                                <div class="help_step_item">
                                    <div class="help_step_left"></div>
                                    <div class="help_step_num">${stat.index + 1}</div>
                                    处理人:${v.user.name}<br/><br/>
                                    批注:${v.description}<br/><br/>
                                    状态:
                                    <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    <div class="help_step_right"></div>
                                </div>
                            </c:if>
                            <c:if test="${stat.last && entity.bizPoHeader.commonProcessList == null}">
                                <div class="help_step_item help_step_set">
                                    <div class="help_step_left"></div>
                                    <div class="help_step_num">${stat.index + 1}</div>
                                    <c:if test="${entity.payProportion == OrderPayProportionStatusEnum.FIFTH.state}">
                                        当前状态:${v.doOrderHeaderProcessFifth.name}
                                            ${v.user.name}<br/>
                                    </c:if>
                                    <c:if test="${entity.payProportion == OrderPayProportionStatusEnum.ALL.state}">
                                        当前状态:${v.doOrderHeaderProcessAll.name}
                                            ${v.user.name}<br/>
                                    </c:if>
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
    <c:if test="${entity.orderType == DefaultPropEnum.PURSEHANGER.propValue}">
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
                    <c:if test="${source ne 'vendor'}">联系电话:${custUser.mobile}</c:if>
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

        <div class="control-group">
            <div class="controls">
                <div style="float:left;color: red;font-size: medium;margin-right: 50px">
                    注：<br>
                    一、甲方是万户通平台的运营商，乙方是箱包厂商，丙方是采购商。丙方委托甲方进行商品采购，并通过甲方向乙方支付货款。三方在友好协商、平等互利的基础上，就甲方提供商品采购服务事宜形成本订单。<br>
                    二、自丙方下单完成起，至丙方支付完毕本订单所有费用时止。<br>
                    三、乙、丙双方确定商品质量标准。丙方负责收货、验货，乙方负责提供质量达标的商品，如果商品达不到丙方要求，丙方有权要求乙方退换货。甲方不承担任何商品质量责任。<br>
                    四、商品交付丙方前，丙方须支付全部货款。如果丙方不能及时付款，甲方有权利拒绝交付商品。<br>
                    五、本订单商品价格为未含税价，如果丙方需要发票，乙方有义务提供正规发票，税点由丙方承担。<br>
                    六、乙方保证其提供的商品具有完整的所有权，并达到国家相关质量标准要求。因乙方商品问题（包括但不限于质量问题、版权问题、款式不符、数量不符等）给甲方及（或）丙方或其他方造成损失的，须由乙方赔偿全部损失。<br>
                    七、本订单在万户通平台经甲、乙、丙三方线上确认后生效，与纸质盖章订单具有同等的法律效力。在系统出现故障时，甲、乙、丙三方也可采用纸质订单并签字或盖章后生效。
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
            <c:if test="${bizOrderHeader.flag=='check_pending'}">
                <c:if test="${orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                    <div class="control-group">
                        <label class="control-label">供货方式:</label>
                        <div class="controls">
                            产地直发:<input name="localOriginType" value="0" checked type="radio" readonly="readonly"/>
                            本地备货:<input name="localOriginType" value="1" type="radio" readonly="readonly"/>
                        </div>
                    </div>
                </c:if>
            </c:if>

        </c:when>
        <c:otherwise>
            <div class="form-actions">
                <!-- 一单到底订单审核 -->
                <shiro:hasPermission name="biz:order:bizOrderHeader:audit">
                    <c:if test="${entity.orderType == BizOrderTypeEnum.PURCHASE_ORDER.state}">
                        <c:if test="${entity.bizPoHeader.commonProcessList == null}">
                            <input id="btnSubmit" type="button" onclick="checkPass('DO')" class="btn btn-primary"
                                   value="审核通过"/>
                            <input id="btnSubmit" type="button" onclick="checkReject('DO')" class="btn btn-primary"
                                   value="审核驳回"/>
                        </c:if>
                    </c:if>
                    <c:if test="${entity.str == 'audit' && type != 0 && type != 1}">
                        <c:if test="${entity.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state}">
                            <c:if test="${entity.bizPoHeader.commonProcessList == null}">
                                <input id="btnSubmit" type="button" onclick="checkPass('SO')" class="btn btn-primary"
                                       value="审核通过"/>
                                <input id="btnSubmit" type="button" onclick="checkReject('SO')" class="btn btn-primary"
                                       value="审核驳回"/>
                            </c:if>
                        </c:if>
                    </c:if>

                    <c:if test="${entity.str == 'audit' && (type != 0 || type != 1)}">
                        <c:if test="${entity.orderType == BizOrderTypeEnum.ORDINARY_ORDER.state}">
                                <input type="button" onclick="checkPass('JO')" class="btn btn-primary"
                                       value="审核通过"/>
                                <input type="button" onclick="checkReject('JO')" class="btn btn-primary"
                                       value="审核驳回"/>
                        </c:if>
                    </c:if>

                    <c:if test="${entity.str == 'pay'}">
                        <input id="btnSubmit" type="button" onclick="pay()" class="btn btn-primary" value="确认支付"/>
                    </c:if>
                </shiro:hasPermission>

                    <!-- 一单到底，采购单审核 -->
                    <shiro:hasPermission name="biz:po:bizPoHeader:audit">
                        <c:if test="${entity.str == 'audit'}">
                            <c:if test="${entity.bizPoHeader.commonProcessList != null && fn:length(entity.bizPoHeader.commonProcessList) > 0}">
                                <input id="btnSubmit" type="button" onclick="checkPass('PO')" class="btn btn-primary"
                                       value="审核通过"/>
                                <input id="btnSubmit" type="button" onclick="checkReject('PO')" class="btn btn-primary"
                                       value="审核驳回"/>
                            </c:if>
                        </c:if>
                    </shiro:hasPermission>

                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails && entity.str!='audit'}">
                    <shiro:hasPermission name="biz:order:bizOrderHeader:edit">
                        <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;
                    </shiro:hasPermission>
                </c:if>
                <%--<c:if test="${empty entity.orderDetails}">--%>
                <c:if test="${(empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails) || (refundSkip eq 'refundSkip')}">
                    <shiro:hasPermission name="biz:order:bizOrderHeader:edit">
                        <c:if test='${entity.drawBack.drawbackStatus==OrderHeaderDrawBackStatusEnum.REFUND.state}'>
                            <input id="refund" class="btn" type="button" value="同意退款" onclick="checkInfo('${OrderHeaderDrawBackStatusEnum.REFUNDING.state}','退款申请',${bizOrderHeader.id})"/>
                        </c:if>
                    </shiro:hasPermission>
                </c:if>
                    <%--<a href="#" onclick="checkInfo('<%=OrderHeaderBizStatusEnum.REFUNDING.getState() %>','退款申请',${orderHeader.id})">退款</a>--%>
            </div>
        </c:otherwise>
    </c:choose>
    <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
</form:form>

<%--详情列表--%>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>详情行号</th>
        <c:if test="${orderType != DefaultPropEnum.PURSEHANGER.propValue}">
            <th>货架名称</th>
        </c:if>
        <th>商品名称</th>
        <th>商品编号</th>
        <th>商品货号</th>
        <%--<th>已生成的采购单</th>--%>
        <c:if test="${entity.orderDetails eq 'details' || entity.orderNoEditable eq 'editable' || bizOrderHeader.flag eq 'check_pending'}">
            <th>商品出厂价</th>
        </c:if>
        <th>供应商</th>
        <th>供应商电话</th>
        <th>商品单价</th>
        <th>采购数量</th>
        <th>总 额</th>
        <th>已发货数量</th>
        <c:if test="${bizOrderHeader.bizStatus>=15 && bizOrderHeader.bizStatus!=45}">
            <th>发货方</th>
        </c:if>
        <th>创建时间</th>
        <shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
            <c:if test="${entity.str != 'audit'}">
                <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                    <th>操作</th>
                </c:if>
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
            <c:if test="${orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                <td>
                        ${bizOrderDetail.shelfInfo.opShelfInfo.name}
                </td>
            </c:if>
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
            <%--<td>--%>
                <%--<a href="${ctx}/biz/po/bizPoHeader/form?id=${detailIdMap.get(bizOrderDetail.getLineNo())}">${orderNumMap.get(bizOrderDetail.getLineNo())}</a>--%>
            <%--</td>--%>
            <c:if test="${entity.orderDetails eq 'details' || entity.orderNoEditable eq 'editable' || bizOrderHeader.flag eq 'check_pending'}">
                <td>
                        ${bizOrderDetail.buyPrice}
                </td>
            </c:if>
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
                <fmt:formatDate value="${bizOrderDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <shiro:hasPermission name="biz:order:bizOrderDetail:edit">
                <c:if test="${entity.str != 'audit'}">
                    <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                        <td>
                            <c:if test="${empty bizOrderHeader.clientModify}">
                                <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}&orderType=${orderType}">修改</a>
                                <a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}&sign=1&orderHeader.oneOrder=${entity.oneOrder}&orderType=${orderType}"
                                   onclick="return confirmx('确认要删除该sku商品吗？', this.href)">删除</a>
                            </c:if>
                            <c:if test="${bizOrderHeader.clientModify eq 'client_modify'}">
                                <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}&orderHeader.flag=check_pending&orderHeader.consultantId=${bizOrderHeader.consultantId}&orderType=${orderType}">修改</a>
                                <a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}&sign=1&orderHeader.oneOrder=${entity.oneOrder}&orderHeader.flag=check_pending&orderHeader.consultantId=${bizOrderHeader.consultantId}&orderType=${orderType}"
                                   onclick="return confirmx('确认要删除该sku商品吗？', this.href)">删除</a>
                            </c:if>
                        </td>
                    </c:if>
                </c:if>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>


<div class="form-actions">
    <c:if test="${entity.str != 'audit'}">
    <c:if test="${empty entity.orderNoEditable}">
        <c:if test="${bizOrderHeader.id!=null}">
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <shiro:hasPermission name="biz:order:bizOrderDetail:edit">
                    <c:if test="${empty bizOrderHeader.clientModify}">
                    <input type="button"
                           onclick="javascript:window.location.href='${ctx}/biz/order/bizOrderDetail/form?orderHeader.id=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}&orderType=${orderType}';"
                           class="btn btn-primary"
                           value="订单商品信息添加"/></c:if>
                    <c:if test="${bizOrderHeader.clientModify eq 'client_modify'}">
                        <input type="button"
                               onclick="javascript:window.location.href='${ctx}/biz/order/bizOrderDetail/form?orderHeader.id=${bizOrderHeader.id}&orderHeader.oneOrder=${entity.oneOrder}&orderHeader.clientModify=client_modify&orderHeader.consultantId=${bizOrderHeader.consultantId}&orderType=${orderType}';"
                               class="btn btn-primary"
                               value="订单商品信息添加"/>
                    </c:if>
                </shiro:hasPermission>
            </c:if>
            <c:if test="${not empty entity.orderDetails}">
                <input onclick="window.print();" type="button" class="btn btn-primary" value="打印订单" style="background:#F78181;"/>
            </c:if>
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

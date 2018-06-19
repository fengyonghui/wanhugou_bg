<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.DefaultPropEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>

<link rel="stylesheet" href="${ctxStatic}/tree-multiselect/dist/jquery.tree-multiselect.min.css">
<link rel="stylesheet" href="${ctxStatic}/jquery-plugin/jquery.searchableSelect.css">

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
        });
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
        <c:if test="${empty bizOrderHeader.clientModify}">
            <li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
        </c:if>
        <c:if test="${bizOrderHeader.clientModify eq 'client_modify'}">
            <li><a href="${ctx}/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=${bizOrderHeader.consultantId}">订单信息列表</a></li>
        </c:if>
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
            <c:if test="${empty bizOrderHeader.clientModify}">
                <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}">订单信息<shiro:hasPermission
                        name="biz:order:bizOrderHeader:edit">${not empty bizOrderHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
                        name="biz:order:bizOrderHeader:edit">查看</shiro:lacksPermission></a>
            </c:if>
            <c:if test="${bizOrderHeader.clientModify eq 'client_modify'}">
                <a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}&flag=check_pending&consultantId=${bizOrderHeader.consultantId}">订单信息<shiro:hasPermission
                        name="biz:order:bizOrderHeader:edit">${not empty bizOrderHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
                        name="biz:order:bizOrderHeader:edit">查看</shiro:lacksPermission></a>
            </c:if>
        </c:if>
    </li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/saveRefund?type=refund" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <input type="hidden" name="oneOrder" value="${entity.oneOrder}">
    <input type="hidden" id="bizOrderMark" name="orderMark" value="${bizOrderHeader.orderMark}">
    <input type="hidden" name="clientModify" value="${bizOrderHeader.clientModify}" />
    <input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}" />
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
                                allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
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
    <%--<div class="control-group">
        <label class="control-label">运费：</label>
        <div class="controls">
            <c:if test="${entity.orderNoEditable eq 'editable' || entity.orderDetails eq 'details' || bizOrderHeader.flag eq 'check_pending'}">
                <form:input path="freight" htmlEscape="false" disabled="true" class="input-xlarge" readonly="true"/>
            </c:if>
            <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
                <form:input path="freight" htmlEscape="false" placeholder="请输入运费" class="input-xlarge required" readonly="true"/>
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
    </div>--%>
    <div class="control-group">
        <label class="control-label">已付金额：</label>
        <div class="controls">
            <font color="#088A29">
                <fmt:formatNumber type="percent" value="${bizOrderHeader.receiveTotal/(bizOrderHeader.totalDetail+bizOrderHeader.totalExp+bizOrderHeader.freight)}" maxFractionDigits="2" />
            </font> (<fmt:formatNumber type="number" value="${bizOrderHeader.receiveTotal}" pattern="0.00"/>)
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">应退金额：</label>
        <div class="controls">
            <input type="text" value="<fmt:formatNumber type="number" value="${bizOrderHeader.receiveTotal}" pattern="0.00"/>"
                   disabled="true" class="input-xlarge">
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">退款凭证:
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
    <form:input path="photos" id="photos" cssStyle="display: none"/>
    <form:input path="bizStatus" id="bizStatus" cssStyle="display: none"/>
    <form:input path="receiveTotal" id="receiveTotal" cssStyle="display: none"/>
    <div class="form-actions">
        <c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
            <shiro:hasPermission name="biz:order:bizOrderHeader:doRefund">
                <input id="btnSubmit" class="btn btn-primary" type="button" value="保存" onclick="return submitCustomForm()"/>&nbsp;
            </shiro:hasPermission>
        </c:if>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
    </div>
</form:form>

<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>
<script src="${ctxStatic}/bootstrap/multiselect.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/tree-multiselect/dist/jquery.tree-multiselect.js"></script>
<script src="${ctxStatic}/jquery-select2/3.5.3/select2.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
<script src="${ctxStatic}/jquery-plugin/jquery.searchableSelect.js" type="text/javascript"></script>
<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/common/base.js" type="text/javascript"></script>

<script type="text/javascript">
    function submitPic(id, multiple){
        var f = $("#" + id).val();
        if(f==null||f==""){
            alert("错误提示:上传文件不能为空,请重新选择文件");
            return false;
        }else{
            var extname = f.substring(f.lastIndexOf(".")+1,f.length);
            extname = extname.toLowerCase();//处理了大小写
            if(extname!= "jpeg"&&extname!= "jpg"&&extname!= "gif"&&extname!= "png"){
                $("#picTip").html("<span style='color:Red'>错误提示:格式不正确,支持的图片格式为：JPEG、GIF、PNG！</span>");
                return false;
            }
        }
        var file = document.getElementById(id).files;
        var size = file[0].size;
        if(size>2097152){
            alert("错误提示:所选择的图片太大，图片大小最多支持2M!");
            return false;
        }
        ajaxFileUploadPic(id, multiple);
    }
    var a = 0;
    function ajaxFileUploadPic(id, multiple) {
        $.ajaxFileUpload({
            url : '${ctx}/biz/order/bizOrderHeader/saveColorImg', //用于文件上传的服务器端请求地址
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
                var imgDivHtml = "<td><img src=\"$Src\" customInput=\""+ id +"Img\" style='width: 100px' onclick=\"removeThis(this);\"></td>";
                var imgPhotosSorts = "<td id=''><input name='imgPhotosSorts' style='width: 70px' type='number'/></td>";
                if (imgList && imgList.length > 0 && multiple) {
                    for (var i = 0; i < imgList.length; i ++) {
                        // imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                        if (id == "prodMainImg") {
                            $("#imgPhotosSorts").append("<td><input id='"+"main" + i + "' name='imgPhotosSorts' value='"+a+"' style='width: 70px' type='number'/></td>");
                            // $("#prodMainImgImg").append(imgDivHtml.replace("$Src", imgList[i]));
                            $("#prodMainImgImg").append("<td><img src=\"" + imgList[i] + "\" customInput=\"" + id + "Img\" style='width: 100px' onclick=\"removeThis(this,"+"$('#main" + i + "'));\"></td>");
                            a += 1;
                        }
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

    function deletePic(id) {
        var f = $("#" + id);
        f.attr("src", "");
    }
    function removeThis(obj,item) {
        $(obj).remove();
        $(item).remove();
    }

    function deleteParentParentEle(that) {
        $(that).parent().parent().remove();
    }

    function submitCustomForm() {
        if(confirm("您确认该订单退款 "+ $("#receiveTotal").val() + " 元的申请吗？")){
            var bb = true;
            $("input[name='imgPhotosSorts']").each(function () {
                if ($(this).val()=='') {
                    bb = false;
                    return;
                }
            });
            if (bb) {
                var mainImg = $("#prodMainImgDiv").find("[customInput = 'prodMainImgImg']");
                var mainImgStr = "";
                for (var i = 0; i < mainImg.length; i ++) {
                    mainImgStr += ($(mainImg[i]).attr("src") + "|");
                }
                if(mainImgStr == null || mainImgStr == ""){
                    alert("请上传退款凭证！");
                } else {
                    $("#photos").val(mainImgStr);
                    $("#receiveTotal").val("0");
                    $("#bizStatus").val("<%=OrderHeaderBizStatusEnum.REFUNDED.getState()%>");
                    loading('正在提交，请稍等...');
                    inputForm.submit();
                }
            } else {
                alert("退款凭证图片序号不能为空");
            }
        }

    }
</script>

</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>出库确认</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function() {
            $('#select_all').live('click',function(){
                var choose=$("input[type='checkbox']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    if($("#prodInfo").find("td").length==0){
                        alert("请先选择待发货订单,然后点击确定。");
                        return;
                    }
                    var tt="";
                    var flag = false;
                    var total = 0;
                    $('input:checkbox:checked').each(function(i) {
                        var t= $(this).val();
                        var detail="";
                        var num ="";
                        var sObj = $("#prodInfo").find("input[title='sent_"+t+"']");
                        var iObj = $("#prodInfo").find("select[title='invInfoId']");
                        var tObj = $("#prodInfo").find("select[title='skuType']");
                        sObj.each(function (index) {
                            total+= parseInt($(this).val());
                        });
                        if(iObj.length!=0){
                            iObj.each(function (index) {
                                if ($(this).val() != ''){
                                    flag = true;
                                }
                            });
                            $("#prodInfo").find("input[title='details_"+t+"']").each(function (i) {
                                detail += $(this).val() + "-" + sObj[i].value + "-" + iObj[i].value + "-" + tObj[i].value + "*";
                            });
                        }else {
                            flag = true;
                            $("#prodInfo").find("input[title='details_"+t+"']").each(function (i) {
                                detail+=$(this).val()+"-"+sObj[i].value+"*";

                            });
                        }

                        tt+=t+"#"+detail+",";

                    });
                    tt=tt.substring(0,tt.length-1);

                    if(window.confirm('你确定要发货吗？') && flag && total > 0){
                        if (tt != '') {
                            $("#prodInfo").append("<input name='orderHeaders' type='hidden' value='"+tt+"'>");
                        }
                        var orderHeaders = $("input[name='orderHeaders']").val();
                            $.ajax({
                                type:"post",
                                url:"${ctx}/biz/inventory/bizInventorySku/findInvSku?orderHeaders="+encodeURIComponent(orderHeaders),
                                success:(function (data) {
                                    if (data == "true"){
                                        form.submit();
                                        return true;
                                        loading('正在提交，请稍等...');
                                    }else {
                                        $("input[name='orderHeaders']").each(function () {
                                            $(this).remove();
                                        });
                                        alert("库存不足！");
                                        return false;
                                    }
                                })
                            })

                    }
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
        });
        function checkNum(ordQty,sentQty, sendQty) {
            if (parseInt(sendQty.value)+parseInt(sentQty) > parseInt(ordQty)){
                alert("发货数量大于需求数量，请修改");
                $(sendQty).val(0);
            }
        }
        function getOrder () {
            var bizStatus = 0;
            $.ajax({
                type:"post",
                url:"${ctx}/biz/order/bizOrderHeader/findByOrderV2?flag=" + bizStatus,
                data:{"orderNum":"${orderNum}"},
                success:function (data) {
                    data = JSON.parse(data);
                    if(data.ret != true && data.ret != 'true') {
                        alert(data.errmsg);
                        window.history.go(-1);
                    }
                    data = data.data;
                    if ($("#id").val() == '') {
                        $("#prodInfo").empty();
                    }

                    if(bizStatus==0){
                        var selecttd="<select class='input-mini' title='invInfoId'>";
                        $.each(data.inventoryInfoList,function (index,inventory) {
                            selecttd+="<option value='"+inventory.id+"'>"+inventory.name+"</option>"
                        });
                        var skuType = "<select class='input-mini' title='skuType'><option value='1'>采购中心</option><option value='2'>供应商</option></select>";
                    }
                    var tr_tds="";
                    var bizName ="";
                    $.each(data.bizOrderHeaderList, function (index,orderHeader) {
                        if(orderHeader.bizStatus==15){
                            bizName="供货中"
                        }
                        if(orderHeader.bizStatus==17){
                            bizName="采购中"
                        }else if(orderHeader.bizStatus==18){
                            bizName="采购完成"
                        }else if(orderHeader.bizStatus==19){
                            bizName="供应商供货"
                        }else if(orderHeader.bizStatus==20){
                            bizName="已发货"
                        }

                        var flag= true;
                        var deId = "";
                        var  num = "";
                        $.each(orderHeader.orderDetailList,function (index,detail) {

                            tr_tds+="<tr class='tr_"+orderHeader.id+"'>";

                            if(flag){
                                tr_tds+="<td rowspan='"+orderHeader.orderDetailList.length+"'><input type='checkbox' checked onclick=\"javascript:return false;\"  value='"+orderHeader.id+"' /></td>";

                                tr_tds+= "<td rowspan='"+orderHeader.orderDetailList.length+"'><a href='${ctx}/biz/order/bizOrderHeader/form?id="+orderHeader.id+"&orderDetails=details'> "+orderHeader.orderNum+"</a></td><td rowspan='"+orderHeader.orderDetailList.length+"'>"+orderHeader.customer.name+"</td><td rowspan='"+orderHeader.orderDetailList.length+"'>"+bizName+"</td>" ;
                            }
                            tr_tds+="<input title='details_"+orderHeader.id+"' name='' type='hidden' value='"+detail.id+"'>";
                            tr_tds+= "<td>"+detail.skuInfo.name+"</td><td>"+detail.vendor.name+"</td><td>"+(detail.skuInfo.itemNo==undefined?"":detail.skuInfo.itemNo)+"</td>" ;
                            if(bizStatus==0) {
                                tr_tds += "<td>" + selecttd + "</td><td>"+ skuType +"</td>"
                            }
                            tr_tds+= "<td>"+detail.ordQty+"</td><td>"+detail.sentQty+"</td>";
                            if(detail.ordQty==detail.sentQty){
                                tr_tds+="<td><input  type='text' readonly='readonly' title='sent_"+orderHeader.id+"' name='' value='0'></td>";
                            }else {
                                tr_tds+="<td><input  type='text'  title='sent_"+orderHeader.id+"' name='' onchange='checkNum("+detail.ordQty+","+detail.sentQty+",this)' value='"+(detail.ordQty-detail.sentQty)+"'></td>";
                            }

                            tr_tds+="</tr>";
                            if(orderHeader.orderDetailList.length>1){
                                flag=false;
                            }
                        });

                    });
                    $("#prodInfo").append(tr_tds);
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }
        getOrder();

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="#">出库确认</a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/save" method="post" class="form-horizontal">
    <sys:message content="${message}"/>
    <form:hidden path="ship" value="0"/>
    <form:hidden path="isConfirm" value="1"/>
    <form:hidden path="bizStatus" value="0"/>

    <div class="control-group">
        <label class="control-label">物流单号：</label>
        <div class="controls">
            <form:input path="trackingNumber" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

        <div class="control-group">
            <label class="control-label">发货人：</label>
            <div class="controls">
                <form:select about="choose" path="carrier" class="input-medium required">
                    <form:options items="${userList}" itemLabel="name" itemValue="name" htmlEscape="false"/>
                </form:select>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
    <div class="control-group">
        <label class="control-label">发货时间：</label>
        <div class="controls">
            <input name="sendDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
                   value="<fmt:formatDate value="${bizInvoice.sendDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">订单信息：</label>

        <div class="controls">
            <table id="contentTable"  class="table table-striped table-bordered table-condensed">
                <thead>
                <tr>
                    <th></th>
                    <th>订单编号</th>
                    <th>经销店名称</th>
                    <th>业务状态</th>
                    <th>商品名称</th>
                    <th>供应商</th>
                    <th>商品货号</th>
                    <th>选择仓库</th>
                    <th>选择备货方</th>
                    <th>采购数量</th>
                    <th>已发货数量</th>
                    <th>发货数量</th>
                </tr>
                </thead>
                <tbody id="prodInfo">
                <input name="bizStatu" value="1" type="hidden"/>
                </tbody>
            </table>
                <%--<input id="ensureData" class="btn btn-primary" type="button"  value="确定"/>--%>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备注：</label>
        <div class="controls">
            <form:textarea path="remarks" htmlEscape="false" maxlength="200" class="input-xlarge "/>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="biz:inventory:bizInvoice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="发 货"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>
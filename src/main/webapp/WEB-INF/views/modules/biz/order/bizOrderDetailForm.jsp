<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>订单详情管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			/* 全选 全不选 */
            $('#chAll').live('click',function(){
                var choose=$("input[name='boxs']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
			$("#inputForm").validate({
				submitHandler: function(form){
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
			if($("#id").val()!=""){
                clickSku();
                <%--shelf();--%>
            }
		});
        function clickSku(){
             var skuInfoId=$('#skuInfoId').val();
                 $("#partNo").empty();
                 $("#unitPrice").empty();
                 $("#skuT").empty();
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSysBySku?skuId="+skuInfoId,
                    dataType:"json",
                    success:function(data){
                                console.log(JSON.stringify(data)+"----测试");
                           $("#partNo").val(data.partNo);
                           $("#unitPrice").val(data.buyPrice);
                           $("#skuT").val(data.skuType);
                       }
                });
                $("#partNo").change();
                $("#unitPrice").change();
                 $("#skuT").change();
            }

    </script>
    <script type="text/javascript">
<%--/* 货架列表 */--%>
function shelf(){
    $.ajax({
        type:"post",
        url:"${ctx}/biz/order/bizOrderDetail/opShelfInfo",
        dataType:"json",
        success:function(data){
            <%--console.log(JSON.stringify(data)+"-测试--");--%>
            var htmlInfo="<tbody>";
            for(var o in data){
                htmlInfo+="<tr><td><input type='checkbox' name='boxs' value="+data[o].id+">"+
                    "</td><td><input name='shelfList["+o+"].shelfInfo.id' value='"+data[o].id+"' type='hidden'/></td>"+
                    "<td>"+data[o].name+"</td><td>"+data[o].description+"<input type='hidden' name='shelfInfo.id' value='"+data[o].id+"'></td></tr>";
            }
            htmlInfo+="</tbody>";
            $("#boxTbody").append(htmlInfo);
        }
     }),
    $.jBox("id:orderDetailTablejBox",{
           title:"货架列表",
           width:500,
           height:250,
           buttons:{"确定":1,"取消":0},
           buttonsFocus:1,
           submit:function(v,h,f){
                if(v == 1){
                      console.log("执行确定方法");
					 var checkID ="";
					 $("input[name='boxs']").each(function(){
						 if($(this).attr("checked")){
								var infoId= $(this).val();
								var trId="shelfInfo"+infoId;
								var remov="<td><a href='#'>移除</a></td>";
								checkID+="<tr>"+$("#"+trId).html()+"</tr>";
								console.log(trId+"-----2");
								console.log($("#"+trId).html()+"-----3");
						 }
					 }); $("#shelfInfo_table").append(checkID);
                }
                return true;
           }
    })
}
<%--移除--%>
function removeItem(obj) {
    console.log(obj+"删除吗");
    $("#"+obj).remove();
}
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
    <li class="active"><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">订单详情<shiro:hasPermission
            name="biz:order:bizOrderDetail:edit">${not empty bizOrderDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="biz:order:bizOrderDetail:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderDetail" action="${ctx}/biz/order/bizOrderDetail/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="orderHeader.id"/>
    <form:hidden path="maxLineNo"/>
    <form:hidden path="orderHeader.oneOrder"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">订单行号：</label>
        <div class="controls">
            <form:input path="lineNo" disabled="true" placeholder="${bizOrderDetail.maxLineNo}" htmlEscape="false" class="input-xlarge required"/>
            <input name="lineNo" value="${bizOrderDetail.maxLineNo}" htmlEscape="false" type="hidden" class="required"/>
            <span class="help-inline">自动生成</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">货架名称：</label>
        <div class="controls">
                <%--<input name="shelfInfo.id" type="button" htmlEscape="false" onclick="shelf();" value="选择货架" class="input-xlarge"/>--%>
            <input type="text" name="shelfInfo.id" class="input-xlarge"/>
            <span class="help-inline"><font color="red">*</font>测试输入1-10</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">已选择货架：</label>
        <div class="controls">
            <table id="contentTable" class="table table-striped table-bordered table-condensed">
                <thead>
                <tr>
                    <th>货架名称</th>
                    <th>货架描述</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody id="shelfInfo_table">
                <c:if test="${bizOrderDetail.id !=null && bizOrderDetail.id!='' }">
                    <c:forEach items="${bizOrderDetail.shelfList}" var="bizOrderDetail">
                        <tr>
                            <td>
                                <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}">
                                        ${bizInvoiceDetail.id}</a>
                            </td>
                            <td>
                                    ${bizOrderDetail.invoiceHeader}
                            </td>
                            <td>
                                    ${bizOrderDetail.invoiceHeader.invTitle}
                            </td>
                            <td>
                                    ${bizOrderDetail.unitPrice}
                            </td>
                            <td>
                                    ${bizOrderDetail.ordQty}
                            </td>
                            <td>
                                    ${bizOrderDetail.sentQty}
                            </td>
                            <td>
                                <fmt:formatDate value="${bizOrderDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            </td>
                            <shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
                                <td>
                                        <%--<a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}">修改</a>--%>
                                        <%--<a href="${ctx}/biz/invoice/bizInvoiceDetail/delete?id=${bizOrderDetail.id}&sign=1"--%>
                                        <%--onclick="return confirmx('确认要删除该sku商品吗？', this.href)">删除</a>--%>
                                    <a href="#">删除</a>
                                </td>
                            </shiro:hasPermission>
                        </tr>
                    </c:forEach>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">sku商品名称：</label>
        <div class="controls">
            <%--<sys:treeselect id="skuInfo" name="skuInfo.id" value="${bizOrderDetail.skuInfo.id}" labelName="skuInfo.id"--%>
                            <%--labelValue="${bizOrderDetail.skuInfo.name}" notAllowSelectRoot="true"--%>
                            <%--notAllowSelectParent="true"--%>
                            <%--title="sku名称" url="/biz/product/bizProductInfo/querySkuTreeList" extId="${skuInfo.id}"--%>
                            <%--cssClass="input-xlarge required" onchange="clickSku();"--%>
                            <%--allowClear="${skuInfo.currentUser.admin}" dataMsgRequired="必填信息">--%>
            <%--</sys:treeselect>--%>
            <%--<input type="text" name="skuInfo.id" id="skuInfo">//商品ID测试--%>
            <input type="text" name="orderDetaIds" id="orderDetaIds">//多选商品 "，"隔开 测试
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">商品编码：</label>
        <div class="controls">
            <form:input path="partNo" htmlEscape="false" readOnly="true" class="input-xlarge"/>
            <input name="partNo" htmlEscape="false" type="hidden"/>
            <span class="help-inline">选择sku商品名称自动生成</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">材质：</label>
        <div class="controls">
            <form:input path="quality" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font>测试输入</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">颜色：</label>
        <div class="controls">
            <form:input path="color" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font>测试输入</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">规格：</label>
        <div class="controls">
            <form:input path="standard" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font>测试输入</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">商品单价：</label>
        <div class="controls">
            <form:input path="unitPrice" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
            <input type="hidden" id="skuT" name="skuType" class="input-xlarge">
            <span class="help-inline"><font color="red">*</font>选择sku商品名称自动生成，可以更改</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">采购数量：</label>
        <div class="controls">
            <form:input path="ordQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
            <input name="ordQtyUpda" value="${bizOrderDetail.ordQtyUpda}" type="hidden" class="input-xlarge"/>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">发货数量：</label>
        <div class="controls">
            <form:input path="sentQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="biz:order:bizOrderDetail:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返回" onclick="history.go(-1)"/>
    </div>
</form:form>

<%--货架名称列表弹窗--%>
<div id="orderDetailTablejBox" style="display: none">
    <table id="cheall" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th><input type="checkbox" id="chAll"/></th>
            <th>货架名称</th>
            <th>货架描述</th>
        </tr>
        </thead>
        <tbody id="boxTbody"></tbody>
    </table>
</div>
</body>
</html>
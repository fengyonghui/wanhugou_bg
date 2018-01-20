<%@ page contentType="text/html;charset=UTF-8" %>
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
            if($("#id").val()!=""){
                clickBut();
            }
            $("#addAddressHref").click(function () {
                var officeId=$("#officeId").val();
                var officeName =$("#officeName").val();
                window.location.href="${ctx}/sys/office/sysOfficeAddress/form?ohId=${bizOrderHeader.id}&office.id="+officeId+"&office.name="+officeName+"&flag=order"
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
                url:"${ctx}/sys/office/sysOfficeAddress/findAddrByOffice?office.id="+officeId,
                success:function(data){
                 <%--console.log(JSON.stringify(data)+"---测试1");--%>
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
                            <%--if(data.deFaultStatus ==1){--%>
                                var option2=$("<option>").text(data.bizLocation.province.name).val(data.bizLocation.province.id);
                                $("#province").append(option2);
                                var option3=$("<option/>").text(data.bizLocation.city.name).val(data.bizLocation.city.id);
                                $("#city").append(option3);
                                var option4=$("<option/>").text(data.bizLocation.region.name).val(data.bizLocation.region.id);
                                $("#region").append(option4);
                                $("#address").val(data.bizLocation.address);
                             <%--}--%>
                       //当省份的数据加载完毕之后
                       $("#province").change();
                       $("#city").change();
                       $("#region").change();
                       $("#address").change();
                    }
                }
            });
        }

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
    <li class="active"><a href="${ctx}/biz/order/bizOrderHeader/form?id=${bizOrderHeader.id}">订单信息<shiro:hasPermission
            name="biz:order:bizOrderHeader:edit">${not empty bizOrderHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="biz:order:bizOrderHeader:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="oneOrder"/>
    <form:hidden path="platformInfo.id" value="1"/>
    <sys:message content="${message}"/>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">订单编号：</label>--%>
        <%--<div class="controls">--%>
            <%--<form:input path="orderNum" disabled="true" placeholder="由系统自动生成" htmlEscape="false" maxlength="30"--%>
                        <%--class="input-xlarge required"/>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">订单类型：</label>--%>
        <%--<div class="controls">--%>
            <%--<form:select path="orderType" class="input-medium required">--%>
                <%--&lt;%&ndash;默认选中&ndash;%&gt;--%>
                <%--<form:option value="1" label="普通订单" itemLabel="label" itemValue="value" htmlEscape="false"/>--%>
                <%--&lt;%&ndash;<form:option value="" label="请选择"/>&ndash;%&gt;--%>
                <%--&lt;%&ndash;<form:options items="${fns:getDictList('biz_order_type')}" itemLabel="label" itemValue="value"&ndash;%&gt;--%>
                              <%--&lt;%&ndash;htmlEscape="false"/>&ndash;%&gt;</form:select>--%>
            <%--<span class="help-inline"><font color="red">*</font>j默认选择</span>--%>
        <%--</div>--%>
    <%--</div>--%>
    <div class="control-group">
        <label class="control-label">采购商名称：</label>
        <div class="controls">
            <sys:treeselect id="office" name="customer.id" value="${entity.customer.id}" labelName="customer.name"
                            labelValue="${entity.customer.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
                            title="采购商" url="/sys/office/queryTreeList?type=6" cssClass="input-xlarge required"
                            allowClear="${office.currentUser.admin}" onchange="clickBut();" dataMsgRequired="必填信息"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">订单详情总价：</label>
        <div class="controls">
            <form:input path="totalDetail" htmlEscape="false" placeholder="0.0" readOnly="true" class="input-xlarge"/>
            <input name="totalDetail" value="${entity.totalDetail}" htmlEscape="false" type="hidden"/>
            <span class="help-inline">自动计算</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">订单总费用：</label>
        <div class="controls">
            <form:input path="totalExp" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">运费：</label>
        <div class="controls">
            <form:input path="freight" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <c:if test="${fns:getUser().isAdmin()}">
    <div class="control-group">
        <label class="control-label">发票状态：</label>
        <div class="controls">
            <form:select path="invStatus" class="input-medium required">
                <form:option value="" label="请选择"/>
                <%--<c:if test="${bizOrderHeader.id==null}"><form:option value="1" label="未开发票"/></c:if>--%>
                <%--默认选中--%>
                <form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/></form:select>

            <span class="help-inline"><font color="red">*</font>默认选择</span>
        </div>
    </div>
    </c:if>
    <c:if test="${fns:getUser().isAdmin()}">
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
    <div class="control-group" id="add1">
        <label class="control-label">收货地址；</label>
        <div class="controls">
            <select id="province" class="input-medium" name="bizLocation.province.id"
                    style="width:150px;text-align: center;">
                <option value="-1">—— 省 ——</option>
            </select>
            <select id="city" class="input-medium" name="bizLocation.city.id" style="width:150px;text-align: center;">
                <option value="-1">—— 市 ——</option>
            </select>
            <select id="region" class="input-medium" name="bizLocation.region.id"
                    style="width:150px;text-align: center;">
                <option value="-1">—— 区 ——</option>
            </select>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group" id="add2" style="display:none">
        <label class="control-label">收货地址；</label>
        <div class="controls">
            <%--<a id="addAddressHref" href="${ctx}/sys/office/sysOfficeAddress/form?ohId=${bizOrderHeader.id}&office.id=${customer.id}&flag=order">--%>
                <input id="addAddressHref" type="button" value="新增地址"  htmlEscape="false" class="input-xlarge required"/>
                <%--</a>--%>
            <label class="error" id="addError" style="display:none;">必填信息</label>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <div class="control-group" id="add3">
        <label class="control-label">详细地址；</label>
        <div class="controls">
            <input type="text" id="address" name="bizLocation.address" htmlEscape="false"
                   class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="biz:order:bizOrderHeader:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                         type="submit"
                                                                         value="保存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>


<%--详情列表--%>
<sys:message content="${message}"/>
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>详情行号</th>
            <th>货架名称</th>
            <th>商品名称</th>
            <th>材质</th>
            <th>颜色</th>
            <th>规格</th>
            <th>商品编号</th>
            <th>商品单价</th>
            <th>采购数量</th>
            <th>发货数量</th>
            <th>创建时间</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${entity.orderDetailList}" var="bizOrderDetail">
            <tr>
                <td>
                    ${bizOrderDetail.lineNo}
                </td>
                <td>
                    ${bizOrderDetail.shelfInfo.id}
                </td>
                <td><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&oneOrder=${bizOrderHeader.oneOrder}">
                    ${bizOrderDetail.skuName}
                </a></td>
                <td>
                    ${bizOrderDetail.quality}
                </td>
                <td>
                    ${bizOrderDetail.color}
                </td>
                <td>
                    ${bizOrderDetail.standard}
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
                <td>
                    ${bizOrderDetail.sentQty}
                </td>
                <td>
                    <fmt:formatDate value="${bizOrderDetail.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
                <shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
                    <td>
                        <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderId=${bizOrderHeader.id}&oneOrder=${bizOrderHeader.oneOrder}">修改</a>
                        <a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}&sign=1"
                           onclick="return confirmx('确认要删除该sku商品吗？', this.href)">删除</a>
                    </td>
                </shiro:hasPermission>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="form-actions">
        <c:if test="${bizOrderHeader.id !=null && bizOrderHeader.id!='' }">
            <shiro:hasPermission name="biz:order:bizOrderDetail:edit"><input type="button"
                                                                       onclick="javascript:window.location.href='${ctx}/biz/order/bizOrderDetail/form?orderHeader.id=${bizOrderHeader.id}';"
                                                                       class="btn btn-primary"
                                                                       value="订单商品信息添加"/></shiro:hasPermission>
        </c:if>
    </div>
</body>
</html>
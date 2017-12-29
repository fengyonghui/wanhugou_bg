<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单详情管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
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
                clickSuk();
            }
		});
		function clickSku(){
             var skuInfoId=$('#skuInfoId').val();
             $("#partNo").empty();
              $("#unitPrice").empty();
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSysBySku?skuId="+skuInfoId,
                    dataType:"json",
                    success:function(data){
                             console.log(data);
                           $("#partNo").val(data.partNo);
                           $("#unitPrice").val(data.buyPrice);
                       }
                });
                $("#partNo").change();
                $("#unitPrice").change();
            }

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">订单详情<shiro:hasPermission name="biz:order:bizOrderDetail:edit">${not empty bizOrderDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizOrderDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOrderDetail" action="${ctx}/biz/order/bizOrderDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="orderHeader.id"/>
		<form:hidden path="maxLineNo"/>
		<sys:message content="${message}"/>
        <div class="control-group">
            <label class="control-label">订单行号：</label>
            <div class="controls">
                <form:input path="lineNo"  disabled="true" placeholder="${bizOrderDetail.maxLineNo}" htmlEscape="false" class="input-xlarge required"/>
                <input name="lineNo" value="${bizOrderDetail.maxLineNo}" htmlEscape="false" type="hidden" class="required"/>
                <span class="help-inline"><font color="red">*</font></span>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label">sku商品名称：</label>
                <div class="controls">
                    <sys:treeselect id="skuInfo" name="skuInfo.id" value="${bizOrderDetail.skuInfo.id}" labelName="skuInfo.id"
                                    labelValue="${bizOrderDetail.skuInfo.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
                                    title="sku名称"  url="/biz/product/bizProductInfo/querySkuTreeList" extId="${skuInfo.id}"
                                    cssClass="input-xlarge required" onchange="clickSku();"
                                    allowClear="${skuInfo.currentUser.admin}"  dataMsgRequired="必填信息">
                    </sys:treeselect>
                </div>
		</div>
		<div class="control-group">
			<label class="control-label">商品编码：</label>
			<div class="controls">
				<form:input path="partNo" htmlEscape="false" readOnly="true" class="input-xlarge"/>
                <input name="partNo" htmlEscape="false" type="hidden"/>
			</div>
		</div>
		<div class="control-group">
            <label class="control-label">商品单价：</label>
            <div class="controls">
                <form:input path="unitPrice" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
            </div>
		</div>
		<div class="control-group">
			<label class="control-label">采购数量：</label>
			<div class="controls">
				<form:input path="ordQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
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


  <%--详情列表
 <sys:message content="${message}"/>
 <table id="contentTable" class="table table-striped table-bordered table-condensed">
     <thead>
     <tr>
         <th>详情行号</th>
         <th>商品名称</th>
         <th>商品编号</th>
         <th>商品单价</th>
         <th>采购数量</th>
         <th>发货数量</th>
         <th>创建时间</th>
         <th>操作</th>
     </tr>
     </thead>
<c:if test="${entity.id !=null && entity.id!='' }">
     <tbody>
   <c:forEach items="${entity.orderDetailList}" var="bizOrderDetail">
     <tr>
         <td><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">
                 ${bizOrderDetail.id}</a>
         </td>
         <td>
                 ${bizOrderDetail.skuName}
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
         <shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><td>
             <a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">修改</a>
             <a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}&sign=1" onclick="return confirmx('确认要删除该sku商品吗？', this.href)">删除</a>
         </td></shiro:hasPermission>
     </tr>
     </c:forEach>
     </tbody>
 </table>
  </c:if>--%>
</body>
</html>
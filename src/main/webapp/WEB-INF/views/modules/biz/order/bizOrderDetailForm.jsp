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
		/*function clickSuk(){
             var skuNameId=$('#skuNameId').val();
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/order/bizOrderDetail/findSysBySku?skuInfo.id="+skuNameId,
                    dataType:"json",
                    success:function(data){
                    console.log(JSON.stringify(data)+"测试");
                       $.each(data,function(index,add){
                             console.log(add);
                            console.log(add.skuInfo.id);
                          //  $("#sku").val(add.skuInfo.id);
                       });
                      // $("#sku").change();
                    }
                });
            }*/
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">订单详情<shiro:hasPermission name="biz:order:bizOrderDetail:edit">${not empty bizOrderDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizOrderDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOrderDetail" action="${ctx}/biz/order/bizOrderDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
            <label class="control-label">订单表ID：</label>
            <div class="controls"><%--不可编辑标签属性 disabled="true" placeholder="系统生成"--%>
                <form:input path="orderHeader.id"  htmlEscape="false" maxlength="11" class="input-xlarge required"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">订单行情号：</label>
            <div class="controls">
                <form:input path="lineNo" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font></span>
            </div>
        </div>
		<div class="control-group">
            <label class="control-label">sku商品名称：</label>
                <div class="controls">
                    <sys:treeselect id="skuName" name="skuName" value="${bizOpShelfSku.skuProd.id}" labelName="skuName"
                            labelValue="${bizOpShelfSku.skuInfo.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
                            title="商品名称"  url="/biz/product/bizProductInfo/querySkuTreeList" extId="${skuInfo.id}"
                            cssClass="input-xlarge required" onchange="clickSuk();"
                            allowClear="${skuInfo.currentUser.admin}"  dataMsgRequired="必填信息">
                        <span class="help-inline"><font color="red">*</font> </span>
                    </sys:treeselect>
                </div>
		</div>
         <div class="control-group">
            <label class="control-label">sku商品ID：</label>
            <div class="controls">
                <form:input path="skuInfo.id" id="sku" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">商品编码：</label>
			<div class="controls">
				<form:input path="partNo" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
            <label class="control-label">商品单价：</label>
            <div class="controls">
                <form:input path="unitPrice" disabled="true" value="11" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
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
            <input id="btnSubmit" class="btn btn-primary" type="submit" onclick="window.location.reload()" value="保存"/>&nbsp;</shiro:hasPermission>
            <input id="btnCancel" class="btn" type="button" value="返回" onclick="history.go(-1)"/>
        </div>
       </form:form>


  <%--详情列表--%>
   	<table id="contentTable" class="table table-striped table-bordered table-condensed">
   		<thead>
   			<tr>
   				<th>biz_order_header.id</th>
   				<th>订单详情行号</th>
   				<th>bom产品 kit</th>
   				<th>biz_sku_info.id</th>
   				<th>商品编号</th>
   				<th>商品名称</th>
   				<th>商品单价</th>
   				<th>采购数量</th>
   				<shiro:hasPermission name="biz:order:bizOrderDetail:edit"><th>操作</th></shiro:hasPermission>
   			</tr>
   		</thead>
   		<tbody>
   		<c:forEach items="${page.list}" var="bizOrderDetail">
   			<tr>
   				<td><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">
   					${bizOrderDetail.orderHeader.id}
   				</a></td>
   				<td>
   					${bizOrderDetail.lineNo}
   				</td>
   				<td>
   					${bizOrderDetail.pLineNo}
   				</td>
   				<td>
   					${bizOrderDetail.skuInfo.id}
   				</td>
   				<td>
   					${bizOrderDetail.partNo}
   				</td>
   				<td>
   					${bizOrderDetail.skuName}
   				</td>
   				<td>
   					${bizOrderDetail.unitPrice}
   				</td>
   				<td>
   					${bizOrderDetail.ordQty}
   				</td>
   				<shiro:hasPermission name="biz:order:bizOrderDetail:edit"><td>
       				<a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">修改</a>
   					<a href="${ctx}/biz/order/bizOrderDetail/delete?id=${bizOrderDetail.id}" onclick="return confirmx('确认要删除该订单详情吗？', this.href)">删除</a>
   				</td></shiro:hasPermission>
   			</tr>
   		</c:forEach>
   		</tbody>
   	</table>
   	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			/* 全选 全不选 */
			$('.chAll').live('click',function(){
                var choose=$(".boxs");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });

			$("#inputForm").validate({
				submitHandler: function(form){
                   /*if($("#addErrorInvo").val()==''){
                        $("#addError").css("display","inline-block")
                        return false;
                    }*/
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
				orderoffice();
			}
	 });
/* 选择经销店 */
function orderoffice(){
    var officeId=$("#officeId").val();
    $.ajax({
       type:"post",
       url:"${ctx}/biz/invoice/bizInvoiceDetail/invOrderHeader?officeId="+officeId,
       dataType:"json",
       success:function(data){
      			 console.log("已有数据"+data);
            var htmlOrder="<tbody>";
             $("#boxTbody").empty();
            $.each(data,function(index,order){
              $("#boxTbody").empty();
                $("<td/>").attr("src", order.id);
                htmlOrder+="<tr id='order"+order.id+"'><td><input type='checkbox' class='boxs' value="+order.id+">"+
                                "</td><td><input name='bizInvoiceDetailList["+index+"].orderHead.id' value='"+order.id+"' type='hidden'/>"+order.customer.name+"</td>"+
                                "<td>"+order.orderNum+"</td><td>"+order.totalDetail+"</td><td>"+order.totalExp+"</td><td>"+order.freight+"</td>"+
                                "<td>"+order.createDate+"</td></tr>";
            });
            htmlOrder+="</tbody>";
            $("#boxTbody").append(htmlOrder);
       }
   })
}
</script>
<script type="text/javascript">
<%--/* 订单列表 */--%>
function dial(){
	$.jBox("id:contentTablejBox",{
		   title:"订单列表",
		   width:700,
		   height:300,
		   buttons:{"确定":1,"取消":0},
		   buttonsFocus:1,
		   submit:function(v,h,f){
				if(v == 1){
					console.log("执行确定方法");
					 var checkID ="";
					 $(".boxs").each(function(){
						 if($(this).attr("checked")){
								var orderId= $(this).val();
								var trId="order"+$(this).val();
								var remov="<td><a href='#' onclick='removeItem("+orderId+")'>移除</a></td>";
								checkID+="<tr>"+$("#"+trId).html()+remov+"</tr>";
						 }
					 });
					 $("#orderHead_table").append(checkID);
				}
				return true;
		   }
	})
 }
 <%--移除--%>
 function removeItem(obj) {
 	console.log(obj+" 点击了移除");
}
</script>

	<title>发票抬头管理</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/invoice/bizInvoiceHeader/">发票抬头列表</a></li>
		<li class="active"><a href="${ctx}/biz/invoice/bizInvoiceHeader/form?id=${bizInvoiceHeader.id}">发票抬头<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit">${not empty bizInvoiceHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:invoice:bizInvoiceHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoiceHeader" action="${ctx}/biz/invoice/bizInvoiceHeader/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">经销店名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${bizInvoiceHeader.office.id}" labelName="office.name" labelValue="${bizInvoiceHeader.office.name}"
                    title="经销店" url="/sys/office/queryTreeList?type=6" onchange="orderoffice();"
								cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
                <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票抬头：</label>
			<div class="controls">
				<form:input path="invTitle" htmlEscape="false" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票类型：</label>
			<div class="controls">
				<form:select path="invType" class="input-xlarge required">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getDictList('invType')}" itemLabel="label" itemValue="value"
                        htmlEscape="false"/></form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票内容：</label>
			<div class="controls">
				<form:textarea path="invContent" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票数额：</label>
			<div class="controls">
				<form:input path="invTotal" htmlEscape="false" placeholder="0.0" readOnly="true" class="input-xlarge"/>
				<input name="invTotal" value="${entity.invTotal}" htmlEscape="false" type="hidden"/>
			</div>
		</div>
		<div class="control-group">
            <label class="control-label">选择订单：</label>
            <div class="controls">
                    <input type="button" onclick="dial();" value="订单列表" htmlEscape="false" class="input-xlarge required"/>
            </div>
        </div>
		<%--勾选订单列表显示隐藏--%>
		<div class="control-group">
		<label class="control-label">销售订单：</label>
		<div class="controls">
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th>经销店</th>
					<th>订单编号</th>
					<th>商品详情总价</th>
					<th>订单总费用</th>
					<th>运费</th>
					<th>创建时间</th>
					<th>操作</th>
				</tr>
				</thead>
				<tbody id="orderHead_table">
				<c:if test="${bizInvoiceHeader.id !=null && bizInvoiceHeader.id!=''}">
						<c:forEach items="${bizInvoiceHeader.bizInvoiceDetailList}" var="bizInvoice">
							<tr>
								<td>
										${bizInvoice.orderHead.customer.name}
								</td>
								<td>
										${bizInvoice.orderHead.orderNum}
								</td>
								<td>
										${bizInvoice.orderHead.totalDetail}
								</td>
								<td>
										${bizInvoice.orderHead.totalExp}
								</td>
								<td>
										${bizInvoice.orderHead.freight}
								</td>
								<td>
									<fmt:formatDate value="${bizInvoice.orderHead.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
								</td>
								<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit"><td>
									<a href="${ctx}/biz/invoice/bizInvoiceDetail/delete?id=${bizInvoice.id}&invoiceHeader.id=${bizInvoiceHeader.id}" onclick="return confirmx('确认要移除该订单吗？', this.href)">
										移除
								</a></td></shiro:hasPermission>
							</tr>
						</c:forEach>
				</c:if>
				</tbody>
			</table>
		</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

 <%--订单列表弹窗--%>
<div id="contentTablejBox" style="display: none">
    <table id="cheall" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <%--<th><input type="checkbox" class="chAll"/></th>--%>
			<th>选择</th>
            <th>经销店</th>
            <th>订单编号</th>
			<th>商品详情总价</th>
            <th>订单总费用</th>
            <th>运费</th>
            <th>创建时间</th>
        </tr>
        </thead>
        <tbody id="boxTbody"></tbody>
        <input type="hidden" id="tempString" name="tempString" />
    </table>
</div>

</body>
</html>
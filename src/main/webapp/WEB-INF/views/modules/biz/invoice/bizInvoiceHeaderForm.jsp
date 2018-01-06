<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票抬头管理</title>
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
			orderoffice();
	 });
/* 选择采购商 */
function orderoffice(){
    var officeId=$("#officeId").val();
    $.ajax({
       type:"post",
       url:"${ctx}/biz/invoice/bizInvoiceDetail/invOrderHeader?officeId="+officeId,
       dataType:"json",
       success:function(data){
            $("#boxTbody").empty();
            var htmlOrder="<tbody>";
                console.log(JSON.stringify(data)+"-测试 1- appendTo('#images')--");
            $.each(data,function(index,order){
                $("<td/>").attr("src", order.id);
                htmlOrder+="<tr><td><input type='checkbox'name='boxs' value="+order.id+">"+
                                "</td><td>"+order.customer.name+"</td>"+
                                "<td>"+order.orderNum+"</td><td>"+order.totalExp+"</td><td>"+order.freight+"</td>"+
                                "<td>"+order.createBy+"</td><td>"+order.createDate+"</td><td>"+
                                    "<a href='#'>修改</a>"+
                                " | "+
                                    "<a href='#'>删除</a>"+
                                "</td></tr>";
            });
            htmlOrder+="</tbody>";
            $("#boxTbody").append(htmlOrder);
       }
   })
}
</script>
<script type="text/javascript">
/* 订单列表 */
function dial(){
    $.jBox("id:contentTablejBox",{
               title:"订单列表",
               width:700,
               height:300,
               buttons:{"确定":1,"取消":0},
               buttonsFocus:1,
               submit:function(v,h,f){
                    if(v == 1){
                        alert("执行确定方法");
                        var bb="",
                        var temp="",
                        var a=$("input[name='boxs']");
                        for(var i=0;i<a.length;i++){
                            if(a[i].checked){
                                temp=a[i].val();
                                bb=bb+","+temp;
                            }
                        }
                        
                    }
                    return true;
               }
           })
    
 }
</script>
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
			<label class="control-label">采购商名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${bizInvoiceHeader.office.id}" labelName="office.name" labelValue="${bizInvoiceHeader.office.name}"
                    title="采购商" url="/sys/office/treeData?type=2" onchange="orderoffice();" cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
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
				<form:input path="invTotal" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
            <label class="control-label">选择订单：</label>
            <div class="controls">
                    <input type="button" onclick="dial();" value="订单列表" htmlEscape="false" class="input-xlarge required"/>
                    <%--<label class="error" id="addError" style="display:none;">必填信息</label>--%>
                    <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:invoice:bizInvoiceHeader:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

<c:if test="${bizInvoiceHeader.id !=null && bizInvoiceHeader.id!='' && bizInvoiceHeader.id!=0 }">
    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>发票行号</th>
            <th>订单编号</th>
            <th>采购商</th>
            <th>订单总费用</th>
            <th>运费</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>操作</th>
        </tr>
        </thead>
    </table>
</c:if>

 <%--订单列表--%>
<div id="contentTablejBox" style="display: none">
    <table id="cheall" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th><input type="checkbox" id="chAll"/></th>
            <th>采购商</th>
            <th>订单编号</th>
            <th>订单总费用</th>
            <th>运费</th>
            <th>创建人</th>
            <th>创建时间</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody id="boxTbody"></tbody>
        <input type="hidden" id="tempString" name="tempString" />
    </table>
</div>

</body>
</html>
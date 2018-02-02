<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    if($("#address2").val()==''){
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
                mechanism();
            }
		});
		 function mechanism(){
             var officeId=$('#officeId').val();
                $.ajax({
                    type:"post",
                    url:"${ctx}/sys/office/sysOfficeAddress/findAddrByOffice?office.id="+officeId,
                    dataType:"json",
                    success:function(data){
                     $("#province").empty();
                     $("#city").empty();
                     $("#region").empty();
                     $("#address").empty();
                     if(data==''){
                         console.log("数据为空");
                         $("#add1").css("display","none");
                         $("#add2").css("display","block");
                         $("#add3").css("display","none");
                     }else{
                         console.log("数据不为空显示");
                        $("#add1").css("display","block");
                        $("#add2").css("display","none");
                        $("#add3").css("display","block");
                           $.each(data,function(index,add){
                                 /*console.log(JSON.stringify(add)+"----");*/
                                if(add.deFault ==1){
                                    var option2=$("<option/>").text(add.bizLocation.province.name).val(add.bizLocation.province.id);
                                    $("#province").append(option2);
                                    var option3=$("<option/>").text(add.bizLocation.city.name).val(add.bizLocation.city.id);
                                    $("#city").append(option3);
                                    var option4=$("<option/>").text(add.bizLocation.region.name).val(add.bizLocation.region.id);
                                    $("#region").append(option4);
                                    $("#address").val(add.bizLocation.address);
                                 }
                           });
                           //当省份的数据加载完毕之后 默认选中第一个遍历出来的省份信息
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
		<li><a href="${ctx}/biz/invoice/bizInvoiceInfo/">发票信息列表</a></li>
		<li class="active"><a href="${ctx}/biz/invoice/bizInvoiceInfo/form?id=${bizInvoiceInfo.id}">发票信息<shiro:hasPermission name="biz:invoice:bizInvoiceInfo:edit">${not empty bizInvoiceInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:invoice:bizInvoiceInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoiceInfo" action="${ctx}/biz/invoice/bizInvoiceInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">机构名称：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${bizInvoiceInfo.office.id}" labelName="office.name" labelValue="${bizInvoiceInfo.office.name}"
					title="机构" url="/sys/office/treeData?type=2" onchange="mechanism();" cssClass="input-xlarge required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票抬头：</label>
			<div class="controls">
				<form:input path="invName" htmlEscape="false" maxlength="200" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">税号：</label>
			<div class="controls">
				<form:input path="taxNo" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开户行：</label>
			<div class="controls">
				<form:input path="bankName" htmlEscape="false" maxlength="200" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group" id="add1">
            <label class="control-label">邮寄地址；</label>
            <div class="controls">
                 <select id="province" class="input-medium" name="bizLocation.province.id" style="width:150px;text-align: center;">
                    <option value="-1">—— 省 ——</option></select>
                 <select id="city" class="input-medium" name="bizLocation.city.id" style="width:150px;text-align: center;">
                     <option value="-1">—— 市 ——</option></select>
                 <select id="region" class="input-medium" name="bizLocation.region.id" style="width:150px;text-align: center;">
                    <option value="-1">—— 区 ——</option></select>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
        <div class="control-group" id="add2" style="display:none">
            <label class="control-label">邮寄地址；</label>
            <div class="controls">
                <a href="${ctx}/sys/office/sysOfficeAddress/form?ohId=${bizOrderHeader.id}&flag=order">
                <input type="button" value="新增地址" htmlEscape="false" class="input-xlarge required"/></a>
                <label class="error" id="addError" style="display:none;">必填信息</label>
                <span class="help-inline"><font color="red">*</font></span>
            </div>
        </div>
        <div class="control-group" id="add3">
            <label class="control-label">详细地址；</label>
            <div class="controls">
                <input type="text" id="address" name="bizLocation.address" htmlEscape="false" class="input-xlarge required"/>
                <span class="help-inline"><font color="red">*</font> </span>
            </div>
        </div>
		<div class="control-group">
			<label class="control-label">电话：</label>
			<div class="controls">
				<form:input path="tel" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票账号：</label>
			<div class="controls">
				<form:input path="account" htmlEscape="false" maxlength="30" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:invoice:bizInvoiceInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
		</div>
	</form:form>
</body>
</html>
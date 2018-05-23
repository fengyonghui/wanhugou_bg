<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购商供应商关联关系管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
            $("#inputForm").validate({
            submitHandler: function(form){
                var custOffice = $("#custOfficeId").val();
                var vendOffice = $("#vendOfficeId").val();
				    $.ajax({
						type:"post",
						url:"${ctx}/biz/order/bizPurchaserVendor/findPurchaserVendor",
						data:{purchaser:custOffice,vendor:vendOffice},
						success:function (data) {
							if (data == 'error') {
							    alert("该采购商和供应商已经建立关联关系");
                            }else {
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizPurchaserVendor/">采购商供应商关联关系列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizPurchaserVendor/form?id=${bizPurchaserVendor.id}">采购商供应商关联关系<shiro:hasPermission name="biz:order:bizPurchaserVendor:edit">${not empty bizPurchaserVendor.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizPurchaserVendor:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizPurchaserVendor" action="${ctx}/biz/order/bizPurchaserVendor/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<sys:treeselect id="vendOffice" name="vendor.id" value="${bizPurchaserVendor.vendor.id}" labelName="vendor.name"
								labelValue="${bizPurchaserVendor.vendor.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="供应商"  url="/sys/office/queryTreeList?type=7" cssClass="input-medium required" dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购商：</label>
			<div class="controls">
				<sys:treeselect id="custOffice" name="purchaser.id" value="${bizPurchaserVendor.purchaser.id}"  labelName="customer.name"
								labelValue="${bizPurchaserVendor.purchaser.name}" notAllowSelectParent="true"
								title="采购商"  url="/sys/office/queryTreeList?type=6"
								cssClass="input-medium required"
								allowClear="true"  dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizPurchaserVendor:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
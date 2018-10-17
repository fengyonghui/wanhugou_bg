<%@ taglib prefix="s" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
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

		});
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active">
		<a href="">产品合并</a>
	</li>
</ul><br/>
<form:form id="inputForm" action="${ctx}/biz/product/bizProductInfoV3/mergeSpu" method="post" class="form-horizontal">

	<div class="control-group">
		<label class="control-label">货号：</label>
		<div class="controls">
			<input name="itemNo" type="text" class="input-xlarge" value=""/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">请选择供应商：</label>
		<div class="controls">
				<sys:treeselect id="office" name="vendId" value="${entity.office.id}" labelName="office.name"
								labelValue="${entity.office.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="供应商" url="/sys/office/queryTreeList?type=7" extId="${office.id}"
								cssClass="input-xlarge required"
								allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">需要的产品ID：</label>
		<div class="controls">
			<input name="itemNo" type="text" class="input-xlarge" value=""/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">被替换的产品ID：</label>
		<div class="controls">
			<input name="itemNo" type="text" class="input-xlarge" value=""/>
		</div>
	</div>
	<div class="form-actions">
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="合并"/>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
	</div>
</form:form>
</body>
</html>
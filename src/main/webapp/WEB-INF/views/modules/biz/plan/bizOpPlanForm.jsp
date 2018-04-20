<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<html>
<head>
	<title>运营计划管理</title>
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
			if($("#id").val!=null && $("#id").val!=""){
			    centerOffUser();
			}
		});



    </script>
	<script type="text/javascript">
    //用于选择采购中心查询采购专员
    function centerOffUser() {
        $("#userSelect").empty();
        	if($("#id").val()!=null && $("#id").val()!='' && ${bizOpPlan.objectName2 !=null}){
        		$("#userSelect").append("<option value='${bizOpPlan.user.id}' selected = 'selected'>${bizOpPlan.objectName2}</option>");
        	}
        var centId = $("#centerOfficeId").val();
        $.ajax({
            type:"post",
            url:"${ctx}/sys/user/bizOpPlanUser?officeId="+centId,
            success:function (data) {
                $("#userSelect").append("<option value=''> ===请选择=== </option>");
                $.each(data,function(index,items) {
                    $("#userSelect").append("<option value='"+items.id+"'>"+items.name+"</option>");
                });
            }
        });
    }


    </script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/biz/plan/bizOpPlan/">运营计划列表</a></li>
	<li class="active"><a href="${ctx}/biz/plan/bizOpPlan/form?id=${bizOpPlan.id}">运营计划<shiro:hasPermission
			name="biz:plan:bizOpPlan:edit">${not empty bizOpPlan.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
			name="biz:plan:bizOpPlan:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOpPlan" action="${ctx}/biz/plan/bizOpPlan/save" method="post"
		   class="form-horizontal">
	<form:hidden path="id"/>
	<sys:message content="${message}"/>
	<div class="control-group">
		<label class="control-label">采购中心：</label>
		<div class="controls">
			<sys:treeselect id="centerOffice" name="objectId" value="${bizOpPlan.objectId}"
							labelName="objectName1" labelValue="${bizOpPlan.objectName1}" notAllowSelectParent="true"
							onchange="centerOffUser();"
							title="采购中心" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type},${OfficeTypeEnum.WITHCAPITAL.type},${OfficeTypeEnum.NETWORKSUPPLY.type}" cssClass="input-xlarge required"
							dataMsgRequired="必填信息">
			</sys:treeselect>
			<span class="help-inline"><font color="red">*</font></span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">采购专员：</label>
		<div class="controls">
			<c:if test="${empty bizOpPlan.id}">
				<select id="userSelect" name="user.id" class="input-xlarge">
					<option value=""> ===请选择=== </option>
				</select>
			</c:if>
			<c:if test="${not empty bizOpPlan.id && not empty bizOpPlan.objectName2}">
				<select id="userSelect" name="user.id" class="input-xlarge">
					<option value="${bizOpPlan.user.id}" selected="selected">${bizOpPlan.objectName2}</option>
				</select>
			</c:if>
			<c:if test="${not empty bizOpPlan.id && empty bizOpPlan.objectName2}">
				<select id="userSelect" name="user.id" class="input-xlarge">
					<option value=""> ===请选择=== </option>
				</select>
			</c:if>
			<span class="help-inline">此处选择就代表添加的是采购专员,不选择采购专员代表添加的是采购中心</span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">计划所在年份：</label>
		<div class="controls">
			<form:input path="year" htmlEscape="false" onkeyup="value=value.replace(/[^\d]/g,'') " maxlength="5"
						class="input-xlarge required"/>
			<span class="help-inline"><font>(在选择年计划时， 月份为0，日为0)</font><font color="red"> *</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">计划所在月份：</label>
		<div class="controls">
			<form:input path="month" htmlEscape="false" onkeyup="value=value.replace(/[^\d]/g,'') " maxlength="3"
						class="input-xlarge required"/>
			<span class="help-inline"><font>(选择月计划时，日为0)</font><font color="red"> *</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">计划所在日：</label>
		<div class="controls">
			<form:input path="day" htmlEscape="false" onkeyup="value=value.replace(/[^\d]/g,'') " maxlength="3"
						class="input-xlarge required"/>
			<span class="help-inline"><font color="red"> *</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">总额：</label>
		<div class="controls">
			<form:input path="amount" htmlEscape="false" class="input-xlarge required"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="form-actions">
		<shiro:hasPermission name="biz:plan:bizOpPlan:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
																   value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</form:form>
</body>
</html>
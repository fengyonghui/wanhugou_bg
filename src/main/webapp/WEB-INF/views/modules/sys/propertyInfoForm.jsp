<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统属性管理</title>
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
			var i=0;
			//var propValue = $("#propValues");
			$("#addPropValue").click(function () {		
			var t=	$(this).attr("title");
			if(t!=''){
			    i=parseInt(t);
			}
				if($("#propValueList"+i).val()==''){
					
					alert('属性不能为空');
					return false;
				}
					
		    	i++;
			$("#propValues").append("<input type='text' id='propValueList"+i+"' name=\"propValueList["+i+"].value\"  maxlength=\"512\" class=\"input-small\"/>")
           
			return true;
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/propertyInfo/">系统属性列表</a></li>
		<li class="active"><a href="${ctx}/sys/propertyInfo/form?id=${propertyInfo.id}">系统属性<shiro:hasPermission name="sys:propertyInfo:edit">${not empty propertyInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:propertyInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="propertyInfo" action="${ctx}/sys/propertyInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">属性名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="30" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">属性描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" maxlength="200" class="input-xlarge"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>


		<div class="control-group">
			<label class="control-label">属性值：</label>
			<div class="controls">
				<span id="propValues">

					<c:if test="${propertyInfo.propValueList==null}">
						<input type="text" id="propValueList0" name="propValueList[0].value" h maxlength="512" class="input-small"/>
					</c:if>
				<c:forEach items="${propertyInfo.propValueList}" var = "propValue"  varStatus="status">
					<input  type="text" name="propValueList[${status.index}].value" value="${propValue.value}" id="propValueList0" htmlEscape="false" maxlength="512" class="input-small" />
					<c:if test="${status.last}">
						<c:set var="j" value="${status.index}"></c:set>
					</c:if>
				</c:forEach>
				</span>
				<button id="addPropValue" type="button" title="${j}"  class="btn btn-default" >
					<span class="icon-plus"></span>
				</button>
			</div>
		</div>


		<div class="form-actions">
			<shiro:hasPermission name="sys:propertyInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分类属性中间表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		    if ($("#id").val()!='') {
                ajaxGetAttributeInfo($("#id").val());
			}
            /**
			 * 根据分类ID获取属性
             */
            function ajaxGetAttributeInfo(vaId) {
				$.ajax({
					type:"post",
					url:"${ctx}/biz/product/bizVarietyAttr/findAttribute?id="+vaId,
					success:function (data) {
						$.each(data,function (index, item) {
						    $("input[name='attributeIds']").each(function () {
                                if (parseInt(item.attributeInfo.id)==parseInt($(this).val())) {
                                    $(this).attr("checked","checked");
								}
                            });
                        });
                    }
				});
            }
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
		<li><a href="${ctx}/biz/product/bizVarietyAttr/">分类属性中间表列表</a></li>
		<li class="active"><a href="${ctx}/biz/product/bizVarietyAttr/form?id=${bizVarietyAttr.id}">分类属性中间表<shiro:hasPermission name="biz:product:bizVarietyAttr:edit">${not empty bizVarietyAttr.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:product:bizVarietyAttr:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizVarietyAttr" action="${ctx}/biz/product/bizVarietyAttr/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">分类：</label>
			<div class="controls">
				<from:select path="varietyInfo.id" cssClass="input-xlarge required">
					<from:option label="请选择分类" value=""/>
					<from:options items="${varietyInfoList}" itemValue="id" itemLabel="name"/>
				</from:select>
				<%--<form:input path="varietyInfo.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>--%>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">属性：</label>
			<div class="controls">
				<c:forEach items="${attributeInfoList}" var="attribute">
					<input name="attributeIds" type="checkbox" value="${attribute.id}" />${attribute.name}
				</c:forEach>
				<%--<form:input path="attributeInfo.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>--%>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:product:bizVarietyAttr:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
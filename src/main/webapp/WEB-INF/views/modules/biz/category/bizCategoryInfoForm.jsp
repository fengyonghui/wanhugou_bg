<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品类别管理</title>
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
			var j=0;
			$("#addPropValue").click(function () {
			    i++;
				$("#propValues").append("<input  name=\"catePropertyInfoList[0].catePropValues["+i+"]\" type='text' maxlength=\"512\" class=\"input-small \"/>")
            });
			$("#fillin").focus(function () {
                $("#propValues").show();

            });
            $("#fillout").focus(function () {
                $("#propValues").hide();

            });
            $("#addProperty").click(function () {
                j++;
				$("#cateProperty").append("<div class=\"controls\">\n" +
                    "<input name=\"catePropertyInfoList["+j+"].name\"  maxlength=\"512\" class=\"input-small\"/>\n" +
                    "<button id=\"addProperty\" type=\"button\" class=\"btn btn-default\">\n" +
                    "<span class=\"icon-plus\"></span>\n" +
                    "</button>\n" +
                    "<label>属性值:</label>是否填写<input type=\"radio\" id=\"fillin\" name=\"fillValue\" value=\"1\" checked=\"checked\"> 是 <input type=\"radio\" id=\"fillout\" name=\"fillValue\" value=\"0\"> 否\n" +
                    "<span id=\"propValues\">\n" +
                    "<input  path=\"catePropertyInfoList["+j+"].catePropValues[0]\"  maxlength=\"512\" class=\"input-small \"/>\n" +
                    "</span>\n" +
                    "<button id=\"addPropValue\" type=\"button\" class=\"btn btn-default\">\n" +
                    "<span class=\"icon-plus\"></span>\n" +
                    "</button>\n" +
                    "</div>")
           	 })
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/category/bizCategoryInfo/">商品类别列表</a></li>
		<li class="active"><a href="${ctx}/biz/category/bizCategoryInfo/form?id=${bizCategoryInfo.id}">商品类别<shiro:hasPermission name="biz:category:bizCategoryInfo:edit">${not empty bizCategoryInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:category:bizCategoryInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>

	<%--@elvariable id="bizCategoryInfo" type="com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo"--%>
	<form:form id="inputForm" modelAttribute="bizCategoryInfo" action="${ctx}/biz/category/bizCategoryInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="parent.id"/>
		<sys:message content="${message}"/>		

	<%--	<div class="control-group">
			<label class="control-label">上级分类:</label>
			<div class="controls">
				<sys:treeselect id="category" name="parent.id" value="${bizCategoryInfo.parent.id}" labelName="parent.name" labelValue="${bizCategoryInfo.parent.name}"
								title="分类" url="/biz/category/bizCategoryInfo/treeData" extId="${bizCategoryInfo.id}" cssClass="" allowClear="${bizCategoryInfo.currentUser.admin}"/>
			</div>
		</div>--%>

		<div class="control-group">
			<label class="control-label">分类名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分类描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" maxlength="512" class="input-xlarge "/>
			</div>
		</div>

		<div id="cateProperty" class="control-group">
			<label class="control-label">分类属性：</label>
			<div  class="controls">
				<form:input path="catePropertyInfoList[0].name" htmlEscape="false" maxlength="512" class="input-small"/>
				<button id="addProperty" type="button" class="btn btn-default">
					<span class="icon-plus"></span>
				</button>
				<label>属性值:</label>是否填写<input type="radio" id="fillin" name="fillValue" value="1" checked="checked"> 是 <input type="radio" id="fillout" name="fillValue" value="0"> 否
				<span id="propValues">
					<form:input  path="catePropertyInfoList[0].catePropValues[0]" htmlEscape="false" maxlength="512" class="input-small "/>
				</span>
				<button id="addPropValue" type="button" class="btn btn-default">
						<span class="icon-plus"></span>
				</button>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:category:bizCategoryInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
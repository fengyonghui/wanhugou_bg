<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>找货定制管理</title>
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
		<li><a href="${ctx}/biz/sku/bizSearch/">找货定制列表</a></li>
		<li class="active"><a href="${ctx}/biz/sku/bizSearch/form?id=${bizSearch.id}">找货定制<shiro:hasPermission name="biz:sku:bizSearch:edit">${not empty bizSearch.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:sku:bizSearch:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizSearch" action="${ctx}/biz/sku/bizSearch/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">商品编码：</label>
			<div class="controls">
				<form:input path="partNo" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分类名称：</label>
			<%--<div class="controls">
				<form:input path="cateId" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>--%>
			<div class="controls">
				<sys:treeselect id="cateId" name="cateId.id" value="${bizSearch.cateId.id}" labelName="cateId.name" labelValue="${bizSearch.cateId.name}"
								title="分类名称" url="/biz/category/bizCategoryInfo/treeData"  cssClass="required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">找货名称：</label>
			<div class="controls">
				<form:input path="cateName" htmlEscape="false" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">材质属性：</label>
			<div class="controls">
				<form:input path="qualityId" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> <font>(1--5)</font></span>
			</div>
			<%--<div class="controls">--%>
				<%--<sys:treeselect id="qualityId" name="qualityId" value="${bizSearch.qualityId}" labelName="qualityId" labelValue="${bizSearch.qualityId}"--%>
								<%--title="材质属性" url="/sys/cate/treeData" cssClass="required" allowClear="true" notAllowSelectParent="true"/>--%>
				<%--<span class="help-inline"><font color="red">*</font> </span>--%>
			<%--</div>--%>
		</div>
		<div class="control-group">
			<label class="control-label">颜色：</label>
			<div class="controls">
				<form:input path="color" htmlEscape="false" maxlength="10" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">规格：</label>
			<div class="controls">
				<form:input path="standard" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务状态 ：</label>
			<%--<div class="controls">--%>
				<%--<form:input path="businessStatus" htmlEscape="false" maxlength="4" class="input-xlarge required"/>--%>
				<%--<span class="help-inline"><font color="red">*</font> <font> 0：关闭 1：开放 2：取消</font></span>--%>
			<%--</div>--%>
			<form:select path="businessStatus" class="input-xlarge ">
				<form:option value="" label="请选择"/>
				<form:options items="${fns:getDictList('businessStatus')}" itemLabel="label" itemValue="value"
							  htmlEscape="false"/>
			</form:select>
		</div>
		<div class="control-group">
			<label class="control-label">期望到货时间：</label>
			<div class="controls">
				<input name="sendTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${bizSearch.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${bizSearch.user.id}" labelName="user.name" labelValue="${bizSearch.user.name}"
					title="用户" url="/sys/office/queryTreeList?type=6" cssClass="required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">期望最低售价：</label>
			<div class="controls">
				<form:input path="minPrice" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">期望最高价：</label>
			<div class="controls">
				<form:input path="maxPrice" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">数量：</label>
			<div class="controls">
				<form:input path="amount" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:input path="comment" htmlEscape="false" maxlength="255" class="input-xlarge "/>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:sku:bizSearch:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
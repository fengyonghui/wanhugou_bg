<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>货架信息管理</title>
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
		<li><a href="${ctx}/biz/shelf/bizOpShelfInfo/">货架信息列表</a></li>
		<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfInfo/form?id=${bizOpShelfInfo.id}">货架信息<shiro:hasPermission name="biz:shelf:bizOpShelfInfo:edit">${not empty bizOpShelfInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizOpShelfInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOpShelfInfo" action="${ctx}/biz/shelf/bizOpShelfInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">货架名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货架描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shelf:bizOpShelfInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
			<th>商品名称</th>
			<th>采购中心</th>
			<th>上架数量(个)</th>
			<th>原价(元)</th>
			<th>现价(元)</th>
			<th>最低销售数量(个)</th>
			<th>最高销售数量(个)</th>
			<th>显示次序</th>
			<th>上架人</th>
			<th>上架时间</th>
			<th>下架人</th>
			<th>下架时间</th>
			<th>创建人</th>
			<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><th>操作</th></shiro:hasPermission>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${bizOpShelfInfo.opShelfSkusList}" var="bizOpShelfSku">
			<tr>
				<td><a href="${ctx}/biz/shelf/bizOpShelfSku/form?id=${bizOpShelfSku.id}">
						${bizOpShelfSku.skuInfo.name}
				</a></td>
				<td>
				<c:choose>
					<c:when test="${bizOpShelfSku.centerOffice.id == 0}">
						平台商品
					</c:when>
					<c:otherwise>
								${bizOpShelfSku.centerOffice.name}
					</c:otherwise>
				</c:choose>
				</td>
				<td>
						${bizOpShelfSku.shelfQty}
				</td>
				<td>
						${bizOpShelfSku.orgPrice}
				</td>
				<td>
						${bizOpShelfSku.salePrice}
				</td>
				<td>
						${bizOpShelfSku.minQty}
				</td>
				<td>
						${bizOpShelfSku.maxQty}
				</td>
				<td>
						${bizOpShelfSku.priority}
				</td>
				<td>
						${bizOpShelfSku.shelfUser.name}
				</td>
				<td>
					<fmt:formatDate value="${bizOpShelfSku.shelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
						${bizOpShelfSku.unshelfUser.name}
				</td>
				<td>
					<fmt:formatDate value="${bizOpShelfSku.unshelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
						${bizOpShelfSku.createBy.name}
				</td>
				<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><td>
					<a href="${ctx}/biz/shelf/bizOpShelfSku/form?id=${bizOpShelfSku.id}">修改</a>
					<a href="${ctx}/biz/shelf/bizOpShelfSku/delete?id=${bizOpShelfSku.id}&shelfSign=1" onclick="return confirmx('确认要删除该商品上架吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
    <c:if test="${bizOpShelfInfo != null and bizOpShelfInfo.id != null}">
	<div class="form-actions">
		<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><input type="button"
																   onclick="javascript:window.location.href='${ctx}/biz/shelf/bizOpShelfSku/form?id=${bizOpShelfSku.id}&opShelfInfo.id=${bizOpShelfInfo.id}';"
																   class="btn btn-primary"
																   value="sku上架商品添加"/></shiro:hasPermission>
	</div>
    </c:if>
</body>
</html>
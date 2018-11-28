<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费物流线路管理</title>
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
		<li><a href="${ctx}/biz/order/bizServiceLine/">服务费物流线路列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizServiceLine/form?id=${bizServiceLine.id}">服务费物流线路<shiro:hasPermission name="biz:order:bizServiceLine:edit">${not empty bizServiceLine.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizServiceLine:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizServiceLine" action="${ctx}/biz/order/bizServiceLine/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="province" value="${bizServiceCharge.serviceLine.province.code}" type="hidden"/>
		<input id="city" value="${bizServiceCharge.serviceLine.city.code}" type="hidden"/>
		<input id="region" value="${bizServiceCharge.serviceLine.region.code}" type="hidden"/>
		<input id="toProvince" value="${bizServiceCharge.serviceLine.toProvince.code}" type="hidden"/>
		<input id="toCity" value="${bizServiceCharge.serviceLine.toCity.code}" type="hidden"/>
		<input id="toRegion" value="${bizServiceCharge.serviceLine.toRegion.code}" type="hidden"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">发货路线</label>
			<div class="controls">
				从
				<form:select id="province0" path="serviceLine.province.code" class="input-medium required">
					<option>===省====</option>
				</form:select>
				<form:select id="city0" path="serviceLine.city.code" class="input-medium required">
					<option>===市====</option>
				</form:select>
				<form:select id="region0" path="serviceLine.region.code" class="input-medium required">
					<option>===县/区====</option>
				</form:select>
				<br>
				至
				<form:select id="toProvince0" path="serviceLine.toProvince.code" class="input-medium required">
					<option>===省====</option>
				</form:select>
				<form:select id="toCity0" path="serviceLine.toCity.code" class="input-medium required">
					<option>===市====</option>
				</form:select>
				<form:select id="toRegion0" path="serviceLine.toRegion.code" class="input-medium required">
					<option>===县/区====</option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否启用：</label>
			<div class="controls">
				<c:if test="${bizServiceLine.usable != 0}">
					<font color="green">已启用</font>
				</c:if>
				<c:if test="${bizServiceLine.usable == 0}">
					<form:radiobutton path="usable" value="${variId}"/>
				</c:if>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizServiceLine:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<div class="control-group">
		<div class="controls">
			<table>
				<c:forEach items="${serviceChargeList}" var="serviceCharge">
					<tr>
						<td><c:if test="${varietyInfo.id == 0}">非拉杆箱</c:if></td>
						<td>
							<select name="serviceMode" class="input-medium required">
								<option value="" label="请选择"/>
								<c:forEach items="${fns:getDictList('service_cha')}" var="dict">
									<option value="${dict.value}" label="${dict.label}"/>
								</c:forEach>
							</select>
						</td>
						<td>
							<input name="servicePrice" value="${serviceCharge.servicePrice}" class="input-mini required" type="number" min="0"/>&nbsp;元/支(元/套)
						</td>
						<td>
							<input type="hidden" name="chargeId" value="${serviceCharge.id}"/>
							<a href="#" onclick="updateCharge(this)">修改</a>

						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
	<title>订单信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(document).ready(function() {
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
	<li class="active"><a href="${ctx}/biz/order/bizOrderHeaderUnline/">线下支付流水列表</a></li>
	<li class="active"><a href="${ctx}/biz/order/bizOrderHeaderUnline/offLineform?orderHeader.id=${bizOrderHeaderUnline.orderHeader.id}">线下支付流水列表<shiro:hasPermission
			name="biz:order:bizOrderHeaderOffLine:edit">${not empty bizOrderHeaderUnline.id?'修改':'添加'}</shiro:hasPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeaderUnline" action="${ctx}/biz/order/bizOrderHeaderUnline/saveOffLine" method="post"
		   class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="orderHeader.id"/>
	<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">订单号：</label>
			<div class="controls">
					<form:input readonly="true" path="orderHeader.orderNum"/>
			</div>
		</div>
	<c:if test="${bizOrderHeaderUnline.id != null}">
		<div class="control-group">
			<label class="control-label">流水号：</label>
			<div class="controls">
				<form:input disabled="true" path="serialNum"/>
			</div>
		</div>
	</c:if>
	<div class="control-group">
		<label class="control-label">线下支付凭证：</label>
		<div class="controls">
			<form:hidden path="imgUrls" htmlEscape="false" maxlength="255" class="input-xlarge"/>
			<sys:ckfinder input="imgUrls" type="images" uploadPath="/offLine/info" selectMultiple="true" maxWidth="100"
						  maxHeight="100"/>
		</div>
	</div>
		<%--<input name="photos" id="photos" cssStyle="display: none"/>--%>
		<div class="control-group">
			<label class="control-label">线下付款金额：</label>
			<div class="controls">
                <form:input id="unlinePayMoney" class="input-mini required" path="unlinePayMoney"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<c:if test="${bizOrderHeaderUnline.id != null}">
			<div class="control-group">
				<label class="control-label">实收金额：</label>
				<div class="controls">
					<form:hidden id="realMoney" disabled="true" class="input-mini" path="realMoney"/>
				</div>
			</div>
		</c:if>
		<c:if test="${bizOrderHeaderUnline.id != null}">
			<div class="control-group">
				<label class="control-label">流水状态：</label>
				<div class="controls">
					<span style="font-size: large; font-style: initial; color: red; width: available; font-family: 楷体">${fns:getDictLabel(bizOrderHeaderUnline.bizStatus, 'biz_order_unline_bizStatus', '未知状态')}</span>
					<%--<input id="bizStatus" type="button" class="btn btn-primary" value="${fns:getDictLabel(bizOrderHeaderUnline.bizStatus, 'biz_order_unline_bizStatus', '未知状态')}"/>--%>
				</div>
			</div>
		</c:if>
		<div class="form-actions">
				<shiro:hasPermission name="biz:order:bizOrderHeaderOffLine:edit">
					<input id="confirm" class="btn btn-primary" type="submit" value="保存"/>
				</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
		</div>
</form:form>
</body>
</html>

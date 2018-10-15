<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
	<title>订单信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(document).ready(function() {
            $("#realMoney").val($("#unlinePayMoney").val());
            $("#inputForm").validate({
                submitHandler: function(form){
                    if (confirm("请再次确认实收金额是否正确？")==true) {
                        loading('正在提交，请稍等...');
                        form.submit();
                    }
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
            $("#back").click(function () {
                var unlineId = $("#id").val();
                if (confirm("请再次确认是否驳回？")==true) {
                    $.ajax({
                        type:"post",
                        url:"${ctx}/biz/order/bizOrderHeaderUnline/changeOrderReceive",
						data:{id:unlineId},
                        success:function (data) {
                            if (data == 'ok') {
                                window.location.href="${ctx}/biz/order/bizOrderHeaderUnline?id="+unlineId;
                            }
                        }
                    });
                }else {
                    return false;
                }
            });
        });
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/biz/order/bizOrderHeaderUnline/">线下支付流水列表</a></li>
	<li class="active"><a href="${ctx}/biz/order/bizOrderHeaderUnline/form?id=${bizOrderHeaderUnline.id}">线下支付流水列表<shiro:hasPermission
			name="biz:order:bizOrderHeaderUnline:edit">${not empty bizOrderHeaderUnline.id?'修改':'添加'}</shiro:hasPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeaderUnline" action="${ctx}/biz/order/bizOrderHeaderUnline/save" method="post"
		   class="form-horizontal">
	<form:hidden path="id"/>
	<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">订单号：</label>
			<div class="controls">
					<form:input disabled="true" path="orderHeader.orderNum"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">流水号：</label>
			<div class="controls">
				<form:input disabled="true" path="serialNum"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">单据凭证：</label>
			<div id="credential" class="controls">
				<c:forEach items="${imgUrlList}" var="imgUrl">
					<a href="${imgUrl}" target="view_window">
					<img src="${imgUrl}" style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/></a>
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">线下付款金额：</label>
			<div class="controls">
                <form:input id="unlinePayMoney" readonly="true" class="input-mini" path="unlinePayMoney"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">实收金额：</label>
			<div class="controls">
				<form:input id="realMoney" class="input-mini" path="realMoney"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">流水状态：</label>
			<div class="controls">
				<span style="font-size: large; font-style: initial; color: red; width: available; font-family: 楷体">${fns:getDictLabel(bizOrderHeaderUnline.bizStatus, 'biz_order_unline_bizStatus', '未知状态')}</span>
				<%--<input id="bizStatus" type="button" class="btn btn-primary" value="${fns:getDictLabel(bizOrderHeaderUnline.bizStatus, 'biz_order_unline_bizStatus', '未知状态')}"/>--%>
			</div>
		</div>
		<div class="form-actions">
				<shiro:hasPermission name="biz:order:bizOrderHeaderUnline:edit">
                    <c:if test="${bizOrderHeaderUnline.source == 'examine'}">
                        <input id="confirm" class="btn btn-primary" type="submit" value="确认"/>&nbsp;
                        <input id="back" class="btn btn-primary" type="button" value="驳回"/>
                    </c:if>
				</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
		</div>
</form:form>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>支付申请管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    var totalVal = $("#total").val();
                    if ($String.isNullOrBlank(totalVal) || Number(totalVal) <= 0) {
                        alert("付款金额输入不正确，请重新输入！")
                    }
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
	<style type="text/css">
		.help_step_box{background: rgba(255, 255, 255, 0.45);overflow:hidden;border-top:1px solid #FFF;width: 100%}
		.help_step_item{margin-right: 30px;width:200px;border:1px #3daae9 solid;float:left;height:150px;padding:0 25px 0 45px;cursor:pointer;position:relative;font-size:14px;font-weight:bold;}
		.help_step_num{width:19px;height:120px;line-height:100px;position:absolute;text-align:center;top:18px;left:10px;font-size:16px;font-weight:bold;color: #239df5;}
		.help_step_set{background: #FFF;color: #3daae9;}
		.help_step_set .help_step_left{width:8px;height:100px;position:absolute;left:0;top:0;}
		.help_step_set .help_step_right{width:8px;height:100px; position:absolute;right:-8px;top:0;}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/">支付申请列表</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizPoPaymentOrder" action="${ctx}/biz/po/bizPoPaymentOrder/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="poHeaderId"/>
		<form:hidden path="orderType"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">订单总金额：</label>
			<div class="controls">
				<input class="totalDetail" value="${totalDetailResult}" htmlEscape="false" disabled="disabled" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款金额：</label>
			<div class="controls">
				<form:input path="total" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" maxlength="200" class="input-xlarge "/>
			</div>
		</div>

		<c:if test="${fn:length(auditList) > 0}">
			<div class="control-group">
				<label class="control-label">审核流程：</label>
				<div class="controls help_wrap">
					<div class="help_step_box fa">
						<c:forEach items="${auditList}" var="v" varStatus="stat">
							<c:if test="${v.id != processId}" >
								<div class="help_step_item">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									处理人:${v.user.name}<br/><br/>
									批注:${v.description}<br/><br/>
									状态:
										${v.paymentOrderProcess.name}
									<br/>
									<fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
									<div class="help_step_right"></div>
								</div>
							</c:if>
							<c:if test="${v.id == processId}">
								<div class="help_step_item help_step_set">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									当前状态:
										${v.paymentOrderProcess.name}
									<br/>
									<fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
									<div class="help_step_right"></div>
									<input type="hidden" value="${v.type}" id="currentJoType"/>
								</div>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</div>
		</c:if>

		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<html>
<head>
	<title>佣金付款表管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.help_step_box {
			background: rgba(255, 255, 255, 0.45);
			overflow: hidden;
			border-top: 1px solid #FFF;
			width: 100%
		}

		.help_step_item {
			margin-right: 30px;
			width: 200px;
			border: 1px #3daae9 solid;
			float: left;
			height: 150px;
			padding: 0 25px 0 45px;
			cursor: pointer;
			position: relative;
			font-size: 14px;
			font-weight: bold;
		}

		.help_step_num {
			width: 19px;
			height: 120px;
			line-height: 100px;
			position: absolute;
			text-align: center;
			top: 18px;
			left: 10px;
			font-size: 16px;
			font-weight: bold;
			color: #239df5;
		}

		.help_step_set {
			background: #FFF;
			color: #3daae9;
		}

		.help_step_set .help_step_left {
			width: 8px;
			height: 100px;
			position: absolute;
			left: 0;
			top: 0;
		}

		.help_step_set .help_step_right {
			width: 8px;
			height: 100px;
			position: absolute;
			right: -8px;
			top: 0;
		}
	</style>
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
		<li><a href="${ctx}/biz/order/bizCommission/">佣金付款表列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizCommission/save?id=${bizCommission.id}">佣金付款表<shiro:hasPermission name="biz:order:bizCommission:edit">${not empty bizCommission.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizCommission:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizCommission" action="${ctx}/biz/order/bizCommission/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="vendId" type="hidden" value="${entity.sellerId}"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">佣金总金额：</label>
			<div class="controls">
				<form:input path="totalCommission" readonly="true" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款金额：</label>
			<div class="controls">
				<form:input path="payTotal" htmlEscape="false" value="${entity.totalCommission}" readonly="true" class="input-xlarge"/>
			</div>
		</div>


		<div id="cardNumber" class="control-group" >
			<label class="control-label">代销商卡号：</label>
			<div class="controls">
				<input id="cardNumberInput" readonly="readonly" value="${entity.customerInfo.cardNumber}" htmlEscape="false" maxlength="30"
					   class="input-xlarge "/>
			</div>
		</div>
		<div id="payee" class="control-group" >
			<label class="control-label">代销商收款人：</label>
			<div class="controls">
				<input id="payeeInput" readonly="readonly" value="${entity.customerInfo.payee}" htmlEscape="false" maxlength="30"
					   class="input-xlarge "/>
			</div>
		</div>
		<div id="bankName" class="control-group" >
			<label class="control-label">代销商开户行：</label>
			<div class="controls">
				<input id="bankNameInput" readonly="readonly" value="${entity.customerInfo.bankName}" htmlEscape="false" maxlength="30"
					   class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">支付凭证：</label>
			<div class="controls">
				<c:forEach items="${entity.imgList}" var="v">
					<a target="_blank" href="${v.imgServer}${v.imgPath}"><img style="width: 100px" src="${v.imgServer}${v.imgPath}"/></a>
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款时间：</label>
			<div class="controls">
				<input name="payTime" id="payTime" type="text" readonly="readonly" maxlength="20"
					   class="input-medium Wdate required"
					   value="<fmt:formatDate value="${entity.payTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
						<c:if test="${entity.str == 'createPay'}"> onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"</c:if>
				/>
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
							<c:if test="${v.current != 1}" >
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
							<c:if test="${v.current == 1}">
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
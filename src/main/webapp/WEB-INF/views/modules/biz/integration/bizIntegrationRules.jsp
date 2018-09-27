<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>万户币规则</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		    //补充注册送信息
            $.ajax({
                url:"${ctx}/biz/integration/bizIntegrationActivity/systemActivity?code=XDK",
                type:"get",
                data:'',
                contentType:"application/json;charset=utf-8",
                success:function(data){
                    $("#integrationId").val(data.id);
                    $("#integrationNum").val(data.integrationNum);
                }
            });
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
		function radioDefault() {
			   alert("请选择指定用户！");
        }

	</script>
</head>
<body>

	<form:form id="inputForm" modelAttribute="bizIntegrationActivity" style="margin-top: 20px" action="${ctx}/biz/integration/bizIntegrationActivity/save" method="post" class="form-horizontal">
		<form:hidden id="integrationId" path="id"/>
		<form:hidden  path="str" value="rules"/>
		<sys:message content="${message}"/>
		<div style="margin-left: 30px">
			每单可使用的万户币≤订单货值的
			<form:input path="integrationNum" id="integrationNum" htmlEscape="false" maxlength="10" class="input-xlarge"/>
			%
		</div>
		<div style="margin-left: 30px;margin-top: 20px">
			<%--<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>保存
			</shiro:hasPermission>--%>
				<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="更新"/>
				</shiro:hasPermission>
		</div>
	</form:form>

		<div class="control-group" style="margin-left: 30px;margin-top: 20px">
			1.积分定义：<br/><br/>

			1.1.命名：万户币；<br/>

			1.2.针对采购商的营销；<br/>

			1.3.积分可抵扣现金；<br/>

			1.4.积分分类，分为：可使用、已使用、已过期；<br/><br/>


			2.积分获取渠道及规则：<br/>

			2.1.手机号注册成功，立即送X个积分；<br/>

			2.2.联营订单-订单完成后，按支付金额的X%，立即赠送积分；（代采订单不赠送）<br/>

			2.3.后台通过【自定义活动】，指定发送时间，赠送的积分；<br/>

			2.4.当计算的积分出现小数点时，小数点后面数字直接舍去；<br/>

			2.5.如：<br/>

			A商品，单价是101元；M经销商购买了3件，总价是303元；积分比率是1%<br/>

			支付成功获得的积分=101*3*1%=3.03个积分≈3个<br/><br/>


			3.积分使用渠道及规则：<br/>

			3.1.当【采购商】，购买商品时，可使用；（联营和代采商品，都可用）<br/>

			3.2.采购时，可以抵扣现金；（提交订单前，可选择是否抵扣）<br/>

			3.3.抵扣比例：1积分=0.01元；<br/><br/>


			4.积分失效规则：<br/>

			4.1.每年7月1日，系统自动消除上一年（1月1日-12月31日）未使用积分；<br/>

			4.2.如：<br/>

			某用户当前可使用积分100个<br/>

			其中2017年获得积分20个，2018年获得积分80个<br/><br/>

			当时间到2018年7月1日时，系统自动消除2017年的20个积分<br/><br/>


			5.积分冻结规则：<br/>

			5.1.下单使用积分抵扣后，订单未完成之前，已抵扣的积分冻结；<br/>

			5.2.如果订单取消，积分返还<br/>

		</div>


</body>
</html>
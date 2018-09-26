<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>注册送</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		    //补充注册送信息
            $.ajax({
                url:"${ctx}/biz/integration/bizIntegrationActivity/systemActivity?code=ZCS",
                type:"get",
                data:'',
                contentType:"application/json;charset=utf-8",
                success:function(data){
                    $("#integrationId").val(data.id);
                    $("#integrationNum").val(data.integrationNum);
                    $("[name='status'][value="+data.status+"]").prop('checked','true');
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
	<script type="text/javascript">
        function doPrint() {
            bdhtml=window.document.body.innerHTML;
            sprnstr="<!--startprint-->";
            eprnstr="<!--endprint-->";
            prnhtml=bdhtml.substr(bdhtml.indexOf(sprnstr)+17);
            prnhtml=prnhtml.substring(0,prnhtml.indexOf(eprnstr));
            window.document.body.innerHTML=prnhtml;
            window.print();
        }
	</script>
</head>
<body >
	<form:form id="inputForm" modelAttribute="bizIntegrationActivity" action="${ctx}/biz/integration/bizIntegrationActivity/save" method="post" style="margin-top: 20px" class="form-horizontal">
		<form:hidden id="integrationId" path="id"/>
		<form:hidden  path="str" value="zcs"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">说明：</label>
			<div class="controls">
				手机号注册成功，即送;
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优惠工具：</label>
			<div class="controls">
				<input type="checkbox" value="万户币" checked="checked">万户币
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">赠送：</label>
			<div class="controls">
				注册成功，赠送：
				<form:input path="integrationNum" id="integrationNum" htmlEscape="false" maxlength="10" class="input-xlarge  digits"/>
				个万户币
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开启状态：</label>
			<div class="controls">
				<form:radiobutton path="status" name="status" onclick="radioDefault" value="1" checked="true"/>开启
				<form:radiobutton path="status" name="status" value="0"/>关闭
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="更新"/>
			</shiro:hasPermission>
		</div>
	</form:form>


	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>局部打印</title>
	</head>

	<br/>
	<br/>
	<br/>
	<br/>
	<div style="margin-left: 100px">
		<!--startprint--><!--注意要加上html里star和end的这两个标记-->
		<h1>打印界面</h1>
		<p>
		<table border="1" border-collapse:collapse style="width: 1000px;height: 1000px">
			<tr align="center">
				<td colspan="6">
					<span style="font-size: 30px">云仓出库签收单</span><div style=" display:inline;margin-left: 100px">www.wanhutong.com</div>
				</td>
			</tr>
			<tr align="center" style="height: 100px">
				<td rowspan="2">单号</td>
				<td></td>
				<td>订单类型</td>
				<td colspan="3">
					<input type="checkbox">备货
					<input type="checkbox">调拨
					<input type="checkbox">样品
					<input type="checkbox">退货
				</td>
			</tr>
			<tr align="center">
				<td></td>
				<td>供货日期</td>
				<td colspan="3"></td>
			</tr>
			<tr align="center">
				<td>发货人</td>
				<td></td>
				<td>联系电话</td>
				<td colspan="3"></td>
			</tr>
			<tr align="center">
				<td>收货地址</td>
				<td></td>
				<td>品类</td>
				<td colspan="3"></td>
			</tr>
			<tr align="center">
				<td>序号</td>
				<td width="300px">厂家型号</td>
				<td>颜色</td>
				<td width="150px">规格</td>
				<td>数量</td>
				<td>供应商</td>
			</tr>
			<tr align="center">
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>
			</tr>
			<tr align="center">
				<td>库管签字</td>
				<td></td>
				<td>财务签字</td>
				<td></td>
				<td>司机签字</td>
				<td></td>
			</tr>
			<tr align="center">
				<td>到货状况</td>
				<td><input type="checkbox">完好
					<input type="checkbox">损坏
					<input type="checkbox">缺少
					<input type="checkbox">其他</td>
				<td>状况说明</td>
				<td></td>
				<td>收货日期</td>
				<td></td>
			</tr>
			<tr align="center">
				<td  colspan="6">1.说明：出库清单互联</td>
			</tr>
			<tr align="center">
				<td  colspan="6">1.说明：出库清单互联</td>
			</tr>


		</table>

		</p>
		<!--endprint-->
		<input class="btn btn-primary" type="button" onclick="doPrint()" value="打印"/>
	</div>

</body>
</html>
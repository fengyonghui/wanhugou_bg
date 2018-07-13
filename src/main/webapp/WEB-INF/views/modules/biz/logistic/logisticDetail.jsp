<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单信息</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		    var trackingNumber = $("#trackingNumber").val();
				$.ajax({
					type:"post",
					url:"${ctx}/biz/inventory/bizInvoice/selectLogistic?trackingNumber="+trackingNumber,
					success:function (logistic) {
						if (logistic.data.logisticStatus == 1) {
                            $("#logisticStatus").val("已发货");
                        } else if (logistic.data.logisticStatus == 2) {
                            $("#logisticStatus").val("运输中");
                        } else if (logistic.data.logisticStatus == 3) {
                            $("#logisticStatus").val("已收货");
                        } else {
                            $("#logisticStatus").val("未知状态");
                        }
						$("#shipperCode").val(logistic.data.shipperCode);
						$("#logisticFee").val(logistic.data.logisticFee);
						var receiverHtml = "";
                        receiverHtml += "<td>"+logistic.data.receiver.name+"</td>";
                        receiverHtml += "<td>"+logistic.data.receiver.tel+"</td>";
                        receiverHtml += "<td>"+logistic.data.receiver.provinceName+logistic.data.receiver.cityName+logistic.data.receiver.expAreaName+"</td>";
                        receiverHtml += "<td>"+logistic.data.receiver.address+"</td>";
                        $("#receiver").append(receiverHtml);
                        var senderHtml = "";
                        senderHtml += "<td>"+logistic.data.sender.name+"</td>";
                        senderHtml += "<td>"+logistic.data.sender.tel+"</td>";
                        senderHtml += "<td>"+logistic.data.sender.provinceName+logistic.data.sender.cityName+logistic.data.sender.expAreaName+"</td>";
                        senderHtml += "<td>"+logistic.data.sender.address+"</td>";
                        $("#sender").append(senderHtml);
                        var logisticInfoHtml = "";
                        var date = new Date(+logistic.data.logisticInfo.logisticTime+8*3600*1000).toISOString().replace(/T/g,' ').replace(/\.[\d]{3}Z/,'');
                        logisticInfoHtml += "<td>"+date+"</td>";
                        logisticInfoHtml += "<td>"+logistic.data.logisticInfo.logisticDetail.logisticName+"</td>";
                        logisticInfoHtml += "<td>"+logistic.data.logisticInfo.logisticDetail.logisticCompanyName+"</td>";
                        logisticInfoHtml += "<td>"+logistic.data.logisticInfo.logisticDetail.mobile+"</td>";
                        $("#logisticInfo").append(logisticInfoHtml);
						$("#remark").text(logistic.data.remark == null ? "":logistic.data.remark);
                    }
				});
			});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="#">运单信息</a></li>
	</ul><br/>
	<form id="inputForm" action="" method="post" class="form-horizontal">
		<input id="trackingNumber" type="hidden" value="${trackingNumber}"/>
		<div class="control-group">
			<label class="control-label">物流单号：</label>
			<div class="controls">
				<input value="${trackingNumber}" htmlEscape="false" class="input-xlarge"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流状态：</label>
			<div class="controls">
				<input id="logisticStatus" value="${fns:getDictLabel('', 'logisticStatus', '')}" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">快递公司编码：</label>
			<div class="controls">
				<input id="shipperCode" value="" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流费用：</label>
			<div class="controls">
				<input id="logisticFee" value="" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">收件人信息：</label>
			<div class="controls">
				<table class="table table-bordered table-striped">
					<thead>
						<th style="width: 20%">姓名</th>
						<th style="width: 20%">手机</th>
						<th style="width: 30%">城市</th>
						<th style="width: 30%">详细地址</th>
					</thead>
					<tbody id="receiver">
					</tbody>
				</table>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">寄件人信息：</label>
			<div class="controls">
				<table class="table table-bordered table-striped">
					<thead>
					<th style="width: 20%">姓名</th>
					<th style="width: 20%">手机</th>
					<th style="width: 30%">城市</th>
					<th style="width: 30%">详细地址</th>
					</thead>
					<tbody id="sender">
					</tbody>
				</table>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流公司信息：</label>
			<div class="controls">
				<table class="table table-bordered table-striped">
					<thead>
					<th style="width: 30%">物流时间</th>
					<th style="width: 30%">公司名称</th>
					<th style="width: 20%">联系人</th>
					<th style="width: 20%">电话</th>
					</thead>
					<tbody id="logisticInfo">
					</tbody>
				</table>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div id="remark" class="controls">

			</div>
		</div>
		<div  class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
		</div>
	</form>
</body>
</html>
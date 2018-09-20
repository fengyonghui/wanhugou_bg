<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单出库</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(document).ready(function() {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    if(window.confirm('你确定要出库吗？')){
                        // alert("确定");
                        form.submit();
                        return true;
                        loading('正在提交，请稍等...');

                    }else{
                        //alert("取消");
                        return false;
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
        });
        $("#btnSubmit").click(function () {
			var reqQty = $("#reqQty").val();
			var sendNum = $("#sendNum").val();
			if (sendNum > reqQty){
			    alert("供货数太大，已超过申报数，请重新调整供货数量！")
				return false;
			}
			$(".reqDetailList").find("td").find("input[title='sendNum']").each(function () {
				console.info($(this).val())
            });
        });

        function checkout(obj) {
            var reqQty = $("#reqQty"+obj).val();	//申报数量
            var receiveNum = $("#receiveNum"+obj).val();		//收货数量
            var recvQty = $("#recvQty"+obj).val();		//已收货数量
            // var sendQty = $("#sendQty"+obj).val();		//已供货数量
            var sum = parseInt(receiveNum) + parseInt(recvQty);
            if (sum > reqQty){
                alert("收货数太大，已超过申报数量，请重新调整收货数量！");
                $("#sendNum"+obj).val(0);
                return false;
            }
        }
        function checkout2(obj) {
            var ordQty = $("#ordQty"+obj).val();
            var sendNum = $("#sendNum"+obj).val();
            var sentQty = $("#sentQty"+obj).val();
            var sum = parseInt(sendNum) + parseInt(sentQty);
            if (sum > ordQty){
                alert("供货数太大，已超过申报数，请重新调整供货数量！");
                $("#sendNum"+obj).val(0);
                return false;
            }
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/biz/request/bizRequestAll?source=${source}&bizStatu=${bizStatu}&ship=${ship}">出库清单列表</a></li>
	<li class="active"><a href="${ctx}/biz/request/bizRequestAll/confirmOut?id=${orderHeader.id}">订单出库</a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="bizSendGoodsRecord" action="${ctx}/biz/inventory/bizSendGoodsRecord/save" method="post" class="form-horizontal">
	<%--<form:hidden path="id"/>--%>
	<sys:message content="${message}"/>
	<input name="bizRequestHeader.id" value="${bizRequestHeader==null?0:bizRequestHeader.id}" type="hidden"/>
	<input name="bizOrderHeader.id" value="${bizOrderHeader==null?0:bizOrderHeader.id}" type="hidden"/>
	<input type="hidden" name="bizStatu" value="${bizStatu}"/>
	<div class="control-group">
		<label class="control-label">销售订单编号：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizOrderHeader.orderNum}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">经销店：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizOrderHeader.customer.name}"/>
			<input type="hidden" name="customer.id" value="${bizOrderHeader.customer.id}">
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">销售订单编号：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${fns:getDictLabel(bizOrderHeader.bizStatus,'biz_order_status','未知')}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">收货地址：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizOrderHeader.bizLocation.fullAddress}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">联系人：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizOrderHeader.bizLocation.receiver}"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">联系电话：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizOrderHeader.bizLocation.phone}"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">出库单：</label>
		<div class="controls">
			<input readonly="readonly" name="sendNo" type="text" class="input-xlarge" value="${sendNo}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">订单详情：</label>
		<div class="controls">
			<table id="orderDetailTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>商品名称</th>
						<th>供应商</th>
						<th>商品货号</th>
						<th>颜色</th>
						<th>尺寸</th>
						<th>采购数量</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${orderDetailList}" var="orderDetail">
						<tr>
							<td>${orderDetail.skuInfo.name}</td>
							<td>${orderDetail.vendor.name}</td>
							<td>${orderDetail.skuInfo.itemNo}</td>
							<td>${orderDetail.color}</td>
							<td>${orderDetail.standard}</td>
							<td>${orderDetail.ordQty}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div class="control-group">
        <c:forEach items="${orderDetailList}" var="orderDetail">
            <label class="control-label">库存信息：</label>
            <div class="controls">
                <table id="inventorySkuTable" class="table table-striped table-bordered table-condensed">
                    <thead>
                    <tr>
                        <th><input type="checkbox" value=""/></th>
                        <th>备货单号</th>
                        <th>商品名称</th>
                        <th>供应商</th>
                        <th>商品货号</th>
                        <th>颜色</th>
                        <th>尺寸</th>
                        <th>库存类型</th>
                        <th>商品类型</th>
                        <th>备货单库存数量</th>
                        <th>已出库数量</th>
                        <th>可出库数量</th>
                        <th>库存数量</th>
                        <th>本次出库数量</th>
                        <th>出库仓库</th>
                    </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${orderDetail.requestDetailList}" var="requestDetail">
                            <tr>
                                <td><input type="checkbox" value=""/></td>
                                <td>${requestDetail.requestHeader.reqNo}</td>
                                <td>${requestDetail.skuInfo.name}</td>
                                <td>${requestDetail.vendorName}</td>
                                <td>${requestDetail.skuInfo.itemNo}</td>
                                <%--<td>${requestDetail.skuInfo.color}</td>--%>
                                <td>--</td>
                                <%--<td>${requestDetail.skuInfo.standard}</td>--%>
                                <td>--</td>
                                <td>--</td>
                                <td>--</td>
                                <%--<td>${fns:getDictLabel(requestDetail.inventory.invType,'inv_type','')}</td>--%>
                                <%--<td>${fns:getDictLabel(requestDetail.inventory.skuType,'inventory_sku_type','')}</td>--%>
                                <td>${requestDetail.recvQty}</td>
                                <td>${requestDetail.outQty}</td>
                                <td>${requestDetail.recvQty - requestDetail.outQty}</td>
                                <%--<td>${requestDetail.inventory.stockQty}</td>--%>
                                <td>--</td>
                                <td><input type="text" name="" value="0"/></td>
                                <td>
                                    <select class='input-mini required'>
                                        <c:forEach items="${inventoryInfoList}" var="inventory">
                                            <option value="${inventory.id}">${inventory.name}</option>
                                        </c:forEach>
                                    </select>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <HR align=center width=100% color=#987cb9 SIZE=1>
        </c:forEach>
	</div>

	<div class="form-actions">
		<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="确认出库"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
	</div>

</form:form>

</body>
</html>
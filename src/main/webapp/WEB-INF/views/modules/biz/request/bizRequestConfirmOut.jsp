<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单出库</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(document).ready(function() {
            $('#select_all').live('click',function(){
                var choose=$("input[type='checkbox']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    var treasuryList = new Array();
                    var i = 0;
                    $("input[name='reqDetail'][checked='checked']").each(function () {
                        var orderDetailId = $(this).parent().parent().find("input[name='orderDetailId']").val();
                        var reqDetailId = $(this).parent().parent().find("input[name='reqDetailId']").val();
                        var invSkuId = $(this).parent().parent().find("input[name='invSkuId']").val();
                        var sentQty = $(this).parent().parent().find("input[name='sentQty']").val();
                        var uVersion = $(this).parent().parent().find("input[name='uVersion']").val();
                        var sendNo = $("#sendNo").val();
                        treasuryList[i] = createTreasury(orderDetailId,reqDetailId,invSkuId,sentQty,uVersion,sendNo);
                        i = i + 1;
                    });
                    console.info(JSON.stringify(treasuryList));
                    
                    if(window.confirm('你确定要出库吗？')){
                        $.ajax({
                            type:"post",
                            contentType: 'application/json;charset=utf-8',
                            url:"${ctx}/biz/inventory/bizSendGoodsRecord/outTreasury",
                            data:JSON.stringify(treasuryList),
                            success:function (data) {
                                if (data == 'ok') {
                                    alert("出库成功");
                                    window.location.href = "${ctx}/biz/request/bizRequestAll?souce=kc&bizStatu=0&ship=xs";
                                }
                           }
                        });
                        // form.submit();
                        // return true;
                        // loading('正在提交，请稍等...');

                    }else{
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

        function checkOrdDetail(obj) {
            if ($(obj).attr("checked")=='checked') {
                $(obj).attr("checked","checked");
                $(obj).parent().parent().parent().parent().find("tbody").find("input[name='reqDetail']").each(function () {
                   $(this).attr("checked","checked");
                });
            } else {
                $(obj).removeAttr("checked");
                $(obj).parent().parent().parent().parent().find("tbody").find("input[name='reqDetail']").each(function () {
                    $(this).removeAttr("checked");
                });
            }
        }


        function checkReqDetail(obj) {
            if ($(obj).attr("checked")=='checked') {
                $(obj).attr("checked","checked");
            } else {
                $(obj).removeAttr("checked");
            }
        }
        
        function createTreasury(orderDetailId, reqDetailId, invSkuId, outQty,uVersion,sendNo) {
            var treasury = new Object();
            treasury.orderDetailId = orderDetailId;
            treasury.reqDetailId = reqDetailId;
            treasury.invSkuId = invSkuId;
            treasury.outQty = outQty;
            treasury.sendNo = sendNo;
            treasury.uVersion = uVersion;
            return treasury;
        }
	</script>
	<script type="text/javascript">
        function doPrint() {
            top.$.jBox.confirm("确认要打印当前出库单吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    bdhtml=window.document.body.innerHTML;
                    sprnstr="<!--startprint-->";
                    eprnstr="<!--endprint-->";
                    prnhtml=bdhtml.substr(bdhtml.indexOf(sprnstr)+17);
                    prnhtml=prnhtml.substring(0,prnhtml.indexOf(eprnstr));
                    window.document.body.innerHTML=prnhtml;
                    window.print();
                    location.reload();
                //    alert("打印出库单成功");
                }
            },{buttonsFocus:1});
            top.$('.jbox-body .jbox-icon').css('top','55px');
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
			<input id="sendNo" readonly="readonly" name="sendNo" type="text" class="input-xlarge" value="${bizSendGoodsRecord.sendNo}"/>
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
						<th>已出库数量</th>
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
							<td>${orderDetail.sentQty}</td>
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
						<c:if test="${source ne 'detail'}">
                        	<th><input name="ordDetail" type="checkbox" onclick="checkOrdDetail(this)"/></th>
						</c:if>
                        <th>备货单号</th>
                        <th>商品名称</th>
                        <th>供应商</th>
                        <th>商品货号</th>
                        <th>颜色</th>
                        <th>尺寸</th>
                        <th>库存类型</th>
                        <th>备货方</th>
                        <th>备货单库存数量</th>
                        <th>已出库数量</th>
                        <th>可出库数量</th>
                        <th>库存数量</th>
						<c:if test="${source ne 'detail'}">
                        	<th>本次出库数量</th>
						</c:if>
                        <th>出库仓库</th>
                    </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${orderDetail.requestDetailList}" var="requestDetail">
                            <tr>
								<c:if test="${source ne 'detail'}">
                                	<td><input name="reqDetail" type="checkbox" onclick="checkReqDetail(this)"/></td>
								</c:if>
                                <td>${requestDetail.requestHeader.reqNo}</td>
                                <td>${requestDetail.skuInfo.name}</td>
                                <td>${requestDetail.vendorName}</td>
                                <td>${requestDetail.skuInfo.itemNo}</td>
                                <%--<td>${requestDetail.skuInfo.color}</td>--%>
                                <td>--</td>
                                <%--<td>${requestDetail.skuInfo.standard}</td>--%>
                                <td>--</td>
                                <td>${fns:getDictLabel(requestDetail.inventorySku.invType,'inv_type','')}</td>
                                <td>${fns:getDictLabel(requestDetail.inventorySku.skuType,'inventory_sku_type','')}</td>
                                <td>${requestDetail.recvQty}</td>
                                <td>${requestDetail.outQty == null ? "0" : requestDetail.outQty}</td>
                                <td>${requestDetail.recvQty - requestDetail.outQty}</td>
                                <input name="orderDetailId" value="${orderDetail.id}" type="hidden"/>
                                <input name="reqDetailId" value="${requestDetail.id}" type="hidden"/>
                                <input name="invSkuId" value="${requestDetail.inventorySku.id}" type="hidden"/>
                                <input name="uVersion" value="${requestDetail.inventorySku.uVersion}" type="hidden"/>
                                <td>${requestDetail.inventorySku.stockQty}</td>
								<c:if test="${source ne 'detail'}">
                                	<td><input type="text" name="sentQty" value="0"/></td>
								</c:if>
                                <td>
                                    ${requestDetail.inventorySku.invInfo.name}
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
		<c:if test="${source ne 'detail'}">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="确认出库"/>&nbsp;</shiro:hasPermission>
		</c:if>
		<%--<input id="yulan" class="btn btn-primary" type="button" value="打印预览"/>--%>
		<input class="btn btn-primary" type="button" onclick="doPrint()" value="打印"/>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
	</div>

</form:form>

	<div>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			<title>局部打印</title>
		</head>
		<br/>
		<div style="margin-left: 100px;display: none">
			<!--startprint--><!--注意要加上html里star和end的这两个标记-->
			<p>
			<table border="1" border-collapse:collapse style="width: 1000px;height: 1000px">
				<tr align="center" style="height: 60px">
					<td colspan="6" valign="middle">
						<img src="${ctxStatic}/jingle/image/logo.png" style="float: left">
						<b style="font-size: 20px">云仓出库单</b>
						<div style="font-size: 15px;font-weight: bold;float: right;margin-bottom: 0px">www.wanhutong.com</div>
					</td>
				</tr>
				<tr align="center">
					<td rowspan="2">单号</td>
					<td rowspan="2">${bizOrderHeader.orderNum}</td>
					<td >订单类型</td>
					<td colspan="3">
						<input type="checkbox">订单
						<input type="checkbox">调拨
						<input type="checkbox">样品
						<input type="checkbox">退货
					</td>
				</tr>
				<tr align="center">
					<td>出库日期</td>
					<td colspan="3">
						<fmt:formatDate value="${bizSendGoodsRecord.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
				</tr>
				<tr align="center">
					<td>发货人</td>
					<td>${bizOrderHeader.bizLocation.receiver}</td>
					<td>联系电话</td>
					<td colspan="3">${bizOrderHeader.bizLocation.phone}</td>
				</tr>
				<tr align="center">
					<td>收货地址</td>
					<td colspan="5">${bizOrderHeader.bizLocation.fullAddress}</td>

				</tr>
				<tr align="center">
					<td>货号</td>
					<td width="200px">商品名称</td>
					<td width="200px">供应商名称</td>
					<td width="120px">颜色</td>
					<td width="120px">规格</td>
					<td width="120px">已出库数量</td>
				</tr>
				<c:forEach items="${orderDetailList}" var="orderDetail">
					<tr align="center">
						<td>${orderDetail.skuInfo.itemNo}</td>
						<td>${orderDetail.skuInfo.name}</td>
						<td>${orderDetail.vendor.name}</td>
						<td>${orderDetail.color}</td>
						<td>${orderDetail.standard}</td>
						<td>${orderDetail.sentQty}</td>
					</tr>
				</c:forEach>
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
					<td  colspan="6">1.说明：此出库清单三联，第一联云仓库管发货存档，第二联账务或采购中心签字留存，第三联云仓司机签收存档</td>
				</tr>
				<tr align="center">
					<td  colspan="6">2.说明：此出库清单经云仓库管签字生效，云仓货品出库需携带此单，认真检查货品状况并填写签收</td>
				</tr>
			</table>
			</p>
			<!--endprint-->

		</div>
	</div>
</body>
</html>
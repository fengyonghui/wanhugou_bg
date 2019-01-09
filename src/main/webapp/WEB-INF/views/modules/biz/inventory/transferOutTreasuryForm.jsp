<%@ page import="com.wanhutong.backend.modules.enums.TransferStatusEnum" %>
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
                    var flag = false;
                    var flag1 = true;
                    var flag2= true;
                    var flag3 = true;
                    $("input[name='reqDetail']").each(function () {
						if ($(this).attr("checked")=='checked') {
							flag = true;
						    var sentQty = $(this).parent().parent().find("input[name='sentQty']").val();
						    var okQty = $(this).parent().parent().find("input[name='okQty']").val();
						    if (sentQty == '' || sentQty == 0) {
                                flag1 = false;
							}
							if (parseInt(sentQty) > parseInt(okQty)) {
						        flag2 = false;
							}
						}
                    });
                    var treasuryList = new Array();
                    var i = 0;
                    var sumSentQty = 0;
                    var stockQty = 0;
                    var map = {};
                    var sendMap = {};
                    var needMap = {};
                    $("input[name='reqDetail'][checked='checked']").each(function () {
                        var transQty = $(this).parent().parent().find("input[name='transQty']").val();
                        var sQty = $(this).parent().parent().find("input[name='sQty']").val();
                        var transferDetailId = $(this).parent().parent().find("input[name='transferDetailId']").val();
                        var reqDetailId = $(this).parent().parent().find("input[name='reqDetailId']").val();
                        var invSkuId = $(this).parent().parent().find("input[name='invSkuId']").val();
                        var sentQty = $(this).parent().parent().find("input[name='sentQty']").val();
                        var uVersion = $(this).parent().parent().find("input[name='uVersion']").val();
                        var sendNo = $("#sendNo").val();
                        stockQty = $(this).parent().parent().find("input[name='stockQty']").val();
                        var has = transferDetailId in map;
                        if (has) {
                            map[transferDetailId] = parseInt(map[transferDetailId]) + parseInt(sentQty);
                        } else {
                            map[transferDetailId] = parseInt(sentQty);
                            sendMap[transferDetailId] = parseInt(stockQty);
                            needMap[transferDetailId] = parseInt(transQty) - parseInt(sQty);
                        }
                        // sumSentQty = parseInt(sumSentQty) + parseInt(sentQty);
                        treasuryList[i] = createTreasury(transferDetailId,reqDetailId,invSkuId,sentQty,uVersion,sendNo);
                        i = i + 1;
                    });
                    var stockFlag = true;
                    $.each(map,function (key, value) {
						if (parseInt(value) > parseInt(sendMap[key])) {
						    stockFlag = false;
						}
						if (parseInt(value) > parseInt(needMap[key])) {
						    flag3 = false;
						}
                    });
                    console.info(JSON.stringify(treasuryList));

                    var trackingNumber = $("#trackingNumber").val();
                    var inspectorId = $("#inspectorId").val();
                    var inspectDate = $("#inspectDate").val();
                    var inspectRemark = $("#inspectRemark").val();
                    var collLocate = $("#collLocate").val();
                    var sendDate = $("#sendDate").val();
                    var settlementStatus = $("#settlementStatus").val();
                    var source = $("#source").val();

                    var bizInvoiceStr = {"trackingNumber":trackingNumber,
						"inspectorId":inspectorId,
						"inspectDate":inspectDate,
						"inspectRemark":inspectRemark,
						"collLocate":collLocate,
						"sendDate":sendDate,
						"settlementStatus":settlementStatus
					};

                    var requestData = {"treasuryList":treasuryList, "bizInvoiceStr": bizInvoiceStr};

                    if(window.confirm('你确定要出库吗？')){
                        if (!flag) {
                            alert("请勾选出库的备货单！");
                            return false;
						} else if (!flag1){
                            alert("选中出库的备货单，本次出库数量不能为空，也不能为0");
                            return false;
						} else if (!flag2){
                            alert("选中出库的备货单，本次出库数量不能大于可出库数量");
                            return false;
						} else if (!flag3){
                            alert("选中出库的备货单，本次出库数量不能大于调拨单的剩余需求数量");
                            return false;
                        } else if (!stockFlag) {
                            alert("该商品库存不足");
                            return false;
						}else {
                            $Mask.AddLogo("正在加载");
							$.ajax({
								type:"post",
								contentType: 'application/json;charset=utf-8',
								url:"${ctx}/biz/inventory/bizSkuTransfer/outTreasury",
								data:JSON.stringify(requestData),
								success:function (data) {
									if (data=='ok') {
										alert("出库成功");
										window.location.href = "${ctx}/biz/inventory/bizSkuTransfer?source=" + source;
									} else if (data=='error') {
									    alert("出库失败，没有选择出库数据");
                                    } else {
									    alert("其他人正在出库，请刷新页面重新操作");
                                    }
							   }
							});
                        }
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

        function checkTransferDetail(obj) {
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

        function addTab($this, refresh){
            parent.addTab($this, refresh);
        }

        function checkReqDetail(obj) {
            if ($(obj).attr("checked")=='checked') {
                $(obj).attr("checked","checked");
            } else {
                $(obj).removeAttr("checked");
            }
        }

        function createTreasury(transferDetailId, reqDetailId, invSkuId, outQty,uVersion,sendNo) {
            var treasury = new Object();
            treasury.transferDetailId = transferDetailId;
            treasury.reqDetailId = reqDetailId;
            treasury.invSkuId = invSkuId;
            treasury.outQty = outQty;
            treasury.sendNo = sendNo;
            treasury.uVersion = uVersion;
            return treasury;
        }
	</script>
	<%--<script type="text/javascript">
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
	</script>--%>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/biz/inventory/bizSkuTransfer?source=${bizSkuTransfer.source}&str=${bizSkuTransfer.str}">调拨单列表</a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="bizSkuTransfer"  action="${ctx}/biz/inventory/bizSkuTransfer/outTreasury" method="post" class="form-horizontal">
	<%--<form:hidden path="id"/>--%>
	<sys:message content="${message}"/>
	<input name="bizRequestHeader.id" value="${bizRequestHeader==null?0:bizRequestHeader.id}" type="hidden"/>
	<input name="bizSkuTransfer.id" value="${bizSkuTransfer==null?0:bizSkuTransfer.id}" type="hidden"/>
    <input id="source" name="source" value="${bizSkuTransfer.source}" type="hidden"/>
	<input id="str" name="str" type="hidden" value="${bizSkuTransfer.str}"/>
	<input type="hidden" name="bizStatu" value="${bizStatu}"/>
	<div class="control-group">
		<label class="control-label">调拨单号：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizSkuTransfer.transferNo}"/>
			<%--<c:if test="${bizOrderHeader.orderType == 8 && source == 'detail'}">--%>
				<%--<a onclick="return addTab($(this), true);" target="mainFrame" href="${ctx}/biz/inventory/bizInvoice/list?orderNum=${bizOrderHeader.orderNum}&bizStatus=0&ship=0">查看物流信息</a>--%>
			<%--</c:if>--%>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">起始采购中心仓库：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizSkuTransfer.fromInv.name}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">目标采购中心仓库：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${bizSkuTransfer.toInv.name}"/>
		</div>
	</div>
	<c:if test="${bizSkuTransfer.str != 'detail'}">
		<div class="control-group">
			<label class="control-label">物流单号：</label>
			<div class="controls">
				<input id="trackingNumber" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">验货员：</label>
			<div class="controls">
				<select about="choose" id="inspectorId" class="input-medium ">
					<option value="" label="请选择">请选择</option>
					<c:forEach var="v" items="${inspectorList}">
						<option value="${v.id}" label="${v.name}">${v.name}</option>
					</c:forEach>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">验货时间：</label>
			<div class="controls">
				<input name="inspectDate" id="inspectDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					   value="<fmt:formatDate value="${bizInvoice.inspectDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">验货备注：</label>
			<div class="controls">
				<textarea id="inspectRemark" htmlEscape="false" maxlength="30" class="input-xlarge "></textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">集货地点：</label>
			<div class="controls">
				<select id="collLocate" htmlEscape="false" maxlength="30" class="input-xlarge required">
					<option value="" label="请选择">请选择</option>
					<c:forEach items="${fns:getDictList('coll_locate')}" var="v">
						<option value="${v.value}" label="${v.label}">${v.label}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发货时间：</label>
			<div class="controls">
				<input name="sendDate" id="sendDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					   value="<fmt:formatDate value="${bizInvoice.sendDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流结算方式：</label>
			<div class="controls">
				<select id="settlementStatus" name="settlementStatus" onmouseout="" class="input-xlarge">
					<c:forEach items="${fns:getDictList('biz_settlement_status')}" var="settlementStatus">
						<option value="${settlementStatus.value}">${settlementStatus.label}</option>
					</c:forEach>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
	</c:if>
	<div class="control-group">
		<label class="control-label">业务状态：</label>
		<div class="controls">
			<input type="text" class="input-xlarge" readonly="readonly" value="${fns:getDictLabel(bizSkuTransfer.bizStatus,'transfer_bizStatus','未知')}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">出库单：</label>
		<div class="controls">
			<input id="sendNo" readonly="readonly" name="sendNo" type="text" class="input-xlarge" value="${sendNo}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">期望收货时间：</label>
		<div class="controls">
			<input id="recvEta" readonly="readonly" name="recvEta" type="text" class="input-xlarge" value="<fmt:formatDate value="${bizSkuTransfer.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">调拨单详情：</label>
		<div class="controls">
			<table id="transferDetailTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>商品名称</th>
						<th>供应商</th>
						<th>商品货号</th>
						<th>颜色</th>
						<th>尺寸</th>
						<th>调拨数量</th>
						<th>已出库数量</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${transferDetailList}" var="transferDetail">
						<tr>
							<td>${transferDetail.skuInfo.name}</td>
							<td>${transferDetail.skuInfo.productInfo.vendorName}</td>
							<td>${transferDetail.skuInfo.itemNo}</td>
							<td>${transferDetail.color}</td>
							<td>${transferDetail.size}</td>
							<td>${transferDetail.transQty}</td>
							<td>${transferDetail.outQty}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<div class="control-group">
        <c:forEach items="${transferDetailList}" var="transferDetail">
			<c:if test="${transferDetail.transQty != transferDetail.outQty || bizSkuTransfer.str eq 'detail'}">
            	<label class="control-label">起始库存信息：</label>
				<div class="controls">
					<table id="inventorySkuTable" class="table table-striped table-bordered table-condensed">
						<thead>
						<tr>
							<c:if test="${bizSkuTransfer.str ne 'detail'}">
								<th><input name="transferDetail" type="checkbox" onclick="checkTransferDetail(this)"/></th>
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
							<th>库存总数</th>
							<c:if test="${bizSkuTransfer.str ne 'detail'}">
								<th>本次出库数量</th>
							</c:if>
							<th>出库仓库</th>
						</tr>
						</thead>
						<tbody>
							<c:forEach items="${transferDetail.requestDetailList}" var="requestDetail">
								<c:if test="${requestDetail.recvQty - requestDetail.outQty != 0 || bizSkuTransfer.str eq 'detail'}">
									<tr>
										<c:if test="${bizSkuTransfer.str ne 'detail'}">
											<td><input name="reqDetail" type="checkbox" onclick="checkReqDetail(this)"/></td>
										</c:if>
										<td>${requestDetail.requestHeader.reqNo}</td>
										<td>${requestDetail.skuInfo.name}</td>
										<td>${requestDetail.skuInfo.vendorName}</td>
										<td>${requestDetail.skuInfo.itemNo}</td>
										<td>${transferDetail.color}</td>
										<td>${transferDetail.size}</td>
										<td>${fns:getDictLabel(requestDetail.inventorySku.invType,'inv_type','')}</td>
										<td>${fns:getDictLabel(requestDetail.inventorySku.skuType,'inventory_sku_type','')}</td>
										<td>${requestDetail.recvQty}</td>
										<td>${requestDetail.outQty == null ? "0" : requestDetail.outQty}</td>
										<td>${requestDetail.recvQty - requestDetail.outQty}</td>
										<input name="okQty" value="${requestDetail.recvQty - requestDetail.outQty}" type="hidden"/>
                                        <input name="sQty" value="${transferDetail.outQty}" type="hidden"/>
                                        <input name="transQty" value="${transferDetail.transQty}" type="hidden"/>
										<input name="transferDetailId" value="${transferDetail.id}" type="hidden"/>
										<input name="reqDetailId" value="${requestDetail.id}" type="hidden"/>
										<input name="invSkuId" value="${requestDetail.inventorySku.id}" type="hidden"/>
										<input name="uVersion" value="${requestDetail.inventorySku.uVersion}" type="hidden"/>
										<input name="stockQty" value="${requestDetail.inventorySku.stockQty}" type="hidden"/>
										<td>${requestDetail.inventorySku.stockQty}</td>
										<c:if test="${bizSkuTransfer.str ne 'detail'}">
											<td><input type="text" name="sentQty" value="0"/></td>
										</c:if>
										<td>
											${requestDetail.inventorySku.invInfo.name}
										</td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:if>
            <HR align=center width=100% color=#987cb9 SIZE=1>
        </c:forEach>
	</div>

	<div class="form-actions">
		<c:if test="${bizSkuTransfer.str ne 'detail'}">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="确认出库"/>&nbsp;</shiro:hasPermission>
		</c:if>
		<%--<input id="yulan" class="btn btn-primary" type="button" value="打印预览"/>--%>
		<%--<input class="btn btn-primary" type="button" onclick="doPrint()" value="打印"/>--%>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
	</div>

</form:form>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.BizInventoryEnum" %>
<html>
<head>
	<title>库存</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.help_step_box{background: rgba(255, 255, 255, 0.45);overflow:hidden;border-top:1px solid #FFF;width: 100%}
		.help_step_item{margin-right: 30px;width:200px;border:1px #3daae9 solid;float:left;height:150px;padding:0 25px 0 45px;cursor:pointer;position:relative;font-size:14px;font-weight:bold;}
		.help_step_num{width:19px;height:120px;line-height:100px;position:absolute;text-align:center;top:18px;left:10px;font-size:16px;font-weight:bold;color: #239df5;}
		.help_step_set{background: #FFF;color: #3daae9;}
		.help_step_set .help_step_left{width:8px;height:100px;position:absolute;left:0;top:0;}
		.help_step_set .help_step_right{width:8px;height:100px; position:absolute;right:-8px;top:0;}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
				    var flag = false;//是否有选中的选项
				    var flag1 = true;
				    var flag2 =true;
				    var flag3 = true;
				    var stockQty = 0;//总库存数
				    var splitQtySum = 0;//累计拆分数量
                    $("input[name='reqDetail'][checked='checked']").each(function (i) {
                        flag = true;
                        splitQtySum = parseInt(splitQtySum) + parseInt(splitQty);
                        var reqDetailId = $(this).parent().parent().find("input[name='reqDetailId']").val();//备货单详情ID
                        var invSkuId = $(this).parent().parent().find("input[name='invSkuId']").val();//库存ID
                        var splitQty = $(this).parent().parent().find("input[name='splitQty']").val();//拆分数量
                        var okQty = $(this).parent().parent().find("input[name='okQty']").val();//可拆分数量
                        var uVersion = $(this).parent().parent().find("input[name='uVersion']").val();//版本号
						stockQty = $(this).parent().parent().find("input[name='stockQty']").val();//总库存数
                        if (splitQty == '' || splitQty == 0) {
                            alert("选中的拆分数量不能为空，也不能为0");
                            flag1 = false;
                        }
                        if (parseInt(splitQty) > parseInt(okQty)) {
                            alert("拆分数量不能大于可拆分数量");
                            flag2 = false;
                        }
                        treasuryList[i] = createTreasury(reqDetailId,invSkuId,splitQty,uVersion);
                    });
                    console.info(JSON.stringify(treasuryList));
                    if (parseInt(splitQtySum) > parseInt(stockQty)) {
                        alert("拆分总数量不能大于总库存数");
                        flag3 = false;
					}
					if (!flag) {
                        alert("请至少勾选一条备货单详情");
                        return false;
					}
                    var requestData = {"treasuryList":treasuryList};
                    if(window.confirm('你确定要拆分吗？')){
                        if (flag && flag1 && flag2 && flag3) {
                            $Mask.AddLogo("正在加载");
                            $.ajax({
                                type:"post",
                                contentType: 'application/json;charset=utf-8',
                                url:"${ctx}/biz/inventory/bizInventorySku/skuSplit",
                                data:JSON.stringify(requestData),
                                success:function (data) {
                                    if (data=='ok') {
                                        alert("拆分成功");
                                        window.location.href = "${ctx}/biz/inventory/bizInventorySku";
                                    } else if (data=='error') {
                                        alert("拆分失败，没有选择拆分数据");
                                    } else {
                                        alert("其他人正在拆分，请刷新页面重新操作");
                                    }
                                }
                            });
						}
                    }
					// loading('正在提交，请稍等...');
					// form.submit();
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

            /* 全选 全不选 */
            $('#chAll').live('click',function(){
                var choose=$("input[name='reqDetail']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
		});

        function createTreasury(reqDetailId,invSkuId,splitQty,uVersion) {
            var treasury = new Object();
            treasury.reqDetailId = reqDetailId;
            treasury.invSkuId = invSkuId;
            treasury.outQty = splitQty;
            treasury.uVersion = uVersion;
            return treasury;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/inventory/bizInventorySku/inventory">商品库存详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku/skuSplitForm?id=${inventorySku.id}">商品库存拆分</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInventorySku" action="${ctx}/biz/inventory/bizInventorySku/skuSplit" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
        <form:hidden path="id"/>
		<div class="control-group">
			<label class="control-label">备货单信息：</label>
			<div class="controls">
				<table id="contentTable"  class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th><input id="chAll" name="reqDetails" type="checkbox"/></th>
						<th>备货单号</th>
						<th>商品名称</th>
						<th>供应商</th>
						<th>商品货号</th>
						<th>库存类型</th>
						<th>备货方</th>
						<th>备货单库存数量</th>
						<th>已出库数量</th>
						<th>可拆分数量</th>
						<th>库存总数</th>
						<th>所属仓库</th>
						<th>拆分数量</th>
					</tr>
					</thead>
					<tbody id="invReq">
						<c:forEach items="${requestDetailList}" var="requestDetail" varStatus="i">
							<c:if test="${inventorySku.invInfo.id == requestDetail.inventorySku.invInfo.id}">
							<tr>
								<td><input name="reqDetail" type="checkbox" onclick="checkReqDetail(this)"/></td>
								<td>${requestDetail.requestHeader.reqNo}</td>
								<td>${requestDetail.skuInfo.name}</td>
								<td>${requestDetail.vendorName}</td>
								<td>${requestDetail.skuInfo.itemNo}</td>
								<td>${fns:getDictLabel(requestDetail.inventorySku.invType,'inv_type','')}</td>
								<td>${fns:getDictLabel(requestDetail.inventorySku.skuType,'inventory_sku_type','')}</td>
								<td>${requestDetail.recvQty}</td>
								<td>${requestDetail.outQty == null ? "0" : requestDetail.outQty}</td>
								<td>${requestDetail.recvQty - requestDetail.outQty}</td>
								<td>${requestDetail.inventorySku.stockQty}</td>
								<td>${requestDetail.inventorySku.invInfo.name}</td>
								<td><input type="number" min="0" name="splitQty" value="0" class="input-mini"/></td>
								<input name="okQty" value="${requestDetail.recvQty - requestDetail.outQty}" type="hidden"/>
								<input name="reqDetailId" value="${requestDetail.id}" type="hidden"/>
								<input name="invSkuId" value="${requestDetail.inventorySku.id}" type="hidden"/>
								<input name="uVersion" value="${requestDetail.inventorySku.uVersion}" type="hidden"/>
								<input name="stockQty" value="${requestDetail.inventorySku.stockQty}" type="hidden"/>
							</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
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

        function checkReqDetail(obj) {
            if ($(obj).attr("checked")=='checked') {
                $(obj).attr("checked","checked");
            } else {
                $(obj).removeAttr("checked");
            }
        }

        function createTreasury(reqDetailId,transferDetailId,invSkuId,mergeQty,uVersion) {
            var treasury = new Object();
            treasury.reqDetailId = reqDetailId;
            treasury.transferDetailId = transferDetailId;
            treasury.invSkuId = invSkuId;
            treasury.outQty = mergeQty;
            treasury.uVersion = uVersion;
            return treasury;
        }

        function merge() {
            var flag = false;//是否有选中的选项
            var flag1 = true;//选中的内容是否为空或0
            var flag2 =true;//合并数量是否大于可合并数量
            var flag3 = true;//各个尺寸的合并数是否大于库存总数
            var flag4 = true;//各个尺寸合并的数量是否相等
			var mergeList = new Array();
			var map = {};
            $("input[name='reqKey']").each(function () {
			    var mergeNum = 0; //累计合并数量
                var stockQty = 0;//总库存数
				var key = $(this).val();//尺寸
                var key2 = "";
                var treasuryList = new Array();
                var index = 0;
                $("#invReq").find("input[name='req_"+key+"']").each(function () {
                    if ($(this).parent().parent().find("input[name='reqDetail']").attr("checked") == undefined) {
                        return;
                    }
					if ($(this).parent().parent().find("input[name='reqDetail']").attr("checked") == 'checked') {
                        key2 = key;
                        flag = true;
                        var mergeQty = $(this).parent().parent().find("input[name='mergeQty']").val();//合并数量
                        var reqDetailId = $(this).parent().parent().find("input[name='reqDetailId']").val();//备货单详情ID
                        var transferDetailId = $(this).parent().parent().find("input[name='transferDetailId']").val();//调拨单详情ID
                        var invSkuId = $(this).parent().parent().find("input[name='invSkuId']").val();//库存ID
                        var okQty = $(this).parent().parent().find("input[name='okQty']").val();//可合并数量
                        var uVersion = $(this).parent().parent().find("input[name='uVersion']").val();//版本号
                        stockQty = $(this).parent().parent().find("input[name='stockQty']").val();//总库存数
                        mergeNum = parseInt(mergeNum) + parseInt(mergeQty);
                        if (mergeQty == '' || mergeQty == 0) {
                            alert("选中的合并数量不能为空，也不能为0");
                            flag1 = false;
                        }
                        if (parseInt(mergeQty) > parseInt(okQty)) {
                            alert("合并数量不能大于可合并数量");
                            flag2 = false;
                        }
                        treasuryList[index] = createTreasury(reqDetailId,transferDetailId,invSkuId,mergeQty,uVersion);
                        index = parseInt(index) + parseInt(1);
					}
                });
                console.info("treasuryList:" + treasuryList);
                if (key2 != "") {
				    map[key2] = treasuryList;
                }
				if (mergeNum != 0) {
					mergeList.push(mergeNum);
				}

				if (parseInt(mergeNum) > parseInt(stockQty)) {
				    alert("每个尺寸合并的数量不能大于对应的总库存数");
				    flag3 = false;
				}
            });
            if (!flag) {
                alert("请至少勾选一条备货单详情");
                return false;
            }
            console.info("mergeList:" + mergeList);
            for (var i=0; i < mergeList.length; i++) {
                if (i != (parseInt(mergeList.length) - parseInt(1))) {
                    if (mergeList[i] != mergeList[parseInt(i)+parseInt(1)]) {
                        alert("各个尺寸选中的合并数量不相等");
                        flag4 = false;
                        break;
                    }
                }
            }
			console.info("flag:"+flag+",flag2:"+flag2+",flag3:"+flag3+",flag4:"+flag4);
            console.info("map:" + JSON.stringify(map));
            if(window.confirm('你确定要合并吗？')) {
                if (flag && flag1 && flag2 && flag3 && flag4) {
                    $Mask.AddLogo("正在加载");
                    $.ajax({
						type:"post",
                        contentType: 'application/json',
						url:"${ctx}/biz/inventory/bizInventorySku/skuMerge",
						data:JSON.stringify(map),
						success:function (data) {
							alert(data);
							window.location.href = "${ctx}/biz/inventory/bizInventorySku?zt=1";
                        },
                        error:function (data) {
						    alert(data);
							console.log(data);
                        }
					});
                }
            }
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/inventory/bizInventorySku?zt=1">商品库存详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku/skuSplitForm?id=${inventorySku.id}">商品库存拆分</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInventorySku" action="${ctx}/biz/inventory/bizInventorySku/skuSplit" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
        <form:hidden path="id"/>
		<input id="invInfoId" type="hidden" value="${inventorySku.invInfo.id}"/>
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
						<th>可合并数量</th>
						<th>库存总数</th>
						<th>所属仓库</th>
						<th>合并数量</th>
					</tr>
					</thead>
					<tbody id="invReq">
						<c:forEach items="${sizeList}" var="req">
                            <c:if test="${fns:containsKey(reqMap, req) || fns:containsKey(transMap, req)}">
							    <input name="reqKey" type="hidden" value="${req}"/>
                            </c:if>
                            <c:forEach items="${reqMap[req]}" var="requestDetail" varStatus="i">
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
                                    <td><input name="req_${req}" type="hidden" value="${req}"/><input type="number" min="0" name="mergeQty" value="0" class="input-mini"/></td>
                                    <input name="okQty" value="${requestDetail.recvQty - requestDetail.outQty}" type="hidden"/>
                                    <input name="reqDetailId" value="${requestDetail.id}" type="hidden"/>
                                    <input name="invSkuId" value="${requestDetail.inventorySku.id}" type="hidden"/>
                                    <input name="uVersion" value="${requestDetail.inventorySku.uVersion}" type="hidden"/>
                                    <input name="stockQty" value="${requestDetail.inventorySku.stockQty}" type="hidden"/>
                                </tr>
                                </c:if>
                            </c:forEach>
                            <c:forEach items="${transMap[req]}" var="transferDetail" varStatus="i">
                                <c:if test="${inventorySku.invInfo.id == transferDetail.inventorySku.invInfo.id}">
                                    <tr>
                                        <td><input name="reqDetail" type="checkbox" onclick="checkReqDetail(this)"/></td>
                                        <td>${transferDetail.transfer.transferNo}</td>
                                        <td>${transferDetail.skuInfo.name}</td>
                                        <td>${transferDetail.skuInfo.vendorName}</td>
                                        <td>${transferDetail.skuInfo.itemNo}</td>
                                        <td>${fns:getDictLabel(transferDetail.inventorySku.invType,'inv_type','')}</td>
                                        <td>${fns:getDictLabel(transferDetail.inventorySku.skuType,'inventory_sku_type','')}</td>
                                        <td>${transferDetail.inQty}</td>
                                        <td>${transferDetail.sentQty == null ? "0" : transferDetail.sentQty}</td>
                                        <td>${transferDetail.inQty - transferDetail.sentQty}</td>
                                        <td>${transferDetail.inventorySku.stockQty}</td>
                                        <td>${transferDetail.inventorySku.invInfo.name}</td>
                                        <td><input name="req_${req}" type="hidden" value="${req}"/><input type="number" min="0" name="mergeQty" value="0" class="input-mini required"/></td>
                                        <input name="okQty" value="${transferDetail.inQty - transferDetail.sentQty}" type="hidden"/>
                                        <input name="transferDetailId" value="${transferDetail.id}" type="hidden"/>
                                        <input name="invSkuId" value="${transferDetail.inventorySku.id}" type="hidden"/>
                                        <input name="uVersion" value="${transferDetail.inventorySku.uVersion}" type="hidden"/>
                                        <input name="stockQty" value="${transferDetail.inventorySku.stockQty}" type="hidden"/>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            <c:if test="${reqMap[req] != null || transMap[req] != null}">
							    <tr><td colspan="13"><HR align=center width=100% color=#987cb9 SIZE=1></td></tr>
                            </c:if>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:split"><input id="btnSubmit" class="btn btn-primary" type="submit" onclick="merge()" value="合 并"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
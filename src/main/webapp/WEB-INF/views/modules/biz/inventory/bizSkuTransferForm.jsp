<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>库存调拨管理</title>
	<meta name="decorator" content="default"/>
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

			$("#searchData").click(function () {
                var skuName=$("#skuName").val();
                $("#skuNameCopy").val(skuName);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                var itemNo = $("#itemNo").val();
                $("#itemNoCopy").val(itemNo);
                var vendor=$("#vendor").val();
                $("#vendorCopy").val(vendor);
                var fromInv = $("#fromInv").val();
                $.ajax({
					type:"post",
					url:"${ctx}/biz/inventory/bizSkuTransfer/findInvSkuList?fromInv =" + fromInv,
					data:$('#searchForm').serialize(),
					success:function (data) {
						if (data == null) {
						    var html = "<p><span style='height: 100px; width: 100%'>原库存没有您查询的商品</span></p>";
						    $("#transferSku2").append(html);
                        }
                        if (data != null) {
						    var html = "";
						    $.each(data,function (index,skuInfo) {
								html += "<tr class='"+skuInfo.id+"'>" +
										"<td><img src='"+skuInfo.productInfo.imgUrl+"' width='100' height='100'/></td>" +
										"<td>"+skuInfo.productInfo.brandName+"</td>" +
										"<td>"+skuInfo.productInfo.office.name+"</td>" +
										"<td>"+skuInfo.name+"</td>" +
										"<td>"+skuInfo.partNo+"</td>" +
										"<td>"+skuInfo.itemNo+"</td>" +
										"<td><input type='hidden' id='skuId_"+skuInfo.id+"' value='"+skuInfo.id+"'/><input class='input-mini' id='transferNum_"+skuInfo.id+"' value='' type='number' min='1'/></td>" +
										"<td id='td_"+skuInfo.id+"'><a href='#' onclick='addItem("+skuInfo.id+")'>增加<a/></td>" +
										"</tr>";
                            });
						    $("#transferSku2").append(html);
                        }
                    }
				});
            });
		});

		function addItem(obj) {
			$("#td_"+obj).html("<a href='#' onclick='removeItem("+obj+")'>移除</a>");
            var trHtml=$("."+obj);
            $("#transferSku").append(trHtml);
            $("#transferSku").find($("#skuId_"+obj)).attr("name","skuIds");
            $("#transferSku").find($("#transferNum_"+obj)).attr("name","transferNums");
        }
        function removeItem(obj) {
            $("#td_"+obj).html("<a href='#' onclick='addItem("+obj+")'>增加</a>");
            var trHtml=$("."+obj);
            $("#transferSku2").append(trHtml);
            $("#transferSku").find($("#skuId_"+obj)).removeAttr("name");
            $("#transferSku").find($("#transferNum_"+obj)).removeAttr("name");
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/inventory/bizSkuTransfer/">库存调拨列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizSkuTransfer/form?id=${bizSkuTransfer.id}">库存调拨<shiro:hasPermission name="biz:inventory:bizSkuTransfer:edit">${not empty bizSkuTransfer.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizSkuTransfer:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizSkuTransfer" action="${ctx}/biz/inventory/bizSkuTransfer/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<c:if test="${bizSkuTransfer.id != null}">
			<div class="control-group">
				<label class="control-label">调拨单号：</label>
				<div class="controls">
					<form:input path="transferNo" cssClass="input-medium required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">原仓库：</label>
			<div class="controls">
				<form:select path="fromInv" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<c:forEach items="${fromInvList}" var="inv">
						<form:option label="${inv.name}" value="${inv.id}" htmlEscape="false"/>
					</c:forEach>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">目标仓库：</label>
			<div class="controls">
				<form:select path="toInv" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<c:forEach items="${toInvList}" var="inv">
						<form:option label="${inv.name}" value="${inv.id}" htmlEscape="false"/>
					</c:forEach>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">业务状态：</label>
			<div class="controls">
				<form:select path="bizStatus" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('transfer_bizStatus')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">期望收货时间：</label>
			<div class="controls">
				<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					value="<fmt:formatDate value="${bizSkuTransfer.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
				<ul class="inline ul-form">
					<li><label>商品名称：</label>
						<input id="skuName"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
					</li>
					<li><label>商品编码：</label>
						<input id="skuCode"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
					</li>
					<li><label>商品货号：</label>
						<input id="itemNo"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
					</li>
					<li><label>供应商：</label>
						<input id="vender" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
					</li>
					<li class="btns"><input id="searchData" class="btn btn-primary" type="button"  value="查询"/></li>
					<li class="clearfix"></li>
				</ul>

			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调拨商品：</label>
			<div class="controls">
				<table id="contentTable" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
					<thead>
						<th>产品图片</th>
						<th>品牌名称</th>
						<th>供应商</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品货号</th>
						<th>调拨数量</th>
						<c:if test="${bizSkuTransfer.str ne 'detail' && bizSkuTransfer.str ne 'audit'}">
							<th>操作</th>
						</c:if>
					</thead>
					<tbody id="transferSku">
						<c:if test="${transferDetailList != null}">
							<c:forEach items="${transferDetailList}" var="transferDetail">
								<td><img src="${transferDetail.skuInfo.productInfo.imgUrl}" width="100" height="100"/></td>
								<td>${transferDetail.skuInfo.productInfo.brandName}</td>
								<td>${transferDetail.skuInfo.productInfo.office.name}</td>
								<td>${transferDetail.skuInfo.name}</td>
								<td>${transferDetail.skuInfo.partNo}</td>
								<td>${transferDetail.skuInfo.itemNo}</td>
								<td><input name="transferNums" type="number" min="1" value=""/></td>
								<input name="skuIds" type="hidden" value="${transferDetail.id}"/>
								<c:if test="${bizSkuTransfer.str ne 'detail' && bizSkuTransfer.str ne 'audit'}">
									<td><a href="#" onclick="delItem(transferDetail.id)"></a>删除</td>
								</c:if>
							</c:forEach>
						</c:if>
					</tbody>
				</table>
				<table id="contentTable2" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
					<thead>
						<th>产品图片</th>
						<th>品牌名称</th>
						<th>供应商</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品货号</th>
						<th>调拨数量</th>
						<c:if test="${bizSkuTransfer.str ne 'detail' && bizSkuTransfer.str ne 'audit'}">
							<th>操作</th>
						</c:if>
					</thead>
					<tbody id="transferSku2">
					</tbody>
				</table>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:input path="remark" htmlEscape="false" maxlength="200" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizSkuTransfer:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	<form:form id="searchForm" modelAttribute="bizSkuInfo" >
		<form:hidden id="skuNameCopy" path="name"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>
		<form:hidden id="itemNoCopy" path="itemNo"/>
		<form:hidden id="venderCopy" path="productInfo.office.id"/>
	</form:form>
</body>
</html>
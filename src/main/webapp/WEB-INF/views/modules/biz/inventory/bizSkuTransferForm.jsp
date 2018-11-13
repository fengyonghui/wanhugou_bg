<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>库存调拨管理</title>
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
				    var transferSku = $("#transferSku").find("tr").length;
				    if (parseInt(transferSku) > parseInt(0)) {
                        $("#btnSubmit").attr("disabled","disabled ");
                        loading('正在提交，请稍等...');
                        form.submit();
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

			$("#searchData").click(function () {
			    $("#transferSku2").html("");
                var skuName=$("#skuName").val();
                $("#skuNameCopy").val(skuName);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                var itemNo = $("#itemNo").val();
                $("#itemNoCopy").val(itemNo);
                var vendor=$("#vendor").val();
                $("#vendorCopy").val(vendor);
                var fromInv = $("#fromInv option:selected").val();
                if (fromInv == "") {
                    alert("请先选择原仓库");
                    return false;
				}
				if (skuName == "" && skuCode == "" && itemNo == "" && vendor == "") {
                    alert("请输入查询条件");
                    return false;
				}
                $.ajax({
					type:"post",
					url:"${ctx}/biz/inventory/bizSkuTransfer/findInvSkuList?fromInv=" + fromInv,
					data:$('#searchForm').serialize(),
					success:function (data) {
                        if (data.length == 0) {
                            alert("原库存没有您查询的商品");
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
		function delItem(obj) {
			$.ajax({
				type:"post",
				url:"${ctx}/biz/inventory/bizSkuTransfer/deleteAjax?id=" + obj,
				success:function (data) {
					if (data == "ok") {
					    alert("删除成功");
					}
					if (data == "error") {
					    alert("删除失败");
					}
					window.location.reload();
                }
			});
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
					<form:input path="transferNo" readonly="true" cssClass="input-medium required"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">原仓库：</label>
			<div class="controls">
				<form:select id="fromInv" path="fromInv.id" class="input-medium required">
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
				<form:select path="toInv.id" class="input-medium required">
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
				<form:select path="bizStatus" class="input-medium required">
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
						<input id="vendor" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
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
								<tr>
									<td><img src="${transferDetail.skuInfo.productInfo.imgUrl}" width="100" height="100"/></td>
									<td>${transferDetail.skuInfo.productInfo.brandName}</td>
									<td>${transferDetail.skuInfo.productInfo.office.name}</td>
									<td>${transferDetail.skuInfo.name}</td>
									<td>${transferDetail.skuInfo.partNo}</td>
									<td>${transferDetail.skuInfo.itemNo}</td>
									<td><input name="transferNums" type="number" min="1" class="input-mini" value="${transferDetail.transQty}"/></td>
									<input name="skuIds" type="hidden" value="${transferDetail.skuInfo.id}"/>
									<c:if test="${bizSkuTransfer.str ne 'detail' && bizSkuTransfer.str ne 'audit'}">
										<td><a href="#" onclick="delItem(${transferDetail.id})">删除</a></td>
									</c:if>
								</tr>
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
        <c:if test="${bizSkuTransfer.str eq 'audit' && bizSkuTransfer.commonProcess.id != null}">
            <div class="control-group">
                <label class="control-label">审核状态：</label>
                <div class="controls">
                    <input type="text" disabled="disabled"
                           value="${transferProcess.name}" htmlEscape="false"
                           maxlength="30" class="input-xlarge "/>
                    <input id="currentType" type="hidden" disabled="disabled"
                           value="${transferProcess.code}" htmlEscape="false"
                           maxlength="30" class="input-xlarge "/>
                </div>
            </div>
        </c:if>
		<c:if test="${fn:length(statusList) > 0}">
			<div class="control-group">
				<label class="control-label">状态流程：</label>
				<div class="controls help_wrap">
					<div class="help_step_box fa">
						<c:forEach items="${statusList}" var="v" varStatus="stat">
							<c:if test="${!stat.last}" >
								<div class="help_step_item">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									处理人:${v.createBy.name}<br/><br/>
									状态:${statusMap[v.bizStatus].desc}<br/>
									<fmt:formatDate value="${v.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
									<div class="help_step_right"></div>
								</div>
							</c:if>
							<c:if test="${stat.last}">
								<div class="help_step_item help_step_set">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									处理人:${v.createBy.name}<br/><br/>
									状态:${statusMap[v.bizStatus].desc}<br/>
									<fmt:formatDate value="${v.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
									<div class="help_step_right"></div>
								</div>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</div>
		</c:if>
        <c:if test="${fn:length(bizSkuTransfer.commonProcessList) > 0}">
            <div class="control-group">
                <label class="control-label">审批流程：</label>
                <div class="controls help_wrap">
                    <div class="help_step_box fa">
                        <c:forEach items="${bizSkuTransfer.commonProcessList}" var="v" varStatus="stat">
                            <c:if test="${!stat.last}" >
                                <div class="help_step_item">
                                    <div class="help_step_left"></div>
                                    <div class="help_step_num">${stat.index + 1}</div>
                                    批注:${v.description}<br/><br/>
                                    审批人:${v.user.name}<br/>
                                    <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    <div class="help_step_right"></div>
                                </div>
                            </c:if>
                            <c:if test="${stat.last}">
                                <div class="help_step_item help_step_set">
                                    <div class="help_step_left"></div>
                                    <div class="help_step_num">${stat.index + 1}</div>
                                    当前状态:${v.transferProcess.name}<br/><br/>
                                        ${v.user.name}<br/>
                                    <div class="help_step_right"></div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<c:if test="${bizSkuTransfer.str ne 'detail' && bizSkuTransfer.str ne 'audit'}">
				<shiro:hasPermission name="biz:inventory:bizSkuTransfer:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			</c:if>
            <c:if test="${bizSkuTransfer.str eq 'audit'}">
                <shiro:hasPermission name="biz:inventory:bizSkuTransfer:audit">
                    <input class="btn btn-primary" type="button" onclick="checkPass()" value="审核通过"/>
                    <input class="btn btn-primary" type="button" onclick="checkReject()" value="审核驳回"/>
                </shiro:hasPermission>
            </c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	<form:form id="searchForm" modelAttribute="bizSkuInfo" >
		<input type="hidden" id="skuNameCopy" name="name" value=""/>
		<input type="hidden" id="skuCodeCopy" name="partNo" value=""/>
		<input type="hidden" id="itemNoCopy" name="itemNo" value=""/>
		<input type="hidden" id="vendorCopy" name="vendorName" value=""/>
	</form:form>
<script type="text/javascript">
    function checkPass() {
        var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认审核通过吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    audit(1, f.description);
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入通过理由:", submit: submit, loaded: function (h) {
            }
        });
    }
    function checkReject() {
        var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认驳回该流程吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    audit(2, f.description);
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入驳回理由:", submit: submit, loaded: function (h) {
            }
        });
    }
    function audit(auditType, description) {
        var id = $("#id").val();
        var currentType = $("#currentType").val();
        $.ajax({
            url: '${ctx}/biz/inventory/bizSkuTransfer/audit',
            contentType: 'application/json',
            data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description},
            type: 'get',
            success: function (result) {
                result = JSON.parse(result);
                if(result.ret == true || result.ret == 'true') {
                    alert('操作成功!');
                    window.location.href = "${ctx}/biz/inventory/bizSkuTransfer";
                }else {
                    alert(result.errmsg);
                }
            },
            error: function (error) {
                console.info(error);
            }
        });
    }
</script>
</body>
</html>
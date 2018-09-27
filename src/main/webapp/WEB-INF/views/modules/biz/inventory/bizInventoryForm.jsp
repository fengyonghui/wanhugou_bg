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
		    if ($("#source").val()!= '') {
		        $("input[name='actualQtys']").each(function () {
                   $(this).attr("disabled","true");
                });
            }
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
				    var i = 0;
				    $("#invReq").find("input[name='reqDetailId']").each(function () {
				        var reqDetailId = $(this).val();
						var actualQty = $(this).parent().find("input[name='actualQtys']").val();
						if (actualQty != 0) {
                            var html = "<input name='invReqDetail' value='" + reqDetailId + "-" + actualQty +"' type='hidden'/>";
						}
						$(this).append(html);
						i = parseInt(i) + parseInt(actualQty);
                    });
				    if (i == 0) {
				        alert("请至少添加一条实际库存");
				        return false;
					} else {
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/inventory/bizInventorySku/inventory">商品库存详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku/inventoryForm?id=${requestHeader.id}&invName=${requestHeader.invInfo.name}">商品库存详情<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">${not empty bizInventorySku.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInventorySku:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="BizRequestHeader" action="${ctx}/biz/inventory/bizInventorySku/inventorySave" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
        <input id="source" value="${source}" type="hidden"/>
        <input id="id" name="id" value="${requestHeader.id}" type="hidden"/>
		<input id="invId" name="invInfo.id" value="${requestHeader.invInfo.id}" type="hidden"/>
		<div class="control-group">
			<label class="control-label">备货单号：</label>
			<div class="controls">
				<input name="" readonly="readonly" value="${requestDetailList[0].requestHeader.reqNo}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<input name="" readonly="readonly" value="${requestDetailList[0].skuInfo.vendorName}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">所属仓库：</label>
			<div class="controls">
				<input name="" readonly="readonly" value="${requestHeader.invInfo.name}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品信息：</label>
			<div class="controls">
				<table id="contentTable"  class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>商品名称</th>
						<th>商品货号</th>
						<th>颜色</th>
						<th>尺寸</th>
						<th>库存类型</th>
						<th>结算价</th>
						<th>图片</th>
						<th>现有库存数</th>
                        <c:if test="${source eq 'pChange'}">
						    <th>出库数量</th>
                        </c:if>
						<th>实际库存数</th>
					</tr>
					</thead>
					<tbody id="invReq">
						<c:forEach items="${requestDetailList}" var="requestDetail" varStatus="v">
							<tr>
								<input name="reqDetailId" value="${requestDetail.id}" type="hidden"/>
								<td>${requestDetail.skuInfo.name}</td>
								<td>${requestDetail.skuInfo.itemNo}</td>
								<td>${requestDetail.skuInfo.color}</td>
								<td>${requestDetail.skuInfo.size}</td>
								<td>${fns:getDictLabel(requestHeader.headerType,'req_header_type','未知')}</td>
								<td>${requestDetail.skuInfo.buyPrice}</td>
								<td style='width: 200px'><img style='width: 200px' src="${requestDetail.skuInfo.skuImgUrl}"></td>
								<td>${requestDetail.recvQty - requestDetail.outQty}</td>
                                <c:if test="${source eq 'pChange'}">
								    <td>${requestDetail.sumSendNum == null ? 0 : requestDetail.sumSendNum}</td>
                                </c:if>
                                <td><input name="actualQtys" title="${v.index}" value="${requestDetail.actualQty}" type="number" class="input-mini required"/></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<c:if test="${source == 'audit' && requestHeader.invCommonProcess.id != null}">
			<div class="control-group">
				<label class="control-label">审核状态：</label>
				<div class="controls">
					<input type="text" disabled="disabled"
						   value="${requestHeader.invCommonProcess.invRequestProcess.name}" htmlEscape="false"
						   maxlength="30" class="input-xlarge "/>
					<input id="currentType" type="hidden" disabled="disabled"
						   value="${requestHeader.invCommonProcess.invRequestProcess.code}" htmlEscape="false"
						   maxlength="30" class="input-xlarge "/>
				</div>
			</div>
		</c:if>
		<c:if test="${fn:length(requestHeader.invCommonProcessList) > 0}">
		<div class="control-group">
			<label class="control-label">审核流程：</label>
			<div class="controls help_wrap">
				<div class="help_step_box fa">
					<c:forEach items="${requestHeader.invCommonProcessList}" var="v" varStatus="stat">
						<c:choose>
							<c:when test="${!stat.last && v.type == autProcessId}">
								<div class="help_step_item help_step_set">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									当前状态:审核完成<br/><br/>
									<br/>
									<div class="help_step_right"></div>
								</div>
							</c:when>
							<c:when test="${!stat.last && v.type != autProcessId}">
								<div class="help_step_item">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									批注:${v.description}<br/><br/>
									审批人:${v.user.name}<br/>
									<fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
									<div class="help_step_right"></div>
								</div>
							</c:when>
							<c:otherwise>
							</c:otherwise>
						</c:choose>
						<c:if test="${stat.last && v.type != autProcessId}">
							<div class="help_step_item help_step_set">
								<div class="help_step_left"></div>
								<div class="help_step_num">${stat.index + 1}</div>
									当前状态:${v.invRequestProcess.name}<br/><br/>
									${v.user.name}<br/>
								<div class="help_step_right"></div>
							</div>
						</c:if>
					</c:forEach>
				</div>
			</div>
		</div>
		</c:if>
		<div class="form-actions">
			<c:if test="${source == null}">
				<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			</c:if>
			<shiro:hasPermission name="biz:inventory:bizInventorySku:audit">
				<c:if test="${source == 'audit'}">
					<input id="btnSubmit" type="button" onclick="checkPass()" class="btn btn-primary" value="审核通过"/>
					<input id="btnSubmit" type="button" onclick="checkReject()" class="btn btn-primary" value="审核驳回"/>
				</c:if>
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
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
        var invId = $("#invId").val();
        $.ajax({
            url: '${ctx}/biz/inventory/bizInventorySku/audit',
            contentType: 'application/json',
            data: {"id": id, "invId": invId, "currentType": currentType, "auditType": auditType, "description": description},
            type: 'get',
            success: function (result) {
                result = JSON.parse(result);
                if(result.ret == true || result.ret == 'true') {
                    alert('操作成功!');
                    window.location.href = "${ctx}/biz/inventory/bizInventorySku/inventory";
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
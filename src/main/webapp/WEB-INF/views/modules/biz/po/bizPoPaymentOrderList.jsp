<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<html>
<head>
	<title>支付申请管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz.po/bizpopaymentorder/bizPoPaymentOrder/">支付申请列表</a></li>
	</ul>
	<form:form modelAttribute="bizPoPaymentOrder" action="${ctx}/biz/po/bizPoPaymentOrder/"
			   method="post" class="breadcrumb form-search">
		<input id="poHeaderId" name="poHeaderId" type="hidden" value="${bizPoPaymentOrder.poHeaderId}"/>
		<input id="orderType" name="orderType" type="hidden" value="${bizPoPaymentOrder.orderType}"/>
		<input id="fromPage" name="fromPage" type="hidden" value="${bizPoPaymentOrder.fromPage}"/>

		<div class="form-horizontal">
			<div class="control-group">
				<label class="control-label">订单/备货单号：</label>
				<div class="controls">
					<input type="text" disabled="disabled" value="${headerNum}" htmlEscape="false"
						   maxlength="30" class="input-xlarge "/>
				</div>
			</div>
		</div>
	</form:form>
	<sys:message content="${message}"/>

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>id</th>
				<th>付款金额</th>
				<th>实际付款金额</th>
				<th>最后付款时间</th>
				<th>实际付款时间</th>
				<th>当前状态</th>
				<th>单次支付审批状态</th>
				<th>备注</th>
				<th>支付凭证</th>
				<shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPoPaymentOrder">
			<tr>
				<td>
					${bizPoPaymentOrder.id}
				</td>
				<td>
					${bizPoPaymentOrder.total}
				</td>
				<td>
					${bizPoPaymentOrder.payTotal}
				</td>
				<td>
					<fmt:formatDate value="${bizPoPaymentOrder.deadline}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
                <td>
					<fmt:formatDate value="${bizPoPaymentOrder.payTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizPoPaymentOrder.bizStatus == 0 ? '未支付' : '已支付'}
				</td>
				<td>
					<c:if test="${bizPoPaymentOrder.total == '0.00' && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成'}">
					待确认支付金额
					</c:if>
					<c:if test="${bizPoPaymentOrder.total != '0.00'}">
						${bizPoPaymentOrder.commonProcess.paymentOrderProcess.name}
					</c:if>
				</td>
				<td>
						${bizPoPaymentOrder.remark}
				</td>
				<td>
					<c:forEach items="${bizPoPaymentOrder.imgList}" var="v">
						<a target="_blank" href="${v.imgServer}${v.imgPath}"><img style="width: 100px" src="${v.imgServer}${v.imgPath}"/></a>
					</c:forEach>
				</td>
				<td>
				<a href="${ctx}/biz/po/bizPoPaymentOrder/formV2?id=${bizPoPaymentOrder.id}">详情</a>
				<shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:audit">
					<c:if test="${bizPoPaymentOrder.hasRole == true}">
					<c:if test="${bizPoPaymentOrder.total != '0.00'}">
						<c:if test="${bizPoPaymentOrder.id == bizPoHeader.bizPoPaymentOrder.id && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成' && bizPoPaymentOrder.total != 0}">
												<%--&& (fns:hasRole(roleSet, bizPoPaymentOrder.commonProcess.paymentOrderProcess.moneyRole.roleEnNameEnum))--%>
							<a href="#" onclick="checkPass(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.orderType})">审核通过</a>
							<a href="#" onclick="checkReject(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.orderType})">审核驳回</a>
						</c:if>
					</c:if>
					</c:if>
					<%--<c:if test="${bizPoPaymentOrder.id == bizRequestHeader.bizPoPaymentOrder.id && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成'}">--%>
						<%--<a onclick="checkPass(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.type})">审核通过</a>--%>
						<%--<a onclick="checkReject(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.type})">审核驳回</a>--%>
					<%--</c:if>--%>
				</shiro:hasPermission>

				<shiro:hasPermission name="biz:po:sure:bizPoPaymentOrder">
					<c:if test="${fromPage == 'requestHeader' && bizPoPaymentOrder.total == '0.00' && (requestHeader == null || requestHeader.bizStatus < ReqHeaderStatusEnum.CLOSE.state)}">
						<a href="${ctx}/biz/po/bizPoPaymentOrder/form?id=${bizPoPaymentOrder.id}&poHeaderId=${bizPoHeader.id}&fromPage=${fromPage}">确认支付金额</a>
					</c:if>

					<c:if test="${fromPage == 'orderHeader' && bizPoPaymentOrder.total == '0.00'}">
						<a href="${ctx}/biz/po/bizPoPaymentOrder/form?id=${bizPoPaymentOrder.id}&poHeaderId=${bizPoHeader.id}&fromPage=${fromPage}">确认支付金额</a>
					</c:if>
				</shiro:hasPermission>
				<shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:edit">
					<shiro:hasPermission name="biz:po:payment:sure:pay">
						<c:if test="${bizPoPaymentOrder.orderType == PoPayMentOrderTypeEnum.PO_TYPE.type && bizPoPaymentOrder.id == bizPoHeader.bizPoPaymentOrder.id
						&& bizPoPaymentOrder.commonProcess.paymentOrderProcess.name == '审批完成'
						&& bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'
						}">
						<c:if test="${fromPage != null && fromPage == 'requestHeader'}">
							<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?bizPoHeader.id=${bizPoHeader.id}&str=pay">确认付款</a>
						</c:if>
						<c:if test="${fromPage != null && fromPage == 'orderHeader'}">
							<a href="${ctx}/biz/order/bizOrderHeader/form?bizPoHeader.id=${bizPoHeader.id}&id=${orderId}&str=pay">确认付款</a>
							<%--<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=pay">确认付款</a>--%>
						</c:if>
						<c:if test="${fromPage == null}">
							<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=pay">确认付款</a>
						</c:if>
						</c:if>
				</shiro:hasPermission>
					<%--<c:if test="${bizPoPaymentOrder.type == PoPayMentOrderTypeEnum.REQ_TYPE.type && bizPoPaymentOrder.id == bizRequestHeader.bizPoPaymentOrder.id--%>
						  <%--&& bizRequestHeader.commonProcess.vendRequestOrderProcess.name == '审批完成'--%>
						  <%--&& bizPoPaymentOrder.commonProcess.paymentOrderProcess.name == '审批完成'}">--%>
						<%--<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizRequestHeader.id}&str=pay">确认付款</a>--%>
					<%--</c:if>--%>
				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div ><input type="button" class="btn" onclick="window.history.go(-1);" value="返回"/></div>
	<div class="pagination">${page}</div>
	<script type="text/javascript">
            function checkPass(poPayId, currentType, money,type) {
                var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
                var submit = function (v, h, f) {
                    if ($String.isNullOrBlank(f.description)) {
                        jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                        return false;
                    }
                    top.$.jBox.confirm("确认审核通过吗？", "系统提示", function (v1, h1, f1) {
                        if (v1 == "ok") {
                            audit(1, f.description, poPayId, currentType, money,type);
                        }
                    }, {buttonsFocus: 1});
                    return true;
                };

                jBox(html, {
                    title: "请输入通过理由:", submit: submit, loaded: function (h) {
                    }
                });

            }

            function checkReject(poPayId, currentType, money,type) {
                var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
                var submit = function (v, h, f) {
                    if ($String.isNullOrBlank(f.description)) {
                        jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                        return false;
                    }
                    top.$.jBox.confirm("确认驳回该流程吗？", "系统提示", function (v1, h1, f1) {
                        if (v1 == "ok") {
                            audit(2, f.description, poPayId, currentType, money,type);
                        }
                    }, {buttonsFocus: 1});
                    return true;
                };

                jBox(html, {
                    title: "请输入驳回理由:", submit: submit, loaded: function (h) {
                    }
                });

            }

            function audit(auditType, description, poPayId, currentType, money,type) {
				var poHeaderId = $("#poHeaderId").val();
				var orderType = $("#orderType").val();
				var fromPage = $("#fromPage").val();
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/auditPay',
                    contentType: 'application/json',
                    data: {"poPayId": poPayId, "currentType": currentType, "auditType": auditType, "description": description, "money": money},
                    type: 'get',
                    success: function (result) {
                        result = JSON.parse(result);
                        if(result.ret == true || result.ret == 'true') {
                            alert('操作成功!');
                            if('${fromPage != null}') {
                                <%--window.location.href = "${ctx}/biz/po/bizPoHeader/listV2";--%>
                                <%--window.location.href = "${ctx}/biz/po/bizPoPaymentOrder/list?poId=" + poHeaderId + "&orderType=" + orderType + "&fromPage=" + fromPage;--%>
                                if (fromPage == "orderHeader") {
                                    window.location.href = "${ctx}/biz/order/bizOrderHeader/";
                                }
                                if (fromPage == "requestHeader") {
                                    window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor";
                                }
                            } else {
                                window.location.href = "${ctx}/biz/po/bizPoHeader";
                            }
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
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum" %>
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
				<th>审批状态</th>
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
					${bizPoPaymentOrder.commonProcess.paymentOrderProcess.name}
				</td>
				<td>
					<c:forEach items="${bizPoPaymentOrder.imgList}" var="v">
						<a target="_blank" href="${v.imgServer}${v.imgPath}"><img style="width: 100px" src="${v.imgServer}${v.imgPath}"/></a>
					</c:forEach>
				</td>
				<td>
				<shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:audit">
					<c:if test="${bizPoPaymentOrder.id == bizPoHeader.bizPoPaymentOrder.id && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成'}">
						<a onclick="checkPass(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.type})">审核通过</a>
						<a onclick="checkReject(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.type})">审核驳回</a>
					</c:if>
					<c:if test="${bizPoPaymentOrder.id == bizRequestHeader.bizPoPaymentOrder.id && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name != '审批完成'}">
						<a onclick="checkPass(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.type})">审核通过</a>
						<a onclick="checkReject(${bizPoPaymentOrder.id}, ${bizPoPaymentOrder.commonProcess.paymentOrderProcess.code}, ${bizPoPaymentOrder.total},${bizPoPaymentOrder.type})">审核驳回</a>
					</c:if>
				</shiro:hasPermission>
				<shiro:hasPermission name="biz:po:bizpopaymentorder:bizPoPaymentOrder:edit">
					<c:if test="${bizPoPaymentOrder.type == PoPayMentOrderTypeEnum.PO_TYPE.type && bizPoPaymentOrder.id == bizPoHeader.bizPoPaymentOrder.id
					&& bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'
					&& bizPoPaymentOrder.commonProcess.paymentOrderProcess.name == '审批完成'
					}">
						<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}&type=pay">确认付款</a>
					</c:if>
					<c:if test="${bizPoPaymentOrder.type == PoPayMentOrderTypeEnum.REQ_TYPE.type && bizPoPaymentOrder.id == bizRequestHeader.bizPoPaymentOrder.id
						  && BizPoPaymentOrder.BizStatus.NO_PAY.desc == '未支付'
						  && bizRequestHeader.commonProcess.vendRequestOrderProcess.name == '审批完成'
						  && bizPoPaymentOrder.commonProcess.paymentOrderProcess.name == '审批完成'}">
						<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizRequestHeader.id}&str=pay">确认付款</a>
					</c:if>
				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div ><input type="button" class="btn" onclick="window.history.go(-1);" value="返回"/></div>
	<div class="pagination">${page}</div>
	<script type="text/javascript">
            function checkPass(id, currentType, money) {
                var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
                var submit = function (v, h, f) {
                    if ($String.isNullOrBlank(f.description)) {
                        jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                        return false;
                    }
                    top.$.jBox.confirm("确认审核通过吗？", "系统提示", function (v1, h1, f1) {
                        if (v1 == "ok") {
                            audit(1, f.description, id, currentType, money);
                        }
                    }, {buttonsFocus: 1});
                    return true;
                };

                jBox(html, {
                    title: "请输入通过理由:", submit: submit, loaded: function (h) {
                    }
                });

            }

            function checkReject(id, currentType, money) {
                var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
                var submit = function (v, h, f) {
                    if ($String.isNullOrBlank(f.description)) {
                        jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                        return false;
                    }
                    top.$.jBox.confirm("确认驳回该流程吗？", "系统提示", function (v1, h1, f1) {
                        if (v1 == "ok") {
                            audit(2, f.description, id, currentType, money);
                        }
                    }, {buttonsFocus: 1});
                    return true;
                };

                jBox(html, {
                    title: "请输入驳回理由:", submit: submit, loaded: function (h) {
                    }
                });

            }

            function audit(auditType, description, id, currentType, money) {
                $.ajax({
                    url: '${ctx}/biz/po/bizPoHeader/auditPay',
                    contentType: 'application/json',
                    data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description, "money": money},
                    type: 'get',
                    success: function (result) {
                        result = JSON.parse(result);
                        if(result.ret == true || result.ret == 'true') {
                            alert('操作成功!');
                            window.location.href = "${ctx}/biz/po/bizPoHeader";
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
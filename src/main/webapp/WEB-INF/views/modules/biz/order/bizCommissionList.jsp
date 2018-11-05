<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>佣金付款表管理</title>
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

	<script type="text/javascript">
        function checkPass(commId, currentType, money) {
            var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
            var submit = function (v, h, f) {
                if ($String.isNullOrBlank(f.description)) {
                    jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                    return false;
                }
                top.$.jBox.confirm("确认审核通过吗？", "系统提示", function (v1, h1, f1) {
                    if (v1 == "ok") {
                        audit(1, f.description, commId, currentType, money);
                    }
                }, {buttonsFocus: 1});
                return true;
            };

            jBox(html, {
                title: "请输入通过理由:", submit: submit, loaded: function (h) {
                }
            });

        }

        function checkReject(commId, currentType, money) {
            var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
            var submit = function (v, h, f) {
                if ($String.isNullOrBlank(f.description)) {
                    jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                    return false;
                }
                top.$.jBox.confirm("确认驳回该流程吗？", "系统提示", function (v1, h1, f1) {
                    if (v1 == "ok") {
                        audit(2, f.description, commId, currentType, money);
                    }
                }, {buttonsFocus: 1});
                return true;
            };

            jBox(html, {
                title: "请输入驳回理由:", submit: submit, loaded: function (h) {
                }
            });

        }

        function audit(auditType, description, commId, currentType, money) {
            $.ajax({
                url: '${ctx}/biz/order/bizCommission/auditPay',
                contentType: 'application/json',
                data: {"commId": commId, "currentType": currentType, "auditType": auditType, "description": description, "money": money},
                type: 'get',
                success: function (result) {
                    result = JSON.parse(result);
                    if(result.ret == true || result.ret == 'true') {
                        alert('操作成功!');
                        window.location.href = "${ctx}/biz/order/bizCommission";
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
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz/order/bizCommission/">佣金付款表列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCommission" action="${ctx}/biz/order/bizCommission/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>

		<ul class="ul-form">
			<li><label>代销商：</label>
				<form:input path="customerName" htmlEscape="false" maxlength="25" class="input-medium"/>
			</li>
			<li><span style="margin-left: 10px"><label>订单编号：</label></span>
				<form:input path="orderNum"  htmlEscape="false" maxlength="25" class="input-medium"/>
			</li>

			<li><label>结佣状态：</label>
				<form:select path="bizStatus" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_commission_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>

			<%--<li><label>审核状态：</label>--%>
				<%--<form:select path="commonProcess.type" class="input-medium">--%>
					<%--<form:option value="" label="请选择"/>--%>
					<%--<form:options items="${processList}" itemLabel="name" itemValue="code" htmlEscape="false"/>--%>
				<%--</form:select>--%>
			<%--</li>--%>

			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>id</th>
				<th>付款金额</th>
				<th>实际付款金额</th>
				<th>代销商</th>
				<th>所属单号</th>
				<th>最后付款时间</th>
				<th>实际付款时间</th>
				<th>当前状态</th>
				<th>单次支付审批状态</th>
				<th>备注</th>
				<th>支付凭证</th>
				<th>操作</th>
				<%--<shiro:hasPermission name="biz:order:bizCommission:edit"><th>操作</th></shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCommission">
			<tr>
				<td>
						${bizCommission.id}
				</td>
				<td>
						${bizCommission.totalCommission}
				</td>
				<td>
						${bizCommission.payTotal}
				</td>
				<td>
						${bizCommission.customer.name}
				</td>
				<td>
						${bizCommission.orderNumsStr}
				</td>
				<td>
					<fmt:formatDate value="${bizCommission.deadline}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bizCommission.payTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
						${bizCommission.bizStatus == 0 ? '未支付' : '已支付'}
				</td>
				<td>
					<c:if test="${bizCommission.totalCommission == '0.00' && bizCommission.commonProcess.paymentOrderProcess.name != '审批完成'}">
						待确认支付金额
					</c:if>
					<c:if test="${bizCommission.totalCommission != '0.00'}">
						${bizCommission.commonProcess.paymentOrderProcess.name}
					</c:if>
				</td>
				<td>
						${bizCommission.remark}
				</td>
				<td>
					<c:forEach items="${bizCommission.imgList}" var="v">
						<a target="_blank" href="${v.imgServer}${v.imgPath}"><img style="width: 100px" src="${v.imgServer}${v.imgPath}"/></a>
					</c:forEach>
				</td>
				<td>
					<shiro:hasPermission name="biz:order:sure:bizCommission">
						<c:if test="${bizCommission.payTotal == '0.00'}">
							<a href="${ctx}/biz/order/bizCommission/form?id=${bizCommission.id}">确认支付金额</a>
						</c:if>
					</shiro:hasPermission>

					<shiro:hasPermission name="biz:order:bizCommission:audit">
						<c:if test="${bizCommission.commonProcess.paymentOrderProcess.name != '驳回'}">
							<c:if test="${bizCommission.payTotal != '0.00' && bizCommission.commonProcess.paymentOrderProcess.name != '审批完成' && bizCommission.totalCommission != 0}">
								<a href="#" onclick="checkPass(${bizCommission.id}, ${bizCommission.commonProcess.paymentOrderProcess.code}, ${bizCommission.totalCommission})">审核通过</a>
								<a href="#" onclick="checkReject(${bizCommission.id}, ${bizCommission.commonProcess.paymentOrderProcess.code}, ${bizCommission.totalCommission})">审核驳回</a>
							</c:if>
						</c:if>
					</shiro:hasPermission>

					<shiro:hasPermission name="biz:order:bizCommission:sure:pay">
						<c:if test="${bizCommission.commonProcess.paymentOrderProcess.name == '审批完成' && bizCommission.bizStatus == '0'}">
							<a href="${ctx}/biz/order/bizCommission/form?id=${bizCommission.id}&str=pay">确认付款</a>
						</c:if>
						<c:if test="${bizCommission.commonProcess.paymentOrderProcess.name == '审批完成' && bizCommission.bizStatus == '1'}">
							支付完成
						</c:if>
					</shiro:hasPermission>

					&nbsp;&nbsp;
					<a href="${ctx}/biz/order/bizCommission/form?id=${bizCommission.id}&str=detail">详情</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
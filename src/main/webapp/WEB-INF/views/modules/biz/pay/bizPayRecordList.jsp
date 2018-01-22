<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易记录管理</title>
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
		<li class="active"><a href="${ctx}/biz/pay/bizPayRecord/">交易记录列表</a></li>
		<shiro:hasPermission name="biz:pay:bizPayRecord:edit"><li><a href="${ctx}/biz/pay/bizPayRecord/form">交易记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPayRecord" action="${ctx}/biz/pay/bizPayRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>订单编号：</label>
				<form:input path="payNum" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>支付宝或微信的业务流水号：</label>
				<form:input path="outTradeNo" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>支付金额：</label>
				<form:input path="payMoney" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>支付人：</label>
				<form:input path="payer" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>客户ID：</label>
				<form:input path="custId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>支付状态：</label>
				<form:input path="bizStatus" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>支付账号：</label>
				<form:input path="account" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>支付到账户：</label>
				<form:input path="toAccount" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>交易类型：充值、体现、支付：</label>
				<form:input path="recordType" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>交易类型名称：</label>
				<form:input path="recordTypeName" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>支付类型：wx(微信) alipay(支付宝)：</label>
				<form:input path="payType" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>支付类型名称：</label>
				<form:input path="payTypeName" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>交易作用/原因：</label>
				<form:input path="tradeReason" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>记录状态：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li><label>创建人：</label>
				<form:input path="createId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>创建时间：</label>
				<input name="createTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizPayRecord.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>版本控制：</label>
				<form:input path="uVersion" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>id</th>
				<th>订单编号</th>
				<th>支付宝或微信的业务流水号</th>
				<th>支付金额</th>
				<th>支付人</th>
				<th>客户ID</th>
				<th>支付状态</th>
				<th>支付账号</th>
				<th>支付到账户</th>
				<th>交易类型：充值、体现、支付</th>
				<th>交易类型名称</th>
				<th>支付类型：wx(微信) alipay(支付宝)</th>
				<th>支付类型名称</th>
				<th>交易作用/原因</th>
				<th>记录状态</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>版本控制</th>
				<th>update_id</th>
				<th>update_time</th>
				<shiro:hasPermission name="biz:pay:bizPayRecord:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPayRecord">
			<tr>
				<td><a href="${ctx}/biz/pay/bizPayRecord/form?id=${bizPayRecord.id}">
					${bizPayRecord.id}
				</a></td>
				<td>
					${bizPayRecord.payNum}
				</td>
				<td>
					${bizPayRecord.outTradeNo}
				</td>
				<td>
					${bizPayRecord.payMoney}
				</td>
				<td>
					${bizPayRecord.payer}
				</td>
				<td>
					${bizPayRecord.custId}
				</td>
				<td>
					${bizPayRecord.bizStatus}
				</td>
				<td>
					${bizPayRecord.account}
				</td>
				<td>
					${bizPayRecord.toAccount}
				</td>
				<td>
					${bizPayRecord.recordType}
				</td>
				<td>
					${bizPayRecord.recordTypeName}
				</td>
				<td>
					${bizPayRecord.payType}
				</td>
				<td>
					${bizPayRecord.payTypeName}
				</td>
				<td>
					${bizPayRecord.tradeReason}
				</td>
				<td>
					${fns:getDictLabel(bizPayRecord.status, 'status', '')}
				</td>
				<td>
					${bizPayRecord.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizPayRecord.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizPayRecord.uVersion}
				</td>
				<td>
					${bizPayRecord.updateId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizPayRecord.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:pay:bizPayRecord:edit"><td>
    				<a href="${ctx}/biz/pay/bizPayRecord/form?id=${bizPayRecord.id}">修改</a>
					<a href="${ctx}/biz/pay/bizPayRecord/delete?id=${bizPayRecord.id}" onclick="return confirmx('确认要删除该交易记录吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
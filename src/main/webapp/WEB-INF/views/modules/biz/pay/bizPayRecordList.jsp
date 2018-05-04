<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>交易记录管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#payBtnExport").click(function(){
                top.$.jBox.confirm("确认要导出交易记录吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/biz/pay/bizPayRecord/payBtnExport");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action","${ctx}/biz/pay/bizPayRecord/");
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });
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
		<%--<shiro:hasPermission name="biz:pay:bizPayRecord:edit"><li><a href="${ctx}/biz/pay/bizPayRecord/form">交易记录添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPayRecord" action="${ctx}/biz/pay/bizPayRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>订单编号：</label>
				<form:input path="payNum" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>业务流水号：</label>
				<form:input path="outTradeNo" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>支付金额：</label>
				<form:input path="payMoney" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>客户名称：</label>
				<form:input path="customer.name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>支付状态：</label>
				<form:select path="bizStatus" class="input-medium">
				<form:option value="" label="请选择"/>
				<form:options items="${fns:getDictList('bizStatus')}" itemLabel="label" itemValue="value"
							  htmlEscape="false"/>
				</form:select>
			</li>
			<%--<li><label>支付账号：</label>--%>
				<%--<form:input path="account" htmlEscape="false" maxlength="50" class="input-medium"/>--%>
			<%--</li>--%>
			<%--<li><label>支付到账户：</label>--%>
				<%--<form:input path="toAccount" htmlEscape="false" maxlength="50" class="input-medium"/>--%>
			<%--</li>--%>
				<li><label>支付时间：</label>
					<input name="trandStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
						   value="<fmt:formatDate value="${bizPayRecord.trandStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
					至
					<input name="trandEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
						   value="<fmt:formatDate value="${bizPayRecord.trandEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				</li>
			<li><label>交易类型：</label>
				<form:select path="recordType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('recordType')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>支付类型：</label>
				<form:select path="payType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('payType')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>支付人：</label>
				<form:input path="createBy.name" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>联系电话：</label>
				<form:input path="customer.moblieMoeny.mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>采购中心：</label>
				<form:input path="custConsultant.centers.name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>支付账号：</label>
				<form:input path="account.name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input id="payBtnExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>订单编号</th>
				<th>业务流水号</th>
				<th>支付金额</th>
				<th>支付人</th>
				<th>客户名称</th>
				<th>采购中心</th>
				<th>联系电话</th>
				<th>支付账号</th>
				<th>支付到账户</th>
				<th>交易类型名称</th>
				<th>支付类型名称</th>
				<th>交易作用/原因</th>
				<th>交易时间</th>
				<shiro:hasPermission name="biz:pay:bizPayRecord:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPayRecord" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
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
						${bizPayRecord.createBy.name}
				</td>
				<td>
					${bizPayRecord.customer.name}
				</td>
				<td>
					${bizPayRecord.custConsultant.centers.name}
				</td>
				<td>
					${bizPayRecord.customer.moblieMoeny.mobile}
				</td>
				<td>
					${bizPayRecord.account.name}
				</td>
				<td>
					<%--<c:choose>--%>
					<%--<c:when test="${bizPayRecord.toAccount == null } ">${bizPayRecord.toAccount}</c:when>--%>
					<%--<c:otherwise>${bizPayRecord.toAccount.name}</c:otherwise>--%>
					<%--</c:choose>--%>
					${bizPayRecord.toAccount.name}
				</td>
				<td>
					${bizPayRecord.recordTypeName}
				</td>
				<td>
					${bizPayRecord.payTypeName}
				</td>
				<td>
					${bizPayRecord.tradeReason}
				</td>
				<td>
					<fmt:formatDate value="${bizPayRecord.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:pay:bizPayRecord:edit"><td>
    				<%--<a href="${ctx}/biz/pay/bizPayRecord/form?id=${bizPayRecord.id}">修改</a>--%>
					<c:if test="${bizPayRecord.delFlag!=null && bizPayRecord.delFlag!=0}">
						<a href="${ctx}/biz/pay/bizPayRecord/delete?id=${bizPayRecord.id}" onclick="return confirmx('确认要删除该交易记录吗？', this.href)">删除</a>
					</c:if>
					<c:if test="${bizPayRecord.delFlag!=null && bizPayRecord.delFlag==0}">
						<a href="${ctx}/biz/pay/bizPayRecord/recovery?id=${bizPayRecord.id}" onclick="return confirmx('确认要恢复该交易记录吗？', this.href)">恢复</a>
					</c:if>

				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
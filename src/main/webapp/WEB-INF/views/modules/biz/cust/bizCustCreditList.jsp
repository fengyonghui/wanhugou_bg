<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>钱包管理</title>
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
		<li class="active"><a href="${ctx}/biz/cust/bizCustCredit/">钱包列表</a></li>
		<shiro:hasPermission name="biz:cust:bizCustCredit:edit"><li><a href="${ctx}/biz/cust/bizCustCredit/form">钱包添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCustCredit" action="${ctx}/biz/cust/bizCustCredit/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>客户名称：</label>
				<sys:treeselect id="customer" name="customer.id" value="" labelName="office.name" labelValue=""
					title="部门" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>会员类型：</label>
				<form:select path="level" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_cust')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/></form:select>
			</li>
			<li><label>手机号：</label>
				<form:input path="customer.moblieMoeny.mobile" htmlEscape="false" maxlength="15" class="input-medium"/>
			</li>
			<%--<li><label>支付宝账号：</label>--%>
				<%--<form:input path="aliAccount" htmlEscape="false" maxlength="30" class="input-medium"/>--%>
			<%--</li>--%>
			<%--<li><label>支付宝用户姓名：</label>--%>
				<%--<form:input path="aliName" htmlEscape="false" maxlength="20" class="input-medium"/>--%>
			<%--</li>--%>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>客户名称</th>
				<th>会员类型</th>
				<th>联系方式</th>
				<th>客户信用值</th>
				<th>客户钱包</th>
				<th>万户币</th>
				<%--<th>支付宝账号</th>--%>
				<%--<th>支付宝用户姓名</th>--%>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新人</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:cust:bizCustCredit:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="custCredit">
			<tr>
				<td>
					<%--<a href="${ctx}/biz/cust/bizCustCredit/form?customer.id=${custCredit.id}">--%>
					<%--${custCredit.customer.name}</a>--%>
					${custCredit.customer.name}
				</td>
				<td>
					${fns:getDictLabel(custCredit.level, 'biz_cust', '未知状态')}
				</td>
				<td>
					${custCredit.customer.moblieMoeny.mobile}
				</td>
				<td>
					${custCredit.credit}
				</td>
				<td>
					${custCredit.wallet}
				</td>
				<td>
					${custCredit.money}
				</td>
				<%--<td>--%>
					<%--${custCredit.aliAccount}--%>
				<%--</td>--%>
				<%--<td>--%>
					<%--${custCredit.aliName}--%>
				<%--</td>--%>
				<td>
					${custCredit.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${custCredit.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${custCredit.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${custCredit.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:cust:bizCustCredit:edit"><td>
    				<%--<a href="${ctx}/biz/cust/bizCustCredit/form?customer.id=${custCredit.id}">修改</a>--%>
					<c:if test="${fns:getUser().isAdmin()}">
					<a href="${ctx}/biz/cust/bizCustCredit/delete?customer.id=${custCredit.customer.id}" onclick="return confirmx('确认要删除该钱包吗？', this.href)">删除</a>
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
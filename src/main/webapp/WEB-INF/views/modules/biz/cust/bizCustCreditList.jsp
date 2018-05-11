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
	</ul>
	<form:form id="searchForm" modelAttribute="bizCustCredit" action="${ctx}/biz/cust/bizCustCredit/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>客户名称：</label>
				<sys:treeselect id="customer" name="customer.id" value="${bizCustCredit.customer.id}"
								labelName="customer.name" labelValue="${bizCustCredit.customer.name}"
					title="客户" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
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
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
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
				<c:if test="${fns:getUser().isAdmin()}">
					<shiro:hasPermission name="biz:cust:bizCustCredit:edit"><th>操作</th></shiro:hasPermission>
				</c:if>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="custCredit" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
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
				<c:if test="${fns:getUser().isAdmin()}">
					<shiro:hasPermission name="biz:cust:bizCustCredit:edit"><td>
						<c:if test="${custCredit.delFlag!=null && custCredit.delFlag!=0}">
							<a href="${ctx}/biz/cust/bizCustCredit/form?customer.id=${custCredit.customer.id}">修改</a>
							<a href="${ctx}/biz/cust/bizCustCredit/delete?customer.id=${custCredit.customer.id}" onclick="return confirmx('确认要删除该钱包吗？', this.href)">删除</a>
						</c:if>
						<c:if test="${custCredit.delFlag!=null && custCredit.delFlag==0}">
							<a href="${ctx}/biz/cust/bizCustCredit/recovery?customer.id=${custCredit.customer.id}" onclick="return confirmx('确认要恢复该钱包吗？', this.href)">恢复</a>
						</c:if>
					</td></shiro:hasPermission>
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
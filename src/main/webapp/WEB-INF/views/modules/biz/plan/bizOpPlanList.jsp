<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运营计划管理</title>
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
		<li class="active"><a href="${ctx}/biz/plan/bizOpPlan/">运营计划列表</a></li>
		<shiro:hasPermission name="biz:plan:bizOpPlan:edit"><li><a href="${ctx}/biz/plan/bizOpPlan/form">运营计划添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOpPlan" action="${ctx}/biz/plan/bizOpPlan/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>关联ID：</label>
				<form:input path="objectId" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>关联表名：</label>
				<form:input path="objectName" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>年：</label>
				<form:input path="year" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>月：</label>
				<form:input path="month" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>日：</label>
				<form:input path="day" htmlEscape="false" maxlength="50" class="input-medium"/>
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
				<th>关联ID</th>
				<th>关联表名</th>
				<th>年</th>
				<th>月</th>
				<th>日</th>
				<th>总额</th>
				<shiro:hasPermission name="biz:plan:bizOpPlan:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOpPlan">
			<tr>
				<td>
					${bizOpPlan.id}
				</td>
				<td>
					${bizOpPlan.objectId}
				</td>
				<td>
					${bizOpPlan.objectName}
				</td>
				<td>
					${bizOpPlan.year}
				</td>
				<td>
					${bizOpPlan.month}
				</td>
				<td>
					${bizOpPlan.day}
				</td>
				<td>
					${bizOpPlan.amount}
				</td>
				<shiro:hasPermission name="biz:plan:bizOpPlan:edit"><td>
    				<a href="${ctx}/biz/plan/bizOpPlan/form?id=${bizOpPlan.id}">修改</a>
					<a href="${ctx}/biz/plan/bizOpPlan/delete?id=${bizOpPlan.id}" onclick="return confirmx('确认要删除该运营计划吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
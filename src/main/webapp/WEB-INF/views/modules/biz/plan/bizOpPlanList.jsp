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
		<li><label>采购中心：</label>
			<form:input path="objectName1" placeholder="请输入采购中心" htmlEscape="false" maxlength="60" class="input-medium"/>
		</li>
		<li><label>客户专员：</label>
			<form:input path="objectName2" placeholder="请输入客户专员" htmlEscape="false" maxlength="60" class="input-medium"/>
		</li>
		<li><label>年：</label>
			<form:input path="year" htmlEscape="false" placeholder="请输入年 如（2018）" maxlength="5" class="input-medium"/>
		</li>
		<li><label>月：</label>
			<form:input path="month" htmlEscape="false" placeholder="请输入月 如（03）" maxlength="5" class="input-medium"/>
		</li>
		<li><label>日：</label>
			<form:input path="day" htmlEscape="false" placeholder="请输入日 如（20）" maxlength="5" class="input-medium"/>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
	<tr>
		<th>名称</th>
		<th>年</th>
		<th>月</th>
		<th>日</th>
		<th>总额</th>
		<th>创建时间</th>
		<shiro:hasPermission name="biz:plan:bizOpPlan:edit"><th>操作</th></shiro:hasPermission>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="bizOpPlan">
		<tr>
			<td>
				<c:choose>
					<c:when test="${bizOpPlan.objectName == 'sys_office'}">
						${bizOpPlan.objectName1}
					</c:when>
					<c:when test="${bizOpPlan.objectName == 'sys_user'}">
						${bizOpPlan.objectName2}
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
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
			<td>
				<fmt:formatDate value="${bizOpPlan.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
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
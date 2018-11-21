<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费--配送方式管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizServiceCharge/">服务费--配送方式列表</a></li>
		<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><li><a href="${ctx}/biz/order/bizServiceCharge/form">服务费--配送方式添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizServiceCharge" action="${ctx}/biz/order/bizServiceCharge/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>分类：</label>
				<form:input path="varietyInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><lable>服务方式</lable>
				<from:select path="serviceMode" class="input-mini required">
					<from:option value="" label="请选择"/>
					<from:options items="${fns:getDictList('service_cha')}" itemValue="value" itemLabel="label"/>
				</from:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizServiceCharge">
			<tr>
				<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><td>
    				<a href="${ctx}/biz/order/bizServiceCharge/form?id=${bizServiceCharge.id}">修改</a>
					<a href="${ctx}/biz/order/bizServiceCharge/delete?id=${bizServiceCharge.id}" onclick="return confirmx('确认要删除该服务费--配送方式吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
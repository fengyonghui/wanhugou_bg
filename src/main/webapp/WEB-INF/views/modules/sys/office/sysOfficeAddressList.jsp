<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>地址信息管理</title>
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
		<li class="active"><a href="${ctx}/sys/office/sysOfficeAddress/">地址信息列表</a></li>
		<shiro:hasPermission name="sys:office:sysOfficeAddress:edit"><li><a href="${ctx}/sys/office/sysOfficeAddress/form">地址信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysOfficeAddress" action="${ctx}/sys/office/sysOfficeAddress/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>地址一：</label>
				<sys:treeselect id="office" name="office.id" value="${sysOfficeAddress.office.id}" labelName="office.name" labelValue="${sysOfficeAddress.office.name}"
					title="名称" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>地址类型：</label>
                 <form:select path="type" class="input-medium required">
                        <form:option value="" label="请选择"/>
                        <form:options items="${fns:getDictList('office_type')}" itemLabel="label" itemValue="value"
                        htmlEscape="false"/></form:select>
			</li>
			<li><label>初始值：</label>
				<form:input path="deFault" htmlEscape="false" maxlength="1" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>地址一</th>
				<th>地址二</th>
				<th>地址类型</th>
				<th>初始值</th>
				<th>活跃度</th>
				<th>创建人</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sys:office:sysOfficeAddress:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysOfficeAddress">
			<tr>
				<td><a href="${ctx}/sys/office/sysOfficeAddress/form?id=${sysOfficeAddress.id}">
					${sysOfficeAddress.office.name}
				</a></td>
				<td>
					${sysOfficeAddress.addrLocation.id}
				</td>
				<td>
					${sysOfficeAddress.type}
				</td>
				<td>
					${sysOfficeAddress.deFault}
				</td>
				<td>
					${fns:getDictLabel(sysOfficeAddress.status, 'status', '')}
				</td>
				<td>
					${sysOfficeAddress.createBy.id}
				</td>
				<td>
					${sysOfficeAddress.createDate}
				</td>
				<shiro:hasPermission name="sys:office:sysOfficeAddress:edit"><td>
    				<a href="${ctx}/sys/office/sysOfficeAddress/form?id=${sysOfficeAddress.id}">修改</a>
					<a href="${ctx}/sys/office/sysOfficeAddress/delete?id=${sysOfficeAddress.id}" onclick="return confirmx('确认要删除该地址信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
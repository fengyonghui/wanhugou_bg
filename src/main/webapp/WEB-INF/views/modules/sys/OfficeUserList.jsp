<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sys/user/export");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/sys/user/officeUserList");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
<div id="importBox" class="hide">
	<form id="importForm" action="${ctx}/sys/user/import" method="post" enctype="multipart/form-data"
		  class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
		<a href="${ctx}/sys/user/import/template">下载模板</a>
	</form>
</div>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/sys/user/officeUserList">用户列表</a></li>
	<shiro:hasPermission name="sys:user:edit"><li><a href="${ctx}/sys/user/form?office.id=${user.office.id}&office.name=${user.office.name}&conn=office_user_save&userFlag=${user.userFlag}">用户添加</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/officeUserList" method="post" class="breadcrumb form-search ">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<input name="userFlag" type="hidden" value="${user.userFlag}"/>
	<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	<ul class="ul-form">
		<c:if test="${fns:getUser().isAdmin()}">
		<li><label>归属公司：</label>
				<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
								title="公司" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true"/>
			</c:if>
			<input type="hidden" name="company.type" value="">
		<li><label>登录名：</label><form:input path="loginName" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li class="clearfix"></li>
		<c:if test="${fns:getUser().isAdmin()}">
			<li><label>归属部门：</label><sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
													title="部门" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/></li>
		</c:if>
		<li><label>姓&nbsp;&nbsp;&nbsp;名：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li><label>电&nbsp;&nbsp;&nbsp;话：</label><form:input path="mobile" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			<input id="btnImport" class="btn btn-primary" type="button" value="导入"/></li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead><tr><th>归属公司</th><th>归属部门</th><th class="sort-column login_name">登录名</th><th class="sort-column name">姓名</th>
		<th>电话</th><th>手机</th><%--<th>角色</th> --%><shiro:hasPermission name="sys:user:edit"><th>操作</th></shiro:hasPermission></tr></thead>
	<tbody>
	<c:forEach items="${page.list}" var="bizUser">
		<c:if test="${bizUser.delFlag==1}">
		<tr>
			<td>${bizUser.company.name}</td>
			<td>${bizUser.office.name}</td>
			<td>
				<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=office_user_save&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">
						${bizUser.loginName}</a>
			</td>
			<td>${bizUser.name}</td>
			<td>${bizUser.phone}</td>
			<td>${bizUser.mobile}</td><%--
			<td>${user.roleNames}</td> --%>
			<shiro:hasPermission name="sys:user:edit"><td>
				<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=office_user_save&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">修改</a>
				<a href="${ctx}/sys/user/delete?id=${bizUser.id}&company.id=${user.company.id}&conn=office_user_save" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
			</td></shiro:hasPermission>
		</tr>
	</c:if>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>

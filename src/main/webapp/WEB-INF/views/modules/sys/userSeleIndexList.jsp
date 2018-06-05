<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<html>
<head>
	<title>品类主管管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sys/user/seleIndexList");
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/sys/user/seleIndexList">品类主管列表</a></li>
	<shiro:hasPermission name="sys:user:edit"><li><a href="${ctx}/sys/user/userSeleForm?id=${user.id}&conn=selectIndex">品类主管添加</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/seleIndexList" method="post" class="breadcrumb form-search ">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	<ul class="ul-form">
		<input type="hidden" name="conn" value="selectIndex"></li>
		<li><label>姓&nbsp;&nbsp;&nbsp;名：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li><label>电&nbsp;&nbsp;&nbsp;话：</label><form:input path="mobile" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li><label>日&nbsp;&nbsp;&nbsp;期：</label>
			<input name="ordrHeaderStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
				   value="<fmt:formatDate value="${user.ordrHeaderStartTime}" pattern="yyyy-MM-dd"/>"
				   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>

		</li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
		<tr>
			<th>归属公司</th>
			<th>归属部门</th>
			<th>姓名</th>
			<th>电话</th>
			<th>商品销售量</th>
			<c:choose>
				<c:when test="${not empty user.ordrHeaderStartTime}">
					<th>每月订单量</th>
					<th>每月回款额</th>
				</c:when>
				<c:otherwise>
					<th>每日订单量</th>
					<th>每日回款额</th>
				</c:otherwise>
			</c:choose>
			<th>新品发布量</th>
			<th>供应商洽谈数</th>
		<shiro:hasPermission name="sys:user:edit"><th>操作</th></shiro:hasPermission></tr></thead>
	<tbody>
	<c:forEach items="${page.list}" var="bizUser">
		<tr>
			<td>${bizUser.company.name}</td>
			<td>${bizUser.office.name}</td>
			<td>${bizUser.name}</td>
			<td>${bizUser.mobile}</td>
			<td>
				${bizUser.userOrder.skuCount}
			</td>
			<td>
				${bizUser.userOrder.orderCount}
			</td>
			<td>
				${bizUser.userOrder.receiveTotal}
			</td>
			<td>
				${bizUser.userOrder.opShelfCount}
			</td>
			<td>
				<a href="${ctx}/biz/chat/bizChatRecord/list?user.id=${bizUser.id}&office.type=7&office.parent.id=12&source=suppli">
					${bizUser.userOrder.officeChatRecord}
				</a>
			</td>
			<shiro:hasPermission name="sys:user:edit"><td>
					<a href="${ctx}/sys/user/userSeleForm?id=${bizUser.id}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}&conn=selectIndex">修改</a>
					<a href="${ctx}/sys/user/delete?id=${bizUser.id}&conn=selectIndex" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
			</td></shiro:hasPermission>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
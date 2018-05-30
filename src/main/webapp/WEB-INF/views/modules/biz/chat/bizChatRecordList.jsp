<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>沟通记录管理</title>
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
		<li class="active"><a href="${ctx}/biz/chat/bizChatRecord/list?office.id=${bizChatRecord.office.id}&source=purchaser">沟通记录列表</a></li>
		<shiro:hasPermission name="biz:chat:bizChatRecord:edit"><li><a href="${ctx}/biz/chat/bizChatRecord/form?office.type=6&office.parent.id=7&office.id=${bizChatRecord.office.id}&source=purchaser">沟通记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizChatRecord" action="${ctx}/biz/chat/bizChatRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="source" type="hidden" value="purchaser"/>
		<ul class="ul-form">
			<%--<li><label>经销店名称:</label>--%>
				<%--<sys:treeselect id="office" name="office.id" value="${bizChatRecord.office.id}" labelName="office.name" labelValue="${bizChatRecord.office.name}"--%>
					<%--title="经销店" url="/sys/office/queryTreeList?type=6" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>--%>
			<%--</li>--%>
			<li><label style="width: 130px;">品类主管或客户专员:</label>
				<%--<sys:treeselect id="user" name="user.id" value="${bizChatRecord.user.id}" labelName="user.name" labelValue="${bizChatRecord.user.name}"--%>
					<%--title="品类主管或客户专员" url="/sys/user/userSelectTreeData" cssClass="input-small"--%>
						<%--allowClear="true" notAllowSelectParent="true"/>--%>
				<form:input path="user.name" htmlEscape="false" maxlength="40" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>经销店名称</th>
				<th>品类主管或客户专员</th>
				<th>沟通记录</th>
				<th>创建人</th>
				<th>创建时间</th>
				<c:if test="${fns:getUser().isAdmin()}">
					<shiro:hasPermission name="biz:chat:bizChatRecord:edit"><th>操作</th></shiro:hasPermission>
				</c:if>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizChatRecord">
			<tr>
				<td>
					<%--<a href="${ctx}/biz/chat/bizChatRecord/form?id=${bizChatRecord.id}&office.type=6&office.parent.id=7&office.id=${bizChatRecord.office.id}&source=purchaser">--%>
					${bizChatRecord.office.name}
					<%--</a>--%>
				</td>
				<td>
					${bizChatRecord.user.name}
				</td>
				<td>
					${bizChatRecord.chatRecord}
				</td>
				<td>
					${bizChatRecord.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizChatRecord.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<c:if test="${fns:getUser().isAdmin()}">
					<shiro:hasPermission name="biz:chat:bizChatRecord:edit"><td>
						<%--<a href="${ctx}/biz/chat/bizChatRecord/form?id=${bizChatRecord.id}&office.type=6&office.parent.id=7&office.id=${bizChatRecord.office.id}&source=purchaser">修改</a>--%>
						<a href="${ctx}/biz/chat/bizChatRecord/delete?id=${bizChatRecord.id}&office.id=${bizChatRecord.office.id}&source=purchaser" onclick="return confirmx('确认要删除该沟通记录吗？', this.href)">删除</a>
					</td></shiro:hasPermission>
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
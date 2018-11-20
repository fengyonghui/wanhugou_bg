<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发送站内信管理</title>
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
		<li class="active"><a href="${ctx}/biz/message/bizMessageInfo/">站内信列表</a></li>
		<shiro:hasPermission name="biz:message:bizMessageInfo:edit"><li><a href="${ctx}/biz/message/bizMessageInfo/form">站内信添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizMessageInfo" action="${ctx}/biz/message/bizMessageInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>标题：</label>
				<form:input path="title" htmlEscape="false" maxlength="128" class="input-medium"/>
			</li>

			<li><label>创建人：</label>
				<form:input path="createName" htmlEscape="false" maxlength="128" class="input-medium"/>
			</li>

			<li><label>状态：</label>
				<form:select path="bizStatus" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_message_info_status')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/></form:select>
			</li>

			<li><label>发布时间：</label>
				<input name="releaseStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizMessageInfo.releaseStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				至
				<input name="releaseEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizMessageInfo.releaseEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>创建人</th>
				<th>标题</th>
				<th>内容</th>
				<th>url</th>
				<th>状态</th>
				<th>发布时间</th>
				<shiro:hasPermission name="biz:message:bizMessageInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizMessageInfo">
			<tr>
				<td>
					${bizMessageInfo.createName}
				</td>
				<td>
					<%--<a href="${ctx}/biz/message/bizMessageInfo/form?id=${bizMessageInfo.id}">--%>
					${bizMessageInfo.title}
					<%--</a>--%>
				</td>
				<td>
					${bizMessageInfo.content}
				</td>
				<td>
					${bizMessageInfo.url}
				</td>
				<td>
					${fns:getDictLabel(bizMessageInfo.bizStatus, 'biz_message_info_status', '未知类型')}
				</td>
				<td>
					<fmt:formatDate value="${bizMessageInfo.releaseTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:message:bizMessageInfo:edit"><td>
					<a href="${ctx}/biz/message/bizMessageInfo/delete?id=${bizMessageInfo.id}" onclick="return confirmx('确认要删除该站内信吗？', this.href)">删除</a>
					<c:if test="${bizMessageInfo.bizStatus == '0'}">
						<a href="${ctx}/biz/message/bizMessageInfo/form?id=${bizMessageInfo.id}">编辑</a>
					</c:if>
					<c:if test="${bizMessageInfo.bizStatus == '1'}">
						<a href="${ctx}/biz/message/bizMessageInfo/copy?id=${bizMessageInfo.id}" onclick="return confirmx('确认要复制该站内信吗？', this.href)">复制</a>
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
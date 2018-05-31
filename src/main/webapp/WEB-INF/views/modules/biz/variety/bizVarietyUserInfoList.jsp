<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品类与用户 关联管理</title>
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
		<li class="active"><a href="${ctx}/biz/variety/bizVarietyUserInfo/">品类与用户 关联列表</a></li>
		<shiro:hasPermission name="biz:variety:bizVarietyUserInfo:edit"><li><a href="${ctx}/biz/variety/bizVarietyUserInfo/form">品类与用户 关联添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizVarietyUserInfo" action="${ctx}/biz/variety/bizVarietyUserInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>编号：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>类别 biz_variety_info：</label>
				<form:input path="varietyId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>用户 sys_user：</label>
				<sys:treeselect id="user" name="user.id" value="${bizVarietyUserInfo.user.id}" labelName="user.name" labelValue="${bizVarietyUserInfo.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>数据状态：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li><label>创建人：</label>
				<form:input path="createId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>创建时间：</label>
				<input name="createTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizVarietyUserInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>版本控制：</label>
				<form:input path="uVersion" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>更新人：</label>
				<form:input path="updateId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>更新时间：</label>
				<input name="updateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizVarietyUserInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>编号</th>
				<th>类别 biz_variety_info</th>
				<th>用户 sys_user</th>
				<th>数据状态</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>版本控制</th>
				<th>更新人</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:variety:bizVarietyUserInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizVarietyUserInfo">
			<tr>
				<td><a href="${ctx}/biz/variety/bizVarietyUserInfo/form?id=${bizVarietyUserInfo.id}">
					${bizVarietyUserInfo.id}
				</a></td>
				<td>
					${bizVarietyUserInfo.varietyId}
				</td>
				<td>
					${bizVarietyUserInfo.user.name}
				</td>
				<td>
					${fns:getDictLabel(bizVarietyUserInfo.status, 'status', '')}
				</td>
				<td>
					${bizVarietyUserInfo.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizVarietyUserInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizVarietyUserInfo.uVersion}
				</td>
				<td>
					${bizVarietyUserInfo.updateId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizVarietyUserInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:variety:bizVarietyUserInfo:edit"><td>
    				<a href="${ctx}/biz/variety/bizVarietyUserInfo/form?id=${bizVarietyUserInfo.id}">修改</a>
					<a href="${ctx}/biz/variety/bizVarietyUserInfo/delete?id=${bizVarietyUserInfo.id}" onclick="return confirmx('确认要删除该品类与用户 关联吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
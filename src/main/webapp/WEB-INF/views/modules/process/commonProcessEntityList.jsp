<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通用流程管理</title>
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
		<li class="active"><a href="${ctx}/process/commonProcessEntity/">通用流程列表</a></li>
		<shiro:hasPermission name="process:commonProcessEntity:edit"><li><a href="${ctx}/process/commonProcessEntity/form">通用流程添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="commonProcessEntity" action="${ctx}/process/commonProcessEntity/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>object_id：</label>
				<form:input path="objectId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>object_name：</label>
				<form:input path="objectName" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>前一个ID.起始为0：</label>
				<form:input path="prevId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>处理结果 0:未处理 1:通过 2:驳回：</label>
				<form:input path="bizStatus" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>处理人：</label>
				<form:input path="processor" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>描述：</label>
				<form:input path="description" htmlEscape="false" maxlength="512" class="input-medium"/>
			</li>
			<li><label>类型, 对应JAVA中的枚举数据：</label>
				<form:input path="type" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>下一下类型：</label>
				<form:input path="nextType" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>创建时间：</label>
				<input name="beginCreateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${commonProcessEntity.beginCreateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endCreateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${commonProcessEntity.endCreateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>id</th>
				<th>object_id</th>
				<th>object_name</th>
				<th>前一个ID.起始为0</th>
				<th>处理结果 0:未处理 1:通过 2:驳回</th>
				<th>处理人</th>
				<th>描述</th>
				<th>类型, 对应JAVA中的枚举数据</th>
				<th>下一下类型</th>
				<th>创建时间</th>
				<shiro:hasPermission name="process:commonProcessEntity:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="commonProcessEntity">
			<tr>
				<td><a href="${ctx}/process/commonProcessEntity/form?id=${commonProcessEntity.id}">
					${commonProcessEntity.id}
				</a></td>
				<td>
					${commonProcessEntity.objectId}
				</td>
				<td>
					${commonProcessEntity.objectName}
				</td>
				<td>
					${commonProcessEntity.prevId}
				</td>
				<td>
					${commonProcessEntity.bizStatus}
				</td>
				<td>
					${commonProcessEntity.processor}
				</td>
				<td>
					${commonProcessEntity.description}
				</td>
				<td>
					${commonProcessEntity.type}
				</td>
				<td>
					${commonProcessEntity.nextType}
				</td>
				<td>
					<fmt:formatDate value="${commonProcessEntity.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="process:commonProcessEntity:edit"><td>
    				<a href="${ctx}/process/commonProcessEntity/form?id=${commonProcessEntity.id}">修改</a>
					<a href="${ctx}/process/commonProcessEntity/delete?id=${commonProcessEntity.id}" onclick="return confirmx('确认要删除该通用流程吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
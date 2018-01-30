<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>栏目管理</title>
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
		<li class="active"><a href="${ctx}/biz/cms/bizCmsColumInfo/">栏目列表</a></li>
		<shiro:hasPermission name="biz:cms:bizCmsColumInfo:edit"><li><a href="${ctx}/biz/cms/bizCmsColumInfo/form">栏目添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCmsColumInfo" action="${ctx}/biz/cms/bizCmsColumInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>biz_page_info.id：</label>
				<form:input path="pageId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>1. banner 2，货架；++自定义：</label>
				<form:input path="type" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>栏目标题名称：</label>
				<form:input path="title" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>biz_op_shelf_info.id;  default:-1 没有货架：</label>
				<form:input path="shelfId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>栏目排序：</label>
				<form:input path="setOrder" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>栏目描述：</label>
				<form:input path="description" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<li><label>status：</label>
				<form:radiobuttons path="status" items="${fns:getDictList('status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</li>
			<li><label>create_id：</label>
				<form:input path="createId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>create_time：</label>
				<input name="createTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizCmsColumInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>1：</label>
				<form:input path="uVersion" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>update_id：</label>
				<form:input path="updateId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>update_time：</label>
				<input name="updateTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizCmsColumInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>biz_page_info.id</th>
				<th>1. banner 2，货架；++自定义</th>
				<th>栏目标题名称</th>
				<th>biz_op_shelf_info.id;  default:-1 没有货架</th>
				<th>栏目排序</th>
				<th>栏目描述</th>
				<th>status</th>
				<th>create_id</th>
				<th>create_time</th>
				<th>1</th>
				<th>update_id</th>
				<th>update_time</th>
				<shiro:hasPermission name="biz:cms:bizCmsColumInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCmsColumInfo">
			<tr>
				<td><a href="${ctx}/biz/cms/bizCmsColumInfo/form?id=${bizCmsColumInfo.id}">
					${bizCmsColumInfo.id}
				</a></td>
				<td>
					${bizCmsColumInfo.pageId}
				</td>
				<td>
					${bizCmsColumInfo.type}
				</td>
				<td>
					${bizCmsColumInfo.title}
				</td>
				<td>
					${bizCmsColumInfo.shelfId}
				</td>
				<td>
					${bizCmsColumInfo.setOrder}
				</td>
				<td>
					${bizCmsColumInfo.description}
				</td>
				<td>
					${fns:getDictLabel(bizCmsColumInfo.status, 'status', '')}
				</td>
				<td>
					${bizCmsColumInfo.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizCmsColumInfo.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizCmsColumInfo.uVersion}
				</td>
				<td>
					${bizCmsColumInfo.updateId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizCmsColumInfo.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:cms:bizCmsColumInfo:edit"><td>
    				<a href="${ctx}/biz/cms/bizCmsColumInfo/form?id=${bizCmsColumInfo.id}">修改</a>
					<a href="${ctx}/biz/cms/bizCmsColumInfo/delete?id=${bizCmsColumInfo.id}" onclick="return confirmx('确认要删除该栏目吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
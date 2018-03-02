<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商拓展表管理</title>
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
		<li class="active"><a href="${ctx}/biz/vend/bizVendInfo/">供应商拓展表列表</a></li>
		<shiro:hasPermission name="biz:vend:bizVendInfo:edit"><li><a href="${ctx}/biz/vend/bizVendInfo/form">供应商拓展表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizVendInfo" action="${ctx}/biz/vend/bizVendInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>供应商名称：</label>
				<form:input path="vendName" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>分类名称：</label>
				<form:input path="cateName" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>供应商代码：</label>
				<form:input path="code" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>供应商名称</th>
				<th>分类名称</th>
				<th>代码</th>
				<shiro:hasPermission name="biz:vend:bizVendInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizVendInfo">
			<tr>
				<td><a href="${ctx}/biz/vend/bizVendInfo/form?id=${bizVendInfo.office.id}">
					${bizVendInfo.vendName}
				</a></td>
				<td>
					${bizVendInfo.cateName}
				</td>
				<td>
					${bizVendInfo.code}
				</td>
				<shiro:hasPermission name="biz:vend:bizVendInfo:edit"><td>
					<c:choose>
						<c:when test="${bizVendInfo.delFlag == 1}">
    				<a href="${ctx}/biz/vend/bizVendInfo/form?id=${bizVendInfo.office.id}">修改</a>
					<a href="${ctx}/biz/vend/bizVendInfo/delete?office.id=${bizVendInfo.office.id}" onclick="return confirmx('确认要删除该供应商拓展表吗？', this.href)">删除</a>
						</c:when>
						<c:otherwise>
							<a href="${ctx}/biz/vend/bizVendInfo/recover?office.id=${bizVendInfo.office.id}">恢复</a>
						</c:otherwise>
					</c:choose>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
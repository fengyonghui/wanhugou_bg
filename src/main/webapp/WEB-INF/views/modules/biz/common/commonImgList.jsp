<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>图片管理</title>
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
		<li class="active"><a href="${ctx}/biz/common/commonImg/">图片列表</a></li>
		<shiro:hasPermission name="biz:common:commonImg:edit"><li><a href="${ctx}/biz/common/commonImg/form">图片添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="commonImg" action="${ctx}/biz/common/commonImg/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>对象名称，表名称：</label>
				<form:input path="objectName" htmlEscape="false" maxlength="25" class="input-medium"/>
			</li>
			<li><label>图片类型：</label>
				<form:input path="imgType" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>图片服务器地址：</label>
				<form:input path="imgServer" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>对象名称，表名称</th>
				<th>对应表的主键</th>
				<th>图片类型</th>
				<th>排序</th>
				<th>图片服务器地址</th>
				<th>图片路径</th>
				<th>图片连接地址</th>
				<th>描述</th>
				<shiro:hasPermission name="biz:common:commonImg:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="commonImg">
			<tr>
				<td><a href="${ctx}/biz/common/commonImg/form?id=${commonImg.id}">
					${commonImg.objectName}
				</a></td>
				<td>
					${commonImg.objectId}
				</td>
				<td>
					${commonImg.imgType}
				</td>
				<td>
					${commonImg.imgSort}
				</td>
				<td>
					${commonImg.imgServer}
				</td>
				<td>
					${commonImg.imgPath}
				</td>
				<td>
					${commonImg.imgLink}
				</td>
				<td>
					${commonImg.comment}
				</td>
				<shiro:hasPermission name="biz:common:commonImg:edit"><td>
    				<a href="${ctx}/biz/common/commonImg/form?id=${commonImg.id}">修改</a>
					<a href="${ctx}/biz/common/commonImg/delete?id=${commonImg.id}" onclick="return confirmx('确认要删除该图片吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
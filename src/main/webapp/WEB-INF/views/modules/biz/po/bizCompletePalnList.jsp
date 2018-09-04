<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>确认排产表管理</title>
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
		<li class="active"><a href="${ctx}/biz/po/bizCompletePaln/">确认排产表列表</a></li>
		<shiro:hasPermission name="biz:po:bizCompletePaln:edit"><li><a href="${ctx}/biz/po/bizCompletePaln/form">确认排产表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCompletePaln" action="${ctx}/biz/po/bizCompletePaln/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>排产ID：</label>
				<form:input path="schedulingId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>完成日期：</label>
				<input name="planDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizCompletePaln.planDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<shiro:hasPermission name="biz:po:bizCompletePaln:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCompletePaln">
			<tr>
				<shiro:hasPermission name="biz:po:bizCompletePaln:edit"><td>
    				<a href="${ctx}/biz/po/bizCompletePaln/form?id=${bizCompletePaln.id}">修改</a>
					<a href="${ctx}/biz/po/bizCompletePaln/delete?id=${bizCompletePaln.id}" onclick="return confirmx('确认要删除该确认排产表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
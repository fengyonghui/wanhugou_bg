<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>单表管理</title>
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
		<li class="active"><a href="${ctx}/biz/farm/bizFarm/">单表列表</a></li>
		<shiro:hasPermission name="biz:farm:bizFarm:edit"><li><a href="${ctx}/biz/farm/bizFarm/form">单表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizFarm" action="${ctx}/biz/farm/bizFarm/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>养殖场名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>建立时间：</label>
				<input name="beginEstablishedDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizFarm.beginEstablishedDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endEstablishedDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizFarm.endEstablishedDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>养殖场名称</th>
				<th>地址id</th>
				<th>建立时间</th>
				<th>经营性质</th>
				<th>实际经营状态</th>
				<th>状态</th>
				<th>create_id</th>
				<th>create_time</th>
				<shiro:hasPermission name="biz:farm:bizFarm:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizFarm">
			<tr>
				<td><a href="${ctx}/biz/farm/bizFarm/form?id=${bizFarm.id}">
					${bizFarm.name}
				</a></td>
				<td>
					${bizFarm.locationId}
				</td>
				<td>
					<fmt:formatDate value="${bizFarm.establishedDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizFarm.bizType}
				</td>
				<td>
					${bizFarm.remark}
				</td>
				<td>
					${bizFarm.status}
				</td>
				<td>
					${bizFarm.createBy.id}
				</td>
				<td>
					<fmt:formatDate value="${bizFarm.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:farm:bizFarm:edit"><td>
    				<a href="${ctx}/biz/farm/bizFarm/form?id=${bizFarm.id}">修改</a>
					<a href="${ctx}/biz/farm/bizFarm/delete?id=${bizFarm.id}" onclick="return confirmx('确认要删除该单表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
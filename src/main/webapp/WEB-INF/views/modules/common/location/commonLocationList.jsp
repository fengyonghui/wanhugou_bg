<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>位置信息管理</title>
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
		<li class="active"><a href="${ctx}/common/location/commonLocation/">位置信息列表</a></li>
		<shiro:hasPermission name="common:location:commonLocation:edit"><li><a href="${ctx}/common/location/commonLocation/form">位置信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="commonLocation" action="${ctx}/common/location/commonLocation/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>省份：</label>
				<form:input path="provinceId" htmlEscape="false" maxlength="6" class="input-medium"/>
			</li>
			<li><label>城市：</label>
				<form:input path="cityId" htmlEscape="false" maxlength="6" class="input-medium"/>
			</li>
			<li><label>区域：</label>
				<form:input path="regionId" htmlEscape="false" maxlength="6" class="input-medium"/>
			</li>
			<li><label>省市区对应名称：</label>
				<form:input path="pcrName" htmlEscape="false" maxlength="60" class="input-medium"/>
			</li>
			<li><label>create_id：</label>
				<form:input path="createId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>update_id：</label>
				<form:input path="updateId.id" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>省份</th>
				<th>城市</th>
				<th>区域</th>
				<th>省市区对应名称</th>
				<th>详细地址</th>
				<th>邮编</th>
				<th>经度</th>
				<th>纬度</th>
				<th>0，无效；1；有效</th>
				<th>create_id</th>
				<th>create_time</th>
				<th>u_version</th>
				<th>update_id</th>
				<th>update_time</th>
				<shiro:hasPermission name="common:location:commonLocation:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="commonLocation">
			<tr>
				<td><a href="${ctx}/common/location/commonLocation/form?id=${commonLocation.id}">
					${commonLocation.provinceId}
				</a></td>
				<td>
					${commonLocation.cityId}
				</td>
				<td>
					${commonLocation.regionId}
				</td>
				<td>
					${commonLocation.pcrName}
				</td>
				<td>
					${commonLocation.address}
				</td>
				<td>
					${commonLocation.zipCode}
				</td>
				<td>
					${commonLocation.longitude}
				</td>
				<td>
					${commonLocation.latitude}
				</td>
				<td>
					${fns:getDictLabel(commonLocation.status, 'status', '')}
				</td>
				<td>
					${commonLocation.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${commonLocation.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${commonLocation.uVersion}
				</td>
				<td>
					${commonLocation.updateId.id}
				</td>
				<td>
					<fmt:formatDate value="${commonLocation.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="common:location:commonLocation:edit"><td>
    				<a href="${ctx}/common/location/commonLocation/form?id=${commonLocation.id}">修改</a>
					<a href="${ctx}/common/location/commonLocation/delete?id=${commonLocation.id}" onclick="return confirmx('确认要删除该位置信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>地址管理</title>
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
		<li class="active"><a href="${ctx}/biz/order/bizOrderAddress/">地址列表</a></li>
		<shiro:hasPermission name="biz:order:bizOrderAddress:edit"><li><a href="${ctx}/biz/order/bizOrderAddress/form">地址添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderAddress" action="${ctx}/biz/order/bizOrderAddress/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>id：</label>
				<form:input path="id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
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
			<li><label>详细地址：</label>
				<form:input path="address" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>收货人姓名：</label>
				<form:input path="receiver" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>收货人联系电话：</label>
				<form:input path="phone" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>创建人：</label>
				<form:input path="createId.id" htmlEscape="false" maxlength="11" class="input-medium"/>
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
				<th>省份</th>
				<th>城市</th>
				<th>区域</th>
				<th>省市区对应名称</th>
				<th>详细地址</th>
				<th>收货人姓名</th>
				<th>收货人联系电话</th>
				<th>数据状态 0：无效 1：有效</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>修改人</th>
				<th>修改时间</th>
				<shiro:hasPermission name="biz:order:bizOrderAddress:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOrderAddress">
			<tr>
				<td><a href="${ctx}/biz/order/bizOrderAddress/form?id=${bizOrderAddress.id}">
					${bizOrderAddress.id}
				</a></td>
				<td>
					${bizOrderAddress.provinceId}
				</td>
				<td>
					${bizOrderAddress.cityId}
				</td>
				<td>
					${bizOrderAddress.regionId}
				</td>
				<td>
					${bizOrderAddress.pcrName}
				</td>
				<td>
					${bizOrderAddress.address}
				</td>
				<td>
					${bizOrderAddress.receiver}
				</td>
				<td>
					${bizOrderAddress.phone}
				</td>
				<td>
					${fns:getDictLabel(bizOrderAddress.status, 'status', '')}
				</td>
				<td>
					${bizOrderAddress.createId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizOrderAddress.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizOrderAddress.updateId.id}
				</td>
				<td>
					<fmt:formatDate value="${bizOrderAddress.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:order:bizOrderAddress:edit"><td>
    				<a href="${ctx}/biz/order/bizOrderAddress/form?id=${bizOrderAddress.id}">修改</a>
					<a href="${ctx}/biz/order/bizOrderAddress/delete?id=${bizOrderAddress.id}" onclick="return confirmx('确认要删除该地址吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
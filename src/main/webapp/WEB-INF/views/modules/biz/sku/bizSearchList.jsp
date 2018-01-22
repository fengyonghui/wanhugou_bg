<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>找货定制管理</title>
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
		<li class="active"><a href="${ctx}/biz/sku/bizSearch/">找货定制列表</a></li>
		<shiro:hasPermission name="biz:sku:bizSearch:edit"><li><a href="${ctx}/biz/sku/bizSearch/form">找货定制添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSearch" action="${ctx}/biz/sku/bizSearch/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品编码：</label>
				<form:input path="partNo" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>分类Id biz_category_info：</label>
				<form:input path="cateId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>找货名称：</label>
				<form:input path="cateName" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>材质属性Id sys_property_info  sys_prop_value：</label>
				<form:input path="qualityId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>颜色：</label>
				<form:input path="color" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>规格：</label>
				<form:input path="standard" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>业务状态 0：关闭 1：开放 2：取消：</label>
				<form:input path="businessStatus" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>商品编码</th>
				<th>分类Id biz_category_info</th>
				<th>找货名称</th>
				<th>材质属性Id sys_property_info  sys_prop_value</th>
				<th>颜色</th>
				<th>规格</th>
				<th>业务状态 0：关闭 1：开放 2：取消</th>
				<th>期望到货时间</th>
				<th>用户ID</th>
				<th>期望最低售价</th>
				<th>期望最高价</th>
				<th>数量</th>
				<shiro:hasPermission name="biz:sku:bizSearch:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSearch">
			<tr>
				<td><a href="${ctx}/biz/sku/bizSearch/form?id=${bizSearch.id}">
					${bizSearch.partNo}
				</a></td>
				<td>
					${bizSearch.cateId}
				</td>
				<td>
					${bizSearch.cateName}
				</td>
				<td>
					${bizSearch.qualityId}
				</td>
				<td>
					${bizSearch.color}
				</td>
				<td>
					${bizSearch.standard}
				</td>
				<td>
					${bizSearch.businessStatus}
				</td>
				<td>
					<fmt:formatDate value="${bizSearch.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizSearch.user.name}
				</td>
				<td>
					${bizSearch.minPrice}
				</td>
				<td>
					${bizSearch.maxPrice}
				</td>
				<td>
					${bizSearch.amount}
				</td>
				<shiro:hasPermission name="biz:sku:bizSearch:edit"><td>
    				<a href="${ctx}/biz/sku/bizSearch/form?id=${bizSearch.id}">修改</a>
					<a href="${ctx}/biz/sku/bizSearch/delete?id=${bizSearch.id}" onclick="return confirmx('确认要删除该找货定制吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
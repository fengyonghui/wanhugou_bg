<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>日志管理</title>
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
		<li class="active"><a href="${ctx}/biz/product/bizProdViewLog/">日志列表</a></li>
		<%--<shiro:hasPermission name="biz:product:bizProdViewLog:edit"><li><a href="${ctx}/biz/product/bizProdViewLog/form">日志添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizProdViewLog" action="${ctx}/biz/product/bizProdViewLog/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>货架名称：</label>
				<form:input path="opShelfInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>采购中心：</label>
				<c:if test="${fns:getUser().isAdmin()}">
				<sys:treeselect id="center" name="center.id" value="${bizProdViewLog.center.id}" labelName="center.name"
								labelValue="${bizProdViewLog.center.name}"  notAllowSelectParent="true" allowClear="true"
								title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-medium required" dataMsgRequired="必填信息">
				</sys:treeselect>
			</li>
			</c:if>
			</li>
			<li><label>产品名称：</label>
				<form:input path="productInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>货架</th>
				<th>采购中心</th>
				<th>产品名称</th>
				<th>用户</th>
				<th>创建时间</th>
				<%--<shiro:hasPermission name="biz:product:bizProdViewLog:edit"><th>操作</th></shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizProdViewLog">
			<tr>
				<td>
					${bizProdViewLog.opShelfInfo.name}
				</td>
				<td>
					<c:choose >
						<c:when test="${bizProdViewLog.center.id == 0 }">
							平台商品
						</c:when>
						<c:otherwise>
							${bizProdViewLog.center.name}
						</c:otherwise>
					</c:choose>
				</td>
				<td>
					${bizProdViewLog.productInfo.name}
				</td>
				<td>
					${bizProdViewLog.user.name}
				</td>
				<td>
					<fmt:formatDate value="${bizProdViewLog.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<%--<shiro:hasPermission name="biz:product:bizProdViewLog:edit"><td>--%>
    				<%--<a href="${ctx}/biz/product/bizProdViewLog/form?id=${bizProdViewLog.id}">修改</a>--%>
					<%--<a href="${ctx}/biz/product/bizProdViewLog/delete?id=${bizProdViewLog.id}" onclick="return confirmx('确认要删除该日志吗？', this.href)">删除</a>--%>
				<%--</td></shiro:hasPermission>--%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
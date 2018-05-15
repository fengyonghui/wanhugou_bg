<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>出厂价日志管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#buttonExport").click(function(){
				top.$.jBox.confirm("确认要导出出厂价日志数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/sku/bizSkuViewLog/skuExprot");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/sku/bizSkuViewLog/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
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
		<li class="active"><a href="${ctx}/biz/sku/bizSkuViewLog/">出厂价日志列表</a></li>
		<%--<shiro:hasPermission name="biz:sku:bizSkuViewLog:edit"><li><a href="${ctx}/biz/sku/bizSkuViewLog/form">出厂价日志添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSkuViewLog" action="${ctx}/biz/sku/bizSkuViewLog/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>商品货号：</label>
				<form:input path="itemNo" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input id="buttonExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>商品名称</th>
				<th>商品货号</th>
				<th>商品修改时间</th>
				<th>商品修改人</th>
				<th>修改前工厂价格</th>
				<th>修改后工厂价格</th>
				<th>改变的价格</th>
				<th>创建时间</th>
				<%--<shiro:hasPermission name="biz:sku:bizSkuViewLog:edit"><th>操作</th></shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSkuViewLog" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
					${bizSkuViewLog.skuInfo.name}
				</td>
				<td>
					${bizSkuViewLog.itemNo}
				</td>
				<td>
					<fmt:formatDate value="${bizSkuViewLog.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizSkuViewLog.updateBy.name}
				</td>
				<td>
					${bizSkuViewLog.frontBuyPrice}
				</td>
				<td>
					${bizSkuViewLog.afterBuyPrice}
				</td>
				<td>
					${bizSkuViewLog.changePrice}
				</td>
				<td>
					<fmt:formatDate value="${bizSkuViewLog.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<%--<shiro:hasPermission name="biz:sku:bizSkuViewLog:edit"><td>--%>
    				<%--<a href="${ctx}/biz/sku/bizSkuViewLog/form?id=${bizSkuViewLog.id}">修改</a>--%>
					<%--<a href="${ctx}/biz/sku/bizSkuViewLog/delete?id=${bizSkuViewLog.id}" onclick="return confirmx('确认要删除该出厂价日志吗？', this.href)">删除</a>--%>
				<%--</td></shiro:hasPermission>--%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
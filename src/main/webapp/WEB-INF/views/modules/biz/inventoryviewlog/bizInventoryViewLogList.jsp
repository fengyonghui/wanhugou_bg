<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>库存盘点记录管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#buttonExport").click(function(){
				top.$.jBox.confirm("确认要导出库存盘点记录数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/inventoryviewlog/bizInventoryViewLog/listExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/inventoryviewlog/bizInventoryViewLog/");
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
		<li class="active"><a href="${ctx}/biz/inventoryviewlog/bizInventoryViewLog/">库存盘点记录列表</a></li>
		<shiro:hasPermission name="biz:inventoryviewlog:bizInventoryViewLog:edit"><li><a href="${ctx}/biz/inventoryviewlog/bizInventoryViewLog/form">库存盘点记录添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInventoryViewLog" action="${ctx}/biz/inventoryviewlog/bizInventoryViewLog/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>仓库：</label>
				<form:input path="invInfo.name" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>商品：</label>
				<form:input path="skuInfo.name" htmlEscape="false" maxlength="20" class="input-medium"/>
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
				<th>盘点日期</th>
				<th>盘点人</th>
				<th>仓库名称</th>
				<th>备货方</th>
				<th>库存类型</th>
				<th>商品</th>
				<th>原库存数量</th>
				<th>现库存数量</th>
				<th>改变数量</th>
				<th>备货单号</th>
				<shiro:hasPermission name="biz:inventoryviewlog:bizInventoryViewLog:view"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInventoryViewLog" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
					<fmt:formatDate value="${bizInventoryViewLog.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
						${bizInventoryViewLog.createBy.name}
				</td>
				<td>
					${bizInventoryViewLog.invInfo.name}
				</td>
				<td>
					${bizInventoryViewLog.requestHeader.fromType==1?'采购中心备货':'厂商备货'}
				</td>
				<td>
					${fns:getDictLabel(bizInventoryViewLog.invType, 'inv_type', '未知状态')}
				</td>
				<td>
					${bizInventoryViewLog.skuInfo.name}
				</td>
				<td>
					${bizInventoryViewLog.stockQty}
				</td>
				<td>
					${bizInventoryViewLog.nowStockQty}
				</td>
				<td>
					${bizInventoryViewLog.stockChangeQty}
				</td>
				<td>
					${bizInventoryViewLog.requestHeader.reqNo}
				</td>

				<shiro:hasPermission name="biz:inventoryviewlog:bizInventoryViewLog:view"><td>
    				<a href="${ctx}/biz/inventoryviewlog/bizInventoryViewLog/form?id=${bizInventoryViewLog.id}">详情</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
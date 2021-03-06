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
				<form:input path="invInfo.name" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>备货单号：</label>
				<form:input path="requestHeader.reqNo" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>备货方：</label>
				<form:select path="requestHeader.fromType" htmlEscape="false" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('req_from_type')}" itemLabel="label" itemValue="value"/>
				</form:select>
			</li>
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>商品货号：</label>
				<form:input path="skuInfo.itemNo" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>盘点时间：</label>
				<input name="createStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizInventoryViewLog.createStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				至
				<input name="createEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizInventoryViewLog.createEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
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
				<th>备货单号</th>
				<th>仓库名称</th>
				<th>采购中心</th>
				<th>备货方</th>
				<th>库存类型</th>
				<th>商品名称</th>
				<th>商品货号</th>
				<th>原库存数量</th>
				<th>现库存数量</th>
				<th>改变数量</th>
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
						${bizInventoryViewLog.requestHeader.reqNo}
				</td>
				<td>
					${bizInventoryViewLog.invInfo.name}
				</td>
				<td>
					${bizInventoryViewLog.requestHeader.fromOffice.name}
				</td>
				<td>
					${fns:getDictLabel(bizInventoryViewLog.requestHeader.fromType,'req_from_type','未知')}
				</td>
				<td>
					${fns:getDictLabel(bizInventoryViewLog.invType, 'inv_type', '未知状态')}
				</td>
				<td>
					${bizInventoryViewLog.skuInfo.name}
				</td>
				<td>
					${bizInventoryViewLog.skuInfo.itemNo}
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
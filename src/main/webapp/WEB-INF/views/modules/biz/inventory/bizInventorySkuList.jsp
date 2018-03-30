<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品库存详情管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#toryExport").click(function(){
                top.$.jBox.confirm("确认要导出库存盘点数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/biz/inventory/bizInventorySku/torySkuExport");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action","${ctx}/biz/inventory/bizInventorySku/");
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
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku?invInfo.id=${bizInventorySku.invInfo.id}&zt=${zt}">商品库存详情列表</a></li>
		<c:if test="${zt eq '2'}">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
				<li><a href="${ctx}/biz/inventory/bizInventorySku/form?invInfo.id=${bizInventorySku.invInfo.id}&zt=${zt}">商品库存详情添加</a></li>
			</shiro:hasPermission>
		</c:if>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInventorySku" action="${ctx}/biz/inventory/bizInventorySku/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="zt" type="hidden" name="zt" value="${zt}"/>
		<input id="invInfo.id" type="hidden" name="invInfo.id" value="${bizInventorySku.invInfo.id}"/>
		<ul class="ul-form">
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" class="input-medium"/>
                <input id="skuInfo.id" type="hidden" name="skuInfo.id" value="${skuInfo.id}"/>
			</li>
			<li><label>商品编号：</label>
				<form:input path="skuInfo.partNo" htmlEscape="false"  class="input-medium"/>
			</li>
			<li><label>商品货号：</label>
				<form:input path="skuInfo.itemNo" htmlEscape="false"  class="input-medium"/>
			</li>
			<li><label>仓库名称：</label>
				<form:input path="invInfo.name" htmlEscape="false"  class="input-medium"/>
				<input id="invInfo.id" type="hidden" name="invInfo.id" value="${invInfo.id}"/>
			</li>
			<%--<li><label>库存类型：</label>
				<form:select path="invType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('inv_type')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/></form:select>
			</li>--%>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
            <li class="btns"><input id="toryExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>库存类型</th>
				<th>仓库名称</th>
				<th>商品名称</th>
				<th>商品编号</th>
				<th>商品货号</th>
				<th>库存数量</th>
				<c:if test="${zt eq '1' || zt eq '2'}">
					<th>销售订单数量</th>
					<th>调入数量</th>
					<th>调出数量</th>
					<%--<th>专属库存的客户</th>--%>
				</c:if>
				<c:if test="${zt eq '3'}">
					<th>修改时间</th>
					<th>修改人</th>
				</c:if>
				<c:if test="${zt eq '2' || zt eq '3'}">
					<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><th>操作</th></shiro:hasPermission>
				</c:if>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInventorySku">
			<tr>
				<td>
					<%--<a href="${ctx}/biz/inventory/bizInventorySku/form?id=${bizInventorySku.id}">--%>
					${fns:getDictLabel(bizInventorySku.invType, 'inv_type', '未知状态')}
				</a></td>
				<td>
					${bizInventorySku.invInfo.name}
				</td>
				<td>
					${bizInventorySku.skuInfo.name}
				</td>
				<td>
					${bizInventorySku.skuInfo.partNo}
				</td><td>
					${bizInventorySku.skuInfo.itemNo}
				</td>
				<td>
					${bizInventorySku.stockQty}
				</td>
				<c:if test="${zt eq '1' || zt eq '2'}">
					<td>
						${bizInventorySku.stockOrdQty}
					</td>
					<td>
						${bizInventorySku.transInQty}
					</td>
					<td>
						${bizInventorySku.transOutQty}
					</td>
					<%--<td>
						${bizInventorySku.customer.name}
					</td>--%>
				</c:if>
				<c:if test="${zt eq '3'}">
					<td>
						<fmt:formatDate value="${bizInventorySku.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						${bizInventorySku.updateBy.name}
					</td>
				</c:if>
				<c:if test="${zt eq '2' || zt eq '3'}">
					<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><td>
						<a href="${ctx}/biz/inventory/bizInventorySku/form?id=${bizInventorySku.id}&invInfo.id=${bizInventorySku.invInfo.id}&zt=${zt}">修改</a>
						<a href="${ctx}/biz/inventory/bizInventorySku/delete?id=${bizInventorySku.id}&zt=${zt}" onclick="return confirmx('确认要删除该商品库存详情吗？', this.href)">删除</a>
					</td></shiro:hasPermission>
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
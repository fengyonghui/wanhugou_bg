<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>库存变更记录管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#buttonExport").click(function(){
				top.$.jBox.confirm("确认要导出库存变更记录数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/inventory/bizCollectGoodsRecord/exportList");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/inventory/bizCollectGoodsRecord/");
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
		<li class="active"><a href="${ctx}/biz/inventory/bizCollectGoodsRecord/stockChangeList">库存变更记录列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCollectGoodsRecord" action="${ctx}/biz/inventory/bizCollectGoodsRecord/stockChangeList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input type="hidden" name="sources" value="changeStock"/>
		<ul class="ul-form">
			<li><label>仓库名称：</label>
				<form:input path="invInfoName" htmlEscape="false" maxlength="80" class="input-medium"/>
			</li>
			<li><label>订单编号：</label>
				<form:input path="orderNum" htmlEscape="false" maxlength="80" class="input-medium"/>
			</li>
			<li><label>商品名称：</label>
				<form:input path="skuInfoName" htmlEscape="false" maxlength="80" class="input-medium"/>
			</li>
			<li><label>商品货号：</label>
				<form:input path="skuInfoItemNo" htmlEscape="false" maxlength="80" class="input-medium"/>
			</li>
			<li><label>商品编号：</label>
				<form:input path="skuInfoPartNo" htmlEscape="false" maxlength="80" class="input-medium"/>
			</li>
			<li><label>创建日期：</label>
				<input name="createDateStart" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizCollectGoodsRecord.createDateStart}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				至
				<input name="createDateEnd" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizCollectGoodsRecord.createDateEnd}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>仓库名称</th>
				<th>商品名称</th>
				<th>商品货号</th>
				<th>商品编号</th>
				<th>备货单号/订单号</th>
				<th>变更记录</th>
				<th>原库存数</th>
				<th>变更数量</th>
				<th>客户名称</th>
				<th>创建人</th>
				<th>变更时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="collectGoods" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
					${collectGoods.invInfo.name}
				</td>
				<td>
						${collectGoods.skuInfo.name}
				</td>
				<td>
						${collectGoods.skuInfo.itemNo}
				</td>
				<td>
						${collectGoods.skuInfo.partNo}
				</td>
				<td>
					<c:if test="${fn:contains(collectGoods.orderNum,'RE')}">
						<a href="${ctx}/biz/request/bizRequestAll/form?id=${collectGoods.bizRequestHeader.id}&source=gh&bizStatu=0">
								${collectGoods.orderNum}</a>
					</c:if>
					<c:if test="${!fn:contains(collectGoods.orderNum,'RE')}">
						<a href="${ctx}/biz/request/bizRequestAll/form?id=${collectGoods.bizOrderHeader.id}&source=ghs&bizStatu=0">
								${collectGoods.orderNum}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${collectGoods.changeState !=null && collectGoods.changeState eq '出库记录'}">
						<font color="#CD3700">${collectGoods.changeState}</font>
					</c:if>
					<c:if test="${collectGoods.changeState !=null && collectGoods.changeState eq '入库记录'}">
						<font color="#3CB371">${collectGoods.changeState}</font>
					</c:if>
				</td>
				<td>${collectGoods.invOldNum}</td>
				<td>
					${collectGoods.changeNumber}
				</td>
				<td>
					${collectGoods.customer.name}
				</td>
				<td>
						${collectGoods.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${collectGoods.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
					<%--<shiro:hasPermission name="biz:inventory:bizSendGoodsRecord:edit"><td>
                        <%--<a href="${ctx}/biz/inventory/bizSendGoodsRecord/form?id=${bizSendGoodsRecord.id}">修改</a>--%>
					<%--<a href="${ctx}/biz/inventory/bizSendGoodsRecord/delete?id=${bizSendGoodsRecord.id}" onclick="return confirmx('确认要删除该供货记录吗？', this.href)">删除</a>--%>
					<%--</td></shiro:hasPermission>&ndash;%&gt;--%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
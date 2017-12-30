<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
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
		<li class="active"><a href="${ctx}/biz/po/bizPoHeader/">采购订单列表</a></li>
		<shiro:hasPermission name="biz:po:bizPoHeader:edit"><li><a href="${ctx}/biz/po/bizPoHeader/form">采购订单添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizPoHeader" action="${ctx}/biz/po/bizPoHeader/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>供应商ID sys_office.id &amp;  type=vend：</label>
				<form:input path="vendId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>0 不开发票 1 未开发票 3 已开发票：</label>
				<form:input path="invStatus" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>业务状态 0未支付；1首付款支付 2全部支付3已发货 4已收货 5 已完成：</label>
				<form:input path="bizStatus" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>订单来源； biz_platform_info.id：</label>
				<form:input path="plateformId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>订单编号-由系统生成；唯一</th>
				<th>供应商ID sys_office.id &amp;  type=vend</th>
				<th>订单详情总价</th>
				<th>订单总费用</th>
				<th>运费</th>
				<th>0 不开发票 1 未开发票 3 已开发票</th>
				<th>业务状态 0未支付；1首付款支付 2全部支付3已发货 4已收货 5 已完成</th>
				<th>订单来源； biz_platform_info.id</th>
				<shiro:hasPermission name="biz:po:bizPoHeader:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizPoHeader">
			<tr>
				<td><a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">
					${bizPoHeader.orderNum}
				</a></td>
				<td>
					${bizPoHeader.vendId}
				</td>
				<td>
					${bizPoHeader.totalDetail}
				</td>
				<td>
					${bizPoHeader.totalExp}
				</td>
				<td>
					${bizPoHeader.freight}
				</td>
				<td>
					${bizPoHeader.invStatus}
				</td>
				<td>
					${bizPoHeader.bizStatus}
				</td>
				<td>
					${bizPoHeader.plateformId}
				</td>
				<shiro:hasPermission name="biz:po:bizPoHeader:edit"><td>
    				<a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">修改</a>
					<a href="${ctx}/biz/po/bizPoHeader/delete?id=${bizPoHeader.id}" onclick="return confirmx('确认要删除该采购订单吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>积分流水管理</title>
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
		<li class="active"><a href="${ctx}/biz/integration/bizMoneyRecode/">积分流水列表</a></li>
		<shiro:hasPermission name="biz:stream:bizMoneyRecode:edit"><li><a href="${ctx}/biz/stream/bizMoneyRecode/form">积分流水添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizMoneyRecode" action="${ctx}/biz/stream/bizMoneyRecode/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>流水类型：</label>
				<form:select path="statusName" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>生成时间：</label>
				<input name="beginCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizMoneyRecode.beginCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizMoneyRecode.endCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>流水id</th>
				<th>采购商id</th>
				<th>流水数量</th>
				<th>流水类型</th>
				<th>流水说明</th>
				<th>生成时间</th>
				<shiro:hasPermission name="biz:stream:bizMoneyRecode:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizMoneyRecode">
			<tr>
				<td><a href="${ctx}/biz/stream/bizMoneyRecode/form?id=${bizMoneyRecode.id}">
					${bizMoneyRecode.id}
				</a></td>
				<td>
					${bizMoneyRecode.office.id}
				</td>
				<td>
					${bizMoneyRecode.money}
				</td>
				<td>
					${fns:getDictLabel(bizMoneyRecode.statusName, '', '')}
				</td>
				<td>
					${bizMoneyRecode.comment}
				</td>
				<td>
					<fmt:formatDate value="${bizMoneyRecode.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:stream:bizMoneyRecode:edit"><td>
    				<a href="${ctx}/biz/stream/bizMoneyRecode/form?id=${bizMoneyRecode.id}">修改</a>
					<a href="${ctx}/biz/stream/bizMoneyRecode/delete?id=${bizMoneyRecode.id}" onclick="return confirmx('确认要删除该积分流水吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
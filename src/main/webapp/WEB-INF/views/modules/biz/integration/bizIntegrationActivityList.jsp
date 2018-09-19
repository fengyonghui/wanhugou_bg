<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>积分活动管理</title>
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
		<li class="active"><a href="${ctx}/biz/integration/bizIntegrationActivity/">积分活动列表</a></li>
		<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit"><li><a href="${ctx}/biz/integration/bizIntegrationActivity/form">积分活动添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizIntegrationActivity" action="${ctx}/biz/integration/bizIntegrationActivity/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>活动名称：</label>
				<form:input path="activityName" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>发送时间：</label>
				<input name="beginSendTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizIntegrationActivity.beginSendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="endSendTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizIntegrationActivity.endSendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>发送状态：</label>
				<form:select about="choose" path="sendStatus" class="input-medium">
					<form:option value="" label="全部"/>
					<form:option value="0" label="未发送"/>
					<form:option value="1" label="已发送"/>
				</form:select>
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
				<th>id</th>
				<th>活动名称</th>
				<th>发送时间</th>
				<th>发送范围</th>
				<th>优惠工具</th>
				<th>发送人数</th>
				<th>每人赠送积分</th>
				<th>备注说明</th>
				<th>发送状态</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizIntegrationActivity">
			<tr>
				<td><a href="${ctx}/biz/integration/bizIntegrationActivity/form?id=${bizIntegrationActivity.id}">
					${bizIntegrationActivity.id}
				</a></td>
				<td>
					${bizIntegrationActivity.activityName}
				</td>
				<td>
					<fmt:formatDate value="${bizIntegrationActivity.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizIntegrationActivity.sendScope}
				</td>
				<td>
					${bizIntegrationActivity.activityTools}
				</td>
				<td>
					${bizIntegrationActivity.sendNum}
				</td>
				<td>
					${bizIntegrationActivity.integrationNum}
				</td>
				<td>
					${bizIntegrationActivity.description}
				</td>
				<td>
						${bizIntegrationActivity.sendStatus==0?'未发送':'已发送'}
				</td>
				<td>
					${bizIntegrationActivity.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${bizIntegrationActivity.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${bizIntegrationActivity.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit"><td>
					<c:if test="${bizIntegrationActivity.sendStatus==0}">
						<a href="${ctx}/biz/integration/bizIntegrationActivity/form?id=${bizIntegrationActivity.id}">修改</a>
					</c:if>
    				<a href="${ctx}/biz/integration/bizIntegrationActivity/form?id=${bizIntegrationActivity.id}&str=detail">详情</a>
					<a href="${ctx}/biz/integration/bizIntegrationActivity/delete?id=${bizIntegrationActivity.id}" onclick="return confirmx('确认要删除该积分活动吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>会员搜索</title>
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
		<li><a href="${ctx}/sys/office/purchasersList">机构列表</a></li>
		<li><a
				href="${ctx}/sys/office/purchasersForm?id=${office.id}&parent.id=${office.parent.id}">机构<shiro:hasPermission
				name="sys:office:edit">${not empty office.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
				name="sys:office:edit">查看</shiro:lacksPermission></a></li>
		<li class="active"><a href="${ctx}/sys/user/contact">联系人列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/contact" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>会员名称：</label>
				<form:input path="company.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>主负责人：</label>
				<form:input path="name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label style="width: 100px;">主负责人电话：</label>
				<form:input path="mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label style="width: 110px;">客户专员姓名：</label>
				<form:input path="user.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>采购中心：</label>
				<form:input path="cent.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>日&nbsp;&nbsp;期：</label>
				<input name="ordrHeaderStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${ordrHeaderStartTime}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
				至
				<input name="orderHeaderEedTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${orderHeaderEedTime}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input type="button" class="btn" onclick="window.history.go(-1);" value="返回"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>会员名称</th>
				<th>主负责人</th>
				<th>主负责人电话</th>
				<th>客户专员</th>
				<th>采购中心</th>
				<th>客户专员电话</th>
				<th>详细地址</th>
				<th>订单采购频次</th>
				<th>累计采购金额</th>
				<th>首次下单时间</th>
				<th>沟通次数</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="user" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td><a href="${ctx}/sys/office/form?id=${user.company.id}&source=contact_ck">
						${user.company.name}</a>
				</td>
				<td><a href="${ctx}/sys/user/form?id=${user.id}&conn=contact_ck">
						${user.name}</a>
				</td>
				<td>
					${user.mobile}
				</td>
				<td><a href="${ctx}/sys/user/form?id=${user.user.id}&conn=contact_ck">
						${user.user.name}</a>
				</td>
				<td>
					<c:if test="${user.user.name!=null}">
						<a href="${ctx}/sys/office/form?id=${user.cent.id}&source=contact_ck">
								${user.cent.name}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${user.user.name!=null}">
						${user.user.mobile}
					</c:if>
				</td>
				<td>
					<c:if test="${user.user.name!=null}">
						${user.userOrder.bizLocation.province.name}${user.userOrder.bizLocation.city.name}
						${user.userOrder.bizLocation.region.name}${user.userOrder.bizLocation.address}
					</c:if>
				</td>
				<td>
					<c:if test="${user.user.name!=null}">
						${user.userOrder.orderCount}
					</c:if>
				</td>
				<td>
					<c:if test="${user.user.name!=null}">
						${user.userOrder.userOfficeReceiveTotal}
					</c:if>
				</td>
				<td>
					<c:if test="${user.user.name!=null}">
						<fmt:formatDate value="${user.userOrder.userOfficeDeta}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</c:if>
				</td>
				<td>
					<a href="${ctx}/biz/chat/bizChatRecord/list?office.id=${user.company.id}&office.parent.id=7&office.type=6&source=purchaser">
						${user.userOrder.officeChatRecord}
					</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
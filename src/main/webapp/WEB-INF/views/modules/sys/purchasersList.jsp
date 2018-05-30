<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			// var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, rootId = "${not empty office.id ? office.id : '0'}";
			addRow("#treeTableList", tpl, data, rootId, true);
			$("#treeTable").treeTable({expandLevel : 5});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				//if ((${fns:jsGetVal('row.parentId')}) == pid){
				if ((!row ? '': !row.parentId ? 0: row.parentId) == pid){
					$(list).append(Mustache.render(tpl, {
						dict: {
							type: getDictLabel(${fns:toJson(fns:getDictList('sys_office_type'))}, row.type)
						}, pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
	</script>
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
		<li class="active"><a href="${ctx}/sys/office/purchasersList">机构列表</a></li>
		<shiro:hasPermission name="sys:office:edit"><li><a href="${ctx}/sys/office/purchasersForm?parent.id=${office.id}&type=6&source=add_prim">机构添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="office" action="${ctx}/sys/office/purchasersList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>经销店名称：</label>
				<sys:treeselect id="office" name="id" value="" labelName="name"
								labelValue="" notAllowSelectParent="true"
								title="经销店" url="/sys/office/queryTreeList?type=6&source=purchaser" cssClass="input-medium"
								allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
				<input type="hidden" name="queryMemberGys" value="query">
			</li>
			<li><label>联系人电话：</label>
				<form:input path="moblieMoeny.mobile" htmlEscape="false" placeholder="请输入联系人电话"  class="input-medium"/></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<%--用于属性展示,太卡换掉--%>
	<%--<table id="treeTable" class="table table-striped table-bordered table-condensed">--%>
		<%--<thead><tr><th>机构名称</th><th>归属区域</th><th>机构编码</th><th>机构类型</th><th>备注</th><shiro:hasPermission name="sys:office:edit"><th>操作</th></shiro:hasPermission></tr></thead>--%>
		<%--<tbody id="treeTableList"></tbody>--%>
	<%--</table>--%>
	<%--<script type="text/template" id="treeTableTpl">--%>
		<%--<tr id="{{row.id}}" pId="{{pid}}">--%>
			<%--<td><a href="${ctx}/sys/office/purchasersForm?id={{row.id}}">{{row.name}}</a></td>--%>
			<%--<td>{{row.area.name}}</td>--%>
			<%--<td>{{row.code}}</td>--%>
			<%--<td>{{dict.type}}</td>--%>
			<%--<td>{{row.remarks}}</td>--%>
			<%--<shiro:hasPermission name="sys:office:edit"><td>--%>
				<%--<a href="${ctx}/sys/buyerAdviser/interrelatedForm?id={{row.id}}">变更客户专员</a>--%>
				<%--<a href="${ctx}/sys/office/purchasersForm?id={{row.id}}">修改</a>--%>
				<%--<a href="${ctx}/sys/office/delete?id={{row.id}}" onclick="return confirmx('要删除该机构及所有子机构项吗？', this.href)">删除</a>--%>
				<%--<a href="${ctx}/sys/office/purchasersForm?parent.id={{row.id}}">添加下级机构</a> --%>
			<%--</td></shiro:hasPermission>--%>
		<%--</tr>--%>
	<%--</script>--%>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr><th>机构名称</th><th>归属区域</th><th>机构编码</th><th>电话</th><th>联系人电话</th><th>机构类型</th><th>备注</th>
			<shiro:hasPermission name="sys:office:edit"><th>操作</th></shiro:hasPermission></tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="off">
			<tr>
                <td><a href="${ctx}/sys/office/purchasersForm?id=${off.id}&source=add_prim">${off.name}</a></td>
				<td>${off.area.name}</td>
				<td>${off.code}</td>
				<td>${off.phone}</td>
				<td>${off.moblieMoeny.mobile}</td>
				<td>
                    ${fns:getDictLabel(off.type, 'sys_office_type', '未知状态')}
                </td>
				<td>${off.remarks}</td>
                <shiro:hasPermission name="sys:office:edit"><td>
                    <a href="${ctx}/sys/buyerAdviser/interrelatedForm?id=${off.id}">变更客户专员</a>
                    <a href="${ctx}/sys/office/purchasersForm?id=${off.id}&source=add_prim">修改</a>
                    <a href="${ctx}/sys/office/delete?id=${off.id}&source=purchListDelete" onclick="return confirmx('要删除该机构及所有子机构项吗？', this.href)">删除</a>
                    <a href="${ctx}/sys/office/purchasersForm?parent.id=${off.id}&source=add_prim">添加下级机构</a>
					<%--<c:if test="${off.type!=null && off.type eq '6'}">--%>
                    	<a href="${ctx}/biz/chat/bizChatRecord/list?office.id=${off.id}&office.parent.id=7&office.type=6&source=purchaser">沟通记录</a>
					<%--</c:if>--%>
                </td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
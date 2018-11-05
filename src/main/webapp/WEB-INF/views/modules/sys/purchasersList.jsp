<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			 <%--var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");--%>
			var data = ${fns:toJson(list)}, rootId = "${not empty office.id ? office.id : '0'}";
			<%--addRow("#treeTableList", tpl, data, rootId, true);--%>
			addRow("#treeTableList",data, rootId, true);
			$("#treeTable").treeTable({expandLevel : 5});
			<%--导出--%>
			$("#buttonExport").click(function(){
				top.$.jBox.confirm("确认要导出会员数据吗？","系统提示！",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sys/office/exportOffice?id=${office.id}&parentIds=${office.parentIds}");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/sys/office/purchasersList");
					}
				},{buttonsFocus:1});
				top.$(".jbox-body.jbox-icon").css("top","55px");
			});
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
        <shiro:hasPermission name="sys:user:view"><li><a href="${ctx}/sys/user/contact">会员搜索</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="office" action="${ctx}/sys/office/purchasersList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>经销店名称：</label>
				<sys:treeselect id="office" name="id" value="${office.id}" labelName="name"
								labelValue="" notAllowSelectParent="true"
								title="经销店" url="/sys/office/queryTreeList?type=6&source=purchaser" cssClass="input-medium"
								allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
				<input type="hidden" name="queryMemberGys" value="query">
			</li>
			<c:if test="${vendor ne 'vendor'}">
				<li><label>联系人电话：</label>
					<form:input path="moblieMoeny.mobile" htmlEscape="false" placeholder="请输入联系人电话"  class="input-medium"/>
				</li>
			</c:if>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input id="buttonExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>机构名称</th>
				<th>归属区域</th><th>机构编码</th>
				<c:if test="${vendor ne 'vendor'}">
					<th>电话</th>
					<th>联系人电话</th>
				</c:if>
				<th>机构类型</th>
				<th>备注</th>
			<shiro:hasPermission name="sys:office:edit"><th>操作</th></shiro:hasPermission></tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="off">
			<tr>
                <td>
					<c:if test="${vendor ne 'vendor'}">
						<a href="${ctx}/sys/office/purchasersForm?id=${off.id}&source=add_prim">${off.name}</a>
					</c:if>
					<c:if test="${vendor eq 'vendor'}">
						${off.name}
					</c:if>
				</td>
				<td>${off.area.name}</td>
				<td>${off.code}</td>
				<c:if test="${vendor ne 'vendor'}">
					<td>${off.phone}</td>
					<td>${off.moblieMoeny.mobile}</td>
				</c:if>
				<td>
                    ${fns:getDictLabel(off.type, 'sys_office_type', '未知状态')}
                </td>
				<td>${off.remarks}</td>
                <td>
					<shiro:hasPermission name="sys:office:view">
						<a href="${ctx}/sys/office/purchasersForm?id=${off.id}&option=view">详情</a>
					</shiro:hasPermission>
					<shiro:hasPermission name="sys:office:edit">
					<c:if test="${off.delRemark==1}">
						<shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:change">
							<a href="${ctx}/sys/buyerAdviser/interrelatedForm?id=${off.id}">变更客户专员</a>
						</shiro:hasPermission>
						<a href="${ctx}/sys/office/purchasersForm?id=${off.id}&source=add_prim">修改</a>
						<c:if test="${fns:getUser().isAdmin()}">
							<a href="${ctx}/sys/office/delete?id=${off.id}&source=purchListDelete" onclick="return confirmx('要删除该机构及所有子机构项吗？', this.href)">删除</a>
						</c:if>
						<a href="${ctx}/sys/office/purchasersForm?parent.id=${off.id}&source=add_prim">添加下级机构</a>
						<%--<c:if test="${off.type!=null && off.type eq '6'}">--%>
							<a href="${ctx}/biz/chat/bizChatRecord/list?office.id=${off.id}&office.parent.id=7&office.type=6&source=purchaser">沟通记录</a>
						<%--</c:if>--%>
						</c:if>
					<c:if test="${off.delRemark==0}">
						<a href="${ctx}/sys/office/recovery?id=${off.id}&source=purchListDelete" onclick="return confirmx('要恢复该机构及所有子机构项吗？', this.href)">恢复</a>
					</c:if>
				</shiro:hasPermission>
                    <a href="${ctx}/sys/office/sysOfficeAddress?office.type=6&office.id=${off.id}">地址信息</a>
					<c:if test="${off.type==15 || off.type==16}">
						<c:if test="${off.commonProcess.type==null}">
							<shiro:hasPermission name="sys:office:upgrade">
								<a href="${ctx}/sys/office/purchasersForm?id=${off.id}&option=upgrade">申请</a>
							</shiro:hasPermission>
						</c:if>
						<c:if test="${off.commonProcess.type!=null && off.commonProcess.type!=0 && off.commonProcess.type!=off.type}">
							<shiro:hasPermission name="sys:office:upgradeAudit">
								<a href="${ctx}/sys/office/purchasersForm?id=${off.id}&option=upgradeAudit">审核</a>
							</shiro:hasPermission>
						</c:if>
					</c:if>
                </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
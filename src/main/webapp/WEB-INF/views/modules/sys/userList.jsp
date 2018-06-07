<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sys/user/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sys/user/list");
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
<div id="importBox" class="hide">
	<form id="importForm" action="${ctx}/sys/user/import" method="post" enctype="multipart/form-data"
		  class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
		<a href="${ctx}/sys/user/import/template">下载模板</a>
	</form>
</div>
<ul class="nav nav-tabs">
	<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex'}">
		<li class="active"><a href="${ctx}/sys/user/list?company.type=8&company.customerTypeTen=10&company.customerTypeEleven=11&office.id=${user.office.id}&office.name=${user.office.name}&conn=${user.conn}">用户列表</a></li>
		<shiro:hasPermission name="sys:user:edit"><li><a href="${ctx}/sys/user/form?office.id=${user.office.id}&office.name=${user.office.name}&conn=${user.conn}">用户添加</a></li></shiro:hasPermission>
	</c:if>
	<c:if test="${empty user.conn}">
		<li class="active"><a href="${ctx}/sys/user/list">用户列表</a></li>
		<shiro:hasPermission name="sys:user:edit"><li><a href="${ctx}/sys/user/form?office.id=${user.office.id}&office.name=${user.office.name}">用户添加</a></li></shiro:hasPermission>
	</c:if>
</ul>
<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/list" method="post" class="breadcrumb form-search ">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	<ul class="ul-form">
		<c:if test="${fns:getUser().isAdmin()}">
		<li><label>归属公司：</label>
			<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex'}">
				<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
						title="公司" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&source=officeConnIndex" cssClass="input-small" allowClear="true"/>
			</c:if>
			<c:if test="${empty user.conn}">
				<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
						title="公司" url="/sys/office/treeData" cssClass="input-small" allowClear="true"/>
			</c:if>
		</c:if>
			<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex'}">
				<input type="hidden" name="company.type" value="8">
				<input type="hidden" name="company.customerTypeTen" value="10">
				<input type="hidden" name="company.customerTypeEleven" value="11">
				<input type="hidden" name="conn" value="${user.conn}"></li>
			</c:if>
			<c:if test="${empty user.conn}">
				<input type="hidden" name="company.type" value="">
			</c:if>
		<li><label>登录名：</label><form:input path="loginName" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li class="clearfix"></li>
		<c:if test="${fns:getUser().isAdmin()}">
			<li><label>归属部门：</label>
			<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex'}">
				<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
						title="部门" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&source=officeConnIndex" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</c:if>
			<c:if test="${empty user.conn}">
				<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
								title="部门" url="/sys/office/treeData" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</c:if>
			</li>
		</c:if>
		<li><label>姓&nbsp;&nbsp;&nbsp;名：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li><label>手&nbsp;&nbsp;&nbsp;机：</label><form:input path="mobile" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<c:if test="${not empty user.conn && user.conn eq 'connIndex'}">
			<li><label>日期：</label>
				<input name="ordrHeaderStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${user.ordrHeaderStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				至
				<input name="orderHeaderEedTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${user.orderHeaderEedTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
		</c:if>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			<input id="btnImport" class="btn btn-primary" type="button" value="导入"/></li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
		<tr>
			<th>归属公司</th>
			<th>归属部门</th>
			<th class="sort-column login_name">登录名</th>
			<th class="sort-column name">姓名</th>
			<th>手机</th>
			<c:if test="${empty user.conn}">
				<th>状态</th>
			</c:if>
			<c:if test="${not empty user.conn && user.conn eq 'connIndex'}">
				<th>洽谈数</th>
				<th>新增订单量</th>
				<th>新增回款额</th>
				<th>新增经销店</th>
			</c:if>
		<shiro:hasPermission name="sys:user:edit"><th>操作</th></shiro:hasPermission></tr></thead>
	<tbody>
	<c:forEach items="${page.list}" var="bizUser">
		<c:if test="${empty user.conn}">
			<c:if test="${bizUser.loginFlag == 0}">
				<tr style="color: rgba(158,158,158,0.45)">
			</c:if>
			<c:if test="${bizUser.loginFlag == 1}">
				<tr>
			</c:if>
			<td>${bizUser.company.name}</td>
			<td>${bizUser.office.name}</td>
			<td>
				<c:if test="${bizUser.delFlag==1}">
				<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">
						${bizUser.loginName}</a>
				</c:if>
				<c:if test="${bizUser.delFlag==0}">${bizUser.loginName}</c:if>
			</td>
			<td>${bizUser.name}</td>
			<td>${bizUser.mobile}</td>
			<td>${bizUser.delFlag == 1 ? '正常' : '删除'}</td>
			<shiro:hasPermission name="sys:user:edit"><td>
				<c:if test="${bizUser.delFlag==1}">
					<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">修改</a>
					<a href="${ctx}/sys/user/delete?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
				</c:if>
				<c:if test="${bizUser.delFlag==0}">
					<a href="${ctx}/sys/user/recovery?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">恢复</a>
				</c:if>
			</td></shiro:hasPermission>
		</tr>
	</c:if>
		<c:if test="${not empty user.conn && user.conn eq 'connIndex'}">
		<c:if test="${bizUser.delFlag==1}">
			<c:if test="${bizUser.loginFlag == 0}">
				<tr style="color: rgba(158,158,158,0.45)">
			</c:if>
			<c:if test="${bizUser.loginFlag == 1}">
				<tr>
			</c:if>
				<td>${bizUser.company.name}</td>
				<td>${bizUser.office.name}</td>
				<td>
					<c:if test="${user.conn != null}">
						<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">${bizUser.loginName}</a>
					</c:if>
					<c:if test="${user.conn == null}">
						<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">${bizUser.loginName}</a>
					</c:if>
				</td>
				<td>${bizUser.name}</td>
				<td>${bizUser.mobile}</td>
				<c:if test="${not empty user.conn && user.conn eq 'connIndex'}">
					<td>
						<c:if test="${bizUser.userOrder.officeChatRecord !=0}">
							<a href="${ctx}/biz/chat/bizChatRecord/list?user.id=${bizUser.id}&office.parent.id=7&office.type=6&source=purchaser">
								${bizUser.userOrder.officeChatRecord}
							</a>
						</c:if>
						<c:if test="${bizUser.userOrder.officeChatRecord ==0}">
							${bizUser.userOrder.officeChatRecord}
						</c:if>
					</td>
					<td>${bizUser.userOrder.orderCount}</td>
					<td>${bizUser.userOrder.userOfficeReceiveTotal}</td>
					<td>
						${bizUser.userOrder.officeCount}
					</td>
				</c:if>
				<shiro:hasPermission name="sys:user:edit"><td>
					<c:if test="${user.conn != null}">
						<c:if test="${user.conn eq 'connIndex'}">
							<a href="${ctx}/biz/custom/bizCustomCenterConsultant/list?consultants.id=${bizUser.id}&conn=${user.conn}&office.id=${bizUser.office.id}">关联经销店</a>
							<a href="${ctx}/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=${bizUser.id}">订单管理</a>
						</c:if>
						<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">修改</a>
						<a href="${ctx}/sys/user/delete?company.type=8&company.customerTypeTen=10&company.customerTypeEleven=11&id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
					</c:if>
					<c:if test="${user.conn == null && bizUser.delFlag==1 }">
						<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">修改</a>
						<a href="${ctx}/sys/user/delete?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
					</c:if>
					<c:if test="${user.conn == null && bizUser.delFlag==0}">
						<a href="${ctx}/sys/user/recovery?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">恢复</a>
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:if>
		</c:if>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
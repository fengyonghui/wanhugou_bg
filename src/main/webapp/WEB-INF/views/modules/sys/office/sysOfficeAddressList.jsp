<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<html>
<head>
	<title>地址信息管理</title>
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
		<li class="active"><a href="${ctx}/sys/office/sysOfficeAddress/list?office.type=${sysOfficeAddress.office.type}&office.customerTypeTen=${sysOfficeAddress.office.customerTypeTen}&office.customerTypeEleven=${sysOfficeAddress.office.customerTypeEleven}&office.source=${sysOfficeAddress.office.source}">地址信息列表</a></li>
		<shiro:hasPermission name="sys:office:sysOfficeAddress:edit"><li><a href="${ctx}/sys/office/sysOfficeAddress/form?office.type=${sysOfficeAddress.office.type}&office.customerTypeTen=${sysOfficeAddress.office.customerTypeTen}&office.customerTypeEleven=${sysOfficeAddress.office.customerTypeEleven}&office.source=${sysOfficeAddress.office.source}">地址信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="sysOfficeAddress" action="${ctx}/sys/office/sysOfficeAddress" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="office.type" type="hidden" value="${sysOfficeAddress.office.type}"/>
		<input name="office.customerTypeTen" type="hidden" value="${sysOfficeAddress.office.customerTypeTen}"/>
		<input name="office.customerTypeEleven" type="hidden" value="${sysOfficeAddress.office.customerTypeEleven}"/>
		<input name="office.source" type="hidden" value="${sysOfficeAddress.office.source}"/>
		<%--<form:hidden path="office.type"/>--%>
		<ul class="ul-form">
			<c:if test="${sysOfficeAddress.office.type != OfficeTypeEnum.CUSTOMER.type}">
				<li><label>采购中心：</label>
			</c:if>
			<c:if test="${sysOfficeAddress.office.type == OfficeTypeEnum.CUSTOMER.type}">
				<li><label>经销店名称：</label>
			</c:if>
					<sys:treeselect id="office" name="office.id" value="${sysOfficeAddress.office.id}"  labelName="office.name"
									labelValue="${sysOfficeAddress.office.name}" notAllowSelectParent="true"
									title="经销店"  url="/sys/office/queryTreeList?type=${sysOfficeAddress.office.type}&customerTypeTen=${sysOfficeAddress.office.customerTypeTen}&customerTypeEleven=${sysOfficeAddress.office.customerTypeEleven}&source=${sysOfficeAddress.office.source}"
									cssClass="input-medium"
									allowClear="true" dataMsgRequired="必填信息"/>
				</li>

			<li><label>联系电话：</label>
				<form:input path="phone" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>机构名称</th>
				<th>联系人</th>
				<th>联系电话</th>
				<th>详细地址</th>
				<th>地址类型</th>
				<th>是否默认</th>
				<th>创建人</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sys:office:sysOfficeAddress:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="sysOfficeAddress" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td><a href="${ctx}/sys/office/sysOfficeAddress/form?id=${sysOfficeAddress.id}">
					${sysOfficeAddress.office.name}
				</a></td>
				<td>
                    ${sysOfficeAddress.receiver}
                </td>
                <td>
                    ${sysOfficeAddress.phone}
                </td>
				<td>
					${sysOfficeAddress.bizLocation.pcrName}${sysOfficeAddress.bizLocation.address}
				</td>
				<td>
					${fns:getDictLabel(sysOfficeAddress.type, 'office_type', '未知类型')}
				</td>
				<td>
					${fns:getDictLabel(sysOfficeAddress.deFaultStatus, 'sysadd_deFault', '未知状态')}
				</td>
				<td>
					${sysOfficeAddress.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${sysOfficeAddress.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sys:office:sysOfficeAddress:edit"><td>
    				<a href="${ctx}/sys/office/sysOfficeAddress/form?id=${sysOfficeAddress.id}">修改</a>
					<a href="${ctx}/sys/office/sysOfficeAddress/delete?id=${sysOfficeAddress.id}" onclick="return confirmx('确认要删除该地址信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
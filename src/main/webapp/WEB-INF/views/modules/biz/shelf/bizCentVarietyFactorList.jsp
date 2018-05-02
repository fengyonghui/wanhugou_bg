<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购中心品类阶梯价管理</title>
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
		<li class="active"><a href="${ctx}/biz/shelf/bizCentVarietyFactor/">采购中心品类阶梯价列表</a></li>
		<shiro:hasPermission name="biz:shelf:bizCentVarietyFactor:edit"><li><a href="${ctx}/biz/shelf/bizCentVarietyFactor/form">采购中心品类阶梯价添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizCentVarietyFactor" action="${ctx}/biz/shelf/bizCentVarietyFactor/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>采购中心：</label>
				<sys:treeselect id="centerOffice" name="center.id" value="" labelName="customer.name"
								labelValue="" notAllowSelectParent="true"
								title="采购中心"  url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" extId="${centerOffice.id}"
								cssClass="input-medium"
								allowClear="${office.currentUser.admin}">
				</sys:treeselect>
			</li>
			<li><label>品类：</label>
				<form:select path="varietyInfo.id" class="input-medium">
					<form:option label="全部" value=""/>
					<form:options items="${varietyList}" itemLabel="name" itemValue="id"/>
				</form:select>
			</li>
			<li><label>货架：</label>
				<form:select path="shelfInfo.id" class="input-medium">
					<form:option label="全部" value=""/>
					<form:options items="${shelfList}" itemLabel="name" itemValue="id"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>采购中心</th>
				<th>货架</th>
				<th>品类</th>
				<th>最小数量</th>
				<th>最大数量</th>
				<th>服务费系数</th>
				<shiro:hasPermission name="biz:shelf:bizCentVarietyFactor:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizCentVarietyFactor">
			<tr>
				<td>${bizCentVarietyFactor.center.name}</td>
				<td>${bizCentVarietyFactor.shelfInfo.name}</td>
				<td>${bizCentVarietyFactor.varietyInfo.name}</td>
				<td>${bizCentVarietyFactor.minQty}</td>
				<td>${bizCentVarietyFactor.maxQty}</td>
				<td>${bizCentVarietyFactor.serviceFactor}</td>
				<shiro:hasPermission name="biz:shelf:bizCentVarietyFactor:edit"><td>
    				<a href="${ctx}/biz/shelf/bizCentVarietyFactor/form?id=${bizCentVarietyFactor.id}">修改</a>
					<a href="${ctx}/biz/shelf/bizCentVarietyFactor/delete?id=${bizCentVarietyFactor.id}" onclick="return confirmx('确认要删除该采购中心品类阶梯价吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
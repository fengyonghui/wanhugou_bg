<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品库存详情管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#toryExport").click(function(){
                top.$.jBox.confirm("确认要导出备货单盘点数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/biz/inventory/bizInventorySku/requestInventoryExport");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action","${ctx}/biz/inventory/bizInventorySku/inventory");
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });

			$("#btn").click(function () {
				var centId = $("#centerOfficeId").val();
				$.ajax({
					type:"post",
					data:{centId:centId},
					url:"${ctx}/biz/inventory/bizInventorySku/invSkuCount",
					success:function (data) {
						$("#invCount").val(data);
                    }
                });
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
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku/inventory">商品库存详情列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="requestHeader" action="${ctx}/biz/inventory/bizInventorySku/inventory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<%--<input id="zt" type="hidden" name="zt" value="${zt}"/>--%>
		<ul class="ul-form">
			<li><label>供应商：</label>
				<input name="vendName" value="${requestHeader.vendName}" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>品类：</label>
				<form:select path="varietyInfo.id" cssStyle="width: 100px" >
					<form:option value="" label="全部"/>
					<form:options items="${varietyList}" itemLabel="name" itemValue="id" htmlEscape="false" />
				</form:select>
			</li>

			<li><label>备货单号：</label>
				<input name="reqNo" value="${requestHeader.reqNo}" htmlEscape="false" class="input-medium"/>
			</li>

			<li><label>备货方：</label>
				<form:select path="fromType" cssStyle="width: 100px" >
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('req_from_type')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</li>
			<li><label>仓库名称：</label>
				<sys:treeselect id="invInfo" name="invInfo.id" value="${requestHeader.invInfo.id}" labelName="invInfo.name"
								labelValue="${requestHeader.invInfo.name}" notAllowSelectParent="true"
								title="仓库"  url="/biz/inventory/bizInventoryInfo/warehouseData"
								cssClass="input-medium" allowClear="true"/>
			</li>
			<li><label style="width: 90px;">库龄时长(天)：</label>
				<form:input path="inventoryAgeDay" htmlEscape="false"  class="input-medium"/>
			</li>
			<li>
				<label>审核状态：</label>
				<select name="invCommonProcess.type" class="input-medium">
					<option value="" label="请选择">请选择</option>
					<c:forEach items="${processList}" var="process">
						<option value="${process.code}">${process.name}</option>
					</c:forEach>
				</select>
			</li>
			<%--<li><label style="width: 90px;">库龄时长(天)：</label>--%>
				<%--<form:input path="inventoryAgeDay" htmlEscape="false"  class="input-medium"/>--%>
			<%--</li>--%>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
            <li class="btns"><input id="toryExport" class="btn btn-primary" type="button" value="导出"/></li>
		</ul>

	</form:form>
	<ul class="ul-form" style="list-style: none">
		<li><label style="width: 80px;text-align:right;">采购中心：</label>
			<sys:treeselect id="centerOffice" name="customer.id" value="" labelName="customer.name"
							labelValue="" notAllowSelectParent="true"
							title="采购中心"  url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" extId="${centerOffice.id}"
							cssClass="input-medium"
							allowClear="true">
			</sys:treeselect>
			<input id="btn" class="btn btn-primary" type="button" value="查询库存数量"/>
			<label style="width: 80px;text-align:right;">库存数量：</label>
			<input id="invCount" type="number" class="input-medium" readonly="readonly" value=""/>
		</li>
	</ul>
	<sys:message content="${message}"/>

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>类型</th>
				<th>仓库名称</th>
				<th>采购中心</th>
				<th>备货方</th>
				<th>备货单号</th>
				<th>供应商</th>
				<th>审核状态</th>
				<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizRequestHeader" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
					<%--<a href="${ctx}/biz/inventory/bizInventorySku/form?id=${bizInventorySku.id}">--%>
					${fns:getDictLabel(bizRequestHeader.headerType, 'req_header_type', '未知状态')}
				</a></td>
				<td>
					${bizRequestHeader.invInfo.name}
				</td>
				<td>
					${bizRequestHeader.fromOffice.name}
				</td>
				<td>
					${fns:getDictLabel(bizRequestHeader.fromType,'req_from_type','未知')}
				</td>
				<td>
					${bizRequestHeader.reqNo}
				</td>
				<td>
					${bizRequestHeader.vendName}
				</td>
				<td>
					${bizRequestHeader.invCommonProcess.invRequestProcess.name}
				</td>
				<td>
					<a href="${ctx}/biz/inventory/bizInventorySku/inventoryForm?id=${bizRequestHeader.id}&invId=${bizRequestHeader.invInfo.id}&source=detail">详情</a>
					<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
						<c:if test="${(bizRequestHeader.invCommonProcess.id == null || bizRequestHeader.invCommonProcess.invRequestProcess.name == '审批完成' || bizRequestHeader.invCommonProcess.invRequestProcess.name == '驳回')}">
							<a href="${ctx}/biz/inventory/bizInventorySku/inventoryForm?id=${bizRequestHeader.id}&invId=${bizRequestHeader.invInfo.id}">日常异动</a>
						</c:if>
					</shiro:hasPermission>
					<%--<a href="${ctx}/biz/inventory/bizInventorySku/inventoryForm?id=${bizRequestHeader.id}&invId=${bizRequestHeader.invInfo.id}&source=pChange">日常异动</a>--%>
					<shiro:hasPermission name="biz:inventory:bizInventorySku:audit">
						<c:if test="${bizRequestHeader.invCommonProcess.id != null && bizRequestHeader.invCommonProcess.invRequestProcess.name != '审批完成'
										&& bizRequestHeader.invCommonProcess.invRequestProcess.name != '驳回'
										&& (fns:hasRole(roleSet, bizRequestHeader.invCommonProcess.invRequestProcess.roleEnNameEnum) || fns:getUser().isAdmin())}">
							<a href="${ctx}/biz/inventory/bizInventorySku/inventoryForm?id=${bizRequestHeader.id}&invId=${bizRequestHeader.invInfo.id}&source=audit">审核</a>
						</c:if>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
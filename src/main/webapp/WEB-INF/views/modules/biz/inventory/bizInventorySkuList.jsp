<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品库存详情管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#toryExport").click(function(){
                top.$.jBox.confirm("确认要导出库存盘点数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/biz/inventory/bizInventorySku/torySkuExport");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action","${ctx}/biz/inventory/bizInventorySku/");
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
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku?invInfo.id=${bizInventorySku.invInfo.id}&zt=${zt}">商品库存详情列表</a></li>
		<c:if test="${zt eq '2'}">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
				<li><a href="${ctx}/biz/inventory/bizInventorySku/form?invInfo.id=${bizInventorySku.invInfo.id}&zt=${zt}">商品库存详情添加</a></li>
			</shiro:hasPermission>
		</c:if>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInventorySku" action="${ctx}/biz/inventory/bizInventorySku/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="zt" type="hidden" name="zt" value="${zt}"/>
		<input id="invInfo.id" type="hidden" name="invInfo.id" value="${bizInventorySku.invInfo.id}"/>
		<c:if test="${not empty bizInventorySku.reqSource && bizInventorySku.reqSource eq 'request_Inv'}">
			<input type="hidden" name="skuInfo.id" value="${bizInventorySku.skuInfo.id}">
		</c:if>
		<ul class="ul-form">
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false" class="input-medium"/>
                <input id="skuInfo.id" type="hidden" name="skuInfo.id" value="${skuInfo.id}"/>
			</li>
			<li><label>供应商：</label>
				<form:input path="skuInfo.vendorName" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>品类：</label>
				<form:select path="skuInfo.variety.id" cssStyle="width: 100px" >
					<form:option value="" label="全部"/>
					<form:options items="${varietyList}" itemLabel="name" itemValue="id" htmlEscape="false" />
				</form:select>
			</li>
			<li><label>商品编号：</label>
				<form:input path="skuInfo.partNo" htmlEscape="false"  class="input-medium"/>
			</li>
			<li><label>商品货号：</label>
				<form:input path="skuInfo.itemNo" htmlEscape="false"  class="input-medium"/>
			</li>
			<li><label>仓库名称：</label>
				<sys:treeselect id="invInfo" name="invInfo.id" value="${bizInventorySku.invInfo.id}" labelName="invInfo.name"
								labelValue="${bizInventorySku.invInfo.name}" notAllowSelectParent="true"
								title="仓库"  url="/biz/inventory/bizInventoryInfo/warehouseData"
								cssClass="input-medium" allowClear="true"/>
			</li>
			<li><label style="width: 90px;">库龄时长(天)：</label>
				<form:input path="inventoryAgeDay" htmlEscape="false"  class="input-medium"/>
			</li>
			<%--<li><label>库存类型：</label>
				<form:select path="invType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('inv_type')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/></form:select>
			</li>--%>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
            <li class="btns"><input id="toryExport" class="btn btn-primary" type="button" value="导出"/></li>
			<c:if test="${not empty bizInventorySku.reqSource && bizInventorySku.reqSource eq 'request_Inv'}">
				<li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/></li>
			</c:if>
			<li class="clearfix"></li>
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
				<th>库存类型</th>
				<th>仓库名称</th>
				<th>采购中心</th>
				<th style="width: 15%">商品名称</th>
				<th>出厂价</th>
				<th>商品总值</th>
				<th style="width: 5%">品类</th>
				<c:if test="${invStatus==1}">
					<th>专属客户</th>
				</c:if>
				<th>商品编号</th>
				<th>商品货号</th>
				<th>供应商</th>
				<th>库存数量</th>
				<c:if test="${zt eq '1' || zt eq '2'}">
					<th>销售订单数量</th>
					<th>出库量</th>
					<th>入库量</th>
					<th>供货部供货量</th>
					<th>调入数量</th>
					<th>调出数量</th>
					<shiro:hasAnyPermissions name="biz:inventory:inventoryAge:view">
					<th>查看库龄</th>
					</shiro:hasAnyPermissions>
					<%--<th>专属库存的客户</th>--%>
				</c:if>
				<c:if test="${zt eq '3'}">
					<th>修改时间</th>
					<th>修改人</th>
				</c:if>
				<c:if test="${zt eq '2' || zt eq '3'}">
					<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><th>操作</th></shiro:hasPermission>
				</c:if>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInventorySku" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
					<%--<a href="${ctx}/biz/inventory/bizInventorySku/form?id=${bizInventorySku.id}">--%>
					${fns:getDictLabel(bizInventorySku.invType, 'inv_type', '未知状态')}
				</a></td>
				<td>
					${bizInventorySku.invInfo.name}
				</td>
				<td>
					${bizInventorySku.customer.name}
				</td>
				<td>
					${bizInventorySku.skuInfo.name}
				</td>
				<td>
					${bizInventorySku.skuInfo.buyPrice}
				</td>
				<td>
					${bizInventorySku.skuInfo.buyPrice * bizInventorySku.stockQty}
				</td>
				<td>
					${bizInventorySku.skuInfo.variety.name}
				</td>
				<c:if test="${invStatus==1}">
					<td>
						${bizInventorySku.cust.name}
					</td>
				</c:if>
				<td>
					${bizInventorySku.skuInfo.partNo}
				</td>
				<td>
					${bizInventorySku.skuInfo.itemNo}
				</td>
				<td>
					${bizInventorySku.skuInfo.vendorName}
				</td>
				<td>
					${bizInventorySku.stockQty}
				</td>
				<c:if test="${zt eq '1' || zt eq '2'}">
					<td>
						${bizInventorySku.stockOrdQty}
					</td>
					<td>${bizInventorySku.outWarehouse}</td>
					<td>${bizInventorySku.inWarehouse}</td>
					<td>${bizInventorySku.sendGoodsNum}</td>
					<td>
						${bizInventorySku.transInQty}
					</td>
					<td>
						${bizInventorySku.transOutQty}
					</td>
					<shiro:hasAnyPermissions name="biz:inventory:inventoryAge:view">
					<td>
						<a href="${ctx}/biz/inventory/bizInventorySku/showInventoryAge?skuId=${bizInventorySku.skuInfo.id}&centId=${bizInventorySku.invInfo.customer.id}">查看库龄</a>
					</td>
					</shiro:hasAnyPermissions>
				</c:if>
				<c:if test="${zt eq '3'}">
					<td>
						<fmt:formatDate value="${bizInventorySku.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						${bizInventorySku.updateBy.name}
					</td>
				</c:if>
				<c:if test="${zt eq '2' || zt eq '3'}">
					<td>
						<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
							<c:if test="${bizInventorySku.delFlag!=null && bizInventorySku.delFlag!=0}">
								<a href="${ctx}/biz/inventory/bizInventorySku/form?id=${bizInventorySku.id}&invInfo.id=${bizInventorySku.invInfo.id}&zt=${zt}">修改</a>
								<a href="${ctx}/biz/inventory/bizInventorySku/delete?id=${bizInventorySku.id}&zt=${zt}"
								   onclick="return confirmx('确认要删除该商品库存详情吗？', this.href)">删除</a>
							</c:if>
							<c:if test="${bizInventorySku.delFlag!=null && bizInventorySku.delFlag==0}">
								<a href="${ctx}/biz/inventory/bizInventorySku/recovery?id=${bizInventorySku.id}&zt=${zt}"
								   onclick="return confirmx('确认要恢复该商品库存详情吗？', this.href)">恢复</a>
							</c:if>
						</shiro:hasPermission>
					</td>
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
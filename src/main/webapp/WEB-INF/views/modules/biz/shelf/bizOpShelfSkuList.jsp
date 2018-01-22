<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品上架管理</title>
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
		<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfSku/">商品上架列表</a></li>
		<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><li><a href="${ctx}/biz/shelf/bizOpShelfSku/form?shelfSign=0">商品上架添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOpShelfSku" action="${ctx}/biz/shelf/bizOpShelfSku/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品名称：</label>
				<form:input path="skuInfo.name" htmlEscape="false"  class="input-medium"/>
			</li>
			<li><label>商品编号：</label>
				<form:input path="skuInfo.partNo" htmlEscape="false"  class="input-medium"/>
			</li>
			<li><label>上架时间：</label>
				<input name="shelfStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizOpShelfSku.shelfStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				至
				<input name="shelfEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizOpShelfSku.shelfEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
		</ul>
			<%--<li><label>上架人：</label>
				<form:input path="shelfUser.id" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>--%>
		<ul class="ul-form">
			<c:if test="${fns:getUser().isAdmin()}">
				<li><label>采购中心：</label>
					<sys:treeselect id="centerOffice" name="centerOffice.id" value="${bizOpShelfSku.centerOffice.id}" labelName="centerOffice.name"
									labelValue="${bizOpShelfSku.centerOffice.name}"  notAllowSelectParent="true"
									title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-medium required" dataMsgRequired="必填信息">
					</sys:treeselect>
				</li>
			</c:if>
			<li><label>货架名称：</label>
				<form:input path="opShelfInfo.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>

			<li><label>下架时间：</label>
				<input name="unShelfStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizOpShelfSku.unShelfStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
                至
                <input name="unShelfEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                       value="<fmt:formatDate value="${bizOpShelfSku.unShelfEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                       onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>

		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>商品名称</th>
				<th>采购中心</th>
				<th>上架数量(个)</th>
				<th>原价(元)</th>
				<th>现价(元)</th>
				<th>最低销售数量(个)</th>
				<th>最高销售数量(个)</th>
                <th>显示次序</th>
                <th>上架人</th>
				<th>上架时间</th>
				<%--<th>下架人</th>--%>
				<th>下架时间</th>
				<th>创建人</th>
				<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizOpShelfSku">
			<tr>
				<td><a href="${ctx}/biz/shelf/bizOpShelfSku/form?id=${bizOpShelfSku.id}">
					${bizOpShelfSku.skuInfo.name}
				</a></td>
				<td>
				<c:choose >
					<c:when test="${bizOpShelfSku.centerOffice.id == 0 }">
						平台商品
					</c:when>
				<c:otherwise>
							${bizOpShelfSku.centerOffice.name}
				</c:otherwise>
				</c:choose>
				</td>
				<td>
					${bizOpShelfSku.shelfQty}
				</td>
				<td>
					${bizOpShelfSku.orgPrice}
				</td>
				<td>
					${bizOpShelfSku.salePrice}
				</td>
				<td>
					${bizOpShelfSku.minQty}
				</td>
				<td>
					${bizOpShelfSku.maxQty}
				</td>
                <td>
                        ${bizOpShelfSku.priority}
                </td>
                <td>
                        ${bizOpShelfSku.shelfUser.name}
                </td>
				<td>
					<fmt:formatDate value="${bizOpShelfSku.shelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<%--<td>--%>
					<%--${bizOpShelfSku.unshelfUser.name}--%>
				<%--</td>--%>
				<td>
					<fmt:formatDate value="${bizOpShelfSku.unshelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizOpShelfSku.createBy.name}
				</td>
				<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><td>
					<a href="${ctx}/biz/shelf/bizOpShelfSku/form?id=${bizOpShelfSku.id}">修改</a>
					<a href="${ctx}/biz/shelf/bizOpShelfSku/delete?id=${bizOpShelfSku.id}&shelfSign=0" onclick="return confirmx('确认要删除该商品上架吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
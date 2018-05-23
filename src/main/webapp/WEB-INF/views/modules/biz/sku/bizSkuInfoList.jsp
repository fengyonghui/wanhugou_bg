<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品管理</title>
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
	<script type="text/javascript">
        function skuInfoDelete(a,b){
            top.$.jBox.confirm("确认要删除该商品sku吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    $.ajax({
                        type:"post",
                        url:"${ctx}/biz/sku/bizSkuInfo/deleteSku",
                        data:"id="+a+"&sign="+b,
                        success:function(data){
							if(data=="opSheSku"){
								alert("商品上下架还有该商品sku，不能删除 ");
							}else if(data=="invSku"){
								alert("库存盘点还有该商品sku，不能删除 ");
							}else{
								$("#messDele").css("display","block");
								<%--使用setTimeout（）方法设定定时600毫秒--%>
								setTimeout(function(){
                                    window.location.reload();
                                },600);
							}
                        }
                    });
                }
            },{buttonsFocus:1});
            top.$('.jbox-body .jbox-icon').css('top','55px');
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz/sku/bizSkuInfo?productInfo.prodType=${prodType}">商品列表</a></li>
		<%--<shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><li><a href="${ctx}/biz/sku/bizSkuInfo/form">商品sku添加</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSkuInfo" action="${ctx}/biz/sku/bizSkuInfo?productInfo.prodType=${prodType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
            <li><label>商品类型：</label>
                <form:select path="skuType" class="input-xlarge required">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getDictList('skuType')}" itemLabel="label" itemValue="value"
                                  htmlEscape="false"/>
                </form:select>
            </li>
            <li><label>商品名称：</label>
                <form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
            </li>
			<li><label>商品编码：</label>
				<form:input path="partNo" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>商品货号：</label>
				<form:input path="itemNo" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<%--ajax删除保留当前搜索结果提示--%>
	<div class="alert alert-warning" style="display: none" id="messDele">
		<a href="#" class="close" data-dismiss="alert">
			&times;
		</a>
		删除商品sku成功
	</div>

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>商品图片</th>
				<th>商品名称</th>
				<th>商品类型</th>
				<th>产品名称</th>
				<th>商品编码</th>
				<th>商品货号</th>
				<%--<th>创建人</th>--%>
				<th>工厂价格</th>
				<th>创建时间</th>
				<th>更新人</th>
				<%--<th>更新时间</th>--%>
				<%--<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">--%>
					<th>操作</th>
				<%--</shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSkuInfo" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td>
						<img src="${bizSkuInfo.productInfo.imgUrl}" width="80px" height="80px"/>
				</td>
				<td style="font-size: larger"><a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}&str=detail&productInfo.prodType=${prodType}">
						${bizSkuInfo.name}</a>
					</td>
					<td>
                        ${fns:getDictLabel(bizSkuInfo.skuType, 'skuType', '未知类型')}
					</td>
					<td><a href="${ctx}/biz/product/bizProductInfoV2/form?id=${bizSkuInfo.productInfo.id}&prodType=${prodType}">
						${bizSkuInfo.productInfo.name}
					</a></td>
				    <td>
						<input name="partNo" value="${bizSkuInfo.partNo}" type="hidden"/>
						${bizSkuInfo.partNo}
					</td>
					<td>
						<input name="itemNo" value="${bizSkuInfo.itemNo}" type="hidden"/>
							${bizSkuInfo.itemNo}
					</td>
					<td>
						<input name="buyPrice" value="${bizSkuInfo.buyPrice}" type="hidden"/>
						${bizSkuInfo.buyPrice}
					</td>
					<%--<td>--%>
						<%--${bizSkuInfo.createBy.id}--%>
					<%--</td>--%>
					<td>
						<fmt:formatDate value="${bizSkuInfo.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
					</td>
					<td>
						${bizSkuInfo.updateBy.name}
					</td>
				<td>
					<c:if test="${bizSkuInfo.delFlag!=null && bizSkuInfo.delFlag==1}">
						<a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}&str=detail&productInfo.prodType=${prodType}">详情</a>
						<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
							<a href="${ctx}/biz/sku/bizSkuInfo/delete?id=${bizSkuInfo.id}&sign=0&productInfo.prodType=${prodType}" onclick="return confirmx('确认要删除该商品sku吗？', this.href)">删除</a>
							<%--<a href="${ctx}/biz/sku/bizSkuInfo/delete?id=${bizSkuInfo.id}&sign=0" onclick="return confirmx('确认要删除该商品sku吗？', this.href)">删除</a>--%>
							<a href="javascript:void(0);" onclick="skuInfoDelete(${bizSkuInfo.id},'0');">删除</a>
						</shiro:hasPermission>
					</c:if>

					<c:if test="${bizSkuInfo.delFlag!=null && bizSkuInfo.delFlag==0}">
						<shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
							<a href="${ctx}/biz/sku/bizSkuInfo/recovery?id=${bizSkuInfo.id}&sign=0&productInfo.prodType=${prodType}" onclick="return confirmx('确认要恢复该商品sku吗？', this.href)">恢复</a>
						</shiro:hasPermission>
					</c:if>

				</td>

			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
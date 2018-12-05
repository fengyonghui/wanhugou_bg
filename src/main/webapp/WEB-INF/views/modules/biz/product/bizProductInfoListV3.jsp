<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.ProdTypeEnum" %>
<html>
<head>
	<title>产品信息表管理</title>
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
        function productDelete(item){
            var cp=$("#cp").val();
            var cpName=$("#cpName").val();
            var cpPc=$("#cpPc").val();
            var cpIn=$("#cpIn").val();
            var cpBr=$("#cpBr").val();
            top.$.jBox.confirm("确认要删除该产品信息表吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    $.ajax({
                        type:"post",
                        url:"${ctx}/biz/product/bizProductInfoV3/prodDelete?id="+item,
                        data:"bizVarietyInfo.name="+cp+"&name="+cpName+"&prodCode="+cpPc+"&itemNo="+cpIn+"&brandName="+cpBr,
                        success:function(data){
                            if(data=="opSheSku"){
								alert("商品上下架还有该产品，不能删除 ");
							}else if(data=="skuS"){
								alert("商品sku还有该产品，不能删除 ");
							}else if(data=="invSku"){
								alert("库存盘点里还有该产品里的商品sku，不能删除 ");
							}else{
                                <%--alert("删除产品信息成功");--%>
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
		<li class="active"><a href="${ctx}/biz/product/bizProductInfoV3?prodType=${prodType}">产品信息表列表</a></li>
		<shiro:hasPermission name="biz:product:bizProductInfo:edit"><li><a href="${ctx}/biz/product/bizProductInfoV3/form?prodType=${prodType}">产品信息表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizProductInfo" action="${ctx}/biz/product/bizProductInfoV3?prodType=${prodType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<c:if test="${prodType == ProdTypeEnum.PROD.type}">
				<li><label>产品品类：</label>
					<form:input id="cp" path="bizVarietyInfo.name" htmlEscape="false" class="input-small"/>
				</li>
			</c:if>
			<li><label>产品名称：</label>
				<form:input id="cpName" path="name" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>产品代码：</label>
				<form:input id="cpPc" path="prodCode" htmlEscape="false" maxlength="10" class="input-small"/>
			</li>
			<li><label>产品货号：</label>
				<form:input id="cpIn" path="itemNo" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>品牌名称：</label>
				<form:input id="cpBr" path="brandName" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>创建日期：</label>
				<input name="createDateStart" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizProductInfo.createDateStart}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				至
				<input name="createDateEnd" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${bizProductInfo.createDateEnd}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
            <li><label>品类主管：</label>
                <form:select about="choose" path="user.id" class="input-medium">
                    <form:option value="" label="请选择"/>
                    <form:options items="${usersList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
                </form:select>
            </li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<%--删除保留当前搜索结果提示--%>
	<div class="alert alert-warning" style="display: none" id="messDele">
		<a href="#" class="close" data-dismiss="alert">
			&times;
		</a>
		删除产品信息成功
	</div>

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>序号</th>
				<th>产品图片</th>
				<c:if test="${prodType == ProdTypeEnum.PROD.type}">
					<th>产品品类</th>
				</c:if>
				<th>产品名称</th>
				<th>产品代码</th>
				<th>产品货号</th>
				<th>品牌名称</th>
				<c:if test="${prodType == ProdTypeEnum.PROD.type}">
					<th>产品描述</th>
				</c:if>
				<th>供应商</th>
				<th>最低售价</th>
				<th>最高售价</th>
                <c:if test="${prodType == ProdTypeEnum.PROD.type}">
                    <th>点击量</th>
                    <th>下单量</th>
                    <th>负责人</th>
                </c:if>
				<th>创建时间</th>
				<shiro:hasPermission name="biz:product:bizProductInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizProductInfo" varStatus="state">
			<c:if test="${bizProductInfo.delFlag!=null && bizProductInfo.delFlag==0}">
				<tr style="text-decoration:line-through;">
			</c:if>
			<c:if test="${bizProductInfo.delFlag!=null && bizProductInfo.delFlag==1}">
				<tr>
			</c:if>
				<td>${state.index+1}</td>
				<td><img src="${bizProductInfo.imgUrl}"style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/></td>
			<c:if test="${prodType == ProdTypeEnum.PROD.type}">
				<td>
					${bizProductInfo.bizVarietyInfo.name}
				</td>
			</c:if>
				<td>

					<a href="${ctx}/biz/product/bizProductInfoV3/form?id=${bizProductInfo.id}">
					${bizProductInfo.name}
				</a></td>
				<td>
					${bizProductInfo.prodCode}
				</td>
				<td>
					${bizProductInfo.itemNo}
				</td>
				<td>
					${bizProductInfo.brandName}
				</td>
			<c:if test="${prodType == ProdTypeEnum.PROD.type}">
				<td>
					${bizProductInfo.description}
				</td>
			</c:if>
				<td>
					${bizProductInfo.office.name}
				</td>
				<td>
					${bizProductInfo.minPrice}
				</td>
				<td>
					${bizProductInfo.maxPrice}
				</td>
            <c:if test="${prodType == ProdTypeEnum.PROD.type}">
                <td>
                    <c:if test="${bizProductInfo.prodVice !=0}">
                        <a href="${ctx}/biz/product/bizProdViewLog/list?productInfo.id=${bizProductInfo.id}&prodChixkSource=prod_chickCount">
                                ${bizProductInfo.prodVice}
                        </a>
                    </c:if>
                    <c:if test="${bizProductInfo.prodVice ==0}">
                        ${bizProductInfo.prodVice}
                    </c:if>
                </td>
                <td>
                    <c:if test="${bizProductInfo.orderCount !=0}">
                        <c:choose>
                            <c:when test="${bizProductInfo.skuItemNo !=null}">
                                <%--<a href="${ctx}/biz/order/bizOrderHeader/list?skuChickCount=prodCick_count&itemNo=${bizProductInfo.skuItemNo}">--%>
                                ${bizProductInfo.orderCount}
                                <%--</a>--%>
                            </c:when>
                            <c:otherwise>${bizProductInfo.orderCount}</c:otherwise>
                        </c:choose>
                    </c:if>
                    <c:if test="${bizProductInfo.orderCount ==0}">
                        ${bizProductInfo.orderCount}
                    </c:if>
                </td>
                <td>
                        ${bizProductInfo.user.name}
                </td>
            </c:if>
				<td>
					<fmt:formatDate value="${bizProductInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:product:bizProductInfo:edit">
					<td>
						<c:if test="${bizProductInfo.delFlag!=null && bizProductInfo.delFlag==1}">
							<a href="${ctx}/biz/product/bizProductInfoV3/form?id=${bizProductInfo.id}&prodType=${prodType}">修改</a>

							<a href="${ctx}/biz/product/bizProductInfoV3/copy?id=${bizProductInfo.id}&prodType=${prodType}">复制</a>
							<%--<a href="${ctx}/biz/product/bizProductInfoV3/delete?id=${bizProductInfo.id}" onclick="return confirmx('确认要删除该产品信息表吗？', this.href)">删除</a>--%>
							<a href="#" onclick="productDelete(${bizProductInfo.id});">删除</a>
							<%--<a href="${ctx}/biz/product/bizProductInfoV3/form?id=${bizProductInfo.id}">sku商品管理</a>--%>
						</c:if>
						<c:if test="${bizProductInfo.delFlag!=null && bizProductInfo.delFlag==0}">
							<a href="${ctx}/biz/product/bizProductInfoV3/recovery?id=${bizProductInfo.id}&prodType=${prodType}" onclick="return confirmx('确认要恢复该产品信息表吗？', this.href)">恢复</a>
						</c:if>
					</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
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
                        url:"${ctx}/biz/product/bizProductInfoV2/prodDelete?id="+item,
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
		<li class="active"><a href="${ctx}/biz/product/bizProductInfoV2?prodType=${prodType}">产品信息表列表</a></li>
		<shiro:hasPermission name="biz:product:bizProductInfo:edit"><li><a href="${ctx}/biz/product/bizProductInfoV2/form?prodType=${prodType}">产品信息表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizProductInfo" action="${ctx}/biz/product/bizProductInfoV2?prodType=${prodType}" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>产品品类：</label>
				<form:input id="cp" path="bizVarietyInfo.name" htmlEscape="false" class="input-small"/>
			</li>
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
				<th>产品品类</th>
				<th>产品名称</th>
				<th>产品代码</th>
				<th>产品货号</th>
				<th>品牌名称</th>
				<th>产品描述</th>
				<th>供应商</th>
				<th>最低售价</th>
				<th>最高售价</th>
				<th>点击量</th>
				<th>下单量</th>
				<th>负责人</th>
				<th>创建时间</th>
				<shiro:hasPermission name="biz:product:bizProductInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="productInfo" varStatus="state">
			<c:if test="${productInfo.delFlag!=null && productInfo.delFlag==0}">
				<tr style="text-decoration:line-through;">
			</c:if>
			<c:if test="${productInfo.delFlag!=null && productInfo.delFlag==1}">
				<tr>
			</c:if>
				<td>${state.index+1}</td>
				<td><img src="${productInfo.imgUrl}"style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/></td>
				<td>
					${productInfo.bizVarietyInfo.name}
				</td>

				<td>

					<a href="${ctx}/biz/product/bizProductInfoV2/form?id=${productInfo.id}">
					${productInfo.name}
				</a></td>
				<td>
					${productInfo.prodCode}
				</td>
				<td>
					${productInfo.itemNo}
				</td>
				<td>
					${productInfo.brandName}
				</td>
				<td>
					${productInfo.description}
				</td>
				<td>
					${productInfo.office.name}
				</td>
				<td>
					${productInfo.minPrice}
				</td>
				<td>
					${productInfo.maxPrice}
				</td>
				<td>
					<c:if test="${productInfo.prodVice !=0}">
						<a href="${ctx}/biz/product/bizProdViewLog/list?productInfo.id=${productInfo.id}&prodChixkSource=prod_chickCount">
							${productInfo.prodVice}
						</a>
					</c:if>
					<c:if test="${productInfo.prodVice ==0}">
						${productInfo.prodVice}
					</c:if>
				</td>
				<td>
					<c:if test="${productInfo.orderCount !=0}">
						<c:choose>
							<c:when test="${productInfo.skuItemNo !=null}">
								<%--<a href="${ctx}/biz/order/bizOrderHeader/list?skuChickCount=prodCick_count&itemNo=${productInfo.skuItemNo}">--%>
										${productInfo.orderCount}
								<%--</a>--%>
							</c:when>
							<c:otherwise>${productInfo.orderCount}</c:otherwise>
						</c:choose>
					</c:if>
					<c:if test="${productInfo.orderCount ==0}">
						${productInfo.orderCount}
					</c:if>
				</td>
				<td>
					${productInfo.user.name}
				</td>
				<td>
					<fmt:formatDate value="${productInfo.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:product:bizProductInfo:edit">
					<td>
						<c:if test="${productInfo.delFlag!=null && productInfo.delFlag==1}">
							<a href="${ctx}/biz/product/bizProductInfoV2/form?id=${productInfo.id}&prodType=${prodType}">修改</a>

							<a href="${ctx}/biz/product/bizProductInfoV2/copy?id=${productInfo.id}&prodType=${prodType}">复制</a>
							<%--<a href="${ctx}/biz/product/bizProductInfoV2/delete?id=${productInfo.id}" onclick="return confirmx('确认要删除该产品信息表吗？', this.href)">删除</a>--%>
							<a href="#" onclick="productDelete(${productInfo.id});">删除</a>
							<%--<a href="${ctx}/biz/product/bizProductInfoV2/form?id=${productInfo.id}">sku商品管理</a>--%>
						</c:if>
						<c:if test="${productInfo.delFlag!=null && productInfo.delFlag==0}">
							<a href="${ctx}/biz/product/bizProductInfoV2/recovery?id=${productInfo.id}&prodType=${prodType}" onclick="return confirmx('确认要恢复该产品信息表吗？', this.href)">恢复</a>
						</c:if>
					</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
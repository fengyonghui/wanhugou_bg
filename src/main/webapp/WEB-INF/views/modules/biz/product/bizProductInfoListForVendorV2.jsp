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
                        url:"${ctx}/biz/product/bizProductInfoForVendorV2/prodDelete?id="+item,
                        data:"bizVarietyInfo.name="+cp+"&name="+cpName+"&prodCode="+cpPc+"&itemNo="+cpIn+"&brandName="+cpBr,
                        success:function(data){
                            if(data=="ok"){
                                <%--alert("删除产品信息成功");--%>
                                $("#messDele").css("display","block");
                                <%--使用setTimeout（）方法设定定时580毫秒--%>
                                setTimeout(function(){
                                    window.location.reload();
                                },580);
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
		<li class="active"><a href="${ctx}/biz/product/bizProductInfoForVendorV2/">产品信息表列表</a></li>
		<shiro:hasPermission name="biz:product:bizProductInfoForVendor:edit"><li><a href="${ctx}/biz/product/bizProductInfoForVendorV2/form">产品信息表添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizProductInfo" action="${ctx}/biz/product/bizProductInfoForVendorV2/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>产品品类：</label>
				<form:input id="cp" path="bizVarietyInfo.name" htmlEscape="false" class="input-small"/>
			</li>
			<li><label>产品名称：</label>
				<form:input  id="cpName" path="name" htmlEscape="false" class="input-medium"/>
			</li>
			<li><label>产品代码：</label>
				<form:input id="cpPc" path="prodCode" htmlEscape="false" maxlength="10" class="input-small"/>
			</li>
			<li><label>产品货号：</label>
				<form:input  id="cpIn" path="itemNo" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<li><label>品牌名称：</label>
				<form:input id="cpBr" path="brandName" htmlEscape="false" maxlength="50" class="input-medium"/>
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
				<td>序号</td>
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
				<th>审核状态</th>
				<th>品类主管</th>
				<th>点击量</th>
				<th>订单量</th>

				<shiro:hasPermission name="biz:product:bizProductInfoForVendor:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizProductInfo" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td><img src="${bizProductInfo.imgUrl}"style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/></td>
				<td>
					${bizProductInfo.bizVarietyInfo.name}
				</td>
				<td>
					<a href="${ctx}/biz/product/bizProductInfoForVendorV2/form?id=${bizProductInfo.id}&view=true">
							${bizProductInfo.name}
					</a>
				</td>
				<td>
					${bizProductInfo.prodCode}
				</td>
				<td>
					${bizProductInfo.itemNo}
				</td>
				<td>
					${bizProductInfo.brandName}
				</td>
				<td>
					${bizProductInfo.description}
				</td>
				<td>
					${bizProductInfo.office.name}
				</td>
				<td>
					${bizProductInfo.minPrice}
				</td>
				<td>
					${bizProductInfo.maxPrice}
				</td>
				<td>
					<c:if test="${bizProductInfo.bizStatus == 1}">未审核</c:if>
					<c:if test="${bizProductInfo.bizStatus == 2}">审核通过</c:if>
					<c:if test="${bizProductInfo.bizStatus == 3}">审核失败</c:if>
				</td>
				<td>
						${bizProductInfo.bizVarietyInfo.user.name}
				</td>
				<td>
						${bizProductInfo.prodVice}
				</td>
				<td>
						${bizProductInfo.orderCount}
				</td>
				<td>
					<shiro:hasPermission name="biz:product:bizProductInfoForVendor:check">
                        <c:if test="${bizProductInfo.bizStatus == 1}">
                            <a href="${ctx}/biz/product/bizProductInfoForVendorV2/form?id=${bizProductInfo.id}&view=audit">审核</a>&nbsp;&nbsp;
                        </c:if>
                    </shiro:hasPermission>
					<shiro:hasPermission name="biz:product:bizProductInfoForVendor:edit">
						<c:if test="${bizProductInfo.bizStatus == 1 || bizProductInfo.bizStatus == 3}">
							<a href="${ctx}/biz/product/bizProductInfoForVendorV2/form?id=${bizProductInfo.id}">修改</a>&nbsp;&nbsp;
						</c:if>
						<c:if test="${bizProductInfo.bizStatus != 1}">
							<a href="${ctx}/biz/product/bizProductInfoForVendorV2/form?id=${bizProductInfo.id}&view=true">查看</a>&nbsp;&nbsp;
						</c:if>
						<a href="${ctx}/biz/product/bizProductInfoForVendorV2/copy?id=${bizProductInfo.id}">复制</a>&nbsp;&nbsp;
						<a href="#" onclick="productDelete(${bizProductInfo.id});">删除</a>&nbsp;&nbsp;
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination ">${page}</div>
</body>
</html>
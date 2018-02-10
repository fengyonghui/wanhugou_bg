<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>找货定制管理</title>
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
        <%--/* 找货列表 */--%>
        function findSku(thisa,searchId){

            var valNmu = $(thisa).attr("findg");
            var html = "<div style='padding:10px;'>输入商品编码：<input type='text' id='partNo' name='partName' value='"+valNmu+"'/></div>";
            var submit = function (v, h, f) {
                if (f.yourname == '') {
                    $.jBox.tip("请输入您的商品编码", 'error', { focusId: "partNo" }); // 关闭设置 partNo 为焦点
                    return false;
                }
                if (v === 'ok') {
                    $.post("${ctx}/biz/sku/bizSearch/savePartNo",
                        {searchId:searchId,partNo:f.partName},
                        function(data,status) {
							if(data){
                                window.location.href="${ctx}/biz/sku/bizSearch";
								$.jBox.tip("商品编码：" + f.partName);
							}
                        });
                }


                // var id = $("#searchId").val();
                // var partNo = $("#partName").val();
				<%--$.ajax({--%>
					<%--url:"${ctx}/biz/sku/bizSearch/save?bizSearchId = " + id + "&&partNo = " + partNo ,--%>
					<%--type:"get"--%>
				<%--})--%>
                return true;
            };
            $.jBox(html, { title: "找货", submit: submit });
        }
		</script>
		<script type="text/javascript">
			function findN(obj){
				$.ajax({
					type:"post",
					url:"${ctx}/biz/sku/bizSearch/saveNone",
					data:"searchId="+obj,
					success:function (data) {
						if(data==true){
						   alert("暂无货源");
                            window.location.href="${ctx}/biz/sku/bizSearch";
						}else{
						    alert("未知");
						}
                    }
				});
			}

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz/sku/bizSearch/">找货定制列表</a></li>
		<shiro:hasPermission name="biz:sku:bizSearch:edit"><li><a href="${ctx}/biz/sku/bizSearch/form">找货定制添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizSearch" action="${ctx}/biz/sku/bizSearch/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>商品编码：</label>
				<form:input path="partNo" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>分类名称：</label>
				<form:input path="cateId.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>找货名称：</label>
				<form:input path="cateName" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li><label>材质名称：</label>
				<form:input path="qualityId" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>颜色：</label>
				<form:input path="color" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>规格：</label>
				<form:input path="standard" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>业务状态：</label>
				<%--<form:input path="businessStatus" htmlEscape="false" maxlength="4" class="input-medium"/>--%>
				<form:select path="businessStatus" class="input-medium ">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('businessStatus')}" itemLabel="label" itemValue="value"
								  htmlEscape="false"/>
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
				<th>商品编码</th>
				<th>分类名称</th>
				<th>找货名称</th>
				<th>材质名称</th>
				<th>颜色</th>
				<th>规格</th>
				<th>业务状态</th>
				<th>期望到货时间</th>
				<th>用户名称</th>
				<th>期望最低售价</th>
				<th>期望最高价</th>
				<th>数量</th>
				<shiro:hasPermission name="biz:sku:bizSearch:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizSearch">
			<tr>
				<td><a href="${ctx}/biz/sku/bizSearch/form?id=${bizSearch.id}">
					${bizSearch.partNo}
				</a></td>
				<td>
					${bizSearch.cateId.name}
				</td>
				<td>
					${bizSearch.cateName}
				</td>
				<td>
					${bizSearch.qualityId}
				</td>
				<td>
					${bizSearch.color}
				</td>
				<td>
					${bizSearch.standard}
				</td>
				<td>
					${fns:getDictLabel(bizSearch.businessStatus, 'businessStatus', '未知状态')}
				</td>
				<td>
					<fmt:formatDate value="${bizSearch.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${bizSearch.user.name}
				</td>
				<td>
					${bizSearch.minPrice}
				</td>
				<td>
					${bizSearch.maxPrice}
				</td>
				<td>
					${bizSearch.amount}
				</td>
				<shiro:hasPermission name="biz:sku:bizSearch:edit"><td>
    				<%--<a href="${ctx}/biz/sku/bizSearch/form?id=${bizSearch.id}">修改</a>--%>
						<div class="control-group">
							<%--<label class="control-label">修改：</label>--%>
							<div class="controls">
								<%--<input type="button" onclick="findSku(this);" value="找货" htmlEscape="false" class="input-xlarge required" findg = ${bizSearch.partNo} />--%>
									<input type="button" onclick="findSku(this,${bizSearch.id});" value="找货" htmlEscape="false" class="input-medium required" findg="${bizSearch.partNo}" />
									<%--<label class="error" id="addError" style="display:none;">必填信息</label>--%>
								<span class="help-inline"></span>
							</div>
						</div>
						<div class="control-group">
							<div class="controls">
								<input type="button" onclick="findN(${bizSearch.id});" value="暂无货源" htmlEscape="false" class="input-medium required" />
								<span class="help-inline"></span>
							</div>
						</div>
					<a href="${ctx}/biz/sku/bizSearch/delete?id=${bizSearch.id}" onclick="return confirmx('确认要删除该找货定制吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
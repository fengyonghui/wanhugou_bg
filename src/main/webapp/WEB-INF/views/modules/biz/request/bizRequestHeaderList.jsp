<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#buttonExport").click(function(){
				top.$.jBox.confirm("确认要导出备货清单数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestHeader/requestHeaderExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestHeader/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
        function checkInfo(obj,val,hid) {
            $.ajax({
                type:"post",
                url:"${ctx}/biz/request/bizRequestHeader/saveInfo",
                data:{checkStatus:obj,id:hid},
                success:function (data) {
                    if(data){
                        alert(val+"成功！");
                        window.location.href="${ctx}/biz/request/bizRequestHeader";

                    }
                }
            })
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz/request/bizRequestHeader/">备货清单列表</a></li>
		<shiro:hasPermission name="biz:request:bizRequestHeader:edit"><li><a href="${ctx}/biz/request/bizRequestHeader/form">备货清单添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestHeader/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>备货单号：</label>
				<form:input path="reqNo" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>货号：</label>
				<form:input path="itemNo" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>供应商：</label>
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>采购中心：</label>
				<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
								labelValue="${entity.fromOffice.name}"
								title="采购中心"  url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" cssClass="input-medium required" dataMsgRequired="必填信息">
				</sys:treeselect>
			</li>
			<li><span><label>期望收货时间：</label></span>
				<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
			<li><label>业务状态：</label>
				<form:select path="bizStatus" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_req_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input id="buttonExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>备货单号</th>
				<th>采购中心</th>
				<th>期望收货时间</th>
				<th>备货商品数量</th>
				<th>备货商品总价</th>
				<th>已到货数量</th>
				<th>备注</th>
				<th>业务状态</th>
				<th>申请人</th>
				<th>更新时间</th>
				<shiro:hasAnyPermissions name="biz:request:bizRequestHeader:edit,biz:request:bizRequestHeader:view"><th>操作</th></shiro:hasAnyPermissions>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="requestHeader">
			<tr>
				<td>
					<c:choose>
						<c:when test="${fns:getUser().isAdmin()}">
							<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}">
						</c:when>
						<c:when test="${requestHeader.bizStatus<ReqHeaderStatusEnum.APPROVE.state}">
							<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}">
							</c:when>
							<c:otherwise>
								<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}&str=detail">
							</c:otherwise>
					</c:choose>
					${requestHeader.reqNo}
					</a>
				</td>
				<td>
					${requestHeader.fromOffice.name}
				</td>
				<td>
					<fmt:formatDate value="${requestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>${requestHeader.reqQtys}</td>
				<td>${requestHeader.totalMoney}</td>
				<td>${requestHeader.recvQtys}</td>
				<td>
					${requestHeader.remark}
				</td>
				<td>
					${fns:getDictLabel(requestHeader.bizStatus, 'biz_req_status', '未知类型')}
				</td>
				<td>
					${requestHeader.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${requestHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:request:bizRequestHeader:view"><td>

					<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}&str=detail">详情</a>

				<shiro:hasPermission name="biz:request:bizRequestHeader:edit">
					<c:choose>
						<c:when test="${fns:getUser().isAdmin()}">
							<c:if test="${requestHeader.delFlag!=null && requestHeader.delFlag!=0}">
								<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}">修改</a>
								<a href="${ctx}/biz/request/bizRequestHeader/delete?id=${requestHeader.id}" onclick="return confirmx('确认要删除该备货清单吗？', this.href)">删除</a>
							</c:if>
							<c:if test="${requestHeader.delFlag!=null && requestHeader.delFlag==0}">
								<a href="${ctx}/biz/request/bizRequestHeader/recovery?id=${requestHeader.id}" onclick="return confirmx('确认要恢复该备货清单吗？', this.href)">恢复</a>
							</c:if>
						</c:when>
						<c:when test="${!fns:getUser().isAdmin() && requestHeader.bizStatus<ReqHeaderStatusEnum.APPROVE.state}">
							<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}">修改</a>

							<a href="${ctx}/biz/request/bizRequestHeader/delete?id=${requestHeader.id}" onclick="return confirmx('确认要删除该备货清单吗？', this.href)">删除</a>
						</c:when>
						<c:when test="${requestHeader.bizStatus>=ReqHeaderStatusEnum.APPROVE.state && requestHeader.bizStatus<=ReqHeaderStatusEnum.STOCK_COMPLETE.state}">
							<a href="#" onclick="checkInfo(${ReqHeaderStatusEnum.CLOSE.state},this.value,${requestHeader.id})">关闭</a>
						</c:when>
						<%--<c:when test="${requestHeader.bizStatus==ReqHeaderStatusEnum.COMPLETE.state}">--%>
							<%--<a href="#" onclick="checkInfo(${ReqHeaderStatusEnum.CLOSE.state},this.value,${requestHeader.id})">关闭</a>--%>
						<%--</c:when>--%>
					</c:choose>


				</shiro:hasPermission>

				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
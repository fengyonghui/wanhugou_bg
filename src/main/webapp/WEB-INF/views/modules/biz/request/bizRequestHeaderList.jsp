<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.RoleEnNameEnum" %>
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
        function pay(reqId){
			$("#myModal").find("#reqId").val(reqId);

		}
        function genPayQRCode(obj) {
			var payMoney =$("#payMoneyId").val();
			var reqId = $("#reqId").val();
			var payMethod=$("input[name='payMethod']:checked").val();
            $.ajax({
                type:"post",
                url:"${ctx}/biz/request/bizRequestPay/genPayQRCode",
               data:{payMoney:payMoney,reqId:reqId,payMethod:payMethod},
                success:function (data) {
                    <%--if(data){--%>
                        <%--alert(val+"成功！");--%>
                        <%--window.location.href="${ctx}/biz/request/bizRequestHeader";--%>

                    <%--}--%>
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
			<%--<li><label>货号：</label>--%>
				<%--<form:input path="itemNo" htmlEscape="false" maxlength="20" class="input-medium"/>--%>
			<%--</li>--%>
			<%--<li><label>供应商：</label>--%>
				<%--<form:input path="name" htmlEscape="false" maxlength="20" class="input-medium"/>--%>
			<%--</li>--%>
			<li><label>采购中心：</label>
				<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
								labelValue="${entity.fromOffice.name}" allowClear="true"
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
			<li><label>测试数据</label>
				<form:checkbox path="includeTestData" htmlEscape="false" maxlength="100" class="input-medium"/>
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
				<td>序号</td>
				<th>备货单号</th>
				<th>采购中心</th>
				<th>期望收货时间</th>
				<th>备货商品数量</th>
				<th>备货商品总价</th>
				<th>已到货数量</th>
				<th>备注</th>
				<th>业务状态</th>
				<th>下单时间</th>
				<th>申请人</th>
				<th>更新时间</th>
				<shiro:hasAnyPermissions name="biz:request:bizRequestHeader:edit,biz:request:bizRequestHeader:view"><th>操作</th></shiro:hasAnyPermissions>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="requestHeader" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
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
					<c:if test="${requestHeader.bizStatus==ReqHeaderStatusEnum.UNREVIEWED.state}">
						${requestHeader.commonProcess.requestOrderProcess.name}
					</c:if>
					<c:if test="${requestHeader.bizStatus!=ReqHeaderStatusEnum.UNREVIEWED.state}">
						${fns:getDictLabel(requestHeader.bizStatus, 'biz_req_status', '未知类型')}
					</c:if>
				</td>
				<td>
					<fmt:formatDate value="${requestHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
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

							<a href="#" onclick="checkInfo(${ReqHeaderStatusEnum.CLOSE.state},'关闭',${requestHeader.id})">关闭</a>

						</c:when>

						<%--<c:when test="${requestHeader.bizStatus==ReqHeaderStatusEnum.COMPLETE.state}">--%>
							<%--<a href="#" onclick="checkInfo(${ReqHeaderStatusEnum.CLOSE.state},this.value,${requestHeader.id})">关闭</a>--%>
						<%--</c:when>--%>
					</c:choose>
					<a data-toggle="modal" onclick="pay(${requestHeader.id})" data-id="${requestHeader.id}" data-target="#myModal">付款</a>
				</shiro:hasPermission>

					<shiro:hasPermission name="biz:request:bizRequestHeader:audit">
					<c:if test="${fn:containsIgnoreCase(fns:getUser().roleList, requestHeader.commonProcess.requestOrderProcess.roleEnNameEnum) && requestHeader.bizStatus==ReqHeaderStatusEnum.UNREVIEWED.state && requestHeader.commonProcess.requestOrderProcess.name != '驳回'
							&& requestHeader.commonProcess.requestOrderProcess.code != auditStatus
							}">
						<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}&str=audit">审核</a>
					</c:if>
				</shiro:hasPermission>

				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>

	<!-- 模态框（Modal） -->
	<div class="modal fade hide" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel">付款</h4>
				</div>
				<div class="modal-body">
					<input id="reqId" type="hidden" value="" />
					支付金额：<input type="text" id="payMoneyId" />
					支付方式：<input type="radio" name="payMethod"  value="0"> 支付宝
							<input type="radio" name="payMethod"  value="1"> 微信
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" onclick="genPayQRCode();" class="btn btn-primary">提交</button>
				</div>
			</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>

	<div class="pagination">${page}</div>
</body>
</html>
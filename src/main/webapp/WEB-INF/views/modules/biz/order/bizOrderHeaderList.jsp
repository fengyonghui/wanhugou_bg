<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        <%--用于页面按下键盘Backspace键回退页面的问题--%>
        <%--处理键盘事件 禁止后退键（Backspace）密码或单行、多行文本框除外   --%>
    function banBackSpace(e){
        var ev = e || window.event;<%--获取event对象--%>
        var obj = ev.target || ev.srcElement;<%--获取事件源--%>
        var t = obj.type || obj.getAttribute('type');<%--获取事件源类型--%>
        <%--获取作为判断条件的事件类型--%>
        var vReadOnly = obj.getAttribute('readonly');
        var vEnabled = obj.getAttribute('enabled');
        <%--处理null值情况--%>
        vReadOnly = (vReadOnly == null) ? false : vReadOnly;
        vEnabled = (vEnabled == null) ? true : vEnabled;
        <%--当敲Backspace键时，事件源类型为密码或单行、多行文本的--%>
        <%--并且readonly属性为true或enabled属性为false的，则退格键失效--%>
        var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea")
        && (vReadOnly==true || vEnabled!=true))?true:false;
        <%--当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效--%>
        var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea")
        ?true:false;
        <%--判断--%>
            if(flag2){
                return false;
            }
            if(flag1){
                return false;
            }
    }
        <%--禁止后退键 作用于Firefox、Opera--%>
        document.onkeypress=banBackSpace;
        <%--禁止后退键 作用于IE、Chrome--%>
        document.onkeydown=banBackSpace;
    </script><%--用于键盘Bcackspace回退BUG问题--%>
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
		<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
			<li class="active"><a href="${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">订单信息列表</a></li>
		</c:if>
		<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
			<li class="active"><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
			<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><li><a href="${ctx}/biz/order/bizOrderHeader/form">订单信息添加</a></li></shiro:hasPermission>
		</c:if>
	</ul>
	<form:form id="searchForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderNum" name="bizOrderHeader.orderNum" type="hidden" value="${bizOrderHeader.orderNum}"/>
		<%--<input id="customer" name="bizOrderHeader.customer.id" type="hidden" value="${bizOrderHeader.customer.id}"/>--%>
		<form:hidden path="consultantId"/>
		<ul class="ul-form">
			<li><label>订单编号：</label>
				<form:input path="orderNum" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<%--<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"--%>
					<%--labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"--%>
					<%--title="采购商"  url="/sys/office/queryTreeList?type=6"--%>
					<%--cssClass="input-medium required"--%>
					<%--allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>--%>
			<li><label>采购商名称：</label>
				<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
					 <sys:treeselect id="office" name="customer.id" value=""  labelName="customer.name"
										labelValue="" notAllowSelectParent="true"
										title="采购商"  url="/sys/office/queryTreeList?type=6"
										cssClass="input-medium required"
										allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
					<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}">
					<input type="hidden" name="flag" value="${bizOrderHeader.flag}">
				</c:if>
				<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
					<sys:treeselect id="office" name="customer.id" value=""  labelName="customer.name"
									labelValue="" notAllowSelectParent="true"
									title="采购商"  url="/sys/office/queryTreeList?type=6"
									cssClass="input-medium required"
									allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
				</c:if>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<c:if test="${bizOrderHeader.flag=='check_pending'}">
				<li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/></li>

			</c:if>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>订单编号</th>
				<th>订单类型</th>
				<th>采购商名称</th>
				<th>商品详情总价</th>
				<th>订单总费用</th>
				<th>运费</th>
				<th>订单总费用</th>
				<th>发票状态</th>
				<th>业务状态</th>
				<th>订单来源</th>
				<%--<th>订单收货地址</th>--%>
				<th>创建人</th>
				<th>订单创建时间</th>
				<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="orderHeader">
			<tr>
				<td>
					<c:if test="${bizOrderHeader.flag=='check_pending'}">
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">
								${orderHeader.orderNum}</a>
					</c:if>
					<c:if test="${empty bizOrderHeader.flag}">
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details">
								${orderHeader.orderNum}</a>
					</c:if>
				</td>
				<%--<td>--%>
					<%--${fns:getDictLabel(bizOrderHeader.orderType, 'biz_order_type', '未知状态')}--%>
				<%--</td>--%>
				<%---start----%>
				<td><c:if test="${orderHeader.bizType ==1}">
						专营
					</c:if>
					<c:if test="${orderHeader.bizType ==2}">
						非专营
					</c:if><c:if test="${orderHeader.bizType ==0 || orderHeader.bizType ==null || orderHeader.bizType =='' || orderHeader.bizType >3}">
						未知
					</c:if>
				</td>
				<%----end---%>
				<td>
					${orderHeader.customer.name}
				</td>
				<td>
					<fmt:formatNumber type="number" value="${orderHeader.totalDetail}" pattern="0.00"/>
				</td>
				<td>
					<fmt:formatNumber type="number" value="${orderHeader.totalExp}" pattern="0.00"/>
				</td>
				<td>
					<fmt:formatNumber type="number" value="${orderHeader.freight}" pattern="0.00"/>
				</td>
				<td><font color="red">
					<fmt:formatNumber type="number" value="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight}" pattern="0.00"/>
				</font></td>
				<td>
					${fns:getDictLabel(orderHeader.invStatus, 'biz_order_invStatus', '未知状态')}
				</td>
				<td>
					${fns:getDictLabel(orderHeader.bizStatus, 'biz_order_status', '未知状态')}
				</td>
				<td>
					${orderHeader.platformInfo.name}
				</td>
				<%--<td>--%>
					<%--${bizOrderHeader.bizLocation.pcrName}${bizOrderHeader.bizLocation.address}--%>
				<%--</td>--%>
				<td>
					${orderHeader.createBy.name}
				</td>
				<td>
                    <fmt:formatDate value="${orderHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </td>
				<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><td>
					<c:choose>
					<c:when test="${bizOrderHeader.flag=='check_pending'}">
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">
							<c:if test="${orderHeader.bizStatus!=OrderHeaderBizStatusEnum.SUPPLYING.state && orderHeader.bizStatus!=OrderHeaderBizStatusEnum.UNAPPROVE.state}">
								待审核
							</c:if>
							<c:if test="${orderHeader.bizStatus==OrderHeaderBizStatusEnum.SUPPLYING.state}">
								审核成功
							</c:if>
							<c:if test="${orderHeader.bizStatus==OrderHeaderBizStatusEnum.UNAPPROVE.state}">
								审核失败
							</c:if>
						</a>
					</c:when>
					<c:otherwise>
						<c:if test="${orderHeader.bizStatus==0 || orderHeader.bizStatus==5 ||
									orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight!=orderHeader.receiveTotal}">
							<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderNoEditable=editable">待支付</a>
						</c:if>
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details">查看详情</a>

							<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}">修改</a>
						<c:if test="${fns:getUser().isAdmin()}">
							<a href="${ctx}/biz/order/bizOrderHeader/delete?id=${orderHeader.id}" onclick="return confirmx('确认要删除该订单信息吗？', this.href)">删除</a>
						</c:if>
					</c:otherwise>
					</c:choose>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
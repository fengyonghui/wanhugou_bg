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
			$("#buttonExport").click(function(){
				top.$.jBox.confirm("确认要导出订单数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/biz/order/bizOrderHeader/orderHeaderExport");
						$("#searchForm").submit();
						<%--$("#buttonExport").attr("disabled",true);--%>
						$("#searchForm").attr("action","${ctx}/biz/order/bizOrderHeader/");
						<%--$("#buttonExport").removeAttr("disabled");--%>
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
	</script>
</head>
<body>
<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz/order/bizOrderHeader/cendList">订单信息列表</a></li>
		<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><li><a href="${ctx}/biz/order/bizOrderHeader/cendform">订单信息添加</a></li></shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader/cendList" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<ul class="ul-form">
		<li><label>订单编号：</label>
			<form:input path="orderNum" htmlEscape="false" maxlength="30" class="input-medium"/>
		</li>
		<li><label>订单状态：</label>
			<form:select path="bizStatus" class="input-medium">
				<form:option value="" label="请选择"/>
				<form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"
							  htmlEscape="false"/></form:select>
		</li>
		<li>
			<label>货架编号：</label>
			<form:input path="itemNo" htmlEscape="false" maxlength="30" class="input-medium"/>
		</li>
		<li><label>采购商名称：</label>
			<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
							labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
							title="采购商"  url="/sys/office/queryTreeList?type=6"
							cssClass="input-medium"
							allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
		</li>
		<li><label>采购中心：</label>
			<form:input path="centersName" htmlEscape="false" maxlength="100" class="input-medium"/>
		</li>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
		<%--<li class="btns"><input id="buttonExport" class="btn btn-primary" type="button" value="导出"/></li>--%>
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
		<th>所属采购中心</th>
		<th>采购商电话</th>
		<th>商品总价</th>
		<th>调整金额</th>
		<th>运费</th>
		<th>应付金额</th>
		<th>利润</th>
		<th>发票状态</th>
		<th>业务状态</th>
		<th>订单来源</th>
		<th>创建人</th>
		<th>创建时间</th>
		<th>更新时间</th>
		<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><th>操作</th></shiro:hasPermission>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="orderHeader">
		<tr>
			<td>
				<a href="${ctx}/biz/order/bizOrderHeader/cendform?id=${orderHeader.id}&orderDetails=details">
						${orderHeader.orderNum}</a>
			</td>
			<td>
					${fns:getDictLabel(orderHeader.orderType, 'biz_order_type', '未知状态')}
			</td>
			<td>
					${orderHeader.customer.name}
			</td>
			<td>
					${orderHeader.centersName}
			</td>
			<td>
					${orderHeader.customer.phone}
			</td>
			<td><font color="#848484">
				<fmt:formatNumber type="number" value="${orderHeader.totalDetail}" pattern="0.00"/>
			</font></td>
			<td><font color="#848484">
				<fmt:formatNumber type="number" value="${orderHeader.totalExp}" pattern="0.00"/>
			</font></td>
			<td><font color="#848484">
				<fmt:formatNumber type="number" value="${orderHeader.freight}" pattern="0.00"/>
			</font></td>
			<td><font color="#0A2A0A">
				<fmt:formatNumber type="number" value="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight}" pattern="0.00"/>
			</font></td>
			<td>
				<fmt:formatNumber type="number"  value="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight-orderHeader.totalBuyPrice}" pattern="0.00"/>
			</td>
			<td>
					${fns:getDictLabel(orderHeader.invStatus, 'biz_order_invStatus', '未知状态')}
			</td>
			<td>
					${fns:getDictLabel(orderHeader.bizStatus, 'biz_cend_orderType', '未知状态')}
				<c:if test="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight != orderHeader.receiveTotal}">
					<c:if test="${orderHeader.bizStatus!=10 && orderHeader.bizStatus!=40}">
						<font color="#FF0000">(有尾款)</font>
					</c:if>
				</c:if>
			</td>
			<td>
					${orderHeader.platformInfo.name}
			</td>
			<td>
					${orderHeader.createBy.name}
			</td>
			<td>
				<fmt:formatDate value="${orderHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</td>
			<td>
				<fmt:formatDate value="${orderHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</td>
			<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><td>
				<c:if test="${orderHeader.delFlag!=null && orderHeader.delFlag eq '1'}">
						<%--<c:if test="${orderHeader.bizStatus!=10 && orderHeader.bizStatus!=40}">--%>
							<%--<c:if test="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight != orderHeader.receiveTotal}">--%>
								<%--<a href="${ctx}/biz/order/bizOrderHeader/cendform?id=${orderHeader.id}&orderNoEditable=editable">待支付</a>--%>
							<%--</c:if>--%>
						<%--</c:if>--%>
						<a href="${ctx}/biz/order/bizOrderHeader/cendform?id=${orderHeader.id}&orderDetails=details">查看详情</a>
						<a href="${ctx}/biz/order/bizOrderHeader/cendform?id=${orderHeader.id}">修改</a>
						<c:if test="${fns:getUser().isAdmin()}">
							<a href="${ctx}/biz/order/bizOrderHeader/delete?id=${orderHeader.id}&flag=cendDelete" onclick="return confirmx('确认要删除该订单信息吗？', this.href)">删除</a>
						</c:if>
				</c:if >
				<c:if test="${orderHeader.delFlag!=null && orderHeader.delFlag eq '0'}">
					<a href="${ctx}/biz/order/bizOrderHeader/recovery?id=${orderHeader.id}&flag=cendRecover" onclick="return confirmx('确认要恢复该订单信息吗？', this.href)">恢复</a>
				</c:if>
			</td></shiro:hasPermission>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
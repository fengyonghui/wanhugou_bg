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
	<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
		<li class="active"><a href="${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">订单信息列表</a></li>
	</c:if>
	<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
		<li class="active"><a href="${ctx}/biz/order/bizOrderHeader?statu=${statu}">订单信息列表</a></li>
		<%--<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><li><a href="${ctx}/biz/order/bizOrderHeader/form">订单信息添加</a></li></shiro:hasPermission>--%>
	</c:if>
</ul>
<form:form id="searchForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader?statu=${statu}" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<input id="orderNum" name="bizOrderHeader.orderNum" type="hidden" value="${bizOrderHeader.orderNum}"/>
	<form:hidden path="consultantId"/>
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
		<li><label>采购商电话：</label>
			<form:input path="customer.phone" htmlEscape="false" maxlength="30" class="input-medium"/>
		</li>
		<li>
			<label>商品货号：</label>
			<form:input path="itemNo" htmlEscape="false" maxlength="30" class="input-medium"/>
		</li>
		<li><label>采购商名称：</label>
			<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
				<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
								labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
								title="采购商"  url="/sys/office/queryTreeList?type=6"
								cssClass="input-medium required"
								allowClear="true"  dataMsgRequired="必填信息"/>
				<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}">
				<input type="hidden" name="flag" value="${bizOrderHeader.flag}">
			</c:if>
			<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
				<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
								labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
								title="采购商"  url="/sys/office/queryTreeList?type=6"
								cssClass="input-medium required"
								allowClear="true"  dataMsgRequired="必填信息"/>
			</c:if>
		</li>
		<li><label>采购中心：</label>
			<form:input path="centersName" htmlEscape="false" maxlength="100" class="input-medium"/>
		</li>
		<li><label>创建日期：</label>
			<input name="ordrHeaderStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
				   value="<fmt:formatDate value="${bizOrderHeader.ordrHeaderStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			至
			<input name="orderHeaderEedTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
				   value="<fmt:formatDate value="${bizOrderHeader.orderHeaderEedTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
		</li>
        <li><label>客户专员：</label>
            <form:input path="con.name" htmlEscape="false" maxlength="100" class="input-medium"/>
        </li>
        <li><label>更新日期：</label>
            <input name="orderUpdaStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${bizOrderHeader.orderUpdaStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
            至
            <input name="orderUpdaEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${bizOrderHeader.orderUpdaEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
        </li>
		<li><label>测试数据</label>
			<form:checkbox path="includeTestData" htmlEscape="false" maxlength="100" class="input-medium"/>
		</li>

		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
		<li class="btns"><input id="buttonExport" class="btn btn-primary" type="button" value="导出"/></li>
		<c:if test="${bizOrderHeader.flag=='check_pending'}">
			<li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/></li>
		</c:if>

		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
	<tr>
		<td>序号</td>
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
	<c:forEach items="${page.list}" var="orderHeader" varStatus="state">
		<tr>
			<td>${state.index+1}</td>
			<td>
				<c:if test="${bizOrderHeader.flag=='check_pending'}">
					<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">
							${orderHeader.orderNum}</a>
				</c:if>
				<c:if test="${empty bizOrderHeader.flag}">
					<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details&statu=${statu}">
							${orderHeader.orderNum}</a>
				</c:if>
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
				<fmt:formatNumber type="number" value="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight-orderHeader.totalBuyPrice}" pattern="0.00"/>
			</td>
			<td>
					${fns:getDictLabel(orderHeader.invStatus, 'biz_order_invStatus', '未知状态')}
			</td>
			<td>
					${fns:getDictLabel(orderHeader.bizStatus, 'biz_order_status', '未知状态')}
					<a style="display: none">
					<fmt:formatNumber type="number" var="total" value="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight}" pattern="0.00"/>
					</a>
			<c:if test="${orderHeader.bizStatus!=10 && orderHeader.bizStatus!=35 && orderHeader.bizStatus!=40 && orderHeader.bizStatus!=45 && total != orderHeader.receiveTotal}">
					<font color="#FF0000">(有尾款)</font>
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
					<c:choose>
					<c:when test="${bizOrderHeader.flag=='check_pending'}">
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">
							<c:if test="${orderHeader.bizStatus==0 || orderHeader.bizStatus==5 || orderHeader.bizStatus==10}">
								待审核
								<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&clientModify=client_modify&consultantId=${bizOrderHeader.consultantId}">修改</a>
							</c:if>
							<c:if test="${orderHeader.bizStatus==OrderHeaderBizStatusEnum.SUPPLYING.state}">
								审核成功
							</c:if>
							<c:if test="${orderHeader.bizStatus==OrderHeaderBizStatusEnum.UNAPPROVE.state}">
								审核失败
								<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&clientModify=client_modify&consultantId=${bizOrderHeader.consultantId}">修改</a>
							</c:if></a>
						<c:if test="${orderHeader.bizStatus!=0 && orderHeader.bizStatus!=5 && orderHeader.bizStatus!=10 && orderHeader.bizStatus!=15 && orderHeader.bizStatus!=45}">
							${fns:getDictLabel(orderHeader.bizStatus, 'biz_order_status', '未知状态')}
						</c:if>
					</c:when>
					<c:otherwise>
						<%--<c:if test="${orderHeader.bizStatus!=10 && orderHeader.bizStatus!=40}">--%>
							<%--<c:if test="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight != orderHeader.receiveTotal}">--%>
								<%--<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderNoEditable=editable">待支付</a>--%>
							<%--</c:if>--%>
						<%--</c:if>--%>
						<c:if test="${statu == 'unline'}">
							<a href="${ctx}/biz/order/bizOrderHeaderUnline?orderHeader.id=${orderHeader.id}">支付流水</a>
						</c:if>
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details&statu=${statu}">查看详情</a>
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}">修改</a>
						<c:if test="${fns:getUser().isAdmin()}">
							<a href="${ctx}/biz/order/bizOrderHeader/delete?id=${orderHeader.id}" onclick="return confirmx('确认要删除该订单信息吗？', this.href)">删除</a>

						</c:if>
					</c:otherwise>
				</c:choose>
				</c:if >
				<c:if test="${orderHeader.delFlag!=null && orderHeader.delFlag eq '0'}">
					<a href="${ctx}/biz/order/bizOrderHeader/recovery?id=${orderHeader.id}" onclick="return confirmx('确认要恢复该订单信息吗？', this.href)">恢复</a>
				</c:if>
			</td></shiro:hasPermission>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
</body>
</html>
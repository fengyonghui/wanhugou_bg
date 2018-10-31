<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.BizOrderTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderDrawBackStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderPayProportionStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum" %>
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
						$("#searchForm").attr("action","${ctx}/biz/order/bizOrderHeader/orderHeaderExport?statu=${statu}");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/order/bizOrderHeader?statu=${statu}");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});

            // $("#applyCommission").click(function () {
            //     applyCommission();
            // });
		});
        function page(n,s,t){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#includeTestData").val(t);
            $("#searchForm").submit();
            return false;
        }
        function testData(checkbox) {
            $("#includeTestData").val(checkbox.checked);
        }
	</script>
	<script type="text/javascript">
        function checkInfo(obj, val, hid) {
            if (confirm("您确认驳回该订单的退款申请吗？")) {
                $.ajax({
                    type: "get",
                    url: "${ctx}/biz/order/bizOrderHeader/refundReject",
                    data: {checkStatus: obj, id: hid},
                    success: function (data) {
                        if (data) {
                            alert(val + "成功！");
                            window.location.href = "${ctx}/biz/order/bizOrderHeader?statu=refund";
                        }
                    }
                })
            }
        }
	</script>

	<script type="text/javascript">
        function orderCommission() {
            var totalCommissionMoney = 0;
            $("[name=settlement]").each(function(){
                if ($(this).is(':checked')) {
                    var orderId = $(this).val();
                    var commissionMoney = $("#" + orderId).text();
                    totalCommissionMoney = Number(totalCommissionMoney) + Number(commissionMoney);
				}
            });
            $('#totalCommissionMoney').text(totalCommissionMoney);
        }

        function checkOrCancelAll(){
            var chElt=$('#chElt')
            var checkedElt=chElt.is(':checked');
            var allCheck=$("[name=settlement]");
            var mySpan=$('#mySpan');
            var totalCommission = 0;
            if(checkedElt){
                for(var i=0;i<allCheck.length;i++){
                    allCheck[i].checked=true;
                    var orderIdTemp = $(allCheck[i]).val();
                    var commission = $("#" + orderIdTemp).text();
                    totalCommission = Number(totalCommission) + Number(commission);
                }

                console.log(totalCommission)
                $('#totalCommissionMoney').text(totalCommission);
                mySpan.innerHTML="取消全选";
            }else{
                for(var i=0;i<allCheck.length;i++){
                    allCheck[i].checked=false;
                }
                $('#totalCommissionMoney').text(0.00);
                mySpan.innerHTML="全选";
            }
        }
	</script>

	<script type="text/javascript">
		function applyCommission() {
            var orderIds = ""
            var totalDetail = 0;
            var totalCommission = 0;
            var sellerId = 0;
            $("[name=settlement]").each(function(){
                if ($(this).is(':checked')) {
                    var orderIdTemp = $(this).val();
                    orderIds += orderIdTemp + ",";
                    var commission = $("#" + orderIdTemp).text();
                    totalCommission = Number(totalCommission) + Number(commission);

                    var detail = $("#" + "totalDetail_" + orderIdTemp).text();
                    totalDetail = Number(totalDetail) + Number(detail);

                    var sellerIdTemp = $("#" + "sellerId_" + orderIdTemp).val();
                    if (sellerId == 0) {
                        sellerId = sellerIdTemp;
					}

                    if (sellerId != sellerIdTemp) {
                        alert("请选择同一代销商的订单，再计算！")
						return;
                    }
                }
            });

            if(orderIds == "") {
                alert("请选择待结算清单！");
                return
			}

            window.location.href = "${ctx}/biz/order/bizCommission/applyCommissionForm?orderIds=" + orderIds
                + "&totalDetail=" + totalDetail + "&totalCommission=" + totalCommission + "&sellerId=" + sellerId;;

        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<c:choose>
		<c:when test="${not empty bizOrderHeader.skuChickCount && bizOrderHeader.skuChickCount eq 'orderCick_count'}">
			<li class="active"><a href="${ctx}/biz/order/bizOrderHeader/list?partNo=${bizOrderHeader.partNo}&skuChickCount=${bizOrderHeader.skuChickCount}&source=${source}&targetPage=COMMISSION_ORDER">佣金管理列表</a></li>
		</c:when>
		<c:otherwise>
			<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
				<li class="active"><a href="${ctx}/biz/order/bizOrderHeader/list?flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}&source=${source}&targetPage=COMMISSION_ORDER">佣金管理列表</a></li>
			</c:if>
			<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
				<li class="active"><a href="${ctx}/biz/order/bizOrderHeader?statu=${statu}&source=${source}&targetPage=COMMISSION_ORDER">佣金管理列表</a></li>
				<%--<shiro:hasPermission name="biz:order:bizOrderHeader:edit"><li><a href="${ctx}/biz/order/bizOrderHeader/form">订单信息添加</a></li></shiro:hasPermission>--%>
			</c:if>
		</c:otherwise>
	</c:choose>
</ul>
<form:form id="searchForm" modelAttribute="bizOrderHeader" action="${ctx}/biz/order/bizOrderHeader?statu=${statu}&targetPage=COMMISSION_ORDER" method="post" class="breadcrumb form-search">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<input id="previousPage" name="previousPage" type="hidden" value="${bizOrderHeader.previousPage}"/>
	<input id="orderNum" name="bizOrderHeader.orderNum" type="hidden" value="${bizOrderHeader.orderNum}"/>
	<input id="includeTestData" name="includeTestData" type="hidden" value="${page.includeTestData}"/>
	<input type="hidden" name="source" value="${source}"/>
	<input type="hidden" name="str" value="${bizOrderHeader.str}"/>
	<c:if test="${not empty bizOrderHeader.skuChickCount && bizOrderHeader.skuChickCount eq 'orderCick_count'}">
		<input type="hidden" name="skuChickCount" value="${bizOrderHeader.skuChickCount}"/>
		<input type="hidden" name="partNo" value="${bizOrderHeader.partNo}"/>
	</c:if>
	<form:hidden path="consultantId"/>
	<ul class="ul-form">
		<li><label>订单编号：</label>
			<form:input path="orderNum" htmlEscape="false" maxlength="30" class="input-medium"/>
		</li>
		<li><label>零售商名称：</label>
			<form:input path="serllerName" htmlEscape="false" maxlength="30" class="input-medium"/>
		</li>
		<li><label>零售商电话：</label>
			<form:input path="serllerPhone" htmlEscape="false" maxlength="30" class="input-medium"/>
		</li>
		<%--<li><label>订单状态：</label>--%>
			<%--<form:select path="bizStatus" class="input-medium">--%>
				<%--<form:option value="" label="请选择"/>--%>
				<%--<form:options items="${fns:getDictList('biz_order_status')}" itemLabel="label" itemValue="value"--%>
							  <%--htmlEscape="false"/></form:select>--%>
		<%--</li>--%>

		<li><label>结佣状态：</label>
			<form:select path="commissionStatus" class="input-medium">
				<form:option value="" label="请选择"/>
				<%--<form:options items="${fns:getDictList('biz_commission_status')}" itemLabel="label" itemValue="value"--%>
							  <%--htmlEscape="false"/>--%>
				<form:option value="0" label="未支付"/>
				<form:option value="1" label="已支付"/>
			</form:select>
		</li>

		<%--<form:select path="invStatus" class="input-xlarge" disabled="true">--%>
			<%--<form:option value="" label="请选择"/>--%>
			<%--<form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value"--%>
						  <%--htmlEscape="false"/></form:select>--%>

		<%--<li><label>尾款：</label>--%>
			<%--<form:select path="retainage" class="input-medium">--%>
				<%--<form:option value="" label="请选择"/>--%>
				<%--<form:option value="1" label="有尾款"/>--%>
			<%--</form:select>--%>
		<%--</li>--%>

		<c:if test="${source ne 'vendor'}">
			<li><label>经销店电话：</label>
				<form:input path="customer.phone" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
		</c:if>
		<li>
			<label>商品货号：</label>
			<form:input path="itemNo" htmlEscape="false" maxlength="30" class="input-medium"/>
		</li>
		<li><label>经销店名称：</label>
			<c:if test="${bizOrderHeader.flag eq 'check_pending'}">
				<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
								labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
								title="经销店"  url="/sys/office/queryTreeList?type=6"
								cssClass="input-medium required"
								allowClear="true"  dataMsgRequired="必填信息"/>
				<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}">
				<input type="hidden" name="flag" value="${bizOrderHeader.flag}">
			</c:if>
			<c:if test="${empty entity.orderNoEditable && empty bizOrderHeader.flag && empty entity.orderDetails}">
				<sys:treeselect id="office" name="customer.id" value="${bizOrderHeader.customer.id}"  labelName="customer.name"
								labelValue="${bizOrderHeader.customer.name}" notAllowSelectParent="true"
								title="经销店"  url="/sys/office/queryTreeList?type=6"
								cssClass="input-medium required"
								allowClear="true"  dataMsgRequired="必填信息"/>
				<%--<input type="hidden" name="consultantId" value="${bizOrderHeader.consultantId}">--%>
			</c:if>
		</li>
		<li><label>采购中心：</label>
			<form:input path="centersName" htmlEscape="false" maxlength="100" class="input-medium"/>
		</li>
		<li><label>创建日期：</label>
			<input name="orderCreatStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
				   value="<fmt:formatDate value="${bizOrderHeader.orderCreatStartTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
				   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			至
			<input name="orderCreatEndTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
				   value="<fmt:formatDate value="${bizOrderHeader.orderCreatEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
		<%--<li><label>待同意发货:</label>--%>
			<%--<form:select path="mobileAuditStatus" class="input-medium">--%>
				<%--<form:option value="">请选择</form:option>--%>
				<%--<form:option value="0">待审核</form:option>--%>
				<%--<form:option value="1">审核失败</form:option>--%>
				<%--<form:option value="2">其他</form:option>--%>
			<%--</form:select>--%>
		<%--</li>--%>
		<%--<li><label>待发货</label>--%>
			<%--<form:select path="waitShipments" class="input-medium">--%>
				<%--<form:option value="" label="请选择"/>--%>
				<%--<form:option value="1" label="是"/>--%>
			<%--</form:select>--%>
		<%--</li>--%>
		<%--<li><label>待出库</label>--%>
			<%--<form:select path="waitOutput" class="input-medium">--%>
				<%--<form:option value="" label="请选择"/>--%>
				<%--<form:option value="1" label="是"/>--%>
			<%--</form:select>--%>
		<%--</li>--%>
		<c:if test="${statu == 'unline'}">
			<li><label>审核状态:</label>
				<form:select path="examine" class="input-medium">
					<form:option value="0">请选择</form:option>
					<form:option value="1">审核完成</form:option>
					<form:option value="2">未审核完成</form:option>
				</form:select>
			</li>
		</c:if>
		<li><label>测试数据</label>
			<form:checkbox path="page.includeTestData" htmlEscape="false" maxlength="100" class="input-medium" onclick="testData(this)"/>
		</li>

		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
		<li class="btns"><input id="buttonExport" class="btn btn-primary" type="button" value="导出"/></li>
		<c:if test="${not empty bizOrderHeader.skuChickCount && bizOrderHeader.skuChickCount eq 'orderCick_count'}">
			<li class="btns"><input class="btn" type="button" value="返回商品信息管理" onclick="location.href='${ctx}/biz/sku/bizSkuInfo?productInfo.prodType=1'"/></li>
		</c:if>
		<c:if test="${not empty bizOrderHeader.skuChickCount && bizOrderHeader.skuChickCount eq 'prodCick_count'}">
			<li class="btns"><input class="btn" type="button" value="返回产品信息管理" onclick="location.href='${ctx}/biz/product/bizProductInfoV2?prodType=1'"/></li>
		</c:if>
		<c:if test="${bizOrderHeader.flag=='check_pending' && bizOrderHeader.previousPage != 'myPanel'}">
			<li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/></li>
		</c:if>
		<li class="clearfix"></li>
	</ul>
</form:form>
<div class="control-group">
	<div class="controls">
		<div style="float:left;color: red;font-size: medium;margin-right: 50px; margin-top: 20px; margin-bottom: 10px;">
			&nbsp;&nbsp;注：1、每月15日后，不可修改加盟信息；</br>
			&nbsp;&nbsp;2、每月20-25日，结算佣金；</br>
			&nbsp;&nbsp;3、不管是否时通过分享链接进入，代销商购买时，只给购买的代销商返佣；</br>
			&nbsp;&nbsp;4、已收货的订单，如无售后，15天后，自动变为【待结佣】
		</div>
	</div>
</div>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
	<tr>
		<td>结佣</td>
		<td>序号</td>
		<th>订单编号</th>
		<th>零售商名称</th>
		<th>订单类型</th>
		<th>经销店名称</th>
		<th>所属采购中心</th>
		<c:if test="${source ne 'vendor'}">
			<th>经销店电话</th>
		</c:if>
		<th>已收货款</th>
		<th>商品总价</th>
		<th>调整金额</th>
		<th>运费</th>
		<th>万户币抵扣</th>
		<th>应付金额</th>
		<th>服务费</th>
		<th>佣金</th>
		<th>发票状态</th>
		<th>业务状态</th>
		<th>审核状态</th>
		<th>创建人</th>
		<th>创建时间</th>
		<th>更新时间</th>
		<shiro:hasPermission name="biz:order:bizOrderHeader:view"><th>操作</th></shiro:hasPermission>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${page.list}" var="orderHeader" varStatus="state">
		<tr>
			<td>
				<c:if test="${orderHeader.applyCommStatus == 'no'}">
					<input class="orderChk" onclick="orderCommission();" type="checkbox" name="settlement" value="${orderHeader.id}" />
				</c:if>
				<c:if test="${orderHeader.applyCommStatus == 'yes' && orderHeader.bizCommission.bizStatus == '0'}">
					<a href="${ctx}/biz/order/bizCommission/list?orderNum=${orderHeader.orderNum}&str=detail">未结佣</a>
				</c:if>
				<c:if test="${orderHeader.applyCommStatus == 'yes' && orderHeader.bizCommission.bizStatus == '1'}">
					<a href="${ctx}/biz/order/bizCommission/list?orderNum=${orderHeader.orderNum}&str=detail">已结佣</a>
				</c:if>
			</td>
			<td>${state.index+1}</td>
			<td>
				<c:if test="${bizOrderHeader.flag=='check_pending'}">
					<c:if test="${orderHeader.bizStatus >= OrderHeaderBizStatusEnum.SUPPLYING.state && orderHeader.orderType != BizOrderTypeEnum.PHOTO_ORDER.state}">
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details&statu=${statu}">
							${orderHeader.orderNum}</a>
					</c:if>
					<c:if test="${orderHeader.bizStatus >= OrderHeaderBizStatusEnum.SUPPLYING.state && orderHeader.orderType == BizOrderTypeEnum.PHOTO_ORDER.state}">
						<a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${orderHeader.id}&orderDetails=details&statu=${statu}">
								${orderHeader.orderNum}</a>
					</c:if>
					<c:if test="${orderHeader.bizStatus < OrderHeaderBizStatusEnum.SUPPLYING.state && orderHeader.orderType != BizOrderTypeEnum.PHOTO_ORDER.state}">
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">
							${orderHeader.orderNum}</a>
					</c:if>
					<c:if test="${orderHeader.bizStatus < OrderHeaderBizStatusEnum.SUPPLYING.state && orderHeader.orderType == BizOrderTypeEnum.PHOTO_ORDER.state}">
						<a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${orderHeader.id}&flag=${bizOrderHeader.flag}&consultantId=${bizOrderHeader.consultantId}">
								${orderHeader.orderNum}</a>
					</c:if>
				</c:if>
				<c:if test="${empty bizOrderHeader.flag}">
					<c:if test="${orderHeader.orderType == BizOrderTypeEnum.PHOTO_ORDER.state}">
						<a href="${ctx}/biz/order/bizPhotoOrderHeader/form?id=${orderHeader.id}&orderDetails=details&statu=${statu}&source=${source}">${orderHeader.orderNum}</a>
					</c:if>
					<c:if test="${orderHeader.orderType != BizOrderTypeEnum.PHOTO_ORDER.state}">
					<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details&statu=${statu}&source=${source}">
							${orderHeader.orderNum}</a>
					</c:if>
				</c:if>
			</td>
			<td>
					${orderHeader.seller.name}
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
			<c:if test="${source ne 'vendor'}">
				<td>
						${orderHeader.customer.phone}
				</td>
			</c:if>
			<td>
				<fmt:formatNumber type="number" value="${orderHeader.receiveTotal==null?0.00:orderHeader.receiveTotal}" pattern="0.00"/>
			</td>
			<td id="totalDetail_${orderHeader.id}"><font color="#848484">
				<fmt:formatNumber type="number" value="${orderHeader.totalDetail}" pattern="0.00"/>
			</font></td>
			<td><font color="#848484">
				<fmt:formatNumber type="number" value="${orderHeader.totalExp}" pattern="0.00"/>
			</font></td>
			<td><font color="#848484">
				<fmt:formatNumber type="number" value="${orderHeader.freight}" pattern="0.00"/>
			</font></td>
			<td><font color="#848484"><%--==null?0.00:orderHeader.scoreMoney--%>
				<fmt:formatNumber type="number" value="${orderHeader.scoreMoney}" pattern="0.00"/>
			</font></td>
			<td><font color="#0A2A0A">
				<fmt:formatNumber type="number" value="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight+orderHeader.serviceFee-orderHeader.scoreMoney}" pattern="0.00"/>
			</font></td>
			<td>
				${orderHeader.totalExp+orderHeader.serviceFee+orderHeader.freight}
			</td>
			<td class="commissionMoney" id="${orderHeader.id}">
				<fmt:formatNumber type="number" value="${orderHeader.commission}" pattern="0.00"/>
			</td>
			<td>
					${fns:getDictLabel(orderHeader.invStatus, 'biz_order_invStatus', '未知状态')}
			</td>
			<td>
				<c:choose>
					<c:when test="${orderHeader.drawBack != null}">
						<c:if test="${orderHeader.drawBack.drawbackStatus==OrderHeaderDrawBackStatusEnum.REFUND.state}">
							申请退款
						</c:if>
						<c:if test="${orderHeader.drawBack.drawbackStatus==OrderHeaderDrawBackStatusEnum.REFUNDING.state}">
							退款中
						</c:if>
						<c:if test="${orderHeader.drawBack.drawbackStatus==OrderHeaderDrawBackStatusEnum.REFUNDREJECT.state}">
							退款驳回
						</c:if>
						<c:if test="${orderHeader.drawBack.drawbackStatus==OrderHeaderDrawBackStatusEnum.REFUNDED.state}">
							退款完成
						</c:if>
					</c:when>
					<c:otherwise>
						${fns:getDictLabel(orderHeader.bizStatus, 'biz_order_status', '未知状态')}
						<a style="display: none">
							<fmt:formatNumber type="number" var="total" value="${orderHeader.totalDetail+orderHeader.totalExp+orderHeader.freight+orderHeader.serviceFee}" pattern="0.00"/>
							<fmt:formatNumber type="number" var="receive" value="${orderHeader.receiveTotal + orderHeader.scoreMoney}" pattern="0.00"/>
						</a>
						<c:if test="${total > receive && orderHeader.bizStatus!=10 && orderHeader.bizStatus!=35 && orderHeader.bizStatus!=40 && orderHeader.bizStatus!=45 && orderHeader.bizStatus!=60}">
							<font color="#FF0000">(有尾款)</font>
						</c:if>
					</c:otherwise>
				</c:choose>
			</td>

			<td>
                <c:if test="${orderHeader.applyCommStatus == 'no'}">
                    待申请
                </c:if>
                <c:if test="${orderHeader.applyCommStatus == 'yes' && orderHeader.bizCommission.bizStatus == '0'}">
                    <c:if test="${orderHeader.bizCommission.totalCommission == '0.00' && orderHeader.bizCommission.paymentOrderProcess.name != '审批完成'}">
                        待确认支付金额
                    </c:if>
                    <c:if test="${bizCommission.totalCommission != '0.00'}">
                        ${bizCommission.commonProcess.paymentOrderProcess.name}
                    </c:if>
                </c:if>
                <c:if test="${orderHeader.applyCommStatus == 'yes' && orderHeader.bizCommission.bizStatus == '1'}">
                    已结佣
                </c:if>

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
			<shiro:hasPermission name="biz:order:bizOrderHeader:view"><td>
				<input id="sellerId_${orderHeader.id}" type="hidden" value="${orderHeader.sellersId}"/>
				<c:if test="${orderHeader.applyCommStatus == 'no'}">
					<a href="${ctx}/biz/order/bizCommission/applyCommissionForm?orderIds=${orderHeader.id}&totalDetail=${orderHeader.totalDetail}&totalCommission=${orderHeader.commission}&sellerId=${orderHeader.sellersId}">申请结佣</a>
				</c:if>
				<c:if test="${orderHeader.applyCommStatus == 'yes' && orderHeader.bizCommission.bizStatus == '0'}">
					<a href="${ctx}/biz/order/bizCommission/list?orderNum=${orderHeader.orderNum}&str=detail">未结佣</a>
				</c:if>
				<c:if test="${orderHeader.applyCommStatus == 'yes' && orderHeader.bizCommission.bizStatus == '1'}">
					<a href="${ctx}/biz/order/bizCommission/list?orderNum=${orderHeader.orderNum}&str=detail">已结佣</a>
				</c:if>
				<%--<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&orderDetails=details&statu=${statu}&source=${source}&commission=${orderHeader.commission}&commSign=commSign">查看详情</a>--%>
				<a href="${ctx}/biz/order/bizCommission/applyCommissionForm?orderIds=${orderHeader.id}&totalDetail=${orderHeader.totalDetail}&totalCommission=${orderHeader.commission}&sellerId=${orderHeader.sellersId}&option=detail">查看详情</a>

				<shiro:hasPermission name="biz:order:bizOrderHeader:edit">
					<c:if test="${fns:getUser().isAdmin()}">
						<a href="${ctx}/biz/order/bizOrderHeader/delete?id=${orderHeader.id}&statu=${statu}&source=${source}"
						   onclick="return confirmx('确认要删除该订单信息吗？', this.href)">删除</a>
					</c:if>
				</shiro:hasPermission>

				<shiro:hasPermission name="biz:order:bizOrderHeader:edit">
					<c:if test="${orderHeader.delFlag!=null && orderHeader.delFlag eq '0'}">
						<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderHeader.id}&str=detail">详情</a>
						<a href="${ctx}/biz/order/bizOrderHeader/recovery?id=${orderHeader.id}&statu=${statu}" onclick="return confirmx('确认要恢复该订单信息吗？', this.href)">恢复</a>
					</c:if>
				</shiro:hasPermission>

			</td></shiro:hasPermission>
		</tr>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>
<div class="form-actions">
	<input class="orderChk" id="chElt"  type="checkbox" onclick="checkOrCancelAll();"  /><span id="mySpan">全选</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	合计佣金：
	<span id="totalCommissionMoney" style="color: red">00.00</span>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="button"
		   onclick="applyCommission()"
		   class="btn btn-primary"
		   value="结算"/>
</div>
</body>
</html>
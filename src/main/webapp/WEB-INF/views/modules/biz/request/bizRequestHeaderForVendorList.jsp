<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.RoleEnNameEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqFromTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.BizOrderSchedulingEnum" %>
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
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestHeaderForVendor/requestHeaderExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/biz/request/bizRequestHeaderForVendor/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
            $('#myModal').on('hide.bs.modal', function () {
                window.location.href="${ctx}/biz/request/bizRequestHeaderForVendor";

            });

            timeoutID= setInterval(tttt,5000);
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
        function checkInfo(obj,val,hid) {
           if(confirm("您确认取消该采购单吗？")){

               $.ajax({
                   type:"post",
                   url:"${ctx}/biz/request/bizRequestHeaderForVendor/saveInfo",
                   data:{checkStatus:obj,id:hid},
                   success:function (data) {
                       if(data){
                           alert(val+"成功！");
                           window.location.href="${ctx}/biz/request/bizRequestHeaderForVendor";

                       }
                   }
               })

			}

        }
        function pay(reqId){
			$("#myModal").find("#reqId").val(reqId);
            var totalMoney= $("#total_"+reqId).text();
            var revMoney=$("#rev_"+reqId).val();
            $("#toPay").text(parseFloat(totalMoney)-parseFloat(revMoney))


		}
        function genPayQRCode(obj) {
			var payMoney =$("#payMoneyId").val();
			var reqId = $("#reqId").val();
			var payMethod=$("input:radio[name='payMethod']:checked").val();
            var totalMoney= $("#total_"+reqId).text();
            var revMoney=$("#rev_"+reqId).val();

            if(parseFloat(totalMoney)-parseFloat(revMoney)-parseFloat(payMoney)<0){
                alert("应付金额超出范围!");
                return;
			}

			if(payMoney==''||parseFloat(payMoney)==0.0){
			    alert("请输入金额！");
			    return;
			}

			if(payMethod=="" || payMethod==undefined || payMethod==null){
			    alert("请选择支付方式！");
			    return;
			}
            $.ajax({
                type:"post",
                url:"${ctx}/biz/request/bizRequestPay/genPayQRCode",
                data:{payMoney:payMoney,reqId:reqId,payMethod:payMethod},
                success:function (data) {
                    var img="<img src='"+data['imgUrl']+"'/>";
					$("#payNum").val(data['payNum']);
                    $("#img").html(img);

                }
            });

        }
        function tttt() {

            if($("#payNum").val()!=''){
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/request/bizRequestPay/checkCondition",
                    data:{payNum:$("#payNum").val()},
                    success:function (data) {
                        if(data=='ok'){
                            clearTimeout(timeoutID);
                            alert("支付成功！");
                            $('#myModal').modal('hide')

                        }

                    }
                })
            }
        }


	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz/request/bizRequestHeaderForVendor/">备货清单列表</a></li>
		<shiro:hasPermission name="biz:request:bizRequestHeader:edit"><li><a href="${ctx}/biz/request/bizRequestHeaderForVendor/form">备货清单添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestHeaderForVendor/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="payNum" type="hidden" />
		<input id="includeTestData" name="includeTestData" type="hidden" value="${page.includeTestData}"/>
		<ul class="ul-form">
			<li><label>备货单号：</label>
				<form:input path="reqNo" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<%--<li><label>货号：</label>--%>
				<%--<form:input path="itemNo" htmlEscape="false" maxlength="20" class="input-medium"/>--%>
			<%--</li>--%>
			<li><label>供应商：</label>
				<form:input path="name" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>采购中心：</label>
				<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
								labelValue="${entity.fromOffice.name}" allowClear="true"
								title="采购中心"  url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" cssClass="input-medium required" dataMsgRequired="必填信息">
				</sys:treeselect>
			</li>
			<li><label>备货方：</label>
				<form:select path="fromType" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('req_from_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
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
			<li><label>品类名称：</label>
				<form:select id="varietyInfoId" about="choose" path="varietyInfo.id" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${varietyInfoList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>测试数据</label>
				<form:checkbox path="page.includeTestData" htmlEscape="false" maxlength="100" class="input-medium" onclick="testData(this)"/>
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
				<th>备货方</th>
				<th>备货商品数量</th>
				<th>备货商品总价</th>
				<th>已收保证金</th>
				<th>付款比例</th>
				<th>已到货数量</th>
				<th>备注</th>
				<th>业务状态</th>
				<th>审核状态</th>
				<th>下单时间</th>
				<th>品类名称</th>
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
						<c:when test="${requestHeader.bizStatus<ReqHeaderStatusEnum.APPROVE.state}">
							<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=detail">
							</c:when>
							<c:otherwise>
								<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=detail">
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
				<td>${fns:getDictLabel(requestHeader.fromType,'req_from_type' ,'未知' )}</td>
				<td>${requestHeader.reqQtys}</td>
				<td id="total_${requestHeader.id}">${requestHeader.totalMoney}</td>
					<input type="hidden" id="rev_${requestHeader.id}" value="${requestHeader.recvTotal}">
				<td>${requestHeader.recvTotal}</td>
				<td>
					<fmt:formatNumber type="number" value="${requestHeader.recvTotal*100/requestHeader.totalMoney}" pattern="0.00" />%
				</td>
				<td>${requestHeader.recvQtys}</td>
				<td>
					${requestHeader.remark}
				</td>
				<td>
						${fns:getDictLabel(requestHeader.bizStatus, 'biz_req_status', '未知类型')}

				</td>
				<td>
					<c:if test="${requestHeader.fromType == ReqFromTypeEnum.CENTER_TYPE.type}">
						${requestHeader.commonProcess.requestOrderProcess.name}
					</c:if>
					<c:if test="${requestHeader.fromType == ReqFromTypeEnum.VENDOR_TYPE.type}">
						${requestHeader.commonProcess.vendRequestOrderProcess.name}
					</c:if>
				</td>
				<td>
					<fmt:formatDate value="${requestHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${requestHeader.varietyInfo.name}
				</td>
				<td>
					${requestHeader.createBy.name}
				</td>
				<td>
					<fmt:formatDate value="${requestHeader.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="biz:request:bizRequestHeader:view"><td>

					<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=detail">详情</a>

				<shiro:hasPermission name="biz:request:bizRequestHeader:edit">
					<c:choose>
						<c:when test="${fns:getUser().isAdmin()}">
							<c:if test="${requestHeader.delFlag!=null && requestHeader.delFlag!=0}">
								<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}">修改</a>
								<a href="${ctx}/biz/request/bizRequestHeaderForVendor/delete?id=${requestHeader.id}" onclick="return confirmx('确认要删除该备货清单吗？', this.href)">删除</a>
								<c:if test="${requestHeader.bizStatus!=ReqHeaderStatusEnum.CLOSE.state}">
									<a href="#" onclick="checkInfo(${ReqHeaderStatusEnum.CLOSE.state},'取消',${requestHeader.id})">取消</a>
								</c:if>

							</c:if>
							<c:if test="${requestHeader.delFlag!=null && requestHeader.delFlag==0}">
								<a href="${ctx}/biz/request/bizRequestHeaderForVendor/recovery?id=${requestHeader.id}" onclick="return confirmx('确认要恢复该备货清单吗？', this.href)">恢复</a>
							</c:if>
						</c:when>
						<c:when test="${!fns:getUser().isAdmin() && requestHeader.bizStatus<ReqHeaderStatusEnum.APPROVE.state}">
							<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}">修改</a>

							<a href="${ctx}/biz/request/bizRequestHeaderForVendor/delete?id=${requestHeader.id}" onclick="return confirmx('确认要删除该备货清单吗？', this.href)">删除</a>

							<a href="#" onclick="checkInfo(${ReqHeaderStatusEnum.CLOSE.state},'取消',${requestHeader.id})">取消</a>

						</c:when>

						<%--<c:when test="${requestHeader.bizStatus==ReqHeaderStatusEnum.COMPLETE.state}">--%>
							<%--<a href="#" onclick="checkInfo(${ReqHeaderStatusEnum.CLOSE.state},this.value,${requestHeader.id})">关闭</a>--%>
						<%--</c:when>--%>
					</c:choose>

					<c:if test="${requestHeader.bizStatus!=ReqHeaderStatusEnum.CLOSE.state && requestHeader.totalDetail != requestHeader.recvTotal}">
						<a data-toggle="modal" onclick="pay(${requestHeader.id})" data-id="${requestHeader.id}" data-target="#myModal">付款</a>
					</c:if>

				</shiro:hasPermission>
				<!-- 保证金支付申请 -->
				<shiro:hasPermission name="biz:request:bizRequestHeader:createPayOrder">
					<c:if test="${requestHeader.bizPoPaymentOrder.id == null
						&& requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.name == '审批完成'
						&& requestHeader.bizStatus >= ReqHeaderStatusEnum.COMPLETE.state
						&& requestHeader.bizStatus < ReqHeaderStatusEnum.VEND_ALL_PAY.state
						&& (requestHeader.balanceTotal == null ? 0 : requestHeader.balanceTotal) < requestHeader.totalDetail
						}">
						<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=createPay">申请付款</a>
					</c:if>
				</shiro:hasPermission>
				<shiro:hasPermission name="biz:request:bizRequestHeader:audit">
					<c:if test="${(fns:hasRole(roleSet, requestHeader.commonProcess.vendRequestOrderProcess.roleEnNameEnum)) && requestHeader.bizStatus<ReqHeaderStatusEnum.APPROVE.state && requestHeader.commonProcess.requestOrderProcess.name != '驳回'
							&& requestHeader.commonProcess.requestOrderProcess.code != auditStatus
							}">
						<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=audit">审核--RE</a>
					</c:if>
					<%--<!-- 本地备货审核 -->--%>
					<%--<c:if test="${(fn:containsIgnoreCase(fns:getUser().roleList, requestHeader.commonProcess.requestOrderProcess.roleEnNameEnum)) && requestHeader.fromType == ReqFromTypeEnum.CENTER_TYPE.type && requestHeader.bizStatus<ReqHeaderStatusEnum.APPROVE.state && requestHeader.commonProcess.requestOrderProcess.name != '驳回'--%>
						<%--&& requestHeader.commonProcess.requestOrderProcess.code != auditStatus--%>
						<%--}">--%>
						<%--<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=audit">审核</a>--%>
					<%--</c:if>--%>
					<%--<!-- 供应商备货审核 -->--%>
					<%--<c:if test="${(fns:hasRole(roleSet, requestHeader.commonProcess.vendRequestOrderProcess.roleEnNameEnum)) && requestHeader.fromType == ReqFromTypeEnum.VENDOR_TYPE.type && requestHeader.bizStatus < ReqHeaderStatusEnum.EXAMINE.state && requestHeader.commonProcess.vendRequestOrderProcess.name != '驳回'--%>
						<%--&& requestHeader.commonProcess.vendRequestOrderProcess.code != vendAuditStatus--%>
						<%--&& !(fn:containsIgnoreCase(fns:getUser().roleList, RoleEnNameEnum.MARKETINGMANAGER.state))--%>
						<%--}">--%>
						<%--<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=audit">审核</a>--%>
					<%--</c:if>--%>
					<%--<!-- 保证金审核 -->--%>
					<%--<c:if test="${requestHeader.fromType == ReqFromTypeEnum.VENDOR_TYPE.type}">--%>
					<a href="${ctx}/biz/po/bizPoPaymentOrder/list?poId=${requestHeader.bizPoHeader.id}&type=${PoPayMentOrderTypeEnum.PO_TYPE.type}">支付申请列表</a>
					<%--</c:if>--%>
				</shiro:hasPermission>

				<shiro:hasPermission name="biz:po:bizPoHeader:audit">
					<c:if test="${requestHeader.bizStatus >= ReqHeaderStatusEnum.APPROVE.state}">
						<c:if test="${requestHeader.bizPoHeader.commonProcess.id != null
					&& requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.name != '驳回'
					&& requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.name != '审批完成'
					&& requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.code != payStatus
					&& (fns:hasRole(roleSet, requestHeader.bizPoHeader.commonProcess.purchaseOrderProcess.roleEnNameEnum) || fns:getUser().isAdmin())
					}">
							<%--<a href="${ctx}/biz/po/bizPoHeader/form?id=${requestHeader.bizPoHeader.id}&type=audit">审核</a>--%>
							<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=audit">审核--PO</a>
						</c:if>
					</c:if>
				</shiro:hasPermission>
				<%--<shiro:hasPermission name="biz:request:bizRequestHeader:startAudit">--%>
					<%--<c:if test="${(fns:hasRole(roleSet, requestHeader.commonProcess.vendRequestOrderProcess.roleEnNameEnum)) && requestHeader.fromType == ReqFromTypeEnum.VENDOR_TYPE.type && requestHeader.bizStatus < ReqHeaderStatusEnum.EXAMINE.state && requestHeader.commonProcess.vendRequestOrderProcess.name != '驳回'--%>
						<%--&& requestHeader.commonProcess.vendRequestOrderProcess.code != vendAuditStatus--%>
						<%--}">--%>
					<%--<a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${requestHeader.id}&str=startAudit">审核</a>--%>
					<%--</c:if>--%>
				<%--</shiro:hasPermission>--%>
				<%--<c:if test="${requestHeader.commonProcess.purchaseOrderProcess.name == '审批完成'}">--%>
					<%--<c:if test="${requestHeader.fromType == ReqFromTypeEnum.VENDOR_TYPE.type && requestHeader.bizStatus >= ReqHeaderStatusEnum.EXAMINE.state}">--%>
						<%--<c:if test="${requestHeader.totalOrdQty != null && requestHeader.totalOrdQty != 0}">--%>
							<%--<shiro:hasPermission name="biz:request:bizPoHeader:addScheduling">--%>
								<%--<a href="${ctx}/biz/request/bizRequestHeaderForVendor/scheduling?id=${requestHeader.id}">排产</a>--%>
							<%--</shiro:hasPermission>--%>
							<%--<shiro:hasPermission name="biz:request:bizPoHeader:confirmScheduling">--%>
								<%--<a href="${ctx}/biz/request/bizRequestHeaderForVendor/scheduling?id=${requestHeader.id}&forward=confirmScheduling">确认排产</a>--%>
							<%--</shiro:hasPermission>--%>
						<%--</c:if>--%>
					<%--</c:if>--%>
				<%--</c:if>--%>

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
					<div style="color: red; font-size: 16px">应付金额:<span id="toPay"></span></div>
					<div style="margin-top: 14px">
					支付金额：<input type="text" id="payMoneyId" />
					支付方式：<input type="radio" name="payMethod"  value="0"> 支付宝
							<input type="radio" name="payMethod"  value="1"> 微信
					</div>
					二维码：<div style="margin-top: 14px" id="img"></div>
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
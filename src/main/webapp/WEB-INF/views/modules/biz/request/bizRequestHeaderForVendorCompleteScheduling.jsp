<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.RoleEnNameEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>


<html>
<head>
	<title>备货清单管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.help_step_box{background: rgba(255, 255, 255, 0.45);overflow:hidden;border-top:1px solid #FFF;width: 100%}
		.help_step_item{margin-right: 30px;width:200px;border:1px #3daae9 solid;float:left;height:150px;padding:0 25px 0 45px;cursor:pointer;position:relative;font-size:14px;font-weight:bold;}
		.help_step_num{width:19px;height:120px;line-height:100px;position:absolute;text-align:center;top:18px;left:10px;font-size:16px;font-weight:bold;color: #239df5;}
		.help_step_set{background: #FFF;color: #3daae9;}
		.help_step_set .help_step_left{width:8px;height:100px;position:absolute;left:0;top:0;}
		.help_step_set .help_step_right{width:8px;height:100px; position:absolute;right:-8px;top:0;}
	</style>
	<script type="text/javascript">
        var skuInfoId="";
		$(document).ready(function() {
            if ($("#id").val() == null || $("#fromType").val() == 1) {
                $("#fromType1").prop("checked","checked");
            } else {
                $("#fromType2").prop("checked","checked");
            }
            $("#fromOfficeButton").hide();

            var officeId = $("#officeId").val();
            $.ajax({
                type:"post",
                url:"${ctx}/biz/request/bizRequestHeaderForVendor/selectVendInfo?vendorId="+officeId,
                success:function (data) {
                    $("#cardNumberInput").val(data.cardNumber);
                    $("#payeeInput").val(data.payee);
                    $("#bankNameInput").val(data.bankName);
                    if (data.compactImgList != undefined) {
                        data.compactImgList.each(function (index, compact) {
                            $("#compactImgs").append("<a href=\"" + compact.imgServer + compact.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + compact.imgServer + compact.imgPath + "\"></a>");
                        });
                    }
                    if (data.identityCardImgList != undefined) {
                        data.identityCardImgList.each(function (index, identity) {
                            $("#identityCards").append("<a href=\"" + identity.imgServer + identity.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + identity.imgServer + identity.imgPath + "\"></a>");
                        });
                    }
                }
            });

            var detailHeaderFlg = '${detailHeaderFlg}';
            var detailSchedulingFlg = '${detailSchedulingFlg}';
            if (detailHeaderFlg == 'true' || detailSchedulingFlg == 'true') {
                var input = $("#schedulingPlanRadio").find("input:radio");
                input.attr("disabled","disabled");
                input.each(function(){
                    if($(this).val()=='${entity.schedulingType}'){
                        $(this).attr("checked",true);
                    }
                });
            }

            if (detailHeaderFlg == 'true') {
                $("#stockGoods").show();
                $("#schedulingPlan_forHeader").show();
                $("#schedulingPlan_forSku").hide();

                $("#schedulingPlanHeaderFlag").val("true");
            }
            if (detailSchedulingFlg == 'true') {
                $("#stockGoods").hide();
                $("#schedulingPlan_forHeader").hide();
                $("#schedulingPlan_forSku").show();

                $("#schedulingPlanDetailFlag").val("true");
            }
            if (detailHeaderFlg != 'true' && detailSchedulingFlg != 'true') {
                $("#stockGoods").show();
                $("#schedulingPlan_forHeader").show();
                $("#schedulingPlan_forSku").hide();

                $("#completeBtn").hide()
            }

            var reqDetailList = '${reqDetailList.size()}';
            if(reqDetailList == 0) {
                $("#completeBtn").hide();
            } else {
                var id = '${bizRequestHeader.id}'
                checkResult(id);
            }
        });

        function selectRemark() {
            if ($("#remarkInput").val() == "") {
                alert("该厂商没有退换货流程");
            } else {
                alert($("#remarkInput").val());
            }
        }

        function checkResult(id) {
            $.ajax({
                url: '${ctx}/biz/request/bizRequestHeaderForVendor/checkSchedulingNum',
                contentType: 'application/json',
                data: {"id": id},
                type: 'get',
                dataType:'json',
                async: false,
                success: function (result) {
                    var totalOrdQty = result['totalOrdQty'];
                    $("#totalOrdQty").val(totalOrdQty)
                    var totalSchedulingDetailNum = result['totalSchedulingDetailNum'] == null ? 0 : result['totalSchedulingDetailNum'];
                    var totalSchedulingHeaderNum = result['totalSchedulingHeaderNum'] == null ? 0 : result['totalSchedulingHeaderNum'];
					var totalCompleteScheduHeaderNum = result['totalCompleteScheduHeaderNum'] == null ? 0 : result['totalCompleteScheduHeaderNum'];
                    $("#totalCompleteScheduHeaderNum").val(totalCompleteScheduHeaderNum)
                    $("#totalSchedulingNumToDo").val(parseInt(totalSchedulingHeaderNum) - parseInt(totalCompleteScheduHeaderNum))
					if ($("#schedulingPlanHeaderFlag").val() == "true") {
                        if($("#totalSchedulingNumToDo").val() == 0) {
                            $("#completeBtn").hide()
							$("#totalCompleteAlert").show()
                        }
                    }

                    if ($("#schedulingPlanDetailFlag").val() == "true") {
                        var toalSchedulingNumForSkuHtml = $("[name=toalSchedulingNumForSku]");
                        var toalSchedulingNumForSkuNum = 0;
                        for(i=0;i<toalSchedulingNumForSkuHtml.length;i++){
                            var schedulingNumForSkuNum = toalSchedulingNumForSkuHtml[i];
                            var scForSkuNum = $(schedulingNumForSkuNum).attr("value")
                            toalSchedulingNumForSkuNum = parseInt(toalSchedulingNumForSkuNum) + parseInt(scForSkuNum);
                        }
                        if(toalSchedulingNumForSkuNum == 0) {
                            $("#completeBtn").hide()
                            $("#totalCompleteAlert").show()
                        }
                    }

                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function choose(obj) {
            $(obj).attr('checked', true);
            if ($(obj).val() == 0) {
                $("#stockGoods").show();
                $("#schedulingPlan_forHeader").show();
                $("#schedulingPlan_forSku").hide();
            } else {
                $("#stockGoods").hide();
                $("#schedulingPlan_forHeader").hide();
                $("#schedulingPlan_forSku").show();
            }
        }

        function confirmComplete() {
            var orderHtml = $(".orderChk");
            var params = new Array();
            $(".orderChk").each(function () {
                if(this.checked){
                    var completeId = $(this).attr("value")
					params.push(completeId);
                }
            })

            if(params.length == 0) {
                alert("未勾选确认项！")
				return false;
			}

            params.unshift('${bizRequestHeader.reqNo}')
            if(confirm("确定执行该确认排产吗？")) {
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/request/bizRequestHeaderForVendor/confirm',
                    contentType: 'application/json',
                    data:JSON.stringify(params),
                    type: 'post',
                    success: function (result) {
                        if(result == true) {
                            window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor/scheduling?id="+${entity.id} + "&forward=confirmScheduling";
                        }
                    },
                    error: function (error) {
                        console.info(error);
                    }
                });
            }
        }

        function batchConfirmComplete() {


            if(confirm("确定执行该确认排产吗？")) {
                $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '${ctx}/biz/request/bizRequestHeaderForVendor/confirm',
                    contentType: 'application/json',
                    data:{"completeId":completeId},
                    type: 'get',
                    success: function (result) {
                        if(result == true) {
                            window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor/scheduling?id="+${entity.id} + "&forward=confirmScheduling";
                        }
                    },
                    error: function (error) {
                        console.info(error);
                    }
                });
            }
        }

	</script>
	<script type="text/javascript">

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/request/bizRequestHeaderForVendor/">备货清单列表</a></li>
		<li class="active"><a href="${ctx}/biz/request/bizRequestHeaderForVendor/scheduling?id=${bizRequestHeader.id}&forward=confirmScheduling">确认排产</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestHeaderForVendor/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="str" type="hidden"  value="${entity.str}"/>
		<input id="fromType" type="hidden" value="${entity.fromType}"/>
		<input id="vendId" type="hidden" value="${entity.bizVendInfo.office.id}"/>
		<input id="remarkInput" type="hidden" value=""/>
		<c:forEach items="${fns:getUser().roleList}" var="role">
			<c:if test="${role.enname==RoleEnNameEnum.STOCKREADYCOMMISSIONER.state}">
				<c:set var="flag" value="true"/>
			</c:if>
			<c:if test="${role.enname==RoleEnNameEnum.MARKETINGMANAGER.state}">
				<c:set var="flag" value="true"/>
			</c:if>
		</c:forEach>

		<sys:message content="${message}"/>
		<c:if test="${entity.id!=null}">
			<div class="control-group">
				<label class="control-label">备货清单编号：</label>
				<div class="controls">
					<form:input path="reqNo" cssClass="input-xlarge" disabled="true"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">备货方：</label>
			<div class="controls">
				<input id="fromType1" type="radio" name="fromType" value="1" disabled="disabled" checked onclick="addAllStyle()"/>采购中心备货
				<input id="fromType2" type="radio" name="fromType" value="2" disabled="disabled" onclick="deleteVendorStyle()"/>供应商备货
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<sys:treeselect id="fromOffice" name="fromOffice.id" disabled="disabled" value="${entity.fromOffice.id}" labelName="fromOffice.name"
								labelValue="${entity.fromOffice.name}"  notAllowSelectParent="true"
								title="采购中心" isAll="false"  url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" cssClass="input-xlarge required" dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div id="vendor" class="control-group">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<sys:treeselect id="office" name="bizVendInfo.office.id" value="${entity.bizVendInfo.office.id}" labelName="bizVendInfo.office.name"
								labelValue="${entity.bizVendInfo.vendName}" notAllowSelectParent="true"
								title="供应商" url="/sys/office/queryTreeList?type=7" cssClass="input-medium required"
								allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
				<a href="#" id="remark" onclick="selectRemark()">《厂家退换货流程》</a>
			</div>
		</div>
		<div id="cardNumber" class="control-group">
			<label class="control-label">供应商卡号：</label>
			<div class="controls">
				<input id="cardNumberInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
							class="input-xlarge "/>
			</div>
		</div>
		<div id="payee" class="control-group">
			<label class="control-label">供应商收款人：</label>
			<div class="controls">
				<input id="payeeInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
							class="input-xlarge "/>
			</div>
		</div>
		<div id="bankName" class="control-group">
			<label class="control-label">供应商开户行：</label>
			<div class="controls">
				<input id="bankNameInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
							class="input-xlarge "/>
			</div>
		</div>
		<div id="compact" class="control-group" >
			<label class="control-label">供应商合同：</label>
			<div id="compactImgs" class="controls">

			</div>
		</div>
		<div id="identityCard" class="control-group" >
			<label class="control-label">供应商身份证：</label>
			<div id="identityCards" class="controls">

			</div>
		</div>

		<div class="control-group">
			<label class="control-label">排产类型：</label>
			<div class="controls" id="schedulingPlanRadio">
				<form:radiobutton id="deliveryStatus0" path="schedulingType" checked="true" onclick="choose(this)" value="0"/>按订单排产
				<form:radiobutton id="deliveryStatus1" path="schedulingType" onclick="choose(this)"  value="1"/>按商品排产
			</div>
		</div>

		<div class="control-group" id="stockGoods">
		<label class="control-label">备货商品：</label>
			<div class="controls">
			<table id="contentTable" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th>产品图片</th>
					<th>品牌名称</th>
					<th>供应商</th>
					<th>商品名称</th>
					<th>商品编码</th>
					<th>商品货号</th>
					<th>价格</th>
					<th>申报数量</th>
					<th>总金额</th>
					<c:if test="${entity.str=='detail' && entity.bizStatus >= ReqHeaderStatusEnum.UNREVIEWED.state}">
						<th>仓库名称</th>
						<th>库存数量</th>
						<th>销售量</th>
						<c:if test="${not empty roleChanne && roleChanne eq 'channeOk'}">
							<th>商品总库存数量</th>
						</c:if>

					</c:if>
					<c:if test="${entity.str=='detail' && entity.bizStatus>=ReqHeaderStatusEnum.PURCHASING.state}">
						<th>已收货数量</th>
					</c:if>
					<c:if test="${not empty bizRequestHeader.str && bizRequestHeader.str eq 'detail'}">
						<%--该备货单已生成采购单就显示--%>
						<c:if test="${empty bizRequestHeader.poSource}">
							<th>已生成的采购单</th>
							<th>采购数量</th>
						</c:if>
					</c:if>
				</tr>
				</thead>
				<tbody id="prodInfo">
				<c:if test="${reqDetailList!=null}">
					<c:forEach items="${reqDetailList}" var="reqDetail" varStatus="state">
						<tr class="${reqDetail.skuInfo.productInfo.id}" id="${reqDetail.id}">
							<td id="detailId_${state.index+1}" style="display: none">${reqDetail.id}</td>
							<td><img src="${reqDetail.skuInfo.productInfo.imgUrl}" width="100" height="100" /></td>
							<td>${reqDetail.skuInfo.productInfo.brandName}</td>
							<td><a href="${ctx}/sys/office/supplierForm?id=${reqDetail.skuInfo.productInfo.office.id}&gysFlag=onlySelect">
									${reqDetail.skuInfo.productInfo.office.name}</a></td>
							<td>${reqDetail.skuInfo.name}</td>
							<td>${reqDetail.skuInfo.partNo}</td>
							<td>${reqDetail.skuInfo.itemNo}</td>
							<td style="white-space: nowrap">
									${reqDetail.unitPrice}
							</td>
							<td>
								<input  type='hidden' name='reqDetailIds' value='${reqDetail.id}'/>
								<input type='hidden' name='skuInfoIds' value='${reqDetail.skuInfo.id}'/>
								<input  type='hidden' name='lineNos' value='${reqDetail.lineNo}'/>
								<input id="reqQty_${state.index+1}" name='reqQtys' readonly="readonly" value="${reqDetail.reqQty}" class="input-mini" type='text'/>
							</td>

							<td>
									${reqDetail.unitPrice * reqDetail.reqQty}
							</td>

							<c:if test="${entity.str=='detail' && entity.bizStatus >= ReqHeaderStatusEnum.UNREVIEWED.state}">
								<td>${reqDetail.invName}</td>
								<td>${reqDetail.skuInvQty}</td>
								<td>${reqDetail.sellCount}</td>
								<c:if test="${not empty roleChanne && roleChanne eq 'channeOk'}">
									<td>
										<a href="${ctx}/biz/inventory/bizInventorySku?skuInfo.id=${reqDetail.skuInfo.id}&reqSource=request_Inv">
											${reqDetail.invenSkuOrd}</a>
									</td>
								</c:if>
							</c:if>

							<c:if test="${entity.str=='detail' && entity.bizStatus>=ReqHeaderStatusEnum.PURCHASING.state}">
								<td>${reqDetail.recvQty}</td>
							</c:if>

							<c:if test="${not empty bizRequestHeader.str && bizRequestHeader.str eq 'detail'}">
								<%--该备货单已生成采购单就显示--%>
								<c:if test="${reqDetail.bizPoHeader!=null}">
									<td><a href="${ctx}/biz/po/bizPoHeader/form?id=${reqDetail.bizPoHeader.id}">${reqDetail.bizPoHeader.orderNum}</a></td>
									<td>${reqDetail.reqQty}</td>
								</c:if>
							</c:if>
						</tr>
						<c:if test="${state.last}">
							<c:set var="aa" value="${state.index}" scope="page"/>
						</c:if>
					</c:forEach>
					<input id="aaId" value="${aa}" type="hidden"/>
				</c:if>
				</tbody>
			</table>
			</div>
		</div>


		<div class="control-group" id="schedulingPlan_forHeader" >
			<label class="control-label">按订单排产：</label>
			<div class="controls">
				<table  style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
					<tr>
						<td colspan="2">
							<input  type='hidden' id='schedulingPlanHeaderFlag' value=''/>
							<label>总申报数量：</label>
							<input id="totalOrdQty" name='reqQtys' readonly="readonly" class="input-mini" type='text'/>
							&nbsp;
							<label>总待确认量：</label>
							<input id="totalSchedulingNumToDo" name='reqQtys' readonly="readonly" class="input-mini" type='text'/>
							&nbsp;
							<label>总已确认量：</label>
							<input id="totalCompleteScheduHeaderNum" name='reqQtys' readonly="readonly" class="input-mini" type='text'/>
							&nbsp;
						</td>
					</tr>


					<c:if test="${fn:length(bizCompletePalns) > 0}">
						<c:forEach items="${bizCompletePalns}" var="bizCompletePaln" varStatus="stat">
							<tr id="comSchedulingHeader">
								<td>
									<c:if test="${bizCompletePaln.completeStatus == 0}">
										<input class="orderChk" type="checkbox" name="${bizCompletePaln.completeStatus}" value="${bizCompletePaln.id}" />
									</c:if>
									<c:if test="${bizCompletePaln.completeStatus == 1}">
										<span style="color:red; ">已确认</span>
									</c:if>
								</td>
								<td>
									<div>
										<label>完成日期：</label>
										<input type="text" maxlength="20" class="input-medium Wdate" readonly="readonly" value="<fmt:formatDate value="${bizCompletePaln.planDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" /> &nbsp;
										<label>排产数量：</label>
										<input class="input-medium" type="text" readonly="readonly" value="${bizCompletePaln.completeNum}" maxlength="30" />
									</div>
								</td>
							</tr>
						</c:forEach>
					</c:if>
				</table>

			</div>
		</div>



		<div class="control-group" id="schedulingPlan_forSku" style="display: none">
			<label class="control-label">按商品排产：</label>
			<div class="controls">
				<table id="" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>产品图片</th>
						<th>品牌名称</th>
						<th>供应商</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品货号</th>
						<th>价格</th>
						<th>申报数量</th>
						<th>总金额</th>
					</tr>
					</thead>
					<tbody>
					<c:if test="${reqDetailList!=null}">
						<c:forEach items="${reqDetailList}" var="reqDetail" varStatus="state">
							<tr class="${reqDetail.skuInfo.productInfo.id}" >
								<td id="detailId_${state.index+1}" style="display: none">${reqDetail.id}</td>
								<td><img src="${reqDetail.skuInfo.productInfo.imgUrl}" width="100" height="100" /></td>
								<td>${reqDetail.skuInfo.productInfo.brandName}</td>
								<td><a href="${ctx}/sys/office/supplierForm?id=${reqDetail.skuInfo.productInfo.office.id}&gysFlag=onlySelect">
										${reqDetail.skuInfo.productInfo.office.name}</a></td>
								<td>${reqDetail.skuInfo.name}</td>
								<td>${reqDetail.skuInfo.partNo}</td>
								<td>${reqDetail.skuInfo.itemNo}</td>
								<td style="white-space: nowrap">
										${reqDetail.unitPrice}
								</td>
								<td>
									<input  type='hidden' name='reqDetailIds' value='${reqDetail.id}'/>
									<input type='hidden' name='skuInfoIds' value='${reqDetail.skuInfo.id}'/>
									<input  type='hidden' name='lineNos' value='${reqDetail.lineNo}'/>
									<input id="reqQty_${state.index+1}" name='reqQtys' readonly="readonly" value="${reqDetail.reqQty}" class="input-mini" type='text'/>
								</td>
								<td>
										${reqDetail.unitPrice * reqDetail.reqQty}
								</td>
							</tr>
							<c:if test="${state.last}">
								<c:set var="aa" value="${state.index}" scope="page"/>
							</c:if>

							<tr>
								<td colspan="10">
									<table style="width:100%;float:left" class="table table-striped table-bordered table-condensed">
										<tr>
											<td colspan="2">
												<input  type='hidden' id='schedulingPlanDetailFlag' value=''/>
												<label>总申报数量：</label>
												<input id="totalOrdQtyForSku" name='reqQtys' readonly="readonly" value="${reqDetail.reqQty}" class="input-mini" type='text'/>
												&nbsp;
												<label>总待确认量：</label>
												<input name="toalSchedulingNumForSku" name='reqQtys' readonly="readonly" value="${reqDetail.sumCompleteNum - reqDetail.sumCompleteDetailNum}" class="input-mini" type='text'/>
												&nbsp;
												<label>总已确认量：</label>
												<input name="sumCompleteDetailNum" name='reqQtys' readonly="readonly" value="${reqDetail.sumCompleteDetailNum == null ? 0 : reqDetail.sumCompleteDetailNum}" class="input-mini" type='text'/>
												&nbsp;
											</td>
										</tr>


                                        <c:if test="${reqDetail.bizSchedulingPlan != null}">
											<c:forEach items="${reqDetail.bizSchedulingPlan.completePalnList}" var="completePaln">
												<tr name="comSchedulingDetail_${reqDetail.id}">
													<td>
														<c:if test="${completePaln.completeStatus == 0}">
															<input class="orderChk" type="checkbox" name="${completePaln.completeStatus}" value="${completePaln.id}" />
														</c:if>
														<c:if test="${completePaln.completeStatus == 1}">
															<span style="color:red; ">已确认</span>
														</c:if>
													</td>
													<td>
														<div>
															<label>完成日期：</label>
															<input type="text" maxlength="20" readonly="readonly" value="<fmt:formatDate value="${completePaln.planDate}" pattern="yyyy-MM-dd HH:mm:ss"/>" class="input-medium Wdate"  /> &nbsp;
															<label>排产数量：</label>
															<input class="input-medium" readonly="readonly" value="${completePaln.completeNum}" type="text" maxlength="30" />
															&nbsp;
														</div>
													</td>
												</tr>
											</c:forEach>
                                        </c:if>
									</table>
								</td>
							</tr>
						</c:forEach>
						<input id="aaId" value="${aa}" type="hidden"/>
					</c:if>
					</tbody>
				</table>
			</div>
		</div>

		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
			&nbsp;&nbsp;
			<!-- 只有供应商拥有确认排产权限 -->
			<c:if test="${roleFlag == false}">
				<input id="completeBtn" class="btn btn-primary" type="button" value="确定" onclick="confirmComplete();"/>

				<span id="totalCompleteAlert" style="color:red; display: none">已全部确认</span>
			</c:if>
		</div>
	</form:form>
</body>
</html>
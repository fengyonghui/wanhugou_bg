<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
	<script type="text/javascript" src="${ctxStatic}/tablesMergeCell/tablesMergeCell.js"></script>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
            $("#contentTable").tablesMergeCell({
                // automatic: true
                // 是否根据内容来合并
                cols:[0]
                // rows:[0,2]
            });

            $('#select_all').live('click',function(){
                var choose=$("input[title='num']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
			//$("#name").focus();
            var str=$("#str").val();
            if(str=='detail'){
                $("#inputForm").find("input[type!='button']").attr("disabled","disabled") ;
                 $("#btnSubmit").hide();
            }
			$("#inputForm").validate({
				submitHandler: function(form){

                        loading('正在提交，请稍等...');
                        form.submit();

				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
            var deliveryStatus=$("#deliveryStatus").val();

            if(deliveryStatus==0){
                $("input[name='deliveryStatus']").attr("checked",false)
                $("#deliveryStatus0").attr("checked",true);
			}
		});
		function saveMon() {
			$("#inputForm").attr("action","${ctx}/biz/po/bizPoHeader/savePoHeader");
            $("#inputForm").submit();
        }
        function choose(obj) {
		    if($(obj).val()==0){
		        $("#buyCenterId").show();
			}else {
                $("#buyCenterId").hide();
			}

        }
        function savePoOrder(){
		    var us=$("input[name='unitPrices']").val();
		    if(us==''){
		        alert("价钱不能为空！");
		        return;
			}
            if(confirm("确认生成预览采购订单吗？")){
                $("#inputForm").submit();
            }
		}
		function selectOrder(obj) {
		    // alert(obj);
            var flag = false;
            var aflag = false;
            if ($("input[name='"+obj+"']").attr("checked")!=undefined){
                $("input[name='orderDetailIds']").each(function () {
                    var ordNum = $(this).attr("about");
                    if (ordNum==obj){
                        $(this).attr("checked","checked");
                    }
                })
            }else {
                $("input[name='orderDetailIds']").each(function () {
                    var ordNum = $(this).attr("about");
                    if (ordNum==obj){
                        $(this).removeAttr("checked");
                    }
                })
            }
        }
        function selectRequest(obj) {
            // alert(obj);
            var flag = false;
            var aflag = false;
			if ($("input[name='"+obj+"']").attr("checked")!=undefined){
                $("input[name='reqDetailIds']").each(function () {
                    var reqNo = $(this).attr("about");
                    if (reqNo==obj){
                        $(this).attr("checked","checked");
                    }
                })
            }else {
                $("input[name='reqDetailIds']").each(function () {
                    var reqNo = $(this).attr("about");
                    if (reqNo==obj){
                        $(this).removeAttr("checked");
                    }
                })
			}
            // $("input[name='reqDetailIds']").each(function () {
			 //    var reqNo = $(this).attr("about");
				// 	alert($(this).attr("about"));
				// 	if (reqNo==obj){
				// 	    if ($(this).attr("checked")==undefined){
				// 	        flag = true;
            //             }
            //             if ($(this).attr("checked")!=undefined){
				// 	        aflag = true;
				// 		}
				// 	}
            //     });
            // if (flag) {
			 //    alert($(this).text());
			 //    $(this).attr("checked","checked");
            //     $("input[name='reqDetailIds']").each(function () {
            //         var reqNo = $(this).attr("about");
            //         if (reqNo==obj){
            //             $(this).attr("checked","checked");
            //         }
            //     })
            // }
            // if (aflag) {
            //     $("input[name='reqDetailIds']").each(function () {
            //         var reqNo = $(this).attr("about");
            //         if (reqNo==obj){
            //             $(this).removeAttr("checked");
            //         }
            //     })
            // }
        }

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/po/bizPoHeader/">采购订单列表</a></li>
		<li class="active"><a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">采购订单<shiro:hasPermission name="biz:po:bizPoHeader:edit">${not empty bizPoHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:po:bizPoHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizPoHeader" action="${ctx}/biz/po/bizPoHeader/save?prewStatus=prew" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<input type="hidden" name="vendOffice.id" value="${vendorId}">
		<input id="str" type="hidden"  value="${bizPoHeader.str}"/>
		<input id="deliveryStatus" type="hidden"  value="${bizPoHeader.deliveryStatus}"/>
		<c:if test="${bizPoHeader.id!=null}">
			<div class="control-group">
			<label class="control-label">采购单编号：</label>
			<div class="controls">
				<form:input disabled="true" path="orderNum" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">采购单来源：</label>
			<div class="controls">
				<c:forEach items="${bizPoHeader.orderSourceMap}" var="so">
					<%--<c:if test="${so.orderHeader!=null}">--%>
						<%--<input type="text" style="margin-bottom: 10px" disabled="disabled" value="${so.orderHeader.orderNum}" htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
						<%--<br/>--%>
					<%--</c:if>--%>
					<%--<c:if test="${so.requestHeader!=null}">--%>
						<input type="text" style="margin-bottom: 10px" disabled="disabled" value="${so.key}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
						<br/>
					<%--</c:if>--%>

				</c:forEach>

			</div>
		</div>

		<div class="control-group">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<form:input disabled="true" path="vendOffice.name" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">订单总价：</label>
			<div class="controls">
				<input type="text" disabled="disabled" value="${bizPoHeader.totalDetail}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">交易费用：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="totalExp"  htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
			<%--</div>--%>
		<%--</div>--%>

		<%--<div class="control-group">--%>
			<%--<label class="control-label">运费：</label>--%>
			<%--<div class="controls">--%>
				<%--<form:input path="freight"  htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
			<%--</div>--%>
		<%--</div>--%>

		<div class="control-group">
			<label class="control-label">应付金额：</label>
			<div class="controls">
				<input type="text" disabled="disabled" value="${bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">首付款：</label>
			<div class="controls">
				<form:input path="initialPay"  htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>

			<div class="control-group">
				<label class="control-label">最后付款时间：</label>
				<div class="controls">
					<input name="lastPayDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
						   value="<fmt:formatDate value="${bizPoHeader.lastPayDate}"  pattern="yyyy-MM-dd"/>"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>

				</div>
			</div>

			<div class="control-group">
				<label class="control-label">交货地点：</label>
				<div class="controls">
					<form:radiobutton id="deliveryStatus0" path="deliveryStatus"  onclick="choose(this)" value="0"/>采购中心
					<form:radiobutton id="deliveryStatus1" path="deliveryStatus" checked="true" onclick="choose(this)" value="1"/>供应商
				</div>
			</div>
			<div class="control-group" id="buyCenterId"  style="display:none">
				<label class="control-label">采购中心：</label>
				<div class="controls">
					<sys:treeselect id="deliveryOffice" name="deliveryOffice.id" value="${bizPoHeader.deliveryOffice.id}" labelName="deliveryOffice.name"
									labelValue="${bizPoHeader.deliveryOffice.name}"  notAllowSelectParent="true"
									title="采购中心"  url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" cssClass="input-xlarge " dataMsgRequired="必填信息">
					</sys:treeselect>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">备注：</label>
				<div class="controls">
					<form:textarea path="remark"  htmlEscape="false" maxlength="30" class="input-xlarge "/>
				</div>
			</div>

		<%--<div class="control-group">--%>
			<%--<label class="control-label">发票状态：</label>--%>
			<%--<div class="controls">--%>
				<%--<input type="text" disabled="disabled" value="${fns:getDictLabel(bizPoHeader.invStatus, 'biz_order_invStatus', '未知类型')}" htmlEscape="false" maxlength="30" class="input-xlarge "/>--%>
			<%--</div>--%>
		<%--</div>--%>

		<div class="control-group">
			<label class="control-label">订单状态：</label>
			<div class="controls">
				<input type="text" disabled="disabled" value="${fns:getDictLabel(bizPoHeader.bizStatus, 'biz_po_status', '未知类型')}" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>
		</c:if>
		<c:if test="${bizPoHeader.poDetailList!=null}">
		<div class="form-actions">
			<shiro:hasPermission name="biz:po:bizPoHeader:edit">
				<c:if test="${prewStatus == 'prew'}">
					<input id="btnSubmit" type="button" onclick="saveMon()"  class="btn btn-primary"  value="确认生成"/>
				</c:if>
				<c:if test="${prewStatus != 'prew'}">
					<input id="btnSubmit" type="button" onclick="saveMon()"  class="btn btn-primary"  value="保存"/>
				</c:if>
				&nbsp;</shiro:hasPermission>
		</div>
	</c:if>
		<table id="contentTable"  class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<c:if test="${bizPoHeader.id==null}">
					<td><input id="select_all" type="checkbox" />订单号/备货单号</td>
					<th>选择</th>
				</c:if>
				<th>产品图片</th>
				<th>品牌名称</th>
				<th>商品名称</th>
				<%--<th>商品编码</th>--%>
				<th>商品货号</th>
				<c:if test="${bizPoHeader.id!=null}">
					<th>所属单号</th>
				</c:if>
				<%--<th>商品属性</th>--%>
				<c:if test="${bizPoHeader.id==null}">
					<th>申报数量</th>
				</c:if>
				<th>采购数量</th>
				<c:if test="${bizPoHeader.id!=null}">
					<th>已供货数量</th>
				</c:if>
				<th>工厂价</th>


			</tr>
			</thead>
			<tbody id="prodInfo">
					<c:if test="${bizPoHeader.poDetailList!=null}">
						<c:forEach items="${bizPoHeader.poDetailList}" var="poDetail">
							<tr>
								<td><img style="max-width: 120px" src="${poDetail.skuInfo.productInfo.imgUrl}"/></td>
								<td>${poDetail.skuInfo.productInfo.brandName}</td>
								<td>${poDetail.skuInfo.name}</td>
								<%--<td>${poDetail.skuInfo.partNo}</td>--%>
								<td>${poDetail.skuInfo.itemNo}</td>
								<c:if test="${bizPoHeader.id!=null}">
									<td>
										<c:forEach items="${bizPoHeader.orderNumMap[poDetail.skuInfo.id]}" var="orderNumStr" varStatus="orderStatus">
											<c:if test="${orderNumStr.soType==1}">
												<a href="${ctx}/biz/order/bizOrderHeader/form?id=${orderNumStr.orderHeader.id}&orderDetails=details">
											</c:if>
											<c:if test="${orderNumStr.soType==2}">
												<a href="${ctx}/biz/request/bizRequestHeader/form?id=${orderNumStr.requestHeader.id}&str=detail">
											</c:if>
												${orderNumStr.orderNumStr}
												</a>
										</c:forEach>

									</td>
								</c:if>
								<%--<td>${poDetail.skuInfo.skuPropertyInfos}</td>--%>
								<td>${poDetail.ordQty}</td>
								<td>${poDetail.sendQty}</td>
								<td>${poDetail.unitPrice}</td>
							</tr>
						</c:forEach>
					</c:if>
					<c:if test="${bizPoHeader.poDetailList==null}">
						<c:if test="${not empty reqDetailMap}">
							<c:forEach items="${reqDetailMap}" var="map">
								<c:forEach items="${map.value}" var="reqDetail">
								<tr>
									<%--<c:set value="${fn:split(map.key, ',')}" var="detail"></c:set>--%>
									<td><input title="num" name="${reqDetail.requestHeader.reqNo}" type="checkbox" onclick="selectRequest('${reqDetail.requestHeader.reqNo}')"/>${reqDetail.requestHeader.reqNo}</td>
									<td name="reqs"><input title="num" name="reqDetailIds" about="${reqDetail.requestHeader.reqNo}" type="checkbox" value="${reqDetail.id}"/></td>
									<td><img style="max-width: 120px" src="${reqDetail.skuInfo.productInfo.imgUrl}"/></td>
									<td>${reqDetail.skuInfo.productInfo.brandName}</td>
									<td>${reqDetail.skuInfo.name}</td>
									<%--<td>${reqDetail.skuInfo.partNo}</td>--%>
									<td>${reqDetail.skuInfo.itemNo}</td>
									<%--<td>${reqDetail.skuInfo.skuPropertyInfos}</td>--%>
									<td>${reqDetail.reqQty-reqDetail.recvQty}
										<%--<input type='hidden' name='reqDetailIds' value='${map.value.reqDetailIds}'/>--%>
										<%--<input type='hidden' name='skuInfoIds' value='${map.key.id}'/>--%>
										<%--<input type='hidden' name='orderDetailIds' value='${map.value.orderDetailIds}'/>--%>
									</td>
									<td><input  name="ordQtys" readonly="readonly"  value="${reqDetail.reqQty-reqDetail.recvQty}" class="input-mini" type='text'/></td>
									<td>
									<input readonly="readonly" type="text" name="unitPrices" value="${reqDetail.skuInfo.buyPrice}" class="input-mini">
									</td>

								</tr>
								</c:forEach>
							</c:forEach>
						</c:if>
						<c:if test="${not empty orderDetailMap}">
							<c:forEach items="${orderDetailMap}" var="map">
								<c:forEach items="${map.value}" var="orderDetail">
								<tr>
									<td><input title="num" name="${orderDetail.orderHeader.orderNum}" type="checkbox" onclick="selectOrder('${orderDetail.orderHeader.orderNum}')"/>${orderDetail.orderHeader.orderNum}</td>
									<td name="ords"><input title="num" name="orderDetailIds" about="${orderDetail.orderHeader.orderNum}" type="checkbox" value="${orderDetail.id}" /></td>
									<td><img style="max-width: 120px" src="${orderDetail.skuInfo.productInfo.imgUrl}"/></td>
									<td>${orderDetail.skuInfo.productInfo.brandName}</td>
									<td>${orderDetail.skuInfo.name}</td>
									<%--<td>${orderDetail.skuInfo.partNo}</td>--%>
									<td>${orderDetail.skuInfo.itemNo}</td>
									<%--<td>${orderDetail.skuInfo.skuPropertyInfos}</td>--%>
									<td>${orderDetail.ordQty-orderDetail.sentQty}
										<%--<input type='hidden' name='reqDetailIds' value='${map.value.reqDetailIds}'/>--%>
										<%--<input type='hidden' name='skuInfoIds' value='${map.key.id}'/>--%>
										<%--<input type='hidden' name='orderDetailIds' value='${map.value.orderDetailIds}'/>--%>
									</td>
									<td><input  name="ordQtys" readonly="readonly"  value="${orderDetail.ordQty-orderDetail.sentQty}" class="input-mini" type='text'/></td>
									<td>
										<input readonly="readonly" type="text" name="unitPrices" value="${orderDetail.skuInfo.buyPrice}" class="input-mini">
									</td>

								</tr>
								</c:forEach>
							</c:forEach>
						</c:if>
					</c:if>
			</tbody>
		</table>


		<div class="form-actions">
			<shiro:hasPermission name="biz:po:bizPoHeader:edit">
				<c:if test="${bizPoHeader.poDetailList==null}">
					<input id="btnSubmit" type="button" onclick="savePoOrder()"  class="btn btn-primary"  value="采购单预览"/>
				</c:if>
				&nbsp;</shiro:hasPermission>
			<c:if test="${not empty bizPoHeader.str && bizPoHeader.str eq 'detail'}">
				<input onclick="window.print();" type="button" class="btn btn-primary" value="打印采购订单" style="background:#F78181;"/>
				&nbsp;&nbsp;&nbsp;
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

</body>
</html>
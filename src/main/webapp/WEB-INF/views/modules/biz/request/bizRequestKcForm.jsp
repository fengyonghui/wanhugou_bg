<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单收货</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(document).ready(function() {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    if(window.confirm('你确定要收货吗？')){
                        // alert("确定");
                        $Mask.AddLogo("正在加载");
                        form.submit();
                        return true;
                        loading('正在提交，请稍等...');

                    }else{
                        //alert("取消");
                        return false;
                    }
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
        });
        $("#btnSubmit").click(function () {
			var reqQty = $("#reqQty").val();
			var sendNum = $("#sendNum").val();
			if (sendNum > reqQty){
			    alert("供货数太大，已超过申报数，请重新调整供货数量！");
				return false;
			}
			$(".reqDetailList").find("td").find("input[title='sendNum']").each(function () {
				console.info($(this).val())
            });
        });

        function checkout(obj) {
            var reqQty = $("#reqQty"+obj).val();	//申报数量
            var receiveNum = $("#receiveNum"+obj).val();		//收货数量
            var recvQty = $("#recvQty"+obj).val();		//已收货数量
            // var sendQty = $("#sendQty"+obj).val();		//已供货数量
            var sum = parseInt(receiveNum) + parseInt(recvQty);
            if (sum > reqQty){
                alert("收货数太大，已超过申报数量，请重新调整收货数量！");
                $("#receiveNum"+obj).val(0);
                return false;
            }
        }
        function checkout2(obj) {
            var ordQty = $("#ordQty"+obj).val();
            var sendNum = $("#sendNum"+obj).val();
            var sentQty = $("#sentQty"+obj).val();
            var sum = parseInt(sendNum) + parseInt(sentQty);
            if (sum > ordQty){
                alert("供货数太大，已超过申报数，请重新调整供货数量！");
                $("#sendNum"+obj).val(0);
                return false;
            }
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/biz/request/bizRequestAll?source=${source}">备货清单列表</a></li>
	<li class="active"><a href="${ctx}/biz/request/bizRequestAll/form?id=${bizRequestHeader.id}&source=${source}">备货清单<shiro:hasPermission name="biz:request:bizRequestHeader:edit">${not empty bizRequestHeader.id?'收货':''}</shiro:hasPermission><shiro:lacksPermission name="biz:request:bizRequestHeader:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>
<%--@elvariable id="bizSendGoodsRecord" type="com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord"--%>
<form:form id="inputForm" modelAttribute="bizCollectGoodsRecord" action="${ctx}/biz/inventory/bizCollectGoodsRecord/save" method="post" class="form-horizontal">
	<%--<form:hidden path="id"/>--%>
	<sys:message content="${message}"/>
	<input name="bizRequestHeader.id" value="${bizRequestHeader==null?0:bizRequestHeader.id}" type="hidden"/>
	<input name="bizOrderHeader.id" value="${bizOrderHeader==null?0:bizOrderHeader.id}" type="hidden"/>
	<input type="hidden" name="bizStatu" value="${bizStatu}"/>
	<div class="control-group">
		<label class="control-label">入库单：</label>
		<div class="controls">
			<input readonly="readonly" name="collectNo" type="text" class="input-xlarge" value="${collectNo}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">备货清单号：</label>
		<div class="controls">
			<input readonly="readonly" type="text" class="input-xlarge" value="${bizRequestHeader.reqNo}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">备货清类型：</label>
		<div class="controls">
			<input readonly="readonly" type="text" class="input-xlarge" value="${fns:getDictLabel(bizRequestHeader.headerType,'req_header_type','未知')}"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">发货单号：</label>
		<div class="controls">
			<c:forEach items="${deliverNoList}" var="deliverNo">
				<input readonly="readonly" type="text" class="input-xlarge" value="${deliverNo}"/>
			</c:forEach>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">采购中心：</label>
		<div class="controls">
			<input readonly="readonly" type="text" class="input-xlarge" value="${bizRequestHeader==null?bizOrderHeader.customer.name:bizRequestHeader.fromOffice.name}"/>
			<input type="hidden" name="customer.id" value="${bizRequestHeader==null?bizOrderHeader.customer.id:bizRequestHeader.fromOffice.id}">
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">期望收货时间：</label>
		<div class="controls">
			<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate required"
				   value="<fmt:formatDate value="${bizRequestHeader==null?'':bizRequestHeader.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
			/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">备货商品：</label>
		<div class="controls">
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th>产品图片</th>
					<th>产品分类</th>
					<th>商品名称</th>
					<th>商品货号</th>
					<th>颜色</th>
					<th>尺寸</th>
					<th>供应商</th>
					<th>品牌</th>
					<th>品类主管</th>
					<th>申报数量</th>
					<th>本次发货数量</th>
					<th>累计发货数量</th>
					<th>已入库数量</th>
					<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
						<th>入库数量</th>
					</shiro:hasPermission>
					<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
						<th>收货仓库</th>
					</shiro:hasPermission>

				</tr>
				</thead>
				<tbody id="prodInfo">
				<c:if test="${reqDetailList!=null && reqDetailList.size()>0}">
					<c:forEach items="${reqDetailList}" var="reqDetail" varStatus="reqStatus">
						<tr id="${reqDetail.id}" class="reqDetailList">
							<td><img style="max-width: 120px" src="${reqDetail.skuInfo.productInfo.imgUrl}"/></td>
							<td>${reqDetail.skuInfo.productInfo.bizVarietyInfo.name}</td>
							<td>${reqDetail.skuInfo.name}</td>
							<td>${reqDetail.skuInfo.itemNo}</td>
							<td>${reqDetail.skuInfo.color}</td>
							<td>${reqDetail.skuInfo.size}</td>

							<td>
									${reqDetail.skuInfo.productInfo.office.name}
									<%--<input name="bizSendGoodsRecord.vend.id" value="${reqDetail.skuInfo.productInfo.office.id}" type="hidden"/>--%>
							</td>
							<td>${reqDetail.skuInfo.productInfo.brandName}</td>
							<td>${reqDetail.varietyUser.name}</td>
							<td>
								<input type='hidden' name='bizCollectGoodsRecordList[${reqStatus.index}].skuInfo.id' value='${reqDetail.skuInfo.id}'/>
								<input type='hidden' name='bizCollectGoodsRecordList[${reqStatus.index}].skuInfo.name' value='${reqDetail.skuInfo.name}'/>
								<input id="reqQty${reqStatus.index}" name='bizCollectGoodsRecordList[${reqStatus.index}].bizRequestDetail.reqQty' readonly="readonly" value="${reqDetail.reqQty}" type='text'/>
								<input name="bizCollectGoodsRecordList[${reqStatus.index}].bizRequestDetail.id" value="${reqDetail.id}" type="hidden"/>
								<input name="bizCollectGoodsRecordList[${reqStatus.index}].bizRequestDetail.requestHeader.id" value="${reqDetail.requestHeader.id}" type="hidden"/>
								<c:if test="${reqDetail.requestHeader.reqNo != null}">
									<input name="bizCollectGoodsRecordList[${reqStatus.index}].orderNum" value="${reqDetail.requestHeader.reqNo}" type="hidden"/>
								</c:if>
							</td>
							<td>
								<input readonly="readonly" value="${reqDetail.sendQty - reqDetail.recvQty}" type='text'/>
							</td>
							<td>
							<input readonly="readonly" value="${reqDetail.sendQty}" type='text'/>
							</td>
							<td>
								<input id="recvQty${reqStatus.index}" name='bizCollectGoodsRecordList[${reqStatus.index}].bizRequestDetail.recvQty' readonly="readonly" value="${reqDetail.recvQty}" type='text'/>
							</td>

							<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
								<td>
									<input id="receiveNum${reqStatus.index}" name="bizCollectGoodsRecordList[${reqStatus.index}].receiveNum" <c:if test="${reqDetail.recvQty==reqDetail.reqQty}">readonly="readonly"</c:if> value="0" type="number" min="0" class="input-medium requried" onblur="checkout(${reqStatus.index})"/>
								</td>
							</shiro:hasPermission>
							<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
								<td>
									<select name="bizCollectGoodsRecordList[${reqStatus.index}].invInfo.id" class="input-medium required">
										<c:forEach items="${invInfoList}" var="invInfo">
											<option value="${invInfo.id}"/>${invInfo.name}
										</c:forEach>
									</select>
										<%--<input id="invInfo.id${reqStatus.index}" name="bizCollectGoodsRecordList[${reqStatus.index}].invInfo.id" <c:if test="${reqDetail.recvQty==reqDetail.reqQty}">readonly="readonly"</c:if> value="" type="text"/>--%>
								</td>
							</shiro:hasPermission>
						</tr>
					</c:forEach>
				</c:if>
				<!-------------------------------------------------------------------------------->
					<c:if test="${ordDetailList!=null && ordDetailList.size()>0}">
						<c:forEach items="${ordDetailList}" var="ordDetail" varStatus="ordStatus">
							<tr id="${ordDetail.id}" class="ordDetailList">
								<td><img style="max-width: 120px" src="${ordDetail.skuInfo.productInfo.imgUrl}"/></td>
								<td>${ordDetail.skuInfo.productInfo.name}</td>
								<td>
									<c:forEach items="${ordDetail.skuInfo.productInfo.categoryInfoList}" var="cate" varStatus="cateIndex" >
										${cate.name}
										<c:if test="${!cateIndex.last}">
											/
										</c:if>

									</c:forEach>
								</td>
								<td>${ordDetail.skuInfo.productInfo.prodCode}</td>
								<td>${ordDetail.skuInfo.productInfo.brandName}</td>
								<td>
										${ordDetail.skuInfo.productInfo.office.name}
										<%--<input name="bizSendGoodsRecord.vend.id" value="${reqDetail.skuInfo.productInfo.office.id}" type="hidden"/>--%>
								</td>
								<td>${ordDetail.skuInfo.name}</td>
								<td>${ordDetail.skuInfo.partNo}</td>
								<td>
									<input type='hidden' name='bizCollectGoodsRecordList[${ordStatus.index}].skuInfo.id' value='${ordDetail.skuInfo.id}'/>
									<input type='hidden' name='bizCollectGoodsRecordList[${ordStatus.index}].skuInfo.name' value='${ordDetail.skuInfo.name}'/>
									<input id="ordQty${ordStatus.index}" name='bizCollectGoodsRecordList[${ordStatus.index}].bizOrderDetail.ordQty' readonly="readonly" value="${ordDetail.ordQty}" type='text'/>
									<input name="bizCollectGoodsRecordList[${ordStatus.index}].bizOrderDetail.id" value="${ordDetail.id}" type="hidden"/>
                                    <input name="bizCollectGoodsRecordList[${ordStatus.index}].bizOrderDetail.orderHeader.id" value="${ordDetail.orderHeader.id}" type="hidden"/>
									<c:if test="${ordDetail.orderHeader.orderNum != null}">
										<input name="bizCollectGoodsRecordList[${ordStatus.index}].orderNum" value="${ordDetail.orderHeader.orderNum}" type="hidden"/>
									</c:if>

								</td>
								<td>
									<input id="sentQty${ordDetail.sentQty}" name='bizCollectGoodsRecordList[${ordStatus.index}].bizOrderDetail.sentQty' readonly="readonly" value="${ordDetail.sentQty}" type='text'/>
								</td>

								<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">
									<td>
										<input title="sendNum${ordStatus.index}" name="bizCollectGoodsRecordList[${ordStatus.index}].sendNum" <c:if test="${ordDetail.ordQty==ordDetail.sentQty}">readonly="readonly"</c:if> value="0" type="number" min="0" class="input-medium requried" onblur="checkout2(${ordStatus.index})"/>
									</td>
								</shiro:hasPermission>
							</tr>
						</c:forEach>
					</c:if>
				<!-------------------------------------------------------------------------------->
				</tbody>
			</table>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">备注：</label>
		<div class="controls">
			<textarea  class="input-xlarge " readonly="readonly">${bizRequestHeader.remark}</textarea>
		</div>
	</div>

	<div class="form-actions">
		<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="确认收货"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
	</div>

</form:form>

</body>
</html>
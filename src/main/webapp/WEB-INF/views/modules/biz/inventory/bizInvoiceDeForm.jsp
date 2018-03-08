<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发货单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    var tt="";
                    $('input:checkbox:checked').each(function(i) {
                        var t= $(this).val();
                        var detail="";
                        var num ="";
                        var sObj= $("#prodInfo").find("input[title='sent_"+t+"']");
                        $("#prodInfo").find("input[title='details_"+t+"']").each(function (i) {
                            detail+=$(this).val()+"-"+sObj[i].value+"*";

                        });
                        tt+=t+"#"+detail+",";

                    });
                    tt=tt.substring(0,tt.length-1);
                    $("#prodInfo").append("<input name='orderHeaders' type='hidden' value='"+tt+"'>")

                    if(window.confirm('你确定要发货吗？')){
                        // alert("确定");
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

            $("#searchData").click(function () {
                var orderNum=$("#orderNum").val();
                $("#orderNumCopy").val(orderNum);
                var skuItemNo=$("#skuItemNo").val();
                $("#skuItemNoCopy").val(skuItemNo);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/order/bizOrderHeader/findByOrder",
                    data:$('#searchForm').serialize(),
                    success:function (data) {
                        if ($("#id").val() == '') {
                            $("#prodInfo2").empty();
                        }
                        var tr_tds="";
                        $.each(data, function (index,orderHeader) {
							if(orderHeader.bizStatus==17){
                                bizName="采购中"
							}else if(orderHeader.bizStatus==18){
                                bizName="采购完成"
							}else if(orderHeader.bizStatus==19){
                                bizName="供应商供货"
							}else if(orderHeader.bizStatus==20){
                                bizName="已发货"
							}

							var flag= true;
							var deId = "";
							var  num = "";
                            $.each(orderHeader.orderDetailList,function (index,detail) {

                                tr_tds+="<tr class='tr_"+orderHeader.id+"'>";

                                if(flag){
                                    tr_tds+="<td rowspan='"+orderHeader.orderDetailList.length+"'><input type='checkbox' value='"+orderHeader.id+"' /></td>";

                                    tr_tds+= "<td rowspan='"+orderHeader.orderDetailList.length+"'>"+orderHeader.orderNum+"</td><td rowspan='"+orderHeader.orderDetailList.length+"'>"+orderHeader.customer.name+"</td><td rowspan='"+orderHeader.orderDetailList.length+"'>"+bizName+"</td>" ;
                                }
                                 tr_tds+="<input title='details_"+orderHeader.id+"' name='' type='hidden' value='"+detail.id+"'>";
                                tr_tds+= "<td>"+detail.skuInfo.name+"</td><td>"+detail.skuInfo.partNo+"</td><td>"+detail.skuInfo.skuPropertyInfos+"</td>" ;
                                tr_tds+= "<td>"+detail.ordQty+"</td><td>"+detail.sentQty+"</td>";
                                tr_tds+="<td><input  type='text' title='sent_"+orderHeader.id+"' name='' value='0'></td>";
                                tr_tds+="</tr>";
                                if(orderHeader.orderDetailList.length>1){
                                    flag=false;
                                }
                            });

                        });
                        $("#prodInfo2").append(tr_tds);
                    }
            });
		});
            <%--点击确定时获取订单详情--%>
            $("#ensureData").click(function () {
                    $('input:checkbox:checked').each(function(i) {
                       var t= $(this).val();
                       var ttp= $(this).parent().parent().parent();
                       var trt= ttp.find($(".tr_"+t))
                        $("#prodInfo").append(trt);
                    });


			});

            });


	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/inventory/bizInvoice?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单<shiro:hasPermission name="biz:inventory:bizInvoice:edit">${not empty bizInvoice.id?'详情':'详情'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInvoice:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/save?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		

		<div class="control-group">
			<label class="control-label">物流商：</label>
			<div class="controls">
				<form:input path="logistics.name" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流信息图：</label>
			<div class="controls">
				<img src="${imgUrl}"style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/>
			</div>
		</div>
		<%--<div class="control-group">
			<label class="control-label">货值：</label>
			<div class="controls">
				<input id="valuePrice" name="valuePrice"  htmlEscape="false" value="" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>--%>
		<div class="control-group">
			<label class="control-label">操作费：</label>
			<div class="controls">
				<form:input path="operation" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">运费：</label>
			<div class="controls">
				<form:input path="freight" htmlEscape="false" class="input-xlarge required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承运人：</label>
			<div class="controls">
				<form:input path="carrier" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流结算方式：</label>
			<div class="controls">
				<form:select id="settlementStatus" path="settlementStatus" onmouseout="" class="input-xlarge">
					<c:forEach items="${fns:getDictList('biz_settlement_status')}" var="settlementStatus">
						<option value="${settlementStatus.value}">${settlementStatus.label}</option>
					</c:forEach>
				</form:select>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">已发货详情：</label>
			<div class="controls">
				<table id="contentTable2"  class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>订单编号</th>
						<th>采购商名称</th>
						<th>业务状态</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品属性</th>
						<th>采购数量</th>
						<th>已发货数量</th>
					</tr>
					</thead>
					<tbody id="prodInfo">
					<c:if test="${orderHeaderList!=null && orderHeaderList.size()>0}">
						<c:forEach items="${orderHeaderList}" var="orderHeader">
							<c:forEach items="${orderHeader.orderDetailList}" var="orderDetail" varStatus="index">
								<td rowspan="${fn:length(orderHeader.orderDetailList)}">${orderHeader.orderNum}</td>
								<td rowspan="${fn:length(orderHeader.orderDetailList)}">${orderHeader.customer.name}</td>
								<td rowspan="${fn:length(orderHeader.orderDetailList)}">${fns:getDictLabel(orderHeader.bizOrderStatus,"biz_order_status",'' )}</td>
								<td>${orderDetail.skuName}</td>
								<td>${orderDetail.parNo}</td>
								<td>${orderDetail.quality},${orderDetail.color},${orderDetail.standard}</td>
								<td>${orderDetail.ordQty}</td>
								<td>${orderDetail.sentQty}</td>
							</c:forEach>
						</c:forEach>
					</c:if>
					</tbody>
				</table>
			</div>
		</div>
		<div class="form-actions">
			<%--<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>--%>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<%--<form:form id="searchForm" modelAttribute="bizOrderHeader">
		<form:hidden id="orderNumCopy" path="orderNum"/>
		<form:hidden id="skuItemNoCopy" path="itemNo"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>

	</form:form>--%>
</body>
</html>
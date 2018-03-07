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
		<li><a href="${ctx}/biz/inventory/bizInvoice/">发货单列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInvoice/form?id=${bizInvoice.id}">发货单<shiro:hasPermission name="biz:inventory:bizInvoice:edit">${not empty bizInvoice.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInvoice:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		

		<div class="control-group">
			<label class="control-label">物流商：</label>
			<div class="controls">
				<select id="bizLogistics" name="logistics.id" onmouseout="" class="input-medium">
					<c:forEach items="${logisticsList}" var="bizLogistics">
						<option value="${bizLogistics.id}"/>${bizLogistics.name}
					</c:forEach>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流信息图：</label>
			<div class="controls">
				<input type="hidden" id="imgUrl" name="imgUrl" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="imgUrl" type="images" uploadPath="/logistics/info" selectMultiple="false" maxWidth="100"
							  maxHeight="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货值：</label>
			<div class="controls">
				<input id="valuePrice" name="valuePrice"  htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
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
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">承运人：</label>
			<div class="controls">
				<form:input path="carrier" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流结算方式：</label>
			<div class="controls">
				<select id="settlementStatus" name="settlementStatus" onmouseout="" class="input-xlarge">
					<c:forEach items="${fns:getDictList('biz_settlement_status')}" var="settlementStatus">
						<option value="${settlementStatus.value}">${settlementStatus.label}</option>
						<%--<option <c:if test="${settlementStatus eq '现结'}"><c:out value="1"/></c:if><c:if test="${settlementStatus eq '账期'}"><c:out value="2"/></c:if> onclick="chenge(settlementStatus)">${settlementStatus}</option>--%>
					</c:forEach>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>


		<div class="control-group">
			<label class="control-label">选择订单：</label>
			<div class="controls">
				<ul class="inline ul-form">
					<li><label>订单编号：</label>
						<input id="orderNum" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
					</li>
					<li><label>商品货号：</label>
						<input id="skuItemNo"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
					</li>
					<li><label>商品编码：</label>
						<input id="skuCode"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
					</li>
					<li class="btns"><input id="searchData" class="btn btn-primary" type="button"  value="查询"/></li>
					<li class="clearfix"></li>
				</ul>

			</div>
		</div>

		<div class="control-group">
			<label class="control-label">待发货订单：</label>
			<div class="controls">
				<table id="contentTable2"  class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th><input id="select_all" type="checkbox" /></th>
						<th>订单编号</th>
						<th>采购商名称</th>
						<th>业务状态</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品属性</th>
						<th>采购数量</th>
						<th>已发货数量</th>
						<th>发货数量</th>
					</tr>
					</thead>
					<tbody id="prodInfo2">

					</tbody>
				</table>
				<input id="ensureData" class="btn btn-primary" type="button"  value="确定"/>
			</div>

			<div class="controls">
				<table id="contentTable"  class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th></th>
						<th>订单编号</th>
						<th>采购商名称</th>
						<th>业务状态</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品属性</th>
						<th>采购数量</th>
						<th>已发货数量</th>
						<th>发货数量</th>
					</tr>
					</thead>
					<tbody id="prodInfo">
						<input name="bizStatu" value="1" type="hidden"/>
					</tbody>
				</table>
				<%--<input id="ensureData" class="btn btn-primary" type="button"  value="确定"/>--%>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<form:form id="searchForm" modelAttribute="bizOrderHeader">
		<form:hidden id="orderNumCopy" path="orderNum"/>
		<form:hidden id="skuItemNoCopy" path="itemNo"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>

	</form:form>
</body>
</html>
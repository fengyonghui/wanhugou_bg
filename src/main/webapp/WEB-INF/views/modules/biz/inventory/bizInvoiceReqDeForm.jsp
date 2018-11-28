<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发货单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/tablesMergeCell/tablesMergeCell.js"></script>
	<script type="text/javascript">
        $(document).ready(function() {
            $('#select_all').live('click',function(){
                var choose=$("input[type='checkbox']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
            var str = $("#str").val();
            if (str != 'audit') {
                $(":input").each(function () {
                   $(this).attr("disabled","disabled");
                });
                $("select").each(function () {
                    $(this).attr("disabled","disabled");
                });
                $("textarea").each(function () {
                   $(this).attr("disabled","disabled");
                });
                $("#id").removeAttr("disabled");
                $("#btnCancel").removeAttr("disabled");
                $("#btnSubmit").removeAttr("disabled");
                $("#freight").removeAttr("disabled");
            }
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    var tt="";
                    var total = 0;
                    var str = $("#str").val();
                    var freight = $("#freight").val();
                    if (str == 'audit') {
                        $("td[name='requestNo']").each(function(i) {
                            var t= $(this).parent().attr("id");
                            var detail="";
                            var num ="";
                            var sObj= $("#prodInfo3").find("input[title='sent_"+t+"']");
                            sObj.each(function (index) {
                                total+= parseInt($(this).val());
                            });
                            $("#prodInfo3").find("input[title='details_"+t+"']").each(function (i) {
                                detail+=$(this).val()+"-"+sObj[i].value+"*";

                            });
                            tt+=t+"#"+detail+",";

                        });
                        tt=tt.substring(0,tt.length-1);
                    }
                    if(window.confirm('你确定要发货吗？')){
                        if (total <= 0 && freight == undefined) {
                            alert("发货数量不能为0");
                            return false;
                        }
							// alert("确定");
							if (tt != '') {
								$("#prodInfo3").append("<input name='requestHeaders' type='hidden' value='"+tt+"'>");
							}
							form.submit();
							loading('正在提交，请稍等...');
                    }else{
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
                var orderNum=$("#reqNo").val();
                $("#reqNoCopy").val(orderNum);
                var skuItemNo=$("#skuItemNo").val();
                $("#skuItemNoCopy").val(skuItemNo);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                var name =$("#name").val();
                $("#nameCopy").val(name);

                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/request/bizRequestHeaderForVendor/findByRequest",
                    data:$('#searchForm').serialize(),
                    success:function (data) {
                        if ($("#id").val() == '') {
                            $("#prodInfo2").empty();
                        }


                        var tr_tds="";
                        var sum = 0;
                        $.each(data, function (index,requestHeader) {
                            if(requestHeader.bizStatus==10){
                                bizName="采购中"
                            }else if(requestHeader.bizStatus==15){
                                bizName="采购完成"
                            }else if(requestHeader.bizStatus==20){
                                bizName="备货中"
                            }else if(requestHeader.bizStatus==25){
                                bizName="供货完成"
                            }

                            var flag= true;
                            $.each(requestHeader.requestDetailList,function (index,detail) {

                                tr_tds+="<tr class='tr_"+requestHeader.id+"'>";

                                if(flag){
                                    tr_tds+="<td rowspan='"+requestHeader.requestDetailList.length+"'><input type='checkbox' value='"+requestHeader.id+"' /></td>";

                                    tr_tds+= "<td rowspan='"+requestHeader.requestDetailList.length+"'><a href='${ctx}/biz/request/bizRequestHeader/form?id="+requestHeader.id+"&str=detail'>"+requestHeader.reqNo+"</a></td><td rowspan='"+requestHeader.requestDetailList.length+"'>"+requestHeader.fromOffice.name+"</td><td rowspan='"+requestHeader.requestDetailList.length+"'>"+bizName+"</td>" ;
                                }
                                tr_tds+="<input title='details_"+requestHeader.id+"' name='' type='hidden' value='"+detail.id+"'>";
                                tr_tds+= "<td>"+detail.skuInfo.name+"</td><td>"+detail.skuInfo.vendorName+"</td><td>"+(detail.skuInfo.itemNo==undefined?"":detail.skuInfo.itemNo)+"</td><td>"+detail.skuInfo.partNo+"</td><td>"+detail.skuInfo.skuPropertyInfos+"</td>" ;

                                tr_tds+= "<td>"+detail.reqQty+"</td><td>"+detail.sendQty+"</td>";
                                tr_tds+="<td><input  type='text' title='sent_"+requestHeader.id+"' name='' onchange='checkNum("+detail.reqQty+","+detail.sendQty+",this)' value='"+(detail.reqQty-detail.sendQty)+"'></td>";
                                tr_tds+="</tr>";
                                // alert(detail.skuInfo.buyPrice)
                                if(requestHeader.requestDetailList.length>1){
                                    flag=false;
                                }
                            });

                        });

                        $("#prodInfo2").append(tr_tds);
                    }
                });
            });
            var num = 0;
            <%--点击确定时获取订单详情--%>
            $("#ensureData").click(function () {
                $('input:checkbox:checked').each(function(i) {
                    var t= $(this).val();
                    var ttp= $(this).parent().parent().parent();
                    var trt= ttp.find($(".tr_"+t))
                    $("#prodInfo").append(trt);
                });
                $("#select_all").removeAttr("checked");

            });

            $("#contentTable3").tablesMergeCell({
                // automatic: true
                // 是否根据内容来合并
                cols: [0, 1, 2]
                // rows:[0,2]
            });

        });
        function checkNum(reqQty, sendQty, sentQty) {
            if (parseInt(sendQty)+parseInt(sentQty.value) > parseInt(reqQty)){
                alert("发货数量大于需求数量，请修改");
                $(sentQty).val(0);
            }
        }


	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/inventory/bizInvoice??ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货记录</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInvoice/invoiceRequestDetail?id=${bizInvoice.id}&?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}">发货单<shiro:hasPermission name="biz:inventory:bizInvoice:edit">${not empty bizInvoice.id?'详情':'详情'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInvoice:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
        <input id="str" name="str" type="hidden" value="${bizInvoice.str}"/>
        <input id="creOrdLogistics" name="creOrdLogistics" type="hidden" value="${bizInvoice.creOrdLogistics}"/>
		<sys:message content="${message}"/>
		<c:if test="${bizInvoice.id != null && bizInvoice.id != ''}">
			<div class="control-group">
				<label class="control-label">发货单号：</label>
				<div class="controls">
					<form:input path="sendNumber" htmlEscape="false" disabled="true" class="input-xlarge "/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">物流单号：</label>
			<div class="controls">
				<form:input path="trackingNumber" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流信息图：</label>
			<div class="controls">
				<form:hidden path="imgUrl" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="imgUrl" type="images" uploadPath="/logistics/info" selectMultiple="true" maxWidth="100"
							  maxHeight="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">验货员：</label>
			<div class="controls">
				<form:select about="choose" path="inspector.id" class="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${inspectorList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">验货时间：</label>
			<div class="controls">
				<input name="inspectDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					   value="<fmt:formatDate value="${bizInvoice.inspectDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">验货备注：</label>
			<div class="controls">
				<form:textarea path="inspectRemark" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">集货地点：</label>
			<div class="controls">
				<form:select path="collLocate" htmlEscape="false" maxlength="30" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('coll_locate')}" itemValue="value" itemLabel="label"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货值：</label>
			<div class="controls">
				<form:input id="valuePrice" path="valuePrice"  htmlEscape="false" value="" readonly="true" class="input-xlarge required"/>
			</div>
		</div>
		<c:if test="${bizInvoice.str == 'freight' || source == 'xq'}">
			<div class="control-group">
				<label class="control-label">运费：</label>
				<div class="controls">
					<form:input path="freight" htmlEscape="false" class="input-xlarge required"/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">发货时间：</label>
			<div class="controls">
				<input name="sendDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					   value="<fmt:formatDate value="${bizInvoice.sendDate}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">物流结算方式：</label>
			<div class="controls">
                <form:select id="settlementStatus" path="settlementStatus" onmouseout="" class="input-xlarge required">
                    <c:forEach items="${fns:getDictList('biz_settlement_status')}" var="settlementStatus">
                        <option value="${settlementStatus.value}">${settlementStatus.label}</option>
                    </c:forEach>
                </form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" maxlength="30" class="input-xlarge "/>
			</div>
		</div>
		<c:if test="${source ne 'xq'}">
			<c:if test="${bizInvoice.id == null}">
				<div class="control-group">
					<label class="control-label">选择备货单：</label>
					<div class="controls">
						<ul class="inline ul-form">
							<li><label>备货清单号：</label>
								<input id="reqNo" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
							</li>
							<li><label>商品货号：</label>
								<input id="skuItemNo"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
							</li>
							<li><label>商品编码：</label>
								<input id="skuCode"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
							</li>
							<li><label>供应商：</label>
								<input id="name"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
							</li>
							<li class="btns"><input id="searchData" class="btn btn-primary" type="button"  value="查询"/><span style="color: red;">(请输入没有供货完成的订单)</span></li>
							<li class="clearfix"></li>
						</ul>

					</div>
				</div>

				<div class="control-group">
					<label class="control-label">待发货单：</label>
					<div class="controls">
						<table id="contentTable2"  class="table table-striped table-bordered table-condensed">
							<thead>
							<tr>
								<th><input id="select_all" type="checkbox" /></th>
								<th>备货清单号</th>
								<th>采购中心</th>
								<th>业务状态</th>
								<th>商品名称</th>
								<th>供应商</th>
								<th>商品货号</th>
								<th>商品编码</th>
								<th>商品属性</th>

								<th>申报数量</th>
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
								<th>备货清单号</th>
								<th>采购中心</th>
								<th>业务状态</th>
								<th>商品名称</th>
								<th>供应商</th>
								<th>商品货号</th>
								<th>商品编码</th>
								<th>商品属性</th>
								<c:if test="${bizStatus==0}">
									<th>选择仓库</th>
								</c:if>
								<th>申报数量</th>
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
			</c:if>
		</c:if>


        <div class="control-group">
            <label class="control-label">已发货详情：</label>
            <div class="controls">
                <table id="contentTable3"  class="table table-striped table-bordered table-condensed">
                    <thead>
                    <tr>
                        <th>备货单编号</th>
                        <th>采购中心名称</th>
                        <th>业务状态</th>
                        <th>商品名称</th>
                        <th>商品编码</th>
                        <th>商品属性</th>
                        <th>申报</th>
                        <th>已发货数量</th>
						<c:if test="${bizInvoice.str == 'audit'}">
                        	<th>发货数量</th>
						</c:if>
                    </tr>
                    </thead>
                    <tbody id="prodInfo3">
                    <c:if test="${requestHeaderList!=null && requestHeaderList.size()>0}">
                        <c:forEach items="${requestHeaderList}" var="requestHeader">
                            <c:set var="flag" value="true"></c:set>
                                <c:forEach items="${requestHeader.requestDetailList}" var="requestDetail" varStatus="index">
									<tr id="${requestHeader.id}">
										<c:if test="${flag}">
											<td name="requestNo" rowspan="${fn:length(requestHeader.requestDetailList)}"><a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}&str=detail">${requestHeader.reqNo}</a></td>
										</c:if>
										<%--<td name="requestNo">--%>
											<%--<a href="${ctx}/biz/request/bizRequestHeader/form?id=${requestHeader.id}&str=detail">${requestHeader.reqNo}</a>--%>
										<%--</td>--%>
										<td>${requestHeader.fromOffice.name}</td>
										<td>${fns:getDictLabel(requestHeader.bizStatus,"biz_req_status",'' )}</td>
										<td>${requestDetail.skuInfo.name}</td>
										<td>${requestDetail.skuInfo.partNo}</td>
										<td>${requestDetail.skuInfo.skuPropertyInfos}</td>
										<td>${requestDetail.reqQty}</td>
										<td>${requestDetail.sendQty}</td>
										<c:if test="${bizInvoice.str == 'audit'}">
											<input type="hidden" title="details_${requestHeader.id}" value="${requestDetail.id}"/>
											<td><input type='text' title='sent_${requestHeader.id}' name='' required="required" onchange='checkNum(${requestDetail.reqQty},${requestDetail.sendQty},this)' value='0'></td>
										</c:if>
									</tr>
									<c:if test="${fn:length(requestHeader.requestDetailList)>1}">
										<c:set var="flag" value="false"></c:set>
									</c:if>
                                </c:forEach>

                        </c:forEach>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
		<div class="form-actions">
			<input onclick="window.print();" type="button" class="btn btn-primary" value="打印发货单" style="background:#F78181;"/>
			<c:if test="${source ne 'xq'}">
				<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<form:form id="searchForm" modelAttribute="bizRequestHeader">
		<input type="hidden" id="reqNoCopy" name="reqNo" value="${bizRequestHeader.reqNo}"/>
		<input type="hidden" id="skuItemNoCopy" name="itemNo" value="${bizRequestHeader.itemNo}"/>
		<input type="hidden" id="skuCodeCopy" name="partNo" value="${bizRequestHeader.partNo}"/>
		<input type="hidden" id="nameCopy" name="name" value="${bizRequestHeader.name}"/>

	</form:form>
</body>
</html>
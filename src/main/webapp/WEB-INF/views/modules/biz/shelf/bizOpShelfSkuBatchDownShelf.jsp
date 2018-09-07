<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.BizOpShelfInfoEnum" %>
<%--当前系统时间--%>
<jsp:useBean id="time" class="java.util.Date"/>
<html>
<head>
	<title>商品上架管理</title>
	<script type="text/javascript" src="${ctxStatic}/tablesMergeCell/tablesMergeCell.js"></script>
	<script type="text/javascript">
        var opShelfType="";
        $(document).ready(function() {

            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    var opShelfSkuId = "";
                    $("#prodInfo2").find("input[checked='checked']").each(function (i) {
                        alert($(this).val()+"---");
						if ($(this).val() != "") {
						    opShelfSkuId += $(this).val() + ",";
						}
                    });
                    $("#opShelfSkuIds").val(opShelfSkuId);
                    alert(opShelfSkuId);
                    if (opShelfSkuId != "") {
                        loading('正在提交，请稍等...');
                        form.submit();
					} else {
                        alert("请选择要下架的商品！");
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
            $('#select_all').live('click',function(){
                var choose=$("input[title='shelfIds']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
            $("#searchData").click(function () {
                var prodBrandName=$("#prodBrandName").val();
                $("#prodBrandNameCopy").val(prodBrandName);
                var opShelfInfo = $("#opShelfInfo").val();
                $("#opShelfInfoCopy").val(opShelfInfo);
                var skuName=$("#skuName").val();
                $("#skuNameCopy").val(skuName);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                var itemNo =$("#itemNo").val();
                $("#itemNoCopy").val(itemNo);
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/shelf/bizOpShelfSkuV2/findDownOpShelfSku",
                    data:$('#searchForm').serialize(),
                    success:function (result) {
                         $("#prodInfo2").empty();
                         if (result == "") {
                             alert("请输入查询条件！");
                             return false;
                         } else {
                             var tr_tds = "";
                             $.each(result, function (index, opShelfSku) {
                                 tr_tds += "<tr>";
                                 tr_tds += "<td><input type='checkbox' value='" + opShelfSku.id + "' name='opshelfSkuIds' title='shelfIds' onclick='chooseThis(this)'/></td>";
                                 tr_tds += "<td>" + opShelfSku.skuInfo.name + "</td><td>" + opShelfSku.skuInfo.partNo + "</td><td>" + opShelfSku.skuInfo.itemNo + "</td><td>" + opShelfSku.opShelfInfo.name + "</td>" ;
                                 if (opShelfSku.centerOffice.id != 0) {
                                     tr_tds += "<td>" + opShelfSku.centerOffice.name + "</td>";
                                 }
                                 if (opShelfSku.centerOffice.id == 0) {
                                     tr_tds += "<td>平台商品</td>";
                                 }
                                 tr_tds += "<td>" + opShelfSku.productInfo.vendorName + "</td>";
                                 tr_tds += "<td>" + opShelfSku.orgPrice + "</td>";
                                 tr_tds += "<td>" + opShelfSku.salePrice + "</td>";
                                 tr_tds += "<td>" + opShelfSku.minQty + "</td>";
                                 tr_tds += "<td>" + opShelfSku.maxQty + "</td>";
                                 tr_tds += "<td>" + opShelfSku.shelfUser.name + "</td>";
                                 tr_tds += "<td>" +opShelfSku.shelfTime + "</td>";
                                 tr_tds += "</tr>";
                             });
                             $("#prodInfo2").append(tr_tds);
                         }
                    }
                });
            });
            /*$("#contentTableService").tablesMergeCell({
                automatic: true,
                // 是否根据内容来合并
                cols: [0, 0]
                // rows:[0,2]
            });*/
        });
        function chooseThis(opShelfSkuId) {
			if ($(opShelfSkuId).attr("checked") == "checked") {
                $(opShelfSkuId).attr("checked","checked");
			}
			if ($(opShelfSkuId).attr("checked") != "checked") {
                $(opShelfSkuId).removeAttr("checked");
			}
        }
	</script>
	<meta name="decorator" content="default"/>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/biz/shelf/bizOpShelfSkuV2/">商品上架列表</a></li>
	<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfSkuV2/downShelfAdd/">批量下架<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit">${not empty bizOpShelfSku.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizOpShelfSku:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="bizOpShelfSku" action="${ctx}/biz/shelf/bizOpShelfSkuV2/batchDownShelf" method="post" class="form-horizontal">
	<%--<form:hidden path="id" var="id"/>--%>
	<form:hidden path="shelfSign"/>
	<input id="opShelfSkuIds" type="hidden" name="opShelfSkuIds" value=""/>
	<%--<input type="hidden" id="opShelfId" value="${bizOpShelfSku.opShelfInfo.id}"/>--%>
	<%--<form:hidden id="shelfId" path="opShelfInfo.id"/>--%>
	<sys:message content="${message}"/>

	<div class="control-group">
		<label class="control-label">选择商品：</label>
		<div class="controls">
			<ul class="inline ul-form">
				<li><label>品牌名称：</label>
					<input id="prodBrandName" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
				</li>
                <li><label>货架名称：</label>
                    <input id="opShelfInfo" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
                </li>
				<li><label>商品名称：</label>
					<input id="skuName"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
				</li>
				<li><label>商品编码：</label>
					<input id="skuCode"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
				</li>
				<li><label>商品货号：</label>
					<input id="itemNo"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
				</li>
				<li class="btns"><input id="searchData" class="btn btn-primary" type="button"  value="查询"/></li>
				<li class="clearfix"></li>
			</ul>

		</div>
	</div>
	<div class="control-group">
		<label class="control-label">下架商品：</label>
		<div class="controls">
			<table id="contentTable2"  class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th><input id="select_all" type="checkbox" /></th>
					<th>商品名称</th>
					<th>商品编码</th>
					<th>商品货号</th>
					<th>货架名称</th>
					<th>采购中心</th>
					<th>供应商</th>
					<th>原价</th>
					<th>现价</th>
					<th>最低销售数量</th>
					<th>最高销售数量</th>
					<th>上架人</th>
					<th>上架时间</th>
				</tr>
				</thead>
				<tbody id="prodInfo2">

				</tbody>
			</table>
			<%--<input id="ensureData" class="btn btn-primary" type="button"  value="确定"/>--%>
		</div>
	</div>

	<%--<div class="control-group">
		<div class="controls">
			<table id="ShelfSkuTable" class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th>商品名称</th>
					<th>商品编码</th>
					<th>商品货号</th>
					<th>货架名称</th>
					<th>采购中心</th>
					<th>供应商</th>
					<th>原价</th>
					<th>现价</th>
					<th>最低销售数量</th>
					<th>最高销售数量</th>
					<th>上架人</th>
					<th>上架时间</th>
					<th>操作</th>
				</tr>
				</thead>
				<tbody id="tbody">

				</tbody>
			</table>
		</div>
	</div>--%>

	<div class="form-actions">
		<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="下 架"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</form:form>

<form:form id="searchForm" modelAttribute="bizOpShelfSku" >
	<form:hidden id="prodBrandNameCopy" path="productInfo.brandName"/>
	<form:hidden id="opShelfInfoCopy" path="opShelfInfo.name"/>
	<form:hidden id="skuNameCopy" path="skuInfo.name"/>
	<form:hidden id="skuCodeCopy" path="skuInfo.partNo"/>
	<form:hidden id="itemNoCopy" path="skuInfo.itemNo"/>
</form:form>

</body>

</html>
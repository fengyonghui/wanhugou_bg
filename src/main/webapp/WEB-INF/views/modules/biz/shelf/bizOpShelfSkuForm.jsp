<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品上架管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    $("input[name='unshelfTimes']").each(function () {
                        if($(this).val()==''){
                            $(this).val(0)
                        }
                    })
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

            $.ajax({
                type:"post",
                url:"${ctx}/biz/shelf/bizOpShelfInfo/findShelf",
                success:function (data) {
                    $.each(data,function(index,shelfInfo) {
                        $("#shelfInfoId").append("<option value='"+shelfInfo.id+"'>"+shelfInfo.name+"</option>")
					})

                }
            })

            $('#select_all').live('click',function(){
                var choose=$("input[title='shelfIds']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });

            var id=$("#id").val();

            $("#searchData").click(function () {
                var prodBrandName=$("#prodBrandName").val();
                $("#prodBrandNameCopy").val(prodBrandName);
                var skuName=$("#skuName").val();
                $("#skuNameCopy").val(skuName);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuList",
                    data:$('#searchForm').serialize(),
                    success:function (data) {
                        if(id==''){
                            $("#prodInfo2").empty();
                        }

                        $.each(data,function (keys,skuInfoList) {
                            var prodKeys= keys.split(",");
                            var prodId= prodKeys[0];
//                            var prodName= prodKeys[1];
                            var prodUrl= prodKeys[2];
//                            var cateName= prodKeys[3];
//                            var prodCode= prodKeys[4];
//                            var prodOfficeName= prodKeys[5];
                            var  brandName=prodKeys[6];
                            var flag=true;

                            var tr_tds="";

                            var t=0;
                            $.each(skuInfoList,function (index,skuInfo) {

                                tr_tds+= "<tr class='"+prodId+"'>";
                                tr_tds+="<td><input type='checkbox' value='"+skuInfo.id+"'  title='shelfIds'/></td>";
                                tr_tds+= "<td>"+skuInfo.name+"</td><td>"+skuInfo.partNo+"</td><td>"+skuInfo.skuPropertyInfos+"</td>" ;

                                if(flag){
                                    tr_tds+="<td rowspan='"+skuInfoList.length+"'>"+brandName+"</td>";
                                    tr_tds+= "<td rowspan='"+skuInfoList.length+"'><img src='"+prodUrl+"'></td>"
                                }

                                tr_tds+="</tr>";
                                if(skuInfoList.length>1){
                                    flag=false;
                                }
                            });

                            t++;
                            $("#prodInfo2").append(tr_tds);

                        });

                    }
                })
            });
            $("#ensureData").click(function () {
                var skuIds="";
                $('input:checkbox:checked').each(function(i){
                    skuIds+=$(this).val()+",";
				});
                skuIds=skuIds.substring(0,skuIds.length-1);
                $.ajax({
                    type:"POST",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuNameList?ids="+skuIds,
                    dataType:"json",
                    success:function(data){
                        var htmlInfo = "";
                        $.each(data,function(index,item) {
                            htmlInfo+="<tr id='"+item.id+"'><td><input name='skuInfoIds' type='hidden' readonly='readonly' value='"+item.id+"'/>"+ item.name +"</td>"+
                                "<td><input name='shelfQtys' value='' htmlEscape='false' maxlength='6' class='input-mini required' type='text' placeholder='必填！'/></td>"+
                                "<td><input name='orgPrices' readonly='readonly' value='"+item.basePrice+"' htmlEscape='false' maxlength='6' class='input-mini required' type='text' placeholder='必填！' /></td>"+
                                "<td><input name=\"salePrices\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" placeholder=\"必填！\"/></td>"+
                                "<td><input name=\"minQtys\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type=\"text\" placeholder=\"必填！\"/></td>"+
                                "<td><input name=\"maxQtys\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type=\"text\" placeholder=\"必填！\"/></td>"+
                                "<td><input name=\"shelfTimes\" value=\"\" type=\"text\" readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate required\"" +
                                "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\" placeholder=\"必填！\"/></td>"+

                                "<td><input name=\"unshelfTimes\" type=\"text\" value='' readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate \"" +
                                "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\" placeholder=\"选填！\"/></td>" +
                                "<td><input name=\"prioritys\" value=\"\" htmlEscape=\"false\" maxlength=\"5\" class=\"input-medium required\" type=\"text\" placeholder=\"必填！\"/></td>"+
								"<td><a href='#' onclick='removeItem(\""+item.id+"\")'>移除</a></td></tr>";
                        });
                        $("#tbody").append(htmlInfo);
                    }
                })
            });

		});
        function removeItem(obj) {
            $("#"+obj).remove();

        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/shelf/bizOpShelfSku/">商品上架列表</a></li>
		<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfSku/form?id=${bizOpShelfSku.id}">商品上架<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit">${not empty bizOpShelfSku.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizOpShelfSku:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOpShelfSku" action="${ctx}/biz/shelf/bizOpShelfSku/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="shelfSign"/>
		<%--<form:hidden id="shelfId" path="opShelfInfo.id"/>--%>
		<sys:message content="${message}"/>

		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<sys:treeselect id="centerOffice" name="centerOffice.id" value="${entity.centerOffice.id}" labelName="centerOffice.name"
								labelValue="${entity.centerOffice.name}"  notAllowSelectParent="true"
								title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-xlarge required" dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货架名称：</label>
			<div class="controls">
				<select id="shelfInfoId" name="opShelfInfo.id" class="input-xlarge required">
					<option value="">请选择</option>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%--<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit">--%>
		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
				<ul class="inline ul-form">
					<li><label>品牌名称：</label>
						<input id="prodBrandName" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
					</li>
					<li><label>商品名称：</label>
						<input id="skuName"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="10" class="input-medium"/>
					</li>
					<li><label>商品编码：</label>
						<input id="skuCode"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false" maxlength="10" class="input-medium"/>
					</li>
					<li class="btns"><input id="searchData" class="btn btn-primary" type="button"  value="查询"/></li>
					<li class="clearfix"></li>
				</ul>

			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上架商品：</label>
			<div class="controls">
			<table id="contentTable2"  class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th><input id="select_all" type="checkbox" /></th>
					<th>商品名称</th>
					<th>商品编码</th>
					<th>商品属性</th>
					<th>品牌名称</th>
					<th>产品图片</th>

					<%--<th>操作</th>--%>
				</tr>
				</thead>
				<tbody id="prodInfo2">

				</tbody>
			</table>
				<input id="ensureData" class="btn btn-primary" type="button"  value="确定"/>
			</div>
		</div>

		<div class="control-group">
			<div class="controls">
				<table id="ShelfSkuTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th>商品名称：</th>
							<c:if test="${bizOpShelfSku.id != null}">
									<th>上架人：</th>
							</c:if>
							<th>上架数量(个)：</th>
							<th>原价(元)：</th>
							<th>销售单价(元)</th>
							<th>最低销售数量(个)：</th>
							<th>最高销售数量(个,0:不限制)：</th>
							<th>上架时间：</th>
							<c:if test="${bizOpShelfSku.id != null}">
									<th>下架人：</th>
							</c:if>
							<th>下架时间：</th>
							<th>显示次序：</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody id="tbody">
						<c:if test="${bizOpShelfSku.id != null}">
							<tr>
								<td>
									<input name="skuInfoIds" value="${bizOpShelfSku.id}" class="input-medium required" type="hidden"/>${bizOpShelfSku.skuInfo.name}
									</td>
								<td><input name="createBy.name" value="${bizOpShelfSku.shelfUser.name}" htmlEscape="false" maxlength="11" class="input-medium" readonly="true" type="text" placeholder="必填！"/></td>
								<td><input name="shelfQtys" value="${bizOpShelfSku.shelfQty}" htmlEscape="false" maxlength="6" class="input-medium required" type="text" placeholder="必填！"/></td>
								<td><input name="orgPrices" value="${bizOpShelfSku.orgPrice}" htmlEscape="false" maxlength="6" class="input-medium required" type="text" placeholder="必填！"/></td>
								<td><input name="salePrices" value="${bizOpShelfSku.salePrice}" htmlEscape="false" maxlength="6" class="input-medium required" placeholder="必填！"/></td>
								<td><input name="minQtys" value="${bizOpShelfSku.minQty}" htmlEscape="false" maxlength="6" class="input-medium required" type="text" placeholder="必填！"/></td>
								<td><input name="maxQtys" value="${bizOpShelfSku.maxQty}" htmlEscape="false" maxlength="6" class="input-medium required" type="text" placeholder="必填！"/></td>
								<td><input name="shelfTimes" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
										   value="<fmt:formatDate value="${bizOpShelfSku.shelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
										   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/></td>
								<td><input name="createBy.name" value="${bizOpShelfSku.unshelfUser.name}" htmlEscape="false" maxlength="11" class="input-medium" readonly="true" type="text" placeholder="必填！"/></td>
								<td><input name="unshelfTimes" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
										   value="<fmt:formatDate value="${bizOpShelfSku.unshelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
										   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="选填！"/></td>
								<td><input name="prioritys" value="${bizOpShelfSku.priority}" htmlEscape="false" maxlength="5" class="input-medium required" type="text" placeholder="必填！"/></td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>

	</form:form>

	<form:form id="searchForm" modelAttribute="bizSkuInfo" >
		<%--<form:hidden id="productNameCopy" path="productInfo.name"/>--%>
		<%--<form:hidden id="prodCodeCopy" path="productInfo.prodCode"/>--%>
		<form:hidden id="prodBrandNameCopy" path="productInfo.brandName"/>
		<form:hidden id="skuNameCopy" path="name"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>
		<%--<form:hidden id="skuTypeCopy" path="skuType"/>--%>
	</form:form>

</body>
</html>
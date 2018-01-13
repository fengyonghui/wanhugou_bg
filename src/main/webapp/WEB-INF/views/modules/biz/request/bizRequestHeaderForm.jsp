<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.SkuTypeEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
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

            var id=$("#id").val();


			$("#searchData").click(function () {
                var productName=$("#productName").val();
                $("#productNameCopy").val(productName);
                var prodCode=$("#prodCode").val();
                $("#prodCodeCopy").val(prodCode);
                var prodBrandName=$("#prodBrandName").val();
                $("#prodBrandNameCopy").val(prodBrandName);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                var skuType=$("#skuType").val();
                $("#skuType").val(skuType);
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuList",
                    data:$('#searchForm').serialize(),
                    success:function (data) {
                        if(id==''){
                            $("#prodInfo").empty();
                        }
                        $.each(data,function (keys,skuInfoList) {
                            var prodKeys= keys.split(",");
                            console.info(prodKeys);
                            var prodId= prodKeys[0];
                            var prodName= prodKeys[1];
                            var prodUrl= prodKeys[2];
                            var cateName= prodKeys[3];
                            var prodCode= prodKeys[4];
                            var prodOfficeName= prodKeys[5];
                            var  brandName=prodKeys[6];
                            var flag=true;
                            var trq="<tr> ";
                            var td="<td><img src='"+prodUrl+"'></td><td>"+prodName+"</td>" +
                                                               "<td>" +cateName+"</td>" +
                                                               "<td>"+prodCode+"</td><td>"+brandName+"</td><td>"+prodOfficeName+"</td>";
                            var tds="";
                            var tr_tds=""
                            $.each(skuInfoList,function (index,skuInfo) {
                                tr_tds+= "<tr>";
								if(flag){
                                    tr_tds+= "<td rowspan='"+skuInfoList.length+"'><img src='"+prodUrl+"'></td><td rowspan='"+skuInfoList.length+"'>"+prodName+"</td>" +
                                    "<td rowspan='"+skuInfoList.length+"'>" +cateName+"</td>" +
                                    "<td rowspan='"+skuInfoList.length+"'>"+brandName+"</td><td rowspan='"+skuInfoList.length+"'>"+prodOfficeName+"</td>";
								}
                                tr_tds+= "<td class='"+skuInfo.id+"'>"+skuInfo.name+"</td><td class='"+skuInfo.id+"'>"+skuInfo.partNo+"</td><td class='"+skuInfo.id+"'>"+skuInfo.skuTypeName+"</td><td class='"+skuInfo.id+"'><input type='hidden' name='requestDetailList["+index+"].skuInfo.id' value='"+skuInfo.id+"'/><input name='requestDetailList["+index+"].reqQty' type='text'/></td>" +
									"<td class='"+skuInfo.id+"'>0</td>" +
									"<td class='"+skuInfo.id+"'><a href='#' onclick=\"removeItem('"+skuInfo.id+"')\">移除</a></td>";
                         		tr_tds+="</tr>";
                           		if(skuInfoList.length>1){
                           		    flag=false;
								}
                            });
                            $("#prodInfo").append(tr_tds);

                        });
                    }
                })
            });
        });
		function removeItem(obj) {
			$("."+obj).remove();
        }
        function delItem(obj) {
		    if(confirm("您确认删除此条信息吗？")){
                $.ajax({
                type:"post",
                url:"${ctx}/biz/request/bizRequestDetail/delItem",
                data:{id:obj},
                success:function (data) {
                if(data=='ok'){
                    alert("删除成功！");
                $("#"+obj).remove();
                	}
                }
                })
			}


        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/request/bizRequestHeader/">备货清单列表</a></li>
		<li class="active"><a href="${ctx}/biz/request/bizRequestHeader/form?id=${bizRequestHeader.id}">备货清单<shiro:hasPermission name="biz:request:bizRequestHeader:edit">${not empty bizRequestHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:request:bizRequestHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestHeader/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
								labelValue="${entity.fromOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="采购中心" isAll="false"  url="/sys/office/queryTreeList?type=8" cssClass="input-xlarge required" dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">期望收货时间：</label>
			<div class="controls">
				<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate required"
					value="<fmt:formatDate value="${entity.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
				<ul class="inline ul-form">
					<li><label>产品名称：</label>
						<input id="productName"  htmlEscape="false" class="input-medium"/>
					</li>
					<li><label>产品编码：</label>
						<input id="prodCode"  htmlEscape="false" maxlength="10" class="input-medium"/>
					</li>
					<li><label>品牌名称：</label>
						<input id="prodBrandName"  htmlEscape="false" maxlength="50" class="input-medium"/>
					</li>
					<li><label>商品名称：</label>
						<input id="skuName"  htmlEscape="false" maxlength="10" class="input-medium"/>
					</li>
					<li><label>商品编码：</label>
						<input id="skuCode"  htmlEscape="false" maxlength="10" class="input-medium"/>
					</li>
					<li><label>商品类型：</label>
						<select id="skuType">
							<option value="">请选择</option>
							<option value="${SkuTypeEnum.OWN_PRODUCT.code}">${SkuTypeEnum.OWN_PRODUCT.name}</option>
							<option value="${SkuTypeEnum.COMMON_PRODUCT.code}">${SkuTypeEnum.COMMON_PRODUCT.name}</option>
							<option value="${SkuTypeEnum.MADE_PRODUCT.code}">${SkuTypeEnum.MADE_PRODUCT.name}</option>
						</select>
					</li>
					<li class="btns"><input id="searchData" class="btn btn-primary" type="button" value="查询"/></li>
					<li class="clearfix"></li>
				</ul>

			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备货商品：</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>产品图片</th>
						<th>产品名称</th>
						<th>产品分类</th>
						<th>品牌名称</th>
						<th>供应商</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品类型</th>
						<th>申报数量</th>
						<th>已收货数量</th>
						<th>操作</th>
					</tr>
					</thead>
					<tbody id="prodInfo">
					<c:if test="${reqDetailList!=null}">
						<c:forEach items="${reqDetailList}" var="reqDetail" varStatus="reqStatus">
							<tr id="${reqDetail.id}">
								<td><img src="${reqDetail.skuInfo.productInfo.imgUrl}"/></td>
								<td>${reqDetail.skuInfo.productInfo.name}</td>
								<td>
								<c:forEach items="${reqDetail.skuInfo.productInfo.categoryInfoList}" var="cate" varStatus="cateIndex" >
								${cate.name}
									<c:if test="${!cateIndex.last}">
										/
									</c:if>

								</c:forEach>
								</td>
								<td>${reqDetail.skuInfo.productInfo.prodCode}</td>
								<td>${reqDetail.skuInfo.productInfo.brandName}</td>
								<td>${reqDetail.skuInfo.productInfo.office.name}</td>
								<td>${reqDetail.skuInfo.name}</td>
								<td>
									<input type='hidden' name='requestDetailList[${reqStatus.index}].id' value='${reqDetail.id}'/>
									<input type='hidden' name='requestDetailList[${reqStatus.index}].skuInfo.id' value='${reqDetail.skuInfo.id}'/>

									<input name='requestDetailList[${reqStatus.index}].reqQty'  value="${reqDetail.reqQty}" class="input-medium" type='text'/>


								</td>
								<td>
									<input  value="${reqDetail.recvQty}" disabled="disabled" type='text'/>
								</td>
								<td><shiro:hasPermission name="biz:request:bizRequestDetail:edit">
									<a href="#" onclick="delItem(${reqDetail.id})">删除</a>
									</shiro:hasPermission>
								</td>
							</tr>
							<c:if test="${reqStatus.last}">
								<c:set var="aa" value="${reqStatus.index}" scope="page"/>
							</c:if>

						</c:forEach>
						<input id="aaId" value="${aa}" type="hidden"/>
					</c:if>
					</tbody>
				</table>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" maxlength="200" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务状态：</label>
			<div class="controls">
				<form:select path="bizStatus" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_req_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:request:bizRequestHeader:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>

	</form:form>
	<form:form id="searchForm" modelAttribute="bizSkuInfo" >
		<form:hidden id="productNameCopy" path="productInfo.name"/>
		<form:hidden id="prodCodeCopy" path="productInfo.prodCode"/>
		<form:hidden id="prodBrandNameCopy" path="productInfo.brandName"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>
		<form:hidden id="skuTypeCopy" path="skuType"/>
	</form:form>
</body>
</html>
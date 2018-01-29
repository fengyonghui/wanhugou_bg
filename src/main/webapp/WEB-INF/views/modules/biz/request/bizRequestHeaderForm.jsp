<%@ page import="com.wanhutong.backend.modules.enums.RoleEnNameEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.SkuTypeEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        var skuInfoId="";
		$(document).ready(function() {
			//$("#name").focus();
			var str=$("#str").val();
			if(str=='detail'){
			   $("#inputForm").find("input[type!='button']").attr("disabled","disabled") ;
			   $("#fromOfficeButton").hide();
			}
			$("#inputForm").validate({
				submitHandler: function(form){
                    $("input[name='reqQtys']").each(function () {
                        if($(this).val()==''){
                            $(this).val(0)
						}
                    });
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
                            if($("#prodInfo").children("."+prodId).length>0){
                            	return;
							}
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
                                skuInfoId+=","+skuInfo.id;
                                tr_tds+= "<tr class='"+prodId+"'>";
								if(flag){
                                    tr_tds+= "<td rowspan='"+skuInfoList.length+"'><img src='"+prodUrl+"'></td>" +

                                    "<td rowspan='"+skuInfoList.length+"'>"+brandName+"</td>";
								}
                                tr_tds+= "<td>"+skuInfo.name+"</td><td>"+skuInfo.partNo+"</td><td>"+skuInfo.skuPropertyInfos+"</td><td><input type='hidden' id='skuId_"+skuInfo.id+"' value='"+skuInfo.id+"'/><input class='input-mini' id='skuQty_"+skuInfo.id+"'   type='text'/></td>" ;
								if(flag){

                                    tr_tds+= "<td id='td_"+prodId+"' rowspan='"+skuInfoList.length+"'>" +
                                    "<a href='#' onclick=\"addItem('"+prodId+"')\">增加</a>" +
                                    "</td>";
								}
                         		tr_tds+="</tr>";
                           		if(skuInfoList.length>1){
                           		    flag=false;
								}
                            });
                            t++;
                            $("#prodInfo2").append(tr_tds);

                        });
                        var s=skuInfoId.indexOf(",");
                        if(s==0){
                            skuInfoId=skuInfoId.substring(1);
						}

                    }
                })
            });

        });
		function removeItem(obj) {
            $("#td_"+obj).html("<a href='#' onclick=\"addItem('"+obj+"')\">增加</a>");

            $("#prodInfo2").append($("."+obj));
            $.each(skuInfoId.split(","), function(i,val){
                $("#prodInfo2").find($("#skuId_"+val)).removeAttr("name");
                $("#prodInfo2").find($("#skuQty_"+val)).removeAttr("name");
            });

        }
        function addItem(obj) {
		   $("#td_"+obj).html("<a href='#' onclick=\"removeItem('"+obj+"')\">移除</a>");
			var trHtml=$("."+obj);
           $("#prodInfo").append(trHtml);
            console.info(skuInfoId);
            $.each(skuInfoId.split(","), function(i,val){
                $("#prodInfo").find($("#skuId_"+val)).attr("name","skuInfoIds");
                $("#prodInfo").find($("#skuQty_"+val)).attr("name","reqQtys");
            });
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
        function checkInfo(obj,val) {
            $.ajax({
                type:"post",
                url:"${ctx}/biz/request/bizRequestHeader/saveInfo",
                data:{checkStatus:obj,id:$("#id").val()},
                success:function (data) {
                    if(data){
                        alert(val+"成功！");
                        window.location.href="${ctx}/biz/request/bizRequestHeader";

                    }
                }
            })
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/request/bizRequestHeader/">备货清单列表</a></li>
		<li class="active"><a href="${ctx}/biz/request/bizRequestHeader/form?id=${bizRequestHeader.id}">备货清单<shiro:hasPermission name="biz:request:bizRequestHeader:edit">${not empty bizRequestHeader.str?'详情':(not empty bizRequestHeader.id?'修改':'添加')}</shiro:hasPermission><shiro:lacksPermission name="biz:request:bizRequestHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestHeader/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="str" type="hidden"  value="${entity.str}"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
								labelValue="${entity.fromOffice.name}"  notAllowSelectParent="true"
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
		<c:if test="${entity.str!='detail'}">

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
					<%--<li><label>商品类型：</label>--%>
						<%--<select id="skuType" class="input-medium">--%>
							<%--<option value="">请选择</option>--%>
							<%--<option value="${SkuTypeEnum.OWN_PRODUCT.code}">${SkuTypeEnum.OWN_PRODUCT.name}</option>--%>
							<%--<option value="${SkuTypeEnum.COMMON_PRODUCT.code}">${SkuTypeEnum.COMMON_PRODUCT.name}</option>--%>
							<%--<option value="${SkuTypeEnum.MADE_PRODUCT.code}">${SkuTypeEnum.MADE_PRODUCT.name}</option>--%>
						<%--</select>--%>
					<%--</li>--%>
					<li class="btns"><input id="searchData" class="btn btn-primary" type="button"  value="查询"/></li>
					<li class="clearfix"></li>
				</ul>

			</div>
		</div>

		</c:if>
			<div class="control-group">
			<label class="control-label">备货商品：</label>
			<div class="controls">
			<table id="contentTable" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th>产品图片</th>
					<th>品牌名称</th>
					<th>商品名称</th>
					<th>商品编码</th>
					<th>商品属性</th>
					<th>申报数量</th>
					<c:if test="${entity.str=='detail' && entity.bizStatus>=ReqHeaderStatusEnum.PURCHASING.state}">
						<th>已收货数量</th>
					</c:if>

					<shiro:hasPermission name="biz:request:bizRequestDetail:edit">
						<c:if test="${entity.str!='detail'}">
							<th>操作</th>
						</c:if>

					</shiro:hasPermission>

				</tr>
				</thead>
				<tbody id="prodInfo">
				<c:if test="${reqDetailList!=null}">
					<c:forEach items="${reqDetailList}" var="reqDetail" varStatus="reqStatus">
						<tr class="${reqDetail.skuInfo.productInfo.id}" id="${reqDetail.id}">
							<td><img src="${reqDetail.skuInfo.productInfo.imgUrl}"/></td>
							<td>${reqDetail.skuInfo.productInfo.brandName}</td>
							<td>${reqDetail.skuInfo.name}</td>
							<td>${reqDetail.skuInfo.partNo}</td>
							<td>${reqDetail.skuInfo.skuPropertyInfos}</td>
							<td>
								<input  type='hidden' name='reqDetailIds' value='${reqDetail.id}'/>
								<input type='hidden' name='skuInfoIds' value='${reqDetail.skuInfo.id}'/>

								<input name='reqQtys'  value="${reqDetail.reqQty}" class="input-mini" type='text'/>
							</td>
							<c:if test="${entity.str=='detail' && entity.bizStatus>=ReqHeaderStatusEnum.PURCHASING.state}">
								<td>${reqDetail.recvQty}</td>
							</c:if>
							<shiro:hasPermission name="biz:request:bizRequestDetail:edit">
								<c:if test="${entity.str!='detail'}">
								<td>
								<a href="#" onclick="delItem(${reqDetail.id})">删除</a>

								</td>
								</c:if>
							</shiro:hasPermission>
						</tr>
						<c:if test="${reqStatus.last}">
							<c:set var="aa" value="${reqStatus.index}" scope="page"/>
						</c:if>

					</c:forEach>
					<input id="aaId" value="${aa}" type="hidden"/>
				</c:if>
				</tbody>
			</table>
			<c:if test="${entity.str!='detail'}">
			<table id="contentTable2" style="width:48%;float: right;background-color:#abcceb;" class="table table-bordered table-condensed">
					<thead>
					<tr>
						<th>产品图片</th>
							<%--<th>产品名称</th>--%>
							<%--<th>产品分类</th>--%>
						<th>品牌名称</th>
							<%--<th>供应商</th>--%>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品属性</th>
							<%--<th>商品类型</th>--%>
						<th>申报数量</th>
							<%--<th>已收货数量</th>--%>
						<th>操作</th>
					</tr>
					</thead>
					<tbody id="prodInfo2">

					</tbody>
				</table>
			</c:if>
			</div>

		</div>

		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" maxlength="200" class="input-xlarge "/>
			</div>
		</div>
		<c:if test="${fns:getUser().isAdmin()}">
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
		</c:if>


		<div class="form-actions">

			<shiro:hasPermission name="biz:request:bizRequestHeader:edit">
				<c:forEach items="${fns:getUser().roleList}" var="role">
					<c:if test="${role.enname==RoleEnNameEnum.P_CENTER_MANAGER.state}">
						<c:set var="flag" value="true"/>
					</c:if>
				</c:forEach>
				<c:if test="${flag && entity.str=='detail' && entity.bizStatus==ReqHeaderStatusEnum.APPROVE.state}">
					<input id="btnCheckF" class="btn btn-primary" onclick="checkInfo(${ReqHeaderStatusEnum.UNREVIEWED.state},this.value)" type="button" value="审核驳回"/>&nbsp;
				</c:if>
				<c:if test="${flag && entity.str=='detail' && entity.bizStatus==ReqHeaderStatusEnum.UNREVIEWED.state}">
					<input id="btnCheckF" class="btn btn-primary" onclick="checkInfo(${ReqHeaderStatusEnum.UNREVIEWED.state},this.value)" type="button" value="审核驳回"/>&nbsp;
					<input id="btnCheck" class="btn btn-primary" onclick="checkInfo(${ReqHeaderStatusEnum.APPROVE.state},this.value)" type="button" value="审核通过"/>&nbsp;
				</c:if>
				<c:if test="${entity.str!='detail'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				</c:if>

			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>

	</form:form>
	<form:form id="searchForm" modelAttribute="bizSkuInfo" >
		<%--<form:hidden id="productNameCopy" path="productInfo.name"/>--%>
		<%--<form:hidden id="prodCodeCopy" path="productInfo.prodCode"/>--%>
		<form:hidden id="prodBrandNameCopy" path="productInfo.brandName"/>
		<form:hidden id="skuNameCopy" path="name"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>
		<input type="hidden" name="skuType" value="${SkuTypeEnum.OWN_PRODUCT.code}"/>
		<%--<form:hidden id="skuTypeCopy" path="skuType"/>--%>
	</form:form>
</body>
</html>
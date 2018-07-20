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
                var officeId = $("#officeId").val();
                if ($("#fromType2").prop("checked") == true && officeId == "") {
					alert("请先选择供应商");
					return false;
				}
                var prodBrandName=$("#prodBrandName").val();
                $("#prodBrandNameCopy").val(prodBrandName);
                var skuName=$("#skuName").val();
                $("#skuNameCopy").val(skuName);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                var itemNo = $("#itemNo").val();
                $("#itemNoCopy").val(itemNo);
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuList?productInfo.office.id="+officeId,
                    data:$('#searchForm').serialize(),
                    success:function (result) {
                        if(id==''){
                            $("#prodInfo2").empty();
                        }
                        var data = JSON.parse(result).data;
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
                                    tr_tds+= "<td rowspan='"+skuInfoList.length+"'><img src='"+prodUrl+"' width='100' height='100' ></td>" +

                                    "<td rowspan='"+skuInfoList.length+"'>"+brandName+"</td>";
								}
                                //tr_tds+= "<td><a href="+ "'${ctx}/sys/office/supplierForm?id=" + skuInfo.productInfo.office.id + "&gysFlag=onlySelect'>"+ skuInfo.productInfo.office.name + "</a></td>";
                                tr_tds+= "<td>"+ skuInfo.productInfo.office.name + "</td>";
                                tr_tds+= "<td>" + skuInfo.name+"</td><td>"+skuInfo.partNo+"</td><td>"+skuInfo.itemNo+"</td><td>"+skuInfo.buyPrice+"</td><td><input type='hidden' id='skuId_"+skuInfo.id+"' value='"+skuInfo.id+"'/><input class='input-mini' id='skuQty_"+skuInfo.id+"'   type='text'/></td>" ;
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

            $("#updateMoney").click(function () {
                updateMoney();
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
        	var valNmu = $(obj).attr("findg");
            var html = "<div style='padding:10px;'>输入驳回原因：<input type='text' id='remark2' name='remarkReject' value=''/>"+
            		"<span class='help-inline'><font color='red'>*</font></span></div>";
            var submit = function (v, h, f) {
                if (f.yourname == '') {
                    $.jBox.tip("请输入您的驳回原因", 'error', { focusId: "partNo" }); // 关闭设置 partNo 为焦点
                    return false;
                }
                if($("#remark2").val()!=null && $("#remark2").val()!=""){
					if (v === 'ok') {
						$.ajax({
							type:"post",
							url:"${ctx}/biz/request/bizRequestHeaderForVendor/saveInfo",
							data:{checkStatus:obj,id:$("#id").val(),remark:$("#remark").val(),remarkReject:$("#remark2").val()},
							success:function (data) {
								if(data){
									alert(val+"成功！");
									window.location.href="${ctx}/biz/request/bizRequestHeaderForVendor";
								}
							}
						})
					}
					return true;
                }else{
                	alert(val+"内容不能为空!");
                	checkInfo(obj,val);
                }
            };
            $.jBox(html, { title: "驳回原因", submit: submit });
        }
        function checkInfo2(obj,val) {
			$.ajax({
				type:"post",
				url:"${ctx}/biz/request/bizRequestHeaderForVendor/saveInfo",
				data:{checkStatus:obj,id:$("#id").val(),remarkReject:"adopt"},
				success:function (data) {
					if(data){
						alert(val+"成功！");
						window.location.href="${ctx}/biz/request/bizRequestHeaderForVendor";
					}
				}
			})
        }
        function updateMoney() {
            if(confirm("确定修改价钱吗？")){
                var skuPrice=$("#skuPrice").val();
                $.ajax({
                    type:"post",
                    url:" ${ctx}/biz/request/bizRequestDetail/saveRequestDetail",
                    data:{detailId:$("#detailId").val(),money:skuPrice},
                    success:function(flag){
                        if(flag=="ok"){
                            alert(" 修改成功 ");

                        }else{
                            alert(" 修改失败 ");
                        }
                    }
                });
            }
        }

        function checkPass() {
            top.$.jBox.confirm("确认审核通过吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    audit(1, '');
                }
            },{buttonsFocus:1});
        }
        function checkReject() {
            top.$.jBox.confirm("确认驳回该流程吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
                    var submit = function (v, h, f) {
                        if ($String.isNullOrBlank(f.description)) {
                            jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                            return false;
                        }
                        audit(2, f.description);
                        return true;
                    };

                    jBox(html, {
                        title: "请输入驳回理由:", submit: submit, loaded: function (h) {
                        }
                    });
                }
            },{buttonsFocus:1});
        }

        function audit(auditType, description) {
            var id = $("#id").val();
            var currentType = $("#currentType").val();
            $.ajax({
                url: '${ctx}/biz/request/bizRequestHeaderForVendor/audit',
                contentType: 'application/json',
                data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description},
                type: 'get',
                success: function (result) {
                    if(result == 'ok') {
                        alert("操作成功！");
                        window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor";
                    }else {
                        alert("操作失败！");
					}
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

	</script>
	<script type="text/javascript">
		$.ready(function () {
            if ($("#id").val() == null || $("#fromType").val() == 1) {
                $("#fromType1").prop("checked","checked");
            } else {
                $("#fromType2").prop("checked","checked");
            }
            if ($("#vendId").val() != "") {
                $("#vendor").removeAttr("style");
                deleteStyle();
            }
        });
		function deleteStyle() {
            $("#remark").removeAttr("style");
            $("#cardNumber").removeAttr("style");
            $("#payee").removeAttr("style");
            $("#bankName").removeAttr("style");
            $("#compact").removeAttr("style");
            $("#identityCard").removeAttr("style");
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
                    $("#remark").val(data.remark);
                }
			});
        }
        function deleteVendorStyle() {
            $("#vendor").removeAttr("style");
		}
		function addAllStyle() {
		    $("#remark").prop("style","display:none");
			$("#vendor").prop("style","display:none");
            $("#cardNumber").prop("style","display:none");
            $("#payee").prop("style","display:none");
            $("#bankName").prop("style","display:none");
            $("#compact").prop("style","display:none");
            $("#identityCard").prop("style","display:none");
        }
        function selectRemark() {
		    if ($("#remarkInput").val() == "") {
                alert("该厂商没有退换货流程");
			} else {
                alert($("#remarkInput").val());
			}
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/request/bizRequestHeaderForVendor/">备货清单列表</a></li>
		<li class="active"><a href="${ctx}/biz/request/bizRequestHeaderForVendor/form?id=${bizRequestHeader.id}">备货清单<shiro:hasPermission name="biz:request:bizRequestHeader:edit">${not empty bizRequestHeader.str?'详情':(not empty bizRequestHeader.id?'修改':'添加')}</shiro:hasPermission><shiro:lacksPermission name="biz:request:bizRequestHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizRequestHeader" action="${ctx}/biz/request/bizRequestHeaderForVendor/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="str" type="hidden"  value="${entity.str}"/>
		<input id="fromType" type="hidden" value="${entity.fromType}"/>
		<input id="vendId" type="hidden" value="${entity.bizVendInfo.id}"/>
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
				<input id="fromType1" type="radio" name="fromType" value="1" checked onclick="addAllStyle()"/>采购中心备货
				<input id="fromType2" type="radio" name="fromType" value="2" onclick="deleteVendorStyle()"/>供应商备货
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<sys:treeselect id="fromOffice" name="fromOffice.id" value="${entity.fromOffice.id}" labelName="fromOffice.name"
								labelValue="${entity.fromOffice.name}"  notAllowSelectParent="true"
								title="采购中心" isAll="false"  url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" cssClass="input-xlarge required" dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div id="vendor" class="control-group" style="display: none">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<sys:treeselect id="office" name="bizVendInfo.office.id" value="${entity.bizVendInfo.office.id}" labelName="bizVendInfo.office.name"
								labelValue="${entity.bizVendInfo.vendName}" notAllowSelectParent="true"
								title="供应商" url="/sys/office/queryTreeList?type=7" cssClass="input-medium required"
								allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息" onchange="deleteStyle()"/>
				<span class="help-inline"><font color="red">*</font> </span>
				<a href="#" id="remark" onclick="selectRemark()" style="display: none">《厂家退换货流程》</a>
			</div>
		</div>
		<div id="cardNumber" class="control-group" style="display: none">
			<label class="control-label">供应商卡号：</label>
			<div class="controls">
				<input id="cardNumberInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
							class="input-xlarge "/>
			</div>
		</div>
		<div id="payee" class="control-group" style="display: none">
			<label class="control-label">供应商收款人：</label>
			<div class="controls">
				<input id="payeeInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
							class="input-xlarge "/>
			</div>
		</div>
		<div id="bankName" class="control-group" style="display: none">
			<label class="control-label">供应商开户行：</label>
			<div class="controls">
				<input id="bankNameInput" readonly="readonly" value="" htmlEscape="false" maxlength="30"
							class="input-xlarge "/>
			</div>
		</div>
		<div id="compact" class="control-group" style="display: none">
			<label class="control-label">供应商合同：</label>
			<div id="compactImgs" class="controls">

			</div>
		</div>
		<div id="identityCard" class="control-group" style="display: none">
			<label class="control-label">供应商身份证：</label>
			<div id="identityCards" class="controls">

			</div>
		</div>
		<c:if test="${entity.str=='detail'}">
			<div class="control-group">
				<label class="control-label">应付金额：</label>
				<div class="controls">
					<input type="text" class="input-xlarge" value="${entity.totalMoney}" disabled="true"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">已收保证金：</label>
				<div class="controls">
					<input type="text" class="input-xlarge" value="${entity.recvTotal}" disabled="true"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">保证金比例：</label>
				<div class="controls">
					<fmt:formatNumber type="number" value="${entity.recvTotal*100/entity.totalMoney}" pattern="0.00" />%
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">期望收货时间：</label>
			<div class="controls">
				<input name="recvEta" type="text" readonly="readonly" maxlength="20" class="input-xlarge Wdate required"
					value="<fmt:formatDate value="${entity.recvEta}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<c:if test="${entity.str!='detail' && entity.str!='audit' }">

		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
				<ul class="inline ul-form">
					<li><label>品牌名称：</label>
						<input id="prodBrandName" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
					</li>
					<li><label>商品名称：</label>
						<input id="skuName"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
					</li>
					<li><label>商品编码：</label>
						<input id="skuCode"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
					</li>
					<li><label>商品货号：</label>
						<input id="itemNo"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
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
					<th>供应商</th>
					<th>商品名称</th>
					<th>商品编码</th>
					<th>商品货号</th>
					<%--<th>商品属性</th>--%>
					<th>价格</th>
					<th>申报数量</th>

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
					<shiro:hasPermission name="biz:request:bizRequestDetail:edit">
						<c:if test="${entity.str!='detail' && entity.str!='audit' }">
							<th>操作</th>
						</c:if>

					</shiro:hasPermission>

				</tr>
				</thead>
				<tbody id="prodInfo">
				<c:if test="${reqDetailList!=null}">
					<c:forEach items="${reqDetailList}" var="reqDetail" varStatus="reqStatus">
						<tr class="${reqDetail.skuInfo.productInfo.id}" id="${reqDetail.id}">
							<td><img src="${reqDetail.skuInfo.productInfo.imgUrl}" width="100" height="100" /></td>
							<td>${reqDetail.skuInfo.productInfo.brandName}</td>
							<td><a href="${ctx}/sys/office/supplierForm?id=${reqDetail.skuInfo.productInfo.office.id}&gysFlag=onlySelect">
									${reqDetail.skuInfo.productInfo.office.name}</a></td>
							<td>${reqDetail.skuInfo.name}</td>
							<td>${reqDetail.skuInfo.partNo}</td>
							<td>${reqDetail.skuInfo.itemNo}</td>
							<%--<td>${reqDetail.skuInfo.skuPropertyInfos}</td>--%>
							<td style="white-space: nowrap">
								<%--<c:choose>--%>
									<%--<c:when test="${flag &&entity.str!='detail'&& entity.bizStatus==ReqHeaderStatusEnum.UNREVIEWED.state}">--%>
									<%--<span style="float: left">--%>
										<%--<input type="text"  class="input-mini" id="skuPrice" value="${reqDetail.unitPrice}"/>--%>
										<%--<a href="#"  id="updateMoney" class="icon-ok-circle"></a>--%>
									<%--</span>--%>
										<%--<input type="hidden"  id="detailId" value="${reqDetail.id}"/>--%>
									<%--</c:when>--%>
									<%--<c:otherwise>--%>
										<%--${reqDetail.unitPrice}--%>
									<%--</c:otherwise>--%>
								<%--</c:choose>--%>

									${reqDetail.unitPrice}
							</td>
							<td>
								<input  type='hidden' name='reqDetailIds' value='${reqDetail.id}'/>
								<input type='hidden' name='skuInfoIds' value='${reqDetail.skuInfo.id}'/>
								<input  type='hidden' name='lineNos' value='${reqDetail.lineNo}'/>
								<input name='reqQtys'  value="${reqDetail.reqQty}" class="input-mini" type='text'/>
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

							<shiro:hasPermission name="biz:request:bizRequestDetail:edit">
								<c:if test="${entity.str!='detail'&& entity.str!='audit'}">
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
			<c:if test="${entity.str!='detail' && entity.str!='audit'}">
			<table id="contentTable2" style="width:48%;float: right;background-color:#abcceb;" class="table table-bordered table-condensed">
					<thead>
					<tr>
						<th>产品图片</th>
							<%--<th>产品名称</th>--%>
							<%--<th>产品分类</th>--%>
						<th>品牌名称</th>
						<th>供应商</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>商品货号</th>
						<%--<th>商品属性</th>--%>
						<th>工厂价</th>
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
		<form>
			<div>
				<label class="control-label">销售单号</label>
				<div class="controls">
					<table>
						<thead>
							<th>订单号</th>
							<th>创建日期</th>
							<th>金额(订单总金额)</th>
						</thead>
						<tbody>
						<c:forEach items="${orderHeaderList}" var="orderHeader">
							<tr>
								<td>${orderHeader.orderNum}</td>
								<td><fmt:formatDate value="${orderHeader.createDate}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
								<td>${orderHeader.detailPrice}(${orderHeader.totalDetail})</td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</form>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" maxlength="200" class="input-xlarge "/>
			</div>
		</div>

		<c:if test="${entity.str == 'audit'}">
			<div class="control-group">
				<label class="control-label">审核状态：</label>
				<div class="controls">
					<input type="text" disabled="disabled"
						   value="${requestOrderProcess.name}" htmlEscape="false"
						   maxlength="30" class="input-xlarge "/>
					<input id="currentType" type="hidden" disabled="disabled"
						   value="${requestOrderProcess.code}" htmlEscape="false"
						   maxlength="30" class="input-xlarge "/>
				</div>
			</div>
		</c:if>
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
		<c:if test="${fn:length(statusList) > 0}">
			<div class="control-group">
				<label class="control-label">状态流程：</label>
				<div class="controls help_wrap">
					<div class="help_step_box fa">
						<c:forEach items="${statusList}" var="v" varStatus="stat">
							<c:if test="${!stat.last}" >
								<div class="help_step_item">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									处理人:${v.createBy.name}<br/><br/>
									状态:${statusMap[v.bizStatus].desc}<br/>
									<fmt:formatDate value="${v.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
									<div class="help_step_right"></div>
								</div>
							</c:if>
							<c:if test="${stat.last}">
								<div class="help_step_item help_step_set">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									处理人:${v.createBy.name}<br/><br/>
									状态:${statusMap[v.bizStatus].desc}<br/>
									<fmt:formatDate value="${v.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
									<div class="help_step_right"></div>
								</div>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</div>
		</c:if>

		<div class="form-actions">

			<shiro:hasPermission name="biz:request:bizRequestHeader:edit">

				<c:if test="${entity.str == 'audit'}">
					<input id="btnSubmit" type="button" onclick="checkPass()" class="btn btn-primary" value="审核通过"/>
					<input id="btnSubmit" type="button" onclick="checkReject()" class="btn btn-primary" value="审核驳回"/>
				</c:if>

				<%--<c:if test="${flag && entity.str=='detail' && entity.bizStatus==ReqHeaderStatusEnum.APPROVE.state}">--%>
					<%--<input id="btnCheckF" class="btn btn-primary" onclick="checkInfo(${ReqHeaderStatusEnum.UNREVIEWED.state},this.value)" type="button" value="审核驳回"/>&nbsp;--%>
				<%--</c:if>--%>
				<%--<c:if test="${flag && entity.str=='detail' && entity.bizStatus==ReqHeaderStatusEnum.UNREVIEWED.state}">--%>

					<%--<input id="btnCheckF" class="btn btn-primary" onclick="checkInfo(${ReqHeaderStatusEnum.UNREVIEWED.state},this.value)" type="button" value="审核驳回"/>&nbsp;--%>
					<%--<input id="btnCheck" class="btn btn-primary" onclick="checkInfo2(${ReqHeaderStatusEnum.APPROVE.state},this.value)" type="button" value="审核通过"/>&nbsp;--%>
				<%--</c:if>--%>
				<c:if test="${entity.str!='detail' && entity.str!='audit'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				</c:if>

			</shiro:hasPermission>
			<c:if test="${not empty bizRequestHeader.str && bizRequestHeader.str eq 'detail'}">
				<input onclick="window.print();" type="button" class="btn btn-primary" value="打印备货清单" style="background:#F78181;"/>
				&nbsp;&nbsp;&nbsp;
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/>
		</div>
	</form:form>
	<form:form id="searchForm" modelAttribute="bizSkuInfo" >
		<%--<form:hidden id="productNameCopy" path="productInfo.name"/>--%>
		<%--<form:hidden id="prodCodeCopy" path="productInfo.prodCode"/>--%>
		<form:hidden id="prodBrandNameCopy" path="productInfo.brandName"/>
		<form:hidden id="skuNameCopy" path="name"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>
		<form:hidden id="itemNoCopy" path="itemNo"/>
		<input type="hidden" name="skuType" value="${SkuTypeEnum.OWN_PRODUCT.code}"/>
		<%--<form:hidden id="skuTypeCopy" path="skuType"/>--%>
	</form:form>
</body>
</html>
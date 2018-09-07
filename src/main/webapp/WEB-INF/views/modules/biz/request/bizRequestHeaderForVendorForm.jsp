<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.RoleEnNameEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqFromTypeEnum" %>


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
            if ($("#id").val() == '' || $("#fromType").val() == 1) {
                $("#fromType1").prop("checked","checked");
            } else {
                $("#fromType2").prop("checked","checked");
            }
            if ($("#vendId").val() != "") {
                $("#vendor").removeAttr("style");
                deleteStyle();
            }
            if (str == 'audit' || str == 'startAudit' || str == 'pay' || str =='createPay') {
                $("input[name='fromType']").each(function () {
					$(this).attr("disabled","disabled");
                });
			}
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
                //if ($("#fromType2").prop("checked") == true && officeId == "") {
				if (officeId == "") {
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

        $(function(){
            if('${entity.str=='detail'}'){
                var poheaderId = "${entity.bizPoHeader.id}";
                if (poheaderId == null || poheaderId == "") {
                    $("#schedulingType").val("未排产")
                    $("#stockGoods").hide();
                    $("#schedulingPlan_forHeader").hide();
                    $("#schedulingPlan_forSku").hide();
                }
                if (poheaderId != null && poheaderId != "") {
                    getScheduling(poheaderId);
                }

            }
        })

        function getScheduling(poheaderId) {
            $.ajax({
                type:"post",
                url:"${ctx}/biz/po/bizPoHeader/scheduling4Mobile",
                data: {"id": poheaderId},
                dataType:'json',
                success:function(result){
                    var data = result['data'];
                    var detailHeaderFlg = data['detailHeaderFlg'];
                    var detailSchedulingFlg = data['detailSchedulingFlg'];
                    if (detailHeaderFlg != true && detailSchedulingFlg != true) {
                        $("#schedulingType").val("未排产")
                        $("#stockGoods").hide();
                        $("#schedulingPlan_forHeader").hide();
                        $("#schedulingPlan_forSku").hide();
                    }

                    if (detailHeaderFlg == true) {
                        $("#schedulingType").val("按订单排产")
                        $("#stockGoods").show();
                        $("#schedulingPlan_forHeader").show();
                        $("#schedulingPlan_forSku").hide();

                        var bizPoHeader = data['bizPoHeader'];
                        var poDetailList = bizPoHeader['poDetailList'];
                        var poDetailHtml = ""
                        for (var i=0; i<poDetailList.length; i++) {
                            var poDetail = poDetailList[i];
                            poDetailHtml += "<tr>";
                            poDetailHtml += "<td><img style='max-width: 120px' src='" + poDetail.skuInfo.productInfo.imgUrl + "'/></td>";
                            poDetailHtml += "<td>" + poDetail.skuInfo.productInfo.brandName + "</td>";
                            poDetailHtml += "<td>" + poDetail.skuInfo.name + "</td>";
                            poDetailHtml += "<td>" + poDetail.skuInfo.itemNo + "</td>";
                            poDetailHtml += "<td>" + poDetail.ordQty + "</td>";
                            poDetailHtml += "<td>" + poDetail.unitPrice + "</td>";
                            poDetailHtml += "<td>" + poDetail.ordQty * poDetail.unitPrice + "</td>";
                            poDetailHtml += "</tr>";
                        }

                        var prodInfoSchedu = $("#prodInfoSchedu");
                        prodInfoSchedu.append(poDetailHtml);

                        var bizCompletePalns = data['bizCompletePalns'];
                        var schedulingHeaderHtml = "";
                        for (var i=0; i<bizCompletePalns.length; i++) {
                            var bizCompletePaln = bizCompletePalns[i];
                            var dateTime = formatDate(bizCompletePaln.planDate);

                            schedulingHeaderHtml += "<tr><td><div><label>完成日期：</label>";
                            schedulingHeaderHtml += "<input type='text' maxlength='20' class='input-medium Wdate' readonly='readonly' " + "value='" + dateTime + "'/>" + '&nbsp;';
                            schedulingHeaderHtml += "<label>排产数量：</label>";
                            schedulingHeaderHtml += "<input class='input-medium' type='text' readonly='readonly'";
                            schedulingHeaderHtml += "value='" + bizCompletePaln.completeNum + "' maxlength='30'/>";
                            schedulingHeaderHtml += "</div></td></tr>";
                        }
                        var schedulingForHeader = $("#schedulingForHeader");
                        schedulingForHeader.append(schedulingHeaderHtml);

                        var remarkHtml = "<textarea id='schRemarkOrder' maxlength='200' class='input-xlarge '>" + bizPoHeader.bizSchedulingPlan.remark + "</textarea>";
                        var schedulingHeaderRemark = $("#schedulingHeaderRemark");
                        schedulingHeaderRemark.append(remarkHtml);
                    }
                    if (detailSchedulingFlg == true) {
                        $("#schedulingType").val("按商品排产")
                        $("#stockGoods").hide();
                        $("#schedulingPlan_forHeader").hide();
                        $("#schedulingPlan_forSku").show();

                        var bizPoHeader = data['bizPoHeader'];
                        var poDetailList = bizPoHeader['poDetailList'];

                        var poDetailHtml = ""
                        var prodInfo2Schedu = $("#prodInfo2Schedu");
                        for (var i=0; i<poDetailList.length; i++) {
                            var poDetail = poDetailList[i];
                            poDetailHtml += "<tr>";
                            poDetailHtml += "<td><img style='max-width: 120px' src='" + poDetail.skuInfo.productInfo.imgUrl + "'/></td>";
                            poDetailHtml += "<td>" + poDetail.skuInfo.productInfo.brandName + "</td>";
                            poDetailHtml += "<td>" + poDetail.skuInfo.name + "</td>";
                            poDetailHtml += "<td>" + poDetail.skuInfo.itemNo + "</td>";
                            poDetailHtml += "<td>" + poDetail.ordQty + "</td>";
                            poDetailHtml += "<td>" + poDetail.unitPrice + "</td>";
                            poDetailHtml += "<td>" + poDetail.ordQty * poDetail.unitPrice + "</td>";
                            poDetailHtml += "</tr>";

                            poDetailHtml += "<tr><td colspan='7'><label>排产记录：</label></td></tr>";

                            var completePalnHtml = "";
                            var completePalnList = poDetail.bizSchedulingPlan.completePalnList;
                            for (var j=0; j<completePalnList.length; j++) {
                                var completePaln = completePalnList[j];
                                var dateTime = formatDate(completePaln.planDate);

                                completePalnHtml += "<tr><td colspan='7'><div><label>完成日期：</label>";
                                completePalnHtml += "<input type='text' maxlength='20' class='input-medium Wdate' readonly='readonly' " + "value='" + dateTime + "'/>" + '&nbsp;';
                                completePalnHtml += "<label>排产数量：</label>";
                                completePalnHtml += "<input class='input-medium' readonly='readonly' value='" + completePaln.completeNum + "' type='text' maxlength='30'";
                                completePalnHtml += "</div></td></tr>";
                            }
                            poDetailHtml += completePalnHtml;
                            prodInfo2Schedu.append(poDetailHtml)
                        }

                        var schedulingDetailRemarkHtml = "<tr><td colspan='7'><div><label>排产备注：</label>";
                        schedulingDetailRemarkHtml += "<textarea id='schRemarkOrder' maxlength='200' class='input-xlarge '>" + bizPoHeader.bizSchedulingPlan.remark + "</textarea>";
                        schedulingDetailRemarkHtml += "</div></td></tr>"
                        prodInfo2Schedu.append(schedulingDetailRemarkHtml);
                    }
                }
            });
        }

        function formatDate(jsonDate) {
            //json日期格式转换为正常格式
            var jsonDateStr = jsonDate.toString();
            try {
                var date = new Date(parseInt(jsonDateStr.replace("/Date(", "").replace(")/", ""), 10));
                var month = date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : date.getMonth() + 1;
                var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
                var hours = date.getHours();
                var minutes = date.getMinutes();
                var seconds = date.getSeconds();
                var milliseconds = date.getMilliseconds()/1000;
                //return date.getFullYear() + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds + "." + milliseconds;//年月日时分秒
                return date.getFullYear() + "-" + month + "-" + day;
                //return date.getFullYear() + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;//年月日时分秒
            } catch (ex) {
                return "时间格式转换错误";
            }
        }


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

        function checkPass(obj) {
            if('${entity.bizStatus == ReqHeaderStatusEnum.IN_REVIEW.state}'){
                var lastPayDateVal = $("#lastPayDate").val();
                if (lastPayDateVal == ""){
                    alert("请输入最后付款时间！")
					return false;
                }
            }
			top.$.jBox.confirm("确认审核通过吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
					var submit = function (v, h, f) {
						if ($String.isNullOrBlank(f.description)) {
							jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
							return false;
						}
                        if (obj == "RE") {
                            audit(1, f.description);
                        }
                        if (obj == "PO") {
                            poAudit(1,f.description);
                        }
						return true;
					};
					jBox(html, {
						title: "请输入通过理由:", submit: submit, loaded: function (h) {
						}
					});
				}
			},{buttonsFocus:1});
        }
        function checkReject(obj) {
            top.$.jBox.confirm("确认驳回该流程吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
                    var submit = function (v, h, f) {
                        if ($String.isNullOrBlank(f.description)) {
                            jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                            return false;
                        }
                        if (obj == "RE") {
                            audit(2, f.description);
						}
                        if (obj == "PO") {
                            poAudit(2,f.description);
						}
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
            var createPo = $("#createPo").val();
            $.ajax({
                url: '${ctx}/biz/request/bizRequestHeaderForVendor/audit',
                contentType: 'application/json',
                data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description},
                type: 'get',
                async: false,
                success: function (result) {
                    if(result == 'ok') {
                        alert("操作成功！");
                        if(auditType==1){
                            //自动生成采购单
                            var id = $("#id").val();
                            var bizStatus = getCurrentBizStatus(id);
                            <%--if ('${ReqHeaderStatusEnum.APPROVE.state}' == bizStatus) {--%>
                            if (createPo == 'yes') {
                                getPoHeaderPara(id);
                            }
                        }
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

        function poAudit(auditType, description) {
            var id = $("#poHeaderId").val();
            var currentType = $("#poCurrentType").val();
            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/audit',
                contentType: 'application/json',
                data: {"id": id, "currentType": currentType, "auditType": auditType, "description": description, "fromPage": "requestHeader"},
                type: 'get',
                success: function (result) {
                    result = JSON.parse(result);
                    if(result.ret == true || result.ret == 'true') {
                        alert('操作成功!');
                        <%--window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor";--%>
                        window.location.href = "${ctx}/biz/po/bizPoHeader/listV2";
                    }else {
                        alert(result.errmsg);
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function getCurrentBizStatus(id) {
		    var bizStatus = "";
            $.ajax({
                url: '${ctx}/biz/request/bizRequestHeaderForVendor/getCurrentBizStatus',
                contentType: 'application/json',
                data: {"id": id},
                type: 'get',
                dataType: 'json',
                async: false,
                success: function (result) {
                    bizStatus = result;
                },
                error: function (error) {
                    console.info(error);
                }
            });
            return bizStatus;
        }

        function getPoHeaderPara(id) {
            $.ajax({
                url: '${ctx}/biz/request/bizRequestOrder/goListAutoSave',
                contentType: 'application/json',
                data: {"orderId": id, "type": "1"},
                type: 'get',
                dataType: 'json',
                async: false,
                success: function (result) {
                    var reqDetailIds = result['unitPrices'];
                    if (reqDetailIds == "") {
                        alert("价钱不能为空！");
                        return;
                    }
                    savePoHeader(result);
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function savePoHeader(result) {
            var reqDetailIds = result['reqDetailIds'];
            var vendorId = result['vendorId'];
            var unitPrices = result['unitPrices'];
            var ordQtys = result['ordQtys'];
            <!-- 最后付款时间 -->
            var lastPayDateVal = $("#lastPayDate").val();
            $.ajax({
                url: '${ctx}/biz/po/bizPoHeader/autoSave',
                contentType: 'application/json',
                data: {"reqDetailIds": reqDetailIds, "orderDetailIds": "", "vendorId":vendorId, "unitPrices":unitPrices, "ordQtys":ordQtys, "lastPayDateVal": lastPayDateVal},
                type: 'get',
                async: false,
                success: function (res) {
                    if (res == "ok") {

                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }

        function saveMon(type) {
            if (type == 'createPay') {
                var payTotal = $("#payTotal").val();
                var payDeadline = $("#payDeadline").val();
                var id = $("#poHeaderId").val();
                if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
                    alert("请输入申请金额!");
                    return false;
                }
                if ($String.isNullOrBlank(payDeadline)) {
                    alert("请选择本次申请付款时间!");
                    return false;
                }
            }

            $("#inputForm").attr("action", "${ctx}/biz/po/bizPoHeaderReq/savePoHeader?type=" + type + "&id=" + id + "&fromPage=requestHeader");
            $("#inputForm").submit();
        }

	</script>
	<script type="text/javascript">
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
				    if (data == null) {
				        return false;
                    }
                    $("#cardNumberInput").val(data.cardNumber);
                    $("#payeeInput").val(data.payee);
                    $("#bankNameInput").val(data.bankName);
                    if (data.compactImgList != undefined) {
                        $.each(data.compactImgList,function (index, compact) {
                            $("#compactImgs").append("<a href=\"" + compact.imgServer + compact.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + compact.imgServer + compact.imgPath + "\"></a>");
                        });
                    }
                    if (data.identityCardImgList != undefined) {
                        $.each(data.identityCardImgList,function (index, identity) {
                            $("#identityCards").append("<a href=\"" + identity.imgServer + identity.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + identity.imgServer + identity.imgPath + "\"></a>");
                        });
                    }
                    $("#remark").val(data.remarks);
                }
			});
        }
        function deleteVendorStyle() {
            //$("#vendor").removeAttr("style");
		}
		function addAllStyle() {
		    $("#remark").prop("style","display:none");
			//$("#vendor").prop("style","display:none");
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
		<input type="hidden" value="${entity.bizPoPaymentOrder.id}" id="paymentOrderId"/>
		<input id="poHeaderId" type="hidden" value="${entity.bizPoHeader.id}"/>
		<input id="str" type="hidden"  value="${entity.str}"/>
		<input id="fromType" type="hidden" value="${entity.fromType}"/>
		<input id="vendId" type="hidden" value="${entity.bizVendInfo.office.id}"/>
		<input id="remarkInput" type="hidden" value=""/>
		<input id="createPo" type="hidden" value="${createPo}"/>
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
					<input name="reqNo" value="${entity.reqNo}" class="input-xlarge" disabled="disabled"/>
					<span class="help-inline"><font color="red">*</font> </span>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">备货方：</label>
			<div class="controls">
				<input id="fromType1" type="radio" name="fromType" value="1" checked onclick="addAllStyle()"/>采购中心备货
				<input id="fromType2" type="radio" name="fromType" value="2" onclick="deleteVendorStyle()"/>厂商备货
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
		<div id="vendor" class="control-group" >
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
		<c:if test="${entity.bizPoPaymentOrder.id != null || entity.str == 'createPay'}">
			<div class="control-group">
				<label class="control-label">申请金额：</label>
				<div class="controls">
					<input id="payTotal" name="planPay" type="text"
						   <c:if test="${entity.str == 'audit' || entity.str == 'pay'}">readonly</c:if>
						   value="${entity.bizPoPaymentOrder.id != null ?
                           entity.bizPoPaymentOrder.total : (entity.totalDetail-(entity.bizPoHeader.payTotal == null ? 0 : entity.bizPoHeader.payTotal))}"
						   htmlEscape="false" maxlength="30" class="input-xlarge"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">本次申请付款时间：</label>
				<div class="controls">
					<input name="payDeadline" id="payDeadline" type="text" readonly="readonly" maxlength="20"
						   class="input-medium Wdate required"
						   value="<fmt:formatDate value="${entity.bizPoPaymentOrder.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
							<c:if test="${entity.str == 'createPay'}"> onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"</c:if>
						   placeholder="必填！"/>
				</div>
			</div>
		</c:if>
		<c:if test="${entity.str == 'startAudit'}">
			<%--<div class="control-group">--%>
				<%--<label class="control-label">是否同时提交支付申请：</label>--%>
				<%--<div class="controls">--%>
					<%--<input name="meanwhilePayOrder" id="meanwhilePayOrderRadioFalse" type="radio" onclick="showTimeTotal(false);" checked/>否--%>
					<%--<input name="meanwhilePayOrder" id="meanwhilePayOrderRadioTrue" type="radio" onclick="showTimeTotal(true);" />是--%>
				<%--</div>--%>
			<%--</div>--%>
			<%--<div class="control-group prewTimeTotal" style="display: none;">--%>
				<%--<label class="control-label">最后付款时间：</label>--%>
				<%--<div class="controls">--%>
					<%--<input name="prewPayDeadline" id="prewPayDeadline" type="text" readonly="readonly" maxlength="20"--%>
						   <%--class="input-medium Wdate required"--%>
						   <%--value="<fmt:formatDate value="${entity.bizPoPaymentOrder.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"--%>
						   <%--onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"--%>
						   <%--placeholder="必填！"/>--%>
				<%--</div>--%>
			<%--</div>--%>
			<%--<div class="control-group prewTimeTotal" style="display: none;">--%>
				<%--<label class="control-label">申请金额：</label>--%>
				<%--<div class="controls">--%>
					<%--<input name="prewPayTotal" id="prewPayTotal" type="text"--%>
						   <%--value="${entity.bizPoPaymentOrder.id != null ?--%>
                           <%--entity.bizPoPaymentOrder.total : (entity.totalDetail-entity.balanceTotal)}"--%>
						   <%--maxlength="20" placeholder="必填！"/>--%>
				<%--</div>--%>
			<%--</div>--%>
		</c:if>
		<c:if test="${entity.str == 'pay'}">
			<div class="control-group">
				<label class="control-label">实际付款金额：</label>
				<div class="controls">
					<input id="truePayTotal" name="payTotal" type="text"
						   value="${entity.bizPoHeader.bizPoPaymentOrder.payTotal}"
						   htmlEscape="false" maxlength="30" class="input-xlarge "/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">上传付款凭证：
					<p style="opacity: 0.5;">点击图片删除</p>
				</label>

				<div class="controls">
					<input class="btn" type="file" name="productImg" onchange="submitPic('payImg', true)" value="上传图片" multiple="multiple" id="payImg"/>
				</div>
				<div id="payImgDiv">
					<img src="${entity.bizPoHeader.bizPoPaymentOrder.img}" customInput="payImgImg" style='width: 100px' onclick="$(this).remove();">
				</div>
			</div>
		</c:if>
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
			<div class="control-group">
				<label class="control-label">已支付厂商保证金：</label>
				<div class="controls">
					<fmt:formatNumber type="number" value="${entity.bizPoHeader.payTotal}" pattern="0.00" />
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

		<shiro:hasPermission name="biz:request:bizRequestHeader:audit">
			<c:if test="${entity.str == 'audit'}">
				<c:if test="${entity.bizStatus == ReqHeaderStatusEnum.IN_REVIEW.state && createPo == 'yes'}">
					<div class="control-group">
						<label class="control-label">最后付款时间：</label>
						<div class="controls">
							<input name="lastPayDate" id="lastPayDate" type="text" readonly="readonly" maxlength="20"
								   class="input-medium Wdate required"
								   value="<fmt:formatDate value="${bizPoHeader.lastPayDate}"  pattern="yyyy-MM-dd"/>"
								   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
								   placeholder="必填！"/>
							<span class="help-inline"><font color="red">*</font></span>
						</div>
					</div>
				</c:if>
			</c:if>
		</shiro:hasPermission>

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
					<th>结算价</th>
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

					<shiro:hasPermission name="biz:request:bizRequestDetail:edit">
						<c:if test="${entity.str!='detail' && entity.str!='audit' && entity.str!='createPay' && entity.str!='pay'}">
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
							<td style="white-space: nowrap">
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
                            <%--<c:if test="${entity.str == 'audit' && entity.commonProcess.type == defaultProcessId}">--%>
                                <%--<c:forEach items="${reqDetail.invSkuMap}" var="stockQty">--%>
                                    <%--<td>${stockQty.value}</td>--%>
                                <%--</c:forEach>--%>
                            <%--</c:if>--%>

							<shiro:hasPermission name="biz:request:bizRequestDetail:edit">
								<c:if test="${entity.str!='detail' && entity.str!='audit' && entity.str!='createPay' && entity.str!='pay' }">
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
						<th>结算价</th>
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
		<c:if test="${entity.str == 'audit' && entity.commonProcess.type == defaultProcessId}">
		<div class="control-group">
			<label class="control-label">商品库存：</label>
			<div class="controls">
				<table id="contentTable" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>库存类型</th>
						<th>仓库名称</th>
						<th>采购中心</th>
						<th>商品名称</th>
						<th>商品货号</th>
						<th>商品数量</th>
					</tr>
					</thead>
					<tbody>
					<c:if test="${inventorySkuList!=null}">
						<c:forEach items="${inventorySkuList}" var="bizInventorySku" varStatus="reqStatus">
							<tr>
								<td>${fns:getDictLabel(bizInventorySku.invType, 'inv_type', '未知状态')}</td>
								<td>${bizInventorySku.invInfo.name}</td>
								<td><font color="red">${bizInventorySku.customer.name}</font></td>
								<td>${bizInventorySku.skuInfo.name}</td>
								<td><font color="red">${bizInventorySku.skuInfo.itemNo}</font></td>
								<td><font color="red">${bizInventorySku.stockQty}</font></td>
							</tr>
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
							<th>结算价</th>
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
		</c:if>
		<c:if test="${entity.str eq 'detail' && entity.fromType eq ReqFromTypeEnum.VENDOR_TYPE.type}">
			<div>
				<label class="control-label">销售单号</label>
				<div class="controls">
					<table class="table table-bordered table-condensed">
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
		</c:if>
        <c:if test="${paymentOrderList != null && fn:length(paymentOrderList) > 0}">
            <div class="control-group">
                <label class="control-label">支付列表：</label>
                <div class="controls">
                    <table class="table table-bordered table-striped">
                        <thead>
                            <tr>
                                <th>付款金额</th>
                                <th>实际付款金额</th>
                                <th>最后付款时间</th>
                                <th>实际付款时间</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${paymentOrderList}" var="paymentOrder">
                                <tr>
                                    <td>${paymentOrder.total}</td>
                                    <td>${paymentOrder.payTotal}</td>
                                    <td><fmt:formatDate value="${paymentOrder.deadline}" pattern="yy-MM-dd HH:mm:ss"/></td>
                                    <td><fmt:formatDate value="${paymentOrder.payTime}" pattern="yy-MM-dd HH:mm:ss"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" maxlength="200" class="input-xlarge "/>
			</div>
		</div>
		<c:if test="${entity.str == 'audit' && entity.processPo != 'processPo'}">
			<div class="control-group">
				<label class="control-label">审核状态：</label>
				<div class="controls">
                    <c:if test="${requestOrderProcess.name != '审核完成'}">
                        <input type="text" disabled="disabled"
                               value="${requestOrderProcess.name}" htmlEscape="false"
                               maxlength="30" class="input-xlarge "/>
                    </c:if>
                    <c:if test="${requestOrderProcess.name == '审核完成'}">
                        <input type="text" disabled="disabled"
                               value="订单支出信息审核" htmlEscape="false"
                               maxlength="30" class="input-xlarge "/>
                    </c:if>
					<input id="currentType" type="hidden" disabled="disabled"
						   value="${requestOrderProcess.code}" htmlEscape="false"
						   maxlength="30" class="input-xlarge "/>
				</div>
			</div>
		</c:if>
		<c:if test="${entity.str == 'audit' && entity.bizPoHeader.commonProcessList != null && fn:length(entity.bizPoHeader.commonProcessList) > 0 && entity.processPo == 'processPo'}">
			<div class="control-group">
				<label class="control-label">审核状态：</label>
				<div class="controls">
					<input type="text" disabled="disabled"
						   value="${purchaseOrderProcess.name}" htmlEscape="false"
						   maxlength="30" class="input-xlarge "/>
					<input id="poCurrentType" type="hidden" disabled="disabled"
						   value="${purchaseOrderProcess.code}" htmlEscape="false"
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
		<c:if test="${entity.id != null}">
			<div class="control-group">
				<label class="control-label">排产状态：</label>
				<div class="controls">
					${fns:getDictLabel(poSchType, 'poSchType', '未排产')}
				</div>
			</div>
		</c:if>


		<!-- 详情页面显示排产信息 -->
		<div class="control-group">
			<label class="control-label">排产类型：</label>
			<div class="controls">
				<input type="text" readonly="readonly" id="schedulingType" value="" pattern="0.00"/>
			</div>
		</div>
		<c:if test="${entity.str=='detail'}">
			<div class="control-group" id="stockGoods" >
				<label class="control-label">采购商品：</label>
				<div class="controls">
					<table style="width:60%;float:left" class="table table-striped table-bordered table-condensed">
						<thead>
						<tr>
							<th>产品图片</th>
							<th>品牌名称</th>
							<th>商品名称</th>
							<th>商品货号</th>
							<th>采购数量</th>
							<th>结算价</th>
							<th>总金额</th>
						</tr>
						</thead>
						<tbody id="prodInfoSchedu">

						</tbody>
					</table>
				</div>
			</div>

			<div class="control-group" id="schedulingPlan_forHeader" >
				<label class="control-label">按订单排产：</label>
				<div class="controls">
					<table id="schedulingForHeader" style="width:60%;float:left" class="table table-striped table-bordered table-condensed">
						<tr id="schedulingHeader">
							<td>
								<label>排产记录：</label>
							</td>
						</tr>
					</table>
					<table style="width:60%;float:left" class="table table-striped table-bordered table-condensed">
						<tr>
							<td>
								<div id="schedulingHeaderRemark">
									<label>排产备注：</label>

								</div>
							</td>
						</tr>
					</table>
				</div>
			</div>

			<div class="control-group" id="schedulingPlan_forSku" >
				<label class="control-label">按商品排产：</label>
				<div class="controls">
					<table id="" style="width:60%;float:left" class="table table-striped table-bordered table-condensed">
						<thead>
						<tr>
							<th>产品图片</th>
							<th>品牌名称</th>
							<th>商品名称</th>
							<th>商品货号</th>
							<th>采购数量</th>
							<th>结算价</th>
							<th>总金额</th>
						</tr>
						</thead>
						<tbody id="prodInfo2Schedu">
						<c:forEach items="${bizPoHeader.poDetailList}" var="poDetail" varStatus="state">
							<tr>
								<td colspan="7">
									<table id="schedulingForDetail_${poDetail.id}" style="width:100%;float:left" class="table table-striped table-bordered table-condensed">

									</table>
								</td>
							</tr>
						</c:forEach>

						</tbody>
					</table>
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
		<c:if test="${fn:length(entity.commonProcessList) > 0}">
			<div class="control-group">
				<label class="control-label">审批流程：</label>
				<div class="controls help_wrap">
					<div class="help_step_box fa">
						<c:forEach items="${entity.commonProcessList}" var="v" varStatus="stat">
							<c:if test="${!stat.last}" >
								<div class="help_step_item">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
									批注:${v.description}<br/><br/>
									审批人:${v.user.name}<br/>
									<fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
									<div class="help_step_right"></div>
								</div>
							</c:if>
							<c:if test="${stat.last && entity.processPo != 'processPo' && v.requestOrderProcess.name != '审核完成'}">
								<div class="help_step_item help_step_set">
									<div class="help_step_left"></div>
									<div class="help_step_num">${stat.index + 1}</div>
                                    <c:if test="${v.requestOrderProcess.name != '审核完成'}">
									    当前状态:${v.requestOrderProcess.name}<br/><br/>
                                    </c:if>
                                    <%--<c:if test="${v.requestOrderProcess.name == '审核完成'}">--%>
                                        <%--当前状态:订单支出信息审核<br/><br/>--%>
                                    <%--</c:if>--%>
										${v.user.name}<br/>
									<div class="help_step_right"></div>
								</div>
							</c:if>
						</c:forEach>
					</div>
				</div>
			</div>
            <div class="controls help_wrap">
                <div class="help_step_box fa">
                    <c:forEach items="${entity.bizPoHeader.commonProcessList}" var="v" varStatus="stat">
                        <c:if test="${stat.first && !stat.last}" >
                            <div class="help_step_item">
                                <div class="help_step_left"></div>
                                <div class="help_step_num">${fn:length(entity.commonProcessList)}</div>
                                批注:${v.description}<br/><br/>
                                审批人:${v.user.name}<br/>
                                <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                <div class="help_step_right"></div>
                            </div>
                        </c:if>
                        <c:if test="${!stat.last && !stat.first}" >
                            <div class="help_step_item">
                                <div class="help_step_left"></div>
                                <div class="help_step_num">${fn:length(entity.commonProcessList) + stat.index}</div>
                                批注:${v.description}<br/><br/>
                                审批人:${v.user.name}<br/>
                                <fmt:formatDate value="${v.updateTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                <div class="help_step_right"></div>
                            </div>
                        </c:if>
                        <c:if test="${stat.last}">
                            <div class="help_step_item help_step_set">
                                <div class="help_step_left"></div>
                                <div class="help_step_num">${fn:length(entity.commonProcessList) + stat.index}</div>
                                当前状态:${v.purchaseOrderProcess.name}<br/><br/>
                                    ${v.user.name}<br/>
                                <div class="help_step_right"></div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
		</c:if>

		<div class="form-actions">

			<shiro:hasPermission name="biz:request:bizRequestHeader:audit">
				<%--<c:if test="${entity.str == 'startAudit'}">--%>
					<%--<input id="btnSubmit" type="button" onclick="startAudit()" class="btn btn-primary" value="审核通过"/>--%>
					<%--<input id="btnSubmit" type="button" onclick="startRejectAudit()" class="btn btn-primary" value="驳回"/>--%>
				<%--</c:if>--%>
				<c:if test="${entity.str == 'audit'}">
					<c:if test="${entiry.commonProcess.type != autProcessId && entity.processPo != 'processPo'}">
						<input id="btnSubmit" type="button" onclick="checkPass('RE')" class="btn btn-primary" value="审核通过"/>
						<input id="btnSubmit" type="button" onclick="checkReject('RE')" class="btn btn-primary" value="审核驳回"/>
					</c:if>
				</c:if>
				<c:if test="${entity.str == 'pay'}">
					<input id="btnSubmit" type="button" onclick="pay()" class="btn btn-primary" value="确认支付"/>
				</c:if>
				<c:if test="${entity.str == 'createPay'}">
					<input id="btnSubmit" type="button" onclick="saveMon('createPay')" class="btn btn-primary" value="申请付款"/>
				</c:if>
			</shiro:hasPermission>

			<!-- 一单到底，采购单审核 -->
			<shiro:hasPermission name="biz:po:bizPoHeader:audit">
				<c:if test="${entity.str == 'audit'}">
					<c:if test="${entity.bizPoHeader.commonProcessList != null && fn:length(entity.bizPoHeader.commonProcessList) > 0 && entity.processPo == 'processPo'}">
						<input id="btnSubmit" type="button" onclick="checkPass('PO')" class="btn btn-primary" value="审核通过"/>
						<input id="btnSubmit" type="button" onclick="checkReject('PO')" class="btn btn-primary" value="审核驳回"/>
					</c:if>
				</c:if>
			</shiro:hasPermission>

			<shiro:hasPermission name="biz:request:bizRequestHeader:edit">
				<c:if test="${entity.str!='detail' && entity.str!='audit' && entity.str != 'startAudit' && entity.str != 'createPay' && entity.str != 'pay'}">
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
<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
<script type="text/javascript">

    function startAudit() {
        var prew = false;
        var prewPayTotal = $("#prewPayTotal").val();
        var prewPayDeadline = $("#prewPayDeadline").val();
        if ($("#meanwhilePayOrderRadioTrue").attr("checked") == "checked") {
            if ($String.isNullOrBlank(prewPayTotal)) {
                alert("请输入申请金额");
                return false;
            }
            if ($String.isNullOrBlank(prewPayDeadline)) {
                alert("请选择日期");
                return false;
            }
            prew = true;
        }
        var html = "<div style='padding:10px;'>通过理由：<input type='text' id='description' name='description' value='' /></div>";
        var submit = function (v, h, f) {
            if ($String.isNullOrBlank(f.description)) {
                jBox.tip("请输入通过理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                return false;
            }
            top.$.jBox.confirm("确认开始后续审核流程吗？", "系统提示", function (v1, h1, f1) {
                if (v1 == "ok") {
                    var id = $("#id").val();
                    $.ajax({
                        url: '${ctx}/biz/request/bizRequestHeaderForVendor/startAudit',
                        contentType: 'application/json',
                        data: {
                            "id": id,
                            "prew": prew,
                            "prewPayTotal": prewPayTotal,
                            "prewPayDeadline": prewPayDeadline,
							"auditType": 1,
                            "desc": f.description
                        },
                        type: 'get',
                        success: function (result) {
                            result = JSON.parse(result);
                            if(result.ret == true || result.ret == 'true') {
                                alert('操作成功!');
                                window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor";
                            }else {
                                alert(result.errmsg);
                            }
                        },
                        error: function (error) {
                            console.info(error);
                        }
                    });
                }
            }, {buttonsFocus: 1});
            return true;
        };

        jBox(html, {
            title: "请输入通过理由:", submit: submit, loaded: function (h) {
            }
        });


    }

    function startRejectAudit() {
        top.$.jBox.confirm("确认驳回流程吗？","系统提示",function(v,h,f){
            if(v=="ok"){
                var html = "<div style='padding:10px;'>驳回理由：<input type='text' id='description' name='description' value='' /></div>";
                var submit = function (v, h, f) {
                    if ($String.isNullOrBlank(f.description)) {
                        jBox.tip("请输入驳回理由!", 'error', {focusId: "description"}); // 关闭设置 yourname 为焦点
                        return false;
                    }
                    var id = $("#id").val();
                    var prew = false;
                    $.ajax({
                        url: '${ctx}/biz/request/bizRequestHeaderForVendor/startAudit',
                        contentType: 'application/json',
                        data: {"id": id, "prew":prew,  "auditType":2, "desc": f.description},
                        type: 'get',
                        success: function (result) {
                            if(result == '操作成功!') {
                                window.location.href = "${ctx}/biz/request/bizRequestHeaderForVendor";
                            }
                        },
                        error: function (error) {
                            console.info(error);
                        }
                    });
                    return true;
                };

                jBox(html, {
                    title: "请输入驳回理由:", submit: submit, loaded: function (h) {
                    }
                });
            }
        },{buttonsFocus:1});
    }

    function showTimeTotal(show) {
        if (show) {
            $(".prewTimeTotal").show();
            return;
        }
        $(".prewTimeTotal").hide();
    }

    function pay() {
        var id = $("#poHeaderId").val();
        var paymentOrderId = $("#paymentOrderId").val();
        var payTotal = $("#truePayTotal").val();

        var mainImg = $("#payImgDiv").find("[customInput = 'payImgImg']");
        var img = "";
        for (var i = 0; i < mainImg.length; i ++) {
            img += $(mainImg[i]).attr("src") + ",";
        }

        if ($String.isNullOrBlank(payTotal) || Number(payTotal) <= 0) {
            alert("错误提示:请输入支付金额");
            return false;
        }
        if ($String.isNullOrBlank(img)) {
            alert("错误提示:请上传支付凭证");
            return false;
        }

        $.ajax({
            url: '${ctx}/biz/po/bizPoHeader/payOrder',
            contentType: 'application/json',
            data: {"poHeaderId": id, "paymentOrderId": paymentOrderId, "payTotal": payTotal, "img": img},
            type: 'get',
            success: function (result) {
                if(result == '操作成功!') {
                    window.location.href = "${ctx}/biz/po/bizPoHeader/listV2";
                }
            },
            error: function (error) {
                console.info(error);
            }
        });

    }

    function submitPic(id, multiple){
        var f = $("#" + id).val();
        if(f==null||f==""){
            alert("错误提示:上传文件不能为空,请重新选择文件");
            return false;
        }else{
            var extname = f.substring(f.lastIndexOf(".")+1,f.length);
            extname = extname.toLowerCase();//处理了大小写
            if(extname!= "jpeg"&&extname!= "jpg"&&extname!= "gif"&&extname!= "png"){
                $("#picTip").html("<span style='color:Red'>错误提示:格式不正确,支持的图片格式为：JPEG、GIF、PNG！</span>");
                return false;
            }
        }
        var file = document.getElementById(id).files;
        var size = file[0].size;
        if(size>2097152){
            alert("错误提示:所选择的图片太大，图片大小最多支持2M!");
            return false;
        }
        ajaxFileUploadPic(id, multiple);
    }

    function ajaxFileUploadPic(id, multiple) {
        $.ajaxFileUpload({
            url : '${ctx}/biz/product/bizProductInfoV2/saveColorImg', //用于文件上传的服务器端请求地址
            secureuri : false, //一般设置为false
            fileElementId : id, //文件上传空间的id属性  <input type="file" id="file" name="file" />
            type : 'POST',
            dataType : 'text', //返回值类型 一般设置为json
            success : function(data, status) {
                //服务器成功响应处理函数
                var msg = data.substring(data.indexOf("{"), data.indexOf("}")+1);
                var msgJSON = JSON.parse(msg);
                var imgList = msgJSON.imgList;
                var imgDiv = $("#" + id + "Div");
                var imgDivHtml = "<img src=\"$Src\" customInput=\""+ id +"Img\" style='width: 100px' onclick=\"$(this).remove();\">";
                if (imgList && imgList.length > 0 && multiple) {
                    for (var i = 0; i < imgList.length; i ++) {
                        imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                    }
                }else if (imgList && imgList.length > 0 && !multiple) {
                    imgDiv.empty();
                    for (var i = 0; i < imgList.length; i ++) {
                        imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                    }
                }else {
                    var img = $("#" + id + "Img");
                    img.attr("src", msgJSON.fullName);
                }
            },
            error : function(data, status, e) {
                //服务器响应失败处理函数
                console.info(data);
                console.info(status);
                console.info(e);
                alert("上传失败");
            }
        });
        return false;
    }
</script>
</body>
</html>
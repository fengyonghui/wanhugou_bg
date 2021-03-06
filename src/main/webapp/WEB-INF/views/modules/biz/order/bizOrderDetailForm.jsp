<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<%@ page import="com.wanhutong.backend.modules.enums.DefaultPropEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.ProdTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum" %>
<html>
<head>
    <title>订单详情管理</title>
    <style type="text/css">
        .table-condensed td {
            padding: 5px 2px;
        }
    </style>
    <script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
            $("input[name='nowPrice']").each(function () {

            });
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

        $("#searchData").click(function () {
            var skuName=$("#skuName").val();
            $("#skuNameCopy").val(skuName);
            var skuCode =$("#skuCode").val();
            $("#skuCodeCopy").val(skuCode);
            var itemNo = $("#itemNo").val();
            $("#itemNoCopy").val(itemNo);
            $.ajax({
                type:"post",
                url:"${ctx}/biz/shelf/bizOpShelfSku/findOpShelfSku",
                data:$('#searchForm').serialize(),
                success:function (data) {
                    var trdatas='';
                    $.each(data,function (index,opShelfSku) {
                    <%--console.log(JSON.stringify(opShelfSku.skuValueList)+"-测试 888--");--%>
                        trdatas+= "<tr id='"+opShelfSku.id+"'>";
                        trdatas+="<td>"+opShelfSku.opShelfInfo.name+"</td>";
                        trdatas+="<td>"+opShelfSku.centerOffice.name+"</td>"
                        trdatas+="<td id='skuInfoName_"+ opShelfSku.id  + "'>"+opShelfSku.skuInfo.name+"</td>";
                        trdatas+="<td>"+opShelfSku.skuInfo.partNo+"</td>";
                        trdatas+="<td>"+opShelfSku.skuInfo.itemNo+"</td>";
                        var arr=opShelfSku.skuValueList;
                        if(arr!=null){
                            var attribute="";<%--页面的属性值遍历--%>
                            for(var jj=0;jj<arr.length;jj++){
                               var items=arr[jj].value+",";
                               attribute+=items;
                            }
                        }else{
                            var attribute="";
                        }
                        trdatas+="<td id='attribute_" + opShelfSku.id +  "'>"+attribute+"</td>";
                        trdatas+="<td id='saleInterval_" + opShelfSku.id +"'><input type='hidden' id='maxQty_"+opShelfSku.id+"' value='"+opShelfSku.maxQty+"'/>";
                        trdatas+="<input type='hidden' id='minQty_"+opShelfSku.id+"' value='"+opShelfSku.minQty+"'/>";
                        trdatas+= opShelfSku.minQty+"-"+opShelfSku.maxQty+"</td>";
                        trdatas+="<td>"+opShelfSku.salePrice+"</td>";
                        trdatas+="<td><input type='number' class='input-mini' id='saleQty_"+opShelfSku.id+"' style='width:58px;' min='1' max='99999' /></td>";
                        trdatas+="<td id='td_"+opShelfSku.id+"'> <a href='#' onclick=\"addItem('"+opShelfSku.id+"')\">增加</a></td>";
                        trdatas+="<input type='hidden' id='orderDetaIds_"+opShelfSku.id+"' value='"+opShelfSku.skuInfo.id+"'>";
                        trdatas+="<input type='hidden' id='shelfSkuId_"+opShelfSku.id+"' value='"+opShelfSku.id+"'>"
                        trdatas+= "</tr>";
                    });
                    $("#prodInfo2").html(trdatas);
                }
            })
        });

        $("#searchPurseData").click(function () {
                var skuName=$("#skuName").val();
                $("#bizSkuNameCopy").val(skuName);
                var skuCode =$("#skuCode").val();
                $("#bizSkuCodeCopy").val(skuCode);
                var itemNo = $("#itemNo").val();
                $("#bizItemNoCopy").val(itemNo);
                var purchaser = $("#purchaserId").val();
                // if (purchaser == null){
                //     alert("请选择采购商");
                // }
                $("#purchaserCopy").val(purchaser);
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findPurseSkuList",
                    data:$('#searchPurseForm').serialize(),
                    success:function (data) {
                        var trdatas='';
                        $.each(data,function (index,skuInfo) {
                            trdatas+= "<tr id='"+skuInfo.id+"'>";
                            trdatas+="<td id='skuInfoName_" + skuInfo.id  + "'>"+skuInfo.name+"</td>";
                            trdatas+="<td>"+skuInfo.partNo+"</td>";
                            trdatas+="<td>"+skuInfo.itemNo+"</td>";
                            var arr=skuInfo.attrValueList;
                            if(arr!=null){
                                var attribute="";<%--页面的属性值遍历--%>
                                for(var jj=0;jj<arr.length;jj++){
                                    if (jj==arr.length-1) {
                                        attribute += arr[jj].attributeInfo.name+":"+arr[jj].value;
                                    } else {
                                        attribute += arr[jj].attributeInfo.name+":"+arr[jj].value+",";
                                    }
                                }
                            }else{
                                var attribute="";
                            }
                            trdatas+="<td>"+attribute+"</td>";
                            trdatas+="<td id='nowPriceHtml_" + skuInfo.id + "'><input type='number' class='input-mini' id='nowPrice_"+skuInfo.id+"' style='width:58px;' /></td>";
                            trdatas+="<td><input type='number' class='input-mini' id='saleQty_"+skuInfo.id+"' style='width:58px;' min='1' max='99999' /></td>";
                            trdatas+="<td id='td_"+skuInfo.id+"'> <a href='#' onclick=\"addItemTwo('"+skuInfo.id+"')\">增加</a></td>";
                            trdatas+="<input type='hidden' id='orderDetaIds_"+skuInfo.id+"' value='"+skuInfo.id+"'>";
                            trdatas+= "</tr>";
                        });
                        $("#prodInfo2").html(trdatas);
                    }
                });
            });
		});

		function addItem(obj) {
        <%--var aa=$("#contentTable").append("<th>商品属性</th>").index()+4;//第4列位置--%>
          var saleQty= $("#saleQty_"+obj).val();
          var maxQty=$("#maxQty_"+obj).val();
          var minQty=$("#minQty_"+obj).val();
              if(saleQty==''){
                  alert("请输入数量");
                  return;
              }
              if((parseInt(saleQty)>parseInt(maxQty)) || (parseInt(saleQty)<parseInt(minQty))){
                  alert("购买的数量与当前价格不符");
                  return;
              }
            $("#td_"+obj).html("<a href='#' onclick=\"removeItem('"+obj+"')\">移除</a>");
            var trHtml=$("#"+obj);

            if (${orderH.bizStatus>=15 && orderH.bizStatus!=45}) {
                var addAttributeHtml = "<td id='suplyisName_" + obj + "'>${detail.suplyis.name}</td>"
                var skuInfoNameHtml = $(trHtml).find("#skuInfoName_" + obj);
                skuInfoNameHtml.before(addAttributeHtml)
            }

            if (${orderH.bizStatus==OrderHeaderBizStatusEnum.SUPPLYING.state || orderH.bizStatus==OrderHeaderBizStatusEnum.APPROVE.state}) {
                var addsendQtyHtml = "<td id='sendQtyTd_" + obj +"'><input type='number' class='input-mini' id='sendQty_"+obj+"' name='sentQtys' style='width:58px;' /></td>"
                var saleIntervalHtml = $(trHtml).find("#saleInterval_" + obj);
                saleIntervalHtml.before(addsendQtyHtml);
            }

            $("#prodInfo").append(trHtml);
            $("#prodInfo").find($("#saleQty_"+obj)).attr("name","saleQtys");
            $("#prodInfo").find($("#saleQty_"+obj)).attr("readonly","readonly");
            $("#prodInfo").find("#orderDetaIds_"+obj).attr("name","orderDetaIds");
            $("#prodInfo").find("#shelfSkuId_"+obj).attr("name","shelfSkus");
        }

        function addItemTwo(obj) {
            <%--var aa=$("#contentTable").append("<th>商品属性</th>").index()+4;//第4列位置--%>
            $("#td_"+obj).html("<a href='#' onclick=\"removeItemTwo('"+obj+"')\">移除</a>");
            var trHtml=$("#"+obj);

            if (${orderH.bizStatus>=15 && orderH.bizStatus!=45}) {
                var addAttributeHtml = "<td id='suplyisName_" + obj + "'>供货部</td>"
                var skuInfoNameHtml = $(trHtml).find("#skuInfoName_" + obj);
                skuInfoNameHtml.before(addAttributeHtml)
            }
            if (${orderH.bizStatus==OrderHeaderBizStatusEnum.SUPPLYING.state || orderH.bizStatus==OrderHeaderBizStatusEnum.APPROVE.state}) {
                var addsendQtyHtml = "<td id='sendQtyTd_" + obj + "'><input type='number' class='input-mini' id='sendQty_"+obj+"' name='sentQtys' style='width:58px;' /></td>"
                var nowPriceHtml = $(trHtml).find("#nowPriceHtml_" + obj);
                nowPriceHtml.before(addsendQtyHtml);
            }

            $("#prodInfo").append(trHtml);
            $("#prodInfo").find($("#saleQty_"+obj)).attr("name","saleQtys");
            $("#prodInfo").find($("#saleQty_"+obj)).attr("readonly","readonly");
            $("#prodInfo").find($("#nowPrice_"+obj)).attr("name","nowPrices");
            $("#prodInfo").find($("#nowPrice_"+obj)).attr("readonly","readonly");
            $("#prodInfo").find("#orderDetaIds_"+obj).attr("name","orderDetaIds");
        }
        function removeItem(obj) {
            $("#td_"+obj).html("<a href='#' onclick=\"addItem('"+obj+"')\">增加</a>");
            var trHtml=$("#"+obj);

            if (${orderH.bizStatus>=15 && orderH.bizStatus!=45}) {
                $("td").remove("#suplyisName_" + obj)
            }

            if (${orderH.bizStatus==OrderHeaderBizStatusEnum.SUPPLYING.state || orderH.bizStatus==OrderHeaderBizStatusEnum.APPROVE.state}) {
                $("td").remove("#sendQtyTd_" + obj)
            }


            $("#prodInfo2").append(trHtml);
            $("#prodInfo2").find($("#saleQty_"+obj)).removeAttr("name");
            $("#prodInfo2").find($("#saleQty_"+obj)).removeAttr("readonly");
            $("#prodInfo2").find("#orderDetaIds_"+obj).removeAttr("name");
            $("#prodInfo2").find("#shelfSkuId_"+obj).removeAttr("name");
        }
        function removeItemTwo(obj) {
            $("#td_"+obj).html("<a href='#' onclick=\"addItemTwo('"+obj+"')\">增加</a>");
            var trHtml=$("#"+obj);

            if (${orderH.bizStatus>=15 && orderH.bizStatus!=45}) {
                $("td").remove("#suplyisName_" + obj)
            }

            if (${orderH.bizStatus==OrderHeaderBizStatusEnum.SUPPLYING.state || orderH.bizStatus==OrderHeaderBizStatusEnum.APPROVE.state}) {
                $("td").remove("#sendQtyTd_" + obj)
            }

            $("#prodInfo2").append(trHtml);
            $("#prodInfo2").find($("#saleQty_"+obj)).removeAttr("name");
            $("#prodInfo2").find($("#saleQty_"+obj)).removeAttr("readonly");
            $("#prodInfo2").find($("#nowPrice_"+obj)).removeAttr("name");
            $("#prodInfo2").find($("#nowPrice_"+obj)).removeAttr("readonly");
            $("#prodInfo2").find("#orderDetaIds_"+obj).removeAttr("name");
        }
    </script>
    <script type="text/javascript">
        function DetailDelete(a,b,c){
            top.$.jBox.confirm("确认要删除该商品吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    $.ajax({
                        type:"post",
                        url:"${ctx}/biz/order/bizOrderDetail//Detaildelete",
                        data:"id="+a+"&sign="+b+"&orderDetailDetele="+c,
                        success:function(data){
                            if(data=="ok"){
                                $("#trRevom_"+a).remove();//主要是删除这tr
                                $("#id").val("");//点击删除后把原id为空
                            }
                        }
                    });
                }
            },{buttonsFocus:1});
        }
    </script>
<meta name="decorator" content="default"/>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
    <li class="active"><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}&orderHeader.id=${orderH.id}&&orderType=${orderH.orderType}">订单详情<shiro:hasPermission
            name="biz:order:bizOrderDetail:edit">${not empty bizOrderDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="biz:order:bizOrderDetail:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderDetail" action="${ctx}/biz/order/bizOrderDetail/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="orderHeader.id"/>
    <form:hidden path="orderHeader.clientModify"/>
    <form:hidden path="orderHeader.consultantId"/>
    <form:hidden path="detailFlag"/>
    <form:hidden path="suplyIds" value="${bizOrderDetail.suplyis.id}"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">选择商品：</label>
        <div class="controls">
            <ul class="inline ul-form">

                <li><label>商品名称：</label>
                    <input id="skuName"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="120" class="input-medium"/>
                </li>
                <li><label>商品编码：</label>
                    <input id="skuCode"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false" maxlength="50" class="input-medium"/>
                </li>
                <li><label>商品货号：</label>
                    <input id="itemNo"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false" maxlength="50" class="input-medium"/>
                </li>
                <c:if test="${orderH.orderType == DefaultPropEnum.PURSEHANGER.propValue}">
                    <%--<li><label>采购商：</label>--%>
                        <%--<sys:treeselect id="purchaser" name="" value=""  labelName=""--%>
                                        <%--labelValue="" notAllowSelectParent="true"--%>
                                        <%--title="采购商"  url="/sys/office/queryTreeList?type=6"--%>
                                        <%--cssClass="input-medium required"--%>
                                        <%--allowClear="true"  dataMsgRequired="必填信息"/>--%>
                    <%--</li>--%>
                    <%--<span class="help-inline"><font color="red">*</font> </span>--%>
                    <input id="purchaserId" type="hidden" value="${customer.id}" />
                </c:if>
                <%--<li><label>查询：</label>--%>
                    <%--<select class="input-medium">--%>
                        <%--<option value=""> 请选择 </option>--%>
                        <%--<option id="skuAll" value="ono"> 查询所有 </option>--%>
                    <%--</select>--%>
                <%--</li>--%>

                <c:if test="${orderH.orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                    <li class="btns"><input id="searchData" class="btn btn-primary" type="button"  value="查询"/></li>
                </c:if>
                <c:if test="${orderH.orderType == DefaultPropEnum.PURSEHANGER.propValue}">
                    <li class="btns"><input id="searchPurseData" class="btn btn-primary" type="button"  value="查询"/></li>
                </c:if>
                <li class="clearfix"></li>
            </ul>

        </div>
    </div>
    <div class="control-group">
        <label class="control-label">订单商品：</label>
        <div class="controls">
            <table id="contentTable" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
                <thead>
                <tr>
                    <c:if test="${orderH.orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                        <th>货架名称</th>
                        <th>采购中心</th>
                    </c:if>
                    <c:if test="${orderH.bizStatus>=15 && orderH.bizStatus!=45}">
                        <th>发货方</th>
                    </c:if>
                    <th>商品名称</th>
                    <th>商品编码</th>
                    <th>商品货号</th>
                    <th>商品属性</th>

                    <c:if test="${orderH.bizStatus==OrderHeaderBizStatusEnum.SUPPLYING.state || orderH.bizStatus==OrderHeaderBizStatusEnum.APPROVE.state}">
                        <th>已发货数量</th>
                    </c:if>
                    <c:if test="${orderH.orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                        <th>销售数量区间</th>
                    </c:if>
                    <th>现价</th>
                    <th>采购数量</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody id="prodInfo">
                    <c:if test="${bizOrderDetail.id!=null}">
                        <tr id="trRevom_${detail.id}">
                            <c:if test="${orderH.orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                                <td>${detail.shelfInfo.opShelfInfo.name}</td>
                                <td>${detail.shelfInfo.centerOffice.name}</td>
                            </c:if>
                            <c:if test="${orderH.bizStatus>=15 && orderH.bizStatus!=45}">
                                <td>
                                    <c:if test="${orderH.orderType == DefaultPropEnum.PURSEHANGER.propValue}">
                                        供货部
                                    </c:if>
                                    <c:if test="${orderH.orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                                        ${detail.suplyis.name}
                                    </c:if>
                                </td>
                            </c:if>
                            <td>
                                 ${detail.skuName}
                            </td>
                            <td>${detail.partNo}</td>
                            <td>${detail.skuInfo.itemNo}</td>
                            <td>
                                <c:forEach items="${detail.attributeValueV2List}" var="attributeValueV2">
                                    ${attributeValueV2.attributeInfo.name}:${attributeValueV2.value},
                                </c:forEach>
                            </td>
                            <c:if test="${orderH.bizStatus==OrderHeaderBizStatusEnum.SUPPLYING.state || orderH.bizStatus==OrderHeaderBizStatusEnum.APPROVE.state}">
                                <td>${detail.sentQty}</td>
                            </c:if>
                            <c:if test="${orderH.orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                                <td>${shelfSku.minQty}-${shelfSku.maxQty}</td>
                                <%--<td>${shelfSku.salePrice}</td>--%>
                                <td>${detail.unitPrice}</td>
                            </c:if>
                            <c:if test="${orderH.orderType == DefaultPropEnum.PURSEHANGER.propValue}">
                                <td>${detail.unitPrice}</td>
                            </c:if>
                            <td style="text-align: center;">${detail.ordQty}</td>
                            <td>
                                <%--<a href="${ctx}/biz/order/bizOrderDetail/delete?id=${detail.id}&sign=1&orderDetailDetele=details" onclick="return confirmx('确认要删除该商品吗？', this.href)">--%>
                                    <%--删除</a>--%>
                                <a href="javascript:void(0);" onclick="DetailDelete(${detail.id},'1','details');">删除</a>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
            <%--<c:if test="${entity.str!='detail'}">--%>
                <table id="contentTable2" style="width:48%;float: right;background-color:#CEECF5;" class="table table-bordered table-condensed">
                    <thead>
                    <tr>
                        <c:if test="${orderH.orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                            <th>货架名称</th>
                            <th>采购中心</th>
                        </c:if>
                        <th>商品名称</th>
                        <th>商品编码</th>
                        <th>商品货号</th>
                        <th>商品属性</th>
                        <c:if test="${orderH.orderType != DefaultPropEnum.PURSEHANGER.propValue}">
                            <th>销售数量区间</th>
                        </c:if>
                        <th>现价</th>
                        <th>采购数量</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody id="prodInfo2">

                    </tbody>
                </table>
            <%--</c:if>--%>
        </div>

    </div>

    <div class="form-actions">
        <shiro:hasPermission name="biz:order:bizOrderDetail:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="保存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返回" onclick="history.go(-1)"/>
    </div>
</form:form>

<form:form id="searchForm" modelAttribute="bizOpShelfSku" >
    <form:hidden id="skuNameCopy" path="skuInfo.name"/>
    <form:hidden id="skuCodeCopy" path="skuInfo.partNo"/>
    <form:hidden id="itemNoCopy" path="skuInfo.itemNo"/>
    <form:hidden id="orderDetailForm" path="orderDetailForm" value="orderDetailForm"/>
</form:form>
<form:form id="searchPurseForm" modelAttribute="bizSkuInfo" >
    <input type="hidden" id="bizSkuNameCopy" name="name" value="${name}"/>
    <input type="hidden" id="bizSkuCodeCopy" name="partNo" value="${partNo}"/>
    <input type="hidden" id="bizItemNoCopy" name="itemNo" value="${itemNo}"/>
    <input type="hidden" id="purchaserCopy" name="purchaser.id" value="${purchaser.id}"/>
</form:form>
</body>
</html>
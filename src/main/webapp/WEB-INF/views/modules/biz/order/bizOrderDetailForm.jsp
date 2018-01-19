<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>订单详情管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			/* 全选 全不选 */
//            $('#chAll').live('click',function(){
//                var choose=$("input[name='boxs']");
//                if($(this).attr('checked')){
//                    choose.attr('checked',true);
//                }else{
//                    choose.attr('checked',false);
//                }
//            });
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
			if($("#id").val()!=""){

            }


        $("#searchData").click(function () {
            var skuName=$("#skuName").val();
            $("#skuNameCopy").val(skuName);
            var skuCode =$("#skuCode").val();
            $("#skuCodeCopy").val(skuCode);
            $.ajax({
                type:"post",
                url:"${ctx}/biz/shelf/bizOpShelfSku/findOpShelfSku",
                data:$('#searchForm').serialize(),
                success:function (data) {
                    var trdatas=''
                    $.each(data,function (index,opShelfSku) {
                        trdatas+= "<tr id='"+opShelfSku.id+"'>";
                        trdatas+="<td>"+opShelfSku.opShelfInfo.name+"</td>";
                        trdatas+="<td>"+opShelfSku.skuInfo.name+"</td>";
                        trdatas+="<td>"+opShelfSku.skuInfo.partNo+"</td>";
                        trdatas+="<td><input type='hidden' id='maxQty_"+opShelfSku.id+"' value='"+opShelfSku.maxQty+"'/>"+opShelfSku.minQty+"-"+opShelfSku.maxQty+"</td>";
                        trdatas+="<td>"+opShelfSku.salePrice+"</td>";
                        trdatas+="<td><input type='text' class='input-mini' id='saleQty_"+opShelfSku.id+"'/></td>";
                        trdatas+="<td id='td_"+opShelfSku.id+"'> <a href='#' onclick=\"addItem('"+opShelfSku.id+"')\">增加</a></td>";
                        trdatas+="<input type='hidden' id='orderDetaIds_"+opShelfSku.id+"' value='"+opShelfSku.skuInfo.id+"'>";
                        trdatas+="<input type='hidden' id='shelfSkuId_"+opShelfSku.id+"' value='"+opShelfSku.id+"'>"
                        trdatas+= "</tr>";

                    })
                    $("#prodInfo2").html(trdatas)


                }
            })
        });
		});
		function addItem(obj) {
          var saleQty= $("#saleQty_"+obj).val();
          var maxQty=$("#maxQty_"+obj).val();
              if(saleQty==''){
                  alert("请输入数量");
                  return;
              }
              if(saleQty>maxQty){
                  alert("购买的数量与当前价格不符");
                  return;
              }
            $("#td_"+obj).html("<a href='#' onclick=\"removeItem('"+obj+"')\">移除</a>");
            var trHtml=$("#"+obj);
            $("#prodInfo").append(trHtml);
            $("#prodInfo").find($("#saleQty_"+obj)).attr("name","saleQtys")
            $("#prodInfo").find($("#saleQty_"+obj)).attr("readonly","readonly");
            $("#prodInfo").find("#orderDetaIds_"+obj).attr("name","orderDetaIds");
            $("#prodInfo").find("#shelfSkuId_"+obj).attr("name","shelfSkus");


        }
        function removeItem(obj) {

        }
    </script>


</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/order/bizOrderHeader/">订单信息列表</a></li>
    <li class="active"><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">订单详情<shiro:hasPermission
            name="biz:order:bizOrderDetail:edit">${not empty bizOrderDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="biz:order:bizOrderDetail:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderDetail" action="${ctx}/biz/order/bizOrderDetail/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="orderHeader.id"/>
    <%--<form:hidden path="maxLineNo"/>--%>
    <%--<form:hidden path="orderHeader.oneOrder"/>--%>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">选择商品：</label>
        <div class="controls">
            <ul class="inline ul-form">

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
        <label class="control-label">订单商品：</label>
        <div class="controls">
            <table id="contentTable" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
                <thead>
                <tr>
                    <th>货架名称</th>
                    <th>商品名称</th>
                    <th>商品编码</th>
                    <th>销售数量区间</th>
                    <th>现价</th>
                    <th>采购数量</th>
                    <%--<th>发货数量</th>--%>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody id="prodInfo">
                </tbody>
            </table>
            <%--<c:if test="${entity.str!='detail'}">--%>
                <table id="contentTable2" style="width:48%;float: right;background-color:#abcceb;" class="table table-bordered table-condensed">
                    <thead>
                    <tr>
                        <th>货架名称</th>
                        <th>商品名称</th>
                        <th>商品编码</th>
                        <th>销售数量区间</th>
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
</form:form>
</body>
</html>
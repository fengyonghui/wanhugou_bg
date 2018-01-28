<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品库存详情管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    $("select[title='invInfoId']").removeAttr("disabled");
                    $("select[title='invType']").removeAttr("disabled");
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
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuInfoList",
                    data:$('#searchForm').serialize(),
                    success:function (data) {
                        if($("#id").val()==''){
                            $("#prodInfo2").empty();
                        }
						var selecttd="<select class='input-mini' title='invInfoId'><option value=''>请选择</option>";
                        $.each(data.inventoryInfoList,function (index,inventory) {
                            selecttd+="<option value='"+inventory.id+"'>"+inventory.name+"</option>"
						});
                        selecttd+="</select>";
                        var selectInvTypetd="<select class='input-mini' title='invType' class='input-medium'><option value=''>请选择</option>";
                        $.each(data.dictList,function (index,dict) {
                            selectInvTypetd+="<option value='"+dict.value+"'>"+dict.label+"</option>"
                        });
                        selectInvTypetd+="</select>";
						var trdatas='';
                            var t=0;
                            $.each(data.skuInfoList,function (index,skuInfo) {
                                if($("#prodInfo").children("."+skuInfo.id).length>0){
                                    return;
                                }
                                trdatas+= "<tr class='"+skuInfo.id+"' id='"+skuInfo.id+"'>";
                                trdatas+="<td id='invInfoId_"+skuInfo.id+"'>"+selecttd+"</td>";
                                trdatas+="<td><input type='hidden' id='skuInfoIds_"+skuInfo.id+"' value='"+skuInfo.id+"'/>"+skuInfo.name+"</td>";
                                trdatas+="<td>"+skuInfo.partNo+"</td>";
                                trdatas+="<td id='invType_"+skuInfo.id+"'>"+selectInvTypetd+"</td>";
                                trdatas+="<td><input  type='text' class='input-mini' id='saleQty_"+skuInfo.id+"'/></td>";
                                trdatas+="<td id='td_"+skuInfo.id+"'> <a href='#' onclick=\"addItem('"+skuInfo.id+"')\">增加</a></td>";
                                trdatas+= "</tr>";
                            });


                        	$("#prodInfo2").html(trdatas)


                    }
                })
            });

		});

		function removeItem(obj) {
		    alert(obj);
            $("#td_"+obj).html("<a href='#' onclick=\"addItem('"+obj+"')\">增加</a>");
            var trHtml=$("#"+obj);
            $("#prodInfo2").append(trHtml);
            $("#prodInfo2").find($("#invInfoId_"+obj)).find($("select[title='invInfoId']")).removeAttr("name");
            $("#prodInfo2").find($("#invInfoId_"+obj)).find($("select[title='invInfoId']")).removeAttr("disabled");
            $("#prodInfo2").find($("#invType_"+obj)).find($("select[title='invType']")).removeAttr("name");
            $("#prodInfo2").find($("#invType_"+obj)).find($("select[title='invType']")).removeAttr("disabled");
            $("#prodInfo2").find($("#saleQty_"+obj)).removeAttr("name");
            $("#prodInfo2").find($("#saleQty_"+obj)).removeAttr("readonly");
            $("#prodInfo2").find($("#skuInfoIds_"+obj)).removeAttr("name");

        }

        function addItem(obj) {
            var invInfoId= $("#invInfoId_"+obj).find("select[title='invInfoId']").val();
            var invType=$("#invType_"+obj).find("select[title='invType']").val();
            var saleQty=$("#saleQty_"+obj).val();
            if(invInfoId==''){
                alert("请选择仓库");
                return;
            }
            if(invType==''){
                alert("请选择库存类型");
                return;
            }
            if(saleQty==''){
                alert("请输入数量");
                return;
			}
            $("#td_"+obj).html("<a href='#' onclick=\"removeItem('"+obj+"')\">移除</a>");
            var trHtml=$("#"+obj);
            $("#prodInfo").append(trHtml);
            $("#prodInfo").find($("#invInfoId_"+obj)).find($("select[title='invInfoId']")).attr("name","invInfoIds")
            $("#prodInfo").find($("#invInfoId_"+obj)).find($("select[title='invInfoId']")).attr("disabled","disabled");
            $("#prodInfo").find($("#invType_"+obj)).find($("select[title='invType']")).attr("name","invTypes");
            $("#prodInfo").find($("#invType_"+obj)).find($("select[title='invType']")).attr("disabled","disabled");
            $("#prodInfo").find($("#saleQty_"+obj)).attr("name","stockQtys");
            $("#prodInfo").find($("#saleQty_"+obj)).attr("readonly","readonly");
            $("#prodInfo").find($("#skuInfoIds_"+obj)).attr("name","skuInfoIds");


        }
        function delItem(obj) {
            if(confirm("您确认删除此条信息吗？")){
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/inventory/bizInventorySku/delItem",
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
		<li><a href="${ctx}/biz/inventory/bizInventorySku?invInfo.id=${bizInventorySku.invInfo.id}&zt=${zt}">商品库存详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/inventory/bizInventorySku/form?invInfo.id=${bizInventorySku.invInfo.id}&zt=${zt}">商品库存详情<shiro:hasPermission name="biz:inventory:bizInventorySku:edit">${not empty bizInventorySku.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:inventory:bizInventorySku:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizInventorySku" action="${ctx}/biz/inventory/bizInventorySku/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="zt" name="zt" value="${zt}" type="hidden"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
				<ul class="inline ul-form">

					<li><label>商品名称：</label>
						<input id="skuName"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
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
			<label class="control-label">盘点商品：</label>
			<div class="controls">
				<table id="contentTable" style="width:48%;float:left" class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>仓库名称</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>库存类型</th>
						<th>库存数量</th>
						<th>操作</th>
					</tr>
					</thead>
					<tbody id="prodInfo">
					<c:if test="${entity.skuInfo!=null}">
						<tr class="${entity.skuInfo.id}" id="${entity.id}">
						<td>${entity.invInfo.name}</td>
						<td>${entity.skuInfo.name}</td>
						<td>${entity.skuInfo.partNo}</td>
						<td>${entity.invType}</td>
						<td>${entity.stockQty}</td>
							<td><a href='#' onclick="delItem('${entity.id}')">删除</a></td>
						</tr>
					</c:if>
					</tbody>
				</table>
				<table id="contentTable2" style="width:48%;float: right;background-color:#abcceb;" class="table table-bordered table-condensed">
					<thead>
					<tr>
						<th>仓库名称</th>
						<th>商品名称</th>
						<th>商品编码</th>
						<th>库存类型</th>
						<th>库存数量</th>
						<th>操作</th>
					</tr>
					</thead>
					<tbody id="prodInfo2">

					</tbody>
				</table>
			</div>

		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:inventory:bizInventorySku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<form:form id="searchForm" modelAttribute="bizSkuInfo" >
		<form:hidden id="skuNameCopy" path="name"/>
		<form:hidden id="skuCodeCopy" path="partNo"/>
	</form:form>
</body>
</html>
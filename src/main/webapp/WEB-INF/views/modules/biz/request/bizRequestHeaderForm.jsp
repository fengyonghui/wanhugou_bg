<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    var ids = [], nodes = tree.getCheckedNodes(true);
                    for(var i=0; i<nodes.length; i++) {
                        if(!nodes[i].isParent){
                            ids.push(nodes[i].id);
                        }
                    }
                    $("#skuIds").val(ids);
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
            var setting = {check:{enable:true,nocheckInherit:true},view:{selectedMulti:false,nameIsHTML: true},
                data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
                    tree.checkNode(node, !node.checked, true, true);
                    return false;
                }
                }}

            var zNodes=[
                    <c:forEach items="${skuList}" var="sku">{id:"${sku.id}", pId:"${not empty sku.pid?sku.pid:0}", name:"${not empty sku.pid?sku.name:'sku列表'}"},
                </c:forEach>];
            // 初始化树结构
            var tree = $.fn.zTree.init($("#skuTree"), setting, zNodes);
            // 不选择父节点
            tree.setting.check.chkboxType = { "Y" : "ps", "N" : "ps" };
            // 默认选择节点
            var ids = "${entity.skuIds}".split(",");
            for(var i=0; i<ids.length; i++) {
                var node = tree.getNodeByParam("id", ids[i]);
                try{tree.checkNode(node, true, false);
                    tree.checkNode(node.getParentNode(), true, false);
                }catch(e){}
            }
            // 默认展开全部节点
            tree.expandAll(true);
            $("#but_sub").click(function () {
                var ids = [], nodes = tree.getCheckedNodes(true);
                for(var i=0; i<nodes.length; i++) {
                    if(!nodes[i].isParent){
                        ids.push(nodes[i].id);
                    }
                }
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuList?skuIds="+ids,
                    success:function (data) {
                            $('#myModal').modal('hide');
                            if(id==''){
                                $("#prodInfo").empty();
							}
                            $.each(data,function (index,skuInfo) {
                                if(id!=''){
                                   if($("#aaId").val()!=''){
                                       index=parseInt($("#aaId").val())+1;
								   }
                                }
                                var cateName="";
                                if(skuInfo.productInfo.categoryInfoList!=null){
                                    $.each(skuInfo.productInfo.categoryInfoList,function (index,cate) {
                                        cateName+= "/"+cate.name;
                                    });
								}
                                console.info(cateName);
                                if(cateName!=""){
                                    cateName=cateName.substring(1);
								}

								$("#prodInfo").append("<tr id='"+skuInfo.id+"'> <td><img src='"+skuInfo.productInfo.imgUrl+"'></td><td>"+skuInfo.productInfo.name+"</td>" +
									"<td>" +cateName+"</td>" +
									"<td>"+skuInfo.productInfo.prodCode+"</td><td>"+skuInfo.productInfo.brandName+"</td><td>"+skuInfo.productInfo.office.name+"</td>" +
                                    "<td>"+skuInfo.name+"</td><td><input type='hidden' name='requestDetailList["+index+"].skuInfo.id' value='"+skuInfo.id+"'/><input name='requestDetailList["+index+"].reqQty' type='text'/></td><td><a href='#' onclick=\"removeItem('"+skuInfo.id+"')\">移除</a></td></tr>")
                            })
//                            $("#myModal").on("hidden.bs.modal", function (e) {
//                                alert("保存成功！");
//
//                            })

                    }
                })
            })
			$("#searchData").click(function () {
				var productName=$("#productName").val();
				$("#productNameCopy").val(productName);
				var prodCode=$("#prodCode").val();
				$("#prodCodeCopy").val(prodCode);
				var prodBrandName=$("#prodBrandName").val();
				$("#prodBrandNameCopy").val(prodBrandName);
				var skuCode =$("#skuCode").val();
				console.info(skuCode);
				$("#skuCodeCopy").val(skuCode);
                $.ajax({
               		 type:"post",
					 url:"${ctx}/biz/request/bizRequestHeader/findSkuTreeList",
					 data:$('#searchForm').serialize(),
               		 success:function (data) {
                         zNodes=data;
                          tree = $.fn.zTree.init($("#skuTree"), setting, zNodes);
                         tree.expandAll(true);
                	}
                })
            });
        });
		function removeItem(obj) {
			$("#"+obj).remove();
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
								title="采购中心"  url="/sys/office/queryTreeList?type=8" cssClass="input-xlarge required" dataMsgRequired="必填信息">
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
		<shiro:hasPermission name="biz:request:bizRequestDetail:edit">
		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
				<button  data-toggle="modal" data-target="#myModal" type="button" class="btn btn-default">
					<span class="icon-plus"></span>
				</button>
			</div>
		</shiro:hasPermission>
		</div>
		<div class="control-group">
			<label class="control-label">备货商品：</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th>商品图片</th>
						<th>商品名称</th>
						<th>商品分类</th>
						<th>商品代码</th>
						<th>品牌名称</th>
						<th>供应商</th>
						<th>SKU</th>
						<th>申报数量</th>
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
									<input name='requestDetailList[${reqStatus.index}].reqQty' value="${reqDetail.reqQty}" type='text'/>
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
		<!-- 模态框（Modal） -->
		<div class="modal fade hide" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">×
						</button>
						<h4 class="modal-title" id="myModalLabel">
							选择商品
						</h4>
					</div>
					<div class="modal-body">
						<div class="control-group">
							<ul class="inline ul-form">
									<li><label>商品名称：</label>
										<input id="productName"  htmlEscape="false" class="input-medium"/>
									</li>	
									<li><label>商品编码：</label>
										<input id="prodCode"  htmlEscape="false" maxlength="10" class="input-medium"/>
									</li>
									<li><label>品牌名称：</label>
										<input id="prodBrandName"  htmlEscape="false" maxlength="50" class="input-medium"/>
									</li>
									<li><label>SKU编码：</label>
										<input id="skuCode"  htmlEscape="false" maxlength="10" class="input-medium"/>
									</li>
									<li class="btns"><input id="searchData" class="btn btn-primary" type="button" value="查询"/></li>
									<li class="clearfix"></li>
								</ul>


							<label class="control-label">选择商品：</label>
							<div class="controls">
								<div id="skuTree" class="ztree required" style="margin-top:3px;float:left;"></div>
								<form:hidden path="skuIds"/>
								<span class="help-inline"><font color="red">*</font> </span>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default"
								data-dismiss="modal">关闭
						</button>
						<shiro:hasPermission name="biz:category:bizCatePropertyInfo:edit">
							<button id="but_sub"type="button" class="btn btn-primary">
								保存
							</button>
						</shiro:hasPermission>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div><!-- /.modal -->

	</form:form>
	<form:form id="searchForm" modelAttribute="bizRequestHeader" >
		<form:hidden id="productNameCopy" path="productInfo.name"/>
		<form:hidden id="prodCodeCopy" path="productInfo.prodCode"/>
		<form:hidden id="prodBrandNameCopy" path="productInfo.brandName"/>
		<form:hidden id="skuCodeCopy" path="productInfo.skuPartNo"/>
	</form:form>
</body>
</html>
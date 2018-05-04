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
            $.ajax({
                type:"POST",
                url:"${ctx}/biz/sku/bizSkuInfo/findSkuNameList?ids=3",
                dataType:"json",
                success:function(data){
                    var htmlInfo = "";
                    $.each(data,function(index,item) {
                        alert(item);
                        htmlInfo+="<tr><td>"+ item.name +"</td>"+
                            "<c:if test='${bizOpShelfSku.id != null}'><td>"+
							"<input name='createBy.name' value='${createBy.name}' htmlEscape='false' maxlength='11' class='input-medium' readonly='true' placeholder='必填！'/></td>"+
							"</c:if>"+
                            "<td><input name='shelfQty' value='' htmlEscape='false' maxlength='6' class='input-medium required' type='text' placeholder='必填！'/></td>"+
                            "<td>"+item.basePrice+"/></td>"+
                            "<td><input name=\"salePrice\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" placeholder=\"必填！\"/></td>"+
                            "<td><input name=\"minQty\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type=\"text\" placeholder=\"必填！\"/></td>"+
                            "<td><input name=\"maxQty\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type=\"text\" placeholder=\"必填！\"/></td>"+
                            "<td><input name=\"shelfTime\" type=\"text\" readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate required\"" +
                            "value=\"<fmt:formatDate value='${bizOpShelfSku.shelfTime}' pattern='yyyy-MM-dd HH:mm:ss'/>\"" +
                            "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\" placeholder=\"必填！\"/></td>"+
                            "<c:if test='${bizOpShelfSku.id != null}'>\n" +
                            "<td><input name=\"createBy.name\" value=\"${createBy.name}\" htmlEscape=\"false\" maxlength=\"11\" class=\"input-medium\" readonly=\"true\" type=\"text\" placeholder=\"必填！\"/></td>" +
                            "</c:if>" +
                            "<td><input name=\"unshelfTime\" type=\"text\" readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate \"" +
                            "value=\"<fmt:formatDate value='${bizOpShelfSku.unshelfTime}' pattern='yyyy-MM-dd HH:mm:ss'/>\"" +
                            "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\" placeholder=\"选填！\"/></td>" +
                            "<td><input name=\"priority\" value=\"\" htmlEscape=\"false\" maxlength=\"5\" class=\"input-medium required\" type=\"text\" placeholder=\"必填！\"/></td></tr>";
                       alert(htmlInfo)
                        $("#tbody").append(htmlInfo);
                    });
                }
            })
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/shelf/bizOpShelfSku/">商品上架列表</a></li>
		<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfSku/form?id=${bizOpShelfSku.id}">商品上架<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit">${not empty bizOpShelfSku.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizOpShelfSku:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOpShelfSku" action="${ctx}/biz/shelf/bizOpShelfSku/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden id="shelfId" path="opShelfInfo.id"/>
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

		<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit">
			<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
				<button  data-toggle="modal" data-target="#myModal" type="button" class="btn btn-default">
					<span class="icon-plus"></span>
				</button>
			</div>
		</shiro:hasPermission>
		</div>
		<%--<div class="control-group">
			<label class="control-label">货架ID：</label>
			<div class="controls">
				<form:input path="opShelfInfo.id" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>--%>
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
							<th>工厂价(元)：</th>
							<th>现价(元)：(销售单价)</th>
							<th>最低销售数量(个)：(该单价所对应的)</th>
							<th>最高销售数量(个)：(该单价所对应的，9999：不限制)</th>
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
						<%--<c:forEach items="">--%>
							<%--<tr>
								<td><input name="" value="${bizOpShelfSku.skuInfo.name}" class="input-medium required" type="text" placeholder="必填！"/>
									</td>
								<c:if test="${bizOpShelfSku.id != null}">
									<td><input name="createBy.name" value="${createBy.name}" htmlEscape="false" maxlength="11" class="input-medium" readonly="true" placeholder="必填！"/></td>
								</c:if>
								<td><input name="shelfQty" value="" htmlEscape="false" maxlength="6" class="input-medium required" type="text" placeholder="必填！"/></td>
								<td><input name="orgPrice" value="${orgPrice}" htmlEscape="false" maxlength="6" class="input-medium required" type="text" placeholder="必填！"/></td>
								<td><input name="salePrice" value="" htmlEscape="false" maxlength="6" class="input-medium required" placeholder="必填！"/></td>
								<td><input name="minQty" value="" htmlEscape="false" maxlength="6" class="input-medium required" type="text" placeholder="必填！"/></td>
								<td><input name="maxQty" value="" htmlEscape="false" maxlength="6" class="input-medium required" type="text" placeholder="必填！"/></td>
								<td><input name="shelfTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
										   value="<fmt:formatDate value="${bizOpShelfSku.shelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
										   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/></td>
								<c:if test="${bizOpShelfSku.id != null}">
										<td><input name="createBy.name" value="${createBy.name}" htmlEscape="false" maxlength="11" class="input-medium" readonly="true" type="text" placeholder="必填！"/></td>
								</c:if>
								<td><input name="unshelfTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
										   value="<fmt:formatDate value="${bizOpShelfSku.unshelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
										   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="选填！"/></td>
								<td><input name="priority" value="" htmlEscape="false" maxlength="5" class="input-medium required" type="text" placeholder="必填！"/></td>
							</tr>--%>
						<%--</c:forEach>--%>
					</tbody>
				</table>
			</div>
		</div>
		<%--<div class="control-group">
			<label class="control-label">商品名称：</label>
			<div class="controls">
				<sys:treeselect id="skuInfo" name="skuInfo.id" value="${bizOpShelfSku.skuInfo.id}" labelName="skuInfo.name"
								labelValue="${bizOpShelfSku.skuInfo.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="sku名称"  url="/biz/product/bizProductInfo/querySkuTreeList" extId="${skuInfo.id}"
								cssClass="input-xlarge required"
								allowClear="${skuInfo.currentUser.admin}"  dataMsgRequired="必填信息">
				</sys:treeselect>
                <span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		&lt;%&ndash;<div class="control-group">&ndash;%&gt;
			&lt;%&ndash;<label class="control-label">采购中心：</label>&ndash;%&gt;
			&lt;%&ndash;<div class="controls">&ndash;%&gt;
					&lt;%&ndash;<sys:treeselect id="centerOffice" name="centerOffice.id" value="${bizOpShelfSku.centerOffice.id}" labelName="centerOffice.name"&ndash;%&gt;
                     &lt;%&ndash;labelValue="${bizOpShelfSku.centerOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true"&ndash;%&gt;
                                    &lt;%&ndash;title="采购中心"  url="/sys/office/queryTreeList?type=8" extId="${centerOffice.id}"&ndash;%&gt;
                                    &lt;%&ndash;cssClass="input-xlarge "&ndash;%&gt;
                                    &lt;%&ndash;allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息">&ndash;%&gt;
                    &lt;%&ndash;</sys:treeselect>&ndash;%&gt;
				&lt;%&ndash;<span class="help-inline"> </span>&ndash;%&gt;

			&lt;%&ndash;</div>&ndash;%&gt;
		&lt;%&ndash;</div>&ndash;%&gt;

		<c:if test="${bizOpShelfSku.id != null}">
				<div class="control-group">
					<label class="control-label">上架人：</label>
					<div class="controls">
						<form:input path="createBy.name" htmlEscape="false" maxlength="11" class="input-xlarge" readonly="true"/>
							&lt;%&ndash;<span class="help-inline"><font color="red">*</font> </span>&ndash;%&gt;
					</div>
				</div>
		</c:if>
		&lt;%&ndash;<div class="control-group">
			<label class="control-label">上架人：</label>
			<div class="controls">
				<form:input path="createBy.name" htmlEscape="false" maxlength="11" class="input-xlarge" readonly="true"/>
				&lt;%&ndash;<span class="help-inline"><font color="red">*</font> </span>&ndash;%&gt;
			</div>
		</div>&ndash;%&gt;

		<div class="control-group">
			<label class="control-label">上架数量(个)：</label>
			<div class="controls">
				<form:input path="shelfQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">工厂价(元)：</label>
			<div class="controls">
				<form:input path="orgPrice" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">现价(元)：</label>
			<div class="controls">
				<form:input path="salePrice" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">* </font><font>(销售单价)</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最低销售数量(个)：</label>
			<div class="controls">
				<form:input path="minQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">* </font><font>(该单价所对应的)</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最高销售数量(个)：</label>
			<div class="controls">
				<form:input path="maxQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">* </font><font>(该单价所对应的，0：不限制)</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上架时间：</label>
			<div class="controls">
				<input name="shelfTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					value="<fmt:formatDate value="${bizOpShelfSku.shelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<c:if test="${bizOpShelfSku.id != null}">
				<div class="control-group">
					<label class="control-label">下架人：</label>
					<div class="controls">
						<form:input path="createBy.name" htmlEscape="false" maxlength="11" class="input-xlarge" readonly="true"/>
					</div>
				</div>
		</c:if>
		&lt;%&ndash;<c:if test="${bizOpShelfSku.id != null}">
		<div class="control-group">
			<label class="control-label">下架人：</label>
			<div class="controls">
				<form:input path="createBy.name" htmlEscape="false" maxlength="11" class="input-xlarge" readonly="true"/>
			</div>
		</div>
		</c:if>&ndash;%&gt;
		<div class="control-group">
			<label class="control-label">下架时间：</label>
			<div class="controls">
				<input name="unshelfTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${bizOpShelfSku.unshelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">显示次序：</label>
			<div class="controls">
				<form:input path="priority" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>--%>
		<div class="form-actions">
			<input type="button" onclick="check()">
			<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
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

</body>
</html>
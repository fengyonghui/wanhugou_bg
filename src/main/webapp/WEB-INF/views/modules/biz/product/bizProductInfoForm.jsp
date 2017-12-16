<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品信息表管理</title>
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
                    $("#cateIds").val(ids);

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
            var setting = {check:{enable:true,nocheckInherit:true},view:{selectedMulti:false},
                data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
                    tree.checkNode(node, !node.checked, true, true);
                    return false;
                },onCheck: zTreeOnCheck
            }}
            function zTreeOnCheck(event, treeId, treeNode) {
                var ids = [], nodes = tree.getCheckedNodes(true);
                for(var i=0; i<nodes.length; i++) {
                    if(!nodes[i].isParent){
                        ids.push(nodes[i].id);
                    }

                }
                alert(ids.toString());
                $.post("${ctx}/biz/product/bizProdCate/findCatePropInfoMap",
                    {catIds:ids.toString()},
                    function(data) {
                        $.map(data, function (PropertyInfo,List<BizCatePropValue>) {
							alert(data+"========="+value);
                        })
                    })

             //   alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
            };

            // 分类--菜单
            var zNodes=[
                    <c:forEach items="${cateList}" var="cate">{id:"${cate.id}", pId:"${not empty cate.parent.id?cate.parent.id:0}", name:"${not empty cate.parent.id?cate.name:'分类列表'}"},
                </c:forEach>];
            // 初始化树结构
            var tree = $.fn.zTree.init($("#cateTree"), setting, zNodes);
            // 不选择父节点
            tree.setting.check.chkboxType = { "Y" : "ps", "N" : "ps" };
            // 默认选择节点
            var ids = "${entity.cateIds}".split(",");
            for(var i=0; i<ids.length; i++) {
                var node = tree.getNodeByParam("id", ids[i]);
                try{tree.checkNode(node, true, false);
                    tree.checkNode(node.getParentNode(), true, false);
                }catch(e){}
            }
            // 默认展开全部节点
            tree.expandAll(true);
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/product/bizProductInfo/">产品信息表列表</a></li>
		<li class="active"><a href="${ctx}/biz/product/bizProductInfo/form?id=${bizProductInfo.id}">产品信息表<shiro:hasPermission name="product:bizProductInfo:edit">${not empty bizProductInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:product:bizProductInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizProductInfo" action="${ctx}/biz/product/bizProductInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">商品名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品代码：</label>
			<div class="controls">
				<form:input path="prodCode" htmlEscape="false" maxlength="10" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">请选择品牌:</label>
			<div class="controls">
				<from:select path="catePropValue.id" items="${catePropValueList}" itemLabel="value" itemValue="id" htmlEscape="false" class="input-xlarge required"/>
								<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">商品描述：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">请选择供应商：</label>
			<div class="controls">
				<sys:treeselect id="office" name="office.id" value="${entity.office.id}"  labelName="office.name"
								labelValue="${entity.office.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="供应商"  url="/sys/office/queryTreeList?type=7" extId="${office.id}"
								cssClass="input-xlarge required"
								allowClear="${office.currentUser.admin}"  dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">请选择商品分类：</label>
			<div class="controls">
				<div id="cateTree" class="ztree" style="margin-top:3px;float:left;"></div>
				<form:hidden path="cateIds"/>
				<%--<span class="help-inline"><font color="red">*</font> </span>--%>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品属性：</label>
			<div class="controls">
				<div id="cateProp"  style="margin-top:3px;float:left;"></div>
				<input type="checkbox" />
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:product:bizProductInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
			<%--<th>商品产品Id</th>--%>
			<th>sku类型</th>
			<th>商品名称</th>
			<th>商品编码</th>
			<th>基础售价</th>
			<th>采购价格</th>
			<th>更新人</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${entity.skuInfosList}" var="bizSkuInfo">
		<tr>
			<%--<td><a href="${ctx}/biz/product/bizProductInfo/form?id=${bizSkuInfo.id}">
					${bizSkuInfo.id}</a>
			</td>--%>
			<td>
					${bizSkuInfo.skuType}
			</td>
			<td>
					${bizSkuInfo.name}
			</td>
			<td>
					${bizSkuInfo.partNo}
			</td>
			<td>
					${bizSkuInfo.basePrice}
			</td>
			<td>
					${bizSkuInfo.buyPrice}
			</td>
			<td>
					${bizSkuInfo.updateBy.name}
			</td>
			<shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><td>
				<a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}">修改</a>
				<a href="${ctx}/biz/sku/bizSkuInfo/delete?id=${bizSkuInfo.id}" onclick="return confirmx('确认要删除该商品sku吗？', this.href)">删除</a>
			</td></shiro:hasPermission>
		</tr>
		</c:forEach>
		<div>
		<a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}&productInfo.id=${bizProductInfo.id}">增加</a>
		</div>
</body>
</html>
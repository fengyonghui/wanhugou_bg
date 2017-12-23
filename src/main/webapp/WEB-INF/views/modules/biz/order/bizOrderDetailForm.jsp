<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单详情管理</title>
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizOrderDetail/">订单详情列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizOrderDetail/form?id=${bizOrderDetail.id}">订单详情<shiro:hasPermission name="biz:order:bizOrderDetail:edit">${not empty bizOrderDetail.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizOrderDetail:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOrderDetail" action="${ctx}/biz/order/bizOrderDetail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
            <label class="control-label">商品名称：</label>
                <div class="controls">
                    <sys:treeselect id="skuInfo" name="skuInfo.id" value="${bizOpShelfSku.skuProd.id}" labelName="skuInfo.name"
                            labelValue="${bizOpShelfSku.skuInfo.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
                            title="商品名称"  url="/biz/product/bizProductInfo/querySkuTreeList" extId="${skuInfo.id}"
                            cssClass="input-xlarge required"
                            allowClear="${skuInfo.currentUser.admin}"  dataMsgRequired="必填信息">
                        <span class="help-inline"><font color="red">*</font> </span>
                    </sys:treeselect>
                </div>
		</div>
		<div class="control-group">
			<label class="control-label">商品编码：</label>
			<div class="controls">
				<form:input path="lineNo" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品单价：</label>
			<div class="controls">
				<form:input path="pLineNo" htmlEscape="false" maxlength="11" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品数量：</label>
			<div class="controls">
				<form:input path="ordQty" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

    	<%--<sys:message content="${message}"/>--%>
    	<div class="breadcrumb">
    		<%--<form id="assignRoleForm" action="${ctx}/sys/role/assignrole" method="post" class="hide">
    			<input type="hidden" name="id" value="${role.id}"/>
    			<input id="idsArr" type="hidden" name="idsArr" value=""/>
    		</form>--%>
    		<input id="assignButton" class="btn btn-primary" type="button" value="保存商品"/>
    		<script type="text/javascript">
    			$("#assignButton").click(function(){
    				alert("测试");
    		</script>
    	</div>
    	<table id="contentTable" class="table table-striped table-bordered table-condensed">
    		<thead><tr><th>商品名称</th><th>商品编码</th><th>商品单价</th><th>商品数量</th><shiro:hasPermission name="sys:user:edit"><th>操作</th></shiro:hasPermission></tr></thead>
    		<tbody>
    		<c:forEach items="${userList}" var="user">
    			<tr>
    				<td>测试1</td>
    				<td>测试2</td>
    				<td><a href="${ctx}/sys/user/form?id=${user.id}">333</a></td>
    				<td>5</td>
    				<td>修改|移除</td>
    				<%--<td>${user.mobile}</td>--%>
    				<shiro:hasPermission name="sys:role:edit"><td>
    					<a href="${ctx}/sys/role/outrole?userId=${user.id}&roleId=${role.id}"
    						onclick="return confirmx('确认要将选购<b>[${user.name}]</b>从<b>[${role.name}]</b>列表中移除吗？', this.href)">移除</a>
    				</td></shiro:hasPermission>
    			</tr>
    		</c:forEach>
    		</tbody>
    	</table>

    	<div class="form-actions">
            <shiro:hasPermission name="biz:order:bizOrderDetail:edit">
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="提交订单详情"/>&nbsp;</shiro:hasPermission>
            <input id="btnCancel" class="btn" type="button" value="返回订单列表" onclick="history.go(-1)"/>
        </div>
       </form:form>
</body>
</html>
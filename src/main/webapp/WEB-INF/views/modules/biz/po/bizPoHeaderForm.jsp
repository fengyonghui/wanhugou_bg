<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购订单管理</title>
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
		<li><a href="${ctx}/biz/po/bizPoHeader/">采购订单列表</a></li>
		<li class="active"><a href="${ctx}/biz/po/bizPoHeader/form?id=${bizPoHeader.id}">采购订单<shiro:hasPermission name="biz:po:bizPoHeader:edit">${not empty bizPoHeader.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:po:bizPoHeader:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizPoHeader" action="${ctx}/biz/po/bizPoHeader/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		

		<div class="control-group">
			<label class="control-label">供应商：</label>
			<div class="controls">
				<sys:treeselect id="vendOffice" name="vendOffice.id" value="${bizPoHeader.vendOffice.id}" labelName="vendOffice.name"
								labelValue="${bizPoHeader.vendOffice.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="供应商"  url="/sys/office/queryTreeList?type=7" cssClass="input-xlarge required" dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单详情总价：</label>
			<div class="controls">
				<form:input readonly="true" path="totalDetail" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">订单总费用：</label>
			<div class="controls">
				<form:input path="totalExp"  htmlEscape="false"  class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">运费：</label>
			<div class="controls">
				<form:input path="freight" htmlEscape="false" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发票状态：</label>
			<div class="controls">
				<form:select path="invStatus" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_order_invStatus')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">业务状态：</label>
			<div class="controls">
				<form:select path="bizStatus" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('biz_po_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:po:bizPoHeader:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
			<th>详情行号</th>
			<th>商品编号</th>
			<th>商品名称</th>
			<th>商品单价</th>
			<th>采购数量</th>
			<shiro:hasPermission name="biz:po:bizPoDetail:edit"><th>操作</th></shiro:hasPermission>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${bizPoHeader.poDetailList}" var="bizPoDetail">
			<tr>
				<td><a href="${ctx}/biz/po/bizPoDetail/form?id=${bizPoDetail.id}">
						${bizPoDetail.lineNo}
				</a></td>
				<td>
						${bizPoDetail.partNo}
				</td>
				<td>
						${bizPoDetail.skuName}
				</td>
				<td>
						${bizPoDetail.unitPrice}
				</td>
				<td>
						${bizPoDetail.ordQty}
				</td>
				<shiro:hasPermission name="biz:po:bizPoDetail:edit"><td>
					<a href="${ctx}/biz/po/bizPoDetail/form?id=${bizPoDetail.id}">修改</a>
					<a href="${ctx}/biz/po/bizPoDetail/delete?id=${bizPoDetail.id}" onclick="return confirmx('确认要删除该采购订单详细信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="form-actions">
		<c:if test="${bizPoHeader.id !=null && bizPoHeader.id!='' }">
			<c:if test="${bizPoHeader!=null && bizPoHeader.poDetailList!=null&&bizPoHeader.poDetailList.size()!=0}">
				<c:set value="${bizPoHeader.poDetailList[bizPoHeader.poDetailList.size()-1].lineNo}" var="lineNo"></c:set>
			</c:if>
			<shiro:hasPermission name="biz:po:bizPoDetail:edit"><input type="button"
																	   onclick="javascript:window.location.href='${ctx}/biz/po/bizPoDetail/form?&poHeader.id=${bizPoHeader.id}&lineNo=${lineNo==null?1:lineNo+1}';"
																	   class="btn btn-primary"
																	   value="增加采购订单详情"/></shiro:hasPermission>
		</c:if>
	</div>
</body>
</html>
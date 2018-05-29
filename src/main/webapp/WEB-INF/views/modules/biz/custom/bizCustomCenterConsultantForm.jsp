<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户专员管理</title>
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
			$('.select_all').live('click',function(){
                var choose=$(".value_user");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/custom/bizCustomCenterConsultant/">客户专员列表</a></li>
		<li class="active"><a href="${ctx}/biz/custom/bizCustomCenterConsultant/form?id=${bizCustomCenterConsultant.id}">经销店<shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:edit">${not empty bizCustomCenterConsultant.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:custom:bizCustomCenterConsultant:edit">查看</shiro:lacksPermission></a></li>
    </ul><br/>
	<form:form id="inputForm" modelAttribute="bizCustomCenterConsultant" action="${ctx}/biz/custom/bizCustomCenterConsultant/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">经销店列表：</label>
			<div class="controls" style="height: 300px;overflow:auto;">
				<table class="table table-striped table-bordered table-condensed">
					<thead>
					<tr>
						<th><input class="select_all"  type="checkbox"/></th>
						<th>经销店名称</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach items="${entity.officeList}" var="off" varStatus="userIndex">
						<tr>
							<td>
								<input name="officeIds" class="value_user" type="checkbox"  value="${off.id}"/>
							</td>
							<td>
								${off.name}
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<sys:treeselect id="centers" name="centers.id" value="${entity.centers.id}" labelName="centers.name"
								labelValue="${entity.centers.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="采购中心" url="/sys/office/queryTreeList?type=8" cssClass="input-xlarge required"
								allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">客户专员：</label>
			<div class="controls">
				<sys:treeselect id="consultants" name="consultants.id" value="${entity.consultants.id}" labelName="consultants.name"
								labelValue="${entity.consultants.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
								title="客户专员" url="/sys/user/treeData?officeId=2" cssClass="input-xlarge required"
								allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

<%--添加采购商--%>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>经销店名称</th>
        <shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><th>操作</th></shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="" var="bizSkuInfo">
        <tr>
            <td><a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}">
                ${bizSkuInfo.name}
            </a>
            </td>
            <shiro:hasPermission name="biz:sku:bizSkuInfo:edit"><td>
                <a href="${ctx}/biz/sku/bizSkuInfo/delete?id=${bizSkuInfo.id}&sign=1" onclick="return confirmx('确认要删除该商品sku吗？', this.href)">删除</a>
            </td></shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
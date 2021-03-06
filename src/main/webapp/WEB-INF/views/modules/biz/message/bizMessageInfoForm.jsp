<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.wanhutong.backend.modules.enums.BizMessageCompanyTypeEnum" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发送站内信管理</title>
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

			var urlStatusVal = '${entity.urlStatus}';
			if (urlStatusVal == 1) {
			    $("#urlStatus").attr('checked', true);
                $("#urlStatus").attr('value', "1");
            }

            if ('${companyIdTypeList}' != null && '${companyIdTypeList}' != '') {
                var companyIdTypeList = JSON.parse('${companyIdTypeList}');
                $.each(companyIdTypeList, function (index, companyIdType) {
                    switch (companyIdType)
                    {
                        case ${BizMessageCompanyTypeEnum.CONSUMER.type}:
                            $("#consumerType").attr('checked', true);
                            break;
                        case ${BizMessageCompanyTypeEnum.CONSIGNEE.type}:
                            $("#consigneeType").attr('checked', true);
                            break;
                        case ${BizMessageCompanyTypeEnum.PURCHASERS.type}:
                            $("#purchasersType").attr('checked', true);
                            break;
                        case ${BizMessageCompanyTypeEnum.SUPPLY_CHAIN.type}:
                            $("#supplyChainType").attr('checked', true);
                            break;
                        case ${BizMessageCompanyTypeEnum.OTHER_TYPE.type}:
                            $("#otherType").attr('checked', true);

                            break;
                        default:
                            break;
                    }
                });
            }
		});
		
		function btnSaveType(saveType) {
		    var companyId = $("#companyId").val();
		    var orherTypeFlag = true;
		    var orherTypeCheckedFlag = true;
            var params = new Array();
            $(".companyIdType").each(function () {
                if(this.checked){
                    var companyIdType = $(this).attr("value")
					if(companyIdType == '${BizMessageCompanyTypeEnum.OTHER_TYPE.type}' && (companyId == null || companyId == '')) {
                        orherTypeFlag = false;
                    }

                    if(companyIdType == '${BizMessageCompanyTypeEnum.OTHER_TYPE.type}') {
                        orherTypeCheckedFlag = false;
                    }

                    params.push(companyIdType);
                }
            })

			var orherTypeCheckedAndCompanyId = true;
			if (orherTypeCheckedFlag == true && companyId != null && companyId != '') {
                orherTypeCheckedAndCompanyId = false;
            }

            if(params.length == 0) {
                alert("未勾选待发送的用户！");
                return false;
            }

            if (orherTypeFlag == false || orherTypeCheckedAndCompanyId == false) {
                alert("部分用户复选框和对应公司必须同时选中！");
                return false;
            }

            $("#inputForm").attr("action", "${ctx}/biz/message/bizMessageInfo/save?saveType=" + saveType + "&companyIdTypeList=" + params);
            $("#inputForm").submit();
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/message/bizMessageInfo/">站内信列表</a></li>
		<li class="active"><a href="${ctx}/biz/message/bizMessageInfo/form?id=${bizMessageInfo.id}">发送站内信<shiro:hasPermission name="biz:message:bizMessageInfo:edit">${not empty bizMessageInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:message:bizMessageInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizMessageInfo" action="${ctx}/biz/message/bizMessageInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input type="hidden" name="str" value="${bizMessageInfo.str}"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">标题：</label>
			<div class="controls">
				<form:input path="title" htmlEscape="false" maxlength="128" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">选择发送用户：</label>
			<div class="controls">
				<input title="num" class="companyIdType" name="companyIdType" id="consumerType" type="checkbox" value="${BizMessageCompanyTypeEnum.CONSUMER.type}" />全部零售商
				&nbsp;&nbsp;
				<input title="num" class="companyIdType" name="companyIdType" id="consigneeType" type="checkbox" value="${BizMessageCompanyTypeEnum.CONSIGNEE.type}" />全部代销商
				&nbsp;&nbsp;
				<input title="num" class="companyIdType" name="companyIdType" id="purchasersType" type="checkbox" value="${BizMessageCompanyTypeEnum.PURCHASERS.type}" />全部经销商
				&nbsp;&nbsp;
				<input title="num" class="companyIdType" name="companyIdType" id="supplyChainType" type="checkbox" value="${BizMessageCompanyTypeEnum.SUPPLY_CHAIN.type}" />全部供应商
				&nbsp;&nbsp;

				<input title="num" class="companyIdType" name="companyIdType" id="otherType" type="checkbox" value="${BizMessageCompanyTypeEnum.OTHER_TYPE.type}" />部分用户
				&nbsp;&nbsp;
				<sys:treeselect id="company" name="companyId" value="${entity.companyId}" labelName="company.name" labelValue="${entity.companyName}"
								title="公司" url="/sys/office/treeData?isAll=true" cssClass="input-small" allowClear="true"/>
				&nbsp;&nbsp;
				<span style="color: red">※：勾选部分用户时，需在下拉菜单中选择对应公司</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容：</label>
			<div class="controls">
				<form:textarea path="content" htmlEscape="false" rows="4" class="input-xxlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">url：</label>
			<div class="controls">
				<form:input path="url" htmlEscape="false" maxlength="128" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发布时间：</label>
			<div class="controls">
				<input name="releaseTime" id="releaseTime" type="text" readonly="readonly"
					   maxlength="20"
					   class="input-medium Wdate required"
					   value="<fmt:formatDate value="${entity.releaseTime}"  pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"
					   placeholder="必填！"/>
				<span class="help-inline"><font color="red">*</font></span>
				&nbsp;&nbsp;
				<input type="checkbox" value="0" id="urlStatus" name="urlStatus" onclick="this.value=(this.value==0)?1:0">产品链接
			</div>
		</div>



		<div class="form-actions">
			<c:if test="${entity.str != 'detail'}">
				<shiro:hasPermission name="biz:message:bizMessageInfo:edit">
					<input id="btnSave" class="btn btn-primary" type="button" value="保存暂不发送" data-toggle="modal" data-target="#modalSave"/>&nbsp;

					<input id="btnSaveAndSend" class="btn btn-primary" type="button" value="保存并发送" data-toggle="modal" data-target="#SaveAndSend" />&nbsp;
				</shiro:hasPermission>
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>


	<div class="modal fade" id="modalSave" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" style="text-align: center;">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabel" >提示</h4>
				</div>
				<div class="modal-body" style="text-align: center;">
					是否发布此条站内信，一旦发布不可撤回！请谨慎！
				</div>
				<div class="modal-footer" style="text-align: center;">
					<button type="button" onclick="btnSaveType('save')" class="btn btn-primary">保存暂不发送</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">返回</button>
				</div>
			</div>
		</div>
	</div>

	<div class="modal fade" id="SaveAndSend" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" style="text-align: center;">
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
					<h4 class="modal-title" id="myModalLabe2" >提示</h4>
				</div>
				<div class="modal-body" style="text-align: center;">
					是否发布此条站内信，一旦发布不可撤回！请谨慎！
				</div>
				<div class="modal-footer" style="text-align: center;">
					<button type="button" onclick="btnSaveType('saveAndSend')" class="btn btn-primary">保存并发送</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">返回</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
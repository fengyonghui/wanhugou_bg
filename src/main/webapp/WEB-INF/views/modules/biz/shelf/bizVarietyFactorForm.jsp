<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品类阶梯价管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
				    var variety = $("#variety").val();
				    var serviceFactor = $("#serviceFactor").val();
                    var minQty = $("#minQty").val();
                    var maxQty = $("#maxQty").val();
				    if (parseInt(serviceFactor) < 0 || parseInt(minQty) < 0 || parseInt(maxQty) < 0) {
				        alert("系数和最大最小数量不能为负");
				        return false;
					}else if (parseInt(minQty) > parseInt(maxQty)) {
				        alert("最小数必须小于最大数");
				        return false;
					}else {
				        $.ajax({
							type:"post",
							url:"${ctx}/biz/shelf/bizVarietyFactor/checkRepeat",
							data:{variety:variety,serviceFactor:serviceFactor,minQty:minQty,maxQty:maxQty},
							success:function (data) {
								if (data=="false") {
								    alert("已经存在该区间数量");
								    return false;
                                }else {
                                    loading('正在提交，请稍等...');
                                    form.submit();
                                }
                            }
						});
                    }
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
		<li><a href="${ctx}/biz/shelf/bizVarietyFactor/">品类阶梯价列表</a></li>
		<li class="active"><a href="${ctx}/biz/shelf/bizVarietyFactor/form?id=${bizVarietyFactor.id}">品类阶梯价<shiro:hasPermission name="biz:shelf:bizVarietyFactor:edit">${not empty bizVarietyFactor.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizVarietyFactor:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizVarietyFactor" action="${ctx}/biz/shelf/bizVarietyFactor/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">品类：</label>
			<div class="controls">
				<form:select id="variety" path="varietyInfo.id" class="input-xlarge required">
					<form:option label="全部" value=""/>
					<form:options items="${varietyList}" itemLabel="name" itemValue="id"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最小数量：</label>
			<div class="controls">
				<input id="minQty" name="minQty" type="number" value="${bizVarietyFactor.minQty}" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最大数量：</label>
			<div class="controls">
				<input id="maxQty" name="maxQty" type="number" value="${bizVarietyFactor.maxQty}" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">服务费系数：</label>
			<div class="controls">
				<input id="serviceFactor" name="serviceFactor" type="number" value="${bizVarietyFactor.serviceFactor}" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:shelf:bizVarietyFactor:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
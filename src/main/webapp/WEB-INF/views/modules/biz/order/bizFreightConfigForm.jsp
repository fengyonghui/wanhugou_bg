<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费设置管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
                    var i = 0;
                    $("input[data-def='defaultStatus'][checked='checked']").each(function () {
                        i = parseInt(i) + parseInt(1);
                    });
                    if (i != 1) {
                        alert("请选择一个默认的服务费设置");
                        return false;
					}
					var officeId = $("#officeId").val();
                    var variId = $("#variId").val();
                    var length = $("input[data-def='id']").length;
                    var flag1 = true;
                    if (length == 0) {
                        $.ajax({
							type:"post",
							url:"${ctx}/biz/order/bizFreightConfig/selectFreightConfig",
							data:{"officeId":officeId,"variId":variId},
							success:function (data) {
								if (data == 'error') {
                                    alert("已经有了该采购中心和该品类的服务费");
								    flag1 = false;
                                }
                            }
						});
					}
					var flag = true;
                    var flag2 = true;
					$("input[data-def='minDistance']").each(function () {
					    var minDistance = $(this).val();
						var maxDistance = $(this).parent().find("input[data-def='maxDistance']").val();
						var nextMinDistance = $(this).parent().parent().next().find("input[data-def='minDistance']").val();
						if (parseFloat(minDistance) > parseFloat(maxDistance)) {
						    alert("同一行前面的公里数必须小于后面的公里数");
						    flag = false;
						    return false;
						}
						if (nextMinDistance != null && parseFloat(maxDistance) > parseFloat(nextMinDistance)) {
						    alert("前一行后面的公里数必须小于等于后一行的前面的公里数");
						    flag2 = false;
						    return false;
						}
                    });
                    if (flag1 && flag && flag2) {
                        $Mask.AddLogo("正在加载");
                        loading('正在提交，请稍等...');
                        form.submit();
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

		function add(obj,len) {
		    var html = "";
			len = parseInt(len) + parseInt(1);
		    html += "<tr>";
		    html += $(obj).parent().parent().html();
            html += "</tr>";
			$(obj).parent().parent().after(html);
            $(obj).parent().parent().next().find("input[data-def='type']").attr("name","freightConfigList["+len+"].type");
            $(obj).parent().parent().next().find("input[data-def='feeCharge']").attr("name","freightConfigList["+len+"].feeCharge");
            $(obj).parent().parent().next().find("input[data-def='minDistance']").attr("name","freightConfigList["+len+"].minDistance");
            $(obj).parent().parent().next().find("input[data-def='maxDistance']").attr("name","freightConfigList["+len+"].maxDistance");
            $(obj).parent().parent().next().find("input[data-def='maxDistance']").attr("name","freightConfigList["+len+"].maxDistance");
            $(obj).parent().parent().next().find("input[data-def='defaultStatus']").attr("name","freightConfigList["+len+"].defaultStatus");
            $(obj).parent().parent().next().find("input[type='button']").attr("onclick","add(this,"+len+")");
            $(obj).parent().parent().find("input[type='button']").remove();
        }

        function checkOne(obj) {
		    var i = 0;
		    if ($(obj).attr("checked") == 'checked') {
                $(obj).attr("checked","checked");
            } else {
                $(obj).removeAttr("checked");
			}
            $("input[data-def='defaultStatus'][type='checkbox'][checked='checked']").each(function () {
				i = parseInt(i) + parseInt(1);
            });
            if (i > 1) {
                $(obj).removeAttr("checked");
                alert("只能选择一个默认的设置");
			}
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizFreightConfig/">服务费设置列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizFreightConfig/form?office.id=${bizFreightConfig.office.id}&varietyInfo.id=${bizFreightConfig.varietyInfo.id}">服务费设置<shiro:hasPermission name="biz:order:bizFreightConfig:edit">${not empty bizFreightConfig.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizFreightConfig:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizFreightConfig" action="${ctx}/biz/order/bizFreightConfig/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="typeLength" type="hidden" value="${fn:length(typeList)}"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<form:select id="officeId" path="office.id" class="input-medium required">
					<form:option value="" label="请选择"/>
					<c:forEach items="${centerList}" var="center">
						<form:option value="${center.id}" label="${center.name}"/>
					</c:forEach>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">品类：</label>
			<div class="controls">
				<form:select id="variId" path="varietyInfo.id" class="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('service_vari')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">服务费：</label>
			<div class="controls">
				<table>
					<c:if test="${bizFreightConfig.office.id == null && bizFreightConfig.varietyInfo.id == null}">
					<c:forEach items="${typeList}" var="type" varStatus="i">
						<tr>
							<td>${type.label}<input data-def="type" name="freightConfigList[${i.index}].type" type="hidden" value="${type.value}"/></td>
							<td><input data-def="feeCharge" name="freightConfigList[${i.index}].feeCharge" type="number" min="0" class="input-mini required"/>元/支(元/套)</td>
							<c:if test="${i.index == 1}">
                            	<td>(&nbsp;<input data-def="minDistance" name="freightConfigList[${i.index}].minDistance" type="number" min="0" class="input-mini required"/>KM&nbsp;-&nbsp;
									<input data-def="maxDistance" name="freightConfigList[${i.index}].maxDistance" type="number" min="0" class="input-mini required"/>KM&nbsp;)
									<input data-def="defaultStatus" name="freightConfigList[${i.index}].defaultStatus" type="checkbox" value="1" onclick="checkOne(this)"/>
									<input type="button" class="btn btn-primary" value="添加" onclick="add(this,${fn:length(typeList)} - 1)"/>
								</td>
							</c:if>
						</tr>
					</c:forEach>
					</c:if>
					<c:if test="${bizFreightConfig.office.id != null && bizFreightConfig.varietyInfo.id != null}">
						<c:forEach items="${freightList}" var="freight" varStatus="i">
							<tr><input data-def="id" name="freightConfigList[${i.index}].id" type="hidden" value="${freight.id}"/>
								<td>${fns:getDictLabel(freight.type,'service_cha','')}<input data-def="type" name="freightConfigList[${i.index}].type" type="hidden" value="${freight.type}"/></td>
								<td><input data-def="feeCharge" name="freightConfigList[${i.index}].feeCharge" type="number" min="0" class="input-mini required" value="${freight.feeCharge}"/>元/支(元/套)</td>
								<c:if test="${freight.type == 2}">
									<td>(&nbsp;<input data-def="minDistance" name="freightConfigList[${i.index}].minDistance" type="number" min="0" class="input-mini required" value="${freight.minDistance}"/>KM&nbsp;-&nbsp;
										<input data-def="maxDistance" name="freightConfigList[${i.index}].maxDistance" type="number" min="0" class="input-mini required" value="${freight.maxDistance}"/>KM&nbsp;)
										<input data-def="defaultStatus" name="freightConfigList[${i.index}].defaultStatus" type="checkbox" value="1" <c:if test="${freight.defaultStatus == 1}">checked="checked"</c:if> onclick="checkOne(this)"/>
										<c:if test="${i.index == fn:length(freightList) - 2}">
										<input type="button" class="btn btn-primary" value="添加" onclick="add(this,${fn:length(freightList)} - 1)"/>
										</c:if>
									</td>
								</c:if>
							</tr>
						</c:forEach>
					</c:if>
				</table>
			</div>
		</div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizFreightConfig:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
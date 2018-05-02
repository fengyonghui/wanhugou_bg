<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购中心品类阶梯价管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			var bcId = $("#id").val();
			alert(bcId);
			if (bcId != "") {
			    $("#centerId").attr("disabled","disabled");
                $("#centerButton").attr("class","btn disabled");
                $("#shelfId").attr("disabled","disabled");
                $("#varietyId").attr("disabled","disabled");
			}
			$("#inputForm").validate({
				submitHandler: function(form){
				    var centId = $("#centerId").val();
				    var shelfId = $("#shelfId").val();
                    var varietyId = $("#varietyId").val();
                    var bcvfId = $("#id").val();
                    var serviceFactors = "";
                    var minQtys = "";
                    var maxQtys = "";
                    $("input[name='serviceFactors']").each(function () {
						serviceFactors += $(this).val() + ",";
                    });
                    $("input[name='minQtys']").each(function () {
                        minQtys += $(this).val() + ",";
                    });
                    $("input[name='maxQtys']").each(function () {
                        maxQtys += $(this).val() + ",";
                    });
                    alert(centId+"--"+shelfId+"--"+varietyId+"--"+serviceFactors+"--"+minQtys+"--"+maxQtys);
                    $.ajax({
						type:"post",
						url:"${ctx}/biz/shelf/bizCentVarietyFactor/checkRepeat",
						data:{centId:centId,shelfId:shelfId,varietyId:varietyId,serviceFactors:serviceFactors,minQtys:minQtys,maxQtys:maxQtys,id:bcvfId},
						success:function (data) {
						    if (data=="false"){
						        alert("已经有该阶梯价，请查询后再添加");
						        return false;
                            }
                            loading('正在提交，请稍等...');
                            form.submit();
                        }
                    });
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
			$("#add").click(function () {
				var html = "";
				html += "<tr>" +
						"	<td><input name='serviceFactors' type='text' class='input-mini required' value=''></td>" +
						"	<td><input name='minQtys' type='text' class='input-mini required' value=''></td>" +
                		"	<td><input name='maxQtys' type='text' class='input-mini required' value=''></td>" +
						"	<td><input type='button' class='btn btn-primary' value='移除' onclick='removeBtn(this)'/><td>" +
						"</tr>";
                $("#varietyBody").append(html);
            });
		});
		function selectVari(obj) {
			// alert(obj.value);
            $("#varietyBody").text("");
			var variId = obj.value;
			if (variId!=""){
				$.ajax({
					type:"post",
					url:"${ctx}/biz/shelf/bizVarietyFactor/selectVari",
					data:{variId:variId},
					success:function (data) {
					    var html = "";
						$.each(data,function (i,variety) {
							// alert(","+variety.serviceFactor+","+variety.minQty+","+variety.maxQty);
                            html += "<tr>" +
									"	<td><input name='serviceFactors' type='text' class='input-mini required' value='"+variety.serviceFactor+"' /></td>" +
									"	<td><input name='minQtys' type='text' class='input-mini required' value='"+variety.minQty+"' /></td>" +
									"	<td><input name='maxQtys' type='text' class='input-mini required' value='"+variety.maxQty+"' /></td>" +
									"	<td><input type='button' class='btn btn-primary required' value='移除' onclick='removeBtn(this)'/><td>" +
                            		"	</tr>";
                        });
						$("#varietyBody").append(html);
					}
				});
            }
        }
        function removeBtn(obj) {
			$(obj).parent().parent().remove();
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/shelf/bizCentVarietyFactor/">采购中心品类阶梯价列表</a></li>
		<li class="active"><a href="${ctx}/biz/shelf/bizCentVarietyFactor/form?id=${bizCentVarietyFactor.id}">采购中心品类阶梯价<shiro:hasPermission name="biz:shelf:bizCentVarietyFactor:edit">${not empty bizCentVarietyFactor.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizCentVarietyFactor:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizCentVarietyFactor" action="${ctx}/biz/shelf/bizCentVarietyFactor/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">采购中心：</label>
			<div class="controls">
				<sys:treeselect id="center" name="center.id" value="${bizCentVarietyFactor.center.id}" labelName="center.name"
								labelValue="${bizCentVarietyFactor.center.name}"  notAllowSelectParent="true"
								title="采购中心"  url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" cssClass="input-xlarge required" dataMsgRequired="必填信息">
				</sys:treeselect>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货架：</label>
			<div class="controls">
				<form:select id="shelfId" path="shelfInfo.id" class="input-xlarge required">
					<form:option label="全部" value=""/>
					<form:options items="${shelfList}" itemLabel="name" itemValue="id"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">品类：</label>
			<div class="controls">
				<form:select id="varietyId" path="varietyInfo.id" class="input-xlarge required" onclick="selectVari(this)">
					<form:option label="全部" value=""/>
					<form:options items="${varietyList}" itemLabel="name" itemValue="id"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<table id="varietyTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<th>服务费系数</th>
					<th>最小数量</th>
					<th>最大数量</th>
					<c:if test="${bizCentVarietyFactor.id == null}">
						<th>操作</th>
					</c:if>
				</thead>
				<tbody id="varietyBody">
					<c:if test="${bizCentVarietyFactor.id != null}">
						<td><input name='serviceFactors' type='text' class='input-mini required' value='${bizCentVarietyFactor.serviceFactor}' /></td>
						<td><input name='minQtys' type='text' class='input-mini required' value='${bizCentVarietyFactor.minQty}' /></td>
						<td><input name='maxQtys' type='text' class='input-mini required' value='${bizCentVarietyFactor.maxQty}' /></td>
						<%--<td><input type='button' class='btn btn-primary required' value='移除' onclick='removeBtn(this)'/></td>--%>
					</c:if>
				</tbody>
			</table>
		</div>
		<c:if test="${bizCentVarietyFactor.id == null}">
			<div class="control-group">
				<input id='add' type='button' class="btn btn-primary" value='增加'/>
			</div>
		</c:if>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shelf:bizCentVarietyFactor:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
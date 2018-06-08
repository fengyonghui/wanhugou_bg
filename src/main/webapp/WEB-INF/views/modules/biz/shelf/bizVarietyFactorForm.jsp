<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>品类阶梯价管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			if($("#id").val()!=null && $("#id").val()!=""){
				$("#variety").attr("disabled","disabled");
			}
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
				    var factorId = $("#id").val();
				    var varietyIDs=$("#varietyIds").val();
				    var variety = $("#variety").val();
                    var minQty = $("#minQty").val();
                    var maxQty = $("#maxQty").val();
                    var serviceFactor = $("#serviceFactor").val();
				    if (parseInt(minQty) < 1 || parseInt(maxQty) < 1 || parseInt(serviceFactor) < 0) {
				        alert("最小、最大数量不能为 0 或者 负数");
				        return false;
					}else if (parseInt(minQty) > parseInt(maxQty)) {
				        alert("最小数必须小于最大数");
				        return false;
					}else {
						var serviceFactors = "";
						var minQtys = "";
						var maxQtys = "";
						$("input[name='minQtys']").each(function () {
							minQtys += $(this).val() + ",";
						});
						$("input[name='maxQtys']").each(function () {
							maxQtys += $(this).val() + ",";
						});
						$("input[name='serviceFactors']").each(function () {
							serviceFactors += $(this).val() + ",";
						});
                    <%--alert("--"+serviceFactors+"---"+minQtys+"---"+maxQtys);--%>
				        $.ajax({
							type:"post",
							url:"${ctx}/biz/shelf/bizVarietyFactor/checkRepeat",
							data:{variety:variety,id:factorId,serviceFactors:serviceFactors,minQtys:minQtys,maxQtys:maxQtys,varietyIds:varietyIDs},
							success:function (data) {
								if (data=="false") {
								    alert("已经存在该区间数量");
								    return false;
                                }else if(data=="minMax"){
                                	alert("最小数量必须小于最大数量");
								    return false;
                                }else if(data=="error"){
                                	alert("最大数量需填写到 9999");
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
			$("#add").click(function () {
				var html = "";
				html += "<tr>" +
						"	<td><input id='varietyIds' name='varietyIds' type='hidden' class='input-mini' value='add' />"+
						"<input id='minQty' name='minQtys' type='number' style='width: 120px;' class='input-mini required' value=''></td>"+
						"	<td><input id='maxQty' name='maxQtys' type='number' style='width: 120px;' class='input-mini required' value=''></td>" +
                		"	<td><input id='serviceFactor' name='serviceFactors' type='text' placeholder='服务费百分比' style='width: 120px;' class='input-mini required' value=''></td>" +
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
						$("#add").css("display","none");
						if(data!=null && data!=""){
							$.each(data,function (i,variety) {
								// alert(","+variety.minQty+","+variety.maxQty+","+variety.serviceFactor);
								html += "<tr id='trItems_"+variety.id+"'>" +
										"	<td><input id='varietyIds' name='varietyIds' type='hidden' class='input-mini' value='"+variety.id+"' />"+
										"<input id='minQtys' name='minQtys' type='number' class='input-mini required' style='width: 120px;' value='"+variety.minQty+"' /></td>" +
										"	<td><input id='maxQtys' name='maxQtys' type='number' class='input-mini required' style='width: 120px;' value='"+variety.maxQty+"' /></td>" +
										"	<td><input id='serviceFactors' name='serviceFactors' type='text' class='input-mini required' placeholder='服务费百分比' style='width: 120px;' value='"+variety.serviceFactor+"' /></td>" +
										<%--"	<td><input type='button' class='btn btn-primary required' value='移除' onclick='removeBtn(this)'/><td>" +--%>
										"	</tr>";
							});
							$("#varietyBody").append(html);
						}else{
							$("#add").css("display","block");
						}
					}
				});
            }
        }
        function removeBtn(obj) {
			$(obj).parent().parent().remove();
        }
	</script>
	<script type="text/javascript">
	function deleteBtn(a){
		top.$.jBox.confirm("确认要移除该区间数量吗吗？","系统提示",function(v,h,f){
		if(v=="ok"){
			$.ajax({
				type:"post",
				url:"${ctx}/biz/shelf/bizVarietyFactor/deleteAjas",
				data:"id="+a,
				success:function(data){
					if(data=="ok"){
						$("#trRevom_"+a).remove();//主要是删除这tr
						<%--$("#id").val("");//点击删除后把原id为空--%>
					}
				}
			});
		}
		},{buttonsFocus:1});
	}
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
				<form:select id="variety" path="varietyInfo.id" class="input-xlarge required" onclick="selectVari(this)">
					<form:option label="请选择" value=""/>
					<form:options items="${varietyList}" itemLabel="name" itemValue="id"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<table id="varietyTable" class="table table-striped table-bordered table-condensed">
				<thead>
				<th>最小数量</th>
				<th>最大数量</th>
				<th>服务费系数</th>
				<c:if test="${bizVarietyFactor.id != null}">
					<th>操作</th>
				</c:if>
				</thead>
				<tbody id="varietyBody">
				<c:if test="${bizVarietyFactor.id != null}">
					<c:if test="${variList!=null}">
						<c:forEach items="${variList}" var="varietyFactor">
							<tr id="trRevom_${varietyFactor.id}">
								<td>
									<input id="varietyIds"  name="varietyIds" type='hidden' class='input-mini' htmlEscape="false"  value='${varietyFactor.id}' />
									<input id='minQtys'  name='minQtys' type='number' class='input-mini required' style="width: 120px;" htmlEscape="false"  value='${varietyFactor.minQty}' />
								</td>
								<td><input id='maxQtys'  name='maxQtys' type='number' class='input-mini required' style="width: 120px;" htmlEscape="false"  value='${varietyFactor.maxQty}' /></td>
								<td><input id='serviceFactors' name='serviceFactors' type='text' class='input-mini required' style="width: 120px;" placeholder="服务费百分比" htmlEscape="false"  value='${varietyFactor.serviceFactor}' /></td>
								<c:if test="${bizVarietyFactor.id != null}">
									<td><input type='button' class='btn btn-primary required' value='移除' onclick='deleteBtn(${varietyFactor.id})'/><td>
								</c:if>
							</tr>
						</c:forEach>
					</c:if>
				</c:if>
				</tbody>
			</table>
		</div>
		<c:if test="${bizVarietyFactor.id == null}">
		<div class="control-group">
			<input id="add" style="display:none;" type="button" class="btn btn-primary" value="增加"/>
		</div>
		</c:if>
		<c:if test="${bizVarietyFactor.id != null}">
			<div class="control-group">
				<input id="add" type="button" class="btn btn-primary" value="增加"/>
			</div>
		</c:if>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shelf:bizVarietyFactor:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
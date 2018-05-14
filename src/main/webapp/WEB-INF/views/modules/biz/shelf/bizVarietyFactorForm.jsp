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
                    var minQty = $("#minQtys").val();
                    var maxQty = $("#maxQtys").val();
                    var serviceFactor = $("#serviceFactors").val();
				    if (parseInt(minQty) < 0 || parseInt(maxQty) < 0 || parseInt(serviceFactor) < 0) {
				        alert("系数和最大最小数量不能为负");
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
						"<input id='minQtys' name='minQtys' type='number' style='width: 120px;' class='input-mini required' value=''>"+
						"</td>" +
						"	<td><input id='maxQtys' name='maxQtys' type='number' style='width: 120px;' class='input-mini required' value=''></td>" +
                		"	<td><input id='serviceFactors' name='serviceFactors' type='number' placeholder='服务费百分比' style='width: 120px;' class='input-mini required' value=''></td>" +
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
							// alert(","+variety.minQty+","+variety.maxQty+","+variety.serviceFactor);
                            html += "<tr id='trItems_"+variety.id+"'>" +
									"	<td><input id='varietyIds' name='varietyIds' type='hidden' class='input-mini' value='"+variety.id+"' />"+
									"<input id='minQtys' name='minQtys' type='number' class='input-mini required' style='width: 120px;' value='"+variety.minQty+"' /></td>" +
									"	<td><input id='maxQtys' name='maxQtys' type='number' class='input-mini required' style='width: 120px;' value='"+variety.maxQty+"' /></td>" +
									"	<td><input id='serviceFactors' name='serviceFactors' type='number' class='input-mini required' placeholder='服务费百分比' style='width: 120px;' value='"+variety.serviceFactor+"' /></td>" +
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
				<c:if test="${bizVarietyFactor.id == null}">
					<th>操作</th>
				</c:if>
				</thead>
				<tbody id="varietyBody">
				<c:if test="${bizVarietyFactor.id != null}">
					<c:if test="${variList!=null}">
						<c:forEach items="${variList}" var="varietyFactor">
							<tr>
								<td>
									<input id="varietyIds"  name="varietyIds" type='hidden' class='input-mini' htmlEscape="false"  value='${varietyFactor.id}' />
									<input id='minQtys'  name='minQtys' type='number' class='input-mini required' style="width: 120px;" htmlEscape="false"  value='${varietyFactor.minQty}' />
								</td>
								<td><input id='maxQtys'  name='maxQtys' type='number' class='input-mini required' style="width: 120px;" htmlEscape="false"  value='${varietyFactor.maxQty}' /></td>
								<td><input id='serviceFactors' name='serviceFactors' type='number' class='input-mini required' style="width: 120px;" placeholder="服务费百分比" htmlEscape="false"  value='${varietyFactor.serviceFactor}' /></td>
							</tr>
						</c:forEach>
					</c:if>
				</c:if>
				</tbody>
			</table>
		</div>
		<c:if test="${bizVarietyFactor.id == null}">
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
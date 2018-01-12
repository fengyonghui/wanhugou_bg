<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function changeSelect(id){
			 $("#adviserId").html("");
			 $("#adviserId").append("<option value='' selected = 'selected'>==请选择采购顾问==</option>");
			 $("#s2id_adviserId span:eq(0)").html("==请选择采购顾问==");
			if(id != null && id !=undefined && id.trim() != ""){
				 $.ajax({
					url:"${ctx}/sys/user/getAdvisers",
					data:{"office.id":id},
					type:"POST",
					dataType:'json',
					success:function(data){
						for(var i =0;i<data.length;i++){
							$("#adviserId").append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
						}
					},
					error:function(er){
						alert("查询失败");
					}
				});
			}
		}
		
		function submitData(){
			var bcid = $("#bcID").val();
			var adviserid = $("#adviserId").val();
			if(bcid == null || bcid ==undefined || bcid.trim() == ""){
				alert("请选择采购中心")
				return false;
			}
			if(adviserid == null || adviserid ==undefined || adviserid.trim() == ""){
				alert("请选择采购顾问")
				return false;
			}
			$.ajax({
				url:"${ctx}/sys/buyerAdviser/save",
				data:$("#inputForm").serialize(),
				type:"POST",
				dataType:'json',
				success:function(data){
					window.location.href = "${ctx}/sys/office/purchasersList";
				},
				error:function(er){
					alert("关联失败");
				}
			});
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/office/purchasersList">机构列表</a></li>
		<li class="active"><a href="${ctx}/sys/office/purchasersForm?id=${office.id}&parent.id=${office.parent.id}">机构<shiro:hasPermission name="sys:office:edit">${not empty office.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:office:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="office" action="${ctx}/sys/buyerAdviser/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">上级机构:</label>
			<div class="controls">
				<input type="text" value="${office.parent.name}" disabled="disabled" />
				<input  type="hidden" name="custId" value="${office.id}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">归属区域:</label>
			<div class="controls">
				<input type="text" value="${office.area.name}" disabled="disabled" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">机构名称:</label>
			<div class="controls">
				<input type="text" value="${office.name}" disabled="disabled" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">机构编码:</label>
			<div class="controls">
				<input type="text" value="${office.code}" disabled="disabled" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">归属采购中心:</label>
			<div class="controls">
				<select name="centerId" id="bcID" style="width: 18%" onchange="changeSelect(this.value)">
                     <option value="">==请选择采购中心==</option>
                     <c:forEach items="${officeList}" var="item" varStatus="vs">
                         <option value="${item.id}" <c:if test="${item.id == buyerAdviser.centerId}">selected</c:if> > ${item.name}</option>
                     </c:forEach>
                 </select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">归属采购顾问:</label>
			<div class="controls">
				<select name="consultantId" id="adviserId" style="width: 18%">
                     <c:if test="${buyerAdviser.consultantName != null }">
                     	<option value="${buyerAdviser.consultantId}" selected = 'selected'>${buyerAdviser.consultantName}</option>
                     </c:if>
                     <c:if test="${buyerAdviser == null || buyerAdviser.consultantName==null}">
                     	<option value="">==请选择采购顾问==</option>
                     </c:if>
                 </select>
			</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="sys:office:edit"><input id="btnSubmit" class="btn btn-primary" type="button" onclick="submitData()" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
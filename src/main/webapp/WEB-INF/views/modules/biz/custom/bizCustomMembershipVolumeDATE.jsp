<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function changeSelect(id){
			 $("#adviserId").html("");
			 $("#adviserId").append("<option value='' selected = 'selected'>==请选择客户专员==</option>");
			 $("#s2id_adviserId span:eq(0)").html("==请选择客户专员==");
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
				alert("请选择客户专员")
				return false;
			}

			var customsId = $("#customsId").val();
			var officeMobile = $("#officeMobile").val().trim();

			if (customsId == "" && officeMobile == "") {
                alert("请选择经销店或者输入经销店手机号！")
                return false;
			}

			var resultFlag = "0";
			if (officeMobile != "") {
                $.ajax({
                    url:"${ctx}/biz/custom/bizCustomCenterConsultant/checkCustoms",
                    data:{"customs.id": customsId, "officeMobile": officeMobile},
                    type:"POST",
                    dataType:'json',
                    async: false,
                    success:function(data){
                        resultFlag = data;
                    }
                });
			} else {
                resultFlag = "3";
            }

            if (resultFlag == "0") {
                alert("输入手机号不正确！")
                return false;
            }

			if (resultFlag == "1") {
                alert("该手机号对应经销店已被别的客户专员关联！")
                return false;
            }

            if (resultFlag == "2") {
                alert("已选经销店和对应手机号不匹配，请重新选择！")
                return false;
            }

            if (resultFlag == "3") {
                $.ajax({
                    url:"${ctx}/biz/custom/bizCustomCenterConsultant/save",
                    data:$("#inputForm").serialize(),
                    type:"POST",
                    dataType:'json',
                    success:function(data){
                        if (data == "1") {
                            window.location.href = "${ctx}/biz/custom/bizCustomCenterConsultant/list?consultants.id="+$("#adviserId").val()+"&conn=connIndex&office.id="+$("#bcID").val();
                        } else {
                            alert("关联经销店失败！")
                        }
                    },
                    error:function(er){
                        alert("关联经销店失败！");
                    }
                });
            }
		}

	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/biz/custom/bizCustomCenterConsultant/list?consultants.id=${office.id}">经销店列表</a></li>
	<%--<li><a href="${ctx}/biz/custom/bizCustomCenterConsultant/returnConnIndex">经销店列表</a></li>--%>
	<%--<li class="active"><a href="${ctx}/sys/office/purchasersForm?id=${user.id}&parent.id=${office.parent.id}">采购商<shiro:hasPermission name="sys:office:edit">${not empty office.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:office:edit">查看</shiro:lacksPermission></a></li>--%>
    <li class="active"><a href="${ctx}/biz/custom/bizCustomCenterConsultant/connOfficeForm?id=${office.id}">经销店添加</a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="office" action="${ctx}/biz/custom/bizCustomCenterConsultant/save" method="post" class="form-horizontal">
	<%--<form:hidden path="id"/>--%>
	<sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">经销店名称:</label>
        <div class="controls">
            <sys:treeselect id="customs" name="customs.id" value="${page.customs.id}" labelName="customs.name"
                            labelValue="${page.customs.name}" notAllowSelectParent="true"
                            title="经销店" url="/sys/office/queryTreeList?type=6&source=con" cssClass="input-medium required"
                            allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
            <input type="text" name="conn" value="${user.conn}" style="display:none">
        </div>
    </div>
	<div class="control-group">
		<label class="control-label">经销店手机号：</label>
		<div class="controls">
			<form:input path="officeMobile" id="officeMobile" htmlEscape="false" maxlength="30" class="input-xlarge"/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">采购中心:</label>
		<div class="controls">
			<select name="centers.id" id="bcID" style="width: 18%" onchange="changeSelect(this.value)">
				<option value="">请选择采购中心</option>
				<c:forEach items="${officeList}" var="item" varStatus="vs">
					<option value="${item.id}" <c:if test='${bcc.centers.id !=null}'>selected</c:if> > ${item.name}</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">客户专员:</label>
		<div class="controls">
			<select name="consultants.id" id="adviserId" style="width: 18%">
				<c:if test="${bcc.consultants != null }">
					<option value="${bcc.consultants.id}" selected = 'selected'>${bcc.consultants.name}</option>
				</c:if>
				<c:if test="${bcc == null || bcc.consultants.id==null}">
					<option value="">请选择客户专员</option>
				</c:if>
			</select>
		</div>
	</div>

	<div class="form-actions">
		<shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:edit"><input id="btnSubmit" class="btn btn-primary" type="button" onclick="submitData()" value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
</form:form>
</body>
</html>
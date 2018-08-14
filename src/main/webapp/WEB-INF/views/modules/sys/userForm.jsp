<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<%@ page import="com.wanhutong.backend.modules.enums.RoleEnNameEnum" %>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#no").focus();
			$("#inputForm").validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${user.loginName}')}
				},
				messages: {
					loginName: {remote: "用户登录名已存在"},
					confirmNewPassword: {equalTo: "输入与上面相同的密码"}
				},
				submitHandler: function(form){
				    var userId = $("#userId").val();
				    var companyId = $("#companyId").val();
				    var userRoleIds = "";
                    $("input[type=checkbox]:checked").each(function (index) {
                        userRoleIds += $(this).val() + ",";
                    });
				    $.ajax({
						type:"post",
						url:"${ctx}/sys/user/selectConsultant",
						data:{userId:userId,companyId:companyId,userRoleIds:userRoleIds},
						success:function (data) {
							if (data=="false"){
							    alert("该采购专员下关联有经销店，请交接后再修改");
							    return false;
							}
                            var headImg = $("#headImgDiv").find("[customInput = 'headImgImg']");
                            var headImgStr = "";
                            for (var i = 0; i < headImg.length; i ++) {
                                headImgStr += ($(headImg[i]).attr("src") + ",");
                            }
                            $("#photo").val(headImgStr);
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:if test="${flag eq 'ck'}">
			<li class="active"><a>用户详情</a></li>
		</c:if>
		<c:if test="${flag ne 'ck'}">
			<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex' || user.conn eq 'selectIndex'}">
				<li><a href="${ctx}/sys/user/list?company.type=8&conn=${user.conn}">用户列表</a></li>
				<li class="active"><a href="${ctx}/sys/user/form?id=${user.id}&conn=${user.conn}">用户<shiro:hasPermission name="sys:user:edit">${not empty user.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:user:edit">查看</shiro:lacksPermission></a></li>
			</c:if>
			<c:if test="${empty user.conn}">
				<li><a href="${ctx}/sys/user/list">用户列表</a></li>
				<li class="active"><a href="${ctx}/sys/user/form?id=${user.id}">用户<shiro:hasPermission name="sys:user:edit">${not empty user.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:user:edit">查看</shiro:lacksPermission></a></li>
			</c:if>
		</c:if>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/sys/user/save" method="post" class="form-horizontal">
		<form:hidden id="userId" path="id"/>
		<form:hidden path="conn"/>
		<input type="hidden" id="photo" name="photo" value=""/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">头像:
				<p style="opacity: 0.5; color: red;">点击图片删除</p>
			</label>

			<div class="controls">
				<input class="btn" type="file" name="productImg" onchange="submitPic('headImg', true)" value="上传图片" multiple="multiple" id="headImg"/>
			</div>
			<div id="headImgDiv">
				<c:forEach items="${headPhotoList}" var="photo">
					<img src="${photo.imgServer}${photo.imgPath}" customInput="headImgImg" style='width: 100px' onclick="$(this).remove();">
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">归属公司:</label>
			<div class="controls">
				<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex' || user.conn eq 'contact_ck' || user.conn eq 'selectIndex'}">
					<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
									title="公司" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&source=officeConnIndex" cssClass="required"/>
				</c:if>
				<c:if test="${empty user.conn}">
					<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
									title="公司" url="/sys/office/treeData" cssClass="required"/>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">归属部门:</label>
			<div class="controls">
				<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex' || user.conn eq 'contact_ck' || user.conn eq 'selectIndex'}">
					<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
									title="部门" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&source=officeConnIndex" cssClass="required" notAllowSelectParent="true"/>
				</c:if>
				<c:if test="${empty user.conn}">
					<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
									title="部门" url="/sys/office/treeData" cssClass="required" notAllowSelectParent="true"/>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">工号:</label>
			<div class="controls">
				<form:input path="no" htmlEscape="false" maxlength="50" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">姓名:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">登录名:</label>
			<div class="controls">
				<input id="oldLoginName" name="oldLoginName" type="hidden" value="${user.loginName}">
				<form:input path="loginName" htmlEscape="false" maxlength="50" class="required userName"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">密码:</label>
			<div class="controls">
				<input id="newPassword" name="newPassword" type="password" value="" maxlength="50" minlength="3" class="${empty user.id?'required':''}"/>
				<c:if test="${empty user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
				<c:if test="${not empty user.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">确认密码:</label>
			<div class="controls">
				<input id="confirmNewPassword" name="confirmNewPassword" type="password" value="" maxlength="50" minlength="3" equalTo="#newPassword"/>
				<c:if test="${empty user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱:</label>
			<div class="controls">
				<form:input path="email" htmlEscape="false" maxlength="100" class="email"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话:</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">手机:</label>
			<div class="controls">
				<form:input path="mobile" htmlEscape="false" maxlength="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否允许登录:</label>
			<div class="controls">
				<form:select path="loginFlag">
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> “是”代表此账号允许登录，“否”则表示此账号不允许登录</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户类型:</label>
			<div class="controls">
				<form:select path="userType" class="input-xlarge">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('sys_user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户角色:</label>
			<div class="controls">
				<form:checkboxes id="userRoleIds" path="roleIdList" items="${allRoles}" itemLabel="name" itemValue="id" htmlEscape="false" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<c:if test="${not empty user.id}">
			<div class="control-group">
				<label class="control-label">创建时间:</label>
				<div class="controls">
					<label class="lbl"><fmt:formatDate value="${user.createDate}" type="both" dateStyle="full"/></label>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">最后登陆:</label>
				<div class="controls">
					<label class="lbl">IP: ${user.loginIp}&nbsp;&nbsp;&nbsp;&nbsp;时间：<fmt:formatDate value="${user.loginDate}" type="both" dateStyle="full"/></label>
				</div>
			</div>
		</c:if>
		<div class="form-actions">
			<shiro:hasPermission name="sys:user:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
	<script type="text/javascript">
		function submitPic(id, multiple){
			var f = $("#" + id).val();
			if(f==null||f==""){
				alert("错误提示:上传文件不能为空,请重新选择文件");
				return false;
			}else{
				var extname = f.substring(f.lastIndexOf(".")+1,f.length);
				extname = extname.toLowerCase();//处理了大小写
				if(extname!= "jpeg"&&extname!= "jpg"&&extname!= "gif"&&extname!= "png"){
					$("#picTip").html("<span style='color:Red'>错误提示:格式不正确,支持的图片格式为：JPEG、GIF、PNG！</span>");
					return false;
				}
			}
			var file = document.getElementById(id).files;
			var size = file[0].size;
			if(size>2097152){
				alert("错误提示:所选择的图片太大，图片大小最多支持2M!");
				return false;
			}
			ajaxFileUploadPic(id, multiple);
		}

        function ajaxFileUploadPic(id, multiple) {
            $.ajaxFileUpload({
                url : '${ctx}/biz/product/bizProductInfoV2/saveColorImg', //用于文件上传的服务器端请求地址
                secureuri : false, //一般设置为false
                fileElementId : id, //文件上传空间的id属性  <input type="file" id="file" name="file" />
                type : 'POST',
                dataType : 'text', //返回值类型 一般设置为json
                success : function(data, status) {
                    //服务器成功响应处理函数
                    var msg = data.substring(data.indexOf("{"), data.indexOf("}")+1);
                    var msgJSON = JSON.parse(msg);
                    var imgList = msgJSON.imgList;
                    var imgDiv = $("#" + id + "Div");
                    var imgDivHtml = "<img src=\"$Src\" customInput=\""+ id +"Img\" style='width: 100px' onclick=\"$(this).remove();\">";
                    if (imgList && imgList.length > 0 && multiple) {
                        for (var i = 0; i < imgList.length; i ++) {
                            imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                        }
                    }else if (imgList && imgList.length > 0 && !multiple) {
                        imgDiv.empty();
                        for (var i = 0; i < imgList.length; i ++) {
                            imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                        }
                    }else {
                        var img = $("#" + id + "Img");
                        img.attr("src", msgJSON.fullName);
                    }
                },
                error : function(data, status, e) {
                    //服务器响应失败处理函数
                    console.info(data);
                    console.info(status);
                    console.info(e);
                    alert("上传失败");
                }
            });
            return false;
        }
	</script>
</body>
</html>
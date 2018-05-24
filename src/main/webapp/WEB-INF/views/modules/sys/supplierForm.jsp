<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<html>
<head>
    <title>机构管理</title>
    <meta name="decorator" content="default"/>

    <script type="text/javascript">
        $(document).ready(function() {
            var vendId = $("#id").val();
            if (vendId==''){
                $("#primaryPersonButton").attr("class","btn disabled");
            }
            $("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    var phone = document.getElementById("phone").value;
                    if(!(/^1[34578]\d{9}$/.test(phone))){
                        alert("手机号码有误，请输入11位有效手机号");
                        return false;
                    }
                    loading('正在提交，请稍等...');
                    var compactImg = $("#compactImgDiv").find("[customInput = 'compactImgImg']");
                    var compactImgStr = "";
                    for (var i = 0; i < compactImg.length; i ++) {
                        compactImgStr += ($(compactImg[i]).attr("src") + "|");
                    }
                    $("#compactPhotos").val(compactImgStr);

                    var idCardImg = $("#idCardImgDiv").find("[customInput = 'idCardImgImg']");
                    var idCardImgStr = "";
                    for (var i = 0; i < idCardImg.length; i ++) {
                        idCardImgStr += ($(idCardImg[i]).attr("src") + "|");
                    }
                    $("#idCardPhotos").val(idCardImgStr);
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
            $("#saveUser").click(function () {
                var aflag = true;
                var flag = true;
                var name = $("#primaryName").val();
                var loginName = $("#loginName").val();
                var newPassword = $("#newPassword").val();
                var confirmNewPassword = $("#confirmNewPassword").val();
                var primaryMobile = $("#primaryMobile").val();
                if (name == null || name == ''){
                    $("#checkName").append("<font color='red'>必填</font>");
                    flag = false;
				}
				if (loginName == null || loginName == ''){
                    $("#checkLoginName").append("<font color='red'>必填</font>");
                    flag = false;
				}
                if (newPassword == null || newPassword == ''){
                    $("#checkNewPassword").append("<font color='red'>必填</font>");
                    flag = false;
                }
                if (confirmNewPassword == null || confirmNewPassword == ''){
                    $("#checkConfirmNewPassword").append("<font color='red'>必填</font>");
                    flag = false;
                }
                if (primaryMobile == null || primaryMobile == ''){
                    $("#checkPrimaryMobile").append("<font color='red'>必填</font>");
                    flag = false;
                }
                if (flag){

				}else {
                    return false;
				}
                if(!(/^1[34578]\d{9}$/.test(primaryMobile))){
                    alert("手机号码有误，请输入11位有效手机号");
                    return false;
                }
                $.ajax({
                    type:"post",
                    url:"${ctx}/sys/user/findVendorUser",
                    data:{loginName:loginName},
                    success:function (data) {
                        if (data == "false"){
                            alert("该登陆名已经存在");
                            aflag = false;
                            return false;
                        }
                        $("#primaryPersonId").val(null);
                        $("#primaryPersonName").val(name);
                        $("#myModal").modal('hide');
                    }
                });
            });
        });
    </script>
    <script type="text/javascript">
        function SubmitPhone(){
            var phone = document.getElementById("phone").value;
            if(!(/^1[34578]\d{9}$/.test(phone))){
                alert("手机号码有误，请输入11位有效手机号");
                return false;
            }
        }
    </script>

</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sys/office/supplierListGys">机构列表</a></li>
    <li class="active"><a href="${ctx}/sys/office/supplierForm?id=${office.id}&parent.id=${office.parent.id}">机构<shiro:hasPermission name="sys:office:edit">${not empty office.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:office:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="office" action="${ctx}/sys/office/save" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <form:hidden path="gysFlag"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">上级机构:</label>
        <div class="controls">
            <sys:treeselect id="office" name="parent.id" value="${office.parent.id}" labelName="parent.name" labelValue="${office.parent.name}"
                            title="机构" url="/sys/office/queryTreeList?type=7" extId="${office.id}" cssClass="" allowClear="${office.currentUser.admin}"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">归属区域:</label>
        <div class="controls">
            <sys:treeselect id="area" name="area.id" value="${office.area.id}" labelName="area.name" labelValue="${office.area.name}"
                            title="区域" url="/sys/area/treeData" cssClass="required"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">机构名称:</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" maxlength="50" class="required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">机构编码:</label>
        <div class="controls">
            <form:input path="code" htmlEscape="false" maxlength="50"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">机构类型:</label>
        <div class="controls">
            <form:select path="type" class="input-medium">
                <form:options items="${fns:getDictList('sys_office_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">机构级别:</label>
        <div class="controls">
            <form:select path="grade" class="input-medium">
                <form:options items="${fns:getDictList('sys_office_grade')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">是否可用:</label>
        <div class="controls">
            <form:select path="useable">
                <form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
            <span class="help-inline">“是”代表此账号允许登陆，“否”则表示此账号不允许登陆</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">经营品类:</label>
        <div class="controls">
            <form:select id="vendInfo" path="bizVendInfo.bizCategoryInfo.id" class="input-xlarge required">
                <form:option value="" label="请选择"/>
                <c:if test="${! empty bizVendInfo.bizCategoryInfo.id}">
                    <form:option value="${bizVendInfo.bizCategoryInfo.id}">${bizVendInfo.cateName}</form:option>
                </c:if>
                <form:options items="${varietyList}" itemLabel="name" itemValue="id"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">主负责人:</label>
        <div class="controls">
            <sys:treeselect id="primaryPerson" name="primaryPerson.id" value="${office.primaryPerson.id}" labelName="office.primaryPerson.name" labelValue="${office.primaryPerson.name}"
                            title="用户" url="/sys/user/treeData?type=7&officeId=${office.id}" allowClear="true" notAllowSelectParent="true"/>
            <button class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal">添加新负责人</button>
            <font color="red">添加新用户请点击添加新负责人，修改请点击左侧放大镜</font>
        </div>
    </div>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">副负责人:</label>--%>
        <%--<div class="controls">--%>
            <%--<sys:treeselect id="deputyPerson" name="deputyPerson.id" value="${office.deputyPerson.id}" labelName="office.deputyPerson.name" labelValue="${office.deputyPerson.name}"--%>
                            <%--title="用户" url="/sys/user/treeData?type=7&officeId=${office.id}" allowClear="true" notAllowSelectParent="true"/>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div class="control-group">--%>
    <%--<label class="control-label">联系地址:</label>--%>
    <%--<div class="controls">--%>
    <%--<form:input path="address" htmlEscape="false" maxlength="50"/>--%>
    <%--</div>--%>
    <%--</div>--%>
    <div class="control-group">
        <label class="control-label">所在地区：</label>
        <div class="controls">
            <input type="hidden" id="locationId" name="locationId" value="${entity.bizLocation.id}"/>
            <form:hidden path="officeAddress.bizLocation.selectedRegionId" id="regionId" value="${entity.bizLocation.selectedRegionId}"/>
            <input type="text" id="regionName" value="${entity.bizLocation.pcrName}" readonly="readonly" required="true"/>
            <biz:selectregion id="region_id" name="regionName" selectedId="regionId"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">详细地址：</label>
        <div class="controls">
            <form:input path="officeAddress.bizLocation.address" value="${entity.bizLocation.address }" htmlEscape="false" maxlength="255" class="input-xlarge "/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">经度：</label>
        <div class="controls">
            <form:input path="officeAddress.bizLocation.longitude" value="${entity.bizLocation.longitude }" htmlEscape="false" class="input-xlarge  number"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">纬度：</label>
        <div class="controls">
            <form:input path="officeAddress.bizLocation.latitude" value="${entity.bizLocation.latitude }" htmlEscape="false" class="input-xlarge  number"/>
        </div>
    </div>


    <div class="control-group">
        <label class="control-label">邮政编码:</label>
        <div class="controls">
            <form:input path="zipCode" htmlEscape="false" maxlength="50"/>
        </div>
    </div>
    <%--<div class="control-group">--%>
    <%--<label class="control-label">负责人:</label>--%>
    <%--<div class="controls">--%>
    <%--<form:input path="master" htmlEscape="false" class="required" maxlength="50"/>--%>
    <%--<span class="help-inline"><font color="red">*</font></span>--%>
    <%--</div>--%>
    <%--</div>--%>
    <div class="control-group">
        <label class="control-label">手机:</label>
        <div class="controls">
            <form:input path="phone" onblur="SubmitPhone();" placeholder="请输入11位有效手机号" class="required" htmlEscape="false" maxlength="11"/>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">传真:</label>
        <div class="controls">
            <form:input path="fax" htmlEscape="false" maxlength="50"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">邮箱:</label>
        <div class="controls">
            <form:input path="email" htmlEscape="false" maxlength="50"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">银行卡号:</label>
        <div class="controls">
            <form:input path="bizVendInfo.cardNumber" htmlEscape="false" maxlength="50"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">合同图:
            <c:if test="${gysFlag != 'gys_audit' && gysFlag != 'gys_view'}">
                <p style="opacity: 0.5; color: red;">点击图片删除</p>
            </c:if>
            <c:if test="${gysFlag == 'gys_audit' || gysFlag == 'gys_view'}">
                <p style="opacity: 0.5; color: red;">点击图片查看大图</p>
            </c:if>
        </label>
        <c:if test="${gysFlag != 'gys_audit'  && gysFlag != 'gys_view'}">
        <div class="controls">
            <input class="btn" type="file" name="productImg" onchange="submitPic('compactImg', true)" value="上传图片" multiple="multiple" id="compactImg"/>
        </div>
        </c:if>
        <div id="compactImgDiv">
            <c:if test="${office.bizVendInfo.compactPhotos != null && office.bizVendInfo.compactPhotos != ''}">
            <c:forEach items='${fn:split(office.bizVendInfo.compactPhotos,"|")}' var="v" varStatus="status">
                <c:if test="${gysFlag != 'gys_audit' && gysFlag != 'gys_view'}">
                    <img src="${v}" customInput="compactImgImg" style='width: 100px' onclick="$(this).remove();">
                </c:if>
                <c:if test="${gysFlag == 'gys_audit' || gysFlag == 'gys_view'}">
                    <a target="_blank" href="${v}"><img src="${v}" customInput="compactImgImg" style='width: 100px'></a>
                </c:if>
            </c:forEach>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">身份证图:
            <c:if test="${gysFlag != 'gys_audit' && gysFlag != 'gys_view'}">
                <p style="opacity: 0.5; color: red;">点击图片删除</p>
            </c:if>
            <c:if test="${gysFlag == 'gys_audit' || gysFlag == 'gys_view'}">
                <p style="opacity: 0.5; color: red;">点击图片查看大图</p>
            </c:if>
        </label>
        <c:if test="${gysFlag != 'gys_audit'  && gysFlag != 'gys_view'}">
        <div class="controls">
            <input class="btn" type="file" name="productImg" onchange="submitPic('idCardImg', true)" value="上传图片" multiple="multiple" id="idCardImg"/>
        </div>
        </c:if>
        <div id="idCardImgDiv">
            <c:if test="${office.bizVendInfo.idCardPhotos != null && office.bizVendInfo.idCardPhotos != ''}">
                <c:forEach items='${fn:split(office.bizVendInfo.idCardPhotos,"|")}' var="v" varStatus="status">
                    <c:if test="${gysFlag != 'gys_audit' && gysFlag != 'gys_view'}">
                        <img src="${v}" customInput="idCardImgImg" style='width: 100px' onclick="$(this).remove();">
                    </c:if>
                    <c:if test="${gysFlag == 'gys_audit' || gysFlag == 'gys_view'}">
                        <a target="_blank" href="${v}"><img src="${v}" customInput="idCardImgImg" style='width: 100px'></a>
                    </c:if>
                </c:forEach>
            </c:if>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备注:</label>
        <div class="controls">
            <form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
        </div>
    </div>
    <c:if test="${empty office.id}">
        <div class="control-group">
            <label class="control-label">快速添加下级部门:</label>
            <div class="controls">
                <form:checkboxes path="childDeptList" items="${fns:getDictList('sys_office_common')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </div>
        </div>
    </c:if>
    <div class="form-actions">
        <shiro:hasPermission name="sys:supplier:audit">
            <c:if test="${gysFlag == 'gys_audit' && office.bizVendInfo.auditStatus == 0}">
                <a href="${ctx}/sys/office/auditSupplier?id=${office.id}&status=1"><input id="auditPass" class="btn btn-primary" type="button" value="审核通过"/>&nbsp;</a>
                <a href="${ctx}/sys/office/auditSupplier?id=${office.id}&status=2"><input id="auditRejected" class="btn btn-primary" type="button" value="审核驳回"/>&nbsp;</a>
            </c:if>
        </shiro:hasPermission>
        <shiro:hasPermission name="sys:office:edit">
            <c:if test="${gysFlag != 'gys_audit' && gysFlag != 'gys_view' && (office.bizVendInfo.auditStatus == 0 || office.bizVendInfo.auditStatus == 2)}">
                <input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
            </c:if>
        </shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
	<!-- 模态框（Modal） -->
	<div class="modal fade hide" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×
					</button>
					<h4 class="modal-title" id="myModalLabel">
						负责人添加
					</h4>
				</div>
				<div class="modal-body">
					<div class="control-group">
						<label class="control-label">姓名:</label>
						<div class="controls">
							<input id="primaryName" name="primaryPerson.name" htmlEscape="false" maxlength="50" class="required"/>
							<span id="checkName" class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">登录名:</label>
						<div class="controls">
							<input id="oldLoginName" name="primaryPerson.oldLoginName" type="hidden" value="">
							<input id="loginName" name="primaryPerson.loginName" htmlEscape="false" maxlength="50" class="required userName"/>
							<span id="checkLoginName" class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">密码:</label>
						<div class="controls">
							<input id="newPassword" name="primaryPerson.newPassword" type="password" value="" maxlength="50" minlength="3" class="required"/>
							<span id="checkNewPassword" class="help-inline"><font color="red">*</font> </span>
								<%--<c:if test="${not empty user.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if>--%>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">确认密码:</label>
						<div class="controls">
							<input id="confirmNewPassword" name="primaryPerson.confirmNewPassword" type="password" value="" maxlength="50" class="required" minlength="3" equalTo="#newPassword"/>
							<span id="checkConfirmNewPassword" class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">手机:</label>
						<div class="controls">
							<input id="primaryMobile" name="primaryPerson.mobile" htmlEscape="false" maxlength="100" class="required"/>
							<span id="checkPrimaryMobile" class="help-inline"><font color="red">*</font> </span>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"
							data-dismiss="modal">关闭
					</button>
					<button id="saveUser" type="button" class="btn btn-primary">
						保存
					</button>
				</div>
			</div>
		</div>
	</div>
    <form:input path="bizVendInfo.compactPhotos" id="compactPhotos" cssStyle="display: none"/>
    <form:input path="bizVendInfo.idCardPhotos" id="idCardPhotos" cssStyle="display: none"/>
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
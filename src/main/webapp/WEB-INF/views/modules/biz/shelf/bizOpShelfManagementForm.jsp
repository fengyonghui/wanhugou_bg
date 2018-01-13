<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>货架信息管理</title>
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

            $('.select_all').live('click',function(){
                var choose=$(".value_user");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
		});

			<%--$.ajax({--%>
				<%--type:"post",--%>
				<%--url:"${ctx}/biz/shelf/bizOpShelfInfo/shelfManagementCommit?id="+${bizOpShelfInfo.id},--%>
				<%--dataType:"json",--%>
				<%--data:"",--%>
				<%--success: function(data){--%>
					<%--alert("ok");--%>
				<%--},--%>
				<%--error:function(data){--%>
					<%--alert("no");--%>
				<%--}--%>
			<%--});--%>


                 // //全不选
                 //
                 // $("#btnAllNotChk").click(function () {
                 //
                 //     $("input:checkbox").removeAttr("checked");
                 //
                 // });
                 //
                 // //全选
                 //
                 // $("#btnAllChk").click(function () {
                 //
                 //     $("input:checkbox").attr("checked", "checked");
                 //
                 // });
        function a() {
            var len = $("input:checkbox:checked").length;

            alert(len);
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/shelf/bizOpShelfInfo/shelfManagementList?id=${bizOpShelfInfo.id}&&flag=false">货架管理员列表</a></li>
		<%--<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfInfo/form?id=${bizOpShelfInfo.id}">货架信息<shiro:hasPermission name="biz:shelf:bizOpShelfInfo:edit">${not empty bizOpShelfInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizOpShelfInfo:edit">查看</shiro:lacksPermission></a></li>--%>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizOpShelfInfo" action="${ctx}/biz/shelf/bizOpShelfInfo/shelfManagementCommit" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">货架名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
            <label class="control-label">货架管理员列表：</label>
			<div class="controls">
                <table id="shelfUser" class="table table-striped table-bordered table-condensed">
                    <thead>
                    <tr>
                        <th><input class="select_all"  type="checkbox"/></th>
                        <th>人员信息</th>
                        <%--<th>操作</th>--%>
                    </tr>
                    </thead>
                    <tbody id="prodInfo">

                        <c:forEach items="${userList}" var="user" varStatus="userIndex">
                            <tr>
                                <td>
                                    <input name="userIds" class="value_user" type="checkbox"  value="${user.id}"/>
                                    <%--<input type="checkbox" name="user" value="${user}" id="user${userIndex}" onclick="checkVal(${userIndex})"/>--%>
                                </td>
                                <td>
                                         ${user.name}
                                </td>
                            </tr>

                        </c:forEach>

                    </tbody>
                </table>
			</div>

		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:shelf:bizOpShelfInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

	<sys:message content="${message}"/>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>积分活动管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<script type="text/javascript">
		    //经销店树形列表
            $(document).ready(function(){
                // if($("#str").val()=='detail')
				// {
				//     $("#createBy").show();
				//     $("#createDate").show();
				// }
                $("#officeTree").hide();
                //查询全部用户，已下单用户，未下单用户的数量
                $.ajax({
                    url:"${ctx}/biz/integration/bizIntegrationActivity/count",
                    type:"get",
                    data:'',
                    contentType:"application/json;charset=utf-8",
                    success:function(data){
                        $("#quanbu").val(data.totalUser);
                        $("#xiadan").val(data.orderUser);
                        $("#weixiadan").val(data.unOrderUser);
                    }
                })
                var val = $('input[name="sendScope"]:checked').val();
                if(val==-3)
				{
                    $("#officeTree").show();
				}
                var setting = {
                           check:{enable:true,nocheckInherit:true},
					       view:{selectedMulti:false},
                           data:{simpleData:{enable:true}},
					       callback:{
                               onCheck: zTreeOnCheck
                           }
                };
                $("input[type='radio']").click(function(){
                    var value= $(this).val();
                    if(value==-3)
                    {
                        $("#officeTree").show();
                    }
                    else
                    {
                        if(value==0)
                        {
                            $("#officeTree").hide();
                            $("#sendNum").val($("#quanbu").val());
                        }
                        if(value==-1)
                        {
                            $("#officeTree").hide();
                            $("#sendNum").val($("#xiadan").val());
                        }
                        if(value==-2)
                        {
                            $("#officeTree").hide();
                            $("#sendNum").val($("#weixiadan").val());
                        }
                    }
                    var integrationNum = $("#integrationNum").val();
                    var sendNum = $("#sendNum").val();
                    if(integrationNum!=null&&integrationNum!=0)
                    {
                        var sendAll = sendNum*integrationNum;
                        $("#sendAll").val(sendAll);
                    }
                });

                //每人赠送积分发生改变计算总数
				$("#integrationNum").change(function(){
				   var sendNum = $("#sendNum").val();
				   var integrationNum = $("#integrationNum").val();
				   if(sendNum!=null&&sendNum!=0)
				   {
                       var sendAll = sendNum*integrationNum;
                       $("#sendAll").val(sendAll);
				   }
				});

                $("#sendNum").change(function(){
				   var integrationNum = $("#integrationNum").val();
				   var sendNum = $("#sendNum").val();
				   if(integrationNum!=null&&integrationNum!=0)
				   {
					   var sendAll = sendNum*integrationNum;
					   $("#sendAll").val(sendAll);
				   }
				});

                function zTreeOnCheck(event, treeId, treeNode) {
                    var treeObj = $.fn.zTree.getZTreeObj("officeTree");
                    nodes=treeObj.getCheckedNodes(true);
					var v="";
					var vv =[];
                    for(var i=0;i<nodes.length;i++){
                        if (nodes[i].isParent) {
                            v.replace(nodes[i].id, "");
                        } else {
                            v+=","+nodes[i].id;
                            vv.push(nodes[i].id)
                        }
                    }
                    var s = v.substring(1);
                    //alert(vv);
                    //alert("拼接后的字符串为："+s+"长度为："+vv.length);
                    $("#sendNum").val(vv.length);
                    $("#officeIds").val(s);
                }
                    var zNodes=[
                        <c:forEach items="${officeList}" var="office">{id:"${office.id}", pId:"${not empty office.parent?office.parent.id:0}", name:"${office.name}"},
                    </c:forEach>];
					// 初始化树结构
					var tree2 = $.fn.zTree.init($("#officeTree"), setting, zNodes);
					// 不选择父节点
					tree2.setting.check.chkboxType = { "Y" : "ps", "N" : "s" };
					var ids3 = $("#officeIds").val().split(",");
					for(var i=0; i<ids3.length; i++) {
						var node = tree2.getNodeByParam("id", ids3[i]);
						try{tree2.checkNode(node, true, false);}catch(e){}
					}
					// 默认展开全部节点
					//tree2.expandAll(true);
					if($("#id").val()!=null)
					{
						tree2.expandAll(true);
					}
			});

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

	</script>

</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/integration/bizIntegrationActivity/">积分活动列表</a></li>
		<li class="active"><a href="${ctx}/biz/integration/bizIntegrationActivity/form?id=${bizIntegrationActivity.id}">积分活动<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">${not empty bizIntegrationActivity.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:integration:bizIntegrationActivity:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizIntegrationActivity" action="${ctx}/biz/integration/bizIntegrationActivity/save" method="post" class="form-horizontal">
		<form:hidden path="id" id="id"/>
		<form:hidden path="str" id="str"/>
		<sys:message content="${message}"/>
		<c:if test="${bizIntegrationActivity.str}!=null">
			<div class="control-group">
				<label class="control-label">创建人：</label>
				<div class="controls">
					<form:hidden path="createBy.name" id="createBy" htmlEscape="false" maxlength="50" class="input-xlarge "/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">创建时间：</label>
				<div class="controls">
					<form:hidden path="createDate" id="createDate" htmlEscape="false" maxlength="50" class="input-xlarge "/>
				</div>
			</div>
		  </c:if>
		<div class="control-group">
			<label class="control-label">活动名称：</label>
			<div class="controls">
				<form:input path="activityName" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优惠工具：</label>
			<div class="controls">
				<form:checkbox path="activityTools" checked="checked" value="万户币"  htmlEscape="false" maxlength="50" class="input-xlarge"/>万户币
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">发送时间：</label>
			<div class="controls">
				<input name="sendTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${bizIntegrationActivity.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">发送范围：</label>
			 <div class="controls">
                    <form:radiobutton name="sendScope" path="sendScope" value="0"/>全部用户
                    <form:radiobutton name="sendScope" path="sendScope" value="-1"/>已下单用户
                    <form:radiobutton name="sendScope" path="sendScope" value="-2"/>未下单用户
				    <form:radiobutton name="sendScope" path="sendScope" id="zhi" value="-3"/>指定用户
				    <input type= "hidden" id="quanbu" value="">
				    <input type= "hidden" id="xiadan" value="">
				    <input type= "hidden" id="weixiadan" value="">
			 </div>
		</div>
		<div class="control-group">
			<label class="control-label">参与人数：</label>
			<div class="controls">
				<form:hidden path="officeIds" id="officeIds"/>
				<form:input path="sendNum" id="sendNum" htmlEscape="false" maxlength="10" readonly="readonly" class="input-xlarge  digits"/>
			</div>
		</div>
		<div class="control-group">
			<div class="controls">
				<div id="officeTree" class="ztree" style="margin-top:3px;float:left;display: none"></div>
			</div>
		</div>


		<div class="control-group">
			<label class="control-label">每人赠送积分：</label>
			<div class="controls">
				<form:input path="integrationNum" id="integrationNum" htmlEscape="false" maxlength="10" class="input-xlarge  digits"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">发送积分总数：</label>
			<div class="controls">
				<form:input path="sendAll" id="sendAll" htmlEscape="false" maxlength="10" class="input-xlarge  digits"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备注说明：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>

		<div class="form-actions">
			<%--<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>保存
			</shiro:hasPermission>--%>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>

</html>
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
                // if($(".radioDefault").attr("checked"))
                // {
                 //    alert("Aaaaaa")
                 //    $("#officeTree").show();
                // }else
				// {
                 //    $("#officeTree").hidden();
				// }
                var setting = {
                           check:{enable:true,nocheckInherit:true},
					       view:{selectedMulti:false},
                           data:{simpleData:{enable:true}},
					       callback:{
                                 // beforeClick:function(id, node){
                                 // tree.checkNode(node, !node.checked, true, true);
                                 // return false;
                                 // }
                               onCheck: zTreeOnCheck
                           }

                };

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
                    alert(vv);
                    alert("拼接后的字符串为："+s+"长度为："+vv.length);
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
                // 默认展开全部节点
              //  tree2.expandAll(true);
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
            var val=$('input:radio[name="radios"]:checked').val()
			if(val==1)
			{
			    $("#officeTree").show();
			}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/integration/bizIntegrationActivity/">积分活动列表</a></li>
		<li class="active"><a href="${ctx}/biz/integration/bizIntegrationActivity/form?id=${bizIntegrationActivity.id}">积分活动<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">${not empty bizIntegrationActivity.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:integration:bizIntegrationActivity:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizIntegrationActivity" action="${ctx}/biz/integration/bizIntegrationActivity/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">活动名称：</label>
			<div class="controls">
				<form:input path="activityName" htmlEscape="false" maxlength="50" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优惠工具：</label>
			<div class="controls">
				<input type="checkbox" value="万户币" checked="checked">万户币
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
				<form:radiobutton path="sendScope" name="radios" value="1"/>指定用户
				<form:radiobutton path="sendScope" value="2"/>已下单用户
				<form:radiobutton path="sendScope" value="3"/>未下单用户
				<form:radiobutton path="sendScope" value="4"  checked="true"/>全部用户
			 </div>
		</div>
		<div class="control-group">
			<label class="control-label">发送人数：</label>
			<div class="controls">
				<form:input path="officeIds" id="officeIds"/>
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
				<form:input path="integrationNum" htmlEscape="false" maxlength="10" class="input-xlarge  digits"/>
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
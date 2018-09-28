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

                var str = $("#str").val();
                if(str=='detail')
				{
                    $("#inputForm").find("input[type!='button']").attr("disabled","disabled") ;
				}

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
                        var val = $('input[name="sendScope"]:checked').val();
                        if(val==-3)
                        {
                            $("#officeTree").show();
                            var officeIds = $("#officeIds").val();
                            var split = officeIds.split(",");
                            $("#sendNum").val(split.length);
                        }
                        else {
                            $("#officeIds").val('')
                            if(val==0)
                            {
                                $("#sendNum").val( $("#quanbu").val());
                            }
                            if(val==-1)
                            {
                                $("#sendNum").val( $("#xiadan").val());
                            }
                            if(val==-2)
                            {
                                $("#sendNum").val( $("#weixiadan").val());
                            }
                        }
                    }
                })

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
                        //取消指定用户节点选中状态
                        var treeObj = $.fn.zTree.getZTreeObj("officeTree");
                        treeObj.checkAllNodes(false);
                        //清空文本域
                        $("#offices").val('');
                        $("#officeTree").show();
                        $("#offices").show();
                        $("#search").show();
                        $("#choose").show();
                        //清除参与人数
						$("#sendNum").val('');
						//清空每人赠送积分和积分总数
						$("#integrationNum").val('');
						$("#sendAll").val('');

                    }
                    else
                    {
                        $("#officeTree").hide();
                        $("#offices").hide();
                        $("#search").hide();
                        $("#choose").hide();
                        $("#officeIds").val('');
                        if(value==0)
                        {
                            $("#sendNum").val($("#quanbu").val());
                        }
                        if(value==-1)
                        {
                            $("#sendNum").val($("#xiadan").val());
                        }
                        if(value==-2)
                        {
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

                $("#buttonExport").click(function(){
                    top.$.jBox.confirm("确认要导出活动参与者列表数据吗？","系统提示",function(v,h,f){
                        if(v=="ok"){
                            //获取发送范围值
                            var sendScope = $('input[name="sendScope"]:checked').val();
                            var officeIds = $("#officeIds").val();
                            location.href="${ctx}/biz/integration/bizIntegrationActivity/activityOfficesExport?officeIds="+officeIds+"&sendScope="+sendScope;
                        }
                    },{buttonsFocus:1});
                    top.$('.jbox-body .jbox-icon').css('top','55px');
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
                    var val = $("#officeIds").val();
                    //动态加载文本域
                    $.ajax({
                        url:"${ctx}/biz/integration/bizIntegrationActivity/activity/special/offices?officeIds="+val,
                        type:"get",
                        data:'',
                        contentType:"application/json;charset=utf-8",
                        success:function(data){
                            var s ="";
                            for(var v in data)
							{
                                  s += data[v].id+'--'+data[v].name+'--'+data[v].primaryPerson.name+'--'+data[v].phone+"\n";
                                  $("#offices").val(s);
							}
                        }
                    })

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


            function searchNode(){
                var treeObj = $.fn.zTree.getZTreeObj("officeTree");
                var keywords=$("#keyword").val();
                var nodeList = treeObj.getNodesByParamFuzzy("name", keywords, null);
                if (nodeList.length>0) {
                    treeObj.selectNode(nodeList[0]);
                }
            }
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
		<c:if test="${bizIntegrationActivity.str=='detail'}">
			<div class="control-group">
				<label class="control-label">创建人：</label>
				<div class="controls">
					<form:input path="createBy.name" htmlEscape="false" maxlength="50" class="input-xlarge "/>
				</div>
			</div>
			<%--<div class="control-group">
				<label class="control-label">创建时间：</label>
				<div class="controls">
					<form:input path="createDate" htmlEscape="false" maxlength="50" class="input-xlarge "/>
				</div>
			</div>--%>
			<div class="control-group">
				<label class="control-label">创建时间：</label>
				<div class="controls">
					<input name="createDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
						   value="<fmt:formatDate value="${bizIntegrationActivity.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
						   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				</div>
			</div>
		  </c:if>
		<div class="control-group">
			<label class="control-label">活动名称：</label>
			<div class="controls">
			 	<form:input path="activityName" htmlEscape="false" maxlength="50" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
				</div>
		</div>
		<div class="control-group">
			<label class="control-label">优惠工具：</label>
			<div class="controls">
				<form:checkbox path="activityTools" readonly="true" checked="checked" value="万户币"  htmlEscape="false" maxlength="50" class="input-xlarge"/>万户币
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">发送时间：</label>
			<div class="controls">
				<input name="sendTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
					value="<fmt:formatDate value="${bizIntegrationActivity.sendTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">发送范围：</label>
			 <div class="controls">
                    <form:radiobutton name="sendScope" path="sendScope" checked="true" value="0"/>全部用户
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
				<form:input path="sendNum" placeholder="由选中发送范围计算得到，不可输入"  id="sendNum"  htmlEscape="false" maxlength="10" readonly="true" class="input-xlarge required  digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<div class="controls">
				<div class="search-bar" style="display: none" id="search">
					<input id="keyword" type="text" placeholder="请输入经销店名称">
					<input type="button" onclick="searchNode()" value="搜索" style="color:green">
				</div>
				<div id="officeTree" class="ztree" style="margin-top:3px;float:left;display: none"></div>
				<span style="display: none" id="choose">已选择:</span>
				<textarea id="offices" cols="300" ro style="margin-left:10px;word-wrap:normal;width: 300px;height: 240px;vertical-align:top;display: none">

			    </textarea>
			</div>

		</div>


		<div class="control-group">
			<label class="control-label">每人赠送积分：</label>
			<div class="controls">
				<form:input path="integrationNum" id="integrationNum" htmlEscape="false" maxlength="10" class="input-xlarge required  digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">发送积分总数：</label>
			<div class="controls">
				<form:input path="sendAll" placeholder="参与人数*每人赠送积分" id="sendAll" htmlEscape="false" maxlength="10" readonly="true" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">备注说明：</label>
			<div class="controls">
				<form:textarea path="description" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>

		<div class="form-actions">

			    <c:if test="${bizIntegrationActivity.str!='detail'}">
					<shiro:hasPermission name="biz:integration:bizIntegrationActivity:edit">
						<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>
					</shiro:hasPermission>
					<%--<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>--%>
				</c:if>
				<c:if test="${bizIntegrationActivity.str=='detail'}">
			 	    <input id="buttonExport" class="btn btn-primary" type="button" value="导出参与者列表"/>
				</c:if>
			        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>

</html>
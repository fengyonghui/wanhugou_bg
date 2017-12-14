<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品类别管理</title>
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
                var obj=$(this).attr("id");
                var choose=$(".value_"+obj);
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });
            var id=$("#id").val();
            var propsEle=$(".select_all");
            var props="";
            propsEle.each(function(){
                props+=$(this).val()+",";
            });
            props=props.substring(0,props.length-1);
            $.post("${ctx}/biz/category/bizCatePropertyInfo/listByCate",
                {catId:id},
                function(data,status){
                    $.each(data, function (index, catePropertyInfo) {

                        $.each(catePropertyInfo.catePropValueList, function (index, catePropValue) {

                            if(props.indexOf(catePropValue.propertyInfoId)!=-1){
                                $("#"+catePropValue.propertyInfoId).attr('checked',true)
                            }
                                $("#value_"+catePropValue.propertyValueId).attr('checked',true)

                        });
                });

            });
            var i=0;
            $("#addPropValue").click(function () {
                if($("#propValueList"+i).val()==''){
                    alert('属性不能为空');
                    return false;
                }
                i++;
                $("#propValues").append("<input type='text' id='propValueList"+i+"' name=\"propValueList["+i+"].value\"  maxlength=\"512\" class=\"input-small\"/>")
            });
	$("#but_sub").click(function () {
        $.ajax({
            type:"post",
            url:"${ctx}/sys/propertyInfo/savePropInfo",
            data:$('#inputForm2').serialize(),
            success:function (data) {
                if(data=='ok'){
                    $('#myModal').modal('hide');
                    $("#myModal").on("hidden.bs.modal", function (e) {
                        alert("保存成功！");
                        window.location.href="${ctx}/biz/category/bizCategoryInfo/form?id="+id;
                    })
                }
            }
        })
    })

	});



	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/category/bizCategoryInfo/">商品类别列表</a></li>
		<li class="active"><a href="${ctx}/biz/category/bizCategoryInfo/form?id=${bizCategoryInfo.id}">商品类别<shiro:hasPermission name="biz:category:bizCategoryInfo:edit">${not empty bizCategoryInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:category:bizCategoryInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>

	<%--@elvariable id="bizCategoryInfo" type="com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo"--%>
	<form:form id="inputForm" modelAttribute="bizCategoryInfo" action="${ctx}/biz/category/bizCategoryInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="parent.id"/>
		<sys:message content="${message}"/>		

		<div class="control-group">
			<label class="control-label">上级分类:</label>
			<div class="controls">
				<sys:treeselect id="category" name="parent.id" value="${bizCategoryInfo.parent.id}" labelName="parent.name" labelValue="${bizCategoryInfo.parent.name}"
								title="分类" url="/biz/category/bizCategoryInfo/treeData" extId="${bizCategoryInfo.id}" cssClass="" allowClear="${bizCategoryInfo.currentUser.admin}"/>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">分类名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge required" />
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分类描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" maxlength="512" class="input-xlarge "/>
			</div>
		</div>

			<div class="control-group">
				<label class="control-label">分类属性：</label>
				<div class="controls">
					<c:forEach items="${propertyInfoList}" var="propertyInfo">
						<input  class="select_all" id="${propertyInfo.id}" type="checkbox" name="catePropertyInfos" value="${propertyInfo.id}"/> ${propertyInfo.name}：
						<c:forEach items="${map[propertyInfo.id]}" var="propValue">
							<input class="value_${propertyInfo.id}" id="value_${propValue.id}" type="checkbox" name="propertyMap[${propertyInfo.id}].catePropertyValues" value="${propValue.id}"/> ${propValue.value}
						</c:forEach>
						<br/>
					</c:forEach>
				</div>
			</div>
			<div class="control-group">
			<label class="control-label">增加属性：</label>
			<div class="controls">
				<button  data-toggle="modal" data-target="#myModal" type="button" class="btn btn-default">
					<span class="icon-plus"></span>
				</button>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:category:bizCategoryInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	<!-- 模态框（Modal） -->
	<div class="modal fade hide" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×
					</button>
					<h4 class="modal-title" id="myModalLabel">
						属性与属性值
					</h4>
				</div>
				<%--@elvariable id="propertyInfo" type="com.wanhutong.backend.modules.sys.entity.PropertyInfo"--%>
				<form:form id="inputForm2" modelAttribute="propertyInfo" action="${ctx}/sys/propertyInfo/savePropInfo" method="post" class="form-horizontal">

				<div class="modal-body">
					<div class="control-group">
							<label class="control-label">属性名称：</label>
							<div class="controls">
								<form:input path="name" htmlEscape="false" maxlength="30" class="input-xlarge required"/>
								<span class="help-inline"><font color="red">*</font> </span>
							</div>
						</div>
					<div class="control-group">
							<label class="control-label">属性描述：</label>
							<div class="controls">
								<form:input path="description" htmlEscape="false" maxlength="200" class="input-xlarge required"/>
							</div>
						</div>
					<div class="control-group">
							<label class="control-label">属性值：</label>
							<div class="controls">
						<span id="propValues">
							<form:input id="propValueList0" path="propValueList[0].value" htmlEscape="false" maxlength="512" class="input-small"/>
						</span>
								<button id="addPropValue" type="button" class="btn btn-default">
									<span class="icon-plus"></span>
								</button>
							</div>
						</div>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default"
							data-dismiss="modal">关闭
					</button>
					<shiro:hasPermission name="biz:category:bizCatePropertyInfo:edit">
					<button id="but_sub"type="button" class="btn btn-primary">
						保存提交
					</button>
					</shiro:hasPermission>
				</div>
				</form:form>
			</div><!-- /.modal-content -->
		</div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
</body>
</html>
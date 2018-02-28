<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品类别管理</title>
	<meta name="decorator" content="default"/>
	<%--<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" />--%>

	<%--<link rel="stylesheet" href="static/multiselect-master/css/style.css" />--%>
	<script type="text/javascript">
        $(document).ready(function() {
            $("#inputForm").validate({
                submitHandler: function(form){
                    var str="";
                    $("select[name='aaa']").find("option").each(function(){

                        str+=$(this).val()+",";
					});
                    str=str.substring(0,str.length-1);
                    $("input[name='catePropertyInfos']").val(str);
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
            var id=$("#id").val();
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
                var flag=true;
				$("#propValues").find("input").each(function (i) {
					alert($(this).val());
					if($(this).val()==''){
                        flag=false;
					}
                });
				if(flag){
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
                                });
                            }
                        }
                    });
				}

            });

		});
    </script>

			<%--//$("#name").focus();--%>



</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/category/bizCategoryInfo/list?id=${bizCategoryInfo.parentId}&parentIds=${bizCategoryInfo.parentIds}&cid=${bizCategoryInfo.id}">商品类别列表</a></li>
		<li class="active"><a href="${ctx}/biz/category/bizCategoryInfo/form?id=${bizCategoryInfo.id}">商品类别<shiro:hasPermission name="biz:category:bizCategoryInfo:edit">${not empty bizCategoryInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:category:bizCategoryInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<%--@elvariable id="bizCategoryInfo" type="com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo"--%>
	<form:form id="inputForm"  modelAttribute="bizCategoryInfo" action="${ctx}/biz/category/bizCategoryInfo/save" method="post" class="form-horizontal">
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
			<label class="control-label">分类图片：</label>
			<div class="controls">
				<form:hidden path="imgId"/>
				<form:hidden id="prodImg" path="catePhoto" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="prodImg" type="images" uploadPath="/cate/item" selectMultiple="false" maxWidth="100" maxHeight="100"/>
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

					<c:forEach items="${bizCategoryInfo.propertyInfoList}" var="propertyInfo">
						<div  style="width: 100%;display: inline-block">
							<%--<input  class="select_all" id="${propertyInfo.id}" type="checkbox" name="catePropertyInfos" value="${propertyInfo.id}"/> --%>
							<span  style="float:left;width:60px;padding-top:3px">${propertyInfo.name}：</span>
							<div style="float: left">
								<select  title="search"  id="search_${propertyInfo.id}" class="input-xlarge" multiple="multiple" size="8">
									<c:forEach items="${bizCategoryInfo.map[propertyInfo.id]}" var="propValue">
										<%--<input class="value_${propertyInfo.id}" id="value_${propValue.id}" type="checkbox" name="propertyMap[${propertyInfo.id}].catePropertyValues" value="${propValue.id}"/> ${propValue.value}--%>
										<option value="${propertyInfo.id}-${propValue.id}">${propValue.value}</option>
									</c:forEach>


								</select>
							</div>
							<%--<span class="icon-chevron-right"></span>--%>
							<div  style="width: 20%;margin-left:10px;float: left">
								<button type="button" id="search_${propertyInfo.id}_rightAll" class="btn-block"><i class="icon-forward"></i></button>

								<button type="button" id="search_${propertyInfo.id}_rightSelected" class="btn-block"><i class="icon-chevron-right"></i></button>

								<button type="button" id="search_${propertyInfo.id}_leftSelected" class="btn-block"><i class="icon-chevron-left"></i></button>

								<button type="button" id="search_${propertyInfo.id}_leftAll" class="btn-block"><i class="icon-backward"></i></button>
							</div>

							<div style="margin-left:10px;float: left">

								<select name="aaa"  id="search_${propertyInfo.id}_to" class="input-xlarge" size="8" multiple="multiple">
									<c:forEach items="${bizCategoryInfo.catePropValueMap[propertyInfo.id]}" var="propValue">
										<%--<input class="value_${propertyInfo.id}" id="value_${propValue.id}" type="checkbox" name="propertyMap[${propertyInfo.id}].catePropertyValues" value="${propValue.id}"/> ${propValue.value}--%>
										<option value="${propertyInfo.id}-${propValue.propertyValueId}">${propValue.value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</c:forEach>
					<input type="hidden" name="catePropertyInfos" value=""/>
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
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>
	<script src="${ctxStatic}/bootstrap/multiselect.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>
	<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
	<script type="text/javascript">
        $(document).ready(function() {

            <%--window.prettyPrint && prettyPrint();--%>

            $('select[title="search"]').multiselect({
                search: {
                    left: '<input type="text" name="q" style="display: block;width: 95%"  placeholder="Search..." />',
                    right: '<input type="text" name="q" style="display: block;width: 95%" class="input-large" placeholder="Search..." />',
                }
                // ,
                // fireSearch: function(value) {
                //     return value.length >=1 ;
                // }
            });
        });
	</script>

</body>
</html>
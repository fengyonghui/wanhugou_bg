<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>商品类别管理</title>
	<meta name="decorator" content="default"/>
	<%--<script src="${ctxStatic}/jquery/jquery-1.8.3.min.js" type="text/javascript"></script>--%>
	<%--<script src="${ctxStatic}/jqGrid/4.6/plugins/ui.multiselect.js" type="text/javascript"></script>--%>
	<%--<link href="assets/js/google-code-prettify/prettify.css" rel="stylesheet">--%>
	<%--<link href="../../../bootstrap/2.3.1/css_default/bootstrap.min.css" rel="stylesheet" type="text/css"/>--%>
	<%--<script src="assets/js/google-code-prettify/prettify.js"></script>--%>
	<%--<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>--%>
	<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" />
	<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
	<script type="text/javascript" src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/prettify/r298/prettify.min.js"></script>
	<script src="${ctxStatic}/bootstrap/multiselect.min.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
            window.prettyPrint && prettyPrint();
			  $("select[title='searchProd']").each(function (i) {
				  $(this).multiselect({
                      // search: {
                      //     left: '<input type="text" name="q" class="form-control" placeholder="Search..." />',
                      //     right: '<input type="text" name="q" class="form-control" placeholder="Search..." />',
                      // },
                      // fireSearch: function(value) {
                      //     return value.length > 3;
                      // }
				  })
              })
            // $('#search').multiselect({
            //     search: {
            //         left: '<input type="text" name="q" class="form-control" placeholder="Search..." />',
            //         right: '<input type="text" name="q" class="form-control" placeholder="Search..." />',
            //     },
            //     fireSearch: function(value) {
            //         return value.length > 3;
            //     }
            // });
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
                    });
                }
            }
        });
    });
		});
        <%--/*function selectedPropertyInfo(obj) {--%>
            <%--alert(obj);--%>
            <%--$("#propertyValueList").html("");--%>
            <%--$.ajax({--%>
                <%--type:"post",--%>
                <%--url:"${ctx}/biz/category/bizCategoryInfo/propertyForm?value=null&id="+obj,--%>
				<%--dataType:"json",--%>
                <%--success:function (data) {--%>
                    <%--$.each(data,function (index,list) {--%>
                       <%--console.log(list)--%>
						<%--$.each(list, function (index,proValue) {--%>
							<%--alert(proValue.value);--%>
							<%--var htmlInfo = "";--%>
							<%--htmlInfo += "<a href='#' onclick='addItem("+proValue+")'><li role='option' id='"+proValue.id+"' class='cc-cbox-item cc-hasChild-item'>"+proValue.value+"</li></a>";--%>
							<%--$("#propertyValueList").append(htmlInfo);--%>
                        <%--});--%>

                    <%--});--%>

                <%--}--%>
            <%--});--%>
        <%--}*/--%>
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/category/bizCategoryInfo/list?id=${bizCategoryInfo.parentId}&parentIds=${bizCategoryInfo.parentIds}&cid=${bizCategoryInfo.id}">商品类别列表</a></li>
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
					<%--<table id="CategoryTable0" style="width: 30%;float: left" class="table table-striped table-bordered table-condensed">
						<c:forEach items="${propertyInfoList}" var="propertyInfo">
							<tr>
							<td>
							<a name="catePropertyInfos" id="${propertyInfo.id}" value="${propertyInfo.id}" onclick="selectedPropertyInfo(${propertyInfo.id})">${propertyInfo.name}</a>
							</td>
							</tr>
						</c:forEach>
					</table>
					<table id="CategoryTable1" style="width: 35%;float: left" class="table table-striped table-bordered table-condensed">

					</table>
					<table id="CategoryTable2" style="width: 35%;float: left" class="table table-striped table-bordered table-condensed">

					</table>--%>
				<%--<div class="cate-main">
					<div id="cate-cascading">
							&lt;%&ndash;<a href="#" class="cc-prev cc-nav" title="上一级" id="J_LinkPrev" style="visibility: hidden;">
								<span>上一级</span>
							</a>&ndash;%&gt;
							<div class="cc-listwrap">
								<ol id="menu" class="cc-list">
									<li id="propertyList" style="width: 20%" class="cc-list-item" tabindex="-1">
										<div id="propertyInfo">
											<ul id="menu1" role="group">
												<c:forEach items="${propertyInfoList}" var="propertyInfo">

													<a href="#" onclick="selectedPropertyInfo(${propertyInfo.id})"><li id="${propertyInfo.id}" value="${propertyInfo.id}" >
																${propertyInfo.name}</li><br/></a>
													&lt;%&ndash;<input  class="select_all" id="${propertyInfo.id}" type="checkbox" name="catePropertyInfos" value="${propertyInfo.id}"/> ${propertyInfo.name}：&ndash;%&gt;
													&lt;%&ndash;<c:forEach items="${map[propertyInfo.id]}" var="propValue">
                                                        <li id="value_${propValue.id}" value="${propValue.id}">${propValue.value}</li>
                                                        &lt;%&ndash;<input class="value_${propertyInfo.id}" id="value_${propValue.id}" type="checkbox" name="propertyMap[${propertyInfo.id}].catePropertyValues" value="${propValue.id}"/> ${propValue.value}&ndash;%&gt;
                                                    </c:forEach>&ndash;%&gt;

												</c:forEach>
											</ul>
										</div>
									</li>
									&lt;%&ndash;<form:form id="selectForm" modelAttribute="bizCategoryInfo" action="${ctx}/biz/category/bizCategoryInfo/propertyForm" method="post" class="form-horizontal">
									<li id="propertyValueList" style="width: 40%" class="cc-list-item" tabindex="-1">
										<input id="selectProValue" name="selectProValue" placeholder="搜索" class="selectProValue" role="textbox" autocomplete="off" style="width: 176px;" value=""/>
										<div role="tree" class="cc-tree" id="propValueList">
											<ul role="listbox" id="pValueList" tabindex="-1" hidefocus="-1" unselectable="on" class="cc-cont">
												<li role="option" id="cc-cbox-item" class="cc-cbox-item cc-hasChild-item">qqqq</li>
											</ul>
										</div>
									</li>
									</form:form>&ndash;%&gt;
									<li id="propertyValueList" style="width: 40%">
									<div class="form-group">
										<label for="id_select"></label>
										<select id="id_select" class="selectpicker bla bla bli" multiple data-live-search="true">
											<option>cow</option>
											<option>bull</option>
											<option>ASD</option>
											<option selected>Bla</option>
											<option>Ble</option>
											</optgroup>
										</select>
									</div>
									</li>

									<li id="propertyValue" style="width: 40%" class="cc-list-item" tabindex="-1">
										<div id="propValue">
											<ul id="proValue" tabindex="-1" hidefocus="-1">
												<li role="option">qqq</li>
											</ul>
										</div>
									</li >
								</ol>
							</div>
						</div>
					</div>--%>

						<ul id="menu1" role="group">

							<c:forEach items="${propertyInfoList}" var="propertyInfo" varStatus="property">

								<%--<a href="#" onclick="selectedPropertyInfo(${propertyInfo.id})"></a>--%>
									<li id="li${propertyInfo.id}" value="${propertyInfo.id}">
										<a href="#" onclick="selectProValue(${propertyInfo.id})">${propertyInfo.name}</a>
											<div class="form-group">
												<label for="id_select${propertyInfo.id}"></label>
												<select id="id_select${propertyInfo.id}" class="selectpicker${propertyInfo.id} " data-actions-box="true" onclick="selectProValue(${propertyInfo.id})" multiple data-live-search="true" data_width="300px">
													<%--<optgroup>--%>
													<%--<option name="propertyMap[${propertyInfo.id}].catePropertyValues" value="">cow</option>--%>
													<%--<option>bull</option>--%>
													<%--<option>ASD</option>--%>
													<%--<option selected>Bla</option>--%>
													<%--<option>Ble</option>--%>
												</select>
											</div></li><br/>

								<div class="row">
									<div class="col-sm-5">
										<select name="from[]" title="searchProd" id="search${property.index}" class="form-control" size="11" multiple="multiple">
											<option value="1" data-position="1">Item 1</option>
											<option value="2" data-position="2">Item 5</option>
											<option value="2" data-position="3">Item 2</option>
											<option value="2" data-position="4">Item 4</option>
											<option value="3" data-position="5">Item 3</option>
										</select>
									</div>

									<div class="col-sm-2">
										<button type="button" id="search_rightAll" class="btn btn-block"><i class="glyphicon glyphicon-forward"></i></button>
										<button type="button" id="search_rightSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-right"></i></button>
										<button type="button" id="search_leftSelected" class="btn btn-block"><i class="glyphicon glyphicon-chevron-left"></i></button>
										<button type="button" id="search_leftAll" class="btn btn-block"><i class="glyphicon glyphicon-backward"></i></button>
									</div>

									<div class="col-sm-5">
										<select name="to[]" id="search${property.index}_to" class="form-control" size="11" multiple="multiple"></select>
									</div>
								</div>
								<%--<input  class="select_all" id="${propertyInfo.id}" type="checkbox" name="catePropertyInfos" value="${propertyInfo.id}"/> ${propertyInfo.name}：--%>
								<%--<c:forEach items="${map[propertyInfo.id]}" var="propValue">
                                    <li id="value_${propValue.id}" value="${propValue.id}">${propValue.value}</li>
                                    &lt;%&ndash;<input class="value_${propertyInfo.id}" id="value_${propValue.id}" type="checkbox" name="propertyMap[${propertyInfo.id}].catePropertyValues" value="${propValue.id}"/> ${propValue.value}&ndash;%&gt;
                                </c:forEach>--%>

							</c:forEach>
						</ul>

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
	<script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
            (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

        ga('create', 'UA-39934286-1', 'github.com');
        ga('send', 'pageview');
	</script>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费物流线路管理</title>
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
		});
	</script>
    <script type="text/javascript">
        $(function () {
            //默认绑定省
            ProviceBind();
            //绑定事件
            $("#province0").change( function () {
                CityBind("from",this);
            });
            $("#toProvince0").change( function () {
                CityBind("to",this);
            });
            $("#city0").change(function () {
                VillageBind("from",this);
            });
            $("#toCity0").change(function () {
                VillageBind("to",this);
            });

        });
        function ProviceBind() {
            var province = $("#province").val();
            var toProvince = $("#toProvince").val();
            //清空下拉数据
            $("#province0").html("");
            $("#toProvince0").html("");

            var str = "<option>===省====</option>";
            $.ajax({
                type: "post",
                url: "${ctx}/sys/sysRegion/selectRegion",
                data:{"level":"prov"},
                async: false,
                success: function (data) {
                    console.info(data);
                    //从服务器获取数据进行绑定
                    $.each(data, function (i, item) {
                        str += "<option value='" + item.code + "'";
                        if (province == item.code) {
                            str += "selected='selected'";
                        }
                        str += ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    $("#province0").append(str);
                    City("from");

                    str = "<option>===省====</option>";
                    $.each(data, function (i, item) {
                        str += "<option value='" + item.code + "'";
                        if (toProvince == item.code) {
                            str += "selected='selected'";
                        }
                        str += ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    $("#toProvince0").append(str);
                    City("to");
                },
                error: function () { alert("Error"); }
            });
        }
        function CityBind(obj,item) {
            var provice = $(item).val();
            //判断省份这个下拉框选中的值是否为空
            if (provice == "") {
                return;
            }
            var id = $(item).attr("id");
            var index;
            if (obj == 'from') {
                index = id.replace('province','');
            }
            if (obj == 'to') {
                index = id.replace('toProvince','');
            }
            if (obj == 'from') {
                $("#city"+index).html("");
            }
            if (obj == 'to') {
                $("#toCity"+index).html("");
            }
            var str = "<option>===市====</option>";
            $.ajax({
                type: "post",
                url: "${ctx}/sys/sysRegion/selectRegion",
                data: { "code":provice,"level":"city"},
                async: false,
                success: function (data) {
                    //从服务器获取数据进行绑定
                    $.each(data, function (i, item) {
                        str += "<option value=" + item.code + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    if (obj == 'from') {
                        $("#city"+index).append(str);
                    }
                    if (obj == 'to') {
                        $("#toCity"+index).append(str);
                    }
                },
                error: function () { alert("Error"); }
            });
        }
        function VillageBind(obj,item) {

            var city = $(item).val();
            //判断市这个下拉框选中的值是否为空
            if (city == "") {
                return;
            }
            var id = $(item).attr("id");
            var index;
            if (obj == 'from') {
                index = id.replace('city','');
            }
            if (obj == 'to') {
                index = id.replace('toCity','');
            }
            if (obj == 'from') {
                $("#region"+index).html("");
            }
            if (obj == 'to') {
                $("#toRegion"+index).html("");
            }
            var str = "<option>===县/区====</option>";
            //将市的ID拿到数据库进行查询，查询出他的下级进行绑定
            $.ajax({
                type: "post",
                url: "${ctx}/sys/sysRegion/selectRegion",
                data: { "code":city, "level":"dist" },
                async: false,
                success: function (data) {
                    //从服务器获取数据进行绑定
                    $.each(data, function (i, item) {
                        str += "<option value=" + item.code + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    if (obj == 'from') {
                        $("#region"+index).append(str);
                    }
                    if (obj == 'to') {
                        $("#toRegion"+index).append(str);
                    }
                },
                error: function () { alert("Error"); }
            });
        }
        function City(obj) {
            var province = $("#province").val();
            var toProvince = $("#toProvince").val();
            var city = $("#city").val();
            var toCity = $("#toCity").val();
            if (obj == 'from') {
                $("#city0").html("");
            }
            if (obj == 'to') {
                $("#toCity0").html("");
            }
            var str = "<option>===市====</option>";
            if (obj == 'from') {
                $.ajax({
                    type: "post",
                    url: "${ctx}/sys/sysRegion/selectRegion",
                    data: { "code":province,"level":"city"},
                    async: false,
                    success: function (data) {
                        //将数据添加到省份这个下拉框里面
                        //从服务器获取数据进行绑定
                        $.each(data, function (i, item) {
                            str += "<option value='" + item.code;
                            if (city == item.code) {
                                str += "' selected='selected";
                            }
                            str += "'>" + item.name + "</option>";
                        });
                        $("#city0").append(str);
                        Region(obj);
                    },
                    error: function () { alert("Error"); }
                });
            }
            if (obj == 'to') {
                $.ajax({
                    type: "post",
                    url: "${ctx}/sys/sysRegion/selectRegion",
                    data: { "code":toProvince,"level":"city"},
                    async: false,
                    success: function (data) {
                        //将数据添加到省份这个下拉框里面
                        //从服务器获取数据进行绑定
                        $.each(data, function (i, item) {
                            str += "<option value='" + item.code + "'";
                            if (toCity == item.code) {
                                str += "selected='selected'";
                            }
                            str += ">" + item.name + "</option>";
                        });
                        $("#toCity0").append(str);
                        Region(obj);
                    },
                    error: function () { alert("Error"); }
                });
            }
        }
        function Region(obj) {
            var city = $("#city").val();
            var toCity = $("#toCity").val();
            var region = $("#region").val();
            var toRegion = $("#toRegion").val();
            if (obj == 'from') {
                $("#region0").html("");
            }
            if (obj == 'to') {
                $("#toRegion0").html("");
            }
            var str = "<option>===市====</option>";
            if (obj == 'from') {
                $.ajax({
                    type: "post",
                    url: "${ctx}/sys/sysRegion/selectRegion",
                    data: { "code":city,"level":"dist"},
                    async: false,
                    success: function (data) {
                        //将数据添加到省份这个下拉框里面
                        //从服务器获取数据进行绑定
                        $.each(data, function (i, item) {
                            str += "<option value='" + item.code + "'";
                            if (region == item.code) {
                                str += "selected='selected'";
                            }
                            str += ">" + item.name + "</option>";
                        });
                        $("#region0").append(str);
                    },
                    error: function () { alert("Error"); }
                });
            }
            if (obj == 'to') {
                $.ajax({
                    type: "post",
                    url: "${ctx}/sys/sysRegion/selectRegion",
                    data: { "code":toCity,"level":"dist"},
                    async: false,
                    success: function (data) {
                        //将数据添加到省份这个下拉框里面
                        //从服务器获取数据进行绑定
                        $.each(data, function (i, item) {
                            str += "<option value='" + item.code + "'";
                            if (toRegion == item.code) {
                                str += "selected='selected'";
                            }
                            str += ">" + item.name + "</option>";
                        });
                        $("#toRegion0").append(str);
                    },
                    error: function () { alert("Error"); }
                });
            }
        }

        function updateCharge(obj) {
            if(window.confirm('确认修改该服务费吗？')) {
                var variId = $(obj).parent().parent().find("input[name='variId']").val();
                $(obj).parent().parent().find("input[name='serviceMode']").removeAttr("disabled");
                var serviceMode = $(obj).parent().parent().find("select[name='serviceMode']").val();
                var servicePrice = $(obj).parent().parent().find("input[name='servicePrice']").val();
                var id = $(obj).parent().parent().find("input[name='chargeId']").val();
                $.ajax({
                   type:"post",
                   url:"${ctx}/biz/order/bizServiceCharge/updateServiceCharge",
                   data:{"variId":variId, "serviceMode":serviceMode, "servicePrice":servicePrice, "id":id},
                   success:function (data) {
                       if (data == 'ok') {
                           alert("修改成功");
                           window.location.reload();
                       }
                       if (data == 'error') {
                           alert("修改失败");
                           window.location.reload();
                       }
                   }
                });
            } else {
                return;
            }
        }
    </script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizServiceLine/">服务费物流线路列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizServiceLine/form?id=${bizServiceLine.id}&source=${bizServiceLine.source}">服务费物流线路<shiro:hasPermission name="biz:order:bizServiceLine:edit">${not empty bizServiceLine.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizServiceLine:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizServiceLine" action="${ctx}/biz/order/bizServiceLine/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="province" value="${bizServiceLine.province.code}" type="hidden"/>
		<input id="city" value="${bizServiceLine.city.code}" type="hidden"/>
		<input id="region" value="${bizServiceLine.region.code}" type="hidden"/>
		<input id="toProvince" value="${bizServiceLine.toProvince.code}" type="hidden"/>
		<input id="toCity" value="${bizServiceLine.toCity.code}" type="hidden"/>
		<input id="toRegion" value="${bizServiceLine.toRegion.code}" type="hidden"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">发货路线</label>
			<div class="controls">
				从
				<form:select id="province0" path="province.code" class="input-medium required">
					<option>===省====</option>
				</form:select>
				<form:select id="city0" path="city.code" class="input-medium required">
					<option>===市====</option>
				</form:select>
				<form:select id="region0" path="region.code" class="input-medium required">
					<option>===县/区====</option>
				</form:select>
				<br>
				至
				<form:select id="toProvince0" path="toProvince.code" class="input-medium required">
					<option>===省====</option>
				</form:select>
				<form:select id="toCity0" path="toCity.code" class="input-medium required">
					<option>===市====</option>
				</form:select>
				<form:select id="toRegion0" path="toRegion.code" class="input-medium required">
					<option>===县/区====</option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否启用：</label>
			<div class="controls">
				<c:if test="${bizServiceLine.usable != 0}">
					<font color="green">已启用</font>
				</c:if>
				<c:if test="${bizServiceLine.usable == 0}">
					<form:checkbox path="usable" value="${variId}"/>是
				</c:if>
			</div>
		</div>
		<div class="form-actions">
            <c:if test="${bizServiceLine.source ne 'detail'}">
			    <shiro:hasPermission name="biz:order:bizServiceLine:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			</c:if>
            <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>

    <form:form class="form-horizontal">
	<div class="control-group">
		<div class="controls">
			<table class="table table-striped table-bordered table-condensed">
                <th>品类</th>
                <th>服务方式</th>
                <th>服务费</th>
                <shiro:hasPermission name="biz:order:bizServiceCharge:edit">
                    <c:if test="${bizServiceLine.source ne 'detail'}">
                        <th>操作</th>
                    </c:if>
                </shiro:hasPermission>
				<c:forEach items="${serviceChargeList}" var="serviceCharge">
					<tr>
						<td>
                            <c:if test="${serviceCharge.varietyInfo.id == 0}">非拉杆箱<input type="hidden" name="variId" value="0"/></c:if>
                            <c:if test="${serviceCharge.varietyInfo.id != 0}">拉杆箱<input type="hidden" name="variId" value="${variId}"/></c:if>
                        </td>
						<td>
							<select name="serviceMode" class="input-medium" disabled="disabled">
								<c:forEach items="${serviceChaDictList}" var="dict">
                                    <option value="${dict.value}" label="${dict.label}"<c:if test="${serviceCharge.serviceMode == dict.value}">selected="selected"</c:if>/>
								</c:forEach>
							</select>
						</td>
						<td>
							<input name="servicePrice" value="${serviceCharge.servicePrice}" class="input-mini required" type="number" min="0"/>&nbsp;元/支(元/套)
						</td>
						<td>
                            <shiro:hasPermission name="biz:order:bizServiceCharge:edit">
                                <c:if test="${bizServiceLine.source ne 'detail'}">
                                    <input type="hidden" name="chargeId" value="${serviceCharge.id}"/>
                                    <a href="#" onclick="updateCharge(this)">修改</a>
                                </c:if>
                            </shiro:hasPermission>
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
    </form:form>
    <script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>
    <script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>
    <script src="${ctxStatic}/bootstrap/2.3.1/docs/assets/js/bootstrap-collapse.js" type="text/javascript"></script>
</body>
</html>
<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费设置管理</title>
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
            var region = $("#region").val();
            var toRegion = $("#toRegion").val();
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizServiceCharge/">服务费设置列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizServiceCharge/form?id=${bizServiceCharge.id}">服务费设置方式<shiro:hasPermission name="biz:order:bizServiceCharge:edit">${not empty bizServiceCharge.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizServiceCharge:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizServiceCharge" action="${ctx}/biz/order/bizServiceCharge/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="province" value="${bizServiceCharge.serviceLine.province.code}" type="hidden"/>
		<input id="city" value="${bizServiceCharge.serviceLine.city.code}" type="hidden"/>
		<input id="region" value="${bizServiceCharge.serviceLine.region.code}" type="hidden"/>
		<input id="toProvince" value="${bizServiceCharge.serviceLine.toProvince.code}" type="hidden"/>
		<input id="toCity" value="${bizServiceCharge.serviceLine.toCity.code}" type="hidden"/>
		<input id="toRegion" value="${bizServiceCharge.serviceLine.toRegion.code}" type="hidden"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">发货路线</label>
			<div class="controls">
				从
				<form:select id="province0" path="serviceLine.province.code" class="input-medium required">
					<option>===省====</option>
				</form:select>
				<form:select id="city0" path="serviceLine.city.code" class="input-medium required">
					<option>===市====</option>
				</form:select>
				<form:select id="region0" path="serviceLine.region.code" class="input-medium required">
					<option>===县/区====</option>
				</form:select>
				<br>
				至
				<form:select id="toProvince0" path="serviceLine.toProvince.code" class="input-medium required">
					<option>===省====</option>
				</form:select>
				<form:select id="toCity0" path="serviceLine.toCity.code" class="input-medium required">
					<option>===市====</option>
				</form:select>
				<form:select id="toRegion0" path="serviceLine.toRegion.code" class="input-medium required">
					<option>===县/区====</option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">商品品类</label>
			<div class="controls">
				<form:select path="varietyInfo.id" cssClass="input-medium required" itemLabel="${bizServiceCharge.varietyInfo.name}" itemValue="${bizServiceCharge.varietyInfo.id}">
					<form:option value="0">非拉杆箱</form:option>
					<form:option value="1">拉杆箱</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">服务费</label>
			<div class="controls">
				<form:select path="serviceMode" cssClass="input-medium required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('service_cha')}" itemLabel="label" itemValue="value"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
				<input name="servicePrice" value="${bizServiceCharge.servicePrice}" class="input-mini required" type="number" min="0"/>&nbsp;元/支(元/套)
				<span class="help-inline"><font color="red">*</font> </span>&nbsp;&nbsp;
				是否开启&nbsp;<input name="serviceLine.usable" type="checkbox" value="1"/>是&nbsp;<input name="serviceLine.usable" type="checkbox" value="0"/>否
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>
	<script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>
	<script src="${ctxStatic}/bootstrap/2.3.1/docs/assets/js/bootstrap-collapse.js" type="text/javascript"></script>
</body>
</html>
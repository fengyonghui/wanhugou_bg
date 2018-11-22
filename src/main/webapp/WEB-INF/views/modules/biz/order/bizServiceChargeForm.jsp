<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费--配送方式管理</title>
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
            $("#province").change( function () {
                CityBind();
            })

            $("#city").change(function () {
                VillageBind();
            })




        })
        function Bind(str) {
            alert($("#Province").html());
            $("#Province").val(str);


        }
        function ProviceBind() {
            //清空下拉数据
            $("#province").html("");

            var str = "<option>==省===</option>";
            $.ajax({
                type: "post",
                url: "/sys/sysRegion/selectRegion",
				data:{"level":"prov"},
                dataType: "JSON",
                async: false,
                success: function (data) {
                    //从服务器获取数据进行绑定
                    $.each(data.data, function (i, item) {
                        str += "<option value=" + item.code + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    $("#province").append(str);
                },
                error: function () { alert("Error"); }
            });




        }
        function CityBind() {

            var provice = $("#province").attr("value");
            //判断省份这个下拉框选中的值是否为空
            if (provice == "") {
                return;
            }
            $("#city").html("");
            var str = "<option>==市===</option>";

            $.ajax({
                type: "post",
                url: "/sys/sysRegion/selectRegion",
                data: { "code":provice,"level":"city"},
                dataType: "JSON",
                async: false,
                success: function (data) {
                    //从服务器获取数据进行绑定
                    $.each(data.data, function (i, item) {
                        str += "<option value=" + item.code + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    $("#city").append(str);
                },
                error: function () { alert("Error"); }
            });
        }
        function VillageBind() {
            var provice = $("#city").attr("value");
            //判断市这个下拉框选中的值是否为空
            if (provice == "") {
                return;
            }
            $("#region").html("");
            var str = "<option>==请选择===</option>";
            //将市的ID拿到数据库进行查询，查询出他的下级进行绑定
            $.ajax({
                type: "post",
                url: "/sys/sysRegion/selectRegion",
                data: { "code":provice, "level":"dist" },
                dataType: "JSON",
                async: false,
                success: function (data) {
                    //从服务器获取数据进行绑定
                    $.each(data.data, function (i, item) {
                        str += "<option value=" + item.id + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    $("#region").append(str);
                },
                error: function () { alert("Error"); }
            });
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
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">发货路线：</label>
			<div class="controls">
				从
				<select id="province" name="provinces">
					<option>----省----</option>
				</select>
				<select id="city" name="citys">
					<option>----市----</option>
				</select>
				<select id="region" name="regions">
					<option>----区----</option>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<%--<div class="form-group">--%>
<%--                <div class="col-sm-2">--%>
<%--                    <select class="form-control" name="Province" id="Province">--%>
<%--                        <option>==省===</option>--%>
<%--                    </select>--%>
<%--                </div>--%>
<%--                <div class="col-sm-2">--%>
<%--                    <select class="form-control" name="City" id="City">--%>
<%--                        <option>==市===</option>--%>
<%--                    </select>--%>
<%--                </div>--%>
<%--                <div class="col-sm-2">--%>
<%--                    <select class="form-control" name="Village" id="Village">--%>
<%--                        <option>==县/区===</option>--%>
<%--                    </select>--%>
<%--                </div>--%>
<%--            </div>--%>
		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
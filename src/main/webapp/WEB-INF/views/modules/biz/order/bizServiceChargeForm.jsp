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
                CityBind("from");
            });
            $("#toProvince").change( function () {
                CityBind("to");
            });

            $("#city").change(function () {
                VillageBind("from");
            });
            $("#toCity").change(function () {
                VillageBind("to");
            })

        });
        function Bind(str) {
            alert($("#Province").html());
            $("#Province").val(str);


        }
        function ProviceBind() {
            //清空下拉数据
            $("#province").html("");
            $("#toProvince").html("");

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
                        str += "<option value=" + item.code + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    $("#province").append(str);
                    $("#toProvince").append(str);
                },
                error: function () { alert("Error"); }
            });




        }
        function CityBind(obj) {
			var provice;
			if (obj == 'from') {
                provice = $("#province").attr("value");
			} else {
                provice = $("#toProvince").attr("value");
			}
            //判断省份这个下拉框选中的值是否为空
            if (provice == "") {
                return;
            }
            if (obj == 'from') {
                $("#city").html("");
			}
            if (obj == 'to') {
                $("#toCity").html("");
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
                        $("#city").append(str);
                    }
                    if (obj == 'to') {
                        $("#toCity").append(str);
                    }
                },
                error: function () { alert("Error"); }
            });
        }
        function VillageBind(obj) {
            var city;
            if (obj == 'from') {
                city = $("#city").attr("value");
			} else {
                city = $("#toCity").attr("value");
			}
            //判断市这个下拉框选中的值是否为空
            if (city == "") {
                return;
            }
            if (obj == 'from') {
                $("#region").html("");
            }
            if (obj == 'to') {
                $("#toRegion").html("");
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
                        str += "<option value=" + item.id + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    if (obj == 'from') {
                        $("#region").append(str);
                    }
                    if (obj == 'to') {
                        $("#toRegion").append(str);
                    }
                },
                error: function () { alert("Error"); }
            });
        }
	</script>

    <%--<style type="text/css">--%>
        <%--*{margin:0;padding:0;}--%>
        <%--body { font-size: 14px; line-height: 130%; padding: 60px }--%>
        <%--#panel { width: 262px; border: 1px solid #0050D0 }--%>
        <%--.head { padding: 5px; background: #96E555; cursor: pointer }--%>
        <%--.content { padding: 10px; text-indent: 2em; border-top: 1px solid #0050D0;display:block; }--%>
    <%--</style>--%>
    <script type="text/javascript">
        $(function(){
        });
        function toggle(item) {
            $("#"+item).next("div.controls").toggle();
        }
        function add(item) {
			var num = parseInt(item.replace('header','')) + parseInt(1);
			var sourceNode = $("#"+item); // 获得被克隆的节点对象
            $("#"+item).attr("onclick","add('"+num+"')");
            var clonedNode = sourceNode[0].cloneNode(true);// 克隆节点
            clonedNode.setAttribute("id", "header" + num); // 修改一下id 值，避免id 重复
            sourceNode.after(clonedNode);
            $("#"+item).next("div.controls").toggle();
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
		<div id="aa0" class="control-group">
			<label id="header0" class="control-label" onclick="toggle(this.id)"><font size="3">发货路线：</font></label>
			<div id="route0" class="controls" style="background-color: #e8e8e8; width: 50%">
				从
				<select id="province0" name="provinces" class="input-medium required">
					<option>===省====</option>
				</select>
				<select id="city0" name="citys" class="input-medium required">
					<option>===市====</option>
				</select>
				<select id="region0" name="regions" class="input-medium">
					<option>===县/区====</option>
				</select>
				<br>
				至
				<select id="toProvince0" name="toProvinces" class="input-medium required">
					<option>===省====</option>
				</select>
				<select id="toCity0" name="toCitys" class="input-medium required">
					<option>===市====</option>
				</select>
				<select id="toRegion0" name="toRegions" class="input-medium">
					<option>===县/区====</option>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
				<div>
					&nbsp;
					<table>
						<tr style="height: 40px">
							<td>商品品类：拉杆箱<input name="variIds" value="${variId}" type="hidden"/></td>
							<td></td>
							<td>
								<input name="torderTypes" type="checkbox" value=""/>联营订单<input name="torderTypes" type="checkbox" value=""/>代采订单<input name="torderTypes" type="checkbox" value=""/>零售订单
							</td>
						</tr>
						<c:set var="flag" value="true"></c:set>
						<c:forEach items="${serviceModeList}" var="serviceMode" varStatus="i">
							<tr style="height: 40px">
								<c:if test="${flag}">
									<td rowspan="3" valign="top">服务费</td>
								</c:if>
								<td>${serviceModeList[i.index].label}<input name="serviceModes" value="${serviceModeList[i.index].value}" type="hidden"/></td>
								<td><input name="servicePrices" class="input-mini required" type="number" min="0"/>元/支(元/套)</td>
							</tr>
							<c:set var="flag" value="false"></c:set>
						</c:forEach>
						<tr style="height: 40px">
							<td>商品品类：非拉杆箱<input name="variIds" value="-1" type="hidden"/></td>
							<td></td>
							<td><input name="forderTypes" type="checkbox" value=""/>联营订单<input name="forderTypes" type="checkbox" value=""/>代采订单<input name="forderTypes" type="checkbox" value=""/>零售订单</td>
						</tr>
						<c:set var="flag" value="true"></c:set>
						<c:forEach items="${serviceModeList}" var="serviceMode" varStatus="i">
							<tr style="height: 40px">
								<c:if test="${flag}">
									<td rowspan="3" valign="top">服务费</td>
								</c:if>
								<td>${serviceModeList[i.index].label}<input name="serviceModes" value="${serviceModeList[i.index].value}" type="hidden"/></td>
								<td><input name="servicePrices" class="input-mini required" type="number" min="0"/>元/支(元/套)</td>
							</tr>
							<c:set var="flag" value="false"></c:set>
						</c:forEach>
						<tr style="height: 40px">
							<td>是否开启&nbsp;<input name="usables" type="checkbox" value="1"/>是</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div align="right"><input class="btn btn-primary" type="button" value="添加" onclick="add('header0')"/></div>

		<div class="form-actions">
			<shiro:hasPermission name="biz:order:bizServiceCharge:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	<script src="${ctxStatic}/bootstrap/2.3.1/docs/assets/js/bootstrap-collapse.js" type="text/javascript"></script>
</body>
</html>
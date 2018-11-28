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
        function Bind(str) {
            // alert($("#Province").html());
            $("#Province").val(str);


        }
        function ProviceBind() {
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
                    // console.info(data);
                    //从服务器获取数据进行绑定
                    $.each(data, function (i, item) {
                        str += "<option value=" + item.code + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    $("#province0").append(str);
                    $("#toProvince0").append(str);
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
            var index;
            var id = $(item).attr("id");
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
            var index;
            var id = $(item).attr("id");
            if (obj == 'from') {
                index = id.replace('city','');
            } else {
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
	</script>

    <%--<style type="text/css">--%>
        <%--*{margin:0;padding:0;}--%>
        <%--body { font-size: 14px; line-height: 130%; padding: 60px }--%>
        <%--#panel { width: 262px; border: 1px solid #0050D0 }--%>
        <%--.head { padding: 5px; background: #96E555; cursor: pointer }--%>
        <%--.content { padding: 10px; text-indent: 2em; border-top: 1px solid #0050D0;display:block; }--%>
    <%--</style>--%>
    <script type="text/javascript">
        function toggle(item) {
            $("#"+item).next("div.controls").toggle();
        }
        function add(item) {
            var n = item.replace('header','');
            var num = parseInt(n) + parseInt(1);
            var sourceNode = $("#"+item).parent(); // 获得被克隆的节点对象
            var clonedNode = sourceNode.clone(true);// 克隆节点
            clonedNode.find("#"+item).attr("id", "header" + num); // 修改一下id 值，避免id 重复
			clonedNode.find("#province"+n).attr("id","province" + num);
			clonedNode.find("#province"+num).attr("name","serviceLineList["+num+"].province.code");
			clonedNode.find("#toProvince"+n).attr("id","toProvince" + num);
            clonedNode.find("#toProvince"+num).attr("name","serviceLineList["+num+"].toProvince.code");
			clonedNode.find("#city"+n).attr("id","city" + num);
            clonedNode.find("#city"+num).attr("name","serviceLineList["+num+"].city.code");
			clonedNode.find("#toCity"+n).attr("id","toCity" + num);
            clonedNode.find("#toCity"+num).attr("name","serviceLineList["+num+"].toCity.code");
			clonedNode.find("#region"+n).attr("id","region" + num);
            clonedNode.find("#region"+num).attr("name","serviceLineList["+num+"].region.code");
			clonedNode.find("#toRegion"+n).attr("id","toRegion" + num);
            clonedNode.find("#toRegion"+num).attr("name","serviceLineList["+num+"].toRegion.code");

            clonedNode.find("td[name='serviceModeTd']").each(function() {
                var childInput = $(this).find("input");
				var nameVal = childInput.attr('name');
                nameVal = nameVal.replace('serviceLineList[' + n +']', 'serviceLineList[' + num +']');
                childInput.attr("name", nameVal);
            });

            clonedNode.find("#usable"+n).attr("id","usable" + num);
            clonedNode.find("#usable"+num).attr("name","serviceLineList["+num+"].usable");

            Province("province" + num);
            Province("toProvince" + num);
            $("#province" + num).change( function () {
                CityBind("from",this);
            });
            $("#toProvince" + num).change( function () {
                CityBind("to",this);
            });
            $("#city" + num).change(function () {
                VillageBind("from",this);
            });
            $("#toCity" + num).change(function () {
                VillageBind("to",this);
            });
            sourceNode.after(clonedNode);
            $("#"+item).next("div.controls").toggle();
            $("#btnAdd").attr("onclick","add('header"+num+"')");
            if (n == 0) {
                $("#btnAdd").after("<input id=\"btnDel\" class=\"btn btn-primary\" type=\"button\" value=\"删除路线\" onclick=\"del('header1')\"/>");
			} else {
                $("#btnDel").attr("onclick","del('header"+num+"')");
            }
        }
        function del(item) {
            var n = item.replace('header','');
            var num = (parseInt(n) - parseInt(1)) < 0 ? 0 : parseInt(n) - parseInt(1);
            if (n > 1) {
                $("#btnDel").attr("onclick","del('header"+num+"')");
			} else {
                $("#btnDel").remove();
			}
            $("#btnAdd").attr("onclick","add('header"+num+"')");
			$("#"+item).parent().remove();
        }
        function Province(item) {
            // alert(item);
			$("#"+item).html("");
            var str = "<option>===省====</option>";
            $.ajax({
                type: "post",
                url: "${ctx}/sys/sysRegion/selectRegion",
                data:{"level":"prov"},
                async: false,
                success: function (data) {
                    // console.info(data);
                    //从服务器获取数据进行绑定
                    $.each(data, function (i, item) {
                        str += "<option value=" + item.code + ">" + item.name + "</option>";
                    });
                    //将数据添加到省份这个下拉框里面
                    $("#"+item).append(str);
                    // console.info(str);
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
			<label id="header0" class="control-label" onclick="toggle(this.id)"><span style="opacity: 0.8;color: red;">折叠或展开</span><font size="3">发货路线：</font></label>
			<div class="controls" style="background-color: #e8e8e8; width: 50%">
				从
				<select id="province0" name="serviceLineList[0].province.code" class="input-medium required">
					<option>===省====</option>
				</select>
				<select id="city0" name="serviceLineList[0].city.code" class="input-medium required">
					<option>===市====</option>
				</select>
				<select id="region0" name="serviceLineList[0].region.code" class="input-medium required">
					<option>===县/区====</option>
				</select>
				<br>
				至
				<select id="toProvince0" name="serviceLineList[0].toProvince.code" class="input-medium required">
					<option>===省====</option>
				</select>
				<select id="toCity0" name="serviceLineList[0].toCity.code" class="input-medium required">
					<option>===市====</option>
				</select>
				<select id="toRegion0" name="serviceLineList[0].toRegion.code" class="input-medium required">
					<option>===县/区====</option>
				</select>
				<span class="help-inline"><font color="red">*</font> </span>
				<div>
					&nbsp;
					<table>
						<tr style="height: 40px">
							<td>商品品类：拉杆箱<input name="" value="${variId}" type="hidden"/></td>
						</tr>
						<c:set var="flag" value="true"></c:set>
						<c:forEach items="${serviceModeList}" var="serviceMode" varStatus="i">
							<tr style="height: 40px">
								<c:if test="${flag}">
									<td rowspan="3" valign="top">服务费</td>
								</c:if>
								<td>${serviceModeList[i.index].label}<input name="" value="${serviceModeList[i.index].value}" type="hidden"/></td>
								<td name="serviceModeTd"><input name="serviceLineList[0].chargeMap[${variId}_${serviceModeList[i.index].value}]" class="input-mini required" type="number" min="0"/>元/支(元/套)</td>
							</tr>
							<c:set var="flag" value="false"></c:set>
						</c:forEach>
						<tr style="height: 40px">
							<td>商品品类：非拉杆箱<input name="" value="0" type="hidden"/></td>
						</tr>
						<c:set var="flag" value="true"></c:set>
						<c:forEach items="${serviceModeList}" var="serviceMode" varStatus="i">
							<tr style="height: 40px">
								<c:if test="${flag}">
									<td rowspan="3" valign="top">服务费</td>
								</c:if>
								<td>${serviceModeList[i.index].label}<input name="" value="${serviceModeList[i.index].value}" type="hidden"/></td>
								<td name="serviceModeTd"><input name="serviceLineList[0].chargeMap[0_${serviceModeList[i.index].value}]" class="input-mini required" type="number" min="0"/>元/支(元/套)</td>
							</tr>
							<c:set var="flag" value="false"></c:set>
						</c:forEach>
						<tr style="height: 40px">
							<td>是否开启&nbsp;<input id="usable0" name="serviceLineList[0].usable" type="checkbox" value="1"/>是</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div align="right">
			<input id="btnAdd" class="btn btn-primary" type="button" value="添加路线" onclick="add('header0')"/>
			<%--<input id="btnDel" class="btn btn-primary" type="button" value="删除路线" onclick="del('header0')"/>--%>
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
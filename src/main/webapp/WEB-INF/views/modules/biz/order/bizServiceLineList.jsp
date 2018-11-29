<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>服务费物流线路管理</title>
	<meta name="decorator" content="default"/>
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
            //清空下拉数据
            $("#province0").html("");
            $("#toProvince0").html("");

            var str = "<option value=''>===省====</option>";
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
            var str = "<option value=''>===市====</option>";

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
            var str = "<option value=''>===县/区====</option>";
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
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/biz/order/bizServiceLine/">服务费物流线路列表</a></li>
		<shiro:hasPermission name="biz:order:bizServiceLine:edit"><li><a href="${ctx}/biz/order/bizServiceCharge/form">服务费物流线路添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="bizServiceLine" action="${ctx}/biz/order/bizServiceLine/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<%--<li><label>订单类型：</label>--%>
				<%--<form:select path="orderType" htmlEscape="false" class="input-medium">--%>
					<%--<form:option value="" label="请选择"/>--%>
					<%--<form:options items="${fns:getDictList('service_order_type')}" itemValue="value" itemLabel="label"/>--%>
				<%--</form:select>--%>
			<%--</li>--%>
            <li><label>订单类型：</label>
                从
                <form:select id="province0" path="province.code" class="input-medium required">
                    <option value="">===省====</option>
                </form:select>
                <form:select id="city0" path="city.code" class="input-medium required">
                    <option value="">===市====</option>
                </form:select>
                <form:select id="region0" path="region.code" class="input-medium required">
                    <option value="">===县/区====</option>
                </form:select>
                至
                <form:select id="toProvince0" path="toProvince.code" class="input-medium required">
                    <option value="">===省====</option>
                </form:select>
                <form:select id="toCity0" path="toCity.code" class="input-medium required">
                    <option value="">===市====</option>
                </form:select>
                <form:select id="toRegion0" path="toRegion.code" class="input-medium required">
                    <option value="">===县/区====</option>
                </form:select>
            </li>
			<li><label>是否启用：</label>
				<form:select path="usable" htmlEscape="false" class="input-mini">
					<form:option value="" label="请选择"/>
					<form:option value="1" label="启用"/>
					<form:option value="0" label="不启用"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>序号</th>
				<th>开始地</th>
				<th>到达地</th>
				<th>是否启用</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<shiro:hasPermission name="biz:order:bizServiceLine:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizServiceLine" varStatus="index">
			<tr>
				<td>${index.index + 1}</td>
				<td>${bizServiceLine.province.name}${bizServiceLine.city.name}${bizServiceLine.region.name}</td>
				<td>${bizServiceLine.toProvince.name}${bizServiceLine.toCity.name}${bizServiceLine.toRegion.name}</td>
				<td>${bizServiceLine.usable == 1 ? '启用' : '不启用'}</td>
				<td>${bizServiceLine.createBy.name}</td>
				<td><fmt:formatDate value="${bizServiceLine.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${bizServiceLine.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>
                    <shiro:hasPermission name="biz:order:bizServiceLine:view">
                        <a href="${ctx}/biz/order/bizServiceLine/form?id=${bizServiceLine.id}&source=detail">详情</a>
                        <shiro:hasPermission name="biz:order:bizServiceLine:edit">
                            <a href="${ctx}/biz/order/bizServiceLine/form?id=${bizServiceLine.id}">修改</a>
                            <a href="${ctx}/biz/order/bizServiceLine/delete?id=${bizServiceLine.id}" onclick="return confirmx('确认要删除该服务费物流线路吗？', this.href)">删除</a>
                        </shiro:hasPermission>
                    </shiro:hasPermission>
                </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
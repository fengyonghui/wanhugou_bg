<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>积分流水管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
            $("#buttonExport").click(function(){
                top.$.jBox.confirm("确认要导出积分流水数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/biz/integration/bizMoneyRecode/recodeExport");
                        $("#searchForm").submit();
                        $("#searchForm").attr("action","${ctx}/biz/integration/bizMoneyRecode/list");
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            });
            $.ajax({
                url:"${ctx}/biz/integration/bizMoneyRecode/detail",
                type:"get",
                data:'',
                contentType:"application/json;charset=utf-8",
                success:function(data){
                    $("#hd").val(data.gainIntegration);
                    $("#sy").val(data.usedIntegration);
                    $("#gq").val(data.expireIntegration);
                    $("#ky").val(data.availableIntegration);
                }
            })
        });
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
		<li class="active"><a href="${ctx}/biz/integration/bizMoneyRecode/">积分流水列表</a></li>
		<shiro:hasPermission name="biz:stream:bizMoneyRecode:edit"><li><a href="${ctx}/biz/stream/bizMoneyRecode/form">积分流水添加</a></li></shiro:hasPermission>
	</ul>

	     <div style="margin-left: 60px">
			 平台万户币汇总: &nbsp;&nbsp;
			 累计获得万户币: <input type="text"  id="hd" style="width:100px" value="" readonly="true">
			 累计使用万户币: <input type="text" id="sy"  style="width:100px" value="" readonly="true">
			 累计过期万户币: <input type="text" id="gq" style="width:100px" value="" readonly="true">
			 可用万户币:<input type="text" id="ky" style="width:100px" value="" readonly="true">
	     <div>
	<form:form id="searchForm" modelAttribute="bizMoneyRecode" action="${ctx}/biz/integration/bizMoneyRecode/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>流水类型：</label>
				<form:select path="statusCode" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('integration_status_code')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>生成时间：</label>
				<input name="beginCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizMoneyRecode.beginCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> -
				<input name="endCreateDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizMoneyRecode.endCreateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>

			<li><label>经销店名称：</label>
					<sys:treeselect id="office" name="customer.id" value="${office.id}"  labelName="office.name"
									labelValue="${office.name}" notAllowSelectParent="true"
									title="经销店"  url="/sys/office/queryTreeList?type=6"
									cssClass="input-medium required"
									allowClear="true"/>
			</li>
			<li><label>经销店电话：</label>
				<form:input path="office.phone" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input id="buttonExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>流水id</th>
				<th>经销店名称</th>
				<th>负责人</th>
				<th>负责人电话</th>
				<th>流水数量</th>
				<th>流水类型</th>
				<th>流水说明</th>
				<th>生成时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizMoneyRecode">
			<tr>
				<td>
						${bizMoneyRecode.id}
				</td>
				<td>
				    	${bizMoneyRecode.office.name}
				</td>
				<td>
						${bizMoneyRecode.office.master}
				</td>
				<td>
						${bizMoneyRecode.office.phone}
				</td>
				<td>
					    ${bizMoneyRecode.money}
				</td>
				<td>
						${bizMoneyRecode.statusName}
				</td>
				<td>
				    	${bizMoneyRecode.comment}
				</td>
				<td>
				    	<fmt:formatDate value="${bizMoneyRecode.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
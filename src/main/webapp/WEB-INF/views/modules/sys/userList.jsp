<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.OfficeTypeEnum" %>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/bootstrap/printThis.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sys/user/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});

            $('#myModal').on('hide.bs.modal', function () {
                window.location.href="${ctx}/sys/user";

            });
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sys/user/list");
			$("#searchForm").submit();
	    	return false;
	    }


	    function genUserCode(id) {
            $.ajax({
                type:"get",
                url:"${ctx}/sys/userCode/genUserQRCode?id="+id,
                success:function (data) {
                    alert(data)
                    var aa="<img src='"+data+"'/>";

                     $("#userImg").html(aa);
				}
            });

                <%--$.getJSON("${ctx}/sys/userCode/genUserQRCode?id="+id+"&date="+new Date(),function(data){--%>
                    <%--alert(data);--%>
                    <%--// var aa="<img src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAYAAAB5fY51AAAfCklEQVR42u3dCZRdVZ3v8VtzpQIK\r\nKKsFhcdrISFDVVJzbt1bt6YkjCIaQGQQgiDyEFcD4mNopgZbfM2g8HSBgHS70ooLH2KvJ2g3DQJJ\r\nkxgQGwnIsEIggwkkqdR0pxp+b+99ap/cgrahnyR1h+9vsVdVqk6duty653P/e5+9z4mIEEIKJBGe\r\nAkIIYBFCCGARQgCLEEIAixBCAIsQAliEEAJYhBACWIQQwCKEEMAihBDAIoQAFiGEABYhhAAWIQSw\r\nCCEEsAghBLAIIYBFCCGARQghgEUIASxCCAEsQggBLEIIYBFCCGARQsh0gBWJRAqifVCP/7+6nz39\r\nPE/X/j+ox1Pov3dP7yffjhfAAizAAizAAizAAizAAizAAizAAizAAizAAizAAizAAizAAqy9cyBN\r\n1+PZ0/vJtxfcdB3Ae/p5yzdAi/V4ASzAAizAAizAAizAAizAAizAAizAAizAAizAAizAAizAAizA\r\n2jundQvlwCuUA3W6XtD5Nj0l394I8+14ASzAAizAAizAAizAAizAAizAAizAAizAAizAAizAAizA\r\nAizAKu4/QKEfSPm2KDefD5h8ABqwAAuwAAuwAAuwAAuwOF4AC7AAC7AAC7AAC7AAC7AAC7AAi+MF\r\nsApxWkO+ATRdf8fpAiKfLwEMWIAFWIAFWIAFWIAFWBwvgAVYgAVYgAVYgAVYgAVYgAVYgMXxAliF\r\n8njYz/S+ARQruFwiGbCABrAAC7AAi/0AFmABFmABFmABFmABDWABFmABFmABFmAVN1j51vLtAGZ7\r\nti/E4wWwAIvt2R6wAIsDhu0BC7AAi+3ZHrAAC7DYHrAAC7A4YNgesACrOMKi6/w8Hc6i9BI6BnkK\r\nAAuwAAuwAAuwAAuwAAuwAAuwAIsAFmABFmABFmABFmABVt68gPJt/6W2uLdQTueX2rQMwAIswAIs\r\nwAIswAIswAIswAIswAIswAIswAIswAIswAIswAKsvQNNsZ7+z7dFqvl2YO/p/69CgSnfbrwKWIAF\r\nWIAFWIAFWIAFWIAFWIAFWIAFWIAFWIAFWIAFWIAFWIC1d17o+fbCyrcDcrqen+l6gynWaSWlvEgb\r\nsAALsAALsAALsAALsAALsAALsAALsAALsAALsAALsAALsEoZrGIFrlAODBZLF9bic26kCliABViA\r\nBViABViABViABViABViABViABViABViABViABVjTC1a+QVasB16xplCet2JdxA5YgAVYgAVYgAVY\r\ngAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgFVYwJXadIR8g6AUbvC5N5+f6dp/XjyHgAVYgAVYgAVY\r\ngAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgFXCYBUrHNMFSqEfGMW6mLlQjgvAAizAAizAAizAAizA\r\nAizAAizAAizAAizAAizAAizAAizAAqziBqVYb/xZrMAV+o1gC/11CFiABViABViABViABViABViA\r\nBViABViABViABViABViABViAtXfgyzdA8w3WfDtg8u3vXuhvtMU6LQOwShisysrKaW1VNdUqr6jR\r\n/nUHSGkpOyGNmcc1dnyHUj2NSnYtUDLRoqGuVtcGehu1y3xdi1pcS3c2u5aKm2Y+AhZgARZg7bFW\r\nXVujSFmVEu1dRqsAq52DSQ32LtRwV4NGTMsFy2I1YMGK5oK1Gy7AAizAAqw91moqDFrlFbr0q5cE\r\nWmXHlf7NbzXaMUuKzZY65rg2Hp+nbOc8jfQc6dpY52yNxeeb7Zo1FmsGLMACrFIAa5999vmTmOy7\r\n77760Ic+9B9+74rLL9cFF1zwrq8feOCBWrJkifv865ddposvvjhsl156qQ4++OCpFZbBqraySj+8\r\n9x8CsNKjWv/QL6Tu+aaKMmh1HGk+HmnAmm/Amq/hntkOrNH4bI3G5jmsxjqaw0oLsAALsIoYrKuu\r\nukpPPfmknnziCW3dulWPP/ZY2OzXvvnNb4a4fOQjH9FnP/tZ1yw+1193XfjveXPnum0OOOAA/duq\r\nVZo1a5Z6e3sdbMuWLdMvH3lEl33ta6qrq5sCVqQ24trq369VWqPS4LD+eP03lOmao1TnLKW7jtSw\r\nwWuwt8l1BUfM5xlTaal9oekSLgzGuEy3MdVpWqIBsAArfwAqtUvf7o39eDgsNM8///x/2n2rr6/X\r\nqpUrdc7y5VPa3XffrUsuuSTc7r8deqir3Oz3LFJbNm/WzX/3d+7fDQ0NU8Gqiahm/xnqzw6GYD17\r\n/lc0HP1LZbvnOrAsUoMGq8HeAKzsu8BaoHQiQKsYphHwRgtYgPUeYB122GHavGmTHvrZz8K2/Oyz\r\n3wXWT+6//12QnXvuuSFYX73oIve5raQeefhhNTc3u0rNfrzu2mv1uVNOmdolLIso1tasjLJKKiW9\r\n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\r\n1tGiUdOyncEY128/e7S0/lU398s9qtOO07Cdp2VasptBd8AqgP/hQr+08XT+4T1W/n6B/q7PHiV7\r\nS3k7mdRfTuYHP/jBlGqsOhKAVV4RXJWhurZCJ51yssbHArAmzMc7v/O9oBrKGmKee1Grl51oqqsj\r\nlE3MDbByYAUVVnISLFtRjcXnKxtt0ljMgjXfDcyv/fQS6aUX3HIgB9a5pyjV0+zQsmAV0g1QP+iT\r\nJ/kOLmAB1gcC1nvdCPXzn/+829aOZ9nW1NQUznTffX9CW5lVu0vM1NZU6dFfP+HGsoJCa8INxmfs\r\nHKw7v6eBnkXaaYAZ7mk13bjmYHb7lLOF84OxLTu9wZ5NjLWGawo3HxWVHvtleAfo9A1XalOPrcha\r\nXLUGWIAFWCUMlp/CYK/U4PPoo4+G3cndi6GrQ7DKzPdaFkU1nMmGd5YPpjuYbuFfX6HBSbCGuluU\r\nTAQz2/18q/BsoZ25Hm91ZwxT8bbwMjObjlok/d8Hd9+y/pYbtaUvNtm1BCzAAqySBsvfpiuRSLjt\r\nfdfw+OOPn3KziRkz9jF4Vbjqqqa6QmVV1brn7/8hXOIzKnsZmoz0uZM01BvVQFdTcNG+xDvWDiam\r\ngmWxsi3bXa9M13xTYRmw/vE+hYNYd9+urUs63X4sfoAFWIBVwmD5MS37+YMPPhjOzbLLd+wNWP0M\r\neAtWeXmlq7DKy8zjr6zSX3z8E26g3ibryMpqcHG3RkxFNGirK9MlTJqunK+w7HIcP5YVdAlbJ6ur\r\nRZOXmJmnLcd0SPd8d3eFteIeB9ZwvEmprlbAAizAKmawCuGs0HQiBFiAlbcvxEJ5AU0XxIBbmuAy\r\nrQGwAAuwAAuwAAuwAAuwAAuwAAuwAAuwAAuwAAuwAAuwAAuwCusPuadP3xb6AVAo/197+vnJt9fn\r\ndP1dmNYAWIAFWIAFWIAFWIAFWIAFWIAFWIAFWIAFWIAFWIAFWIAFWMWRUjit++c8nnybZlGsB2Sh\r\nXzoZsAALsAALsAALsAALsAALsAALsAALsAALsAALsAALsAALsACrsKCZrtPYxQpQvk0XKBSI8+1x\r\nFlVRAliABViABViABViABViABViABViABViABViABViABViABVgFCFahvLD2NLjFOr2AN4D8XDRe\r\nTFACFmABFmABFmABFmABFmABFmABFmABFmABFmABFmABFmABVjGBVSiLVAsd3EI5nT9tBwPTa/I+\r\ngAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgFVMYO3pP2ShT48o1gOy0J+HQv+75Nvr\r\nE7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAq7D2k2+g59sbSaE8n/kG03S9YbP4\r\nGbAAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7DInnjBTdcLOt8O+GJd9Fusl4QGLMAC\r\nLMACLMACLMACLMACLMACLMACLMACLMACLMACLMACLMCa3hdooRzYhQJZvj2fAPHBbl9M0x0AC7AA\r\nC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAC7AAq5jAyrfTz/n2Aso3CPINlHxbvF0ogBbi8QtY\r\ngAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYgAVYpQBWoZ+m5bT39EJc6NNBSm0xPGABFmDR\r\nAAuwAAuwAAuwAAuwAAuwAAuwAIsGWIAFWIAFWIBVWGCV2o1I8+3vUihvVIXy/8WNVAELsAALsAAL\r\nsAALsAALsAALsAALsAALsAALsAALsAALsAALsIoJrHyb1jBdUBYKuKU8fQGwAAuwAAuwAAuwAAuw\r\nAAuwAAuwAAuwAAuwAAuwAAuwAAuwShksHs/0XqK3WOFgMfP0vnECFo8HsAALsAACsAALsAALsAAL\r\nsAALsHg8gAVYgAUQgAVYgAVYxfGHzzdQ8g3WYr2RKq9nwAIswAIswAIswAIswAIswAIswAIswAIs\r\nwAIswAIswAIswAIsQggBLEIIASxCCGARQghgEUIAixBCAIsQQgCLEAJYhBACWIQQAliEEMAihBDA\r\nIoQQwCKEABYhhAAWIYQAFiEEsAghBLAIIQSwCCGARQghgEUIIYBFCAEsQggBLEIIASxCCGARQsje\r\nyv8DMt78JGzaME4AAAAASUVORK5C'>"--%>
                    <%--// var aa="<img src='"+data+"'/>";--%>
                    <%--//--%>
                    <%--// $("#userImg").html(aa);--%>
                    <%--// console.log(data)--%>
                <%--});--%>
        //    });

        }

        /**
         * 打印局部div
         * @param printpage 局部div的ID
         */
        function printdiv(printpage) {
            $("#userImg").printThis({
                debug: false,
                importCSS: false,
                importStyle: false,
                printContainer: true,
                pageTitle: "二维码",
                removeInline: false,
                printDelay: 333,
                header: null,
                formValues: false
            });
        }
	</script>
</head>
<body id="body">
<div id="importBox" class="hide">
	<form id="importForm" action="${ctx}/sys/user/import" method="post" enctype="multipart/form-data"
		  class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
		<a href="${ctx}/sys/user/import/template">下载模板</a>
	</form>
</div>
<ul class="nav nav-tabs">
	<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex'}">
		<li class="active"><a href="${ctx}/sys/user/list?company.type=8&company.customerTypeTen=10&company.customerTypeEleven=11&office.id=${user.office.id}&office.name=${user.office.name}&conn=${user.conn}">用户列表</a></li>
		<shiro:hasPermission name="sys:user:edit"><li><a href="${ctx}/sys/user/form?office.id=${user.office.id}&office.name=${user.office.name}&conn=${user.conn}">用户添加</a></li></shiro:hasPermission>
	</c:if>
	<c:if test="${empty user.conn}">
		<li class="active"><a href="${ctx}/sys/user/list">用户列表</a></li>
		<shiro:hasPermission name="sys:user:edit"><li><a href="${ctx}/sys/user/form?office.id=${user.office.id}&office.name=${user.office.name}">用户添加</a></li></shiro:hasPermission>
	</c:if>
</ul>
<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/list" method="post" class="breadcrumb form-search ">
	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
	<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	<ul class="ul-form">
		<c:if test="${fns:getUser().isAdmin()}">
		<li><label>归属公司：</label>
			<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex'}">
				<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
						title="公司" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&source=officeConnIndex" cssClass="input-small" allowClear="true"/>
			</c:if>
			<c:if test="${empty user.conn}">
				<sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}"
						title="公司" url="/sys/office/treeData" cssClass="input-small" allowClear="true"/>
			</c:if>
		</c:if>
			<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex'}">
				<input type="hidden" name="company.type" value="8">
				<input type="hidden" name="company.customerTypeTen" value="10">
				<input type="hidden" name="company.customerTypeEleven" value="11">
				<input type="hidden" name="conn" value="${user.conn}"></li>
			</c:if>
			<c:if test="${empty user.conn}">
				<input type="hidden" name="company.type" value="">
			</c:if>
		<li><label>登录名：</label><form:input path="loginName" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li class="clearfix"></li>
		<c:if test="${fns:getUser().isAdmin()}">
			<li><label>归属部门：</label>
			<c:if test="${not empty user.conn && user.conn eq 'connIndex' || user.conn eq 'stoIndex'}">
				<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
						title="部门" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&source=officeConnIndex" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</c:if>
			<c:if test="${empty user.conn}">
				<sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}"
								title="部门" url="/sys/office/treeData" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</c:if>
			</li>
		</c:if>
		<li><label>姓&nbsp;&nbsp;&nbsp;名：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<li><label>手&nbsp;&nbsp;&nbsp;机：</label><form:input path="mobile" htmlEscape="false" maxlength="50" class="input-medium"/></li>
		<c:if test="${not empty user.conn && user.conn eq 'connIndex'}">
			<li><label>日期：</label>
				<input name="ordrHeaderStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${ordrHeaderStartTime}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
				至
				<input name="orderHeaderEedTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					   value="<fmt:formatDate value="${orderHeaderEedTime}" pattern="yyyy-MM-dd"/>"
					   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
			</li>
		</c:if>
		<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
			<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			<input id="btnImport" class="btn btn-primary" type="button" value="导入"/></li>
		<li class="clearfix"></li>
	</ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
	<thead>
		<tr>
			<th>归属公司</th>
			<th>归属部门</th>
			<th class="sort-column login_name">登录名</th>
			<th class="sort-column name">姓名</th>
			<th>手机</th>
			<c:if test="${empty user.conn}">
				<th>状态</th>
			</c:if>
			<c:if test="${not empty user.conn && user.conn eq 'connIndex'}">
				<th>洽谈数</th>
				<th>新增订单量</th>
				<th>新增回款额</th>
				<th>新增会员</th>
			</c:if>
		<shiro:hasPermission name="sys:user:edit"><th>操作</th></shiro:hasPermission></tr></thead>
	<tbody>
	<c:forEach items="${page.list}" var="bizUser">
		<c:if test="${empty user.conn}">
			<c:if test="${bizUser.loginFlag == 0}">
				<tr style="color: rgba(158,158,158,0.45)">
			</c:if>
			<c:if test="${bizUser.loginFlag == 1}">
				<tr>
			</c:if>
			<td>${bizUser.company.name}</td>
			<td>${bizUser.office.name}</td>
			<td>
				<c:if test="${bizUser.delFlag==1}">
				<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">
						${bizUser.loginName}</a>
				</c:if>
				<c:if test="${bizUser.delFlag==0}">${bizUser.loginName}</c:if>
			</td>
			<td>${bizUser.name}</td>
			<td>${bizUser.mobile}</td>
			<td>${bizUser.delFlag == 1 ? '正常' : '删除'}</td>
			<shiro:hasPermission name="sys:user:edit"><td>
				<c:if test="${bizUser.delFlag==1}">
					<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">修改</a>
					<a href="${ctx}/sys/user/delete?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
					<%--<a  data-toggle="modal" data-target="#exampleModal" onclick="genUserCode(${bizUser.id})">生成二维码</a>--%>
					<a data-toggle="modal" onclick="genUserCode(${bizUser.id})" data-id="${requestHeader.id}" data-target="#myModal">生成二维码</a>
				</c:if>
				<c:if test="${bizUser.delFlag==0}">
					<a href="${ctx}/sys/user/recovery?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">恢复</a>
				</c:if>
			</td></shiro:hasPermission>
		</tr>
	</c:if>
		<c:if test="${not empty user.conn && user.conn eq 'connIndex'}">
		<c:if test="${bizUser.delFlag==1}">
			<c:if test="${bizUser.loginFlag == 0}">
				<tr style="color: rgba(158,158,158,0.45)">
			</c:if>
			<c:if test="${bizUser.loginFlag == 1}">
				<tr>
			</c:if>
				<td>${bizUser.company.name}</td>
				<td>${bizUser.office.name}</td>
				<td>
					<c:if test="${user.conn != null}">
						<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">${bizUser.loginName}</a>
					</c:if>
					<c:if test="${user.conn == null}">
						<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">${bizUser.loginName}</a>
					</c:if>
				</td>
				<td>${bizUser.name}</td>
				<td>${bizUser.mobile}</td>
				<c:if test="${not empty user.conn && user.conn eq 'connIndex'}">
					<td>
						<c:if test="${bizUser.userOrder.officeChatRecord !=0}">
							<a href="${ctx}/biz/chat/bizChatRecord/list?user.id=${bizUser.id}&office.parent.id=7&office.type=6&source=purchaser">
								${bizUser.userOrder.officeChatRecord}
							</a>
						</c:if>
						<c:if test="${bizUser.userOrder.officeChatRecord ==0}">
							${bizUser.userOrder.officeChatRecord}
						</c:if>
					</td>
					<td>${bizUser.userOrder.orderCount}</td>
					<td>${bizUser.userOrder.userOfficeReceiveTotal}</td>
					<td>
						${bizUser.userOrder.officeCount}
					</td>
				</c:if>
				<shiro:hasPermission name="sys:user:edit"><td>
					<c:if test="${user.conn != null}">
						<c:if test="${user.conn eq 'connIndex'}">
							<a href="${ctx}/biz/custom/bizCustomCenterConsultant/list?consultants.id=${bizUser.id}&conn=${user.conn}&office.id=${bizUser.office.id}">关联经销店</a>
							<a href="${ctx}/biz/order/bizOrderHeader/list?flag=check_pending&consultantId=${bizUser.id}">订单管理</a>
						</c:if>
						<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">修改</a>
						<a href="${ctx}/sys/user/delete?company.type=8&company.customerTypeTen=10&company.customerTypeEleven=11&id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
					</c:if>
					<c:if test="${user.conn == null && bizUser.delFlag==1 }">
						<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">修改</a>
						<a href="${ctx}/sys/user/delete?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
					</c:if>
					<c:if test="${user.conn == null && bizUser.delFlag==0}">
						<a href="${ctx}/sys/user/recovery?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">恢复</a>
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:if>
		</c:if>
		<c:if test="${not empty user.conn && user.conn eq 'stoIndex'}">
			<c:if test="${bizUser.loginFlag == 0}">
				<tr style="color: rgba(158,158,158,0.45)">
			</c:if>
			<c:if test="${bizUser.loginFlag == 1}">
				<tr>
			</c:if>
			<td>${bizUser.company.name}</td>
			<td>${bizUser.office.name}</td>
			<td>
				<c:if test="${bizUser.delFlag==1}">
					<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">
							${bizUser.loginName}</a>
				</c:if>
				<c:if test="${bizUser.delFlag==0}">${bizUser.loginName}</c:if>
			</td>
			<td>${bizUser.name}</td>
			<td>${bizUser.mobile}</td>
			<td>${bizUser.delFlag == 1 ? '正常' : '删除'}</td>
			<shiro:hasPermission name="sys:user:edit"><td>
				<a href="${ctx}/sys/user/form?id=${bizUser.id}&conn=${user.conn}&company.id=${bizUser.company.id}&office.id=${bizUser.office.id}">修改</a>
				<a href="${ctx}/sys/user/delete?id=${bizUser.id}&company.id=${user.company.id}&conn=${user.conn}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
			</td></shiro:hasPermission>
			</tr>
		</c:if>
	</c:forEach>
	</tbody>
</table>
<div class="pagination">${page}</div>

<!-- 模态框（Modal） -->
<div class="modal fade hide" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">用户二维码</h4>
			</div>
			<div class="modal-body">
				二维码：<div style="margin-top: 14px" id="userImg"></div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<button type="button"  onclick="printdiv('myModal');" class="btn btn-primary">打印</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>

</body>
</html>
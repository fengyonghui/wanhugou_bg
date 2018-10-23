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
                    var aa="<img src='"+data+"'/>";

                     $("#userImg").html(aa);
				}
            });

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
		<li class="active"><a href="${ctx}/sys/user/list?company.type=8,13&company.customerTypeTen=10&company.customerTypeEleven=11&office.id=${user.office.id}&office.name=${user.office.name}&conn=${user.conn}">用户列表</a></li>
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
						title="公司" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&customerTypeThirteen=${OfficeTypeEnum.NETWORK.type}&source=officeConnIndex" cssClass="input-small" allowClear="true"/>
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
				<input type="hidden" name="company.customerTypeThirteen" value="13">
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
						title="部门" url="/sys/office/queryTreeList?type=${OfficeTypeEnum.PURCHASINGCENTER.type}&customerTypeTen=${OfficeTypeEnum.WITHCAPITAL.type}&customerTypeEleven=${OfficeTypeEnum.NETWORKSUPPLY.type}&customerTypeThirteen=${OfficeTypeEnum.NETWORK.type}&source=officeConnIndex" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
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
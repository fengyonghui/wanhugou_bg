<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>客户专员管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }

    </script>

    <script type="text/javascript">
        // 全选按钮控制
        function checkOrCancelAll(){
            var chElt=$('#chElt')
            var checkedElt=chElt.is(':checked');
            var allCheck=$("[name=settlement]");
            var mySpanAllSellected=$('#mySpanAllSellected');
            var mySpanAllCancel=$('#mySpanAllCancel');
            if(checkedElt){
                for(var i=0;i<allCheck.length;i++){
                    allCheck[i].checked=true;
                }
                mySpanAllSellected.hide();
                mySpanAllCancel.show();
            }else{
                for(var i=0;i<allCheck.length;i++){
                    allCheck[i].checked=false;
                }
                mySpanAllSellected.show();
                mySpanAllCancel.hide();
            }
        }

        //批量移除按钮点击方法
        function applyCommission() {
            var executeFlag = true;
            var custIds = ""
            $("[name=settlement]").each(function(){
                if ($(this).is(':checked')) {
                    var custIdTemp = $(this).val();
                    custIds += custIdTemp + ",";
                }
            });

            if(custIds == "") {
                alert("请选择经销店！");
                return
            }
            if (confirm("确定批量移除吗？")) {
                var consultantId = $("#consultantId").val();
                window.location.href = "${ctx}/biz/custom/bizCustomCenterConsultant/deleteBatch?custIds=" + custIds
                    + "&consultants.id=" + consultantId;
            }
        }

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a
            href="${ctx}/biz/custom/bizCustomCenterConsultant/list?consultants.id=${bcUser.consultants.id}">经销店列表</a>
    </li>
    <li>
        <a href="${ctx}/biz/custom/bizCustomCenterConsultant/connOfficeForm?id=${bcUser.consultants.id}&office.id=${bcUser.centers.id}">经销店添加</a>
    </li>
</ul>
<form:form id="searchForm" modelAttribute="bizCustomCenterConsultant" action="${ctx}/biz/custom/bizCustomCenterConsultant/" method="post" class="breadcrumb form-search">
    <ul class="ul-form">
        <li><label>经销店名称：</label>
            <sys:treeselect id="customs" name="customs.id" value="${entity.customs.id}" labelName="customs.name"
                            labelValue="${entity.customs.name}" notAllowSelectParent="true"
                            title="经销店" url="/sys/office/queryTreeList?type=6" cssClass="input-medium required"
                            allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
            <c:if test="${bizCustomCenterConsultant.queryCustomes!=null && bizCustomCenterConsultant.queryCustomes eq 'query_Custome'}">
                <input type="hidden" name="consultants.id" value="${bcUser.consultants.id}">
                <input type="hidden" name="queryCustomes" value="query_Custome">
            </c:if>
            <c:if test="${empty bizCustomCenterConsultant.queryCustomes}">
                <input type="hidden" name="consultants.id" value="${bizCustomCenterConsultant.consultants.id}">
                <input type="hidden" name="queryCustomes" value="query_Custome">
            </c:if>
        </li>
        <li><label>联系电话：</label>
            <form:input path="consultants.mobile" htmlEscape="false" maxlength="11" class="input-medium"/>
        </li>
        <li class="clearfix"></li>
        <li><label>日期：</label>
            <input name="ordrHeaderStartTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${ordrHeaderStartTime}" pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
            至
            <input name="orderHeaderEedTime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${orderHeaderEedTime}" pattern="yyyy-MM-dd"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
        </li>
        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="btns"><input id="btnCancel" class="btn" type="button" value="返 回" onclick="javascript:history.go(-1);"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<div class="form-actions">
    <input class="orderChk" id="chElt"  type="checkbox" onclick="checkOrCancelAll();"  />
    <span id="mySpanAllSellected">全选</span><span id="mySpanAllCancel" style="display: none">取消全选</span>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <input type="button"
           onclick="applyCommission()"
           class="btn btn-primary"
           value="批量移除"/>
</div>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>移除</th>
        <th>采购中心</th>
        <th>客户专员</th>
        <th>电话</th>
        <th>经销店名称</th>
        <th>负责人</th>
        <th>详细地址</th>
        <th>订单采购频次</th>
        <th>累计采购金额</th>
        <th>首次开单时间</th>
        <shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:if test="${resultMap !=null}">
    <c:forEach items="${resultMap}" var="bizCustomCenterConsultant">
        <tr>
            <td>
                <input class="orderChk" type="checkbox" name="settlement" value="${bizCustomCenterConsultant.key.customs.id}" />
            </td>
            <td>
                ${bizCustomCenterConsultant.key.centers.name}
            </td>
            <td>
                ${bizCustomCenterConsultant.key.consultants.name}
            </td>
            <td>
                ${bizCustomCenterConsultant.key.consultants.mobile}
            </td>
            <td>
                ${bizCustomCenterConsultant.key.customs.name}
            </td>
            <td>
                ${bizCustomCenterConsultant.key.customs.primaryPerson.name}
            </td>
            <td>
                <c:if test="${bizCustomCenterConsultant.value.userOfficeReceiveTotal !=null}">
                    ${bizCustomCenterConsultant.value.bizLocation.province.name}${bizCustomCenterConsultant.value.bizLocation.city.name}
                    ${bizCustomCenterConsultant.value.bizLocation.region.name}${bizCustomCenterConsultant.value.bizLocation.address}
                </c:if>
            </td>
            <td>${bizCustomCenterConsultant.value.orderCount}</td>
            <td>${bizCustomCenterConsultant.value.userOfficeReceiveTotal}</td>
            <td>
                <fmt:formatDate value="${bizCustomCenterConsultant.value.userOfficeDeta}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>
            <shiro:hasPermission name="biz:custom:bizCustomCenterConsultant:edit">
                <td>
                    <a href="${ctx}/biz/custom/bizCustomCenterConsultant/delete?customs.id=${bizCustomCenterConsultant.key.customs.id}&consultants.id=${bizCustomCenterConsultant.key.consultants.id}"
                       onclick="return confirmx('确认要移除该关联信息吗？', this.href)">移除</a>
                    <input type="hidden" id="consultantId" value="${bizCustomCenterConsultant.key.consultants.id}">
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </c:if>
    </tbody>
</table>
<%--<div class="pagination">${page}</div>--%>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
	<title>订单信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        $(document).ready(function() {
            $("#inputForm").validate({
                submitHandler: function(form){
                    if (confirm("请再次确认实收金额是否正确？")==true) {
                        var unlinePayMoney = $("#unlinePayMoney").val();
                        $("#realMoney").val(unlinePayMoney);
                        loading('正在提交，请稍等...');
                        form.submit();
                    }
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
            $("#back").click(function () {
                var unlineId = $("#id").val();
                if (confirm("请再次确认是否驳回？")==true) {
                    $.ajax({
                        type:"post",
                        url:"${ctx}/biz/order/bizOrderHeaderUnline/changeOrderReceive",
						data:{id:unlineId},
                        success:function (data) {
                            if (data == 'ok') {
                                window.location.href="${ctx}/biz/order/bizOrderHeaderUnline?id="+unlineId;
                            }
                        }
                    });
                }else {
                    return false;
                }
            });
        });
	</script>
	<script type="text/javascript">
        $(function() {

            //点击图片放大
            $("#img-zoom").click(function(){
                $('#img-modal').modal("hide");
            });
            $("#img-dialog").click(function(){
                $('#img-modal').modal("hide");
            });
            //index-list-content为显示文章内容div的class
            $("#credential img").each(function(i){
                var src = $(this).attr("src");
                $(this).click(function () {
                    $("#img-zoom").attr("src", src);
                    var oImg = $(this);
                    var img = new Image();
                    img.src = $(oImg).attr("src");
                    var realWidth = img.width;//真实的宽度
                    var realHeight = img.height;//真实的高度
                    var ww = $(window).width();//当前浏览器可视宽度
                    var hh = $(window).height();//当前浏览器可视宽度
                    $("#img-content").css({"top":0,"left":0,"height":"auto"});
                    $("#img-zoom").css({"height":"auto"});
                    if((realWidth+20)>ww){
                        $("#img-content").css({"width":"100%"});
                        $("#img-zoom").css({"width":"99%"});
                    }else{
                        $("#img-content").css({"width":realWidth+20, "height":realHeight+20});
                        $("#img-zoom").css({"width":realWidth, "height":realHeight});
                    }
                    if((hh-realHeight-40)>0){
                        $("#img-content").css({"top":(hh-realHeight-40)/2});
                    }
                    if((ww-realWidth-20)>0){
                        $("#img-content").css({"left":(ww-realWidth-20)/2});
                    }
                    //console.log("realWidth:"+realWidth+" realHeight:"+realHeight+" ww:"+ww)
                    $('#img-modal').modal();
                    $("#img-modal").css({"width":realWidth+20});
                });
            });
        });
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/biz/order/bizOrderHeaderUnline/">线下支付流水列表</a></li>
	<li class="active"><a href="${ctx}/biz/order/bizOrderHeaderUnline/form?id=${bizOrderHeaderUnline.id}">线下支付流水列表<shiro:hasPermission
			name="biz:order:bizOrderHeaderUnline:edit">${not empty bizOrderHeaderUnline.id?'修改':'添加'}</shiro:hasPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="bizOrderHeaderUnline" action="${ctx}/biz/order/bizOrderHeaderUnline/save" method="post"
		   class="form-horizontal">
	<form:hidden path="id"/>
	<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">订单号：</label>
			<div class="controls">
					<form:input disabled="true" path="orderHeader.orderNum"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">流水号：</label>
			<div class="controls">
				<form:input disabled="true" path="serialNum"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">单据凭证：</label>
			<div id="credential" class="controls">
				<c:forEach items="${imgUrlList}" var="imgUrl">
					<img src="${imgUrl}" style="max-width:100px;max-height:100px;_height:100px;border:0;padding:3px;"/>
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">线下付款金额：</label>
			<div class="controls">
                <form:input id="unlinePayMoney" readonly="true" class="input-mini" path="unlinePayMoney"/>
			</div>
		</div>
		<%--<div class="control-group">--%>
			<%--<label class="control-label">实收金额：</label>--%>
			<%--<div class="controls">--%>
				<form:hidden id="realMoney" class="input-mini" path="realMoney"/>
				<%--<button id="confirm" disabled="disabled" type="button" class="btn btn-primary">确认</button>--%>
			<%--</div>--%>
		<%--</div>--%>
		<div class="control-group">
			<label class="control-label">流水状态：</label>
			<div class="controls">
				<span style="font-size: large; font-style: initial; color: red; width: available; font-family: 楷体">${fns:getDictLabel(bizOrderHeaderUnline.bizStatus, 'biz_order_unline_bizStatus', '未知状态')}</span>
				<%--<input id="bizStatus" type="button" class="btn btn-primary" value="${fns:getDictLabel(bizOrderHeaderUnline.bizStatus, 'biz_order_unline_bizStatus', '未知状态')}"/>--%>
			</div>
		</div>
		<div class="form-actions">
				<shiro:hasPermission name="biz:order:bizOrderHeaderUnline:edit">
                    <c:if test="${bizOrderHeaderUnline.source == 'examine'}">
                        <input id="confirm" class="btn btn-primary" type="submit" value="确认"/>&nbsp;
                        <input id="back" class="btn btn-primary" type="button" value="驳回"/>
                    </c:if>
				</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1);"/>
		</div>
</form:form>
<div id="img-modal" class="modal fade">
    <div id="img-dialog" class="modal-dialog" style="width: 98%; height: 98%;text-align: center;">
        <div id="img-content" class="modal-content">
            <img id="img-zoom" src="" style="max-height: 100%; max-width: 100%;margin:10px;">
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
</body>
</html>

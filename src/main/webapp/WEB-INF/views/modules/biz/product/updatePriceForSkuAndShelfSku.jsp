<%@ taglib prefix="s" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ page import="com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>备货清单管理</title>
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

        function changePrice() {
            var prodId = $("#prodId").val();
            var itemNo = $("#itemNo").val();
			var size = $("#size").val();
			var settlementPrice = $("#settlementPrice").val();
			var marketingPrice = $("#marketingPrice").val();
			$.ajax({
				type:"post",
				url:"${ctx}/biz/product/bizProductInfoV3/changePrice",
				data:{"prodId":prodId,"itemNo":itemNo,"size":size,"settlementPrice":settlementPrice,"marketingPrice":marketingPrice},
				success:function (data) {
					alert(data);
                }
			});
        }

        function changeSpu() {
            var prodId = $("#prodId").val();
            $.ajax({
                type:"post",
                url:"${ctx}/biz/product/bizProductInfoV3/changeSpu",
                data:{"prodId":prodId},
                success:function (data) {
                    alert(data);
                }
            });
        }
	</script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active">
		<a href="">修改价格</a>
	</li>
</ul><br/>
<form:form id="inputForm" action="${ctx}/biz/product/bizProductInfoV3/mergeSpu" method="post" class="form-horizontal">

	<div class="control-group">
		<label class="control-label">货号：</label>
		<div class="controls">
			<input id="itemNo" name="itemNo" type="text" class="input-xlarge" value=""/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">尺寸：</label>
		<div class="controls">
			<input id="size" name="size" type="text" class="input-xlarge" value=""/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">结算价：</label>
		<div class="controls">
			<input id="settlementPrice" name="settlementPrice" type="text" class="input-xlarge" value=""/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">销售价：</label>
		<div class="controls">
			<input id="marketingPrice" name="marketingPrice" type="text" class="input-xlarge" value=""/>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">产品ID：</label>
		<div class="controls">
			<input id="prodId" name="prodId" type="text" class="input-xlarge" value=""/>
		</div>
	</div>
	<div>
		<label></label>
		<div>
			<input id="btnChangePrice" class="btn btn-primary" type="button" value="修改" onclick="changePrice()"/>
			<input id="btnChangeSpu" class="btn btn-primary" type="button" value="整合" onclick="changeSpu()"/>
		</div>
	</div>
</form:form>
</body>
</html>
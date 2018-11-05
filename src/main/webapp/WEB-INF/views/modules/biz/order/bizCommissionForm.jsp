<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="biz" tagdir="/WEB-INF/tags/biz" %>
<html>
<head>
	<title>佣金付款表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1-min.js"></script>
	<script src="${ctxStatic}/bootstrap/multiselect.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/tree-multiselect/dist/jquery.tree-multiselect.js"></script>
	<script src="${ctxStatic}/jquery-select2/3.5.3/select2.js" type="text/javascript"></script>
	<script src="${ctxStatic}/jquery-validation/1.9/jquery.validate.js" type="text/javascript"></script>
	<script src="${ctxStatic}/jquery-plugin/ajaxfileupload.js" type="text/javascript"></script>
	<script src="${ctxStatic}/jquery-plugin/jquery.searchableSelect.js" type="text/javascript"></script>
	<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/common/base.js" type="text/javascript"></script>
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
        function pay() {
            var commId = $("#id").val();

            var mainImg = $("#payImgDiv").find("[customInput = 'payImgImg']");
            var img = "";
            if(mainImg.length >= 2) {
                for (var i = 1; i < mainImg.length; i ++) {
                    img += $(mainImg[i]).attr("src") + ",";
                }
            }

            if ($String.isNullOrBlank(img)) {
                alert("错误提示:请上传支付凭证");
                return false;
            }

            var remark = $("#remark").text();

            $.ajax({
                url: '${ctx}/biz/order/bizCommission/payOrder',
                contentType: 'application/json',
                data: {"commId": commId, "img": img, "remark":remark},
                type: 'get',
                success: function (result) {
                    alert(result);
                    if (result == '操作成功!') {
                        window.location.href = "${ctx}/biz/order/bizCommission/";
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }
	</script>
	<script type="text/javascript">
        function submitPic(id, multiple) {
            var f = $("#" + id).val();
            if (f == null || f == "") {
                alert("错误提示:上传文件不能为空,请重新选择文件");
                return false;
            } else {
                var extname = f.substring(f.lastIndexOf(".") + 1, f.length);
                extname = extname.toLowerCase();//处理了大小写
                if (extname != "jpeg" && extname != "jpg" && extname != "gif" && extname != "png") {
                    $("#picTip").html("<span style='color:Red'>错误提示:格式不正确,支持的图片格式为：JPEG、GIF、PNG！</span>");
                    return false;
                }
            }
            var file = document.getElementById(id).files;
            var size = file[0].size;
            if (size > 2097152) {
                alert("错误提示:所选择的图片太大，图片大小最多支持2M!");
                return false;
            }
            if("payImg" == id) {
                ajaxFileUploadPicForPoHeader(id, multiple)
            } else if ("prodMainImg" == id) {
                ajaxFileUploadPicForRefund(id, multiple);
            }
        }

        function ajaxFileUploadPicForPoHeader(id, multiple) {
            $.ajaxFileUpload({
                url : '${ctx}/biz/product/bizProductInfoV2/saveColorImg', //用于文件上传的服务器端请求地址
                secureuri : false, //一般设置为false
                fileElementId : id, //文件上传空间的id属性  <input type="file" id="file" name="file" />
                type : 'POST',
                dataType : 'text', //返回值类型 一般设置为json
                success : function(data, status) {
                    //服务器成功响应处理函数
                    var msg = data.substring(data.indexOf("{"), data.indexOf("}")+1);
                    var msgJSON = JSON.parse(msg);
                    var imgList = msgJSON.imgList;
                    var imgDiv = $("#" + id + "Div");
                    var imgDivHtml = "<img src=\"$Src\" customInput=\""+ id +"Img\" style='width: 100px' onclick=\"$(this).remove();\">";
                    if (imgList && imgList.length > 0 && multiple) {
                        for (var i = 0; i < imgList.length; i ++) {
                            imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                        }
                    }else if (imgList && imgList.length > 0 && !multiple) {
                        imgDiv.empty();
                        for (var i = 0; i < imgList.length; i ++) {
                            imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                        }
                    }else {
                        var img = $("#" + id + "Img");
                        img.attr("src", msgJSON.fullName);
                    }
                },
                error : function(data, status, e) {
                    //服务器响应失败处理函数
                    console.info(data);
                    console.info(status);
                    console.info(e);
                    alert("上传失败");
                }
            });
            return false;
        }

        var a = 0;
        function ajaxFileUploadPicForRefund(id, multiple) {
            $.ajaxFileUpload({
                url: '${ctx}/biz/order/bizOrderHeader/saveColorImg', //用于文件上传的服务器端请求地址
                secureuri: false, //一般设置为false
                fileElementId: id, //文件上传空间的id属性  <input type="file" id="file" name="file" />
                type: 'POST',
                dataType: 'text', //返回值类型 一般设置为json
                success: function (data, status) {
                    //服务器成功响应处理函数
                    var msg = data.substring(data.indexOf("{"), data.indexOf("}") + 1);
                    var msgJSON = JSON.parse(msg);
                    var imgList = msgJSON.imgList;
                    var imgDiv = $("#" + id + "Div");
                    var imgDivHtml = "<td><img src=\"$Src\" customInput=\"" + id + "Img\" style='width: 100px' onclick=\"removeThis(this);\"></td>";
                    var imgPhotosSorts = "<td id=''><input name='imgPhotosSorts' style='width: 70px' type='number'/></td>";
                    if (imgList && imgList.length > 0 && multiple) {
                        for (var i = 0; i < imgList.length; i++) {
                            // imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                            if (id == "prodMainImg") {
                                $("#imgPhotosSorts").append("<td><input id='" + "main" + i + "' name='imgPhotosSorts' value='" + a + "' style='width: 70px' type='number'/></td>");
                                // $("#prodMainImgImg").append(imgDivHtml.replace("$Src", imgList[i]));
                                $("#prodMainImgImg").append("<td><img src=\"" + imgList[i] + "\" customInput=\"" + id + "Img\" style='width: 100px' onclick=\"removeThis(this," + "$('#main" + i + "'));\"></td>");
                                a += 1;
                            }
                        }
                    } else if (imgList && imgList.length > 0 && !multiple) {
                        imgDiv.empty();
                        for (var i = 0; i < imgList.length; i++) {
                            imgDiv.append(imgDivHtml.replace("$Src", imgList[i]));
                        }
                    } else {
                        var img = $("#" + id + "Img");
                        img.attr("src", msgJSON.fullName);
                    }
                },
                error: function (data, status, e) {
                    //服务器响应失败处理函数
                    console.info(data);
                    console.info(status);
                    console.info(e);
                    alert("上传失败");
                }
            });
            return false;
        }

        function deletePic(id) {
            var f = $("#" + id);
            f.attr("src", "");
        }

        function removeThis(obj, item) {
            $(obj).remove();
            $(item).remove();
        }

        function deleteParentParentEle(that) {
            $(that).parent().parent().remove();
        }

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/biz/order/bizCommission/">佣金付款表列表</a></li>
		<li class="active"><a href="${ctx}/biz/order/bizCommission/save?id=${bizCommission.id}">佣金付款表<shiro:hasPermission name="biz:order:bizCommission:edit">${not empty bizCommission.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:order:bizCommission:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="bizCommission" action="${ctx}/biz/order/bizCommission/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input id="vendId" type="hidden" value="${entity.sellerId}"/>
		<sys:message content="${message}"/>

		<c:if test="${entity.str != 'pay'}">
			<div class="control-group">
				<label class="control-label">总的待付款金额：</label>
				<div class="controls">
					<form:input path="totalCommission" readonly="true" htmlEscape="false" class="input-xlarge"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">付款金额：</label>
				<div class="controls">
					<form:input path="payTotal" htmlEscape="false" value="${bizCommission.totalCommission}" readonly="true" class="input-xlarge"/>
					&nbsp;&nbsp;<span style="color: red">※:付款金额为总的待付金额，禁止禁止修改</span>
				</div>
			</div>
		</c:if>

		<c:if test="${entity.str == 'pay'}">
			<div id="cardNumber" class="control-group" >
				<label class="control-label">代销商卡号：</label>
				<div class="controls">
					<input id="cardNumberInput" readonly="readonly" value="${entity.customerInfo.cardNumber}" htmlEscape="false" maxlength="30"
						   class="input-xlarge "/>
				</div>
			</div>
			<div id="payee" class="control-group" >
				<label class="control-label">代销商收款人：</label>
				<div class="controls">
					<input id="payeeInput" readonly="readonly" value="${entity.customerInfo.payee}" htmlEscape="false" maxlength="30"
						   class="input-xlarge "/>
				</div>
			</div>
			<div id="bankName" class="control-group" >
				<label class="control-label">代销商开户行：</label>
				<div class="controls">
					<input id="bankNameInput" readonly="readonly" value="${entity.customerInfo.bankName}" htmlEscape="false" maxlength="30"
						   class="input-xlarge "/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">申请金额：</label>
				<div class="controls">
					<form:input path="payTotal" htmlEscape="false" value="${entity.totalCommission}" readonly="true" class="input-xlarge"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">上传付款凭证：
					<p style="opacity: 0.5;">点击图片删除</p>
				</label>

				<div class="controls">
					<input class="btn" type="file" name="productImg" onchange="submitPic('payImg', true)" value="上传图片" multiple="multiple" id="payImg"/>
				</div>
				<div id="payImgDiv">
					<img src="${entity.imgUrl}" customInput="payImgImg" style='width: 100px' onclick="$(this).remove();">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">最后付款时间：</label>
				<div class="controls">
					<input name="payDeadline" id="payDeadline" type="text" readonly="readonly" maxlength="20"
						   class="input-medium Wdate required"
						   value="<fmt:formatDate value="${entity.deadline}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
							<c:if test="${entity.str == 'createPay'}"> onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"</c:if>
						   placeholder="必填！"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" style="color: red">支付金额：</label>
				<div class="controls">
					<form:input path="payTotal" htmlEscape="false" value="${entity.totalCommission}" readonly="true" class="input-xlarge"/>
					&nbsp;&nbsp;<span style="color: red">※:需一次付清，禁止修改</span>
				</div>
			</div>
		</c:if>



		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remark" maxlength="200" class="input-xlarge "/>
			</div>
		</div>

		<div class="form-actions">
			<%--<shiro:hasPermission name="biz:order:bizCommission:edit">--%>
				<c:if test="${entity.str != 'pay'}">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				</c:if>
			<%--</shiro:hasPermission>--%>
			<c:if test="${entity.str == 'pay'}">
				<input id="btnSubmit" type="button" onclick="pay()" class="btn btn-primary" value="确认支付"/>
			</c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
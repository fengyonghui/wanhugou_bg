<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>物流单列表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			if($("#ship").val()!=null && $("#ship").val()!="" && $("#ship").val()==1){
				$("#invoiceExport").click(function(){
					top.$.jBox.confirm("确认要导出备货单发货信息数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/biz/inventory/bizInvoice/exportList?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}");
							$("#searchForm").submit();
							$("#searchForm").attr("action","${ctx}/biz/inventory/bizInvoice/");
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
			}else{
				$("#invoiceExport").click(function(){
					top.$.jBox.confirm("确认要导出订单发货信息数据吗？","系统提示",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/biz/inventory/bizInvoice/exportList?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}&targetPage=${bizInvoice.targetPage}");
							$("#searchForm").submit();
							$("#searchForm").attr("action","${ctx}/biz/inventory/bizInvoice/");
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});
			}
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
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
            $("#contentTable img").each(function(i){
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
		<li class="active"><a href="${ctx}/biz/inventory/bizInvoice?ship=${bizInvoice.ship}&bizStatus=${bizInvoice.bizStatus}&targetPage=${bizInvoice.targetPage}">物流单列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="bizInvoice" action="${ctx}/biz/inventory/bizInvoice/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="ship" name="ship" type="hidden" value="${bizInvoice.ship}"/>
		<input id="bizStatus" name="bizStatus" type="hidden" value="${bizInvoice.bizStatus}"/>
		<input id="targetPage" name="targetPage" type="hidden" value="${bizInvoice.targetPage}"/>
		<ul class="ul-form">
			<li><label>发货单号：</label>
				<form:input path="sendNumber" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<li><label>物流单号：</label>
				<form:input path="trackingNumber" htmlEscape="false" maxlength="30" class="input-medium"/>
			</li>
			<c:if test="${bizInvoice.ship==0}">
				<li><label>订单号：</label>
					<form:input path="orderNum" htmlEscape="false" maxlength="30" class="input-medium"/>
				</li>
			</c:if>
			<c:if test="${bizInvoice.ship==1}">
				<li><label>备货单号：</label>
					<form:input path="reqNo" htmlEscape="false" maxlength="30" class="input-medium"/>
				</li>
			</c:if>
			<li><label>物流商：</label>
				<form:input path="logistics.name" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>发货人：</label>
				<form:input path="carrier" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"><input id="invoiceExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>序号</td>
				<th>物流单号</th>
				<%--<th>发货号</th>--%>
				<%--<th>物流商</th>--%>
				<th>运费</th>
				<th>操作费</th>
				<th>货值</th>
				<th>运费/货值</th>
				<%--<th>发货人</th>--%>
				<%--<th>物流结算方式</th>--%>
				<%--<th>备注</th>--%>
				<%--<th>发货时间</th>--%>
				<th>物流信息图</th>
				<th>操作</th>
				<%--<shiro:hasPermission name="biz:inventory:bizInvoice:edit"><th>操作</th></shiro:hasPermission>--%>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizInvoice" varStatus="state">
			<tr>
				<td>${state.index+1}</td>
				<td><a href="${ctx}/biz/inventory/bizInvoice/logisticsOrderDetail?id=${bizInvoice.id}&source=xq&targetPage=logistics">${bizInvoice.trackingNumber}</a></td>
				<%--<td><a href="${ctx}/biz/inventory/bizInvoice/invoiceOrderDetail?id=${bizInvoice.id}&source=xq">${bizInvoice.sendNumber}</a></td>--%>
				<%--<td>${bizInvoice.logistics.name}</td>--%>
				<td>${bizInvoice.logisticsFreight}</td>
				<td>${bizInvoice.logisticsOperation}</td>
				<td>${bizInvoice.logisticsValuePrice}</td>
				<td>
					<c:if test="${bizInvoice.valuePrice != 0}">
					<fmt:formatNumber type="number" value="${bizInvoice.logisticsFreight*100/bizInvoice.logisticsValuePrice}" maxFractionDigits="0"/>%
					</c:if>
				</td>
				<%--<td>${bizInvoice.carrier}</td>--%>
				<%--<td>${fns:getDictLabel(bizInvoice.settlementStatus, 'biz_settlement_status', '未知状态')}</td>--%>
				<%--<td>${bizInvoice.remarks}</td>--%>
				<%--<td>
					<fmt:formatDate value="${bizInvoice.sendDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>--%>
				<td><img src="${bizInvoice.imgUrl}"style="max-width:150px;max-height:150px;_height:150px;border:0;padding:3px;"/></td>
				<td>
					<a href="${ctx}/biz/inventory/bizInvoice/logisticsOrderDetail?id=${bizInvoice.id}&source=xq&targetPage=logistics">物流单详情</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	<div id="img-modal" class="modal fade">
		<div id="img-dialog" class="modal-dialog" style="width: 98%; height: 98%;text-align: center;">
			<div id="img-content" class="modal-content">
				<img id="img-zoom" src="" style="max-height: 100%; max-width: 100%;margin:10px;">
			</div><!-- /.modal-content -->
		</div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ page import="com.wanhutong.backend.modules.enums.BizOpShelfInfoEnum" %>
<%--当前系统时间--%>
<jsp:useBean id="time" class="java.util.Date"/>
<html>
<head>
	<title>商品上架管理</title>
	<script type="text/javascript" src="${ctxStatic}/tablesMergeCell/tablesMergeCell.js"></script>
    <script type="application/javascript" src="${ctxStatic}/common/base.js?v=20181210"></script>
	<script type="text/javascript">
        var opShelfType="";
        $(document).ready(function() {
            var opShelfInfoType=$("#opShelfInfoType").val();
            if (opShelfInfoType == 5) {
                $("#tbody").append("<c:set var='retail' value='1'/>");
                $("#tbody").append("<input id='retail' type='hidden' value='1'/>");
			}
            needPutawayForMyPanel();
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function(form){
                    var shelfSkuId = $("#bizOpShelfSkuId").val();
                    var shelfInfoIds = "";
                    $("input:checkbox[name='shelfs']:checked").each(function (i) {
                        shelfInfoIds += this.value+",";
                    });
                    // alert(shelfInfoIds);
                    var centId = $("#centerOfficeId").val();
                    var flag = true;
                    var vflag = false;
                    var checkFlag = false;
                    var numFlag = true;
                    var checkMassege = "";
                    var skuInfoIds = "";
                    var minQtys = "";
                    var maxQtys = "";
                    $("#tbody").find("td").each(function () {
                        if ($(this).attr("style") == "display: none;") {
                            var skuTitle = $(this).find("input").attr("about");
                            var skuTitles = $("input[about='" + skuTitle + "']");
                            skuTitles.each(function (i) {
                                if ($(this).val() == '') {
                                    $(this).val($(skuTitles[0]).val());
                                }
                            });
                        }
                        $(this).find("input[name!='unshelfTimes']").each(function () {
                            if ($(this).val() == '') {
                                $(this).next().show();
                                $(this).next().text("必填信息");
                                flag = false;
                                return;
                            }
                        });
                    }) ;
                    var hasUnderPriceRole = $("#hasUnderPriceRole").val();
                    $("#tbody").find("tr").each(function (i) {
                        var minQty = $(this).find("td").find("input[name='minQtys']").val();
                        var maxQty = $(this).find("td").find("input[name='maxQtys']").val();
                        var nextMinQty = $(this).next().find("td").find("input[name='minQtys']").val();
                        var thisClass = $(this).attr("class");
                        var nextClass = $(this).next().attr("class");
                        if (parseInt(minQty) >= parseInt(maxQty)) {
                            alert("最高销售数量必须大于最低销售数量");
                            numFlag = false;
                            return;
                        }

                        if (parseInt(thisClass) == parseInt(nextClass)) {
                            if (parseInt(nextMinQty) <= parseInt(maxQty)) {
                                alert("销售数量重复");
                                numFlag = false;
                                return false;
                            }
                        }
                        var orgPrice = $(this).find("td").find("input[name='orgPrices']").val();
                        var salePrice = $(this).find("td").find("input[name='salePrices']").val();
                        if (Number(orgPrice) > Number(salePrice) && hasUnderPriceRole == 'false') {
                            alert("售价不能低于结算价");
                            numFlag = false;
                            return false;
                        }
                    });
                    $("#tbody").find("td").each(function () {
                        var skuId = $(this).find("input[name='skuInfoIds']").val();
                        var minQty = $(this).find("input[name='minQtys']").val();
                        var maxQty = $(this).find("input[name='maxQtys']").val();
                        if (skuId != undefined){
                            skuInfoIds += skuId+",";
                        }
                        if (minQty != undefined){
                            minQtys += minQty+",";
						}
                        if (maxQty != undefined){
                            maxQtys += maxQty+",";
                        }
                    });
                    if(numFlag){
                        $.ajax({
                            url:"${ctx}/biz/shelf/bizOpShelfSkuV2/checkNum",
                            type:"post",
                            cache:false,
                            data:{skuInfoIds:skuInfoIds,minQtys:minQtys,maxQtys:maxQtys,shelfSkuId:shelfSkuId,shelfInfoIds:shelfInfoIds,centId:centId},
                            success:function(data){
                                if (data=="false"){
                                    checkFlag = true;
                                    checkMassege = "您添加的商品在该阶梯价已经存在，请查询后再添加";
                                }
                                if (data=="true"){
                                    vflag = true;
                                }
                                if(checkFlag) {
                                    alert(checkMassege);
                                }
                                if(flag && vflag && numFlag) {
                                    $("#tbody").find("td").each(function () {
                                        if ($(this).attr("style") == "display: none;") {
                                            $(this).removeAttr("style");
                                        }
                                    });
                                    $("#shelfInfoId").removeAttr("disabled");
                                    loading('正在提交，请稍等...');
                                    form.submit();
                                }
                            }
                        });
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
            <%--属于选择货架名称--%>
            <%--var opShelfId=$("#opShelfId").val();--%>
            <%--$.ajax({--%>
                <%--type:"post",--%>
                <%--url:"${ctx}/biz/shelf/bizOpShelfInfo/findShelf",--%>
                <%--success:function (data) {--%>
                    <%--$.each(data,function(index,shelfInfo) {--%>
                        <%--if(opShelfId==shelfInfo.id){--%>
                            <%--if(shelfInfo.type==3){--%>
                                <%--$("#PurchaseID").css("display","block");--%>
                            <%--}else{--%>
                                <%--$("#PurchaseID").css("display","none");--%>
                            <%--}--%>
                            <%--$("#s2id_shelfInfoId").find("span").eq(0).text(shelfInfo.name);--%>
                            <%--$("#shelfInfoId").append("<option selected='selected' value='"+shelfInfo.id+"'>"+shelfInfo.name+"</option>")--%>
                        <%--}else {--%>
                            <%--$("#shelfInfoId").append("<option value='"+shelfInfo.id+"'>"+shelfInfo.name+"</option>")--%>
                        <%--}--%>
                    <%--})--%>
                <%--}--%>
            <%--});--%>
            $('#select_all').live('click',function(){
                var choose=$("input[title='shelfIds']");
                if($(this).attr('checked')){
                    choose.attr('checked',true);
                }else{
                    choose.attr('checked',false);
                }
            });

            var id=$("#id").val();

            $("#searchData").click(function () {
                var prodBrandName=$("#prodBrandName").val();
                $("#prodBrandNameCopy").val(prodBrandName);
                var skuName=$("#skuName").val();
                $("#skuNameCopy").val(skuName);
                var skuCode =$("#skuCode").val();
                $("#skuCodeCopy").val(skuCode);
                var itemNo =$("#itemNo").val();
                $("#itemNoCopy").val(itemNo);
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuListV2",
                    data:$('#searchForm').serialize(),
                    success:function (result) {
                         $("#prodInfo2").empty();
                        var data = JSON.parse(result).data;
                         if (data == '') {
                             return false;
                         } else {
                             $.each(data.skuMap, function (keys, skuInfoList) {

                                 var prodKeys = keys.split(",");
                                 var prodId = prodKeys[0];
                                 var prodUrl = prodKeys[2];
                                 var brandName = prodKeys[6];
                                 var varietyId = prodKeys[7];
                                 var varietyName = prodKeys[8];

                                 var flag = true;

                                 var tr_tds = "";
                                 var t = 0;

                                 var factorMap = data.serviceFactor;
                                 var factorStr = factorMap[varietyId];

                                 //  var factorArr=$(factorStr).split(",");
                                 var f = "";
                                 if (factorStr != undefined) {
                                     $.each(factorStr, function (i) {
                                         f += factorStr[i] + "<br/>";
                                     });
                                 }

                                 $.each(skuInfoList, function (index, skuInfo) {

                                     tr_tds += "<tr class='" + prodId + "'>";
                                     tr_tds += "<td><input type='checkbox' value='" + skuInfo.id + "' title='shelfIds'/></td>";
                                     tr_tds += "<td>" + skuInfo.name + "</td><td>" + skuInfo.buyPrice + "</td><td>" + skuInfo.partNo + "</td><td>" + skuInfo.itemNo + "</td>" ;
                                     //商品已上货架名称
                                     tr_tds += "<td>" + skuInfo.shelfNames + "</td>";
										 // "<td>" + skuInfo.skuPropertyInfos + "</td>";

                                     if (flag) {
                                         tr_tds += "<td rowspan='" + skuInfoList.length + "'>" + varietyName + "<br/>" + f + "</td>";
                                         tr_tds += "<td rowspan='" + skuInfoList.length + "'>" + brandName + "</td>";
                                         tr_tds += "<td rowspan='" + skuInfoList.length + "'><img style='width: 160px;height: 160px' src='" + prodUrl + "' maxWidth='100' maxHeight='100'></td>"
                                     }

                                     tr_tds += "</tr>";
                                     if (skuInfoList.length > 1) {
                                         flag = false;
                                     }
                                 });

                                 t++;
                                 $("#prodInfo2").append(tr_tds);

                             });
                         }
                    }
                })
            });

            /**
             *
             * 获取当前时间
             */
            function p(s) {
                return s < 10 ? '0' + s: s;
            }


            <%--点击确定时填写商品数量--%>
            $("#ensureData").click(function () {
                var retail = $("#retail").val();
                var skuIds="";
                $("#prodInfo2").find($('input:checkbox:checked')).each(function(i){
                    skuIds+=$(this).val()+",";
                });
                skuIds=skuIds.substring(0,skuIds.length-1);
                var myDate = new Date();
                var year=myDate.getFullYear();
                var month=myDate.getMonth()+1;
                var date=myDate.getDate();
                var h=myDate.getHours();       //获取当前小时数(0-23)
                var m=myDate.getMinutes();     //获取当前分钟数(0-59)
                var s=myDate.getSeconds();
                var now=year+'-'+p(month)+"-"+p(date)+" "+p(h)+':'+p(m)+":"+p(s);
                var commissionRatio = $("#commissionRatio").val();
                $.ajax({
                    type:"POST",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuNameListV2?ids="+skuIds,
                    dataType:"json",
                    success:function(result){
                        var data = result.data;
                        console.info(data);
                        var htmlInfo = "";
                        var pri = 10;
                        $.each(data,function(index,item) {
                            console.info(item)
                            if (item.bvFactorList != undefined && retail != 1) {
                                if (item.bvFactorList == "" || item.bvFactorList == null) {
									alert(item.name + "未设置阶梯价，不能上架！")
                                }
								$.each(item.bvFactorList,function(index,bvFactor){
									htmlInfo+="<tr class='"+item.id+"'><td id='"+item.id+"'><input name='skuInfoIds' type='hidden' readonly='readonly' value='"+item.id+"'/>"+ item.name +"</td>"+
										"<td><input about='shQtys"+item.id+"' name='shelfQtys' value='1000' htmlEscape='false' maxlength='6' class='input-mini required' type='number' placeholder='必填！'/><label style='display: none' class=\"error\"></label></td>"+
										"<td><input about='orgPrices"+item.id+"' name='orgPrices' readonly='readonly' value='"+item.buyPrice+"' htmlEscape='false' maxlength='6' class='input-mini required' type='number' placeholder='必填！' /></td>"+
										"<td><input name=\"salePrices\" value=\""+bvFactor.salePrice+"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type='number' placeholder=\"必填！\"/><label style='display: none' class=\"error\"></label></td>";
                                    	htmlInfo += "<td><input readonly=\"true\" name=\"minQtys\" value=\""+bvFactor.minQty+"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type=\"number\" placeholder=\"必填！\"/><label style='display: none'  class=\"error\"></label></td>"+
										"<td><input readonly=\"true\" name=\"maxQtys\" value=\""+bvFactor.maxQty+"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type=\"number\" placeholder=\"必填！\" onchange='addOne(this,"+item.id+")'/><label style='display: none' class=\"error\"></label></td>"+
										"<td><input about='shelfDate"+item.id+"' name=\"shelfTimes\" value=\""+now+"\" type=\"text\" readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate required\"" +
										"onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});\" placeholder=\"必填！\"/></td>"+

										"<td><input about='unshelfTimes"+item.id+"' name=\"unshelfTimes\" type=\"text\" value='' readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate \"" +
										"onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\" placeholder=\"选填！\"/></td>" +
										"<td><input about='prioritys"+item.id+"' name=\"prioritys\" value=\""+pri+"\" onchange='changeNum(this.value,"+item.id+")' htmlEscape=\"false\" maxlength=\"5\" class=\"input-medium required\" placeholder=\"必填！\" type=\"number\" /><label style='display: none' class=\"error\"></label></td>"+
										"<td><a href='#' onclick='removeItem(\""+item.id+"\")'>移除</a></td></tr>";
								});
                            } else {
                                htmlInfo+="<tr class='"+item.id+"'><td id='"+item.id+"'><input name='skuInfoIds' type='hidden' readonly='readonly' value='"+item.id+"'/>"+ item.name +"</td>"+
                                    "<td><input about='shQtys"+item.id+"' name='shelfQtys' value='1000' htmlEscape='false' maxlength='6' class='input-mini required' type='number' placeholder='必填！'/><label style='display: none' class=\"error\"></label></td>"+
                                    "<td><input about='orgPrices"+item.id+"' name='orgPrices' readonly='readonly' value='"+item.buyPrice+"' htmlEscape='false' maxlength='6' class='input-mini required' type='number' placeholder='必填！' /></td>"+
                                    "<td><input name=\"salePrices\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type='number' placeholder=\"必填！\" onchange='getCommission(this)'/><label style='display: none' class=\"error\"></label></td>";
									if (retail == 1) {
										htmlInfo += "<td><input name='marketPrices' value='' class='input-mini required' type='number'placeholder=\"必填！\"/></td>";
										htmlInfo += "<td><input name='commissionRatios' value='"+commissionRatio+"' class='input-mini required' type='number' min='0' placeholder=\"必填！\" onchange='getCommissionByRatio(this)'/></td>";
										htmlInfo += "<td><input name='commission' value='' class='input-mini required' readonly='readonly' type='number' placeholder=\"必填！\"/></td>";
									}
                               		 htmlInfo += "<td><input name=\"minQtys\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type=\"number\" placeholder=\"必填！\"/><label style='display: none'  class=\"error\"></label></td>"+
                                    "<td><input name=\"maxQtys\" value=\"\" htmlEscape=\"false\" maxlength=\"6\" class=\"input-medium required\" type=\"number\" placeholder=\"必填！\" onchange='addOne(this,"+item.id+")'/><label style='display: none' class=\"error\"></label></td>"+
                                    "<td><input about='shelfDate"+item.id+"' name=\"shelfTimes\" value=\""+now+"\" type=\"text\" readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate required\"" +
                                    "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});\" placeholder=\"必填！\"/></td>"+

                                    "<td><input about='unshelfTimes"+item.id+"' name=\"unshelfTimes\" type=\"text\" value='' readonly=\"readonly\" maxlength=\"20\" class=\"input-medium Wdate \"" +
                                    "onclick=\"WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});\" placeholder=\"选填！\"/></td>" +
                                    "<td><input about='prioritys"+item.id+"' name=\"prioritys\" value=\""+pri+"\" onchange='changeNum(this.value,"+item.id+")' htmlEscape=\"false\" maxlength=\"5\" class=\"input-medium required\" placeholder=\"必填！\" type=\"number\" /><label style='display: none' class=\"error\"></label></td>"+
                                    "<td><a href='#' onclick='removeItem(\""+item.id+"\")'>移除</a></td></tr>";
							}
                            pri += 10;
                        });
                        $("#tbody").append(htmlInfo);
                        if (retail != 1) {
							$("#ShelfSkuTable").tablesMergeCell({
								// automatic: true
								// 是否根据内容来合并
								cols:[0,1,2,6,7,8,9]
								// rows:[0,2]
							});
                        }
						if (retail == 1) {
							$("#ShelfSkuTable").tablesMergeCell({
								// automatic: true
								// 是否根据内容来合并
								cols:[0,1,2,9,10,11,12]
								// rows:[0,2]
							});
						}

                        <%--遍历每个tr下td--%>
                        <%--var leng = $("#tbody tr").length;--%>
                        <%--var filter_numbs = new Array();--%>
                        <%--for(var i=0; i<=leng; i++){--%>
                        <%--$("#rowsTd").attr("rowspan",""+i+"");--%>
                        <%--numberStr = $("#tbody tr").eq(i).find("td:first").empty();--%>
                        <%--filter_numbs.push(numberStr);--%>
                        <%--}--%>
                        <%---end---%>
                    }
                })

            });

            $("#contentTableService").tablesMergeCell({
                automatic: true,
                // 是否根据内容来合并
                cols: [0, 0]
                // rows:[0,2]
            });
            <c:if test="${bizOpShelfSku.id != null}">
            var opShelfId=$("#shelfInfoId").val();
            $.ajax({
                type:"post",
                url:"${ctx}/biz/shelf/bizOpShelfInfo/findColum?id="+opShelfId,
                success:function (data) {
                    opShelfType = data.type;
                    if(data.type=='<%=BizOpShelfInfoEnum.LOCAL_STOCK.getLocal() %>' || data.type=='<%=BizOpShelfInfoEnum.EXCLUSIVE_OFF.getLocal() %>'){
                        $("#PurchaseID").css("display","block");

                        $("#centerOfficeName").prop("disabled", "disabled")
                        $("#centerOfficeButton").prop("disabled", "disabled")
                        $("#centerOfficeButton").css("pointer-events","none");
                    }else{
                        $("#PurchaseID").css("display","none");
                        $("#centerOfficeId").prop("value","0");
                    }
                }
            });
            </c:if>

        });
        function removeItem(obj) {
            $("."+obj).remove();
        }

        function addOne(item,skuId) {
            var trIndex = parseInt($(item).parent("td").parent("tr").index()) + 1;
            var second = $("#tbody").find("tr:eq("+trIndex+")");
            if ($(second).attr("class")==skuId){
                $(second).find("td").each(function () {
                    $(this).find("input[name='minQtys']").val(parseInt(item.value) + 1);
                    $(this).find("input[name='minQtys']").attr("readonly","readonly");
                });
            }
        }

        function changeNum(num,obj) {
			$("input[about='prioritys"+obj+"']").each(function () {
				$(this).val(num);
            });
        }

        function getCommission(item) {
            var orgPrice = $(item).parent().parent().find("input[name='orgPrices']");
			var commissionRatios = $(item).parent().parent().find("input[name='commissionRatios']");
			var commission = $(item).parent().parent().find("input[name='commission']");
			$(commission).val((parseFloat($(item).val()) - parseFloat($(orgPrice).val())) * parseFloat($(commissionRatios).val()) / 100);
        }

        function getCommissionByRatio(item) {
            var orgPrice = $(item).parent().parent().find("input[name='orgPrices']");
			var salePrice = $(item).parent().parent().find("input[name='salePrices']");
            var commission = $(item).parent().parent().find("input[name='commission']");
            $(commission).val((parseFloat(($(salePrice).val()) - parseFloat($(orgPrice).val())) * parseFloat($(item).val()) / 100).toFixed(2));
        }

        function shelfInfoChanged() {
            $.ajax({
                type:"post",
                url:"${ctx}/biz/shelf/bizOpShelfInfo/findColum?id="+opShelfId,
                success:function (data) {
                    if(data.type=='<%=BizOpShelfInfoEnum.LOCAL_STOCK.getLocal() %>' || data.type=='<%=BizOpShelfInfoEnum.EXCLUSIVE_OFF.getLocal() %>'){
                        $("#PurchaseID").css("display","block");
                        if (opShelfType != data.type) {
                            $("#centerOfficeName").prop("disabled", false)
                            $("#centerOfficeButton").prop("disabled", false)
                            $("#centerOfficeButton").css("pointer-events","auto");
                        }
                    }else{
                        $("#PurchaseID").css("display","none");
                        $("#centerOfficeId").prop("value","0");
                    }
                }
            });
		}

        function needPutawayForMyPanel() {
            var previousPageVal = $("#previousPage").val();
            console.log(previousPageVal)
            if (previousPageVal == 'myPanel') {
                $Mask.AddLogo("正在加载");
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/needPutawayForMyPanel",
                    data:{"notPutaway": 1, "productInfo.prodType":1},
                    success:function (result) {
                        $("#prodInfo2").empty();
                        var data = JSON.parse(result).data;
                        if (data == '') {
                            $Mask.RemoveLogo();
                            $Mask.RemoveContent();
                            return false;
                        } else {
                            $.each(data.skuMap, function (keys, skuInfoList) {

                                var prodKeys = keys.split(",");
                                var prodId = prodKeys[0];
                                var prodUrl = prodKeys[2];
                                var brandName = prodKeys[6];
                                var varietyId = prodKeys[7];
                                var varietyName = prodKeys[8];

                                var flag = true;

                                var tr_tds = "";
                                var t = 0;

                                var factorMap = data.serviceFactor;
                                var factorStr = factorMap[varietyId];

                                //  var factorArr=$(factorStr).split(",");
                                var f = "";
                                if (factorStr != undefined) {
                                    $.each(factorStr, function (i) {
                                        f += factorStr[i] + "<br/>";
                                    });
                                }

                                $.each(skuInfoList, function (index, skuInfo) {

                                    tr_tds += "<tr class='" + prodId + "'>";
                                    tr_tds += "<td><input type='checkbox' value='" + skuInfo.id + "' title='shelfIds'/></td>";
                                    tr_tds += "<td>" + skuInfo.name + "</td><td>" + skuInfo.buyPrice + "</td><td>" + skuInfo.partNo + "</td><td>" + skuInfo.itemNo + "</td>" ;
                                    //商品已上货架名称
                                    tr_tds += "<td>" + skuInfo.shelfNames + "</td>";
                                    // "<td>" + skuInfo.skuPropertyInfos + "</td>";

                                    if (flag) {
                                        tr_tds += "<td rowspan='" + skuInfoList.length + "'>" + varietyName + "<br/>" + f + "</td>";
                                        tr_tds += "<td rowspan='" + skuInfoList.length + "'>" + brandName + "</td>";
                                        tr_tds += "<td rowspan='" + skuInfoList.length + "'><img style='width: 160px;height: 160px' src='" + prodUrl + "' maxWidth='100' maxHeight='100'></td>"
                                    }

                                    tr_tds += "</tr>";
                                    if (skuInfoList.length > 1) {
                                        flag = false;
                                    }
                                });

                                t++;
                                $("#prodInfo2").append(tr_tds);
                            });
                        }
                        $Mask.RemoveLogo();
                        $Mask.RemoveContent();
                    }
                })
            }
        }

	</script>
	<script type="text/javascript">
        function selectedColum(){
            <%--属于选中货架名称下的 本地备货--%>
            var type = 0;
            var retail = false;
            $("input:checkbox[name='shelfs']:checked").each(function (i) {
                var opshelf = $(this);
                var opShelfId=$(this).val();
                var price = $("#price").text();
                var market = $("#market");
                var commission = $("#commission");
                var commissions = $("#commissions");
                $.ajax({
					type:"post",
                    async:false,
					url:"${ctx}/biz/shelf/bizOpShelfInfo/findColum?id="+opShelfId,
					success:function (data) {
                        if (data.type==${BizOpShelfInfoEnum.SELL_OFF.local}) {
                            notSellOff();
                            sellOff();
                            retail = true;
                        } else {
                            notSellOff();
                        }
                        // if (retail = 'first1') {
                        //     notSellOff();
                        // }
						if(data.type==${BizOpShelfInfoEnum.LOCAL_STOCK.local} || data.type==${BizOpShelfInfoEnum.EXCLUSIVE_OFF.local}){
                            if (type != 0 && type != data.type) {
                                alert("平台商品和本地商品和代销不能同时选择");
                                opshelf.removeAttr("checked");
                                $("#price").text(price);
                                return false;
                            }
                            type = data.type;
                            $("#PurchaseID").css("display","block");
						}else{
                            if (type != 0 && type != data.type) {
                                alert("平台商品和本地商品和代销不能同时选择");
                                opshelf.removeAttr("checked");
                                $("#price").text(price);
                                if (market != null) {
                                    $("#market").remove();
                                }
                                if (commission != null) {
                                    $("#commission").remove();
                                }
                                if (commissions != null) {
                                    $("#commissions").remove();
                                }
                                return false;
                            }
                            type = data.type;
                            $("#PurchaseID").css("display","none");
                            $("#centerOfficeId").prop("value","");
						}
					}
				});
			});
            if (retail) {
                $("#tbody").append("<c:set var='retail' value='1'/>");
                $("#tbody").append("<input id='retail' type='hidden' value='1'/>");
            }
            if (!retail) {
                $("#tbody").append("<c:remove var="retail"/>");
                notSellOff();
                $("#retail").remove();
            }
        }

        function sellOff() {
			$("#price").text("零售价(元)");
			$("#price").after("<th id='market'>市场价(元)</th>");
			$("#market").after("<th id='commission'>佣金比(%)</th>");
			$("#commission").after("<th id='commissions'>佣金</th>");
        }
        function notSellOff() {
            $("#price").text("销售单价(元)");
            $("#market").remove();
            $("#commission").remove();
            $("#commissions").remove();
        }

        function centerOfficeChange(org) {
            var previousPageVal = $("#previousPage").val();
            if (previousPageVal != 'myPanel') {
                var officeId = $("#centerOfficeId").val();
                $Mask.AddLogo("正在加载");
                $.ajax({
                    type:"post",
                    url:"${ctx}/biz/sku/bizSkuInfo/findSkuListByCustomer",
                    data:{"officeId": officeId},
                    success:function (result) {
                        $("#prodInfo2").empty();
                        var data = JSON.parse(result).data;
                        if (data == '') {
                            $Mask.RemoveLogo();
                            $Mask.RemoveContent();
                            return false;
                        } else {
                            $.each(data.skuMap, function (keys, skuInfoList) {

                                var prodKeys = keys.split(",");
                                var prodId = prodKeys[0];
                                var prodUrl = prodKeys[2];
                                var brandName = prodKeys[6];
                                var varietyId = prodKeys[7];
                                var varietyName = prodKeys[8];

                                var flag = true;

                                var tr_tds = "";
                                var t = 0;

                                var factorMap = data.serviceFactor;
                                var factorStr = factorMap[varietyId];

                                //  var factorArr=$(factorStr).split(",");
                                var f = "";
                                if (factorStr != undefined) {
                                    $.each(factorStr, function (i) {
                                        f += factorStr[i] + "<br/>";
                                    });
                                }

                                $.each(skuInfoList, function (index, skuInfo) {

                                    tr_tds += "<tr class='" + prodId + "'>";
                                    tr_tds += "<td><input type='checkbox' value='" + skuInfo.id + "' title='shelfIds'/></td>";
                                    tr_tds += "<td>" + skuInfo.name + "</td><td>" + skuInfo.buyPrice + "</td><td>" + skuInfo.partNo + "</td><td>" + skuInfo.itemNo + "</td>" ;
                                    //商品已上货架名称
                                    tr_tds += "<td>" + skuInfo.shelfNames + "</td>";
                                    // "<td>" + skuInfo.skuPropertyInfos + "</td>";

                                    if (flag) {
                                        tr_tds += "<td rowspan='" + skuInfoList.length + "'>" + varietyName + "<br/>" + f + "</td>";
                                        tr_tds += "<td rowspan='" + skuInfoList.length + "'>" + brandName + "</td>";
                                        tr_tds += "<td rowspan='" + skuInfoList.length + "'><img style='width: 160px;height: 160px' src='" + prodUrl + "' maxWidth='100' maxHeight='100'></td>"
                                    }

                                    tr_tds += "</tr>";
                                    if (skuInfoList.length > 1) {
                                        flag = false;
                                    }
                                });

                                t++;
                                $("#prodInfo2").append(tr_tds);
                            });
                        }
                        $Mask.RemoveLogo();
                        $Mask.RemoveContent();
                    }
                })
			}
        }
	</script>
	<meta name="decorator" content="default"/>
</head>
<body>
<ul class="nav nav-tabs">
	<li><a href="${ctx}/biz/shelf/bizOpShelfSkuV2/">商品上架列表</a></li>
	<li class="active"><a href="${ctx}/biz/shelf/bizOpShelfSkuV2/form?id=${bizOpShelfSku.id}">商品上架<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit">${not empty bizOpShelfSku.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="biz:shelf:bizOpShelfSku:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>
<form:form id="inputForm" modelAttribute="bizOpShelfSku" action="${ctx}/biz/shelf/bizOpShelfSkuV2/save" method="post" class="form-horizontal">
	<form:hidden path="id"/>
	<form:hidden path="searchItemNo" value="${searchItemNo}"/>
    <c:set var="id" value="${bizOpShelfSku.id}"/>
	<form:hidden path="shelfSign"/>
	<input id="commissionRatio" type="hidden" value="${commissionRatio}"/>
	<input id="previousPage" name="previousPage" type="hidden" value="${bizOpShelfSku.previousPage}"/>
	<%--<input type="hidden" id="opShelfId" value="${bizOpShelfSku.opShelfInfo.id}"/>--%>
	<%--<form:hidden id="shelfId" path="opShelfInfo.id"/>--%>
	<sys:message content="${message}"/>
	<%--<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit">--%>
	<c:if test="${bizOpShelfSku.id == null}">
		<div class="control-group">
			<label class="control-label">货架名称：</label>
			<div class="controls">
				<c:forEach items="${shelfList}" var="shelf" varStatus="i">
					<input id="shelfs_${i.index}" name="shelfs" type="checkbox" onchange="selectedColum()" class="required" value="${shelf.id}"/>
					<label for="shelfs_${i.index}">${shelf.name}</label>
				</c:forEach>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
	</c:if>
	<c:if test="${bizOpShelfSku.id != null}">
		<div class="control-group">
			<label class="control-label">货架名称：</label>
			<div class="controls">
				<form:select id="shelfInfoId" path="opShelfInfo.id" class="input-xlarge required" disabled="true" onchange="shelfInfoChanged()">
					<form:option value="">请选择</form:option>
					<form:options items="${shelfList}" itemLabel="name" itemValue="id"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<input id="opShelfInfoType" type="hidden" value="${bizOpShelfSku.opShelfInfo.type}"/>
	</c:if>

	<div class="control-group" id="PurchaseID" style="display:none">
		<label class="control-label">采购中心：</label>
		<div class="controls">
			<sys:treeselect id="centerOffice" name="centerOffice.id" value="${bizOpShelfSku.centerOffice.id}" labelName="centerOffice.name"
							labelValue="${bizOpShelfSku.centerOffice.name}"  notAllowSelectParent="true" onchange="centerOfficeChange(this)"
							title="采购中心" url="/sys/office/queryTreeList?type=8&customerTypeTen=10&customerTypeEleven=11&source=officeConnIndex" cssClass="input-xlarge required" dataMsgRequired="必填信息">
			</sys:treeselect>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
	</div>
	<%--<c:if test="${bizOpShelfSku.previousPage != 'myPanel'}">--%>
		<div class="control-group">
			<label class="control-label">选择商品：</label>
			<div class="controls">
				<ul class="inline ul-form">
					<li><label>品牌名称：</label>
						<input id="prodBrandName" onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false" maxlength="50" class="input-medium"/>
					</li>
					<li><label>商品名称：</label>
						<input id="skuName"  onkeydown='if(event.keyCode==13) return false;'   htmlEscape="false"  class="input-medium"/>
					</li>
					<li><label>商品编码：</label>
						<input id="skuCode"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
					</li>
					<li><label>商品货号：</label>
						<input id="itemNo"  onkeydown='if(event.keyCode==13) return false;'  htmlEscape="false"  class="input-medium"/>
					</li>
					<li class="btns"><input id="searchData" class="btn btn-primary" type="button"  value="查询"/></li>
					<li class="clearfix"></li>
				</ul>

			</div>
		</div>
	<%--</c:if>--%>
	<div class="control-group">
		<label class="control-label">上架商品：</label>
		<div class="controls">
			<table id="contentTable2"  class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th><input id="select_all" type="checkbox" /></th>
					<th>商品名称</th>
					<th>出厂价(元)：</th>
					<th>商品编码</th>
					<th>商品货号</th>
					<th style="width:8%;word-wrap:break-word;word-break:break-all;">已上架货架名称</th>
					<%--<th>商品属性</th>--%>
					<th>分类与服务系数</th>
					<th>品牌名称</th>
					<th>产品图片</th>
						<%--<th>操作</th>--%>
				</tr>
				</thead>
				<tbody id="prodInfo2">

				</tbody>
			</table>
			<input id="ensureData" class="btn btn-primary" type="button"  value="确定"/>
		</div>
	</div>

	<div class="control-group">
		<div class="controls">
			<table id="ShelfSkuTable" class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th>商品名称：</th>
						<%--<c:if test="${bizOpShelfSku.id != null}">--%>
						<%--<th>上架人：</th>--%>
						<%--</c:if>--%>
					<th>上架数量(个)：</th>
					<th>出厂价(元)：</th>
					<th id="price">
						<c:if test="${bizOpShelfSku.opShelfInfo.type == 5}">零售价(元)</c:if>
						<c:if test="${bizOpShelfSku.opShelfInfo.type != 5}">销售单价(元)</c:if>
					</th>
					<c:if test="${bizOpShelfSku.opShelfInfo.type == 5}">
						<th>市场价</th>
						<th>佣金比(%)</th>
						<th>佣金</th>
					</c:if>
					<th>最低销售数量(个)：</th>
					<th>最高销售数量(个，9999表示不限制)：</th>
					<th>上架时间：</th>
						<%--<c:if test="${bizOpShelfSku.id != null}">--%>
						<%--<th>下架人：</th>--%>
						<%--</c:if>--%>
					<th>下架时间：</th>
					<th>显示次序：</th>
					<th>操作</th>
				</tr>
				</thead>
				<tbody id="tbody">
				<c:if test="${bizOpShelfSku.id != null}">
					<tr>
						<td>
							<input id="bizOpShelfSkuId" name="id" value="${bizOpShelfSku.id}" class="input-medium required" type="hidden"/>
							<input name="skuInfoIds" value="${bizOpShelfSku.skuInfo.id}" class="input-medium required" type="hidden"/>${bizOpShelfSku.skuInfo.name}
						</td>
							<%--<td><input name="createBy.name" value="${bizOpShelfSku.shelfUser.name}" htmlEscape="false" maxlength="11" class="input-medium" readonly="true" type="number" placeholder="必填！"/></td>--%>
						<td><input name="shelfQtys" value="${bizOpShelfSku.shelfQty}" htmlEscape="false" maxlength="6" class="input-medium required" type="number" placeholder="必填！"/></td>
						<td><input name="orgPrices" value="${bizOpShelfSku.orgPrice}" htmlEscape="false" maxlength="6" class="input-medium required" readonly="readonly" type="number" placeholder="必填！"/></td>
						<td><input name="salePrices" value="${bizOpShelfSku.salePrice}" htmlEscape="false" maxlength="6" class="input-medium required" type="number" placeholder="必填！" onchange="getCommission(this)"/></td>
						<c:if test="${bizOpShelfSku.opShelfInfo.type == 5}">
							<td><input name="marketPrices" value="${bizOpShelfSku.marketPrice}" htmlEscape="false" maxlength="6" class="input-medium required" type="number" placeholder="必填！"/></td>
							<td><input name="commissionRatios" value="${bizOpShelfSku.commissionRatio}" htmlEscape="false" maxlength="6" class="input-medium required" type="number" min="0" placeholder="必填！" onchange="getCommissionByRatio(this)"/></td>
							<td><input name="commission" value=<fmt:formatNumber type="number" value="${(bizOpShelfSku.salePrice - bizOpShelfSku.orgPrice) * bizOpShelfSku.commissionRatio / 100}" maxFractionDigits="2"/> htmlEscape="false" maxlength="6" class="input-medium required" readonly="readonly" type="number" placeholder="必填！"/></td>
						</c:if>
						<td><input name="minQtys" readonly="true" value="${bizOpShelfSku.minQty}" htmlEscape="false" maxlength="6" class="input-medium required" type="number" placeholder="必填！"/></td>
						<td><input name="maxQtys" readonly="true" value="${bizOpShelfSku.maxQty}" htmlEscape="false" maxlength="6" class="input-medium required" type="number" placeholder="必填！"/></td>
						<td><input name="shelfTimes" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate required"
								   value="<fmt:formatDate value="${bizOpShelfSku.shelfTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>"
								   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="必填！"/></td>
							<%--<td><input name="createBy.name" value="${bizOpShelfSku.unshelfUser.name}" htmlEscape="false" maxlength="11" class="input-medium" readonly="true" type="number" placeholder="必填！"/></td>--%>
						<td><input name="unshelfTimes" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
								   value="<fmt:formatDate value="${bizOpShelfSku.unshelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
								   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});" placeholder="选填！"/></td>
						<td><input name="prioritys" value="${bizOpShelfSku.priority}" htmlEscape="false" maxlength="5" class="input-medium required" type="number" placeholder="必填！"/></td>
					</tr>
				</c:if>
				</tbody>
			</table>
		</div>
	</div>

	<div class="form-actions">
		<shiro:hasPermission name="biz:shelf:bizOpShelfSku:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</div>
	<input type="hidden" value="${hasUnderPriceRole}" id="hasUnderPriceRole">
</form:form>
<c:if test="${bizOpShelfSkuList.size() > 0 }">
	<div class="control-group">
		<div class="controls">
			<table id="ShelfSkuTableRefer" class="table table-striped table-bordered table-condensed">
				<thead>
				<tr>
					<th>商品名称：</th>
					<th>上架数量(个)：</th>
					<th>出厂价(元)：</th>
					<th id="price">
						<c:if test="${bizOpShelfSku.opShelfInfo.type == 5}">零售价(元)</c:if>
						<c:if test="${bizOpShelfSku.opShelfInfo.type != 5}">销售单价(元)</c:if>
					</th>
					<c:if test="${bizOpShelfSku.opShelfInfo.type == 5}">
						<th>市场价</th>
						<th>佣金比(%)</th>
						<th>佣金</th>
					</c:if>
					<th>最低销售数量(个)：</th>
					<th>最高销售数量(个，9999表示不限制)：</th>
					<th>上架时间：</th>
					<th>下架时间：</th>
					<th>显示次序：</th>
					<th>操作</th>
				</tr>
				</thead>
				<tbody id="tbody">
				<c:if test="${bizOpShelfSku.id != null}">
					<c:forEach items="${bizOpShelfSkuList}" var="bizOpShelfSku">
						<tr>
							<td>
								<input name="skuInfoIds" readonly="readonly" value="${bizOpShelfSku.skuInfo.id}"
									   class="input-medium required" type="hidden"/>${bizOpShelfSku.skuInfo.name}
							</td>
							<td><input name="shelfQtys" value="${bizOpShelfSku.shelfQty}" htmlEscape="false"
									   maxlength="6" class="input-medium required" readonly="readonly" type="number"
									   placeholder="必填！"/></td>
							<td><input name="orgPrices" value="${bizOpShelfSku.orgPrice}" htmlEscape="false"
									   maxlength="6" class="input-medium required" readonly="readonly" type="number"
									   placeholder="必填！"/></td>
							<td><input name="salePrices" value="${bizOpShelfSku.salePrice}" htmlEscape="false"
									   maxlength="6" class="input-medium required" readonly="readonly" type="number"
									   placeholder="必填！"/></td>
							<c:if test="${bizOpShelfSku.opShelfInfo.type == 5}">
								<td><input name="marketPrices" value="${bizOpShelfSku.marketPrice}" htmlEscape="false"
										   maxlength="6" class="input-medium required" readonly="readonly" type="number"
										   placeholder="必填！"/></td>
								<td><input name="commissionRatios" value="${bizOpShelfSku.commissionRatio}" htmlEscape="false"
										   maxlength="6" class="input-medium required" readonly="readonly" type="number"
										   placeholder="必填！"/></td>
								<td><input name="commission" value=<fmt:formatNumber type="number" value="${(bizOpShelfSku.salePrice - bizOpShelfSku.orgPrice) * bizOpShelfSku.commissionRatio / 100}" maxFractionDigits="2"/> htmlEscape="false"
										   maxlength="6" class="input-medium required" readonly="readonly" type="number"
										   placeholder="必填！"/></td>
							</c:if>
							<td><input name="minQtys" value="${bizOpShelfSku.minQty}" htmlEscape="false" maxlength="6"
									   class="input-medium required" readonly="readonly" type="number"
									   placeholder="必填！"/></td>
							<td><input name="maxQtys" value="${bizOpShelfSku.maxQty}" htmlEscape="false" maxlength="6"
									   class="input-medium required" readonly="readonly" type="number"
									   placeholder="必填！"/></td>
							<td><input name="shelfTimes" type="text" readonly="readonly" maxlength="20"
									   class="input-medium Wdate required"
									   value="<fmt:formatDate value="${bizOpShelfSku.shelfTime}"  pattern="yyyy-MM-dd HH:mm:ss"/>"/>
							</td>
							<td><input name="unshelfTimes" type="text" readonly="readonly" maxlength="20"
									   class="input-medium Wdate "
									   value="<fmt:formatDate value="${bizOpShelfSku.unshelfTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
							</td>
							<td><input name="prioritys" value="${bizOpShelfSku.priority}" htmlEscape="false"
									   maxlength="5" class="input-medium required" readonly="readonly" type="number"
									   placeholder="必填！"/></td>
						</tr>
					</c:forEach>
				</c:if>
				</tbody>
			</table>
		</div>
	</div>
</c:if>

<form:form id="searchForm" modelAttribute="bizSkuInfo" >
	<%--<form:hidden id="productNameCopy" path="productInfo.name"/>--%>
	<%--<form:hidden id="prodCodeCopy" path="productInfo.prodCode"/>--%>
	<form:hidden id="prodBrandNameCopy" path="productInfo.brandName"/>
	<form:hidden id="skuNameCopy" path="name"/>
	<form:hidden id="skuCodeCopy" path="partNo"/>
	<form:hidden id="itemNoCopy" path="itemNo"/>
	<%--<form:hidden id="skuTypeCopy" path="skuType"/>--%>
</form:form>

</body>

</html>
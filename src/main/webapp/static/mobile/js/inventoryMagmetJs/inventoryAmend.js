(function($) {
    var ACCOUNT = function() {
        this.ws = null;
        this.userInfo = GHUTILS.parseUrlParam(window.location.href);
        this.expTipNum = 0;
        this.datagood = [];
        this.skuInfoIds_1="";
        this.skuInfoIds_2="";
        this.reqQtys_1="";
        this.reqQtys_2="";
        this.reqDetailIds="";
        this.LineNos="";
        this.fromOfficeId="";
        this.deleteBtnFlag = "false"
        return this;
    }

    var bizStatusDesc = (function() {
        var result;
        $.ajax({
            type: "GET",
            url: "/a/sys/dict/listData",
            data: {type:"biz_req_status"},
            dataType: "json",
            async:false,
            success: function(res){
                result = res;
            }
        });
        return result;

    })();

    ACCOUNT.prototype = {
        init: function() {
            this.hrefHtml('.newinput', '.input_div');
            this.pageInit(); //页面初始化
            //this.getData();//获取数据
            GHUTILS.nativeUI.closeWaiting();//关闭等待状态
            //GHUTILS.nativeUI.showWaiting()//开启
        },
        pageInit: function() {
            var _this = this;
		},
		rendHtml: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reult.push(item)

					}
				})
			$.each(reult, function(i, item) {
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div').html(htmlList)

		},
        ajaxGoodList: function() {
            var _this = this;
            var htmlList = ''
            $.ajax({
                type: 'GET',
                url: '/a/sys/office/queryTreeList',
                data: {
                    type: 8
                },
                dataType: 'json',
                success: function(res) {
                    _this.datagood = res
                    $.each(res, function(i, item) {
                        htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
                    });
                    $('.input_div').html(htmlList)
                }
            });

        },
        ajaxCheckStatus: function() {
            var _this = this;
            var optHtml ='<option value="">请选择</option>';
            var htmlStatusAmend = ''
            $.ajax({
                type: 'GET',
                url: '/a/sys/dict/listData',
                data: {type:'biz_req_status'},
                dataType: 'json',
                success: function(res) {
                    $.each(res, function(i, item) {
                        htmlStatusAmend += '<option class="soption" createDate="' + item.createDate + '" description="' + item.description + '" id="' + item.id + '" isNewRecord="' + item.isNewRecord + '"  sort="' + item.sort +  '" value="' + item.value + '">' + item.label + '</option>'
                    });
                    $('#inputDivAmend').html(optHtml+htmlStatusAmend)
                    _this.getPermissionList();
                    _this.getData()
                }
            });
        },

//	$(function() {
//          //业务状态select初始化
//          var bizStatusSelect = $("#bizStatusSelect");
//          var bizStatusOptionHtml = '<select class="secStyle" name="" id="bizStatusShow">' +
//              '<option value="">请选择</option>';
//
//          $.each(bizStatusDesc, function(i, item) {
//              bizStatusOptionHtml += '<option value="' + item.value + '">' + item.label + '</option>'
//          });
//          bizStatusOptionHtml += '</select><input class="savedata" type="hidden" value="" />' +
//              '<div style="clear:both"></div>';
//          bizStatusSelect.append(bizStatusOptionHtml);
//      },
        getData: function() {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeader/form4Mobile",
                data: {id:_this.userInfo.reqId},
                dataType: "json",
                success: function(res){
                    $('#inPoordNum').val(res.data.entity.reqNo)
                    $('#inOrordNum').val(res.data.entity.fromOffice.name)
                    $('#inPototal').val(res.data.entity.totalMoney)
                    $('#inPoRemark').val(res.data.entity.remark)
                    $('#inMoneyReceive').val()
                    $('#inMarginLevel').val()
                    var dataValue =_this.newData(res.data.entity.recvEta)
                    $('#inPoLastDa').val(dataValue)

                    /*业务状态*/
                    var bizstatus = res.data.entity.bizStatus;
                    $('#inputDivAmend  option[value="' + bizstatus + '"]').attr("selected",true)
                    _this.commodityHtml(res.data)
                    _this.statusListHtml(res.data)
                    _this.searchSkuHtml()
                    _this.saveDetail();

                }
            });
        },
        newData:function(da){
        	var _this = this;
            var now = new Date(da),
                y = now.getFullYear(),
                m = now.getMonth() + 1,
                d = now.getDate();
           // return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + "T" + now.toTimeString().substr(0, 8);
             return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d);
        },
        saveDetail: function () {
            var _this = this;
            mui('.saveDetailBtn').on('tap','#saveDetailBtn',function(){
                var skuIds = _this.skuInfoIds_2.split(",");
                var skuInfoIdsTemp = ""
                for (var i=0; i<skuIds.length; i++){
                    var skuId = skuIds[i];
                    if (skuId != null && skuId != "") {
                        skuInfoIdsTemp += "," + skuId;
                    }
                }
                _this.skuInfoIds_2 = skuInfoIdsTemp.substring(1);

                var skuIds2 = _this.skuInfoIds_2.split(",");
                var reqQtysTemp = "";
                for (var j=0; j<skuIds2.length; j++) {
                    var cheId = skuIds2[j];
                    var reqQty = $("#reqQty_" + cheId).val()
                    if (reqQty == null || reqQty == "") {
                    	mui.toast("请输入申报数量！")
                        return;
                    }
                    reqQtysTemp += "," + reqQty;
                }
                _this.reqQtys_2 = reqQtysTemp.substring(1);

                skuInfoIds = _this.skuInfoIds_1 + _this.skuInfoIds_2;
                reqQtys = _this.reqQtys_1 + _this.reqQtys_2;
                _this.reqDetailIds = _this.reqDetailIds.substring(0,(_this.reqDetailIds.lastIndexOf(",")))
                _this.LineNos = _this.LineNos.substring(0,(_this.LineNos.lastIndexOf(",")))

                var inPoLastDaVal = $("#inPoLastDa").val(); //期望收货时间
//              console.log("inPoLastDaVal=" + inPoLastDaVal);

                var inPoRemarkVal = $("#inPoRemark").val(); //备注
                var bizStatusVal = $("#inputDivAmend")[0].value; //业务状态
                var id = _this.userInfo.reqId; //业务状态

                if (_this.fromOfficeId == null || _this.fromOfficeId == "") {
                    var inOrordNum = $("#inOrordNum").val();
                    _this.fromOfficeId = _this.getFromOfficeId(inOrordNum);
                }

                if(_this.fromOfficeId == null || _this.fromOfficeId == ""){
                    mui.toast("请选择采购中心！")
                    return;
                }
                if(inPoLastDaVal == null || inPoLastDaVal == "") {
                    mui.toast("请选择收货时间！")
                    return;
                }
                if(bizStatusVal == null || bizStatusVal == "") {
                    mui.toast("请选择业务状态！")
                    return;
                }

                $.ajax({
                    type: "post",
                    url: "/a/biz/request/bizRequestHeader/save4Mobile",
                    data: {"id":id, "fromOffice.id": _this.fromOfficeId, "recvEta":inPoLastDaVal, "remark": inPoRemarkVal, "bizStatus": bizStatusVal, "skuInfoIds": skuInfoIds, "reqQtys": reqQtys, "reqDetailIds":_this.reqDetailIds, "LineNos":_this.LineNos},
                    dataType: 'json',
                    success: function (resule) {
                        if (resule.data.value == '操作成功!') {
                            mui.toast("保存备货单成功！");
                            GHUTILS.OPENPAGE({
                                url: "../../html/inventoryMagmetHtml/inventoryList.html",
                                extras: {
                                }
                            })
                        }
                    }
                })
            })
        },
        getPermissionList: function () {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": "biz:request:bizRequestDetail:edit"},
                async:false,
                success: function(res){
                    _this.deleteBtnFlag = res.data;
//                  console.log(_this.deleteBtnFlag)
                }
            });
        },
        getFromOfficeId: function(inOrordNum) {
            var _this = this;
            var fromOfficeId = "";
            $.ajax({
                type: 'GET',
                url: '/a/sys/office/queryTreeList',
                data: {
                    type: 8
                },
                dataType: 'json',
                async:false,
                success: function(res) {
                    $.each(res, function(i, item) {
                        // htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
                        if (item.name == inOrordNum){
                            fromOfficeId = item.id;
                        }
                    });
                }
            });
            return fromOfficeId;
        },
        hrefHtml: function (newinput, input_div) {
            var _this = this;
            _this.ajaxGoodList()
            _this.ajaxCheckStatus()
            $(newinput).on('focus', function () {
                $(input_div).find('hasoid').removeClass('hasoid')
                $(input_div).show()
                $('#hideSpanAmend').show()
            })
            $(newinput).on('keyup', function () {
                _this.rendHtml(_this.datagood, $(this).val())
            })
            $('#hideSpanAmend').on('click', function () {
                $(input_div).find('hasoid').removeClass('hasoid')
                $(input_div).hide()
                $('#hideSpanAmend').hide()
            })

            $(input_div).on('click', '.soption', function () {
                $(this).addClass('hasoid')
                _this.fromOfficeId = $(this).attr("id");
                $(newinput).val($(this).text())
                $(input_div).hide()
                $('#hideSpanAmend').hide()
            })
        },
        statusListHtml:function(data){
            var _this = this;
            var pHtmlList = '';
//			var len = data.bizPoHeader.commonProcessList.length
            $.each(data.statusList, function(i, item) {
                var checkBizStatus = getTizstatusTxt(item.bizStatus);
                var step = i + 1;
                pHtmlList +='<li id="procList" class="step_item">'+
                    '<div class="step_num">'+ step +' </div>'+
                    '<div class="step_num_txt">'+
                    '<div class="mui-input-row">'+
                    '<label>处理人:</label>'+
                    '<input type="text" value="'+ item.createBy.name +'" class="mui-input-clear" disabled>'+
                    '</div>'+
                    '<div class="mui-input-row">'+
                    '<label>状态:</label>'+
                    '<input type="text" value="'+ checkBizStatus +'" class="mui-input-clear" disabled>'+
                    '<label>时间:</label>'+
                    '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
                    '</div>'+
                    '</div>'+
                    '</li>'
            });
            $("#inCheckAddMenu").html(pHtmlList)
        },
        commodityHtml: function(data) {
        	//备货商品初始化反填
            var _this = this;
            var htmlCommodity = '';
            $.each(data.reqDetailList, function(i, item) {
                _this.skuInfoIds_1 += item.skuInfo.id + ","
                _this.reqQtys_1 += item.reqQty + ","
                _this.reqDetailIds += item.id + ","
                _this.LineNos += item.lineNo + ","// 最开始的id="' + item.id + '"    修改后1、id="' + item.skuInfo.id + '"     2、id="serskudiv_' + skuInfo.id + '"
                htmlCommodity += '<div class="mui-row app_bline" id="' + item.id + '">' +
                '<input style="top: 30px;" name="" class="skuinfo_check" id="' + item.skuInfo.id + '" type="checkbox"></div>' +
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label class="commodityName">商品名称：:</label>' +
                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.name + '" disabled></div></li></div></div>' +
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>品牌名称:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.productInfo.brandName + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>结算价:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div></div>' +
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>商品货号:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>申报数量:</label>' +
                    '<input type="text" class="mui-input-clear inDeclareNum" id="" value="' + item.reqQty + '">'+
                    '<font>*</font>'+
                    '</div></li></div></div>';

                if (_this.deleteBtnFlag == true) {
                    htmlCommodity += '<div class="addBtn">' +
                    '<button id="' + item.id +'" type="button" class="deleteSkuButton addBtnClass app_btn_search mui-btn-blue mui-btn-block" >删除</button></div>';
                }
                htmlCommodity += '</div>';
            });
            $("#commodityMenu").html(htmlCommodity)
            _this.delItem()
            _this.removeItem()
        },
        delItem:function () {
        	var that=this;
            mui('#commodityMenu').on('tap','.deleteSkuButton',function(e){
                var obj = e.detail.target.id;
                if (confirm("此删除不需点保存,即可生效.确认删除此条信息吗？")) {
                    $.ajax({
                        type: "post",
                        url: "/a/biz/request/bizRequestDetail/delItem",
                        data: {id: obj},
                        success: function (data) {
                            if (data == 'ok') {
                                mui.toast("删除成功！");
                                $("#" + obj).remove();
                            }
                        }
                    })
                }
            });
        },
        removeItem:function () {
            var _this = this;
            mui('#commodityMenu').on('tap','.removeSkuButton',function(e){
                var obj = e.detail.target.id;
                var cheId = obj.split("_")[1]
                var cheDiv = $("#serskudiv_" + cheId);
                $("#" + cheId).show();
                $("#batchAddDiv").before(cheDiv)
                $("#removeBtn_" + cheId).remove();
                _this.skuInfoIds_2 = _this.skuInfoIds_2.replace(cheId, "");
            });
        },
        searchSkuHtml: function() {
            var _this = this;
            mui('#inAmendPoLastDaDiv').on('tap','#comChoiceBtn',function(){
                var itemNo = $("#inAmendPoLastDa").val();
                $.ajax({
                    type: "post",
                    url: "/a/biz/sku/bizSkuInfo/findSkuList",
                    data: {itemNo: itemNo},
                    success: function (result) {
                    	console.log('修改查询数据')  
                        $("#searchInfo").empty();
                        var data = JSON.parse(result).data;
                        $.each(data,function (keys,skuInfoList) {
                            var prodKeys= keys.split(",");
                            var prodId= prodKeys[0];

//                            var prodName= prodKeys[1];
                            var prodUrl= prodKeys[2];
//                            var cateName= prodKeys[3];
//                            var prodCode= prodKeys[4];
//                            var prodOfficeName= prodKeys[5];
                            var  brandName=prodKeys[6];
                            //var flag=true;
                            var resultListHtml="";
                            var t=0;
                            $.each(skuInfoList,function (index,skuInfo) {
                                //skuInfoId+=","+skuInfo.id;
                                if($("#commodityMenu").children("#serskudiv_"+skuInfo.id).length>0){
                                    return;
                                }
                                resultListHtml += '<div class="mui-row app_bline" id="serskudiv_' + skuInfo.id + '">' +
                                        '<div class="mui-row mui-checkbox mui-left">' +
                                        '<input style="top: 30px;" name="" class="skuinfo_check" id="' + skuInfo.id + '" type="checkbox"></div>' +
                                        '<div class="mui-row">' +
                                        '<div class="mui-row">' +
                                        '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                                        '<div class="mui-col-sm-10 mui-col-xs-10">' +
                                        '<li class="mui-table-view-cell">' +
                                        '<div class="mui-input-row inputClassAdd">' +
                                        '<label class="commodityName">商品名称:</label>' +
                                        '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + skuInfo.name + '" disabled>' +
                                        '</div></li></div></div>' +
                                        '<div class="mui-row">' +
                                        '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                                        '<div class="mui-col-sm-5 mui-col-xs-5">' +
                                        '<li class="mui-table-view-cell">' +
                                        '<div class="mui-input-row inputClassAdd">' +
                                        '<label>品牌名称:</label>' +
                                        '<input type="text" class="mui-input-clear" id="" value="' + brandName +'" disabled>' +
                                        '</div></li></div>' +
                                        '<div class="mui-col-sm-5 mui-col-xs-5">' +
                                        '<li class="mui-table-view-cell">' +
                                        '<div class="mui-input-row inputClassAdd">' +
                                        '<label>结算价:</label>' +
                                        '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.buyPrice + '" disabled>' +
                                        '</div></li></div></div>' +
                                        '<div class="mui-row">' +
                                        '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                                        '<div class="mui-col-sm-5 mui-col-xs-5">' +
                                        '<li class="mui-table-view-cell">' +
                                        '<div class="mui-input-row inputClassAdd">' +
                                        '<label>商品货号:</label>' +
                                        '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.itemNo + '" disabled>' +
                                        '</div></li></div>' +
                                        '<div class="mui-col-sm-5 mui-col-xs-5">' +
                                        '<li class="mui-table-view-cell">' +
                                        '<div class="mui-input-row inputClassAdd">' +
                                        '<label>申报数量:</label>' +
                                        '<input type="hidden" class="mui-input-clear" value="' + skuInfo.id + '">' +
                                        '<input type="text" class="mui-input-clear" placeholder="请输入申报数量" id="reqQty_'+ skuInfo.id +'">' +
                                        '<font>*</font>'+
                                        '</div></li></div></div></div></div>';
                            });
                            t++;                          
                            $("#searchInfo").append(resultListHtml);
                            console.log('=========')
                            var dis=$("#searchInfo .skuinfo_check");
                            var dos=$("#commodityMenu .skuinfo_check");
                            $.each(dis,function(n,v){
                            	var s=$(this).attr('id')
                            	$.each(dos,function(n,v){
                            		var that=this;	                            	
	                            	var y=$(that).attr('id')
	                            	var divs=$("#serskudiv_"+s);
	                            	if (s==y) {
	                            		divs.html('');
	                            	} else{
	                            		
	                            	}
	                            })
                            })
                            console.log('=========')
                        })
                        var addButtonHtml = '<div class="addBtn" id="batchAddDiv">' +
                                '<button id="batchAdd" type="submit" class="addSkuButton addBtnClass app_btn_search mui-btn-blue mui-btn-block">添加' +
                                '</button></div>';
                        $("#searchInfo").append(addButtonHtml);
                    }
                })
            });
            //$("#searchInfo").html(htmlCommodity)
            _this.addSku()
        },
        addSku:function () {
            var _this = this;
            mui('#searchInfo').on('tap','.addSkuButton',function(){
                $(".skuinfo_check").each(function () {
                    var cheId = $(this)[0].id;

                    var cheFlag = $("#" + cheId).is(':checked');
                    if (cheFlag == true) {
                        var cheDiv = $("#serskudiv_" + cheId);
                        $("#" + cheId).prop('checked',false);
                        $("#" + cheId).hide();
                        var resultHtml = '<div class="addBtn" id="removeBtn_' + cheId + '">' +
                            '<button id="remove_' + cheId +'" type="submit" class="removeSkuButton addBtnClass app_btn_search mui-btn-blue mui-btn-block">移除' +
                            '</button></div>';
                            cheDiv.append(resultHtml)
                        $("#commodityMenu").append($(cheDiv))
                        _this.skuInfoIds_2 += cheId + ",";
                    }
                })
            });
        },
        formatDateTime: function(unix) {
            var _this = this;

            var now = new Date(parseInt(unix) * 1);
            now =  now.toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
            if(now.indexOf("下午") > 0) {
                if (now.length == 18) {
                    var temp1 = now.substring(0, now.indexOf("下午"));   //2014/7/6
                    var temp2 = now.substring(now.indexOf("下午") + 2, now.length);  // 5:17:43
                    var temp3 = temp2.substring(0, 1);    //  5
                    var temp4 = parseInt(temp3); // 5
                    temp4 = 12 + temp4;  // 17
                    var temp5 = temp4 + temp2.substring(1, temp2.length); // 17:17:43
//	                now = temp1 + temp5; // 2014/7/6 17:17:43
//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
                    now = now.replace("-"); //  2014-7-6 17:17:43
                }else {
                    var temp1 = now.substring(0, now.indexOf("下午"));   //2014/7/6
                    var temp2 = now.substring(now.indexOf("下午") + 2, now.length);  // 5:17:43
                    var temp3 = temp2.substring(0, 2);    //  5
                    if (temp3 == 12){
                        temp3 -= 12;
                    }
                    var temp4 = parseInt(temp3); // 5
                    temp4 = 12 + temp4;  // 17
                    var temp5 = temp4 + temp2.substring(2, temp2.length); // 17:17:43
//	                now = temp1 + temp5; // 2014/7/6 17:17:43
//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
                    now = now.replace("-"); //  2014-7-6 17:17:43
                }
            }else {
                var temp1 = now.substring(0,now.indexOf("上午"));   //2014/7/6
                var temp2 = now.substring(now.indexOf("上午")+2,now.length);  // 5:17:43
                var temp3 = temp2.substring(0,1);    //  5
                var index = 1;
                var temp4 = parseInt(temp3); // 5
                if(temp4 == 0 ) {   //  00
                    temp4 = "0"+temp4;
                }else if(temp4 == 1) {  // 10  11  12
                    index = 2;
                    var tempIndex = temp2.substring(1,2);
                    if(tempIndex != ":") {
                        temp4 = temp4 + "" + tempIndex;
                    }else { // 01
                        temp4 = "0"+temp4;
                    }
                }else {  // 02 03 ... 09
                    temp4 = "0"+temp4;
                }
                var temp5 = temp4 + temp2.substring(index,temp2.length); // 07:17:43
//	            now = temp1 + temp5; // 2014/7/6 07:17:43
	            now = now.replace("/","-"); //  2014-7/6 07:17:43
 //               now = now.replace("-"); //  2014-7-6 07:17:43
            }
            return now;
        }
    }
    $(function() {

        var ac = new ACCOUNT();
        ac.init();
    });

    $(function() {

    });

    function getTizstatusTxt(bizstatus) {
        var desc = "";
        $.each(bizStatusDesc, function(i, item) {
            if(bizstatus == item.value){
                desc = item.label;
            }
        });
        return desc;
    }
})(Zepto);

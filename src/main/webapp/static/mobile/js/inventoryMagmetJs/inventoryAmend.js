(function($) {
    var ACCOUNT = function() {
        this.ws = null;
        this.userInfo = GHUTILS.parseUrlParam(window.location.href);
        this.expTipNum = 0;
        this.datagood = [];
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
        console.log(result)
        return result;

    })();

    ACCOUNT.prototype = {
        init: function() {
            this.hrefHtml('.newinput', '.input_div', );
            this.pageInit(); //页面初始化
            this.getData();//获取数据

            GHUTILS.nativeUI.closeWaiting();//关闭等待状态
            //GHUTILS.nativeUI.showWaiting()//开启
        },
        pageInit: function() {
            var _this = this;
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
                    console.log(res)
                    $.each(res, function(i, item) {
                        console.log(item)
                        htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
                    });
                    $('.input_div').html(htmlList)
                }
            });

        },
        ajaxCheckStatus: function() {
            var _this = this;
            var optHtml ='<option value="">全部</option>';
            var htmlStatusAmend = ''
            $.ajax({
                type: 'GET',
                url: '/a/sys/dict/listData',
                data: {type:'biz_req_status'},
                dataType: 'json',
                success: function(res) {
                    console.log(res)
                    $.each(res, function(i, item) {
                        console.log(item)
                        htmlStatusAmend += '<option class="soption" createDate="' + item.createDate + '" description="' + item.description + '" id="' + item.id + '" isNewRecord="' + item.isNewRecord + '"  sort="' + item.sort + '">' + item.label + '</option>'
                    });
                    $('#input_div_Amend').html(optHtml+htmlStatusAmend)
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
                data: {id:735},
                dataType: "json",
                success: function(res){
                    console.log(res)
                    $('#inPoordNum').val(res.data.entity.reqNo)
                    $('#inOrordNum').val(res.data.entity.fromOffice.name)
                    $('#inPototal').val(res.data.entity.totalMoney)
                    $('#inPoRemark').val(res.data.entity.remark)
                    $('#inMoneyReceive').val()
                    $('#inMarginLevel').val()
                    $('#inPoLastDa').val(_this.formatDateTime(res.data.entity.recvEta))

                    /*业务状态*/
                    var bizstatus = res.data.entity.bizStatus;
                    $('#bizStatusShow  option[value="' + bizstatus + '"]').attr("selected",true)
                    _this.commodityHtml(res.data)
                    _this.statusListHtml(res.data)
                    _this.searchSkuHtml(res.data)
                }
            });
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
                $(newinput).val($(this).text())
                $(input_div).hide()
                $('#hideSpanAmend').hide()
            })
        },
        statusListHtml:function(data){
            var _this = this;
            console.log(data)
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
                    '<textarea name="" rows="" cols="" disabled>'+ item.createBy.name +'</textarea>'+
                    '</div>'+
                    '<br />'+
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
            var _this = this;
            console.log(data)
            var htmlCommodity = '';
            $.each(data.reqDetailList, function(i, item) {
                console.log(item)
                htmlCommodity += '<div class="mui-row border-btm5" id="' + item.id + '">' +
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>商品名称:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.name + '" disabled></div></li></div></div>' +
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
                    '<label>工厂价:</label>' +
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
                    '<input type="text" class="mui-input-clear" id="" value="' + item.reqQty + '" disabled></div></li></div></div>' +
                    '<div class="addBtn">' +
                    '<button id="' + item.id +'" type="button" class="removeSkuButton addBtnClass app_btn_search mui-btn-blue mui-btn-block" >删除</button></div>' +
                    '</div>';
            });
            $("#commodityMenu").html(htmlCommodity)
            _this.delItem()
        },
        delItem:function () {
            mui('#commodityMenu').on('tap','.removeSkuButton',function(e){
                var obj = e.detail.target.id;
                if (confirm("此删除不需点保存,即可生效.确认删除此条信息吗？")) {
                    $.ajax({
                        type: "post",
                        url: "/a/biz/request/bizRequestDetail/delItem",
                        data: {id: obj},
                        success: function (data) {
                            if (data == 'ok') {
                                alert("删除成功！");
                                $("#" + obj).remove();
                            }
                        }
                    })
                }
            });
        },
        searchSkuHtml: function(data) {
            var _this = this;
            console.log(data)
            var htmlCommodity = '';
            var count = 1;
            $.each(data.reqDetailList, function(i, item) {
                console.log(item)
                htmlCommodity += '<div class="mui-row border-btm5" id="' + item.id + '">' +
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>商品名称:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.name + '" disabled></div></li></div></div>' +
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
                    '<label>工厂价:</label>' +
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
                    '<input type="text" class="mui-input-clear" id="" value="' + item.reqQty + '" disabled></div></li></div></div>' +
                    '<div class="addBtn" id="addSkuButton_"' + item.id + '>' +
                    '<button id="' + count + '" type="button" class="addSkuButton addBtnClass app_btn_search mui-btn-blue mui-btn-block" >添加</button>' +
                    '</div></div>';
                    count++;
            });
            //$("#searchInfo").html(htmlCommodity)
            _this.addSku()
            _this.removeSku()
        },
        addSku:function () {
            mui('#searchInfo').on('tap','.addSkuButton',function(e){
                var obj = e.detail.target.id;
                $("#addSkuButton_" + obj).html('<button id="' + obj +'" type="button" class="addSkuButton addBtnClass app_btn_search mui-btn-blue mui-btn-block" >移除</button>')
                var divHtml=$("."+obj);
                $("#commodityMenu").append(divHtml);
            });
        },
        removeSku:function () {
            mui('#commodityMenu').on('tap','.addSkuButton',function(e){
                var obj = e.detail.target.id;

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
//	            now = now.replace("/","-"); //  2014-7/6 07:17:43
                now = now.replace("-"); //  2014-7-6 07:17:43
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

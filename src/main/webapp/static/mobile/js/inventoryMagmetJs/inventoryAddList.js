(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.datagood = [];
		this.selectOpen = false
		return this;
	}

    var skuInfoIds="";
    var reqQtys="";
    var reqDetailIds="";
    var LineNos="";

	ACCOUNT.prototype = {
		init: function() {
			this.hrefHtml('.newinput', '.input_div', );
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			
		},
		getData: function() {
			var _this = this;

            _this.searchSkuHtml()

            _this.removeItem()

            _this.saveDetail();

		},
        saveDetail: function () {
            mui('.inSaveBtn').on('tap','#inSaveBtn',function(){
                var skuIds = skuInfoIds.split(",");
                var skuInfoIdsTemp = ""
                for (var i=0; i<skuIds.length; i++){
                    var skuId = skuIds[i];
                    if (skuId != null && skuId != "") {
                        skuInfoIdsTemp += "," + skuId;
                    }
                }
                skuInfoIds = skuInfoIdsTemp.substring(1);


                var skuIds2 = skuInfoIds.split(",");
                for (var j=0; j<skuIds2.length; j++) {
                    var cheId = skuIds2[j];
                    var reqQty = $("#reqQty_" + cheId).val()
                    reqQtys += "," + reqQty;
                }
                reqQtys = reqQtys.substring(1);

                var inOrordNumVal = $("#inOrordNum").val(); //采购中心
                //var inPoLastDaVal = $("#inPoLastDa").val(); //期望收货时间
                var inPoLastDaVal = '2018-07-26 20:30:09'

                var inPoRemarkVal = $("#inPoRemark").val(); //备注
                var bizStatusVal = $("#inputDivAdd")[0].value; //业务状态
                var id = 735; //业务状态

				console.log(inPoLastDaVal)
                console.log(inPoRemarkVal)
                console.log(bizStatusVal)
                console.log(skuInfoIds)
                console.log(reqQtys)
                console.log(reqDetailIds)
                console.log(LineNos)


                $.ajax({
                    type: "post",
                    url: "/a/biz/request/bizRequestHeader/save4Mobile",
                    data: {"id":"", "fromOffice.id": 240, "recvEta":inPoLastDaVal, "remark": inPoRemarkVal, "bizStatus": bizStatusVal, "skuInfoIds": skuInfoIds, "reqQtys": reqQtys, "reqDetailIds":reqDetailIds, "LineNos":LineNos},
                    success: function (data) {
                        if (data == 'ok') {
                            alert("删除成功！");
                            $("#" + obj).remove();
                        }
                    }
                })
            })
        },
        removeItem:function () {
            mui('#commodityMenu').on('tap','.removeSkuButton',function(e){
                var obj = e.detail.target.id;
                var cheId = obj.split("_")[1]
                var cheDiv = $("#serskudiv_" + cheId);
                $("#" + cheId).show();
                $("#batchAddDiv").before(cheDiv)
                $("#removeBtn_" + cheId).remove();
                skuInfoIds = skuInfoIds.replace(cheId, "");
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
                                resultListHtml += '<div class="mui-row border-btm5" id="serskudiv_' + skuInfo.id + '">' +
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
                                    '<label>工厂价:</label>' +
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
                                    '</div></li></div></div></div></div>';
                            });
                            t++;
                            $("#searchInfo").append(resultListHtml);
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
            mui('#searchInfo').on('tap','.addSkuButton',function(){
                $(".skuinfo_check").each(function () {
                    var cheId = $(this)[0].id;

                    var cheFlag = $("#" + cheId).is(':checked');
                    if (cheFlag == true) {
                        var cheDiv = $("#serskudiv_" + cheId);
                        $("#" + cheId).prop('checked',false);
                        $("#" + cheId).hide();
                        $("#commodityMenu").append(cheDiv)

                        var removeButtonHtml = '<div class="addBtn" id="removeBtn_' + cheId + '">' +
                            '<button id="remove_' + cheId +'" type="submit" class="removeSkuButton addBtnClass app_btn_search mui-btn-blue mui-btn-block">移除' +
                            '</button></div>';
                        $("#commodityMenu").append(removeButtonHtml)
                        skuInfoIds += cheId + ",";
                    }
                })
            });
        },
		hrefHtml: function(newinput, input_div) {
			var _this = this;
			_this.ajaxGoodList()
			_this.ajaxCheckStatus()

			$(newinput).on('focus', function() {
				//$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$('#hideSpanAdd').show()
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				
				_this.rendHtml(_this.datagood,$(this).val())
			})
			
			$('#hideSpanAdd').on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$('#hideSpanAdd').hide()
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
				$(newinput).val($(this).text())
				$(input_div).hide()
				$('#hideSpanAdd').hide()
				_this.selectOpen = true
			})
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
				console.log(item)
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
			var optHtml ='<option value="">请选择</option>';
			var htmlStatusAdd = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'biz_req_status'},
				dataType: 'json',
				success: function(res) {
					console.log(res)
					$.each(res, function(i, item) {
						console.log(item)
						htmlStatusAdd += '<option class="soption" createDate="' + item.createDate + '" description="' + item.description + '" id="' + item.id + '" isNewRecord="' + item.isNewRecord + '"  sort="' + item.sort + '" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#inputDivAdd').html(optHtml+htmlStatusAdd)
					_this.getData()
				}
			});
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
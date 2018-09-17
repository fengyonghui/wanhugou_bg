(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.datagood = [];
		this.dataSupplier = [];
		this.selectOpen = false;
        this.skuInfoIds="";
        this.reqQtys="";
        this.fromOfficeId="";
        this.bizOfficeId="";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.hrefHtml('.newinput01', '.input_div01','#hideSpanAdd01');
			this.hrefHtmls('.newinput02', '.input_div02','#hideSpanAdd02');
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			_this.ajaxCheckStatus();
			_this.saveDetail();
		},
		getData: function() {
			var _this = this;
            _this.removeItem();
		},
        saveDetail: function () {
            var _this = this;
            mui('.inSaveBtn').on('tap','#inSaveBtn',function(){
                var skuIds = _this.skuInfoIds.split(",");
                var skuInfoIdsTemp = ""
                for (var i=0; i<skuIds.length; i++){
                    var skuId = skuIds[i];
                    if (skuId != null && skuId != "") {
                        skuInfoIdsTemp += "," + skuId;
                    }
                }
                _this.skuInfoIds = skuInfoIdsTemp.substring(1);

                var skuIds2 = _this.skuInfoIds.split(",");

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
                _this.reqQtys = reqQtysTemp.substring(1);

                var inOrordNumVal = $("#inOrordNum").val(); //采购中心
                var inPoLastDaVal = $("#inPoLastDa").val(); //期望收货时间
                //console.log("inPoLastDaVal=" + inPoLastDaVal)

                var inPoRemarkVal = $("#inPoRemark").val(); //备注
                var bizStatusVal = $("#inputDivAdd")[0].value; //业务状态

                if(_this.fromOfficeId == null || _this.fromOfficeId == ""){
				    mui.toast("请选择采购中心！")
                    return;
                }
                if(_this.bizOfficeId == null || _this.bizOfficeId == ""){
				    mui.toast("请选择供应商！")
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
                //选择备货方：
                var r2 = document.getElementsByName("localOriginType");
	            var localOriginType = "";
	            for (var i = 0; i < r2.length; i++) {
	                if (r2[i].checked == true) {
	                    localOriginType = r2[i].value;
	                }
	            }
	            console.log(localOriginType)
                $.ajax({
                    type: "post",
                    url: "/a/biz/request/bizRequestHeaderForVendor/saveForMobile",
                    dataType: 'json',
                    data: {
                    	"fromOffice.id": _this.fromOfficeId, //采购中心 id
                    	'fromOffice.name': _this.fromOfficeName,//采购中心名称
                    	'fromOffice.type': _this.fromOfficeType,//采购中心机构类型1：公司；2：部门；3：小组
                    	'bizVendInfo.office.id ': _this.bizOfficeId,//供应商 id
                    	'bizVendInfo.office.name': _this.bizOfficeName,//供应商名称
                    	'bizVendInfo.office.type': _this.bizOfficeType,//供应商所在机构类型
                    	fromType: localOriginType, //备货方
                    	recvEta: inPoLastDaVal, //期望收货时间
                    	remark: inPoRemarkVal, //备注信息
                    	bizStatus: bizStatusVal, //业务状态
                    	skuInfoIds: _this.skuInfoIds, //要添加的商品 id
                    	reqQtys: _this.reqQtys //申报数量
                    },
                    success: function (resule) {
//                  	console.log(resule)
                        if (resule == true) {
                            mui.toast("添加备货单成功！");
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
        removeItem:function () {
            var _this = this;
            mui('#commodityMenu').on('tap','.removeSkuButton',function(e){
                var obj = e.detail.target.id;
                var cheId = obj.split("_")[1]
                var cheDiv = $("#serskudiv_" + cheId);
                $("#" + cheId).show();
                $("#batchAddDiv").before(cheDiv)
                $("#removeBtn_" + cheId).remove();
                _this.skuInfoIds = _this.skuInfoIds.replace(cheId, "");
            });
        },
        searchSkuHtml: function(Id) {
            var _this = this;
            mui('#inAmendPoLastDaDiv').on('tap','#comChoiceBtn',function(){
//          	console.log(_this.bizOfficeId)
            	if(_this.bizOfficeId == '' || !$('#inSupply').val()) {
            		mui.toast("请选择供应商！");
            		return;
            	}
                var itemNo = $("#inAmendPoLastDa").val();
                if(itemNo == null||itemNo == undefined){
					itemNo == "";
                }
                if(itemNo == ""){
                	mui.toast("请输入查询商品的货号！");
                	return;
                }
                $.ajax({
                    type: "post",
                    url: "/a/biz/sku/bizSkuInfo/findSkuList",
                    data: {
                    	itemNo: itemNo,
//                  	'bizVendInfo.office.id ': _this.bizOfficeId//供应商 id
                	},
                    success: function (result) {
//                  	console.log(result)
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
//                          	console.log(skuInfo)
                                //skuInfoId+=","+skuInfo.id;
                                if($("#commodityMenu").children("#serskudiv_"+skuInfo.id).length>0){
                                    return;
                                }
                                resultListHtml += '<div class="mui-row app_bline" id="serskudiv_' + skuInfo.id + '">' +
                                    '<div class="mui-row mui-checkbox mui-left">' +
                                    '<input style="top: 61px;" name="" class="skuinfo_check" id="' + skuInfo.id + '" type="checkbox"></div>' +
                                    '<div class="mui-row">' +
                                    
                                    '<div class="mui-row lineStyle">' +
                                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                                    '<li class="mui-table-view-cell app_bline3">' +
                                    '<div class="mui-input-row ">' +
                                    '<label class="">商品名称:</label>' +
                                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.name + '" disabled>' +
                                    '</div></li></div></div>' +
                                   
                                  	'<div class="mui-row lineStyle">' +
                                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                                    '<li class="mui-table-view-cell app_bline3">' +
                                    '<div class="mui-input-row ">' +
                                    '<label class="">商品货号:</label>' +
                                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.itemNo + '" disabled>' +
                                    '</div></li></div></div>' +
                                    
                                    '<div class="mui-row lineStyle">' +
                                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                                    '<li class="mui-table-view-cell app_bline3">' +
                                    '<div class="mui-input-row ">' +
                                    '<label class="">商品编码:</label>' +
                                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.partNo + '" disabled>' +
                                    '</div></li></div></div>' +
                                    
                                    '<div class="mui-row">' +
                                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                                    '<li class="mui-table-view-cell app_bline3">' +
                                    '<div class="mui-input-row ">' +
                                    '<label>品牌名称:</label>' +
                                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.productInfo.brandName +'" disabled>' +
                                    '</div></li></div>' +
                                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                                    '<li class="mui-table-view-cell app_bline3">' +
                                    '<div class="mui-input-row ">' +
                                    '<label>供应商:</label>' +
                                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.productInfo.brandName + '" disabled>' +
                                    '</div></li></div></div>' +
                                   
                                    '<div class="mui-row  inAddFont">' +
                                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                                    '<li class="mui-table-view-cell app_bline3">' +
                                    '<div class="mui-input-row">' +
                                    '<label>结算价:</label>' +
                                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.buyPrice + '" disabled></div></li></div>' +
                                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                                    '<li class="mui-table-view-cell app_bline3">' +
                                    '<div class="mui-input-row">' +
                                    '<label>申报数量:</label>' +
                                    '<input type="hidden" class="mui-input-clear" value="' + skuInfo.id + '">' +
                                    '<input type="text" class="mui-input-clear" placeholder="请输入数量" id="reqQty_'+ skuInfo.id +'">' +
                                    '<font>*</font>'+
                                    '</div></li></div></div></div></div>';
                            });
                            t++;
                            $("#searchInfo").append(resultListHtml);
                        })
                        var addButtonHtml = '<div class="inAddBtnParent" id="batchAddDiv">' +
                            '<button id="batchAdd" type="submit" class="addSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">添加' +
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
                        var removeButtonHtml = '<div class="inAddBtnParent" id="removeBtn_' + cheId + '">' +
                            '<button id="remove_' + cheId +'" type="submit" class="removeSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">移除' +
                            '</button></div>';
                            cheDiv.append(removeButtonHtml)
                        $("#commodityMenu").append(cheDiv)
                        _this.skuInfoIds += cheId + ",";
                    }
                })
            });
        },
		hrefHtml: function(newinput, input_div,hideSpanAdd) {
			var _this = this;
			_this.ajaxGoodList()
			$(newinput).on('focus', function() {
				//$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$(hideSpanAdd).show();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				_this.rendHtml(_this.datagood,$(this).val())
			})
			$(hideSpanAdd).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$(hideSpanAdd).hide()
			})
			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
                _this.fromOfficeId = $(this).attr("id");
                _this.fromOfficeName = $(this).attr("name");
                _this.fromOfficeType = $(this).attr("type");
				$(newinput).val($(this).text())
				$(input_div).hide()
				$(hideSpanAdd).hide()
				_this.selectOpen = true
			})
		},
		hrefHtmls: function(newinput, input_div,hideSpanAdd) {
			var _this = this;
			_this.ajaxSupplier()
			$(newinput).on('focus', function() {
				//$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$(hideSpanAdd).show();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				_this.rendHtmls(_this.dataSupplier,$(this).val())
				if($(this).val() == '') {
            		$('#inSupplierNum').parent().hide();
					$('#inSupplierName').parent().hide();
					$('#inSupplierBank').parent().hide();
            	}
			})
			$(hideSpanAdd).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$(hideSpanAdd).hide()
			})
			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
                _this.bizOfficeId = $(this).attr("id");
                _this.bizOfficeName = $(this).attr("name");
                _this.bizOfficeType = $(this).attr("type");
				$(newinput).val($(this).text())
				$(input_div).hide()
				$(hideSpanAdd).hide()
				_this.selectOpen = true
				_this.supplier(_this.bizOfficeId)
			})
			_this.searchSkuHtml(_this.bizOfficeId)
		},
		//供应商信息
		supplier:function(supplierId){						
			$.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeaderForVendor/selectVendInfo",
                data: {vendorId:supplierId},		                
                dataType: "json",
                success: function(rest){
//              	console.log(rest)
                	if(rest) {
                		if(rest.cardNumber) {
	                		$('#inSupplierNum').parent().show();
	                		$('#inSupplierNum').val(rest.cardNumber);//供应商卡号
	                	}else {
	                		$('#inSupplierNum').parent().hide();
	                	}
	                	if(rest.payee) {
	                		$('#inSupplierName').parent().show();
	                		$('#inSupplierName').val(rest.payee);//收款人
	                	}else {
	                		$('#inSupplierName').parent().hide();
	                	}
	                	if(rest.bankName) {
							$('#inSupplierBank').parent().show();
							$('#inSupplierBank').val(rest.bankName);//开户行
	                	}else {
	                		$('#inSupplierBank').parent().hide();
	                	}
                	}else {
                		$('#inSupplierNum').parent().hide();
						$('#inSupplierName').parent().hide();
						$('#inSupplierBank').parent().hide();
                	}
            	}
			});
		},
		rendHtml: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList='';
			$.each(data, function(i, item) {
				if(item.name.indexOf(key) > -1) {
					reult.push(item)

				}
			})
			$.each(reult, function(i, item) {
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
			});
			$('.input_div01').html(htmlList);

		},
		rendHtmls: function(data, key) {
			var _this = this;
			var reults = [];
			var htmlLists=''
			$.each(data, function(i, item) {
				if(item.name.indexOf(key) > -1) {
					reults.push(item)
				}
			})
			$.each(reults, function(i, item) {
				htmlLists += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
			});
			$('.input_div02').html(htmlLists);

		},
		ajaxGoodList: function() {
			var _this = this;
			var htmlList = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeListByPhone',
				data: {
					type: 8,
					source:'officeConnIndex'
				},
				dataType: 'json',
				success: function(res) {
//					console.log(res)
					_this.datagood = res
					$.each(res, function(i, item) {
						htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
					});
					$('.input_div01').html(htmlList)
					_this.getData()
				}
			});
		},
		ajaxSupplier: function() {
			var _this = this;
			var htmlSupplier = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeList',
				data: {
					type: 7
				},
				dataType: 'json',
				success: function(res) {
					_this.dataSupplier = res
					$.each(res, function(i, item) {
						htmlSupplier += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
					});
					$('.input_div02').html(htmlSupplier)
					_this.getData()
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
					$.each(res, function(i, item) {
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
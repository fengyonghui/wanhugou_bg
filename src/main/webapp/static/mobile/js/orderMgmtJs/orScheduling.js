(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.fs == false;
		this.outSaveFlag = "false"
		this.inSsaveFlag = "false"
		
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//biz:po:bizPoHeader:addScheduling		biz:po:bizPoHeader:saveScheduling	保存、批量保存	
			this.getPermissionList('biz:po:bizPoHeader:addScheduling','outSaveFlag')	
			this.getPermissionList('biz:po:bizPoHeader:saveScheduling','inSsaveFlag')	
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			_this.getData();
		},
		getPermissionList: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.outSaveFlag = res.data;
					_this.inSsaveFlag = res.data;
                    console.log(_this.outSaveFlag)
					console.log(_this.inSsaveFlag)
                }
            });
        },
        ajaxNum: function() {
        	var _this = this;
        	 $.ajax({
                type: "GET",
                url: "/a/biz/po/bizPoHeader/checkSchedulingNum",
                dataType: "json",
                data: {id: _this.userInfo.staOrdId},
                async:false,
                success: function(mm){
                	console.log(mm)
                	$('#purchOrdQty').val(mm.totalOrdQty);
                	console.log(mm.totalSchedulingHeaderNum)
                	if(mm.totalSchedulingHeaderNum != null) {
                		$('#purchWaiteNum').val(mm.totalOrdQty - mm.totalSchedulingHeaderNum);
                		$('#toalSchedulingNum').val(mm.totalSchedulingHeaderNum);
                	}else{
                		$('#purchWaiteNum').val(mm.totalOrdQty);
                		$('#toalSchedulingNum').val('0');
                	}
        	    }
            });
        },
		getData: function() {
			var _this = this;
			$.ajax({
				type:"get",
				url:"/a/biz/po/bizPoHeader/scheduling4Mobile",
                data:{id: _this.userInfo.staOrdId},
                dataType: "json",
                success: function(res){
                	console.log(res)
	              	$.each(res.data.bizPoHeader.orderNumMap, function(a, c) {
	              		$.each(c, function(v, n) {
	              			$('#schedOrdNum').val(n.orderNumStr);
	              		})
	              	})
                	var poDetailList = res.data.bizPoHeader.poDetailList;
                	if(poDetailList.length == 0) {
		                $('#saveBtnPt').hide();
		            } else {
		                _this.ajaxNum();
		            }
                	var htmlPurch = '';
					var htmlSave = '';
                	$.each(poDetailList, function(i,item) {
                		
					htmlPurch +='<li class="mui-table-view-cell mui-media app_bline app_pr">'+
//		产品图片
					'<div class="photoParent mui-pull-left app_pa">'+
						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'"></div>'+
//		产品信息
					'<div class="mui-media-body app_w80p app_fr">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.brandName +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuInfo.namee +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>采购数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.ordQty +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>结算价：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>总金额：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.ordQty * item.unitPrice +'" disabled>'+
						'</div></div></li>'
					htmlSave = '<button id="saveBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">保存</button>'
					});
					$("#orSchedPurch").html(htmlPurch)
            		$(".saveBtnPt").html(htmlSave)
                	_this.showContent(res);
//              	if(_this.outSaveFlag == true) {
//	                	if(_this.inSsaveFlag == true) {
	                		_this.saveSchedul(res);
//	                	}
//	                }
                }
			});	
			_this.schedulPlan();
		
		},
		showContent: function(data) {
			var _this = this;
			console.log(data)
			if(data.data.detailHeaderFlg == true) {
				_this.purchContent(data);
				$(".inputRadio").attr("disabled", true);
			}
			if(data.data.detailSchedulingFlg == true) {
				_this.commdContent(data);
				$(".inputRadio").attr("disabled", true);
			}
			if(data.data.detailHeaderFlg == false && data.data.detailSchedulingFlg == false) {
				$('.schedCommd').hide();
				_this.btnshow(data);
			}
		},
		purchContent: function(a) {
			var _this = this;
			console.log(a)
			if(a.data.bizPoHeader.poSchType == 0) {
				$('#chedulingStatus').val('未排产');
			}
			if(a.data.bizPoHeader.poSchType == 1) {
				$('#chedulingStatus').val('排产中');
			}
			if(a.data.bizPoHeader.poSchType == 2) {
				$('#chedulingStatus').val('排产完成');
				$('#saveBtnPt').hide();
			}
			var htmlPurch = '';
			var htmlSave = '';
			$.each(a.data.bizPoHeader.poDetailList, function(i,item) {
			htmlPurch +='<li class="mui-table-view-cell mui-media app_bline app_pr">'+
//		产品图片
			'<div class="photoParent mui-pull-left app_pa">'+
				'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'"></div>'+
//		产品信息
			'<div class="mui-media-body app_w80p app_fr">'+
				'<div class="mui-input-row">'+
					'<label>品牌名称：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.brandName +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品名称：</label>'+
					'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuInfo.namee +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品货号：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>采购数量：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.ordQty +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>结算价：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>总金额：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.ordQty * item.unitPrice +'" disabled>'+
				'</div></div></li>'
			htmlSave = '<button id="saveBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">保存</button>'
			});
    		$("#orSchedPurch").html(htmlPurch)
    		$(".saveBtnPt").html(htmlSave)
		},
		commdContent: function(b) {
			var _this = this;
			console.log(b)
			var chedulingStatus = '';
			if(a.data.bizPoHeader.poSchType == 0) {
				chedulingStatus = '未排产'
			}
			if(a.data.bizPoHeader.poSchType == 1) {
				chedulingStatus = '排产中'
			}
			if(a.data.bizPoHeader.poSchType == 2) {
				chedulingStatus = '排产完成'
				$('#saveBtnPt').hide();
			}
			var htmlCommodity = '';
			var htmlAllSave = '';
			$.each(b.data.bizPoHeader.poDetailList, function(i,item) {
				var waiteNum = item.ordQty - item.sumCompleteNum;
				htmlCommodity += '<li class="mui-table-view-cell app_bline2">'+
				'<div class="mui-input-row inComdty inDetailComdty app_pall11_15">'+
	//							<!--产品图片-->
					'<div class="photoParent mui-pull-left app_pa">'+
						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'"></div>'+
	//							<!--产品信息-->
					'<div class="mui-media-body app_w80p app_fr">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" class="" value="'+ item.skuInfo.productInfo.brandName +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" class="app_color40 " value="'+ item.skuInfo.namee +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>采购数量：</label>'+
							'<input type="text" class="" value="'+ item.ordQty +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>结算价：</label>'+
							'<input type="text" class="" value="'+ item.unitPrice +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>总金额：</label>'+
							'<input type="text" class="" value="'+ item.ordQty * item.unitPrice +'" disabled></div></div></div>'+
					'<div class="mui-row app_f13 app_bline">'+
						'<div class="mui-input-row">'+
							'<label>总申报数量：</label>'+
							'<input type="text" value="'+ item.ordQty +'" id="totalOrdQtyForSku_'+ item.id+'" class="commdOrdQty" sidabled></div>'+
						'<div class="mui-input-row">'+
							'<label>总待排产量：</label>'+
							'<input type="text" value="'+ waiteNum +'" class="commdWaiteNum"></div>'+	
						'<div class="mui-input-row">'+
							'<label>已排产数量：</label>'+
							'<input type="text" name="toalSchedulingNumForSku" value="'+ item.sumCompleteNum +'" class="commdCompleteNum" sidabled></div>'+
						'<div class="mui-input-row">'+
							'<label>当前状态：</label>'+
							'<input type="text" value="'+ chedulingStatus +'" class="commdCompleteNum app_cred3" sidabled></div>'+
						'<button type="submit" class="commdAddBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">添加排产计划</button>'+
						'<button type="submit" class="singleAddBtn inAddBtn app_btn_search mui-btn-blue mui-btn-block">保存</button></div>'+
					'<div class="mui-row plan">'+
						'<div class="labelLf">排产计划：</div>'+
						'<div class="mui-row app_f13 commdAddPlan" id="'+ item.id+'">'+
							'<div class="mui-row app_bline commdPlan" name="'+ item.id +'">'+
								'<div class="mui-input-row">'+
									'<label>完成日期：</label>'+
									'<input type="date" name="'+ item.id +'_date" class="commdDate"></div>'+
								'<div class="mui-input-row">'+
									'<label>排产数量：</label>'+
									'<input type="text" name="'+ item.id +'_value" class="commdNum mui-input-clear"></div></div>'+	
								'</div></div></div></li>'
				htmlAllSave = '<button id="allSaveBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">批量保存</button>'			
			});
    		$("#orSchedCommd").html(htmlCommodity)
    		$(".saveBtnPt").html(htmlAllSave)
		},
		btnshow: function(data) {
			var _this = this;
			$('#chedulingStatus').val('未排产');
			$('input[type=radio]').on('change', function() {
				if(this.checked && this.value == 0) {
					$('.schedPurch').show();
					$('.schedCommd').hide();
					_this.purchContent(data);
				}
				if(this.checked && this.value == 1) {
					$('.schedPurch').hide();
					$('.schedCommd').show();
					_this.commdContent(data);
				}
			})
		},
		schedulPlan: function() {
			var _this = this;
			var htmlPurchPlan = '<div class="mui-row app_bline purchAddCont">'+
				'<div class="mui-input-row">'+
					'<label>完成日期：</label>'+
					'<input type="date" class="addpurchDate"></div>'+
				'<div class="mui-input-row">'+
					'<label>排产数量：</label>'+
					'<input type="text" class="addpurchNum mui-input-clear"></div>'+
				'<button type="submit" class="removeBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">删除</button>'+
			'</div>';
			var htmlcommdPlan = '<div class="mui-row app_bline commdAddCont">'+
				'<div class="mui-input-row">'+
					'<label>完成日期：</label>'+
					'<input type="date" name="" class="addCommdDate"></div>'+
				'<div class="mui-input-row">'+
					'<label>排产数量：</label>'+
					'<input type="text" name="" class="addCommdNum mui-input-clear"></div>'+
				'<button type="submit" class="removeBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">删除</button>'+
			'</div>';
			
			$(".schedPurch").on("tap", "#purchAddBtn", function() {
				$('#purchAddCont').append(htmlPurchPlan);
				var addPurchNum = _this.userInfo.staOrdId;
				$('.purchAddCont').attr('name', addPurchNum);
				$('#purchPlan').attr('name', addPurchNum);
				$('.addpurchDate').attr('name', addPurchNum + '_date');
				$('.addpurchNum').attr('name', addPurchNum + '_value');
				$('#purchDate').attr('name', addPurchNum + '_date');
				$('#purchNum').attr('name', addPurchNum + '_value');
			})
			$(".schedCommd").on("tap", ".commdAddBtn", function() {
				$(this).parent('.app_f13').next('.plan').find('.commdAddPlan').append(htmlcommdPlan);
				var commdDateName = ($('.commdDate').attr('name'));
				var commdNumName = ($('.commdNum').attr('name'));
				var commdPlanName = ($('.commdPlan').attr('name'));
				$('.addCommdDate').attr('name', commdDateName);
				$('.addCommdNum').attr('name', commdNumName);
				$('.commdAddCont').attr('name', commdPlanName);
			})
			_this.removeSchedul();
		},
		removeSchedul: function() {
			var _this = this;
			$('.mui-content').on('tap', '.removeBtn', function() {
				$(this.parentNode).remove();
			})
		},
		saveSchedul: function(m) {
			var _this = this;
			$('.inSaveBtn').on('tap', '#saveBtn', function() {
				var schedulOneId = _this.userInfo.staOrdId;
				_this.saveComplete(0, schedulOneId)
			});
			$('.inSaveBtn').on('tap', '#allSaveBtn', function() {
				_this.batchSave(m)
			});
			$('.schedCommd').on('tap', '.singleAddBtn', function() {
				var commdPurchId = $('.commdAddPlan').attr('id');
				console.log(commdPurchId)
				_this.saveComplete(1, commdPurchId)
			});
	},
//	"saveComplete('1',${poDetail.id})      saveComplete('0',${bizPoHeader.id})
//单个保存
	saveComplete:function(schedulingType,id){
		var _this = this;
        var trArray = $("[name='" + id + "']");
        var params = new Array();
        var schRemark = "";
        schRemark = $("#orRemark").val();
        if (schedulingType == "0"){
            var originalNum = $("#purchOrdQty").val();
        } else {
            var originalNum = $(eval("totalOrdQtyForSku_" + id)).val();
        }
        var totalSchedulingNum = 0;
        var totalSchedulingHeaderNum = 0;
        var totalSchedulingDetailNum = 0;
        var poSchType = 0;
        for(i=0;i<trArray.length;i++){
            var div = trArray[i];
            var jqDiv = $(div);
            var value = jqDiv.find("[name='" + id + "_value']").val();

            if (schedulingType == "0"){
                totalSchedulingHeaderNum = parseInt(totalSchedulingHeaderNum) + parseInt(value);
            } else {
                totalSchedulingDetailNum = parseInt(totalSchedulingDetailNum) + parseInt(value);
            }
        }

        var totalTotalSchedulingNum = 0;
        if (schedulingType == "0"){
            var toalSchedulingNum = $('#toalSchedulingNum').val();
            poSchType = originalNum >  parseInt(totalSchedulingHeaderNum) + parseInt(toalSchedulingNum)  ? 1 : 2;
            totalTotalSchedulingNum = parseInt(totalSchedulingHeaderNum) + parseInt(toalSchedulingNum);
        } else {
            var toalSchedulingNumForSku = $('#toalSchedulingNumForSku').val();
            poSchType = originalNum > parseInt(totalSchedulingDetailNum) + parseInt(toalSchedulingNumForSku) ? 1 : 2;
            totalTotalSchedulingNum = parseInt(totalSchedulingDetailNum) + parseInt(toalSchedulingNumForSku);
        }
        console.log("originalNum=" + originalNum);
        console.log("totalTotalSchedulingNum=" + totalTotalSchedulingNum);

        if(parseInt(totalTotalSchedulingNum) > parseInt(originalNum)) {
            alert("排产量总和太大，请从新输入!")
            return false
        }

        for(i=0;i<trArray.length;i++){
            var div = trArray[i];
            var jqDiv = $(div);
            var date = jqDiv.find("[name='" + id + "_date']").val();
            var value = jqDiv.find("[name='" + id + "_value']").val();
            if(date == null || date == ""){
                alert("完成日期不能为空!")
                return false;
            }
            var reg= /^[0-9]+[0-9]*]*$/;
            if(value == null || value == "" || parseInt(value)<=0 || parseInt(value) > originalNum || !reg.test(value)){
                alert("确认值输入不正确!")
                return false;
            }
            var entity = {};
            entity.id = id;
            entity.objectId = id;
            entity.originalNum = originalNum;
            entity.schedulingNum = value;
            entity.planDate= date + ' 00:00:00';
            entity.schedulingType = schedulingType;
            entity.remark = schRemark;
            entity.poSchType = poSchType;

            if (schedulingType == "0"){
                totalSchedulingHeaderNum = parseInt(totalSchedulingHeaderNum) + parseInt(value);
            } else {
                totalSchedulingDetailNum = parseInt(totalSchedulingDetailNum) + parseInt(value);
            }

            params[i]=entity;

            totalSchedulingNum = parseInt(totalSchedulingNum) + parseInt(value);
        }
        console.log(params)
//          return
        if(confirm("确定执行该排产确认吗？")) {
//              $Mask.AddLogo("正在加载");
            $.ajax({
                url: '/a/biz/po/bizPoHeader/saveSchedulingPlan',
                contentType: 'application/json',
                data:JSON.stringify(params),
                datatype:"json",
                type: 'post',
                success: function (result) {
                	console.log(result)
                    if(result == true) {
                       GHUTILS.OPENPAGE({
							url: "../../html/orderMgmtHtml/orderpaymentinfo.html",
							extras: {
								
							}
						})
                    }
                },
                error: function (error) {
                    console.info(error);
                }
            });
        }
    },
	batchSave: function (m) {
			var _this = this;
			var reqDetailIdList = m.data.poDetailIdListJson;
//          var reqDetailIdList = JSON.parse('${poDetailIdListJson}');
            var params = new Array();
            var totalSchedulingNum = 0;
            var totalOriginalNum = 0;
            var toalSchedulingNumForSkuDetailNum = 0;
            var count = 1
            var ind = 0;
            var schRemark = "";
            schRemark = $("#orRemark").val();

            var totalSchedulingHeaderNum = 0;
            var totalSchedulingDetailNum = 0;
            var poSchType = 0;

            for(var index in reqDetailIdList) {
                var reqDetailId = reqDetailIdList[index];

                var originalNum = $(eval("totalOrdQtyForSku_" + reqDetailId)).val();
                totalOriginalNum += parseInt(totalOriginalNum) + parseInt(originalNum);
            }

            var toalSchedulingNumForSkuHtml = $("[name=toalSchedulingNumForSku]");
            var toalSchedulingNumForSkuNum = 0;
            for(i=0;i<toalSchedulingNumForSkuHtml.length;i++){
                var schedulingNumForSkuNum = toalSchedulingNumForSkuHtml[i];
                var scForSkuNum = $(schedulingNumForSkuNum).attr("value")
                toalSchedulingNumForSkuNum = parseInt(toalSchedulingNumForSkuNum) + parseInt(scForSkuNum);
            }

            for(var index in reqDetailIdList) {
                var reqDetailId = reqDetailIdList[index];
                var trArray = $("[name='" + reqDetailId + "']");
                for(i=0;i<trArray.length;i++) {
                    var div = trArray[i];
                    var jqDiv = $(div);
                    var value = jqDiv.find("[name='" + reqDetailId + "_value']").val();
                    totalSchedulingDetailNum = parseInt(totalSchedulingDetailNum) + parseInt(value);
                }
            }
            poSchType = totalOriginalNum > parseInt(totalSchedulingDetailNum) + parseInt(toalSchedulingNumForSkuNum) ? 1 : 2;
	console.log(poSchType)
//return;
            for(var index in reqDetailIdList) {
//          	alert(999)
                var reqDetailId = reqDetailIdList[index];
                var trArray = $("[name='" + reqDetailId + "']");
                
                 console.log(trArray)
       			 console.log(trArray.length)
                for(i=0;i<trArray.length;i++) {
                    var div = trArray[i];
                    var jqDiv = $(div);
                    var date = jqDiv.find("[name='" + reqDetailId + "_date']").val();
                    var value = jqDiv.find("[name='" + reqDetailId + "_value']").val();
                    if (date == null || date == "") {
                        alert("第" + count + "个商品完成日期不能为空!")
                        return false;
                    }
                    var reg = /^[0-9]+[0-9]*]*$/;
                    if (value == null || value == "" || parseInt(value) <= 0 || parseInt(value) > originalNum || !reg.test(value)) {
                        alert("第" + count + "个商品确认值输入不正确!")
                        return false;
                    }
                    var entity = {};
                    entity.id = _this.userInfo.staOrdId;
                    entity.objectId = reqDetailId;
                    entity.originalNum = originalNum;
                    entity.schedulingNum = value;
                    entity.planDate = date + ' 00:00:00';
                    entity.schedulingType = 1;
                    entity.remark = schRemark;
                    entity.poSchType = poSchType;

                    params[ind]=entity;
                    totalSchedulingNum = parseInt(totalSchedulingNum) + parseInt(value);
                    ind++;
                }
                count++;
            }
            console.log(params)
            if(parseInt(totalSchedulingNum) > parseInt(totalOriginalNum)) {
                alert("排产量总和太大，请从新输入!")
                return false
            }
//return
            if(confirm("确定执行该排产确认吗？")) {
//              $Mask.AddLogo("正在加载");
                $.ajax({
                    url: '/a/biz/po/bizPoHeader/saveSchedulingPlan',
                    contentType: 'application/json',
                    data:JSON.stringify(params),
                    datatype:"json",
                    type: 'post',
                    success: function (result) {
                        if(result == true) {
                            GHUTILS.OPENPAGE({
								url: "../../html/orderMgmtHtml/orderpaymentinfo.html",
								extras: {
									
								}
							})
                        }
                    },
                    error: function (error) {
                        console.info(error);
                    }
                });
            }
        }
	}
	$(function() {
		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
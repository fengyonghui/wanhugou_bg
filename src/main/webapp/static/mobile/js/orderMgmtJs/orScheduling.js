(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.fs == false;
		this.outSaveFlag = "false"
		this.inSsaveFlag = "false"
		this.htmlcommdPlans = '';
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
                	$('#purchOrdQty').val(mm.totalOrdQty);
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
                	var remarkTxt = '';
                	if(res.data.bizPoHeader.bizSchedulingPlan.remark) {
                		remarkTxt = res.data.bizPoHeader.bizSchedulingPlan.remark
                	}else {
                		remarkTxt = ''
                	}
                	$('#orRemark').val(remarkTxt);
	              	$.each(res.data.bizPoHeader.orderNumMap, function(a, c) {
	              		$.each(c, function(v, n) {
	              			$('#schedOrdNum').val(n.orderNumStr);
	              		})
	              	})
                	var poDetailList = res.data.bizPoHeader.poDetailList;
                	if(poDetailList.length == 0) {
		                $('.saveBtnPt').hide();
		            } else {
		                _this.ajaxNum();
		            }
		            if(res.data.bizPoHeader.poSchType != 0) {
		            	$('.schedPurch').hide();
		            }else {
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
								'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuInfo.name +'" disabled></div>'+
							'<div class="mui-input-row">'+
								'<label>商品货号：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
							'<div class="mui-input-row">'+
								'<label>采购数量：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.ordQty +'" disabled></div>'+
//								隐藏结算价
//							'<div class="mui-input-row">'+
//								'<label>结算价：</label>'+
//								'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled></div>'+
							'<div class="mui-input-row">'+
								'<label>总金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.ordQty * item.unitPrice +'" disabled>'+
							'</div></div></li>'
						htmlSave = '<button id="saveBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">保存</button>'
						});
						$("#orSchedPurch").html(htmlPurch)
	            		$(".saveBtnPt").html(htmlSave)
		            }
                	_this.showContent(res);
                }
			});	
			_this.schedulPlan();
		},
		showContent: function(data) {
			var _this = this;
//			console.log(data)
			if(data.data.detailHeaderFlg == true) {
				$('#schedPlan1').attr('checked', 'checked');
				$('#schedPlan2').removeAttr('checked');
				$('.schedCommd').hide();
				_this.purchContent(data);
				$(".inputRadio").attr("disabled", true);
			}
			if(data.data.detailSchedulingFlg == true) {
				$('#schedPlan2').attr('checked', 'checked');
				$('#schedPlan1').removeAttr('checked');
				$('.schedPurch').hide();
				_this.commdContent(data);
				$(".inputRadio").attr("disabled", true);
			}
			if(data.data.detailHeaderFlg == false && data.data.detailSchedulingFlg == false) {
				if($('#purchOrdQty').val() == 0) {
					$('#chedulingStatus').val('采购商品无申报数量！');
					$(".inputRadio").attr("disabled", true);
					$('#purchAddBtn').hide();
					$('.saveBtnPt').hide();
					$('#purchSchedRecord').parent().hide();
					$('#purchAddCont').parent().hide();
				}else {
					$('#chedulingStatus').val('未排产');
					_this.btnshow(data);
				}
				$('.schedCommd').hide();
			}
			_this.saveSchedul(data);
		},
		purchContent: function(a) {
			var _this = this;
			$('.schedPurch').show();
			if(a.data.bizPoHeader.poSchType == 1) {
				$('#chedulingStatus').val('排产中');
			}
			if(a.data.bizPoHeader.poSchType == 1 || a.data.bizPoHeader.poSchType == 2) {
				var htmlPurchPlans = '';
				$.each(a.data.bizCompletePalns, function(d, h) {
					htmlPurchPlans += '<div class="mui-row app_bline">'+
						'<div class="mui-input-row">'+
							'<label>完成日期：</label>'+
							'<input type="text" value="'+ _this.formatDateTime(h.planDate) +'" class="addpurchDate" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>排产数量：</label>'+
							'<input type="text" value="'+ h.completeNum +'" class="addpurchNum" disabled></div>'+
					'</div>'
				})
				$('#purchSchedRecord').html(htmlPurchPlans);
			}
			if(a.data.bizPoHeader.poSchType == 2) {
				$('#purchAddCont').parent().remove();
				$('#chedulingStatus').val('排产完成');
				$('#purchAddBtn').hide();
				
				$('.saveBtnPt').hide();
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
					'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuInfo.name +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>商品货号：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
				'<div class="mui-input-row">'+
					'<label>采购数量：</label>'+
					'<input type="text" class="mui-input-clear" value="'+ item.ordQty +'" disabled></div>'+
//					隐藏结算价
//				'<div class="mui-input-row">'+
//					'<label>结算价：</label>'+
//					'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled></div>'+
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
			var chedulingStatus = '';
//			if(b.data.bizPoHeader.poSchType == 0) {
////				chedulingStatus = '未排产'
//			}
//			if(b.data.bizPoHeader.poSchType == 1) {
////				chedulingStatus = '排产中';
//				console.log(b.data.bizPoHeader.poDetailList);
//				var itemdate="";
//				var itemnum="";
//				$.each(b.data.bizPoHeader.poDetailList, function(o, p) {
////					console.log(p.bizSchedulingPlan.completePalnList);
//					$.each(p.bizSchedulingPlan.completePalnList, function(x, y) {
////						console.log(y);
//							itemdate=_this.formatDateTime(y.planDate);
//							itemnum=y.completeNum
//					});
//				});
//			}
//			if(b.data.bizPoHeader.poSchType == 2) {
//				$('.commdAddPlan').parent().remove();
////				chedulingStatus = '排产完成'
//				$('.saveBtnPt').hide();
//			}
			var htmlCommodity = '';
			var htmlAllSave = '';
			$.each(b.data.bizPoHeader.poDetailList, function(i,item) {
				var waiteNum = item.ordQty - item.sumCompleteNum;
				var comPlanTx = '';
				var comdPlans = '';
				var comdAddBtns = '';
				if(item.ordQty == item.sumCompleteNum) {
					chedulingStatus = '排产完成'
					comdAddBtns = ''
					comdPlans = ''
					$('.saveBtnPt').hide();
				}else {
					comdAddBtns = '<button type="submit" class="commdAddBtn schedull app_btn_search  mui-btn-blue mui-btn-block">添加排产计划</button>'+
					'<button type="submit" commdPurchId="'+item.id+'" id="singleAddBtn_'+ item.id+'" class="singleAddBtn schedulr app_btn_search mui-btn-blue mui-btn-block">保存</button>'

					comdPlans = '<div class="mui-row plan">'+
					'<div class="labelLf">排产计划：</div>'+
					'<div class="mui-row app_f13 commdAddPlan" id="'+ item.id+'">'+
						'<div class="mui-row app_bline commdPlan" name="'+ item.id +'">'+
							'<div class="mui-input-row">'+
								'<label>完成日期：</label>'+
								'<input type="date" name="'+ item.id +'_date" class="commdDate"></div>'+
							'<div class="mui-input-row">'+
								'<label>排产数量：</label>'+
								'<input type="text" name="'+ item.id +'_value" class="commdNum mui-input-clear"></div></div>'+
				'</div></div>'
				}
				if(waiteNum == item.ordQty) {
					chedulingStatus = '未排产'
					$('.commdSchedRecord').hide();
				}
				if(waiteNum != item.ordQty && item.ordQty != item.sumCompleteNum) {
					chedulingStatus = '排产中'
				}
				if(waiteNum != item.ordQty) {
					comPlanTx = _this.htmlcommdPlanTxt(item)
				}
//				}
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
							'<input type="text" class="app_color40 " value="'+ item.skuInfo.name +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="" value="'+ item.skuInfo.itemNo +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>采购数量：</label>'+
							'<input type="text" class="" value="'+ item.ordQty +'" disabled></div>'+
//							隐藏结算价
//						'<div class="mui-input-row">'+
//							'<label>结算价：</label>'+
//							'<input type="text" class="" value="'+ item.unitPrice +'" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>总金额：</label>'+
							'<input type="text" class="" value="'+ item.ordQty * item.unitPrice +'" disabled></div></div></div>'+
					'<div class="mui-row app_f13 app_bline">'+
						'<div class="mui-input-row">'+
							'<label>总申报数量：</label>'+
							'<input type="text" value="'+ item.ordQty +'" id="totalOrdQtyForSku_'+ item.id+'" class="commdOrdQty" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>总待排产量：</label>'+
							'<input type="text" value="'+ waiteNum +'" class="commdWaiteNum" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>已排产数量：</label>'+
							'<input type="text" name="toalSchedulingNumForSku" value="'+ item.sumCompleteNum +'" class="commdCompleteNum" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>当前状态：</label>'+
							'<input type="text" value="'+ chedulingStatus +'" class="commdCompleteNum app_cred3" disabled></div>'+
							comdAddBtns+'</div>'+
					'<div class="mui-row commdSchedRecord">'+
						'<div class="labelLf">排产记录：</div>'+
						'<div class="mui-row app_f13">'+comPlanTx+'</div></div>'+
					comdPlans +
					'</div></li>'
				htmlAllSave = '<button id="allSaveBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">批量保存</button>'
				var commdItemId = item.id;
				_this.commdEverySave(commdItemId)
			});
    		$("#orSchedCommd").html(htmlCommodity)
    		$(".saveBtnPt").html(htmlAllSave)
		},
		htmlcommdPlanTxt: function(tt) {
			var _this = this;
			var htmlcommdPlans = '';
			$.each(tt.bizSchedulingPlan.completePalnList, function(o, p) {
				htmlcommdPlans += '<div class="mui-row app_bline">'+
					'<div class="mui-input-row">'+
						'<label>完成日期：</label>'+
						'<input type="text" value="'+ _this.formatDateTime(p.planDate) +'" disabled></div>'+
					'<div class="mui-input-row">'+
						'<label>排产数量：</label>'+
						'<input type="text" value="'+ p.completeNum +'" disabled></div>'+
				'</div>'
			});
			return htmlcommdPlans;
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
				$(this).parent('.app_f13').next('.commdSchedRecord').next('.plan').find('.commdAddPlan').append(htmlcommdPlan);
				var commdDateName = $(this).parent('.app_f13').next('.commdSchedRecord').next('.plan').find('.commdDate').attr('name');
				var commdNumName = $(this).parent('.app_f13').next('.commdSchedRecord').next('.plan').find('.commdNum').attr('name');
				var commdPlanName = $(this).parent('.app_f13').next('.commdSchedRecord').next('.plan').find('.commdPlan').attr('name');
				$(this).parent('.app_f13').next('.commdSchedRecord').next('.plan').find('.addCommdDate').attr('name', commdDateName);
				$(this).parent('.app_f13').next('.commdSchedRecord').next('.plan').find('.addCommdNum').attr('name', commdNumName);
				$(this).parent('.app_f13').next('.commdSchedRecord').next('.plan').find('.commdAddCont').attr('name', commdPlanName);
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
			console.log(_this.userInfo.source)
			$('.inSaveBtn').on('tap', '#saveBtn', function() {
				var schedulOneId = _this.userInfo.staOrdId;
				_this.saveComplete(0, schedulOneId)
			});
			$('.inSaveBtn').on('tap', '#allSaveBtn', function() {
				_this.batchSave(m)
			});
		},
		commdEverySave: function(id) {
			var _this = this;
			$('.schedCommd').on('tap', '#singleAddBtn_'+ id, function() {
				_this.saveComplete(1, id)
			});
		},
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
	        if(confirm("确定执行该排产确认吗？")) {
	            $.ajax({
	                url: '/a/biz/po/bizPoHeader/saveSchedulingPlan',
	                contentType: 'application/json',
	                data:JSON.stringify(params),
	                datatype:"json",
	                type: 'post',
	                success: function (result) {
	                    if(result == true) {
//	                    	GHUTILS.OPENPAGE({
//								url: "../../html/orderMgmtHtml/orScheduling.html",
//								extras: {
//
//								}
//							})
	                    	var urlTxt = '';
	                    	if(_this.userInfo.source == 'orderList') {
	                    		urlTxt = "../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html";
	                    	}
	                    	if(_this.userInfo.source == 'inventoryList') {
	                    		urlTxt = "../../html/inventoryMagmetHtml/inventoryList.html";
	                    	}
	                        GHUTILS.OPENPAGE({
								url: urlTxt,
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
                totalOriginalNum = parseInt(totalOriginalNum) + parseInt(originalNum);
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
            for(var index in reqDetailIdList) {
                var reqDetailId = reqDetailIdList[index];
                var trArray = $("[name='" + reqDetailId + "']");
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
            if(parseInt(totalSchedulingNum) > parseInt(totalOriginalNum)) {
                alert("排产量总和太大，请从新输入!")
                return false
            }
            if(confirm("确定执行该排产确认吗？")) {
                $.ajax({
                    url: '/a/biz/po/bizPoHeader/saveSchedulingPlan',
                    contentType: 'application/json',
                    data:JSON.stringify(params),
                    datatype:"json",
                    type: 'post',
                    success: function (result) {
                        if(result == true) {
                            var urlTxt = '';
	                    	if(_this.userInfo.source == 'orderList') {
	                    		urlTxt = "../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html";
	                    	}
	                    	if(_this.userInfo.source == 'inventoryList') {
	                    		urlTxt = "../../html/inventoryMagmetHtml/inventoryList.html";
	                    	}
	                        GHUTILS.OPENPAGE({
								url: urlTxt,
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
        formatDateTime: function(unix) {

			var now = new Date(parseInt(unix) * 1);
			now = now.toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
			if(now.indexOf("下午") > 0) {
				if(now.length == 18) {
					var temp1 = now.substring(0, now.indexOf("下午")); //2014/7/6
					var temp2 = now.substring(now.indexOf("下午") + 2, now.length); // 5:17:43
					var temp3 = temp2.substring(0, 1); //  5
					var temp4 = parseInt(temp3); // 5
					temp4 = 12 + temp4; // 17
					var temp5 = temp4 + temp2.substring(1, temp2.length); // 17:17:43
					//	                now = temp1 + temp5; // 2014/7/6 17:17:43
					//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
					now = now.replace("-"); //  2014-7-6 17:17:43
				} else {
					var temp1 = now.substring(0, now.indexOf("下午")); //2014/7/6
					var temp2 = now.substring(now.indexOf("下午") + 2, now.length); // 5:17:43
					var temp3 = temp2.substring(0, 2); //  5
					if(temp3 == 12) {
						temp3 -= 12;
					}
					var temp4 = parseInt(temp3); // 5
					temp4 = 12 + temp4; // 17
					var temp5 = temp4 + temp2.substring(2, temp2.length); // 17:17:43
					//	                now = temp1 + temp5; // 2014/7/6 17:17:43
					//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
					now = now.replace("-"); //  2014-7-6 17:17:43
				}
			} else {
				var temp1 = now.substring(0, now.indexOf("上午")); //2014/7/6
				var temp2 = now.substring(now.indexOf("上午") + 2, now.length); // 5:17:43
				var temp3 = temp2.substring(0, 1); //  5
				var index = 1;
				var temp4 = parseInt(temp3); // 5
				if(temp4 == 0) { //  00
					temp4 = "0" + temp4;
				} else if(temp4 == 1) { // 10  11  12
					index = 2;
					var tempIndex = temp2.substring(1, 2);
					if(tempIndex != ":") {
						temp4 = temp4 + "" + tempIndex;
					} else { // 01
						temp4 = "0" + temp4;
					}
				} else { // 02 03 ... 09
					temp4 = "0" + temp4;
				}
				var temp5 = temp4 + temp2.substring(index, temp2.length); // 07:17:43
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
})(Zepto);
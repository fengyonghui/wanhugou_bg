(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.fs == false;
//		this.outSaveFlag = "false"
//		this.inSsaveFlag = "false"
		this.htmlcommdPlans = '';
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//biz:po:bizPoHeader:addScheduling		biz:po:bizPoHeader:saveScheduling	保存、批量保存	
//			this.getPermissionList1('biz:po:bizPoHeader:addScheduling','outSaveFlag')	
//			this.getPermissionList2('biz:po:bizPoHeader:saveScheduling','inSsaveFlag')	
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			_this.getData();
		},
//		getPermissionList1: function (markVal,flag) {
//          var _this = this;
//          $.ajax({
//              type: "GET",
//              url: "/a/sys/menu/permissionList",
//              dataType: "json",
//              data: {"marking": markVal},
//              async:false,
//              success: function(res){
//                  _this.outSaveFlag = res.data;
//              }
//          });
//      },
		getData: function() {
			var _this = this;
			$.ajax({
				type:"get",
				url:"/a/biz/po/bizPoHeader/scheduling4Mobile",
                data:{id: _this.userInfo.staOrdId},
                dataType: "json",
                success: function(res){
//              	console.log(res)
                	$('#poHeaderId').val(res.data.bizPoHeader.id);
                	var bizReqReqNo = '';
                	if(res.data.bizPoHeader.bizRequestHeader) {
                		bizReqReqNo = res.data.bizPoHeader.bizRequestHeader.reqNo;
                	}
                	$('#bizReqReqNo').val(bizReqReqNo);
                	if(res.data.roleFlag != false) {
                		$('#completeBtn').hide();
                		$("#totalCompleteAlert").hide()
                	}
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
		            console.log(res)
					var poDetailList = res.data.bizPoHeader.poDetailList;
		            if(res.data.bizPoHeader.poSchType != 0) {
		            	$('#schedPurch').hide();
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
						});
						$("#orSchedPurch").html(htmlPurch)
		            }
                	_this.showContent(res);
                }
			});	
		},
		showContent: function(data) {
			var _this = this;
//			console.log(data)
			var schedulingPlanHeaderFlag = false;
			var schedulingPlanDetailFlag = false;
			if(data.data.detailHeaderFlg == true) {
				$('#schedPlan1').attr('checked', 'checked');
				$('#schedPlan2').removeAttr('checked');
				$('#schedCommd').hide();
				$(".inputRadio").attr("disabled", true);
				schedulingPlanHeaderFlag = true;
				_this.purchContent(data);
			}
			if(data.data.detailSchedulingFlg == true) {
				$('#schedPlan2').attr('checked', 'checked');
				$('#schedPlan1').removeAttr('checked');
				$('#schedPurch').hide();
				$(".inputRadio").attr("disabled", true);
				schedulingPlanDetailFlag = true;
				_this.commdContent(data,'yes');
			}
			if (data.data.detailHeaderFlg != true && data.data.detailSchedulingFlg != true) {
//              $("#stockGoods").show();
                $("#schedPurch").show();
                $("#schedCommd").hide();
				$("#purhSched").hide();
				
                $("#completeBtn").hide()

                alert("该采购单未排产！")
                _this.btnshow(data);
            }
			_this.ajaxNum(schedulingPlanHeaderFlag,schedulingPlanDetailFlag);
			_this.affirmSchedu();
		},
		ajaxNum: function(schedulingPlanHeaderFlag,schedulingPlanDetailFlag) {
        	var _this = this;
        	 $.ajax({
                type: "GET",
                url: "/a/biz/po/bizPoHeader/checkSchedulingNum",
                dataType: "json",
                data: {id: _this.userInfo.staOrdId},
                async:false,
                success: function(result){
//              	console.log(result)
               	 	var totalOrdQty = result['totalOrdQty'];
                    $("#totalOrdQty").val(totalOrdQty)
                    var totalSchedulingHeaderNum = result['totalSchedulingHeaderNum'] == null ? 0 : result['totalSchedulingHeaderNum'];
                    var totalCompleteScheduHeaderNum = result['totalCompleteScheduHeaderNum'] == null ? 0 : result['totalCompleteScheduHeaderNum'];
                    $("#totalCompleteScheduHeaderNum").val(totalCompleteScheduHeaderNum)
//                  console.log(totalSchedulingHeaderNum)
//                  console.log(totalCompleteScheduHeaderNum)
                    $("#totalSchedulingNumToDo").val(parseInt(totalSchedulingHeaderNum) - parseInt(totalCompleteScheduHeaderNum))
//                  console.log(schedulingPlanHeaderFlag)
                    if (schedulingPlanHeaderFlag == true) {
//                  	console.log($("#totalSchedulingNumToDo").val())
                        if($("#totalSchedulingNumToDo").val() == 0) {
//                      	console.log(111)
                            $("#completeBtn").hide()
                            $("#totalCompleteAlert").show()
                        }
                    }
//                  console.log(schedulingPlanDetailFlag)
                    if (schedulingPlanDetailFlag == true) {
                        var toalSchedulingNumForSkuHtml = $("[name=toalSchedulingNumForSku]");
                        var toalSchedulingNumForSkuNum = 0;
                        for(i=0;i<toalSchedulingNumForSkuHtml.length;i++){
                            var schedulingNumForSkuNum = toalSchedulingNumForSkuHtml[i];
                            var scForSkuNum = $(schedulingNumForSkuNum).attr("value")
                            toalSchedulingNumForSkuNum = parseInt(toalSchedulingNumForSkuNum) + parseInt(scForSkuNum);
                        }
                        if(toalSchedulingNumForSkuNum == 0) {
                            $("#completeBtn").hide()
                            $("#totalCompleteAlert").show()
                        }
                    }
        	    }
            });
        },
		btnshow: function(data) {
			var _this = this;
			$('input[type=radio]').on('change', function() {
				if(this.checked && this.value == 0) {
					$('#schedPurch').show();
					$('#schedCommd').hide();
					_this.purchContent(data);
				}
				if(this.checked && this.value == 1) {
					$('#schedPurch').hide();
					$('#schedCommd').show();
					_this.commdContent(data,'no');
				}
			})
		},
		purchContent: function(a) {
			var _this = this;
			$('#schedPurch').show();
			var htmlPurchPlans = '';
			if(a.data.bizCompletePalns) {
				$.each(a.data.bizCompletePalns, function(d, h) {
					if(h.completeStatus == 0) {
						htmlPurchPlans += '<div class="mui-row app_bline">'+
							'<div class="mui-row mui-checkbox mui-right">' +
		                    '<input style="top: 23px;" name="'+h.completeStatus+'" value="'+h.id+'" class="orderChk"  type="checkbox"></div>' +
		                    '<div class="mui-row" style="width: 80%;float: left;">'+             
								'<div class="mui-input-row">'+
									'<label>完成日期：</label>'+
									'<input type="text" value="'+ _this.formatDateTime(h.planDate) +'" class="addpurchDate" disabled></div>'+
								'<div class="mui-input-row">'+
									'<label>排产数量：</label>'+
									'<input type="text" value="'+ h.completeNum +'" class="addpurchNum" disabled></div>'+
							'</div>'+
						'</div>'
					}
					if(h.completeStatus == 1) {
						htmlPurchPlans += '<div class="mui-row app_bline">'+
							'<div class="mui-row alleryAffirm">' +
							'<span class="app_cred3">已确认</span></div>' +
		                    '<div class="mui-row" style="width: 80%;float: left;">'+             
								'<div class="mui-input-row">'+
									'<label>完成日期：</label>'+
									'<input type="text" value="'+ _this.formatDateTime(h.planDate) +'" class="addpurchDate" disabled></div>'+
								'<div class="mui-input-row">'+
									'<label>排产数量：</label>'+
									'<input type="text" value="'+ h.completeNum +'" class="addpurchNum" disabled></div>'+
							'</div>'+
						'</div>'
					}
				})
				$('#purchSchedRecord').html(htmlPurchPlans);
			}
			var htmlPurch = '';
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
			});
    		$("#orSchedPurch").html(htmlPurch)
		},
		commdContent: function(b,choice) {
			var _this = this;
			var htmlCommodity = '';
			$.each(b.data.bizPoHeader.poDetailList, function(i,item) {
//				console.log(item)
				if (b.data.detailHeaderFlg != true && b.data.detailSchedulingFlg != true) {
					$('.commdSchedRecord').hide();
				}
				var waiteNum = item.sumCompleteNum - item.sumCompleteDetailNum;
				var comPlanTx = '';
				if(choice == 'yes') {
					comPlanTx = _this.htmlcommdPlanTxt(item);
				}else {
					comPlanTx = '';
				}
				var alreaAffirm = '';
				if(item.sumCompleteDetailNum) {
					alreaAffirm = item.sumCompleteDetailNum;
				}else {
					alreaAffirm = 0
				}
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
							'<input type="text" name="reqQtys" value="'+ item.ordQty +'" id="totalOrdQtyForSku" class="commdOrdQty" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>总待确认量：</label>'+
							'<input type="text" name="toalSchedulingNumForSku" value="'+ waiteNum +'" class="commdWaiteNum" disabled></div>'+
						'<div class="mui-input-row">'+
							'<label>总已确认量：</label>'+
							'<input type="text" name="sumCompleteDetailNum" value="'+ alreaAffirm +'" class="commdCompleteNum" disabled></div>'+
							'</div>'+
					'<div class="mui-row commdSchedRecord">'+
						'<div class="labelLf">申请记录：</div>'+
						'<div class="mui-row app_f13">'+comPlanTx+'</div></div>'+
					'</div></li>'
				var commdItemId = item.id;
			});
    		$("#orSchedCommd").html(htmlCommodity)
		},
		htmlcommdPlanTxt: function(tt) {
			var _this = this;
			var htmlcommdPlans = '';
			$.each(tt.bizSchedulingPlan.completePalnList, function(o, p) {
				if(p.completeStatus == 0) {
					htmlcommdPlans += '<div class="mui-row app_bline">'+
						'<div class="mui-row mui-checkbox mui-right">' +
	                        '<input style="top: 23px;" name="'+p.completeStatus+'" value="'+p.id+'" class="orderChk"  type="checkbox"></div>' +
	                    '<div class="mui-row" style="width: 80%;float: left;">'+ 
							'<div class="mui-input-row">'+
								'<label>完成日期：</label>'+
								'<input type="text" value="'+ _this.formatDateTime(p.planDate) +'" disabled></div>'+
							'<div class="mui-input-row">'+
								'<label>排产数量：</label>'+
								'<input type="text" value="'+ p.completeNum +'" disabled></div>'+
						'</div>'+
					'</div>'
				}
				if(p.completeStatus == 1) {
					htmlcommdPlans += '<div class="mui-row app_bline">'+
						'<div class="mui-row alleryAffirm">' +
							'<span class="app_cred3">已确认</span></div>' +
	                    '<div class="mui-row" style="width: 80%;float: left;">'+ 
							'<div class="mui-input-row">'+
								'<label>完成日期：</label>'+
								'<input type="text" value="'+ _this.formatDateTime(p.planDate) +'" disabled></div>'+
							'<div class="mui-input-row">'+
								'<label>排产数量：</label>'+
								'<input type="text" value="'+ p.completeNum +'" disabled></div>'+
						'</div>'+
					'</div>'
				}
			});
			return htmlcommdPlans;
		},
		affirmSchedu: function() {
			var _this = this;
			$('.inSaveBtn').on('tap', '#completeBtn', function() {
	            var params = new Array();
	            var poHeaderId = $("#poHeaderId").val();
	            $(".orderChk").each(function () {
	                if(this.checked){
	                    var completeId = $(this).attr("value")
	                    params.push(completeId);
	                }
	            })
	
	            if(params.length == 0) {
	                alert("未勾选确认项！")
	                return false;
	            }
	
	            params.unshift($('#bizReqReqNo').val());
	            if(confirm("确定执行该确认排产吗？")) {
//	            	console.log(params)
	                $.ajax({
	                    url: '/a/biz/po/bizPoHeader/confirm4Mobile?poHeaderId='+poHeaderId,
	                    contentType: 'application/json',
	                    data:JSON.stringify(params),
	                    type: 'post',
	                    success: function (result) {
	                    	result = $.parseJSON(result.replace(/<.*?>/ig,""));
//	                    	console.log(result)
	                      if(result.data.resultFlag == true) {
	                      	GHUTILS.OPENPAGE({
								url: "../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
								extras: {}
							})
	                      }
	                    },
	                    error: function (error) {
	                        console.info(error);
	                    }
	                });
	            }
            });
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
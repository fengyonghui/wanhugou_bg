(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData();//获取数据
			
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
		},
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeader/form4Mobile",
                data: {id:_this.userInfo.inListId},
                dataType: "json",
                success: function(res){
					console.log(res)
				/*业务状态*/
					var bizstatus = res.bizStatus;
					var bizstatusTxt = '';
					if(bizstatus==0) {
						bizstatusTxt = "未审核"
					}else if(bizstatus==1) {
						bizstatusTxt = "首付支付"
					}else if(bizstatus==2) {
						bizstatusTxt = "全部支付"
					}else if(bizstatus==4) {
						bizstatusTxt = "审核中"
					}else if(bizstatus==5) {
						bizstatusTxt = "审核通过"
					}else if(bizstatus==6) {
						bizstatusTxt = "审批中"
					}else if(bizstatus==7) {
						bizstatusTxt = "审批完成"
					}else if(bizstatus==10) {
						bizstatusTxt = "采购中"
					}else if(bizstatus==13) {
						bizstatusTxt = "部分结算"
					}else if(bizstatus==15) {
						bizstatusTxt = "采购完成"
					}else if(bizstatus==20) {
						bizstatusTxt = "备货中"
					}else if(bizstatus==25) {
						bizstatusTxt = "供货完成"
					}else if(bizstatus==30) {
						bizstatusTxt = "收货完成"
					}else if(bizstatus==37) {
						bizstatusTxt = "结算完成"
					}else if(bizstatus==40) {
						bizstatusTxt = "取消"
					}else {
						bizstatusTxt = "未知类型"
					}
					$('#inPoDizstatus').val(bizstatusTxt)
					$('#inPoordNum').val(res.data.entity.reqNo)
					$('#inOrordNum').val(res.data.entity.fromOffice.name)
					$('#inPototal').val(res.data.entity.totalMoney)
					$('#inMoneyReceive').val(res.data.entity.recvQtys)
					$('#inMarginLevel').val(res.data.entity.recvTotal + '%')
					$('#inPoLastDa').val(_this.formatDateTime(res.data.entity.recvEta))
					_this.commodityHtml(res.data)
					_this.statusListHtml(res.data)
                }
            });
		},
		statusListHtml:function(data){
			var _this = this;
			console.log(data)
			var pHtmlList = '';
			$.each(data.statusList, function(i, item) {
				console.log(item)
//				0未审核 1首付款支付,2是全部支付 5审核通过 10 采购中 15采购完成 20备货中  25 供货完成 30收货完成 35关闭
				var checkBizStatus = '';
				if(item.bizStatus==0) {
					checkBizStatus = '未审核'
				}else if(item.bizStatus==1) {
					checkBizStatus = '首付款支付'
				}else if(item.bizStatus==2) {
					checkBizStatus = '全部支付'
				}else if(item.bizStatus==5) {
					checkBizStatus = '审核通过'
				}else if(item.bizStatus==10) {
					checkBizStatus = '采购中'
				}else if(item.bizStatus==15) {
					checkBizStatus = '采购完成'
				}else if(item.bizStatus==20) {
					checkBizStatus = '备货中'
				}else if(item.bizStatus==25) {
					checkBizStatus = '供货完成'
				}else if(item.bizStatus==30) {
					checkBizStatus = '收货完成'
				}else if(item.bizStatus==35) {
					checkBizStatus = '关闭'
				}else {
					checkBizStatus = ''
				}
				var step = i + 1;
				pHtmlList +='<li class="step_item">'+
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
			var _this = this;
//			console.log(data)
			var htmlCommodity = '';
			$.each(data.reqDetailList, function(i, item) {
//				console.log(item)
				var orderNum = '';
				if(item.bizPoHeader) {
					orderNum = item.bizPoHeader.orderNum;
				}else {
					orderNum = ''
				}
				htmlCommodity +='<li class="mui-table-view-cell mui-media">'+
//		产品图片
					'<div class="photoParent mui-pull-left app_pr">'+
						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'">'+
					'</div>'+
//		产品信息
					'<div class="mui-media-body commodity">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.brandName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>供应商：</label>'+
							'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuInfo.productInfo.vendorName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.name +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品编码：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.prodCode +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>单价：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>申报数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.reqQty +'" reqQty disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>仓库名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.invName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>库存数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInvQty +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>总库存数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.invenSkuOrd +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>已收货数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.recvQty +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>已生成采购单：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ orderNum +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>采购数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.reqQty +'" disabled>'+
						'</div>'+
					'</div>'+
				'</li>'
			});
			$("#commodityMenu").html(htmlCommodity)
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
})(Zepto);

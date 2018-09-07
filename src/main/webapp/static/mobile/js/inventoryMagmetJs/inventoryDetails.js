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
//			_this.scheduling();
		},
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeaderForVendor/form4MobileNew",
                data: {id:_this.userInfo.inListId,str:'detail'},
                dataType: "json",
                success: function(res){
                	console.log(res)
				    /*业务状态*/
				    var itemStatus=res.data.bizRequestHeader.bizStatus;
				    var bizstatusTxt = '';
				    $.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {type:"biz_req_status"},		                
		                dataType: "json",
		                success: function(res){
		                	$.each(res,function(i,item){
		                		 if(item.value==itemStatus){
		                		 	  bizstatusTxt = item.label 
		                		 }
		                	})
		                	$('#inPoDizstatus').val(bizstatusTxt);
						}
					});
				    //排产状态
				    var itempoSchType=res.data.bizRequestHeader.bizPoHeader.poSchType;
				    var SchedulstatusTxt = '';
				    $.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {type:"poSchType"},		                
		                dataType: "json",
		                success: function(res){
		                	$.each(res,function(i,item){
		                		 if(item.value==itempoSchType){
		                		 	  SchedulstatusTxt = item.label 
		                		 }
		                	})
		                	$('#inSchedulstatus').val(SchedulstatusTxt);
						}
					})
				    //排产类型
				    $('#inSchedultype').val();
				    
					$('#inPoDizstatus').val(bizstatusTxt)
					$('#inPoordNum').val(res.data.bizRequestHeader.reqNo)
					$('#inOrordNum').val(res.data.bizRequestHeader.fromOffice.name)
					$('#inPototal').val(res.data.bizRequestHeader.totalMoney.toFixed(1))
					$('#inMoneyReceive').val(res.data.bizRequestHeader.recvTotal.toFixed(1))//已收保证金entity.recvQtys
					$('#inMarginLevel').val((res.data.bizRequestHeader.recvTotal*100/res.data.bizRequestHeader.totalMoney) .toFixed(2)+ '%')//保证金比例           
					$('#inMoneyPay').val(res.data.bizRequestHeader.bizPoHeader.payTotal.toFixed(2))
					$('#inPoLastDa').val(_this.formatDateTime(res.data.bizRequestHeader.recvEta))
					_this.commodityHtml(res.data);
//					_this.statusListHtml(res.data);
					_this.purchaseHtml(res.data);
					_this.paylistHtml(res.data);
                }
            });
		},
		//排产信息接口
//		scheduling:function(){
//			var _this = this;
//			$.ajax({
//              type: "GET",
//              url: "/a/biz/po/bizPoHeader/scheduling4Mobile",
//              data: {id:},
//              dataType: "json",
//              success: function(res){
//              	console.log('---')
//              	console.log(res)
//				}
//			})
//		},
        //支付列表
        paylistHtml:function(data){
        	var _this = this;
        	var htmlPaylist = '';
        	$.each(data.paymentOrderList, function(i, item) {
				console.log(item)			
				htmlPaylist +='<li class="mui-table-view-cell mui-media step_items">'+					
					'<div class="mui-media-body commoditys">'+
						'<div class="mui-input-row">'+
							'<label>付款金额：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.total.toFixed(2) +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>实际付款金额：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.payTotal.toFixed(2) +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>最后付款时间：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.deadline) +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>实际付款时间：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.payTime) +'" disabled>'+
						'</div>'+
					'</div>'+
				'</li>'
			});
			$("#inPaylist").html(htmlPaylist);
        },
		//采购商品
		purchaseHtml: function(data) {
			var _this = this;
			var htmlPurchase = '';
			$.each(data.reqDetailList, function(i, item) {
				console.log(item)
				var orderNum = '';
				if(item.bizPoHeader) {
					orderNum = item.bizPoHeader.orderNum;
				}else {
					orderNum = ''
				}
				htmlPurchase +='<li class="mui-table-view-cell mui-media">'+
					'<div class="photoParent mui-pull-left app_pr">'+
						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'">'+
					'</div>'+
					'<div class="mui-media-body commoditys">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.brandName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.name +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>采购数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.unitPrice.toFixed(1) +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>结算价：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.unitPrice.toFixed(1) +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>总金额：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.reqQty +'" reqQty disabled>'+
						'</div>'+

					'</div>'+
				'</li>'
			});
			$("#purchaseMenu").html(htmlPurchase)
		},		
		//状态流程
//		statusListHtml:function(data){
//			var _this = this;
//			console.log(data)
//			var pHtmlList = '';
//			$.each(data.statusList, function(i, item) {
//				console.log(item)
////				0未审核 1首付款支付,2是全部支付 5审核通过 10 采购中 15采购完成 20备货中  25 供货完成 30收货完成 35关闭
//				var checkBizStatus = '';
//				if(item.bizStatus==0) {
//					checkBizStatus = '未审核'
//				}else if(item.bizStatus==1) {
//					checkBizStatus = '首付款支付'
//				}else if(item.bizStatus==2) {
//					checkBizStatus = '全部支付'
//				}else if(item.bizStatus==5) {
//					checkBizStatus = '审核通过'
//				}else if(item.bizStatus==10) {
//					checkBizStatus = '采购中'
//				}else if(item.bizStatus==15) {
//					checkBizStatus = '采购完成'
//				}else if(item.bizStatus==20) {
//					checkBizStatus = '备货中'
//				}else if(item.bizStatus==25) {
//					checkBizStatus = '供货完成'
//				}else if(item.bizStatus==30) {
//					checkBizStatus = '收货完成'
//				}else if(item.bizStatus==35) {
//					checkBizStatus = '关闭'
//				}else {
//					checkBizStatus = ''
//				}
//				var step = i + 1;
//				pHtmlList +='<li class="step_item">'+
//					'<div class="step_num">'+ step +' </div>'+
//					'<div class="step_num_txt">'+
//						'<div class="mui-input-row">'+
//							'<label>处理人:</label>'+
//							'<input type="text" value="'+ item.createBy.name +'" class="mui-input-clear" disabled>'+
//					    '</div>'+
//						'<div class="mui-input-row">'+
//					        '<label>状态:</label>'+
//					        '<input type="text" value="'+ checkBizStatus +'" class="mui-input-clear" disabled>'+
//					    	'<label>时间:</label>'+
//					        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
//					    '</div>'+
//					'</div>'+
//				'</li>'
//			});
//			$("#inCheckAddMenu").html(pHtmlList)
//		},
		//备货商品
		commodityHtml: function(data) {
			var _this = this;
			var htmlCommodity = '';
			$.each(data.reqDetailList, function(i, item) {
				var orderNum = '';
				if(item.bizPoHeader) {
					orderNum = item.bizPoHeader.orderNum;
				}else {
					orderNum = ''
				}
				htmlCommodity +='<li class="mui-table-view-cell mui-media app_bline">'+
//		产品图片
					'<div class="photoParent mui-pull-left app_pr">'+
						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'">'+
					'</div>'+
//		产品信息
					'<div class="mui-media-body commoditys">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.brandName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>供应商：</label>'+
							'<input type="text" class="app_color40 mui-input-clear" value="'+ item.skuInfo.productInfo.office.name +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.name +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品编码：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.partNo +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>结算价：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.unitPrice.toFixed(1) +'" disabled>'+
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
//						'<div class="mui-input-row">'+
//							'<label>已生成采购单：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ orderNum +'" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>采购数量：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ item.reqQty +'" disabled>'+
//						'</div>'+
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

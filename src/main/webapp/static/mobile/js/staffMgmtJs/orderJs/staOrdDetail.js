staPoLastDa(function($) {
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
                url: "/a/biz/order/bizOrderHeader/listData4mobile",
                data: {id:_this.userInfo.staOrdListId},
                dataType: "json",
                success: function(res){
					console.log(res)
					$.each(res.data.page.list, function(i, item) {
						console.log(item)
						var shouldPay = item.totalDetail + item.totalExp + item.freight;
						var serverPrice = (item.totalDetail+item.totalExp+item.freight)-item.totalBuyPrice;
						//发票状态
						var invStatusTxt = '';
						if(item.invStatus==0) {
							invStatusTxt = "不开发票"					
						}
						//业务状态
						var statusTxt = '';
						if(item.staStatus=15) {
							statusTxt = "供货中"
						}
						$('#staPoordNum').val(item.orderNum)
						$('#staRelNum').val(item.customer.name)
						$('#staPototal').val(item.totalDetail)
						$('#staAdjustmentMoney').val(item.totalExp)
						$('#staFreight').val(item.freight)
						$('#staShouldPay').val(shouldPay)
						var poLastDa = item.receiveTotal/(item.totalDetail+item.totalExp+item.freight);
						$('#staPoLastDa').val(item.receiveTotal)
						$('#staServerPrice').val(serverPrice)
						$('#staInvoice').val(invStatusTxt)
						$('#staStatus').val(statusTxt)
						$('#staConsignee').val(item.bizLocation.receiver)
						$('#staMobile').val(item.bizLocation.phone)
						$('#staShippAddress').val()
						$('#staDateilAddress').val(item.bizLocation.address)
						$('#staEvolve').val()
//						_this.commodityHtml(res.data)
//						_this.statusListHtml(res.data)
					}) 
                }
            });
		},
		statusListHtml:function(data){
			var _this = this;
//			console.log(data)
			var pHtmlList = '';
//			var len = data.bizPoHeader.commonProcessList.length
			$.each(data.statusList, function(i, item) {
//				console.log(item)
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
//			console.log(data)
			var htmlCommodity = '';
			$.each(data.reqDetailList, function(i, item) {
//				console.log(item)
				var hightNum = i + 1;
				
				 htmlCommodity += '<div class="mui-row border-btm5" id="' + item.id + '">' +
					'<div class="mui-row mui-checkbox mui-left">'+hightNum+'</div>'+
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label class="commodityName">商品货号:</label>' +
                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.name + '" disabled></div></li></div></div>' +
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label class="commodityName">已生成的采购单:</label>' +
                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.name + '" disabled></div></li></div></div>' +
                   
                   '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>货架名称:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.productInfo.brandName + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>商品名称:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div></div>' +
                   
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>商品编号:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.productInfo.brandName + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>创建时间:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div></div>' +
                   
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>商品出厂价:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.productInfo.brandName + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>供应商:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div></div>' +
                   
                   '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>供应商电话:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>商品单价:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.reqQty + '" disabled></div></li></div></div>'+
				
					'<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>采购数量:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>总 额:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.reqQty + '" disabled></div></li></div></div>'+
				
					'<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>已发货数量:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>发货方:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.reqQty + '" disabled></div></li></div></div>';
			});
			$("#staCommodity").html(htmlCommodity)
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

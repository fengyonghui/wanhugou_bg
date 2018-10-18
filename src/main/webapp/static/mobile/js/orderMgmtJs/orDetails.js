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
                url: "/a/biz/po/bizPoHeader/form4Mobile",
                data: {
                	id:_this.userInfo.staOrdId,
                	str:_this.userInfo.str,
                	fromPage:_this.userInfo.fromPage
                },
                dataType: "json",
                success: function(res){
                console.log(res)
					/*业务状态*/
					$('#orpoNum').val(res.data.bizOrderHeader.orderNumber)//单号
					$('#ordtotal').val(res.data.entity.fromOffice.name)//总价
					$('#orshouldPay').val(res.data.entity.totalMoney)//应付金额
					$('#orLastDa').val(res.data.entity.recvQtys)//最后付款时间
					$('#delSite').val(res.data.entity.recvTotal + '%')//交货地点
					$('#orRemark').val(_this.formatDateTime(res.data.entity.recvEta))//备注
					$('#orTypes').val(_this.formatDateTime(res.data.entity.recvEta))//订单状态
					$('#orSupplier').val(_this.formatDateTime(res.data.entity.recvEta))//供应商
					
					$('#orSupplierNum').val(_this.formatDateTime(res.data.entity.recvEta))//供应商卡号
					$('#orSupplierMoney').val(_this.formatDateTime(res.data.entity.recvEta))//供应商收款人
					$('#orSupplierBank').val(_this.formatDateTime(res.data.entity.recvEta))//供应商开户行
					$('#orSuppliercontract').val(_this.formatDateTime(res.data.entity.recvEta))//供应商合同
					$('#orSuppliercardID').val(_this.formatDateTime(res.data.entity.recvEta))//供应商身份证
					$('#orApplyNum').val(_this.formatDateTime(res.data.entity.recvEta))//申请时间
					$('#orDateNow').val(_this.formatDateTime(res.data.entity.recvEta))//本次申请付款时间
					
//					_this.commodityHtml(res.data)
//					_this.statusListHtml(res.data)
                }
            });
		},
		statusListHtml:function(data){
			var _this = this;
			var pHtmlList = '';
			$.each(data.statusList, function(i, item) {
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
			var htmlCommodity = '';
			$.each(data.reqDetailList, function(i, item) {
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
							'<input type="text" class="font-color mui-input-clear" value="'+ item.skuInfo.productInfo.vendorName +'" disabled>'+
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
							'<input type="text" class="mui-input-clear" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>库存数量：</label>'+
							'<input type="text" class="mui-input-clear" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>总库存数量：</label>'+
							'<input type="text" class="mui-input-clear" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>已收货数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.recvQty +'" disabled>'+
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

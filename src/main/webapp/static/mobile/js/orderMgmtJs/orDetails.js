(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.unitPriceFlag = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData();//获取数据
			this.getPermissionList('biz:order:unitPrice:view','unitPriceFlag')//结算价权限
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
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
                	console.log(res.data)
                    _this.unitPriceFlag = res.data;
                }
            });
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
					/*业务状态*/
					var bizPoHeader = res.data.bizPoHeader;
					//申请金额，最后付款时间显示
					var bizPoPaymentOrderId = '';
					if(bizPoHeader.bizPoPaymentOrder) {
						bizPoPaymentOrderId = bizPoHeader.bizPoPaymentOrder.id;
					}
					if(bizPoPaymentOrderId != '' || res.data.type == 'createPay') {
						$('#orNumDate').show();
						var orApplyNum = bizPoPaymentOrderId != '' ? bizPoHeader.bizPoPaymentOrder.total : (bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight-bizPoHeader.payTotal);
						$('#orApplyNum').val(orApplyNum)//申请金额
						$('#orNowDate').val(_this.formatDateTime(bizPoHeader.bizPoPaymentOrder.deadline))//本次申请付款时间
					}else {
						$('#orNumDate').hide();
					}  
					var orshouldPay = bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight
					$('#orpoNum').val(res.data.bizOrderHeader.orderNumber)//单号
					$('#ordtotal').val(bizPoHeader.totalDetail)//总价
					$('#orshouldPay').val(orshouldPay)//应付金额
					$('#orLastDa').val(_this.formatDateTime(bizPoHeader.lastPayDate))//最后付款时间
					//交货地点
					if(bizPoHeader.deliveryStatus==0 || bizPoHeader.deliveryStatus == ''){
						$('#fromType1').attr('checked','checked');
						$('#fromType2').removeAttr('checked');
					}
					if(bizPoHeader.deliveryStatus==1){
						$('#fromType1').removeAttr('checked');
						$('#fromType2').attr('checked','checked');						
					}
					$('#orRemark').val(bizPoHeader.remark)//备注
//					$('#orTypes').val()
					//订单状态 
					var valueTxt = res.data.bizPoHeader.bizStatus;
//					console.log(valueTxt)
					$('#orTypes').val(valueTxt);
					$('#orSupplier').val(bizPoHeader.vendOffice.name)//供应商
					if(bizPoHeader.vendOffice.bizVendInfo) {
						$('#orSupplierNum').val(bizPoHeader.vendOffice.bizVendInfo.cardNumber)//供应商卡号
						$('#orSupplierMoney').val(bizPoHeader.vendOffice.bizVendInfo.payee)//供应商收款人
						$('#orSupplierBank').val(bizPoHeader.vendOffice.bizVendInfo.bankName)//供应商开户行
						$('#orSuppliercontract').val(bizPoHeader.vendOffice.bizVendInfo.compactImgList)//供应商合同
						$('#orSuppliercardID').val(bizPoHeader.vendOffice.bizVendInfo.identityCardImgList)//供应商身份证
					}
					_this.commodityHtml(res.data)
					_this.statusListHtml(res.data)
                }
            });
		},
		statusListHtml:function(data){
			var _this = this;
			var pHtmlList = '';
			if(data.bizPoHeader.commonProcessList) {
				$.each(data.bizPoHeader.commonProcessList, function(i, item) {
					var comLen = data.bizPoHeader.commonProcessList.length;
					var step = i + 1;
					if(i != comLen-1) {
						pHtmlList +='<li class="step_item">'+
							'<div class="step_num">'+ step +' </div>'+
							'<div class="step_num_txt">'+
								'<div class="mui-input-row">'+
									'<label>批注:</label>'+
							        '<textarea name="" rows="" cols="" disabled>'+ item.description +'</textarea>'+
							    '</div>'+
								'<br />'+
								'<div class="mui-input-row">'+
							        '<label>审批人:</label>'+
							        '<input type="text" value="'+ item.user.name +'" disabled>'+
							    	'<label>时间:</label>'+
							        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " disabled>'+
							    '</div>'+
							'</div>'+
						'</li>'
					}
					if(i == comLen-1) {
						pHtmlList += '<li class="step_item">' +
							'<div class="step_num">' + step + ' </div>' +
							'<div class="step_num_txt">' +
							'<div class="mui-input-row">' +
							'<label>当前状态:</label>' +
							'<input type="text" value="' + item.purchaseOrderProcess.name + '" disabled>' +
							'</div>' +
							'</div>' +
							'</li>'
					}
				});
				$("#orCheckMenu").html(pHtmlList)
			}
		},
		commodityHtml: function(data) {
			var _this = this;
			var htmlCommodity = '';
			if(data.bizPoHeader.poDetailList) {
				$.each(data.bizPoHeader.poDetailList, function(i, item) {
				var outHtml = '';
				if(data.bizPoHeader.id!=null) {
					outHtml = '<div class="mui-input-row">'+
								'<label>所属单号：</label>'+
								'<input type="text" value="'+ data.bizOrderHeader.orderNumber +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>已供货数量：</label>'+
								'<input type="text" value="'+ item.sendQty +'" disabled>'+
							'</div>'
				}
				if(data.bizPoHeader.id==null) {
					outHtml	= '<div class="mui-input-row">'+
								'<label>申报数量：</label>'+
								'<input type="text" value="'+ item.reqQty +'" disabled>'+
							'</div>'
				}
				htmlCommodity +='<li class="mui-table-view-cell mui-media app_bline app_pr">'+
					/*产品图片*/
					'<div class="photoParent mui-pull-left app_pa">'+
						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'">'+
					'</div>'+
					/*产品信息*/
					'<div class="mui-media-body app_w80p app_fr">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" value="'+ item.skuInfo.productInfo.brandName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" value="'+ item.skuInfo.name +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" value="'+ item.skuInfo.itemNo +'" disabled>'+
						'</div>'+
						outHtml +
						'<div class="mui-input-row">'+
							'<label>采购数量：</label>'+
							'<input type="text" value="'+ item.ordQty +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row" id="unitprice">'+
							'<label>结算价：</label>'+
							'<input type="text" value="'+ item.unitPrice +'" disabled>'+
						'</div>'+
					'</div>'+
				'</li>'
				});
				$("#orCheckCommodity").html(htmlCommodity);
				var unitPriceList=$('#orCheckCommodity #unitprice');
				$.each(unitPriceList,function(z,x){
					if(_this.unitPriceFlag==true){
						$(x).show();
					}else{
						$(x).hide();
					}
				})
			}
			if(data.bizPoHeader.poDetailList==null) {
				console.log(data.bizPoHeader.poDetailList==null)
			}
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

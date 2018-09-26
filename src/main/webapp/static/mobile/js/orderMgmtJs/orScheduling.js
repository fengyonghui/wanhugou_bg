(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.fs == false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			_this.getData();
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
                	var poDetailList = res.data.bizPoHeader.poDetailList;
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
                	_this.btnshow(poDetailList);
                }
			});	
			_this.schedul();
		},
		btnshow: function(data) {
			var _this = this;
			console.log(data)
			$('.schedCommd').hide();
			$('input[type=radio]').on('change', function() {
				if(this.checked && this.value == 1) {
					var htmlPurch = '';
					var htmlSave = '';
					$.each(data, function(i,item) {
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
					$('.schedPurch').show();
					$('.schedCommd').hide();
            		$("#orSchedPurch").html(htmlPurch)
            		$(".saveBtnPt").html(htmlSave)
				}
				if(this.checked && this.value == 2) {
					var htmlCommodity = '';
					var htmlAllSave = '';
					$.each(data, function(i,item) {
					htmlCommodity = '<li class="mui-table-view-cell">'+
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
								'<input type="text" class="inOrdNum mui-input-clear"></div>'+
							'<div class="mui-input-row">'+
								'<label>总待排产量：</label>'+
								'<input type="text" class="inOrdNum mui-input-clear"></div>'+	
							'<div class="mui-input-row">'+
								'<label>已排产数量：</label>'+
								'<input type="text" class="inOrdNum mui-input-clear"></div>'+
							'<button type="submit" class="commdAddBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">添加排产计划</button></div>'+
						'<div class="mui-row">'+
							'<div class="labelLf">排产计划：</div>'+
							'<div class="mui-row app_f13 commdAddCont">'+
								'<div class="mui-row app_bline">'+
									'<div class="mui-input-row">'+
										'<label>完成日期：</label>'+
										'<input type="date" class="inOrdNum"></div>'+
									'<div class="mui-input-row">'+
										'<label>排产数量：</label>'+
										'<input type="text" class="inOrdNum mui-input-clear"></div></div>'+	
									'</div></div></div></li>'
					htmlAllSave = '<button id="allSaveBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">批量保存</button>'			
                	});
            		$('.schedPurch').hide();
					$('.schedCommd').show();
            		$("#orSchedCommd").html(htmlCommodity)
            		$(".saveBtnPt").html(htmlAllSave)
				}
			})
		},
		schedul: function() {
			var _this = this;
			var htmlPurchPlan = '<div class="mui-row app_bline">'+
				'<div class="mui-input-row">'+
					'<label>完成日期：</label>'+
					'<input type="date" class="inOrdNum mui-input-clear"></div>'+
				'<div class="mui-input-row">'+
					'<label>排产数量：</label>'+
					'<input type="text" class="inOrdNum mui-input-clear"></div>'+
				'<button type="submit" class="removeBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">删除</button>'+
			'</div>';
			$(".schedPurch").on("tap", "#purchAddBtn", function() {
//				alert(999)
				$('#purchAddCont').append(htmlPurchPlan);
			})
			$(".schedCommd").on("tap", ".commdAddBtn", function() {
//				alert(999)
				$('.commdAddCont').append(htmlPurchPlan);
			})
			_this.removeSchedul();
		},
		removeSchedul: function() {
			var _this = this;
			$('.mui-content').on('tap', '.removeBtn', function() {
				$(this.parentNode).remove();
			})
		}
	}
	$(function() {
		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
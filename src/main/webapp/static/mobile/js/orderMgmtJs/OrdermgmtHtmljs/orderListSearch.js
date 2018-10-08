(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.datagood = [];
		this.dataSupplier = [];
		this.selectOpen = false
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.hrefHtml('.newinput', '.input_div','#hideSpanAdd' );
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
//			this.ajaxGoodName()
		},
		pageInit: function() {
			var _this = this;
			_this.getData();
			_this.ajaxinvoiceStatus();//发票状态
			_this.ajaxPoStatus();//业务状态
			_this.ajaxorderStatus();//订单来源
			_this.ajaxcheckStatus();//审核状态
		},
		getData: function() {
			var _this = this;
			$('#inSearchBtn').on('tap', function() {
				var optionsBusiness = $("#input_div_business option").eq($("#input_div_business").attr("selectedIndex"))
//				console.log(optionsBusiness)
				if(_this.selectOpen){
						if($('.hasoid').attr('id')){
							_this.sureSelect(optionsBusiness)
						}else{
							mui.toast('请选择匹配的选项')
						}
					
				}else{
					_this.sureSelect(optionsBusiness)
					
				}
				

			})
		},
		sureSelect:function(optionsBusiness){
			var _this = this;
				_this.selectOpen = false
				var optionsClass = $("#input_div_class option").eq($("#input_div_class").attr("selectedIndex"));
				GHUTILS.OPENPAGE({
					url: "../../html/inventoryMagmetHtml/inventoryList.html",
					extras: {
						reqNo: $('.inOrdNum').val(),
						name: $('.inReqNum').val(),
						fromOffice: $('.hasoid').attr('id'),
						bizStatusid: optionsBusiness.val(),
						varietyInfoid: optionsClass.val(),
						isFunc: true
						}
					})
		},
		hrefHtml: function(newinput, input_div,hideSpanAdd) {
			var _this = this;
			_this.ajaxSupplier();//供应商
//			_this.ajaxCheckStatus();

			$(newinput).on('focus', function() {
				//$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$(hideSpanAdd).show()
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}				
				_this.rendHtml(_this.dataSupplier,$(this).val());
			})
			
			$(hideSpanAdd).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$(hideSpanAdd).hide()
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
				$(newinput).val($(this).text())
				$(input_div).hide()
				$(hideSpanAdd).hide()
				_this.selectOpen = true
			})
		},
		rendHtml: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reult.push(item)
					}
				})
			$.each(reult, function(i, item) {
//				console.log(item)
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div').html(htmlList)

		},
		//供应商
		ajaxSupplier: function() {
			var _this = this;
			var htmlSupplier = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeListByPhone',
				data: {
					type: 7
				},
				dataType: 'json',
				success: function(res) {
					_this.dataSupplier = res
					$.each(res, function(i, item) {
						htmlSupplier += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
					});
					$('.input_div').html(htmlSupplier)
				}
			});
		},
		//发票状态
		ajaxinvoiceStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlinvoice = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'biz_order_invStatus'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlinvoice += '<option class="soption"  value="' + item.id + '">' + item.label + '</option>'
					});
					$('#input_div_invoiceStatus').html(optHtml+htmlinvoice);
					
				}
			});
		},
		//业务状态
		ajaxPoStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'biz_po_status'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlClass += '<option class="soption" value="' + item.id + '">' + item.label + '</option>'
					});
					$('#input_div_poStatus').html(optHtml+htmlClass)
				}
			});
		},	
		//订单来源
		ajaxorderStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlClass = '';
//			$.ajax({
//				type: 'GET',
//				url: '/a/sys/dict/listData',
//				data: {type:'biz_po_status'},
//				dataType: 'json',
//				success: function(res) {
//					console.log(res)
//					$.each(res, function(i, item) {
//						console.log(item)
//						htmlClass += '<option class="soption" value="' + item.id + '">' + item.label + '</option>'
//					});
//					$('#input_div_orderStatus').html(optHtml+htmlClass)
//				}
//			});
		},
		//审核状态
		ajaxcheckStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlClass = '';
////			$.ajax({
////				type: 'GET',
////				url: '/a/sys/dict/listData',
////				data: {type:'biz_po_status'},
////				dataType: 'json',
////				success: function(res) {
////					console.log(res)
////					$.each(res, function(i, item) {
////						console.log(item)
////						htmlClass += '<option class="soption" value="' + item.id + '">' + item.label + '</option>'
////					});
////					$('#input_div_orderStatus').html(optHtml+htmlClass)
////				}
////			});
		},
},	
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
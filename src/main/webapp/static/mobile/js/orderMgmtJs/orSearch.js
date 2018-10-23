(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.datagood = [];
		this.dataSupplier = [];
		this.selectOpen = false;
		this.includeTestData = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.hrefHtml('.newinput', '.input_div','#hideSpanAdd' );
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
			this.testData();
		},
		pageInit: function() {
			var _this = this;
			_this.getData();			
			_this.ajaxinvoiceStatus();//发票状态
			_this.ajaxPoStatus();//业务状态
			_this.ajaxcheckStatus();//审核状态
		},
		getData: function() {
			var _this = this;
			$('#inSearchBtn').on('tap', function() {
				if(_this.selectOpen){
					if($('.hasoid').attr('id')){
						_this.sureSelect()
					}else{
						mui.toast('请选择匹配的选项')
					}
				}else{
					_this.sureSelect()
				}
			})
		},
		sureSelect:function(){
			var _this = this;
				_this.selectOpen = false
//				console.log($('#inSupply').val());
//				console.log($('.hasoid').attr('id'));
//				console.log($('#input_div_invoiceStatus').val());
//				console.log($('#input_div_poStatus').val());
//				console.log($('#input_div_orderStatus').val());
//				console.log($('#input_div_poSchType').val());
//				console.log($('#wait_pay').val());
//				console.log($('#apply_pay').val());
				GHUTILS.OPENPAGE({
					url: "../../html/orderMgmtHtml/orderpaymentinfo.html",
					extras: {
						num: $('.inOrdNum').val(),//备货单号
						vendOffice: $('.hasoid').attr('id'),//供应商
						invStatus: $('#input_div_invoiceStatus').val(),//发票状态
						bizStatus: $('#input_div_poStatus').val(),//业务状态
						processTypeStr:$('#input_div_orderStatus').val(),//审核状态
						poSchType:$('#input_div_poSchType').val(),//排产状态
                        waitPay: $('#wait_pay').val(),//待支付
                        applyPayment:$('#apply_pay').val(),//可申请付款
                        includeTestData: _this.includeTestData,//测试数据
                        isFunc: true
						}
				})
		},
		testData:function() {
			var _this = this;
            $('.testCheckbox').on('change',function(){
            	if(this.checked){
            		_this.includeTestData = true
            	}else {
            		_this.includeTestData = false
            	}
	        })
        },
		hrefHtml: function(newinput, input_div,hideSpanAdd) {
			var _this = this;
			_this.ajaxSupplier();//供应商
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
						htmlinvoice += '<option class="soption"  value="' + item.value + '">' + item.label + '</option>'
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
						htmlClass += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#input_div_poStatus').html(optHtml+htmlClass)
				}
			});
		},
		//审核状态
		ajaxcheckStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/biz/po/bizPoHeader/listV2Data4Mobile',
//				data: {type:'biz_po_status'},
				dataType: 'json',
				success: function(res) {
					$.each(res.data.processList, function(i, item) {
						htmlClass += '<option class="soption" value="' + item.name + '">' + item.name + '</option>'
					});
					$('#input_div_orderStatus').html(optHtml+htmlClass)
				}
			});
		},
	},
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
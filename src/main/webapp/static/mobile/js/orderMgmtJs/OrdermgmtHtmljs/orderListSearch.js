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
			this.hrefHtml('.newinput', '.input_div','#hideSpanAdd','#showSpanAdd');
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			_this.testData();
			_this.getData();
			_this.ajaxorderStatus();//订单状态
			_this.ajaxcheckStatus();//审核状态
		},
		getData: function() {
			var _this = this;
			$('#inSearchBtn').on('tap', function() {
				if(_this.selectOpen){
						if($('.hasoid').attr('id')){
							_this.sureSelect();
						}else{
							mui.toast('请选择匹配的选项');
						}					
				}else{
					_this.sureSelect();					
				}
			})
		},
		sureSelect:function(){
			var _this = this;
				_this.selectOpen = false;
				GHUTILS.OPENPAGE({
					url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
					extras: {
						orderNum: $('.inOrdNum').val(),//订单编号
						bizStatus:$('#input_div_ordStatus').val(),//订单状态
                        selectAuditStatus:$('#input_div_cheStatus').val(),//审核状态
                        retainage:$('#input_div_finalMoney').val(),//尾款
                        customerPhone:$('.inOrdPhone').val(),//经销商电话
                        itemNo:$('.inItemNum').val(),//商品货号
                        customerName:$('.hasoid').attr('id'),//经销店名称
                        centersName:$('.centersName').val(),//采购中心
                        conName:$('.centersName').val(),//客户专员
						mobileAuditStatus: $('#input_div_waitchkStatus').val(),//待同意发货
						waitShipments: $('#input_div_waitsendgoods').val(),//待发货
						waitOutput: $('#input_div_outbound').val(),//待出库
						includeTestData: _this.includeTestData,//测试数据
						isFunc: true
					}
				})
		},
		testData:function() {
			var _this = this;
            $('.testCheckbox').on('change',function(){
            	if(this.checked){
            		_this.includeTestData = true;
            	}else {
            		_this.includeTestData = false;
            	}
	        })
        },
		hrefHtml: function(newinput, input_div,hideSpanAdd,showSpanAdd) {
			var _this = this;
			_this.ajaxSupplier();//经销店名称
			$(newinput).on('focus', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show();
				$(hideSpanAdd).show();
				$(showSpanAdd).hide();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false;
				}else{
					_this.selectOpen = true;
				}				
				_this.rendHtml(_this.dataSupplier,$(this).val());
			})
			$(showSpanAdd).on('click', function() {
				$(showSpanAdd).hide();
				$(input_div).show();
				$(hideSpanAdd).show();
			})
			$(hideSpanAdd).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid');
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid');
				$(newinput).val($(this).text());
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
				_this.selectOpen = true;
			})
		},
		rendHtml: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reult.push(item);
					}
				})
			$.each(reult, function(i, item) {
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div').html(htmlList);

		},
		//经销店名称
		ajaxSupplier: function() {
			var _this = this;
			var htmlSupplier = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeListByPhone',
				data: {
					type: 6
				},
				dataType: 'json',
				success: function(res) {
					_this.dataSupplier = res;
					$.each(res, function(i, item) {
						htmlSupplier += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
					});
					$('.input_div').html(htmlSupplier);
				}
			});
		},	
		//订单状态
		ajaxorderStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlOrdstatus = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'biz_order_status'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlOrdstatus += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#input_div_ordStatus').html(optHtml+htmlOrdstatus);
				}
			});
		},
		//审核状态
		ajaxcheckStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlChstatus = '';
			$.ajax({
				type: 'GET',
				url: '/a/biz/order/bizOrderHeader/listData4mobile',
				dataType: 'json',
				success: function(res) {
					$.each(res.data.originConfigMap, function(i, item) {
						htmlChstatus += '<option class="soption" value="' + item+ '">' + item + '</option>'
					});
					$('#input_div_cheStatus').html(optHtml+htmlChstatus);
				}
			});
		},
},	
	$(function() {
		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
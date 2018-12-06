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
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
			this.testData();
		},
		pageInit: function() {
			var _this = this;
			_this.getData();	
			_this.ajaxcommStatus();//审核状态
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
				GHUTILS.OPENPAGE({
					url: "../../../html/orderMgmtHtml/commissionMgmtHtml/alreadlycomList.html",
					extras: {
						customerName:$('.inOrdName').val(),//代销商
						orderNum: $('.inOrdNum').val(),//订单编号
						commissionStatus: $('#input_div_commissionStatus').val(),//结佣状态
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
		//结佣状态
		ajaxcommStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlOrdstatus = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'biz_commission_status'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlOrdstatus += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#input_div_commissionStatus').html(optHtml+htmlOrdstatus);
				}
			});
		},
	},
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
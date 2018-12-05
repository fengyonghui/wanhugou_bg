(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
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
				GHUTILS.OPENPAGE({
					url: "../../html/orderMgmtHtml/ApplicationList.html",
					extras: {
						num: $('.inOrdNum').val(),//备货单号
						processTypeStr:$('#input_div_orderStatus').val(),//审核状态
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
		//审核状态
		ajaxcheckStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/biz/po/bizPoPaymentOrder/listV2ForMobile',
				dataType: 'json',
				success: function(res) {
					$.each(res.data.configMap, function(i, item) {
						htmlClass += '<option class="soption" value="' + item + '">' + item + '</option>'
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
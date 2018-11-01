(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			_this.getData();
		},
		getData: function() {
			var _this = this;
			$('#waterSearchBtn').on('tap', function() {
				var orWaterCouNumSc = $("#orWaterCouNumSc").val(); //订单号
                var waterCouNumSc = $("#waterCouNumSc").val(); //流水号
                if(orWaterCouNumSc == null||orWaterCouNumSc == undefined){
					orWaterCouNumSc == "";
                }
                if(waterCouNumSc == null||waterCouNumSc == undefined) {
                	waterCouNumSc == "";
                }
                if(orWaterCouNumSc == "" && waterCouNumSc == ""){
                	 mui.toast("请输入查询条件！");
                	 return;
                }
				_this.sureSelect()
			})
		},
		sureSelect:function(){
			var _this = this;
			GHUTILS.OPENPAGE({
				url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordwaterCourseList.html",
				extras: {
					orderNum: $("#orWaterCouNumSc").val(),//备货单号
					serialNum: $("#waterCouNumSc").val(),//供应商
					isFunc: true
				}
			})
		}
	}
	$(function() {
		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
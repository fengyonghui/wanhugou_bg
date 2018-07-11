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
//			this.oneHtml();
		},
		getData: function() {
			var _this = this;
			//var dataList = _this.moniData()

			$.ajax({
                type: "GET",
                url: "/a/biz/po/bizPoHeader/form4Mobile",
                data: {id:_this.userInfo.listId},
                dataType: "json",
                success: function(res){
					console.log(res)
                   $('#OrordNum').val(res.data.bizOrderHeader.orderNumber)
                   $('#PoordNum').val(res.data.bizPoHeader.orderNumber)
                   $('#Pototal').val(res.data.bizPoHeader.total)
                   $('#PotoDel').val(res.data.bizPoHeader.totalDetail)
                   $('#PoLastDa').val(res.data.bizPoHeader.lastPayDate)
                   $('#PoRemark').val(res.data.bizPoHeader.remark)
                   $('#PoDizstatus').val(res.data.bizPoHeader.bizStatus)
                   $('#PoVenName').val(res.data.bizPoHeader.vendOffice.name)
                   $('#PoVenBizCard').val(res.data.bizPoHeader.vendOffice.bizVendInfo.cardNumber)
                   $('#PoVenBizPayee').val(res.data.bizPoHeader.vendOffice.bizVendInfo.payee)
                   $('#PoVenBizBankname').val(res.data.bizPoHeader.vendOffice.bizVendInfo.bankName)
//                   $('#ordNum').val(res.data.bizPoHeader.orderNumber)
//                  $('#ordNum').val(res.data.bizPoHeader.orderNumber)
                	
                }
            });
//		_this.herfHTtml()

		}
//		oneHtml:function(){
//			var _this = this;
//			var data = _this.getData()
//			console.log(data)
//			$('#ordNum').val(data.bizOrderHeader.orderNumber)
//			if(data.bizOrderHeader.orderNumber){
//				
//			}
//			$.each(data.bizOrderHeader.orderNumber, function(i, item) {
//						console.log(item.orderNumber)
//                       var orderNumber = item.orderNumber;
//					$('#ordNum').html(orderNumber)
////                        htmlList += 
//			});
////		                    $('#htmlMenu').html(htmlList)
//		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

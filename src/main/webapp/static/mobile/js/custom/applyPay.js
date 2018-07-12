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
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.getData()
		},
		getData: function() {
			var _this = this;
			var url = $(this).attr('url');
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/form4Mobile",
				data: {id:_this.userInfo.listId},
				dataType: "json",
				success: function(res) {
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
//                   $('#codeId').val(res.data.bizPoHeader.process.purchaseOrderProcess.code)
//					var pHtmlList = '';
//					$.each(res.data.resultList, function(i, item) {
//						console.log(item)
//						pHtmlList +=
//					});
//					$("#addBtn").html(pHtmlList)
				}
			});
		_this.hrefHtml()
		},

        hrefHtml: function() {
			var _this = this;
		
            $('#appForm').on('tap','#applyBtn',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
				$.ajax({
					type: "GET",
					url: "/a/biz/po/bizPoHeader/createPay4Mobile",
					data: {
						id:_this.userInfo.listId,
						planPay:$('#planPay').val(),
//						deadline:
					},
					dataType: "json",
					success: function(res) {
						console.log(res)
						if(res.ret==true){
							//$('#mask').hide()
							GHUTILS.OPENPAGE({
							url: "../../mobile/html/purchase.html",
							extras: {
								key:res.key,
								}
							})
						}
						
					}
				});
			})
		}

	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
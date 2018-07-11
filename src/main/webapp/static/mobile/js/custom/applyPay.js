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
//					$('#payId').val(res.data.page.list[0].id)
//					$('#Pototal').val(res.data.page.list[0].total)
//					$('#payTotal').val(res.data.page.list[0].payTotal)
//					$('#PoLastDa').val(res.data.page.list[0].deadline)
//					$('#PoPayTm').val(res.data.page.list[0].payTime)
//					$('#PoStas').val(res.data.page.list[0].bizStatus)
//					$('#PoName').val(res.data.page.list[0].commonProcess.paymentOrderProcess.name)
//					$('#PoImg').val(res.data.page.list[0].imgList)
//					var pHtmlList = '';
//					$.each(res.data.resultList, function(i, item) {
//						console.log(item)
//						pHtmlList +=
//					});
//					$("#addBtn").html(pHtmlList)
				}
			});
//		_this.hrefHtml()
		},

//      hrefHtml: function() {
//			var _this = this;
//		
//          $('#addBtn').on('tap','.detailBtn',function(){
//          	var url = $(this).attr('url');
//				var listId = $(this).attr('listId');
//              if(url) {
//              	mui.toast('子菜单不存在')
//              }else if(listId==listId) {
//              	GHUTILS.OPENPAGE({
//						url: "../../mobile/html/details.html",
//						extras: {
//								listId:listId,
//						}
//					})
//              }
//			})
//		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
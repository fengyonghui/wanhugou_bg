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
			this.getData();
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
		    _this.ajaxData()
		},
		getData: function() {
			var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeader/form4Mobile",
                data: {id: _this.userInfo.inListId},
                dataType: "json",
                success: function(res){
                	console.log(res)
                	var shouldPayNum = res.data.entity.totalMoney - res.data.entity.recvTotal
                	$('#shouldPay').val(shouldPayNum)
                }
            });
		},
		
		ajaxData: function() {
			var _this = this;
             $('#inPayBtn').on('tap',function(){
                
                $.ajax({
                    type: "GET",
                    url: "/a/biz/request/bizRequestPay/genPayQRCode",
                    data: {
                    	payMoney:$('#inPayNum').val(),
                    	reqId:_this.userInfo.inListId,
                    	payMethod:$('payMode').val()
                    },
                    dataType: "json",
                    success: function(res){
                    	console.log(res)
                    }
                });
			})
        _this.hrefHtml()
        },

        hrefHtml: function() {
			var _this = this;
		/*采购单管理*/
            $('#menuMaget').on('click','.childMenu',function(){
            	var url = $(this).attr('url');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(purchId==132) {
                	GHUTILS.OPENPAGE({
						url: "../html/purchaseMagmetHtml/purchase.html",
						extras: {
								purchId:purchId,
						}
					})
                }
			})
        /*备货单管理*/
            $('#menuMaget').on('click','.childMenu',function(){
            	var url = $(this).attr('url');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(purchId==229) {
                	GHUTILS.OPENPAGE({
						url: "../html/inventoryMagmetHtml/inventoryList.html",
						extras: {
								purchId:purchId,
						}
					})
                }
			})
		}
		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

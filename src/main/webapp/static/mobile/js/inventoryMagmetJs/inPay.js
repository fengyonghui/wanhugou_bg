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
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
		    _this.paymentMode();
		    _this.hrefHtml()
		},
		paymentMode: function() {
			var _this = this;
		//支付宝支付
			$('#zfbPayBtn').on('tap', function() {
				alert('欢迎使用支付宝')
//				_this.zhifubao()
			}),
		//微信支付
			$('#wxPayBtn').on('tap', function() {
				var ua = window.navigator.userAgent.toLowerCase();
			    if(ua.match(/MicroMessenger/i) == 'micromessenger' || ua.match(/_SQ_/i) == '_sq_'){
			        //微信里面打开
			        alert('欢迎使用微信内部打开支付')
	//		        _this.wxIn()
			    }else{
			    	//微信外面打开
			    	alert('欢迎使用微信外部打开支付')
	//		        _this.wxOut()
			    }
			})
		},
		zhifubao: function() {
			var _this = this;
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
//                  	console.log(res)
                }
            });
        },
        wxIn: function() {
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
//                  	console.log(res)
                    }
                });
			})
        },
        wxOut: function() {
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
                    }
                });
			})
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

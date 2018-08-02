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
		},
		paymentMode: function() {
			var _this = this;
			var is_weixin = (function(){return navigator.userAgent.toLowerCase().indexOf('micromessenger') !== -1})();
			if(is_weixin){
			//微信里面打开
				alert('微信内部支付')
				if($('#inPayNum').val() == '') {
					mui.toast('请输入支付金额！')
				}else {
			        $('#wxPayBtn').on('click', function() {
				    	 $.ajax({
			                type: "GET",
			                url: "/a/biz/request/bizRequestPay/wechatPay4JSAPI",
			                data: {
			                	payMoney:$('#inPayNum').val(),
			                	reqId:_this.userInfo.inListId,
			                },
			                dataType: "json",
			                success: function(res){
		                    	console.log(res)
			                }
			            })
					})
				}
			}else{
			//微信外面打开
		    	alert('微信外部支付')
		    	//支付宝支付
				$('#zfbPayBtn').on('tap', function() {
					if($('#inPayNum').val() == '') {
						mui.toast('请输入支付金额！')
					}else {
						alert('支付宝支付')
						$.ajax({
		                    type: "GET",
		                    url: "/a/biz/request/bizRequestPay/alipayForH5",
		                    data: {
		                    	payMoney:$('#inPayNum').val(),
			                	reqId:_this.userInfo.inListId,
		                    },
		                    dataType: "json",
		                    success: function(res){
		                  		console.log(res)
		                  		GHUTILS.OPENPAGE({
									url: "https://openapi.alipay.com/gateway.do?charset=UTF-8&method=alipay.trade.wap.pay&sign=sSZPztJA7RgtVmn%2FknHdEjjFprxgvsWxeEQutn8qwRF9cCIGy1UMlBlq0q3InUNd1PuW%2BpDZYGSu081e3ibPmBoxclc%2Bto0OLMqGn55BRAhI36Gggn6mSnhb4aaDDSIMLHdNctuekqnA5JW3qZaMkUMmjrn0hc0LENb2nQ0hM2dji7kseA38wsDh0jQTBZ9QODLHfrzezeBd1ut2%2FuOY7PniYe1zvzvrHE3KR7Ozt1GhM%2BhPs7nOmQLLZWUaeyII9ccrdV8wSfIIR1JUh2Q6GKlMRYxM5%2BMBUPBS2PlCsDKFhlQrEb8huBmxx4n125PDpQKPNDFFAgCK8%2F5hKWt1QA%3D%3D&notify_url=http%3A%2F%2Fdreamer.ngrok.xiaomiqiu.cn%2Fpayment%2Falipay%2Fnotify&version=1.0&app_id=2017121200617602&sign_type=RSA2&timestamp=2018-08-02+17%3A00%3A44&alipay_sdk=alipay-sdk-java-dynamicVersionNo&format=json",
									extras: {
										payMoney:payMoney,
										reqId:reqId
									}
								})
		                    }
		                })
					}
				}),
				//微信支付
				$('#wxPayBtn').on('tap', function() {
					if($('#inPayNum').val() == '') {
						mui.toast('请输入支付金额！')
					}else {
						alert('微信支付')
						$.ajax({
		                    type: "GET",
		                    url: "/a/biz/request/bizRequestPay/genPayQRCode",
		                    data: {
		                    	payMoney:$('#inPayNum').val(),
			                	reqId:_this.userInfo.inListId,
		                    },
		                    dataType: "json",
		                    success: function(res){
		                  	console.log(res)
		                    }
		                })
					}
				})
			}
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

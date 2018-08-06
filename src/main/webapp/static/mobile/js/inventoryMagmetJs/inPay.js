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
			                	openid:oYFKb0Sy0Hs7c6iSvoUOFS4RbIMo
			                },
			                dataType: "json",
			                success: function(res){
		                    	console.log(res)
		                    	var btnArray = ['是','否']
		                    	mui.confirm('你好',res,btnArray,function(){
		                    		
		                    	})
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
						var payMoney = $('#inPayNum').val();
						var reqId = _this.userInfo.inListId;
						window.open('/a/biz/request/bizRequestPay/alipayForH5?payMoney='+payMoney+'&reqId='+reqId)
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
		                    url: "",
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

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
			(function($){
		    $(".mui-scroll-wrapper").scroll({
		          bounce: false,//滚动条是否有弹力默认是true
		          indicators: true, //是否显示滚动条,默认是true
		    }); 
})(mui);
		},
		pageInit: function() {
			var _this = this;
			_this.ajaxData();
			_this.getData();		    
		},
		ajaxData: function() {
			var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/listData",
                data: {parentId:_this.userInfo.idData},
                dataType: "json",
                success: function(res){
//              	console.log(res)
                    var htmlList = '';
                    $.each(res.data, function(i, item) {
                    	if(item.mobileUrl == -1) {
//                  		console.log(item)
                    		htmlList += '<li class="mui-table-view-cell mui-collapse menuBtn" indexNum = "'+ i+'" dataId="'+item.id+'">'+
							'<a class="mui-navigate-right">'+ item.name + '</a>'+
							'<div  class = "mui-collapse-content childData'+ i+'">'
							'</div>'+
							'</li>'
                    	}
                    });
                	$('#menuMaget').html(htmlList)
                }
            });
		},
		getData: function() {
			var _this = this;
            $('#menuMaget').on('tap','.menuBtn',function(){
                var dataId = $(this).attr('dataId');
                var indexNum = $(this).attr('indexNum');               
                if(dataId){
                    $.ajax({
                        type: "GET",
                        url: "/a/sys/menu/listData",
                        data: {parentId:dataId},
                        dataType: "json",
                        success: function(res){
                            var pHtmlList = '';
                            $.each(res.data, function(i, item) {
                            	console.log(item)
                                if(item.mobileUrl){
                                	pHtmlList += '<p class="childMenu" purchId="'+item.id+'" mobileUrl="'+item.mobileUrl+'">'+ item.name+'</p>'
                                }
                            });
                            $(".childData"+indexNum).html(pHtmlList)
                        }
                    });
				}/*else {
                	mui.toast('没有子菜单')
				}*/
			})
        _this.hrefHtml()
        },

        hrefHtml: function() {
			var _this = this;
		/*采购单管理*/
            $('#menuMaget').on('click','.childMenu',function(){
            	var url = $(this).attr('url');
				var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(mobileUrl == '/mobile/html/purchaseMagmetHtml/purchase.html') {
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
				var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(mobileUrl == '/mobile/html/inventoryMagmetHtml/inventoryList.html') {
                	GHUTILS.OPENPAGE({
						url: "../html/inventoryMagmetHtml/inventoryList.html",
						extras: {
								purchId:purchId,
						}
					})
                }
			})
        /*员工管理*/
            $('#menuMaget').on('click','.childMenu',function(){
            	var url = $(this).attr('url');
				var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(mobileUrl == '/mobile/html/staffMgmtHtml/staffList.html') {
                	GHUTILS.OPENPAGE({
						url: "../html/staffMgmtHtml/staffList.html",
						extras: {
								purchId:purchId,
								
						}
					})
                }
			})    
        /*订单管理*/
            $('#menuMaget').on('click','.childMenu',function(){
            	var url = $(this).attr('url');
            	var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(mobileUrl == '/mobile/html/orderMgmtHtml/OrdermgmtHtml/orderList.html') {
                	GHUTILS.OPENPAGE({
						url: "../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
						extras: {
								purchId:purchId,
						}
					})
                }
			})
            /*订单支出信息*/
            $('#menuMaget').on('click','.childMenu',function(){
            	var url = $(this).attr('url');
				var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(mobileUrl == '/mobile/html/orderMgmtHtml/orderpaymentinfo.html') {
                	GHUTILS.OPENPAGE({
						url: "../html/orderMgmtHtml/orderpaymentinfo.html",
						extras: {
								purchId:purchId,
						}
					})
                }
			})
            //线下支付订单
            $('#menuMaget').on('click','.childMenu',function(){
            	var url = $(this).attr('url');
				var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(mobileUrl == '/mobile/html/orderMgmtHtml/OrderUnlinemgmtHtml/orderList.html') {
                	GHUTILS.OPENPAGE({
						url: "../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
						extras: {
							purchId:purchId,
							statu:'unline',
						}
					})
                }
			})
        /*会员管理*/
//          $('#menuMaget').on('click','.childMenu',function(){
//          	var url = $(this).attr('url');
//				var purchId = $(this).attr('purchId');
//              if(url) {
//              	mui.toast('子菜单不存在')
//              }else if(purchId==169) {
//              	GHUTILS.OPENPAGE({
//						url: "../html/memberMgmtHtml/memberList.html",
//						extras: {
//								purchId:purchId,
//						}
//					})
//              }
//			})
		}
		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

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
		},
		ajaxData: function() {
			var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/listData",
                data: {parentId:_this.userInfo.idData},
                dataType: "json",
                success: function(res){
                    var htmlList = '';
                    $.each(res.data, function(i, item) {
                    	if(item.mobileUrl == -1) {
                    		htmlList += '<li class="mui-table-view-cell mui-collapse menuBtn" indexNum = "'+ i+'" dataId="'+item.id+'">'+
							'<a class="mui-navigate-right" indexNum = "'+ i+'" dataId="'+item.id+'">'+ item.name + '</a>'+   
                            '<ul class = "mui-collapse-content app_color40 childData'+ i+'">'+
                            '</ul>'+
							'</li>'
                    	}
                    });
                	$('#menuMaget').html(htmlList);
                	_this.getData();
                }
            });
		},
		getData: function() {
			var _this = this;
			$('.menuBtn>a').on('tap',function(){ 				
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
                                if(item.mobileUrl){                              	
                                    pHtmlList += '<li class="mui-table-view-cell mui-collapse childMenu" mobileUrl="'+item.mobileUrl+'" purchid="'+item.id+'">'+
										'<a class="" purchid="'+item.id+'" mobileUrl="'+item.mobileUrl+'" style="color:#8f8f94">'+ item.name + '</a>'+   
			                            '<ul class = "mui-collapse-content cMenu">'+
			                            '</ul>'+
							        '</li>' 
                                } 
                            });
                            $(".childData"+indexNum).html(pHtmlList);
                            var sArr=$(".childData"+indexNum+'>'+'.childMenu');
		                    $.each(sArr, function(i, items) {
		                    	var mobileUrls = $(this).attr('mobileurl');
		                    	if(mobileUrls=='/mobile/html/orderMgmtHtml/commissionMgmtHtml'){
		                        	$(this).children('a').attr('class','mui-navigate-right');                   	
		                        	$(this).attr('id','commission');
		                        }
		                    });
		                    _this.ulThird();
                        }
                    })                   
                }  
                //菜单收缩以及背景颜色控制
                var getUl=$(this).next('ul').children('li').length;
                if(getUl==0){
                	$(this).next('ul').css('display','none');
                }
	            if ($(this).next('ul').css('display') == "none") {
	            	$(this).parent().css('background','#eee');
	                $(this).parent().siblings('li').css('background','#fff');               
	            }
	            if ($(this).parent().css('background','#eee')&&$(this).next('ul').css('display') == "block") {
	            	$(this).parent().css('background','#fff');             
	            }
	            if ($(this).next('ul').css('display') == "none"||getUl==0) {
                    $(this).next('ul').css('display','block')
	                $(this).parent().siblings('li').children('ul').hide();
	            }else{
	                $(this).next('ul').hide();
	                $(this).parent().siblings('li').children('ul').hide();
	            }
	            if ($(this).next('ul').children('li').find('.cMenu').children('li').length>0) {
                    $(this).next('ul').children('li').find('.cMenu').hide();
	            }
			})
        },
        ulThird:function(){
        	var _this = this;
        	$('.childMenu>a').on('tap',function(){
        		var url = $(this).attr('url');
				var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
                if(url) {
                	mui.toast('子菜单不存在')             	
                }else if(mobileUrl == '/mobile/html/orderMgmtHtml/commissionMgmtHtml') {               	
                	$.ajax({
                        type: "GET",
                        url: "/a/sys/menu/listData",
                        data: {parentId:purchId},
                        dataType: "json",
                        success: function(res){
                            var pHtmlLists = '';
                            $.each(res.data, function(i, ite) {
                                if(ite.mobileUrl){
                                	pHtmlLists += '<li class="mui-table-view-cell mui-collapse " mobileUrl="'+ite.mobileUrl+'">'+
						                '<a class="comMenu" purchId="'+ite.id+'" mobileUrl="'+ite.mobileUrl+'" style="color:#8f8f94">'+ ite.name + '</a>'+
							        '</li>'
                                }
                            });
                            $('#commission .cMenu').html(pHtmlLists);
                            var dArr=$('.cMenu li');
		                    $.each(dArr, function(i, items) {
		                    	var mobileUrls = $(this).attr('mobileurl');		                    	        if(mobileUrls=='/mobile/html/orderMgmtHtml/commissionMgmtHtml/commProportion.html'){
		                        	$(this).attr('id','commiss');		                        	
		                        }
		                    });
                            _this.getDataTwo();
                        }
                   });
                }
                //三级菜单收缩控制
                var getUll=$(this).next().find('li').length;
	            if ($(this).next().css('display') == "none"||getUll==0) {
	                $(this).next('ul').show();               
	            }else{
	                $(this).next('ul').hide();
                }	            
	            var url = $(this).attr('url');
            	var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
				/*订单管理*/
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(mobileUrl == '/mobile/html/orderMgmtHtml/OrdermgmtHtml/orderList.html') {
                	GHUTILS.OPENPAGE({
						url: "../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
						extras: {
							purchId:purchId,
						}
					})
                };
                /*备货单管理*/
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
                /*员工管理*/          
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
                /*采购单管理*/
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
                /*员工管理*/
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
                /*订单支出信息*/
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
                //线下支付订单
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
                //支付申请列表
                if(url) {
                	mui.toast('子菜单不存在')             	
                }else if(mobileUrl == '/mobile/html/orderMgmtHtml/ApplicationList.html') {
                	GHUTILS.OPENPAGE({
						url: "../html/orderMgmtHtml/ApplicationList.html",
						extras: {
							purchId:purchId,
						}
					})
                }
            });          
        },        
		getDataTwo:function(){
	        $('.cMenu').on('tap','.comMenu',function(){
	        	var url = $(this).attr('url');
				var mobileUrl = $(this).attr('mobileUrl');
				var purchId = $(this).attr('purchId');
	            if(url) {
	            	mui.toast('子菜单不存在')             	
	            }else if(mobileUrl == 'mobile/html/orderMgmtHtml/commissionMgmtHtml/commissionList.html'){ 
	            	//佣金管理
	            	GHUTILS.OPENPAGE({
						url: "../html/orderMgmtHtml/commissionMgmtHtml/commissionList.html",
						extras: {
							purchId:purchId,
						}
					})
	            }else if(mobileUrl == '/mobile/html/orderMgmtHtml/commissionMgmtHtml/applyKnotList.html'){
	            	//申请结佣
	            	GHUTILS.OPENPAGE({
						url: "../html/orderMgmtHtml/commissionMgmtHtml/alreadlycomList.html",
						extras: {
							purchId:purchId,
							isFin:true
						}
					})
	            }else if(mobileUrl == '/mobile/html/orderMgmtHtml/commissionMgmtHtml/commProportion.html'){
	            	//佣金比例设置
	            	GHUTILS.OPENPAGE({
						url: "../html/orderMgmtHtml/commissionMgmtHtml/commProportion.html",
						extras: {
							purchId:purchId,
							isFin:true
						}
					})
	            }
	            return false;
			})
		}		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

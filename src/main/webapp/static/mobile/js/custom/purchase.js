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
			this.getData(); //获取数据
//			this.btnshow()
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
	
		},
		getData: function() {
			var _this = this;
			var purchId = this.userInfo.purchId
//	                if(dataId){
            $.ajax({
                type: "GET",
                url: "/a/biz/po/bizPoHeader/listData4Mobile",
                data: {idData:purchId},
                dataType: "json",
                success: function(res){
                    console.log(res)
                    var pHtmlList = '';
                    $.each(res.data, function(i, item) {
                        console.log(item)
                        pHtmlList += '<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg">' +
							'<div class="content_part mui-row app_text_indt15 app_bline">'+
						        '<div class="mui-col-sm-4 mui-col-xs-4">'+
						            '<li class="mui-table-view-cell">采购单号 </li>'+
						            '<li class="mui-table-view-cell">供应商</li>'+
								'</div>'+
						        '<div class="mui-col-sm-8 mui-col-xs-8">'+
						            '<li class="mui-table-view-cell"> + 'item1.orderNum' + </li>'+
						            '<li class="mui-table-view-cell"> + 'item2.vendOffice' + </li>'+
			    		        '</div>'+
						    '</div>'+
							'<div class="app_font_cl content_part mui-row app_text_center">'+
								'<div class="mui-col-xs-3">'+
									'<li class="mui-table-view-cell">开启审核</li>'+
								'</div>'+
								'<div class="mui-col-xs-4">'+
									'<li class="mui-table-view-cell">支付申请列表</li>'+
								'</div>'+
								'<div class="mui-col-xs-2">'+
									'<li class="mui-table-view-cell">取消</li>'+
								'</div>'+
								'<div class="mui-col-xs-3 detailBtn">'+
									'<li id="details" class="mui-table-view-cell">详情</li>'+
								'</div>'
							'</div>'+
					    '</div>'

                    });
                        $("#addBtn").html(pHtmlList)
                    }
            });
/*				}else {
                	mui.toast('没有子菜单')
				}*/
			//运营管理
/*			$("#details").off().on("tap", function() {
					GHUTILS.OPENPAGE({
						url: "../../html/details.html",
						extras: {
								actionUrl:'FFFF'
							}
					})
			});*/
		_this.herfHTtml()
		},
		
		herfHTtml:function(){
			var _this = this;
			$('.detailBtn').on('click','#details',function(){
				var url = $(this).attr('url');
                var purchId = $(this).attr('purchId');
				if(url){
					mui.toast('子菜单不存在')
				}else  if(purchId==){
					GHUTILS.OPENPAGE({
					url: "../../mobile/html/details.html",
                        extras: {
                            purchId:purchId ,
						}
				})
				}
			})
		},
/*		btnshow:function(){
			$('#search_btn').on('tap',function(){
					mui('.mui-off-canvas-wrap').offCanvas().toggle()
			})
			$('.closeBtn').on('tap',function(){
					mui('.mui-off-canvas-wrap').offCanvas().toggle()
			})
		}*/
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

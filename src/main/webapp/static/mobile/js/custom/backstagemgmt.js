(function($) {
		var ACCOUNT = function() {
			this.ws = null;
			this.userInfo = null;
			this.historySY = '--';
			this.totalVal = '--';
			this.yincome = '--';
			this.t0 = '--';
			this.tn = '--';
			this.canuse = 0;
			this.invitecode = null;
			this.expTipNum = 0;
			return this;
		}
		ACCOUNT.prototype = {
				init: function() {
					this.pageInit(); //页面初始化
					this.getData(); //获取数据
					GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
					//GHUTILS.nativeUI.showWaiting()//开启
				},
				pageInit: function() {
					var _this = this;
//					_this.ajaxData()

				},
				getData: function() {
					var _this = this;
					//var dataList = _this.moniData()

                    $.ajax({
                        type: "GET",
                        url: "/a/sys/menu/listData",
                        data: {parentId:1},
                        dataType: "json",
                        success: function(res){
							console.log(res)
                            var htmlList = '';
                            $.each(res.data, function(i, item) {
                                console.log(item)
                                htmlList += '<li class="mui-table-view-cell mui-col-xs-4  text-align-center app_pl0 app_border_rb app_prot0"  idData="'+item.id +'" url="'+item.url +'">' +
                                    '<div>' +
                                    '<div class="app_icon_myt0 app_color_myt0 app_pt10 app_f30"></div>' +
                                    '<div class="app_mt10">' + item.name + '</div>' +
                                    '<div class="app_cgray app_mb20"><span>&nbsp;</span></div>' +
                                    '</div>' +
                                    '</li>'
                            });
                            $('#htmlMenu').html(htmlList)
                        }
                    });
				_this.herfHTtml()

				},

				herfHTtml:function(){
					var _this = this;
					$('#htmlMenu').on('click','.app_prot0',function(){
						var url = $(this).attr('url');
                        var idData = $(this).attr('idData');
						if(url){
							mui.toast('子菜单不存在')
						}else  if(idData==110){
							GHUTILS.OPENPAGE({
							url: "../../mobile/html/purchaseMagmetHtml/management.html",
                                extras: {
                                    idData:idData ,

                                }
						})
						}
					})
				},
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
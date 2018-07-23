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
				_this.userajaxData()

			},
			userajaxData: function() {
				var _this = this;
				$.ajax({
                    type: "GET",
                    url: "/a/getUser",
                    dataType: "json",
                    success: function(res){
//						console.log(res)
						$('#userName').html('您好 ! '+ res.data.name)
                    }
                });
                _this.userComfirDialig()
			},
			
			getData: function() {
				var _this = this;

                $.ajax({
                    type: "GET",
                    url: "/a/sys/menu/listData",
                    data: {parentId:1},
                    dataType: "json",
                    success: function(res){
//						console.log(res)
                        var htmlList = '';
                        $.each(res.data, function(i, item) {
//                          console.log(item)
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
				$('#htmlMenu').on('click','.app_prot0',function(){
					var url = $(this).attr('url');
                    var idData = $(this).attr('idData');
					if(url){
						mui.toast('子菜单不存在')
					}else  if(idData==110){
						GHUTILS.OPENPAGE({
						url: "../html/management.html",
                            extras: {
                                idData:idData ,
                            }
						})
					}
				})
				/*用户信息*/
				$('.mui-bar').on('click','.mui-icon-bars',function(){
	            	var url = $(this).attr('url');
					var purchId = $(this).attr('purchId');
					GHUTILS.OPENPAGE({
						url: "../html/user.html",
						extras: {
							purchId:purchId,
						}
					})
				})
			},
			userComfirDialig: function() {
//			var rejectBtn = document.getElementById("rejectBtn");
				document.getElementById("appQuit").addEventListener('tap', function() {
					var btnArray = ['取消', '确定'];
					mui.confirm('确定要注销当前账号？', '确定注销', btnArray, function(choice) {
						if(choice.index == 1) {
	                        window.location.href = "/a/logout";
						} else {
							
						}
					})
				});
			}
		}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
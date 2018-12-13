(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.datagood = [];
		this.selectOpen = false
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.hrefHtml('.newinput', '.input_div','#hideSpanAdd','#showSpanAdd');
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			_this.getData();
		},
		getData: function() {
			var _this = this;
			$('#inSearchBtn').on('tap', function() {
				if(_this.selectOpen){
					if($('.hasoid').attr('id')){
						_this.sureSelect()
					}else{
						mui.toast('请选择匹配的选项')
					}					
				}else{
					_this.sureSelect()					
				}
			})
		},
		sureSelect:function(){
			var _this = this;
				_this.selectOpen = false;			
				GHUTILS.OPENPAGE({
					url: "../../html/memberMgmtHtml/memberList.html",
					extras: {
						id: $('.hasoid').attr('id'),
						phone: $('.inReqPhone').val(),
						isFunc: true
					}
				})
		},
		hrefHtml: function(newinput, input_div,hideSpanAdd,showSpanAdd) {
			var _this = this;
			_this.ajaxSupplier();//经销店名称
			$(newinput).on('focus', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show();
				$(hideSpanAdd).show();
				$(showSpanAdd).hide();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false;
				}else{
					_this.selectOpen = true;
				}				
				_this.rendHtml(_this.dataSupplier,$(this).val());
			})
			$(showSpanAdd).on('click', function() {
				$(showSpanAdd).hide();
				$(input_div).show();
				$(hideSpanAdd).show();
			})
			$(hideSpanAdd).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid');
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid');
				$(newinput).val($(this).text());
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
				_this.selectOpen = true;
			})
		},
		rendHtml: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reult.push(item)
					}
				})
			$.each(reult, function(i, item) {
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div').html(htmlList)

		},
		ajaxSupplier: function() {
			var _this = this;
			var htmlList = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeListByPhone',
				data: {
					type: 6
				},
				dataType: 'json',
				success: function(res) {
					_this.dataSupplier = res
					$.each(res, function(i, item) {
//						console.log(item)
						htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
					});
					$('.input_div').html(htmlList)
				}
			});

		},
		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
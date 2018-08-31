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
			this.hrefHtml('.newinput', '.input_div','#cpySchRelHideSpan');
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
		},
		getData: function() {
			var _this = this;
			$('#staRelSerhBtn').on('tap', function() {	
				var customsNamVal=$('.newinput').val();
				var consultantsMobileVal=$('#staRelMobile').val();
				if(customsNamVal == null||customsNamVal == undefined){
					customsNamVal == "";
                }
                if(consultantsMobileVal == null||consultantsMobileVal == undefined) {
                	consultantsMobileVal == "";
                }
                if(customsNamVal == ""&&consultantsMobileVal == ""){
                	 mui.toast("请输入查询条件！");
                	 return;
                }
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
		trim:function(str) {
			return str.replace(/(^\s+)|(\s+$)/g, "");
		},
		sureSelect:function(){
			var _this = this;
			var consultantsid = _this.userInfo.consultantsIdTxt;
			_this.selectOpen = false
			
			GHUTILS.OPENPAGE({
				url: "../../../html/staffMgmtHtml/relevanceHtml/relList.html",
				extras: {
					customsIds: $('.hasoid').attr('id'),
					consultantsMobile: $('#staRelMobile').val(),
					sehConsultantsid:consultantsid,
					isFunc: true
				}
			})
		},
		hrefHtml: function(newinput, input_div, cpySchRelHideSpan) {
			var _this = this;
			_this.ajaxGoodList()

			$(newinput).on('focus', function() {
				//$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show();
				$(cpySchRelHideSpan).show();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				_this.rendHtml(_this.datagood,$(this).val());
			})
			$(cpySchRelHideSpan).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid');
				$(input_div).hide();
				$(cpySchRelHideSpan).hide();
			})
			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid');
				$(newinput).val($(this).text());
				$(input_div).hide();
				$('#cpySchRelHideSpan').hide();
				_this.selectOpen = true
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
		ajaxGoodList: function() {
			var _this = this;
			var htmlList = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeList',
				data: {
					type: 6,
				},
				dataType: 'json',
				success: function(res) {
					_this.datagood = res
					$.each(res, function(i, item) {
						htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
					});
					$('.input_div').html(htmlList)
				}
			});
		_this.getData()
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
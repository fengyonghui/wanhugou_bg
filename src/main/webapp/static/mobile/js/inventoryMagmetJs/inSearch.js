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
			this.hrefHtml('.newinput', '.input_div','#inHideSpan' );
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
			this.ajaxGoodName()
		},
		pageInit: function() {
			var _this = this;
		},
		getData: function() {
			var _this = this;
			$('#inSearchBtn').on('tap', function() {
				var optionsBusiness = $("#input_div_business option").eq($("#input_div_business").attr("selectedIndex"))
//				console.log(optionsBusiness)
				if(_this.selectOpen){
						if($('.hasoid').attr('id')){
							_this.sureSelect(optionsBusiness)
						}else{
							mui.toast('请选择匹配的选项')
						}
				}else{
					_this.sureSelect(optionsBusiness)
				}
			})
		},
		sureSelect:function(optionsBusiness){
			var _this = this;
			_this.selectOpen = false
			var optionsClass = $("#input_div_class option").eq($("#input_div_class").attr("selectedIndex"));
			GHUTILS.OPENPAGE({
				url: "../../html/inventoryMagmetHtml/inventoryList.html",
				extras: {
					reqNo: $('.inOrdNum').val(),
					name: $('.inReqNum').val(),
					fromOffice: $('.hasoid').attr('id'),
					bizStatusid: optionsBusiness.val(),
					varietyInfoid: optionsClass.val(),
					isFunc: true
				}
			})
		},
		hrefHtml: function(newinput, input_div,inHideSpan) {
			var _this = this;
			_this.ajaxGoodList()
			_this.ajaxCheckStatus()

			$(newinput).on('focus', function() {
				//$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$(inHideSpan).show()
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				_this.rendHtml(_this.datagood,$(this).val())
			})
			
			$(inHideSpan).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$(inHideSpan).hide()
			})
			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
				$(newinput).val($(this).text())
				$(input_div).hide()
				$('#inHideSpan').hide()
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
//				console.log(item)
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
					type: 8
				},
				dataType: 'json',
				success: function(res) {
					_this.datagood = res
					$.each(res, function(i, item) {
//						console.log(item)
						htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
					});
					$('.input_div').html(htmlList)
				}
			});
		},
		ajaxCheckStatus: function() {
			var _this = this;
			var optHtml ='<option value="">全部</option>';
			var htmlBusiness = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'biz_req_status'},
				dataType: 'json',
				success: function(res) {
//					console.log(res)
					$.each(res, function(i, item) {
//						console.log(item)
						htmlBusiness += '<option class="soption"  value="' + item.value + '">' + item.label + '</option>'
					});
					$('#input_div_business').html(optHtml+htmlBusiness)
					_this.getData()
				}
			});
		},
		ajaxGoodName: function() {
			var _this = this;
			var optHtml ='<option value="">全部</option>';
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/biz/request/bizRequestHeader/list4Mobile',
				data: {},
				dataType: 'json',
				success: function(res) {
//					console.log(res)
					$.each(res.data.varietyInfoList, function(i, item) {
//						console.log(item)
						htmlClass += '<option class="soption" value="' + item.id + '">' + item.name + '</option>'
					});
					$('#input_div_class').html(optHtml+htmlClass)
					_this.getData()
				}
			});
		},
	}
	$(function() {
		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
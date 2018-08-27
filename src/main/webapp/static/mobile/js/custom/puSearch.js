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
			this.hrefHtml('.newinput', '.input_div', );
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			

		},
		getData: function() {
			var _this = this;
			$('#puSearchBtn').on('tap', function() {
				var options = $("#input_div_check option").eq($("#input_div_check").attr("selectedIndex"))
//				console.log(options)
				if(_this.selectOpen){
						if($('.hasoid').attr('id')){
							_this.sureSelect(options)
						}else{
							mui.toast('请选择匹配的选项')
						}
					
				}else{
					_this.sureSelect(options)
					
				}
				

			})
		},
		sureSelect:function(options){
			var _this = this;
				_this.selectOpen = false
			GHUTILS.OPENPAGE({
						url: "../../html/purchaseMagmetHtml/purchase.html",
						extras: {
							orderNum: $('.ordNum').val(),
							num: $('.detaNum').val(),
							vendOffice: $('.hasoid').attr('id'),
							commonProcess: options.val(),
							isFunc: true
						}
					})
		},
		hrefHtml: function(newinput, input_div) {
			var _this = this;
			_this.ajaxGoodList()
			_this.ajaxCheckStatus()

			$(newinput).on('focus', function() {
				//$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$('#hideSpan').show()
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				
				_this.rendHtml(_this.datagood,$(this).val())
			})
			
			$('#hideSpan').on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$('#hideSpan').hide()
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
				$(newinput).val($(this).text())
				$(input_div).hide()
				$('#hideSpan').hide()
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
				console.log(item)
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
					type: 7
				},
				dataType: 'json',
				success: function(res) {
					_this.datagood = res
					console.log(res)
					$.each(res, function(i, item) {
						console.log(item)
						htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
					});
					$('.input_div').html(htmlList)
				}
			});
		},
		ajaxCheckStatus: function() {
			var _this = this;
			var optHtml ='<option value="">全部</option>';
			var htmlCheck = ''
			$.ajax({
				type: 'GET',
				url: '/a/biz/po/bizPoHeader/listData4Mobile',
				data: {},
				dataType: 'json',
				success: function(res) {
					console.log(res)
					$.each(res.data.processList, function(i, item) {
						console.log(item)
						htmlCheck += '<option class="soption" value="' + item.code + '" roleEnNameEnum="' + item.roleEnNameEnum + '" passCode="' + item.passCode + '" rejectCode="' + item.rejectCode + '">' + item.name + '</option>'
					});
					$('#input_div_check').html(optHtml+htmlCheck)
					_this.getData()
				}
			});
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
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
			this.hrefHtml('.newinput', '.input_div','#staOrdHideSpan' );
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
			this.ajaxGoodName()
		},
		pageInit: function() {
			var _this = this;
		},
		//点击查询
		getData: function() {
			var _this = this;
			$('#inSearchBtn').on('tap', function() {
				var optionsBusiness = $("#input_div_business option").eq($("#input_div_business").attr("selectedIndex"));
				var ordNumVal = $(".inOrdNum").val(); 
                var reqNumVal = $(".inReqNum").val(); 
                var newInputVal = $('.newinput').val(); 
                var secStyleVal = $('.secStyle').val();
                var cateGoryVal = $('#input_div_class').val();
//				console.log(optionsBusiness)
               if(ordNumVal == null||ordNumVal == undefined){
					ordNumVal == "";
                }
                if(reqNumVal == null||reqNumVal == undefined) {
                	reqNumVal == "";
                }
                if(newInputVal == null||newInputVal == undefined) {
                	newInputVal == "";
                }
                if(secStyleVal == null||secStyleVal == undefined) {
                	secStyleVal == "";
                }
                if(cateGoryVal == null||cateGoryVal == undefined) {
                	cateGoryVal == "";
                }
                if(ordNumVal == ""&&reqNumVal == ""&&newInputVal == ""&&secStyleVal == ""&&cateGoryVal == ""){
                	 mui.toast("请输入查询条件！");
                	 return;
                }
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
		hrefHtml: function(newinput, input_div,staOrdHideSpan) {
			var _this = this;
			_this.ajaxGoodList()
			_this.ajaxCheckStatus()

			$(newinput).on('focus', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$(staOrdHideSpan).show()
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				_this.rendHtml(_this.datagood,$(this).val())
			})
			
			$(staOrdHideSpan).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$(staOrdHideSpan).hide()
			})
			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
//				_this.fromOfficeId = $(this).attr("id");
				$(newinput).val($(this).text())
				$(input_div).hide()
				$('#staOrdHideSpan').hide()
				_this.selectOpen = true
			})
		},
		//选择经销店
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
		//订单状态
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
					$.each(res, function(i, item) {
						htmlBusiness += '<option class="soption"  value="' + item.value + '">' + item.label + '</option>'
					});
					$('#input_div_orderStatus').html(optHtml+htmlBusiness)
					_this.getData()
				}
			});
		},
		//审核状态
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
					$('#input_div_checkStatus').html(optHtml+htmlClass)
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
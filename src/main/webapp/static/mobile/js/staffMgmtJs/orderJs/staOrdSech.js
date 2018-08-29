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
			this.ajaxGoodName();
		},
		pageInit: function() {
			var _this = this;
		},
		//点击查询
		getData: function() {
			var _this = this;
			$('#staOrdSechBtn').on('tap', function() {
//				var optionsBusiness = $("#input_div_business option").eq($("#input_div_business").attr("selectedIndex"));
				var optionsBusiness = $("#input_div_orderStatus option").eq($("#input_div_orderStatus").attr("selectedIndex"));
				var ordNumVal = $("#staOrderNum").val(); 
                var reqNumVal = $("#staOrdPurchasing").val(); 
                var OrdMobileVal = $('#staOrdMobile').val(); 
                var secStyleVal = $('#staOrdNumbers').val();
                var cateGoryVal = $('#staOrdClient').val();
                var orderStatusVal = $('#input_div_orderStatus').val();
                var checkStatusVal = $('#input_div_checkStatus').val();
                var newinputVal = $('.newinput').val();
                if(ordNumVal == null||ordNumVal == undefined){
					ordNumVal == "";
                }
                if(reqNumVal == null||reqNumVal == undefined) {
                	reqNumVal == "";
                }
                if(OrdMobileVal == null||OrdMobileVal == undefined) {
                	OrdMobileVal == "";
                }
                if(secStyleVal == null||secStyleVal == undefined) {
                	secStyleVal == "";
                }
                if(cateGoryVal == null||cateGoryVal == undefined) {
                	cateGoryVal == "";
                }
                if(orderStatusVal == null||orderStatusVal == undefined) {
                	orderStatusVal == "";
                }
                if(checkStatusVal == null||checkStatusVal == undefined) {
                	checkStatusVal == "";
                }
                if(newinputVal == null||newinputVal == undefined) {
                	newinputVal == "";
                }
                if(ordNumVal == ""&&reqNumVal == ""&&OrdMobileVal == ""&&secStyleVal == ""&&cateGoryVal == ""&&orderStatusVal == ""&&checkStatusVal == ""&&newinputVal == ""){
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
//					optionsBusiness
				}
			})
		},
		sureSelect:function(){
			var _this = this;
			_this.selectOpen = false
//			var optionsClass = $("#input_div_class option").eq($("#input_div_class").attr("selectedIndex"));
//			var optionsClass = $("#input_div_checkStatus option").eq($("#input_div_checkStatus").attr("selectedIndex"));
			GHUTILS.OPENPAGE({
				url: "../../../html/staffMgmtHtml/orderHtml/staOrderList.html",
				extras: {
					staOrder: $('#staOrderNum').val(),
					Purchasing: $('#staOrdPurchasing').val(),
					OrdMobile: $('#staOrdMobile').val(),
					OrdNumbers: $('#staOrdNumbers').val(),
					OrdClient: $('#staOrdClient').val(),
					orderStatus: $('#input_div_orderStatus').val(),
					checkStatus: $('#input_div_checkStatus').val(),
					newinput: $('.newinput').val(),
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
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div').html(htmlList)
		},
		//经销店名称
		ajaxGoodList: function() {
			var _this = this;
			var htmlList = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeList',
				data: {
					type: 6
				},
				dataType: 'json',
				success: function(res) {
					_this.datagood = res
					$.each(res, function(i, item) {
						htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type +  '" pIds="' + item.pIds + '">' + item.name + '</span>'
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
				data: {type:'biz_order_status'},
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
			$('#input_div_checkStatus').html(optHtml)
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/biz/order/bizOrderHeader/listData4mobile',
				data: {flag:'check_pending',consultantId:_this.userInfo.staListId},
				dataType: 'json',
				success: function(res) {
					console.log('====')
					console.log(res)
					$.each(res.data.originConfigMap, function(i, item) {
						htmlClass += '<option class="soption" value="">' + item + '</option>'
					});
					$('#input_div_checkStatus').append(htmlClass)
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
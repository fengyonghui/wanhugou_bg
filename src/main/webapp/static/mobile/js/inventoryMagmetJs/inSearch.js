(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.datagood = [];
		this.selectOpen = false
		this.includeTestData = false
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.hrefHtml('.newinput', '.input_div','#inHideSpan' );
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
			this.ajaxGoodName();
			this.ajaxPoStatus();//付款单业务状态
			this.ajaxPocheckStatus();//付款单审核状态
			this.ajaxpoSchTypeStatus();//付款单排产状态
		},
		pageInit: function() {
			var _this = this;
			_this.testData();
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
			var optionsstock = $("#inputRadio option").eq($("#inputRadio").attr("selectedIndex"));//备货方
			var optionsBusiness = $("#input_div_business option").eq($("#input_div_business").attr("selectedIndex"));//业务状态
			var optionsClass = $("#input_div_class option").eq($("#input_div_class").attr("selectedIndex"));//品类名称
			var optionscheck = $("#input_div_check option").eq($("#input_div_check").attr("selectedIndex"));//审核状态
//			console.log(optionscheck)
			GHUTILS.OPENPAGE({
				url: "../../html/inventoryMagmetHtml/inventoryList.html",
				extras: {
					reqNo: $('.inOrdNum').val(),//备货单号
					name: $('.inReqNum').val(),//供应商
					fromType:optionsstock.val(),//备货方
					fromOffice: $('.hasoid').attr('id'),//采购中心 
					bizStatusid: optionsBusiness.val(),//业务状态
					varietyInfoid: optionsClass.val(),//品类名称
					process:optionscheck.val(),//审核状态
					poBizStatus:$('#input_div_poStatus').val(),//付款单业务状态
					processTypeStr:$('#input_div_orderStatus').val(),//付款单审核状态
					poSchType:$('#input_div_poSchType').val(),//付款单排产状态
					poWaitPay:$('#wait_pay').val(),//付款单待支付
					includeTestData: _this.includeTestData,//测试数据
					isFunc: true
				}
			})
		},
		testData:function() {
			var _this = this;
            $('.testCheckbox').on('change',function(){
            	if(this.checked){
            		_this.includeTestData = true
            	}else {
            		_this.includeTestData = false
            	}
	        })
        },
		hrefHtml: function(newinput, input_div,inHideSpan) {
			var _this = this;
			_this.ajaxGoodList()
			_this.ajaxServiceStates()
			_this.ajaxCheckStatus()

			$(newinput).on('focus', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
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
//				_this.fromOfficeId = $(this).attr("id");
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
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div').html(htmlList)
		},
		ajaxGoodList: function() {
			var _this = this;
			var htmlList = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeListByPhone',
				data: {
					type: 8,
					source:'officeConnIndex'
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
		},
		//审核状态
		ajaxCheckStatus: function() {
			var _this = this;
			var optHtml ='<option value="">全部</option>';
			var htmlCheckStatus = ''
			$.ajax({
				type: 'GET',
				url: '/a/biz/request/bizRequestHeaderForVendor/list4MobileNew',
				data: {consultantId: _this.userInfo.staListId},
				dataType: 'json',
				success: function(res) {
					$.each(res.data.requestMap, function(i, item) {
						htmlCheckStatus += '<option class="soption"  value="' + i + '">' + item + '</option>'
					});
					$('#input_div_check').html(optHtml+htmlCheckStatus)
					_this.getData()
				}
			});
		},
		//业务状态
		ajaxServiceStates: function() {
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
					$('#input_div_business').html(optHtml+htmlBusiness)
					_this.getData()
				}
			});
		},
		//品类名称
		ajaxGoodName: function() {
			var _this = this;
			var optHtml ='<option value="">全部</option>';
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/biz/request/bizRequestHeaderForVendor/list4MobileNew',
				data: {consultantId: _this.userInfo.staListId},
				dataType: 'json',
				success: function(res) {
					console.log(res)
					$.each(res.data.varietyInfoList, function(i, item) {
						htmlClass += '<option class="soption" value="' + item.id + '">' + item.name + '</option>'
					});
					$('#input_div_class').html(optHtml+htmlClass)
					_this.getData()
				}
			});
		},
		//付款单业务状态
		ajaxPoStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'biz_po_status'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlClass += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#input_div_poStatus').html(optHtml+htmlClass)
				}
			});
		},
		//付款单审核状态
		ajaxPocheckStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/biz/request/bizRequestHeaderForVendor/list4MobileNew',
				dataType: 'json',
				success: function(res) {
					$.each(res.data.processList, function(i, item) {
						console.log(item)
						htmlClass += '<option class="soption" value="' + item + '">' + item + '</option>'
					});
					$('#input_div_orderStatus').html(optHtml+htmlClass)
				}
			});
		},
		//付款单排产状态
		ajaxpoSchTypeStatus: function() {
			var _this = this;
			var optHtml ='<option value="">请选择</option>';
			var htmlClass = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'poSchType'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlClass += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#input_div_poSchType').html(optHtml+htmlClass)
				}
			});
		},
	}
	$(function() {
		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
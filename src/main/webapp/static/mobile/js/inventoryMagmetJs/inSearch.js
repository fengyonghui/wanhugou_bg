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
		},
		pageInit: function() {
			var _this = this;
			_this.testData();
		},
		getData: function() {
			var _this = this;
			$('#inSearchBtn').on('tap', function() {
				var ordNumVal = $(".inOrdNum").val(); //备货单号
                var reqNumVal = $(".inReqNum").val(); //供应商
                var inputRadioVal = $("#inputRadio").val(); //备货方
                var newInputVal = $('.newinput').val();//采购中心 
                var div_businessVal = $('#input_div_business').val();//业务状态
                var div_checkVal = $('#input_div_check').val();//审核状态
                var cateGoryVal = $('#input_div_class').val();//品类名称
                var testCheckVal = $('#testCheckbox').val();//测试数据
               if(ordNumVal == null||ordNumVal == undefined){
					ordNumVal == "";
                }
                if(reqNumVal == null||reqNumVal == undefined) {
                	reqNumVal == "";
                }
                if(inputRadioVal == null||inputRadioVal == undefined) {
                	inputRadioVal == "";
                }
                if(newInputVal == null||newInputVal == undefined) {
                	newInputVal == "";
                }
				if(div_businessVal == null||div_businessVal == undefined) {
                	div_businessVal == "";
                }
				if(div_checkVal == null||div_checkVal == undefined) {
                	div_checkVal == "";
                }
                if(cateGoryVal == null||cateGoryVal == undefined) {
                	cateGoryVal == "";
                }
                if(ordNumVal == ""&&reqNumVal == ""&inputRadioVal == ""&&newInputVal == ""&&div_businessVal == ""&&div_checkVal == ""&&cateGoryVal == ""&&_this.includeTestData==false){
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
		sureSelect:function(){
			var _this = this;
			_this.selectOpen = false;
			var optionsstock = $("#inputRadio option").eq($("#inputRadio").attr("selectedIndex"));//备货方
			var optionsBusiness = $("#input_div_business option").eq($("#input_div_business").attr("selectedIndex"));//业务状态
			var optionsClass = $("#input_div_class option").eq($("#input_div_class").attr("selectedIndex"));//品类名称
			var optionscheck = $("#input_div_check option").eq($("#input_div_check").attr("selectedIndex"));//审核状态
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
				console.log($(this).val())
				
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
					console.log(res)
					$.each(res.data.requestMap, function(i, item) {
//						console.log(i)
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
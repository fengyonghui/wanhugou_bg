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
			this.hrefHtml('.newinput', '.input_div','#cpySchHideSpan');
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
		},
		getData: function() {
			var _this = this;
			$('#staffSearchBtn').on('tap', function() {
				var companyIdVal = $(".newinput").val(); 
//              var companyNameVal = $("#inPoLastDa").val(); 
                var loginNameVal = $('.staLogName').val(); 
                var nameVal = $('.staName').val();
                var mobielVal = $('.staMobile').val();
				if(companyIdVal == null||companyIdVal == undefined){
					companyIdVal == "";
                }
                if(loginNameVal == null||loginNameVal == undefined) {
                	loginNameVal == "";
                }
                if(nameVal == null||nameVal == undefined) {
                	nameVal == "";
                }
                if(mobielVal == null||mobielVal == undefined) {
                	mobielVal == "";
                }
                if(companyIdVal == ""&&loginNameVal == ""&&nameVal == ""&&mobielVal == ""){
                	 mui.toast("请输入查询条件！");
                	 return;
                }
				if(_this.selectOpen){
					if($('.hasoid').attr('id')){
						_this.sureSelect();
					}else{
						mui.toast('请选择匹配的选项！')
					}
				}else{
					_this.sureSelect();
				}
			})
		},
		sureSelect:function(){
			var _this = this;
			_this.selectOpen = false
			GHUTILS.OPENPAGE({
				url: "../../html/staffMgmtHtml/staffList.html",
				extras: {
					companyId: $('.hasoid').attr('id'),
					companyName: $('.hasoid').attr('name'),
					loginName: $('.staLogName').val(),
					name: $('.staName').val(),
					mobile: $('.staMobile').val(),
					isFunc: true
				}
			})
		},
		hrefHtml: function(newinput, input_div, cpySchHideSpan) {
			var _this = this;
			_this.ajaxGoodList()

			$(newinput).on('focus', function() {
				//$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$(cpySchHideSpan).show()
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				_this.rendHtml(_this.datagood,$(this).val())
			})
			$(cpySchHideSpan).on('click', function() {
//				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$(cpySchHideSpan).hide()
			})
			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
				$(newinput).val($(this).text())
				$(input_div).hide()
				$('#cpySchHideSpan').hide()
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
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="'+item.name+'">' + item.name + '</span>'
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
					type: 8,
					customerTypeTen: 10
				},
				dataType: 'json',
				success: function(res) {
					_this.datagood = res
					$.each(res, function(i, item) {
//						console.log(item)
						htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="'+item.name+'">' + item.name + '</span>'
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
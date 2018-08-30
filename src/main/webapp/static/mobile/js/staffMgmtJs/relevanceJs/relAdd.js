(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
//			this.getData();
			this.ajaxCheckStatus();
		},
		pageInit: function() {
			var _this = this;
			
		},
//		getData: function() {
//			var _this = this;
//			$.ajax({
//				type: 'GET',
//				url: '/a/biz/custom/bizCustomCenterConsultant/connOfficeForm4mobile',
//				data: {
//					id: _this.userInfo.cosultasIdTxt,
//					'office.id': _this.userInfo.officeIdTxt,
//				},
//				dataType: 'json',
//				success: function(res) {
//					console.log(res)
//					$('cosultasName').val(res.data.office.name);
//					
//					$().val()
////					$('.input_div').html(htmlList)
//				}
//			});
//		},

		ajaxCheckStatus: function() {
			var _this = this;
			var htmlBusiness="";
			var htmloffice="";
			$.ajax({
				type: 'GET',
				url: '/a/biz/custom/bizCustomCenterConsultant/connOfficeForm4mobile',
				data: {
					id: _this.userInfo.cosultasIdTxt,
					'office.id': _this.userInfo.officeIdTxt,
				},
				dataType: 'json',
				success: function(res) {
										
					htmlBusiness = '<option class="soption"  value="' + res.data.office.name + '">' + res.data.office.name + '</option>'
					$('#cosultasName').html(htmlBusiness);
//					htmloffice = '<option class="soption"  value="' + res.data.office.office.name + '">' + res.data.office.office.name + '</option>'
//					$('#officeName').html(htmloffice);
//					_this.choiceofficeName(res.data)
					$.each(res.data.officeList,function(i,item){
						console.log(res.data.bcc.centers.id)
						console.log(item)
						var aa = '';
						if(item.id==res.data.bcc.centers.id){
							htmloffice = '<option class="soption"  value="' + res.data.office.office.name + '">' + res.data.office.office.name + '</option>'
						}
		                 htmloffice += '<option class="soption"  value="' + item.name + '">' + item.name + '</option>'
		                
					});
					 $('#officeName').html(htmloffice); 
				}
			});
			
		},
//		choiceofficeName:function(data){
//			var htmloffice="";
//			$.each(data.officeList,function(i,item){
//				console.log(item)
//               htmloffice += '<option class="soption"  value="' +  + '">' + item.name + '</option>'
//               $('#officeName').append(htmloffice); 
//			});
//			
//		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
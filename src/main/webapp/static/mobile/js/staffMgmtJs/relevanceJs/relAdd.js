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
					console.log(res)
					htmlBusiness = '<option class="soption"  value="' + res.data.office.name + '">' + res.data.office.name + '</option>'
					$('#cosultasName').html(htmlBusiness);
					htmloffice = '<option class="soption"  value="' + res.data.office.office.id + '">' + res.data.office.office.name + '</option>'
					$('#officeName').html(htmloffice);
					$.each(res.data.officeList,function(i,item){
						console.log(item)
		                 htmloffice += '<option class="soption"  value="' + item.id + '">' + item.name + '</option>'                                                                                                	              	
					});
					 $('#officeName').html(htmloffice); 
					  _this.officeNamechoice();
				}
			});
			
		},
		officeNamechoice:function(){
			$('#officeName').on('change',function(){
				alert($(this).val())
				 $("#cosultasName").html("");
				 $("#cosultasName").append("<option value='' selected = 'selected'>请选择客户专员</option>");
				if($(this).val()!= null && $(this).val() !=undefined && $(this).val().trim() != ""){
					 $.ajax({
						url:"/a/sys/user/getAdvisers4mobile",
						data:{"office.id":$(this).val()},
						type:"POST",
						dataType:'json',
						success:function(data){
							console.log(data)
							for(var i =0;i<data.length;i++){
								$("#cosultasName").append("<option value='"+data[i].id+"'>"+data[i].name+"</option>");
							}
						},
						error:function(er){
							alert("查询失败");
						}
					});
				}
			})
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
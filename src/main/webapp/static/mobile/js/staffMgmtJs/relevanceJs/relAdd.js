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
			_this.staRelAdd();
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
					htmlBusiness = '<option class="soption"  value="' + res.data.office.id + '">' + res.data.office.name + '</option>'
					$('#cosultasName').html(htmlBusiness);
					htmloffice = '<option class="soption"  value="' + res.data.office.office.id + '">' + res.data.office.office.name + '</option>'
					$('#officeName').html(htmloffice);
					$.each(res.data.officeList,function(i,item){
						console.log(item)
						$('#centersId').val(item.id)
						
		                 htmloffice += '<option class="soption"  value="' + item.id + '">' + item.name + '</option>'
					});
					 $('#officeName').html(htmloffice); 
					  _this.officeNamechoice();
				}
			});
			
		},
		officeNamechoice:function(){
			$('#officeName').on('change',function(){
//				alert($(this).val())
				 $("#cosultasName").html("");
				 $("#cosultasName").append("<option value='' selected = 'selected'>请选择客户专员</option>");
				if($(this).val()!= null && $(this).val() !=undefined && $(this).val().trim() != ""){
					 $.ajax({
						url:"/a/sys/user/getAdvisers4mobile",
						data:{"office.id":$(this).val()},
						type:"POST",
						dataType:'json',
						success:function(data){
							$('#consultantsId').val(data.data.id)
							for(var i =0;i<data.data.length;i++){
								$("#cosultasName").append("<option value='"+data.data[i].id+"'>"+data.data[i].name+"</option>");
							}
						},
						error:function(er){
							alert("查询失败");
						}
					});
				}
			})
		},
		staRelAdd:function() {
			var _this = this;
			
			$('#staRelSaveBtn').on('tap', function() {
				$.ajax({
					type: "GET",
					url: "/a/biz/custom/bizCustomCenterConsultant/save4mobile",
					data: {
//						mobeil: ,
						'centers.id':$('#currentType').val(),
						'consultants.id':num,
					},
					dataType: "json",
					success: function(res) {
						console.log(res)
						if(res.ret==true){
							alert('操作成功!')
							GHUTILS.OPENPAGE({
							url: "../../../html/staffMgmtHtml/relevanceHtml/relList.html",
							extras: {
								}
							})
						}
					}
				});
			})
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
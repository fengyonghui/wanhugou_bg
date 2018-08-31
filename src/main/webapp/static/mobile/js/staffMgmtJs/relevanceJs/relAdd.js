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
//					console.log(res)
					//采购中心默认值渲染
					htmloffice = '<option class="soption"  value="' + res.data.office.office.id + '">' + res.data.office.office.name + '</option>'
					$('#officeName').html(htmloffice);
					//客户专员默认值渲染
					htmlBusiness = '<option class="soption"  value="' + res.data.office.id + '">' + res.data.office.name + '</option>'
					$('#cosultasName').html(htmlBusiness);
					
					// 客户专员默认值id
					$('#consultantsId').val(res.data.office.id)					
					console.log($('#consultantsId').val())
                    //采购中心下拉列表默认值渲染
					$.each(res.data.officeList,function(i,item){
//						console.log(item)
						// 采购中心认值id
						$('#centersId').val(item.id)
						console.log($('#centersId').val())
		                htmloffice += '<option class="soption"  value="' + item.id + '">' + item.name + '</option>'
					});
					 $('#officeName').html(htmloffice); 
					 _this.officeNamechoice();
				}
			});
			
		},
		//采购中心点击时事件方法
		officeNamechoice:function(){
			$('#officeName').on('change',function(){
				alert($(this).val())
//				$('#consultantsId').val($(this).val())
//				console.log($('#consultantsId').val())
				 $("#cosultasName").html("");
				 $("#cosultasName").append("<option value='' selected = 'selected'>请选择客户专员</option>");
//				 var htmlBusiness="";
				if($(this).val()!= null && $(this).val() !=undefined && $(this).val().trim() != ""){
					 $.ajax({
						url:"/a/sys/user/getAdvisers4mobile",
						data:{"office.id":$(this).val()},
						type:"POST",
						dataType:'json',
						success:function(res){
							console.log(res)
//                          $.each(res.data,function(i,item){
//                              $("#cosultasName").append("<option value='"+item.id+"'>"+item.name+"</option>");
//							});
							for(var i =0;i<data.length;i++){
								$("#cosultasName").append("<option value='"+res.data[i].id+"'>"+res.data[i].name+"</option>");
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
			$('.staRelSave').on('tap','#staRelSaveBtn', function() {
				alert(999)
				$.ajax({
					type: "GET",
					url: "/a/biz/custom/bizCustomCenterConsultant/save4mobile",
					data: {
						 phone:$('#phpneId').val() ,
						'centers.id':$('#centersId').val(),
						'consultants.id':$('#consultantsId').val()
					},
					dataType: "json",
					success: function(res) {
						console.log(res)
						var consultantsId = $('#consultantsId').val();
						var officeId = $('#centersId').val();
						if(res==1){
							alert('操作成功!')
							GHUTILS.OPENPAGE({
							url: "../../../html/staffMgmtHtml/relevanceHtml/relList.html",
							extras: {
								staListId: consultantsId,
								dptmtId: officeId,
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
(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
//		this.staffFlag = "false"
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
//			biz:custom:bizCustomCenterConsultant:edit   保存
//			this.getPermissionList('biz:custom:bizCustomCenterConsultant:edit','staRelAddFlag')
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
			this.ajaxCheckStatus();
		},
		pageInit: function() {
			var _this = this;
			_this.staRelAdd();
		},
//		getPermissionList: function (markVal,flag) {
//          var _this = this;
//          $.ajax({
//              type: "GET",
//              url: "/a/sys/menu/permissionList",
//              dataType: "json",
//              data: {"marking": markVal},
//              async:false,
//              success: function(res){
//                  _this.staRelAddFlag = res.data;
//					console.log(_this.staRelAddFlag)
//              }
//          });
//      },
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
					//采购中心默认值渲染
					htmloffice = '<option class="soption"  value="' + res.data.office.office.id + '">' + res.data.office.office.name + '</option>'
					$('#officeName').html(htmloffice);
					//客户专员默认值渲染
					htmlBusiness = '<option class="soption"  value="' + res.data.office.id + '">' + res.data.office.name + '</option>'
					$('#cosultasName').html(htmlBusiness);

					// 客户专员默认值id
					$('#consultantsId').val(res.data.office.id)
                    //采购中心下拉列表默认值渲染
					$.each(res.data.officeList,function(i,item){
						// 采购中心认值id
						$('#centersId').val(item.id)
		                htmloffice += '<option class="soption"  value="' + item.id + '">' + item.name + '</option>'
					});
					 $('#officeName').html(htmloffice); 
					 _this.officeNamechoice();
				}
			});
			
		},
		//采购中心点击时事件方法
		officeNamechoice:function(){
			var _this = this;
			$('#officeName').on('change',function(){
				 $('#centersId').val($(this).val())
				 $("#cosultasName").html("");
				 $("#cosultasName").append("<option value='' selected = 'selected'>请选择客户专员</option>");
				if($(this).val()!= null && $(this).val() !=undefined && $(this).val().trim() != ""){
					 $.ajax({
						url:"/a/sys/user/getAdvisers4mobile",
						data:{"office.id":$(this).val()},
						type:"POST",
						dataType:'json',
						success:function(res){
							for(var i =0;i<res.data.length;i++){
								$("#cosultasName").append("<option value='"+res.data[i].id+"'>"+res.data[i].name+"</option>");
							}
						},
						error:function(er){
							alert("查询失败");
						}
					});
				}
				
			})
			_this.cosultasNamechoice();
		},
		cosultasNamechoice:function(){
			$('#cosultasName').on('change',function(){
				 $('#consultantsId').val($(this).val());					
			})
		},
		staRelAdd:function() {
			var _this = this;			
			$('.staRelSave').on('tap','#staRelSaveBtn', function() {
				var officeNameVal = $('#officeName').val();
				var cosultasNameVal = $('#cosultasName').val();
				var phoneVal = $('#phpneId').val();
				if(officeNameVal == null || officeNameVal ==undefined || officeNameVal.trim() == ""){
					mui.toast("请选择采购中心！")
					return false;
				}
				if(cosultasNameVal == null || cosultasNameVal ==undefined || cosultasNameVal.trim() == ""){
					mui.toast("请选择客户专员！")
					return false;
				}
				if(phoneVal == null || phoneVal ==undefined || phoneVal.trim() == ""){
					mui.toast("请输入登录名！")
					return false;
				}
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
						var consultantsId = $('#consultantsId').val();//客户专员ID
						var officeId = $('#centersId').val();//采购中心ID
						if(res==1){
							mui.toast('关联成功!')
							window.setTimeout(function(){
				                GHUTILS.OPENPAGE({
									url: "../../../html/staffMgmtHtml/relevanceHtml/relList.html",
									extras: {
										staListId: consultantsId,//客户专员ID
										dptmtId: officeId,//采购中心ID
									}
								})
			                },800);							
						}else{							
							mui.toast('关联失败，请输入内容！')
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
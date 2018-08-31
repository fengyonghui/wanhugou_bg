(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
//		this.staRelFlag = "false"
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
//			biz:custom:bizCustomCenterConsultant:edit 			操作、移除
//			this.getPermissionList('biz:custom:bizCustomCenterConsultant:edit','staRelFlag')
			if(this.userInfo.isFunc){
				this.seachFunc()
			}else{
				this.pageInit(); //页面初始化
			}
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			var pager = {};//分页 
		    var totalPage;//总页码
		    pullRefresh(pager);//启用上拉下拉 
		    function pullRefresh(){
		        mui("#refreshContainer").pullRefresh({
//			        up:{
//			            contentnomore:'没 有 更 多 数 据 了',
//			            callback:function(){
//			                window.setTimeout(function(){
//			                    getData(pager);
//			                },500);
//			            }
//			         },
			        down : {
			            auto: true,
			            contentdown : "",
			            contentover : "",
			            contentrefresh : "正在加载...",
			            callback :function(){ 
			                    pager['size']= 10;//条数
			                    pager['pageNo'] = 1;
			                    pager['consultants.id'] = _this.userInfo.staListId;//客户专员
			                    pager['office.id'] = _this.userInfo.dptmtId;//采购中心ID
			                    pager['conn'] ="connIndex"
			                   
				                var f = document.getElementById("staReleList");
				                var childs = f.childNodes;
				                for(var i = childs.length - 1; i >= 0; i--) {
				                    f.removeChild(childs[i]);
				                }
				                $('.mui-pull-caption-down').html('');				                
				                getData(pager);
			            }
			        }
			    })
		    }
		    function getData(params){
		    	var staffHtmlList = '';
		        mui.ajax("/a/biz/custom/bizCustomCenterConsultant/listData4mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	$('#cosultasId').val(res.data.bcUser.consultants.id);
		            	$('#officeId').val(res.data.bcUser.centers.id);
		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						var arrLen = res.data.resultData.length;						
                        if(arrLen > 0) {
                        $.each(res.data.resultData, function(i, item) {
                        	$('#consultantsId').val(item.consultantsId)
                        	var userOfficeDeta = '';
			                if(item.userOfficeDeta) {
			                	userOfficeDeta = _this.formatDateTime(item.userOfficeDeta)
			                }else {
			                	userOfficeDeta = ''
			                }
			                var reAddress = '';
			                if(item.userOfficeReceiveTotal) {
			                	reAddress = item.provinceName + item.cityName + item.regionName + item.address
			                }else {
			                	reAddress = ''
			                }
									staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row">' +
											'<label>采购中心:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.centersName+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>客户专员:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.consultantsName+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>电话:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.consultantsMobile+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>经销店名称:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.customsName +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>负责人:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.customsPrimaryPersonName+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>详细地址:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+reAddress+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>采购频次:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderCount+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>累计金额:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.userOfficeReceiveTotal+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>首次开单:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+userOfficeDeta+' ">' +
										'</div>' +
										'<div class="app_font_cl content_part mui-row">' +
											'<div class="staReMoveBtn" customsId="'+item.customsId +'"  consultantsId="'+ item.consultantsId +'">解除关联</div>'+
										'</div>' +
//										'<div class="app_font_cl content_part mui-row">' +
//											'<input type="hidden" id="hideul" class="mui-input-clear" disabled="disabled" value=" '+item.consultantsId+' ">' +
//										'</div>' +
									'</div>'
								});
								$('#staReleList').html(staffHtmlList);
								_this.stHrefHtml()
						} 
						else {
								$('.mui-pull-caption').html('');
						}								                	
//			           totalPage = res.data.page.count%pager.size!=0?
//		                parseInt(res.data.page.count/pager.size)+1:
//		                res.data.page.count/pager.size;
                        var totalPage=1;
		                if(totalPage==pager.pageNo){
			                mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
			            }else{
			                pager.pageNo++;
			                mui('#refreshContainer').pullRefresh().refresh(true);
			            }          
			        },
		            error:function(xhr,type,errorThrown){
//			            console.log(type);
		            }
		        })
		    }
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
//                  _this.staRelFlag = res.data;
//                  console.log(_this.staRelFlag)
//              }
//          });
//      },
		stHrefHtml: function() {
			var _this = this;
		/*查询*/
			$('.header').on('tap', '#staReleSechBtn', function() {
				var url = $(this).attr('url');
				var consultantsIdTxt = $('#consultantsId').val();
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/relevanceHtml/relSech.html",
						extras:{
							consultantsIdTxt: consultantsIdTxt,
						}
					})
				}
			}),
		/*返回客户专员列表*/
			$('#nav').on('tap','.staRelStaBtn', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../../html/staffMgmtHtml/staffList.html",
					extras: {
						
					}
				})
			}),	
		/*经销店添加*/
			$('#nav').on('tap','.staRelAddBtn', function() {
				var url = $(this).attr('url');
		        var cosultasIdTxt=$('#cosultasId').val();
		        var officeIdTxt=$('#officeId').val();
				GHUTILS.OPENPAGE({
					url: "../../../html/staffMgmtHtml/relevanceHtml/relAdd.html",
					extras: {
						cosultasIdTxt:cosultasIdTxt,
						officeIdTxt:officeIdTxt
					}
				})
			}),
		/*解除关联*/
            $('.content').on('tap','.staReMoveBtn',function(){
            	var url = $(this).attr('url');
				var customsId = $(this).attr('customsId');
				var consultantsId = $(this).attr('consultantsId');
            	var btnArray = ['取消', '确定'];
				mui.confirm('您确定要解除关联吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						$.ajax({
			                type: "GET",
			                url: "/a/biz/custom/bizCustomCenterConsultant/delete4mobile",
			                data: {
			                	'customs.id': customsId ,
			                	'consultants.id': consultantsId
			                },
			                dataType: "json",
			                success: function(res){
			                	if(res.data == "ok") {
			                		mui.toast("操作成功")
			                		 window.setTimeout(function(){
					                    _this.pageInit();
					                },1000);
			                		
			                	}
		                	}
		            	})
					}else {
						
					}
				})	
			})
        },
		seachFunc:function(){			
			var _this = this;
			var staffHtmlList = '';
//			解码：
//			var nameTxt = '';
//			if(_this.userInfo.name) {
//				nameTxt = decodeURIComponent(_this.userInfo.name)
//			}else {
//				nameTxt = ''
//			}
			$.ajax({
				type: 'GET',
				url: '/a/biz/custom/bizCustomCenterConsultant/listData4mobile',
				data: {
					pageNo: 1,
					'customs.id':_this.userInfo.customsIds,
					'consultants.id':_this.userInfo.sehConsultantsid,
					'consultants.mobile':_this.userInfo.consultantsMobile,
					queryCustomes: 'query_Custome'
				},
				dataType: 'json',
				success: function(res) {
		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						var arrLen = res.data.resultData.length;						
                        if(arrLen > 0) {
                        $.each(res.data.resultData, function(i, item) {
                        	
                        	$('#consultantsId').val(item.consultantsId)
							var userOfficeDeta = '';
			                if(item.userOfficeDeta) {
			                	userOfficeDeta = _this.formatDateTime(item.userOfficeDeta)
			                }else {
			                	userOfficeDeta = ''
			                }
			                var reAddress = '';
			                if(item.userOfficeReceiveTotal) {
			                	reAddress = item.provinceName + item.cityName + item.regionName + item.address
			                }else {
			                	reAddress = ''
			                }
									staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row">' +
											'<label>采购中心:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.centersName+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>客户专员:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.consultantsName+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>电话:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.consultantsMobile+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>经销店名称:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.customsName +' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>负责人:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.customsPrimaryPersonName+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>详细地址:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+reAddress+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>采购频次:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderCount+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>累计金额:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.userOfficeReceiveTotal+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>首次开单:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+userOfficeDeta+' ">' +
										'</div>' +
										'<div class="app_font_cl content_part mui-row">' +
											'<div class="staReMoveBtn" customsId="'+item.customsId +'"  consultantsId="'+ item.consultantsId +'">解除关联</div>'+
										'</div>' +
//										'<div class="app_font_cl content_part mui-row">' +
//											'<input type="hidden" id="hideul" class="mui-input-clear" disabled="disabled" value=" '+item.consultantsId+' ">' +
//										'</div>' +
									'</div>'
                        });
							$('#staReleList').html(staffHtmlList);
							_this.stHrefHtml()
					} 
					else {
							$('#staReleList').html('<p class="noneTxt">暂无数据</p>');
							$('#staReleSechBtn').hide();

					}
				}
			});
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

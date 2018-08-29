(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.detileFlag = "false"
		this.cancelAmendPayFlag = "false"
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
//			biz:request:bizRequestHeader:view   详情
//			biz:request:bizRequestHeader:edit    取消、修改、付款
			this.getPermissionList('biz:request:bizRequestHeader:view','detileFlag')
			this.getPermissionList('biz:request:bizRequestHeader:edit','cancelAmendPayFlag')
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
			                    pager['consultants.id'] = _this.userInfo.staListId;
			                    pager['office.id'] = _this.userInfo.dptmtId;
			                    pager['conn'] ="connIndex"
				                var f = document.getElementById("staReleList");
				                var childs = f.childNodes;
				                for(var i = childs.length - 1; i >= 0; i--) {
				                    f.removeChild(childs[i]);
				                }
				                console.log('222')
				                console.log(pager)
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
		          	    console.log(res)
		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						var arrLen = res.data.resultData.length;						
                        if(arrLen > 0) {
                        $.each(res.data.resultData, function(i, item) {
//                      	console.log(item)
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
			//							'<div class="mui-input-row">' +
			//								'<label>详细地址:</label>' +
			//								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+varietyInfoName+' ">' +
			//							'</div>' +
										'<div class="mui-input-row">' +
											'<label>采购频次:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderCount+' ">' +
										'</div>' +
			//							'<div class="mui-input-row">' +
			//								'<label>累计金额:</label>' +
			//								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
			//							'</div>' +
			//							'<div class="mui-input-row">' +
			//								'<label>首次开单:</label>' +
			//								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
			//							'</div>' +
										'<div class="app_font_cl content_part mui-row">' +
//											'<div class="mui-col-xs-6">' +
//											'</div>'+
											'<div class="staReMoveBtn" staListId="'+ item.id +'">' +
												'<div class="mui-row">移除</div>' +
											'</div>'+
										'</div>' +
										'<div class="app_font_cl content_part mui-row">' +
											'<input type="hidden" id="hideul" class="mui-input-clear" disabled="disabled" value=" '+item.consultantsId+' ">' +
										'</div>' +
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
			            console.log(type);
		            }
		        })
		    }
		},
		getPermissionList: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.detileFlag = res.data;
					_this.cancelAmendPayFlag = res.data;
//                  console.log(_this.detileFlag)
//					console.log(_this.cancelAmendPayFlag)
                }
            });
        },
		stHrefHtml: function() {
			var _this = this;
		/*查询*/
			$('.header').on('tap', '#staReleSechBtn', function() {
				var url = $(this).attr('url');
				var hideul=$('#hideul').val();
				alert(hideul)
				
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/relevanceHtml/relSech.html",
						extras:{
							hideul:hideul
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
				GHUTILS.OPENPAGE({
					url: "../../../html/staffMgmtHtml/relevanceHtml/relAdd.html",
					extras: {
						
					}
				})
			}),
		/*移除*/	
            $('.content').on('tap','.staReMoveBtn',function(){
            	var url = $(this).attr('url');
				var staListId = $(this).attr('staListId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(staListId==staListId) {
                	var btnArray = ['取消', '确定'];
					mui.confirm('您确定要移除该关联信息吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
//							$.ajax({
//				                type: "GET",
//				                url: "",
//				                data: {id:staListId},
//				                dataType: "json",
//				                success: function(res){
//				                	alert('操作成功！')
//				                	GHUTILS.OPENPAGE({
//										url: "../../../html/staffMgmtHtml/relevanceHtml/relList.html",
//										extras: {
//												staListId:staListId,
//										}
//									})
//			                	}
//			            	})
						}else {
							
						}
					})	
                }
			})
        },
		formatDateTime: function(unix) {
			var _this = this;
			var now = new Date(parseInt(unix) * 1);
			now = now.toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
			if(now.indexOf("下午") > 0) {
				if(now.length == 18) {
					var temp1 = now.substring(0, now.indexOf("下午")); //2014/7/6
					var temp2 = now.substring(now.indexOf("下午") + 2, now.length); // 5:17:43
					var temp3 = temp2.substring(0, 1); //  5
					var temp4 = parseInt(temp3); // 5
					temp4 = 12 + temp4; // 17
					var temp5 = temp4 + temp2.substring(1, temp2.length); // 17:17:43
					//	                now = temp1 + temp5; // 2014/7/6 17:17:43
					//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
					now = now.replace("-"); //  2014-7-6 17:17:43
				} else {
					var temp1 = now.substring(0, now.indexOf("下午")); //2014/7/6
					var temp2 = now.substring(now.indexOf("下午") + 2, now.length); // 5:17:43
					var temp3 = temp2.substring(0, 2); //  5
					if(temp3 == 12) {
						temp3 -= 12;
					}
					var temp4 = parseInt(temp3); // 5
					temp4 = 12 + temp4; // 17
					var temp5 = temp4 + temp2.substring(2, temp2.length); // 17:17:43
					//	                now = temp1 + temp5; // 2014/7/6 17:17:43
					//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
					now = now.replace("-"); //  2014-7-6 17:17:43
				}
			} else {
				var temp1 = now.substring(0, now.indexOf("上午")); //2014/7/6
				var temp2 = now.substring(now.indexOf("上午") + 2, now.length); // 5:17:43
				var temp3 = temp2.substring(0, 1); //  5
				var index = 1;
				var temp4 = parseInt(temp3); // 5
				if(temp4 == 0) { //  00
					temp4 = "0" + temp4;
				} else if(temp4 == 1) { // 10  11  12
					index = 2;
					var tempIndex = temp2.substring(1, 2);
					if(tempIndex != ":") {
						temp4 = temp4 + "" + tempIndex;
					} else { // 01
						temp4 = "0" + temp4;
					}
				} else { // 02 03 ... 09
					temp4 = "0" + temp4;
				}
				var temp5 = temp4 + temp2.substring(index, temp2.length); // 07:17:43
				//	            now = temp1 + temp5; // 2014/7/6 07:17:43
				//	            now = now.replace("/","-"); //  2014-7/6 07:17:43
				now = now.replace("-"); //  2014-7-6 07:17:43
			}
			return now;
		},
		seachFunc:function(){
			var _this = this;
			var staffHtmlList = '';
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
					'customs.id':_this.userInfo.customsName, 
					'consultants.id':3000,
					'consultants.mobile':_this.userInfo.consultantsMobile
				},
				dataType: 'json',
				success: function(res) {
					console.log(res)
		                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						var arrLen = res.data.resultData.length;						
                        if(arrLen > 0) {
                        $.each(res.data.resultData, function(i, item) {
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
		//							'<div class="mui-input-row">' +
		//								'<label>详细地址:</label>' +
		//								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+varietyInfoName+' ">' +
		//							'</div>' +
									'<div class="mui-input-row">' +
										'<label>采购频次:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderCount+' ">' +
									'</div>' +
		//							'<div class="mui-input-row">' +
		//								'<label>累计金额:</label>' +
		//								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
		//							'</div>' +
		//							'<div class="mui-input-row">' +
		//								'<label>首次开单:</label>' +
		//								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
		//							'</div>' +
									'<div class="app_font_cl content_part mui-row">' +
//											'<div class="mui-col-xs-6">' +
//											'</div>'+
										'<div class="staReMoveBtn" staListId="'+ item.id +'">' +
											'<div class="mui-row">移除</div>' +
										'</div>'+
									'</div>' +
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

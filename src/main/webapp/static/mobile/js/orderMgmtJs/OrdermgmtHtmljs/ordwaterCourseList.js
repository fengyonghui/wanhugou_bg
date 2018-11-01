(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.orWaterConFlag = "false";
		this.orWaterCheckFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
//			biz:order:bizOrderHeaderUnline:view     详情、审核
//			biz:order:bizOrderHeaderUnline:edit		审核
			this.getPermissionList1('biz:order:bizOrderHeaderUnline:view','orWaterConFlag')
			this.getPermissionList2('biz:order:bizOrderHeaderUnline:edit','orWaterCheckFlag')
			if(this.userInfo.isFunc){
				this.seachFunc()
			}else{
				this.pageInit(); //页面初始化
			}						
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		getPermissionList1: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
//              	console.log(res)
                    _this.orWaterConFlag = res.data;
//                  console.log(_this.orWaterConFlag)
                }
            });
        },
        getPermissionList2: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.orWaterCheckFlag = res.data;
//                  console.log(_this.orWaterCheckFlag)
                }
            });
        },
		pageInit: function() {
			var _this = this;
			var pager = {};//分页 
		    var totalPage;//总页码
		    pullRefresh(pager);//启用上拉下拉 
		    function pullRefresh(){
		        mui("#refreshContainer").pullRefresh({
			        up:{
			            contentnomore:'没 有 更 多 数 据 了',
			            callback:function(){			            	
			                window.setTimeout(function(){
			                    getData(pager);
			                },100);
			            }
			         },
			        down : {
			            height:50,
			            auto: true,
			            contentdown : "",
			            contentover : "",
			            contentrefresh : "正在加载...",
			            callback :function(){ 
			                    pager['size']= 20;
			                    pager['pageNo'] = 1;
			                    pager['orderHeader.id'] = _this.userInfo.staOrdId;
				                var f = document.getElementById("ordWaterCourseList");
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
		    	var waterCourseHtml = '';
		    	var ass=[];
		        mui.ajax("/a/biz/order/bizOrderHeaderUnline/list4Mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	console.log(res)
						var arrLen = res.data.page.list.length;
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true)
						}
                        var that=this;
                        
                        var orWaterStatus = '';
                        var orWaterStatusList = [];
                        $.ajax({
	                		type: "GET",
			                url: "/a/sys/dict/listData",
			                data:{type:'biz_order_unline_bizStatus'},
			                dataType: "json",
			                async:false,
			                success: function(zl){
			                	orWaterStatusList = zl;
	                		}
	                	});
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
//                          	console.log(item)
//                          	console.log(item.orderHeader.id)
                            	$('#orderId').val(item.orderHeader.id);
                            	$.each(orWaterStatusList,function(z, l) {
			                		if(item.bizStatus == l.value) {
			                			orWaterStatus = l.label//流水状态
			                		}
			                	});
			                	var waterCourseDetailBtn = '';
								var waterCourseDetailBtnTxt = '';
								var waterCourseCheckBtn = '';
								var waterCourseCheckBtnTxt = '';
			                	if(_this.orWaterConFlag == true) {
			                		waterCourseDetailBtn = 'waterCourseDetailBtn'
			                		waterCourseDetailBtnTxt = '详情'
			                		if(_this.orWaterCheckFlag == true) {
			                			if(item.bizStatus == 0) {
											waterCourseCheckBtnTxt = '审核'
											waterCourseCheckBtn = 'waterCourseCheckBtn'
			                			}
			                		}
			                	}
								waterCourseHtml +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
									'<div class="mui-input-row">' +
										'<label>订单号:</label>' +
										'<input type="text" disabled="disabled" value=" '+item.orderHeader.orderNum+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>流水号:</label>' +
										'<input type="text" disabled="disabled" value=" '+item.serialNum +' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>线下支付金额:</label>' +
										'<input type="text" disabled="disabled" value=" '+item.unlinePayMoney+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>实收金额:</label>' +
										'<input type="text" disabled="disabled" value=" '+ item.realMoney +' ">' + 
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>流水状态:</label>' +
										'<input type="text" disabled="disabled" value=" '+orWaterStatus+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>创建时间:</label>' +
										'<input type="text" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>更新时间:</label>' +
										'<input type="text" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
									'</div>' +
									'<div class="app_color40 mui-row app_text_center content_part operation " id="foot">' +
										'<div class="'+ waterCourseDetailBtn +'" waterCourseId="'+ item.id +'">' +
											waterCourseDetailBtnTxt +
										'</div>'+
										'<div class="'+waterCourseCheckBtn+'"  waterCourseId="'+ item.id +'">' +
											waterCourseCheckBtnTxt +
										'</div>'+
									'</div>' +
								'</div>'
								});
								$('#ordWaterCourseList').append(waterCourseHtml);
								_this.stOrdHrefHtml();
						} else {
								$('.mui-pull-bottom-pocket').html('');
								$('#ordWaterCourseList').append('<p class="noneTxt">暂无数据</p>');
								mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);							
						}
		                if(res.data.page.totalPage==pager.pageNo){		                	
			                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);			                
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
		    _this.ordHrefHtml();
	    },
	    ordHrefHtml: function() {
        	var _this = this;
        	console.log(_this.userInfo.statu)
			/*返回*/
			if(_this.userInfo.statu=='unline'){
				$('#back').on('tap', function() {
					var url = $(this).attr('url');
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
						extras: {
							statu:'unline',
						}
					})
			    })
			}else if(_this.userInfo.statu=='undefined'){
//				alert(1)
				$('#back').on('tap', function() {
					var url = $(this).attr('url');
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
						extras: {
							statu:'',
						}
					})
			    })
			}
			/*查询*/
			$('.app_header').on('tap', '#orWaterSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordwaterCouListSearch.html",
						extras:{
//							staListId: conId,
						}
					})
				}
			})
        },
		stOrdHrefHtml: function() {
			var _this = this;
		 /*审核*/
	       $('.content_part').on('tap', '.waterCourseCheckBtn', function() {
				var url = $(this).attr('url');
				var waterCourseId = $(this).attr('waterCourseId');//流水 ID
				var source = 'examine';
				if(url) {
					mui.toast('子菜单不存在')
				} else if(waterCourseId == waterCourseId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordwaterCourseCheck.html",
						extras: {
							waterCourseId: waterCourseId,
							source:source,
						}
					})
				}
			}),
		/*详情*/
			$('.content_part').on('tap', '.waterCourseDetailBtn', function() {
				var url = $(this).attr('url');
				var waterCourseId = $(this).attr('waterCourseId');
				var source = 'detail';
				if(url) {
					mui.toast('子菜单不存在')
				} else if(waterCourseId == waterCourseId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordwaterCourseDetail.html",
						extras: {
							waterCourseId: waterCourseId,
							source:source,
						}
					})
				}
			})
        },
        //时间戳转化方法：
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
		//查询过来渲染页面:
		seachFunc:function(){
			var _this = this;
			var waterCourseHtml = '';
//			var nameTxt = '';
//			if(_this.userInfo.checkStatus) {
//				nameTxt = decodeURIComponent(_this.userInfo.checkStatus)
//			}else {
//				nameTxt = ''
//			}
			var nameTxts = '';
			if(_this.userInfo.Purchasing) {
				nameTxts = decodeURIComponent(_this.userInfo.Purchasing)
			}else {
				nameTxts = ''
			}
			$.ajax({
				type: 'GET',
                url: '/a/biz/order/bizOrderHeaderUnline/list4Mobile',
				data: {
					pageNo: 1,
					pageSize: 20,
					'orderHeader.orderNum': _this.userInfo.orderNum,
					serialNum: _this.userInfo.serialNum
				},
				dataType: 'json',
				success: function(res) {
					mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
					var orWaterStatusList = [];
                    $.ajax({
                		type: "GET",
		                url: "/a/sys/dict/listData",
		                data:{type:'biz_order_unline_bizStatus'},
		                dataType: "json",
		                async:false,
		                success: function(zl){
		                	orWaterStatusList = zl;
                		}
                	});
					var arrLen = res.data.page.list.length;
                    if(arrLen > 0) {
                        $.each(res.data.page.list, function(i, item) {
//                      	console.log(item)
                        	$.each(orWaterStatusList,function(z, l) {
		                		if(item.bizStatus == l.value) {
		                			orWaterStatus = l.label//流水状态
		                		}
		                	});
		                	var waterCourseDetailBtn = '';
							var waterCourseDetailBtnTxt = '';
							var waterCourseCheckBtn = '';
							var waterCourseCheckBtnTxt = '';
		                	if(_this.orWaterConFlag == false) {
		                		waterCourseDetailBtn = 'waterCourseDetailBtn'
		                		waterCourseDetailBtnTxt = '详情'
		                		if(_this.orWaterCheckFlag == true) {
		                			if(item.bizStatus == 0) {
										waterCourseCheckBtnTxt = '审核'
										waterCourseCheckBtn = 'waterCourseCheckBtn'
		                			}
		                		}
		                	}
							waterCourseHtml +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
								'<div class="mui-input-row">' +
									'<label>订单号:</label>' +
									'<input type="text" disabled="disabled" value=" '+item.orderHeader.orderNum+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>流水号:</label>' +
									'<input type="text" disabled="disabled" value=" '+item.serialNum +' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>线下支付金额:</label>' +
									'<input type="text" disabled="disabled" value=" '+item.unlinePayMoney+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>实收金额:</label>' +
									'<input type="text" disabled="disabled" value=" '+ item.realMoney +' ">' + 
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>流水状态:</label>' +
									'<input type="text" disabled="disabled" value=" '+orWaterStatus+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>创建时间:</label>' +
									'<input type="text" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>更新时间:</label>' +
									'<input type="text" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
								'</div>' +
								'<div class="app_color40 mui-row app_text_center content_part operation " id="foot">' +
									'<div class="'+ waterCourseDetailBtn +'" waterCourseId="'+ item.id +'">' +
										waterCourseDetailBtnTxt +
									'</div>'+
									'<div class="'+waterCourseCheckBtn+'"  waterCourseId="'+ item.id +'">' +
										waterCourseCheckBtnTxt +
									'</div>'+
								'</div>' +
							'</div>'
							});
							$('#ordWaterCourseList').append(waterCourseHtml);
							_this.stOrdHrefHtml();
					}else{
						$('#ordWaterCourseList').append('<p class="noneTxt">暂无数据</p>');
						$('#orWaterSechBtn').hide();
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

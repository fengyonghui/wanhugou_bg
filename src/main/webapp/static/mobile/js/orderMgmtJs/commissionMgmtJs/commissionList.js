(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.commOrdFlag = "false";
		this.commAuditFlag = "false";
		this.commEditFlag = "false";
		this.commRefundFlag = "false";
		this.commDoRefundFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加biz:order:bizOrderHeader:view biz:order:bizOrderHeader:edit
			this.getPermissionList1('biz:order:bizOrderHeader:view','commOrdFlag')//true 操作
			this.getPermissionList3('biz:order:bizOrderHeader:edit','commEditFlag')//true 删除
			
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
//			console.log(_this.commOrdFlag)
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
							//查询页面传过来的值
							var nameTxts = '';
							if(_this.userInfo.conName) {
								nameTxts = decodeURIComponent(_this.userInfo.conName)
							}else {
								nameTxts = ''
							}
							if(_this.userInfo.orderNum==undefined){
								_this.userInfo.orderNum="";
							}
							if(_this.userInfo.serllerName==undefined){
								_this.userInfo.serllerName="";
							}
							if(_this.userInfo.serllerPhone==undefined){
								_this.userInfo.serllerPhone="";
							}
							if(_this.userInfo.commissionStatus==undefined){
								_this.userInfo.commissionStatus="";
							}	
							if(_this.userInfo.customerPhone==undefined){
								_this.userInfo.customerPhone="";
							}
							if(_this.userInfo.itemNo==undefined){
								_this.userInfo.itemNo="";
							}
							if(_this.userInfo.customerName==undefined){
								_this.userInfo.customerName="";
							}
							if(_this.userInfo.centersName==undefined){
								_this.userInfo.centersName="";
							}
							if(nameTxts==undefined){
								nameTxts="";
							}
							if(_this.userInfo.includeTestData==undefined){
								_this.userInfo.includeTestData="";
							}
		            	    var statu = _this.userInfo.statu;	
			                var f = document.getElementById("commList");
			                var childs = f.childNodes;
			                for(var i = childs.length - 1; i >= 0; i--) {
			                    f.removeChild(childs[i]);
			                }			                
			                $('.mui-pull-caption-down').html('');
			                if(_this.userInfo.isFunc){
		                	    //查询过来传的参数
		                    	pager['size']= 20;
		                    	pager['pageNo'] = 1;
		                    	pager['targetPage'] = 'COMMISSION_ORDER';
		                    	pager['orderNum'] = _this.userInfo.orderNum;//订单编号
		                    	pager['serllerName'] = _this.userInfo.serllerName;//零售商名称
		                    	pager['serllerPhone'] = _this.userInfo.serllerPhone;//零售商电话
		                    	pager['commissionStatus'] = _this.userInfo.commissionStatus,//结佣状态
		                    	pager['customer.phone'] =  _this.userInfo.customerPhone,//经销商电话
		                    	pager['itemNo'] = _this.userInfo.itemNo,//商品货号
		                    	pager['customer.id'] = _this.userInfo.customerName,//经销店名称
		                    	pager['centersName'] = _this.userInfo.centersName,//采购中心
		                    	pager['con.name'] = nameTxts,//客户专员
		                    	pager['page.includeTestData'] = _this.userInfo.includeTestData;//测试数据
		                    	getData(pager);
		                    }else{
		                    	//直接进来的参数数据
		                    	pager['size']= 20;
			                    pager['pageNo'] = 1;
			                    pager['targetPage'] = 'COMMISSION_ORDER';
			                    getData(pager);
		                    }				                
			            }
			        }
			    })
		    }
		    function getData(params){
		    	var commHtmlList = '';
		    	var ass=[];
		        mui.ajax("/a/biz/order/bizOrderHeader/listData4mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	console.log(res)
		            	var dataRow = res.data.roleSet;
						var arrLen = res.data.page.list.length;
						if(arrLen <20){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						}
                        var that=this;
                        /*当前用户信息*/
						var userId = '';
						$.ajax({
			                type: "GET",
			                url: "/a/getUser",
			                dataType: "json",
			                async:false,
			                success: function(user){
								userId = user.data.id
			                }
			            });

                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
								console.log(item)
	                        //订单类型
							var orderTypeTxt = '';
			          	    $.ajax({
				                type: "GET",
				                url: "/a/sys/dict/getDictLabel4Mobile",
				                dataType: "json",
				                data: {
				                	value:item.orderType,
				                	type: "biz_order_type",
				                	defaultValue:'未知状态'
				                },
				                async:false,
				                success: function(hz){
				                	orderTypeTxt=hz.data.dictLabel;
				                }
				            });
								/*已结佣*/
//
//								var alreadyKnotBtnTxt = '';
								/*申请结佣*/
								var applyKnotBtn = '';
								var applyKnotBtnTxt = '';
								var noKnotBtn = '';
								var noKnotBtnTxt = '';
								var alreadyKnotBtn = '';
								var alreadyKnotBtnTxt = '';
								/*结佣详情*/
								var commDetailBtn = '';
								var commDetailBtnTxt = '详情';
								/*删除*/
								var commDeleteBtn = '';
								var commDeleteBtnTxt = '';
								/*恢复*/
								var commRecoverBtn = '';
								var commRecoverBtnTxt = '';

								if(_this.commOrdFlag == true) {
									if(item.applyCommStatus == 'no'){
										applyKnotBtnTxt="申请结佣";
										applyKnotBtn="applyKnotBtn"
									}
									if(item.applyCommStatus == 'yes' && item.bizCommission.bizStatus == '0'){
										applyKnotBtnTxt="未结佣";
										applyKnotBtn="noKnotBtn"
									}
									if(item.applyCommStatus == 'yes' && item.bizCommission.bizStatus == '1'){
										applyKnotBtnTxt="已结佣";
										applyKnotBtn="alreadyKnotBtn"
									}
									if(_this.commEditFlag == true){
										if(userId!=""&&userId==1){
											commDeleteBtn = 'commDeleteBtn';
								            commDeleteBtnTxt = '删除';
										}
										if(item.delFlag!=null && item.delFlag =='0'){
											commDetailBtnTxt="详情";
											commDeleteBtn = 'commRecoverBtn';
								            commDeleteBtnTxt = '恢复';
										}
									}


								}
							commHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group" id="rodiv_' + item.orderType + '">'+
								'<div class="mui-input-row">' +
									'<label>订单编号:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.orderNum+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>订单类型:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+orderTypeTxt+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>经销店:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.customer.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>创建时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>更新时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
								'</div>' +
								'<div class="app_color40 mui-row app_text_center content_part operation">' +
										'<div class="mui-col-xs-4 '+ applyKnotBtn +'" orderIds="'+ item.id +'" totalDetail="'+ item.totalDetail +'" totalCommission="'+ item.commission +'" sellerId="'+ item.sellersId +'" orderNum="'+ item.orderNum +'">' +
											'<div class="">'+applyKnotBtnTxt+'</div>' +
										'</div>' +
										'<div class="mui-col-xs-4 commDetailBtn" id="'+ commDetailBtn +'" staOrdId="'+ item.id +'" totalDetail="'+ item.totalDetail +'" totalCommission="'+ item.commission +'" sellerId="'+ item.sellersId +'">' +
											'<div class="">'+commDetailBtnTxt+'</div>' +
										'</div>' +
										'<div class="mui-col-xs-4 '+ commDeleteBtn +'" orderIds="'+ item.id +'" statu="'+ item.statu +'" source="'+ item.source +'">' +
											'<div class="">'+ commDeleteBtnTxt+'</div>' +
										'</div>' +
									'</div>' +
							'</div>'

								});
								$('#commList').append(commHtmlList);
								_this.stOrdHrefHtml();
								
					}else{
						$('.mui-pull-bottom-pocket').html('');
						$('#orderList').append('<p class="noneTxt">暂无数据</p>');
						$('#OrdSechBtn').hide();
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
		getPermissionList1: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.commOrdFlag = res.data;
                }
            });
       },
        getPermissionList3: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.commEditFlag = res.data;
                }
            });
        },
        ordHrefHtml: function() {
        	var _this = this;
        	/*查询*/
			$('.app_header').on('tap', '#OrdSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commListsearch.html",
						extras:{
//							statu: myStatu,
						}
					})
				}
			});
		/*首页*/
			$('#nav').on('tap','.staHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			})
        },
		stOrdHrefHtml: function() {
			var _this = this;
			//已结佣
			$('.alreadyKnotBtn').on('tap', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var orderNum = $(this).attr('orderNum');
				console.log(orderNum)
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/commissionMgmtHtml/alreadlycomList.html",
						extras: {
							orderNum: orderNum,
						}
					})
				}
			}),
		    /*申请结佣*/
	        $('.applyKnotBtn').on('tap', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var totalDetail=$(this).attr('totalDetail');
				var totalCommission=$(this).attr('totalCommission');
				var sellerId=$(this).attr('sellerId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commDetil.html",
						extras: {
							orderIds: orderIds,
							totalDetail:totalDetail,
							totalCommission:totalCommission,
							sellerId:sellerId
						}
					})
				}
			}),	
		/*详情*/
			$('.content_part').on('tap', '.commDetailBtn', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('staOrdId');
				var totalDetail = $(this).attr('totalDetail');
				var totalCommission = $(this).attr('totalCommission');
				var sellerId = $(this).attr('sellerId');
				
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commDetil.html",
						extras: {
							orderIds: orderIds,
							totalDetail:totalDetail,
							totalCommission:totalCommission,
							sellerId:sellerId,
							option: 'detail'
						}
					})
				}
			}),
			//删除
			$('.commDeleteBtn').on('tap', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var statu=$(this).attr('statu');
				var source=$(this).attr('source');
				
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认删除该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderHeader/delete4Mobile",
				                data: {id:orderIds,statu:statu,source:source},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commissionList.html",
											extras: {
	//												inListId:inListId,
											}
										})
					                },300);
			                	}
			            	})
						}else {

						}
					})
				}
			})
			//恢复
			$('.commRecoverBtn').on('tap', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var statu=$(this).attr('statu');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认恢复该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderHeader/recovery4Mobile",
				                data: {id:orderIds,statu:statu},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commissionList.html",
											extras: {
	//												inListId:inListId,
											}
										})
					                },300);
				                	
			                	}
			            	})
						}else {
							
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
		}		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

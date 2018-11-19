(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.staOrdFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
			this.getPermissionList('biz:order:bizOrderHeader:view','staOrdFlag')//true 操作
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
//			console.log(_this.staOrdFlag)
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
		    	var staffHtmlList = '';
		    	var ass=[];
		        mui.ajax("/a/biz/order/bizCommission/applyCommissionForm4Mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	console.log(res)
		            	var dataRow = res.data.roleSet;
		            	
		            	return;
		            	
		            	
						var arrLen = res.data.page.list.length;
						if(arrLen <20){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						}
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
								console.log(item)
								/*已结佣*/
								var alreadyKnotBtn = '';
								var alreadyKnotBtnTxt = '';
								/*申请结佣*/
								var applyKnotBtn = '';
								var applyKnotBtnTxt = '';
								/*结佣详情*/
								var commDetailBtn = '';
								var commDetailBtnTxt = '';
								/*删除*/
								var commDeleteBtn = '';
								var commDeleteBtnTxt = '';
								
							staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group" id="rodiv_' + item.orderType + '">'+
								'<div class="mui-input-row">' +
									'<label>订单编号:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderNum+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>订单类型:</label>' +
									'<input type="text" class="mui-input-clear orderTypeTxt" disabled="disabled" value=" '+orderTypeTxt+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>经销店:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.customer.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>佣金:</label>' +
									'<input type="text" class="mui-input-clear" value=" '+ ProcessName +' " disabled="disabled">' + 
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>创建时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>更新时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
								'</div>' +
								'<div class="app_color40 mui-row app_text_center content_part operation " id="foot">' +
									'<div class="'+ alreadyKnotBtn +'" staOrdId="'+ item.id +'">' +
										alreadyKnotBtnTxt +
									'</div>'+
									'<div class="'+applyKnotBtn+'"  staOrdId="'+ item.id +'">' +
										applyKnotBtnTxt +
									'</div>'+
									'<div class="'+commDetailBtn+'"  staOrdId="'+ item.id +'">' +
										commDetailBtnTxt +
									'</div>'+
									'<div class="'+commDeleteBtn+'"  staOrdId="'+ item.id +'">' +
										commDeleteBtnTxt +
									'</div>'+
								'</div>' +
							'</div>'

								});
								$('#orderList').append(staffHtmlList);
								_this.stOrdHrefHtml();
								//先隐藏Ro订单
//								var RoList=$('#refreshContainer div[id^=rodiv_8]');
//								$(RoList).hide();
								
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
		getPermissionList: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.staOrdFlag = res.data;
                }
            });
        },
        ordHrefHtml: function() {
        	var _this = this;
        	/*查询*/
        	var myStatu = $('#myStatu').val();
			$('.app_header').on('tap', '#OrdSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderListSeach.html",
						extras:{
							statu: myStatu,
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
		/*修改*/
	       $('.content_part').on('tap', '.ordAmendBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				var statu=$(this).attr('ordstatu');
				var source=$(this).attr('ordsource');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
						extras: {
							staOrdId: staOrdId,
							statu:statu,
							source:source,
						}
					})
				}
			}),	
		/*详情*/
			$('.content_part').on('tap', '.ordDetailBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
				var orderDetail="details";
				var statu=$(this).attr('ordstatu');
				var source=$(this).attr('ordsource');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordDetail.html",
						extras: {
							staOrdId: staOrdId,
							orderDetails:orderDetail,
							statu:statu,
							source:source,
						}
					})
				}
			}),
			//删除
			$('.content_part').on('tap', '.ordDeleteBtn', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('staOrdId');
//				console.log(staOrdId)
				var statu=$(this).attr('ordstatu');
				var source=$(this).attr('ordsource');
				
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认删除该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderHeader/delete4Mobile",
				                data: {id:staOrdId,statu:statu,source:source},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
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

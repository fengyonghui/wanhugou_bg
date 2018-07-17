(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.btnshow()
			this.getData()
//			this.getKey()
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			//下拉刷新
			 $(document).ready(function(){
            //上拉加载下拉刷新
            mui.init({
                pullRefresh: {
                    container: '#refreshContainer',
//                  down: {
//                      contentdown: "下拉可以刷新", //可选，在下拉可刷新状态时，下拉刷新控件上显示的标题内容
//                      contentover: "释放立即刷新", //可选，在释放可刷新状态时，下拉刷新控件上显示的标题内容
//                      contentrefresh: "正在刷新…", //可选，正在刷新状态时，下拉刷新控件上显示的标题内容
//                      auto: false,//可选,默认false.首次加载自动下拉刷新一次
//                      callback: pulldownRefresh
//                  },
                    up: {
                        contentrefresh: '正在加载...',
                        contentnomore:'我是有底线的',
                        callback: pullupRefresh
 
                    }
                }
            });
            /**
              * 上拉加载
              */
            function pullupRefresh() {
            	 console.log(123)
//              setTimeout(function () {
//                  mui('#refreshContainer').pullRefresh().endPullupToRefresh((isOver)); //参数为true代表没有更多数据了。
//                 // getData();//ajax
//                  console.log(123)
//              }, 500);
            }
 
        });

		},
//		getKey: function() {
//			var _this = this;
//			if
//			request.getAttribute("key");
//			$("#inTex").html("审核")
//		},
		
		getData: function(down) {
			var _this = this;
			var url = $(this).attr('url');
			var i=0
			if(down == 'down'){
			   i++
			}else{
				i=1
			}
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/listData4Mobile",
				data: {
					parentId:_this.userInfo.poId,
					pageNo:i
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
//					$('#pageId').html(res.data.page.html)
					var pHtmlList = '';
					$.each(res.data.resultList, function(i, item) {
						console.log(item)
						var startBtn = '';
						var classBtn = '';
						var payBtn = '';
/*有没有开启审核*/		if(item.process) {
							startBtn = '审核'
							classBtn = 'shenHe'
						}else {
							startBtn = '开启审核'
							classBtn = 'startShenhe'
						}
						var code = item.process.purchaseOrderProcess.code;
						var bizStatus = item.bizStatus;
						var payment = item.currentPaymentId;
/*审核流程*/		        if(code==7 || code==-1) {
							startBtn = ''
						}
						var applyStatus = item.process.bizStatus;  
/*有没有申请支付单*/		if((code==7 && applyStatus==1 && bizStatus=='部分支付') || (code==7 && payment=='')) {
							payBtn = '申请付款';
						}else {
							payBtn = ''
						}
						pHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
							'<div class="mui-input-row">' +
								'<label>采购单号:</label>' +
								'<input id="orderNum" name="orderNum" type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderNum+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>供应商:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.vendOffice+' ">' +
							'</div>' +
//							'<div class="mui-input-row">' +
//								'<label>创建时间:</label>' +
//								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.process.createTime)+' ">' +
//							'</div>' +
							'<div class="app_font_cl content_part mui-row app_text_center">' +
								'<div class="mui-col-xs-2" id="startCheckBtn" disabled="disabled">' +
									'<li class="mui-table-view-cell '+classBtn+'"  listId="'+ item.id +'" codeId="'+ code +'">'+startBtn+'</li>' +
								'</div>' +
								'<div class="mui-col-xs-3 ApplyPayListBtn">' +
									'<li class="mui-table-view-cell paying" listId="'+ item.id +'" poId="'+ item.id +'">'+ payBtn +'</li>' +
								'</div>' +
								'<div class="mui-col-xs-4 payListBtn" listId="'+ item.id +'">' +
									'<li class="mui-table-view-cell">支付申请列表</li>' +
								'</div>'+
								'<div class="mui-col-xs-3 detailBtn" listId="'+ item.id +'">' +
									'<li class="mui-table-view-cell">详情</li>' +
								'</div>'+
							'</div>' +
						'</div>'
					});
					$("#addBtn").html(pHtmlList)
				}
			});
		_this.hrefHtml()
		},
        hrefHtml: function() {
			var _this = this;
		/*申请付款*/
			$('#addBtn').on('tap', '.paying', function() {
				var url = $(this).attr('url');
				var poId = $(this).attr('poId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(poId == poId) {
					GHUTILS.OPENPAGE({
						url: "../../mobile/html/applyPay.html",
						extras: {
							poId: poId,
						}
					})
				}
			}),
		/*支付单列表*/
            $('#addBtn').on('tap','.payListBtn',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
				var poId = $(this).attr('poId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(listId==listId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/ApplyPayList.html",
						extras: {
								poId:poId,
								listId:listId,
						}
					})
                }
			}),
		/*详情*/
            $('#addBtn').on('tap','.detailBtn',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(listId==listId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/details.html",
						extras: {
								listId:listId,
						}
					})
                }
			})
            //审核
            $('#addBtn').on('tap','.shenHe',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
				var codeId = $(this).attr('codeId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(listId==listId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/check.html",
						extras: {
								listId:listId,
								codeId:codeId,
						}
					})
                }
			})
            //开启审核
            $('#addBtn').on('tap','.startShenhe',function(){
            	var url = $(this).attr('url');
				var listId = $(this).attr('listId');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(listId==listId) {
                	GHUTILS.OPENPAGE({
						url: "../../mobile/html/startCheck.html",
						extras: {
								listId:listId,
						}
					})
                }
			})
        },
//		
		btnshow: function() {
			$('#search_btn').on('tap', function() {
				mui('.mui-off-canvas-wrap').offCanvas().toggle()
			})
			$('.closeBtn').on('tap', function() {
				mui('.mui-off-canvas-wrap').offCanvas().toggle()
			})
		},
		formatDateTime: function(unix) {
        	var _this = this;

    		var now = new Date(parseInt(unix) * 1);
	        now =  now.toLocaleString().replace(/年|月/g, "-").replace(/日/g, " ");
	        if(now.indexOf("下午") > 0) {
	            if (now.length == 18) {
	                var temp1 = now.substring(0, now.indexOf("下午"));   //2014/7/6
	                var temp2 = now.substring(now.indexOf("下午") + 2, now.length);  // 5:17:43
	                var temp3 = temp2.substring(0, 1);    //  5
	                var temp4 = parseInt(temp3); // 5
	                temp4 = 12 + temp4;  // 17
	                var temp5 = temp4 + temp2.substring(1, temp2.length); // 17:17:43
//	                now = temp1 + temp5; // 2014/7/6 17:17:43
//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
	                now = now.replace("-"); //  2014-7-6 17:17:43
	            }else {
	                var temp1 = now.substring(0, now.indexOf("下午"));   //2014/7/6
	                var temp2 = now.substring(now.indexOf("下午") + 2, now.length);  // 5:17:43
	                var temp3 = temp2.substring(0, 2);    //  5
	                if (temp3 == 12){
	                    temp3 -= 12;
	                }
	                var temp4 = parseInt(temp3); // 5
	                temp4 = 12 + temp4;  // 17
	                var temp5 = temp4 + temp2.substring(2, temp2.length); // 17:17:43
//	                now = temp1 + temp5; // 2014/7/6 17:17:43
//	                now = now.replace("/", "-"); //  2014-7/6 17:17:43
	                now = now.replace("-"); //  2014-7-6 17:17:43
	            }
	        }else {
	            var temp1 = now.substring(0,now.indexOf("上午"));   //2014/7/6
	            var temp2 = now.substring(now.indexOf("上午")+2,now.length);  // 5:17:43
	            var temp3 = temp2.substring(0,1);    //  5
	            var index = 1;
	            var temp4 = parseInt(temp3); // 5
	            if(temp4 == 0 ) {   //  00
	                temp4 = "0"+temp4;
	            }else if(temp4 == 1) {  // 10  11  12
	                index = 2;
	                var tempIndex = temp2.substring(1,2);
	                if(tempIndex != ":") {
	                    temp4 = temp4 + "" + tempIndex;
	                }else { // 01
	                    temp4 = "0"+temp4;
	                }
	            }else {  // 02 03 ... 09
	                temp4 = "0"+temp4;
	            }
	            var temp5 = temp4 + temp2.substring(index,temp2.length); // 07:17:43
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
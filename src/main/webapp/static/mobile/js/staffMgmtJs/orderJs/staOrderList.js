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
			// 页数
			var page = 0;
			// 每页展示10个
			var size = 10;
			//下拉刷新
			$('.inListAdd').dropload({
				scrollArea: window,
				domDown: {
					domClass: 'dropload-down',
					domRefresh: '<div class="dropload-refresh">↑上拉加载更多内容</div>',
					domLoad: '<div class="dropload-load"><span class="loading"></span>加载中...</div>',
					domNoData: '<div class="dropload-noData">暂无更多内容</div>'
				},
				loadDownFn: function(me) {
				    page++;
					var inPHtmlList = '';
					$.ajax({
						type: 'GET',
						url: '/a/biz/request/bizRequestHeader/list4Mobile?page2='+page+'&size='+size,
						data: {
							pageNo: page
						},
						dataType: 'json',
						success: function(res) {
							console.log(res)
							var dataRow = res.data.roleSet;
							var arrLen = res.data.page.list.length;
							
							if(arrLen > 0) {
								$.each(res.data.page.list, function(i, item) {
									console.log(item)
									
									inPHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
										'<div class="mui-input-row">' +
											'<label>订单编号:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.reqNo+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>经销店:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+bizstatusTxt+' ">' +
										'</div>' +
										'<div class="mui-input-row">' +
											'<label>业务状态:</label>' +
											'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
										'</div>' +
										'<div class="app_font_cl content_part mui-row app_text_center">' +
											'<div class="mui-col-xs-4 '+staCheckSucBtn+'" inListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell"> '+staCheckSuc+'</li>' +
											'</div>'+
											'<div class="mui-col-xs-4 '+staOraAmendBtn+'"  inListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">'+staOraAmend+'</li>' +
											'</div>'+
											'<div class="mui-col-xs-4 '+staOrDetalBtn+'"  inListId="'+ item.id +'">' +
												'<li class="mui-table-view-cell">详情</li>' +
											'</div>'+
											'<div class="mui-col-xs-4 app_c0" inListId="'+ item.id +'"  bizStatus="'+item.bizStatus+'">' +
												'<li class="mui-table-view-cell">'+ staOff +'</li>' +
											'</div>'+
										'</div>' +
									'</div>'
								});
							} else {
								// 锁定
								me.lock();
								// 无数据
								me.noData();
							}
							// 为了测试，延迟1秒加载
							setTimeout(function() {
								// 插入数据到页面，放到最后面
								$('.inListAdd').append(inPHtmlList);
								_this.inHrefHtml()
								// 每次数据插入，必须重置
								me.resetload();
							}, 1000);
						},
						error: function(xhr, type) {
							alert('Ajax error!222222');
							// 即使加载出错，也得重置
							me.resetload();
						}
					});
				},
				threshold: 50
			});
//			_this.comfirDialig()
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
		inHrefHtml: function() {
			var _this = this;
		/*查询*/
			$('.header').on('tap', '#staOrdSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdSech.html",
						extras:{
							
						}
					})
				}
			}),
		/*首页*/
			$('#nav').on('tap','.inHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			}),
		 /*审核状态*/
	       $('.listBlue').on('tap', '.staCheckSucBtn', function() {
				var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(inListId == inListId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdCheck.html",
						extras: {
							inListId: inListId,
						}
					})
				}
			}),
		/*修改*/
	       $('.listBlue').on('tap', '.staOraAmendBtn', function() {
				var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(inListId == inListId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdAmend.html",
						extras: {
							inListId: inListId,
						}
					})
				}
			}),	
		/*详情*/
			$('.listBlue').on('tap', '.staOrDetalBtn', function() {
				var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(inListId == inListId) {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrdDetail.html",
						extras: {
							inListId: inListId,
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
			var inPHtmlList = '';
			var nameTxt = '';
			if(_this.userInfo.name) {
				nameTxt = decodeURIComponent(_this.userInfo.name)
			}else {
				nameTxt = ''
			}
			
			$.ajax({
				type: 'GET',
				url: '/a/biz/request/bizRequestHeader/list4Mobile',
				data: {
					pageNo: 1,
					reqNo:_this.userInfo.reqNo,
					name:nameTxt,
					'fromOffice.id':_this.userInfo.fromOffice,
					bizStatus:_this.userInfo.bizStatusid,
					'varietyInfo.id':_this.userInfo.varietyInfoid
				},
				dataType: 'json',
				success: function(res) {
//							console.log(res)
					var dataRow = res.data.roleSet;
					var arrLen = res.data.page.list.length;
					if(arrLen > 0) {
						$.each(res.data.page.list, function(i, item) {
							console.log(item)
							
							
							inPHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
								'<div class="mui-input-row">' +
									'<label>订单编号:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.reqNo+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>经销店:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+bizstatusTxt+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>业务状态:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
								'</div>' +
								'<div class="app_font_cl content_part mui-row app_text_center">' +
									'<div class="mui-col-xs-3 '+staCheckSucBtn+'" inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell"> '+staCheckSuc+'</li>' +
									'</div>'+
									'<div class="mui-col-xs-3 '+staOraAmendBtn+'"  inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">'+staOraAmend+'</li>' +
									'</div>'+
									'<div class="mui-col-xs-3 '+staOrDetalBtn+'"  inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">详情</li>' +
									'</div>'+
									'<div class="mui-col-xs-3" inListId="'+ item.id +'"  bizStatus="'+item.bizStatus+'">' +
										'<li class="mui-table-view-cell">'+ staOff +'</li>' +
									'</div>'+
								'</div>' +
							'</div>'
						});
						$('.inListAdd').append(inPHtmlList);
						_this.inHrefHtml()
					}else{
						$('.inListAdd').append('<p class="noneTxt">暂无数据</p>');
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

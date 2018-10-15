
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
			$('.membListAdd').dropload({
				scrollArea: window,
				domDown: {
					domClass: 'dropload-down',
					domRefresh: '<div class="dropload-refresh">↑上拉加载更多内容</div>',
					domLoad: '<div class="dropload-load"><span class="loading"></span>加载中...</div>',
					domNoData: '<div class="dropload-noData">暂无更多内容</div>'
				},
				loadDownFn: function(me) {
				    page++;
					// 拼接HTML
					//'/a/biz/request/bizRequestHeader/list4Mobile?page2='+page+'&size='+size
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
								/*业务状态*/
									var bizstatus = item.bizStatus;
									var bizstatusTxt = '';
									if(bizstatus==0) {
										bizstatusTxt = "未审核"
									}else if(bizstatus==1) {
										bizstatusTxt = "首付支付"
									}else if(bizstatus==2) {
										bizstatusTxt = "全部支付"
									}else if(bizstatus==4) {
										bizstatusTxt = "审核中"
									}else if(bizstatus==5) {
										bizstatusTxt = "审核通过"
									}else if(bizstatus==6) {
										bizstatusTxt = "审批中"
									}else if(bizstatus==7) {
										bizstatusTxt = "审批完成"
									}else if(bizstatus==10) {
										bizstatusTxt = "采购中"
									}else if(bizstatus==13) {
										bizstatusTxt = "部分结算"
									}else if(bizstatus==15) {
										bizstatusTxt = "采购完成"
									}else if(bizstatus==20) {
										bizstatusTxt = "备货中"
									}else if(bizstatus==25) {
										bizstatusTxt = "供货完成"
									}else if(bizstatus==30) {
										bizstatusTxt = "收货完成"
									}else if(bizstatus==37) {
										bizstatusTxt = "结算完成"
									}else if(bizstatus==40) {
										bizstatusTxt = "取消"
									}else {
										bizstatusTxt = "未知类型"
									}
								/*审核按钮*/	
									var inCheck = '';
									var inCheckBtn='';
									if (item.commonProcess.requestOrderProcess) {
								 		var codeNum = item.commonProcess.requestOrderProcess.code;
									}
									if(item.commonProcess.requestOrderProcess) {
										var DataRoleGener = item.commonProcess.requestOrderProcess.roleEnNameEnum;
										var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
										if(item.commonProcess && fileRoleData.length>0 && bizstatus<5 && codeNum != -1) {
											inCheck = '审核'
											inCheckBtn='inCheckBtn'
											
										}else {
											inCheck = ''
											inCheckBtn=''
										}
									}
								//取消、修改、付款
									var inPay = '';
									var inPayBtn = '';
									var inCancel = '';
									var inCancelBtn = '';
									var inAmend = '';
									var inAmendBtn = '';
									if(_this.cancelAmendPayFlag == true){
										/*修改按钮*/
										inAmend = '修改'
										inAmendBtn = 'inAmendBtn'
										/*付款按钮*/
										if(bizstatus==35 || bizstatus==40 || item.recvTotal == item.totalMoney) {
											inPay = ''
											inPayBtn = ''
										}else {
											inPay = '付款'
											inPayBtn = 'inPayBtn'
										}
										/*取消按钮*/	
										if(bizstatus<5) {
											inCancel = '取消'
											inCancelBtn = 'inCancelBtn'
										}else {
											inCancel = ''
											inCancelBtn = ''
										}
									}else {
										inAmendBtn = ''
										inAmend = ''
									}
								//详情
									var inDetail = '';
									var inDetailBtn = '';
									if(_this.detileFlag == true) {
										inDetail = '详情'
										inDetailBtn = 'inDetailBtn'
									}else {
										inDetail = ''
										inDetailBtn = ''
									}
								/*品类名称*/	
									var varietyInfoName = '';
									if(item.varietyInfo.name) {
										varietyInfoName = item.varietyInfo.name
									}else {
										varietyInfoName = ''
										
									}
								/*审核状态*/		
									var checkStatus = '';
									if(item.commonProcess.requestOrderProcess) {
										checkStatus = item.commonProcess.requestOrderProcess.name
									}else {
										checkStatus = ''
									}
									inPHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
								'<div class="mui-input-row">' +
									'<label>机构名称:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.reqNo+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>归属地区:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+bizstatusTxt+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>机构编码:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>电话:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.name +' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>下单时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>机构类型:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+varietyInfoName+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>申请人:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.createBy.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>更新时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
								'</div>' +
								'<div class="app_font_cl content_part mui-row app_text_center">' +
									'<div class="mui-col-xs-4 '+membChangBtn+'" inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">变更客户专员</li>' +
									'</div>' +
									'<div class="mui-col-xs-2 '+membAmendBtn+'" inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">修改</li>' +
									'</div>'+
									'<div class="mui-col-xs-4 '+membAddBtn+'"  inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">添加下级机构</li>' +
									'</div>'+
									'<div class="mui-col-xs-2 '+membRecordBtn+'" inListId="'+ item.id +'"  bizStatus="'+item.bizStatus+'">' +
										'<li class="mui-table-view-cell">沟通记录</li>' +
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
								$('.membListAdd').append(inPHtmlList);
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
			$('.header').on('tap', '#membSearchBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../html/memberMgmtHtml/meSearch.html",
						extras:{
						}
						
					})
				}
					
			}),
		/*机构添加*/
			$('#nav').on('tap','.membAddBtn', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/memberMgmtHtml/mem.html",
					extras: {
						
					}
				})
			}),
		/*首页*/
			$('#nav').on('tap','.membHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			}),
		/*变更客户专员*/
			$('.listBlue').on('tap', '.membChangBtn', function() {
				var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(inListId == inListId) {
					GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inventoryDetails.html",
						extras: {
							inListId: inListId,
						}
					})
				}
			}),
		/*修改*/
            $('.content').on('tap','.membAmendBtn', function() {
				var url = $(this).attr('url');
                var reqId = $(this).attr('inListId');
				GHUTILS.OPENPAGE({
					url: "../../html/inventoryMagmetHtml/inventoryAmend.html",
					extras: {
                        reqId: reqId,
					}
				})
			}),
        /*添加下级机构*/
	       $('.listBlue').on('tap', '.membAddBtn', function() {
					var url = $(this).attr('url');
					var inListId = $(this).attr('inListId');
					if(url) {
						mui.toast('子菜单不存在')
					} else if(inListId == inListId) {
						GHUTILS.OPENPAGE({
							url: "../../html/inventoryMagmetHtml/inPay.html",
							extras: {
								inListId: inListId,
							}
						})
					}
				}),
        /*沟通记录*/
            $('.listBlue').on('tap','.membRecordBtn',function(){
            	var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				var bizStatus = $(this).attr('bizStatus');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(inListId==inListId) {
                	GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inCheck.html",
						extras: {
								inListId:inListId,
								bizStatus:bizStatus,
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
//							console.log(item)
						/*业务状态*/
							var bizstatus = item.bizStatus;
							var bizstatusTxt = '';
							if(bizstatus==0) {
								bizstatusTxt = "未审核"
							}else if(bizstatus==1) {
								bizstatusTxt = "首付支付"
							}else if(bizstatus==2) {
								bizstatusTxt = "全部支付"
							}else if(bizstatus==4) {
								bizstatusTxt = "审核中"
							}else if(bizstatus==5) {
								bizstatusTxt = "审核通过"
							}else if(bizstatus==6) {
								bizstatusTxt = "审批中"
							}else if(bizstatus==7) {
								bizstatusTxt = "审批完成"
							}else if(bizstatus==10) {
								bizstatusTxt = "采购中"
							}else if(bizstatus==13) {
								bizstatusTxt = "部分结算"
							}else if(bizstatus==15) {
								bizstatusTxt = "采购完成"
							}else if(bizstatus==20) {
								bizstatusTxt = "备货中"
							}else if(bizstatus==25) {
								bizstatusTxt = "供货完成"
							}else if(bizstatus==30) {
								bizstatusTxt = "收货完成"
							}else if(bizstatus==37) {
								bizstatusTxt = "结算完成"
							}else if(bizstatus==40) {
								bizstatusTxt = "取消"
							}else {
								bizstatusTxt = "未知类型"
							}
						/*审核按钮*/	
							var inCheck = '';
							var inCheckBtn='';
							if (item.commonProcess.requestOrderProcess) {
						 		var codeNum = item.commonProcess.requestOrderProcess.code;
							}
							if(item.commonProcess.requestOrderProcess) {
								var DataRoleGener = item.commonProcess.requestOrderProcess.roleEnNameEnum;
								var fileRoleData = dataRow.filter(v => DataRoleGener.includes(v));
								if(item.commonProcess && fileRoleData.length>0 && bizstatus<5 && codeNum != -1) {
									inCheck = '审核'
									inCheckBtn='inCheckBtn'
									
								}else {
									inCheck = ''
									inCheckBtn=''
								}
							}
						//取消、修改、付款
							var inPay = '';
							var inPayBtn = '';
							var inCancel = '';
							var inCancelBtn = '';
							var inAmend = '';
							var inAmendBtn = '';
							if(_this.cancelAmendPayFlag == true){
								/*修改按钮*/
								inAmend = '修改'
								inAmendBtn = 'inAmendBtn'
								/*付款按钮*/
								if(bizstatus==35 || bizstatus==40 || item.recvTotal == item.totalMoney) {
									inPay = ''
									inPayBtn = ''
								}else {
									inPay = '付款'
									inPayBtn = 'inPayBtn'
								}
								/*取消按钮*/	
								if(bizstatus<5) {
									inCancel = '取消'
									inCancelBtn = 'inCancelBtn'
								}else {
									inCancel = ''
									inCancelBtn = ''
								}
							}else {
								inAmendBtn = ''
								inAmend = ''
							}
						//详情
							var inDetail = '';
							var inDetailBtn = '';
							if(_this.detileFlag == true) {
								inDetail = '详情'
								inDetailBtn = 'inDetailBtn'
							}else {
								inDetail = ''
								inDetailBtn = ''
							}
						/*品类名称*/	
							var varietyInfoName = '';
							if(item.varietyInfo.name) {
								varietyInfoName = item.varietyInfo.name
							}else {
								varietyInfoName = ''
								
							}
						/*审核状态*/		
							var checkStatus = '';
							if(item.commonProcess.requestOrderProcess) {
								checkStatus = item.commonProcess.requestOrderProcess.name
							}else {
								checkStatus = ''
							}
							inPHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
								'<div class="mui-input-row">' +
									'<label>机构名称:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.reqNo+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>归属地区:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+bizstatusTxt+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>机构编码:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>电话:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.name +' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>下单时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>机构类型:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+varietyInfoName+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>申请人:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.createBy.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>更新时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
								'</div>' +
								'<div class="app_font_cl content_part mui-row app_text_center">' +
									'<div class="mui-col-xs-4 '+inAmendBtn+'" inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">'+inAmend+'</li>' +
									'</div>' +
									'<div class="mui-col-xs-2 '+inCancelBtn+'" inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell"> '+inCancel+'</li>' +
									'</div>'+
									'<div class="mui-col-xs-4 '+inPayBtn+'"  inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">'+inPay+'</li>' +
									'</div>'+
									'<div class="mui-col-xs-2 '+inCheckBtn+'" inListId="'+ item.id +'"  bizStatus="'+item.bizStatus+'">' +
										'<li class="mui-table-view-cell">'+ inCheck +'</li>' +
									'</div>'+
								'</div>' +
							'</div>'
						});
						$('.membListAdd').append(inPHtmlList);
						_this.inHrefHtml()
					}else{
						$('.membListAdd').append('<p class="noneTxt">暂无数据</p>');
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
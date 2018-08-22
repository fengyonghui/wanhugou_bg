
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
			$.ajax({
				type: 'GET',
				url: '/a/biz/request/bizRequestHeader/list4Mobile?page2='+page+'&size='+size,
				data: {
					pageNo: page
				},
				dataType: 'json',
				success: function(res) {
					console.log(res)
					var staffHtmlList = '';
					$.each(res.data.page.list, function(i, item) {
						console.log(item)
						staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
							'<div class="mui-input-row">' +
								'<label>采购中心:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.reqNo+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>客户专员:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+bizstatusTxt+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>电话:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>经销店名称:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.name +' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>负责人:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>详细地址:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+varietyInfoName+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>采购频次:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.createBy.name+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>累计金额:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
							'</div>' +
							'<div class="mui-input-row">' +
								'<label>首次开单:</label>' +
								'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
							'</div>' +
							'<div class="app_font_cl content_part mui-row app_text_center">' +
								'<div class="mui-col-xs-6">' +
								'</div>'+
								'<div class="mui-col-xs-6 staReMoveBtn" staListId="'+ item.id +'">' +
									'<li class="mui-table-view-cell">移除</li>' +
								'</div>'+
							'</div>' +
						'</div>'
					});
				}
			});
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
			$('.header').on('tap', '#staffSearchBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/relevanceHtml/relSech.html",
						extras:{
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
							$.ajax({
				                type: "GET",
				                url: "",
				                data: {id:staListId},
				                dataType: "json",
				                success: function(res){
				                	alert('操作成功！')
				                	GHUTILS.OPENPAGE({
										url: "../../../html/staffMgmtHtml/relevanceHtml/relList.html",
										extras: {
												staListId:staListId,
										}
									})
			                	}
			            	})
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
							staffHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
								'<div class="mui-input-row">' +
									'<label>归属公司:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.reqNo+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>归属部门:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+bizstatusTxt+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>登录名:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+checkStatus+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>姓名:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.name +' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>手机:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.createDate)+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>洽谈数:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+varietyInfoName+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>新增订单量:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.createBy.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>新增回款额:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>新增会员:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.updateDate)+' ">' +
								'</div>' +
								'<div class="app_font_cl content_part mui-row app_text_center">' +
									'<div class="mui-col-xs-4" staListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">关联经销店</li>' +
									'</div>' +
									'<div class="mui-col-xs-4" inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">订单管理</li>' +
									'</div>'+
									'<div class="mui-col-xs-2"  inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">修改</li>' +
									'</div>'+
									'<div class="mui-col-xs-2" inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">删除</li>' +
									'</div>'+
								'</div>' +
							'</div>'
						});
						$('.staffList').append(staffHtmlList);
						_this.stHrefHtml()
					}else{
						$('.staffList').append('<p class="noneTxt">暂无数据</p>');
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

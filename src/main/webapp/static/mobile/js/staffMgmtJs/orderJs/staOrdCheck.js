(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.prew = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
					//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.getData()
		},
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizOrderHeader/form4Mobile",
                data: {
                	id: _this.userInfo.staOrdId,
                	orderDetails: 'details',
	                flag: _this.userInfo.flagTxt
                },
                dataType: "json",
                success: function(res){
					console.log(res)
					$('#firstPart').val(res.data.entity2.customer.name);
					$('#firstPrincipal').val(res.data.custUser.name);
					$('#firstMobile').val(res.data.custUser.mobile);
					
					$('#partB').val(res.data.vendUser.vendor.name);
					$('#partBPrincipal').val(res.data.vendUser.name);
					$('#partBMobile').val(res.data.vendUser.mobile);
					
					$('#partCPrincipal').val(res.data.orderCenter.consultants.name);
					$('#partCMobile').val(res.data.orderCenter.consultants.mobile);
					if(res.data.appointedTimeList) {
						$.each(res.data.appointedTimeList, function(n, v) {
							$('#staPayTime').val(_this.formatDateTime(v.appointedDate));
							$('#staPayMoney').val(v.appointedMoney);
						})
					}else {
						$('#staPayTime').val();
						$('#staPayMoney').val();
					}
					//订单id
					$('#ordId').val(_this.userInfo.staOrdId);
					
					
					
					var item = res.data.bizOrderHeader;
					console.log(item)
					//交货时间
					$('#appointedTime').val(item.bizLocation.appointedTime);
					//标志位
					$('#flag').val(item.flag);
					//选择供货方式
					if(item.orderType==5){
						$('#orderTypebox').hide();
					}
					//本地发货 id
					if(item.localSendIds) {
						$('#localSendIds').val(item.localSendIds);
					}else {
						$('#localSendIds').val();
					}
					//详细地址
					if(item.bizLocation) {
						$('#provinceId').val(item.bizLocation.province.id); 
						$('#cityId').val(item.bizLocation.city.id); 
						$('#regionId').val(item.bizLocation.region.id); 
					}

					var shouldPay = item.totalDetail + item.totalExp + item.freight;
					var serverPrice = (item.totalDetail+item.totalExp+item.freight)-item.totalBuyPrice;
					//发票状态
					var invStatusTxt = '';
					if(item.invStatus==0) {
						invStatusTxt = "不开发票"					
					}
					//业务状态
					var statusTxt = '';
					if(item.staStatus=15) {
						statusTxt = "供货中"
					}
					
					$('#staPoordNum').val(item.orderNum);
					$('#staRelNum').val(item.customer.name);
					$('#staPototal').val(item.totalDetail);
					$('#staAdjustmentMoney').val(item.totalExp);
					$('#staFreight').val(item.freight);
					$('#staShouldPay').val(shouldPay);
					var poLastDa = (item.receiveTotal/(item.totalDetail+item.totalExp+item.freight))*100+'%';
//						console.log(poLastDa)
					$('#staPoLastDa').val(item.receiveTotal);
					$('#staServerPrice').val(serverPrice);
					$('#staInvoice').val(invStatusTxt);
					$('#staStatus').val(statusTxt);
					$('#staConsignee').val(item.bizLocation.receiver);
					$('#staMobile').val(item.bizLocation.phone);
					$('#staShippAddress').val(item.bizLocation.pcrName);
					$('#staDateilAddress').val(item.bizLocation.address);
					$('#staEvolve').val();
					
					_this.statusListHtml(res.data)
//					_this.checkProcessHtml(res.data);
					_this.commodityHtml(res.data)
					_this.comfirDialig(res.data)

                }
            });
		},
		//状态流程
		statusListHtml:function(data){
			var _this = this;
			console.log(data)
			var statusLen = data.statusList.length;
			if(statusLen > 0) {
				var pHtmlList = '';
				$.each(data.statusList, function(i, item) {
					console.log(item)
					var step = i + 1;
					pHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>处理人:</label>'+
								'<input type="text" value="'+ item.createBy.name +'" class="mui-input-clear" disabled>'+
						    '</div>'+
							'<div class="mui-input-row">'+
						        '<label>状态:</label>'+
						        '<input type="text" class="mui-input-clear" disabled>'+
//						         value="'++'"
						    	'<label>时间:</label>'+
						        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
				});
				$("#staStatusMenu").html(pHtmlList)
			}
		},
		//审核流程
//		checkProcessHtml:function(data){
//			var _this = this;
//			console.log(data)
//			var auditLen = data.auditList.length;
//			if(auditLen > 0) {
//				var CheckHtmlList ='';
//				$.each(data.auditList, function(i, item) {
//					console.log(item)
//					var step = i + 1;
//					var current = item.current;
//					if(current !== 1) {
//						CheckHtmlList +='<li class="step_item">'+
//						'<div class="step_num">'+ step +' </div>'+
//						'<div class="step_num_txt">'+
//							'<div class="mui-input-row">'+
//								'<label>处理人:</label>'+
//								'<input type="text" value="'+ item.user.name +'" class="mui-input-clear" disabled>'+
//						    '</div>'+
//							'<div class="mui-input-row">'+
//						        '<label>批注:</label>'+
//						        '<input type="text" value="'+ item.description +'" class="mui-input-clear" disabled>'+
//						    	'<label>状态:</label>'+
//						        '<input type="text" value=" '+ item.jointOperationLocalProcess.name +' " class="mui-input-clear" disabled>'+
//						    '</div>'+
//						'</div>'+
//					'</li>'
//					}
//					if(current == 1) {
//						CheckHtmlList +='<li class="step_item">'+
//						'<div class="step_num">'+ step +' </div>'+
//						'<div class="step_num_txt">'+
//							'<div class="mui-input-row">'+
//								'<label>当前状态:</label>'+
//								'<input type="text" value="'+ item.jointOperationLocalProcess.name +'" class="mui-input-clear" disabled>'+
//						   		'<label>时间:</label>'+
//						        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " class="mui-input-clear" disabled>'+
//						    '</div>'+
//						'</div>'+
//					'</li>'
//					}
//				});
//				$("#staCheckMenu").html(CheckHtmlList)
//			}
//		},
		commodityHtml: function(data) {
			var _this = this;
			console.log(data)
			var orderDetailLen = data.bizOrderHeader.orderDetailList.length;
			if(orderDetailLen > 0) {
				var htmlCommodity = '';
				$.each(data.bizOrderHeader.orderDetailList, function(i, item) {
					console.log(item)
					var opShelfInfo = '';
					if(item.shelfInfo.opShelfInfo) {
						opShelfInfo = item.shelfInfo.opShelfInfo.name
					}else {
						opShelfInfo = ''
					}
					htmlCommodity += '<div class="mui-row app_bline commodity" id="' + item.id + '">' +
	                    '<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>详情行号:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.lineNo + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>货架名称:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + opShelfInfo + '" disabled></div></li></div></div>' +
                    
                    	'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>商品名称:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuName + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>创建时间:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div></div>' +
	                   
                    	'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>商品出厂价:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.buyPrice + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>供应商:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.vendor.name + '" disabled></div></li></div></div>' +
	                   
                    	 '<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>供应商电话:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.primary.mobile + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>商品单价:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div></div>'+
					
						'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>采购数量:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.ordQty + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>总 额:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice * item.ordQty + '" disabled></div></li></div></div>'+
					
						'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>已发货数量:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.sentQty + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>发货方:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.suplyis.name + '" disabled></div></li></div></div>'+
						
                    	'<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">商品货号:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div>' +
	                    
	                    '<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">商品编号:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.partNo + '" disabled></div></li></div>' +
	                    
//                  	'<div class="mui-row">' +
//	                    '<li class="mui-table-view-cell">' +
//	                    '<div class="mui-input-row ">' +
//	                    '<label class="commodityName">已生成的采购单:</label>' +
//	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.name + '" disabled></div></li></div>'+
                    '</div>'
				});
				$("#staCheckCommodity").html(htmlCommodity)
			}
		},
		comfirDialig: function(data) {
			var _this = this;
			document.getElementById("rejectBtns").addEventListener('tap', function() {
				var btnArray = ['否', '是'];
				mui.confirm('确认驳回审核吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						_this.ajaxData(15)
					} else {
						
					}
				})
			});
			document.getElementById("checkBtns").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
				var btnArray = ['否', '是'];
				mui.confirm('确认通过审核吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
//								console.log(inText)
						_this.ajaxData(45)
					} else {
						
					}
				})
			});
		},
		ajaxData:function(num) {
			var _this = this;
			var r2 = document.getElementsByName("localOriginType");
            var localOriginType = "";
            for (var i = 0; i < r2.length; i++) {
                if (r2[i].checked == true) {
                    localOriginType = r2[i].value;
                }
            }
            console.log(localOriginType)
            if(num==15){
            	datas: {
					id:$('#ordId').val(),
					flag:$('#flag').val(),
					objJsp:num,
					'bizLocation.address':$('#staDateilAddress').val(),
					'bizLocation.appointedTime': $('#appointedTime').val(),
					localSendIds: $('#localSendIds').val(),
					'bizLocation.province.id': $('#provinceId').val() ,
					'bizLocation.city.id': $('#cityId').val(), 
					'bizLocation.region.id': $('#regionId').val(),
					boo: _this.prew,
					localOriginType:localOriginType
				}
            }
            if(num==45){
            	datas: {
					id:$('#ordId').val(),
					flag:$('#flag').val(),
					objJsp:num,
					'bizLocation.address':$('#staDateilAddress').val(),
					'bizLocation.appointedTime': $('#appointedTime').val(),
					localSendIds: $('#localSendIds').val(),
					'bizLocation.province.id': $('#provinceId').val() ,
					'bizLocation.city.id': $('#cityId').val(), 
					'bizLocation.region.id': $('#regionId').val(),
					localOriginType:localOriginType
				}
            }
			$.ajax({
				type: "POST",
				url: "/a/biz/order/bizOrderHeader/Commissioner4mobile",
				data: datas,
				dataType: "json",
				success: function(res) {
					var stcheckIdTxt = _this.userInfo.stcheckIdTxt;
					console.log(res)
					if(res.data=='ok'){
						alert('操作成功!')
						GHUTILS.OPENPAGE({
						url: "../../../html/staffMgmtHtml/orderHtml/staOrderList.html",
						extras: {
							staListId:stcheckIdTxt,
							}
						})
					}
				},
				error: function (e) {
				    //服务器响应失败处理函数
//				    console.info(data);
//				    console.info(status);
				    console.info(e);
				}
			});
		},
		rejectData:function(rejectTxt,num) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/biz/request/bizRequestHeader/audit",
				data: {
					id:_this.userInfo.inListId,
					currentType:$('#currentType').val(),
					auditType:num,
					description:rejectTxt
				},
				dataType: "json",
				success: function(res) {
//					console.log(res)
					if(res.ret==true){
						alert('操作成功!')
						GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inventoryList.html",
						extras: {
							}
						})
					}
				}
			});
		},
		formatDateTime: function(unix) {
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







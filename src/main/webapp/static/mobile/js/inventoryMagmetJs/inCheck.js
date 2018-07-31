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
			//this.radioShow()
//			this.btnshow()
//			this.searchShow()
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
					//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.getData()
			_this.comfirDialig()
		},
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeader/form4Mobile",
                data: {id:_this.userInfo.inListId},
                dataType: "json",
                success: function(res){
//					console.log(res)
					/*业务状态*/
					var bizstatus = res.data.entity.bizStatus;
					var bizstatusTxt = '';
					if(bizstatus==0) {
						bizstatusTxt = "未审核"
					}else if(bizstatus==1) {
						bizstatusTxt = "首付支付"
					}else if(bizstatus==2) {
						bizstatusTxt = "全部支付"
						inCheck = "审核"
					}else if(bizstatus==5) {
						bizstatusTxt = "审核通过"
					}else if(bizstatus==6) {
						bizstatusTxt = "审批中"
					}else if(bizstatus==7) {
						bizstatusTxt = "审批完成"
					}else if(bizstatus==10) {
						bizstatusTxt = "采购中"
					}else if(bizstatus==15) {
						bizstatusTxt = "采购完成"
					}else if(bizstatus==20) {
						bizstatusTxt = "备货中"
					}else if(bizstatus==25) {
						bizstatusTxt = "供货完成"
					}else if(bizstatus==30) {
						bizstatusTxt = "收货完成"
					}else if(bizstatus==35) {
						bizstatusTxt = "部分结算"
					}else if(bizstatus==37) {
						bizstatusTxt = "结算完成"
					}else if(bizstatus==40) {
						bizstatusTxt = "取消"
					}else {
						bizstatusTxt = '未知类型'
					}
					
					$('#checkPoDizstatus').val(bizstatusTxt)
					$('#checkPoordNum').val(res.data.entity.reqNo)
					$('#checkOrordNum').val(res.data.entity.fromOffice.name)
					$('#inPoLastDa').val(_this.formatDateTime(res.data.entity.recvEta))
					$('#currentType').val(res.data.entity.commonProcess.type)
					_this.commodityHtml(res.data)
					_this.statusListHtml(res.data)
                }
            });
		},
		statusListHtml:function(data){
			var _this = this;
//			console.log(data)
			var pHtmlList = '';
//			var len = data.bizPoHeader.commonProcessList.length
			$.each(data.statusList, function(i, item) {
//				console.log(item)
				
				var checkBizStatus = '';
				if(item.bizStatus==0) {
					checkBizStatus = '未审核'
				}
				var step = i + 1;
				pHtmlList +='<li class="step_item">'+
					'<div class="step_num">'+ step +' </div>'+
					'<div class="step_num_txt">'+
						'<div class="mui-input-row">'+
							'<label>处理人:</label>'+
					        '<textarea name="" rows="" cols="" disabled>'+ item.createBy.name +'</textarea>'+
					    '</div>'+
						'<br />'+
						'<div class="mui-input-row">'+
					        '<label>状态:</label>'+
					        '<input type="text" value="'+ checkBizStatus +'" class="mui-input-clear" disabled>'+
					    	'<label>时间:</label>'+
					        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
					    '</div>'+
					'</div>'+
				'</li>'
			});
			$("#inCheckMen").html(pHtmlList)
		},
		commodityHtml: function(data) {
			var _this = this;
//			console.log(data)
			var htmlCommodity = '';
			$.each(data.reqDetailList, function(i, item) {
//				console.log(item)
				htmlCommodity +='<li class="mui-table-view-cell mui-media">'+
//		产品图片
					'<div class="photoParent mui-pull-left position_Re">'+
						'<img class="position_Ab" src="'+item.skuInfo.productInfo.imgUrl+'">'+
					'</div>'+
//		产品信息
					'<div class="mui-media-body commodity">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.brandName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>供应商：</label>'+
							'<input type="text" class="font-color mui-input-clear" value="'+ item.skuInfo.productInfo.vendorName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.name +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品编码：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.prodCode +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>单价：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>申报数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.reqQty +'" reqQty disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>仓库名称：</label>'+
							'<input type="text" class="mui-input-clear" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>库存数量：</label>'+
							'<input type="text" class="mui-input-clear" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品总库存数量：</label>'+
							'<input type="text" class="mui-input-clear" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>已收货数量：</label>'+
							'<input type="text" class="mui-input-clear" value="'+ item.recvQty +'" disabled>'+
						'</div>'+
					'</div>'+
				'</li>'
			});
			$("#checkCommodityMenu").html(htmlCommodity)
		},
		comfirDialig: function() {
			var _this = this;
			document.getElementById("inRejectBtn").addEventListener('tap', function() {
				var btnArray = ['否', '是'];
				mui.confirm('确认驳回审核吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						
						var btnArray = ['取消', '确定'];
						mui.prompt('请输入驳回理由：', '驳回理由', '', btnArray, function(a) {
							if(a.index == 1) {
								var rejectTxt = a.value;
//								console.log(rejectTxt)
								if(a.value=='') {
									mui.toast('驳货理由不能为空！')
								}else {
									_this.rejectData(rejectTxt,2)
								}
							} else {
								//		            info.innerText = '你点了取消按钮';
							}
						})
						//		            info.innerText = '你刚确认MUI是个好框架';
					} else {
						//		            info.innerText = 'MUI没有得到你的认可，继续加油'
					}
				})
			});
			document.getElementById("inCheckBtn").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入通过理由：', '通过理由', '', btnArray, function(e) {
					if(e.index == 1) {
						var inText = e.value;
						if(e.value=='') {
							mui.toast('通过理由不能为空！')
							return;
						}else {
							var btnArray = ['否', '是'];
							mui.confirm('确认通过审核吗？', '系统提示！', btnArray, function(choice) {
							if(choice.index == 1) {
//								console.log(inText)
								_this.ajaxData(inText,1)
							} else {
								//		            info.innerText = '你点了取消按钮';
							}
						})
						}

						//		            info.innerText = '你刚确认MUI是个好框架';
					} else {
						//		            info.innerText = 'MUI没有得到你的认可，继续加油'
					}
				})
			});
//			alert("操作成功")
		},
		ajaxData:function(inText,num) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/biz/request/bizRequestHeader/audit",
				data: {
					id:_this.userInfo.inListId,
					currentType:$('#currentType').val(),
					auditType:num,
					description:inText
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
		//			var data = _this.getData()
//			console.log(data)
//			$('#ordNum').val(data.bizOrderHeader.orderNumber)
//			if(data.bizOrderHeader.orderNumber){
//				
//			}
//			$.each(data.bizOrderHeader.orderNumber, function(i, item) {
//						console.log(item.orderNumber)
//                       var orderNumber = item.orderNumber;
//					$('#ordNum').html(orderNumber)
////                        htmlList += 
//			});
////		                    $('#htmlMenu').html(htmlList)
//		}
							
		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);







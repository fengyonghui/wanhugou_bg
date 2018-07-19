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
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.getData1()
			_this.payComfirDialig()
		},
		getData1: function() {
			var _this = this;
			var url = $(this).attr('url');
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoPaymentOrder/listData4Mobile",
				data: {
					poId: _this.userInfo.listId
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
					$('#confirmPayId').val(res.data.bizPoHeader.id)
					var pHtmlList = '';
					if(res.data.page.list.length>0) {
						$.each(res.data.page.list, function(i, item) {
							console.log(item)
							$('#applyPayId').val(item.id)
							console.log(item.total)
							if(item.total) {
								$('#applyPayMoney').val(item.total)
							}
							var payCodeId = item.commonProcess.paymentOrderProcess.code
							$('#payCodeId').val(item.commonProcess.paymentOrderProcess.code)
							/*有没有支付单*/
							var bizStatus = '';
							if(item.bizStatus == 0) {
								bizStatus = '未支付'
							} else if(item.bizStatus == 1) {
								bizStatus = '已支付'
							}
		/*最后付款时间*/		var deadlineTime = '';
							if(item.deadline) {
//								console.log(item.deadline)
								deadlineTime = _this.formatDateTime(item.deadline);
							} else {
								deadlineTime = ''
							}
							/*实际付款时间*/
							var practicalTimeTxt = '';
							if(item.payTime) {
//								console.log(item.payTime)
								practicalTimeTxt = _this.formatDateTime(item.payTime);
							} else {
								practicalTimeTxt = ''
							}
							/*支付凭证*/
							var imgPath = ''
							if(item.imgList.imgPath) {
								var imgPath = item.imgList.imgPath;
							}else {
								imgPath = ''
							}
							var PoName = item.commonProcess.paymentOrderProcess.name
							if(PoName=='审批完成') {
//								 && item.bizStatus == 1
								pHtmlList += '<div class="mui-input-row">' +
								'<div class="mui-input-row">' +
								'<label>id：</label>' +
								'<input type="text" value="' + item.id + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>付款金额：</label>' +
								'<input type="text" value="' + item.total + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>实际付款金额：</label>' +
								'<input type="text" value="' + item.payTotal + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>最后付款时间：</label>' +
								'<input type="text" value="' + deadlineTime + '" class="mui-input-clear PoLastDa" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>实际付款时间：</label>' +
								'<input type="text" value="' + practicalTimeTxt + '" class="mui-input-clear PoPayTm" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>当前状态：</label>' +
								'<input type="text" value="' + bizStatus + '" class="mui-input-clear PoStas" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>审批状态：</label>' +
								'<input type="text" value="' + PoName + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>支付凭证：</label>' +
								'<img src="' + imgPath + '"/>' +
								'</div>'+
							'</div>'	
							}/*else if(item.bizStatus == 0) {         //确认付款html
								pHtmlList += '<div class="mui-input-row">' +
								'<div class="mui-input-row">' +
								'<label>id：</label>' +
								'<input type="text" value="' + item.id + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>付款金额：</label>' +
								'<input type="text" value="' + item.total + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>实际付款金额：</label>' +
								'<input type="text" value="' + item.payTotal + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>最后付款时间：</label>' +
								'<input type="text" value="' + deadlineTime + '" class="mui-input-clear PoLastDa" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>实际付款时间：</label>' +
								'<input type="text" value="' + practicalTimeTxt + '" class="mui-input-clear PoPayTm" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>当前状态：</label>' +
								'<input type="text" value="' + bizStatus + '" class="mui-input-clear PoStas" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>审批状态：</label>' +
								'<input type="text" value="' + PoName + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>支付凭证：</label>' +
								'<img src="' + imgPath + '"/>' +
								'</div>'+
								'<div class="app_p20" style="float:left;width:100%;">'+
							    	'<button id="confirmPayBtn" style="width: 26%;float:none;left: 50%;margin-left: -41px;" class="mui-btn mui-btn-blue">确认付款</button>'+
								'</div>'+
							'</div>'
							}*/else {
								pHtmlList += '<div class="mui-input-row">' +
								'<div class="mui-input-row">' +
								'<label>id：</label>' +
								'<input type="text" value="' + item.id + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>付款金额：</label>' +
								'<input type="text" value="' + item.total + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>实际付款金额：</label>' +
								'<input type="text" value="' + item.payTotal + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>最后付款时间：</label>' +
								'<input type="text" value="' + deadlineTime + '" class="mui-input-clear PoLastDa" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>实际付款时间：</label>' +
								'<input type="text" value="' + practicalTimeTxt + '" class="mui-input-clear PoPayTm" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>当前状态：</label>' +
								'<input type="text" value="' + bizStatus + '" class="mui-input-clear PoStas" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>审批状态：</label>' +
								'<input type="text" value="' + PoName + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>支付凭证：</label>' +
								'<img src="' + imgPath + '"/>' +
								'</div>' +
								'<div class="payingCheckBtn app_p20">'+
								    '<button type="submit" class="payCheckBtn mui-btn-blue">审核通过</button>'+
									'<button type="submit" class="payRejectBtn mui-btn-blue">审核驳回</button>'+
								'</div>'+
							'</div>'
							}
						});
				    } else {
				    	pHtmlList += '<div class="hintTxt mui-input-row">' + 
		    						'<p>暂无申请支付信息</p>'+
	    						'</div>'
				    }
					$("#addPayListBtn").html(pHtmlList)
				}
			});
//			_this.hrefHtmlConfirmPay()
		},
//		hrefHtmlConfirmPay: function() {
//			var _this = this;
//			$('#addPayListBtn').on('tap','#confirmPayBtn',function(){
//          	var url = $(this).attr('url');
//          	var poId = $(this).attr('poId');
//          	var listId = $(this).attr('listId');
//				var applyPayId = $(this).attr('applyPayId');
//				var confirmPayId =  _this.userInfo.listId;
//              if(url) {
//              	mui.toast('子菜单不存在')
//              }else if(applyPayId==applyPayId) {
//              	GHUTILS.OPENPAGE({
//						url: "../../mobile/html/confirmPayment.html",
//						extras: {
//								applyPayId:applyPayId,
//								id: _this.userInfo.listId,
//								poId:poId,
//								listId:listId,
//						}
//					})
//              }
//			})
//		},
		            
		payComfirDialig: function() {
			var _this = this;
			$('#addPayListBtn').on('click','.payRejectBtn', function() {
//			document.getElementById("payRejectBtn").addEventListener('click', function() {
				var btnArray = ['否', '是'];
				mui.confirm('确认驳回审核吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						
						var btnArray = ['取消', '确定'];
						mui.prompt('请输入驳回理由：', '驳回理由', '', btnArray, function(a) {
							if(a.index == 1) {
								var rejectTxt = a.value;
								console.log(rejectTxt)
								if(a.value=='') {
									mui.toast('驳货理由不能为空！')
								}else {
									_this.psyRejectData(rejectTxt,2)
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
			$('#addPayListBtn').on('click','.payCheckBtn', function(e) {
//			document.getElementById("payCheckBtn").addEventListener('click', function(e) {
				//e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
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
								console.log(inText)
								_this.payAjaxData(inText,1)
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
		payAjaxData:function(inText,num) {
			var _this = this;
			var payCodeId = $(this).attr('payCodeId');
			var applyPayId = $(this).attr('applyPayId');//得到支付单id
			var applyPayMoney = $(this).attr('applyPayMoney');//得到实际付款金额
//			console.log(applyPayMoney)
			//$('#mask').show()
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/auditPay",
				data: {
					id:$('#applyPayId').val(),//支付申请订单ID
					currentType:$('#payCodeId').val(),//流程code
					auditType:num,
					description:inText,
					money:$('#applyPayMoney').val()//支付单创建时申请的金额
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
					if(res.ret==true){
						//$('#mask').hide()
						GHUTILS.OPENPAGE({
						url: "../../mobile/html/purchase.html",
						extras: {
							key:res.key,
							}
						})
					}
				}
			});
		},
		psyRejectData:function(rejectTxt,num) {
			var _this = this;
			var codeId = $(this).attr('codeId');
			var applyPayId = $(this).attr('applyPayId');//得到支付单id
			//$('#mask').show()
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/auditPay",
				data: {
					id:$('#applyPayId').val(),//支付申请订单ID
					currentType:$('#payCodeId').val(),//流程code
					auditType:num,
					description:inText,
					money:$('#applyPayMoney').val()//支付单创建时申请的金额
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
					if(res.ret==true){
						//$('#mask').hide()
						GHUTILS.OPENPAGE({
						url: "../../mobile/html/purchase.html",
						extras: {
							key:res.key,
							}
						})
					}
				}
			});
			
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
		}
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
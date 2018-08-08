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
			//			this.radioShow()
			this.btnshow()
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
			var url = $(this).attr('url');
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/form4Mobile",
				data: {
					id: _this.userInfo.listId
				},
				dataType: "json",
				success: function(res) {
//					console.log(res)
					var cardNumber = res.data.bizPoHeader.vendOffice.bizVendInfo.cardNumber;
					if(cardNumber) {
						$('#PoVenBizCard').val(cardNumber)
					} else {
						$('#PoVenBizCard').val('')
					}
					var payee = res.data.bizPoHeader.vendOffice.bizVendInfo.payee;
					if(payee) {
						$('#PoVenBizPayee').val(payee)
					} else {
						$('#PoVenBizPayee').val('')
					}
					var bankName = res.data.bizPoHeader.vendOffice.bizVendInfo.bankName;
					if(bankName) {
						$('#PoVenBizBankname').val(bankName)
					} else {
						$('#PoVenBizBankname').val('')
					}
					$('#OrordNum').val(res.data.bizOrderHeader.orderNumber)
					$('#PoordNum').val(res.data.bizPoHeader.orderNumber)
					$('#Pototal').val(res.data.bizPoHeader.total)
					$('#PotoDel').val(res.data.bizPoHeader.totalDetail)
					$('#PoLastDa').val(_this.formatDateTime(res.data.bizPoHeader.lastPayDate))
					$('#PoRemark').val(res.data.bizPoHeader.remark)
					$('#PoDizstatus').val(res.data.bizPoHeader.bizStatus)
					$('#PoVenName').val(res.data.bizPoHeader.vendOffice.name)
					//                  if(res.data.bizOrderHeaderTest){
					//                 	   $('#PoDizstatus').val(res.data.bizPoHeader.bizStatus)
					//	                   $('#PoVenName').val(res.data.bizPoHeader.vendOffice.name)
					//	                   $('#PoVenBizCard').val(res.data.bizPoHeader.vendOffice.bizVendInfo.cardNumber)
					//	                   $('#PoVenBizPayee').val(res.data.bizPoHeader.vendOffice.bizVendInfo.payee)
					//	                   $('#PoVenBizBankname').val(res.data.bizPoHeader.vendOffice.bizVendInfo.bankName)
					//                   }else{
					//                  		$('#img').hide()//show()
					//                  		$('.priceList').hide()//show()
					//                  	}
				}
			});
		},
		comfirDialig: function() {
			var _this = this;
			var rejectBtn = document.getElementById("rejectBtn");
			var prewStatus = -this.prew;
			document.getElementById("rejectBtn").addEventListener('tap', function() {
				var btnArray = ['否', '是'];
				mui.confirm('确认驳回流程吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {

						var btnArray = ['取消', '确定'];
						mui.prompt('请输入驳回理由：', '驳回理由', '', btnArray, function(a) {
							if(a.index == 1) {
								var rejectTxt = a.value;
//								console.log(rejectTxt)
								if(a.value == '') {
									mui.toast('驳回理由不能为空！')
									return;
								} else {
									_this.ajaxPoPayData(rejectTxt)
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
			document.getElementById("checkBtn").addEventListener('tap', function(e) { //&& ($('#lastDate').val()=='' || $('#totalMoney').val()=='')
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
				var totalMoney = $('#totalMoney').val();
				var lastDate = $('#lastDate').val();
				
				if(	_this.prew ){
					if(!totalMoney  || !lastDate){
					mui.toast('最后付款时间或申请金额不能为空')
					return 
				   }
				}
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入通过理由：', '通过理由', '', btnArray, function(e) {
					if(e.index == 1) {
						var inText = e.value;
						if(e.value == '') {
							mui.toast('通过理由不能为空！')
							return;
						} else {
							var btnArray = ['否', '是'];
							mui.confirm('确认开启审核吗？', '系统提示！', btnArray, function(choice) {
								if(choice.index == 1) {
//									console.log(inText)
									_this.ajaxData(inText)
								} else {}
							})
						}
					} else {}
				})

			});
		},
		ajaxData: function(inText) {
			var _this = this;
			var totalMoney = $('#totalMoney').val();
			var lastDate = $('#lastDate').val();
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/startAudit",
				data: {
					id: _this.userInfo.listId,
					prew: _this.prew,
					prewPayTotal: totalMoney,
					prewPayDeadline: lastDate,
					desc: inText
				},
				dataType: "json",
				success: function(res) {
//					console.log(res)
					if(res.ret == true) {
						alert('操作成功!')
						//$('#mask').hide()
						GHUTILS.OPENPAGE({
							url: "../../html/purchaseMagmetHtml/purchase.html",
							extras: {
								key: res.key,
							}
						})
					}

				}
			});

		},
		/*驳回*/
		ajaxPoPayData: function(rejectTxt) {
			var _this = this;
			//$('#mask').show()
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/startAudit",
				data: {
					id: _this.userInfo.listId,
					auditType: 0,
					desc: rejectTxt
				},
				dataType: "json",
				success: function(res) {
//					console.log(res)
					if(res.ret == true) {
						alert('操作成功!')
						//$('#mask').hide()
						GHUTILS.OPENPAGE({
							url: "../../html/purchaseMagmetHtml/purchase.html",
							extras: {
								key: res.key,
							}
						})
					}

				}
			});
		},
		btnshow: function() {
			var _this = this;
			$('#showMoney').hide()
			$('input[type=radio]').on('change', function() {
				if(this.id && this.checked) {
					$('#showMoney').show()
					_this.prew = true
				} else {
					$('#showMoney').hide()
					_this.prew = false
				}
			})
		},
//		dataNew: function(str) {
//			var _this = this;
//			Data = str.replace(/-/g, '/');
//			var date = new Date(Data);
//			var time = date.getTime();
//			return time
//		},
		formatDateTime: function(unix) {

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

		/*		searchFromShow: function() {
					var mask = mui.createMask(callback);//callback为用户点击蒙版时自动执行的回调；
					mask.show();//显示遮罩
					mask.close();//关闭遮罩
				}*/
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
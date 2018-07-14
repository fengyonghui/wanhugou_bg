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
			_this.getData()
		},
		getData: function() {
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
					var pHtmlList = '';
					if(res.data.page.list.length>0) {
						$.each(res.data.page.list, function(i, item) {
//							console.log(item)
							var code = item.commonProcess.paymentOrderProcess.code
							/*有没有支付单*/
							var PoName = item.commonProcess.paymentOrderProcess.name
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
							pHtmlList += '<div class="mui-input-row">' +
								'<div class="mui-input-row">' +
								'<label>id：</label>' +
								'<input id="payingId" type="text" value="' + item.id + '" class="mui-input-clear" disabled>' +
								'</div>' +
								'<div class="mui-input-row">' +
								'<label>付款金额：</label>' +
								'<input id="payNum" type="text" value="' + item.total + '" class="mui-input-clear" disabled>' +
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
								'</div>'
						});
					
				    } else {
				    	pHtmlList += '<div class="hintTxt mui-input-row">' + 
		    						'<h1>暂无申请支付信息</h1>'+
	    						'</div>'
				    }
					$("#addPayListBtn").html(pHtmlList)
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
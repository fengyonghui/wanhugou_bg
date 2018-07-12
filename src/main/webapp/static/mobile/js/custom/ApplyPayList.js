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
				data: {poId:_this.userInfo.poId},
				dataType: "json",
				success: function(res) {
					console.log(res)
					var pHtmlList = '';
					$.each(res.data.page.list, function(i, item) {
						if(res.data.page.list=='') {
							document.write('<h1>暂无信息</h1>')
						}else {
							var   bizStatus = '';
							if(item.bizStatus==0) {
								bizStatus='未支付'
							}else if(item.bizStatus==1) {
								bizStatus='已支付'
							}
							console.log(item)
							var PoName = item.commonProcess.paymentOrderProcess.name
							console.log(PoName)
//							if(item.payTime=='') {
//								_this.formatDateTime()
//							}else {
//								_this.formatDateTime(item.payTime)
//							}
							pHtmlList +='<div class="mui-input-row">'+
								'<div class="mui-input-row">' +
							        '<label>id：</label>'+
							        '<input type="text" value="'+item.id+'" class="mui-input-clear" disabled>'+
							    '</div>'+
							    '<div class="mui-input-row">'+
							        '<label>付款金额：</label>'+
							        '<input type="text" value="'+item.total+'" class="mui-input-clear" disabled>'+
							    '</div>'+
							    '<div class="mui-input-row">'+
							        '<label>实际付款金额：</label>'+
							        '<input type="text" value="'+item.payTotal+'" class="mui-input-clear" disabled>'+
							    '</div>'+
							    '<div class="mui-input-row">'+
							        '<label>最后付款时间：</label>'+
							        '<input type="text" value="'+_this.formatDateTime(item.deadline)+'" class="mui-input-clear PoLastDa" disabled>'+
							    '</div>'+
							    '<div class="mui-input-row">'+ 
							        '<label>实际付款时间：</label>'+
							        '<input type="text" value="'+ _this.formatDateTime(item.payTime) +'" class="mui-input-clear PoPayTm" disabled>'+
							    '</div>'+
							    '<div class="mui-input-row">'+
							        '<label>当前状态：</label>'+
							        '<input type="text" value="'+ bizStatus +'" class="mui-input-clear PoStas" disabled>'+
							    '</div>'+
							    '<div class="mui-input-row">'+
							        '<label>审批状态：</label>'+
							        '<input type="text" value="'+ PoName +'" class="mui-input-clear" disabled>'+
							    '</div>'+
							    '<div class="mui-input-row">'+
							        '<label>支付凭证：</label>'+
							        '<img src="'+item.imgList.imgPath+'"/>'+
							    '</div>'+
							'</div>'
						}
						    
					});
					$("#addBtn").html(pHtmlList)
				}
			});
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
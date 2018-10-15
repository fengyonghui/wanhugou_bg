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
			this.getData();//获取数据			
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			
		},
		getData: function() {
			var _this = this;
			var datas={};
			var idd=_this.userInfo.waterCourseId;
			var source=_this.userInfo.source;
			datas={
				id:idd,
                source:source
			}
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizOrderHeaderUnline/form4Mobile",
                data:datas,
                dataType: "json",
                success: function(res){
                	console.log(res);
                	var bizOrderHeaderUnline = res.data.bizOrderHeaderUnline;
                	var imgUrlList = res.data.imgUrlList;
                	$('#orderId').val(bizOrderHeaderUnline.orderHeader.id);//订单id
                	$('#orWaterCouNum').val(bizOrderHeaderUnline.orderHeader.orderNum);//订单号
                	$('#waterCouNum').val(bizOrderHeaderUnline.serialNum);//流水号
                	$('#unlineMoney').val(bizOrderHeaderUnline.unlinePayMoney);//线下付款金额
                	$('#courseId').val(bizOrderHeaderUnline.id);//线下付款金额
                	//单据凭证
                	if(imgUrlList){
                		$.each(imgUrlList, function(i,item) {
	                		$("#orWaterPototal").append("<a><img width=\"100px\" src=\"" + item+ "\"></a>");
	                	});
                	}               	
                	$.ajax({
                		type: "GET",
		                url: "/a/sys/dict/listData",
		                data:{type:'biz_order_unline_bizStatus'},
		                dataType: "json",
		                success: function(zl){
		                	console.log(zl)
		                	$.each(zl, function(z,l) {
		                		if(l.value == bizOrderHeaderUnline.bizStatus) {
		                			$('#orWaterStatus').val(l.label);//流水状态
		                		}
		                	});
                		}
                	});
                	_this.checkBtns(bizOrderHeaderUnline.id);
			        _this.rejectBtns(bizOrderHeaderUnline.id);
                }
            });
		},
		checkBtns:function(courseId){
			var _this = this;
			document.getElementById("checkBtns").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault();				
				var btnArray = ['取消', '确定'];
				mui.confirm('请再次确认实收金额是否正确？',  '', btnArray, function(e) {
					if(e.index == 1) {
						$.ajax({
	                		type: "GET",
			                url: "/a/biz/order/bizOrderHeaderUnline/save4Mobile",
			                data:{'orderHeader.orderNum':$('#orWaterCouNum').val(),
				                serialNum:$('#waterCouNum').val(),
				                unlinePayMoney:$('#unlineMoney').val(),
				                realMoney:$('#unlineMoney').val(),
				                id:courseId
				            },
			                dataType: "json",
			                success: function(res){
			                	console.log(res)
			                	mui.toast('审核通过！');
			                	window.setTimeout(function(){
			                		GHUTILS.OPENPAGE({
										url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordwaterCourseList.html",
										extras: {
												staOrdId:$('#orderId').val(),
										}
									})
			                	},500)			                	
	                		}
	                	});	
					} else {}
				})
			});
		},
		rejectBtns:function(courseId){
			var _this = this;
			document.getElementById("rejectBtns").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault();				
				var btnArray = ['取消', '确定'];
				mui.confirm('请再次确认是否驳回？',  '', btnArray, function(e) {
					if(e.index == 1) {
						$.ajax({
	                		type: "GET",
			                url: "/a/biz/order/bizOrderHeaderUnline/changeOrderReceive4Mobile",
			                data:{
			                	id:courseId
				            },
			                dataType: "json",
			                success: function(res){
			                	console.log(res)
			                	mui.toast('审核驳回！');
			                	window.setTimeout(function(){
			                		GHUTILS.OPENPAGE({
										url: "../../../html/orderMgmtHtml/OrdermgmtHtml/ordwaterCourseList.html",
										extras: {
												staOrdId:$('#orderId').val(),
										}
									})
			                	},500)			                	
	                		}
	                	});	
					} else {}
				})
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

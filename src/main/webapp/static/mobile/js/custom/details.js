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
			$.ajax({
                type: "GET",
                url: "/a/biz/po/bizPoHeader/form4Mobile",
                data: {id:_this.userInfo.listId},
                dataType: "json",
                success: function(res){
//					console.log(res)
					var cardNumber = res.data.bizPoHeader.vendOffice.bizVendInfo.cardNumber;
					if(cardNumber) {
						$('#PoVenBizCard').val(cardNumber)
					}else {
						$('#PoVenBizCard').val('')
					}
					var payee = res.data.bizPoHeader.vendOffice.bizVendInfo.payee;
					if(payee) {
						$('#PoVenBizPayee').val(payee)
					}else {
						$('#PoVenBizPayee').val('')
					}
					var bankName = res.data.bizPoHeader.vendOffice.bizVendInfo.bankName;
					if(bankName) {
						$('#PoVenBizBankname').val(bankName)
					}else {
						$('#PoVenBizBankname').val('')
					}
                   $('#OrordNum').val(res.data.bizOrderHeader.orderNumber)
                   $('#PoordNum').val(res.data.bizPoHeader.orderNumber)
                   $('#Pototal').val(res.data.bizPoHeader.total)
                   $('#PotoDel').val(res.data.bizPoHeader.totalDetail)
                   $('#PoRemark').val(res.data.bizPoHeader.remark)
                   $('#PoDizstatus').val(res.data.bizPoHeader.bizStatus)
                   $('#PoVenName').val(res.data.bizPoHeader.vendOffice.name)
/*最后付款时间*/ 	   $('#PoLastDa').val(_this.formatDateTime(res.data.bizPoHeader.lastPayDate))
					_this.processHtml(res.data)              
                }
            });
		},
		processHtml:function(data){
			var _this = this;
//			console.log(data)
			var process = data.bizPoHeader.process;
			var pHtmlList = '';
			var len = data.bizPoHeader.commonProcessList.length
			$.each(data.bizPoHeader.commonProcessList, function(i, item) {
//				console.log(item)
				var step = i + 1;
				if(len-1==i){
					pHtmlList +='<li id="procList" class="step_item">'+
					'<div class="step_num">'+ step +' </div>'+
					'<div class="step_num_txt">'+
						'<div class="mui-input-row sucessColor">'+
							'<label>当前状态:</label>'+
					        '<textarea name="" rows="" cols="" disabled>'+ process.purchaseOrderProcess.name +'</textarea>'+
					    '</div>'+
						'<div class="mui-input-row">'+
					        '<label></label>'+
					        '<input type="text" value="" class="mui-input-clear" disabled>'+
					    	'<label></label>'+
					        '<input type="text" value="" class="mui-input-clear" disabled>'+
					    '</div>'+
					'</div>'+
				'</li>'
				}else{
					pHtmlList +='<li id="procList" class="step_item">'+
					'<div class="step_num">'+ step +' </div>'+
					'<div class="step_num_txt">'+
						'<div class="mui-input-row">'+
							'<label>批注:</label>'+
					        '<textarea name="" rows="" cols="" disabled>'+ item.description +'</textarea>'+
					    '</div>'+
						'<div class="mui-input-row">'+
					        '<label>审批人:</label>'+
					        '<input type="text" value="'+ item.user.name +'" class="mui-input-clear" disabled>'+
					    	'<label>时间:</label>'+
					        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " class="mui-input-clear" disabled>'+
					    '</div>'+
					'</div>'+
				'</li>'
				}
			});
			$("#addDetailMen").html(pHtmlList)
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

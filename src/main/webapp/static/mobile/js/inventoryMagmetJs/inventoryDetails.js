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
                url: "/a/biz/request/bizRequestHeader/form4Mobile",
                data: {id:110},
                dataType: "json",
                success: function(res){
					console.log(res)
				/*业务状态*/
					var bizstatus = res.data.entity.bizStatus;
					var bizstatusTxt = '';
					if(bizstatus==0) {
						bizstatusTxt = "未审核"
					}else if(bizstatus==5) {
						bizstatusTxt = "审核通过"
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
						bizstatusTxt = "关闭"
					}
                   $('#inPoordNum').val(res.data.entity.reqNo)
                   $('#inOrordNum').val(res.data.entity.fromOffice.name)
                   $('#inPoRemark').val(res.data.entity.remark)
                   $('#inPototal').val(res.data.entity.totalMoney)
//                 $('#inPoRemark').val(res.data.bizPoHeader.remark)
                   $('#inPoDizstatus').val(bizstatusTxt)
//                 $('#inPoVenName').val(res.data.bizPoHeader.vendOffice.name)
/*期望收货时间*/ 	   $('#inPoLastDa').val(_this.formatDateTime(res.data.entity.recvEta))
//				   $('#inplanPayDetails').val(res.data.bizPoHeader.total)
//                   $('#nowDateDetails').val(_this.formatDateTime(res.data.bizPoHeader.lastPayDate))
					_this.processHtml(res.data)
					_this.commodityHtml(res.data)
                }
            });
		},
		processHtml:function(data){
			var _this = this;
			console.log(data)
			var process = data.bizPoHeader.process;
			var pHtmlList = '';
//			var len = data.bizPoHeader.commonProcessList.length
			$.each(data.bizPoHeader.commonProcessList, function(i, item) {
				console.log(item)
				var step = i + 1;
//				if(len-1==i){
//					pHtmlList +='<li id="procList" class="step_item">'+
//					'<div class="step_num">'+ step +' </div>'+
//					'<div class="step_num_txt">'+
//						'<div class="mui-input-row sucessColor">'+
//							'<label>当前状态:</label>'+
//					        '<textarea name="" rows="" cols="" disabled>'+ process.purchaseOrderProcess.name +'</textarea>'+
//					    '</div>'+
//						'<br />'+
//						'<div class="mui-input-row">'+
//					        '<label></label>'+
//					        '<input type="text" value="" class="mui-input-clear" disabled>'+
//					    	'<label></label>'+
//					        '<input type="text" value="" class="mui-input-clear" disabled>'+
//					    '</div>'+
//					'</div>'+
//				'</li>'
//				}else{
				pHtmlList +='<li id="procList" class="step_item">'+
					'<div class="step_num">'+ step +' </div>'+
					'<div class="step_num_txt">'+
						'<div class="mui-input-row">'+
							'<label>处理人:</label>'+
					        '<textarea name="" rows="" cols="" disabled>'+ item.description +'</textarea>'+
					    '</div>'+
						'<br />'+
						'<div class="mui-input-row">'+
					        '<label>状态:</label>'+
					        '<input type="text" value="'+ item.user.name +'" class="mui-input-clear" disabled>'+
					    	'<label>时间:</label>'+
					        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " class="mui-input-clear" disabled>'+
					    '</div>'+
					'</div>'+
				'</li>'
//				}
			});
			$("#inCheckAddMenu").html(pHtmlList)
		},
		commodityHtml: function(data) {
			var _this = this;
			console.log(data)
			var commodityHtmlList = '';
			$.each(data.bizPoHeader.commonProcessList, function(i, item) {
				console.log(item)
				pHtmlList +='<li class="mui-table-view-cell mui-media">'+
//		产品图片
					'<div class="photoParent mui-pull-left position_Re">'+
						'<img class="position_Ab" src="../images/shuijiao.jpg">'+
					'</div>'+
//		产品信息
					'<div id="commodity" class="mui-media-body">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>供应商：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品编码：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>价格：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>申报数量：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>仓库名称：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>库存数量：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品总库存数量：</label>'+
							'<input type="text" class="mui-input-clear" id="" disabled>'+
						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>已收货数量：</label>'+
//							'<input type="text" class="mui-input-clear" id="" disabled>'+
//						'</div>'+
					'</div>'+
				'</li>'
				
			});
			$("#commodityMenu").html(commodityHtml)
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

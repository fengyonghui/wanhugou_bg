(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.buyPriceFlag = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData();//获取数据		
			this.getPermissionList('biz:order:buyPrice:view','buyPriceFlag')//佣金权限
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
//			_this.addRemark();
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
                    _this.buyPriceFlag = res.data;
                }
            });
        },
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizCommission/form4Mobile",
                data:{
                	id: _this.userInfo.orderIds,
					str:_this.userInfo.detail
                },
                dataType: "json",
                success: function(res){
//              	console.log(res)
					var entity = res.data.entity;
					$('#commTotalMoney').val(entity.payTotal);//佣金总金额
					$('#commPayMoney').val(entity.totalCommission);//付款金额
					$('#commcardNumber').val(entity.customerInfo.cardNumber);//代销商卡号
					$('#commName').val(entity.customerInfo.payee);//代销商收款人
					$('#commBank').val(entity.customerInfo.bankName);//代销商开户行
					//支付凭证
					if(entity.imgList){
						$.each(entity.imgList,function (m, n) {
                            $(".imgLists").append("<a href=\"" + n.imgServer + n.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + n.imgServer + n.imgPath + "\"></a>");
                        });
					}else{
						$('#imgLists').hide();
					}
					var payTime = '';
					if(entity.payTime) {
						payTime = _this.formatDateTime(entity.payTime);
					}
					$('#commTime').val(payTime);//付款时间
					$('#commRemark').val(entity.remark);//备注					
					/*审核流程*/
					if(res.data.auditList.length > 0) {
						var CheckHtmlList = '';
						$.each(res.data.auditList, function(dd,mm) {
//							console.log(mm)
							var step = dd + 1;
							if(mm.current != 1) {
	                            CheckHtmlList +='<li class="step_item">'+
									'<div class="step_num">'+ step +' </div>'+
									'<div class="step_num_txt">'+
										'<div class="mui-input-row">'+
											'<label>处理人:</label>'+
											'<input type="text" value="'+ mm.user.name +'" class="mui-input-clear" disabled>'+
									    '</div>'+
										'<div class="mui-input-row">'+
									        '<label>批注:</label>'+
									        '<input type="text" value="'+ mm.description +'" class="mui-input-clear" disabled>'+
									    	'<label>状态:</label>'+
									        '<input type="text" value=" '+ mm.paymentOrderProcess.name +' " class="mui-input-clear" disabled>'+
									    	'<label>时间:</label>'+
									        '<input type="text" value="'+ _this.formatDateTime(mm.updateTime) +'" class="mui-input-clear" disabled>'+
									    '</div>'+
									'</div>'+
								'</li>'
	                        }
							if(mm.current == 1) {
	                            CheckHtmlList +='<li class="step_item">'+
									'<div class="step_num">'+ step +' </div>'+
									'<div class="step_num_txt">'+
										'<div class="mui-input-row">'+
											'<label>当前状态:</label>'+
											'<input type="text" value="'+ mm.paymentOrderProcess.name +'" class="mui-input-clear" disabled>'+
									   		'<label>时间:</label>'+
									        '<input type="text" value=" '+ _this.formatDateTime(mm.updateTime) +' " class="mui-input-clear" disabled>'+
									    '</div>'+
									'</div>'+
								'</li>'
	                        }
						});
						$('#commCheckMenu').html(CheckHtmlList);
					}					
//					$('#ommodity').html(htmlCommodity);
					_this.checkProcessHtml(res.data);				
                }
            });
		},
		checkProcessHtml:function(data){
			var _this = this;
			var auditLen = data.auditList.length;
			if(auditLen > 0) {
				var CheckHtmlList ='';
				$.each(data.auditList, function(i, item) {
					//状态
					var ProcessName = '';
					var step = i + 1;
					var current = item.current;
					if(current !== 1) {
						if(item.objectName == 'ORDER_HEADER_SO_LOCAL') {
							ProcessName = item.jointOperationLocalProcess.name
						}
						if(item.objectName == 'ORDER_HEADER_SO_ORIGIN') {
							ProcessName = item.jointOperationOriginProcess.name
						}
						if(item.objectName == 'biz_po_header') {
							ProcessName = item.purchaseOrderProcess.name
						}
						if(item.objectName == 'biz_order_header') {
							if(data.entity2.payProportion == 1) {
								ProcessName = item.doOrderHeaderProcessFifth.name
							}
							if(data.entity2.payProportion == 2) {
								ProcessName = item.doOrderHeaderProcessAll.name
							}
						}
						//处理人
						var userName ="";
						if(item.user){
							userName = item.user.name;
						}else{
							userName = "";
						}
						//批注
						var Description ="";
						if(item.user){
							Description = item.description;
						}else{
							Description = "";
						}						
						CheckHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>处理人:</label>'+
								'<input type="text" value="'+ userName +'" class="mui-input-clear" disabled>'+
						    '</div>'+
							'<div class="mui-input-row">'+
						        '<label>批注:</label>'+
						        '<input type="text" value="'+ Description +'" class="mui-input-clear" disabled>'+
						    	'<label>状态:</label>'+
						        '<input type="text" value=" '+ ProcessName +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
					}
					if(current == 1) {
						if(item.objectName == 'ORDER_HEADER_SO_LOCAL') {
							ProcessName = item.jointOperationLocalProcess.name
						}
						if(item.objectName == 'ORDER_HEADER_SO_ORIGIN') {
							ProcessName = item.jointOperationOriginProcess.name
						}
						if(item.objectName == 'biz_po_header') {
							ProcessName = item.purchaseOrderProcess.name
						}
						if(item.objectName == 'biz_order_header') {
							ProcessName = item.doOrderHeaderProcessFifth.name
						}
						CheckHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>当前状态:</label>'+
								'<input type="text" value="'+ ProcessName +'" class="mui-input-clear" disabled>'+
						   		'<label>时间:</label>'+
						        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
					}
				});
				$("#staCheckMenu").html(CheckHtmlList);
			}else{
				$("#staCheckMenu").parent().hide();
			}
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

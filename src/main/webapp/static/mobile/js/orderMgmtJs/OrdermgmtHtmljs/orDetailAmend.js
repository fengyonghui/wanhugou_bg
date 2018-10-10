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
                url: "/a/biz/order/bizOrderHeader/form4Mobile",
                data: {id:_this.userInfo.amendId},
                dataType: "json",
                success: function(res){
					console.log(res)
					$('#ordCenter').val(res.data.bizOrderHeader.centersName);
					
					var orderDetailList = res.data.bizOrderHeader.orderDetailList;
					$.each(orderDetailList, function(y,j) {
						console.log(j)
						$('#skuInfoName').val(j.skuInfo.name);
						$('#partNo').val(j.partNo);
						$('#itemNo').val(j.skuInfo.itemNo);
						
					});
					
//					resultListHtml += '<div class="mui-row app_bline" id="serskudiv_' + skuInfo.id + '">' +
//                  
//                  '<div class="mui-row lineStyle">' +
//                  '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
//                  '<div class="mui-col-sm-10 mui-col-xs-10">' +
//                  '<li class="mui-table-view-cell app_bline3">' +
//                  '<div class="mui-input-row ">' +
//                  '<label class="">商品名称:</label>' +
//                  '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.name + '" disabled>' +
//                  '</div></li></div></div>' +
//                 
//                	'<div class="mui-row lineStyle">' +
//                  '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
//                  '<div class="mui-col-sm-10 mui-col-xs-10">' +
//                  '<li class="mui-table-view-cell app_bline3">' +
//                  '<div class="mui-input-row ">' +
//                  '<label class="">商品货号:</label>' +
//                  '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.itemNo + '" disabled>' +
//                  '</div></li></div></div>' +
//                  
//                  '<div class="mui-row lineStyle">' +
//                  '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
//                  '<div class="mui-col-sm-10 mui-col-xs-10">' +
//                  '<li class="mui-table-view-cell app_bline3">' +
//                  '<div class="mui-input-row ">' +
//                  '<label class="">商品编码:</label>' +
//                  '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.partNo + '" disabled>' +
//                  '</div></li></div></div>' +
//                  
//                  '<div class="mui-row">' +
//                  '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
//                  '<div class="mui-col-sm-5 mui-col-xs-5">' +
//                  '<li class="mui-table-view-cell app_bline3">' +
//                  '<div class="mui-input-row ">' +
//                  '<label>品牌名称:</label>' +
//                  '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.productInfo.brandName +'" disabled>' +
//                  '</div></li></div>' +
//                  '<div class="mui-col-sm-5 mui-col-xs-5">' +
//                  '<li class="mui-table-view-cell app_bline3">' +
//                  '<div class="mui-input-row ">' +
//                  '<label>供应商:</label>' +
//                  '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.productInfo.brandName + '" disabled>' +
//                  '</div></li></div></div>' +
//                 
//                  '<div class="mui-row  inAddFont">' +
//                  '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
//                  '<div class="mui-col-sm-5 mui-col-xs-5">' +
//                  '<li class="mui-table-view-cell app_bline3">' +
//                  '<div class="mui-input-row">' +
//                  '<label>结算价:</label>' +
//                  '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.buyPrice + '" disabled></div></li></div>' +
//                  '<div class="mui-col-sm-5 mui-col-xs-5">' +
//                  '<li class="mui-table-view-cell app_bline3">' +
//                  '<div class="mui-input-row">' +
//                  '<label>申报数量:</label>' +
//                  '<input type="hidden" class="mui-input-clear" value="' + skuInfo.id + '">' +
//                  '<input type="text" class="mui-input-clear" placeholder="请输入数量" id="reqQty_'+ skuInfo.id +'">' +
//                  '<font>*</font>'+
//                  '</div></li></div></div></div></div>';
                }
            });
            _this.searchSkuHtml();
		},
		searchSkuHtml: function() {
            var _this = this;
            mui('#ordAmendPoLastDaDiv').on('tap','#ordComChoiceBtn',function(){
                var itemNo = $("#ordAmendPoLastDa").val();
                $.ajax({
                    type: "post",
                    url: "/a/biz/sku/bizSkuInfo/findSkuList",
                    data: {
                    	itemNo: itemNo,//输入的商品货号
//                  	'productInfo.office.id': $('#supplierId').val()
                	},
                    success: function (result) {
                    	
//                      $("#searchInfo").empty();
                        var data = JSON.parse(result).data;
                        console.log(data)
//                      if($.isEmptyObject(data)){
//                      	mui.toast("您输入的货号有误，或者此供应商暂无商品！");
//	                    }else {
	                        $.each(data,function (keys,skuInfoList) {
	                        	console.log(skuInfoList)
	                            var prodKeys= keys.split(",");
	                            var prodId= prodKeys[0];
//                              var prodName= prodKeys[1];
	                            var prodUrl= prodKeys[2];
//                              var cateName= prodKeys[3];
//                              var prodCode= prodKeys[4];
//                              var prodOfficeName= prodKeys[5];
	                            var  brandName=prodKeys[6];
	                            //var flag=true;
	                            var resultListHtml="";
	                            var t=0;
	                            $.each(skuInfoList,function (index,skuInfo) {
                            		console.log(skuInfo)
//	                                //skuInfoId+=","+skuInfo.id;
//	                                if($("#commodityMenu").children("#serskudiv_"+skuInfo.id).length>0){
//	                                    return;
//	                                }
	                             resultListHtml += '<div class="mui-row app_bline">'+ 
	                                '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">采购中心:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.name + '" disabled></div></li></div>'+ 
	                             
									'<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品名称:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.name  +'" disabled></div></li></div>'+ 
			                   
				                  	'<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品货号:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.itemNo + '" disabled></div></li></div>'+ 
				                    
				                    '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品编码:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.partNo + '" disabled></div></li></div>'+ 
				                    
				                    '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品属性:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.partNo + '" disabled></div></li></div>'+ 
									
				                    '<div class="mui-row">'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label>货架名称:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.productInfo.brandName+ '" disabled></div></li></div>'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label>数量区间:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.productInfo.brandName + '" disabled></div></li></div></div>'+
			                   
				                    '<div class="mui-row  inAddFont">'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row">'+ 
				                    '<label>现价:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.buyPrice+  '" disabled></div></li></div>'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row">'+ 
				                    '<label>采购数量:</label>'+ 
				                    '<input type="hidden" class="mui-input-clear" value="'  +skuInfo.id + '">'+ 
				                    '<input type="text" class="mui-input-clear" placeholder="请输入数量" id="reqQty_' +skuInfo.id+ '">'+ 
				                    '<font>*</font>'+
				                    '</div></li></div></div></div>'
				                    
	                            });
//	                            t++;
	                            $("#searchInfo").append(resultListHtml);
	                        })
	                        var addButtonHtml = '<button type="submit" class="addSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">添加</button>';
                       		$("#searchInfo").append(addButtonHtml);
//                      }
                    }
                })
            });
            //$("#searchInfo").html(htmlCommodity)
            _this.addSku()
        },
        addSku:function () {
            var _this = this;
            mui('#searchInfo').on('tap','.addSkuButton',function(){
            	var removeButtonHtml = '<button id="" type="submit" class="removeSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">移除</button>';
                console.log($(this).parent())
                $(this).parent().append(removeButtonHtml)
            	$('#commodityMenu').append($(this).parent());
            	$(this).hide();
//              $(".skuinfo_check").each(function () {
//                  var cheId = $(this)[0].id;
//
//                  var cheFlag = $("#" + cheId).is(':checked');
//                  if (cheFlag == true) {
//                      var cheDiv = $("#serskudiv_" + cheId);
//                      $("#" + cheId).prop('checked',false);
//                      $("#" + cheId).hide();
//                      var removeButtonHtml = '<div id="removeBtn_' + cheId + '">' +
//                          '<button id="remove_' + cheId +'" type="submit" class="removeSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">移除' +
//                          '</button></div>';
//                          cheDiv.append(removeButtonHtml)
//                      $("#commodityMenu").append(cheDiv)
//                      _this.skuInfoIds += cheId + ",";
//                  }
//              })
            });
        },
//		statusListHtml:function(data){
//			var _this = this;
////			console.log(data)
//			var pHtmlList = '';
////			var len = data.bizPoHeader.commonProcessList.length
//			$.each(data.statusList, function(i, item) {
////				console.log(item)
////				0未审核 1首付款支付,2是全部支付 5审核通过 10 采购中 15采购完成 20备货中  25 供货完成 30收货完成 35关闭
//				var checkBizStatus = '';
//				if(item.bizStatus==0) {
//					checkBizStatus = '未审核'
//				}else if(item.bizStatus==1) {
//					checkBizStatus = '首付款支付'
//				}else if(item.bizStatus==2) {
//					checkBizStatus = '全部支付'
//				}else if(item.bizStatus==5) {
//					checkBizStatus = '审核通过'
//				}else if(item.bizStatus==10) {
//					checkBizStatus = '采购中'
//				}else if(item.bizStatus==15) {
//					checkBizStatus = '采购完成'
//				}else if(item.bizStatus==20) {
//					checkBizStatus = '备货中'
//				}else if(item.bizStatus==25) {
//					checkBizStatus = '供货完成'
//				}else if(item.bizStatus==30) {
//					checkBizStatus = '收货完成'
//				}else if(item.bizStatus==35) {
//					checkBizStatus = '关闭'
//				}else {
//					checkBizStatus = ''
//				}
//				var step = i + 1;
//				pHtmlList +='<li class="step_item">'+
//					'<div class="step_num">'+ step +' </div>'+
//					'<div class="step_num_txt">'+
//						'<div class="mui-input-row">'+
//							'<label>处理人:</label>'+
//					        '<textarea name="" rows="" cols="" disabled>'+ item.createBy.name +'</textarea>'+
//					    '</div>'+
//						'<br />'+
//						'<div class="mui-input-row">'+
//					        '<label>状态:</label>'+
//					        '<input type="text" value="'+ checkBizStatus +'" class="mui-input-clear" disabled>'+
//					    	'<label>时间:</label>'+
//					        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
//					    '</div>'+
//					'</div>'+
//				'</li>'
//			});
//			$("#inCheckAddMenu").html(pHtmlList)
//		},
//		commodityHtml: function(data) {
//			var _this = this;
////			console.log(data)
//			var htmlCommodity = '';
//			$.each(data.reqDetailList, function(i, item) {
////				console.log(item)
//				htmlCommodity +='<li class="mui-table-view-cell mui-media">'+
////		产品图片
//					'<div class="photoParent mui-pull-left app_pr">'+
//						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'">'+
//					'</div>'+
////		产品信息
//					'<div class="mui-media-body commodity">'+
//						'<div class="mui-input-row">'+
//							'<label>品牌名称：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.brandName +'" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>供应商：</label>'+
//							'<input type="text" class="font-color mui-input-clear" value="'+ item.skuInfo.productInfo.vendorName +'" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>商品名称：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.name +'" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>商品编码：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.productInfo.prodCode +'" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>商品货号：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ item.skuInfo.itemNo +'" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>单价：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ item.unitPrice +'" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>申报数量：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ item.reqQty +'" reqQty disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>仓库名称：</label>'+
//							'<input type="text" class="mui-input-clear" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>库存数量：</label>'+
//							'<input type="text" class="mui-input-clear" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>总库存数量：</label>'+
//							'<input type="text" class="mui-input-clear" disabled>'+
//						'</div>'+
//						'<div class="mui-input-row">'+
//							'<label>已收货数量：</label>'+
//							'<input type="text" class="mui-input-clear" value="'+ item.recvQty +'" disabled>'+
//						'</div>'+
//					'</div>'+
//				'</li>'
//			});
//			$("#commodityMenu").html(htmlCommodity)
//		},
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

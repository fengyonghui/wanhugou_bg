(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.skuInFoIds="";
		this.skuInfoIds="";
		this.skuInfoIds2="";
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
//			$('#commodityMenu').hide();
           
		},
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizOrderDetail/form4Mobile",
                data: {'orderHeader.id':_this.userInfo.orderId,
                	'orderHeader.oneOrder':_this.userInfo.oneOrderId,orderType:_this.userInfo.orderType},
                dataType: "json",
                success: function(res){
					console.log(res);
					$('#saveDetailBtn').attr('orderid',res.data.bizOrderDetail.orderHeader.id);					
					$('#saveDetailBtn').attr('client',res.data.bizOrderDetail.orderHeader.clientModify);
					$('#saveDetailBtn').attr('consultant',res.data.bizOrderDetail.orderHeader.consultantId);
					$('#saveDetailBtn').attr('source',res.data.bizOrderDetail.orderHeader.source);
					$('#saveDetailBtn').attr('statu',res.data.bizOrderDetail.orderHeader.statu);
					_this.saveBtn(res.data.bizOrderDetail.orderHeader.id,res.data.bizOrderDetail.orderHeader.clientModify,res.data.bizOrderDetail.orderHeader.consultantId);//保存
                }
            });
            _this.searchSkuHtml();
            _this.removeItem();
		},
		searchSkuHtml: function() {
            var _this = this;
            mui('#ordAmendPoLastDaDiv').on('tap','#ordComChoiceBtn',function(){
                var itemNo = $("#ordAmendPoLastDa").val();//商品货号
                if(itemNo==""){
                	mui.toast('请输入商品货号！');
                	return;
                };
                $.ajax({
                    type: "post",
                    url: "/a/biz/shelf/bizOpShelfSku/findOpShelfSku4Mobile",
                    data: {
                    	'skuInfo.itemNo': itemNo,
                	},
                    success: function (result) {
//                  	console.log(result)
//                      $("#searchInfo").empty();
                        var data = JSON.parse(result).data;
//                      console.log(data)
//                      if($.isEmptyObject(data)){
//                      	mui.toast("暂无商品！");
//	                    }else {
	                        $.each(data,function (keys,skuInfoList) {
//	                        	console.log(skuInfoList)
	                        	if(skuInfoList==""){
	                        		mui.toast("暂无商品！");
	                        	}
	                            var prodKeys= keys.split(",");
	                            var prodId= prodKeys[0];
	                            var prodUrl= prodKeys[2];
	                            var  brandName=prodKeys[6];
	                            var resultListHtml="";
	                            var t=0;
	                            $.each(skuInfoList,function (index,skuInfo) {
//                          		console.log(skuInfo);
//                                 $('#saveDetailBtn').attr('saleqty',skuInfo.id);
//                                 $('#saveDetailBtn').attr('detaId',skuInfo.skuInfo.id);
//	                                //skuInfoId+=","+skuInfo.id;
                                if($("#searchInfo").children("#serskudiv_"+skuInfo.id).length>0){
                                	mui.toast('已有此类商品！请选择其他商品！');
                                    return;
                                }
			                    //商品属性
								var ListHtml="";
								$.each(skuInfo.skuValueList, function(i, item) {
								 	ListHtml+=item.value;                                                              
								});								
	                            resultListHtml += '<div class="mui-row app_bline" id="serskudiv_' + skuInfo.id + '">'+ 
	                                '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">采购中心:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  + skuInfo.centerOffice.name + '" disabled></div></li></div>'+ 
	                             
									'<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品名称:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.skuInfo.name  +'" disabled></div></li></div>'+ 
			                   
				                  	'<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品货号:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.skuInfo.itemNo+ '" disabled></div></li></div>'+ 
				                    
				                    '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品编码:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.skuInfo.partNo + '" disabled></div></li></div>'+ 
				                    
				                    '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品属性:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="' + ListHtml + '" disabled></div></li></div>'+ 
									
				                    '<div class="mui-row">'+ 
				                        '<div class="mui-col-sm-6 mui-col-xs-6">'+ 
				                            '<li class="mui-table-view-cell app_bline3">'+ 
				                                '<div class="mui-input-row ">'+ 
				                                    '<label>货架名称:</label>'+ 
				                                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.opShelfInfo.name+ '" disabled></div></li></div>'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label>数量区间:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +(skuInfo.minQty+ '-'+skuInfo.maxQty) + '" disabled></div></li></div></div>'+
			                   
				                    '<div class="mui-row  inAddFont">'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row">'+ 
				                    '<label>现价:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.salePrice+  '" disabled></div></li></div>'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+ 
				                        '<li class="mui-table-view-cell app_bline3">'+ 
				                            '<div class="mui-input-row">'+ 
				                                '<label>采购数量:</label>'+ 
				                                    '<input type="hidden" class="mui-input-clear" value="'  +skuInfo.id + '">'+ 
				                                    '<input type="text" class="mui-input-clear" placeholder="请输入数量" id="reqQty_' +skuInfo.id+ '">'+ 
				                                    '<font>*</font>'+
				                    '</div></li></div>'+
				                    
				                       '</div>'+
				                       '<button id="'+skuInfo.id+'" detaid="'+skuInfo.skuInfo.id+'" type="submit" class="addSkuButton inAddBtn skuinfo_check app_btn_search mui-btn-blue mui-btn-block">添加</button>'+ 
				                    '</div>'
	                           });
	                            $("#searchInfo").append(resultListHtml);
	                            //判断是否有相同的商品 
	                            var dis=$("#searchInfo .addSkuButton");//可选商品
	                            var dos=$("#commodityMenu .addSkuButton ");//订单商品
	                            $.each(dis,function(n,v){
	                            	var s=$(this).attr('id')
	                            	$.each(dos,function(n,v){
	                            		var that=this;	                            	
		                            	var y=$(that).attr('id');
		                            	var divs=$("#searchInfo #serskudiv_"+s);
		                            	if (s==y) {
		                            		mui.toast('已有此类商品！请选择其他商品！！');
		                            		divs.html('');
		                            	}
		                            })
	                            })
	                        })
                    }   
                })
            });
            _this.addSku();
        },
        //查询之后添加
        addSku:function () {
//      	   var ss=$('#searchInfo .app_bline button');
//          	console.log(ss)
            var _this = this;
            mui('#searchInfo').on('tap','.addSkuButton',function(){        	

      		var reqQtyId = $(this).attr('id');
            		console.log(reqQtyId)
            		$('#skuInfoid').val(reqQtyId);           		            		           		
            		var reqQtyIds = $(this).attr("detaid");
            		console.log(reqQtyIds)
            		$('#detaId').val(reqQtyIds);
	            	var removeButtonHtml = '<button id="'+reqQtyId+'" type="submit" class="removeButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">移除</button>'; 

	                $(this).parent().append(removeButtonHtml);
	            	$('#commodityMenu').append($(this).parent());
	            	$(this).hide();  
	            	_this.skuInFoIds +=reqQtyId+",";
	            	console.log(_this.skuInFoIds);
	            	_this.skuInfoIds2 += reqQtyIds+",";
	            	console.log(_this.skuInfoIds2);
	            	_this.skuInfoIds += reqQtyId+",";
	            	console.log(_this.skuInfoIds);
        		
            });
        },
        //订单商品移除按钮操作
        removeItem:function () {
            var _this = this;
            mui('#commodityMenu').on('tap','.removeButton',function(e){
            	var reqQtyId = $(this).attr('id');
            	var addBtnId=$('#skuInfoid').val();
            	var addBtndetaId=$('#detaId').val();           	
            	console.log(addBtndetaId)
            	console.log(_this.skuInFoIds)
            	console.log(_this.skuInfoIds2)
            	_this.skuInFoIds =  _this.skuInFoIds.replace(reqQtyId, "");
            	_this.skuInfoIds2 = _this.skuInfoIds2.replace(addBtndetaId, "");
            	_this.skuInfoIds =  _this.skuInFoIds.replace(reqQtyId, "");
                console.log(_this.skuInFoIds)
            	console.log(_this.skuInfoIds2)
            	$(this).hide();
            	var addButtonHtml =  '<button id="'+ addBtnId +'" detaId="'+addBtndetaId+'" type="submit" class="addSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">添加</button>';
            	$(this).parent().append(addButtonHtml)
            	$('#searchInfo').append($(this).parent());
            	
            });
        },
        /*保存*/
        saveBtn:function(orid,clientModify,consultantId){
			var _this = this;
			var saveData={};
			var reqQtysTemp = "";
            $('.inSaveBtn').on('tap','#saveDetailBtn', function() {
            	var statu=$(this).attr('statu');
            	var source=$(this).attr('source');
            	console.log(_this.skuInFoIds)
            	  console.log(_this.skuInfoIds)
            	   console.log(_this.skuInfoIds2) 
            	var skuIds = _this.skuInFoIds.substring(0,_this.skuInFoIds.length-1).split(",");
            	var skuIds1 = _this.skuInfoIds.substring(0,_this.skuInFoIds.length-1);
            	var skuIds2 = _this.skuInfoIds2.substring(0,_this.skuInFoIds.length-1);  
            	 console.log(skuIds)
            	  console.log(skuIds1)
            	   console.log(skuIds2)
                for (var j=0; j<skuIds.length; j++) {
                    var cheIds = skuIds[j];
                    console.log(cheIds)
                    //  $("#commodityMenu #reqQty_"+cheIds).val();
                    var reqQty = $("#reqQty_"+cheIds).val();
                    console.log(reqQty)
                    if (reqQty == null || reqQty == "") {
                  	    mui.toast("请输入采购数量！！")
                        return;
                    } 
                    reqQtysTemp += "," + reqQty;
                } 
                var reqQtys= reqQtysTemp.substring(1);
            	saveData={
					id:"",
					'orderHeader.id': orid,
					'orderHeader.clientModify':clientModify,
					'orderHeader.consultantId':consultantId,
					detailFlag:"",
					saleQtys:reqQtys,//采购数量
					orderDetaIds:skuIds2,//上面查询skuInfo.skuInfo.id
					shelfSkus:skuIds1,//按钮上的id			
				}; 
				console.log(saveData)
//            	$.ajax({
//	                type: "post",
//	                url: "/a/biz/order/bizOrderDetail/save4Mobile",
//	                data: saveData,		                
//	                dataType: "json",
//	                success: function(rest){
//	                	console.log(rest);
//	                	if(rest.ret==true){		                		
//	                		mui.toast('保存成功！');
//	                		window.setTimeout(function(){
//			                	GHUTILS.OPENPAGE({
//									url:"../../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
//										extras: {
//											staOrdId:orid,
//											source:source,
//											statu:statu
//										}
//								})
//		                	},500)
//	                	}
//	                }	
//				})

			})
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

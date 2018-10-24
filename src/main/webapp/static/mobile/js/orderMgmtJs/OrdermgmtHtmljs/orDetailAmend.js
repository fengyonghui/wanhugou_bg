(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.OrdviewFlag = false;
		this.OrdFlag = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData();//获取数据
			this.getPermissionList2('biz:order:bizOrderDetail:view','OrdviewFlag');//true订单信息操作中的删除
			this.getPermissionList2('biz:order:bizOrderDetail:edit','OrdFlag');//保存
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
                url: "/a/biz/order/bizOrderDetail/form4Mobile",
                data: {id:_this.userInfo.amendId,orderId:_this.userInfo.orderId,
                	'orderHeader.oneOrder':_this.userInfo.oneOrderId,orderType:_this.userInfo.orderType},
                dataType: "json",
                success: function(res){
					console.log(res)	
	                $('.buttonCont .addSkuButton').attr('id',res.data.detail.shelfInfo.id);
	                if(res.data.detail.shelfInfo){
	                	$('#ordCenter').val(res.data.detail.shelfInfo.centerOffice.name)//采购中心
	                }else{
	                	$('#ordCenter').val()//采购中心
	                }
	                if(res.data.detail.shelfInfo){
	                	$('#brandName').val(res.data.detail.shelfInfo.opShelfInfo.name)//货架名称
	                }else{
	                	$('#brandName').val()//货架名称
	                }					
					$('#skuInfoName').val(res.data.detail.skuName)//商品名称
					$('#itemNo').val(res.data.detail.skuInfo.itemNo)//商品货号
					$('#partNo').val(res.data.detail.partNo)//商品编码
					//商品属性
					var ListHtml="";
					$.each(res.data.detail.attributeValueV2List, function(i, item) {
					 	ListHtml+=item.attributeInfo.name + ':'+ item.value;                                                              
					});
					$('#comProperty').val(ListHtml);  
					if(res.data.shelfSku){
						$('#numberInterval').val(res.data.shelfSku.minQty+ '-'+ res.data.shelfSku.maxQty)//数量区间
					    $('#buyPrice').val(res.data.shelfSku.salePrice)//现价
					}else{
						$('#numberInterval').val()//数量区间
					    $('#buyPrice').val()//现价
					}					
					$('#reqQty').val(res.data.detail.ordQty)//采购数量

                    $('#saveDetailBtn').attr('orderid',res.data.bizOrderDetail.orderHeader.id);					
					$('#saveDetailBtn').attr('client',res.data.bizOrderDetail.orderHeader.clientModify);
					$('#saveDetailBtn').attr('consultant',res.data.bizOrderDetail.orderHeader.consultantId);
					$('#saveDetailBtn').attr('source',res.data.bizOrderDetail.orderHeader.source);
					$('#saveDetailBtn').attr('statu',res.data.bizOrderDetail.orderHeader.statu);
                    _this.deleteItem(res.data.detail); 
                    if(this.OrdFlag = true){
                    	_this.save(res.data.bizOrderDetail.id,res.data.bizOrderDetail.orderHeader.id,
                    	res.data.bizOrderDetail.orderHeader.clientModify,
                    	res.data.bizOrderDetail.orderHeader.consultantId);//保存
                    }
                    var orderTypes="";
		            var purchaserId="";
                    if(res.data.bizOrderDetail.orderType==1){
		            	var url= "/a/biz/shelf/bizOpShelfSku/findOpShelfSku4Mobile",
		            	orderTypes=res.data.bizOrderDetail.orderType;
		            	_this.searchSkuHtml(url,purchaserId,orderTypes);
		            }
		            if(res.data.bizOrderDetail.orderType==5){
		            	var url= "/a/biz/sku/bizSkuInfo/findPurseSkuList4Mobile",
		            	purchaserId=res.data.customer.id;
		            	orderTypes=res.data.bizOrderDetail.orderType;
		            	_this.searchSkuHtml(url,purchaserId,orderTypes);
		            }
                }
           });
            _this.removeItem();           
		},
		getPermissionList2: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
//              	console.log(res.data)
                    _this.OrdviewFlag = res.data;
                }
            });
       },
		searchSkuHtml: function(url,purchaserId,orderTypes) {
            var _this = this;
            mui('#ordAmendPoLastDaDiv').on('tap','#ordComChoiceBtn',function(){
                var itemNo = $("#ordSearch").val();
                if(itemNo==""){
                	mui.toast('请输入商品货号！');
                	return;
                }
                var datas={};
                if(orderTypes==1){
                	datas={
                		'skuInfo.itemNo': itemNo,
                	}
                }
                if(orderTypes==5){
                	datas={
                		itemNo: itemNo,
                		'purchaser.id':purchaserId
                	}
                }
                $.ajax({
                    type: "post",
                    url: url,
                    data:datas,
                    success: function (result) {
                        var data = JSON.parse(result).data;
	                        $.each(data,function (keys,skuInfoList) {
	                        	if(skuInfoList==""){
	                        		mui.toast("暂无商品！");
	                        	}
	                            var resultListHtml="";
	                            var t=0;
	                            $.each(skuInfoList,function (index,skuInfo) {
                                //商品属性
								var ListHtml="";
								$.each(skuInfo.skuValueList, function(i, item) {
								 	ListHtml+=item.value;                                                              
								});
	                             resultListHtml += '<div class="mui-row app_bline"  id="serskudiv_' + skuInfo.id + '">'+ 
	                                '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">采购中心:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.centerOffice.name + '" disabled></div></li></div>'+ 
	                             
									'<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品名称:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.skuInfo.name  +'" disabled></div></li></div>'+ 
			                   
				                  	'<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品货号:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfo.skuInfo.itemNo + '" disabled></div></li></div>'+ 
				                    
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
				                    '<input type="text" class="mui-input-clear" max="' +skuInfo.maxQty+ '" id="maxQty_' +skuInfo.id+ '" value="'  +(skuInfo.minQty+ '-'+skuInfo.maxQty) + '" disabled></div></li></div></div>'+
			                   
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
				                                    '<input type="text" class="mui-input-clear" placeholder="请输入数量" id="saleQty_' +skuInfo.id+ '">'+ 
				                                    '<font>*</font>'+
				                    '</div></li></div>'+
				                     '<input type="hidden" class="mui-input-clear" id="orderDetaIds_' +skuInfo.id+ '" value="'  +skuInfo.skuInfo.id+  '" disabled>'+ 
				                    '<input type="hidden" class="mui-input-clear" id="shelfSkuId_' +skuInfo.id+ '" value="'  +skuInfo.id+  '" disabled>'+
				                       '</div>'+
				                       '<button id="'+skuInfo.id+'"  detaId="'+skuInfo.skuInfo.id+'" type="submit" class="addSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">添加</button>'+ 
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
            var _this = this;
            mui('#searchInfo').on('tap','.addSkuButton',function(){        	
             	var reqQtyId = $(this).attr('id');
	      		var saleQty= $("#saleQty_"+reqQtyId).val();
	      		if(saleQty==''){
	      			mui.toast("请输入数量");
	                 return;
	      		}
	      		var maxQty=$("#maxQty_"+reqQtyId).attr('max');
	            if(parseInt(saleQty)>parseInt(maxQty)){
	            	mui.toast("购买的数量与当前价格不符");
	                 return;
	            }
        		$('#skuInfoid').val(reqQtyId);           		            		           		
        		var reqQtyIds = $(this).attr("detaid");
        		$('#detaId').val(reqQtyIds);
            	var removeButtonHtml = '<button id="'+reqQtyId+'" type="submit" class="removeButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">移除</button>'; 
                $(this).parent().append(removeButtonHtml);
            	$('#commodityMenu').append($(this).parent());
            	$("#commodityMenu").find($("#saleQty_"+reqQtyId)).attr('readonly','readonly');
            	$(this).hide();        		
            });
        },
        //订单商品移除按钮操作
        removeItem:function () {
            var _this = this;
            mui('#commodityMenu').on('tap','.removeButton',function(e){
            	var reqQtyId = $(this).attr('id');
            	var addBtnId=$('#skuInfoid').val();
            	var addBtndetaId=$('#detaId').val();           	
            	$(this).hide();
            	var addButtonHtml =  '<button id="'+ addBtnId +'" detaId="'+addBtndetaId+'" type="submit" class="addSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">添加</button>';
            	$(this).parent().append(addButtonHtml)
            	$('#searchInfo').append($(this).parent()); 
            	$("#searchInfo").find($("#saleQty_"+reqQtyId)).removeAttr('readonly');
            });
        },
        //订单商品删除按钮操作
        deleteItem:function (data) {
//      	console.log(data.orderHeader);
        	var idd=data.orderHeader.id;//订单id
			var statu=data.orderHeader.statu;
			var source=data.orderHeader.source;
            var _this = this;
            mui('.buttonCont').on('tap','.inAddBtn',function(e){
            	if(_this.OrdviewFlag==true){
            		var btnArray = ['取消', '确定'];
					mui.confirm('确认要删除该商品吗？', '系统提示！',btnArray, function(e) {
						if(e.index == 1) {
	                        $.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderDetail/Detaildelete4Mobile",
				                data: {
				                	id:data.id,
				                	sign:'1',
				                	orderDetailDetele:'details'
				                },
				                dataType: "json",
				                success: function(res){
				                	if(res.data.aa=="ok"){
				                		mui.toast('删除商品成功！')
				                		window.setTimeout(function(){
				                			GHUTILS.OPENPAGE({
											url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
											extras: {
												staOrdId:idd,
												statu:statu,
												source:source,
											}
										})
				                		},500);
				                	}
								}
							})
						} else {						
						}
				    })
                }            	    
            });
        },
        //保存按钮操作
        save:function(id,orid,clientModify,consultantId){
        	var _this = this;
        	var saveData={};
			var reqQtysTemp = "";
        	mui('.inSaveBtn').on('tap','#saveDetailBtn',function(e){
            	var statu=$(this).attr('statu');
            	var source=$(this).attr('source');
            	var DetaIdsList=$('#commodityMenu input[id^=orderDetaIds_]');
            	var skuIds2="";
            	$.each(DetaIdsList,function(m,n){
            		skuIds2+=$(this).val()+',';
//          	    console.log(skuIds2)
            	})
            	var orderDetaId = skuIds2.substring(0,skuIds2.length-1); 
//          	console.log(orderDetaId);
            	//数量saleQtys
            	var saleQtysList=$('#commodityMenu input[id^=saleQty_]');
            	var reqQty="";
            	$.each(saleQtysList,function(m,n){
            		reqQty+=$(this).val()+',';
            		var ss=$(this).attr('id').substring(8);
//          		console.log(ss)
            		var ReqQty= $("#commodityMenu #saleQty_"+ss).val();
//          		 console.log(ReqQty)
            		if (ReqQty == null || ReqQty == "") {
                  	    mui.toast("请输入采购数量！！")
                        return;
                    }            	   
            	})
            	var reqQtys = reqQty.substring(0,reqQty.length-1); 
//          	console.log(reqQtys)
            	
            	var shelfSkusList=$('#commodityMenu input[id^=shelfSkuId_]');
            	var shelfSku="";
            	$.each(shelfSkusList,function(m,n){
            		shelfSku+=$(this).val()+',';
//          	    console.log(shelfSku)
            	})
            	var shelfSkus = shelfSku.substring(0,shelfSku.length-1); 
//          	console.log(shelfSkus)
                saveData={
					id:id,
					'orderHeader.id': orid,
					'orderHeader.clientModify':clientModify,
					'orderHeader.consultantId':consultantId,
					detailFlag:"",
					saleQtys:reqQtys,//采购数量
					orderDetaIds:skuIds2,//上面查询skuInfo.skuInfo.id
					shelfSkus:shelfSkus,//按钮上的id			
				}; 
	            $.ajax({
	                type: "post",
	                url: "/a/biz/order/bizOrderDetail/save4Mobile",
	                data: saveData,		                
	                dataType: "json",
	                success: function(rest){
//	                	console.log(rest);
	                	if(rest.ret==true){		                		
	                		mui.toast('保存成功！');
	                		window.setTimeout(function(){
			                	GHUTILS.OPENPAGE({
									url:"../../../html/orderMgmtHtml/OrdermgmtHtml/orderAmend.html",
									extras: {
										staOrdId:orid,
										source:source,
										statu:statu
									}
								})
		                	},500)
	                	}
	                }	
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

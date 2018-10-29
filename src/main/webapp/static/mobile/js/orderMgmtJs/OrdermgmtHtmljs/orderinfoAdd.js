(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.OrdFlag = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData();//获取数据	
			this.getPermissionList('biz:order:bizOrderDetail:edit','OrdFlag');//true 订单信息操作
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;         
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
                    _this.OrdFlag = res.data;
                }
            });
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
					$('#saveDetailBtn').attr('orderid',res.data.bizOrderDetail.orderHeader.id);					
					$('#saveDetailBtn').attr('client',res.data.bizOrderDetail.orderHeader.clientModify);
					$('#saveDetailBtn').attr('consultant',res.data.bizOrderDetail.orderHeader.consultantId);
					$('#saveDetailBtn').attr('source',res.data.bizOrderDetail.orderHeader.source);
					$('#saveDetailBtn').attr('statu',res.data.bizOrderDetail.orderHeader.statu);
					if(_this.OrdFlag = true){
						_this.saveBtn(res.data.bizOrderDetail.orderHeader.id,res.data.bizOrderDetail.orderHeader.clientModify,res.data.bizOrderDetail.orderHeader.consultantId);//保存
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
		searchSkuHtml: function(url,purchaserId,orderTypes) {
            var _this = this;
            mui('#ordAmendPoLastDaDiv').on('tap','#ordComChoiceBtn',function(){
                var itemNo = $("#ordAmendPoLastDa").val();//商品货号
                if(itemNo==""){
                	mui.toast('请输入商品货号！');
                	return;
                };
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
                        $("#searchInfo").empty();
                        $.each(data,function (keys,skuInfoList) {
                        	if(skuInfoList==""){
                        		mui.toast("暂无商品！");
                        	}
                            var resultListHtml="";
                            $.each(skuInfoList,function (index,skuInfo) {
                            	console.log(skuInfo)
                                if($("#searchInfo").children("#serskudiv_"+skuInfo.id).length>0){
                                	mui.toast('已有此类商品！请选择其他商品！');
                                    return;
                                }
			                    //商品属性
								var ListHtml="";
								if(skuInfo.skuValueList){
									$.each(skuInfo.skuValueList, function(i, item) {
									 	ListHtml+=item.value+',';                                                              
									});	
								}
								if(skuInfo.attrValueList){
									$.each(skuInfo.attrValueList, function(i, item) {
									 	ListHtml+=item.attributeInfo.name+':'+item.value+',';                                                              
									});	
								}
								//采购中心
                                var centerOfficeName="";
                                if(skuInfo.centerOffice){
                                	centerOfficeName=skuInfo.centerOffice.name;
                                }
                                var skuInfoName="";//商品名称
                                var skuInfoitemNo="";//商品货号
                                var skuInfopartNo="";//商品编码
                                var orderDetaIdsVal="";
                                if(skuInfo.skuInfo){
                                	skuInfoName=skuInfo.skuInfo.name;
                                	skuInfoitemNo=skuInfo.skuInfo.itemNo;
                                	skuInfopartNo=skuInfo.skuInfo.partNo;
                                	orderDetaIdsVal=skuInfo.skuInfo.id;
                                }else{
                                	skuInfoName=skuInfo.name;
                                	skuInfoitemNo=skuInfo.itemNo;
                                	skuInfopartNo=skuInfo.partNo;
                                	orderDetaIdsVal=skuInfo.id;
                                }
                                //货架名称
                                var opShelfInfoName="";
                                if(skuInfo.opShelfInfo){
                                	opShelfInfoName=skuInfo.opShelfInfo.name;
                                }else{
                                	opShelfInfoName="";
                                }
                                //现价
                                var salePrice="";
                                if(skuInfo.salePrice){
                                	salePrice=skuInfo.salePrice;
                                }else{
                                	salePrice="";
                                }
                                //数量区间
                                var minQty="";
                                var maxQty="";
                                if(skuInfo.minQty){
                                	minQty=skuInfo.minQty+'-';
                                }else{
                                	minQty="";
                                }
                                if(skuInfo.maxQty){
                                	maxQty=skuInfo.maxQty;
                                }else{
                                	maxQty="";
                                }
	                            resultListHtml += '<div class="mui-row app_bline" id="serskudiv_' + skuInfo.id + '">'+ 
	                            
	                                '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">采购中心:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  + centerOfficeName + '" disabled></div></li></div>'+ 
	                             
									'<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品名称:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfoName  +'" disabled></div></li></div>'+ 
			                   
				                  	'<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品货号:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfoitemNo+ '" disabled></div></li></div>'+ 
				                    
				                    '<div class="mui-row lineStyle">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label class="">商品编码:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +skuInfopartNo + '" disabled></div></li></div>'+ 
				                    
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
				                                    '<input type="text" class="mui-input-clear" id="" value="'  +opShelfInfoName+ '" disabled></div></li></div>'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row ">'+ 
				                    '<label>数量区间:</label>'+ 
				                    '<input type="text" class="mui-input-clear" max="' +maxQty+ '" id="maxQty_' +skuInfo.id+ '" value="' +(minQty+maxQty) + '" disabled></div></li></div></div>'+
			                   
				                    '<div class="mui-row  inAddFont">'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+ 
				                    '<li class="mui-table-view-cell app_bline3">'+ 
				                    '<div class="mui-input-row">'+ 
				                    '<label>现价:</label>'+ 
				                    '<input type="text" class="mui-input-clear" id="" value="'  +salePrice+  '" disabled></div></li></div>'+ 
				                    '<div class="mui-col-sm-6 mui-col-xs-6">'+ 
				                        '<li class="mui-table-view-cell app_bline3">'+ 
				                            '<div class="mui-input-row">'+ 
				                                '<label>采购数量:</label>'+ 
				                                    '<input type="hidden" class="mui-input-clear" value="'  +skuInfo.id + '">'+ 
			                                    '<input type="text" class="mui-input-clear" placeholder="请输入数量" id="saleQty_' +skuInfo.id+ '">'+ 
				                                    '<font>*</font>'+
				                    '</div></li></div>'+
				                    '<input type="hidden" class="mui-input-clear" id="orderDetaIds_' +skuInfo.id+ '" value="'  +orderDetaIdsVal+  '" disabled>'+ 
				                    '<input type="hidden" class="mui-input-clear" id="shelfSkuId_' +skuInfo.id+ '" value="'  +skuInfo.id+  '" disabled>'+
				                       '</div>'+
				                       
				                    '<button id="' +skuInfo.id+ '" detaid="'+orderDetaIdsVal+'" type="submit" class="addSkuButton inAddBtn skuinfo_check app_btn_search mui-btn-blue mui-btn-block">添加</button>'+ 
				                       
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
	                            		mui.toast('已有此类商品！')
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
	      			mui.toast("请输入采购数量");
	                return;
	      		}
	      		if(saleQty<=0){
	      			mui.toast("请输入一个最小为 1 的值");
	                return;
	      		}
	      		var maxQty=$("#maxQty_"+reqQtyId).attr('max');
	            if(parseInt(saleQty)>parseInt(maxQty)){
	            	mui.toast("购买的数量与当前销售数量区间不符");
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
            	$(this).parent().append(addButtonHtml);
            	$('#searchInfo').append($(this).parent()); 
            	$("#searchInfo").find($("#saleQty_"+reqQtyId)).removeAttr('readonly');
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
            	var DetaIdsList=$('#commodityMenu input[id^=orderDetaIds_]');
            	var skuIds2="";
            	$.each(DetaIdsList,function(m,n){
            		skuIds2+=$(this).val()+',';
            	})
            	var orderDetaId = skuIds2.substring(0,skuIds2.length-1); 
            	//数量saleQtys
            	var saleQtysList=$('#commodityMenu input[id^=saleQty_]');
            	var reqQty="";
            	$.each(saleQtysList,function(m,n){
            		reqQty+=$(this).val()+',';
            		var ss=$(this).attr('id').substring(8);
            		var ReqQty= $("#commodityMenu #saleQty_"+ss).val();
            		if (ReqQty == null || ReqQty == "") {
                  	    mui.toast("请输入采购数量！！")
                        return;
                    }            	   
            	})
            	var reqQtys = reqQty.substring(0,reqQty.length-1); 
            	
            	var shelfSkusList=$('#commodityMenu input[id^=shelfSkuId_]');
            	var shelfSku="";
            	$.each(shelfSkusList,function(m,n){
            		shelfSku+=$(this).val()+',';
            	})
            	var shelfSkus = shelfSku.substring(0,shelfSku.length-1); 

            	saveData={
					id:"",
					'orderHeader.id': orid,
					'orderHeader.clientModify':clientModify,
					'orderHeader.consultantId':consultantId,
					detailFlag:"",
					saleQtys:reqQtys,//采购数量
					orderDetaIds:orderDetaId,//上面查询skuInfo.skuInfo.id
					shelfSkus:shelfSkus,//按钮上的id			
				}; 
              	$.ajax({
	                type: "post",
	                url: "/a/biz/order/bizOrderDetail/save4Mobile",
	                data: saveData,		                
	                dataType: "json",
	                success: function(rest){
	                	console.log(rest);
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

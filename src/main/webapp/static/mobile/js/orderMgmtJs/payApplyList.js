
(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.PaymentFlag = "false"
//		this.cancelAmendPayFlag = "false"
//		this.cancelFlag = "false"
		this.editFlag = "false"
		this.checkFlag = "false"
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.getPermissionList('biz:po:sure:bizPoPaymentOrder','PaymentFlag')
			this.getPermissionList2('biz:po:bizpopaymentorder:bizPoPaymentOrder:edit','editFlag')
			this.getPermissionList1('biz:po:bizpopaymentorder:bizPoPaymentOrder:audit','checkFlag')
//			if(this.userInfo.isFunc){
//				this.seachFunc()
//			}else{
				
				this.pageInit(); //页面初始化
                this.removeBtn();
//			}
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit:function(){			
			var _this = this;			
			var pager = {};//分页 
		    var totalPage;//总页码
		    pullRefresh(pager);//启用上拉下拉 
		    function pullRefresh(){
		        mui("#refreshContainer").pullRefresh({
			        up:{
			            contentnomore:'没 有 更 多 数 据 了',
			            callback:function(){
			                window.setTimeout(function(){
			                    getData(pager);
			                },500);
			            }
			         },
			        down : {
			            height:50,
			            auto: true,
			            contentdown : "",
			            contentover : "",
			            contentrefresh : "正在加载...",
			            callback :function(){ 
			            	pager['size']= 20;
		                    pager['pageNo'] = 1;
			                    pager['poId'] = _this.userInfo.staOrdId;
			                    pager['type'] = 1;
			                    pager['fromPage'] = 'requestHeader';
			                    pager['orderId'] = _this.userInfo.orderId;
				                var f = document.getElementById("payApplyList");
				                var childs = f.childNodes;
				                for(var i = childs.length - 1; i >= 0; i--) {
				                    f.removeChild(childs[i]);
				                }
				                $('.mui-pull-caption-down').html('');				                
				                getData(pager);
			            }
			        }
			    })
		    }
		    function getData(params){
		    	var inPayHtmlList = '';
		    	var arrImg=[];
		        mui.ajax("/a/biz/po/bizPoPaymentOrder/listData4MobileNew",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		          	    console.log(res)
			            var returnData = res.data.page.list;
			            var dataRow = res.data.roleSet;
						var arrLen = res.data.page.list.length; 
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						}
                        if(arrLen > 0) {
							$.each(returnData, function(i, item) {
								console.log(item)
								//当前状态
								var nowbizStatus="";
								var nowbizStatus="";
								if(item.bizStatus==0){
									nowbizStatus='未支付';
								}else{
									nowbizStatus='已支付';
								}
								//实际付款时间
								var payTime = "";
								if(item.payTime){
									payTime=_this.formatDateTime(item.payTime);
								}else{
									payTime = "";
								}						
								//操作确认支付金额
								var inPay ="";								
								var inPayBtn="";
								if(_this.PaymentFlag ==true){
									if(res.data.fromPage == 'requestHeader' && item.total == '0.00' && (res.data.requestHeader == null || res.data.requestHeader.bizStatus < res.data.CLOSE)){
										inPay = '确认支付金额';
									}
								}
								//操作审核
								var inCheck ="";
								var inChecks ="";
								if(_this.checkFlag ==true){	
									if(item.total != '0.00'){
										console.log(item.commonProcess.paymentOrderProcess.name)
										if(item.id == res.data.bizPoHeader.bizPoPaymentOrder.id && item.commonProcess.paymentOrderProcess.name != '审批完成' && item.total != 0){
											inCheck ="审核通过";
											inChecks ="审核驳回";
										}
									}
								}
								var mt = item;
								inPayHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
									'<div class="mui-input-row">' +
										'<label>ID:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.id+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>付款金额:</label>' +
										'<input type="text" id="totalmoney" class="mui-input-clear" disabled="disabled" value=" '+item.total+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>实际付款金额:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.payTotal+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>最后付款时间:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+_this.formatDateTime(item.deadline)+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>实际付款时间:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ payTime +' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>当前状态:</label>' +
										'<input type="text" id="nowStatus" class="mui-input-clear" disabled="disabled" value=" '+nowbizStatus+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>单次支付审批状态:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.commonProcess.paymentOrderProcess.name+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>备注:</label>' +
										'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.remark+' ">' +
									'</div>' +
									'<div class="mui-input-row">' +
										'<label>支付凭证:</label>' +
										'<div class="imgLists">' +_this.photoShow(mt)+
										'</div>' +
									'</div>' +
									'<div class="app_color40 mui-row app_text_center operation">' +
										'<div class="inPayBtn" inListId="'+ item.id +'">' +
											'<div class="" >'+inPay+'</div>' +
										'</div>' +
									'</div>' +
									'<div class="app_color40 mui-row app_text_center operation">' +
										'<div class="mui-col-xs-6 inCheckBtn" id="inCheckBtn" inListId="'+ item.id +'" curType="'+ item.commonProcess.paymentOrderProcess.code +'" total="'+ item.total +'" orderType="'+ item.orderType +'">' +
											'<div class="">'+inCheck+'</div>' +
										'</div>' +
										'<div class="mui-col-xs-6 inCheckBtns"  id="inCheckBtns" inListId="'+ item.id +'" curType="'+ item.commonProcess.paymentOrderProcess.code +'" total="'+ item.total +'" orderType="'+ item.orderType +'">' +
											'<div class="">'+inChecks+'</div>' +
										'</div>' +
									'</div>' +
								'</div>'

								    
						});	
							$('#payApplyList').append(inPayHtmlList);
							//支付凭证
//							console.log(res.data.page.list)
//							$.each(res.data.page.list,function (i, cards) {
////                               console.log(cards.imgList)
//                               if(cards.imgList){
//									$.each(cards.imgList,function (i, card) {
////									console.log(card)
//		                            $(".imgLists").html("<a href=\"" + card.imgServer + card.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + card.imgServer + card.imgPath + "\"></a>");
//		                           });
//		                        }else{
//		                        	$('.imgLists').parent().hide();
//		                        }
//	                        });
							
							_this.inHrefHtml();
											
						} else {
							$('.mui-pull-caption').html('');
							$('#payApplyList').append('<p class="noneTxt">暂无数据</p>');
						}
		                if(res.data.page.totalPage==pager.pageNo){		                	
			                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
			            }else{
			                pager.pageNo++;
			                mui('#refreshContainer').pullRefresh().refresh(true);
			            }          
			        },
		            error:function(xhr,type,errorThrown){
			            console.log(type);
		            }
		        })
		    } 
		},
		photoShow: function(item) {
			var _this = this;
//			console.log(item)
			var imgs = '';
			 if(item.imgList){
				$.each(item.imgList,function (i, card) {
                imgs = "<a href=\"" + card.imgServer + card.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + card.imgServer + card.imgPath + "\"></a>"
               });
               return imgs;
            }else{
            	$('.imgLists').parent().hide();
            }
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
                    _this.PaymentFlag = res.data;
                    console.log(_this.PaymentFlag)
                }
            });
        },
        getPermissionList1: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.checkFlag = res.data;
                    console.log(_this.checkFlag)
                }
            });
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
                    _this.editFlag = res.data;
                    console.log(_this.editFlag)
                }
            });
        },
        removeBtn:function(){
        	$('#payBtnremove').on('tap',  function() {
        		$('.payMoney').hide();
        	})
        },
        comfirDialig: function() {
			var _this = this;
			document.getElementById("inCheckBtns").addEventListener('tap', function() {
				var inListId = $(this).attr('inlistid');
				console.log(inListId);
				var auditType= $(this).attr('ordertype');
				console.log(auditType);
				var money= $(this).attr('total');
				console.log(money);
				var currentType= $(this).attr('curtype');
				console.log(currentType);
				var btnArray = ['否', '是'];
				mui.confirm('确认驳回审核吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						var btnArray = ['取消', '确定'];
						mui.prompt('请输入驳回理由：', '驳回理由', '', btnArray, function(a) {
							if(a.index == 1) {
								var rejectTxt = a.value;
								if(a.value == '') {
									mui.toast('驳货理由不能为空！')
								} else {
									_this.rejectData(rejectTxt,2,inListId,money,currentType)
								}
							} else {}
						})
					} else {}
				})
			});
			document.getElementById("inCheckBtn").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了                
				var inListId = $(this).attr('inlistid');
				console.log(inListId)
				var auditType= $(this).attr('ordertype');
				console.log(auditType);
				var money= $(this).attr('total');
				console.log(money);
				var currentType= $(this).attr('curtype');
				console.log(currentType);
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入通过理由：', '通过理由', '', btnArray, function(e) {
					if(e.index == 1) {
						var inText = e.value;
						if(e.value == '') {
							mui.toast('通过理由不能为空！')
							return;
						} else {
							var btnArray = ['否', '是'];
							mui.confirm('确认通过审核吗？', '系统提示！', btnArray, function(choice) {
								if(choice.index == 1) {
									_this.ajaxData(inText,1,inListId,money,currentType)
								} else {}
							})
						}
					} else {}
				})
			});
		},
		//审核通过
		ajaxData: function(inText,num,inListId,money,currentType) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/auditPay",
				data: {
					id: inListId,
					currentType: currentType,
					money: money,
					auditType: num,
					description: inText
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
					if(res.ret == true) {
						mui.toast(res.data.right);
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
								url: "../orderMgmtHtml/orderpaymentinfo.html",
								extras: {}
							})
			            },300);						
					}
					if(res.ret == false) {
						mui.toast(res.errmsg)
					}
				},
				error: function(e) {
					//服务器响应失败处理函数
				}
			});

		},
		rejectData: function(rejectTxt,num,inListId,money,currentType) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/auditPay",
				data: {
					id: inListId,
					currentType: currentType,
					money: money,
					auditType: num,
					description: rejectTxt
				},
				dataType: "json",
				success: function(res) {
					if(res.ret == true) {
						mui.toast(res.data.right);
						$('app_color40 .inCheckBtn').find('div').css('display','none');
						$('app_color40 .inCheckBtns').find('div').css('display','none');
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
								url: "../orderMgmtHtml/orderpaymentinfo.html",
								extras: {}
							})
			            },300);
					}
					if(res.ret == false) {
						mui.toast(res.errmsg)
					}
				}
			});
		},
		inHrefHtml: function() {
			var _this = this;
			_this.comfirDialig();
		/*首页*/
//			$('#nav').on('tap','.inHomePage', function() {
//				var url = $(this).attr('url');
//				GHUTILS.OPENPAGE({
//					url: "../../html/backstagemgmt.html",
//					extras: {
//						
//					}
//				})
//			}),
		/*	确认支付金额*/
			$('#payApplyList').on('tap', '.inPayBtn', function() {
				$('.payMoney').show();
				var inListId = $(this).attr('inListId');
				console.log(inListId)
				console.log(_this.userInfo.staOrdId)
				$.ajax({
	                type: "GET",
	                url: "/a/biz/po/bizPoPaymentOrder/form4Mobile",
	                dataType: "json",
	                data: {id:inListId,poHeaderId:_this.userInfo.staOrdId,fromPage:'requestHeader'},
	                async:false,
	                success: function(res){
	                    console.log(res);
	                    var resid=res.data.bizPoPaymentOrder.id;
	                    var poHeaderIds=res.data.bizPoPaymentOrder.poHeaderId;
	                     var orderTypes=res.data.bizPoPaymentOrder.orderType;
	                    $('#totalMoney').val(res.data.totalDetailResult);
	                    $('#paymoney').val(res.data.bizPoPaymentOrder.total);
	                    if(_this.editFlag){
	                    	$('.btnbox').show();
	                    }
	                    $('#payBtnsave').on('tap',function(){
	                    	var totalVal = $("#paymoney").val();
//		                    console.log(totalVal);	
                            if (totalVal=="" || totalVal<= 0) {
                                mui.toast("付款金额输入不正确，请重新输入！");
                                return;
                            }
	                    	$.ajax({
				                type: "get",
				                url: "/a/biz/po/bizPoPaymentOrder/save4Mobile",
				                dataType: "json",
				                data: {id:resid,poHeaderId:poHeaderIds,orderType:orderTypes,total:$('#paymoney').val(),remark:$('#textxt').val()},
				                async:false,
				                success: function(res){				                   
//				                    console.log(res.data.result);					                    
				                    if(res.data.result==true){
				                    	mui.toast('保存成功！！');
				                    	$('.payMoney').hide();
				                    	window.setTimeout(function(){
						                    _this.pageInit();
						                },800);
				                    	
				                    }else{
				                    	mui.toast('保存失败！！');
				                    }
				                }
				            });
	                    })	
	                }
	            });
		})
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
		},
		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

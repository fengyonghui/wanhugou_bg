(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.OrdFlaginfo = "false";
		this.OrdFlagaudit = "false";
		this.OrdFlagpay = "false";
		this.OrdFlagstartAudit = "false";
		this.OrdFlagScheduling = "false";
		this.creatPayFlag = "false";
		this.ordCreatPayFlag = "false";
		this.poCreatPayFlag = "false";
		this.orCancAmenFlag = "false";
		this.affirmSchedulingFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加
//			biz:order:bizOrderHeader:view  		操作
			this.getPermissionList('biz:po:bizpopaymentorder:bizPoPaymentOrder:audit','OrdFlaginfo');//操作总权限
			this.pageInit(); //页面初始化
			
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
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
                	console.log(res.data)
                	_this.OrdFlaginfo = res.data;
                }
            });
        },
	    pageInit: function() {
			var _this = this;
			var pager = {};//参数
		    var totalPage;//总页码
		    pullRefresh(pager);//启用上拉下拉 
		    function pullRefresh(){
		        mui("#refreshContainer").pullRefresh({
			        up:{
			            contentnomore:'没 有 更 多 数 据 了',
			            callback:function(){			            	
			                getData(pager);
			            }
			         },
			        down : {
			            height:50,
			            auto: true,
			            contentdown : "",
			            contentover : "",
			            contentrefresh : "正在加载...",
			            callback :function(){ 
			            	//解码
				            var nameTxt = '';
							if(_this.userInfo.processTypeStr) {
								nameTxt = decodeURIComponent(_this.userInfo.processTypeStr)
							}else {
								nameTxt = ''
							}
							//查询页面传过来的值
							if(_this.userInfo.num==undefined){
								_this.userInfo.num="";
							}
							console.log(_this.userInfo.num)
							if(nameTxt==undefined){
								nameTxt="";
							}
							if(_this.userInfo.includeTestData==undefined){
								_this.userInfo.includeTestData="";
							}
			                var f = document.getElementById("orderinfoList");
			                var childs = f.childNodes;
			                for(var i = childs.length - 1; i >= 0; i--) {
			                    f.removeChild(childs[i]);
			                }
			                $('.mui-pull-caption-down').html('');
			                if(_this.userInfo.isFunc){
			                	//查询过来传的参数
		                    	pager['size']= 20;
		                    	pager['pageNo'] = 1;
		                    	pager['orderNum'] = _this.userInfo.num;//备货单号
		                    	pager['selectAuditStatus'] = nameTxt;//审核状态
		                    	pager['includeTestData'] = _this.userInfo.includeTestData;//测试数据
		                    	getData(pager);
		                    }else{
		                    	//直接进来的参数数据
		                    	pager['size']= 20;
		                        pager['pageNo'] = 1;
		                    	getData(pager);
		                    }

			            }
			        }
			    })
		    }
		    function getData(params){
		    	var orderHtmlList = '';
		    	var ass=[];
		    	var postatus=[];
		        mui.ajax("/a/biz/po/bizPoPaymentOrder/listV2ForMobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){		            	
		            	var dataRow = res.data.roleSet;
		            	/*当前用户信息*/
						var userId = '';
						$.ajax({
			                type: "GET",
			                url: "/a/getUser",
			                dataType: "json",
			                async:false,
			                success: function(user){
								userId = user.data.id
			                }
			            });	
                        //订单支出状态                        
                        $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "biz_po_status"},
			                async:false,
			                success: function(res){
				                postatus=res;
			                }
			            });	
						var arrLen = res.data.page.list.length;
						if(arrLen <20 ){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true)
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true)
						}
                        var that=this;
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
                            	console.log(item);
                            	if(item.orderNum != '' || item.reqNo != ''){
                            		//订单号
	                            	var num="";
	                            	var href="";
	                            	if(item.orderNum!=""){
	                            		num=item.orderNum;
	//                          		href="../../html/inventoryMagmetHtml/inventoryDetails.html.html"
	                            	}
	                            	if(item.reqNo!=""){
	                            		num=item.reqNo;//备货单
	//                          		href=
	                            	}
		                            //当前状态
		                            var postatusTxt = '';
		                            if(item.bizStatus == 0){
		                            	postatusTxt = '未支付';
		                            }else{
		                            	postatusTxt = '已支付';
		                            }
		                            
		                            $.each(postatus,function(i,items){
			                        	if(item.bizStatus==items.value) {
			                        		postatusTxt = items.label
			                        	}
		                            })
		                            //单次支付审批状态
		                            var paycheckTxt = '';
		                            if(item.total == '0.00' && item.commonProcess.paymentOrderProcess.name != '审批完成'){
		                            	paycheckTxt='待确认支付金额';
		                            }
		                            if(item.total != '0.00'){
		                            	if(item.commonProcess.paymentOrderProcess){
		                            		paycheckTxt=item.commonProcess.paymentOrderProcess.name;
		                            	}else{
		                            		paycheckTxt = '';
		                            	}
		                            	
		                            }
		                            //支付凭证
		                            var mt = item;
		                            //实际付款时间
		                            var payTime="";
		                            if(item.payTime){
		                            	payTime= _this.formatDateTime(item.payTime);
		                            }else{
		                            	payTime="";
		                            }
		                            //最后付款时间
		                            var deadline="";
		                            if(item.deadline){
		                            	deadline= _this.formatDateTime(item.deadline);
		                            }else{
		                            	deadline="";
		                            }
		                            //操作权限
		                            var appCheckSucBtn = "";
		                            var CheckSucBtnid = "";
				                	var appCheckSuc = "";
				                	var CheckSucid = "";
						            if(item.poHeader.bizStatus != 10){
						                if(_this.OrdFlaginfo==true){
						                	if(item.total != '0.00'){
						                		if(item.commonProcess.paymentOrderProcess.name != '驳回' &&item.commonProcess.paymentOrderProcess.name != '审批完成' && item.total != 0){
						                			var appCheckSucBtn = "审核通过";
						                			CheckSucBtnid = 'CheckSucBtnid';
				                	                var appCheckSuc = "审核驳回";
				                	                CheckSucid = 'CheckSucid';
						                		}
						                	}
						                }
						            }
						            var Processcode="";
						            if(item.commonProcess.paymentOrderProcess){
						            	Processcode=item.commonProcess.paymentOrderProcess.code;
						            }else{
						            	Processcode="";
						            }
		                        	//详情
		                        	var detail="";
		                        	detail='详情'
									orderHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
											'<div class="mui-input-row">' +
											    '<label>订单号:</label>' +
												'<div class="numLists" style="margin-top:7px;" staordid="'+ item.requestId +'"  orderid="'+ item.orderId +'" ordernum="'+ item.orderNum +'" reqno="'+ item.reqNo +'">' +
														num+
											    '</div>' +
	
											'</div>' +
										
											'<div class="mui-input-row">' +
												'<label>付款金额:</label>' +
												'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.total.toFixed(2) +' ">' +
											'</div>' +
											'<div class="mui-input-row">' +
												'<label>实际付款金额:</label>' +
												'<input type="text" class="mui-input-clear orderTypeTxt" disabled="disabled" value=" '+ item.payTotal.toFixed(2) +' ">' +
											'</div>' +
											'<div class="mui-input-row">' +
												'<label>最后付款时间:</label>' +
												'<input type="text" class="mui-input-clear orderTypeTxt" disabled="disabled" value=" '+ deadline +' ">' +
											'</div>' +
											'<div class="mui-input-row">' +
												'<label>实际付款时间:</label>' +
												'<input type="text" class="mui-input-clear orderTypeTxt" disabled="disabled" value=" '+ payTime +' ">' +
											'</div>' +
											'<div class="mui-input-row">' +
												'<label>当前状态:</label>' +
												'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+postatusTxt +' ">' +
											'</div>' +
											'<div class="mui-input-row">' +
												'<label>审批状态:</label>' +
												'<input type="text" class="mui-input-clear" id="paycheckSta" disabled="disabled" value=" '+ paycheckTxt +' ">' +
											'</div>' +
											'<div class="mui-input-row">' +
												'<label>备注:</label>' +
												'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+  item.remark+' ">' +
											'</div>' +
											'<div class="mui-input-row">' +
												'<label>支付凭证:</label>' +
												'<div class="imgLists">' +_this.photoShow(mt)+
											    '</div>' +
											'</div>' +
											'<div class="app_color40 mui-row app_text_center operation">' +
											'<div class="mui-col-xs-4 CheckSucBtnid" inListId="'+ item.id +'" curType="'+ Processcode +'" total="'+ item.total +'" orderType="'+ item.orderType +'">' +
												'<div class="">'+appCheckSucBtn+'</div>' +
											'</div>' +
											'<div class="mui-col-xs-4 CheckSucid" inListId="'+ item.id +'" curType="'+ Processcode +'" total="'+ item.total +'" orderType="'+ item.orderType +'">' +
												'<div class="">'+appCheckSuc+'</div>' +
											'</div>' +
											'<div class="mui-col-xs-4 orDetailBtn" inListId="'+ item.id +'">' +
												'<div class="">'+detail+'</div>' +
											'</div>' +
										'</div>' +
										'</div>'
                            	}
                            	
							});
							$('#orderinfoList').append(orderHtmlList);
							_this.stOrdHrefHtml();
							
					    }else {
							$('.mui-pull-bottom-pocket').html('');
							$('#orderinfoList').append('<p class="noneTxt">暂无数据</p>');
							$('#staOrdSechBtn').hide();
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);							
						}
		                if(res.data.page.totalPage==pager.pageNo){		                	
			                mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);			                
			            }else{
			                pager.pageNo++;
			                mui('#refreshContainer').pullRefresh().refresh(true);
			            } 			           
			        },
		            error:function(xhr,type,errorThrown){
		            }
		        })
		    }
		    _this.ordHrefHtml();
	    },
	    photoShow: function(item) {
			var _this = this;
			var imgs = '';
			 if(item.imgList){
				$.each(item.imgList,function (i, card) {
                imgs = "<a href=\"" + card.imgServer + card.imgPath + '\" target=\"_blank\"><img width=\"100px\" src=\"' + card.imgServer + card.imgPath + "\"></a>"
               });
               return imgs;
            }else{
            	$('.imgLists').parent().hide();
            }
		},
		comfirDialig: function() {
			var _this = this;
			$('.CheckSucid').on('tap', function(e) {
				var inListId = $(this).attr('inlistid');
//				console.log(inListId);
				var auditType= $(this).attr('ordertype');
//				console.log(auditType);
				var money= $(this).attr('total');
//				console.log(money);
				var currentType= $(this).attr('curtype');
//				console.log(currentType);
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
			$('.CheckSucBtnid').on('tap', function(e) {
				e.detail.gesture.preventDefault(); 
				var inListId = $(this).attr('inlistid');
//				console.log(inListId)
				var auditType= $(this).attr('ordertype');
//				console.log(auditType);
				var money= $(this).attr('total');
//				console.log(money);
				var currentType= $(this).attr('curtype');
//				console.log(currentType);
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
					poPayId: inListId,
					currentType: currentType,
					money: money,
					auditType: num,
					description: inText
				},
				dataType: "json",
				success: function(res) {
					if(res.ret == true) {
						mui.toast(res.data.right);
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
								url: "../../html/orderMgmtHtml/ApplicationList.html",
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
					poPayId: inListId,
					currentType: currentType,
					money: money,
					auditType: num,
					description: rejectTxt
				},
				dataType: "json",
				success: function(res) {
					if(res.ret == true) {
						mui.toast(res.data.right);
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
								url: "../../html/orderMgmtHtml/ApplicationList.html",
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
		ordHrefHtml: function() {
        	var _this = this;
        	/*查询*/
			$('.app_header').on('tap', '#OrdSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/Applydetailseach.html",
						extras:{
						}
					})
				}
			});
		    /*首页*/
			$('#nav').on('tap','.staHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			});
        },
        stOrdHrefHtml: function() {
            var _this = this;
            _this.comfirDialig();
            $('.numLists').on('tap',function(){
               	var url = $(this).attr('url');
                var staordid = $(this).attr('staordid');
                var orderid = $(this).attr('orderid');
                var ordernum = $(this).attr('ordernum');
                var reqno = $(this).attr('reqno');
            	if(ordernum==""&&reqno!=""){
            		GHUTILS.OPENPAGE({
						//备货单						
						url: "../../html/inventoryMagmetHtml/inventoryDetails.html",
						extras: {
							inListId: staordid,
						}
					})
            	}
                if(ordernum!=""&&reqno==""){
            		GHUTILS.OPENPAGE({					
						url: "../../html/orderMgmtHtml/OrdermgmtHtml/ordDetail.html",
						extras: {
							staOrdId: orderid,
							orderDetails:'details',
							statu:'',
							source:'',
						}
					})
            	}
			}),
							    
			/*详情*/
			$('.orDetailBtn').on('tap', function() {
				var url = $(this).attr('url');
				var staOrdId = $(this).attr('inListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(staOrdId == staOrdId) {
					GHUTILS.OPENPAGE({
						url: "../../html/orderMgmtHtml/ApplyListdetail.html",
						extras: {
							staOrdId: staOrdId,
						}
					})
				}
			})						
        },
        
        //时间戳转化方法：
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

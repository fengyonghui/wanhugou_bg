(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.commOrdFlag = "false";
		this.commAuditFlag = "false";
		this.commEditFlag = "false";
		this.commRefundFlag = "false";
		this.commDoRefundFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			//权限添加biz:order:bizOrderHeader:view biz:order:bizOrderHeader:edit
			//
			this.getPermissionList1('biz:order:sure:bizCommission','commOrdFlag')//确认支付金额
			this.getPermissionList3('biz:order:bizCommission:audit','commEditFlag')//审核
			this.getPermissionList2('biz:order:bizCommission:sure:pay','commAuditFlag')//确认付款
			this.pageInit(); //页面初始化
			this.removeBtn();
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
//			console.log(_this.commOrdFlag)
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
			                },100);
			            }
			         },
			        down : {
			            height:50,
			            auto: true,
			            contentdown : "",
			            contentover : "",
			            contentrefresh : "正在加载...",
			            callback :function(){ 
							//查询页面传过来的值
							var nameTxts = '';
							if(_this.userInfo.conName) {
								nameTxts = decodeURIComponent(_this.userInfo.conName)
							}else {
								nameTxts = ''
							}
							if(_this.userInfo.orderNum==undefined){
								_this.userInfo.orderNum="";
							}
							if(_this.userInfo.serllerName==undefined){
								_this.userInfo.serllerName="";
							}
							if(_this.userInfo.serllerPhone==undefined){
								_this.userInfo.serllerPhone="";
							}
							if(_this.userInfo.commissionStatus==undefined){
								_this.userInfo.commissionStatus="";
							}	
							if(_this.userInfo.customerPhone==undefined){
								_this.userInfo.customerPhone="";
							}
							if(_this.userInfo.itemNo==undefined){
								_this.userInfo.itemNo="";
							}
							if(_this.userInfo.customerName==undefined){
								_this.userInfo.customerName="";
							}
							if(_this.userInfo.centersName==undefined){
								_this.userInfo.centersName="";
							}
							if(nameTxts==undefined){
								nameTxts="";
							}
							if(_this.userInfo.includeTestData==undefined){
								_this.userInfo.includeTestData="";
							}
		            	    var statu = _this.userInfo.statu;	
			                var f = document.getElementById("commList");
			                var childs = f.childNodes;
			                for(var i = childs.length - 1; i >= 0; i--) {
			                    f.removeChild(childs[i]);
			                }			                
			                $('.mui-pull-caption-down').html('');
			                if(_this.userInfo.isFunc){
		                	    //查询过来传的参数
		                    	pager['size']= 20;
		                    	pager['pageNo'] = 1;
		                    	pager['targetPage'] = 'COMMISSION_ORDER';
		                    	pager['orderNum'] = _this.userInfo.orderNum;//订单编号
		                    	pager['serllerName'] = _this.userInfo.serllerName;//零售商名称
		                    	pager['serllerPhone'] = _this.userInfo.serllerPhone;//零售商电话
		                    	pager['commissionStatus'] = _this.userInfo.commissionStatus,//结佣状态
		                    	pager['customer.phone'] =  _this.userInfo.customerPhone,//经销商电话
		                    	pager['itemNo'] = _this.userInfo.itemNo,//商品货号
		                    	pager['customer.id'] = _this.userInfo.customerName,//经销店名称
		                    	pager['centersName'] = _this.userInfo.centersName,//采购中心
		                    	pager['con.name'] = nameTxts,//客户专员
		                    	pager['page.includeTestData'] = _this.userInfo.includeTestData;//测试数据
		                    	getData(pager);
		                    }else if(_this.userInfo.isFin){
		                    	pager['size']= 20;
			                    pager['pageNo'] = 1;
			                    getData(pager);
		                    }
			                else{
		                    	//直接进来的参数数据
		                    	pager['size']= 20;
			                    pager['pageNo'] = 1;
			                    pager['orderNum'] = _this.userInfo.orderNum;
			                    pager['str'] = 'detail';
			                    getData(pager);
		                    }				                
			            }
			        }
			    })
		    }
		    function getData(params){
		    	var commHtmlList = '';
		    	var ass=[];
		        mui.ajax("/a/biz/order/bizCommission/list4Mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	console.log(res)
		            	var dataRow = res.data.roleSet;
						var arrLen = res.data.page.list.length;
						if(arrLen <20){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						}
                        var that=this;
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
			            
                        if(arrLen > 0) {
                            $.each(res.data.page.list, function(i, item) {
								console.log(item)
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
								/*结佣详情*/
								var commDetailBtn = '';
								var commDetailBtnTxt = '详情';
								
								//审核
								var sureCheckBtn = '';
								var sureCheckBtnTxt = '';
								var noCheckBtn = '';
								var noCheckBtnTxt = '';
								if(_this.commEditFlag == true) {
									if(item.commonProcess.paymentOrderProcess.name != '驳回'){
										if(item.payTotal != '0.00' && item.commonProcess.paymentOrderProcess.name != '审批完成' && item.totalCommission != 0){
											sureCheckBtnTxt="审核通过";
											sureCheckBtn='sureCheckBtn';
											noCheckBtn = 'noCheckBtn';
											noCheckBtnTxt = '审核驳回';
										}
									}									
								}
								
								//确认支付金额
								var surePayMoneyTxt = '';
								var surePayMoneyBtn = '';
//								var sureApplyMoneyTxt="";
//								var sureApplyMoneyBtn="";
								if(_this.commOrdFlag == true) {																		
									if(item.payTotal == '0.00'){
//										sureApplyMoneyTxt="确认支付金额";
//										sureApplyMoneyBtn="sureApplyMoneyBtn"
                                        surePayMoneyTxt = '确认支付金额';
										surePayMoneyBtn = 'sureApplyMoneyBtn';
									}
								}
								//确认付款								
								if(_this.commAuditFlag == true) {
//									if(item.commonProcess.paymentOrderProcess.name == '审批完成' && item.bizStatus == '0'){
//										surePayMoneyTxt = '确认付款';
//								        surePayMoneyBtn = 'surePayMoneyBtn';
//									}
									if(item.commonProcess.paymentOrderProcess.name == '审批完成' && item.bizStatus == '1'){
										surePayMoneyTxt = '支付完成';
										surePayMoneyBtn = 'downPayMoneyBtn';
									}
									
								}
								//详情
								var detailBtn="detailBtn";
								var detailBtnTxt="详情";
								//当前状态
								var commissionBizStatus="";
								if(item.bizStatus == 0){
									commissionBizStatus="未支付";
								}else{
									commissionBizStatus="已支付";
								}
								//单次支付审批状态
								var commissionStatus="";
								if(item.totalCommission == '0.00' && item.commonProcess.paymentOrderProcess.name != '审批完成'){
									commissionStatus="待确认支付金额";
								}
								if(item.totalCommission != '0.00'){
									if(item.commonProcess.paymentOrderProcess){
										commissionStatus=item.commonProcess.paymentOrderProcess.name;
									}else{
										commissionStatus='';
									}									
								}
							commHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
							'<div class="mui-input-row">' +
									'<label>ID:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.id+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>付款金额:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ item.totalCommission+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>实际付款金额:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.payTotal+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>代销商:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.customer.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>所属单号:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.orderNumsStr+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>最后付款时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+deadline+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>实际付款时间:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+payTime+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>当前状态:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+commissionBizStatus+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>单次支付审批状态:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+commissionStatus+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>备注:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.remark+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>支付凭证:</label>' +
									'<div class="imgLists">' +_this.photoShow(item)+
									'</div>' +
								'</div>' +
								'<div class="app_color40 mui-row app_text_center content_part operation" id="check">' +
									'<div class="mui-col-xs-6 '+ sureCheckBtn +'" commId="'+ item.id +'" code="'+ item.commonProcess.paymentOrderProcess.code +'" money="'+ item.totalCommission +'">' +
										'<div class="">'+sureCheckBtnTxt +'</div>' +
									'</div>' +
									'<div class="mui-col-xs-6 '+ noCheckBtn  +'" commId="'+ item.id +'" code="'+ item.commonProcess.paymentOrderProcess.code +'" money="'+ item.totalCommission +'">' +
										'<div class="">'+ noCheckBtnTxt+'</div>' +
									'</div>' +
								'</div>' +
								'<div class="app_color40 mui-row app_text_center content_part operation" id="detail">' +
									'<div class="mui-col-xs-6 '+ surePayMoneyBtn +'" orderIds="'+ item.id +'" str="pay">' +
										'<div class="">'+surePayMoneyTxt +'</div>' +
									'</div>' +
									'<div class="mui-col-xs-6 '+ detailBtn  +'" orderIds="'+ item.id +'" str="detail">' +
										'<div class="">'+ detailBtnTxt+'</div>' +
									'</div>' +
								'</div>' +
							'</div>'

							});
							$('#commList').append(commHtmlList);
							_this.stOrdHrefHtml();	
							_this.payMoneyHtml(res.data.page.list);//字体变颜色
							_this.inHrefHtml();//确认支付金额
						}else{
							$('.mui-pull-bottom-pocket').html('');
							$('#orderList').append('<p class="noneTxt">暂无数据</p>');
							$('#OrdSechBtn').hide();
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
			            console.log(type);
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
		getPermissionList1: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.commOrdFlag = res.data;
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
                    _this.commAuditFlag = res.data;
                }
            });
        },
        getPermissionList3: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.commEditFlag = res.data;
                }
            });
        },
        payMoneyHtml:function(data){
        	var _this = this;
        	$.each(data,function(m,n){
        		if(_this.commAuditFlag == true) {						
					if(n.commonProcess.paymentOrderProcess.name == '审批完成' && n.bizStatus == '1'){
						$('.downPayMoneyBtn').css('color','#000');						
					}					
				} 
        	})
        },
        removeBtn:function(){
        	$('#payBtnremove').on('tap',  function() {
        		$('#tanchuang_pay').hide();
        	})
        },
        comfirDialig: function() {
			var _this = this;
//			document.getElementById("inCheckBtns").addEventListener('tap', function() {
			$('.noCheckBtn').on('tap', function(e) {
				var commId = $(this).attr('commId');
				var currentType= $(this).attr('code');
				var money= $(this).attr('money');				
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
									_this.rejectData(rejectTxt,2,commId,money,currentType)
								}
							} else {}
						})
					} else {}
				})
			});
//			document.getElementById("inCheckBtn").addEventListener('tap', function(e) {
			$('.sureCheckBtn').on('tap', function(e) {
				e.detail.gesture.preventDefault(); 
				var commId = $(this).attr('commId');
				var currentType= $(this).attr('code');
				var money= $(this).attr('money');
				console.log(commId)
				console.log(currentType)
				console.log(money)
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
									_this.ajaxData(inText,1,commId,money,currentType)
								} else {}
							})
						}
					} else {}
				})
			});
		},
		//审核通过
		ajaxData: function(inText,num,commId,money,currentType) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/biz/order/bizCommission/auditPay",
				data: {
					commId: commId,
					currentType: currentType,
					money: money,
					auditType: num,
					description: inText
				},
				dataType: "json",
				success: function(res) {
//					console.log(res)
					if(res.ret == true) {
						mui.toast(res.data.right);
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
								url: "../../../html/orderMgmtHtml/commissionMgmtHtml/alreadlycomList.html",
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
		rejectData: function(rejectTxt,num,commId,money,currentType) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/biz/order/bizCommission/auditPay",
				data: {
					commId: commId,
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
								url: "../../../html/orderMgmtHtml/commissionMgmtHtml/alreadlycomList.html",
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
        /*确认支付金额*/
        inHrefHtml: function() {
			var _this = this;
			_this.comfirDialig();		    
			$('.sureApplyMoneyBtn').on('tap', function() {
				$('#tanchuang_pay').show();
				var orderIds = $(this).attr('orderIds');
//				console.log(inListId)
				$.ajax({
	                type: "GET",
	                url: "/a/biz/order/bizCommission/form4Mobile",
	                dataType: "json",
	                data: {id:orderIds},
	                async:false,
	                success: function(res){
	                    console.log(res);
	                    var resid=res.data.entity.id;
	                    $('#totalMoney').val(res.data.entity.totalCommission);
	                    $('#paymoney').val(res.data.entity.totalCommission);
	                    if(res.data.entity.str != 'pay'){
	                    	$('.btnbox').show();
	                    }
	                    $('#payBtnsave').on('tap',function(){
//	                    	var totalVal = $("#paymoney").val();
//                          if (totalVal=="" || totalVal<= 0) {
//                              mui.toast("付款金额输入不正确，请重新输入！");
//                              return;
//                          }
	                    	$.ajax({
				                type: "get",
				                url: "/a/biz/order/bizCommission/save4Mobile",
				                dataType: "json",
				                data: {id:resid},
				                async:false,
				                success: function(res){				                   
				                    if(res.ret==true||res.ret=='true'){
				                    	mui.toast('保存成功！！');
				                    	$('#tanchuang_pay').hide();
				                    	window.setTimeout(function(){
						                    _this.pageInit();
						                },500);
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
        ordHrefHtml: function() {
        	var _this = this;
        	/*查询*/
    		$('.app_header').on('tap', '#OrdSechBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commListsearch.html",
						extras:{
							statu: true,
						}
					})
				}
			});
			
		/*首页*/
			$('#nav').on('tap','.staHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			})
        },
		stOrdHrefHtml: function() {
			var _this = this;
			//已结佣
			$('#alreadyKnotBtn').on('tap', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var totalDetail=$(this).attr('totalDetail');
				var totalCommission=$(this).attr('totalCommission');
				var sellerId=$(this).attr('sellerId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commDetil.html",
						extras: {							
							orderIds: orderIds,
							totalDetail:totalDetail,
							totalCommission:totalCommission,
							sellerId:sellerId
						}
					})
				}
			}),	
		    /*申请结佣*/
	        $('#applyKnotBtn').on('tap', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var totalDetail=$(this).attr('totalDetail');
				var totalCommission=$(this).attr('totalCommission');
				var sellerId=$(this).attr('sellerId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commDetil.html",
						extras: {							
							orderIds: orderIds,
							totalDetail:totalDetail,
							totalCommission:totalCommission,
							sellerId:sellerId
						}
					})
				}
			}),	
		    /*详情*/
			$('.detailBtn').on('tap',function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var detail = $(this).attr('str');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
					GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/commissionMgmtHtml/alreadlyDetil.html",
						extras: {
							orderIds: orderIds,
							detail:detail,
						}
					})
				}
			}),
			//删除
			$('#commDeleteBtn').on('tap', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var statu=$(this).attr('statu');
				var source=$(this).attr('source');
				
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认删除该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderHeader/delete4Mobile",
				                data: {id:orderIds,statu:statu,source:source},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commissionList.html",
											extras: {
	//												inListId:inListId,
											}
										})
					                },300);				                	
			                	}
			            	})
						}else {
							
						}
					})
				}
			})
			//恢复
			$('#commRecoverBtn').on('tap', function() {
				var url = $(this).attr('url');
				var orderIds = $(this).attr('orderIds');
				var statu=$(this).attr('statu');		
				if(url) {
					mui.toast('子菜单不存在')
				} else if(orderIds == orderIds) {
                    var btnArray = ['取消', '确定'];
					mui.confirm('您确认恢复该订单吗？', '系统提示！', btnArray, function(choice) {
						if(choice.index == 1) {
							$.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderHeader/recovery4Mobile",
				                data: {id:orderIds,statu:statu},
				                dataType: "json",
				                success: function(res){
				                	mui.toast('操作成功！')
				                	window.setTimeout(function(){
					                    GHUTILS.OPENPAGE({
											url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commissionList.html",
											extras: {
	//												inListId:inListId,
											}
										})
					                },300);
				                	
			                	}
			            	})
						}else {
							
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
		}		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

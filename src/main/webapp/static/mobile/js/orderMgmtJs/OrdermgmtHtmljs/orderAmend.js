(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.OrdFlag = false;
		this.OrdeditFlag = false;
		this.OrdDetailFlag = false;
		this.OrdviewFlag = false;
		this.buyPriceFlag = false;
		this.unitPriceFlag = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化					
			GHUTILS.nativeUI.closeWaiting();//关闭等待状态
			this.getPermissionList('biz:sku:bizSkuInfo:edit','OrdFlag');//true 订单信息操作
			this.getPermissionList('biz:order:bizOrderHeader:edit','OrdeditFlag');//true 订单信息操作
			this.getPermissionList1('biz:order:bizOrderDetail:edit','OrdDetailFlag');//false订单信息操作中的修改、删除
			this.getPermissionList2('biz:order:bizOrderDetail:view','OrdviewFlag');//true订单信息操作中的修改、删除
			this.getPermissionList3('biz:order:buyPrice:view','buyPriceFlag')//佣金权限
			this.getPermissionList4('biz:order:unitPrice:view','unitPriceFlag')//结算价权限
			this.buyPrice(); //
			//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.inInitHrefHtml();
			_this.getData();//获取数据	
			_this.addRemark();//添加备注
			_this.ajaxCheckStatus();//业务状态
			_this.ajaxInvoiceStatus();//发票状态
			_this.addItem();//订单商品信息添加
		},
		inInitHrefHtml: function() {
        	var _this = this;
        	/*返回订单列表*/
			$('#nav').on('tap','.ordList', function() {
				GHUTILS.OPENPAGE({
					url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
					extras: {
						
					}
				})
			})
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
                    _this.buyPriceFlag = res.data;
                }
            });
        },
        getPermissionList4: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.unitPriceFlag = res.data;
                }
            });
        },
        buyPrice:function(){
        	var _this = this;
			if(_this.buyPriceFlag==true){
				$('#staCommission').parent().show();
			}else{
				$('#staCommission').parent().hide();
			}
		},
		getData: function() {
			var _this = this;
			var datas={};
			var idd=_this.userInfo.staOrdId;//订单id
			var statu=_this.userInfo.statu;
			var source=_this.userInfo.source;
			var str=_this.userInfo.starStr;
			var createPayStr=_this.userInfo.createPayStr;
			if(_this.userInfo.starStr){
				datas={
					id:idd,
	                str:str,
	                statu:statu,
	                source:source
				};
			}else if(_this.userInfo.createPayStr){
				datas={
					id:idd,
					str:createPayStr,
				};
			}else{
				datas={
					id:idd,
	                statu:statu,
	                source:source
				};
			}
			var btnMeun = '';
	        if(createPayStr == 'createPay') {
	        	$('#changeHeader').html('申请付款');
	        	btnMeun = '<button id="payMentBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">申请付款</button>'
	        }else if(str == 'startAudit') {
	        	$('#changeHeader').html('开启审核');
	        	btnMeun = '<button id="startCheckBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">开启审核</button>'
	        }else {
	        	$('#changeHeader').html('订单修改');
	        	btnMeun = '<button id="saveDetailBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">保存</button>'
	        }
	        $('.inSaveBtn').html(btnMeun);
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizOrderHeader/form4Mobile",
                data:datas,
                dataType: "json",
                success: function(res){
                	var strTxt = res.data.bizOrderHeader.str;
                	var payMentCont = '';
                	if(res.data.bizOrderHeader.bizPoPaymentOrder.id != null || strTxt == 'createPay'){
                		payMentCont = '<div class="mui-input-row"><label>申请金额：</label>'+
							'<input type="text" id="payMentNum" class="mui-input-clear"><font>*</font></div>'+
						'<div class="mui-input-row"><label>付款时间：</label>'+
							'<input type="date" id="payMentDate" class="mui-input-clear"><font>*</font></div>'+
						'<div class="mui-input-row remark"><label>支付备注：</label>'+
							'<textarea id="payPoRemark" name="" rows="" cols=""></textarea></div>'
	        			$('#payMents').append(payMentCont);
                	}
                	$('#statuPath').val(res.data.statuPath);
                	//调取供应商信息
                	if(res.data.entity2){
                		var officeId = res.data.entity2.sellersId;
                	    $('#supplierId').val(officeId);
                	    _this.supplier($('#supplierId').val());
                	}else{
                		$('#insupplier').parent().hide();//供应商
						$('#insupplierNum').parent().hide();//供应商卡号
						$('#insupplierMoney').parent().hide();//供应商收款人
						$('#insupplierBank').parent().hide();//供应商开户行
						$('#insuppliercontract').parent().hide();//供应商合同
					    $('#insuppliercardID').parent().hide();//供应商身份证
                	} 
                	//红色字体内容
					$('#firstPart').val(res.data.entity2.customer.name);
					$('#firstPrincipal').val(res.data.custUser.name);
					$('#firstMobile').val(res.data.custUser.mobile);
					
					$('#partB').val(res.data.vendUser.vendor.name);
					$('#partBPrincipal').val(res.data.vendUser.name);
					$('#partBMobile').val(res.data.vendUser.mobile);
					
					$('#partCPrincipal').val(res.data.orderCenter.consultants.name);
					$('#partCMobile').val(res.data.orderCenter.consultants.mobile);
					//付款约定
					if(res.data.appointedTimeList) {
						$.each(res.data.appointedTimeList, function(n, v) {
							$('#staPayTime').val(_this.formatDateTime(v.appointedDate));
							$('#staPayMoney').val(v.appointedMoney);
						})
					}else {
						$('#staPayTime').val();
						$('#staPayMoney').val();
					}
					//备注
					var RemarkHtml="";
					$.each(res.data.commentList, function(q, w) {						
						RemarkHtml +='<li class="step_items">'+
							'<div class="step_num_txt">'+
								'<div class="">'+
									w.comments +
							    '</div>'+
								'<div class="">'+
                                    w.createBy.name +
							    '</div>'+
							    '<div class="">'+
                                    _this.formatDateTime(w.createDate) +
							    '</div>'+
							'</div>'+
						'</li>'
						$('#Remarks').html(RemarkHtml);
					})
					//订单id
					$('#ordId').val(_this.userInfo.staOrdId);	
					var item = res.data.bizOrderHeader;
					var shouldPay = item.totalDetail + item.totalExp + item.freight + item.serviceFee-item.scoreMoney;
					$('#staPoordNum').val(item.orderNum);//订单编号
					$('#staCoin').val(item.scoreMoney.toFixed(2));//万户币抵扣
					$('#staRelNum').val(item.customer.name);//经销店名称
					
					$('#custId').val(item.customer.id);//经销店id
					$('#invStatus').val(item.invStatus);//发票状态
					$('#bizStatus').val(item.bizStatus);//业务状态
					$('#bizType').val(item.bizType);//状态
					$('#plateformId').val(item.platformInfo.id);//状态
					$('#shiptoAddr').val(item.bizLocation.id);//地址					
					$('#orderType').val(item.orderType);
					$('#address').val(item.updateBy.id);
                    $('#staPoLastDas').val(item.receiveTotal);//已付金额
					$('#staPototals').val(item.totalDetail);//商品总价
					
					$('#staPototal').val(item.totalDetail.toFixed(2));//商品总价
					$('#staAdjustmentMoney').val(item.totalExp.toFixed(2));//调整金额
					
					$('#staFreight').val(item.freight.toFixed(2));//运费
					$('#staShouldPay').val(shouldPay.toFixed(2));//应付金额
					$('#staPoLastDa').val('('+ item.receiveTotal.toFixed(2) + ')');//已付金额
					var poLastDa = ((item.receiveTotal/(item.totalDetail+item.totalExp+item.freight+item.serviceFee-item.scoreMoney))*100).toFixed(2)+'%';
					$('#staPoLastDaPerent').val(poLastDa);//已付金额百分比
					$('#staServerPrice').val((item.totalExp + item.serviceFee + item.freight).toFixed(2));//服务费
                    $('#staCommission').val((item.totalDetail - item.totalBuyPrice).toFixed(2));//佣金
					$('#staAddprice').val(item.serviceFee.toFixed(2));//增值服务费
					$('#staConsignee').val(item.bizLocation.receiver);//收货人
					$('#staMobile').val(item.bizLocation.phone);//联系电话
					$('#staShippAddress').val(item.bizLocation.pcrName);//收货地址
					$('#staDateilAddress').val(item.bizLocation.address);//详细地址
					//发票状态
					var staInvoice = res.data.bizOrderHeader.invStatus;
                    $('#staInvoice  option[value="' + staInvoice + '"]').attr("selected",true);
					//业务状态	            				       			       
			       	var bizstatus = res.data.bizOrderHeader.bizStatus;
                    $('#inputDivAmend  option[value="' + bizstatus + '"]').attr("selected",true);
                   
					var total = item.totalDetail+item.totalExp+item.freight
					if(total > (item.receiveTotal+item.scoreMoney) && item.bizStatus!=10 && item.bizStatus!=35 && item.bizStatus!=40 && item.bizStatus!=45 && item.bizStatus!=60) {
						$('#staFinal').val("(有尾款)");
					}
					//注意事项
					if(item.orderType==5){
						$('#notes').html('<ul><li>注：</li><li>一、甲方是万户通平台的运营商，乙方是箱包厂商，丙方是采购商。丙方委托甲方进行商品采购，并通过甲方向乙方支付货款。三方在友好协商、平等互利的基础上，就甲方提供商品采购服务事宜形成本订单。</li><li>二、自丙方下单完成起，至丙方支付完毕本订单所有费用时止。</li><li>三、乙、丙双方确定商品质量标准。丙方负责收货、验货，乙方负责提供质量达标的商品，如果商品达不到丙方要求，丙方有权要求乙方退换货。甲方不承担任何商品质量责任。</li><li>四、商品交付丙方前，丙方须支付全部货款。如果丙方不能及时付款，甲方有权利拒绝交付商品。</li><li>五、本订单商品价格为未含税价，如果丙方需要发票，乙方有义务提供正规发票，税点由丙方承担。</li><li>六、乙方保证其提供的商品具有完整的所有权，并达到国家相关质量标准要求。因乙方商品问题（包括但不限于质量问题、版权问题、款式不符、数量不符等）给甲方及（或）丙方或其他方造成损失的，须由乙方赔偿全部损失。</li><li>七、本订单在万户通平台经甲、乙、丙三方线上确认后生效，与纸质盖章订单具有同等的法律效力。在系统出现故障时，甲、乙、丙三方也可采用纸质订单并签字或盖章后生效。</li></ul>')
					}else {
						$('#notes').html('<ul><li>注：</li><li>（1）本订单作为丙方采购、验货和收货的依据，丙方可持本订单及付款凭证到甲方的仓库提货。</li><li>（2）本订单商品价格为未含税价，如果丙方需要发票，则乙方有义务提供正规发票，税点由丙方承担。</li><li>（3）乙方负责处理在甲方平台上销售的全部商品的质量问题和售后服务问题，由乙丙双方自行解决；甲方可配合协调处理；</li><li>（4）本订单在万户通平台经甲、乙、丙三方线上确认后生效，与纸质盖章订单具有同等的法律效力。在系统出现故障时，甲、乙、丙三方也可采用纸质订单签字或盖章后生效。</li></ul>')
					}
					_this.statusListHtml(res.data);//状态流程
					_this.checkProcessHtml(res.data);//审核流程
					_this.commodityHtml(res.data);//商品信息
					_this.paylistHtml(res.data);//支付信息					
					var poHeaderId = res.data.bizOrderHeader.bizPoHeader.id;
					_this.apply(strTxt,poHeaderId);//申请付款
					if(res.data.bizOrderHeader.str == 'startAudit'){
						_this.startCheck(poHeaderId);//开启审核
					}					
					if(res.data.bizOrderHeader.orderNoEditable==""&& res.data.bizOrderHeader.flag=="" &&  res.data.bizOrderHeader.orderDetails=="" && res.data.bizOrderHeader.str!='audit' && res.data.bizOrderHeader.str!='startAudit'){      
						if(_this.OrdeditFlag==true){
						    _this.saveBtn(res.data.bizOrderHeader.id);//保存
					    }					
					}
					
                }
            });
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
//              	console.log(res.data)//true
                    _this.OrdFlag = res.data;
                    _this.OrdeditFlag = res.data;
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
                    _this.OrdDetailFlag = res.data;
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
                    _this.OrdviewFlag = res.data;
                }
            });
        },
        //业务状态
        ajaxCheckStatus: function() {
            var _this = this;
            var optHtml ='<option value="">请选择</option>';
            var htmlStatusAmend = ''
            $.ajax({
                type: 'GET',
                url: '/a/sys/dict/listData',
                data: {type:'biz_order_status'},
                dataType: 'json',
                success: function(res) { 
                    $.each(res, function(i, item) {
                        htmlStatusAmend += '<option class="soption" createDate="' + item.createDate + '" description="' + item.description + '" id="' + item.id + '" isNewRecord="' + item.isNewRecord + '"  sort="' + item.sort +  '" value="' + item.value + '">' + item.label + '</option>'
                    });
                    $('#inputDivAmend').html(optHtml+htmlStatusAmend);
                }
            });
        },
        //发票状态
        ajaxInvoiceStatus: function() {
            var _this = this;
            var optHtml ='<option value="">请选择</option>';
            var htmlInvoiceAmend = ''
            $.ajax({
                type: 'GET',
                url: '/a/sys/dict/listData',
                data: {type:'biz_order_invStatus'},
                dataType: 'json',
                success: function(res) { 
                    $.each(res, function(i, item) {
                        htmlInvoiceAmend += '<option class="soption" createDate="' + item.createDate + '" description="' + item.description + '" id="' + item.id + '" isNewRecord="' + item.isNewRecord + '"  sort="' + item.sort +  '" value="' + item.value + '">' + item.label + '</option>'
                    });
                    $('#staInvoice').html(optHtml+htmlInvoiceAmend);
                }
            });
        },
        //申请付款
        apply: function(type, id) {
        	$('#payMentBtn').on('tap',function(){
	            if (type == 'createPay') {
	            	var ss = $('#payMentNum').val();
					IsNum(ss)
					function IsNum(num) {
						if(num) {
							var reNum = /^\d+(\.\d+)?$/;
							if(reNum.test(num)) {
				                var payDeadline = $("#payMentDate").val() + ' 00:00:00';
				                if ($("#payMentDate").val() == '') {
				                    mui.toast("请选择本次申请付款时间!");
				                    return;
				                }
								var paymentApplyRemark = $("#payPoRemark").val();
								$.ajax({
				                    type: "get", 
				                    url: "/a/biz/po/bizPoHeaderReq/savePoHeader4Mobile",
				                    data: {
				                    	type:type, 
				                    	id:id, 
				                    	planPay: ss, 
				                    	payDeadline: payDeadline, 
				                    	fromPage: 'orderHeader', 
				                    	paymentApplyRemark: paymentApplyRemark
				                    },
				                    dataType: 'json',
				                    success: function (resule) {
				                        if (resule.ret== true || resule.ret == 'true') {
				                            mui.toast("本次申请付款成功！");
				                          	GHUTILS.OPENPAGE({
				                                url: "../../../html/orderMgmtHtml/orderpaymentinfo.html",
				                                extras: {
				                                }
				                            })
				                        }
				                    }
				                })
							} else {
								if(num < 0) {
									mui.toast("申请金额不能为负数！");
								}else {
									mui.toast("申请金额必须为数字！");
								}
							}
						}else {
							mui.toast("申请金额不能为空！");
						}
					}
	            }
	        })
        },
        //开启审核
        startCheck: function(id) {
        	var _this = this;
        	var prew = false;
        	$("#startCheckBtn").on('tap', function() {
				var btnArray = ['否', '是'];
				mui.confirm('确认开启审核吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						var btnArray = ['取消', '确定'];
						mui.prompt('请输入开启理由：', '开启理由', '', btnArray, function(a) {
							if(a.index == 1) {
								var inText = a.value;
								if(a.value == '') {
									mui.toast('开启理由不能为空！')
									return;
								} else {
									$.ajax({
					                    type: "get", 
					                    url: "/a/biz/po/bizPoHeader/startAudit",
					                    data: {
					                    	id:id, 
					                    	prew: prew, 
					                    	desc: inText, 
					                    	action: 'startAuditAfterReject'
					                    },
					                    dataType: 'json',
					                    success: function (result) {
					                        if (result.ret == true || result.ret == 'true') {
					                            mui.toast("开启审核成功！");
					                          	GHUTILS.OPENPAGE({
					                                url: "../../../html/orderMgmtHtml/orderpaymentinfo.html",
					                                extras: {
					                                }
					                            })
					                        }
					                    }
					                })
								}
							} else {}
						})
					} else {}
				})
			});
        },
		//添加备注
		addRemark:function(){
			var _this = this;
			document.getElementById("addRemarkBtn").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); 
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入你要添加的备注', '系统提示！', '系统提示！',btnArray, function(e) {
					if(e.index == 1) {
						var inText = e.value;
                        if (inText == null) {
			                return false;
			            }
                        $.ajax({
			                type:"post",
			                url:"/a/biz/order/bizOrderComment/addComment",
			                data:{orderId:$('#ordId').val(),remark:inText},
			                success:function (data) {
			                    if (data == "error") {
			                        mui.toast("添加订单备注失败，备注可能为空!");
			                    }
			                    if (data == "ok") {
			                        mui.toast("添加订单备注成功!");
			                        window.setTimeout(function(){
					                    _this.getData();
					                },500);
			                    }
			                }
			            });
					} else {						
					}
				})
			});
		},
		//供应商信息
		supplier:function(supplierId){						
			$.ajax({
                type: "GET",
                url: "/a/biz/order/bizOrderHeader/selectVendInfo",
                data: {vendorId:supplierId},		                
                dataType: "json",
                success: function(rest){
//              	console.log(rest)
                	if(rest){
                		if(rest.vendName){
                			$('#insupplier').val(rest.vendName);//供应商
                		}else{
                			$('#insupplier').parent().hide();//供应商
                		}
                		if(rest.cardNumber){
                			$('#insupplierNum').val(rest.cardNumber);//供应商卡号
                		}else{
                			$('#insupplierNum').parent().hide();//供应商卡号
                		}
						if(rest.payee){
                			$('#insupplierMoney').val(rest.payee);//供应商收款人
                		}else{
                			$('#insupplierMoney').parent().hide();//供应商收款人
                		}
						if(rest.bankName){
                			$('#insupplierBank').val(rest.bankName);//供应商开户行
                		}else{
                			$('#insupplierBank').parent().hide();//供应商收款人
                		}	
						//供应商合同
						if(rest.compactImgList != undefined){
							$.each(rest.compactImgList,function (m, n) {
                                $("#insuppliercontract").append("<a href=\"" + n.imgServer + n.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + n.imgServer + n.imgPath + "\"></a>");
                            });
						}else{
							$('#insuppliercontract').parent().hide();
						}
						//供应商身份证
						if (rest.identityCardImgList != undefined) {
                        $.each(rest.identityCardImgList,function (i, card) {
                            $("#insuppliercardID").append("<a href=\"" + card.imgServer + card.imgPath + "\" target=\"_blank\"><img width=\"100px\" src=\"" + card.imgServer + card.imgPath + "\"></a>");
                           });
                        }else{
                        	$('#insuppliercardID').parent().hide();
                        }
                	}else{
                		$('#insupplier').parent().hide();//供应商
						$('#insupplierNum').parent().hide();//供应商卡号
						$('#insupplierMoney').parent().hide();//供应商收款人
						$('#insupplierBank').parent().hide();//供应商开户行
						$('#insuppliercontract').parent().hide();//供应商合同
						$('#insuppliercardID').parent().hide();//供应商身份证
                	}
				}
			});
		},
		saveBtn:function(orid){
			var _this = this;
			/*保存*/
			var saveData={};
            $('.inSaveBtn').on('tap','#saveDetailBtn', function() {
            	$('#staAdjustmentMoney').attr('class','totalExp');//调整金额
            	$('#staFreight').attr('class','freight');//运费
            	$('#staDateilAddress').attr('class','DateilAddress');//详细地址
            	var bizStatusVal = $("#inputDivAmend")[0].value;//业务状态
            	var bizInvoiceVal = $("#staInvoice")[0].value;//发票状态
            	var totalExp = $(".totalExp").val();//调整金额
                var totalDetail = $("#staPototal").val();//商品总价
                var freight = $(".freight").val();//运费
            	if(totalExp<0){
            		var totalExpTemp = Math.abs(totalExp)
                    if (totalExpTemp > Number(freight)) {
                        mui.toast("调整金额不能大于运费！");
                        return;
                    }
                    if (totalExpTemp > Number(totalDetail) * 0.015) {
                        mui.toast("调整金额不能大于商品总价的1.5%倍！");
                        return;
                    }
            	}
            	saveData={
					statuPath:$('#statuPath').val(),
					'id':orid,
					'orderNum': $('#staPoordNum').val(),
					'orderType':$('#orderType').val(),
					'customer.id' :$('#custId').val(),
					'sellers.id':$('#supplierId').val(),
					'totalDetail':$('#staPototals').val(),
					'receiveTotal':$('#staPoLastDas').val(),
					'totalExp' :$('.totalExp').val(),//调整金额 
					'freight' :$('.freight').val(),//运费
					'invStatus' :bizInvoiceVal,//发票状态
					'bizStatus' :bizStatusVal,//业务状态              
					'bizType' :$('#bizType').val(),
					'platformInfo.id': $('#plateformId').val(),
					'bizLocation.id':$('#shiptoAddr').val(),				
					'bizLocation.address':$('.DateilAddress').val()	//详细地址			
				}; 
              	$.ajax({
	                type: "GET",
	                url: "/a/biz/order/bizOrderHeader/save4mobile",
	                data: saveData,		                
	                dataType: "json",
	                success: function(rest){
	                	console.log(rest);
	                	if(rest.ret==true){		                		
	                		mui.toast('保存成功！');
	                		window.setTimeout(function(){
			                		GHUTILS.OPENPAGE({
									url:"../../../html/orderMgmtHtml/OrdermgmtHtml/orderList.html",
										extras: {
										}
									})
			                	},500)
	                	}
	                }	
				})
			})
		},
		//状态流程
		statusListHtml:function(data){
			var _this = this;
			var statusLen = data.statusList.length;
			if(statusLen > 0) {
				var pHtmlList = '';
				$.each(data.statusList, function(i, item) {
					var step = i + 1;
					pHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>处理人:</label>'+
								'<input type="text" value="'+ item.createBy.name +'" class="mui-input-clear" disabled>'+
						    '</div>'+
							'<div class="mui-input-row">'+
						        '<label>状态:</label>'+
						        '<input type="text" class="mui-input-clear" disabled value="'+ data.stateDescMap[item.bizStatus] +'">'+
						    	'<label>时间:</label>'+
						        '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
				});
				$("#staEvoMenu").html(pHtmlList)
			}else{
				$("#staEvoMenu").parent().hide();
			}
		},
		//审核流程
		checkProcessHtml:function(data){
			var _this = this;
			var auditLen = data.auditList.length;
			if(auditLen > 0) {
				var CheckHtmlList ='';
				$.each(data.auditList, function(i, item) {
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
				$("#staCheckMenu").html(CheckHtmlList)
			}else{
				$("#staCheckMenu").parent().hide();
			}
		},
		//支付列表
        paylistHtml:function(data){
        	var _this = this;
        	var htmlPaylist = '';
        	if(data.statu != '' && data.statu =='unline'){
        		var orWaterStatus = '';
                $.ajax({
            		type: "GET",
	                url: "/a/sys/dict/listData",
	                data:{type:'biz_order_unline_bizStatus'},
	                dataType: "json",
	                async:false,
	                success: function(zl){
	                	$.each(zl,function(z, l) {
	                		$.each(data.unlineList,function(u, n) {
	                		     if(n.bizStatus==l.value){
	                		     	orWaterStatus=l.label
	                		     }
	                	    });
	                	});
            		}
            	});
        		$.each(data.unlineList, function(i, item) {
					htmlPaylist +='<li class="mui-table-view-cell mui-media payList">'+
						'<div class="mui-media-body">'+
							'<div class="mui-input-row">'+
								'<label>流水号：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.serialNum +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>支付金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.unlinePayMoney.toFixed(2) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>实收金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.realMoney.toFixed(2) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>状态：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ orWaterStatus +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>创建时间：</label>'+
								'<input type="text" class="mui-input-clear realitypayTime" value="'+ _this.formatDateTime(item.createDate) +'" disabled>'+
							'</div>'+
						'</div>'+
					'</li>'
			    });
			    $("#inPaylist").html(htmlPaylist);
        	}else{
        		$('#inPaylistbox').hide();
        	}       	
        },
		//商品信息
		commodityHtml: function(data) {
			$('#orderId').val(data.bizOrderHeader.id);
			$('#oneOrderId').val(data.bizOrderHeader.oneOrder);
			$('#orderType').val(data.orderType);
			var _this = this;
			var orderDetailLen = data.bizOrderHeader.orderDetailList.length;
			if(orderDetailLen > 0) {
				var htmlCommodity = '';
				$.each(data.bizOrderHeader.orderDetailList, function(i, item) {
					var opShelfInfo = '';
					if(data.orderType != data.PURSEHANGER){
						if(item.shelfInfo.opShelfInfo) {
							opShelfInfo = item.shelfInfo.opShelfInfo.name
						}else {
							opShelfInfo = ''
						}
					}
					var primaryMobile = '';
					if(item.primary.mobile) {
						primaryMobile = item.primary.mobile
					}else {
						primaryMobile = ''
					}
					var suplyisName = '';
					if(data.bizOrderHeader.bizStatus>=15 && data.bizOrderHeader.bizStatus!=45) {
						suplyisName = item.suplyis.name
					}else {
						suplyisName = ''
					}
					var totalMoney="";
					if(item.unitPrice !=null && item.ordQty !=null){
						totalMoney=(item.unitPrice * item.ordQty).toFixed(2);						
					}
					htmlCommodity += '<div class="mui-row app_bline commodity" id="commoditybox_' + item.id + '">' +
	                    
                    	'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>详情行号:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.lineNo + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6" id="opShelfInfo">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>货架名称:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + opShelfInfo  + '" disabled></div></li></div></div>' +
	                   
                    	'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>供应商:</label>' + 
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.vendor.name + '" disabled></div></li></div>' +
//	                    隐藏商品结算价
	                    '<div class="mui-col-sm-6 mui-col-xs-6" id="buyPrice">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>商品结算价:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.buyPrice + '" disabled></div></li></div>'+
	                    '</div>' +
	                   
                    	 '<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>供应商电话:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + primaryMobile + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>商品单价:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div></div>'+
					
						'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>采购数量:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.ordQty + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>总 额:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + totalMoney + '" disabled></div></li></div></div>'+
					
						'<div class="mui-row">' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>已发货数量:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + item.sentQty + '" disabled></div></li></div>' +
	                    '<div class="mui-col-sm-6 mui-col-xs-6">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label>发货方:</label>' +
	                    '<input type="text" class="mui-input-clear" id="" value="' + suplyisName + '" disabled></div></li></div></div>'+
						
						'<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +   
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">商品名称:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuName + '" disabled></div></li></div>'+
						
                    	'<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">商品货号:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div>' +
	                    
	                    '<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">商品编号:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.partNo + '" disabled></div></li></div>' +
	                   
	                    '<div class="mui-row lineStyle">' +
	                    '<li class="mui-table-view-cell">' +
	                    '<div class="mui-input-row ">' +
	                    '<label class="commodityName">创建时间:</label>' +
	                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + _this.formatDateTime(item.createDate) + '" disabled></div></li></div>' +
	                   
	                    '<div class="mui-row lineStyle">' +
		                    '<li class="mui-table-view-cell">' +
			                    '<div class="mui-input-row ">' +
				                    '<button type="submit" style="float:left;margin:0 0 0 50px;" amendId="'+item.id+'" orderId="'+data.bizOrderHeader.id+'" oneOrderId="'+data.bizOrderHeader.oneOrder
+'" orderType="'+data.orderType+'" class="ordAmendBtn inAddBtn app_btn_search  mui-btn-blue mui-btn-block">修改</button>'+
									'<button type="submit" style="float:right;margin:0 50px 0 0;" amendId="'+item.id+'" oneOrderId="'+data.bizOrderHeader.oneOrder
+'" orderType="'+data.orderType+'"  class="orddeleteBtn inAddBtn app_btn_search mui-btn-blue mui-btn-block">删除</button>'+
		                    	'</div></li></div>' +
		                    	
	                   
                    '</div>'
                    
				});
				$("#staCommodity").append(htmlCommodity);
				if(data.orderType == data.PURSEHANGER){
					var buyPriceArr=$('.commodity #opShelfInfo')			
					$.each(buyPriceArr, function(o,p) {
						$(p).hide();
					});
				}
//				商品结算价隐藏
                if(_this.unitPriceFlag==true){
                	if(data.bizOrderHeader.orderDetails == 'details' || data.bizOrderHeader.orderNoEditable == 'editable' || data.bizOrderHeader.flag == 'check_pending'){	
	                    var buyPriceArr=$('.commodity #buyPrice');
						$.each(buyPriceArr, function(o,p) {
							$(p).show();
						});
					}else{
						var buyPriceArr=$('.commodity #buyPrice');
						$.each(buyPriceArr, function(o,p) {
							$(p).hide();
						});
					}
                }else{
					var buyPriceArr=$('.commodity #buyPrice');					
					$.each(buyPriceArr, function(o,p) {
						$(p).hide();
					});
				}				
				_this.ordHrefHtml();
				//操作权限
				if(_this.OrdFlag == true){
						if(data.bizOrderHeader.str != 'audit' && data.bizOrderHeader.str!='detail' && data.bizOrderHeader.str!='createPay'){
							if(data.bizOrderHeader.orderNoEditable=="" && data.bizOrderHeader.flag ==""&& data.bizOrderHeader.orderDetails==""){
								$('.ordAmendBtn').parent().parent().parent().show();							
							}
						}
				}
				if(_this.OrdDetailFlag == true){
					if(data.bizOrderHeader.str != 'audit'){
						if(data.bizOrderHeader.orderNoEditable=="" && data.bizOrderHeader.flag ==""&& data.bizOrderHeader.orderDetails==""){
							if(data.bizOrderHeader.clientModify ==""){
								$('.ordAmendBtn').show();
								$('.orddeleteBtn').show();
							}
							if(data.bizOrderHeader.clientModify == 'client_modify'){
								$('.ordAmendBtn').show();
								$('.orddeleteBtn').show();
							}
						}
					}
				}
			}
		},
		//订单商品添加按钮操作
        addItem:function (data) {
        	var _this=this;
            //订单商品信息添加
            $('.staCommodity').on('tap','#secDetailBtn', function() {		
                var orderId = $('#orderId').val();
                var oneOrderId =$('#oneOrderId').val();
                var orderType = $('#orderType').val();
				GHUTILS.OPENPAGE({
					url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orderinfoAdd.html",
					extras: {
                        orderId: orderId,
                        oneOrderId: oneOrderId,
                        orderType: orderType,
					}
				})
			})
        },
		ordHrefHtml: function() {
			var _this = this;
			/*修改*/
            $('#staCommodity').on('tap','.ordAmendBtn', function() {
                var amendId = $(this).attr('amendId');
                var orderId = $(this).attr('orderId');
                var oneOrderId = $(this).attr('oneOrderId');
                var orderType = $(this).attr('orderType');
                if(_this.OrdviewFlag==true){
                	GHUTILS.OPENPAGE({
						url: "../../../html/orderMgmtHtml/OrdermgmtHtml/orDetailAmend.html",
						extras: {
	                        amendId: amendId,
	                        orderId: orderId,
	                        oneOrderId: oneOrderId,
	                        orderType: orderType,
						}
					})
                }				
			}) 
			//订单商品删除按钮操作
			mui('#staCommodity').on('tap','.orddeleteBtn', function() {
                var amendId = $(this).attr('amendId');
                var oneOrderId = $(this).attr('oneOrderId');
                var orderType = $(this).attr('orderType');
	            if(_this.OrdviewFlag==true){
	              	var btnArray = ['取消', '确定'];
					mui.confirm('确认要删除该sku商品吗？', '系统提示！',btnArray, function(e) {
						if(e.index == 1) {
	                        $.ajax({
				                type: "GET",
				                url: "/a/biz/order/bizOrderDetail/delete4Mobile",
				                data: {
				                	id:amendId,
				                	sign:1,
				                	'orderHeader.oneOrder':oneOrderId,
				                	orderType:orderType
				                },
				                dataType: "json",
				                success: function(res){
				                	console.log(res)
				                	if(res.data.value=="操作成功!"){
				                		mui.toast('删除商品成功！')                                    
                                        $("#commoditybox_" + amendId).remove();
				                	}
								}
							})
						} else {
							
						}
					})	                	
	            }
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

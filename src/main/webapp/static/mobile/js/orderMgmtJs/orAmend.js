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
			this.hrefHtml('.newinput01', '.input_div01','#hideSpanAmend01');
		},
		pageInit: function() {
			var _this = this;
		},
		getData: function() {
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/po/bizPoHeader/form4Mobile",
                data: {
                	id:_this.userInfo.staOrdId,
                	str:_this.userInfo.str,
                	fromPage:_this.userInfo.fromPage
                },
                dataType: "json",
                success: function(res){
                	console.log(res)
					/*业务状态*/
					var bizPoHeader = res.data.bizPoHeader;
					console.log(bizPoHeader)
					var orshouldPay = bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight
					$('#orpoNum').val(res.data.bizOrderHeader.orderNumber)//单号
					$('#ordtotal').val(bizPoHeader.totalDetail)//总价
					$('#orshouldPay').val(orshouldPay)//应付金额
					$('#orLastDa').val(_this.newData(bizPoHeader.lastPayDate))//最后付款时间
					//交货地点
					if(bizPoHeader.deliveryStatus==0 || bizPoHeader.deliveryStatus == ''){
						$('#fromType1').attr('checked','checked');
						$('#fromType2').removeAttr('checked');
						$('#deliveryStatus').val('0');
					}
					if(bizPoHeader.deliveryStatus==1){
						$('#fromType1').removeAttr('checked');
						$('#fromType2').attr('checked','checked');
						$('#deliveryStatus').val('1');
					}
					$('#orRemark').val(bizPoHeader.remark)//备注
//					$('#orTypes').val()
					//订单状态 
					var valueTxt = res.data.bizPoHeader.bizStatus;
					console.log(valueTxt)
					$('#orTypes').val(valueTxt);
					$('#orSupplier').val(bizPoHeader.vendOffice.name)//供应商
					if(bizPoHeader.vendOffice.bizVendInfo) {
						$('#orSupplierNum').val(bizPoHeader.vendOffice.bizVendInfo.cardNumber)//供应商卡号
						$('#orSupplierMoney').val(bizPoHeader.vendOffice.bizVendInfo.payee)//供应商收款人
						$('#orSupplierBank').val(bizPoHeader.vendOffice.bizVendInfo.bankName)//供应商开户行
						$('#orSuppliercontract').val(bizPoHeader.vendOffice.bizVendInfo.compactImgList)//供应商合同
						$('#orSuppliercardID').val(bizPoHeader.vendOffice.bizVendInfo.identityCardImgList)//供应商身份证
					}
					var orapplyNum = bizPoHeader.bizPoPaymentOrder.id != null ?
                    bizPoHeader.bizPoPaymentOrder.total : (bizPoHeader.totalDetail+bizPoHeader.totalExp+bizPoHeader.freight-bizPoHeader.payTotal)
                    $('#orapplyNum').val(orapplyNum.toFixed(2));//申请金额
                    $('#orNowDate').val(_this.formatDateTime(bizPoHeader.bizPoPaymentOrder.deadline));//本次申请付款时间
                    
					_this.commodityHtml(res.data)
					_this.statusListHtml(res.data)
					_this.saveDetail(res.data)
                }
            });
            _this.btnshow();
		},
		btnshow: function(data) {
			var _this = this;
			$('input[type=radio]').on('change', function() {
				if(this.checked && this.value == 0) {
					$('#buyCenterId').show();
					$('#deliveryStatus').val(this.value);
				}
				if(this.checked && this.value == 1) {
					$('#buyCenterId').hide();
					$('#deliveryStatus').val(this.value);
				}
			})
		},
		hrefHtml: function(newinput, input_div,hideSpanAmend) {
			var _this = this;
			_this.ajaxGoodList();
			$(newinput).on('focus', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show()
				$(hideSpanAmend).show();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false
				}else{
					_this.selectOpen = true
				}
				_this.rendHtml(_this.datagood,$(this).val())
			})
			
			$(hideSpanAmend).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).hide()
				$(hideSpanAmend).hide()
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
                _this.fromOfficeId = $(this).attr("id");
				$(newinput).val($(this).text())
				$(input_div).hide()
				$(hideSpanAmend).hide()
				_this.selectOpen = true
			})
		},
		rendHtml: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reult.push(item)
					}
				})
			$.each(reult, function(i, item) {
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div01').html(htmlList)
		},
		//采购中心
        ajaxGoodList: function() {
            var _this = this;
            var htmlList = ''
            $.ajax({
                type: 'GET',
                url: '/a/sys/office/queryTreeListByPhone',
                data: {
                    type: 8,
                    source:'officeConnIndex'
                },
                dataType: 'json',
                success: function(res) {
                    _this.datagood = res
                    $.each(res, function(i, item) {
                        htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
                    });
                    $('.input_div01').html(htmlList)
                }
            });
        },
		statusListHtml:function(data){
			var _this = this;
			var pHtmlList = '';
			if(data.bizPoHeader.commonProcessList) {
				$.each(data.bizPoHeader.commonProcessList, function(i, item) {
					var comLen = data.bizPoHeader.commonProcessList.length;
					var step = i + 1;
					if(i != comLen-1) {
						pHtmlList +='<li class="step_item">'+
							'<div class="step_num">'+ step +' </div>'+
							'<div class="step_num_txt">'+
								'<div class="mui-input-row">'+
									'<label>批注:</label>'+
							        '<textarea name="" rows="" cols="" disabled>'+ item.description +'</textarea>'+
							    '</div>'+
								'<br />'+
								'<div class="mui-input-row">'+
							        '<label>审批人:</label>'+
							        '<input type="text" value="'+ item.user.name +'" disabled>'+
							    	'<label>时间:</label>'+
							        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " disabled>'+
							    '</div>'+
							'</div>'+
						'</li>'
					}
					if(i == comLen-1) {
						pHtmlList += '<li class="step_item">' +
							'<div class="step_num">' + step + ' </div>' +
							'<div class="step_num_txt">' +
							'<div class="mui-input-row">' +
							'<label>当前状态:</label>' +
							'<input type="text" value="' + item.purchaseOrderProcess.name + '" disabled>' +
							'</div>' +
							'</div>' +
							'</li>'
					}
				});
				$("#orCheckMenu").html(pHtmlList)
			}
		},
		commodityHtml: function(data) {
			var _this = this;
			var htmlCommodity = '';
			if(data.bizPoHeader.poDetailList) {
				$.each(data.bizPoHeader.poDetailList, function(i, item) {
				var outHtml = '';
				if(data.bizPoHeader.id!=null) {
					outHtml = '<div class="mui-input-row">'+
								'<label>所属单号：</label>'+
								'<input type="text" value="'+ data.bizOrderHeader.orderNumber +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>已供货数量：</label>'+
								'<input type="text" value="'+ item.sendQty +'" disabled>'+
							'</div>'
				}
				if(data.bizPoHeader.id==null) {
					outHtml	= '<div class="mui-input-row">'+
								'<label>申报数量：</label>'+
								'<input type="text" value="'+ item.reqQty +'" disabled>'+
							'</div>'
				}
				htmlCommodity +='<li class="mui-table-view-cell mui-media app_bline app_pr">'+
					/*产品图片*/
					'<div class="photoParent mui-pull-left app_pa">'+
						'<img class="app_pa" src="'+item.skuInfo.productInfo.imgUrl+'">'+
					'</div>'+
					/*产品信息*/
					'<div class="mui-media-body app_w80p app_fr">'+
						'<div class="mui-input-row">'+
							'<label>品牌名称：</label>'+
							'<input type="text" value="'+ item.skuInfo.productInfo.brandName +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品名称：</label>'+
							'<input type="text" value="'+ item.skuInfo.name +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>商品货号：</label>'+
							'<input type="text" value="'+ item.skuInfo.itemNo +'" disabled>'+
						'</div>'+
						outHtml +
						'<div class="mui-input-row">'+
							'<label>采购数量：</label>'+
							'<input type="text" value="'+ item.ordQty +'" disabled>'+
						'</div>'+
						'<div class="mui-input-row">'+
							'<label>结算价：</label>'+
							'<input type="text" value="'+ item.unitPrice +'" disabled>'+
						'</div>'+
					'</div>'+
				'</li>'
				});
				$("#orCheckCommodity").html(htmlCommodity)
			}
			if(data.bizPoHeader.poDetailList==null) {
				console.log(data.bizPoHeader.poDetailList==null)
			}
		},
		//保存按钮操作
        saveDetail: function (res) {
            var _this = this;
            
            //点击保存按钮操作 保存按钮控制修改商品申报数量和备货商品的添加
            $('#orSaveBtn').on('tap',function(){
            	console.log(res)
            	var id = res.bizPoHeader.id;
            	var bizPoPaymentOrderId = res.bizPoHeader.bizPoPaymentOrder.id; 
            	var vendOfficeId = res.bizPoHeader.vendOffice.id;
            	var lastPayDate = $('#orLastDa').val() + ' 00:00:00';
            	var deliveryStatus = $('#deliveryStatus').val();
            	var deliveryOfficeId = res.bizPoHeader.deliveryOffice.id;
            	var deliveryOfficeName = res.bizPoHeader.deliveryOffice.name;
            	var remarks = $('#orRemark').val();
            	var planPay = $('#orapplyNum').val();
            	var payDeadline = $('#orNowDate').val();
//          	console.log(id)
//          	console.log(bizPoPaymentOrderId)
//          	console.log(vendOfficeId)
//          	console.log(lastPayDate)
//          	console.log(deliveryStatus)
//          	console.log(deliveryOfficeId)
//          	console.log(deliveryOfficeName)
//          	console.log(remarks)
//          	console.log(planPay)
//          	console.log(payDeadline)
                $.ajax({
                    type: "post", 
                    url: "/a/biz/po/bizPoHeader/savePoHeader4Mobile",
                    data: {
						id: id,
						'bizPoPaymentOrder.id': bizPoPaymentOrderId,
						'vendOffice.id': vendOfficeId,
						lastPayDate: lastPayDate,
						deliveryStatus: deliveryStatus,
						'deliveryOffice.id': deliveryOfficeId,
						'deliveryOffice.name': deliveryOfficeName,
						remark: remarks,
						planPay: planPay,
						payDeadline: payDeadline
                    },
                    dataType: 'json',
                    success: function (resule) {
                    	console.log(resule)
                        if (resule.data.right == "操作成功!") {
                            mui.toast("操作成功！");
//                          window.setTimeout(function(){
                            	GHUTILS.OPENPAGE({
	                                url: "../../html/orderMgmtHtml/orderpaymentinfo.html",
	                                extras: {
	                                }
	                            })
//                          },500)                            
                        }
                    }
                })
           })
        },
		//时间格式转化
        newData:function(da){
        	var _this = this;
//      	var date = new Date(da);//时间戳为10位需*1000，时间戳为13位的话不需乘1000      
            var now = new Date(da);
                y = now.getFullYear(),
                m = now.getMonth() + 1,
                d = now.getDate();
                var hours = now.getHours();
                var minutes = now.getMinutes();
                var seconds = now.getSeconds();
           // return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + "T" + now.toTimeString().substr(0, 8);
             return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d);
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

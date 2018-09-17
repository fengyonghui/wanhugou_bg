(function($) {
    var ACCOUNT = function() {
        this.ws = null;
        this.userInfo = GHUTILS.parseUrlParam(window.location.href);
        this.expTipNum = 0;
        this.datagood = [];
        this.dataSupplier = [];
        this.skuInfoIds_1="";
        this.skuInfoIds_2="";
        this.reqQtys_1="";
        this.reqQtys_2="";
        this.reqDetailIds="";
        this.LineNos="";
        this.fromOfficeId="";
        this.deleteBtnFlag = "false"
        return this;
    }

    var bizStatusDesc = (function() {
        var result;
        $.ajax({
            type: "GET",
            url: "/a/sys/dict/listData",
            data: {type:"biz_req_status"},
            dataType: "json",
            async:false,
            success: function(res){
                result = res;
            }
        });
        return result;

    })();

    ACCOUNT.prototype = {
        init: function() {
            this.hrefHtml('.newinput01', '.input_div01','#hideSpanAmend01');
			this.hrefHtmls('.newinput02', '.input_div02','#hideSpanAmend02');
            this.pageInit(); //页面初始化
            //this.getData();//获取数据
            GHUTILS.nativeUI.closeWaiting();//关闭等待状态
            //GHUTILS.nativeUI.showWaiting()//开启
        },
        pageInit: function() {
            var _this = this;
            _this.searchSkuHtml();
            _this.saveDetail();
            _this.getData();
		},
        getData: function() {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeaderForVendor/form4MobileNew",
                data: {id:_this.userInfo.reqId,str:'detail'},
                dataType: "json",
                success: function(res){
                	console.log(res)
                	//供应商
                	if(res.data.bizRequestHeader.bizVendInfo){
	                	var officeId = res.data.bizRequestHeader.bizVendInfo.office.id;
	                	$('#inSta').val(officeId);
	                	$('#insupplier').attr('officeId',officeId);
	                	_this.supplier($('#inSta').val());
                	}     
                	//备货方
					console.log(res.data.bizRequestHeader.fromType);
					if(res.data.bizRequestHeader.fromType==1){
						$('#fromType1').attr('checked','checked');
						$('#fromType2').removeAttr('checked');
					}
					if(res.data.bizRequestHeader.fromType==2){
						$('#fromType1').removeAttr('checked');
						$('#fromType2').attr('checked','checked');						
					}
                    $('#inPoordNum').val(res.data.bizRequestHeader.reqNo);//备货单编号
                    $('#inOrordNum').val(res.data.bizRequestHeader.fromOffice.name);//采购中心
                    $('#inPototal').val(res.data.bizRequestHeader.totalMoney);
                    $('#inPoRemark').val(res.data.bizRequestHeader.remark);//备注
                    $('#inMoneyReceive').val();
                    $('#inMarginLevel').val();
                    var dataValue =_this.newData(res.data.bizRequestHeader.recvEta);
                    $('#inPoLastDa').val(dataValue);//收货时间

                    /*业务状态*/
                    var bizstatus = res.data.bizRequestHeader.bizStatus;
                    $('#inputDivAmend  option[value="' + bizstatus + '"]').attr("selected",true);
                    //排产状态
				    var itempoSchType=res.data.bizRequestHeader.bizPoHeader.poSchType;
				    var SchedulstatusTxt = '';
				    $.ajax({
		                type: "GET",
		                url: "/a/sys/dict/listData",
		                data: {type:"poSchType"},		                
		                dataType: "json",
		                success: function(res){
		                	console.log(res)
		                	$.each(res,function(i,item){
		                		 if(item.value==itempoSchType){
		                		 	  SchedulstatusTxt = item.label 
		                		 }
		                	})
		                	$('#inSchedulstatus').val(SchedulstatusTxt);
						}
					});
                    _this.commodityHtml(res.data);//备货商品反填
                    _this.paylistHtml(res.data);//支付列表
                    _this.statusListHtml(res.data);//状态流程
                    _this.checkProcessHtmls(res.data);//审批流程
                    //排产信息
					if(res.data.bizRequestHeader.str=='detail'){
						var poheaderId = res.data.bizRequestHeader.bizPoHeader.id;
						console.log(poheaderId)
		                if (poheaderId == null || poheaderId == "") {
		                    $("#inSchedultype").val("未排产")
		                }
		                if (poheaderId != null && poheaderId != "") {
		                	_this.scheduling(poheaderId);
		                }
					}
                }
            });
        },      		
        newData:function(da){
        	var _this = this;
            var now = new Date(da),
                y = now.getFullYear(),
                m = now.getMonth() + 1,
                d = now.getDate();
           // return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d) + "T" + now.toTimeString().substr(0, 8);
             return y + "-" + (m < 10 ? "0" + m : m) + "-" + (d < 10 ? "0" + d : d);
        },
        saveDetail: function () {
            var _this = this;
            //点击保存按钮操作 保存按钮控制修改商品申报数量和备货商品的添加
            $('#saveDetailBtn').on('tap',function(){
            	console.log('获取_this.skuInfoIds_2的值');
            	console.log(_this.skuInfoIds_2);
               if(_this.skuInfoIds_2){
               	console.log('添加哈哈')
               	    //备货商品的添加
	  		        var skuIds = _this.skuInfoIds_2.split(",");
	                var skuInfoIdsTemp = ""
	                for (var i=0; i<skuIds.length; i++){
	                    var skuId = skuIds[i];
	                    if (skuId != null && skuId != "") {
	                        skuInfoIdsTemp += "," + skuId;
	                    }
	                }
	                _this.skuInfoIds_2 = skuInfoIdsTemp.substring(1);
	              
	                var skuIds2 = _this.skuInfoIds_2.split(",");
	                var reqQtysTemp = "";
	                for (var j=0; j<skuIds2.length; j++) {
	                    var cheId = skuIds2[j];
	                    var reqQty = $("#reqQty_" + cheId).val()
	                    if (reqQty == null || reqQty == "") {
	                  	    mui.toast("请输入申报数量！！")
	                        return;
	                    }
	                    reqQtysTemp += "," + reqQty;
	              }
	                _this.reqQtys_2 = reqQtysTemp.substring(1);
	                skuInfoIds = _this.skuInfoIds_1 + _this.skuInfoIds_2;
                    reqQtys = _this.reqQtys_1 + _this.reqQtys_2;
               }
                else{
               	    //保存之后以及初始化反填数据申报数量的修改
               	    console.log('修改哈哈')
           	        var skuInfoId='';
	                var reqQty='';
	                var dos=$("#commodityMenu .skuinfo_check");
	            	$.each(dos,function(n,v){
	            		var that=this;	                            	
	                	var y=$(that).attr('id');
	                	var reqQtyVal=$("#reqQty_"+y);
	                	skuInfoId+=","+y;
	                	reqQty+=","+reqQtyVal.val();
	                	if (reqQtyVal.val() == null || reqQtyVal.val() == "") {
	                  	    mui.toast("请输入申报数量！！！")
	                        return false;
	                    }
	               })
	            	skuInfoIds=skuInfoId.substring(1);
	            	reqQtys=reqQty.substring(1);	              
               }    
                _this.reqDetailIds = _this.reqDetailIds.substring(0,(_this.reqDetailIds.lastIndexOf(",")));
                _this.LineNos = _this.LineNos.substring(0,(_this.LineNos.lastIndexOf(",")));
                console.log(_this.reqDetailIds);
                console.log(_this.LineNos);
                var inPoLastDaVal = $("#inPoLastDa").val(); //期望收货时间
//              console.log("inPoLastDaVal=" + inPoLastDaVal);

                var inPoRemarkVal = $("#inPoRemark").val(); //备注
                var bizStatusVal = $("#inputDivAmend")[0].value; //业务状态
                var id = _this.userInfo.reqId; //业务状态

                if (_this.fromOfficeId == null || _this.fromOfficeId == "") {
                    var inOrordNum = $("#inOrordNum").val();
                    _this.fromOfficeId = _this.getFromOfficeId(inOrordNum);
                }

                if(_this.fromOfficeId == null || _this.fromOfficeId == ""){
                    mui.toast("请选择采购中心！")
                    return;
                }
                if(inPoLastDaVal == null || inPoLastDaVal == "") {
                    mui.toast("请选择收货时间！")
                    return;
                }
                if(bizStatusVal == null || bizStatusVal == "") {
                    mui.toast("请选择业务状态！")
                    return;
                }
                $.ajax({
                    type: "post", 
                    url: "/a/biz/request/bizRequestHeaderForVendor/saveForMobile",
                    data: {"id":id, "fromOffice.id": _this.fromOfficeId, "recvEta":inPoLastDaVal, "remark": inPoRemarkVal, "bizStatus": bizStatusVal, "skuInfoIds": skuInfoIds, "reqQtys": reqQtys, "reqDetailIds":_this.reqDetailIds, "LineNos":_this.LineNos},
                    dataType: 'json',
                    success: function (resule) {
                    	console.log(resule)
                        if (resule == true) {
                            alert("保存备货单成功！");
                            GHUTILS.OPENPAGE({
                                url: "../../html/inventoryMagmetHtml/inventoryList.html",
                                extras: {
                                }
                            })
                        }
                    }
                })
           })
        },
        getPermissionList: function () {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": "biz:request:bizRequestDetail:edit"},
                async:false,
                success: function(res){
                    _this.deleteBtnFlag = res.data;
                }
            });
        },
        getFromOfficeId: function(inOrordNum) {
            var _this = this;
            var fromOfficeId = "";
            $.ajax({
                type: 'GET',
                url: '/a/sys/office/queryTreeList',
                data: {
                    type: 8
                },
                dataType: 'json',
                async:false,
                success: function(res) {
                    $.each(res, function(i, item) {
                        // htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
                        if (item.name == inOrordNum){
                            fromOfficeId = item.id;
                        }
                    });
                }
            });
            return fromOfficeId;
        },       
        hrefHtml: function(newinput, input_div,hideSpanAmend) {
			var _this = this;
			_this.ajaxGoodList();
			_this.ajaxCheckStatus();
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
		hrefHtmls: function(newinput, input_div,hideSpanAmend) {
			var _this = this;
			_this.ajaxSupplier();
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
				_this.rendHtmls(_this.dataSupplier,$(this).val());
				if($(this).val() == '') {
            		$('#insupplierNum').parent().hide();
					$('#insupplierMoney').parent().hide();
					$('#insupplierBank').parent().hide();
            	}
			})
			
			$(hideSpanAmend).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid');
				$(input_div).hide();
				$(hideSpanAmend).hide();
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid')
//              _this.fromOfficeId = $(this).attr("id");
//              alert($(this).attr("id"))
				$(newinput).val($(this).text());
				$(input_div).hide();
				$(hideSpanAmend).hide();
				_this.selectOpen = true;
				$('#inSta').val($(this).attr("id"));
				_this.supplier($(this).attr("id"));
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
		rendHtmls: function(data, key) {
			var _this = this;
			var reults = [];
			var htmlLists=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reults.push(item)
					}
				})
			$.each(reults, function(i, item) {
				htmlLists += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div02').html(htmlLists)
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
                    console.log(res)
                    $.each(res, function(i, item) {
                        htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
                    });
                    $('.input_div01').html(htmlList)
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
                data: {type:'biz_req_status'},
                dataType: 'json',
                success: function(res) {
                    $.each(res, function(i, item) {
                        htmlStatusAmend += '<option class="soption" createDate="' + item.createDate + '" description="' + item.description + '" id="' + item.id + '" isNewRecord="' + item.isNewRecord + '"  sort="' + item.sort +  '" value="' + item.value + '">' + item.label + '</option>'
                    });
                    $('#inputDivAmend').html(optHtml+htmlStatusAmend)
                    _this.getPermissionList();
//                  _this.getData();
                }
            });
        },
        //供应商信息
        ajaxSupplier: function() {
			var _this = this;
			var htmlSupplier = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeList',
				data: {
					type: 7
				},
				dataType: 'json',
				success: function(res) {
					console.log('777');
					console.log(res)
					_this.dataSupplier = res
					$.each(res, function(i, item) {
						htmlSupplier += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
					});
					$('.input_div02').html(htmlSupplier)
//					_this.getData();
				}
			});
		},
		//供应商信息反填
		supplier:function(supplierId){	
			var _this = this;
			$.ajax({
                type: "GET",
                url: "/a/biz/request/bizRequestHeaderForVendor/selectVendInfo",
                data: {vendorId:supplierId},		                
                dataType: "json",
                success: function(rest){
                	console.log(rest);               	
                	if(rest){
                		$('#inSta').val(rest.office.id);
                		$('#insupplier').attr('officeid',rest.office.id);
                		if(rest.vendName){
                			$('#insupplierNum').parent().show();
                			$('#insupplier').val(rest.vendName);//供应商
                		}else{
                			$('#insupplierNum').html('');
                		}
                		if(rest.cardNumber) {
	                		$('#insupplierNum').parent().show();
	                		$('#insupplierNum').val(rest.cardNumber);//供应商卡号
	                	}else {
	                		$('#insupplierNum').html('');
	                	}
	                	if(rest.payee) {
	                		$('#insupplierMoney').parent().show();
	                		$('#insupplierMoney').val(rest.payee);//供应商收款人
	                	}else {
	                		$('#insupplierMoney').html('');
	                	}
	                	if(rest.bankName) {
							$('#insupplierBank').parent().show();
							$('#insupplierBank').val(rest.bankName);//供应商开户行
	                	}else {
	                		$('#insupplierBank').html('');
	                	}						
                	}
                	else{
						$('#insupplierNum').parent().hide();//供应商卡号
						$('#insupplierMoney').parent().hide();//供应商收款人
						$('#insupplierBank').parent().hide();//供应商开户行
                	}                						
				}
			});
		},
		//排产信息接口
		scheduling:function(idval){
			var _this = this;			
			$.ajax({
                type: "GET",
                url: "/a/biz/po/bizPoHeader/scheduling4Mobile",
                data: {id:idval},
                dataType: "json",
                success: function(res){
                	console.log('---')
                	console.log(res.data);
                	if (res.data.detailHeaderFlg != true && res.data.detailSchedulingFlg != true) {
                        $("#inSchedultype").val("未排产");
                    }
                	//按订单排产
                	if (res.data.detailHeaderFlg == true) {
                        $("#inSchedultype").val("按订单排产");                      
                    }
                	//按商品排产
                	if (res.data.detailSchedulingFlg == true) {
                        $("#inSchedultype").val("按商品排产");                         
                	}
				}
			})
		},
		 //支付列表
        paylistHtml:function(data){
        	var _this = this;
        	var htmlPaylist = '';
        	console.log(data)
        	if(data.paymentOrderList != null && data.paymentOrderList.length > 0){
        		$.each(data.paymentOrderList, function(i, item) {
					console.log(item)			
					htmlPaylist +='<li class="mui-table-view-cell mui-media payList">'+
						'<div class="mui-media-body">'+
							'<div class="mui-input-row">'+
								'<label>付款金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.total.toFixed(2) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>实际付款金额：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ item.payTotal.toFixed(2) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>最后付款时间：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.deadline) +'" disabled>'+
							'</div>'+
							'<div class="mui-input-row">'+
								'<label>实际付款时间：</label>'+
								'<input type="text" class="mui-input-clear" value="'+ _this.formatDateTime(item.payTime) +'" disabled>'+
							'</div>'+
						'</div>'+
					'</li>'
			   });
			   $("#inPaylist").html(htmlPaylist);
        	}else{
        		$('#inPaylistbox').hide();
        	}
        	
        },
		//状态流程
        statusListHtml:function(data){
            var _this = this;
            var pHtmlList = '';
            $.each(data.auditStatusList, function(i, item) {
                var checkBizStatus = getTizstatusTxt(item.bizStatus);
                var step = i + 1;
                pHtmlList +='<li id="procList" class="step_item">'+
                    '<div class="step_num">'+ step +' </div>'+
                    '<div class="step_num_txt">'+
                    '<div class="mui-input-row">'+
                    '<label>处理人:</label>'+
                    '<input type="text" value="'+ item.createBy.name +'" class="mui-input-clear" disabled>'+
                    '</div>'+
                    '<div class="mui-input-row">'+
                    '<label>状态:</label>'+
                    '<input type="text" value="'+ checkBizStatus +'" class="mui-input-clear" disabled>'+
                    '<label>时间:</label>'+
                    '<input type="text" value=" '+ _this.formatDateTime(item.createDate) +' " class="mui-input-clear" disabled>'+
                    '</div>'+
                    '</div>'+
                    '</li>'
            });
            $("#inCheckAddMenu").html(pHtmlList)
        },
        //审批流程
		checkProcessHtmls:function(data){
			var _this = this;
			var auditLen = data.bizRequestHeader.commonProcessList.length;
			if(data.bizRequestHeader.commonProcessList) {
				var CheckHtmlList ='';
				$.each(data.bizRequestHeader.commonProcessList, function(i, item) {
					console.log(item)
					var auditLen = data.bizRequestHeader.commonProcessList.length;
					console.log(auditLen-1)
					var step = i + 1;
					if(i!=auditLen-1) {
						CheckHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
						        '<label>批注:</label>'+
						        '<input type="text" value="'+ item.description +'" class="mui-input-clear" disabled>'+
						    	'<label>审批人:</label>'+
						        '<input type="text" value=" '+ item.user.name +' " class="mui-input-clear" disabled>'+
						        '<label>时间:</label>'+
						        '<input type="text" value=" '+ _this.formatDateTime(item.updateTime) +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
					}								
					if(i==auditLen-1 && data.bizRequestHeader.processPo != 'processPo' && item.requestOrderProcess.name != '审核完成') {
						if(item.requestOrderProcess.name != '审核完成'){
							CheckHtmlList +='<li class="step_item">'+
								'<div class="step_num">'+ step +' </div>'+
								'<div class="step_num_txt">'+
									'<div class="mui-input-row">'+
								        '<label>当前状态:</label>'+
								        '<input type="text" value="'+ item.requestOrderProcess.name +'" class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
						}
					}
				});
				if(data.bizRequestHeader.bizPoHeader!=""){
					$.each(data.bizRequestHeader.bizPoHeader.commonProcessList, function(a, items) {
						console.log(items)
						var len = data.bizRequestHeader.bizPoHeader.commonProcessList.length;
						console.log(len)
						var totalStep = auditLen + a;
                        if(a==0&&len>1){
                        	CheckHtmlList +='<li class="step_item">'+
								'<div class="step_num">'+ totalStep +' </div>'+
								'<div class="step_num_txt">'+
									'<div class="mui-input-row">'+
								        '<label>批注:</label>'+
								        '<input type="text" value="'+ items.description +'" class="mui-input-clear" disabled>'+
								    	'<label>审批人:</label>'+
								        '<input type="text" value=" '+ items.user.name +' " class="mui-input-clear" disabled>'+
								        '<label>时间:</label>'+
								        '<input type="text" value=" '+ _this.formatDateTime(items.updateTime) +' " class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
                        }
                        if(a>0&&a<len-1){
                        	CheckHtmlList +='<li class="step_item">'+
								'<div class="step_num">'+ totalStep +' </div>'+
								'<div class="step_num_txt">'+
									'<div class="mui-input-row">'+
								        '<label>批注:</label>'+
								        '<input type="text" value="'+ items.description +'" class="mui-input-clear" disabled>'+
								    	'<label>审批人:</label>'+
								        '<input type="text" value=" '+ items.user.name +' " class="mui-input-clear" disabled>'+
								        '<label>时间:</label>'+
								        '<input type="text" value=" '+ _this.formatDateTime(items.updateTime) +' " class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
                        }
                        if(a==len-1){
                        	CheckHtmlList +='<li class="step_item">'+
								'<div class="step_num">'+ totalStep +' </div>'+
								'<div class="step_num_txt">'+
									'<div class="mui-input-row">'+
								        '<label>当前状态:</label>'+
								        '<input type="text" value="'+ items.purchaseOrderProcess.name +'" class="mui-input-clear" disabled>'+
								    '</div>'+
								'</div>'+
							'</li>'
                        }
					});
				}					
				$("#inapprovalAddMenu").html(CheckHtmlList);
			}else{
				$("#inapprovalAddMenu").parent().hide();
			}
		},
        commodityHtml: function(data) {
        	//备货商品初始化反填
            var _this = this;
            var htmlCommodity = '';
            $.each(data.reqDetailList, function(i, item) {
            	console.log(item)
                _this.skuInfoIds_1 += item.skuInfo.id + ","
                _this.reqQtys_1 += item.reqQty + ","
                _this.reqDetailIds += item.id + ","
                _this.LineNos += item.lineNo + ","
                console.log(_this.reqDetailIds)
                console.log(_this.LineNos)
                htmlCommodity += '<div class="mui-row app_bline" id="' + item.id + '">' +
                	'<input style="display:none;" name="" class="skuinfo_check" id="' + item.skuInfo.id + '" type="checkbox">' +
                    
                    '<div class="mui-row lineStyle">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                    '<li class="mui-table-view-cell app_bline3">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label class="commodityName">商品名称:</label>' +
                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.name + '" disabled></div></li></div></div>' +
                    
                    '<div class="mui-row lineStyle">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                    '<li class="mui-table-view-cell app_bline3">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label class="commodityName">商品货号:</label>' +
                    '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + item.skuInfo.itemNo + '" disabled></div></li></div></div>' +
                    
                    '<div class="mui-row lineStyle">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
                    '<li class="mui-table-view-cell app_bline3">' +
                    '<div class="mui-input-row ">' +
                    '<label class="">商品编码:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.partNo + '" disabled>' +
                    '</div></li></div></div>' +
                    
                    '<div class="mui-row">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell app_bline3">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>品牌名称:</label>' + 
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.productInfo.brandName + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell app_bline3">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>供应商:</label>' +
                    '<input type="text" class="mui-input-clear" id="" value="' + item.skuInfo.productInfo.brandName + '" disabled></div></li></div></div>' +
                   
                    '<div class="mui-row inAddFont">' +
                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell app_bline3">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>结算价:</label>' +  
                    '<input type="text" class="mui-input-clear" id="" value="' + item.unitPrice + '" disabled></div></li></div>' +
                    '<div class="mui-col-sm-5 mui-col-xs-5">' +
                    '<li class="mui-table-view-cell app_bline3">' +
                    '<div class="mui-input-row inputClassAdd">' +
                    '<label>申报数量:</label>' +
                    '<input type="text" class="mui-input-clear inDeclareNum" id="reqQty_'+ item.skuInfo.id + '" value="' + item.reqQty + '">'+
                    '<font>*</font>'+
                    '</div></li></div></div>';

                if (_this.deleteBtnFlag == true) {
                    htmlCommodity += '<div class="inAddBtnParent">' +
                    '<button id="' + item.id +'" type="button" class="deleteSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block" >删除</button></div>';
                    }
                htmlCommodity += '</div>';
            });
            $("#commodityMenu").html(htmlCommodity);
            _this.delItem();
            _this.removeItem();
        },
        //备货商品删除按钮操作
        delItem:function () {
        	var that=this;
            mui('#commodityMenu').on('tap','.deleteSkuButton',function(e){
                var obj = e.detail.target.id;
                mui.confirm("此删除不需点保存,即可生效.确认删除此条信息吗？",'系统提示！',function (choice){
					if(choice.index==1){
                        $.ajax({
                            type: "post",
	                        url: "/a/biz/request/bizRequestDetail/delItem",
	                        data: {id: obj},
	                        success: function (data) {
	                            if (data == 'ok') {
	                                mui.toast("删除成功！");
	                                $("#" + obj).remove();
	                            }
	                        }
	                    })
					}else{
	
                    }
              });
            });
        },
        //备货商品移除按钮操作
        removeItem:function () {
            var _this = this;
            mui('#commodityMenu').on('tap','.removeSkuButton',function(e){
                var obj = e.detail.target.id;
                var cheId = obj.split("_")[1]
                var cheDiv = $("#serskudiv_" + cheId);
                $("#" + cheId).show();
                $("#batchAddDiv").before(cheDiv)
                $("#removeBtn_" + cheId).remove();
                _this.skuInfoIds_2 = _this.skuInfoIds_2.replace(cheId, "");
            });
        },
        //查询按钮操作
        searchSkuHtml: function() {
            var _this = this;
            mui('#inAmendPoLastDaDiv').on('tap','#comChoiceBtn',function(){
                var itemNo = $("#inAmendPoLastDa").val();
                if(itemNo == null||itemNo == undefined){
					itemNo == "";
                }
                if(itemNo == ""){
                	 mui.toast("请输入商品货号！");
                	 return;
                }
                if($('#inSta').val()==""){
                	mui.toast("请选择供应商！");
                	return;
                }
                $.ajax({
                    type: "post",
                    url: "/a/biz/sku/bizSkuInfo/findSkuList",
                    data: {'productInfo.office.id': $('#inSta').val(),itemNo: itemNo},
                    success: function (result) { 
                    	var datas = JSON.parse(result).data;                                          		
                    	console.log('修改查询数据');  
                    	console.log(datas)
	                        $("#searchInfo").empty();
	                        if($.isEmptyObject(datas)){
	                        	mui.toast("此供应商下没有此类商品！");
	                        }else{
	                        $.each(datas,function (keys,skuInfoList) {
	                        	console.log(datas);
	                            var prodKeys= keys.split(",");
	                            var prodId= prodKeys[0];
	                            var prodUrl= prodKeys[2];
	                            var  brandName=prodKeys[6];
	                            var resultListHtml="";
	                            var t=0;
	                            $.each(skuInfoList,function (index,skuInfo) {
	                                console.log(skuInfo)
	                                if($("#commodityMenu").children("#serskudiv_"+skuInfo.id).length>0){
	                                    return;
	                                }
	                                resultListHtml += '<div class="mui-row app_bline" id="serskudiv_' + skuInfo.id + '">' +
	                                        '<div class="mui-row mui-checkbox mui-left">' +
	                                        '<input style="top:61px" name="" class="skuinfo_check" id="' + skuInfo.id + '" type="checkbox"></div>' +
	                                        '<div class="mui-row">' +
	                                        
	                                        '<div class="mui-row lineStyle">' +
	                                        '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
	                                        '<div class="mui-col-sm-10 mui-col-xs-10">' +
	                                        '<li class="mui-table-view-cell app_bline3">' +
	                                        '<div class="mui-input-row inputClassAdd">' +
	                                        '<label class="commodityName">商品名称:</label>' +
	                                        '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + skuInfo.name + '" disabled>' +
	                                        '</div></li></div></div>' +
	                                       
	                                      	'<div class="mui-row lineStyle">' +
	                                        '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
	                                        '<div class="mui-col-sm-10 mui-col-xs-10">' +
	                                        '<li class="mui-table-view-cell app_bline3">' +
	                                        '<div class="mui-input-row inputClassAdd">' +
	                                        '<label class="commodityName">商品货号:</label>' +
	                                        '<input type="text" class="mui-input-clear commodityTxt" id="" value="' + skuInfo.itemNo + '" disabled>' +
	                                        '</div></li></div></div>' +
	                                        
	                                        '<div class="mui-row lineStyle">' +
						                    '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
						                    '<div class="mui-col-sm-10 mui-col-xs-10">' +
						                    '<li class="mui-table-view-cell app_bline3">' +
						                    '<div class="mui-input-row ">' +
						                    '<label class="">商品编码:</label>' +
						                    '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.partNo + '" disabled>' +
						                    '</div></li></div></div>' +
	                                        
	                                        '<div class="mui-row">' +
	                                        '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
	                                        '<div class="mui-col-sm-5 mui-col-xs-5">' +
	                                        '<li class="mui-table-view-cell app_bline3">' +
	                                        '<div class="mui-input-row inputClassAdd">' +
	                                        '<label>品牌名称:</label>' +
	                                        '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.productInfo.brandName +'" disabled>' +
	                                        '</div></li></div>' +
	                                        '<div class="mui-col-sm-5 mui-col-xs-5">' +
	                                        '<li class="mui-table-view-cell app_bline3">' +
	                                        '<div class="mui-input-row inputClassAdd">' +
	                                        '<label>供应商:</label>' +
	                                        '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.productInfo.brandName + '" disabled>' +
	                                        '</div></li></div></div>' +
	                                       
	                                        '<div class="mui-row inAddFont">' +
	                                        '<div class="mui-col-sm-2 mui-col-xs-2"></div>' +
	                                        '<div class="mui-col-sm-5 mui-col-xs-5">' +
	                                        '<li class="mui-table-view-cell app_bline3">' +
	                                        '<div class="mui-input-row inputClassAdd">' +
	                                        '<label>结算价:</label>' +
	                                        '<input type="text" class="mui-input-clear" id="" value="' + skuInfo.buyPrice + '" disabled></div></li></div>' +
	                                        '<div class="mui-col-sm-5 mui-col-xs-5">' +
	                                        '<li class="mui-table-view-cell app_bline3">' +
	                                        '<div class="mui-input-row inputClassAdd">' +
	                                        '<label>申报数量:</label>' +
	                                        '<input type="hidden" class="mui-input-clear" value="' + skuInfo.id + '">' +
	                                        '<input type="text" class="mui-input-clear" placeholder="请输入数量" id="reqQty_'+ skuInfo.id +'">' +
	                                        '<font>*</font>'+
	                                        '</div></li></div></div></div></div>';
	                            });
	                            t++;                          
	                            $("#searchInfo").append(resultListHtml);
	                            //判断是否有相同的商品 
	                            var dis=$("#searchInfo .skuinfo_check");
	                            var dos=$("#commodityMenu .skuinfo_check");
	                            $.each(dis,function(n,v){
	                            	var s=$(this).attr('id')
	                            	$.each(dos,function(n,v){
	                            		var that=this;	                            	
		                            	var y=$(that).attr('id')
		                            	var divs=$("#serskudiv_"+s);
		                            	if (s==y) {
		                            		mui.toast('此供应商下已添加过此类备货商品！请选择其他商品！');
		                            		divs.html('');
		                            	}
		                            })
	                            })
	                        })
	                        var addButtonHtml = '<div class="inAddBtnParent" id="batchAddDiv">' +
	                                '<button id="batchAdd" type="submit" class="addSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">添加' +
	                                '</button></div>';
	                        $("#searchInfo").append(addButtonHtml);
	                    }
                    	                  	                    	                       
                    }
                })
            });
            //$("#searchInfo").html(htmlCommodity)
            _this.addSku()
        },
        //添加按钮操作
        addSku:function () {
            var _this = this;
            mui('#searchInfo').on('tap','.addSkuButton ',function(){
                $(".skuinfo_check").each(function () {
                    var cheId = $(this)[0].id;

                    var cheFlag = $("#" + cheId).is(':checked');
                    if (cheFlag == true) {
                        var cheDiv = $("#serskudiv_" + cheId);
                        $("#" + cheId).prop('checked',false);
                        $("#" + cheId).hide();
                        var resultHtml = '<div class="inAddBtnParent" id="removeBtn_' + cheId + '">' +
                            '<button id="remove_' + cheId +'" type="submit" class="removeSkuButton inAddBtn app_btn_search mui-btn-blue mui-btn-block">移除' +
                            '</button></div>';
                            cheDiv.append(resultHtml)
                        $("#commodityMenu").append($(cheDiv))
                        _this.skuInfoIds_2 += cheId + ",";
                        console.log(_this.skuInfoIds_2)
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
	            now = now.replace("/","-"); //  2014-7/6 07:17:43
 //               now = now.replace("-"); //  2014-7-6 07:17:43
            }
            return now;
        }
    }
    $(function() {

        var ac = new ACCOUNT();
        ac.init();
    });

    $(function() {

    });

    function getTizstatusTxt(bizstatus) {
        var desc = "";
        $.each(bizStatusDesc, function(i, item) {
            if(bizstatus == item.value){
                desc = item.label;
            }
        });
        return desc;
    }
})(Zepto);

(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.datagood = [];
		this.AreaId="";
		this.dataSupplier = [];
		this.dataarea = [];
		this.selectOpen = false;
		this.includeTestData = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.hrefHtml('.newinput', '.input_div','#hideSpanAdd','#showSpanAdd');
			this.hrefHtmls('.newinputs', '.input_divs','#hideSpanAdds','#showSpanAdds');
			this.hrefHtml1('.primaryPerson', '.input_div1','#hideAdd','#showAdd');
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
			this.pageInit(); //页面初始化
		},
		pageInit: function() {
			var _this = this;
			_this.getData();
//			_this.ajaxtypeStatus();//机构类型			
			_this.ajaxgradeStatus();//机构级别
            _this.ajaxyesnoStatus();//是否可用
            _this.ajaxcreditStatus();//钱包账户
//          _this.tanchuangShow();//添加新负责人            
		},
		getData: function() {
			var _this = this;			
			if(this.userInfo.option=='upgrade'){
				id=_this.userInfo.inListId;
				option=this.userInfo.option
			}
			if(this.userInfo.option=='upgradeAudit'){
				id=_this.userInfo.reqId;
				option=this.userInfo.option
			}
			$.ajax({
                type: "GET",
                url: "/a/sys/office/purchasersForm4Mobile",
                data: {id:id,option:option},
                dataType: "json",
                success: function(res){
//              	console.log(res)
                	var btnMeun = '';
			        if(res.data.option == 'upgrade') {
			        	$('.inSaveBtns').hide();
			        	$('#changeHeader').html('申请');
			        	btnMeun = '<button id="payMentBtn" type="submit" class="app_btn_search mui-btn-blue mui-btn-block">确认申请</button>'
			        	$('.inSaveBtn').html(btnMeun);
			        	
			        }else if(res.data.option == 'upgradeAudit') {
			        	$('.inSaveBtn').hide();
			        	$('#changeHeader').html('审核');
			        	btnMeun = '<div id="staOrdCheckBtn" class="app_p20 app_pl25 app_pr25 checkBtn">'+
						    '<button id="checkBtns" type="submit" class="mui-btn mui-btn-blue passBtn">'+
						         '审核通过'+
						    '</button>'+
							'<button id="rejectBtns" type="submit" class="mui-btn mui-btn-blue app_fr RejectBtn">'+
							    '审核驳回'+
							'</button>'+
						'</div>'
						
						$('.inSaveBtns').html(btnMeun);
			        }
			        if(res.data.option == 'upgrade') {
			        	_this.apply(res.data.office.id,res.data.option);
			        }else if(res.data.option == 'upgradeAudit') {
                        _this.comfirDialig(res.data);//审核
			        }			        
			        $('.newinput').val(res.data.office.parent.name);//上级机构
			        $('.newinput').attr('parentId',res.data.office.parent.id);//上级机构id
			        $('.newinputs').val(res.data.office.area.name);//归属区域
//			        $('.newinputs').attr('areaId',res.data.office.area.id);//归属区域id
                    _this.AreaId = res.data.office.area.id;
			        $('#memName').val(res.data.office.name);//机构名称
			        $('#memCode').val(res.data.office.code);//机构编码
			        //机构类型
	                var typestatus = res.data.office.type;
//                  $('#typeDiv option[value="' + typestatus + '"]').attr("selected",true);
                    $.ajax({
						type: 'GET',
						url: '/a/sys/dict/listData',
						data: {type:'sys_office_type'},
						dataType: 'json',
						async:false,
						success: function(res) {
							$.each(res,function(i,items){
								console.log(items)
	                        	if(typestatus==items.value) {
	                        		$('#typeDiv').val(items.label);
	                        		$('#typeDiv').attr("officeid",items.value);
	                        	}
                           })					        
						}
					});
                    if(res.data.option == 'upgradeAudit' || res.data.option == 'upgrade'){
                    	$('#type').show();
                    	if(res.data.option == 'upgradeAudit'){
                            _this.ajaxtypesStatus(res.data.office.commonProcess.type);//申请机构类型                           
                    	}
                    	if(res.data.option == 'upgrade'){
                    		if(typestatus == 15){  
                    			_this.ajaxtypesStatu(16);//申请机构类型 
                    		}else{
                    			_this.ajaxtypesStatu(6);//申请机构类型 
                    		}
                    	}
                    }
                    if(res.data.office.bizCustomerInfo){
                    	$('#cardNumber').val(res.data.office.bizCustomerInfo.cardNumber);//代销商/经销商卡号
	                    $('#bankName').val(res.data.office.bizCustomerInfo.bankName);//代销商/经销商开户行
	                    $('#payee').val(res.data.office.bizCustomerInfo.payee);//代销商/经销商收款人
	                    $('#idCardNumber').val(res.data.office.bizCustomerInfo.idCardNumber);//代销商/经销商收款人身份证号
                    }
                    //机构级别
                    var gradestatus = res.data.office.grade;
                    $('#officeGrade  option[value="' + gradestatus + '"]').attr("selected",true);
                    //是否可用
                    var yesnostatus = res.data.office.useable;
                    $('#yesNo  option[value="' + yesnostatus + '"]').attr("selected",true);
                    //钱包账户
                    var Levelstatus = res.data.office.level;
                    $('#creditLevel  option[value="' + Levelstatus + '"]').attr("selected",true);                   
                    $('.primaryPerson').val(res.data.office.primaryPerson.name);//主负责人
                    $('.primaryPerson').attr('valid',res.data.office.primaryPerson.id);//主负责人
                    _this.ajaxprimaryPerson(res.data.office.id);//主负责人
                    if(res.data.office.deputyPerson.name){
                    	$('.deputyPerson').val(res.data.office.deputyPerson.name);//副负责人
                    }else{
                    	$('.deputyPerson').val();
                    }
                    $('#memaddress').val(res.data.office.address);//联系地址
                    $('#zipCode').val(res.data.office.zipCode);//邮政编码
                    $('#master').val(res.data.office.master);//负责人
                    $('#phone').val(res.data.office.phone);//电话
                    $('#fax').val(res.data.office.fax);//传真
                    $('#email').val(res.data.office.email);//邮箱
                    $('#remarks').val(res.data.office.remarks);//备注
                    _this.statusListHtml(res.data.processList);//升级申请记录                   
                }
            })    	
		},
		comfirDialig: function(data) {
			var _this = this;
			var id=data.office.id;
			var applyForLevel=data.office.commonProcess.type;
			document.getElementById("rejectBtns").addEventListener('tap', function() {
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
									_this.rejectData(rejectTxt,2,id,applyForLevel)
								}
							} else {}
						})
					} else {}
				})
			});
			document.getElementById("checkBtns").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); 
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
									_this.ajaxData(inText,1,id,applyForLevel)
								} else {}
							})
						}
					} else {}
				})
			});
		},
		//审核通过
		ajaxData: function(inText,num,id,applyForLevel) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/sys/office/upgradeAudit4Mobile",
				data: {
					id: id,
					applyForLevel: applyForLevel,
					auditType: num,
					desc: inText
				},
				dataType: "json",
				success: function(res) {
					if(res==true||res=='true') {
						mui.toast('审核成功！');
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
								url: "../../html/memberMgmtHtml/memberList.html",
								extras: {}
							})
			            },300);						
					}
					if(res == false||res == 'false') {
						mui.toast('审核失败！');
					}
				},
				error: function(e) {
					//服务器响应失败处理函数
				}
			});

		},
		rejectData: function(rejectTxt,num,id,applyForLevel) {
			var _this = this;
			$.ajax({
				type: "GET",
				url: "/a/sys/office/upgradeAudit4Mobile",
				data: {
					id: id,
					applyForLevel: applyForLevel,
					auditType: num,
					desc: rejectTxt
				},
				dataType: "json",
				success: function(res) {
					if(res == true||res == 'true') {
						mui.toast('驳回成功！');
						window.setTimeout(function(){
			                GHUTILS.OPENPAGE({
								url: "../../html/memberMgmtHtml/memberList.html",
								extras: {}
							})
			            },300);
					}
					if(res == false||res == 'false') {
						mui.toast('驳回失败!');
					}
				}
			});
		},
//		tanchuangShow:function() {
//			var _this = this;
//          document.getElementById("AddBtn").addEventListener('tap', function(e) {
//				e.detail.gesture.preventDefault(); 			
//				var btnArray = ['关闭', '保存'];
//				var choiceTxt = '';
//				var hint = '';
//					choiceTxt = '<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
//								'<div class="mui-input-row">' +
//									'<label>姓名:</label>' +
//									'<input type="text" class="mui-input-clear" value="" id="saveName">' +
//								'</div>' +
//								'<div class="mui-input-row">' +
//									'<label>登录名:</label>' +
//									'<input type="text" class="mui-input-clear" value="" id="saveloginName">' +
//								'</div>' +
//								'<div class="mui-input-row">' +
//									'<label>密码:</label>' +
//									'<input type="text" class="mui-input-clear" value="" id="savePassword">' +
//								'</div>' +
//								'<div class="mui-input-row">' +
//									'<label>确认密码:</label>' +
//									'<input type="text" class="mui-input-clear" value="" id="savePasswordsure">' +
//								'</div>' +
//								'<div class="mui-input-row">' +
//									'<label>手机:</label>' +
//									'<input type="text" class="mui-input-clear" value="" id="savePhone">' +
//								'</div>' +
//							   '</div>'
//					hint = '负责人添加：'
//              mui.confirm(choiceTxt, hint, btnArray, function(choice) {
//					if(choice.index == 1) {
////						_this.ajaxData(15)
//					} else {						
//					}
//				})
//				
//			});
//      },
        apply: function(id,option) {
        	var _this = this;
        	$('#payMentBtn').on('tap',function(){
	            var Payee=$('#payee').val();
	            var bizTypeVal = $('#typeDiv').attr("officeid"); //机构类型
	            console.log(bizTypeVal)
	            var bizTypesVal = $("#typeDivs")[0].value; //申请机构类型
	            console.log(bizTypesVal)
	            if($('.newinput').val()==''||$('.newinput').val()==null){
	            	mui.toast("上级机构为必填项！");
				    return;
	            }
	            if(_this.AreaId==''||_this.AreaId==null||_this.AreaId==0){
	            	mui.toast("归属区域为必填项！");
				    return;
	            }
	            if(bizTypeVal==''||bizTypeVal==null){
	            	mui.toast("机构类型为必填项！");
				    return;
	            }
	            if(bizTypesVal==''||bizTypesVal==null){
	            	mui.toast("申请机构类型为必填项！");
				    return;
	            }
	            if(Payee==null||Payee==''){
	            	mui.toast("代销商/经销商收款人为必填项！");
				    return;
	            }
				$.ajax({
                    type: "get", 
                    url: "/a/sys/office/purchaserSave4Mobile",
                    data: {
                    	option:option,
                    	id:id,
                    	'parent.id':$('.newinput').attr('parentId'), 
                    	'area.id':_this.AreaId, 
                    	name: $('#memName').val(), 
                    	type: bizTypeVal, 
                    	'commonProcess.type': bizTypesVal, 
                    	'bizCustomerInfo.payee': Payee
                    },
                    dataType: 'json',
                    success: function (resule) {
                    	console.log(resule)
                        if (resule== true || resule == 'true') {
                            mui.toast("申请成功！");
                            window.setTimeout(function(){
			                    GHUTILS.OPENPAGE({
	                                url: "../../html/memberMgmtHtml/memberList.html",
	                                extras: {	   
	                                }
	                            })
			                },300);
                          	
                        }
                    }
                })
	        })
        },
        //状态流程
		statusListHtml:function(data){
			var _this = this;
			var statusLen = data.length;
			var ass=[];
			if(statusLen > 0) {
				var pHtmlList = '';				
				var officeType ='';
				//申请等级
				$.ajax({
					type: 'GET',
					url: '/a/sys/dict/listData',
					data: {type:'sys_office_type'},
					dataType: 'json',
					async:false,
					success: function(res) {
						ass=res	
					}
				});
				var Result ='';				
				var Description ='';
				var userName ='';
				var createTime ='';
				var updateTime ='';
				$.each(data, function(i, item) {
                    $.each(ass,function(i,items){
                    	if(item.type==items.value) {                   		
                    		officeType = items.label
                    	}
                    })
                    Result=item.bizStatus == 1 ? '通过' : (item.bizStatus == 2 ? '驳回' : '未审核');
                    if(item.description){
                    	Description=item.description;
                    }else{
                    	Description='';
                    }
                    if(item.user.name){
                    	userName=item.user.name;
                    }else{
                    	userName='';
                    }
                    if(item.createTime){
                    	createTime=_this.formatDateTime(item.createTime);
                    }else{
                    	createTime='';
                    }
                    if(item.bizStatus == 1){
                    	if(item.updateTime){
	                    	updateTime=_this.formatDateTime(item.updateTime);
	                    }else{
	                    	updateTime='';
	                    }
                    }                    
					var step = i + 1;
					pHtmlList +='<li class="step_item">'+
						'<div class="step_num">'+ step +' </div>'+
						'<div class="step_num_txt">'+
							'<div class="mui-input-row">'+
								'<label>申请等级:</label>'+
								'<input type="text" value="'+ officeType +'" class="mui-input-clear" disabled>'+
						    '</div>'+
						    '<div class="mui-input-row">'+
								'<label>审批结果:</label>'+
								'<input type="text" value="'+ Result +'" class="mui-input-clear" disabled>'+
						    '</div>'+
						    '<div class="mui-input-row">'+
								'<label>批注:</label>'+
								'<input type="text" value="'+ Description +'" class="mui-input-clear" disabled>'+
						    '</div>'+
						    '<div class="mui-input-row">'+
								'<label>审批人:</label>'+
								'<input type="text" value="'+ userName +'" class="mui-input-clear" disabled>'+
						    '</div>'+
							'<div class="mui-input-row">'+
						    	'<label>申请时间:</label>'+
						        '<input type="text" value=" '+ createTime +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						    '<div class="mui-input-row">'+
						    	'<label>审批时间:</label>'+
						        '<input type="text" value=" '+ updateTime +' " class="mui-input-clear" disabled>'+
						    '</div>'+
						'</div>'+
					'</li>'
				});
				$("#staEvoMenu").html(pHtmlList)
			}else{
				$("#staEvoMenu").parent().hide();
			}
		},
		hrefHtml: function(newinput, input_div,hideSpanAdd,showSpanAdd) {
			var _this = this;
			_this.ajaxSupplier();//经销店名称
			$(newinput).on('focus', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show();
				$(hideSpanAdd).show();
				$(showSpanAdd).hide();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false;
				}else{
					_this.selectOpen = true;
				}				
				_this.rendHtml(_this.dataSupplier,$(this).val());
			})
			$(showSpanAdd).on('click', function() {
				$(showSpanAdd).hide();
				$(input_div).show();
				$(hideSpanAdd).show();
			})
			$(hideSpanAdd).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid');
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid');
				$(newinput).val($(this).text());
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
				_this.selectOpen = true;
			})
		},
		hrefHtmls: function(newinput, input_div,hideSpanAdd,showSpanAdd) {
			var _this = this;
			_this.ajaxarea();//归属区域
			$(newinput).on('focus', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show();
				$(hideSpanAdd).show();
				$(showSpanAdd).hide();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false;
				}else{
					_this.selectOpen = true;
				}				
				_this.rendHtmls(_this.dataarea,$(this).val());
			})
			$(showSpanAdd).on('click', function() {
				$(showSpanAdd).hide();
				$(input_div).show();
				$(hideSpanAdd).show();
			})
			$(hideSpanAdd).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid');
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid');
				$(newinput).val($(this).text());
				_this.AreaId = $(this).attr("id");
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
				_this.selectOpen = true;
			})
		},
		hrefHtml1: function(newinput, input_div,hideSpanAdd,showSpanAdd) {
			var _this = this;
			
			$(newinput).on('focus', function() {
				$(input_div).find('hasoid').removeClass('hasoid')
				$(input_div).show();
				$(hideSpanAdd).show();
				$(showSpanAdd).hide();
			})
			$(newinput).on('keyup', function() {
				if($(this).val()==''){
					_this.selectOpen = false;
				}else{
					_this.selectOpen = true;
				}				
				_this.rendHtml1(_this.dataarea,$(this).val());
			})
			$(showSpanAdd).on('click', function() {
				$(showSpanAdd).hide();
				$(input_div).show();
				$(hideSpanAdd).show();
			})
			$(hideSpanAdd).on('click', function() {
				$(input_div).find('hasoid').removeClass('hasoid');
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
			})

			$(input_div).on('click', '.soption', function() {
				$(this).addClass('hasoid').siblings().removeClass('hasoid');
				$(newinput).val($(this).text());
				$(input_div).hide();
				$(hideSpanAdd).hide();
				$(showSpanAdd).show();
				_this.selectOpen = true;
			})
		},
		rendHtml: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reult.push(item);
					}
				})
			$.each(reult, function(i, item) {
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_div').html(htmlList);

		},
		rendHtmls: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reult.push(item);
					}
				})
			$.each(reult, function(i, item) {
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.input_divs').html(htmlList);

		},
		rendHtml1: function(data, key) {
			var _this = this;
			var reult = [];
			var htmlList=''
				$.each(data, function(i, item) {
					if(item.name.indexOf(key) > -1) {
						reult.push(item);
					}
				})
			$.each(reult, function(i, item) {
				htmlList += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '">' + item.name + '</span>'
			});
			$('.primaryPerson').html(htmlList);

		},
		//经销店名称
		ajaxSupplier: function() {
			var _this = this;
			var htmlSupplier = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/office/queryTreeListByPhone',
				data: {
					type: 6
				},
				dataType: 'json',
				success: function(res) {
					_this.dataSupplier = res;
					$.each(res, function(i, item) {
						htmlSupplier += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
					});
					$('.input_div').html(htmlSupplier);				
				}
			});
		},
		//归属区域
		ajaxarea: function() {
			var _this = this;
			var htmlSupplier = ''
			$.ajax({
				type: 'GET',
				url: '/a/sys/area/treeData',
//				data: {
//					type: 6
//				},
				dataType: 'json',
				success: function(res) {
					_this.dataarea = res;
					$.each(res, function(i, item) {
						htmlSupplier += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
					});
					$('.input_divs').html(htmlSupplier);				
				}
			});
		},
		//机构类型
//		ajaxtypeStatus: function() {
//			var _this = this;
////			var optHtml ='<option value="">请选择</option>';
//			var htmlOrdstatus = '';
//			$.ajax({
//				type: 'GET',
//				url: '/a/sys/dict/listData',
//				data: {type:'sys_office_type'},
//				dataType: 'json',
//				success: function(res) {
//					$.each(res, function(i, item) {
//						htmlOrdstatus += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
//					});
//					$('#typeDiv').html(htmlOrdstatus);
//				}
//			});
//		},
		//申请机构类型
		ajaxtypesStatus: function(type) {
			var _this = this;
//			var optHtml ='<option value="">请选择</option>';
			var htmlOrdstatus = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'sys_office_type'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						if(type==item.value) {
		                    htmlOrdstatus += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
		               }			         
					});
					$('#typeDivs').html(htmlOrdstatus);	
                    $('#typeDivs  option[value="' + type + '"]').attr("selected",true);
				}
			});
		},
		ajaxtypesStatu: function(type) {
			var _this = this;
//			var optHtml ='<option value="">请选择</option>';
			var htmlOrdstatus = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'sys_office_type'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						if(type==16){
							htmlOrdstatus = '<option class="soption" value="' + type + '">' + '代销商' + '</option>'
						}
		                if(type==6){
							htmlOrdstatus = '<option class="soption" value="' + type + '">' + '经销店' + '</option>'
						}
					});
					$('#typeDivs').html(htmlOrdstatus);	
				}
			});
		},
		//机构级别
		ajaxgradeStatus: function() {
			var _this = this;
//			var optHtml ='<option value="">请选择</option>';
			var htmlChstatus = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'sys_office_grade'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlChstatus += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#officeGrade').html(htmlChstatus);
				}
			});
		},
		//是否可用
		ajaxyesnoStatus: function() {
			var _this = this;
//			var optHtml ='<option value="">请选择</option>';
			var htmlChstatus = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'yes_no'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlChstatus += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#yesNo').html(htmlChstatus);
				}
			});
		},
		//钱包账户
		ajaxcreditStatus: function() {
			var _this = this;
//			var optHtml ='<option value="">请选择</option>';
			var htmlChstatus = '';
			$.ajax({
				type: 'GET',
				url: '/a/sys/dict/listData',
				data: {type:'biz_cust_credit_level'},
				dataType: 'json',
				success: function(res) {
					$.each(res, function(i, item) {
						htmlChstatus += '<option class="soption" value="' + item.value + '">' + item.label + '</option>'
					});
					$('#creditLevel').html(htmlChstatus);
				}
			});
		},
		//主负责人
		ajaxprimaryPerson: function(officeId) {
			var _this = this;
			var htmlSupplier = ''
			$.ajax({
				type: 'GET',
				url: '/a//sys/user/treeData',
				data: {
					type: 6,
					officeId:officeId
				},
				dataType: 'json',
				success: function(res) {
					_this.dataprimaryPerson = res;
					$.each(res, function(i, item) {
						htmlSupplier += '<span class="soption" pId="' + item.pId + '" id="' + item.id + '" type="' + item.type + '" pIds="' + item.pIds + '" name="' + item.name + '">' + item.name + '</span>'
					});
					$('.input_div1').html(htmlSupplier);				
				}
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
},	
	$(function() {
		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.prew = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			//this.radioShow()
//			this.btnshow()
//			this.searchShow()
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
					//GHUTILS.nativeUI.showWaiting()//开启
		},
		pageInit: function() {
			var _this = this;
			_this.getData()
			_this.comfirDialig()
		},
		getData: function() {
			var _this = this;
			var url = $(this).attr('url');
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/form4Mobile",
				data: {id:_this.userInfo.listId},
				dataType: "json",
				success: function(res) {
					console.log(res)
					
					$("#addCheckBtn").html(pHtmlList)
					$('#OrordNum').val(res.data.bizOrderHeader.orderNumber)
					$('#PoordNum').val(res.data.bizPoHeader.orderNumber)
					$('#Pototal').val(res.data.bizPoHeader.total)
					$('#PotoDel').val(res.data.bizPoHeader.totalDetail)
					$('#PoLastDa').val(res.data.bizPoHeader.lastPayDate)
					$('#PoRemark').val(res.data.bizPoHeader.remark)
					$('#PoDizstatus').val(res.data.bizPoHeader.bizStatus)
					$('#PoVenName').val(res.data.bizPoHeader.vendOffice.name)
					$('#PoVenBizCard').val(res.data.bizPoHeader.vendOffice.bizVendInfo.cardNumber)
					$('#PoVenBizPayee').val(res.data.bizPoHeader.vendOffice.bizVendInfo.payee)
					$('#PoVenBizBankname').val(res.data.bizPoHeader.vendOffice.bizVendInfo.bankName)
					$('#codeId').val(res.data.bizPoHeader.process.purchaseOrderProcess.code)
					
					
//                  if(res.data.bizOrderHeaderTest){
//                 	   $('#PoDizstatus').val(res.data.bizPoHeader.bizStatus)
//	                   $('#PoVenName').val(res.data.bizPoHeader.vendOffice.name)
//	                   $('#PoVenBizCard').val(res.data.bizPoHeader.vendOffice.bizVendInfo.cardNumber)
//	                   $('#PoVenBizPayee').val(res.data.bizPoHeader.vendOffice.bizVendInfo.payee)
//	                   $('#PoVenBizBankname').val(res.data.bizPoHeader.vendOffice.bizVendInfo.bankName)
//                   }else{
//                  		$('#img').hide()//show()
//                  		$('.priceList').hide()//show()
//                  	}
				}
			});
//			_this.herfHTtml()
//			getFormatDate(timestamp)
		},
		comfirDialig: function() {
			var _this = this;
			var rejectBtn = document.getElementById("rejectBtn");
			document.getElementById("rejectBtn").addEventListener('tap', function() {
				var btnArray = ['否', '是'];
				mui.confirm('确认驳回审核吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						
						var btnArray = ['取消', '确定'];
						mui.prompt('请输入驳回理由：', '驳回理由', '', btnArray, function(a) {
							if(a.index == 1) {
								var rejectTxt = a.value;
								console.log(rejectTxt)
								if(a.value=='') {
									mui.toast('驳货理由不能为空！')
								}else {
									_this.rejectData(rejectTxt,2)
								}
							} else {
								//		            info.innerText = '你点了取消按钮';
							}
						})
						//		            info.innerText = '你刚确认MUI是个好框架';
					} else {
						//		            info.innerText = 'MUI没有得到你的认可，继续加油'
					}
				})
			});
			document.getElementById("checkBtn").addEventListener('tap', function(e) {
				e.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
				var btnArray = ['取消', '确定'];
				mui.prompt('请输入通过理由：', '通过理由', '', btnArray, function(e) {
					if(e.index == 1) {
						var inText = e.value;
						if(e.value=='') {
							mui.toast('通过理由不能为空！')
							return;
						}else {
							var btnArray = ['否', '是'];
							mui.confirm('确认通过审核吗？', '系统提示！', btnArray, function(choice) {
							if(choice.index == 1) {
								console.log(inText)
								_this.ajaxData(inText,1)
							} else {
								//		            info.innerText = '你点了取消按钮';
							}
						})
						}

						//		            info.innerText = '你刚确认MUI是个好框架';
					} else {
						//		            info.innerText = 'MUI没有得到你的认可，继续加油'
					}
				})
			});
//			alert("操作成功")
		},
		ajaxData:function(inText,num) {
			var _this = this;
			var codeId = $(this).attr('codeId');
			//$('#mask').show()
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/audit",
				data: {
					id:_this.userInfo.listId,
					currentType:$('#codeId').val(),//流程code
					auditType:num,
					description:inText
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
					if(res.ret==true){
						//$('#mask').hide()
						GHUTILS.OPENPAGE({
						url: "../../mobile/html/purchase.html",
						extras: {
							key:res.key,
							}
						})
					}
					
				}
			});
			
		},
		rejectData:function(rejectTxt,num) {
			var _this = this;
			var codeId = $(this).attr('codeId');
			//$('#mask').show()
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/audit",
				data: {
					id:_this.userInfo.listId,
					currentType:$('#codeId').val(),//流程code
					auditType:num,
					description:inText
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
					if(res.ret==true){
						//$('#mask').hide()
						GHUTILS.OPENPAGE({
						url: "../../mobile/html/purchase.html",
						extras: {
							key:res.key,
							}
						})
					}
					
				}
			});
			
		}
		//			var data = _this.getData()
//			console.log(data)
//			$('#ordNum').val(data.bizOrderHeader.orderNumber)
//			if(data.bizOrderHeader.orderNumber){
//				
//			}
//			$.each(data.bizOrderHeader.orderNumber, function(i, item) {
//						console.log(item.orderNumber)
//                       var orderNumber = item.orderNumber;
//					$('#ordNum').html(orderNumber)
////                        htmlList += 
//			});
////		                    $('#htmlMenu').html(htmlList)
//		}
							
		
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);







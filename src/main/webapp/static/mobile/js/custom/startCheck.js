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
//			this.radioShow()
			this.btnshow()
			this.searchShow()
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
				mui.confirm('确认驳回流程吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						
						var btnArray = ['取消', '确定'];
						mui.prompt('请输入驳回理由：', '驳回理由', '', btnArray, function(a) {
							if(a.index == 1) {
								var rejectTxt = a.value;
								console.log(rejectTxt)
								if(a.value=='') {
									mui.toast('驳回理由不能为空！')
									return;
								}else {
//									_this.rejectData(rejectTxt)
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
							mui.confirm('确认开启审核吗？', '系统提示！', btnArray, function(choice) {
								if(choice.index == 1) {
									console.log(inText)
									if(_this.prew){
										_this.ajaxPoPayData(inText)
									}else{
										_this.ajaxData(inText)
									}
									
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
		},
		ajaxData:function(inText) {
			var _this = this;
			//$('#mask').show()
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/startAudit",
				data: {
					id:_this.userInfo.listId,
					prew:_this.prew,
					prewPayTotal:$('#totalMoney').val(),
					prewPayDeadline: _this.dataNew($('#Date').val()),
					desc:inText
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
//					if(res.ret==true){
//						//$('#mask').hide()
//						GHUTILS.OPENPAGE({
//						url: "../../mobile/html/purchase.html",
//						extras: {
//							key:res.key,
//							}
//						})
//					}
					
				}
			});
			
		},
		ajaxPoPayData:function(inText) {
			var _this = this;
			//$('#mask').show()
			$.ajax({
				type: "GET",
				url: "/a/biz/po/bizPoHeader/auditPay",
				data: {
					id:_this.userInfo.listId,
					time:_this.dataNew($('#totalMoney').val()),//
					currentType:_this.userInfo.codeId
					money: _this.dataNew($('#Date').val()),
					auditType:1,
					desc:inText
				},
				dataType: "json",
				success: function(res) {
					console.log(res)
//					if(res.ret==true){
//						//$('#mask').hide()
//						GHUTILS.OPENPAGE({
//						url: "../../mobile/html/purchase.html",
//						extras: {
//							key:res.key,
//							}
//						})
//					}
					
				}
			});
			
		},

		btnshow: function() {
			var _this = this;
			$('#showMoney').hide()
			$('input[type=radio]').on('change', function() {
				if(this.id && this.checked) {
					$('#showMoney').show()
					_this.prew  = true
				} else {
					$('#showMoney').hide()
					_this.prew  = false
				}
			})
		},
		searchShow: function() {
			var _this = this;
			$('#searchIfrom').hide()
			$('#showDress').on('tap','#adressBtn', function() {
				$('#searchIfrom').show()
			})
			$('#closeBtn').on('tap', function() {
				$('#searchIfrom').hide()
			})
		},
		dataNew:function(str){
			var _this = this;
			Data = str.replace(/-/g,'/');
			var date = new Date(Data);
			var time = date.getTime();
			return time
		}
		
/*		searchFromShow: function() {
			var mask = mui.createMask(callback);//callback为用户点击蒙版时自动执行的回调；
			mask.show();//显示遮罩
			mask.close();//关闭遮罩
		}*/
		
/*		function getFormatDate(timestamp) {
		  timestamp = parseInt(timestamp + '000');
		  var newDate = new Date(timestamp);
		  Date.prototype.format = function (format) {
		    var date = {
		      'M+': this.getMonth() + 1,
		      'd+': this.getDate(),
		      'h+': this.getHours(),
		      'm+': this.getMinutes(),
		      's+': this.getSeconds(),
		      'q+': Math.floor((this.getMonth() + 3) / 3),
		      'S+': this.getMilliseconds()
		    };
		    if (/(y+)/i.test(format)) {
		      format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
		    }
		    for (var k in date) {
		      if (new RegExp('(' + k + ')').test(format)) {
		        format = format.replace(RegExp.$1, RegExp.$1.length == 1
		        ? date[k] : ('00' + date[k]).substr(('' + date[k]).length));
		      }
		    }
		    return format;
		  }
		  return newDate.format('yyyy-MM-dd h:m');
		}*/ 

	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);
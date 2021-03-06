(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.editFlag = false;
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.pageInit(); //页面初始化
			this.getData();//获取数据		
			this.getPermissionList('sys:dict:commissionRatioSave:edit','editFlag')//保存权限
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
                    _this.editFlag = res.data;
                }
            });
        },
        getData: function() {
        	var _this = this;
        	$.ajax({
                type: "GET",
                url: "/a/sys/dict/commissionRatioView4Mobile",
                data:{},
                dataType: "json",
                success: function(res){
//              	console.log(res)
                	$('#commNum').val(res.data.dict.value);
                	$("#commNum").focus(function(){
					  	$("#commNum").css({"border":"1px solid #5CC6DD","box-shadow": "rgb(92, 198, 221) 0px 0px 5px"});
					});
					$("#commNum").blur(function(){
					  	$("#commNum").css({"border":"1px solid #ccc","box-shadow": "rgb(92, 198, 221) 0px 0px 0px"});
					});
//              	console.log(_this.editFlag)
                	if(_this.editFlag == true) {
                		_this.comfirDialig(res.data)
                	}else {
                		$('#proSaveBtn').parent().hide();
                	}
                }
            })
        },
        comfirDialig: function(param) {
        	var _this = this;
        	document.getElementById("proSaveBtn").addEventListener('tap', function(e) {
				var btnArray = ['取消', '确定'];
				mui.confirm('确定修改佣金比例吗？', '系统提示！', btnArray, function(choice) {
					if(choice.index == 1) {
						_this.saveDetail(param)
					} else {}
				})
			});
        },
        saveDetail: function(param) {
        	var _this = this;
        	var dict = param.dict;
//      	console.log(dict)
        	var value = $('#commNum').val();
        	
    		$.ajax({
                type: "GET",
                url: "/a/sys/dict/commissionRatioSave",
                data:{
                	id: dict.id,/*字典id*/
					type: dict.type,/*字典类型*/
					value: value/*佣金比例值*/
                },
                dataType: "json",
                success: function(result){
                	if(result.data.left == true || result.data.left == 'true') {
                		mui.toast('修改完成！')
                		window.setTimeout(function(){
                        	GHUTILS.OPENPAGE({
                                url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commProportion.html",
                                extras: {
                                }
                            })
                        },500)
                	}else{
                		mui.toast('修改佣金比例失败！')
                		window.setTimeout(function(){
                        	GHUTILS.OPENPAGE({
                                url: "../../../html/orderMgmtHtml/commissionMgmtHtml/commProportion.html",
                                extras: {
                                }
                            })
                        },500)
                	}
                }
            })
        }
	}
	$(function() {

		var ac = new ACCOUNT();
		ac.init();
	});
})(Zepto);

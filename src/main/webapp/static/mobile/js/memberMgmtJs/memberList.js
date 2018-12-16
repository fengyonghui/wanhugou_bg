
(function($) {
	var ACCOUNT = function() {
		this.ws = null;
		this.userInfo = GHUTILS.parseUrlParam(window.location.href);
		this.expTipNum = 0;
		this.applyFlag = "false";
		this.checkFlag = "false";
		return this;
	}
	ACCOUNT.prototype = {
		init: function() {
			this.getPermissionList1('sys:office:upgrade','applyFlag');//申请
			this.getPermissionList2('sys:office:upgradeAudit','checkFlag')			
			this.pageInit(); //页面初始化
			GHUTILS.nativeUI.closeWaiting(); //关闭等待状态
			//GHUTILS.nativeUI.showWaiting()//开启
		},		
		pageInit: function() {
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
			                var f = document.getElementById("memberList");
			                var childs = f.childNodes;
			                for(var i = childs.length - 1; i >= 0; i--) {
			                    f.removeChild(childs[i]);
			                }			                
			                $('.mui-pull-caption-down').html('');
			                if(_this.userInfo.isFunc){
			                	if(_this.userInfo.id==undefined){
								    _this.userInfo.id="";
								}
								if(_this.userInfo.phone==undefined){
									_this.userInfo.phone="";
								}
		                	    //查询过来传的参数
		                    	pager['size']= 20;
		                    	pager['pageNo'] = 1;
		                    	pager['id'] = _this.userInfo.id;//经销店id
		                    	pager['moblieMoeny.mobile'] = _this.userInfo.phone;//电话	                    	
		                    	getData(pager);
		                    }else{
		                    	//直接进来的参数数据
		                    	pager['size']= 20;
			                    pager['pageNo'] = 1;			                    
			                    pager['id'] = 7;	
			                    getData(pager);
		                    }				                
			            }
			        }
			    })
		    }
		    function getData(params){
		    	var inPHtmlList = '';
		    	var ass=[];
		        mui.ajax("/a/sys/office/purchasersList4Mobile",{
		            data:params,               
		            dataType:'json',
		            type:'get',
		            headers:{'Content-Type':'application/json'},
		            success:function(res){
		            	var dataRow = res.data.roleSet;
		            	//机构类型
		          	    $.ajax({
			                type: "GET",
			                url: "/a/sys/dict/listData",
			                dataType: "json",
			                data: {type: "sys_office_type"},
			                async:false,
			                success: function(res){ 
						        ass=res;
			                }
			            });
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
						var arrLen = res.data.page.list.length;
						if(arrLen <20){
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
						}else{
							mui('#refreshContainer').pullRefresh().endPulldownToRefresh(true);
							mui('#refreshContainer').pullRefresh().endPullupToRefresh(true);
						}
                        var that=this;
                        if(arrLen > 0) {
                        	var orgName="";
                        	var orgPhone="";
                        	var orgNamePhone="";
                        	var orgType="";                       	
                            $.each(res.data.page.list, function(i, item) {
//                          	console.log(item)
                            	if(item.vendor != 'vendor'){
                            		orgName=item.name;
                            	}
                            	if(item.vendor == 'vendor'){
                            		orgName=item.name;
                            	}
                            	if(item.vendor != 'vendor'){
                            		if(item.phone){
                            			orgPhone=item.phone;
                            		}
                            		if(item.moblieMoeny){
                            			orgNamePhone=item.moblieMoeny.mobile;
                            		}                           		
                            	}
                            	//机构类型
	                            $.each(ass,function(i,items){
		                        	if(item.type==items.value) {
		                        		orgType = items.label
		                        	}
	                            })	
	                            //申请、审核
	                            var ClassBtn = '';
	                        	var BtnTxt = '';
	                        	if(item.type==15 || item.type==16){
	                        		if(item.commonProcess.type==null){
	                        			if(_this.applyFlag==true){
	                        				BtnTxt="申请";
	                        				ClassBtn="ApplyBtn";
	                        			}
	                        		}
	                        		if(item.commonProcess.type!=null && item.commonProcess.type!=0 && item.commonProcess.type!=item.type){
	                        			if(_this.checkFlag==true){
	                        				BtnTxt="审核";
	                        				ClassBtn="CheckBtn";
	                        			}
	                        		}
	                        	}
								inPHtmlList +='<div class="ctn_show_row app_li_text_center app_bline app_li_text_linhg mui-input-group">'+
								'<div class="mui-input-row">' +
									'<label>机构名称:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+orgName+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>归属区域:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.area.name+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>机构编码:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+item.code+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>电话:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ orgPhone +' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>联系人电话:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+ orgNamePhone +' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>机构类型:</label>' +
									'<input type="text" class="mui-input-clear" disabled="disabled" value=" '+orgType+' ">' +
								'</div>' +
								'<div class="mui-input-row">' +
									'<label>备注:</label>' +
									'<input type="text" style="overflow-x:hidden;" class="mui-input-clear" disabled="disabled" value=" '+item.remarks+' ">' +
								'</div>' +
								'<div class="app_color40 mui-row app_text_center content_part operation">' +
									'<div class="mui-col-xs-12 '+ClassBtn+'" inListId="'+ item.id +'">' +
										'<li class="mui-table-view-cell">'+BtnTxt+'</li>' +
									'</div>' +
								'</div>' +
							   '</div>'

							});
							$('#memberList').append(inPHtmlList);
							_this.inHrefHtml();								
						}else{
							$('.mui-pull-bottom-pocket').html('');
							$('#memberList').append('<p class="noneTxt">暂无数据</p>');
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
		getPermissionList1: function (markVal,flag) {
            var _this = this;
            $.ajax({
                type: "GET",
                url: "/a/sys/menu/permissionList",
                dataType: "json",
                data: {"marking": markVal},
                async:false,
                success: function(res){
                    _this.applyFlag = res.data;
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
                    _this.checkFlag = res.data;
                }
            });
        },
        ordHrefHtml: function() {
        	var _this = this;
        	/*查询*/
			$('.header').on('tap', '#membSearchBtn', function() {
				var url = $(this).attr('url');
				if(url) {
					mui.toast('子菜单不存在')
				} else {
					GHUTILS.OPENPAGE({
						url: "../../html/memberMgmtHtml/meSearch.html",
						extras:{
						}
						
					})
				}					
			})
		    /*机构添加*/
			$('#nav').on('tap','.membAddBtn', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/memberMgmtHtml/mem.html",
					extras: {
						
					}
				})
			})
		    /*首页*/
			$('#nav').on('tap','.membHomePage', function() {
				var url = $(this).attr('url');
				GHUTILS.OPENPAGE({
					url: "../../html/backstagemgmt.html",
					extras: {
						
					}
				})
			})
        },
		inHrefHtml: function() {
			var _this = this;		
		    /*申请*/
			$('.content_part').on('tap', '.ApplyBtn', function() {
				var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				if(url) {
					mui.toast('子菜单不存在')
				} else if(inListId == inListId) {
					GHUTILS.OPENPAGE({
						url: "../../html/memberMgmtHtml/memberApply.html",
						extras: {
							inListId: inListId,
							option:'upgrade'
						}
					})
				}
			}),
		    /*审核*/
            $('.content_part').on('tap','.CheckBtn', function() {
				var url = $(this).attr('url');
                var reqId = $(this).attr('inListId');
				GHUTILS.OPENPAGE({
					url: "../../html/memberMgmtHtml/memberApply.html",
					extras: {
                        reqId: reqId,
                        option:'upgradeAudit'
					}
				})
			}),
           /*添加下级机构*/
	       $('.listBlue').on('tap', '.membAddBtn', function() {
					var url = $(this).attr('url');
					var inListId = $(this).attr('inListId');
					if(url) {
						mui.toast('子菜单不存在')
					} else if(inListId == inListId) {
						GHUTILS.OPENPAGE({
							url: "../../html/inventoryMagmetHtml/inPay.html",
							extras: {
								inListId: inListId,
							}
						})
					}
				}),
           /*沟通记录*/
            $('.listBlue').on('tap','.membRecordBtn',function(){
            	var url = $(this).attr('url');
				var inListId = $(this).attr('inListId');
				var bizStatus = $(this).attr('bizStatus');
                if(url) {
                	mui.toast('子菜单不存在')
                }else if(inListId==inListId) {
                	GHUTILS.OPENPAGE({
						url: "../../html/inventoryMagmetHtml/inCheck.html",
						extras: {
								inListId:inListId,
								bizStatus:bizStatus,
						}
					})
                }
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

<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page import="com.wanhutong.backend.modules.enums.DefaultPropEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>产品信息表管理</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/treeview.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            var tree="";
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    var ids = [], nodes = tree.getCheckedNodes(true);
                    for (var i = 0; i < nodes.length; i++) {
                        if (!nodes[i].isParent) {
                            ids.push(nodes[i].id);
                        }
                    }

                    $("#cateIds").val(ids);

                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });

            if ($("#id").val() != '') {
                var ids = "${entity.cateIds}";//后台获取的分类id集合
               var  brandId=$("#propValueId").val();
                ajaxFindCateByBrand(brandId);

                var cateValueId = $("#cateValueId").val();
             //   ajaxGetPropInfoBrand(ids);
                t = setTimeout(function () {
                    ajaxGetPropInfo(ids);

                }, 100);
                t = setTimeout(function () {
                    ajaxGetProdOwnPropInfo($("#id").val());
                }, 100);


            }
            $('.select_all').live('click', function () {
                var obj = $(this).attr("id");
                var choose = $(".value_" + obj);
                if ($(this).attr('checked')) {
                    choose.attr('checked', true);
                } else {
                    choose.attr('checked', false);
                }
            });










            <%--// 分类--菜单--%>
            <%--var zNodes = [--%>
                    <%--<c:forEach items="${cateList}" var="cate">{--%>
                    <%--id: "${cate.id}",--%>
                    <%--pId: "${not empty cate.parent.id?cate.parent.id:0}",--%>
                    <%--name: "${not empty cate.parent.id?cate.name:'分类列表'}"--%>
                <%--},--%>
                <%--</c:forEach>];--%>
            <%--// 初始化树结构--%>
            <%--var tree = $.fn.zTree.init($("#cateTree"), setting, zNodes);--%>
            <%--// 不选择父节点--%>
            <%--tree.setting.check.chkboxType = {"Y": "ps", "N": "ps"};--%>
            <%--// 默认选择节点--%>
            <%--var ids = "${entity.cateIds}".split(",");--%>
            <%--for (var i = 0; i < ids.length; i++) {--%>
                <%--var node = tree.getNodeByParam("id", ids[i]);--%>
                <%--try {--%>
                    <%--tree.checkNode(node, true, false);--%>
                    <%--tree.checkNode(node.getParentNode(), true, false);--%>
                <%--} catch (e) {--%>
                <%--}--%>
            <%--}--%>
            <%--// 默认展开全部节点--%>
            <%--tree.expandAll(true);--%>

            var i = 0;
            $("#addPropValue").click(function () {
                if ($("#prodPropValueList" + i).val() == '') {
                    alert('属性不能为空');
                    return false;
                }
                i++;
                $("#propValues").append("<input type='text' id='prodPropValueList" + i + "' name=\"prodPropValueList[" + i + "].value\"  maxlength=\"512\" class=\"input-small\"/>")
            });

            /**
             * 弹出框保存
             */
            $("#but_sub").click(function () {
                var propName = $("#propName").val();
                //  var propDescription=$("#propDescription").val();
                var propValues = $("#propValues input");
                if (propName != '') {
                    $("#ownPropInfo").append("<input title='" + str[a] + "' style='margin-bottom: 10px' onblur='cancelValue($(this),\"" + str[a] + "\");' class='input-mini' type='text' name='propNames' value='" + propName + "'/>:<input type='hidden'  name='propNames' value='" + str[a] + "_'/>");
                    propValues.each(function () {
                        var v = $(this).val();
                        if (v != "") {
                            $("#ownPropInfo").append("<input style='width: 60px;margin-bottom: 10px' class='" + str[a] + "' type='text' name='propOwnValues' value='" + v + "'/><input type='hidden'  name='propOwnValues' value='" + str[a] + "_'>")
                        }
                    })
                    $("#ownPropInfo").append("<button id='id_" + str[a] + "' onclick='addPropValueInfo(\"" + str[a] + "\")'  type=\"button\" class=\"btn btn-default\"><span class=\"icon-plus\"></span></button><br/>");
                }
                a++;
                $('#myModal').modal('hide');
            });

        $("#propValueId").change(function () {
            var brandId=$(this).val();
            ajaxFindCateByBrand(brandId);
        });

            function ajaxFindCateByBrand(brandId) {
                $.post("${ctx}/biz/category/bizCategoryInfo/findListByBrandId",
                    {brandId: brandId, ranNum: Math.random()},
                    function (data, status) {
                        treeNodes = data;   //把后台封装好的简单Json格式赋给treeNodes
                        var setting = {
                            check: {enable: true, nocheckInherit: true}, view: {selectedMulti: false},
                            data: {simpleData: {enable: true}}, callback: {
                                beforeClick: function (id, node) {
                                    tree.checkNode(node, !node.checked, true, true);
                                    return false;
                                }, onCheck: zTreeOnCheck
                            }
                        };
                        tree= $.fn.zTree.init($("#cateTree"), setting, treeNodes);
                        // 不选择父节点
                        tree.setting.check.chkboxType = {"Y": "ps", "N": "ps"};

                        //ztree 复选框操作控制函数
                        function zTreeOnCheck(event, treeId, treeNode) {
                            var ids = [], nodes = tree.getCheckedNodes(true);
                            for (var i = 0; i < nodes.length; i++) {
                                if (!nodes[i].isParent) {
                                    ids.push(nodes[i].id);
                                }
                            }

                            ajaxGetPropInfo(ids);


                        };

                        // 默认选择节点
                        var ids = "${entity.cateIds}".split(",");
                        for (var i = 0; i < ids.length; i++) {
                            var node = tree.getNodeByParam("id", ids[i]);
                            try {
                                tree.checkNode(node, true, false);
                                tree.checkNode(node.getParentNode(), true, false);
                            } catch (e) {
                            }
                        }
                        // 默认展开全部节点
                        tree.expandAll(true);
                    });

            }

        });






        function addPropValueInfo(va) {
            var t = $("input[title=" + va + "]").val();
            if (t == '') {
                alert("属性不能为空");
                return;
            }
            var flag = true;
            $("." + va).each(function (pv) {
                if ($(this).val() == '') {
                    flag = false;
                    return false;
                }
            });
            if (!flag) {
                alert("属性值不能为空");
                return false;
            } else {
                $("#id_" + va).before("<input class='" + va + "' style=\"width: 60px;margin-bottom: 10px\" type=\"text\" name=\"propOwnValues\" /><input type=\"hidden\" name=\"propOwnValues\" value=" + va + "_>")
            }

        }

        /**
         * 通过分类获取分类属性
         * @param ids
         */
        function ajaxGetPropInfo(ids) {
            $.post("${ctx}/biz/product/bizProdCate/findCatePropInfoMap",
                {catIds: ids.toString()},
                function (data, status) {
                    $("#cateProp").empty();
                    $.each(data, function (keys, values) {
                        var propKeys = keys.split(",");
                        var propId = propKeys[0];
                        if (propId != $("#brandDefId").val()) {
                            var propName = propKeys[1]
                            $("#cateProp").append('<input class="select_all" id="' + propId + '" name="prodPropertyInfos" type="checkbox" value="' + propId + '" />' + propName + ':<span id="span_' + propId + '"/><br/>')
                            for (var p in values) {
                                if (values[p].value != null) {
                                    $("#span_" + propId).append('<input id="value_' + values[p].propertyValueId + '" class="value_' + propId + '" name="propertyMap[' + propId + '].prodPropertyValues" type="checkbox" value="' + values[p].propertyValueId + '" />' + values[p].value + '')
                                }
                            }
                        }

                    });
                    if ($("#id").val() != '' && status == 'success') {
                        ajaxGetProdPropInfo($("#id").val());
                    }


                })
        }

        function ajaxGetProdPropInfo(prodId) {
            $.post("${ctx}/biz/product/bizProdPropertyInfo/findProdPropertyList",
                {prodId: prodId, ranNum: Math.random()},
                function (data, status) {
                    $.each(data, function (index, prodPropertyInfo) {
                        $.each(prodPropertyInfo.prodPropValueList, function (index, prodPropValue) {
                            console.log("prodPropValue===" + prodPropValue);
                            $("#" + prodPropValue.propertyInfoId).attr('checked', true)
                            $("#value_" + prodPropValue.propertyValueId).attr('checked', true)
                        });
                    });
                });
        }

        var a = 0;
        var str = ["a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n"];

        function ajaxGetProdOwnPropInfo(prodId) {
            $.post("${ctx}/biz/product/bizProdPropValue/findList",
                {prodId: prodId, source: "prod", ranNum: Math.random()},
                function (data, status) {
                    $.each(data, function (keys, values) {
                        var propKeys = keys.split(",");
                        var propName = propKeys[0];
                        var propDesc = propKeys[1]
                        $("#ownPropInfo").append("<input title='" + str[a] + "' style='margin-bottom: 10px' onblur='cancelValue($(this),\"" + str[a] + "\");' class='input-mini' type='text' name='propNames' value='" + propName + "'/>:<input type='hidden'  name='propNames' value='" + str[a] + "_'/>");
                        for (var p in values) {
                            $("#ownPropInfo").append("<input style='width: 60px;margin-bottom: 10px' class='" + str[a] + "' type='text' name='propOwnValues' value='" + values[p].propValue + "'/><input type='hidden'  name='propOwnValues' value='" + str[a] + "_'>")


                        }
                        $("#ownPropInfo").append("<button id='id_" + str[a] + "' onclick='addPropValueInfo(\"" + str[a] + "\")'  type=\"button\" class=\"btn btn-default\"><span class=\"icon-plus\"></span></button><br/>");
                        a++;
                    });
                });
        };

        function cancelValue(obj, k) {
            if (obj.val() == '') {
                obj.parent().children("." + k).remove();
            }

        }


    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/biz/product/bizProductInfo/">产品信息表列表</a></li>
    <li class="active"><a
            href="${ctx}/biz/product/bizProductInfo/form?id=${bizProductInfo.id}">产品信息表<shiro:hasPermission
            name="product:bizProductInfo:edit">${not empty bizProductInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="biz:product:bizProductInfo:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<%--@elvariable id="bizProductInfo" type="com.wanhutong.backend.modules.biz.entity.product.BizProductInfo"--%>
<form:form id="inputForm" modelAttribute="bizProductInfo" action="${ctx}/biz/product/bizProductInfo/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <%--<input type="hidden" id="cateValueId" value="${bizProductInfo.catePropValue.id}"/>--%>
    <input type="hidden" id="brandDefId" value="${DefaultPropEnum.PROPBRAND.getPropValue()}"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">产品名称：</label>
        <div class="controls">
            <form:input path="name" htmlEscape="false" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">请选择品牌:</label>
        <div class="controls">
            <%--<form:select items="${propValueList}" id="propValueId" path="propValue.id" class="input-xlarge required" itemLabel="value" itemValue="id">--%>
                <%--<from:option value="">请选择</from:option>--%>
            <%--</form:select>--%>
            <form:select path="propValue.id" class="input-xlarge required" id="propValueId">
                <form:option value="" label="请选择品牌"/>
                <form:options items="${propValueList}" itemLabel="value" itemValue="id" htmlEscape="false"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">上市时间：</label>
        <div class="controls">
            <input name="marketingDate" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
                   value="<fmt:formatDate value="${entity.marketingDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品主图:</label>
        <div class="controls">
            <form:hidden id="prodMaxImg" path="photos" htmlEscape="false" maxlength="255" class="input-xlarge"/>
            <sys:ckfinder input="prodMaxImg" type="images" uploadPath="/prod/main" selectMultiple="true" maxWidth="100"
                          maxHeight="100"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品列表图:</label>
        <div class="controls">
            <form:hidden id="prodListImg" path="photoLists" htmlEscape="false" maxlength="255" class="input-xlarge"/>
            <sys:ckfinder input="prodListImg" type="images" uploadPath="/prod/item" selectMultiple="true" maxWidth="100"
                          maxHeight="100"/>
        </div>
    </div>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">产品代码：</label>--%>
        <%--<div class="controls">--%>
            <%--<form:input path="prodCode" htmlEscape="false" maxlength="10" class="input-xlarge required"/>--%>
            <%--<span class="help-inline"><font color="red">*</font> </span>--%>
        <%--</div>--%>
    <%--</div>--%>
    <div class="control-group">
        <label class="control-label">产品描述：</label>
        <div class="controls">
            <form:textarea path="description" htmlEscape="false" class="input-xlarge "/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">产品详情图片:</label>
        <div class="controls">
            <form:hidden id="prodDetailImg" path="photoDetails" htmlEscape="false" maxlength="255"
                         class="input-xlarge"/>
            <sys:ckfinder input="prodDetailImg" type="images" uploadPath="/prod/detail" selectMultiple="true"
                          maxWidth="100" maxHeight="100"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">请选择供应商：</label>
        <div class="controls">
            <sys:treeselect id="office" name="office.id" value="${entity.office.id}" labelName="office.name"
                            labelValue="${entity.office.name}" notAllowSelectRoot="true" notAllowSelectParent="true"
                            title="供应商" url="/sys/office/queryTreeList?type=7" extId="${office.id}"
                            cssClass="input-xlarge required"
                            allowClear="${office.currentUser.admin}" dataMsgRequired="必填信息"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">请选择产品标签：</label>
        <div class="controls">
            <form:select path="bizVarietyInfo.id" class="input-medium required">
                <form:option value="" label="请选择"/>
                <form:options items="${varietyInfoList}" itemLabel="name" itemValue="id" htmlEscape="false"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">请选择产品标签：</label>
        <div class="controls">
            <div id="cateTree" class="ztree" style="margin-top:3px;float:left;"></div>
            <form:hidden path="cateIds"/>
                <%--<span class="help-inline"><font color="red">*</font> </span>--%>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">产品属性：</label>
        <div id="cateProp" class="controls">
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">增加特有属性：</label>
        <div class="controls">
					<span id="ownPropInfo" style="margin-bottom: 10px">
					</span>
            <button data-toggle="modal" data-target="#myModal" type="button" class="btn btn-default">
                <span class="icon-plus"></span>
            </button>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="biz:product:bizProductInfo:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                           type="submit"
                                                                           value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
<!-- 模态框（Modal） -->
<div class="modal fade hide" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                        aria-hidden="true">×
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    属性与属性值
                </h4>
            </div>
            <%--&lt;%&ndash;@elvariable id="prodPropertyInfo" type="com.wanhutong.backend.modules.biz.entity.product.BizProdPropertyInfo"&ndash;%&gt;--%>
            <%--<form:form id="inputForm2" modelAttribute="prodPropertyInfo" action="${ctx}/biz/product/bizProdPropertyInfo/savePropInfo" method="post" class="form-horizontal">--%>

            <div class="modal-body">
                <div class="control-group">
                    <label class="control-label">属性名称：</label>
                    <div class="controls">
                        <input type="text" id="propName" maxlength="30" class="input-xlarge required"/>
                        <span class="help-inline"><font color="red">*</font> </span>
                    </div>
                </div>
                <%--<div class="control-group">--%>
                <%--<label class="control-label">属性描述：</label>--%>
                <%--<div class="controls">--%>
                <%--<input type="text" id="propDescription" maxlength="200" class="input-xlarge required"/>--%>
                <%--</div>--%>
                <%--</div>--%>
                <div class="control-group">
                    <label class="control-label">属性值：</label>
                    <div class="controls">
						<span id="propValues">
							<input id="prodPropValueList0" type="text" maxlength="512" class="input-small"/>
						</span>
                        <button id="addPropValue" type="button" class="btn btn-default">
                            <span class="icon-plus"></span>
                        </button>
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default"
                        data-dismiss="modal">关闭
                </button>
                <shiro:hasPermission name="biz:category:bizCatePropertyInfo:edit">
                    <button id="but_sub" type="button" class="btn btn-primary">
                        保存
                    </button>
                </shiro:hasPermission>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <%--<th>商品产品Id</th>--%>
        <th>商品图片</th>
        <th>商品名称</th>
        <th>商品类型</th>
        <th>商品编码</th>
        <th>基础售价</th>
        <th>采购价格</th>
        <th>更新人</th>
        <shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${entity.skuInfosList}" var="bizSkuInfo">
        <tr>
                <%--<td><a href="${ctx}/biz/product/bizProductInfo/form?id=${bizSkuInfo.id}">
                        ${bizSkuInfo.id}</a>
                </td>--%>
            <td>
                <img src="${bizSkuInfo.defaultImgThumbUrl}" width="120px" height="120px"/>
            </td>
            <td><a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}">
                    ${bizSkuInfo.name}
            </a>
            </td>
            <td>
                    ${fns:getDictLabel(bizSkuInfo.skuType, 'skuType', '未知类型')}
            </td>
            <td>
                    ${bizSkuInfo.partNo}
            </td>
            <td>
                    ${bizSkuInfo.basePrice}
            </td>
            <td>
                    ${bizSkuInfo.buyPrice}
            </td>
            <td>
                    ${bizSkuInfo.updateBy.name}
            </td>
            <shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
                <td>
                    <a href="${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}">修改</a>
                    <a href="${ctx}/biz/sku/bizSkuInfo/delete?id=${bizSkuInfo.id}&sign=1"
                       onclick="return confirmx('确认要删除该商品sku吗？', this.href)">删除</a>
                </td>
            </shiro:hasPermission>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="form-actions">
    <c:if test="${entity.id !=null && entity.id!='' }">
        <shiro:hasPermission name="biz:sku:bizSkuInfo:edit">
            <c:choose>
                <c:when test="${entity.skuInfosList.size()==0}">
                    <input type="button" onclick="javascript:window.location.href='${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}&productInfo.id=${bizProductInfo.id}&sort=01';" class="btn btn-primary" value="商品信息添加"/>
                </c:when>
                <c:otherwise>
                    <input type="button" onclick="javascript:window.location.href='${ctx}/biz/sku/bizSkuInfo/form?id=${bizSkuInfo.id}&productInfo.id=${bizProductInfo.id}&sort=0${entity.skuInfosList.size()+1}';" class="btn btn-primary" value="商品信息添加"/>
                </c:otherwise>
            </c:choose>


        </shiro:hasPermission>


    </c:if>
</div>


</body>
</html>
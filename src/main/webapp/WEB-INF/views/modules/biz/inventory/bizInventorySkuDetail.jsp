<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>库存详情</title>
    <meta name="decorator" content="default"/>
</head>
<body>
    <form:form id="inputForm" modelAttribute="bizCollectGoodsRecord" action="" method="post" class="form-horizontal">
        <div class="control-group">
            <label class="control-label">采购中心：</label>
            <div class="controls">
                    ${bcgrList[0].centName}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">仓库：</label>
            <div class="controls">
                    ${bcgrList[0].invInfo.name}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">商品名称：</label>
            <div class="controls">
                    ${bcgrList[0].skuInfo.name}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">商品货号：</label>
            <div class="controls">
                    ${bcgrList[0].skuInfo.itemNo}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">颜色：</label>
            <div class="controls">
                    ${bcgrList[0].skuInfo.color}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">尺寸：</label>
            <div class="controls">
                    ${bcgrList[0].skuInfo.size}
            </div>
        </div>
        <!-- 隐藏结算价 -->
        <shiro:hasPermission name="biz:order:unitPrice:view">
            <div class="control-group">
                <label class="control-label">结算价：</label>
                <div class="controls">
                        ${bcgrList[0].skuInfo.buyPrice}
                </div>
            </div>
        </shiro:hasPermission>
            <div class="control-group">
            <label class="control-label">图片：</label>
            <div class="controls">
                    <img src="${bcgrList[0].skuInfo.skuImgUrl}" width="80px" height="80px"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">供应商：</label>
            <div class="controls">
                    ${bcgrList[0].vendorName}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">总库存数：</label>
            <div class="controls">
                    ${bcgrList[0].inventorySku.stockQty}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">出库数量：</label>
            <div class="controls">
                    ${outWarehouse}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">供货部发货数量：</label>
            <div class="controls">
                    ${sendGoodsNum}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">入库数量：</label>
            <div class="controls">
                    ${inWarehouse}
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">入库信息：</label>
            <div class="controls">
            <table id="contentTable" class="table table-striped table-bordered table-condensed">
                <thead>
                    <tr>
                        <th>备货单号</th>
                        <th>库存类型</th>
                        <th>入库数量</th>
                        <th>已出库数量</th>
                        <th>入库时间</th>
                        <th>入库时长(天)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:set var="nowDate" value="<%=System.currentTimeMillis()%>"></c:set>
                    <c:forEach items="${bcgrList}" var="bcgr">
                        <tr>
                            <td>${bcgr.requestHeader.reqNo}</td>
                            <td>${fns:getDictLabel(bcgr.requestHeader.headerType, 'req_header_type', '未知')}</td>
                            <td>${bcgr.receiveNum}</td>
                            <td>${bcgr.outQty == null ? 0 : bcgr.outQty}</td>
                            <td>
                                <fmt:formatDate value="${bcgr.receiveDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            </td>
                            <td>
                                <fmt:formatNumber value="${(nowDate - bcgr.receiveDate.getTime())/1000/60/60/24}" type="number" pattern="#"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </div>
        </div>
        <div class="form-actions">
            <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
        </div>
    </form:form>
</body>
</html>
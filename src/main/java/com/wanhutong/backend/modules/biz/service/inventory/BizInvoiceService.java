/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import com.wanhutong.backend.common.config.Global;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DsConfig;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInvoiceDao;
import com.wanhutong.backend.modules.biz.entity.common.CommonImg;
import com.wanhutong.backend.modules.biz.entity.inventory.*;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.common.CommonImgService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.po.BizPoDetailService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.*;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.AliOssClientUtil;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 发货单Service
 *
 * @author 张腾飞
 * @version 2018-03-05
 */
@Service
@Transactional(readOnly = true)
public class BizInvoiceService extends CrudService<BizInvoiceDao, BizInvoice> {

    @Autowired
    private BizSendGoodsRecordService bizSendGoodsRecordService;
    @Autowired
    private CommonImgService commonImgService;
    @Autowired
    private BizDetailInvoiceService bizDetailInvoiceService;
    @Autowired
    private OfficeService officeService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;
    @Autowired
    private BizInvoiceDao bizInvoiceDao;
    @Autowired
    private BizPoDetailService bizPoDetailService;
    @Autowired
    private BizPoOrderReqService bizPoOrderReqService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizRequestHeaderService bizRequestHeaderService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizInventorySkuService bizInventorySkuService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizPoHeaderService bizPoHeaderService;
    @Autowired
    private BizInventoryInfoService bizInventoryInfoService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;

    protected Logger log = LoggerFactory.getLogger(getClass());//日志

    @Override
    public BizInvoice get(Integer id) {
        return super.get(id);
    }

    @Override
    public List<BizInvoice> findList(BizInvoice bizInvoice) {
        return super.findList(bizInvoice);
    }

    @Override
    public Page<BizInvoice> findPage(Page<BizInvoice> page, BizInvoice bizInvoice) {
        User user = UserUtils.getUser();
        if (user.isAdmin()) {
            return super.findPage(page, bizInvoice);
        }
//        else {
//            bizInvoice.getSqlMap().put("bizInvoice", BaseService.dataScopeFilter(user, "so", ""));
//        }
        return super.findPage(page, bizInvoice);
    }

    @Transactional(readOnly = false)
    public void save(BizInvoice bizInvoice) {
        boolean flagRequest = true;        //备货单完成状态
        boolean flagOrder = true;        //销售单完成状态
        boolean flagPo = true;     //采购单完成状态
        boolean flagInvoceId = bizInvoice.getId() == null ? true : false;
        //修改发货单
        if (bizInvoice.getId() != null) {
            BizInvoice invoice = bizInvoiceDao.get(bizInvoice.getId());
            invoice.setTrackingNumber(bizInvoice.getTrackingNumber());
            invoice.setFreight(bizInvoice.getFreight());
            invoice.setCarrier(bizInvoice.getCarrier());
            invoice.setSettlementStatus(bizInvoice.getSettlementStatus());
            invoice.setSendDate(bizInvoice.getSendDate());
            invoice.setRemarks(bizInvoice.getRemarks());
            invoice.setInspector(bizInvoice.getInspector());
            invoice.setInspectDate(bizInvoice.getInspectDate());
            invoice.setInspectRemark(bizInvoice.getInspectRemark());
            invoice.setCollLocate(bizInvoice.getCollLocate());
            invoice.setSendDate(bizInvoice.getSendDate());
            invoice.setIsConfirm(bizInvoice.getIsConfirm());
            if ("audit".equals(bizInvoice.getStr())) {
                invoice.setCarrier(UserUtils.getUser().getName());
            }
            bizInvoice.setIsConfirm(BizInvoice.IsConfirm.YES.getIsConfirm());
            invoice.setIsConfirm(BizInvoice.IsConfirm.YES.getIsConfirm());
            bizInvoiceDao.update(invoice);
            //保存图片
//            saveCommonImg(bizInvoice);
        } else {
            // 取出当前用户所在机构，
            User user = UserUtils.getUser();
            Office company = officeService.get(user.getCompany().getId());
            //采购商或采购中心
//        Office office = officeService.get(bizSendGoodsRecord.getCustomer().getId());
            bizInvoice.setSendNumber("");
            bizInvoice.setIsConfirm(bizInvoice.getIsConfirm());
            super.save(bizInvoice);
            bizInvoice.setSendNumber(GenerateOrderUtils.getSendNumber(OrderTypeEnum.SE, company.getId(), 0, bizInvoice.getId()));
            super.save(bizInvoice);
            //保存图片
//            saveCommonImg(bizInvoice);
        }
        //获取订单ID
        String orderHeaders = bizInvoice.getOrderHeaders();
        //获取备货单ID
        String requestHeaders = bizInvoice.getRequestHeaders();

        //货值
        Double valuePrice = 0.0;
//        List<BizRequestHeader> requestHeaderList = bizInvoice.getRequestHeaderList();
        if (StringUtils.isNotBlank(orderHeaders)) {
            Integer ordId = 0;
            String[] orders = orderHeaders.split(",");
            for (int a = 0; a < orders.length; a++) {
                boolean ordFlag = true;
                String[] oheaders = orders[a].split("#");
                BizOrderHeader orderHeader = bizOrderHeaderService.get(Integer.parseInt(oheaders[0]));
                //加入中间表关联关系
                if (flagInvoceId && !ordId.equals(orderHeader.getId())) {
                    BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                    bizDetailInvoice.setInvoice(bizInvoice);
                    bizDetailInvoice.setOrderHeader(orderHeader);
                    bizDetailInvoiceService.save(bizDetailInvoice);
                }
                ordId = orderHeader.getId();
                String[] odNumArr = oheaders[1].split("\\*");
                for (int i = 0; i < odNumArr.length; i++) {
                    String[] odArr = odNumArr[i].split("-");
                    BizOrderDetail orderDetail = bizOrderDetailService.get(Integer.parseInt(odArr[0]));
                    //商品
                    BizSkuInfo bizSkuInfo = bizSkuInfoService.get(orderDetail.getSkuInfo().getId());
                    int sendNum = Integer.parseInt(odArr[1]);    //供货数
                    valuePrice += bizSkuInfo.getBuyPrice() * sendNum;//累计货值
                    //采购商
                    Office office = officeService.get(orderHeader.getCustomer().getId());
                    int sentQty = orderDetail.getSentQty();    //销售单累计供货数量
                    //当供货数量和申报数量不相等时，更改销售单状态
                    if (orderDetail.getOrdQty() != (sentQty + sendNum)) {
                        flagOrder = false;
                    }
                    if (sendNum == 0) {
                        continue;
                    }
                    //采购中心订单发货
                    if (bizInvoice.getBizStatus() == 0) {
                        //销售单状态改为采购中心供货（16）
                        orderHeader.setBizStatus(OrderHeaderBizStatusEnum.APPROVE.getState());
                        bizOrderHeaderService.saveOrderHeader(orderHeader);
                        //获取库存数
                        BizInventorySku bizInventorySku = new BizInventorySku();
                        bizInventorySku.setSkuInfo(bizSkuInfo);
                        if (odArr.length == 4) {
                            BizInventoryInfo inventoryInfo = bizInventoryInfoService.get(Integer.parseInt(odArr[2]));
                            bizInventorySku.setInvInfo(inventoryInfo);
                            bizInventorySku.setSkuType(Integer.parseInt(odArr[3]));
                            if (InventorySkuTypeEnum.VENDOR_TYPE.getType().equals(Integer.parseInt(odArr[3]))) {
                                bizInventorySku.setVendor(bizSkuInfo.getProductInfo().getOffice());
                            }
                        }
                        bizInventorySku.setInvType(InvSkuTypeEnum.CONVENTIONAL.getState());
                        List<BizInventorySku> list = bizInventorySkuService.findList(bizInventorySku);
                        int stock = 0;
                        //发货之前库存数
                        int invOldNum = 0;
                        //没有库存，
                        if (list == null || list.size() == 0 || list.get(0).getStockQty() == 0) {
                            /*bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
                            bizOrderHeaderService.saveOrderHeader(bizOrderHeader);*/
                            flagOrder = false;
                        } else {
                            //有库存
                            for (BizInventorySku invSku : list) {
                                stock = invSku.getStockQty();
                                invOldNum = stock;
                                //如果库存不够，
                                if (stock < orderDetail.getOrdQty()) {
                                    /*bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
                                    bizOrderHeaderService.saveOrderHeader(bizOrderHeader);*/
                                    flagOrder = false;
                                    if (sendNum > stock) {
                                        sendNum = stock;
                                    }
                                    invSku.setStockQty(stock - sendNum);
                                    bizInventorySkuService.save(invSku);
                                } else {
                                    invSku.setStockQty(stock - sendNum);
                                    bizInventorySkuService.save(invSku);
                                }
                            }
                            //累计已供数量
                            orderDetail.setSentQty(sentQty + sendNum);
                            bizOrderDetailService.saveStatus(orderDetail);
                            //生成供货记录表
                            BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                            bsgr.setSendNum(sendNum);
                            if (odArr.length == 4) {
                                BizInventoryInfo inventoryInfo = bizInventoryInfoService.get(Integer.parseInt(odArr[2]));
                                bsgr.setInvInfo(inventoryInfo);
                            }
                            bsgr.setInvOldNum(invOldNum);
                            bsgr.setCustomer(office);
                            bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());
                            bsgr.setOrderNum(orderHeader.getOrderNum());
                            bsgr.setBizOrderHeader(orderHeader);
                            bsgr.setSkuInfo(bizSkuInfo);
                            bsgr.setSendDate(bizInvoice.getSendDate());
                            bizSendGoodsRecordService.save(bsgr);
                        }
                    }
                    //供应商或供货部发货
                    if (bizInvoice.getBizStatus() == 1) {
                        //获取销售单相应的采购单详情,累计采购单单个商品的供货数
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        BizPoHeader poHeader = null;
                        BizPoDetail bizPoDetail = new BizPoDetail();
                        BizPoDetail poDetail = null;
                        BizOrderDetail bizOrdDetail = new BizOrderDetail();
                        bizOrdDetail.setSkuInfo(bizSkuInfo);
                        bizPoOrderReq.setOrderDetail(bizOrdDetail);
                        bizPoOrderReq.setOrderHeader(orderHeader);
                        bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
                        List<BizPoOrderReq> bizPoOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                        if (bizPoOrderReqList != null && bizPoOrderReqList.size() > 0) {
                            poHeader = bizPoOrderReqList.get(0).getPoHeader();
                        }
                        bizPoDetail.setPoHeader(poHeader);
                        bizPoDetail.setSkuInfo(bizSkuInfo);
                        List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);
                        if (bizPoDetailList != null && bizPoDetailList.size() > 0) {
                            poDetail = bizPoDetailList.get(0);
                        }
                        if (poDetail.getSendQty() + sendNum != poDetail.getOrdQty()) {
                            flagPo = false;
                        }
                        poDetail.setSendQty(poDetail.getSendQty() + sendNum);
                        bizPoDetailService.save(poDetail);
                        //累计已供数量
                        orderDetail.setSentQty(sentQty + sendNum);
                        bizOrderDetailService.saveStatus(orderDetail);
                        //修改订单状态为供应商发货（19）
                        orderHeader.setBizStatus(OrderHeaderBizStatusEnum.STOCKING.getState());
                        bizOrderHeaderService.saveOrderHeader(orderHeader);
                        //生成供货记录
                        BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                        bsgr.setSendNum(sendNum);
                        bsgr.setCustomer(office);
                        bsgr.setOrderNum(orderHeader.getOrderNum());
                        bsgr.setBizOrderHeader(orderHeader);
                        bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
                        bsgr.setSkuInfo(bizSkuInfo);
                        bsgr.setSendDate(bizInvoice.getSendDate());
                        bizSendGoodsRecordService.save(bsgr);
                    }
                }
                /*用于 订单状态表 保存状态*/
                bizOrderStatusService.saveOrderStatus(orderHeader);
                BizOrderDetail ordDetail = new BizOrderDetail();
                ordDetail.setOrderHeader(orderHeader);
                List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(ordDetail);
                for (BizOrderDetail orderDetail : orderDetailList) {
                    if (!orderDetail.getOrdQty().equals(orderDetail.getSentQty())) {
                        ordFlag = false;
                    }
                }

                //销售单完成时，更该销售单状态为已供货（20）
                if (ordFlag) {
                    orderHeader.setBizStatus(OrderHeaderBizStatusEnum.SEND.getState());
                    bizOrderHeaderService.saveOrderHeader(orderHeader);
                    if (bizInvoice.getBizStatus() == 0) {
                        BizOrderDetail bizOrderDetail = new BizOrderDetail();
                        bizOrderDetail.setOrderHeader(orderHeader);
                        List<BizOrderDetail> ordDetailList = bizOrderDetailService.findList(bizOrderDetail);
                        for (BizOrderDetail orderDetail : ordDetailList) {
                            BizSkuInfo skuInfo = orderDetail.getSkuInfo();
                            BizInventoryInfo inventoryInfo = orderDetail.getInventoryInfo();
                            BizInventorySku bizInventorySku = new BizInventorySku();
                            bizInventorySku.setSkuInfo(skuInfo);
                            bizInventorySku.setInvInfo(inventoryInfo);
                            List<BizInventorySku> invSkuList = bizInventorySkuService.findList(bizInventorySku);
                            if (invSkuList != null && invSkuList.size() > 0) {
                                BizInventorySku inventorySku = invSkuList.get(0);
                                inventorySku.setStockOrdQty(inventorySku.getStockOrdQty() + 1);
                                bizInventorySkuService.save(inventorySku);
                            }
                        }
                    }
                    /*用于 订单状态表 保存状态*/
                    bizOrderStatusService.saveOrderStatus(orderHeader);
                }

                //当供货部或供应商发货时，才涉及采购单状态
                if (bizInvoice.getBizStatus() == 1) {
                    //更改采购单状态,已完成（5）
                    if (flagPo) {
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        BizPoOrderReq por = null;
                        bizPoOrderReq.setOrderHeader(orderHeader);
                        List<BizPoOrderReq> porList = bizPoOrderReqService.findList(bizPoOrderReq);
                        if (porList != null && porList.size() > 0) {
                            por = porList.get(0);
                        }
                        BizPoHeader poHeader = por.getPoHeader();
                        //获取该采购单的所有采购详情，如果所有的供货数都和采购数相等，则更改采购单状态为完成
                        BizPoHeader bizPoHeader = bizPoHeaderService.get(poHeader.getId());
                        BizPoDetail poDetail = new BizPoDetail();
                        poDetail.setPoHeader(bizPoHeader);
                        List<BizPoDetail> poDetailList = bizPoDetailService.findList(poDetail);
                        boolean flag = true;
                        for (BizPoDetail bizPoDetail : poDetailList) {
                            if (!bizPoDetail.getOrdQty().equals(bizPoDetail.getSendQty())) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            Byte bizStatus = bizPoHeader.getBizStatus();
                            int status = PoHeaderStatusEnum.COMPLETE.getCode();
                            poHeader.setBizStatus((byte) status);
                            bizPoHeaderService.saveStatus(poHeader);
                            if (bizStatus == null || !bizStatus.equals(poHeader.getBizStatus())) {
                                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), poHeader.getId());
                            }
                        }
                    }
                }
            }
            bizInvoice.setValuePrice(bizInvoice.getValuePrice() == null ? 0 : bizInvoice.getValuePrice() + valuePrice);
            super.save(bizInvoice);
        }


        if (StringUtils.isNotBlank(requestHeaders)) {
            boolean reqFlag = true;
            Integer reqId = 0;
            String[] requests = requestHeaders.split(",".trim());
            for (int b = 0; b < requests.length; b++) {
                String[] rheaders = requests[b].split("#".trim());
                BizRequestHeader requestHeader = bizRequestHeaderService.get(Integer.parseInt(rheaders[0]));
                //加入中间表关联关系
                if (flagInvoceId && !reqId.equals(requestHeader.getId())) {
                    BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                    bizDetailInvoice.setInvoice(bizInvoice);
                    bizDetailInvoice.setRequestHeader(requestHeader);
                    bizDetailInvoiceService.save(bizDetailInvoice);
                }
                reqId = requestHeader.getId();
                String[] reNumArr = rheaders[1].split("\\*");
                for (int i = 0; i < reNumArr.length; i++) {
                    String[] reArr = reNumArr[i].split("-");
                    BizRequestDetail requestDetail = bizRequestDetailService.get(Integer.parseInt(reArr[0]));
                    //商品
                    BizSkuInfo bizSkuInfo = bizSkuInfoService.get(requestDetail.getSkuInfo().getId());
                    int sendNum = Integer.parseInt(reArr[1]);     //供货数
                    valuePrice += bizSkuInfo.getBuyPrice() * sendNum;//累计货值
                    //采购中心
                    Office office = officeService.get(requestHeader.getFromOffice().getId());
                    int sendQty = requestDetail.getSendQty();   //备货单累计供货数量
                    //当供货数量和申报数量不相等时，更改备货单状态
                    if (requestDetail.getReqQty() != (sendQty + sendNum)) {
                        flagRequest = false;
                    }
                    if (sendNum == 0) {
                        continue;
                    }
                    //获取备货单相应的采购单详情,累计采购单单个商品的供货数
                    if (ReqFromTypeEnum.CENTER_TYPE.getType().equals(requestHeader.getFromType())) {
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        BizPoHeader poHeader = null;
                        BizPoDetail bizPoDetail = new BizPoDetail();
                        BizPoDetail poDetail = null;
                        BizRequestDetail bizReqDetail = new BizRequestDetail();
                        bizReqDetail.setSkuInfo(bizSkuInfo);
                        bizPoOrderReq.setRequestDetail(bizReqDetail);
                        bizPoOrderReq.setRequestHeader(requestHeader);
                        bizPoOrderReq.setSoType((Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())));
                        List<BizPoOrderReq> bizPoOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                        if (bizPoOrderReqList != null && bizPoOrderReqList.size() > 0) {
                            poHeader = bizPoOrderReqList.get(0).getPoHeader();
                        }
                        bizPoDetail.setPoHeader(poHeader);
                        bizPoDetail.setSkuInfo(bizSkuInfo);
                        List<BizPoDetail> bizPoDetailList = bizPoDetailService.findList(bizPoDetail);
                        if (bizPoDetailList != null && bizPoDetailList.size() > 0) {
                            poDetail = bizPoDetailList.get(0);
                        }
                        if (poDetail.getSendQty() + sendNum != poDetail.getOrdQty()) {
                            flagPo = false;
                        }
                        poDetail.setSendQty(poDetail.getSendQty() + sendNum);
                        bizPoDetailService.save(poDetail);
                    }
                    //累计备货单供货数量
                    requestDetail.setSendQty(sendQty + sendNum);
                    bizRequestDetailService.save(requestDetail);
                    //改备货单状态为备货中(20)
                    Integer bizStatus = requestHeader.getBizStatus();
                    requestHeader.setBizStatus(ReqHeaderStatusEnum.STOCKING.getState());
                    bizRequestHeaderService.saveInfo(requestHeader);
                    if (bizStatus == null || !bizStatus.equals(requestHeader.getBizStatus())) {
                        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), requestHeader.getId());
                    }
                    //生成供货记录
                    BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
                    bsgr.setSendNum(sendNum);
                    bsgr.setBizRequestHeader(requestHeader);
                    bsgr.setCustomer(office);
                    bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.VENDOR.getState());
                    bsgr.setSkuInfo(bizSkuInfo);
                    bsgr.setOrderNum(requestHeader.getReqNo());
                    bsgr.setSendDate(bizInvoice.getSendDate());
                    bizSendGoodsRecordService.save(bsgr);
                }
                BizRequestDetail reqDetail = new BizRequestDetail();
                reqDetail.setRequestHeader(requestHeader);
                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(reqDetail);
                for (BizRequestDetail requestDetail : requestDetailList) {
                    if (!requestDetail.getReqQty().equals(requestDetail.getSendQty())) {
                        reqFlag = false;
                    }
                }
                //更改备货单状态
                if (reqFlag) {
                    Integer bizStatus = requestHeader.getBizStatus();
                    requestHeader.setBizStatus(ReqHeaderStatusEnum.STOCK_COMPLETE.getState());
                    bizRequestHeaderService.saveRequestHeader(requestHeader);
                    if (bizStatus == null || !bizStatus.equals(requestHeader.getBizStatus())) {
                        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), requestHeader.getId());
                    }
                }
                //更改采购单状态,已完成（5）
                if (flagPo && ReqFromTypeEnum.CENTER_TYPE.getType().equals(requestHeader.getFromType())) {
                    BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                    BizPoOrderReq por = null;
                    bizPoOrderReq.setRequestHeader(requestHeader);
                    List<BizPoOrderReq> porList = bizPoOrderReqService.findList(bizPoOrderReq);
                    if (porList != null && porList.size() > 0) {
                        por = porList.get(0);
                    }
                    BizPoHeader poHeader = por.getPoHeader();
                    //获取该采购单的所有采购详情，如果所有的供货数都和采购数相等，则更改采购单状态为完成
                    BizPoHeader bizPoHeader = bizPoHeaderService.get(poHeader.getId());
                    BizPoDetail poDetail = new BizPoDetail();
                    poDetail.setPoHeader(bizPoHeader);
                    List<BizPoDetail> poDetailList = bizPoDetailService.findList(poDetail);
                    boolean flag = true;
                    for (BizPoDetail bizPoDetail : poDetailList) {
                        if (bizPoDetail.getOrdQty().equals(bizPoDetail.getSendQty())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        Byte bizStatus = bizPoHeader.getBizStatus();
                        int status = PoHeaderStatusEnum.COMPLETE.getCode();
                        poHeader.setBizStatus((byte) status);
                        bizPoHeaderService.saveStatus(poHeader);
                        if (bizStatus == null || !bizStatus.equals(poHeader.getBizStatus())) {
                            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), poHeader.getId());
                        }
                    }
                }
            }
            bizInvoice.setValuePrice(bizInvoice.getValuePrice() == null ? 0 : bizInvoice.getValuePrice() + valuePrice);
            super.save(bizInvoice);
        }
    }

    /**
     * 保存物流信息图片
     *
     * @param bizInvoice
     */
    @Transactional(readOnly = false)
    public void saveCommonImg(BizInvoice bizInvoice) {
        if (bizInvoice.getImgUrl() == null || StringUtils.isBlank(bizInvoice.getImgUrl())) {
            return;
        }
        String imgUrl = null;
        try {
            imgUrl = URLDecoder.decode(bizInvoice.getImgUrl(), "utf-8");//主图转换编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("物流信息图转换编码异常." + e.getMessage(), e);
        }
        if (imgUrl != null) {
            String[] photoArr = imgUrl.split("\\|");
            saveLogisticsImg(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizInvoice, photoArr);
        }
        List<CommonImg> commonImgs = getImgList(ImgEnum.MAIN_PRODUCT_TYPE.getCode(), bizInvoice.getId());
        for (int i = 0; i < commonImgs.size(); i++) {
            CommonImg commonImg = commonImgs.get(i);
            commonImg.setImgSort(i);
            commonImgService.save(commonImg);

            if (i == 0) {
                bizInvoice.setImgUrl(commonImg.getImgServer() + commonImg.getImgPath());
                bizInvoiceDao.update(bizInvoice);
            }
        }
    }

    private List<CommonImg> getImgList(Integer imgType, Integer bizInvoiceId) {
        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(bizInvoiceId);
        commonImg.setObjectName("biz_invoice");
        commonImg.setImgType(imgType);
        return commonImgService.findList(commonImg);
    }

    public void saveLogisticsImg(Integer imgType, BizInvoice bizInvoice, String[] photoArr) {
        if (bizInvoice.getId() == null) {
            log.error("Can't save logistics image without bizInvoice ID!");
            return;
        }

        List<CommonImg> commonImgs = getImgList(imgType, bizInvoice.getId());

        Set<String> existSet = new HashSet<>();
        for (CommonImg commonImg1 : commonImgs) {
            existSet.add(commonImg1.getImgServer() + commonImg1.getImgPath());
        }
        Set<String> newSet = new HashSet<>(Arrays.asList(photoArr));

        Set<String> result = new HashSet<String>();
        //差集，结果做删除操作
        result.clear();
        result.addAll(existSet);
        result.removeAll(newSet);
        for (String url : result) {
            for (CommonImg commonImg1 : commonImgs) {
                if (url.equals(commonImg1.getImgServer() + commonImg1.getImgPath())) {
                    commonImg1.setDelFlag("0");
                    commonImgService.delete(commonImg1);
                }
            }
        }
        //差集，结果做插入操作
        result.clear();
        result.addAll(newSet);
        result.removeAll(existSet);

        CommonImg commonImg = new CommonImg();
        commonImg.setObjectId(bizInvoice.getId());
        commonImg.setObjectName("biz_invoice");
        commonImg.setImgType(imgType);
        commonImg.setImgSort(40);
        for (String name : result) {
            if (name.trim().length() == 0 || name.contains(DsConfig.getImgServer())) {
                continue;
            }
            String pathFile = Global.getUserfilesBaseDir() + name;
            String ossPath = AliOssClientUtil.uploadFile(pathFile, true);

            commonImg.setId(null);
            commonImg.setImgPath("/" + ossPath);
            commonImg.setImgServer(DsConfig.getImgServer());
            commonImgService.save(commonImg);
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(BizInvoice bizInvoice) {
        super.delete(bizInvoice);
    }

    /**
     * 物流单信息详情
     * @param bizInvoice
     * @return
     */
    @Transactional(readOnly = false)
    public List<BizOrderDetail> findLogisticsDetail(BizInvoice bizInvoice) {
        return bizInvoiceDao.findLogisticsDetail(bizInvoice);
    }

    /**
     * 自动生成发货单
     * @param poHeaderId
     */
    @Transactional(readOnly = false)
    public void saveDeliver(Integer poHeaderId) {
        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
        bizPoOrderReq.setPoHeader(new BizPoHeader(poHeaderId));
        List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
        BizInvoice invoice = new BizInvoice();
        invoice.setBizStatus(BizInvoice.BizStatus.SUPPLY.getBizStatus());
        if (CollectionUtils.isNotEmpty(poOrderReqList)) {
            bizPoOrderReq = poOrderReqList.get(0);
            if (bizPoOrderReq.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())) {
                BizOrderHeader orderHeader = bizOrderHeaderService.get(bizPoOrderReq.getSoId());
                BizOrderDetail orderDetail = new BizOrderDetail();
                orderDetail.setOrderHeader(orderHeader);
                orderDetail.setSuplyis(new Office(0));
                List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(orderDetail);
                StringBuilder sb = new StringBuilder();
                for (BizOrderDetail bizOrderDetail : orderDetailList) {
                    sb.append(orderHeader.getId()).append("#").append(bizOrderDetail.getId()).append("-").append("0").append("*").append(",");
                }
                invoice.setOrderHeaders(sb.toString());
                invoice.setShip(BizInvoice.Ship.SO.getShip());
            } else {
                BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizPoOrderReq.getSoId());
                BizRequestDetail requestDetail = new BizRequestDetail();
                requestDetail.setRequestHeader(bizRequestHeader);
                List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(requestDetail);
                StringBuilder sb = new StringBuilder();
                for (BizRequestDetail bizRequestDetail : requestDetailList) {
                    sb.append(bizRequestHeader.getId()).append("#").append(bizRequestDetail.getId()).append("-").append("0").append("*").append(",");
                }
                invoice.setRequestHeaders(sb.toString());
                invoice.setShip(BizInvoice.Ship.RE.getShip());
            }
            this.save(invoice);
        }

    }

}
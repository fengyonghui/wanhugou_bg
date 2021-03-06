/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import com.google.common.collect.ImmutableMap;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.po.BizPoHeaderDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestExpandDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.paltform.BizPlatformInfoService;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoV3Service;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.config.parse.PhoneConfig;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.DefaultPropEnum;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.PoOrderReqTypeEnum;
import com.wanhutong.backend.modules.enums.PoPayMentOrderTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;

/**
 * 采购订单表Service
 *
 * @author liuying
 * @version 2017-12-30
 */
@Service
@Transactional(readOnly = true)
public class BizPoHeaderService extends CrudService<BizPoHeaderDao, BizPoHeader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BizPoHeaderService.class);


    @Autowired
    private BizSkuInfoV2Service bizSkuInfoService;
    @Autowired
    private BizPoDetailService bizPoDetailService;
    @Autowired
    private BizPlatformInfoService bizPlatformInfoService;
    @Autowired
    private BizPoOrderReqService bizPoOrderReqService;
    @Autowired
    private BizOrderDetailService bizOrderDetailService;
    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizRequestHeaderService bizRequestHeaderService;
    @Autowired
    private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;
    @Autowired
    private BizPoPaymentOrderService bizPoPaymentOrderService;
    @Autowired
    private CommonProcessService commonProcessService;
    @Autowired
    private BizOrderStatusService bizOrderStatusService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private BizPoHeaderDao bizPoHeaderDao;
    @Autowired
    private BizProductInfoV3Service bizProductInfoV3Service;
    @Autowired
    private BizRequestExpandDao bizRequestExpandDao;

    public static final String REQUEST_HEADER_TYPE = "1";
    public static final String DO_ORDER_HEADER_TYPE = "2";

    /**
     * 默认表名
     */
    public static final String DATABASE_TABLE_NAME = "biz_po_header";

    @Override
    public BizPoHeader get(Integer id) {
        return super.get(id);
    }

    @Override
    public List<BizPoHeader> findList(BizPoHeader bizPoHeader) {
        return super.findList(bizPoHeader);
    }

    public int findCount(BizPoHeader bizPoHeader) {
        return dao.findCount(bizPoHeader);
    }

    @Override
    public Page<BizPoHeader> findPage(Page<BizPoHeader> page, BizPoHeader bizPoHeader) {
        return super.findPage(page, bizPoHeader);
    }

    @Transactional(readOnly = false)
    @Override
    public void save(BizPoHeader bizPoHeader) {

        super.save(bizPoHeader);
        if (bizPoHeader.getCommonProcess() != null) {
            updateProcessToInit(bizPoHeader);
        }
        savePoHeaderDetail(bizPoHeader);
    }

    /**
     * 通过采购单获取订单/备货单号
     * @param bizPoHeader
     * @return
     */
    public List<String> getOrderNumOrReqNoByPoId(BizPoHeader bizPoHeader) {
        return dao.getOrderNumOrReqNoByPoId(bizPoHeader);
    }

    /**
     * 自动生成采购单
     *
     * @param orderId
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String > autoGenPO(Integer orderId, String lastPayDateVal) {
        BizOrderDetail bizOrderDetail = new BizOrderDetail();
        BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(orderId);

        bizOrderHeaderService.updateBizStatus(orderId, OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());

        bizOrderDetail.setOrderHeader(bizOrderHeader);

        List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
        if (CollectionUtils.isEmpty(orderDetailList)) {
            return Pair.of(Boolean.FALSE, "操作失败, 订单详情为空");
        }

        BizOrderDetail bzod = orderDetailList.get(0);
        BizProductInfo bizProductInfo = bizProductInfoV3Service.get(bzod.getSkuInfo().getProductInfo().getId());


        BizPoHeader bizPoHeader = new BizPoHeader();

        StringBuilder orderDetailIds = new StringBuilder();
        StringBuilder unitPrices = new StringBuilder();
        StringBuilder ordQtys = new StringBuilder();
        // orderDetailIds
        // unitPrices
        // ordQtys
        for (BizOrderDetail bd : orderDetailList) {
            if (bd.getSuplyis().getId() == 0 || bd.getSuplyis().getId() == 721) {
                orderDetailIds.append(bd.getId()).append(",");
                unitPrices.append(bd.getUnitPrice()).append(",");
                ordQtys.append(bd.getOrdQty()).append(",");
            }
        }

        bizPoHeader.setOrderDetailIds(orderDetailIds.toString());
        bizPoHeader.setUnitPrices(unitPrices.toString());
        bizPoHeader.setOrdQtys(ordQtys.toString());
        Office office = new Office();
        office.setId(bizProductInfo.getOffice().getId());
        bizPoHeader.setVendOffice(office);

        Set<Integer> poIdSet = this.findPrewPoHeader(bizPoHeader);
        if (poIdSet.size() == 1) {
            List<Integer> poIdList = new ArrayList<>(poIdSet);
            String poId = String.valueOf(poIdList.get(0));
            return Pair.of(Boolean.TRUE, poId);
        }
        int deOfifceId = 0;
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null) {
            deOfifceId = bizPoHeader.getDeliveryOffice().getId();
        }
        String poNo = "0";
        bizPoHeader.setOrderNum(poNo);
        bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
        bizPoHeader.setIsPrew(0);
        Integer id = bizPoHeader.getId();

        //设置最后付款时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isBlank(lastPayDateVal)) {
            bizPoHeader.setLastPayDate(new Date());
        } else {
            Date lastPayDate = null;
            try {
                lastPayDate = sdf.parse(lastPayDateVal);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bizPoHeader.setLastPayDate(lastPayDate);
        }

        this.save(bizPoHeader);
        if (id == null) {
            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), bizPoHeader.getId());
        }
        if (bizPoHeader.getOrderNum() == null || "0".equals(bizPoHeader.getOrderNum())) {
            poNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO, deOfifceId, bizPoHeader.getVendOffice().getId(), bizPoHeader.getId());
            bizPoHeader.setOrderNum(poNo);
            this.savePoHeader(bizPoHeader);
        }

        autoSavePaymentOrder(bizPoHeader.getId(), orderId, DO_ORDER_HEADER_TYPE);

        String poId = String.valueOf(bizPoHeader.getId());

        return Pair.of(Boolean.TRUE, poId);
    }


    private void updateProcessToInit(BizPoHeader bizPoHeader) {
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        Byte soType = getBizPoOrderReqByPo(bizPoHeader);
        Integer code = 0;
        if (soType == Byte.parseByte("1")) {
            code = purchaseOrderProcessConfig.getOrderHeaderDefaultProcessId();
        } else {
            code = purchaseOrderProcessConfig.getDefaultNewProcessId();
        }
        com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = purchaseOrderProcessConfig.getProcessMap().get(code);

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(bizPoHeader.getId().toString());
        commonProcessEntity.setObjectName(BizPoHeaderService.DATABASE_TABLE_NAME);
        commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
        commonProcessService.save(commonProcessEntity);

        this.updateProcessId(bizPoHeader.getId(), commonProcessEntity.getId());
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateProcessToInitAudit(BizPoHeader bizPoHeader, String mark) {
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        Byte soType = getBizPoOrderReqByPo(bizPoHeader);
        Integer code = 0;
        if ("oldAudit".equals(mark)) {
            code = purchaseOrderProcessConfig.getDefaultProcessId();
        } else {
            if (soType == Byte.parseByte("1")) {
                code = purchaseOrderProcessConfig.getOrderHeaderDefaultProcessId();
            } else {
                code = purchaseOrderProcessConfig.getDefaultNewProcessId();
            }
        }
        com.wanhutong.backend.modules.config.parse.Process purchaseOrderProcess = purchaseOrderProcessConfig.getProcessMap().get(code);

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(bizPoHeader.getId().toString());
        commonProcessEntity.setObjectName(BizPoHeaderService.DATABASE_TABLE_NAME);
        commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
        commonProcessEntity.setCurrent(CommonProcessEntity.CURRENT);
        List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
        if (CollectionUtils.isEmpty(list) || "startAuditAfterReject".equals(mark)) {
            commonProcessService.save(commonProcessEntity);
            this.updateProcessId(bizPoHeader.getId(), commonProcessEntity.getId());
        }

    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void savePoHeader(BizPoHeader bizPoHeader) {

        if (bizPoHeader.getDeliveryStatus() != null && bizPoHeader.getDeliveryStatus() == 1) {
            BizPoHeader poHeader = get(bizPoHeader.getId());
            bizPoHeader.setDeliveryOffice(poHeader.getVendOffice());
        }
        if (bizPoHeader.getId() != null && bizPoHeader.getIsPrew() == 0) {
            saveOrdReqBizStatus(bizPoHeader);
        }
        if (bizPoHeader.getCommonProcess() != null) {
            updateProcessToInit(bizPoHeader);
        }
        if (bizPoHeader.getPlanPay() != null
                && bizPoHeader.getPlanPay().compareTo(BigDecimal.ZERO) > 0
                && bizPoHeader.getCurrentPaymentId() != null
                && bizPoHeader.getCurrentPaymentId() > 0) {
            BizPoPaymentOrder bizPoPaymentOrder = bizPoPaymentOrderService.get(bizPoHeader.getCurrentPaymentId());
            bizPoPaymentOrder.setTotal(bizPoHeader.getPlanPay());
            bizPoPaymentOrderService.save(bizPoPaymentOrder);
        }

        super.save(bizPoHeader);

    }

    @Transactional(readOnly = false)
    public void savePoHeaderDetail(BizPoHeader bizPoHeader) {
        String orderDetailIds = bizPoHeader.getOrderDetailIds();
        String reqDetailIds = bizPoHeader.getReqDetailIds();
        Map<Integer, BizSkuInfo> skuMap = new HashMap<>();
        if (StringUtils.isNotBlank(orderDetailIds)) {
            String[] orderDetailArr = orderDetailIds.split(",");
            for (String orderDetailId : orderDetailArr) {
                BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(orderDetailId));
                BizSkuInfo skuInfo = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                skuInfo.setBuyPrice(bizOrderDetail.getBuyPrice());
//                if (bizOrderDetail.getOrderHeader() != null && bizOrderDetail.getOrderHeader().getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
//                    skuInfo.setBuyPrice(bizOrderDetail.getBuyPrice());
//                }
                if (skuMap.containsKey(skuInfo.getId())) {
                    BizSkuInfo sku = skuMap.get(skuInfo.getId());
                    Integer ordQty = sku.getReqQty() + bizOrderDetail.getOrdQty() - bizOrderDetail.getSentQty();
                    sku.setReqQty(ordQty);
                    skuMap.put(skuInfo.getId(), sku);
                } else {
//                    BizSkuInfo sku = skuMap.get(skuInfo.getId());
                    skuInfo.setReqQty(bizOrderDetail.getOrdQty() - bizOrderDetail.getSentQty());
                    skuMap.put(skuInfo.getId(), skuInfo);
                }

            }
        }
        if (StringUtils.isNotBlank(reqDetailIds)) {
            String[] reqDetailArr = reqDetailIds.split(",");
            for (String reqDetailId : reqDetailArr) {
                BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailId));
                BizSkuInfo skuInfo = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                skuInfo.setBuyPrice(bizRequestDetail.getUnitPrice());
                if (skuMap.containsKey(skuInfo.getId())) {
                    BizSkuInfo sku = skuMap.get(skuInfo.getId());
                    Integer reqQty = sku.getReqQty() + bizRequestDetail.getReqQty() - bizRequestDetail.getRecvQty();
                    sku.setReqQty(reqQty);
                    skuMap.put(skuInfo.getId(), sku);
                } else {
                    skuInfo.setReqQty(bizRequestDetail.getReqQty() - bizRequestDetail.getRecvQty());
                    skuMap.put(skuInfo.getId(), skuInfo);
                }
            }
        }
        int t = 0;
        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
        BizPoDetail poDetail = new BizPoDetail();
        for (Map.Entry<Integer, BizSkuInfo> entry : skuMap.entrySet()) {
            BizSkuInfo skuInfo = entry.getValue();
            bizPoOrderReq.setId(null);
            poDetail.setId(null);
            poDetail.setPoHeader(bizPoHeader);
            poDetail.setSkuInfo(skuInfo);
            poDetail.setLineNo(++t);
            /*
             * 这里part no 字段保存的是 item no
             */
            poDetail.setPartNo(skuInfo.getItemNo());
            poDetail.setSkuName(skuInfo.getName());
            poDetail.setOrdQty(skuInfo.getReqQty());
            poDetail.setUnitPrice(skuInfo.getBuyPrice());
            bizPoDetailService.save(poDetail);
            bizPoDetailService.calculateTotalOrderPrice(poDetail);
            bizPoOrderReq.setPoHeader(poDetail.getPoHeader());
            bizPoOrderReq.setPoLineNo(poDetail.getLineNo());
            if (StringUtils.isNotBlank(orderDetailIds)) {
                String[] orderDetailArr = orderDetailIds.split(",");
                for (String orderDetailId : orderDetailArr) {
                    BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(orderDetailId));
                    if (bizOrderDetail.getSkuInfo().getId().equals(skuInfo.getId())) {
                        bizPoOrderReq.setOrderHeader(bizOrderDetail.getOrderHeader());
                        bizPoOrderReq.setRequestHeader(null);
                        bizPoOrderReq.setSoLineNo(bizOrderDetail.getLineNo());
                        bizPoOrderReq.setSoQty(bizOrderDetail.getOrdQty());
                        bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
                        bizPoOrderReqService.save(bizPoOrderReq);
                    }
                }
            }
            if (StringUtils.isNotBlank(reqDetailIds)) {
                String[] reqDetailArr = reqDetailIds.split(",");
                for (String reqDetailId : reqDetailArr) {
                    BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailId));
                    if (bizRequestDetail.getSkuInfo().getId().equals(skuInfo.getId())) {
                        bizPoOrderReq.setRequestHeader(bizRequestDetail.getRequestHeader());
                        bizPoOrderReq.setOrderHeader(null);
                        bizPoOrderReq.setSoLineNo(bizRequestDetail.getLineNo());
                        bizPoOrderReq.setSoQty(bizRequestDetail.getReqQty());
                        bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType()));
                        bizPoOrderReqService.save(bizPoOrderReq);
                    }
                }
            }
        }
    }

    @Transactional(readOnly = false)
    public void saveOrdReqBizStatus(BizPoHeader bizPoHeader) {
        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
        BizOrderDetail bizOrderDetail = new BizOrderDetail();
        BizRequestDetail bizRequestDetail = new BizRequestDetail();
        bizPoOrderReq.setPoHeader(bizPoHeader);
        List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
        Map<Integer, List<BizPoOrderReq>> collectOrder = poOrderReqList.stream().filter(item -> item.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType())).collect(groupingBy(BizPoOrderReq::getSoId));
        Map<Integer, List<BizPoOrderReq>> collectReq = poOrderReqList.stream().filter(item -> item.getSoType() == Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType())).collect(groupingBy(BizPoOrderReq::getSoId));
        for (Map.Entry<Integer, List<BizPoOrderReq>> entry : collectOrder.entrySet()) {
            BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(entry.getKey());
            bizOrderDetail.setOrderHeader(bizOrderHeader);
            bizOrderDetail.setSuplyis(new Office(0));
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            if (orderDetailList.size() == entry.getValue().size()) {
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                    bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());
                    bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
                    /*用于 订单状态表 insert状态*/
                    bizOrderStatusService.saveOrderStatus(bizOrderHeader);
                }
            } else if (orderDetailList.size() > entry.getValue().size()) {
                bizPoOrderReq.setOrderHeader(bizOrderHeader);
                bizPoOrderReq.setRequestHeader(null);
                // bizPoOrderReq.setPoHeader(null);
                bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
                List<BizPoOrderReq> poOrderReqs = bizPoOrderReqService.findList(bizPoOrderReq);
                bizPoOrderReq.setIsPrew(0);
                bizPoOrderReq.setPoHeader(null);
                List<BizPoOrderReq> poOrderReqNotPrew = bizPoOrderReqService.findList(bizPoOrderReq);
                int commonPoOrderSize = poOrderReqNotPrew == null ? 0 : poOrderReqNotPrew.size();
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                    if (poOrderReqs.size() + commonPoOrderSize == orderDetailList.size()) {
                        bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());
                        bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
                        /*用于 订单状态表 insert状态*/
                        bizOrderStatusService.saveOrderStatus(bizOrderHeader);
                    } else {
                        bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
                        bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
                        /*用于 订单状态表 insert状态*/
                        bizOrderStatusService.saveOrderStatus(bizOrderHeader);
                    }
                }
            }
//            if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
//                //发货短信提醒
//                sendSmsForDeliver(bizOrderHeader.getOrderNum(), "");
//                //发货邮件提醒
//                sendMailForDeliver(bizOrderHeader.getOrderNum(), "");
//            }
        }
        for (Map.Entry<Integer, List<BizPoOrderReq>> entry : collectReq.entrySet()) {
            BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(entry.getKey());
            bizRequestDetail.setRequestHeader(bizRequestHeader);
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
            Integer bizStatus = bizRequestHeader.getBizStatus();
            if (requestDetailList.size() == entry.getValue().size()) {
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
//                    bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
//                    bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                }
            } else if (requestDetailList.size() > entry.getValue().size()) {
                bizPoOrderReq.setRequestHeader(bizRequestHeader);
                bizPoOrderReq.setOrderHeader(null);
                //  bizPoOrderReq.setPoHeader(null);
                bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType()));
                List<BizPoOrderReq> poOrderReqs = bizPoOrderReqService.findList(bizPoOrderReq);
                bizPoOrderReq.setIsPrew(0);
                bizPoOrderReq.setPoHeader(null);
                List<BizPoOrderReq> poOrderReqNotPrew = bizPoOrderReqService.findList(bizPoOrderReq);
                int commonPoOrderSize = poOrderReqNotPrew == null ? 0 : poOrderReqNotPrew.size();
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                    if (poOrderReqs.size() + commonPoOrderSize == requestDetailList.size()) {
//                        bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
//                        bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                    } else {
                        bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.PURCHASING.getState());
                        bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                    }
                }
            }
            if (bizStatus == null || !bizStatus.equals(bizRequestHeader.getBizStatus())) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
            }
//            if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
//                //发货短信提醒
//                sendSmsForDeliver("", bizRequestHeader.getReqNo());
//                //发货邮件提醒
//                sendMailForDeliver("", bizRequestHeader.getReqNo());
//            }
        }

    }


    public Set<Integer> findPrewPoHeader(BizPoHeader bizPoHeader) {
        String orderDetailIds = bizPoHeader.getOrderDetailIds();
        String reqDetailIds = bizPoHeader.getReqDetailIds();
        Set<Integer> poIdList = new LinkedHashSet<>();
        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
        if (StringUtils.isNotBlank(orderDetailIds)) {
            String[] orderDetailArr = orderDetailIds.split(",");
            for (int i = 0; i < orderDetailArr.length; i++) {
                BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(orderDetailArr[i]));
                bizPoOrderReq.setSoLineNo(bizOrderDetail.getLineNo());
                bizPoOrderReq.setOrderHeader(bizOrderDetail.getOrderHeader());
                bizPoOrderReq.setSoType((byte) 1);
                List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                if (poOrderReqList != null && poOrderReqList.size() > 0) {
                    BizPoOrderReq poOrderReq = poOrderReqList.get(0);
                    BizPoHeader poHeader = poOrderReq.getPoHeader();
                    poIdList.add(poHeader.getId());
                }
            }

        }
        if (StringUtils.isNotBlank(reqDetailIds)) {
            String[] reqDetailIdArr = reqDetailIds.split(",");
            for (int i = 0; i < reqDetailIdArr.length; i++) {
                BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailIdArr[i]));
                bizPoOrderReq.setSoLineNo(bizRequestDetail.getLineNo());
                bizPoOrderReq.setRequestHeader(bizRequestDetail.getRequestHeader());
                bizPoOrderReq.setSoType((byte) 2);
                List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                if (poOrderReqList != null && poOrderReqList.size() > 0) {
                    BizPoOrderReq poOrderReq = poOrderReqList.get(0);
                    BizPoHeader poHeader = poOrderReq.getPoHeader();
                    poIdList.add(poHeader.getId());
                }

            }
        }
        return poIdList;
    }

    /**
     * 采购单供货完成时，更改采购单状态
     *
     * @param bizPoHeader
     */
    @Transactional(readOnly = false)
    public void saveStatus(BizPoHeader bizPoHeader) {
        super.save(bizPoHeader);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(BizPoHeader bizPoHeader) {
        super.delete(bizPoHeader);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updatePaymentOrderId(Integer id, Integer paymentId) {
        return dao.updatePaymentOrderId(id, paymentId);
    }

    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateBizStatus(Integer id, BizPoHeader.BizStatus status) {
        return dao.updateBizStatus(id, status.getStatus());
    }

    /**
     * 生成支付申请单
     *
     * @param bizPoHeader
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> genPaymentOrder(BizPoHeader bizPoHeader) {
        if (bizPoHeader.getBizPoPaymentOrder() != null && bizPoHeader.getBizPoPaymentOrder().getId() != null && bizPoHeader.getBizPoPaymentOrder().getId() != 0) {
            return Pair.of(Boolean.FALSE, "操作失败,该订单已经有正在申请的支付单!");
        }
        PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
        PaymentOrderProcessConfig.Process purchaseOrderProcess = null;
        if (paymentOrderProcessConfig.getDefaultBaseMoney().compareTo(bizPoHeader.getPlanPay()) > 0) {
            purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getPayProcessId());
        } else {
            purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getDefaultProcessId());
        }

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(bizPoHeader.getId().toString());
        commonProcessEntity.setObjectName(BizPoPaymentOrderService.DATABASE_TABLE_NAME);
        commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
        commonProcessService.save(commonProcessEntity);

        BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
        bizPoPaymentOrder.setPoHeaderId(bizPoHeader.getId());
        List<BizPoPaymentOrder> poPaymentOrderList = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
        if (poPaymentOrderList.size() == 0) {
            bizPoPaymentOrder.setTotal(BigDecimal.ZERO);
        } else {
            bizPoPaymentOrder.setTotal(bizPoHeader.getPlanPay());
        }
        bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
        bizPoPaymentOrder.setDeadline(bizPoHeader.getPayDeadline());
        bizPoPaymentOrder.setProcessId(commonProcessEntity.getId());
        bizPoPaymentOrderService.save(bizPoPaymentOrder);

        commonProcessEntity.setObjectId(bizPoPaymentOrder.getId().toString());
        commonProcessService.save(commonProcessEntity);

        bizPoHeader.setBizPoPaymentOrder(bizPoPaymentOrder);
        this.updatePaymentOrderId(bizPoHeader.getId(), bizPoPaymentOrder.getId());
        return Pair.of(Boolean.TRUE, "操作成功!");
    }

    /**
     * 申请付款时，生成支付申请单
     *
     * @param bizPoHeader
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> genPaymentOrderForApply(BizPoHeader bizPoHeader, String paymentApplyRemark) {
        if (bizPoHeader.getBizPoPaymentOrder() != null && bizPoHeader.getBizPoPaymentOrder().getId() != null && bizPoHeader.getBizPoPaymentOrder().getId() != 0) {
            return Pair.of(Boolean.FALSE, "操作失败,该订单已经有正在申请的支付单!");
        }
        PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
        PaymentOrderProcessConfig.Process purchaseOrderProcess = null;
        if (paymentOrderProcessConfig.getDefaultBaseMoney().compareTo(bizPoHeader.getPlanPay()) > 0) {
            purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getPayProcessId());
        } else {
            purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getDefaultProcessId());
        }

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(bizPoHeader.getId().toString());
        commonProcessEntity.setObjectName(BizPoPaymentOrderService.DATABASE_TABLE_NAME);
        commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
        commonProcessService.save(commonProcessEntity);

        BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
        bizPoPaymentOrder.setPoHeaderId(bizPoHeader.getId());
        List<BizPoPaymentOrder> poPaymentOrderList = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
        if (poPaymentOrderList.size() == 0) {
            bizPoPaymentOrder.setTotal(BigDecimal.ZERO);
        } else {
            bizPoPaymentOrder.setTotal(bizPoHeader.getPlanPay());
        }
        bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
        bizPoPaymentOrder.setDeadline(bizPoHeader.getPayDeadline());
        bizPoPaymentOrder.setProcessId(commonProcessEntity.getId());
        bizPoPaymentOrder.setRemark(paymentApplyRemark);
        bizPoPaymentOrderService.save(bizPoPaymentOrder);

        bizPoHeader.setBizPoPaymentOrder(bizPoPaymentOrder);
        this.updatePaymentOrderId(bizPoHeader.getId(), bizPoPaymentOrder.getId());
        return Pair.of(Boolean.TRUE, "操作成功!");
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> genPaymentOrder(int id, BigDecimal planPay, Date deadline) {
        BizPoHeader bizPoHeader = this.get(id);
        bizPoHeader.setPlanPay(planPay);
        bizPoHeader.setPayDeadline(deadline);
        return genPaymentOrder(bizPoHeader);
    }

    public Pair<Boolean, String> audit(int id, String currentType, int auditType, String description, CommonProcessEntity cureentProcessEntity) {
        if (cureentProcessEntity == null) {
            return Pair.of(Boolean.FALSE, "操作失败,当前订单无审核状态!");
        }
        cureentProcessEntity = commonProcessService.get(cureentProcessEntity.getId());
        if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
            LOGGER.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}]", id, currentType);
            return Pair.of(Boolean.FALSE,  "操作失败,当前审核状态异常!");
        }

        PurchaseOrderProcessConfig purchaseOrderProcessConfig = new PurchaseOrderProcessConfig();

        purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        // 当前流程
        com.wanhutong.backend.modules.config.parse.Process currentProcess = purchaseOrderProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
        // 下一流程
        com.wanhutong.backend.modules.config.parse.Process nextProcess = purchaseOrderProcessConfig.getProcessMap().get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? currentProcess.getPassCode() : currentProcess.getRejectCode());
        if (nextProcess == null) {
            return Pair.of(Boolean.FALSE,  "操作失败,当前流程已经结束!");
        }


        User user = UserUtils.getUser();
        List<String> roleEnNameEnumList = currentProcess.getRoleEnNameEnum();
        boolean hasRole = false;
        for (String s : roleEnNameEnumList) {
            RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
            Role role = new Role();
            role.setEnname(roleEnNameEnum.getState());
            if (user.getRoleList().contains(role)) {
                hasRole = true;
                break;
            }
        }

        if (!user.isAdmin() && !hasRole) {
            return Pair.of(Boolean.FALSE,  "操作失败,该用户没有权限!");
        }

        if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
            return Pair.of(Boolean.FALSE,  "请输入驳回理由!");
        }

        cureentProcessEntity.setBizStatus(auditType);
        cureentProcessEntity.setProcessor(user.getId().toString());
        cureentProcessEntity.setDescription(description);
        cureentProcessEntity.setCurrent(CommonProcessEntity.NOT_CURRENT);
        commonProcessService.save(cureentProcessEntity);

        CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
        nextProcessEntity.setObjectId(String.valueOf(id));
        nextProcessEntity.setObjectName(BizPoHeaderService.DATABASE_TABLE_NAME);
        nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
        nextProcessEntity.setPrevId(cureentProcessEntity.getId());
        nextProcessEntity.setCurrent(CommonProcessEntity.CURRENT);
        commonProcessService.save(nextProcessEntity);
        this.updateProcessId(id, nextProcessEntity.getId());

        BizPoHeader bizPoHeader = this.get(id);
        if (nextProcess.getCode() == purchaseOrderProcessConfig.getPayProcessId()) {
            Byte bizStatus = bizPoHeader.getBizStatus();
            this.updateBizStatus(id, BizPoHeader.BizStatus.PROCESS_COMPLETE);
            if (bizStatus == null || !bizStatus.equals(bizPoHeader.getBizStatus())) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), bizPoHeader.getId());
            }
        }

        //采购单审核驳回的时候，需要重新提付款申请
        if (auditType == CommonProcessEntity.AuditType.REJECT.getCode()) {
            updatePoPayment(id);
        }

        try {
            StringBuilder phone = new StringBuilder();
            for (String s : nextProcess.getRoleEnNameEnum()) {
                List<User> userList = systemService.findUser(new User(systemService.getRoleByEnname(s)));
                if (CollectionUtils.isNotEmpty(userList)) {
                    for (User u : userList) {
                        phone.append(u.getMobile()).append(",");
                    }
                }
            }

            if (StringUtils.isNotBlank(phone.toString())) {
                AliyunSmsClient.getInstance().sendSMS(
                        SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                        phone.toString(),
                        ImmutableMap.of("order", "采购单", "orderNum", bizPoHeader.getOrderNum()));
            }
        } catch (Exception e) {
            LOGGER.error("[exception]PO审批短信提醒发送异常[poHeaderId:{}]", id, e);
            EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.COMMON_EXCEPTION.name());
            AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
                    String.format(email.getBody(),
                            "BizPoHeaderService:541",
                            e.toString(),
                            "PO审批短信提醒发送异常",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                    ));
        }

        return Pair.of(Boolean.TRUE,  "操作成功!");
    }


    /**
     * 审批PO
     *
     * @param id
     * @param currentType
     * @param auditType
     * @param description
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> auditPo(int id, String currentType, int auditType, String description) {
        BizPoHeader bizPoHeader = this.get(id);
        CommonProcessEntity cureentProcessEntity = bizPoHeader.getCommonProcess();
        return audit(id, currentType, auditType, description, cureentProcessEntity);
    }

    /**
     * 审批支付单
     *
     * @param id
     * @param currentType
     * @param auditType
     * @param description
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> auditPay(Integer id, String currentType, int auditType, String description, BigDecimal money) {
        BizPoPaymentOrder bizPoPaymentOrder = bizPoPaymentOrderService.get(id);
        BizPoHeader bizPoHeader = new BizPoHeader();
        BizRequestHeader bizRequestHeader = new BizRequestHeader();
        if (PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
            bizRequestHeader = bizRequestHeaderForVendorService.get(bizPoPaymentOrder.getPoHeaderId());
        } else if (PoPayMentOrderTypeEnum.PO_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
            bizPoHeader = this.get(bizPoPaymentOrder.getPoHeaderId());
        }
        CommonProcessEntity cureentProcessEntity = bizPoPaymentOrder.getCommonProcess();
        if (cureentProcessEntity == null) {
            return Pair.of(Boolean.FALSE,  "操作失败,当前订单无审核状态!");
        }
        cureentProcessEntity = commonProcessService.get(cureentProcessEntity.getId());
        if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
            LOGGER.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}][{}]", id, currentType, bizPoPaymentOrder.getOrderType());
            return Pair.of(Boolean.FALSE,   "操作失败,当前审核状态异常!");
        }

        PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
        // 当前流程
        PaymentOrderProcessConfig.Process currentProcess = paymentOrderProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
        // 下一流程
        PaymentOrderProcessConfig.Process nextProcess = CommonProcessEntity.AuditType.PASS.getCode() == auditType ?
                paymentOrderProcessConfig.getPassProcess(money, currentProcess) : paymentOrderProcessConfig.getRejectProcess(money, currentProcess);
        if (nextProcess == null) {
            return Pair.of(Boolean.FALSE,   "操作失败,当前流程已经结束!");
        }

        User user = UserUtils.getUser();
        List<PaymentOrderProcessConfig.MoneyRole> moneyRoleList = currentProcess.getMoneyRole();
        PaymentOrderProcessConfig.MoneyRole moneyRole = null;
        for (PaymentOrderProcessConfig.MoneyRole role : moneyRoleList) {
            if (role.getEndMoney().compareTo(money) > 0 && role.getStartMoney().compareTo(money) <= 0) {
                moneyRole = role;
            }
        }
        if (moneyRole == null) {
            return Pair.of(Boolean.FALSE,"操作失败,当前流程无审批人,请联系技术部!");
        }

        boolean hasRole = false;
        for (String s : moneyRole.getRoleEnNameEnum()) {
            RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
            Role role = new Role();
            role.setEnname(roleEnNameEnum.getState());
            if (user.getRoleList().contains(role)) {
                hasRole = true;
                break;
            }
        }

        if (!user.isAdmin() && !hasRole) {
            return Pair.of(Boolean.FALSE,"操作失败,该用户没有权限!");
        }

        if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
            return Pair.of(Boolean.FALSE,"请输入驳回理由!");
        }

        cureentProcessEntity.setBizStatus(auditType);
        cureentProcessEntity.setProcessor(user.getId().toString());
        cureentProcessEntity.setDescription(description);
        commonProcessService.save(cureentProcessEntity);

        CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
        nextProcessEntity.setObjectId(String.valueOf(id));
        nextProcessEntity.setObjectName(BizPoPaymentOrderService.DATABASE_TABLE_NAME);
        nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
        nextProcessEntity.setPrevId(cureentProcessEntity.getId());
        commonProcessService.save(nextProcessEntity);
        bizPoPaymentOrderService.updateProcessId(id, nextProcessEntity.getId());

        if (nextProcess.getCode() == paymentOrderProcessConfig.getEndProcessId()) {
            this.updatePaymentOrderId(bizPoPaymentOrder.getPoHeaderId(), null);
        }

        try {
            List<PaymentOrderProcessConfig.MoneyRole> nextMoneyRoleList = nextProcess.getMoneyRole();
            PaymentOrderProcessConfig.MoneyRole nextMoneyRole = null;
            for (PaymentOrderProcessConfig.MoneyRole role : nextMoneyRoleList) {
                if (role.getEndMoney() != null && role.getStartMoney() != null && role.getEndMoney().compareTo(money) > 0 && role.getStartMoney().compareTo(money) <= 0) {
                    nextMoneyRole = role;
                }
            }

            if (nextMoneyRole != null) {
                StringBuilder phone = new StringBuilder();
                for (String s : nextMoneyRole.getRoleEnNameEnum()) {
                    RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
                    User sendUser = new User(systemService.getRoleByEnname(roleEnNameEnum == null ? "" : roleEnNameEnum.getState()));
                    List<User> userList = systemService.findUser(sendUser);
                    if (CollectionUtils.isNotEmpty(userList)) {
                        for (User u : userList) {
                            phone.append(u.getMobile()).append(",");
                        }
                    }
                }

                List<BizPoPaymentOrder> list = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
                BizPoPaymentOrder poPaymentOrder = list.get(0);
                BizPoHeader poHeader = this.get(poPaymentOrder.getPoHeader().getId());

                Byte soType = this.getBizPoOrderReqByPo(poHeader);
                String orderStr = "";
                String orderNum = "";
                if (soType == Byte.parseByte("1")) {
                    orderStr = "订单支付";
                    orderNum = poPaymentOrder.getOrderNum();
                } else {
                    orderStr = "备货单支付";
                    orderNum = poPaymentOrder.getReqNo();
                }

                AliyunSmsClient.getInstance().sendSMS(
                        SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                        phone.toString(),
                        ImmutableMap.of("order", orderStr, "orderNum", orderNum));

//                if (StringUtils.isNotBlank(phone.toString()) && PoPayMentOrderTypeEnum.PO_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
//                    AliyunSmsClient.getInstance().sendSMS(
//                            SmsTemplateCode.PENDING_AUDIT_1.getCode(),
//                            phone.toString(),
//                            ImmutableMap.of("order", "采购单支付", "orderNum", bizPoHeader.getOrderNum()));
//                }
//                if (StringUtils.isNotBlank(phone.toString()) && PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
//                    AliyunSmsClient.getInstance().sendSMS(
//                            SmsTemplateCode.PENDING_AUDIT_1.getCode(),
//                            phone.toString(),
//                            ImmutableMap.of("order", "备货单支付", "orderNum", bizRequestHeader.getReqNo()));
//                }


                if ("666".equals(String.valueOf(nextProcess.getCode()))) {
                    RoleEnNameEnum.FINANCE.getState();
                    User sendUser = new User(systemService.getRoleByEnname(RoleEnNameEnum.FINANCE.getState()));
                    List<User> userList = systemService.findUser(sendUser);
                    if (CollectionUtils.isNotEmpty(userList)) {
                        for (User u : userList) {
                            phone.append(u.getMobile()).append(",");
                        }
                    }

                    if (soType == Byte.parseByte("1")) {
                        orderStr = "订单付款";
                    } else {
                        orderStr = "备货单付款";
                    }

                    AliyunSmsClient.getInstance().sendSMS(
                            SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                            phone.toString(),
                            ImmutableMap.of("order", orderStr, "orderNum", orderNum));
                }

            }
        } catch (Exception e) {
            if (PoPayMentOrderTypeEnum.PO_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
                LOGGER.error("[exception]PO支付审批短信提醒发送异常[poHeaderId:{}]", id, e);
                EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.COMMON_EXCEPTION.name());
                AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
                        String.format(email.getBody(),
                                "BizPoHeaderService:703",
                                e.toString(),
                                "PO支付审批短信提醒发送异常",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                        ));
            }
            if (PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
                LOGGER.error("[exception]RE支付审批短信提醒发送异常[RequestHeaderId:{}]", id, e);
                EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.COMMON_EXCEPTION.name());
                AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
                        String.format(email.getBody(),
                                "BizPoHeaderService:709",
                                e.toString(),
                                "RE支付审批短信提醒发送异常",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                        ));
            }
        }

        return Pair.of(Boolean.TRUE, "操作成功!");
    }

    /**
     * 开始审核流程
     *
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> startAudit(int id, Boolean prew, BigDecimal prewPayTotal, Date prewPayDeadline, int auditType, String desc, String mark) {
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        BizPoHeader bizPoHeader = this.get(id);
        if (bizPoHeader == null) {
            LOGGER.error("start audit error [{}]", id);
            return Pair.of(Boolean.FALSE,   "操作失败!参数错误[id]");
        }

        if (prew) {
            bizPoHeader.setPayDeadline(prewPayDeadline);
            bizPoHeader.setPlanPay(prewPayTotal);
            this.genPaymentOrder(bizPoHeader);
        }
        Byte bizStatus = bizPoHeader.getBizStatus();
        this.updateBizStatus(bizPoHeader.getId(), BizPoHeader.BizStatus.PROCESS);
        if (bizStatus == null || !bizStatus.equals(bizPoHeader.getBizStatus())) {
            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), bizPoHeader.getId());
        }

        if ("startAuditAfterReject".equals(mark)) {
            CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
            commonProcessEntity.setObjectId(String.valueOf(bizPoHeader.getId()));
            commonProcessEntity.setObjectName(DATABASE_TABLE_NAME);
            commonProcessEntity.setCurrent(1);

            List<CommonProcessEntity> list = commonProcessService.findList(commonProcessEntity);
            if (CollectionUtils.isNotEmpty(list)) {
                CommonProcessEntity commonProcess = list.get(0);
                commonProcess.setCurrent(0);
                commonProcessService.save(commonProcess);
            }

            User user = UserUtils.getUser();
            commonProcessEntity.setObjectId(String.valueOf(bizPoHeader.getId()));
            commonProcessEntity.setObjectName(DATABASE_TABLE_NAME);
            commonProcessEntity.setProcessor(user.getId().toString());
            commonProcessEntity.setType("0");
            commonProcessEntity.setDescription(desc);
            commonProcessEntity.setCurrent(0);
            commonProcessService.save(commonProcessEntity);

        }

        this.updateProcessToInitAudit(bizPoHeader, mark);
        Byte soType = getBizPoOrderReqByPo(bizPoHeader);
        String currentType = "";

        if ("oldAudit".equals(mark)) {
            currentType = String.valueOf(purchaseOrderProcessConfig.getDefaultProcessId());
            auditPo(id, currentType, auditType, desc);
        }else {
            if (soType == Byte.parseByte("1")) {
                currentType = String.valueOf(purchaseOrderProcessConfig.getOrderHeaderDefaultProcessId());
            } else {
                currentType = String.valueOf(purchaseOrderProcessConfig.getDefaultNewProcessId());
            }
        }

        return Pair.of(Boolean.TRUE,   "操作成功!");
    }

    /**
     * 支付订单
     *
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String payOrder(Integer poHeaderId,Integer reqHeaderId, Integer paymentOrderId, BigDecimal payTotal, String img, String paymentRemark) {
        // 当前流程
        User user = UserUtils.getUser();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.FINANCE.getState());
        Role role1 = new Role();
        role1.setEnname(RoleEnNameEnum.TELLER.getState());
        if (!user.isAdmin() && !user.getRoleList().contains(role) && !user.getRoleList().contains(role1)) {
            return "操作失败,该用户没有权限!";
        }

        BizPoPaymentOrder bizPoPaymentOrder = bizPoPaymentOrderService.get(paymentOrderId);

        if (bizPoPaymentOrder.getBizStatus() != BizPoPaymentOrder.BizStatus.NO_PAY.getStatus() && PoPayMentOrderTypeEnum.PO_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
            LOGGER.warn("[exception]BizPoHeaderController payOrder currentType mismatching [{}][{}][{}]", poHeaderId, paymentOrderId, bizPoPaymentOrder.getOrderType());
            return "操作失败,当前状态有误!";
        } else if (bizPoPaymentOrder.getBizStatus() != BizPoPaymentOrder.BizStatus.NO_PAY.getStatus() && PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getOrderType())) {
            LOGGER.warn("[exception]BizRequestHeaderController payOrder currentType mismatching [{}][{}][{}]", reqHeaderId, paymentOrderId, bizPoPaymentOrder.getOrderType());
            return "操作失败,当前状态有误!";
        }

        bizPoPaymentOrder.setPayTotal(payTotal);
        bizPoPaymentOrder.setImg(img);
        bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.ALL_PAY.getStatus());
        bizPoPaymentOrder.setPayTime(new Date());
        bizPoPaymentOrder.setRemark(paymentRemark);
        bizPoPaymentOrderService.save(bizPoPaymentOrder);


        // 状态改为全款或首款支付
//      DOWN_PAYMENT(1, "付款支付"),
//      ALL_PAY(2, "全部支付"),
        if (reqHeaderId == null) {
            BizPoHeader bizPoHeader = this.get(poHeaderId);
            BigDecimal orderTotal = BigDecimal.valueOf(bizPoHeader.getTotalDetail()).add(BigDecimal.valueOf(bizPoHeader.getTotalExp())).add(BigDecimal.valueOf(bizPoHeader.getFreight()));
            Byte bizStatus = bizPoHeader.getBizStatus();
            this.updateBizStatus(bizPoHeader.getId(), bizPoHeader.getPayTotal().add(payTotal).compareTo(orderTotal) >= 0 ? BizPoHeader.BizStatus.ALL_PAY : BizPoHeader.BizStatus.DOWN_PAYMENT);
            if (bizStatus == null || !bizStatus.equals(bizPoHeader.getBizStatus())) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), bizPoHeader.getId());
            }
            BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
            bizPoOrderReq.setPoHeader(bizPoHeader);
            //bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType()));
            List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
            Integer reqId = 0;
            if (CollectionUtils.isNotEmpty(poOrderReqList)) {
                BizPoOrderReq poOrderReq = poOrderReqList.get(0);
                reqId = poOrderReq.getSoId();
                Byte soType = poOrderReq.getSoType();
                if (PoOrderReqTypeEnum.RE.getOrderType().equals(soType)) {
                    BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(reqId);
                    Integer reqBizStatus = bizRequestHeader.getBizStatus();
                    bizRequestHeader.setBizStatus(bizPoHeader.getPayTotal().add(payTotal).compareTo(orderTotal) >= 0 ? ReqHeaderStatusEnum.VEND_INITIAL_PAY.getState() : ReqHeaderStatusEnum.VEND_ALL_PAY.getState());
                    bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                    if (reqBizStatus == null || !reqBizStatus.equals(bizRequestHeader.getBizStatus())) {
                        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizPoHeader.getId());
                    }
                } else if (PoOrderReqTypeEnum.SO.getOrderType().equals(soType)) {
                    BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(reqId);
                    Integer orderBizStatus = bizOrderHeader.getBizStatus();
                    bizOrderHeader.setBizStatus(bizPoHeader.getPayTotal().add(payTotal).compareTo(orderTotal) >= 0 ? ReqHeaderStatusEnum.VEND_INITIAL_PAY.getState() : ReqHeaderStatusEnum.VEND_ALL_PAY.getState());
                    if (orderBizStatus == null || !orderBizStatus.equals(bizOrderHeader.getBizStatus())) {
                        bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizPoHeader.getId());
                    }
                }
                incrPayTotal(bizPoHeader.getId(), payTotal);
            }
            // 清除关联的支付申请单
            this.updatePaymentOrderId(bizPoHeader.getId(), null);
        } else {
            BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(reqHeaderId);
            Integer bizStatus = bizRequestHeader.getBizStatus();
            Integer status = (bizRequestHeader.getBalanceTotal() == null ? BigDecimal.valueOf(0) : bizRequestHeader.getBalanceTotal()).add(payTotal).compareTo(BigDecimal.valueOf(bizRequestHeader.getTotalDetail())) >= 0 ? ReqHeaderStatusEnum.VEND_ALL_PAY.getState() : ReqHeaderStatusEnum.VEND_INITIAL_PAY.getState();
            if ((ReqHeaderStatusEnum.VEND_INITIAL_PAY.getState().equals(status) && bizStatus < ReqHeaderStatusEnum.VEND_INITIAL_PAY.getState()) || ReqHeaderStatusEnum.VEND_ALL_PAY.getState().equals(status)) {
                bizRequestHeaderForVendorService.updateBizStatus(reqHeaderId, status);
                if (!bizStatus.equals(status)) {
                    bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
                }
            }
            bizRequestExpandDao.incrPayTotal(bizRequestExpandDao.getIdByRequestHeaderId(bizRequestHeader.getId()), payTotal);
            // 清除关联的支付申请单
            bizRequestExpandDao.updatePaymentOrderId(bizRequestExpandDao.getIdByRequestHeaderId(reqHeaderId), null);
        }
        return "操作成功!";
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int incrPayTotal(int id, BigDecimal payTotal) {
        return dao.incrPayTotal(id, payTotal);
    }

    /**
     * 更新流程ID
     *
     * @param headerId
     * @param processId
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateProcessId(int headerId, int processId) {
        return dao.updateProcessId(headerId, processId);
    }

    public void findPoHeaderDetail(String orderDetailIds, String reqDetailIds) {
//		String orderDetailIds=bizPoHeader.getOrderDetailIds();
//		String reqDetailIds=bizPoHeader.getReqDetailIds();
        Map<Integer, BizSkuInfo> skuMap = new HashMap<>();
        if (StringUtils.isNotBlank(orderDetailIds)) {
            String[] orderDetailArr = orderDetailIds.split(",");
            for (String orderDetailId : orderDetailArr) {
                BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(orderDetailId));
                BizSkuInfo skuInfo = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                if (skuMap.containsKey(skuInfo.getId())) {
                    BizSkuInfo sku = skuMap.get(skuInfo.getId());
                    Integer ordQty = sku.getReqQty() + bizOrderDetail.getOrdQty() - bizOrderDetail.getSentQty();
                    sku.setReqQty(ordQty);
                    skuMap.put(skuInfo.getId(), sku);
                } else {
                    skuInfo.setReqQty(bizOrderDetail.getOrdQty() - bizOrderDetail.getSentQty());
                    skuMap.put(skuInfo.getId(), skuInfo);
                }

            }
        }
        if (StringUtils.isNotBlank(reqDetailIds)) {
            String[] reqDetailArr = reqDetailIds.split(",");
            for (String reqDetailId : reqDetailArr) {
                BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailId));
                BizSkuInfo skuInfo = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                if (skuMap.containsKey(skuInfo.getId())) {
                    BizSkuInfo sku = skuMap.get(skuInfo.getId());
                    Integer reqQty = sku.getReqQty() + bizRequestDetail.getReqQty() - bizRequestDetail.getRecvQty();
                    sku.setReqQty(reqQty);
                    skuMap.put(skuInfo.getId(), sku);
                } else {
                    skuInfo.setReqQty(bizRequestDetail.getReqQty() - bizRequestDetail.getRecvQty());
                    skuMap.put(skuInfo.getId(), skuInfo);
                }
            }
        }

    }

    public void getCommonProcessListFromDB(Integer id, List<CommonProcessEntity> list) {
        if (id == null || id == 0) {
            return;
        }
        CommonProcessEntity commonProcessEntity = commonProcessService.get(id);
        if (commonProcessEntity != null) {
            list.add(commonProcessEntity);
            if (commonProcessEntity.getPrevId() != 0) {
                getCommonProcessListFromDB(commonProcessEntity.getPrevId(), list);
            }
        }
    }

    /**
     * 发货短信提醒
     *
     * @param orderNum
     * @param reqNum
     */
    public void sendSmsForDeliver(String orderNum, String reqNum) {
        try {
            List<User> userList = systemService.findUserByRoleEnName(RoleEnNameEnum.SHIPPER.getState());
            if (CollectionUtils.isEmpty(userList)) {
                return;
            }
            StringBuilder phones = new StringBuilder();
            for (User user : userList) {
                if (StringUtils.isNotBlank(user.getMobile())) {
                    phones.append(user.getMobile()).append(",");
                }
            }
            if (StringUtils.isNotBlank(orderNum)) {
                AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.ORDER_DELIVER.getCode(), phones.toString(), ImmutableMap.of("order", "订单","number",orderNum.substring(3)));
            }
            if (StringUtils.isNotBlank(reqNum)) {
                AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.ORDER_DELIVER.getCode(), phones.toString(), ImmutableMap.of("order", "备货单","number",reqNum.substring(3)));
            }

        } catch (Exception e) {
            if (StringUtils.isNotBlank(orderNum)) {
                logger.error("[Exception]发货的短信提醒异常[orderNum:{}]", orderNum, e);
            }
            if (StringUtils.isNotBlank(reqNum)) {
                logger.error("[Exception]发货的短信提醒异常[reqNum:{}]", reqNum, e);
            }
            PhoneConfig.Phone phone = PhoneConfig.getPhone(PhoneConfig.PhoneType.OFFLINE_PAY_RECORD_EXCEPTION.name());
            AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.EXCEPTION_WARN.getCode(), phone.getNumber(), ImmutableMap.of("type", "Exception", "service", "发货短信提醒"));
            EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.COMMON_EXCEPTION.name());
            AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
                    String.format(email.getBody(),
                            "BizPoheaderService:882,886",
                            e.toString(),
                            "发货短信提醒异常",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
        }
    }

    /**
     * 发货邮件提醒
     *
     * @param orderNum
     * @param reqNum
     */
    public void sendMailForDeliver(String orderNum, String reqNum) {
        try {
            List<User> userList = systemService.findUserByRoleEnName(RoleEnNameEnum.SHIPPER.getState());
            if (CollectionUtils.isEmpty(userList)) {
                return;
            }
            StringBuilder emails = new StringBuilder();
            for (User user : userList) {
                if (StringUtils.isNotBlank(user.getEmail())) {
                    emails.append(user.getEmail()).append(",");
                }
            }
//            emails.append("zhangtengfei_cn@163.com,").append("785461218@qq.com");
            if (StringUtils.isNotBlank(orderNum) && StringUtils.isNotBlank(emails.toString())) {
                AliyunMailClient.getInstance().sendTxt(
                        emails.toString(),
                        "又到了发货时间",
                        "您有新的订单需要发货，请尽快登陆后台系统处理，订单号：" + orderNum);
            }
            if (StringUtils.isNotBlank(reqNum) && StringUtils.isNotBlank(emails.toString())) {
                AliyunMailClient.getInstance().sendTxt(
                        emails.toString(),
                        "又到了发货时间",
                        "您有新的备货单需要发货，请尽快登陆后台系统处理，备货单号：" + reqNum);
            }
        } catch (Exception e) {
            if (StringUtils.isNotBlank(orderNum)) {
                logger.error("[Exception]发货的邮件提醒异常[orderNum:{}]", orderNum, e);
            }
            if (StringUtils.isNotBlank(reqNum)) {
                logger.error("[Exception]发货的邮件提醒异常[reqNum:{}]", reqNum, e);
            }
            PhoneConfig.Phone phone = PhoneConfig.getPhone(PhoneConfig.PhoneType.OFFLINE_PAY_RECORD_EXCEPTION.name());
            AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.EXCEPTION_WARN.getCode(), phone.getNumber(), ImmutableMap.of("type", "Exception", "service", "发货邮件提醒"));
            EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.COMMON_EXCEPTION.name());
            AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
                    String.format(email.getBody(),
                            "BizPoheaderService:927,933",
                            e.toString(),
                            "发货邮件提醒异常",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)));
        }
    }


    /**
     * 供应商供应总额统计
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public List<BizOrderStatisticsDto> vendorProductPrice(String startDate, String endDate, String vendName) {
        return dao.vendorProductPrice(startDate, endDate + " 23:59:59", vendName);
    }

    /**
     * 供应商供应SKU总额统计
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public List<BizOrderStatisticsDto> vendorSkuPrice(String startDate, String endDate, Integer officeId) {
        return dao.vendorSkuPrice(startDate, endDate + " 23:59:59", officeId);
    }

    /**
     * 该备货单下所有商品的总采购数量，总排产数量（分为按订单排产的总排产量和按商品排产的总排产量）
     * @param id
     * @return
     */
    public BizPoHeader getTotalQtyAndSchedulingNum(Integer id){
        return bizPoHeaderDao.getTotalQtyAndSchedulingNum(id);
    }

    /**
     * 供应商确认排产后，更新排产表中排产状态
     * @param bizPoHeader
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateSchedulingType(BizPoHeader bizPoHeader) {
        bizPoHeaderDao.updateSchedulingType(bizPoHeader);
    }

    /**
     * 采购单开启审核，同时自动生成付款单
     * @param poHeaderIdid
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> autoSavePaymentOrder(Integer poHeaderIdid, Integer orderId, String orderType){
        BizPoHeader bizPoHeader = this.get(poHeaderIdid);
        Boolean prew = false;
        BigDecimal prewPayTotal = new BigDecimal(0);
        Date prewPayDeadline = null;
        Integer auditType = 1;
        String desc = "";

        BizPoPaymentOrder bizPoPaymentOrder = bizPoHeader.getBizPoPaymentOrder();
        if (bizPoPaymentOrder != null) {
            if (bizPoPaymentOrder.getId() != null) {
                prewPayTotal = bizPoPaymentOrder.getTotal();
            }
        } else {
            BigDecimal totalDetail = new BigDecimal((bizPoHeader.getTotalDetail() == null ? 0:bizPoHeader.getTotalDetail()));
            BigDecimal totalExp = new BigDecimal((bizPoHeader.getTotalExp() == null ? 0:bizPoHeader.getTotalExp()));
            BigDecimal freight = new BigDecimal((bizPoHeader.getFreight() == null ? 0:bizPoHeader.getFreight()));
            BigDecimal payTotal = bizPoHeader.getPayTotal();

            prewPayTotal = totalDetail.add(totalExp).add(freight).subtract(payTotal).setScale(0, BigDecimal.ROUND_HALF_UP);
        }
        String mark = "newAudit";
        Pair<Boolean, String> result = this.startAudit(poHeaderIdid, prew, prewPayTotal, prewPayDeadline, auditType, desc, mark);

        //短信通知财务，审核付采购单
        StringBuilder phone = new StringBuilder();
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        Byte soType = getBizPoOrderReqByPo(bizPoHeader);
        Integer code = 0;
        if ("oldAudit".equals(mark)) {
            code = purchaseOrderProcessConfig.getDefaultProcessId();
        } else {
            if (soType == Byte.parseByte("1")) {
                code = purchaseOrderProcessConfig.getOrderHeaderDefaultProcessId();
            } else {
                code = purchaseOrderProcessConfig.getDefaultNewProcessId();
            }
        }

        String orderNum = "";
        if (REQUEST_HEADER_TYPE.equals(orderType)) {
            BizRequestHeader requestHeader = bizRequestHeaderForVendorService.get(orderId);
            orderNum = requestHeader.getReqNo();
        } else if (DO_ORDER_HEADER_TYPE.equals(orderType)) {
            BizOrderHeader orderHeader = bizOrderHeaderService.get(orderId);
            orderNum = orderHeader.getOrderNum();
        }

        com.wanhutong.backend.modules.config.parse.Process currentProcess = purchaseOrderProcessConfig.getProcessMap().get(code);
        if (currentProcess.getRoleEnNameEnum() != null && currentProcess.getRoleEnNameEnum().get(0) != null) {
            User sendUser=new User(systemService.getRoleByEnname(currentProcess.getRoleEnNameEnum().get(0).toLowerCase()));
            List<User> userList = systemService.findUser(sendUser);
            if (CollectionUtils.isNotEmpty(userList)) {
                for (User u : userList) {
                    phone.append(u.getMobile()).append(",");
                }
            }
            if (StringUtils.isNotBlank(phone.toString())) {
                String orderStr = "";
                if (soType == Byte.parseByte("1")) {
                    orderStr = "订单(订单支出信息)";
                } else {
                    orderStr = "备货清单(订单支出信息)";
                }
                AliyunSmsClient.getInstance().sendSMS(
                        SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                        phone.toString(),
                        ImmutableMap.of("order",orderStr, "orderNum", orderNum));
            }
        }



        //自动生成付款单
        bizPoHeader = this.get(poHeaderIdid);
        //应付金额
        bizPoHeader.setPlanPay(prewPayTotal);
        //本次申请付款时间
        String deadLine = DateUtils.getDateTime();
        Date deadlineDate = DateUtils.parseDate(deadLine);
        bizPoHeader.setPayDeadline(deadlineDate);
        Pair<Boolean, String> audit = this.genPaymentOrder(bizPoHeader);

        //短信通知采销经理，付款单确认支付金额
        StringBuilder phone2 = new StringBuilder();
        User sendUser=new User(systemService.getRoleByEnname(RoleEnNameEnum.MARKETINGMANAGER.getState()));
        List<User> userList = systemService.findUser(sendUser);
        if (CollectionUtils.isNotEmpty(userList)) {
            for (User u : userList) {
                phone2.append(u.getMobile()).append(",");
            }
        }
        if (StringUtils.isNotBlank(phone2.toString())) {
            String orderStr = "";
            if (soType == Byte.parseByte("1")) {
                orderStr = "订单待确认支付金额";
            } else {
                orderStr = "备货清单待确认支付金额";
            }

            AliyunSmsClient.getInstance().sendSMS(
                    SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                    phone2.toString(),
                    ImmutableMap.of("order",orderStr, "orderNum", orderNum));
        }


        return audit;
    }

    public Byte getBizPoOrderReqByPo(BizPoHeader bizPoHeader) {
        List<BizPoOrderReq> poOrderReqs = bizPoOrderReqService.getByPo(bizPoHeader.getId());
        Byte result = 0;
        if (poOrderReqs.size() > 0) {
            result = poOrderReqs.get(0).getSoType();
        }
        return result;
    }

    //获取自动生成采购单所需的必要信息
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> goListForAutoSave(Integer orderId, String type, String lastPayDateVal, HttpServletRequest request,HttpServletResponse response) throws ParseException {
        Pair<Boolean, String> result = Pair.of(Boolean.FALSE, "自动生成采购单失败");
        if (REQUEST_HEADER_TYPE.equals(type)) {
            BizRequestHeader requestHeader = new BizRequestHeader();
            requestHeader.setId(orderId);
            //Page<BizRequestHeader> requestHeaderList = findBizRequestV3(requestHeader,request,response);
            Page<BizRequestHeader> requestHeaderList = bizRequestHeaderService.pageFindListV3(new Page<BizRequestHeader>(request, response), requestHeader);
            if (requestHeaderList.getList().size() > 0) {
                requestHeader = requestHeaderList.getList().get(0);
                String reqDetailIds = requestHeader.getReqDetailIds();
                String vendorId = String.valueOf(requestHeader.getOnlyVendor());
                String unitPrices = "";
                String ordQtys = "";
                Map<String,List<BizRequestDetail>> reqDetailMap = new LinkedHashMap<>();
                if (StringUtils.isNotBlank(reqDetailIds)) {
                    String[] reqDetailArr = reqDetailIds.split(",");
                    for (int i = 0; i < reqDetailArr.length; i++) {
                        if (StringUtils.isBlank(reqDetailArr[i])){
                            continue;
                        }
                        BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailArr[i].trim()));
                        Integer reqQty = bizRequestDetail.getReqQty();
                        Integer recvQty = bizRequestDetail.getRecvQty();
                        Integer ordQty = reqQty - recvQty;
                        ordQtys += ordQty + ",";
                        Integer key =bizRequestDetail.getRequestHeader().getId();
                        Integer lineNo=bizRequestDetail.getLineNo();
                        BizPoOrderReq bizPoOrderReq =new BizPoOrderReq();
                        bizPoOrderReq.setSoLineNo(lineNo);
                        bizPoOrderReq.setRequestHeader(bizRequestDetail.getRequestHeader());
                        bizPoOrderReq.setIsPrew(0);
                        List<BizPoOrderReq> poOrderReqList=bizPoOrderReqService.findList(bizPoOrderReq);
                        if(poOrderReqList!=null && poOrderReqList.size()==0){
                            BizSkuInfo sku = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
                            BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizRequestDetail.getRequestHeader().getId());
                            bizRequestDetail.setRequestHeader(bizRequestHeader);
                            BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                            Double buyPrice = skuInfo.getBuyPrice();
                            unitPrices += String.valueOf(buyPrice) + ",";
                            bizRequestDetail.setSkuInfo(skuInfo);
                            if(reqDetailMap.containsKey(key.toString())){
                                List<BizRequestDetail> requestDetails = reqDetailMap.get(key.toString());
                                requestDetails.add(bizRequestDetail);
                                reqDetailMap.put(key.toString(),requestDetails);
                            }else {
                                List<BizRequestDetail> requestDetails =  new ArrayList<>();
                                requestDetails.add(bizRequestDetail);
                                reqDetailMap.put(key.toString(),requestDetails);
                            }
                        }
                    }
                    ordQtys = ordQtys.substring(0, ordQtys.length()-1);
                    unitPrices = unitPrices.substring(0, unitPrices.length()-1);
                }

                result = this.autoSave(reqDetailIds,"", vendorId, unitPrices, ordQtys, lastPayDateVal, orderId, REQUEST_HEADER_TYPE);
            }
        } else if (DO_ORDER_HEADER_TYPE.equals(type)) {
            BizOrderHeader orderHeader = new BizOrderHeader();
            orderHeader.setId(orderId);
            //orderHeader.setBizStatusStart(OrderHeaderBizStatusEnum.SUPPLYING.getState());
            //orderHeader.setBizStatusEnd(OrderHeaderBizStatusEnum.PURCHASING.getState());
            Page<BizOrderHeader>  bizOrderHeaderList =bizOrderHeaderService.pageFindListV3(new Page<BizOrderHeader>(request, response), orderHeader);
            if (bizOrderHeaderList.getList().size() > 0) {
                orderHeader = bizOrderHeaderList.getList().get(0);
                String orderDetailIds = orderHeader.getOrderDetails();
                String vendorId =  String.valueOf(orderHeader.getOnlyVendor());

                String unitPrices = "";
                String ordQtys = "";
                Map<String, List<BizOrderDetail>> orderDetailMap = new LinkedHashMap<>();
                if (StringUtils.isNotBlank(orderDetailIds)) {
                    String[] orderDetailArr = orderDetailIds.split(",");
                    for (int i = 0; i < orderDetailArr.length; i++) {
                        if (StringUtils.isBlank(orderDetailArr[i])) {
                            continue;
                        }
                        BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(orderDetailArr[i].trim()));
                        Integer ordQty = bizOrderDetail.getOrdQty();
                        Integer sentQty = bizOrderDetail.getSentQty();
                        Integer ordHerderQty = ordQty - sentQty;
                        ordQtys += ordHerderQty + ",";

                        Integer key = bizOrderDetail.getOrderHeader().getId();
                        Integer lineNo = bizOrderDetail.getLineNo();
                        BizPoOrderReq bizPoOrderReq = new BizPoOrderReq();
                        bizPoOrderReq.setSoLineNo(lineNo);
                        bizPoOrderReq.setOrderHeader(bizOrderDetail.getOrderHeader());
                        bizPoOrderReq.setIsPrew(0);
                        List<BizPoOrderReq> poOrderReqList = bizPoOrderReqService.findList(bizPoOrderReq);
                        if (poOrderReqList != null && poOrderReqList.size() == 0) {
                            BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderDetail.getOrderHeader().getId());
                            bizOrderDetail.setOrderHeader(bizOrderHeader);
                            BizSkuInfo sku = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
                            BizSkuInfo skuInfo = bizSkuInfoService.findListProd(sku);
                            Double buyPrice = skuInfo.getBuyPrice();
                            unitPrices += String.valueOf(buyPrice) + ",";
                            bizOrderDetail.setSkuInfo(skuInfo);
                            if (orderDetailMap.containsKey(key.toString())) {
                                List<BizOrderDetail> orderDetails = orderDetailMap.get(key.toString());
                                orderDetails.add(bizOrderDetail);
                                orderDetailMap.put(key.toString(), orderDetails);
                            } else {
                                List<BizOrderDetail> orderDetails = new ArrayList<>();
                                orderDetails.add(bizOrderDetail);
                                orderDetailMap.put(key.toString(), orderDetails);
                            }
                        }
                    }
                    ordQtys = ordQtys.substring(0, ordQtys.length() - 1);
                    unitPrices = unitPrices.substring(0, unitPrices.length() - 1);
                }
                result = this.autoSave("",orderDetailIds, vendorId, unitPrices, ordQtys, lastPayDateVal, orderId, DO_ORDER_HEADER_TYPE);
            }
        }
        return result;
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> autoSave(String reqDetailIds, String orderDetailIds, String vendorId, String unitPrices,
                                          String ordQtys, String lastPayDateVal, Integer orderId, String orderType) throws ParseException {
        Office vendOffice = new Office();
        vendOffice.setId(Integer.parseInt(vendorId));

        BizPoHeader bizPoHeader = new BizPoHeader();
        bizPoHeader.setVendOffice(vendOffice);
        if (reqDetailIds != null || !"".equals(reqDetailIds)) {
            bizPoHeader.setReqDetailIds(reqDetailIds);
        }
        if (orderDetailIds != null || !"".equals(orderDetailIds)) {
            bizPoHeader.setOrderDetailIds(orderDetailIds);
        }
        bizPoHeader.setUnitPrices(unitPrices);
        bizPoHeader.setOrdQtys(ordQtys);

        Set<Integer> poIdSet = this.findPrewPoHeader(bizPoHeader);
        if (poIdSet.size() == 1) {
            return Pair.of(Boolean.FALSE, "采购单已存在");
        }
        int deOfifceId = 0;
        if (bizPoHeader.getDeliveryOffice() != null && bizPoHeader.getDeliveryOffice().getId() != null) {
            deOfifceId = bizPoHeader.getDeliveryOffice().getId();
        }

        //生成采购单预览
        String poNo = "0";
        bizPoHeader.setOrderNum(poNo);
        bizPoHeader.setPlateformInfo(bizPlatformInfoService.get(1));
        //bizPoHeader.setIsPrew("prew".equals(prewStatus) ? 1 : 0);
        bizPoHeader.setIsPrew(0);
        Integer id = bizPoHeader.getId();
        this.save(bizPoHeader);
        if (id == null) {
            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.PURCHASEORDER.getDesc(), BizOrderStatusOrderTypeEnum.PURCHASEORDER.getState(), bizPoHeader.getId());
        }
        if (bizPoHeader.getOrderNum() == null || "0".equals(bizPoHeader.getOrderNum())) {
            poNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.PO, deOfifceId, bizPoHeader.getVendOffice().getId(), bizPoHeader.getId());
            bizPoHeader.setOrderNum(poNo);
            this.savePoHeader(bizPoHeader);
        }

        //确认生成采购单
        Integer poHeaderIdid = bizPoHeader.getId();
        bizPoHeader = this.get(poHeaderIdid);
        bizPoHeader.setDeliveryStatus(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (org.apache.commons.lang3.StringUtils.isBlank(lastPayDateVal)) {
            bizPoHeader.setLastPayDate(new Date());
        } else {
            Date lastPayDate = sdf.parse(lastPayDateVal);
            bizPoHeader.setLastPayDate(lastPayDate);
        }
        bizPoHeader.setIsPrew(0);
        bizPoHeader.setType("createPo");
        this.savePoHeader(bizPoHeader);

        //采购单开启审核，同时自动生成付款单
        Pair<Boolean, String> audit = this.autoSavePaymentOrder(poHeaderIdid, orderId, orderType);
        if (audit.getLeft().equals(Boolean.TRUE)) {
            String poId = String.valueOf(bizPoHeader.getId());
            return Pair.of(Boolean.TRUE, "采购单生成," + poId);
        }
        return Pair.of(Boolean.FALSE, "自动生成付款单失败");
    }

    //清楚现有的支付申请列表数据
    public void updatePoPayment(int poHeaderId) {
        //清空采购单中currentPaymentId
        BizPoHeader bizPoHeader = this.get(poHeaderId);
        this.updatePaymentOrderId(poHeaderId, null);
        //将采购单对应的所有付款单status设为0
        BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
        bizPoPaymentOrder.setPoHeaderId(poHeaderId);

        List<BizPoPaymentOrder> list = bizPoPaymentOrderService.findListByIdOrPoID(bizPoPaymentOrder);

        if (CollectionUtils.isNotEmpty(list)) {
            for (BizPoPaymentOrder poPaymentOrder : list) {
                bizPoPaymentOrderService.delete(poPaymentOrder);
            }
        }
    }

}
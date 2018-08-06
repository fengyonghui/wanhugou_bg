/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import com.google.common.collect.ImmutableMap;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.po.BizPoHeaderDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestExpandDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizProductStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
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
import com.wanhutong.backend.modules.enums.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private BizRequestExpandDao bizRequestExpandDao;


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

    private void updateProcessToInit(BizPoHeader bizPoHeader) {
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        PurchaseOrderProcessConfig.PurchaseOrderProcess purchaseOrderProcess = purchaseOrderProcessConfig.getProcessMap().get(purchaseOrderProcessConfig.getDefaultProcessId());

        CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
        commonProcessEntity.setObjectId(bizPoHeader.getId().toString());
        commonProcessEntity.setObjectName(BizPoHeaderService.DATABASE_TABLE_NAME);
        commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
        commonProcessService.save(commonProcessEntity);

        this.updateProcessId(bizPoHeader.getId(), commonProcessEntity.getId());
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
                if (bizOrderDetail.getOrderHeader() != null && bizOrderDetail.getOrderHeader().getOrderType() == Integer.parseInt(DefaultPropEnum.PURSEHANGER.getPropValue())) {
                    skuInfo.setBuyPrice(bizOrderDetail.getBuyPrice());
                }
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
            if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                //发货短信提醒
                sendSmsForDeliver(bizOrderHeader.getOrderNum(), "");
                //发货邮件提醒
                sendMailForDeliver(bizOrderHeader.getOrderNum(), "");
            }
        }
        for (Map.Entry<Integer, List<BizPoOrderReq>> entry : collectReq.entrySet()) {
            BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(entry.getKey());
            bizRequestDetail.setRequestHeader(bizRequestHeader);
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
            Integer bizStatus = bizRequestHeader.getBizStatus();
            if (requestDetailList.size() == entry.getValue().size()) {
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                    bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
                    bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
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
                        bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
                        bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                    } else {
                        bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.PURCHASING.getState());
                        bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                    }
                }
            }
            if (bizStatus == null || !bizStatus.equals(bizRequestHeader.getBizStatus())) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizRequestHeader.getId());
            }
            if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                //发货短信提醒
                sendSmsForDeliver("", bizRequestHeader.getReqNo());
                //发货邮件提醒
                sendMailForDeliver("", bizRequestHeader.getReqNo());
            }
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
        bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
        bizPoPaymentOrder.setTotal(bizPoHeader.getPlanPay());
        bizPoPaymentOrder.setDeadline(bizPoHeader.getPayDeadline());
        bizPoPaymentOrder.setProcessId(commonProcessEntity.getId());
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

        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        // 当前流程
        PurchaseOrderProcessConfig.PurchaseOrderProcess currentProcess = purchaseOrderProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
        // 下一流程
        PurchaseOrderProcessConfig.PurchaseOrderProcess nextProcess = purchaseOrderProcessConfig.getProcessMap().get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? currentProcess.getPassCode() : currentProcess.getRejectCode());
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
        commonProcessService.save(cureentProcessEntity);

        CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
        nextProcessEntity.setObjectId(String.valueOf(id));
        nextProcessEntity.setObjectName(BizPoHeaderService.DATABASE_TABLE_NAME);
        nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
        nextProcessEntity.setPrevId(cureentProcessEntity.getId());
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
    public Pair<Boolean, String> auditPay(int id, String currentType, int auditType, String description, BigDecimal money) {
        BizPoPaymentOrder bizPoPaymentOrder = bizPoPaymentOrderService.get(id);
        BizPoHeader bizPoHeader = new BizPoHeader();
        BizRequestHeader bizRequestHeader = new BizRequestHeader();
        if (PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getType())) {
            bizRequestHeader = bizRequestHeaderForVendorService.get(bizPoPaymentOrder.getPoHeaderId());
        } else if (PoPayMentOrderTypeEnum.PO_TYPE.getType().equals(bizPoPaymentOrder.getType())) {
            bizPoHeader = this.get(bizPoPaymentOrder.getPoHeaderId());
        }
        CommonProcessEntity cureentProcessEntity = bizPoPaymentOrder.getCommonProcess();
        if (cureentProcessEntity == null) {
            return Pair.of(Boolean.FALSE,  "操作失败,当前订单无审核状态!");
        }
        cureentProcessEntity = commonProcessService.get(cureentProcessEntity.getId());
        if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
            LOGGER.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}][{}]", id, currentType, bizPoPaymentOrder.getType());
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
                if (role.getEndMoney().compareTo(money) > 0 && role.getStartMoney().compareTo(money) <= 0) {
                    nextMoneyRole = role;
                }
            }

            if (nextMoneyRole != null) {
                StringBuilder phone = new StringBuilder();
                for (String s : nextMoneyRole.getRoleEnNameEnum()) {
                    List<User> userList = systemService.findUser(new User(systemService.getRoleByEnname(s)));
                    if (CollectionUtils.isNotEmpty(userList)) {
                        for (User u : userList) {
                            phone.append(u.getMobile()).append(",");
                        }
                    }
                }

                if (StringUtils.isNotBlank(phone.toString()) && PoPayMentOrderTypeEnum.PO_TYPE.getType().equals(bizPoPaymentOrder.getType())) {
                    AliyunSmsClient.getInstance().sendSMS(
                            SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                            phone.toString(),
                            ImmutableMap.of("order", "采购单支付", "orderNum", bizPoHeader.getOrderNum()));
                }
                if (StringUtils.isNotBlank(phone.toString()) && PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getType())) {
                    AliyunSmsClient.getInstance().sendSMS(
                            SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                            phone.toString(),
                            ImmutableMap.of("order", "备货单支付", "orderNum", bizRequestHeader.getReqNo()));
                }

            }
        } catch (Exception e) {
            if (PoPayMentOrderTypeEnum.PO_TYPE.getType().equals(bizPoPaymentOrder.getType())) {
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
            if (PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getType())) {
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
    public Pair<Boolean, String> startAudit(int id, Boolean prew, BigDecimal prewPayTotal, Date prewPayDeadline, int auditType, String desc) {
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
        this.updateProcessToInit(bizPoHeader);
        auditPo(id, String.valueOf(purchaseOrderProcessConfig.getDefaultProcessId()), auditType, desc);
        return Pair.of(Boolean.TRUE,   "操作成功!");
    }

    /**
     * 支付订单
     *
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String payOrder(Integer poHeaderId,Integer reqHeaderId, Integer paymentOrderId, BigDecimal payTotal, String img) {
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

        if (bizPoPaymentOrder.getBizStatus() != BizPoPaymentOrder.BizStatus.NO_PAY.getStatus() && PoPayMentOrderTypeEnum.PO_TYPE.getType().equals(bizPoPaymentOrder.getType())) {
            LOGGER.warn("[exception]BizPoHeaderController payOrder currentType mismatching [{}][{}][{}]", poHeaderId, paymentOrderId, bizPoPaymentOrder.getType());
            return "操作失败,当前状态有误!";
        } else if (bizPoPaymentOrder.getBizStatus() != BizPoPaymentOrder.BizStatus.NO_PAY.getStatus() && PoPayMentOrderTypeEnum.REQ_TYPE.getType().equals(bizPoPaymentOrder.getType())) {
            LOGGER.warn("[exception]BizRequestHeaderController payOrder currentType mismatching [{}][{}][{}]", reqHeaderId, paymentOrderId, bizPoPaymentOrder.getType());
            return "操作失败,当前状态有误!";
        }

        bizPoPaymentOrder.setPayTotal(payTotal);
        bizPoPaymentOrder.setImg(img);
        bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.ALL_PAY.getStatus());
        bizPoPaymentOrder.setPayTime(new Date());
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
            incrPayTotal(bizPoHeader.getId(), payTotal);

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
                AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.ORDER_DELIVER.getCode(), phones.toString(), ImmutableMap.of("order", "订单"));
//                AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.ORDER_DELIVER.getCode(), "17703313909", ImmutableMap.of("order", "订单","number",orderNum.substring(3)));
            }
            if (StringUtils.isNotBlank(reqNum)) {
                AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.ORDER_DELIVER.getCode(), phones.toString(), ImmutableMap.of("order", "备货单"));
//                AliyunSmsClient.getInstance().sendSMS(SmsTemplateCode.ORDER_DELIVER.getCode(), "17703313909", ImmutableMap.of("order", "备货单","number",reqNum.substring(3)));
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
}
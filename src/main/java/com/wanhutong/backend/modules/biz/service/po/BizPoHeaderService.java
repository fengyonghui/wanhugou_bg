/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.dao.po.BizPoHeaderDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.request.BizPoOrderReq;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderDetailService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.request.BizPoOrderReqService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV2Service;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.PurchaseOrderProcessConfig;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.PoOrderReqTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private BizPoPaymentOrderService bizPoPaymentOrderService;
    @Autowired
    private CommonProcessService commonProcessService;


    /**
     * 默认表名
     */
    public static final String DATABASE_TABLE_NAME = "biz_po_payment_order";

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
                && bizPoHeader.getCurrentPaymentId() > 0 ) {
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
                if (skuMap.containsKey(skuInfo.getId())) {
                    BizSkuInfo sku = skuMap.get(skuInfo.getId());
                    Integer ordQty = sku.getReqQty() + bizOrderDetail.getOrdQty() - bizOrderDetail.getSentQty();
                    sku.setReqQty(ordQty);
                    skuMap.put(skuInfo.getId(), sku);
                } else {
//                    BizSkuInfo sku = skuMap.get(skuInfo.getId());
                    skuInfo.setReqQty(bizOrderDetail.getOrdQty()-bizOrderDetail.getSentQty());
                    skuMap.put(skuInfo.getId(),skuInfo);
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
            poDetail.setPartNo(skuInfo.getPartNo());
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
            List<BizOrderDetail> orderDetailList = bizOrderDetailService.findList(bizOrderDetail);
            if (orderDetailList.size() == entry.getValue().size()) {
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                    bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());
                    bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
                }
            } else if (orderDetailList.size() > entry.getValue().size()) {
                bizPoOrderReq.setOrderHeader(bizOrderHeader);
                bizPoOrderReq.setRequestHeader(null);
                bizPoOrderReq.setPoHeader(null);
                bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.SO.getOrderType()));
                List<BizPoOrderReq> poOrderReqs = bizPoOrderReqService.findList(bizPoOrderReq);
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                    if (poOrderReqs.size() == orderDetailList.size()) {
                        bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ACCOMPLISH_PURCHASE.getState());
                        bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
                    } else {
                        bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.PURCHASING.getState());
                        bizOrderHeaderService.saveOrderHeader(bizOrderHeader);
                    }
                }
            }


        }
        for (Map.Entry<Integer, List<BizPoOrderReq>> entry : collectReq.entrySet()) {
            BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(entry.getKey());
            bizRequestDetail.setRequestHeader(bizRequestHeader);
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
            if (requestDetailList.size() == entry.getValue().size()) {
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                    bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
                    bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                }
            } else if (requestDetailList.size() > entry.getValue().size()) {
                bizPoOrderReq.setRequestHeader(bizRequestHeader);
                bizPoOrderReq.setOrderHeader(null);
                bizPoOrderReq.setPoHeader(null);
                bizPoOrderReq.setSoType(Byte.parseByte(PoOrderReqTypeEnum.RE.getOrderType()));
                List<BizPoOrderReq> poOrderReqs = bizPoOrderReqService.findList(bizPoOrderReq);
                if (bizPoHeader.getType() != null && "createPo".equals(bizPoHeader.getType())) {
                    if (poOrderReqs.size() == requestDetailList.size()) {
                        bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
                        bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                    } else {
                        bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.PURCHASING.getState());
                        bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
                    }
                }
            }

        }

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
    public String genPaymentOrder(BizPoHeader bizPoHeader) {
        if (bizPoHeader.getBizPoPaymentOrder() != null && bizPoHeader.getBizPoPaymentOrder().getId() != null && bizPoHeader.getBizPoPaymentOrder().getId() != 0) {
            return "操作失败,该订单已经有正在申请的支付单!";
        }

        BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
        bizPoPaymentOrder.setPoHeaderId(bizPoHeader.getId());
        bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.NO_PAY.getStatus());
        bizPoPaymentOrder.setTotal(bizPoHeader.getPlanPay());
        bizPoPaymentOrder.setDeadline(bizPoHeader.getPayDeadline());
        bizPoPaymentOrderService.save(bizPoPaymentOrder);

        bizPoHeader.setBizPoPaymentOrder(bizPoPaymentOrder);
        this.updatePaymentOrderId(bizPoHeader.getId(), bizPoPaymentOrder.getId());
        return "操作成功!";
    }

    /**
     * @param poHeaderId
     * @param currentType
     * @param auditType
     * @param description
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String audit(int poHeaderId, String currentType, int auditType, String description) {
        BizPoHeader bizPoHeader = this.get(poHeaderId);
        CommonProcessEntity cureentProcessEntity = bizPoHeader.getCommonProcess();
        if (cureentProcessEntity == null) {
            return "操作失败,当前订单无审核状态!";
        }
        cureentProcessEntity = commonProcessService.get(bizPoHeader.getCommonProcess().getId());
        if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
            LOGGER.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}]", poHeaderId, currentType);
            return "操作失败,当前审核状态异常!";
        }

        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        // 当前流程
        PurchaseOrderProcessConfig.PurchaseOrderProcess currentProcess = purchaseOrderProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
        // 下一流程
        PurchaseOrderProcessConfig.PurchaseOrderProcess nextProcess = purchaseOrderProcessConfig.getProcessMap().get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? currentProcess.getPassCode() : currentProcess.getRejectCode());
        if (nextProcess == null) {
            return "操作失败,当前流程已经结束!";
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
            return "操作失败,该用户没有权限!";
        }

        if (CommonProcessEntity.AuditType.PASS.getCode() != auditType && org.apache.commons.lang3.StringUtils.isBlank(description)) {
            return "请输入驳回理由!";
        }

        cureentProcessEntity.setBizStatus(auditType);
        cureentProcessEntity.setProcessor(user.getId().toString());
        cureentProcessEntity.setDescription(description);
        commonProcessService.save(cureentProcessEntity);

        CommonProcessEntity nextProcessEntity = new CommonProcessEntity();
        nextProcessEntity.setObjectId(bizPoHeader.getId().toString());
        nextProcessEntity.setObjectName(BizPoHeaderService.DATABASE_TABLE_NAME);
        nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
        nextProcessEntity.setPrevId(cureentProcessEntity.getId());
        commonProcessService.save(nextProcessEntity);
        this.updateProcessId(bizPoHeader.getId(), nextProcessEntity.getId());

        if (nextProcess.getCode() == purchaseOrderProcessConfig.getPayProcessId()) {
            this.updateBizStatus(bizPoHeader.getId(), BizPoHeader.BizStatus.PROCESS_COMPLETE);
        }

        return "操作成功!";
    }

    /**
     * 开始审核流程
     *
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String startAudit(int id, Boolean prew, BigDecimal prewPayTotal, Date prewPayDeadline, int auditType, String desc) {
        PurchaseOrderProcessConfig purchaseOrderProcessConfig = ConfigGeneral.PURCHASE_ORDER_PROCESS_CONFIG.get();
        BizPoHeader bizPoHeader = this.get(id);
        if (bizPoHeader == null) {
            LOGGER.error("start audit error [{}]", id);
            return "操作失败!参数错误[id]";
        }

        if (prew) {
            bizPoHeader.setPayDeadline(prewPayDeadline);
            bizPoHeader.setPlanPay(prewPayTotal);
            this.genPaymentOrder(bizPoHeader);
        }
        this.updateBizStatus(bizPoHeader.getId(), BizPoHeader.BizStatus.PROCESS);
        this.updateProcessToInit(bizPoHeader);
        audit(id, String.valueOf(purchaseOrderProcessConfig.getDefaultProcessId()), auditType, desc);
        return "操作成功!";
    }

    /**
     * 支付订单
     *
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String payOrder(Integer poHeaderId, Integer paymentOrderId, BigDecimal payTotal, String img) {
        // 当前流程
        User user = UserUtils.getUser();
        Role role = new Role();
        role.setEnname(RoleEnNameEnum.FINANCE.getState());
        Role role1 = new Role();
        role1.setEnname(RoleEnNameEnum.TELLER.getState());
        if (!user.isAdmin() && !user.getRoleList().contains(role) && !user.getRoleList().contains(role1)) {
            return "操作失败,该用户没有权限!";
        }

        BizPoHeader bizPoHeader = this.get(poHeaderId);
        BizPoPaymentOrder bizPoPaymentOrder = bizPoPaymentOrderService.get(paymentOrderId);

        if (bizPoPaymentOrder.getBizStatus() != BizPoPaymentOrder.BizStatus.NO_PAY.getStatus()) {
            LOGGER.warn("[exception]BizPoHeaderController payOrder currentType mismatching [{}][{}]", poHeaderId, paymentOrderId);
            return "操作失败,当前状态有误!";
        }

        bizPoPaymentOrder.setPayTotal(payTotal);
        bizPoPaymentOrder.setImg(img);
        bizPoPaymentOrder.setBizStatus(BizPoPaymentOrder.BizStatus.ALL_PAY.getStatus());
        bizPoPaymentOrderService.save(bizPoPaymentOrder);


        // 状态改为全款或首款支付
//      DOWN_PAYMENT(1, "付款支付"),
//      ALL_PAY(2, "全部支付"),
        BigDecimal orderTotal = BigDecimal.valueOf(bizPoHeader.getTotalDetail()).add(BigDecimal.valueOf(bizPoHeader.getTotalExp())).add(BigDecimal.valueOf(bizPoHeader.getFreight()));

        this.updateBizStatus(bizPoHeader.getId(), bizPoHeader.getPayTotal().add(payTotal).compareTo(orderTotal) >= 0 ? BizPoHeader.BizStatus.ALL_PAY : BizPoHeader.BizStatus.DOWN_PAYMENT);

        incrPayTotal(bizPoHeader.getId(), payTotal);

        // 清除关联的支付申请单
        this.updatePaymentOrderId(bizPoHeader.getId(), null);
        return "操作成功!";
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int incrPayTotal(int id, BigDecimal payTotal) {
        return dao.incrPayTotal(id, payTotal);
    }

    /**
     * 更新流程ID
     * @param headerId
     * @param processId
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateProcessId(int headerId, int processId) {
        return dao.updateProcessId(headerId, processId);
	}

	public void findPoHeaderDetail(String orderDetailIds,String reqDetailIds) {
//		String orderDetailIds=bizPoHeader.getOrderDetailIds();
//		String reqDetailIds=bizPoHeader.getReqDetailIds();
		Map<Integer,BizSkuInfo> skuMap = new HashMap<>();
		if(StringUtils.isNotBlank(orderDetailIds)) {
			String[] orderDetailArr = orderDetailIds.split(",");
			for (String orderDetailId:orderDetailArr) {
				BizOrderDetail bizOrderDetail = bizOrderDetailService.get(Integer.parseInt(orderDetailId));
				BizSkuInfo skuInfo = bizSkuInfoService.get(bizOrderDetail.getSkuInfo().getId());
				if (skuMap.containsKey(skuInfo.getId())){
					BizSkuInfo sku = skuMap.get(skuInfo.getId());
					Integer ordQty = sku.getReqQty()+bizOrderDetail.getOrdQty()-bizOrderDetail.getSentQty();
					sku.setReqQty(ordQty);
					skuMap.put(skuInfo.getId(),sku);
				}else {
					skuInfo.setReqQty(bizOrderDetail.getOrdQty()-bizOrderDetail.getSentQty());
					skuMap.put(skuInfo.getId(),skuInfo);
				}

			}
		}
		if (StringUtils.isNotBlank(reqDetailIds)) {
			String[] reqDetailArr = reqDetailIds.split(",");
			for (String reqDetailId:reqDetailArr) {
				BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.parseInt(reqDetailId));
				BizSkuInfo skuInfo = bizSkuInfoService.get(bizRequestDetail.getSkuInfo().getId());
				if (skuMap.containsKey(skuInfo.getId())) {
					BizSkuInfo sku = skuMap.get(skuInfo.getId());
					Integer reqQty = sku.getReqQty()+bizRequestDetail.getReqQty()-bizRequestDetail.getRecvQty();
					sku.setReqQty(reqQty);
					skuMap.put(skuInfo.getId(),sku);
				}else {
					skuInfo.setReqQty(bizRequestDetail.getReqQty()-bizRequestDetail.getRecvQty());
					skuMap.put(skuInfo.getId(),skuInfo);
				}
			}
		}

	}

    public void getCommonProcessListFromDB(Integer id, List<CommonProcessEntity> list) {
        if(id == null || id == 0) {
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
}
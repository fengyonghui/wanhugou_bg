package com.wanhutong.backend.modules.sys.service;


import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoHeader;
import com.wanhutong.backend.modules.biz.entity.po.BizPoPaymentOrder;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoHeaderService;
import com.wanhutong.backend.modules.biz.service.po.BizPoPaymentOrderService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.biz.service.shelf.BizOpShelfSkuService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MyPanelService {

    @Autowired
    private BizOrderHeaderService bizOrderHeaderService;
    @Autowired
    private BizPoHeaderService bizPoHeaderService;
    @Autowired
    private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;
    @Autowired
    private BizPoPaymentOrderService bizPoPaymentOrderService;
    @Autowired
    private BizSkuInfoService bizSkuInfoService;


    /**
     * 订单审核
     *
     * @param roleName
     * @return
     */
    public int getOrderWaitAuditCount(String roleName) {
        //        originConfigMap.put("渠道经理", "渠道经理");
//        originConfigMap.put("总经理", "总经理");
//        originConfigMap.put("品类主管", "品类主管");
//        originConfigMap.put("财务经理", "财务经理");
//        originConfigMap.put("完成", "完成");
//        originConfigMap.put("驳回", "驳回");
//        originConfigMap.put("不需要审批", "不需要审批");
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        bizOrderHeader.setSelectAuditStatus(roleName);
        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<>(), bizOrderHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 订单审核  待同意发货
     *
     * @return
     */
    public int getOrderWaitAgreedDeliveryCount() {
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        bizOrderHeader.setMobileAuditStatus(0);
        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<>(), bizOrderHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 有尾款
     *
     * @return
     */
    public int getOrderHasRetainageCount() {
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        bizOrderHeader.setRetainage(1);
        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<>(), bizOrderHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 未排产
     *
     * @return
     */
    public int getWaitSchedulingCount() {
        BizPoHeader bizPoHeader = new BizPoHeader();
        bizPoHeader.setPoSchType(0);
        Page<BizPoHeader> page = bizPoHeaderService.findPage(new Page<>(), bizPoHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 可申请支付
     *
     * @return
     */
    public int getApplyPaymentCount() {
        BizPoHeader bizPoHeader = new BizPoHeader();
        bizPoHeader.setApplyPayment(1);
        Page<BizPoHeader> page = bizPoHeaderService.findPage(new Page<>(), bizPoHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 备货单审核
     *
     * @return
     */
    public int getReAuditCount(String processType) {
        BizRequestHeader bizRequestHeader = new BizRequestHeader();
        bizRequestHeader.setProcess(processType);
        Page<BizRequestHeader> page = bizRequestHeaderForVendorService.findPage(new Page<>(), bizRequestHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 备货单入库
     *
     * @return
     */
    public int getFhdrkCount() {
//        备货清单供货完成，供货中，收货中
        BizRequestHeader bizRequestHeader = new BizRequestHeader();
        bizRequestHeader.setBizStatusStart(ReqHeaderStatusEnum.STOCKING.getState().byteValue());
        bizRequestHeader.setBizStatusEnd(ReqHeaderStatusEnum.COMPLETEING.getState().byteValue());
        Page<BizRequestHeader> page = bizRequestHeaderForVendorService.findPage(new Page<>(), bizRequestHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 备货单待发货
     *
     * @return
     */
    public int getReWaitShipmentsCount() {
        BizRequestHeader bizRequestHeader = new BizRequestHeader();
        bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.ACCOMPLISH_PURCHASE.getState());
        Page<BizRequestHeader> page = bizRequestHeaderForVendorService.findPage(new Page<>(), bizRequestHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 订单待发货
     *
     * @return
     */
    public int getOrderWaitShipmentsCount() {
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        bizOrderHeader.setWaitShipments(1);
        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<>(), bizOrderHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 订单出库
     *
     * @return
     */
    public int getDdckCount() {
        BizOrderHeader bizOrderHeader = new BizOrderHeader();
        bizOrderHeader.setWaitOutput(1);
        Page<BizOrderHeader> page = bizOrderHeaderService.findPage(new Page<>(), bizOrderHeader);
        return CollectionUtils.isEmpty(page.getList()) ? 0 : page.getList().size();
    }

    /**
     * 付款单审核
     *
     * @return
     */
    public int getPaymentOrderAuditCount(String selectAuditStatus) {
//        configMap.put("供货部", "供货部");
//        configMap.put("财务总监", "财务总监");
//        configMap.put("财务总经理", "财务总经理");
//        configMap.put("完成", "完成");
//        configMap.put("驳回", "驳回");
        PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
        BizPoPaymentOrder bizPoPaymentOrder = new BizPoPaymentOrder();
        bizPoPaymentOrder.setSelectAuditStatus(selectAuditStatus);
        for (PaymentOrderProcessConfig.Process process : paymentOrderProcessConfig.getProcessList()) {
            if (StringUtils.isNotBlank(bizPoPaymentOrder.getSelectAuditStatus()) && process.getName().contains(bizPoPaymentOrder.getSelectAuditStatus())) {
                bizPoPaymentOrder.setAuditStatusCode(process.getCode());
            }
        }

        List<BizPoPaymentOrder> list = bizPoPaymentOrderService.findList(bizPoPaymentOrder);
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }

    /**
     * 付款单待付款
     *
     * @return
     */
    public int getOrderPaymentCount() {
        BizPoHeader bizPoHeader = new BizPoHeader();
        bizPoHeader.setWaitPay(1);
        List<BizPoHeader> list = bizPoHeaderService.findList(bizPoHeader);
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }

    /**
     * 未上架商品
     *
     * @return
     */
    public int getNeedPutawayCount() {
        BizSkuInfo bizSkuInfo = new BizSkuInfo();
        bizSkuInfo.setNotPutaway(1);
        List<BizSkuInfo> list = bizSkuInfoService.findList(bizSkuInfo);
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }
}

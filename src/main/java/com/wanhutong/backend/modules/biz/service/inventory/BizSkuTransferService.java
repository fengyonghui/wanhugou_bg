/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.entity.inventory.BizDetailInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryOrderRequest;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInvoice;
import com.wanhutong.backend.modules.biz.entity.inventory.BizOutTreasuryEntity;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.TransferProcessConfig;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.enums.SendGoodsRecordBizStatusEnum;
import com.wanhutong.backend.modules.enums.TransferStatusEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransfer;
import com.wanhutong.backend.modules.biz.dao.inventory.BizSkuTransferDao;

/**
 * 库存调拨Service
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
@Service
@Transactional(readOnly = true)
public class BizSkuTransferService extends CrudService<BizSkuTransferDao, BizSkuTransfer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BizSkuTransferService.class);

	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private BizSkuTransferDetailService bizSkuTransferDetailService;
	@Autowired
    private CommonProcessService commonProcessService;
	@Autowired
    private BizOrderStatusService bizOrderStatusService;
	@Autowired
    private SystemService systemService;
    @Autowired
    private BizInvoiceService bizInvoiceService;
    @Autowired
    private BizDetailInvoiceService bizDetailInvoiceService;
    @Autowired
    private BizInventorySkuService bizInventorySkuService;
    @Autowired
    private BizRequestDetailService bizRequestDetailService;
    @Autowired
    private BizSendGoodsRecordService bizSendGoodsRecordService;
    @Autowired
    private BizInventoryOrderRequestService bizInventoryOrderRequestService;

	public BizSkuTransfer get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSkuTransfer> findList(BizSkuTransfer bizSkuTransfer) {
		return super.findList(bizSkuTransfer);
	}
	
	public Page<BizSkuTransfer> findPage(Page<BizSkuTransfer> page, BizSkuTransfer bizSkuTransfer) {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(bizSkuTransfer.getSource()) && "out".equals(bizSkuTransfer.getSource())) {
			bizSkuTransfer.getSqlMap().put("transfer", BaseService.dataScopeFilter(user,"outCent","u"));
		}
		if (StringUtils.isNotBlank(bizSkuTransfer.getSource()) && "in".equals(bizSkuTransfer.getSource())) {
			bizSkuTransfer.getSqlMap().put("transfer", BaseService.dataScopeFilter(user,"inCent","u"));
		}
		return super.findPage(page, bizSkuTransfer);
	}
	
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(BizSkuTransfer bizSkuTransfer) {
		if (StringUtils.isBlank(bizSkuTransfer.getSkuIds()) || StringUtils.isBlank(bizSkuTransfer.getTransferNums())) {
			return;
		}
		BizSkuTransfer skuTransfer = new BizSkuTransfer();
		if (bizSkuTransfer.getId() == null) {
			//生成调拨单号
			BizInventoryInfo fromInv = bizInventoryInfoService.get(bizSkuTransfer.getFromInv());
			BizInventoryInfo toInv = bizInventoryInfoService.get(bizSkuTransfer.getToInv());
			int s = dao.findCountByToCent(toInv.getId());
			String transferNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.TR, fromInv.getCustomer().getId(), toInv.getCustomer().getId(), s + 1);
			skuTransfer.setTransferNo(transferNo);
		} else {
			skuTransfer.setId(bizSkuTransfer.getId());
		}
		skuTransfer.setFromInv(bizSkuTransfer.getFromInv());
		skuTransfer.setToInv(bizSkuTransfer.getToInv());
		skuTransfer.setApplyer(UserUtils.getUser());
		skuTransfer.setBizStatus(bizSkuTransfer.getBizStatus() == null ? TransferStatusEnum.UNREVIEWED.getState().byteValue() : bizSkuTransfer.getBizStatus());
		skuTransfer.setRecvEta(bizSkuTransfer.getRecvEta());
		skuTransfer.setRemark(bizSkuTransfer.getRemark());
		super.save(skuTransfer);
		//初始化审核状态
        CommonProcessEntity processEntity = new CommonProcessEntity();
        processEntity.setObjectId(String.valueOf(skuTransfer.getId()));
        processEntity.setObjectName(BizOrderStatusOrderTypeEnum.SKUTRANSFER.getDesc());
        processEntity.setCurrent(CommonProcessEntity.CURRENT);
        List<CommonProcessEntity> commonProcessEntityList = commonProcessService.findList(processEntity);
        if (CollectionUtils.isNotEmpty(commonProcessEntityList)) {
            commonProcessService.updateCurrentByObject(skuTransfer.getId(),BizOrderStatusOrderTypeEnum.SKUTRANSFER.getDesc(),CommonProcessEntity.NOT_CURRENT);
        }
        processEntity.setBizStatus(0);
        processEntity.setType(ConfigGeneral.SKU_TRANSFER_PROCESS_CONFIG.get().getDefaultProcessId().toString());
        commonProcessService.save(processEntity);
        //状态表
        if (bizSkuTransfer.getId() == null) {
            bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SKUTRANSFER.getDesc(), BizOrderStatusOrderTypeEnum.SKUTRANSFER.getState(), skuTransfer.getId());
        }
        if (bizSkuTransfer.getId() != null) {
            Byte bizStatus = bizSkuTransfer.getBizStatus();
            this.updateBizStatus(skuTransfer.getId(), TransferStatusEnum.UNREVIEWED);
            if (bizStatus == null || !bizStatus.equals(TransferStatusEnum.UNREVIEWED.getState().byteValue())) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SKUTRANSFER.getDesc(), BizOrderStatusOrderTypeEnum.SKUTRANSFER.getState(), skuTransfer.getId());
            }
        }
		//调拨单detail
		List<BizSkuTransferDetail> transferDetailList = Lists.newArrayList();
		if (bizSkuTransfer.getId() != null) {
			BizSkuTransferDetail bizSkuTransferDetail = new BizSkuTransferDetail();
			bizSkuTransferDetail.setTransfer(bizSkuTransfer);
			transferDetailList = bizSkuTransferDetailService.findList(bizSkuTransferDetail);
		}
		Map<Integer,Integer> map = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(transferDetailList)) {
			for (BizSkuTransferDetail transferDetail : transferDetailList) {
				map.put(transferDetail.getSkuInfo().getId(),transferDetail.getId());
			}
		}
		String skuIds = bizSkuTransfer.getSkuIds();
		String transferNums = bizSkuTransfer.getTransferNums();
		String[] skuIdAttr = StringUtils.split(skuIds, ",");
		String[] transferAttr = StringUtils.split(transferNums, ",");
		for (int i = 0; i < skuIdAttr.length; i++) {
			BizSkuTransferDetail bizSkuTransferDetail = new BizSkuTransferDetail();
			if (map.containsKey(Integer.valueOf(skuIdAttr[i]))) {
				bizSkuTransferDetail.setId(map.get(Integer.valueOf(skuIdAttr[i])));
			}
			bizSkuTransferDetail.setTransfer(skuTransfer);
			bizSkuTransferDetail.setSkuInfo(new BizSkuInfo(Integer.valueOf(skuIdAttr[i])));
			bizSkuTransferDetail.setTransQty(Integer.valueOf(transferAttr[i]));
			bizSkuTransferDetailService.save(bizSkuTransferDetail);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSkuTransfer bizSkuTransfer) {
		super.delete(bizSkuTransfer);
	}


    /**
     * 审核调拨单
     * @param id
     * @param currentType
     * @param auditType
     * @param description
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Pair<Boolean, String> auditTransfer(int id, String currentType, int auditType, String description) {
        BizSkuTransfer bizSkuTransfer = this.get(id);
        CommonProcessEntity cureentProcessEntity = bizSkuTransfer.getCommonProcess();
        return audit(id, currentType, auditType, description, cureentProcessEntity);
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

        TransferProcessConfig transferProcessConfig = new TransferProcessConfig();

        transferProcessConfig = ConfigGeneral.SKU_TRANSFER_PROCESS_CONFIG.get();
        // 当前流程
        com.wanhutong.backend.modules.config.parse.TransferProcessConfig.TransferProcess currentProcess = transferProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
        // 下一流程
        com.wanhutong.backend.modules.config.parse.TransferProcessConfig.TransferProcess nextProcess = transferProcessConfig.getProcessMap().get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? currentProcess.getPassCode() : currentProcess.getRejectCode());
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
        nextProcessEntity.setObjectName(BizSkuTransfer.DATABASE_TABLE_NAME);
        nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
        nextProcessEntity.setPrevId(cureentProcessEntity.getId());
        nextProcessEntity.setCurrent(CommonProcessEntity.CURRENT);
        commonProcessService.save(nextProcessEntity);

        BizSkuTransfer bizSkuTransfer = this.get(id);
        if (transferProcessConfig.getAutProcessId().equals(nextProcess.getCode())) {
            Byte bizStatus = bizSkuTransfer.getBizStatus();
            this.updateBizStatus(id, TransferStatusEnum.APPROVE);
            if (bizStatus == null || !bizStatus.equals(TransferStatusEnum.APPROVE.getState().byteValue())) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SKUTRANSFER.getDesc(), BizOrderStatusOrderTypeEnum.SKUTRANSFER.getState(), bizSkuTransfer.getId());
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
                        ImmutableMap.of("order", "调拨单", "orderNum", bizSkuTransfer.getTransferNo()));
            }
        } catch (Exception e) {
            LOGGER.error("[exception]TR审批短信提醒发送异常[TransferId:{}]", id, e);
            EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.COMMON_EXCEPTION.name());
            AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
                    String.format(email.getBody(),
                            "BizSkuTransferService:242",
                            e.toString(),
                            "TR审批短信提醒发送异常",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                    ));
        }

        return Pair.of(Boolean.TRUE,  "操作成功!");
    }

    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public int updateBizStatus(Integer id, TransferStatusEnum status) {
        return dao.updateBizStatus(id, status.getState());
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String outTreasury(BizInvoice bizInvoice, List<BizOutTreasuryEntity> outTreasuryList) {
        if (bizInvoice == null) {
            return "error";
        }
        bizInvoiceService.save(bizInvoice);
        for (BizOutTreasuryEntity outTreasuryEntity : outTreasuryList) {
            Integer transferDetailId = outTreasuryEntity.getTransferDetailId();
            BizSkuTransferDetail transferDetail = bizSkuTransferDetailService.get(transferDetailId);
            BizSkuTransfer skuTransfer = this.get(transferDetail.getTransfer().getId());
            if (StringUtils.isNotBlank(skuTransfer.getTransferNo())) {
                BizDetailInvoice bizDetailInvoice = new BizDetailInvoice();
                bizDetailInvoice.setInvoice(bizInvoice);
                bizDetailInvoice.setBizSkuTransfer(skuTransfer);
                bizDetailInvoiceService.save(bizDetailInvoice);
                break;
            }
        }
        boolean orderFlag = true; //用于判断调拨单是否已全部出库
        BizSkuTransfer skuTransfer = new BizSkuTransfer();
        Map<Integer, Integer> transferDetailMap = Maps.newHashMap();
        for (BizOutTreasuryEntity outTreasuryEntity : outTreasuryList) {
            Integer transferDetailId = outTreasuryEntity.getTransferDetailId();
            Integer reqDetailId = outTreasuryEntity.getReqDetailId();
            Integer invId = outTreasuryEntity.getInvSkuId();
            Integer outQty = outTreasuryEntity.getOutQty();
            Integer version = outTreasuryEntity.getuVersion();
            BizSkuTransferDetail transferDetail = bizSkuTransferDetailService.get(transferDetailId);
            BizInventorySku inventorySku = bizInventorySkuService.get(invId);
            BizRequestDetail bizRequestDetail = bizRequestDetailService.get(reqDetailId);
            skuTransfer = this.get(transferDetail.getTransfer().getId());
            if (!version.equals(inventorySku.getuVersion())) {
                return "其他人正在出库，请刷新页面重新操作";
            }
            //生成发货单
            BizSendGoodsRecord bsgr = new BizSendGoodsRecord();
            bsgr.setSendNo(outTreasuryEntity.getSendNo());
            bsgr.setSkuInfo(transferDetail.getSkuInfo());
            bsgr.setInvOldNum(inventorySku.getStockQty());
            bsgr.setInvInfo(inventorySku.getInvInfo());
            bsgr.setBizSkuTransfer(transferDetail.getTransfer());
            bsgr.setOrderNum(skuTransfer.getTransferNo());
            bsgr.setBizStatus(SendGoodsRecordBizStatusEnum.CENTER.getState());
            bsgr.setSendNum(outQty);
            BizInventoryInfo inventoryInfo = bizInventoryInfoService.get(skuTransfer.getToInv());
            bsgr.setCustomer(inventoryInfo.getCustomer());
            bsgr.setSendDate(new Date());
            bizSendGoodsRecordService.save(bsgr);
            //修改库存数量
            bizInventorySkuService.updateStockQty(inventorySku,inventorySku.getStockQty() - outQty);
            //修改备货单详情已出库数量
            bizRequestDetail.setOutQty((bizRequestDetail.getOutQty() == null ? 0 : bizRequestDetail.getOutQty()) + outQty);
            bizRequestDetailService.save(bizRequestDetail);
            //修改订单详情已发货数量,出库人和出库时间
            transferDetail.setOutQty((transferDetail.getOutQty() == null ? 0 : transferDetail.getOutQty()) + outQty);
            transferDetail.setFromInvOp(UserUtils.getUser());
            transferDetail.setFromInvOpTime(new Date());
            bizSkuTransferDetailService.save(transferDetail);
            if (transferDetailMap.containsKey(transferDetail.getId())) {
                transferDetailMap.put(transferDetail.getId(),transferDetailMap.get(transferDetail.getId()) + outQty);
            } else {
                transferDetailMap.put(transferDetail.getId(), transferDetail.getOutQty());
            }
            //修改订单状态
            int status = skuTransfer.getBizStatus();
            skuTransfer.setBizStatus(TransferStatusEnum.OUTING_WAREHOUSE.getState().byteValue());
            super.save(skuTransfer);
            if (status != TransferStatusEnum.OUTING_WAREHOUSE.getState()) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SKUTRANSFER.getDesc(),BizOrderStatusOrderTypeEnum.SKUTRANSFER.getState(),skuTransfer.getId());
            }
            //订单关联出库备货单
            BizInventoryOrderRequest ior = new BizInventoryOrderRequest();
            ior.setInvSku(inventorySku);
            ior.setTransferDetail(transferDetail);
            ior.setRequestDetail(bizRequestDetail);
            List<BizInventoryOrderRequest> iorList = bizInventoryOrderRequestService.findList(ior);
            if (CollectionUtils.isEmpty(iorList)) {
                bizInventoryOrderRequestService.save(ior);
            }
        }
        //是否出库完成
        BizSkuTransferDetail transferDetail = new BizSkuTransferDetail();
        transferDetail.setTransfer(new BizSkuTransfer(skuTransfer.getId()));
        List<BizSkuTransferDetail> detailList = bizSkuTransferDetailService.findList(transferDetail);
        if (CollectionUtils.isNotEmpty(detailList)) {
            for (BizSkuTransferDetail bizSkuTransferDetail : detailList) {
                Integer transQty = bizSkuTransferDetail.getTransQty();
                Integer outQty = bizSkuTransferDetail.getOutQty();
                Integer id = bizSkuTransferDetail.getId();
                if (transferDetailMap.containsKey(id) && !transferDetailMap.get(id).equals(transQty)) {
                    orderFlag = false;
                    break;
                }
                if (!transferDetailMap.containsKey(id) && !transQty.equals(outQty)) {
                    orderFlag = false;
                    break;
                }
            }
        }
        if (orderFlag) {
            int status = skuTransfer.getBizStatus();
            skuTransfer.setBizStatus(TransferStatusEnum.ALREADY_OUT_WAREHOUSE.getState().byteValue());
            super.save(skuTransfer);
            if (status != TransferStatusEnum.ALREADY_OUT_WAREHOUSE.getState()) {
                bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.SKUTRANSFER.getDesc(),BizOrderStatusOrderTypeEnum.SKUTRANSFER.getState(),skuTransfer.getId());
            }
        }
        return "ok";
    }
	
}
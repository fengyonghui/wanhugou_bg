/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.inventory.BizCollectGoodsRecordDao;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInventorySkuDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestDetailDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestHeaderForVendorDao;
import com.wanhutong.backend.modules.biz.dao.sku.BizSkuInfoV3Dao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventoryviewlog.BizInventoryViewLogService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.InventorySkuRequestProcessConfig;
import com.wanhutong.backend.modules.enums.InventorySkuTypeEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.ReqFromTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 商品库存详情Service
 * @author 张腾飞
 * @version 2017-12-29
 */
@Service
@Transactional(readOnly = true)
public class BizInventorySkuService extends CrudService<BizInventorySkuDao, BizInventorySku> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BizInventorySkuService.class);
	@Autowired
	private BizInventorySkuDao bizInventorySkuDao;
	@Autowired
	private BizCollectGoodsRecordDao bizCollectGoodsRecordDao;
	@Autowired
	private BizInventoryInfoService bizInventoryInfoService;
	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizRequestHeaderForVendorDao bizRequestHeaderForVendorDao;
	@Autowired
	private CommonProcessService commonProcessService;
	@Autowired
	private SystemService systemService;
	@Autowired
    private BizRequestDetailService bizRequestDetailService;
	@Autowired
	private BizInventoryViewLogService bizInventoryViewLogService;
	@Autowired
	private BizRequestDetailDao bizRequestDetailDao;
	@Autowired
	private OfficeDao officeDao;
	@Autowired
	private BizSkuInfoV3Dao bizSkuInfoDao;

	@Override
	public BizInventorySku get(Integer id) {
		return super.get(id);
	}

	@Override
	public List<BizInventorySku> findList(BizInventorySku bizInventorySku) {
		return super.findList(bizInventorySku);
	}

	@Override
	public Page<BizInventorySku> findPage(Page<BizInventorySku> page, BizInventorySku bizInventorySku) {
		return super.findPage(page, bizInventorySku);
	}

	@Override
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void save(BizInventorySku bizInventorySku) {
		bizInventorySku.setSkuType(InventorySkuTypeEnum.CENTER_TYPE.getType());
		if (bizInventorySku.getStockQty()<0){
			return;
		}
		List<BizInventorySku> invSkuList = bizInventorySkuDao.findList(bizInventorySku);
		BizInventorySku invSku = new BizInventorySku();
		if (invSkuList != null && !invSkuList.isEmpty() && bizInventorySku.getId() == null){
			invSku = invSkuList.get(0);
			invSku.setStockQty(invSku.getStockQty() + bizInventorySku.getStockQty());
			super.save(invSku);
		} else {
			super.save(bizInventorySku);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(BizInventorySku bizInventorySku) {
		super.delete(bizInventorySku);
	}

	@Transactional(readOnly = false)
	public void orderSave(BizInventorySku bizInventorySku) {
		bizInventorySkuDao.orderUpdate(bizInventorySku);
	}

	/**
	 * 查询唯一商品库存
	 * @param inventorySku
	 * @return
	 */
	public BizInventorySku findOnly(BizInventorySku inventorySku){
		return bizInventorySkuDao.findOnly(inventorySku);
	}

	/**
	 * 统计采购中心库存总数
	 * @param centId
	 * @return
	 */
	public Integer invSkuCount(Integer centId){
		return bizInventorySkuDao.invSkuCount(centId);
	}

	/**
	 * 取SKU库龄数据
	 * @param skuId skuId
	 * @return
	 */
	public Map<String, Object> getInventoryAge(Integer skuId, Integer centId, Integer invInfoId) {
		Map<String, Object> result = Maps.newHashMap();

		BizInventoryInfo inventoryInfo = bizInventoryInfoService.get(invInfoId);
		BizSkuInfo bizSku = bizSkuInfoService.get(skuId);

		Integer skuIdInvId = bizInventorySkuDao.getStockQtyBySkuIdInvId(skuId, invInfoId);
		if (skuIdInvId == null || skuIdInvId == 0) {
			return result;
		}

		List<BizCollectGoodsRecord> recordList = bizCollectGoodsRecordDao.getListBySkuIdInvId(skuId, invInfoId);

		recordList.sort((o1, o2) -> o2.getReceiveDate().compareTo(o1.getReceiveDate()));
		Map<BizCollectGoodsRecord, Integer> resultRecordList = Maps.newLinkedHashMap();

		int counter = skuIdInvId;
		String skuName = bizSku.getName();
		String invName = inventoryInfo.getName();
		String itemNo = bizSku.getItemNo();

		for (BizCollectGoodsRecord bizCollectGoodsRecord : recordList) {
			if (counter <=0) {
				break;
			}
			counter -= bizCollectGoodsRecord.getReceiveNum();
			resultRecordList.put(bizCollectGoodsRecord, counter <=0 ? bizCollectGoodsRecord.getReceiveNum() + counter : bizCollectGoodsRecord.getReceiveNum());
		}
		if (counter > 0) {
			BizCollectGoodsRecord bizCollectGoodsRecord = new BizCollectGoodsRecord();
			BizInventoryInfo bizInventoryInfo = new BizInventoryInfo();
			bizInventoryInfo.setName(invName);
			bizCollectGoodsRecord.setInvInfo(bizInventoryInfo);
			BizSkuInfo bizSkuInfo = new BizSkuInfo();
			bizSkuInfo.setName(skuName);
			bizSkuInfo.setItemNo(itemNo);
			bizCollectGoodsRecord.setSkuInfo(bizSkuInfo);
			resultRecordList.put(bizCollectGoodsRecord, counter);
		}

		result.put("stockQty", skuIdInvId);
		result.put("recordList", recordList);
		result.put("resultRecordList", resultRecordList);
		return result;
	}

	/**
	 * 根据SKU和采购中心和仓库商品类型取库存数量
	 * @param skuId
	 * @param centId
	 * @param skuType
	 * @return
	 */
	public Integer getStockQtyBySkuIdCentIdSkuType(Integer skuId, Integer centId, Integer skuType) {
		return dao.getStockQtyBySkuIdCentIdSkuType(skuId, centId, skuType);
	}
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void updateStockQty(BizInventorySku inventorySku, Integer stockQty) {
		dao.updateStockQty(inventorySku.getId(),stockQty);
	}

	/**
	 * 库存详情
	 * @param invSkuId
	 * @param skuId
	 */
	public List<BizCollectGoodsRecord> getInventoryDetail(Integer invSkuId, Integer skuId) {
		return bizCollectGoodsRecordDao.getInventoryDetail(invSkuId, skuId);
	}

	/**
	 * 分页盘点
	 * @param page
	 * @param requestHeader
	 * @return
	 */
	public Page<BizRequestHeader> inventoryPage(Page<BizRequestHeader> page, BizRequestHeader requestHeader) {
		User user = UserUtils.getUser();
		requestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "cent","su"));
		requestHeader.setPage(page);
		page.setList(bizRequestHeaderForVendorDao.inventoryPage(requestHeader));
		return page;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void inventorySave(BizRequestHeader requestHeader) {
		BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorDao.get(requestHeader.getId());
		String invReqDetail = requestHeader.getInvReqDetail();
		Integer requestHeaderId = 0;
		String[] invReqDetailArr = invReqDetail.split(",");
		for(int i = 0; i < invReqDetailArr.length; i++) {
			String[] split = invReqDetailArr[i].split("-");
			if (split.length ==2) {
				BizRequestDetail bizRequestDetail = bizRequestDetailService.get(Integer.valueOf(split[0]));
				bizRequestDetail.setActualQty(Integer.valueOf(split[1]));
				bizRequestDetailService.save(bizRequestDetail);
				requestHeaderId = bizRequestDetail.getRequestHeader().getId();
			}
		}

		CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
		commonProcessEntity.setObjectId(requestHeaderId.toString());
		commonProcessEntity.setObjectName(BizInventorySku.INVSKUREQUESTTABLE);
//		commonProcessEntity.setType(ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().getAutProcessId().toString());
		List<CommonProcessEntity> commonProcessEntityList = commonProcessService.findList(commonProcessEntity);
		if (CollectionUtils.isEmpty(commonProcessEntityList)) {
			commonProcessEntity.setCurrent(CommonProcessEntity.CURRENT);
			commonProcessEntity.setBizStatus(CommonProcessEntity.NOT_CURRENT);
			commonProcessEntity.setType(ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().getDefaultProcessId().toString());
			commonProcessService.save(commonProcessEntity);
		}
		if (CollectionUtils.isNotEmpty(commonProcessEntityList)) {
			for (CommonProcessEntity processEntity : commonProcessEntityList) {
				if (ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().getAutProcessId().toString().equals(processEntity.getType()) && CommonProcessEntity.CURRENT.equals(processEntity.getCurrent())) {
					processEntity.setCurrent(CommonProcessEntity.NOT_CURRENT);
					processEntity.setBizStatus(CommonProcessEntity.AuditType.PASS.getCode());
					commonProcessService.save(processEntity);
					commonProcessEntity.setCurrent(CommonProcessEntity.CURRENT);
					commonProcessEntity.setBizStatus(CommonProcessEntity.NOT_CURRENT);
					commonProcessEntity.setType(ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().getDefaultProcessId().toString());
					commonProcessEntity.setPrevId(processEntity.getId());
					commonProcessService.save(commonProcessEntity);
					break;
				}
			}
		}
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Pair<Boolean, String> auditInventory(int id, int invId, String currentType, int auditType, String description) {
		BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorDao.get(id);
		CommonProcessEntity cureentProcessEntity = bizRequestHeader.getInvCommonProcess();
		return audit(id, invId, currentType, auditType, description, cureentProcessEntity);
	}

	public Pair<Boolean, String> audit(int id, int invId, String currentType, int auditType, String description, CommonProcessEntity cureentProcessEntity) {
		if (cureentProcessEntity == null) {
			return Pair.of(Boolean.FALSE, "操作失败,当前订单无审核状态!");
		}
		cureentProcessEntity = commonProcessService.get(cureentProcessEntity.getId());
		if (!cureentProcessEntity.getType().equalsIgnoreCase(currentType)) {
			LOGGER.warn("[exception]BizPoHeaderController audit currentType mismatching [{}][{}]", id, currentType);
			return Pair.of(Boolean.FALSE,  "操作失败,当前审核状态异常!");
		}
		InventorySkuRequestProcessConfig inventorySkuRequestProcessConfig = ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get();
		// 当前流程
		InventorySkuRequestProcessConfig.InvRequestProcess currentProcess = inventorySkuRequestProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
		// 下一流程
		InventorySkuRequestProcessConfig.InvRequestProcess nextProcess = inventorySkuRequestProcessConfig.getProcessMap().get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? currentProcess.getPassCode() : currentProcess.getRejectCode());

		if (nextProcess == null) {
			return Pair.of(Boolean.FALSE,  "操作失败,当前流程已经结束!");
		}
        //审核完成时，修改出库数量为收货数量-实际库存数
        if (ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().getAutProcessId().equals(nextProcess.getCode())) {
            BizRequestDetail bizRequestDetail = new BizRequestDetail();
            bizRequestDetail.setRequestHeader(new BizRequestHeader(id));
            List<BizRequestDetail> requestDetailList = bizRequestDetailService.findList(bizRequestDetail);
            if (CollectionUtils.isNotEmpty(requestDetailList)) {
                for (BizRequestDetail requestDetail : requestDetailList) {
                    if (requestDetail.getActualQty() == null) {
                        continue;
                    }
                    Integer outQty = requestDetail.getOutQty();
                    requestDetail.setOutQty(requestDetail.getRecvQty() - requestDetail.getActualQty());
                    bizRequestDetailService.save(requestDetail);
					BizInventorySku inventorySku = new BizInventorySku();
					BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorDao.get(id);
					inventorySku.setInvInfo(new BizInventoryInfo(invId));
					inventorySku.setSkuInfo(requestDetail.getSkuInfo());
					if (BizRequestHeader.HeaderType.ROUTINE.getHeaderType() == bizRequestHeader.getHeaderType().intValue()) {
						inventorySku.setInvType(BizInventorySku.InvType.ROUTINE.getInvType());
					}
					if (BizRequestHeader.HeaderType.SAMPLE.getHeaderType() == bizRequestHeader.getHeaderType().intValue()) {
						inventorySku.setInvType(BizInventorySku.InvType.SAMPLE.getInvType());
					}
					if (ReqFromTypeEnum.CENTER_TYPE.getType().equals(bizRequestHeader.getFromType())) {
						inventorySku.setSkuType(InventorySkuTypeEnum.CENTER_TYPE.getType());
					}
					if (ReqFromTypeEnum.VENDOR_TYPE.getType().equals(bizRequestHeader.getFromType())) {
						inventorySku.setSkuType(InventorySkuTypeEnum.VENDOR_TYPE.getType());
					}
					List<BizInventorySku> inventorySkuList = bizInventorySkuDao.findList(inventorySku);
					if (CollectionUtils.isNotEmpty(inventorySkuList)) {
						BizInventorySku invSku = inventorySkuList.get(0);
						int stock = invSku.getStockQty();
						Integer num = requestDetail.getRecvQty() - (outQty == null ? 0 : outQty) - requestDetail.getActualQty();
						invSku.setStockQty(stock - num);
						bizInventorySkuDao.update(invSku);
						//盘点记录
						if (stock != invSku.getStockQty()) {
							BizInventoryViewLog viewLog = new BizInventoryViewLog();
							viewLog.setInvInfo(new BizInventoryInfo(invId));
							viewLog.setInvType(inventorySku.getInvType());
							viewLog.setSkuInfo(requestDetail.getSkuInfo());
							viewLog.setStockQty(stock);
							viewLog.setStockChangeQty(-num);
							viewLog.setNowStockQty(invSku.getStockQty());
							viewLog.setRequestHeader(bizRequestHeader);
							bizInventoryViewLogService.save(viewLog);
						}
                }
            }

			}
        }

		User user = UserUtils.getUser();
		String s = currentProcess.getRoleEnNameEnum();
		boolean hasRole = false;
		RoleEnNameEnum roleEnNameEnum = RoleEnNameEnum.valueOf(s);
		Role role = new Role();
		role.setEnname(roleEnNameEnum.getState());
		if (user.getRoleList().contains(role)) {
			hasRole = true;
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
		nextProcessEntity.setObjectName(BizInventorySku.INVSKUREQUESTTABLE);
		nextProcessEntity.setType(String.valueOf(nextProcess.getCode()));
		nextProcessEntity.setPrevId(cureentProcessEntity.getId());
		nextProcessEntity.setCurrent(CommonProcessEntity.CURRENT);
		commonProcessService.save(nextProcessEntity);

		try {
			StringBuilder phone = new StringBuilder();
			List<User> userList = systemService.findUser(new User(systemService.getRoleByEnname(nextProcess.getRoleEnNameEnum())));
			if (CollectionUtils.isNotEmpty(userList)) {
                for (User u : userList) {
                    phone.append(u.getMobile()).append(",");
                }
            }
			if (StringUtils.isNotBlank(phone.toString())) {
                AliyunSmsClient.getInstance().sendSMS(
                        SmsTemplateCode.PENDING_AUDIT_1.getCode(),
                        phone.toString(),
                        ImmutableMap.of("order", "库存修改", "orderNum", bizRequestHeaderForVendorDao.get(id).getReqNo()));
            }
		} catch (Exception e) {
			LOGGER.error("[exception]库存修改审批短信提醒发送异常[requestHeaderId:{}]", id, e);
			EmailConfig.Email email = EmailConfig.getEmail(EmailConfig.EmailType.COMMON_EXCEPTION.name());
			AliyunMailClient.getInstance().sendTxt(email.getReceiveAddress(), email.getSubject(),
					String.format(email.getBody(),
							"BizInventorySkuService:290",
							e.toString(),
							"库存修改审批短信提醒发送异常",
							LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
					));
		}

		return Pair.of(Boolean.TRUE,  "操作成功!");
	}

	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void correctOutQty() {
		List<Integer> skuInfoList = bizSkuInfoDao.findReqSku();
		List<Office> centList = officeDao.findListByType(OfficeTypeEnum.PURCHASINGCENTER.getType());
		for (Office cent : centList) {
			for (Integer skuId : skuInfoList) {
				int stockTotal = bizInventorySkuDao.findStockTotal(cent.getId(), skuId) == null ? 0 : bizInventorySkuDao.findStockTotal(cent.getId(), skuId);
				if (stockTotal <= 0) {
					continue;
				}
				int recvTotal = bizRequestDetailDao.findRecvTotal(cent.getId(), skuId) == null ? 0 : bizRequestDetailDao.findRecvTotal(cent.getId(), skuId);
				int alOutQty = recvTotal - stockTotal;
				if (alOutQty < 0) {
					continue;
				}
				List<BizRequestDetail> requestDetailList = bizRequestDetailDao.findListByCentIdAndSkuId(cent.getId(), skuId);
				if (CollectionUtils.isNotEmpty(requestDetailList)) {
					for (BizRequestDetail requestDetail : requestDetailList) {
						if (alOutQty == 0) {
							break;
						}
						if (alOutQty - requestDetail.getRecvQty() >= 0) {
							bizRequestDetailDao.updateOutQty(requestDetail.getId(),requestDetail.getRecvQty());
							alOutQty = alOutQty - requestDetail.getRecvQty();
						} else {
							bizRequestDetailDao.updateOutQty(requestDetail.getId(),alOutQty);
							alOutQty = 0;
						}
					}
				}
			}
		}
	}

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateSkuId(Integer needSkuId, Integer skuId) {
        BizInventorySku orderDetail = new BizInventorySku();
        orderDetail.setSkuInfo(new BizSkuInfo(skuId));
        List<BizInventorySku> orderDetails = findList(orderDetail);
        if (CollectionUtils.isNotEmpty(orderDetails)) {
            for (BizInventorySku bizOrderDetail : orderDetails) {
                bizInventorySkuDao.updateSkuId(needSkuId,bizOrderDetail.getId());
            }
        }
    }
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.inventory.BizCollectGoodsRecordDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestHeaderForVendorDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.InventorySkuRequestProcessConfig;
import com.wanhutong.backend.modules.enums.InventorySkuTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
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

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInventorySkuDao;

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

	public void updateStockQty(BizInventorySku inventorySku) {
		dao.updateStockQty(inventorySku.getId());
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
		requestHeader.setPage(page);
		page.setList(bizRequestHeaderForVendorDao.inventoryPage(requestHeader));
		return page;
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public Pair<Boolean, String> auditInventory(int id, String currentType, int auditType, String description) {
		BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorDao.get(id);
		CommonProcessEntity cureentProcessEntity = bizRequestHeader.getInvCommonProcess();
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
		InventorySkuRequestProcessConfig inventorySkuRequestProcessConfig = ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get();
		// 当前流程
		InventorySkuRequestProcessConfig.OrderHeaderProcess currentProcess = inventorySkuRequestProcessConfig.getProcessMap().get(Integer.valueOf(currentType));
		// 下一流程
		InventorySkuRequestProcessConfig.OrderHeaderProcess nextProcess = inventorySkuRequestProcessConfig.getProcessMap().get(CommonProcessEntity.AuditType.PASS.getCode() == auditType ? currentProcess.getPassCode() : currentProcess.getRejectCode());
		if (nextProcess == null) {
			return Pair.of(Boolean.FALSE,  "操作失败,当前流程已经结束!");
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
}
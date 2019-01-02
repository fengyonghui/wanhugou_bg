/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.common.utils.mail.AliyunMailClient;
import com.wanhutong.backend.common.utils.sms.AliyunSmsClient;
import com.wanhutong.backend.common.utils.sms.SmsTemplateCode;
import com.wanhutong.backend.modules.biz.dao.inventory.BizCollectGoodsRecordDao;
import com.wanhutong.backend.modules.biz.dao.inventory.BizInventorySkuDao;
import com.wanhutong.backend.modules.biz.dao.inventory.BizSkuTransferDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestDetailDao;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestHeaderForVendorDao;
import com.wanhutong.backend.modules.biz.dao.sku.BizSkuInfoV3Dao;
import com.wanhutong.backend.modules.biz.entity.dto.BizInventorySkus;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventoryInfo;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.inventory.BizOutTreasuryEntity;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransfer;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import com.wanhutong.backend.modules.biz.entity.inventoryviewlog.BizInventoryViewLog;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.inventoryviewlog.BizInventoryViewLogService;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoV3Service;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderForVendorService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.EmailConfig;
import com.wanhutong.backend.modules.config.parse.InventorySkuRequestProcessConfig;
import com.wanhutong.backend.modules.enums.InventorySkuTypeEnum;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.enums.ReqFromTypeEnum;
import com.wanhutong.backend.modules.enums.RoleEnNameEnum;
import com.wanhutong.backend.modules.enums.TransferStatusEnum;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import com.wanhutong.backend.modules.sys.dao.OfficeDao;
import com.wanhutong.backend.modules.sys.entity.Dict;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.Role;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.service.SystemService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	private BizSkuInfoV3Service bizSkuInfoService;
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
	@Autowired
	private BizOrderStatusService bizOrderStatusService;
	@Autowired
	private OfficeService officeService;
	@Lazy
	@Autowired
	private BizSkuTransferService transferService;
	@Autowired
	private BizSkuTransferDao transferDao;
	@Autowired
	private BizSkuTransferDetailService transferDetailService;
	@Autowired
	private BizRequestHeaderForVendorService bizRequestHeaderForVendorService;

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

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveOnly(BizInventorySku bizInventorySku) {
		super.save(bizInventorySku);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void saveBizInventorySku(BizInventorySkus bizInventorySkus) {
		if (bizInventorySkus != null && bizInventorySkus.getSkuInfoIds() != null) {
			String[] invInfoIdArr = bizInventorySkus.getInvInfoIds().split(",");
			String[] customerIdArr = null;
			if (bizInventorySkus.getCustomerIds() != null && !bizInventorySkus.getCustomerIds().isEmpty()) {
				customerIdArr = bizInventorySkus.getCustomerIds().split(",");
			}
			String[] invTypeArr = bizInventorySkus.getInvTypes().split(",");
			String[] skuTypeArr = bizInventorySkus.getSkuTypes().split(",");
			String[] skuInfoIdArr = bizInventorySkus.getSkuInfoIds().split(",");
			String[] stockQtyArr = bizInventorySkus.getStockQtys().split(",");
			BizInventorySku bizInventorySku = new BizInventorySku();
			BizInventoryViewLog bizInventoryViewLog = new BizInventoryViewLog();
			for (int i = 0; i < skuInfoIdArr.length; i++) {
				bizInventorySku.setId(null);
				bizInventorySku.setSkuInfo(bizSkuInfoService.get(Integer.parseInt(skuInfoIdArr[i].trim())));
				if (bizInventorySkus.getCustomerIds() != null && !bizInventorySkus.getCustomerIds().isEmpty()) {
					bizInventorySku.setCust(officeService.get(Integer.parseInt(customerIdArr[i].trim())));
				}
				bizInventorySku.setInvInfo(bizInventoryInfoService.get(Integer.parseInt(invInfoIdArr[i].trim())));
				bizInventorySku.setInvType(Integer.parseInt(invTypeArr[i].trim()));
				bizInventorySku.setDelFlag(BizInventorySku.DEL_FLAG_DELETE);
				bizInventorySku.setSkuType(Integer.parseInt(skuTypeArr[i].trim()));
				bizInventoryViewLog.setSkuInfo(bizInventorySku.getSkuInfo());
				bizInventoryViewLog.setInvInfo(bizInventorySku.getInvInfo());
				bizInventoryViewLog.setInvType(bizInventorySku.getInvType());
				//查询是否有已删除的该商品库存
				BizInventorySku only = findOnly(bizInventorySku);
				if (only == null) {
					bizInventoryViewLog.setStockQty(0);
					bizInventorySku.setStockQty(Integer.parseInt(stockQtyArr[i].trim()));
					bizInventorySku.setDelFlag(BizInventorySku.DEL_FLAG_NORMAL);
					bizInventoryViewLog.setStockChangeQty(bizInventorySku.getStockQty());
					bizInventoryViewLog.setNowStockQty(bizInventorySku.getStockQty());
					this.save(bizInventorySku);
				} else {
					if (StringUtils.isNotBlank(bizInventorySkus.getCustomerIds())) {
						only.setCust(officeService.get(Integer.parseInt(customerIdArr[i].trim())));
					}
					bizInventoryViewLog.setStockQty(0);
					only.setStockQty(Integer.parseInt(stockQtyArr[i].trim()));
					bizInventoryViewLog.setStockChangeQty(only.getStockQty());
					bizInventoryViewLog.setNowStockQty(only.getStockQty());
					this.save(only);
				}
				bizInventoryViewLogService.save(bizInventoryViewLog);
			}
		}//修改
		else if (bizInventorySkus != null && bizInventorySkus.getStockQtys() != null && !bizInventorySkus.getStockQtys().equals("")) {
			BizInventoryViewLog bizInventoryViewLog = new BizInventoryViewLog();
			BizInventorySku bizInventorySku = this.get(bizInventorySkus.getId());
			if (bizInventorySku != null) {
				Integer stockQtys = Integer.parseInt(bizInventorySkus.getStockQtys());
				if (!stockQtys.equals(bizInventorySku.getStockQty())) {
					bizInventoryViewLog.setStockQty(bizInventorySku.getStockQty());//原
					bizInventoryViewLog.setStockChangeQty(Integer.parseInt(bizInventorySkus.getStockQtys()) - bizInventorySku.getStockQty());
					bizInventoryViewLog.setNowStockQty(Integer.parseInt(bizInventorySkus.getStockQtys()));//现
					bizInventoryViewLog.setInvInfo(bizInventorySku.getInvInfo());
					bizInventoryViewLog.setInvType(bizInventorySku.getInvType());
					bizInventoryViewLog.setSkuInfo(bizInventorySku.getSkuInfo());
					bizInventoryViewLogService.save(bizInventoryViewLog);
				}
			}
			bizInventorySku.setStockQty(Integer.parseInt(bizInventorySkus.getStockQtys()));
			this.save(bizInventorySku);
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

	public List<BizRequestHeader> inventory(BizRequestHeader requestHeader) {
		User user = UserUtils.getUser();
		requestHeader.getSqlMap().put("request", BaseService.dataScopeFilter(user, "cent","su"));
		return bizRequestHeaderForVendorDao.inventoryPage(requestHeader);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void inventorySave(BizRequestHeader requestHeader) {
		if (StringUtils.isNotBlank(requestHeader.getInventoryRemark())) {
			BizRequestHeader bizRequestHeader = bizRequestHeaderForVendorService.get(requestHeader.getId());
			bizRequestHeader.setInventoryRemark(requestHeader.getInventoryRemark());
			bizRequestHeaderForVendorService.saveInfo(bizRequestHeader);
		}
		//发起盘点记录
		BizOrderStatus bizOrderStatus = new BizOrderStatus();
		bizOrderStatus.setOrderHeader(new BizOrderHeader(requestHeader.getId()));
		bizOrderStatus.setOrderType(BizOrderStatus.OrderType.INVENTORY.getType());
		bizOrderStatus.setBizStatus(0);
		bizOrderStatusService.save(bizOrderStatus);
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
				if ((ConfigGeneral.INVENTORY_SKU_REQUEST_PROCESS_CONFIG.get().getAutProcessId().toString().equals(processEntity.getType()) && CommonProcessEntity.CURRENT.equals(processEntity.getCurrent()))
					|| (CommonProcessEntity.CURRENT.equals(processEntity.getCurrent()) && ("-1").equals(processEntity.getType()))) {
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
            //盘点人
			int currentStatusId = bizOrderStatusService.findCurrentStatus(id,BizOrderStatus.OrderType.INVENTORY.getType());
			BizOrderStatus currentStatus = bizOrderStatusService.get(currentStatusId);

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
							viewLog.setCreateBy(currentStatus.getCreateBy());
							viewLog.setCreateDate(currentStatus.getCreateDate());
							viewLog.setUpdateBy(currentStatus.getUpdateBy());
							viewLog.setUpdateDate(currentStatus.getUpdateDate());
							viewLog.setuVersion(1);
							bizInventoryViewLogService.saveCurrentViewLog(viewLog);
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

    @Transactional(readOnly = false, noRollbackFor = Exception.class)
    public void updateSkuId(Integer needSkuId, Integer skuId) {
		try {
			BizInventorySku orderDetail = new BizInventorySku();
			orderDetail.setSkuInfo(new BizSkuInfo(skuId));
			List<BizInventorySku> orderDetails = findList(orderDetail);
			if (CollectionUtils.isNotEmpty(orderDetails)) {
                for (BizInventorySku bizOrderDetail : orderDetails) {
                    bizInventorySkuDao.updateSkuId(needSkuId,bizOrderDetail.getId());
                }
            }
		} catch (Exception e) {
			LOGGER.error("库存有重复，商品ID：【{}】,修改后的商品ID【{}】",skuId,needSkuId);
		}
	}

	/**
	 * 出库量
	 * @param id
	 * @return
	 */
	public Integer findOutWarehouse(Integer id) {return bizInventorySkuDao.findOutWarehouse(id);}

	/**
	 * 供货部发货数量
	 * @param id
	 * @return
	 */
	public Integer findSendGoodsNum(Integer id) {
		BizInventorySku inventorySku = bizInventorySkuDao.get(id);
		BizInventoryInfo bizInventoryInfo = bizInventoryInfoService.get(inventorySku.getInvInfo().getId());
		return bizInventorySkuDao.findSendGoodsNum(id);
	}

	/**
	 * 入库量
	 * @param id
	 * @return
	 */
	public Integer findInWarehouse(Integer id) {return bizInventorySkuDao.findInWarehouse(id);}

	/**
	 * 检查拆分的sku是否存在
	 * @param bizInventorySku
	 * @return
	 */
	public String checkSku(BizInventorySku bizInventorySku) {
		String itemNo = bizInventorySku.getSkuInfo().getItemNo();
		List<String> skuItemNoSplit = skuSplit(itemNo);
		if (CollectionUtils.isEmpty(skuItemNoSplit)) {
			return "该货号商品无法拆分";
		}
		for (String skuItemNo : skuItemNoSplit) {
			BizSkuInfo skuInfo = new BizSkuInfo();
			skuInfo.setItemNo(skuItemNo);
			List<BizSkuInfo> bizSkuInfos = bizSkuInfoService.findList(skuInfo);
			if (CollectionUtils.isEmpty(bizSkuInfos)) {
				return "不存在货号为：" + skuItemNo + "的商品";
			}
		}
		return "ok";
	}

	/**
	 * 根据货号中的尺寸拆分商品货号
	 * @param itemNo
	 * @return
	 */
	public List<String> skuSplit(String itemNo) {
		if (!itemNo.contains("/") || !itemNo.contains("-")) {
			return null;
		}
		String before = itemNo.substring(0,itemNo.indexOf("/") + 1);
		String middle = itemNo.substring(itemNo.indexOf("/") + 1,itemNo.lastIndexOf("/"));
		String after = itemNo.substring(itemNo.lastIndexOf("/"));
		middle = middle.replaceAll("\\(","").replaceAll("\\)","").replaceAll("套二","").replaceAll("套三","");
		String size = middle.substring(0,middle.lastIndexOf("-") +  3);
		String suffix = middle.substring(middle.lastIndexOf("-") + 3);
		String[] sizeArr = size.split("-");
		List<String> skuSplitList = Lists.newArrayList();
		for (int i = 0; i < sizeArr.length; i ++) {
			skuSplitList.add(before.concat(sizeArr[i]).concat(suffix).concat(after));
		}
		return skuSplitList;
	}

    /**
     * 商品拆分
     * @return
     */
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public String doSkuSplit(List<BizOutTreasuryEntity> skuSplitList) {
		BizInventorySku inventorySku = new BizInventorySku();
		String itemNo = "";//拆分前的货号
		Integer splitNumSum = 0;//累计拆分数量
		Integer oldOutStockNum = 0;//出库原库存数
		Integer oldInStockNum = 0;//入库原库存数
		BizRequestHeader requestHeader = new BizRequestHeader();
		for (BizOutTreasuryEntity skuSplit : skuSplitList) {
			Integer reqDetailId = skuSplit.getReqDetailId();
			Integer transferDetailId = skuSplit.getTransferDetailId();
			Integer splitNum = skuSplit.getOutQty();
			Integer invSkuId = skuSplit.getInvSkuId();
			Integer uVersion = skuSplit.getuVersion();
			if (!uVersion.equals(get(invSkuId).getuVersion())) {
				return "有其他人正在拆分，请刷新后查看";
			}
			if (reqDetailId != null) {
				bizRequestDetailService.updateOutQty(reqDetailId, (bizRequestDetailService.get(reqDetailId).getOutQty() == null ? 0 : bizRequestDetailService.get(reqDetailId).getOutQty()) + splitNum);
				requestHeader = bizRequestDetailService.get(reqDetailId).getRequestHeader();
			}
			if (transferDetailId != null) {
				transferDetailService.updateSentQty(transferDetailId,transferDetailService.get(transferDetailId).getSentQty() + splitNum);
			}
			inventorySku = get(invSkuId);
			itemNo = inventorySku.getSkuInfo().getItemNo();
			splitNumSum += splitNum;
		}
		oldOutStockNum = inventorySku.getStockQty();
		//减去原库存，生成库存变更记录
		updateStockQty(inventorySku, inventorySku.getStockQty() - splitNumSum);
		BizInventoryViewLog outLog = new BizInventoryViewLog();
		outLog.setInvInfo(inventorySku.getInvInfo());
		outLog.setInvType(inventorySku.getInvType());
		outLog.setSkuInfo(inventorySku.getSkuInfo());
		outLog.setStockQty(oldOutStockNum);
		outLog.setStockChangeQty(-splitNumSum);
		outLog.setNowStockQty(oldOutStockNum - splitNumSum);
		outLog.setRequestHeader(requestHeader);
		bizInventoryViewLogService.save(outLog);
		//拆分货号，获取拆分后的商品,添加相应库存
		List<String> splitItemNoList = skuSplit(itemNo);
		for (String item : splitItemNoList) {
			BizSkuInfo skuInfo = bizSkuInfoService.getSkuByItemNo(item);
			BizInventorySku bizInventorySku = new BizInventorySku();
			bizInventorySku.setSkuType(inventorySku.getSkuType());
			bizInventorySku.setInvType(inventorySku.getInvType());
			bizInventorySku.setSkuInfo(skuInfo);
			bizInventorySku.setInvInfo(inventorySku.getInvInfo());
			List<BizInventorySku> inventorySkus = findList(bizInventorySku);
			//增加变更后的商品库存
			if (CollectionUtils.isEmpty(inventorySkus)) {
				BizInventorySku invSku = new BizInventorySku();
				invSku.setInvInfo(inventorySku.getInvInfo());
				invSku.setSkuInfo(skuInfo);
				invSku.setInvType(inventorySku.getInvType());
				invSku.setStockQty(splitNumSum);
				invSku.setSkuType(inventorySku.getSkuType());
				saveOnly(invSku);
			}
			if (CollectionUtils.isNotEmpty(inventorySkus)) {
				if (BizInventorySku.DEL_FLAG_DELETE.equals(inventorySkus.get(0).getDelFlag())) {
					updateStockQty(inventorySkus.get(0), splitNumSum);
				}
				if (BizInventorySku.DEL_FLAG_NORMAL.equals(inventorySkus.get(0).getDelFlag())) {
					oldInStockNum = inventorySkus.get(0).getStockQty();
					updateStockQty(inventorySkus.get(0), inventorySkus.get(0).getStockQty() + splitNumSum);
				}
			}
			//生成库存变更记录
			BizInventoryViewLog inLog = new BizInventoryViewLog();
			inLog.setInvInfo(inventorySku.getInvInfo());
			inLog.setInvType(inventorySku.getInvType());
			inLog.setSkuInfo(skuInfo);
			inLog.setStockQty(oldInStockNum);
			inLog.setStockChangeQty(splitNumSum);
			inLog.setNowStockQty(oldInStockNum + splitNumSum);
			bizInventoryViewLogService.save(inLog);
		}
		//生成拆分单
		BizSkuTransfer skuTransfer = new BizSkuTransfer();
		int s = transferDao.findCountByToCent(inventorySku.getInvInfo().getId());
		String transferNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.RES, inventorySku.getInvInfo().getCustomer().getId(), inventorySku.getInvInfo().getCustomer().getId(), s + 1);
		skuTransfer.setTransferNo(transferNo);
		skuTransfer.setFromInv(inventorySku.getInvInfo());
		skuTransfer.setToInv(inventorySku.getInvInfo());
		skuTransfer.setApplyer(UserUtils.getUser());
		skuTransfer.setBizStatus(TransferStatusEnum.ALREADY_IN_WAREHOUSE.getState().byteValue());
		skuTransfer.setRecvEta(new Date());
		skuTransfer.setRemark("库存商品拆分");
		transferService.saveOnly(skuTransfer);
		for (String item : splitItemNoList) {
			BizSkuInfo skuInfo = bizSkuInfoService.getSkuByItemNo(item);
			BizSkuTransferDetail bizSkuTransferDetail = new BizSkuTransferDetail();
			bizSkuTransferDetail.setTransfer(skuTransfer);
			bizSkuTransferDetail.setSkuInfo(skuInfo);
			bizSkuTransferDetail.setTransQty(splitNumSum);
			bizSkuTransferDetail.setFromInvOp(UserUtils.getUser());
			bizSkuTransferDetail.setFromInvOpTime(new Date());
			bizSkuTransferDetail.setToInvOp(UserUtils.getUser());
			bizSkuTransferDetail.setToInvOpTime(new Date());
			bizSkuTransferDetail.setOutQty(splitNumSum);
			bizSkuTransferDetail.setInQty(splitNumSum);
			transferDetailService.save(bizSkuTransferDetail);
		}
		return "拆分完成";
	}

	/**
	 * 取map最后一个元素
	 * @param map
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public <K, V> Map.Entry<K, V> getTail(LinkedHashMap<K, V> map) {
		Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
		Map.Entry<K, V> tail = null;
		while (iterator.hasNext()) {
			tail = iterator.next();
		}
		return tail;
	}

	/**
	 * 商品合并
	 * @param map
	 * @return
	 */
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public String skuMerge(LinkedHashMap<String,List<BizOutTreasuryEntity>> map) {
		if (map.isEmpty() || map.size() < 2) {
			return "没有选择合并的商品";
		}
		Map.Entry<String, List<BizOutTreasuryEntity>> tail = getTail(map);
		String mergeSize = "";
		for (String size : map.keySet()) {
			if (!tail.getKey().equals(size)) {
				mergeSize = mergeSize.concat(size).concat("-");
			}
			if (tail.getKey().equals(size)) {
				mergeSize = mergeSize.concat(size);
			}
		}
		String before = "";
		String suffix = "";
		String after = "";
		for (Map.Entry<String,List<BizOutTreasuryEntity>> entry : map.entrySet()) {
			for (BizOutTreasuryEntity entity : entry.getValue()) {
				String itemNo = "";
				if (entity.getReqDetailId() != null) {
					BizRequestDetail requestDetail = bizRequestDetailService.get(entity.getReqDetailId());
					itemNo = requestDetail.getSkuInfo().getItemNo();
					before = itemNo.substring(0,itemNo.indexOf("/") + 1);
					String middle = itemNo.substring(itemNo.indexOf("/") + 1,itemNo.lastIndexOf("/"));
					after = itemNo.substring(itemNo.lastIndexOf("/"));
					suffix = middle.substring(2);
				}
				if (entity.getTransferDetailId() != null) {
					BizSkuTransferDetail bizSkuTransferDetail = transferDetailService.get(entity.getTransferDetailId());
					itemNo = bizSkuTransferDetail.getSkuInfo().getItemNo();
					before = itemNo.substring(0,itemNo.indexOf("/") + 1);
					String middle = itemNo.substring(itemNo.indexOf("/") + 1,itemNo.lastIndexOf("/"));
					after = itemNo.substring(itemNo.lastIndexOf("/"));
					suffix = middle.substring(2);
				}
				break;
			}
		}
		String mergeItemNo = before.concat(mergeSize).concat(suffix).concat(after);
		BizSkuInfo mergeSku = bizSkuInfoService.getSkuByItemNo(mergeItemNo);
		Integer mergeSumNum = 0;//合并总数
		Integer skuType = 0;//合并商品的库存类型
		Integer invType = 0;//合并商品的库存商品类型
		BizInventoryInfo inventoryInfo = new BizInventoryInfo();//合并商品所属仓库
		if (mergeSku == null) {
			return "没有货号为:"+mergeItemNo+"的商品";
		}
		for(Map.Entry<String,List<BizOutTreasuryEntity>> entry : map.entrySet()) {
			List<BizOutTreasuryEntity> treasuryList = entry.getValue();
			if (CollectionUtils.isNotEmpty(treasuryList)) {
				mergeSumNum = 0;//初始化合并总数
				BizInventorySku bizInventorySku = new BizInventorySku();
				BizRequestHeader requestHeader = new BizRequestHeader();
				BizRequestDetail requestDetail = new BizRequestDetail();
				BizSkuTransferDetail bizSkuTransferDetail = new BizSkuTransferDetail();
				for (BizOutTreasuryEntity treasuryEntity : treasuryList) {
					Integer reqDetailId = treasuryEntity.getReqDetailId();
					Integer transferDetailId = treasuryEntity.getTransferDetailId();
					Integer mergeNum = treasuryEntity.getOutQty();
					Integer uVersion = treasuryEntity.getuVersion();
					Integer invSkuId = treasuryEntity.getInvSkuId();
					mergeSumNum += mergeNum;
					if (reqDetailId != null) {
						requestDetail = bizRequestDetailService.get(reqDetailId);
						requestHeader = requestDetail.getRequestHeader();
					}
					if (transferDetailId != null) {
						bizSkuTransferDetail = transferDetailService.get(transferDetailId);
					}
					bizInventorySku = get(invSkuId);
					skuType = bizInventorySku.getSkuType();
					invType = bizInventorySku.getInvType();
					inventoryInfo = bizInventorySku.getInvInfo();
					if (!uVersion.equals(bizInventorySku.getuVersion())) {
						return "有其他人正在操作库存，请稍后再进行操作";
					}
					if (reqDetailId != null && (requestDetail.getOutQty() == null ? 0 : requestDetail.getOutQty()) + mergeNum > requestDetail.getRecvQty()) {
						return "合并数量不能超过可合并数量";
					}
					if (transferDetailId != null && (bizSkuTransferDetail.getSentQty() == null ? 0 : bizSkuTransferDetail.getSentQty()) + mergeNum > bizSkuTransferDetail.getInQty()) {
						return "合并数量不能超过可合并数量";
					}
					if (bizInventorySku.getStockQty() < mergeNum) {
						return "合并数量不能大于库存数";
					}
					if (transferDetailId != null) {
						transferDetailService.updateSentQty(transferDetailId,(bizSkuTransferDetail.getSentQty() == null ? 0 : bizSkuTransferDetail.getSentQty()) + mergeNum);
					}
					if (reqDetailId != null) {
						bizRequestDetailService.updateOutQty(reqDetailId, (requestDetail.getOutQty() == null ? 0 : requestDetail.getOutQty()) + mergeNum);
					}
				}
				//原商品变更库存,生成记录
				Integer oldOutStockNum = bizInventorySku.getStockQty();
				updateStockQty(bizInventorySku,bizInventorySku.getStockQty() - mergeSumNum);
				BizInventoryViewLog outLog = new BizInventoryViewLog();
				outLog.setInvInfo(bizInventorySku.getInvInfo());
				outLog.setInvType(bizInventorySku.getInvType());
				outLog.setSkuInfo(bizInventorySku.getSkuInfo());
				outLog.setStockQty(oldOutStockNum);
				outLog.setStockChangeQty(-mergeSumNum);
				outLog.setNowStockQty(oldOutStockNum - mergeSumNum);
				outLog.setRequestHeader(requestHeader);
				bizInventoryViewLogService.save(outLog);
			}
		}
		//合并后的商品变更库存，生成记录
		BizInventorySku bizInventorySku = new BizInventorySku();
		bizInventorySku.setSkuType(skuType);
		bizInventorySku.setInvType(invType);
		bizInventorySku.setSkuInfo(mergeSku);
		bizInventorySku.setInvInfo(inventoryInfo);
		List<BizInventorySku> inventorySkus = findList(bizInventorySku);
		//增加变更后的商品库存
		BizInventorySku inventorySku = new BizInventorySku();
		if (CollectionUtils.isEmpty(inventorySkus)) {
			BizInventorySku invSku = new BizInventorySku();
			invSku.setInvInfo(inventoryInfo);
			invSku.setSkuInfo(mergeSku);
			invSku.setInvType(invType);
			invSku.setStockQty(mergeSumNum);
			invSku.setSkuType(skuType);
			saveOnly(invSku);
			inventorySku = invSku;
		}
		Integer oldInStockNum = 0;
		if (CollectionUtils.isNotEmpty(inventorySkus)) {
			inventorySku = inventorySkus.get(0);
			if (BizInventorySku.DEL_FLAG_DELETE.equals(inventorySku.getDelFlag())) {
				updateStockQty(inventorySkus.get(0), mergeSumNum);
			}
			if (BizInventorySku.DEL_FLAG_NORMAL.equals(inventorySku.getDelFlag())) {
				oldInStockNum = inventorySku.getStockQty();
				updateStockQty(inventorySku, inventorySku.getStockQty() + mergeSumNum);
			}
		}
		//生成库存变更记录
		BizInventoryViewLog inLog = new BizInventoryViewLog();
		inLog.setInvInfo(inventoryInfo);
		inLog.setInvType(invType);
		inLog.setSkuInfo(mergeSku);
		inLog.setStockQty(oldInStockNum);
		inLog.setStockChangeQty(mergeSumNum);
		inLog.setNowStockQty(oldInStockNum + mergeSumNum);
		bizInventoryViewLogService.save(inLog);
		//生成合并单
		BizSkuTransfer skuTransfer = new BizSkuTransfer();
		int s = transferDao.findCountByToCent(inventorySku.getInvInfo().getId());
		String transferNo = GenerateOrderUtils.getOrderNum(OrderTypeEnum.REM, inventorySku.getInvInfo().getCustomer().getId(), inventorySku.getInvInfo().getCustomer().getId(), s + 1);
		skuTransfer.setTransferNo(transferNo);
		skuTransfer.setFromInv(inventorySku.getInvInfo());
		skuTransfer.setToInv(inventorySku.getInvInfo());
		skuTransfer.setApplyer(UserUtils.getUser());
		skuTransfer.setBizStatus(TransferStatusEnum.ALREADY_IN_WAREHOUSE.getState().byteValue());
		skuTransfer.setRecvEta(new Date());
		skuTransfer.setRemark("库存商品合并");
		transferService.saveOnly(skuTransfer);
		BizSkuTransferDetail bizSkuTransferDetail = new BizSkuTransferDetail();
		bizSkuTransferDetail.setTransfer(skuTransfer);
		bizSkuTransferDetail.setSkuInfo(mergeSku);
		bizSkuTransferDetail.setTransQty(mergeSumNum);
		bizSkuTransferDetail.setFromInvOp(UserUtils.getUser());
		bizSkuTransferDetail.setFromInvOpTime(new Date());
		bizSkuTransferDetail.setToInvOp(UserUtils.getUser());
		bizSkuTransferDetail.setToInvOpTime(new Date());
		bizSkuTransferDetail.setOutQty(mergeSumNum);
		bizSkuTransferDetail.setInQty(mergeSumNum);
		transferDetailService.save(bizSkuTransferDetail);
		return "合并成功";
	}

}
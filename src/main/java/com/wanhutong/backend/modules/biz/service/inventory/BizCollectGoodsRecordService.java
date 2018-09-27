/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.Date;
import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizSkuInputOutputDto;
import com.wanhutong.backend.modules.biz.entity.inventory.BizInventorySku;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.order.BizOrderStatusService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestDetailService;
import com.wanhutong.backend.modules.biz.service.request.BizRequestHeaderService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.enums.BizOrderStatusOrderTypeEnum;
import com.wanhutong.backend.modules.enums.InvSkuTypeEnum;
import com.wanhutong.backend.modules.enums.ReqHeaderStatusEnum;
import com.wanhutong.backend.modules.sys.entity.Office;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.dao.inventory.BizCollectGoodsRecordDao;

import javax.annotation.Resource;

/**
 * 收货记录表Service
 * @author 张腾飞
 * @version 2018-01-03
 */
@Service
@Transactional(readOnly = true)
public class BizCollectGoodsRecordService extends CrudService<BizCollectGoodsRecordDao, BizCollectGoodsRecord> {

	@Resource
	private BizRequestDetailService bizRequestDetailService;
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Resource
	private BizRequestHeaderService bizRequestHeaderService;
	@Resource
	private BizInventorySkuService bizInventorySkuService;
	@Resource
	private OfficeService officeService;

	@Resource
	private BizOrderStatusService bizOrderStatusService;

	@Override
	public BizCollectGoodsRecord get(Integer id) {
		return super.get(id);
	}

	@Override
	public List<BizCollectGoodsRecord> findList(BizCollectGoodsRecord bizCollectGoodsRecord) {
		User user = UserUtils.getUser();
		if (user.isAdmin()) {
			return super.findList(bizCollectGoodsRecord);
		} else {
			bizCollectGoodsRecord.getSqlMap().put("collectGoodsRecord", BaseService.dataScopeFilter(user, "cent", "su"));
			return super.findList(bizCollectGoodsRecord);
		}
	}

	@Override
	public Page<BizCollectGoodsRecord> findPage(Page<BizCollectGoodsRecord> page, BizCollectGoodsRecord bizCollectGoodsRecord) {
		User user=UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizCollectGoodsRecord);
		}else {
			bizCollectGoodsRecord.getSqlMap().put("collectGoodsRecord", BaseService.dataScopeFilter(user, "cent", "su"));
			return super.findPage(page, bizCollectGoodsRecord);
		}


	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void save(BizCollectGoodsRecord bizCollectGoodsRecord) {
		User user = UserUtils.getUser();
		officeService.get(user.getCompany().getId());
		boolean flagRequest = true;		//备货单完成状态
		for (BizCollectGoodsRecord bcgr : bizCollectGoodsRecord.getBizCollectGoodsRecordList()) {
			int receiveNum = bcgr.getReceiveNum();    //收货数
			int recvQty = bcgr.getBizRequestDetail().getRecvQty();		//已收货数量
			BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			//累计备货单收货数量
			if (bcgr.getBizRequestDetail() != null && bcgr.getBizRequestDetail().getId() != 0) {
				//当收货数量和申报数量不相等时，更改备货单状态
				if (bcgr.getBizRequestDetail().getReqQty() != (recvQty + receiveNum)) {
					flagRequest = false;
					//改状态为收货中
					bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.COMPLETEING.getState());
					bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
					Integer bizStatus = bizRequestHeader.getBizStatus();
					if (bizStatus == null || !bizStatus.equals(ReqHeaderStatusEnum.COMPLETEING.getState())) {
						bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizCollectGoodsRecord.getBizRequestHeader().getId());
					}
				}
				if (receiveNum == 0) {
					continue;
				}
				BizRequestDetail bizRequestDetail = bizRequestDetailService.get(bcgr.getBizRequestDetail().getId());
				bizRequestDetail.setRecvQty(recvQty + receiveNum);
				bizRequestDetailService.save(bizRequestDetail);
			}
			//收货之前的库存
			int invOldNum = 0;
			//获取库存信息
			//BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			BizInventorySku bizInventorySku = new BizInventorySku();
			bizInventorySku.setSkuInfo(bcgr.getSkuInfo());
			bizInventorySku.setInvInfo(bcgr.getInvInfo());
			bizInventorySku.setCustomer(bcgr.getCustomer());
			bizInventorySku.setInvType(InvSkuTypeEnum.CONVENTIONAL.getState());
			bizInventorySku.setSkuType(bizRequestHeader.getFromType());
			if (bizRequestHeader.getBizVendInfo() != null && bizRequestHeader.getBizVendInfo().getOffice().getId() != null) {
				bizInventorySku.setVendor(new Office(bizRequestHeader.getBizVendInfo().getOffice().getId()));
			}
			//库存有该商品,增加相应数量
			if(bizInventorySkuService.findList(bizInventorySku) != null && bizInventorySkuService.findList(bizInventorySku).size() > 0){
				List<BizInventorySku> bizInventorySkuList = bizInventorySkuService.findList(bizInventorySku);
				BizInventorySku bizInventorySku1 = bizInventorySkuList.get(0);
				invOldNum = bizInventorySku1.getStockQty();
				bizInventorySku1.setStockQty(bizInventorySku1.getStockQty()+receiveNum);
				bizInventorySkuService.save(bizInventorySku1);
			}
			//库存没有该商品，增加该商品相应库存
			if(bizInventorySkuService.findList(bizInventorySku) == null || bizInventorySkuService.findList(bizInventorySku).size() == 0){
				BizInventorySku bizInventorySku1 = new BizInventorySku();
				bizInventorySku1.setInvInfo(bcgr.getInvInfo());
				bizInventorySku1.setSkuInfo(bcgr.getSkuInfo());
				bizInventorySku1.setInvType(InvSkuTypeEnum.CONVENTIONAL.getState());
				bizInventorySku1.setStockQty(receiveNum);
				bizInventorySku1.setCustomer(bcgr.getCustomer());
				bizInventorySku1.setSkuType(bizRequestHeader.getFromType());
				bizInventorySku1.setVendor(bizInventorySku.getVendor());
				bizInventorySkuService.save(bizInventorySku1);

			}
			//生成收货记录表
			BizSkuInfo bizSkuInfo = bizSkuInfoService.get(bcgr.getSkuInfo().getId());
			bcgr.setCollectNo(bizCollectGoodsRecord.getCollectNo());
			bcgr.setInvInfo(bcgr.getInvInfo());
			bcgr.setInvOldNum(invOldNum);
			bcgr.setSkuInfo(bizSkuInfo);
			bcgr.setVender(officeService.get(user.getCompany().getId()));
			bcgr.setBizRequestHeader(bizRequestHeader);
			bcgr.setOrderNum(bcgr.getOrderNum());
			bcgr.setReceiveDate(new Date());
			bcgr.setReceiveNum(bcgr.getReceiveNum());
			super.save(bcgr);
		}

		//更改备货单状态:收货完成（30）
		if (flagRequest) {
			BizRequestHeader bizRequestHeader = bizRequestHeaderService.get(bizCollectGoodsRecord.getBizRequestHeader().getId());
			Integer bizStatus = bizRequestHeader.getBizStatus();
			bizRequestHeader.setBizStatus(ReqHeaderStatusEnum.COMPLETE.getState());
			bizRequestHeaderService.saveRequestHeader(bizRequestHeader);
			if (bizStatus == null || !bizStatus.equals(ReqHeaderStatusEnum.COMPLETE.getState())) {
				bizOrderStatusService.insertAfterBizStatusChanged(BizOrderStatusOrderTypeEnum.REPERTOIRE.getDesc(), BizOrderStatusOrderTypeEnum.REPERTOIRE.getState(), bizCollectGoodsRecord.getBizRequestHeader().getId());
			}
		}
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void delete(BizCollectGoodsRecord bizCollectGoodsRecord) {
		super.delete(bizCollectGoodsRecord);
	}

	/**
	 * 库存变更记录，分页
	 * */
	public Page<BizCollectGoodsRecord> collectSendFindPage(Page<BizCollectGoodsRecord> page, BizCollectGoodsRecord bizCollectGoodsRecord) {
		bizCollectGoodsRecord.setPage(page);
		page.setList(dao.collectSendFindPage(bizCollectGoodsRecord));
		return page;
	}

	public List<BizCollectGoodsRecord> getListBySkuIdCentId(Integer skuId, Integer centId) {
		return dao.getListBySkuIdCentId(skuId, centId);
	}
	/**
	 * 导出所有
	 * */
	public List<BizCollectGoodsRecord> collectSendFindList(BizCollectGoodsRecord bizCollectGoodsRecord) {
		return dao.collectSendFindPage(bizCollectGoodsRecord);
	}

	public List<BizSkuInputOutputDto> skuInputOutputRecord(String startDate, String endDate, String invName, String skuItemNo) {
		return dao.getSkuInputOutputRecord(startDate, endDate, invName, skuItemNo);
	}

	public Integer findContByCentId(Integer centId) {
		return dao.findContByCentId(centId);
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanhutong.backend.modules.biz.dao.inventory.BizCollectGoodsRecordDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
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
	@Autowired
	private BizInventorySkuDao bizInventorySkuDao;
	@Autowired
	private BizCollectGoodsRecordDao bizCollectGoodsRecordDao;

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
	@Transactional(readOnly = false)
	public void save(BizInventorySku bizInventorySku) {
		if (bizInventorySku.getStockQty()<0){
			return;
		}
        List<BizInventorySku> invSkuList = bizInventorySkuDao.findList(bizInventorySku);
        BizInventorySku invSku = new BizInventorySku();
		if (invSkuList != null && !invSkuList.isEmpty()){
            invSku = invSkuList.get(0);
            invSku.setStockQty(invSku.getStockQty() + bizInventorySku.getStockQty());
            super.save(invSku);
        }
        super.save(bizInventorySku);
		/*if (bizInventorySku.getStockQty() == 0){
			this.delete(bizInventorySku);
		}*/
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
	public Map<String, Object> getInventoryAge(Integer skuId, Integer centId) {
		Integer stockQtyBySkuIdCentId = bizInventorySkuDao.getStockQtyBySkuIdCentId(skuId, centId);
		List<BizCollectGoodsRecord> recordList = bizCollectGoodsRecordDao.getListBySkuIdCentId(skuId, centId);

		recordList.sort((o1, o2) -> o2.getReceiveDate().compareTo(o1.getReceiveDate()));
		Map<BizCollectGoodsRecord, Integer> resultRecordList = Maps.newLinkedHashMap();

		int counter = stockQtyBySkuIdCentId;
		for (BizCollectGoodsRecord bizCollectGoodsRecord : recordList) {
			if (counter <=0) {
				break;
			}
			counter -= bizCollectGoodsRecord.getReceiveNum();
			resultRecordList.put(bizCollectGoodsRecord, counter <=0 ? bizCollectGoodsRecord.getReceiveNum() + counter : bizCollectGoodsRecord.getReceiveNum());
		}

		Map<String, Object> result = Maps.newHashMap();
		result.put("stockQty", stockQtyBySkuIdCentId);
		result.put("recordList", recordList);
		result.put("resultRecordList", resultRecordList);
		return result;
	}
}
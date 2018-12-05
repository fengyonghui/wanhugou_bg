/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSkuTransferDetail;
import com.wanhutong.backend.modules.biz.dao.inventory.BizSkuTransferDetailDao;

/**
 * 库存调拨详情Service
 * @author Tengfei.Zhang
 * @version 2018-11-08
 */
@Service
@Transactional(readOnly = true)
public class BizSkuTransferDetailService extends CrudService<BizSkuTransferDetailDao, BizSkuTransferDetail> {

	public BizSkuTransferDetail get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSkuTransferDetail> findList(BizSkuTransferDetail bizSkuTransferDetail) {
		return super.findList(bizSkuTransferDetail);
	}
	
	public Page<BizSkuTransferDetail> findPage(Page<BizSkuTransferDetail> page, BizSkuTransferDetail bizSkuTransferDetail) {
		return super.findPage(page, bizSkuTransferDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(BizSkuTransferDetail bizSkuTransferDetail) {
		super.save(bizSkuTransferDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizSkuTransferDetail bizSkuTransferDetail) {
		super.delete(bizSkuTransferDetail);
	}

	/**
	 * 修改入库数量
	 * @param id
	 * @param inQty
	 */
	@Transactional(readOnly = false)
	public void updateInQty(Integer id, Integer inQty) {dao.updateInQty(id,inQty);}

	/**
	 * 根据采购中心ID和商品ID查询可出库的调拨单详情
	 * @param centId
	 * @param skuId
	 * @return
	 */
	public List<BizSkuTransferDetail> findInventorySkuByskuIdAndcentId(Integer centId,Integer skuId) {
		return dao.findInventorySkuByskuIdAndcentId(centId,skuId);
	}

	/**
	 * 根据订单详情ID查询已出库的调拨单信息
	 * @param orderDetailId
	 * @return
	 */
	public List<BizSkuTransferDetail> findInvTransferByOrderDetailId(Integer orderDetailId) {
		return dao.findInvTransferByOrderDetailId(orderDetailId);
	}

	/**
	 * 修改已卖出数量
	 * @param id
	 * @param sentQty
	 */
	public void updateSentQty(Integer id, Integer sentQty) {
		dao.updateSentQty(id,sentQty);
	}
	
}
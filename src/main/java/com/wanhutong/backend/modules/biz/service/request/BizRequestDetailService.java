/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.util.List;

import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestDetailDao;

/**
 * 备货清单详细信息Service
 * @author liuying
 * @version 2017-12-23
 */
@Service
@Transactional(readOnly = true)
public class BizRequestDetailService extends CrudService<BizRequestDetailDao, BizRequestDetail> {
	@Autowired
	private BizRequestDetailDao bizRequestDetailDao;

	public BizRequestDetail get(Integer id) {
		return super.get(id);
	}
	
	public List<BizRequestDetail> findList(BizRequestDetail bizRequestDetail) {
		return super.findList(bizRequestDetail);
	}
	
	public Page<BizRequestDetail> findPage(Page<BizRequestDetail> page, BizRequestDetail bizRequestDetail) {
		return super.findPage(page, bizRequestDetail);
	}

	public  List<BizRequestDetail>findReqTotalByVendor(BizRequestHeader bizRequestHeader){
		return bizRequestDetailDao.findReqTotalByVendor(bizRequestHeader);
	}


	@Transactional(readOnly = false)
	public void save(BizRequestDetail bizRequestDetail) {
		super.save(bizRequestDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizRequestDetail bizRequestDetail) {
		super.delete(bizRequestDetail);
	}

	@Transactional(readOnly = false)
	public List<BizRequestDetail> findPoRequet(BizRequestDetail bizRequestDetail) {
		return dao.findPoRequet(bizRequestDetail);
	}

	/**
	 * 获取已排产总量和已确认量
	 * @param objectId
	 * @return
	 */
	@Transactional(readOnly = false)
	public BizRequestDetail getsumSchedulingNum(Integer objectId){
		return bizRequestDetailDao.getsumSchedulingNum(objectId);
	}

	public List<BizRequestDetail> findInventorySkuByskuIdAndcentId(Integer centerId, Integer skuId) {
		return dao.findInventorySkuByskuIdAndcentId(centerId, skuId);
	}

	public List<BizRequestDetail> findInvReqByOrderDetailId(Integer orderDetailId) {
		return dao.findInvReqByOrderDetailId(orderDetailId);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateSkuId(Integer needSkuId, Integer skuId) {
		BizRequestDetail orderDetail = new BizRequestDetail();
		orderDetail.setSkuInfo(new BizSkuInfo(skuId));
		List<BizRequestDetail> orderDetails = findList(orderDetail);
		if (CollectionUtils.isNotEmpty(orderDetails)) {
			for (BizRequestDetail bizOrderDetail : orderDetails) {
				bizRequestDetailDao.updateSkuId(needSkuId,bizOrderDetail.getId());
			}
		}
	}

	/**
	 * 根据仓库ID和商品ID查询可出库的备货单详情
	 * @param invInfoId
	 * @param skuId
	 * @return
	 */
	public List<BizRequestDetail> findListByinvAndSku(Integer invInfoId, Integer skuId) {
		return bizRequestDetailDao.findListByinvAndSku(invInfoId,skuId);
	}

}
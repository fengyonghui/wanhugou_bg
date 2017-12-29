/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderDetailDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单详情(销售订单)Service
 * @author OuyangXiutian
 * @version 2017-12-22
 */
@Service
@Transactional(readOnly = true)
public class BizOrderDetailService extends CrudService<BizOrderDetailDao, BizOrderDetail> {

	@Autowired
	private BizSkuInfoService bizSkuInfoService;
	
	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	
	@Autowired
	private  BizOrderDetailDao bizOrderDetailDao;

	public BizOrderDetail get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderDetail> findList(BizOrderDetail bizOrderDetail) {
		return super.findList(bizOrderDetail);
	}
	
	public Page<BizOrderDetail> findPage(Page<BizOrderDetail> page, BizOrderDetail bizOrderDetail) {
		return super.findPage(page, bizOrderDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderDetail bizOrderDetail) {
		Integer skuId = bizOrderDetail.getSkuInfo().getId();
		BizSkuInfo bizSkuInfo = bizSkuInfoService.get(skuId);
		bizOrderDetail.setSkuName(bizSkuInfo.getName());
		super.save(bizOrderDetail);
		Double sum = 0.0;
		List<BizOrderDetail> list = super.findList(bizOrderDetail);
		for (BizOrderDetail detail : list) {
			Double price = detail.getUnitPrice();
			Integer ordQty = detail.getOrdQty();
			sum+=price*ordQty;
		}
		BizOrderHeader orderHeader = bizOrderDetail.getOrderHeader();
		BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(orderHeader.getId());
		bizOrderHeader.setTotalDetail(sum);
		bizOrderHeaderService.save(bizOrderHeader);
	}
	
	public Integer findMaxLine(BizOrderDetail bizOrderDetail){
		return bizOrderDetailDao.findMaxLine(bizOrderDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderDetail bizOrderDetail) {
		super.delete(bizOrderDetail);
	}
	
}
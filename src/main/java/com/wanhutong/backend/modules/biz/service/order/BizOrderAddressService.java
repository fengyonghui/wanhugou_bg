/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;
import java.util.Stack;

import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.service.SysRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderAddress;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderAddressDao;

import javax.annotation.Resource;

/**
 * 订单地址Service
 * @author OuyangXiutian
 * @version 2018-01-22
 */
@Service
@Transactional(readOnly = true)
public class BizOrderAddressService extends CrudService<BizOrderAddressDao, BizOrderAddress> {

	@Autowired
	private SysRegionService sysRegionService;
	@Resource
	private BizOrderAddressDao bizOrderAddressDao;

	public BizOrderAddress get(Integer id) {
		return super.get(id);
	}

//	交货地址
	public BizOrderAddress getAddress(BizOrderAddress bizOrderAddress) {
		return bizOrderAddressDao.getAddress(bizOrderAddress);
	}

	public List<BizOrderAddress> findList(BizOrderAddress bizOrderAddress) {
		return super.findList(bizOrderAddress);
	}
	
	public Page<BizOrderAddress> findPage(Page<BizOrderAddress> page, BizOrderAddress bizOrderAddress) {
		return super.findPage(page, bizOrderAddress);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderAddress bizOrderAddress) {
		super.save(bizOrderAddress);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderAddress bizOrderAddress) {
		super.delete(bizOrderAddress);
	}

	public BizOrderAddress updateBizOrderAddress(BizOrderAddress orderAddress){
		if(orderAddress == null){
			orderAddress = new BizOrderAddress();
		}
		Integer oldRegionId = orderAddress.getRegion()==null ? null : orderAddress.getRegion().getId();
		Integer selectedRegionId = orderAddress.getSelectedRegionId();
		Stack<SysRegion> regionStack=getRegionStack(selectedRegionId,oldRegionId);
		if(regionStack!=null){
			orderAddress.setProvince(regionStack.pop());
			if (regionStack.size() > 0) {
				orderAddress.setCity(regionStack.pop());
			}else if(regionStack.size()==0){
				SysRegion sysRegion1=new SysRegion();
				sysRegion1.setId(0);
				orderAddress.setCity(sysRegion1);
			}
			if (regionStack.size() > 0) {
				orderAddress.setRegion(regionStack.pop());
			}
		}
//		bizLocation.setPcrName(bizLocation.getFullpcrName());
		save(orderAddress);
		return orderAddress;
	}
	private Stack<SysRegion> getRegionStack(Integer selectedRegionId,Integer oldRegionId) {
		Stack<SysRegion> regionStack = null;
		if (selectedRegionId != null && !selectedRegionId.equals(oldRegionId)) {
			regionStack=new Stack<SysRegion>();
			SysRegion sysRegion = sysRegionService.get(selectedRegionId);
			regionStack.push(sysRegion);

			while (sysRegion.getPcode() != null && !"0".equals(sysRegion.getPcode())) {
				sysRegion = sysRegionService.getByCode(sysRegion.getPcode());
				regionStack.push(sysRegion);
			}

		}
		return regionStack;
	}

	/**
	 * 根据订单ID取订单收货地址
	 * @param orderId 订单ID
	 * @return 订单收货地址实体
	 */
	public BizOrderAddress getOrderAddrByOrderId(Integer orderId) {
		 return bizOrderAddressDao.getOrderAddrByOrderId(orderId);

	}
}
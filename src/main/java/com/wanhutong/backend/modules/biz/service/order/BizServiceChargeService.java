/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.order.BizServiceLine;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.service.SysRegionService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizServiceCharge;
import com.wanhutong.backend.modules.biz.dao.order.BizServiceChargeDao;

/**
 * 服务费--配送方式Service
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
@Service
@Transactional(readOnly = true)
public class BizServiceChargeService extends CrudService<BizServiceChargeDao, BizServiceCharge> {

	@Autowired
	private BizServiceLineService bizServiceLineService;
	@Autowired
	private SysRegionService sysRegionService;

	public BizServiceCharge get(Integer id) {
		return super.get(id);
	}
	
	public List<BizServiceCharge> findList(BizServiceCharge bizServiceCharge) {
		return super.findList(bizServiceCharge);
	}
	
	public Page<BizServiceCharge> findPage(Page<BizServiceCharge> page, BizServiceCharge bizServiceCharge) {
		return super.findPage(page, bizServiceCharge);
	}
	
	@Transactional(readOnly = false)
	public void save(BizServiceCharge bizServiceCharge) {
		if (bizServiceCharge.getId() != null) {
			BizServiceLine serviceLine = bizServiceCharge.getServiceLine();
			Byte usable = serviceLine.getUsable();
			serviceLine.setUsable(null);
			serviceLine.setProvince(sysRegionService.getByCode(serviceLine.getProvince().getCode()));
			serviceLine.setCity(sysRegionService.getByCode(serviceLine.getCity().getCode()));
			serviceLine.setRegion(sysRegionService.getByCode(serviceLine.getRegion().getCode()));
			serviceLine.setToProvince(sysRegionService.getByCode(serviceLine.getToProvince().getCode()));
			serviceLine.setToCity(sysRegionService.getByCode(serviceLine.getToCity().getCode()));
			serviceLine.setToRegion(sysRegionService.getByCode(serviceLine.getToRegion().getCode()));
			List<BizServiceLine> serviceLines = bizServiceLineService.findList(serviceLine);
			if (CollectionUtils.isNotEmpty(serviceLines)) {
				bizServiceCharge.setServiceLine(serviceLines.get(0));
			}
			if (CollectionUtils.isEmpty(serviceLines)) {
				BizServiceLine bizServiceLine = new BizServiceLine();
				bizServiceLine.setProvince(serviceLine.getProvince());
				bizServiceLine.setCity(serviceLine.getCity());
				bizServiceLine.setRegion(serviceLine.getRegion());
				bizServiceLine.setToProvince(serviceLine.getToProvince());
				bizServiceLine.setToCity(serviceLine.getToCity());
				bizServiceLine.setToRegion(serviceLine.getToRegion());
				bizServiceLine.setUsable(usable);
				bizServiceLine.setOrderType((byte)1);
				bizServiceLineService.save(bizServiceLine);
				bizServiceCharge.setServiceLine(bizServiceLine);
			}
			super.save(bizServiceCharge);
			return;
		}
		List<BizServiceLine> serviceLineList = bizServiceCharge.getServiceLineList();
		for (BizServiceLine serviceLine : serviceLineList) {
			//路线
			BizServiceLine bizServiceLine = new BizServiceLine();
			bizServiceLine.setProvince(sysRegionService.getByCode(serviceLine.getProvince().getCode()));
			bizServiceLine.setCity(sysRegionService.getByCode(serviceLine.getCity().getCode()));
			bizServiceLine.setRegion(sysRegionService.getByCode(serviceLine.getRegion().getCode()));
			bizServiceLine.setToProvince(sysRegionService.getByCode(serviceLine.getToProvince().getCode()));
			bizServiceLine.setToCity(sysRegionService.getByCode(serviceLine.getToCity().getCode()));
			bizServiceLine.setToRegion(sysRegionService.getByCode(serviceLine.getToRegion().getCode()));
			bizServiceLine.setOrderType((byte)1);
			if (serviceLine.getUsable() != null) {
				bizServiceLine.setUsable(serviceLine.getUsable());
			} else {
				bizServiceLine.setUsable((byte)0);
			}
			bizServiceLineService.save(bizServiceLine);
			//服务费
			Map<String, Integer> chargeMap = serviceLine.getChargeMap();
			for (Map.Entry<String, Integer> entry : chargeMap.entrySet()) {
				String key = entry.getKey();
				String[] keyAttr = key.split("_");
				BizServiceCharge serviceCharge = new BizServiceCharge();
				serviceCharge.setServiceLine(bizServiceLine);
				serviceCharge.setVarietyInfo(new BizVarietyInfo(Integer.valueOf(keyAttr[0])));
				serviceCharge.setServiceMode(Byte.valueOf(keyAttr[1]));
				serviceCharge.setServicePrice(new BigDecimal(entry.getValue()));
				super.save(serviceCharge);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizServiceCharge bizServiceCharge) {
		super.delete(bizServiceCharge);
	}

	/**
	 * 异步修改服务费
	 * @return
	 */
	@Transactional(readOnly = false)
	public String updateServiceCharge(BizServiceCharge serviceCharge, Integer variId) {
		if (serviceCharge.getServiceMode() == null || serviceCharge.getServicePrice() == null || serviceCharge.getId() == null || variId == null) {
			return "error";
		}
		BizServiceCharge bizServiceCharge = get(serviceCharge.getId());
		bizServiceCharge.setVarietyInfo(new BizVarietyInfo(variId));
		bizServiceCharge.setServiceMode(serviceCharge.getServiceMode());
		bizServiceCharge.setServicePrice(serviceCharge.getServicePrice());
		super.save(bizServiceCharge);
		return "ok";
	}
	
}
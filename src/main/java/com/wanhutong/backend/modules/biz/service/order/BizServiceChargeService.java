/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.biz.entity.order.BizServiceLine;
import com.wanhutong.backend.modules.sys.service.SysRegionService;
import com.wanhutong.backend.modules.sys.utils.DictUtils;
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
//		if (bizServiceCharge)
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
//		super.save(bizServiceCharge);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizServiceCharge bizServiceCharge) {
		super.delete(bizServiceCharge);
	}
	
}
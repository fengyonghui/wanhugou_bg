/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.service.SysRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizServiceLine;
import com.wanhutong.backend.modules.biz.dao.order.BizServiceLineDao;

/**
 * 服务费物流线路Service
 * @author Tengfei.Zhang
 * @version 2018-11-21
 */
@Service
@Transactional(readOnly = true)
public class BizServiceLineService extends CrudService<BizServiceLineDao, BizServiceLine> {

	@Autowired
	private SysRegionService sysRegionService;

	public BizServiceLine get(Integer id) {
		return super.get(id);
	}
	
	public List<BizServiceLine> findList(BizServiceLine bizServiceLine) {
		return super.findList(bizServiceLine);
	}
	
	public Page<BizServiceLine> findPage(Page<BizServiceLine> page, BizServiceLine bizServiceLine) {
		return super.findPage(page, bizServiceLine);
	}
	
	@Transactional(readOnly = false)
	public void save(BizServiceLine bizServiceLine) {
		if (bizServiceLine.getId() == null) {
		    super.save(bizServiceLine);
			return;
		}
		BizServiceLine serviceLine = get(bizServiceLine.getId());
		serviceLine.setProvince(sysRegionService.getByCode(bizServiceLine.getProvince().getCode()));
		serviceLine.setCity(sysRegionService.getByCode(bizServiceLine.getCity().getCode()));
		serviceLine.setRegion(sysRegionService.getByCode(bizServiceLine.getRegion().getCode()));
		serviceLine.setToProvince(sysRegionService.getByCode(bizServiceLine.getToProvince().getCode()));
		serviceLine.setToCity(sysRegionService.getByCode(bizServiceLine.getToCity().getCode()));
		serviceLine.setToRegion(sysRegionService.getByCode(bizServiceLine.getToRegion().getCode()));
		serviceLine.setUsable(bizServiceLine.getUsable() == null ? 0 : bizServiceLine.getUsable());
		super.save(serviceLine);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizServiceLine bizServiceLine) {
		super.delete(bizServiceLine);
	}
	
}
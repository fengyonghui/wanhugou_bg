/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.common.service.location;

import java.util.List;
import java.util.Stack;

import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.service.SysRegionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.dao.location.CommonLocationDao;

import javax.annotation.Resource;

/**
 * 地理位置信息Service
 * @author OuyangXiutian
 * @version 2017-12-21
 */
@Service
@Transactional(readOnly = true)
public class CommonLocationService extends CrudService<CommonLocationDao, CommonLocation> {
	@Resource
	private SysRegionService sysRegionService;

	public CommonLocation get(Integer id) {
		return super.get(id);
	}
	
	public List<CommonLocation> findList(CommonLocation commonLocation) {
		return super.findList(commonLocation);
	}
	
	public Page<CommonLocation> findPage(Page<CommonLocation> page, CommonLocation commonLocation) {
		return super.findPage(page, commonLocation);
	}
	
	@Transactional(readOnly = false)
	public void save(CommonLocation commonLocation) {
		super.save(commonLocation);
	}
	
	@Transactional(readOnly = false)
	public void delete(CommonLocation commonLocation) {
		super.delete(commonLocation);
	}

	@Transactional(readOnly = false)
	public CommonLocation updateCommonLocation(CommonLocation bizLocation){
		if(bizLocation == null){
			bizLocation = new CommonLocation();
		}
		Integer oldRegionId = bizLocation.getRegion()==null ? null : bizLocation.getRegion().getId();
		Integer selectedRegionId = bizLocation.getSelectedRegionId();
		Stack<SysRegion> regionStack=getRegionStack(selectedRegionId,oldRegionId);
		if(regionStack!=null){
			bizLocation.setProvince(regionStack.pop());
			if (regionStack.size() > 0) {
				bizLocation.setCity(regionStack.pop());
			}else if(regionStack.size()==0){
				SysRegion sysRegion1=new SysRegion();
				sysRegion1.setId(0);
				bizLocation.setCity(sysRegion1);
			}

			if (regionStack.size() > 0) {
				bizLocation.setRegion(regionStack.pop());
			}

		}
//		bizLocation.setPcrName(bizLocation.getFullpcrName());
		save(bizLocation);
		return bizLocation;
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


}
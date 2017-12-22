/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.common.service.location;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.dao.location.CommonLocationDao;

/**
 * 地理位置信息Service
 * @author OuyangXiutian
 * @version 2017-12-21
 */
@Service
@Transactional(readOnly = true)
public class CommonLocationService extends CrudService<CommonLocationDao, CommonLocation> {

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
	
}
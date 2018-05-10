/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.config.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.config.entity.CommonDynamicConfig;
import com.wanhutong.backend.modules.config.dao.CommonDynamicConfigDao;

/**
 * 动态配置文件Service
 * @author Ma.Qiang
 * @version 2018-04-28
 */
@Service
@Transactional(readOnly = true)
public class CommonDynamicConfigService extends CrudService<CommonDynamicConfigDao, CommonDynamicConfig> {

	public CommonDynamicConfig get(Integer id) {
		return super.get(id);
	}
	
	public List<CommonDynamicConfig> findList(CommonDynamicConfig commonDynamicConfig) {
		return super.findList(commonDynamicConfig);
	}
	
	public Page<CommonDynamicConfig> findPage(Page<CommonDynamicConfig> page, CommonDynamicConfig commonDynamicConfig) {
		return super.findPage(page, commonDynamicConfig);
	}
	
	@Transactional(readOnly = false)
	public void save(CommonDynamicConfig commonDynamicConfig) {
		super.save(commonDynamicConfig);
	}
	
	@Transactional(readOnly = false)
	public void delete(CommonDynamicConfig commonDynamicConfig) {
		super.delete(commonDynamicConfig);
	}
	
}
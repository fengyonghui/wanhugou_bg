/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.process.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.dao.CommonProcessDao;

/**
 * 通用流程Service
 * @author Ma.Qiang
 * @version 2018-04-28
 */
@Service
@Transactional(readOnly = true)
public class CommonProcessService extends CrudService<CommonProcessDao, CommonProcessEntity> {

	@Override
	public CommonProcessEntity get(Integer id) {
		return super.get(id);
	}

	@Override
	public List<CommonProcessEntity> findList(CommonProcessEntity commonProcessEntity) {
		return super.findList(commonProcessEntity);
	}

	@Override
	public Page<CommonProcessEntity> findPage(Page<CommonProcessEntity> page, CommonProcessEntity commonProcessEntity) {
		return super.findPage(page, commonProcessEntity);
	}

	@Override
	@Transactional(readOnly = false)
	public void save(CommonProcessEntity commonProcessEntity) {
		super.save(commonProcessEntity);
	}
	
	@Transactional(readOnly = false)
	@Override
	public void delete(CommonProcessEntity commonProcessEntity) {
		super.delete(commonProcessEntity);
	}
	
}
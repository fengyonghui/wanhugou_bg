/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.sys.service;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.sys.dao.SysCustDetailsDao;
import com.wanhutong.backend.modules.sys.entity.SysCustDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 采购商店铺Service
 * @author zx
 * @version 2018-03-05
 */
@Service
@Transactional(readOnly = true)
public class SysCustDetailsService extends CrudService<SysCustDetailsDao, SysCustDetails> {

	public SysCustDetails get(Integer id) {
		return super.get(id);
	}
	
	public List<SysCustDetails> findList(SysCustDetails sysCustDetails) {
		return super.findList(sysCustDetails);
	}
	
	public Page<SysCustDetails> findPage(Page<SysCustDetails> page, SysCustDetails sysCustDetails) {
		return super.findPage(page, sysCustDetails);
	}
	
	@Transactional(readOnly = false)
	public void save(SysCustDetails sysCustDetails) {
		super.save(sysCustDetails);
	}
	
	@Transactional(readOnly = false)
	public void delete(SysCustDetails sysCustDetails) {
		super.delete(sysCustDetails);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.po.BizCompletePaln;
import com.wanhutong.backend.modules.biz.dao.po.BizCompletePalnDao;

/**
 * 确认排产表Service
 * @author 王冰洋
 * @version 2018-07-24
 */
@Service
@Transactional(readOnly = true)
public class BizCompletePalnService extends CrudService<BizCompletePalnDao, BizCompletePaln> {

	public BizCompletePaln get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCompletePaln> findList(BizCompletePaln bizCompletePaln) {
		return super.findList(bizCompletePaln);
	}
	
	public Page<BizCompletePaln> findPage(Page<BizCompletePaln> page, BizCompletePaln bizCompletePaln) {
		return super.findPage(page, bizCompletePaln);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCompletePaln bizCompletePaln) {
		super.save(bizCompletePaln);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCompletePaln bizCompletePaln) {
		super.delete(bizCompletePaln);
	}
	
}
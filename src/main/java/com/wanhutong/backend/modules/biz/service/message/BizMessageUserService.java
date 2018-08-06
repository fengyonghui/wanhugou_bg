/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.message;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.message.BizMessageUser;
import com.wanhutong.backend.modules.biz.dao.message.BizMessageUserDao;

/**
 * 站内信关系Service
 * @author Ma.Qiang
 * @version 2018-07-27
 */
@Service
@Transactional(readOnly = true)
public class BizMessageUserService extends CrudService<BizMessageUserDao, BizMessageUser> {

	public BizMessageUser get(Integer id) {
		return super.get(id);
	}
	
	public List<BizMessageUser> findList(BizMessageUser bizMessageUser) {
		return super.findList(bizMessageUser);
	}
	
	public Page<BizMessageUser> findPage(Page<BizMessageUser> page, BizMessageUser bizMessageUser) {
		return super.findPage(page, bizMessageUser);
	}
	
	@Transactional(readOnly = false)
	public void save(BizMessageUser bizMessageUser) {
		super.save(bizMessageUser);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizMessageUser bizMessageUser) {
		super.delete(bizMessageUser);
	}
	
}
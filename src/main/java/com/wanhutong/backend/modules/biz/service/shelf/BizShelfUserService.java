/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shelf;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.shelf.BizShelfUser;
import com.wanhutong.backend.modules.biz.dao.shelf.BizShelfUserDao;

/**
 * 货架用户中间表Service
 * @author 张腾飞
 * @version 2018-01-11
 */
@Service
@Transactional(readOnly = true)
public class BizShelfUserService extends CrudService<BizShelfUserDao, BizShelfUser> {

	public BizShelfUser get(Integer id) {
		return super.get(id);
	}
	
	public List<BizShelfUser> findList(BizShelfUser bizShelfUser) {
		return super.findList(bizShelfUser);
	}
	
	public Page<BizShelfUser> findPage(Page<BizShelfUser> page, BizShelfUser bizShelfUser) {
		return super.findPage(page, bizShelfUser);
	}
	
	@Transactional(readOnly = false)
	public void save(BizShelfUser bizShelfUser) {
		super.save(bizShelfUser);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizShelfUser bizShelfUser) {
		super.delete(bizShelfUser);
	}
	
}
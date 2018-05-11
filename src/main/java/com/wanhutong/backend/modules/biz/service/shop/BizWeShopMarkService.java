/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shop;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.shop.BizWeShopMark;
import com.wanhutong.backend.modules.biz.dao.shop.BizWeShopMarkDao;

/**
 * 收藏微店Service
 * @author Oytang
 * @version 2018-04-10
 */
@Service
@Transactional(readOnly = true)
public class BizWeShopMarkService extends CrudService<BizWeShopMarkDao, BizWeShopMark> {

	public BizWeShopMark get(Integer id) {
		return super.get(id);
	}
	
	public List<BizWeShopMark> findList(BizWeShopMark bizWeShopMark) {
		return super.findList(bizWeShopMark);
	}
	
	public Page<BizWeShopMark> findPage(Page<BizWeShopMark> page, BizWeShopMark bizWeShopMark) {
		return super.findPage(page, bizWeShopMark);
	}
	
	@Transactional(readOnly = false)
	public void save(BizWeShopMark bizWeShopMark) {
		super.save(bizWeShopMark);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizWeShopMark bizWeShopMark) {
		super.delete(bizWeShopMark);
	}
	
}
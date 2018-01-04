/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shop;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.shop.BizShopCartDao;
import com.wanhutong.backend.modules.biz.entity.shop.BizShopCart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品购物车Service
 * @author OuyangXiutian
 * @version 2018-01-03
 */
@Service
@Transactional(readOnly = true)
public class BizShopCartService extends CrudService<BizShopCartDao, BizShopCart> {

	public BizShopCart get(Integer id) {
		return super.get(id);
	}
	
	public List<BizShopCart> findList(BizShopCart bizShopCart) {
		return super.findList(bizShopCart);
	}
	
	
	public Page<BizShopCart> findPage(Page<BizShopCart> page, BizShopCart bizShopCart) {
		return super.findPage(page, bizShopCart);
	}
	
	@Transactional(readOnly = false)
	public void save(BizShopCart bizShopCart) {
		super.save(bizShopCart);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizShopCart bizShopCart) {
		super.delete(bizShopCart);
	}
	
}
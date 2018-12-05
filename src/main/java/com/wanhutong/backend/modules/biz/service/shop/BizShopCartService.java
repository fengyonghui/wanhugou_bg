/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shop;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.shop.BizShopCartDao;
import com.wanhutong.backend.modules.biz.entity.shop.BizShopCart;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private BizShopCartDao bizShopCartDao;

	public BizShopCart get(Integer id) {
		return super.get(id);
	}
	
	public List<BizShopCart> findList(BizShopCart bizShopCart) {
		return super.findList(bizShopCart);
	}
	
	
	public Page<BizShopCart> findPage(Page<BizShopCart> page, BizShopCart bizShopCart) {
		User user= UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizShopCart);
		}else {
			bizShopCart.setDataStatus("filter");
			bizShopCart.getSqlMap().put("shopCart", BaseService.dataScopeFilter(user, "s", "su"));
			return super.findPage(page, bizShopCart);
		}


	}
	
	@Transactional(readOnly = false)
	public void save(BizShopCart bizShopCart) {
		super.save(bizShopCart);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizShopCart bizShopCart) {
		super.delete(bizShopCart);
	}

	/**
	 * //删除购物车数据
	 * @param status
	 * @param userId
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateShopCartByUserId(Integer status, Integer updateId, Integer userId) {
		bizShopCartDao.updateShopCartByUserId(status, updateId, userId);
	}

	/**
	 * 删除购物车中间表数据
	 * @param status
	 * @param userId
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void updateCartSkuByUserId(Integer status, Integer updateId, Integer userId) {
		bizShopCartDao.updateCartSkuByUserId(status, updateId, userId);
	}
	
}
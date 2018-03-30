/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shelf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.biz.dao.shelf.BizOpShelfInfoDao;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.product.BizProductInfoService;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfSku;
import com.wanhutong.backend.modules.biz.dao.shelf.BizOpShelfSkuDao;

import javax.annotation.Resource;

/**
 * 商品上架管理Service
 * @author liuying
 * @version 2017-12-19
 */
@Service
@Transactional(readOnly = true)
public class BizOpShelfSkuService extends CrudService<BizOpShelfSkuDao, BizOpShelfSku> {
	@Resource
	private BizProductInfoService bizProductInfoService;
	@Resource
	private BizSkuInfoService bizSkuInfoService;
	@Autowired
	private BizOpShelfSkuDao bizOpShelfSkuDao;
	public BizOpShelfSku get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOpShelfSku> findList(BizOpShelfSku bizOpShelfSku) {
		return super.findList(bizOpShelfSku);
	}
	
	public Page<BizOpShelfSku> findPage(Page<BizOpShelfSku> page, BizOpShelfSku bizOpShelfSku) {
		User user= UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizOpShelfSku);
		}else {
			bizOpShelfSku.getSqlMap().put("shelfSku", BaseService.dataScopeFilter(user, "so", "suc"));
			return super.findPage(page, bizOpShelfSku);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(BizOpShelfSku bizOpShelfSku) {
		Integer skuId=bizOpShelfSku.getSkuInfo().getId();//skuId 为key
		BizSkuInfo skuInfo=bizSkuInfoService.get(skuId);
		BizProductInfo bizProductInfo=bizProductInfoService.get(skuInfo.getProductInfo().getId());
			if(bizProductInfo.getMinPrice()>bizOpShelfSku.getSalePrice()){
				bizProductInfo.setMinPrice(bizOpShelfSku.getSalePrice());
			}
			if(bizProductInfo.getMaxPrice()==null){
				bizProductInfo.setMaxPrice(0.0);

			}
				if(bizProductInfo.getMaxPrice()<bizOpShelfSku.getSalePrice()){
					bizProductInfo.setMaxPrice(bizOpShelfSku.getSalePrice());
				}

			if(bizProductInfo.getMinPrice()==0.0){
				bizProductInfo.setMinPrice(bizOpShelfSku.getSalePrice());
			}

		bizProductInfoService.saveProd(bizProductInfo);
		bizOpShelfSku.setProductInfo(bizProductInfo);
		super.save(bizOpShelfSku);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOpShelfSku bizOpShelfSku) {
		super.delete(bizOpShelfSku);
	}

	@Transactional(readOnly = false)
	public void updateShelves(BizOpShelfSku bizOpShelfSku){
		bizOpShelfSkuDao.shelvesUpdate(bizOpShelfSku);
	}

	@Transactional(readOnly = false)
	public void updateDateTime(BizOpShelfSku bizOpShelfSku){
		bizOpShelfSkuDao.dateTimeUpdate(bizOpShelfSku);
	}

	public void updateSkuIdById(Integer id, Integer skuId) {
		bizOpShelfSkuDao.updateSkuIdById(id,skuId);
	}
}
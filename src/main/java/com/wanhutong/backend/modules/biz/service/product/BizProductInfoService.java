/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropValue;
import com.wanhutong.backend.modules.biz.entity.sku.BizSkuInfo;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;
import org.springframework.beans.factory.annotation.Autowired;
import com.wanhutong.backend.modules.biz.service.sku.BizSkuInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;

import javax.annotation.Resource;


/**
 * 产品信息表Service
 * @author zx
 * @version 2017-12-13
 */
@Service
@Transactional(readOnly = true)
public class BizProductInfoService extends CrudService<BizProductInfoDao, BizProductInfo> {
	@Resource
	private BizCatePropValueService bizCatePropValueService;
	@Autowired
	private BizProductInfoDao bizProductInfoDao;
	private BizSkuInfoService bizSkuInfoService;


	public BizProductInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizProductInfo> findList(BizProductInfo bizProductInfo) {
		return super.findList(bizProductInfo);
	}
	
	public Page<BizProductInfo> findPage(Page<BizProductInfo> page, BizProductInfo bizProductInfo) {
		return super.findPage(page, bizProductInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizProductInfo bizProductInfo) {

		BizCatePropValue bizCatePropValue=bizCatePropValueService.get(bizProductInfo.getCatePropValue().getId());
		bizProductInfo.setBrandName(bizCatePropValue.getValue());
		super.save(bizProductInfo);
		bizProductInfoDao.deleteProdCate(bizProductInfo);
		if (bizProductInfo.getCategoryInfoList().size() > 0){
			bizProductInfoDao.insertProdCate(bizProductInfo);
		}

	}


	@Transactional(readOnly = false)
	public void delete(BizProductInfo bizProductInfo) {
		super.delete(bizProductInfo);
	}
	
}
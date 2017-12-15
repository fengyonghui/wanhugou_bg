/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.List;

import com.wanhutong.backend.modules.biz.dao.product.BizProductInfoDao;
import com.wanhutong.backend.modules.biz.service.category.BizCatePropValueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizProductInfo;


/**
 * 产品信息表Service
 * @author zx
 * @version 2017-12-13
 */
@Service
@Transactional(readOnly = true)
public class BizProductInfoService extends CrudService<BizProductInfoDao, BizProductInfo> {
	private BizCatePropValueService bizCatePropValueService;
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
		super.save(bizProductInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizProductInfo bizProductInfo) {
		super.delete(bizProductInfo);
	}
	
}
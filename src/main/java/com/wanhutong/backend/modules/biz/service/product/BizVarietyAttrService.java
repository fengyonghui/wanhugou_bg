/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.product;

import java.util.List;

import com.wanhutong.backend.common.utils.StringUtils;
import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.product.BizVarietyAttr;
import com.wanhutong.backend.modules.biz.dao.product.BizVarietyAttrDao;

/**
 * 分类属性中间表Service
 * @author ZhangTengfei
 * @version 2018-05-28
 */
@Service
@Transactional(readOnly = true)
public class BizVarietyAttrService extends CrudService<BizVarietyAttrDao, BizVarietyAttr> {

	public BizVarietyAttr get(Integer id) {
		return super.get(id);
	}
	
	public List<BizVarietyAttr> findList(BizVarietyAttr bizVarietyAttr) {
		return super.findList(bizVarietyAttr);
	}
	
	public Page<BizVarietyAttr> findPage(Page<BizVarietyAttr> page, BizVarietyAttr bizVarietyAttr) {
		return super.findPage(page, bizVarietyAttr);
	}
	
	@Transactional(readOnly = false)
	public void save(BizVarietyAttr bizVarietyAttr) {
        super.save(bizVarietyAttr);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizVarietyAttr bizVarietyAttr) {
		super.delete(bizVarietyAttr);
	}
	
}
/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.category.BizCatePropertyInfo;
import com.wanhutong.backend.modules.biz.dao.category.BizCatePropertyInfoDao;

/**
 * 记录当前分类下的所有属性Service
 * @author liuying
 * @version 2017-12-06
 */
@Service
@Transactional(readOnly = true)
public class BizCatePropertyInfoService extends CrudService<BizCatePropertyInfoDao, BizCatePropertyInfo> {

	public BizCatePropertyInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCatePropertyInfo> findList(BizCatePropertyInfo bizCatePropertyInfo) {
		return super.findList(bizCatePropertyInfo);
	}
	
	public Page<BizCatePropertyInfo> findPage(Page<BizCatePropertyInfo> page, BizCatePropertyInfo bizCatePropertyInfo) {
		return super.findPage(page, bizCatePropertyInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCatePropertyInfo bizCatePropertyInfo) {
		super.save(bizCatePropertyInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCatePropertyInfo bizCatePropertyInfo) {
		super.delete(bizCatePropertyInfo);
	}
	
}
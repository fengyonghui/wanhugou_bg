/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.category.BizCatelogInfo;
import com.wanhutong.backend.modules.biz.dao.category.BizCatelogInfoDao;

/**
 * 目录分类表Service
 * @author liuying
 * @version 2017-12-06
 */
@Service
@Transactional(readOnly = true)
public class BizCatelogInfoService extends CrudService<BizCatelogInfoDao, BizCatelogInfo> {

	public BizCatelogInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCatelogInfo> findList(BizCatelogInfo bizCatelogInfo) {
		return super.findList(bizCatelogInfo);
	}
	
	public Page<BizCatelogInfo> findPage(Page<BizCatelogInfo> page, BizCatelogInfo bizCatelogInfo) {
		return super.findPage(page, bizCatelogInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCatelogInfo bizCatelogInfo) {
		super.save(bizCatelogInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCatelogInfo bizCatelogInfo) {
		super.delete(bizCatelogInfo);
	}
	
}
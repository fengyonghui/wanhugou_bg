/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.category;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.category.BizCategoryInfoDao;
import com.wanhutong.backend.modules.biz.entity.category.BizCategoryInfo;
import com.wanhutong.backend.modules.biz.entity.category.BizCatelogInfo;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 垂直商品类目表Service
 * @author liuying
 * @version 2017-12-06
 */
@Service
@Transactional(readOnly = true)
public class BizCategoryInfoService extends CrudService<BizCategoryInfoDao, BizCategoryInfo> {

	public BizCategoryInfo get(Integer id) {
		return super.get(id);
	}
	

	@Transactional(readOnly = true)
	public List<BizCategoryInfo> findList(BizCategoryInfo bizCategoryInfo){
		if(bizCategoryInfo != null){
			bizCategoryInfo.setParentIds(bizCategoryInfo.getParentIds()+"%");
			return dao.findByParentIdsLike(bizCategoryInfo);
		}
		return  new ArrayList<BizCategoryInfo>();
	}
	public List<BizCategoryInfo> findListInfo(BizCatelogInfo catelogInfo){
		return UserUtils.getCategoryInfoList(catelogInfo);
	}
	public Page<BizCategoryInfo> findPage(Page<BizCategoryInfo> page, BizCategoryInfo bizCategoryInfo) {
		return super.findPage(page, bizCategoryInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCategoryInfo bizCategoryInfo) {
		super.save(bizCategoryInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCategoryInfo bizCategoryInfo) {
		super.delete(bizCategoryInfo);
	}
	
}
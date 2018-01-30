/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.cms;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.cms.BizCmsPageInfo;
import com.wanhutong.backend.modules.biz.dao.cms.BizCmsPageInfoDao;

/**
 * 定义产品页面Service
 * @author OuyangXiutian
 * @version 2018-01-30
 */
@Service
@Transactional(readOnly = true)
public class BizCmsPageInfoService extends CrudService<BizCmsPageInfoDao, BizCmsPageInfo> {

	public BizCmsPageInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCmsPageInfo> findList(BizCmsPageInfo bizCmsPageInfo) {
		return super.findList(bizCmsPageInfo);
	}
	
	public Page<BizCmsPageInfo> findPage(Page<BizCmsPageInfo> page, BizCmsPageInfo bizCmsPageInfo) {
		return super.findPage(page, bizCmsPageInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCmsPageInfo bizCmsPageInfo) {
		super.save(bizCmsPageInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCmsPageInfo bizCmsPageInfo) {
		super.delete(bizCmsPageInfo);
	}
	
}
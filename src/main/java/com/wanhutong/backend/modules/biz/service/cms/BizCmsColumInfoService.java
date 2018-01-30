/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.cms;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.cms.BizCmsColumInfo;
import com.wanhutong.backend.modules.biz.dao.cms.BizCmsColumInfoDao;

/**
 * 页面栏目设置Service
 * @author OuyangXiutian
 * @version 2018-01-30
 */
@Service
@Transactional(readOnly = true)
public class BizCmsColumInfoService extends CrudService<BizCmsColumInfoDao, BizCmsColumInfo> {

	public BizCmsColumInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCmsColumInfo> findList(BizCmsColumInfo bizCmsColumInfo) {
		return super.findList(bizCmsColumInfo);
	}
	
	public Page<BizCmsColumInfo> findPage(Page<BizCmsColumInfo> page, BizCmsColumInfo bizCmsColumInfo) {
		return super.findPage(page, bizCmsColumInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCmsColumInfo bizCmsColumInfo) {
		super.save(bizCmsColumInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCmsColumInfo bizCmsColumInfo) {
		super.delete(bizCmsColumInfo);
	}
	
}
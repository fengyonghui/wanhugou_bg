/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.message;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.message.BizMessageInfo;
import com.wanhutong.backend.modules.biz.dao.message.BizMessageInfoDao;

/**
 * 站内信Service
 * @author Ma.Qiang
 * @version 2018-07-27
 */
@Service
@Transactional(readOnly = true)
public class BizMessageInfoService extends CrudService<BizMessageInfoDao, BizMessageInfo> {

	public BizMessageInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizMessageInfo> findList(BizMessageInfo bizMessageInfo) {
		return super.findList(bizMessageInfo);
	}
	
	public Page<BizMessageInfo> findPage(Page<BizMessageInfo> page, BizMessageInfo bizMessageInfo) {
		return super.findPage(page, bizMessageInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizMessageInfo bizMessageInfo) {
		super.save(bizMessageInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizMessageInfo bizMessageInfo) {
		super.delete(bizMessageInfo);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.paltform;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.paltform.BizPlatformInfo;
import com.wanhutong.backend.modules.biz.dao.paltform.BizPlatformInfoDao;

/**
 * Android, iOS, PC, 线下Service
 * @author OuyangXiutian
 * @version 2017-12-21
 */
@Service
@Transactional(readOnly = true)
public class BizPlatformInfoService extends CrudService<BizPlatformInfoDao, BizPlatformInfo> {

	public BizPlatformInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizPlatformInfo> findList(BizPlatformInfo bizPlatformInfo) {
		return super.findList(bizPlatformInfo);
	}
	
	public Page<BizPlatformInfo> findPage(Page<BizPlatformInfo> page, BizPlatformInfo bizPlatformInfo) {
		return super.findPage(page, bizPlatformInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizPlatformInfo bizPlatformInfo) {
		super.save(bizPlatformInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizPlatformInfo bizPlatformInfo) {
		super.delete(bizPlatformInfo);
	}
	
}
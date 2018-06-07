/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.variety;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.variety.BizVarietyUserInfo;
import com.wanhutong.backend.modules.biz.dao.variety.BizVarietyUserInfoDao;

/**
 * 品类与用户 关联Service
 * @author Oy
 * @version 2018-05-31
 */
@Service
@Transactional(readOnly = true)
public class BizVarietyUserInfoService extends CrudService<BizVarietyUserInfoDao, BizVarietyUserInfo> {

	public BizVarietyUserInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizVarietyUserInfo> findList(BizVarietyUserInfo bizVarietyUserInfo) {
		return super.findList(bizVarietyUserInfo);
	}
	
	public Page<BizVarietyUserInfo> findPage(Page<BizVarietyUserInfo> page, BizVarietyUserInfo bizVarietyUserInfo) {
		return super.findPage(page, bizVarietyUserInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizVarietyUserInfo bizVarietyUserInfo) {
		super.save(bizVarietyUserInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizVarietyUserInfo bizVarietyUserInfo) {
		super.delete(bizVarietyUserInfo);
	}
	
}
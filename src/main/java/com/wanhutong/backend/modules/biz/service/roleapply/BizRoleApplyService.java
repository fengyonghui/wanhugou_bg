/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.roleapply;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.roleapply.BizRoleApply;
import com.wanhutong.backend.modules.biz.dao.roleapply.BizRoleApplyDao;

/**
 * 零售申请表Service
 * @author wangby
 * @version 2018-10-30
 */
@Service
@Transactional(readOnly = true)
public class BizRoleApplyService extends CrudService<BizRoleApplyDao, BizRoleApply> {

	public BizRoleApply get(Integer id) {
		return super.get(id);
	}
	
	public List<BizRoleApply> findList(BizRoleApply bizRoleApply) {
		return super.findList(bizRoleApply);
	}
	
	public Page<BizRoleApply> findPage(Page<BizRoleApply> page, BizRoleApply bizRoleApply) {
		return super.findPage(page, bizRoleApply);
	}
	
	@Transactional(readOnly = false)
	public void save(BizRoleApply bizRoleApply) {
		super.save(bizRoleApply);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizRoleApply bizRoleApply) {
		super.delete(bizRoleApply);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizDrawBack;
import com.wanhutong.backend.modules.biz.dao.order.BizDrawBackDao;

/**
 * 退款记录Service
 * @author 王冰洋
 * @version 2018-07-13
 */
@Service
@Transactional(readOnly = true)
public class BizDrawBackService extends CrudService<BizDrawBackDao, BizDrawBack> {

	public BizDrawBack get(Integer id) {
		return super.get(id);
	}
	
	public List<BizDrawBack> findList(BizDrawBack bizDrawBack) {
		return super.findList(bizDrawBack);
	}
	
	public Page<BizDrawBack> findPage(Page<BizDrawBack> page, BizDrawBack bizDrawBack) {
		return super.findPage(page, bizDrawBack);
	}
	
	@Transactional(readOnly = false)
	public void save(BizDrawBack bizDrawBack) {
		super.save(bizDrawBack);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizDrawBack bizDrawBack) {
		super.delete(bizDrawBack);
	}
	
}
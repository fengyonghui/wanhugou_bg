/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.po;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.po.BizPoDetail;
import com.wanhutong.backend.modules.biz.dao.po.BizPoDetailDao;

/**
 * 采购订单信息信息Service
 * @author liuying
 * @version 2017-12-30
 */
@Service
@Transactional(readOnly = true)
public class BizPoDetailService extends CrudService<BizPoDetailDao, BizPoDetail> {

	public BizPoDetail get(Integer id) {
		return super.get(id);
	}
	
	public List<BizPoDetail> findList(BizPoDetail bizPoDetail) {
		return super.findList(bizPoDetail);
	}
	
	public Page<BizPoDetail> findPage(Page<BizPoDetail> page, BizPoDetail bizPoDetail) {
		return super.findPage(page, bizPoDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(BizPoDetail bizPoDetail) {
		super.save(bizPoDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizPoDetail bizPoDetail) {
		super.delete(bizPoDetail);
	}
	
}
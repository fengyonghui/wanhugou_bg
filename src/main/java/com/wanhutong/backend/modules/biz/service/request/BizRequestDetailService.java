/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.dao.request.BizRequestDetailDao;

/**
 * 备货清单详细信息Service
 * @author liuying
 * @version 2017-12-23
 */
@Service
@Transactional(readOnly = true)
public class BizRequestDetailService extends CrudService<BizRequestDetailDao, BizRequestDetail> {

	public BizRequestDetail get(Integer id) {
		return super.get(id);
	}
	
	public List<BizRequestDetail> findList(BizRequestDetail bizRequestDetail) {
		return super.findList(bizRequestDetail);
	}
	
	public Page<BizRequestDetail> findPage(Page<BizRequestDetail> page, BizRequestDetail bizRequestDetail) {
		return super.findPage(page, bizRequestDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(BizRequestDetail bizRequestDetail) {
		super.save(bizRequestDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizRequestDetail bizRequestDetail) {
		super.delete(bizRequestDetail);
	}
	
}
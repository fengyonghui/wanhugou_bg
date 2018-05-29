/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.request;

import java.util.List;

import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private BizRequestDetailDao bizRequestDetailDao;

	public BizRequestDetail get(Integer id) {
		return super.get(id);
	}
	
	public List<BizRequestDetail> findList(BizRequestDetail bizRequestDetail) {
		return super.findList(bizRequestDetail);
	}
	
	public Page<BizRequestDetail> findPage(Page<BizRequestDetail> page, BizRequestDetail bizRequestDetail) {
		return super.findPage(page, bizRequestDetail);
	}

	public  List<BizRequestDetail>findReqTotalByVendor(BizRequestHeader bizRequestHeader){
		return bizRequestDetailDao.findReqTotalByVendor(bizRequestHeader);
	}


	@Transactional(readOnly = false)
	public void save(BizRequestDetail bizRequestDetail) {
		super.save(bizRequestDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizRequestDetail bizRequestDetail) {
		super.delete(bizRequestDetail);
	}

	@Transactional(readOnly = false)
	public List<BizRequestDetail> findPoRequet(BizRequestDetail bizRequestDetail) {
		return dao.findPoRequet(bizRequestDetail);
	}

}
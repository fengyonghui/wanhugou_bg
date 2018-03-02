/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.vend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.vend.BizVendInfo;
import com.wanhutong.backend.modules.biz.dao.vend.BizVendInfoDao;

/**
 * 供应商拓展表Service
 * @author liuying
 * @version 2018-02-24
 */
@Service
@Transactional(readOnly = true)
public class BizVendInfoService extends CrudService<BizVendInfoDao, BizVendInfo> {

	@Autowired
	private BizVendInfoDao bizVendInfoDao;

	public BizVendInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizVendInfo> findList(BizVendInfo bizVendInfo) {
		return super.findList(bizVendInfo);
	}
	
	public Page<BizVendInfo> findPage(Page<BizVendInfo> page, BizVendInfo bizVendInfo) {
		return super.findPage(page, bizVendInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(BizVendInfo bizVendInfo) {
		super.save(bizVendInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizVendInfo bizVendInfo) {
		super.delete(bizVendInfo);
	}

	@Transactional(readOnly = false)
	public void recover(BizVendInfo bizVendInfo) {
		bizVendInfoDao.recover(bizVendInfo);
	}
	
}
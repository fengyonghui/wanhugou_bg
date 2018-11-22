/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.message;

import java.util.Date;
import java.util.List;

import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.message.BizMessageOfficeType;
import com.wanhutong.backend.modules.biz.dao.message.BizMessageOfficeTypeDao;

/**
 * 站内信发送用户类型表Service
 * @author wangby
 * @version 2018-11-22
 */
@Service
@Transactional(readOnly = true)
public class BizMessageOfficeTypeService extends CrudService<BizMessageOfficeTypeDao, BizMessageOfficeType> {

	@Autowired
	private BizMessageOfficeTypeDao bizMessageOfficeTypeDao;

	public BizMessageOfficeType get(Integer id) {
		return super.get(id);
	}
	
	public List<BizMessageOfficeType> findList(BizMessageOfficeType bizMessageOfficeType) {
		return super.findList(bizMessageOfficeType);
	}
	
	public Page<BizMessageOfficeType> findPage(Page<BizMessageOfficeType> page, BizMessageOfficeType bizMessageOfficeType) {
		return super.findPage(page, bizMessageOfficeType);
	}
	
	@Transactional(readOnly = false)
	public void save(BizMessageOfficeType bizMessageOfficeType) {
		super.save(bizMessageOfficeType);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizMessageOfficeType bizMessageOfficeType) {
		super.delete(bizMessageOfficeType);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public int insertBatch(List<Integer> officeTypeList, Integer messageId, Integer userId) {
		User user = UserUtils.getUser();
		return bizMessageOfficeTypeDao.insertBatch(officeTypeList, messageId, user.getId());
	}
}
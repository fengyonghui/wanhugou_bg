/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderComment;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderCommentDao;

/**
 * 订单备注表Service
 * @author oy
 * @version 2018-06-12
 */
@Service
@Transactional(readOnly = true)
public class BizOrderCommentService extends CrudService<BizOrderCommentDao, BizOrderComment> {

	public BizOrderComment get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderComment> findList(BizOrderComment bizOrderComment) {
		return super.findList(bizOrderComment);
	}
	
	public Page<BizOrderComment> findPage(Page<BizOrderComment> page, BizOrderComment bizOrderComment) {
		return super.findPage(page, bizOrderComment);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderComment bizOrderComment) {
		super.save(bizOrderComment);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderComment bizOrderComment) {
		super.delete(bizOrderComment);
	}
	
}
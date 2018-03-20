/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.feedback;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.feedback.BizFeedbackDao;
import com.wanhutong.backend.modules.biz.entity.feedback.BizFeedback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 意见反馈Service
 * @author zx
 * @version 2018-03-06
 */
@Service
@Transactional(readOnly = true)
public class BizFeedbackService extends CrudService<BizFeedbackDao, BizFeedback> {

	public BizFeedback get(Integer id) {
		return super.get(id);
	}
	
	public List<BizFeedback> findList(BizFeedback bizFeedback) {
		return super.findList(bizFeedback);
	}
	
	public Page<BizFeedback> findPage(Page<BizFeedback> page, BizFeedback bizFeedback) {
		return super.findPage(page, bizFeedback);
	}
	
	@Transactional(readOnly = false)
	public void save(BizFeedback bizFeedback) {
		super.save(bizFeedback);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizFeedback bizFeedback) {
		super.delete(bizFeedback);
	}
	
}
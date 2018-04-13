/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.pay;

import java.util.List;

import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.dao.pay.BizPayRecordDao;

/**
 * 交易记录Service
 * @author OuyangXiutian
 * @version 2018-01-20
 */
@Service
@Transactional(readOnly = true)
public class BizPayRecordService extends CrudService<BizPayRecordDao, BizPayRecord> {

	public BizPayRecord get(Integer id) {
		return super.get(id);
	}
	
	public List<BizPayRecord> findList(BizPayRecord bizPayRecord) {
		return super.findList(bizPayRecord);
	}
	
	public Page<BizPayRecord> findPage(Page<BizPayRecord> page, BizPayRecord bizPayRecord) {
		User user=UserUtils.getUser();
		if(user.isAdmin()){
			bizPayRecord.setDataStatus("filter");
		}
		return super.findPage(page, bizPayRecord);
	}
	
	@Transactional(readOnly = false)
	public void save(BizPayRecord bizPayRecord) {
		super.save(bizPayRecord);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizPayRecord bizPayRecord) {
		super.delete(bizPayRecord);
	}
	
}
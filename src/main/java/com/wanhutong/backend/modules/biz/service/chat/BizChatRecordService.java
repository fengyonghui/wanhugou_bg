/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.chat;

import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;
import com.wanhutong.backend.modules.biz.dao.chat.BizChatRecordDao;

/**
 * 沟通记录：品类主管或客户专员，机构沟通Service
 * @author Oy
 * @version 2018-05-22
 */
@Service
@Transactional(readOnly = true)
public class BizChatRecordService extends CrudService<BizChatRecordDao, BizChatRecord> {

	public BizChatRecord get(Integer id) {
		return super.get(id);
	}
	
	public List<BizChatRecord> findList(BizChatRecord bizChatRecord) {
		return super.findList(bizChatRecord);
	}
	
	public Page<BizChatRecord> findPage(Page<BizChatRecord> page, BizChatRecord bizChatRecord) {
		User user = UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizChatRecord);
		}else{
			bizChatRecord.getSqlMap().put("chat", BaseService.dataScopeFilter(user, "so", "su"));
			return super.findPage(page, bizChatRecord);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(BizChatRecord bizChatRecord) {
		super.save(bizChatRecord);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizChatRecord bizChatRecord) {
		super.delete(bizChatRecord);
	}
	
}
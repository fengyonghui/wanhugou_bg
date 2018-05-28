/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.chat;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.chat.BizChatRecord;

/**
 * 沟通记录：品类主管或客户专员，机构沟通DAO接口
 * @author Oy
 * @version 2018-05-22
 */
@MyBatisDao
public interface BizChatRecordDao extends CrudDao<BizChatRecord> {
	
}
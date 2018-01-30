/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.cms;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.cms.BizCmsPageInfo;

/**
 * 定义产品页面DAO接口
 * @author OuyangXiutian
 * @version 2018-01-30
 */
@MyBatisDao
public interface BizCmsPageInfoDao extends CrudDao<BizCmsPageInfo> {
	
}
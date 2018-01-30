/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.request;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;

import java.util.List;

/**
 * 备货清单详细信息DAO接口
 * @author liuying
 * @version 2017-12-23
 */
@MyBatisDao
public interface BizRequestDetailDao extends CrudDao<BizRequestDetail> {

    List<BizRequestDetail>findReqTotalByVendor(BizRequestHeader bizRequestHeader);
	
}
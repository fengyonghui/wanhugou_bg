/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shelf;

import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import com.wanhutong.backend.modules.biz.dao.shelf.BizOpShelfInfoDao;

/**
 * 运营货架信息Service
 * @author liuying
 * @version 2017-12-19
 */
@Service
@Transactional(readOnly = true)
public class BizOpShelfInfoService extends CrudService<BizOpShelfInfoDao, BizOpShelfInfo> {

	public BizOpShelfInfo get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOpShelfInfo> findList(BizOpShelfInfo bizOpShelfInfo) {
		return super.findList(bizOpShelfInfo);
	}
	
	public Page<BizOpShelfInfo> findPage(Page<BizOpShelfInfo> page, BizOpShelfInfo bizOpShelfInfo) {
		User user= UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizOpShelfInfo);
		}else {
			bizOpShelfInfo.getSqlMap().put("shelfInfo", BaseService.dataScopeFilter(user, "so", "suc"));
			return super.findPage(page, bizOpShelfInfo);
		}

	}
	
	@Transactional(readOnly = false)
	public void save(BizOpShelfInfo bizOpShelfInfo) {
		super.save(bizOpShelfInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOpShelfInfo bizOpShelfInfo) {
		super.delete(bizOpShelfInfo);
	}
	
}
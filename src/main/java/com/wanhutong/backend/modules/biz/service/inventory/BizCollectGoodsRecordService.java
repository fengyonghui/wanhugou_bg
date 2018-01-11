/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import java.util.List;

import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import com.wanhutong.backend.modules.biz.dao.inventory.BizCollectGoodsRecordDao;

/**
 * 收货记录表Service
 * @author 张腾飞
 * @version 2018-01-03
 */
@Service
@Transactional(readOnly = true)
public class BizCollectGoodsRecordService extends CrudService<BizCollectGoodsRecordDao, BizCollectGoodsRecord> {

	public BizCollectGoodsRecord get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCollectGoodsRecord> findList(BizCollectGoodsRecord bizCollectGoodsRecord) {

		return super.findList(bizCollectGoodsRecord);
	}
	
	public Page<BizCollectGoodsRecord> findPage(Page<BizCollectGoodsRecord> page, BizCollectGoodsRecord bizCollectGoodsRecord) {
		User user=UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizCollectGoodsRecord);
		}else {
			bizCollectGoodsRecord.getSqlMap().put("collectGoodsRecord", BaseService.dataScopeFilter(user, "s", "su"));
			return super.findPage(page, bizCollectGoodsRecord);
		}


	}
	
	@Transactional(readOnly = false)
	public void save(BizCollectGoodsRecord bizCollectGoodsRecord) {
		super.save(bizCollectGoodsRecord);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCollectGoodsRecord bizCollectGoodsRecord) {
		super.delete(bizCollectGoodsRecord);
	}
	
}
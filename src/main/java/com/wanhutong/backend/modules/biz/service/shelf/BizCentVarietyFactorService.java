/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.shelf;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.shelf.BizCentVarietyFactor;
import com.wanhutong.backend.modules.biz.dao.shelf.BizCentVarietyFactorDao;

/**
 * 采购中心品类阶梯价Service
 * @author ZhangTengfei
 * @version 2018-04-27
 */
@Service
@Transactional(readOnly = true)
public class BizCentVarietyFactorService extends CrudService<BizCentVarietyFactorDao, BizCentVarietyFactor> {

	public BizCentVarietyFactor get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCentVarietyFactor> findList(BizCentVarietyFactor bizCentVarietyFactor) {
		return super.findList(bizCentVarietyFactor);
	}
	
	public Page<BizCentVarietyFactor> findPage(Page<BizCentVarietyFactor> page, BizCentVarietyFactor bizCentVarietyFactor) {
		return super.findPage(page, bizCentVarietyFactor);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCentVarietyFactor bizCentVarietyFactor) {
        String serviceFactors = bizCentVarietyFactor.getServiceFactors();
        String minQtys = bizCentVarietyFactor.getMinQtys();
        String maxQtys = bizCentVarietyFactor.getMaxQtys();
        String[] serviceFactorArr = serviceFactors.split(",".trim());
        String[] minQtyArr = minQtys.split(",".trim());
        String[] maxQtyArr = maxQtys.split(",".trim());
        for(int i = 0; i < serviceFactorArr.length; i++) {
            bizCentVarietyFactor.setServiceFactor(Integer.parseInt(serviceFactorArr[i]));
            bizCentVarietyFactor.setMinQty(Integer.parseInt(minQtyArr[i]));
            bizCentVarietyFactor.setMaxQty(Integer.parseInt(maxQtyArr[i]));
            super.save(bizCentVarietyFactor);
        }
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCentVarietyFactor bizCentVarietyFactor) {
		super.delete(bizCentVarietyFactor);
	}
	
}
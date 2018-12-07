/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.math.BigDecimal;
import java.util.List;

import com.wanhutong.backend.modules.biz.entity.category.BizVarietyInfo;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizFreightConfig;
import com.wanhutong.backend.modules.biz.dao.order.BizFreightConfigDao;

/**
 * 服务费设置Service
 * @author Tengfei.Zhang
 * @version 2018-12-04
 */
@Service
@Transactional(readOnly = true)
public class BizFreightConfigService extends CrudService<BizFreightConfigDao, BizFreightConfig> {

	public BizFreightConfig get(Integer id) {
		return super.get(id);
	}
	
	public List<BizFreightConfig> findList(BizFreightConfig bizFreightConfig) {
		return super.findList(bizFreightConfig);
	}
	
	public Page<BizFreightConfig> findPage(Page<BizFreightConfig> page, BizFreightConfig bizFreightConfig) {
		return super.findPage(page, bizFreightConfig);
	}
	
	@Transactional(readOnly = false)
	public void save(BizFreightConfig bizFreightConfig) {
		List<BizFreightConfig> freightConfigList = bizFreightConfig.getFreightConfigList();
		for (BizFreightConfig freightConfig : freightConfigList) {
			Byte defaultStatus = freightConfig.getDefaultStatus();
			BigDecimal feeCharge = freightConfig.getFeeCharge();
			BigDecimal minDistance = freightConfig.getMinDistance();
			BigDecimal maxDistance = freightConfig.getMaxDistance();
			Byte type = freightConfig.getType();
			if (freightConfig.getId() != null) {
				freightConfig = get(freightConfig.getId());
				freightConfig.setDefaultStatus(defaultStatus == null ? 0 : defaultStatus);
				freightConfig.setFeeCharge(feeCharge);
				freightConfig.setMinDistance(minDistance);
				freightConfig.setMaxDistance(maxDistance);
				freightConfig.setType(type);
			}
			freightConfig.setOffice(bizFreightConfig.getOffice());
			freightConfig.setVarietyInfo(bizFreightConfig.getVarietyInfo());
			super.save(freightConfig);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(BizFreightConfig bizFreightConfig) {
		List<BizFreightConfig> freightConfigs = findListByOfficeAndVari(bizFreightConfig.getOffice().getId(), bizFreightConfig.getVarietyInfo().getId());
		if (CollectionUtils.isNotEmpty(freightConfigs)) {
			for (BizFreightConfig freightConfig : freightConfigs) {
				super.delete(freightConfig);
			}
		}
	}

	/**
	 * 出列表页外的List查询
	 * @param freightConfig
	 * @return
	 */
	public List<BizFreightConfig> findFreightList(BizFreightConfig freightConfig) {
		return dao.findFreightList(freightConfig);
	}

	/**
	 * 根据采购中心和分类查询服务费
	 * @param centerId
	 * @param variId
	 * @return
	 */
	public List<BizFreightConfig> findListByOfficeAndVari(Integer centerId, Integer variId) {
		return dao.findListByOfficeAndVari(centerId,variId);
	}
	
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.integration;

import java.math.BigDecimal;
import java.util.List;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.dao.integration.BizMoneyRecodeDao;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecode;
import com.wanhutong.backend.modules.biz.entity.integration.BizMoneyRecodeDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 积分流水Service
 * @author LX
 * @version 2018-09-16
 */
@Service
@Transactional(readOnly = true)
public class BizMoneyRecodeService extends CrudService<BizMoneyRecodeDao, BizMoneyRecode> {
	@Resource
	private BizMoneyRecodeDao bizMoneyRecodeDao;

	public BizMoneyRecode get(Integer id) {
		return super.get(id);
	}
	
	public List<BizMoneyRecode> findList(BizMoneyRecode bizMoneyRecode) {
		return super.findList(bizMoneyRecode);
	}
	
	public Page<BizMoneyRecode> findPage(Page<BizMoneyRecode> page, BizMoneyRecode bizMoneyRecode) {
		return super.findPage(page, bizMoneyRecode);
	}
	
	@Transactional(readOnly = false)
	public void save(BizMoneyRecode bizMoneyRecode) {
		super.save(bizMoneyRecode);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizMoneyRecode bizMoneyRecode) {
		super.delete(bizMoneyRecode);
	}

	public BizMoneyRecodeDetail selectRecordDetail(){
		BizMoneyRecodeDetail bizMoneyRecodeDetail = bizMoneyRecodeDao.selectRecodeDetail();
        BigDecimal availableIntegration = bizMoneyRecodeDetail.getGainIntegration().subtract(bizMoneyRecodeDetail.getExpireIntegration()).subtract(bizMoneyRecodeDetail.getUsedIntegration());
		int i = availableIntegration.compareTo(BigDecimal.ZERO);
		if(i==0)
		{
			double v = availableIntegration.doubleValue();
			v = 0;
			availableIntegration = new BigDecimal(v);
		}
        bizMoneyRecodeDetail.setAvailableIntegration(availableIntegration);
        return bizMoneyRecodeDetail;
	}

	public 	List<BizMoneyRecodeDetail> selectExpireMoney(){
		List<BizMoneyRecodeDetail> bizMoneyRecodeDetails = bizMoneyRecodeDao.selectExpireMoney();
		for(BizMoneyRecodeDetail biz:bizMoneyRecodeDetails)
		{
			BigDecimal gainIntegration = biz.getGainIntegration();
			BigDecimal usedIntegration = biz.getUsedIntegration();
			BigDecimal expireIntegration = gainIntegration.subtract(usedIntegration);
			biz.setExpireIntegration(expireIntegration);
		}
		return  bizMoneyRecodeDetails;
	}

	//添加积分流水记录
	@Transactional(readOnly = false)
	public void saveAll(List<BizMoneyRecode> arrayList) {
		bizMoneyRecodeDao.saveAll(arrayList);
	}

	//更新用户信用表积分数
	@Transactional
	public void updateMoney(List<BizMoneyRecode> list){
         bizMoneyRecodeDao.updateMoney(list);
	}

	//查询用户所有的可用积分
	public BigDecimal selectMoneyByOfficeId(Integer officeId){
        return bizMoneyRecodeDao.selectMoney(officeId);
	}




}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.pay;

import java.util.List;

import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.pay.BizPayRecord;
import com.wanhutong.backend.modules.biz.dao.pay.BizPayRecordDao;

import javax.annotation.Resource;

/**
 * 交易记录Service
 * @author OuyangXiutian
 * @version 2018-01-20
 */
@Service
@Transactional(readOnly = true)
public class BizPayRecordService extends CrudService<BizPayRecordDao, BizPayRecord> {
	@Resource
	private BizPayRecordDao bizPayRecordDao;

	@Override
	public BizPayRecord get(Integer id) {
		return super.get(id);
	}
	@Override
	public List<BizPayRecord> findList(BizPayRecord bizPayRecord) {
		return super.findList(bizPayRecord);
	}

	public BizPayRecord findBizPayRecord(String payNum){
		return bizPayRecordDao.findBizPayRecord(payNum);
	}
	@Override
	public Page<BizPayRecord> findPage(Page<BizPayRecord> page, BizPayRecord bizPayRecord) {
		User user=UserUtils.getUser();
		if(user.isAdmin()){
			bizPayRecord.setDataStatus("filter");
		}
		return super.findPage(page, bizPayRecord);
	}
	@Override
	@Transactional(readOnly = false)
	public void save(BizPayRecord bizPayRecord) {
		super.save(bizPayRecord);
	}
	@Override
	@Transactional(readOnly = false)
	public void delete(BizPayRecord bizPayRecord) {
		super.delete(bizPayRecord);
	}


	public List<BizOrderStatisticsDto> getReceiveData(String startDate, String endDate, String centerType) {
		return dao.getReceiveData(startDate, endDate, centerType);
	}

	public List<BizOrderStatisticsDto> getSingleReceiveData(String startDate, String endDate, String officeId) {
		return dao.getSingleReceiveData(startDate, endDate, officeId);
	}

	/**
	 * 根据零售商id获取支付记录
	 * @param custId
	 * @return
	 */
	public List<BizPayRecord> findListByCustomerId(Integer custId, Integer tradeTypeCode) {
		return bizPayRecordDao.findListByCustomerId(custId, tradeTypeCode);
	}
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.inventory;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.DateUtils;
import com.wanhutong.backend.modules.biz.dao.inventory.BizSendGoodsRecordDao;
import com.wanhutong.backend.modules.biz.entity.inventory.BizSendGoodsRecord;
import com.wanhutong.backend.modules.enums.OfficeTypeEnum;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.service.OfficeService;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * 供货记录表Service
 * @author 张腾飞
 * @version 2018-01-03
 */
@Service
@Transactional(readOnly = true)
public class BizSendGoodsRecordService extends CrudService<BizSendGoodsRecordDao, BizSendGoodsRecord> {

    @Resource
    private OfficeService officeService;
	@Autowired
    private BizSendGoodsRecordDao bizSendGoodsRecordDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(BizSendGoodsRecordService.class);

	public BizSendGoodsRecord get(Integer id) {
		return super.get(id);
	}
	
	public List<BizSendGoodsRecord> findList(BizSendGoodsRecord bizSendGoodsRecord) {
		return super.findList(bizSendGoodsRecord);
	}
	
	public Page<BizSendGoodsRecord> findPage(Page<BizSendGoodsRecord> page, BizSendGoodsRecord bizSendGoodsRecord) {
			User user=UserUtils.getUser();
			if(user.isAdmin()){
				return super.findPage(page, bizSendGoodsRecord);
			}else {
				bizSendGoodsRecord.setDataStatus("filter");
				if(user.getCompany().getType().equals(OfficeTypeEnum.PURCHASINGCENTER.getType()) ||
                   user.getCompany().getType().equals(OfficeTypeEnum.WITHCAPITAL.getType()) ||
                   user.getCompany().getType().equals(OfficeTypeEnum.NETWORKSUPPLY.getType())){
					bizSendGoodsRecord.getSqlMap().put("sendGoodsRecord", BaseService.dataScopeFilter(user, "cent", "su"));
				}

				return super.findPage(page, bizSendGoodsRecord);
			}

	}
	
	@Transactional(readOnly = false)
	public void save(BizSendGoodsRecord bizSendGoodsRecord) {
	    super.save(bizSendGoodsRecord);
    }

    public Integer getSumSendNumByReqDetailId(Integer reqDetailId) {

		Date todayStartTime = DateUtils.getTodayStartTime();
		Date oneDayBefore = DateUtils.getOneDayBefore(todayStartTime);
		Date yesterdayEnd = DateUtils.getYesterdayEnd();
		return bizSendGoodsRecordDao.getSumSendNumByReqDetailId(reqDetailId,oneDayBefore,yesterdayEnd);
	}
	@Transactional(readOnly = false)
	public void delete(BizSendGoodsRecord bizSendGoodsRecord) {
		super.delete(bizSendGoodsRecord);
	}
}
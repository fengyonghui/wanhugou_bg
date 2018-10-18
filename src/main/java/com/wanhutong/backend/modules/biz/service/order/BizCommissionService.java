/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.List;

import com.wanhutong.backend.modules.biz.entity.order.BizCommissionOrder;
import com.wanhutong.backend.modules.config.ConfigGeneral;
import com.wanhutong.backend.modules.config.parse.PaymentOrderProcessConfig;
import com.wanhutong.backend.modules.process.entity.CommonProcessEntity;
import com.wanhutong.backend.modules.process.service.CommonProcessService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizCommission;
import com.wanhutong.backend.modules.biz.dao.order.BizCommissionDao;

/**
 * 佣金付款表Service
 * @author wangby
 * @version 2018-10-18
 */
@Service
@Transactional(readOnly = true)
public class BizCommissionService extends CrudService<BizCommissionDao, BizCommission> {

	@Autowired
	private CommonProcessService commonProcessService;

	@Autowired
	private BizCommissionOrderService bizCommissionOrderService;

	public static final String DATABASE_TABLE_NAME = "BIZ_COMMISSION";

	public BizCommission get(Integer id) {
		return super.get(id);
	}
	
	public List<BizCommission> findList(BizCommission bizCommission) {
		return super.findList(bizCommission);
	}
	
	public Page<BizCommission> findPage(Page<BizCommission> page, BizCommission bizCommission) {
		return super.findPage(page, bizCommission);
	}
	
	@Transactional(readOnly = false)
	public void save(BizCommission bizCommission) {
		super.save(bizCommission);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizCommission bizCommission) {
		super.delete(bizCommission);
	}


	public Pair<Boolean, String> createCommissionOrder(BizCommission bizCommission, Integer orderId){
		PaymentOrderProcessConfig paymentOrderProcessConfig = ConfigGeneral.PAYMENT_ORDER_PROCESS_CONFIG.get();
		PaymentOrderProcessConfig.Process purchaseOrderProcess = null;
		if (paymentOrderProcessConfig.getDefaultBaseMoney().compareTo(bizCommission.getTotalCommission()) > 0) {
			purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getPayProcessId());
		} else {
			purchaseOrderProcess = paymentOrderProcessConfig.getProcessMap().get(paymentOrderProcessConfig.getDefaultProcessId());
		}

		CommonProcessEntity commonProcessEntity = new CommonProcessEntity();
		commonProcessEntity.setObjectId("0");
		commonProcessEntity.setObjectName(BizCommissionService.DATABASE_TABLE_NAME);
		commonProcessEntity.setType(String.valueOf(purchaseOrderProcess.getCode()));
		commonProcessService.save(commonProcessEntity);

		bizCommission.setBizStatus(BizCommission.BizStatus.NO_PAY.getStatus());
		this.save(bizCommission);

		BizCommissionOrder bizCommissionOrder = new BizCommissionOrder();
		bizCommissionOrder.setOrderId(orderId);
		bizCommissionOrder.setCommId(bizCommission.getId());
		bizCommissionOrder.setCommission(bizCommission.getTotalCommission());
		bizCommissionOrderService.save(bizCommissionOrder);

		return Pair.of(Boolean.TRUE, "操作成功!");
	}
}
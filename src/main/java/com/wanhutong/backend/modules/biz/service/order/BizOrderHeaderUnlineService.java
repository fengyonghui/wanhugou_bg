/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.enums.OrderHeaderBizStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeaderUnline;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderUnlineDao;

/**
 * 线下支付订单(线下独有)Service
 * @author ZhangTengfei
 * @version 2018-04-17
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderUnlineService extends CrudService<BizOrderHeaderUnlineDao, BizOrderHeaderUnline> {

	@Autowired
	private BizOrderHeaderService bizOrderHeaderService;
	@Autowired
	private BizOrderStatusService bizOrderStatusService;

	public BizOrderHeaderUnline get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderHeaderUnline> findList(BizOrderHeaderUnline bizOrderHeaderUnline) {
		return super.findList(bizOrderHeaderUnline);
	}
	
	public Page<BizOrderHeaderUnline> findPage(Page<BizOrderHeaderUnline> page, BizOrderHeaderUnline bizOrderHeaderUnline) {
		return super.findPage(page, bizOrderHeaderUnline);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderHeaderUnline bizOrderHeaderUnline) {
		super.save(bizOrderHeaderUnline);
		BizOrderHeader bizOrderHeader = bizOrderHeaderService.get(bizOrderHeaderUnline.getOrderHeader().getId());
        BigDecimal money = new BigDecimal(bizOrderHeader.getTotalDetail()+bizOrderHeader.getTotalExp()+bizOrderHeader.getFreight());
        if (bizOrderHeader.getBizStatus().equals(OrderHeaderBizStatusEnum.UNPAY.getState())) {
            if (money.compareTo(bizOrderHeaderUnline.getRealMoney())>=0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.INITIAL_PAY.getState());
            }
            if (money.compareTo(bizOrderHeaderUnline.getRealMoney())==0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            }
		}
		if (bizOrderHeader.getBizStatus().equals(OrderHeaderBizStatusEnum.INITIAL_PAY.getState())) {
            if (money.compareTo(bizOrderHeaderUnline.getRealMoney())==0) {
                bizOrderHeader.setBizStatus(OrderHeaderBizStatusEnum.ALL_PAY.getState());
            }
        }
		bizOrderHeader.setReceiveTotal(bizOrderHeaderUnline.getRealMoney().doubleValue());
		bizOrderHeaderService.saveOrderHeader(bizOrderHeader);

		/*用于 订单状态表 insert状态*/
		if(bizOrderHeader!=null && bizOrderHeader.getId()!=null || bizOrderHeader.getBizStatus()!=null){
			BizOrderStatus orderStatus = new BizOrderStatus();
			orderStatus.setOrderHeader(bizOrderHeader);
			orderStatus.setBizStatus(bizOrderHeader.getBizStatus());
			bizOrderStatusService.save(orderStatus);
		}

	}

	@Transactional(readOnly = false)
	public void saveOrderOffline(BizOrderHeaderUnline bizOrderHeaderUnline) {
		super.save(bizOrderHeaderUnline);
	}

	@Transactional(readOnly = false)
	public void delete(BizOrderHeaderUnline bizOrderHeaderUnline) {
		super.delete(bizOrderHeaderUnline);
	}
	
}
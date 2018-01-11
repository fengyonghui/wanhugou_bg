/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.BaseService;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.common.utils.GenerateOrderUtils;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderHeaderDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.common.entity.location.CommonLocation;
import com.wanhutong.backend.modules.common.service.location.CommonLocationService;
import com.wanhutong.backend.modules.enums.OrderTypeEnum;
import com.wanhutong.backend.modules.sys.entity.SysRegion;
import com.wanhutong.backend.modules.sys.entity.User;
import com.wanhutong.backend.modules.sys.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单管理(1: 普通订单 ; 2:帐期采购 3:配资采购)Service
 * @author OuyangXiutian
 * @version 2017-12-20
 */
@Service
@Transactional(readOnly = true)
public class BizOrderHeaderService extends CrudService<BizOrderHeaderDao, BizOrderHeader> {
	@Resource
	private CommonLocationService commonLocationService;

	public BizOrderHeader get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderHeader> findList(BizOrderHeader bizOrderHeader) {
		return super.findList(bizOrderHeader);
	}
	
	public Page<BizOrderHeader> findPage(Page<BizOrderHeader> page, BizOrderHeader bizOrderHeader) {
		User user= UserUtils.getUser();
		if(user.isAdmin()){
			return super.findPage(page, bizOrderHeader);
		}else {
			bizOrderHeader.getSqlMap().put("order", BaseService.dataScopeFilter(user, "s", "su"));
			return super.findPage(page, bizOrderHeader);
		}

	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderHeader bizOrderHeader) {
		CommonLocation bizLocation = bizOrderHeader.getBizLocation();
		if(bizLocation.getRegion()==null){
			bizLocation.setRegion(new SysRegion());
		}
		commonLocationService.updateCommonLocation(bizLocation);
		String orderNum=GenerateOrderUtils.getOrderNum(OrderTypeEnum.stateOf(bizOrderHeader.getOrderType().toString()),bizOrderHeader.getCustomer().getId());
		bizOrderHeader.setOrderNum(orderNum);
		bizOrderHeader.setBizLocation(bizLocation);
		super.save(bizOrderHeader);
	}

	@Transactional(readOnly = false)
	public void saveOrderHeader(BizOrderHeader bizOrderHeader) {
		super.save(bizOrderHeader);
	}
	@Transactional(readOnly = false)
	public void delete(BizOrderHeader bizOrderHeader) {
		super.delete(bizOrderHeader);
	}
	
}
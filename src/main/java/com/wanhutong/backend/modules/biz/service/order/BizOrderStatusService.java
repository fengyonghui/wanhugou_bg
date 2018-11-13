/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.service.order;

import java.util.Date;
import java.util.List;

import com.wanhutong.backend.common.persistence.DataEntity;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.service.CrudService;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import com.wanhutong.backend.modules.biz.dao.order.BizOrderStatusDao;

/**
 * 订单状态修改日志Service
 * @author Oy
 * @version 2018-05-15
 */
@Service
@Transactional(readOnly = true)
public class BizOrderStatusService extends CrudService<BizOrderStatusDao, BizOrderStatus> {

	@Autowired
	private BizOrderStatusDao bizOrderStatusDao;

	public BizOrderStatus get(Integer id) {
		return super.get(id);
	}
	
	public List<BizOrderStatus> findList(BizOrderStatus bizOrderStatus) {
		return super.findList(bizOrderStatus);
	}
	
	public Page<BizOrderStatus> findPage(Page<BizOrderStatus> page, BizOrderStatus bizOrderStatus) {
		return super.findPage(page, bizOrderStatus);
	}
	
	@Transactional(readOnly = false)
	public void save(BizOrderStatus bizOrderStatus) {
		super.save(bizOrderStatus);
	}
	
	@Transactional(readOnly = false)
	public void delete(BizOrderStatus bizOrderStatus) {
		super.delete(bizOrderStatus);
	}

	/**
	 * 备货清单业务状态改变时，往订单状态修改日志表中插入相应日志数据
	 *
	 * @param orderTypeDesc 订单类型对应的表
	 * @param orderType     订单类型
	 * @param id            备货清单id
	 * @return
	 */
	@Transactional(readOnly = false)
	public void insertAfterBizStatusChanged(String orderTypeDesc, Integer orderType, Integer id) {
		Date createTime = new Date();
		Date updateTime = createTime;
		Integer bizStatusTemp = null;
		bizOrderStatusDao.insertAfterBizStatusChanged(bizStatusTemp, createTime, updateTime, orderTypeDesc, orderType, id);
	}

	@Transactional(readOnly = false)
	public void insertAfterBizStatusChangedNew(Integer bizStatusTemp, String orderTypeDesc, Integer orderType, Integer id) {
		Date createTime = new Date();
		Date updateTime = createTime;
		bizOrderStatusDao.insertAfterBizStatusChanged(bizStatusTemp, createTime, updateTime, orderTypeDesc, orderType, id);
	}

	/**
	 * 订单状态改变时，插入订单状态表数据
	 * @param bizOrderHeader
	 */
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public void saveOrderStatus(BizOrderHeader bizOrderHeader) {
		if (bizOrderHeader.getId() == null || bizOrderHeader.getBizStatus() == null) {
			return;
		}
		BizOrderStatus orderStatus = new BizOrderStatus();
		orderStatus.setOrderHeader(bizOrderHeader);
		orderStatus.setBizStatus(bizOrderHeader.getBizStatus());
		List<BizOrderStatus> list = findList(orderStatus);
		if (CollectionUtils.isNotEmpty(list)) {
			boolean flag = true;
			for (BizOrderStatus bizOrderStatus : list) {
				if (bizOrderStatus.getBizStatus().equals(bizOrderHeader.getBizStatus())) {
					flag = false;
					break;
				}
			}
			if (flag) {
				save(orderStatus);
			}
		} else {
			save(orderStatus);
		}
	}

	public Integer findCurrentStatus(Integer orderId, Integer orderType) {
		return bizOrderStatusDao.findCurrentStatus(orderId, orderType);
	}

	/**
	 * 只查找状态表
	 * @param bizOrderStatus
	 * @return
	 */
	public List<BizOrderStatus> findStatusList(BizOrderStatus bizOrderStatus) {
		return bizOrderStatusDao.findStatusList(bizOrderStatus);
	}

}
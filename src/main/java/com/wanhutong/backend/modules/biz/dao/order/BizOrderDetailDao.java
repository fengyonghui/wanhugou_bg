/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderDetail;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.sys.entity.Office;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单详情(销售订单)DAO接口
 * @author OuyangXiutian
 * @version 2017-12-22
 */
@MyBatisDao
public interface BizOrderDetailDao extends CrudDao<BizOrderDetail> {
	public Integer findMaxLine(BizOrderDetail bizOrderDetail);

	List<BizOrderDetail>findOrderTotalByVendor(BizOrderHeader bizOrderHeader);

	List<Map> findRequestTotalByVendor(boolean includeTestData);

	List<BizOrderDetail> findPoHeader(BizOrderDetail bizOrderDetail);

	List<BizOrderDetail> findOrderDetailList( @Param("invoiceId") Integer invoiceId);

	/**
	 * 获取待供货需求汇总列表
	 * @param office
	 * @return
	 */
	List<Map> findRequestTotalByVendorList(Office office);
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.order;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderStatus;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 订单状态修改日志DAO接口
 * @author Oy
 * @version 2018-05-15
 */
@MyBatisDao
public interface BizOrderStatusDao extends CrudDao<BizOrderStatus> {
    /**
     * 备货清单业务状态改变时，往订单状态修改日志表中插入相应日志数据
     * @param orderTypeDesc 订单类型对应的表
     * @param orderType 订单类型
     * @param id 备货清单id
     * @return
     */
    public int insertAfterBizStatusChanged(@Param("bizStatusTemp") Integer bizStatusTemp, @Param("createTime") Date createTime, @Param("updateTime") Date updateTime, @Param("orderTypeDesc") String orderTypeDesc, @Param("orderType") Integer orderType, @Param("id") Integer id);

    int findCurrentStatus(@Param("orderId") Integer orderId, @Param("orderType") Integer orderType);

    /**
     * 只查找状态表
     * @param bizOrderStatus
     * @return
     */
    List<BizOrderStatus> findStatusList(BizOrderStatus bizOrderStatus);
}
/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.request;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestDetail;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 备货清单详细信息DAO接口
 * @author liuying
 * @version 2017-12-23
 */
@MyBatisDao
public interface BizRequestDetailDao extends CrudDao<BizRequestDetail> {

    List<BizRequestDetail>findReqTotalByVendor(BizRequestHeader bizRequestHeader);

    List<BizRequestDetail> findPoRequet(BizRequestDetail bizRequestDetail);

    /**
     * 获取已排产总量和已确认量
     * @param objectId
     * @return
     */
    BizRequestDetail getsumSchedulingNum(@Param("objectId") Integer objectId);

    List<BizRequestDetail> findInventorySkuByskuIdAndcentId(@Param("centerId") Integer centerId, @Param("skuId") Integer skuId);

    List<BizRequestDetail> findInvReqByOrderDetailId(@Param("orderDetailId") Integer orderDetailId);

    /**
     * 根据采购中心和商品查询已收货总数
     * @param centId
     * @param skuId
     * @return
     */
    Integer findRecvTotal(@Param("centId") Integer centId, @Param("skuId") Integer skuId);

    void updateOutQty(@Param("id") int id, @Param("outQty") int outQty);

    List<BizRequestDetail> findListByCentIdAndSkuId(@Param("centId") Integer centId, @Param("skuId") Integer skuId);

    void updateSkuId(@Param("needSkuId") Integer needSkuId, @Param("id") Integer id);

}
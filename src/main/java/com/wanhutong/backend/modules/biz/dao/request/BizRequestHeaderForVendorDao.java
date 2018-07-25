/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.request;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.order.BizOrderHeader;
import com.wanhutong.backend.modules.biz.entity.request.BizRequestHeader;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 备货清单DAO接口
 * @author ZhangTengfei
 * @version 2018-07-19
 */
@MyBatisDao
public interface BizRequestHeaderForVendorDao extends CrudDao<BizRequestHeader> {

    int updateProcessId(@Param("headerId") Integer headerId, @Param("processId") Integer processId);

    int findContByFromOffice(Integer fromOfficeId);

    List<BizRequestHeader>findListForPoHeader(BizRequestHeader bizRequestHeader);

    Integer findSellCount( @Param("centId") Integer centId, @Param("skuId") Integer skuId);

    /**
     * 该备货单下所有商品的总采购数量，总排产数量，总已确认排产数
     * @param id
     * @return
     */
    BizRequestHeader getTotalNum(@Param("id") Integer id);

}
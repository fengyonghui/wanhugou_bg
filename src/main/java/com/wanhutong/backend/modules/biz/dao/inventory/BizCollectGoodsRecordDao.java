/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.inventory;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.Page;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.dto.BizOrderStatisticsDto;
import com.wanhutong.backend.modules.biz.entity.dto.BizSkuInputOutputDto;
import com.wanhutong.backend.modules.biz.entity.inventory.BizCollectGoodsRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * 收货记录表DAO接口
 * @author 张腾飞
 * @version 2018-01-03
 */
@MyBatisDao
public interface BizCollectGoodsRecordDao extends CrudDao<BizCollectGoodsRecord> {
    List<BizCollectGoodsRecord> collectSendFindPage(BizCollectGoodsRecord bizCollectGoodsRecord);


    List<BizCollectGoodsRecord> getListBySkuIdCentId(@Param("skuId") Integer skuId, @Param("centId")Integer centId);

    List<BizCollectGoodsRecord> getListBySkuIdInvId(@Param("skuId") Integer skuId, @Param("invId")Integer invId);

    List<BizSkuInputOutputDto> getSkuInputOutputRecord(@Param("startDate")String startDate, @Param("endDate")String endDate,
                                                       @Param("invName")String invName, @Param("skuItemNo")String skuItemNo);

    Integer findContByCentId(@Param("centId") Integer centId);

    List<BizCollectGoodsRecord> getInventoryDetail(@Param("invSkuId") Integer invSkuId, @Param("skuId") Integer skuId);

    void updateSkuId(@Param("needSkuId") Integer needSkuId, @Param("id") Integer id);
}
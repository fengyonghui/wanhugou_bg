/**
 * Copyright &copy; 2017 <a href="www.wanhutong.com">wanhutong</a> All rights reserved.
 */
package com.wanhutong.backend.modules.biz.dao.shelf;

import com.wanhutong.backend.common.persistence.CrudDao;
import com.wanhutong.backend.common.persistence.annotation.MyBatisDao;
import com.wanhutong.backend.modules.biz.entity.shelf.BizOpShelfInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 运营货架信息DAO接口
 * @author liuying
 * @version 2017-12-19
 */
@MyBatisDao
public interface BizOpShelfInfoDao extends CrudDao<BizOpShelfInfo> {

    /**
     * 根据商品id获取该商品已上货架名称
     * @param skuId
     * @return
     */
    List<BizOpShelfInfo> getShelfNames(@Param("skuId") Integer skuId);
}